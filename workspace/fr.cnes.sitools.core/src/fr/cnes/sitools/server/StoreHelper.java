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
import java.io.IOException;
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
import fr.cnes.sitools.dataset.filter.FilterStoreXML;
import fr.cnes.sitools.dataset.filter.model.FilterChainedModel;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.opensearch.OpenSearchStoreXML;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.dataset.services.ServiceStoreXML;
import fr.cnes.sitools.dataset.services.model.ServiceCollectionModel;
import fr.cnes.sitools.dataset.view.DatasetViewStoreXML;
import fr.cnes.sitools.dataset.view.model.DatasetView;
import fr.cnes.sitools.datasource.jdbc.JDBCDataSourceStoreXML;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSource;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSourceFactory;
import fr.cnes.sitools.datasource.jdbc.model.JDBCDataSource;
import fr.cnes.sitools.datasource.mongodb.MongoDBDataSourceStoreXML;
import fr.cnes.sitools.datasource.mongodb.model.MongoDBDataSource;
import fr.cnes.sitools.dictionary.ConceptTemplateStoreXML;
import fr.cnes.sitools.dictionary.DictionaryStoreXML;
import fr.cnes.sitools.dictionary.model.ConceptTemplate;
import fr.cnes.sitools.dictionary.model.Dictionary;
import fr.cnes.sitools.engine.SitoolsEngine;
import fr.cnes.sitools.feeds.FeedsStoreXML;
import fr.cnes.sitools.form.components.FormComponentsStoreXML;
import fr.cnes.sitools.form.components.model.FormComponent;
import fr.cnes.sitools.form.dataset.FormStoreXML;
import fr.cnes.sitools.form.dataset.model.Form;
import fr.cnes.sitools.form.project.FormProjectStoreXML;
import fr.cnes.sitools.form.project.model.FormProject;
import fr.cnes.sitools.inscription.InscriptionStoreXML;
import fr.cnes.sitools.inscription.model.Inscription;
import fr.cnes.sitools.notification.store.NotificationStore;
import fr.cnes.sitools.notification.store.NotificationStoreXML;
import fr.cnes.sitools.order.OrderStoreXML;
import fr.cnes.sitools.order.model.Order;
import fr.cnes.sitools.persistence.PersistenceDao;
import fr.cnes.sitools.persistence.Persistent;
import fr.cnes.sitools.plugins.applications.ApplicationPluginStore;
import fr.cnes.sitools.plugins.applications.ApplicationPluginStoreInterface;
import fr.cnes.sitools.plugins.applications.ApplicationPluginStoreXmlImpl;
import fr.cnes.sitools.plugins.applications.ApplicationPluginStoreXmlMap;
import fr.cnes.sitools.plugins.filters.FilterPluginStoreXML;
import fr.cnes.sitools.plugins.filters.model.FilterModel;
import fr.cnes.sitools.plugins.guiservices.declare.GuiServiceStoreXML;
import fr.cnes.sitools.plugins.guiservices.declare.model.GuiServiceModel;
import fr.cnes.sitools.plugins.guiservices.implement.GuiServicePluginStoreXML;
import fr.cnes.sitools.plugins.guiservices.implement.model.GuiServicePluginModel;
import fr.cnes.sitools.plugins.resources.ResourcePluginStoreXML;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.portal.PortalStore;
import fr.cnes.sitools.portal.PortalStoreInterface;
import fr.cnes.sitools.portal.PortalStoreXmlImpl;
import fr.cnes.sitools.portal.PortalStoreXmlMap;
import fr.cnes.sitools.project.ProjectStoreXML;
import fr.cnes.sitools.project.graph.GraphStoreXML;
import fr.cnes.sitools.project.graph.model.Graph;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.project.modules.ProjectModuleStoreXML;
import fr.cnes.sitools.project.modules.model.ProjectModuleModel;
import fr.cnes.sitools.registry.AppRegistryStoreXML;
import fr.cnes.sitools.registry.ApplicationStoreInterface;
import fr.cnes.sitools.registry.ApplicationStoreXMLMap;
import fr.cnes.sitools.registry.model.AppRegistry;
import fr.cnes.sitools.role.RoleStoreInterface;
import fr.cnes.sitools.role.RoleStoreMapXML;
import fr.cnes.sitools.role.RoleStoreXML;
import fr.cnes.sitools.role.model.Role;
import fr.cnes.sitools.security.JDBCUsersAndGroupsStore;
import fr.cnes.sitools.security.authorization.AuthorizationStore;
import fr.cnes.sitools.security.authorization.AuthorizationStoreInterface;
import fr.cnes.sitools.security.authorization.AuthorizationStoreXML;
import fr.cnes.sitools.security.authorization.AuthorizationStoreXMLMap;
import fr.cnes.sitools.security.authorization.client.ResourceAuthorization;
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
import fr.cnes.sitools.userstorage.model.UserStorage;

/**
 * Store helper
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class StoreHelper {

  /**
   * Private constructor for this utility class
   */
  private StoreHelper() {
    super();
  }

  /**
   * Initializes the context with default stores
   * 
   * @param context
   *          a Restlet {@link Context}. It must contains the global {@link SitoolsSettings}
   * @return the map of initial context
   * @throws SitoolsException
   *           if an error occured while creating the stores
   */
  public static Map<String, Object> initContext(Context context) throws SitoolsException {

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
    RoleStoreInterface storeRole = new RoleStoreMapXML(new File(settings.getStoreDIR(Consts.APP_ROLES_STORE_DIR)
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

    NotificationStore storeNotification = new NotificationStoreXML(new File(
        settings.getStoreDIR(Consts.APP_NOTIFICATIONS_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_NOTIFICATION, storeNotification);

    SitoolsStore<Inscription> storeIns = new InscriptionStoreXML(new File(
        settings.getStoreDIR(Consts.APP_INSCRIPTIONS_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_INSCRIPTION, storeIns);

    SitoolsStore<JDBCDataSource> storeDS = new JDBCDataSourceStoreXML(new File(
        settings.getStoreDIR(Consts.APP_DATASOURCES_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_DATASOURCE, storeDS);

    SitoolsStore<MongoDBDataSource> storeMongoDBDS = new MongoDBDataSourceStoreXML(new File(
        settings.getStoreDIR(Consts.APP_DATASOURCES_MONGODB_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_DATASOURCE_MONGODB, storeMongoDBDS);

    SitoolsStore<Dictionary> storeDictionary = new DictionaryStoreXML(new File(
        settings.getStoreDIR(Consts.APP_DICTIONARIES_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_DICTIONARY, storeDictionary);

    SitoolsStore<ConceptTemplate> storeConceptTemplate = new ConceptTemplateStoreXML(new File(
        settings.getStoreDIR(Consts.APP_DICTIONARIES_TEMPLATES_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_TEMPLATE, storeConceptTemplate);

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

    SitoolsStore<FilterModel> storeFilterPlugin = new FilterPluginStoreXML(new File(
        settings.getStoreDIR(Consts.APP_PLUGINS_FILTERS_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_PLUGINS_FILTERS, storeFilterPlugin);

    SitoolsStore<ResourceModel> storeResourcePlugins = new ResourcePluginStoreXML(new File(
        settings.getStoreDIR(Consts.APP_PLUGINS_RESOURCES_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_PLUGINS_RESOURCES, storeResourcePlugins);

    // ======== converter store ===============
        
    new File(settings.getStoreDIR(Consts.APP_DATASETS_CONVERTERS_STORE_DIR) + "/map").mkdirs();
    ConverterStoreInterface storeConverter = new ConverterStoreXMLMap(new File(
        settings.getStoreDIR(Consts.APP_DATASETS_CONVERTERS_STORE_DIR) + "/map"), context);
    stores.put(Consts.APP_STORE_DATASETS_CONVERTERS, storeConverter);
    
    SitoolsStore<ConverterChainedModel> storeConvOLD = new ConverterStoreXML(new File(
        settings.getStoreDIR(Consts.APP_DATASETS_CONVERTERS_STORE_DIR)), context);
    if (storeConverter.getList().isEmpty()) {
      storeConverter.saveList(storeConvOLD.getList());
    }
    
    
    
    SitoolsStore<FilterChainedModel> storeFilter = new FilterStoreXML(new File(
        settings.getStoreDIR(Consts.APP_DATASETS_FILTERS_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_DATASETS_FILTERS, storeFilter);

    SitoolsStore<DatasetView> storeDsView = new DatasetViewStoreXML(new File(
        settings.getStoreDIR(Consts.APP_DATASETS_VIEWS_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_DATASETS_VIEWS, storeDsView);

    // ======== portal ===============

    new File(settings.getStoreDIR(Consts.APP_PORTAL_STORE_DIR) + "/map").mkdirs();
    PortalStoreInterface storePortal = new PortalStoreXmlMap(new File(settings.getStoreDIR(Consts.APP_PORTAL_STORE_DIR)
        + "/map"), context);
    stores.put(Consts.APP_STORE_PORTAL, storePortal);

    // Migrating Portal
    PortalStore storePortalOLD = new PortalStoreXmlImpl(new File(settings.getStoreDIR(Consts.APP_PORTAL_STORE_DIR)),
        context);
    if (!storePortalOLD.getList().isEmpty()) {
      storePortal.saveList(storePortalOLD.getList());
    }

    SitoolsStore<FormComponent> storefc = new FormComponentsStoreXML(new File(
        settings.getStoreDIR(Consts.APP_FORMCOMPONENTS_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_FORMCOMPONENT, storefc);
    
    // ======== collection ===============

    new File(settings.getStoreDIR(Consts.APP_COLLECTIONS_STORE_DIR)).mkdirs();
    CollectionStoreInterface storeCollections = new CollectionsStoreXMLMap(
        new File(settings.getStoreDIR(Consts.APP_COLLECTIONS_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_COLLECTIONS, storeCollections);
        
    // Migrating collection 
    SitoolsStore<Collection> storeCollectionsOLD = new CollectionsStoreXML(new File(
        settings.getStoreDIR(Consts.APP_COLLECTIONS_STORE_DIR)), context);
    if (storeCollections.getList().isEmpty()) {
      storeCollections.saveList(storeCollectionsOLD.getList());
    }


    SitoolsStore<FormProject> storeFormProject = new FormProjectStoreXML(new File(
        settings.getStoreDIR(Consts.APP_FORMPROJECT_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_FORMPROJECT, storeFormProject);

    SitoolsStore<Project> storePrj = new ProjectStoreXML(new File(settings.getStoreDIR(Consts.APP_PROJECTS_STORE_DIR)),
        context);
    stores.put(Consts.APP_STORE_PROJECT, storePrj);

    SitoolsStore<Graph> storeGraph = new GraphStoreXML(new File(settings.getStoreDIR(Consts.APP_GRAPHS_STORE_DIR)),
        context);
    stores.put(Consts.APP_STORE_GRAPH, storeGraph);

    SitoolsStore<Form> storeForm = new FormStoreXML(new File(settings.getStoreDIR(Consts.APP_FORMS_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_FORM, storeForm);

    FeedsStoreXML storeFeeds = new FeedsStoreXML(new File(settings.getStoreDIR(Consts.APP_FEEDS_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_FEED, storeFeeds);

    SitoolsStore<Opensearch> storeOS = new OpenSearchStoreXML(new File(
        settings.getStoreDIR(Consts.APP_OPENSEARCH_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_OPENSEARCH, storeOS);

    SitoolsStore<Order> storeOrd = new OrderStoreXML(new File(settings.getStoreDIR(Consts.APP_ORDERS_STORE_DIR)),
        context);
    stores.put(Consts.APP_STORE_ORDER, storeOrd);

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

    SitoolsStore<ProjectModuleModel> storeProjectModule = new ProjectModuleStoreXML(new File(
        settings.getStoreDIR(Consts.APP_PROJECTS_MODULES_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_PROJECTS_MODULES, storeProjectModule);

    SitoolsStore<GuiServiceModel> storeGuiService = new GuiServiceStoreXML(new File(
        settings.getStoreDIR(Consts.APP_GUI_SERVICES_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_GUI_SERVICE, storeGuiService);

    SitoolsStore<GuiServicePluginModel> storeGuiServicePlugin = new GuiServicePluginStoreXML(new File(
        settings.getStoreDIR(Consts.APP_GUI_SERVICES_PLUGIN_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_GUI_SERVICES_PLUGIN, storeGuiServicePlugin);

    SitoolsStore<ServiceCollectionModel> storeServices = new ServiceStoreXML(new File(
        settings.getStoreDIR(Consts.APP_SERVICES_STORE_DIR)), context);
    stores.put(Consts.APP_STORE_SERVICES, storeServices);

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
  @SuppressWarnings({"resource", "unchecked"})
  private static void migrateStores(Map<String, Object> stores) {
    for (Object store : stores.values()) {
      if (store instanceof SitoolsStore) {
        SitoolsStore<IResource> storeImpl = (SitoolsStore<IResource>) store;
        List<IResource> list = storeImpl.getList();
        for (IResource iResource : list) {
          storeImpl.update(iResource);
        }
      }
      else if (store instanceof PersistenceDao) {
        PersistenceDao<Persistent> storeImpl = (PersistenceDao<Persistent>) store;
        java.util.Collection<Persistent> list = storeImpl.getList();
        for (Persistent persistent : list) {
          storeImpl.update(persistent);
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
        if (store instanceof AuthorizationStore) {
          AuthorizationStore storeImpl = (AuthorizationStore) store;
          List<ResourceAuthorization> authorization = storeImpl.getList();
          for (ResourceAuthorization persistent : authorization) {
            storeImpl.update(persistent);
          }
        }
        else if (store instanceof UserStorageStoreXML) {
          UserStorageStore storeImpl = (UserStorageStore) store;
          List<UserStorage> userStorages = storeImpl.getList();
          for (UserStorage userStorage : userStorages) {
            storeImpl.update(userStorage);
          }
        }
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
  @SuppressWarnings({"resource", "unchecked"})
  private static void readStores(Map<String, Object> stores) throws SitoolsException {
    for (Object store : stores.values()) {
      if (store instanceof SitoolsStore) {
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
      else if (store instanceof PersistenceDao) {
        PersistenceDao<Persistent> storeImpl = (PersistenceDao<Persistent>) store;
        try {
          java.util.Collection<Persistent> list = storeImpl.getList();
          for (Persistent persistent : list) {
            storeImpl.get(persistent.getId());
          }
        }
        catch (Exception e) {
          throw new SitoolsException("ERROR WHILE LOADING STORE : " + storeImpl.getClass().getSimpleName(), e);
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
        if (store instanceof AuthorizationStore) {
          AuthorizationStore storeImpl = (AuthorizationStore) store;
          try {
            List<ResourceAuthorization> authorization = storeImpl.getList();
            for (IResource persistent : authorization) {
              storeImpl.retrieve(persistent.getId());
            }
          }
          catch (Exception e) {
            try {
              storeImpl.close();
              throw new SitoolsException("ERROR WHILE LOADING STORE : " + storeImpl.getClass().getSimpleName(), e);

            }
            catch (IOException e1) {
              throw new SitoolsException("ERROR WHILE LOADING STORE : " + storeImpl.getClass().getSimpleName(), e1);
            }
          }
        }
        else if (store instanceof UserStorageStoreXML) {
          UserStorageStore storeImpl = (UserStorageStore) store;

          try {
            List<UserStorage> userStorages = storeImpl.getList();
            for (UserStorage userStorage : userStorages) {
              storeImpl.retrieve(userStorage.getUserId());
            }
          }
          catch (Exception e) {
            try {
              storeImpl.close();
              throw new SitoolsException("ERROR WHILE LOADING STORE : " + storeImpl.getClass().getSimpleName(), e);
            }
            catch (IOException e1) {
              throw new SitoolsException("ERROR WHILE LOADING STORE : " + storeImpl.getClass().getSimpleName(), e1);
            }
          }
        }
      }

    }
  }
}
