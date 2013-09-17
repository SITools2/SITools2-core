/*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 * This file is part of SITools2.
 *
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package fr.cnes.sitools.service.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.resource.Directory;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;
import org.restlet.security.Authorizer;

import fr.cnes.sitools.common.SitoolsMediaType;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.filter.TemplateFilter;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.plugins.filters.business.FilterFactory;
import fr.cnes.sitools.plugins.filters.model.FilterModel;
import fr.cnes.sitools.proxy.DirectoryProxy;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.service.storage.model.StorageDirectory;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Application for managing directories for data storage in SITools.
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class StorageApplication extends SitoolsApplication {

  /** RESTlet Directories attached directly or indirectly behind RESTlets filters */
  private Map<String, Directory> directories = new ConcurrentHashMap<String, Directory>();

  /** RESTlet secured directly or indirectly */
  private Map<String, Restlet> securedDirectories = new ConcurrentHashMap<String, Restlet>();

  /** RESTlet storage */
  private DataStorageStore store;

  /** Internal router for directory attachment */
  private Router route;

  /**
   * Constructor
   * 
   * @param context
   *          the RESTlet context must contain a <code>DataStorageStore</code> attribute for APP_STORE key
   */
  public StorageApplication(Context context) {
    super(context);

    // Par défaut .html car restlet remplace sur un POST tout les .html en .htm
    this.getMetadataService().addExtension("html", MediaType.TEXT_HTML, true);
    // this.getMetadataService().addExtension("gz", MediaType.APPLICATION_GNU_ZIP, true);

    this.store = (DataStorageStore) context.getAttributes().get(ContextAttributes.APP_STORE);
    if (this.store == null) {
      getLogger().warning("Missing APP_STORE in StorageApplication context");
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.common.application.SitoolsApplication#sitoolsDescribe()
   */
  @Override
  public void sitoolsDescribe() {
    setCategory(Category.USER);
    setName("StorageApplication");
    setDescription("Storage service - Gives access to the files on the storages\n"
        + "-> The administrator does not need authorizations"
        + "-> The public user must have all authorizations according to ones specified on each storage."
        + " (Default GET and HEAD to authorize clients to detect content-type of a file)");
  }

  @Override
  public Restlet createInboundRoot() {

    route = new Router();

    route.attach("/copy/{directoryNameSrc}/{directoryNameDest}", StorageCopyResource.class);

    // Attach directories in the store
    if (store.getList().size() != 0) {
      for (StorageDirectory dir : store.getList()) {
        initDirectory(dir);
      }
    }

    return route;
  }

  /**
   * Attach a directory in the application at start
   * 
   * @param storageDirectory
   *          the directory to attach
   */
  public void initDirectory(StorageDirectory storageDirectory) {

    try {
      // Basic directory
      String localPath = getSettings().getFormattedString(storageDirectory.getLocalPath());

      DirectoryProxy directory = new DirectoryProxy(getContext(), localPath);
      directory.setName(storageDirectory.getName());
      directory.setDescription(storageDirectory.getDescription());

      directory.setDeeplyAccessible(storageDirectory.isDeeplyAccessible());
      directory.setListingAllowed(storageDirectory.isListingAllowed());
      directory.setModifiable(storageDirectory.isModifiable());

      // Security filters
      Restlet secureDir = directory;

      if ("STARTED".equals(storageDirectory.getStatus())) {

        // 1. optional customizable filter
        String filterId = storageDirectory.getId(); // getAuthorizerId();

        // get the Application corresponding to the given applicationId
        FilterModel filterModel = RIAPUtils.getObject(filterId,
            getSettings().getString(Consts.APP_PLUGINS_FILTERS_INSTANCES_URL), getContext(),
            SitoolsMediaType.APPLICATION_JAVA_OBJECT_SITOOLS_MODEL);

        Filter filter = null;
        // if the filterModel is null no specific filter defined.
        if (filterModel != null) {
          filter = FilterFactory.getInstance(this.getContext().createChildContext(), filterId, filterModel);

          if (filter != null) {
            filter.setNext(directory);
            secureDir = filter;
            getLogger().info("Directory secured with specific authoriser");
          }
        }

        // 2. priority classic authorizer
        Authorizer directoryAuthorizer = getAuthorizer(storageDirectory.getId());
        if ((directoryAuthorizer == null) || (directoryAuthorizer == Authorizer.ALWAYS)) {
          getLogger().warning(
              "No security configuration for [" + storageDirectory.getName() + "] datastorage directory.");
        }
        else {
          directoryAuthorizer.setNext(secureDir);
          secureDir = directoryAuthorizer;
        }

        // Insert a TemplateFilter before secureDir
        TemplateFilter tf = new TemplateFilter();
        tf.getConfiguration().setCustomAttribute("directory", storageDirectory);
        tf.setNext(secureDir);

        // if (secureDir == directory) {
        // route.attach(storageDirectory.getAttachUrl(), tf, Router.MODE_FIRST_MATCH);
        // }
        // else {
        // route.attach(storageDirectory.getAttachUrl(), tf, Router.MODE_BEST_MATCH);
        // }

        route.attach(storageDirectory.getAttachUrl(), tf);

        securedDirectories.put(storageDirectory.getId(), tf); // secureDir
      }

      directories.put(storageDirectory.getId(), directory);

    }
    catch (Exception io) {
      getLogger().log(Level.WARNING, io.getMessage(), io);
      storageDirectory.setStatus("ERROR");
      store.save(storageDirectory);
    }

  }

  // /**
  // * Attach a directory in the application : starts it and stores it
  // *
  // * @param storageDirectory
  // * the directory to attach
  // */
  // public void attachDirectory(StorageDirectory storageDirectory) {
  //
  // Directory directory = startDirectory(storageDirectory);
  // directories.put(storageDirectory.getId(), directory);
  // }

  /**
   * Detach directory from application
   * 
   * @param storageDirectory
   *          the directory to detach
   */
  public void detachDirectory(StorageDirectory storageDirectory) {
    stopDirectory(storageDirectory);
    directories.remove(storageDirectory.getId());
    securedDirectories.remove(storageDirectory.getId());
  }

  /**
   * Start a directory by attaching it to the route
   * 
   * @param storageDirectory
   *          the initial StorageDirectory
   */
  public void startDirectory(StorageDirectory storageDirectory) {

    try {
      Directory directory = directories.get(storageDirectory.getId());

      // Security filters
      Restlet secureDir = directory;

      // 1. optional customizable filter
      String filterId = storageDirectory.getId(); // getAuthorizerId();

      // get the Application corresponding to the given applicationId
      FilterModel filterModel = RIAPUtils.getObject(filterId,
          getSettings().getString(Consts.APP_PLUGINS_FILTERS_INSTANCES_URL), getContext(),
          SitoolsMediaType.APPLICATION_JAVA_OBJECT_SITOOLS_MODEL);

      Filter filter = null;
      // if the filterModel is null no specific filter defined.
      if (filterModel != null) {

        // FIXME Create a child context ... perte des attributs => les répliquer ?
        // Est ce que le directory a besoin d'infos du contexte ?
        Context childContext = this.getContext().createChildContext();
        childContext.getAttributes().putAll(childContext.getAttributes());

        filter = FilterFactory.getInstance(childContext, filterId, filterModel);

        if (filter != null) {
          filter.setNext(directory);
          secureDir = filter;
          getLogger().info("Directory secured with specific authoriser");
        }
      }

      // 2. priority classic authorizer
      Authorizer directoryAuthorizer = getAuthorizer(storageDirectory.getId());
      if ((directoryAuthorizer == null) || (directoryAuthorizer == Authorizer.ALWAYS)) {
        getLogger()
            .warning("No security configuration for [" + storageDirectory.getName() + "] datastorage directory.");
      }
      else {
        directoryAuthorizer.setNext(secureDir);
        secureDir = directoryAuthorizer;
      }

      // Insert a TemplateFilter before secureDir
      TemplateFilter tf = new TemplateFilter();
      tf.getConfiguration().setCustomAttribute("directory", storageDirectory);
      tf.setNext(secureDir);

      route.attach(storageDirectory.getAttachUrl(), tf); // tf

      // if (secureDir == directory) {
      // route.attach(storageDirectory.getAttachUrl(), secureDir, Router.MODE_FIRST_MATCH);
      // }
      // else {
      // route.attach(storageDirectory.getAttachUrl(), secureDir, Router.MODE_BEST_MATCH);
      // }

      directories.put(storageDirectory.getId(), directory);
      securedDirectories.put(storageDirectory.getId(), tf); // secureDir

      storageDirectory.setStatus("STARTED");
      store.update(storageDirectory);
    }
    catch (SitoolsException e) {
      getLogger().log(Level.WARNING, e.getMessage(), e);
      storageDirectory.setStatus("ERROR");
      store.update(storageDirectory);
    }
  }

  /**
   * Stop a directory resource
   * 
   * @param storageDirectory
   *          the directory to stop
   */
  public void stopDirectory(StorageDirectory storageDirectory) {

    Directory directory = (Directory) directories.get(storageDirectory.getId());
    if (directory == null) {
      return;
    }

    route.getRoutes().removeAll(directory);

    Restlet secure = (Restlet) securedDirectories.get(storageDirectory.getId());
    if (secure != directory) {
      route.getRoutes().removeAll(secure);
    }

    storageDirectory.setStatus("STOPPED");
    store.update(storageDirectory);

    try {
      directory.stop();
    }
    catch (Exception e) {
      getLogger().log(Level.WARNING, "Error stopping directory " + storageDirectory.getName(), e);
    }

    try {
      if (secure != directory) {
        secure.stop();
      }
    }
    catch (Exception e) {
      getLogger().log(Level.WARNING, "Error stopping secure directory " + storageDirectory.getName(), e);
    }
  }

  /**
   * Get the store associated to the application
   * 
   * @return the store
   */
  public DataStorageStore getStore() {
    return store;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Storage application for directories exposed by Sitools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}
