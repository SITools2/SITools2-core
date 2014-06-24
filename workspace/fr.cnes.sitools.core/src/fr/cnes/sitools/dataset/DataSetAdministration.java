/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.dataset;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;

import com.google.common.io.Files;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.watch.DataSetsWatchServiceRunnable;
import fr.cnes.sitools.notification.business.NotifierFilter;
import fr.cnes.sitools.service.watch.ScheduledThreadWatchService;

/**
 * Application for managing DataSets Dependencies : DataSets
 * 
 * @author AKKA <a
 *         href="https://sourceforge.net/tracker/?func=detail&atid=2158259&aid=3409012&group_id=531341">[531341]</a><br/>
 *         2011/09/19 d.arpin {add Cookie authentification when creating DataSet Application}
 * 
 */
public final class DataSetAdministration extends AbstractDataSetApplication {

  /** host parent router */
  private Router parentRouter = null;

  // ************************************************************
  // ATTRIBUTES USED FOR SYNCHRONIZATION AND SCALABILITY

  /** lastRefresh timestamp */
  private long lastRefresh = 0;

  /** The service used for synchronization */
  private ScheduledThreadWatchService scheduledService;

  /** The period of time to wait between each refresh */
  private int refreshPeriod;

  /** The number of threads used to check for refresh */
  private int refreshThreads;

  // END OF ATTRIBUTES USED FOR SYNCHRONIZATION AND SCALABILITY
  // ************************************************************

  /**
   * Constructor with parentRouter
   * 
   * @param parentRouter
   *          for DataSetApplication attachment
   * @param context
   *          RESTlet Host Context
   */
  public DataSetAdministration(Router parentRouter, Context context) {
    super(context);
    this.parentRouter = parentRouter;

    DataSet[] datasets = store.getArray();
    for (int i = 0; i < datasets.length; i++) {
      if ("ACTIVE".equals(datasets[i].getStatus())) {
        attachDataSet(datasets[i], true);
      }
    }

    if (isAppsRefreshSync()) {
      setRefreshThreads(Integer.parseInt(getSettings().getString("Starter.APPS_REFRESH_THREADS")));
      setRefreshPeriod(Integer.parseInt(getSettings().getString("Starter.APPS_REFRESH_PERIOD")));

      // set the rolesLastModified
      File appFile = getEventFile();
      if (appFile != null && appFile.exists()) {
        setLastRefresh(appFile.lastModified());
      }

      DataSetsWatchServiceRunnable runnable = new DataSetsWatchServiceRunnable(getSettings(), this);

      scheduledService = new ScheduledThreadWatchService(runnable, this.refreshThreads, this.refreshPeriod,
          TimeUnit.SECONDS);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.common.application.SitoolsApplication#start()
   */
  @Override
  public synchronized void start() throws Exception {
    if (isStopped() && isAppsRefreshSync()) {
      scheduledService.start();
    }
    super.start();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.common.application.SitoolsApplication#stop()
   */
  @Override
  public synchronized void stop() throws Exception {
    if (isStarted() && isAppsRefreshSync()) {
      scheduledService.stop();
    }
    super.stop();
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("DataSetAdministration");
    setDescription("DataSets management application");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    router.attachDefault(DataSetCollectionResource.class);

    // attach dynamic resources
    attachParameterizedResources(router);

    router.attach("/{datasetId}", DataSetResource.class);

    router.attach("/{datasetId}/start", ActivationDataSetResource.class);
    router.attach("/{datasetId}/getSqlString", ActivationDataSetResource.class);
    router.attach("/{datasetId}/stop", ActivationDataSetResource.class);
    router.attach("/{datasetId}/refresh", RefreshDataSetResource.class);

    router.attach("/{datasetId}/notify", DataSetNotificationResource.class);
    router.attach("/{datasetId}/mappings", DataSetDictionaryMappingCollectionResource.class);
    router.attach("/{datasetId}/mappings/{dictionaryId}", DataSetDictionaryMappingResource.class);

    Filter filter = new NotifierFilter(getContext());
    filter.setNext(router);
    return filter;
  }

  @Override
  public void attachDataSet(DataSet ds, boolean isSynchro) {
    if ((ds.getSitoolsAttachementForUsers() == null) || ds.getSitoolsAttachementForUsers().equals("")) {
      ds.setSitoolsAttachementForUsers("/" + ds.getId());
      store.update(ds);
    }

    Context appContext = parentRouter.getContext().createChildContext();
    // Le register est fait explicitement dans le constructeur du
    // DataSetApplication
    // une fois l'instance complètement initialisée
    appContext.getAttributes().put(ContextAttributes.SETTINGS, getSettings());
    appContext.getAttributes().put(ContextAttributes.APP_REGISTER, false);
    appContext.getAttributes().put(ContextAttributes.APP_ATTACH_REF, ds.getSitoolsAttachementForUsers());
    appContext.getAttributes().put(ContextAttributes.APP_ID, ds.getId());
    appContext.getAttributes().put(ContextAttributes.APP_STORE, store);

    appContext.getAttributes().put("maxThreads", 50);
    appContext.getAttributes().put("maxTotalConnections", 50);
    appContext.getAttributes().put("maxConnectionsPerHost", 50);

    appContext.getAttributes().put(ContextAttributes.LOG_TO_APP_LOGGER, Boolean.TRUE);

    // to allow SVA to request the Dataset with cookie authentification.
    appContext.getAttributes().put(ContextAttributes.COOKIE_AUTHENTICATION, Boolean.TRUE);
    appContext.getAttributes().put("IS_SYNCHRO", new Boolean(isSynchro));

    DataSetApplication dsa = new DataSetApplication(appContext, ds.getId());

    getSettings().getAppRegistry().attachApplication(dsa);

    // Attach the application with RIAP access
    getSettings().getComponent().getInternalRouter().attach(ds.getSitoolsAttachementForUsers(), dsa);

    if (!isSynchro) {
      updateLastModified();
    }
    // get rid of the IS_SYNCHRO attributes for further operations on the dataset
    appContext.getAttributes().remove("IS_SYNCHRO");
  }

  @Override
  public void detachDataSet(DataSet ds, boolean isSynchro) {
    DataSetApplication dsa = (DataSetApplication) getSettings().getAppRegistry().getApplication(ds.getId());
    if (dsa != null) {
      dsa.getContext().getAttributes().put("IS_SYNCHRO", new Boolean(isSynchro));
      getSettings().getComponent().getInternalRouter().detach(dsa);
      getSettings().getAppRegistry().detachApplication(dsa);

      if (!isSynchro) {
        updateLastModified();
      }
    }
  }

  /**
   * Create and attach a DataSetApplication according to the given DataSet object
   * 
   * @param ds
   *          DataSet object
   */
  @Override
  public void attachDataSet(DataSet ds) {
    attachDataSet(ds, false);
  }

  /**
   * Detach the DataSetApplication according to the given DataSet object
   * 
   * @param ds
   *          DataSet object
   */
  @Override
  public void detachDataSet(DataSet ds) {
    detachDataSet(ds, false);
  }

  @Override
  public void detachDataSetDefinitif(DataSet ds) {
    detachDataSetDefinitif(ds, false);
  }

  @Override
  public void detachDataSetDefinitif(DataSet ds, boolean isSynchro) {
    DataSetApplication dsa = (DataSetApplication) getSettings().getAppRegistry().getApplication(ds.getId());
    if (dsa != null) {
      dsa.getContext().getAttributes().put("IS_SYNCHRO", new Boolean(isSynchro));
      getSettings().getAppRegistry().detachApplication(dsa);
      dsa.unregister();

      if (!isSynchro) {
        updateLastModified();
      }
    }
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Dataset administration.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

  // ************************************************************
  // METHOD USED FOR SYNCHRONIZATION AND SCALABILITY

  /**
   * True is sitools is in Application refresh mode, false otherwise
   * 
   * @return True is sitools is in Application refresh mode, false otherwise
   */
  private boolean isAppsRefreshSync() {
    return Boolean.parseBoolean(getSettings().getString("Starter.APPS_REFRESH", "false"));
  }

  /**
   * Gets the lastRefresh value
   * 
   * @return the lastRefresh
   */
  public long getLastRefresh() {
    return lastRefresh;
  }

  /**
   * Sets the value of lastRefresh
   * 
   * @param lastRefresh
   *          the lastRefresh to set
   */
  public void setLastRefresh(long lastRefresh) {
    this.lastRefresh = lastRefresh;
  }

  /**
   * Get the eventFile
   * 
   * @return the eventFile
   */
  public File getEventFile() {
    String fileUrl = getSettings().getStoreDIR("Starter.DATASETS_EVENT_FILE");
    File file = new File(fileUrl);
    return file;
  }

  /**
   * Get the number of threads to keep in the scheduled pool (ScheduledThreadPoolExecutor)
   * 
   * @return number of threads to keep in the scheduled pool (ScheduledThreadPoolExecutor)
   */
  public int getRefreshThreads() {
    return refreshThreads;
  }

  /**
   * Sets the number of threads to keep in the scheduled pool (ScheduledThreadPoolExecutor)
   * 
   * @param refreshThreads
   *          , the number of threads to keep in the pool, even if they are idle
   */
  public void setRefreshThreads(int refreshThreads) {
    this.refreshThreads = refreshThreads;
  }

  /**
   * Get the period between successive executions of the ScheduledThreadPoolExecutor
   * 
   * @return the period between successive executions of the ScheduledThreadPoolExecutor
   */
  public int getRefreshPeriod() {
    return refreshPeriod;
  }

  /**
   * Sets the period between successive executions of the ScheduledThreadPoolExecutor
   * 
   * @param refreshPeriod
   *          , the period between successive executions (in a time unit to be specified)
   */
  public void setRefreshPeriod(int refreshPeriod) {
    this.refreshPeriod = refreshPeriod;
  }

  /**
   * updateLastModified
   */
  public synchronized void updateLastModified() {
    if (isAppsRefreshSync()) {
      try {
        File file = getEventFile();
        Files.touch(file);
        // pour eviter un refresh de l'instance courante à chaque modification
        setLastRefresh(file.lastModified());
      }
      catch (IOException e) {
        getLogger().log(Level.WARNING, "Error while updating last refresh file ", e);
      }
    }
  }

}
