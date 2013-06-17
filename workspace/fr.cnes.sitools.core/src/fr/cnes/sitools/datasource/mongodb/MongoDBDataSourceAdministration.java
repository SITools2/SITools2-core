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
package fr.cnes.sitools.datasource.mongodb;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.routing.Router;

import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.datasource.common.SitoolsDataSourceModel;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSourceFactory;
import fr.cnes.sitools.datasource.mongodb.business.SitoolsMongoDBDataSource;
import fr.cnes.sitools.datasource.mongodb.business.SitoolsMongoDBDataSourceFactory;
import fr.cnes.sitools.datasource.mongodb.dbexplorer.MongoDBExplorerApplication;
import fr.cnes.sitools.datasource.mongodb.model.MongoDBDataSource;
import fr.cnes.sitools.registry.AppRegistryApplication;

/**
 * Application for managing JDBC Data Sources (JDBC at first time)
 * 
 * @author AKKA
 */
public final class MongoDBDataSourceAdministration extends SitoolsApplication {

  /** Store */
  private SitoolsStore<MongoDBDataSource> store = null;

  /** To attach RESTlet DataSource to server component. */
  private Router parentRouter = null;

  /**
   * Constructor with component to activate DataSources
   * 
   * @param parentRouter
   *          for attaching DataSource explorer applications
   * @param context
   *          RESTlet application context
   */
  @SuppressWarnings("unchecked")
  public MongoDBDataSourceAdministration(Router parentRouter, Context context) {
    super(context);
    this.store = (SitoolsStore<MongoDBDataSource>) context.getAttributes().get(ContextAttributes.APP_STORE);

    this.parentRouter = parentRouter;

    // registering all active datasource...
    MongoDBDataSource[] datasources = store.getArray();
    for (int i = 0; i < datasources.length; i++) {
      if ("ACTIVE".equals(datasources[i].getStatus())) {
        attachDataSource(datasources[i]);
      }
    }
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("MongoDBDataSourceAdministration");
    setDescription("Administration of MongoDB datasources");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    router.attachDefault(MongoDBDataSourceCollectionResource.class);
    router.attach("/monitoring", DataSourceMonitoringResource.class);
    router.attach("/test", ActivationDataSourceResource.class);
    router.attach("/{datasourceId}", MongoDBDataSourceResource.class);
    router.attach("/{datasourceId}/start", ActivationDataSourceResource.class);
    router.attach("/{datasourceId}/stop", ActivationDataSourceResource.class);
    router.attach("/{datasourceId}/test", ActivationDataSourceResource.class);
    router.attach("/{datasourceId}/monitoring", DataSourceMonitoringResource.class);

    return router;
  }

  /**
   * Create and attach a DBExplorerApplication according to the MongoDBDataSource definition
   * 
   * @param ds
   *          MongoDBDataSource
   */
  public void attachDataSource(MongoDBDataSource ds) {

    if ((ds.getSitoolsAttachementForUsers() != null) && !ds.getSitoolsAttachementForUsers().equals("")) {
      SitoolsMongoDBDataSource dsRestlet = SitoolsMongoDBDataSourceFactory.getInstance().setupDataSourceForUsers(ds);

      Context appContext = parentRouter.getContext().createChildContext();
      appContext.getAttributes().put(ContextAttributes.SETTINGS, getSettings());
      appContext.getAttributes().put(ContextAttributes.APP_REGISTER, false);
      appContext.getAttributes().put(ContextAttributes.APP_ATTACH_REF, ds.getSitoolsAttachementForUsers());
      appContext.getAttributes().put(ContextAttributes.APP_ID, ds.getId());
      appContext.getAttributes().put(ContextAttributes.APP_STORE, store);

      appContext.getAttributes().put("maxThreads", 5);
      appContext.getAttributes().put("maxTotalConnections", 5);
      appContext.getAttributes().put("maxConnectionsPerHost", 5);

      MongoDBExplorerApplication appUsers = new MongoDBExplorerApplication(appContext, dsRestlet);
      dsRestlet.setExplorer(appUsers);

      getSettings().getAppRegistry().attachApplication(appUsers);
    }
  }

  /**
   * Detach the DBExplorerApplication corresponding to the MongoDBDataSource identifier
   * 
   * @param ds
   *          MongoDBDataSource
   */
  public void detachDataSource(SitoolsDataSourceModel ds) {

    SitoolsApplication dbea = getSettings().getAppRegistry().getApplication(ds.getId());
    getSettings().getAppRegistry().detachApplication(dbea);

    SitoolsMongoDBDataSourceFactory.removeDataSource(ds.getId());
  }

  /**
   * Remove DBExplorerApplication and MongoDBDataSource
   * 
   * @param ds
   *          MongoDBDataSource object
   */
  public void detachDataSourceDefinitif(SitoolsDataSourceModel ds) {
    // Connection Pool
    SitoolsSQLDataSourceFactory.removeDataSource(ds.getId());

    AppRegistryApplication registry = getSettings().getAppRegistry();
    if (registry == null) {
      throw new RuntimeException("detachDataSourceDefinitif - settings.appRegistry is null");
    }

    SitoolsApplication dbea = registry.getApplication(ds.getId());
    if (dbea != null) {
      registry.detachApplication(dbea);
      dbea.unregister();
    }
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public SitoolsStore<MongoDBDataSource> getStore() {
    return store;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Application for MongoDB datasources administration.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}
