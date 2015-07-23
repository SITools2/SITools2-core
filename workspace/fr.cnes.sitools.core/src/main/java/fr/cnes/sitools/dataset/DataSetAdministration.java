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

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.notification.business.NotifierFilter;

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
        attachDataSet(datasets[i]);
      }
    }
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

  /**
   * Create and attach a DataSetApplication according to the given DataSet object
   * 
   * @param ds
   *          DataSet object
   */
  @Override
  public void attachDataSet(DataSet ds) {

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

    DataSetApplication dsa = new DataSetApplication(appContext, ds.getId());

    getSettings().getAppRegistry().attachApplication(dsa);

    // Attach the application with RIAP access
    getSettings().getComponent().getInternalRouter().attach(ds.getSitoolsAttachementForUsers(), dsa);

  }

  /**
   * Detach the DataSetApplication according to the given DataSet object
   * 
   * @param ds
   *          DataSet object
   */
  @Override
  public void detachDataSet(DataSet ds) {
    DataSetApplication dsa = (DataSetApplication) getSettings().getAppRegistry().getApplication(ds.getId());
    getSettings().getComponent().getInternalRouter().detach(dsa);
    getSettings().getAppRegistry().detachApplication(dsa);
  }

  /**
   * Detach the DataSetApplication according to the given DataSet object
   * 
   * @param ds
   *          DataSet object
   */
  public void detachDataSetDefinitif(DataSet ds) {
    DataSetApplication dsa = (DataSetApplication) getSettings().getAppRegistry().getApplication(ds.getId());

    if (dsa != null) {
      getSettings().getAppRegistry().detachApplication(dsa);

      dsa.unregister();
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

  @Override
  public void attachDataSet(DataSet ds, boolean isSynchro) {

    
  }

  @Override
  public void detachDataSet(DataSet ds, boolean isSynchro) {

    
  }

  @Override
  public void detachDataSetDefinitif(DataSet ds, boolean isSynchro) {

    
  }

}
