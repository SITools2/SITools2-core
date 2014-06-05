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
package fr.cnes.sitools.server;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.collections.CollectionStoreInterface;
import fr.cnes.sitools.collections.CollectionsStoreXML;
import fr.cnes.sitools.collections.CollectionsStoreXMLMap;
import fr.cnes.sitools.collections.model.Collection;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.dataset.DataSetStoreInterface;
import fr.cnes.sitools.dataset.DataSetStoreXML;
import fr.cnes.sitools.dataset.DataSetStoreXMLMap;
import fr.cnes.sitools.dataset.converter.ConverterStoreInterface;
import fr.cnes.sitools.dataset.converter.ConverterStoreXML;
import fr.cnes.sitools.dataset.converter.ConverterStoreXMLMap;
import fr.cnes.sitools.dataset.converter.model.ConverterChainedModel;
import fr.cnes.sitools.dataset.filter.FilterStoreInterface;
import fr.cnes.sitools.dataset.filter.FilterStoreXML;
import fr.cnes.sitools.dataset.filter.FilterStoreXMLMap;
import fr.cnes.sitools.dataset.filter.model.FilterChainedModel;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.opensearch.OpenSearchStoreInterface;
import fr.cnes.sitools.dataset.opensearch.OpenSearchStoreXML;
import fr.cnes.sitools.dataset.opensearch.OpenSearchStoreXMLMap;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.dataset.services.ServiceStoreInterface;
import fr.cnes.sitools.dataset.services.ServiceStoreXML;
import fr.cnes.sitools.dataset.services.ServiceStoreXMLMap;
import fr.cnes.sitools.dataset.services.model.ServiceCollectionModel;
import fr.cnes.sitools.dataset.view.DatasetViewStoreInterface;
import fr.cnes.sitools.dataset.view.DatasetViewStoreXML;
import fr.cnes.sitools.dataset.view.DatasetViewStoreXMLMap;
import fr.cnes.sitools.dataset.view.model.DatasetView;
import fr.cnes.sitools.datasource.jdbc.JDBCDataSourceStoreInterface;
import fr.cnes.sitools.datasource.jdbc.JDBCDataSourceStoreXML;
import fr.cnes.sitools.datasource.jdbc.JDBCDataSourceStoreXMLMap;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSource;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSourceFactory;
import fr.cnes.sitools.datasource.jdbc.model.JDBCDataSource;
import fr.cnes.sitools.datasource.mongodb.MongoDBDataSourceStoreInterface;
import fr.cnes.sitools.datasource.mongodb.MongoDBDataSourceStoreXML;
import fr.cnes.sitools.datasource.mongodb.MongoDBDataSourceStoreXMLMap;
import fr.cnes.sitools.datasource.mongodb.model.MongoDBDataSource;
import fr.cnes.sitools.dictionary.ConceptTemplateStoreInterface;
import fr.cnes.sitools.dictionary.ConceptTemplateStoreXML;
import fr.cnes.sitools.dictionary.ConceptTemplateStoreXMLMap;
import fr.cnes.sitools.dictionary.DictionaryStoreInterface;
import fr.cnes.sitools.dictionary.DictionaryStoreXML;
import fr.cnes.sitools.dictionary.DictionaryStoreXMLMap;
import fr.cnes.sitools.dictionary.model.ConceptTemplate;
import fr.cnes.sitools.dictionary.model.Dictionary;
import fr.cnes.sitools.engine.SitoolsEngine;
import fr.cnes.sitools.feeds.FeedsStoreInterface;
import fr.cnes.sitools.feeds.FeedsStoreXML;
import fr.cnes.sitools.feeds.FeedsStoreXMLMap;
import fr.cnes.sitools.form.components.FormComponentsStoreInterface;
import fr.cnes.sitools.form.components.FormComponentsStoreXML;
import fr.cnes.sitools.form.components.FormComponentsStoreXMLMap;
import fr.cnes.sitools.form.components.model.FormComponent;
import fr.cnes.sitools.form.dataset.FormStoreInterface;
import fr.cnes.sitools.form.dataset.FormStoreXML;
import fr.cnes.sitools.form.dataset.FormStoreXMLMap;
import fr.cnes.sitools.form.dataset.model.Form;
import fr.cnes.sitools.form.project.FormProjectStoreInterface;
import fr.cnes.sitools.form.project.FormProjectStoreXML;
import fr.cnes.sitools.form.project.FormProjectStoreXMLMap;
import fr.cnes.sitools.form.project.model.FormProject;
import fr.cnes.sitools.inscription.InscriptionStoreInterface;
import fr.cnes.sitools.inscription.InscriptionStoreXML;
import fr.cnes.sitools.inscription.InscriptionStoreXMLMap;
import fr.cnes.sitools.inscription.model.Inscription;
import fr.cnes.sitools.notification.store.NotificationStore;
import fr.cnes.sitools.notification.store.NotificationStoreXML;
import fr.cnes.sitools.order.OrderStoreInterface;
import fr.cnes.sitools.order.OrderStoreXML;
import fr.cnes.sitools.order.OrderStoreXMLMap;
import fr.cnes.sitools.order.model.Order;
import fr.cnes.sitools.plugins.applications.ApplicationPluginStore;
import fr.cnes.sitools.plugins.applications.ApplicationPluginStoreInterface;
import fr.cnes.sitools.plugins.applications.ApplicationPluginStoreXmlImpl;
import fr.cnes.sitools.plugins.applications.ApplicationPluginStoreXmlMap;
import fr.cnes.sitools.plugins.filters.FilterPluginStoreInterface;
import fr.cnes.sitools.plugins.filters.FilterPluginStoreXML;
import fr.cnes.sitools.plugins.filters.FilterPluginStoreXMLMap;
import fr.cnes.sitools.plugins.filters.model.FilterModel;
import fr.cnes.sitools.plugins.guiservices.declare.GuiServiceStoreInterface;
import fr.cnes.sitools.plugins.guiservices.declare.GuiServiceStoreXML;
import fr.cnes.sitools.plugins.guiservices.declare.GuiServiceStoreXMLMap;
import fr.cnes.sitools.plugins.guiservices.declare.model.GuiServiceModel;
import fr.cnes.sitools.plugins.guiservices.implement.GuiServicePluginStoreInterface;
import fr.cnes.sitools.plugins.guiservices.implement.GuiServicePluginStoreXML;
import fr.cnes.sitools.plugins.guiservices.implement.GuiServicePluginStoreXMLMap;
import fr.cnes.sitools.plugins.guiservices.implement.model.GuiServicePluginModel;
import fr.cnes.sitools.plugins.resources.ResourcePluginStoreXML;
import fr.cnes.sitools.plugins.resources.ResourcePluginStoreXMLMap;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.portal.PortalStore;
import fr.cnes.sitools.portal.PortalStoreInterface;
import fr.cnes.sitools.portal.PortalStoreXmlImpl;
import fr.cnes.sitools.portal.PortalStoreXmlMap;
import fr.cnes.sitools.project.ProjectStoreInterface;
import fr.cnes.sitools.project.ProjectStoreXML;
import fr.cnes.sitools.project.ProjectStoreXMLMap;
import fr.cnes.sitools.project.graph.GraphStoreInterface;
import fr.cnes.sitools.project.graph.GraphStoreXML;
import fr.cnes.sitools.project.graph.GraphStoreXMLMap;
import fr.cnes.sitools.project.graph.model.Graph;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.project.modules.ProjectModuleStoreInterface;
import fr.cnes.sitools.project.modules.ProjectModuleStoreXML;
import fr.cnes.sitools.project.modules.ProjectModuleStoreXMLMap;
import fr.cnes.sitools.project.modules.model.ProjectModuleModel;
import fr.cnes.sitools.registry.AppRegistryStoreXML;
import fr.cnes.sitools.registry.ApplicationStoreInterface;
import fr.cnes.sitools.registry.ApplicationStoreXMLMap;
import fr.cnes.sitools.registry.model.AppRegistry;
import fr.cnes.sitools.role.RoleStoreInterface;
import fr.cnes.sitools.role.RoleStoreXMLMap;
import fr.cnes.sitools.role.RoleStoreXML;
import fr.cnes.sitools.role.model.Role;
import fr.cnes.sitools.security.JDBCUsersAndGroupsStore;
import fr.cnes.sitools.security.authorization.AuthorizationStore;
import fr.cnes.sitools.security.authorization.AuthorizationStoreInterface;
import fr.cnes.sitools.security.authorization.AuthorizationStoreXML;
import fr.cnes.sitools.security.authorization.AuthorizationStoreXMLMap;
import fr.cnes.sitools.security.userblacklist.UserBlackListModel;
import fr.cnes.sitools.security.userblacklist.UserBlackListStoreInterface;
import fr.cnes.sitools.security.userblacklist.UserBlackListStoreXML;
import fr.cnes.sitools.security.userblacklist.UserBlackListStoreXMLMap;
import fr.cnes.sitools.service.storage.DataStorageStore;
import fr.cnes.sitools.service.storage.DataStorageStoreInterface;
import fr.cnes.sitools.service.storage.DataStorageStoreXmlImpl;
import fr.cnes.sitools.service.storage.DataStorageStoreXmlMap;
import fr.cnes.sitools.tasks.TaskStoreInterface;
import fr.cnes.sitools.tasks.TaskStoreXML;
import fr.cnes.sitools.tasks.TaskStoreXMLMap;
import fr.cnes.sitools.tasks.model.TaskModel;
import fr.cnes.sitools.units.dimension.DimensionStoreInterface;
import fr.cnes.sitools.units.dimension.DimensionStoreXML;
import fr.cnes.sitools.units.dimension.DimensionStoreXMLMap;
import fr.cnes.sitools.units.dimension.model.SitoolsDimension;
import fr.cnes.sitools.userstorage.UserStorageStore;
import fr.cnes.sitools.userstorage.UserStorageStoreInterface;
import fr.cnes.sitools.userstorage.UserStorageStoreXML;
import fr.cnes.sitools.userstorage.UserStorageStoreXmlMap;

/**
 * Store helper
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class StoreHelper implements StoreHelperInterface {

  /**
   * Private constructor for this utility class
   */
  public StoreHelper() {
    super();
  }

  /* (non-Javadoc)
   * @see fr.cnes.sitools.server.StoreHelperInterface#initContext(org.restlet.Context)
   */
  @Override
  public Map<String, Object> initContext(Context context) throws SitoolsException {

    // init the SitoolsEngine in order to register all plugins
    // Expecialy the units
    SitoolsEngine.getInstance();

    Map<String, Object> stores = new ConcurrentHashMap<String, Object>();
    SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);

    SitoolsSQLDataSource dsSecurity = SitoolsSQLDataSourceFactory
        .getInstance()
        .setupDataSource(
            settings.getString("Starter.DATABASE_DRIVER"), settings.getString("Starter.DATABASE_URL"), settings.getString("Starter.DATABASE_USER"), settings.getString("Starter.DATABASE_PASSWORD"), settings.getString("Starter.DATABASE_SCHEMA")); //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    JDBCUsersAndGroupsStore storeUandG = new JDBCUsersAndGroupsStore("SitoolsJDBCStore", dsSecurity, context);
    stores.put(Consts.APP_STORE_USERSANDGROUPS, storeUandG);

    // ======== role ===============

    new File(settings.getStoreDIR(Consts.APP_ROLES_STORE_DIR) + "/map").mkdirs();
    RoleStoreInterface storeRole = new RoleStoreXMLMap(new File(settings.getStoreDIR(Consts.APP_ROLES_STORE_DIR)
        + "/map"), context);
    stores.put(Consts.APP_STORE_ROLE, storeRole);

    // Migrating Roles
    SitoolsStore<Role> storeRoleOLD = new RoleStoreXML(new File(settings.getStoreDIR(Consts.APP_ROLES_STORE_DIR)),
        context);
    if (storeRole.getList().isEmpty()) {
      storeRole.saveList(storeRoleOLD.getList());
    }

    // ======= application ==========

    new File(settings.getStoreDIR(Consts.APP_APPLICATIONS_STORE_DIR) + "/map").mkdirs();
    ApplicationStoreInterface storeApplication = new ApplicationStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_APPLICATIONS_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_REGISTRY, storeApplication);

    // Migrating Applications
    SitoolsStore<AppRegistry> storeAppOLD = new AppRegistryStoreXML(new File(
        settings.getStoreDIR(Consts.APP_APPLICATIONS_STORE_DIR)), context);
    if (storeApplication.getList().isEmpty()) {
      storeApplication.saveList(storeAppOLD.getList());
    }

    // ======= authorization ==========

    new File(settings.getStoreDIR(Consts.APP_AUTHORIZATIONS_STORE_DIR) + "/map").mkdirs();
    AuthorizationStoreInterface storeAuthorization = new AuthorizationStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_AUTHORIZATIONS_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_AUTHORIZATION, storeAuthorization);

    // Migrating Authorizations
    AuthorizationStore storeAuthorizationOLD = new AuthorizationStoreXML(new File(
        settings.getStoreDIR(Consts.APP_AUTHORIZATIONS_STORE_DIR)), context);
    if (storeAuthorization.getList().isEmpty()) {
      storeAuthorization.saveList(storeAuthorizationOLD.getList());
    }

    // ======= notifications ==========
    // no migration needed , already map store

    NotificationStore storeNotification = new NotificationStoreXML(new File(
        settings.getStoreDIR(Consts.APP_NOTIFICATIONS_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_NOTIFICATION, storeNotification);

    // ======= inscription ==========

    new File(settings.getStoreDIR(Consts.APP_INSCRIPTIONS_STORE_DIR) + "/map").mkdirs();
    InscriptionStoreInterface storeIns = new InscriptionStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_INSCRIPTIONS_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_INSCRIPTION, storeIns);

    // Migrating inscription
    SitoolsStore<Inscription> storeInsOLD = new InscriptionStoreXML(new File(
        settings.getStoreDIR(Consts.APP_INSCRIPTIONS_STORE_DIR)), context);
    if (storeIns.getList().isEmpty()) {
      storeIns.saveList(storeInsOLD.getList());
    }

    // ======= data source ==========

    new File(settings.getStoreDIR(Consts.APP_DATASOURCES_STORE_DIR) + "/map").mkdirs();
    JDBCDataSourceStoreInterface storeDatasource = new JDBCDataSourceStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_DATASOURCES_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_DATASOURCE, storeDatasource);

    // Migrating datasource
    SitoolsStore<JDBCDataSource> storeDatasourceOLD = new JDBCDataSourceStoreXML(new File(
        settings.getStoreDIR(Consts.APP_DATASOURCES_STORE_DIR)), context);
    if (storeDatasource.getList().isEmpty()) {
      storeDatasource.saveList(storeDatasourceOLD.getList());
    }

    // ======= mongoDB data source ==========

    new File(settings.getStoreDIR(Consts.APP_DATASOURCES_MONGODB_STORE_DIR) + "/map").mkdirs();
    MongoDBDataSourceStoreInterface storeMongoDBDataSource = new MongoDBDataSourceStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_DATASOURCES_MONGODB_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_DATASOURCE_MONGODB, storeMongoDBDataSource);

    // Migrating MongoDB datasource
    SitoolsStore<MongoDBDataSource> storeMongoDBDataSourceOLD = new MongoDBDataSourceStoreXML(new File(
        settings.getStoreDIR(Consts.APP_DATASOURCES_MONGODB_STORE_DIR)), context);
    if (storeMongoDBDataSource.getList().isEmpty()) {
      storeMongoDBDataSource.saveList(storeMongoDBDataSourceOLD.getList());
    }

    // ======= dictionaries ==========

    new File(settings.getStoreDIR(Consts.APP_DICTIONARIES_STORE_DIR) + "/map").mkdirs();
    DictionaryStoreInterface storeDictionary = new DictionaryStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_DICTIONARIES_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_DICTIONARY, storeDictionary);

    // Migrating dictionaries
    SitoolsStore<Dictionary> storeDictionaryOLD = new DictionaryStoreXML(new File(
        settings.getStoreDIR(Consts.APP_DICTIONARIES_STORE_DIR)), context);
    if (storeDictionary.getList().isEmpty()) {
      storeDictionary.saveList(storeDictionaryOLD.getList());
    }

    // ======= dictionary templates ==========

    new File(settings.getStoreDIR(Consts.APP_DICTIONARIES_TEMPLATES_STORE_DIR) + "/map").mkdirs();
    ConceptTemplateStoreInterface storeConceptTemplate = new ConceptTemplateStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_DICTIONARIES_TEMPLATES_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_TEMPLATE, storeConceptTemplate);

    // Migrating dictionary templates
    SitoolsStore<ConceptTemplate> storeConceptTemplateOLD = new ConceptTemplateStoreXML(new File(
        settings.getStoreDIR(Consts.APP_DICTIONARIES_TEMPLATES_STORE_DIR)), context);
    if (storeConceptTemplate.getList().isEmpty()) {
      storeConceptTemplate.saveList(storeConceptTemplateOLD.getList());
    }

    // ======== application plugin ===============

    new File(settings.getStoreDIR(Consts.APP_PLUGINS_APPLICATIONS_STORE_DIR) + "/map").mkdirs();
    ApplicationPluginStoreInterface storeApplicationPlugin = new ApplicationPluginStoreXmlMap(new File(
        settings.getStoreDIR(Consts.APP_PLUGINS_APPLICATIONS_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_PLUGINS_APPLICATIONS, storeApplicationPlugin);

    // Migrating ApplicationPlugin
    ApplicationPluginStore storeApplicationPluginOLD = new ApplicationPluginStoreXmlImpl(new File(
        settings.getStoreDIR(Consts.APP_PLUGINS_APPLICATIONS_STORE_DIR)), context);
    if (storeApplicationPlugin.getList().isEmpty()) {
      storeApplicationPlugin.saveList(storeApplicationPluginOLD.getList());
    }

    // ======== Filter plugins ===============

    new File(settings.getStoreDIR(Consts.APP_PLUGINS_FILTERS_STORE_DIR) + "/map").mkdirs();
    FilterPluginStoreInterface storeFilterPlugin = new FilterPluginStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_PLUGINS_FILTERS_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_PLUGINS_FILTERS, storeFilterPlugin);

    SitoolsStore<FilterModel> storeFilterPluginOLD = new FilterPluginStoreXML(new File(
        settings.getStoreDIR(Consts.APP_PLUGINS_FILTERS_STORE_DIR)), context);
    if (storeFilterPlugin.getList().isEmpty()) {
      storeFilterPlugin.saveList(storeFilterPluginOLD.getList());
    }

    // ======== Resources plugins ===============

    new File(settings.getStoreDIR(Consts.APP_PLUGINS_RESOURCES_STORE_DIR) + "/map").mkdirs();
    SitoolsStore<ResourceModel> storeResourcePlugins = new ResourcePluginStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_PLUGINS_RESOURCES_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_PLUGINS_RESOURCES, storeResourcePlugins);

    SitoolsStore<ResourceModel> storeResourcePluginsOLD = new ResourcePluginStoreXML(new File(
        settings.getStoreDIR(Consts.APP_PLUGINS_RESOURCES_STORE_DIR)), context);
    if (storeResourcePlugins.getList().isEmpty()) {
      storeResourcePlugins.saveList(storeResourcePluginsOLD.getList());
    }

    // ======== dataset converter ===============

    new File(settings.getStoreDIR(Consts.APP_DATASETS_CONVERTERS_STORE_DIR) + "/map").mkdirs();
    ConverterStoreInterface storeConverter = new ConverterStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_DATASETS_CONVERTERS_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_DATASETS_CONVERTERS, storeConverter);

    SitoolsStore<ConverterChainedModel> storeConvOLD = new ConverterStoreXML(new File(
        settings.getStoreDIR(Consts.APP_DATASETS_CONVERTERS_STORE_DIR)), context);
    if (storeConverter.getList().isEmpty()) {
      storeConverter.saveList(storeConvOLD.getList());
    }

    // ======== dataset filter ===============

    new File(settings.getStoreDIR(Consts.APP_DATASETS_FILTERS_STORE_DIR) + "/map").mkdirs();
    FilterStoreInterface storeFilter = new FilterStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_DATASETS_FILTERS_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_DATASETS_FILTERS, storeFilter);

    SitoolsStore<FilterChainedModel> storeFilterOLD = new FilterStoreXML(new File(
        settings.getStoreDIR(Consts.APP_DATASETS_FILTERS_STORE_DIR)), context);
    if (storeFilter.getList().isEmpty()) {
      storeFilter.saveList(storeFilterOLD.getList());
    }

    // ======== dataset view ===============

    new File(settings.getStoreDIR(Consts.APP_DATASETS_VIEWS_STORE_DIR) + "/map").mkdirs();
    DatasetViewStoreInterface storeDsView = new DatasetViewStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_DATASETS_VIEWS_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_DATASETS_VIEWS, storeDsView);

    SitoolsStore<DatasetView> storeDsViewOLD = new DatasetViewStoreXML(new File(
        settings.getStoreDIR(Consts.APP_DATASETS_VIEWS_STORE_DIR)), context);
    if (storeDsView.getList().isEmpty()) {
      storeDsView.saveList(storeDsViewOLD.getList());
    }

    // ======== portal ===============

    new File(settings.getStoreDIR(Consts.APP_PORTAL_STORE_DIR) + "/map").mkdirs();
    PortalStoreInterface storePortal = new PortalStoreXmlMap(new File(settings.getStoreDIR(Consts.APP_PORTAL_STORE_DIR)
        + "/map"), context);
    stores.put(Consts.APP_STORE_PORTAL, storePortal);

    // Migrating Portal
    PortalStore storePortalOLD = new PortalStoreXmlImpl(new File(settings.getStoreDIR(Consts.APP_PORTAL_STORE_DIR)),
        context);
    if (storePortal.getList().isEmpty()) {
      storePortal.saveList(storePortalOLD.getList());
    }

    // ======== form components ===============

    new File(settings.getStoreDIR(Consts.APP_FORMCOMPONENTS_STORE_DIR) + "/map").mkdirs();
    FormComponentsStoreInterface storeFormComponents = new FormComponentsStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_FORMCOMPONENTS_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_FORMCOMPONENT, storeFormComponents);

    // Migrating form components
    SitoolsStore<FormComponent> storeFormComponentsOLD = new FormComponentsStoreXML(new File(
        settings.getStoreDIR(Consts.APP_FORMCOMPONENTS_STORE_DIR)), context);
    if (storeFormComponents.getList().isEmpty()) {
      storeFormComponents.saveList(storeFormComponentsOLD.getList());
    }

    // ======== collection ===============

    new File(settings.getStoreDIR(Consts.APP_COLLECTIONS_STORE_DIR) + "/map").mkdirs();
    CollectionStoreInterface storeCollections = new CollectionsStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_COLLECTIONS_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_COLLECTIONS, storeCollections);

    // Migrating collection
    SitoolsStore<Collection> storeCollectionsOLD = new CollectionsStoreXML(new File(
        settings.getStoreDIR(Consts.APP_COLLECTIONS_STORE_DIR)), context);
    if (storeCollections.getList().isEmpty()) {
      storeCollections.saveList(storeCollectionsOLD.getList());
    }

    // ======== form project ===============

    new File(settings.getStoreDIR(Consts.APP_FORMPROJECT_STORE_DIR) + "/map").mkdirs();
    FormProjectStoreInterface storeFormProject = new FormProjectStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_FORMPROJECT_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_FORMPROJECT, storeFormProject);

    SitoolsStore<FormProject> storeFormProjectOLD = new FormProjectStoreXML(new File(
        settings.getStoreDIR(Consts.APP_FORMPROJECT_STORE_DIR)), context);
    if (storeFormProject.getList().isEmpty()) {
      storeFormProject.saveList(storeFormProjectOLD.getList());
    }

    // ======== Projects ===============
    new File(settings.getStoreDIR(Consts.APP_PROJECTS_STORE_DIR) + "/map").mkdirs();
    ProjectStoreInterface storeProject = new ProjectStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_PROJECTS_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_PROJECT, storeProject);

    SitoolsStore<Project> storeProjectOld = new ProjectStoreXML(new File(
        settings.getStoreDIR(Consts.APP_PROJECTS_STORE_DIR)), context);
    if (storeProject.getList().isEmpty()) {
      storeProject.saveList(storeProjectOld.getList());
    }

    // ======== graphs ===============

    new File(settings.getStoreDIR(Consts.APP_GRAPHS_STORE_DIR) + "/map").mkdirs();
    GraphStoreInterface storeGraph = new GraphStoreXMLMap(new File(settings.getStoreDIR(Consts.APP_GRAPHS_STORE_DIR)
        + "/map"), context);
    stores.put(Consts.APP_STORE_GRAPH, storeGraph);

    SitoolsStore<Graph> storeGraphOLD = new GraphStoreXML(new File(settings.getStoreDIR(Consts.APP_GRAPHS_STORE_DIR)),
        context);

    if (storeGraph.getList().isEmpty()) {
      storeGraph.saveList(storeGraphOLD.getList());
    }

    // ======== forms ===============

    new File(settings.getStoreDIR(Consts.APP_FORMS_STORE_DIR) + "/map").mkdirs();
    FormStoreInterface storeForm = new FormStoreXMLMap(new File(settings.getStoreDIR(Consts.APP_FORMS_STORE_DIR)
        + "/map"), context);
    stores.put(Consts.APP_STORE_FORM, storeForm);

    // Migrating forms
    SitoolsStore<Form> storeFormOLD = new FormStoreXML(new File(settings.getStoreDIR(Consts.APP_FORMS_STORE_DIR)),
        context);
    if (storeForm.getList().isEmpty()) {
      storeForm.saveList(storeFormOLD.getList());
    }

    // ======== feed ===============

    new File(settings.getStoreDIR(Consts.APP_FEEDS_STORE_DIR) + "/map").mkdirs();
    FeedsStoreInterface storeFeeds = new FeedsStoreXMLMap(new File(settings.getStoreDIR(Consts.APP_FEEDS_STORE_DIR)
        + "/map"), context);
    stores.put(Consts.APP_STORE_FEED, storeFeeds);

    // Migrating feeds
    FeedsStoreXML storeFeedsOLD = new FeedsStoreXML(new File(settings.getStoreDIR(Consts.APP_FEEDS_STORE_DIR)), context);
    if (storeFeeds.getList().isEmpty()) {
      storeFeeds.saveList(storeFeedsOLD.getList());
    }

    // ======== open search ===============

    new File(settings.getStoreDIR(Consts.APP_OPENSEARCH_STORE_DIR) + "/map").mkdirs();
    OpenSearchStoreInterface storeOpenSearch = new OpenSearchStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_OPENSEARCH_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_OPENSEARCH, storeOpenSearch);

    // Migrating open search
    SitoolsStore<Opensearch> storeOpenSearchOLD = new OpenSearchStoreXML(new File(
        settings.getStoreDIR(Consts.APP_OPENSEARCH_STORE_DIR)), context);
    if (storeOpenSearch.getList().isEmpty()) {
      storeOpenSearch.saveList(storeOpenSearchOLD.getList());
    }

    // ======== orders ===============

    new File(settings.getStoreDIR(Consts.APP_ORDERS_STORE_DIR) + "/map").mkdirs();
    OrderStoreInterface storeOrder = new OrderStoreXMLMap(new File(settings.getStoreDIR(Consts.APP_ORDERS_STORE_DIR)
        + "/map"), context);
    stores.put(Consts.APP_STORE_ORDER, storeOrder);

    // Migrating orders
    SitoolsStore<Order> storeOrderOLD = new OrderStoreXML(new File(settings.getStoreDIR(Consts.APP_ORDERS_STORE_DIR)),
        context);
    if (storeOrder.getList().isEmpty()) {
      storeOrder.saveList(storeOrderOLD.getList());
    }

    // ======== user storage ===============

    new File(settings.getStoreDIR(Consts.APP_USERSTORAGE_STORE_DIR) + "/map").mkdirs();
    UserStorageStoreInterface storeUserStorage = new UserStorageStoreXmlMap(new File(
        settings.getStoreDIR(Consts.APP_USERSTORAGE_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_USERSTORAGE, storeUserStorage);

    // Migrating UserStorage
    UserStorageStore storeUserStorageOLD = new UserStorageStoreXML(new File(
        settings.getStoreDIR(Consts.APP_USERSTORAGE_STORE_DIR)), context);
    if (storeUserStorage.getList().isEmpty()) {
      storeUserStorage.saveList(storeUserStorageOLD.getList());
    }

    // ======== data storage ===============

    new File(settings.getStoreDIR(Consts.APP_DATASTORAGE_STORE_DIR) + "/map").mkdirs();
    DataStorageStoreInterface storeDataStorage = new DataStorageStoreXmlMap(new File(
        settings.getStoreDIR(Consts.APP_DATASTORAGE_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_DATASTORAGE, storeDataStorage);

    // Migrating DataStorage
    DataStorageStore storeDataStorageOLD = new DataStorageStoreXmlImpl(new File(
        settings.getStoreDIR(Consts.APP_DATASTORAGE_STORE_DIR)), context);
    if (storeDataStorage.getList().isEmpty()) {
      storeDataStorage.saveList(storeDataStorageOLD.getList());
    }

    // ======== dataset ===============

    new File(settings.getStoreDIR(Consts.APP_DATASETS_STORE_DIR) + "/map").mkdirs();
    DataSetStoreInterface storeDataSet = new DataSetStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_DATASETS_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_DATASET, storeDataSet);

    // migrating datasets
    SitoolsStore<DataSet> storeDataSetOLD = new DataSetStoreXML(new File(
        settings.getStoreDIR(Consts.APP_DATASETS_STORE_DIR)), context);
    if (storeDataSet.getList().isEmpty()) {
      storeDataSet.saveList(storeDataSetOLD.getList());
    }

    // ========= dimension =============

    new File(settings.getStoreDIR(Consts.APP_DIMENSION_STORE_DIR) + "/map").mkdirs();
    DimensionStoreInterface storeDimensions = new DimensionStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_DIMENSION_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_DIMENSION, storeDimensions);

    // Migrating Dimension
    SitoolsStore<SitoolsDimension> storeDimensionsOLD = new DimensionStoreXML(new File(
        settings.getStoreDIR(Consts.APP_DIMENSION_STORE_DIR)), context);
    if (storeDimensions.getList().isEmpty()) {
      storeDimensions.saveList(storeDimensionsOLD.getList());
    }

    // ========= tasks =============

    new File(settings.getStoreDIR(Consts.APP_TASK_STORE_DIR) + "/map").mkdirs();
    TaskStoreInterface storeTasks = new TaskStoreXMLMap(new File(settings.getStoreDIR(Consts.APP_TASK_STORE_DIR)
        + "/map"), context);
    stores.put(Consts.APP_STORE_TASK, storeTasks);

    SitoolsStore<TaskModel> storeTaskModelOld = new TaskStoreXML(new File(
        settings.getStoreDIR(Consts.APP_TASK_STORE_DIR)), context);
    if (storeTasks.getList().isEmpty()) {
      storeTasks.saveList(storeTaskModelOld.getList());
    }

    // ========= ProjectModules =============

    new File(settings.getStoreDIR(Consts.APP_PROJECTS_MODULES_STORE_DIR) + "/map").mkdirs();
    ProjectModuleStoreInterface storeProjectModule = new ProjectModuleStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_PROJECTS_MODULES_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_PROJECTS_MODULES, storeProjectModule);

    SitoolsStore<ProjectModuleModel> storeProjectModuleOld = new ProjectModuleStoreXML(new File(
        settings.getStoreDIR(Consts.APP_PROJECTS_MODULES_STORE_DIR)), context);
    if (storeProjectModule.getList().isEmpty()) {
      storeProjectModule.saveList(storeProjectModuleOld.getList());
    }

    // ========= gui services =============

    new File(settings.getStoreDIR(Consts.APP_GUI_SERVICES_STORE_DIR) + "/map").mkdirs();
    GuiServiceStoreInterface storeGuiService = new GuiServiceStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_GUI_SERVICES_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_GUI_SERVICE, storeGuiService);

    // Migrating gui services
    SitoolsStore<GuiServiceModel> storeGuiServiceOLD = new GuiServiceStoreXML(new File(
        settings.getStoreDIR(Consts.APP_GUI_SERVICES_STORE_DIR)), context);
    if (storeGuiService.getList().isEmpty()) {
      storeGuiService.saveList(storeGuiServiceOLD.getList());
    }

    // ========= GuiServicePlugin =============
    new File(settings.getStoreDIR(Consts.APP_GUI_SERVICES_PLUGIN_STORE_DIR) + "/map").mkdirs();
    GuiServicePluginStoreInterface storeGuiServicePlugin = new GuiServicePluginStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_GUI_SERVICES_PLUGIN_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_GUI_SERVICES_PLUGIN, storeGuiServicePlugin);

    SitoolsStore<GuiServicePluginModel> storeGuiServicePluginOld = new GuiServicePluginStoreXML(new File(
        settings.getStoreDIR(Consts.APP_GUI_SERVICES_PLUGIN_STORE_DIR)), context);
    if (storeGuiServicePlugin.getList().isEmpty()) {
      storeGuiServicePlugin.saveList(storeGuiServicePluginOld.getList());
    }

    // ========= Services =============
    new File(settings.getStoreDIR(Consts.APP_SERVICES_STORE_DIR) + "/map").mkdirs();
    ServiceStoreInterface storeServices = new ServiceStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_SERVICES_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_SERVICES, storeServices);

    // Migrating services
    SitoolsStore<ServiceCollectionModel> storeServicesOld = new ServiceStoreXML(new File(
        settings.getStoreDIR(Consts.APP_SERVICES_STORE_DIR)), context);
    if (storeServices.getList().isEmpty()) {
      storeServices.saveList(storeServicesOld.getList());
    }

    // ========= userblacklist =============
    new File(settings.getStoreDIR(Consts.APP_USER_BLACKLIST_STORE_DIR) + "/map").mkdirs();
    UserBlackListStoreInterface storeUserBlacklist = new UserBlackListStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_USER_BLACKLIST_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_USER_BLACKLIST, storeUserBlacklist);

    // Migrating userblacklist
    SitoolsStore<UserBlackListModel> storeUserBlacklistOld = new UserBlackListStoreXML(new File(
        settings.getStoreDIR(Consts.APP_USER_BLACKLIST_STORE_DIR)), context);
    if (storeUserBlacklist.getList().isEmpty()) {
      storeUserBlacklist.saveList(storeUserBlacklistOld.getList());
    }

    if (settings.isStartWithMigration()) {
      migrateStores(stores);
    }

    if (settings.isCheckStores()) {
      readStores(stores);
    }

    return stores;
  }

  /**
   * Simply reads all the data from the given stores, and update the data. It rewrites the data with the proper model.
   * 
   * @param stores
   *          the Map of stores
   */
  private void migrateStores(Map<String, Object> stores) {
    for (Object store : stores.values()) {
      if (store instanceof SitoolsStore) {
        @SuppressWarnings("unchecked")
        SitoolsStore<IResource> storeImpl = (SitoolsStore<IResource>) store;
        List<IResource> list = storeImpl.getList();
        for (IResource iResource : list) {
          storeImpl.update(iResource);
        }
      }
      else {
        // specific Stores
        // if (store instanceof NotificationStoreXML) {
        // TODO voir ce que fait ce store

        // NotificationStoreXML storeImpl = (NotificationStoreXML) store;
        // List<Notification> notifications = storeImpl.getList();
        // for (Persistent persistent : list) {
        // storeImpl.update(persistent);
        // }
        // }
      }

    }
  }

  /**
   * Read all the stores to check if there's some errors
   * 
   * @param stores
   *          The list of Stores
   * @throws SitoolsException
   *           if there are errors
   */
  private void readStores(Map<String, Object> stores) throws SitoolsException {
    for (Object store : stores.values()) {
      if (store instanceof SitoolsStore) {
        @SuppressWarnings("unchecked")
        SitoolsStore<IResource> storeImpl = (SitoolsStore<IResource>) store;
        try {
          List<IResource> list = storeImpl.getList();
          for (IResource iResource : list) {
            storeImpl.retrieve(iResource.getId());
          }
        }
        catch (Exception e) {
          // try {
          // storeImpl.close();
          throw new SitoolsException("ERROR WHILE LOADING STORE : " + storeImpl.getClass().getSimpleName(), e);
          // }
          // catch (IOException e1) {
          // throw new SitoolsException("ERROR WHILE LOADING STORE : " + storeImpl.getClass().getSimpleName(), e1);
          // }
        }
      }
      else {
        // specific Stores
        // if (store instanceof NotificationStoreXML) {
        // TODO voir ce que fait ce store

        // NotificationStoreXML storeImpl = (NotificationStoreXML) store;
        // List<Notification> notifications = storeImpl.getList();
        // for (Persistent persistent : list) {
        // storeImpl.update(persistent);
        // }
        // }
      }
    }
  }
}
