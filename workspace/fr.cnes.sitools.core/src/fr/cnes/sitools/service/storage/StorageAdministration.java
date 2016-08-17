    /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.ResourceInfo;
import org.restlet.ext.wadl.WadlWrapper;
import org.restlet.routing.Filter;
import org.restlet.routing.Redirector;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.notification.business.NotifierFilter;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Application for managing directories for data storage in SITools.
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class StorageAdministration extends SitoolsApplication {

  /** RESTlet storage */
  private DataStorageStoreInterface store;

  /** Internal router for directory attachment */
  private Router route;

  /** DataStorage application */
  private StorageApplication storageApplication = null;

  /** identifier of StorageApplication */
  private String storageApplicationId;

  /**
   * Constructor
   * 
   * @param context
   *          the RESTlet context must contain a <code>DataStorageStore</code> attribute for APP_STORE key
   */
  public StorageAdministration(Context context) {
    super(context);
    this.store = (DataStorageStoreInterface) context.getAttributes().get(ContextAttributes.APP_STORE);
    if (this.store == null) {
      getLogger().warning("Missing APP_STORE in StorageApplication context");
    }
    storageApplication = (StorageApplication) context.getAttributes().get("DATASTORAGE_APPLICATION");
    storageApplicationId = storageApplication.getId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.common.application.SitoolsApplication#sitoolsDescribe()
   */
  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("StorageAdministration");
    setDescription("Administration of Storage service \n" + "-> The administrator must have all authorizations" + "");
  }

  @Override
  public Restlet createInboundRoot() {

    route = new Router();

    // POST : add a directory
    // GET : retrieve the list of directories
    route.attach("/directories", StorageCollectionResource.class);

    // GET : get the directory from ID
    // PUT : modifies the directory from ID
    // DELETE : deletes a directory from ID
    route.attach("/directories/{directoryId}", StorageResource.class);

    String target = RIAPUtils.getRiapBase() + getSettings().getString(Consts.APP_PLUGINS_FILTERS_INSTANCES_URL)
        + "/{directoryId}";

    Redirector redirector = new Redirector(getContext(), target);

    // WadlWrapper to document the redirector
    WadlWrapper wadlWrapper = new WadlWrapper(redirector) {
      @Override
      public ResourceInfo getResourceInfo(ApplicationInfo applicationInfo) {
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setDocumentation("Please see WADL documention of the FilterPluginApplication");
        return resourceInfo;
      }
    };

    // Extractor extractor = new Extractor(getContext(), redirector);
    // extractor.extractFromQuery("directoryId", "objectId", true);

    route.attach("/directories/{directoryId}/filter", wadlWrapper);

    Filter filter = new NotifierFilter(getContext());
    filter.setNext(route);
    return filter;
  }

  /**
   * Get the store associated to the application
   * 
   * @return the store
   */
  public DataStorageStoreInterface getStore() {
    return store;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo(
        "Storage administration application for directories exposed by Sitools2.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

  /**
   * Get associated StorageApplication
   * 
   * @return StorageApplication
   */
  public StorageApplication getStorageApplication() {

    return (StorageApplication) getSettings().getAppRegistry().getApplication(storageApplicationId);
  }

}
