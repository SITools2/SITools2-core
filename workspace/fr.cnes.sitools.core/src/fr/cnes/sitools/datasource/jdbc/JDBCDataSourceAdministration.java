/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.datasource.jdbc;

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
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSource;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSourceFactory;
import fr.cnes.sitools.datasource.jdbc.dbexplorer.DBExplorerApplication;
import fr.cnes.sitools.datasource.jdbc.model.JDBCDataSource;
import fr.cnes.sitools.registry.AppRegistryApplication;

/**
 * Application for managing JDBC Data Sources (JDBC at first time)
 * 
 * @author AKKA
 */
public final class JDBCDataSourceAdministration extends SitoolsApplication {

  /** Store */
  private SitoolsStore<JDBCDataSource> store = null;

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
  public JDBCDataSourceAdministration(Router parentRouter, Context context) {
    super(context);
    this.store = (SitoolsStore<JDBCDataSource>) context.getAttributes().get(ContextAttributes.APP_STORE);

    this.parentRouter = parentRouter;

    // registering all active datasource...
    JDBCDataSource[] datasources = store.getArray();
    for (int i = 0; i < datasources.length; i++) {
      if ("ACTIVE".equals(datasources[i].getStatus())) {
        attachDataSource(datasources[i]);
      }
    }
  }

  @Override
  public void sitoolsDescribe() {
    setCategory(Category.ADMIN);
    setName("JDBCDataSourceAdministration");
    setDescription("Administration of JDBC datasources");
  }

  @Override
  public Restlet createInboundRoot() {

    Router router = new Router(getContext());

    router.attachDefault(JDBCDataSourceCollectionResource.class);
    router.attach("/monitoring", DataSourceMonitoringResource.class);
    router.attach("/test", ActivationDataSourceResource.class);
    router.attach("/{datasourceId}", JDBCDataSourceResource.class);
    router.attach("/{datasourceId}/start", ActivationDataSourceResource.class);
    router.attach("/{datasourceId}/stop", ActivationDataSourceResource.class);
    router.attach("/{datasourceId}/test", ActivationDataSourceResource.class);
    router.attach("/{datasourceId}/monitoring", DataSourceMonitoringResource.class);

    return router;
  }

  /**
   * Create and attach a DBExplorerApplication according to the JDBCDataSource definition
   * 
   * @param ds
   *          JDBCDataSource
   */
  public void attachDataSource(JDBCDataSource ds) {

    if ((ds.getSitoolsAttachementForUsers() != null) && !ds.getSitoolsAttachementForUsers().equals("")) {
      SitoolsSQLDataSource dsRestlet = SitoolsSQLDataSourceFactory.getInstance().setupDataSourceForUsers(ds);

      Context appContext = parentRouter.getContext().createChildContext();
      appContext.getAttributes().put(ContextAttributes.SETTINGS, getSettings());
      appContext.getAttributes().put(ContextAttributes.APP_REGISTER, false);
      appContext.getAttributes().put(ContextAttributes.APP_ATTACH_REF, ds.getSitoolsAttachementForUsers());
      appContext.getAttributes().put(ContextAttributes.APP_ID, ds.getId());
      appContext.getAttributes().put(ContextAttributes.APP_STORE, store);

      appContext.getAttributes().put("maxThreads", 5);
      appContext.getAttributes().put("maxTotalConnections", 5);
      appContext.getAttributes().put("maxConnectionsPerHost", 5);

      DBExplorerApplication appUsers = new DBExplorerApplication(appContext, dsRestlet);
      dsRestlet.setExplorer(appUsers);

      getSettings().getAppRegistry().attachApplication(appUsers);
    }
  }

  /**
   * Detach the DBExplorerApplication corresponding to the JDBCDataSource identifier
   * 
   * @param ds
   *          JDBCDataSource
   */
  public void detachDataSource(JDBCDataSource ds) {

    DBExplorerApplication dbea = (DBExplorerApplication) getSettings().getAppRegistry().getApplication(ds.getId());
    getSettings().getAppRegistry().detachApplication(dbea);

    SitoolsSQLDataSourceFactory.removeDataSource(ds.getId());
  }

  /**
   * Remove DBExplorerApplication and JDBCDataSource
   * 
   * @param ds
   *          JDBCDataSource object
   */
  public void detachDataSourceDefinitif(JDBCDataSource ds) {
    // Connection Pool
    SitoolsSQLDataSourceFactory.removeDataSource(ds.getId());

    AppRegistryApplication registry = getSettings().getAppRegistry();
    if (registry == null) {
      throw new RuntimeException("detachDataSourceDefinitif - settings.appRegistry is null");
    }
    
    DBExplorerApplication dbea = (DBExplorerApplication) registry.getApplication(ds.getId());
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
  public SitoolsStore<JDBCDataSource> getStore() {
    return store;
  }

  @Override
  public ApplicationInfo getApplicationInfo(Request request, Response response) {
    ApplicationInfo result = super.getApplicationInfo(request, response);
    DocumentationInfo docInfo = new DocumentationInfo("Application for JDBC datasources administration.");
    docInfo.setTitle("API documentation.");
    result.getDocumentations().add(docInfo);
    return result;
  }

}
