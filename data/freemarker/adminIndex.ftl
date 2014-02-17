<!-- Copyright 2010- 2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
-- 
-- This file is part of SITools2.
-- 
-- SITools2 is free software: you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
-- 
-- SITools2 is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
-- 
-- You should have received a copy of the GNU General Public License
-- along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>

  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>SITOOLS WEB ADMIN</title>

    <!-- Commom css -->
	<!--  <link rel="stylesheet" type="text/css" href="${appUrl}/cots/extjs/resources/css/ext-all.css" ></link> -->
    <link rel="stylesheet" type="text/css" href="${appUrl}/cots/extjs/resources/css/ext-all.css"></link>

   	<!--  ext.ux  -->
       
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/statusbar.css"></link>
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/spinner.css"></link>
    <!-- Sitools specific css -->
    <link rel="stylesheet" type="text/css" href="res/css/main.css"></link>
    <link rel="stylesheet" type="text/css" href="res/css/commons.css"></link>
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/js/widgets/notification/css/Notification.css"></link>
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/js/widgets/lockingGrid/columnLock.css"></link>
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/js/widgets/imageChooser/imageChooser.css"></link>
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/js/widgets/fileUploadField/fileUploadField.css"></link>
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/js/widgets/sliderRange/css/ux/SliderRange.css"></link>
    <link rel="stylesheet" type="text/css" href="res/css/icons.css"></link>
    <link rel="stylesheet" type="text/css" href="res/css/animated-dataview.css"></link>
    <link rel="stylesheet" type="text/css" href="res/css/animated-seeAlso.css"></link>
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/main.css"></link>
   	<link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/combo.css"></link>
	
	<link rel="stylesheet" type="text/css" href="res/css/quickStart.css"></link>
  
<!-- --------------------------------------------------------------------------------------------------------------------------
					IMPORT DES LIBRAIRIES JS
-------------------------------------------------------------------------------------------------------------------------- -->
    <script type="text/javascript" src="${appUrl}/cots/extjs/adapter/ext/ext-base.js"></script>

    <!-- Need the Ext itself, either debug or production version. -->
<!--script type="text/javascript" src="${appUrl}/cots/extjs/ext-all-debug.js"></script-->
    <!--script type="text/javascript" src="${appUrl}/cots/extjs/ext-all.js"></script-->

	<script type="text/javascript" src="${appUrl}/cots/extjs4/ext-4.2.1.883/ext-all-debug.js"></script>
	
	<script type="text/javascript" src="${appUrl}/cots/extjs4/compatibility/ext3-core-compat.js"></script>   
 	<script type="text/javascript" src="${appUrl}/cots/extjs4/compatibility/ext3-compat.js"></script>   

	<script type="text/javascript" src="${appUrl}/cots/OpenLayers-2.11/OpenLayers.js"></script>
<!-- <script type="text/javascript" src="${appUrl}/cots/GeoExt/script/GeoExt.js"></script> -->
	
	<script type="text/javascript" src="${appUrl}/common/js/widgets/ckeditor/ckeditor.js"></script>

	
    <!-- Need in debug mode, to remove in production version. -->
    <script type="text/javascript" src="${appUrl}/common/js/utils/xpath.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/debug.js"></script>
<!-- --------------------------------------------------------------------------------------------------
						LISTE DES FICHIERS A INCLURE POUR LA VERSION DE DEV
--------------------------------------------------------------------------------------------------- -->
<!-- BEGIN_JS_DEV_INCLUDES -->
    <script type="text/javascript" src="${appUrl}/common/js/env.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/siteMap.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/crypto/base64.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/crypto/MD5.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/crypto/digest.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/utils/console.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/utils/Utils.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/utils/Date.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/utils/reference.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/statusbar.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/sliderRange/js/ux/SliderRange.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/userProfile/loginUtils.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/userProfile/login.js"></script>	
    <script type="text/javascript" src="${appUrl}/common/js/widgets/actionlink.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/highlighttext.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/checkcolumn.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/mif.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/textfilter.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/spinner.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/spinnerfield.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/panelSelectItems.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/gridUp.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/Ext.ux.Plugin.RemoteComponent.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/lockingGrid/columnLock.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/notification/Ext.ux.Notification.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/multiSelect/Ext.ux.multiselect.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/forms/formParameterToComponent.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/forms/DatasetContext.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/forms/ProjectContext.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/forms/ComponentFactory.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/forms/AbstractComponentsWithUnit.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/rowExpander.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/sitoolsItems.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/utils/logout.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/imageChooser/imageChooser.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/fileUploadField/fileUploadField.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/sitoolsImageSelect.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/vtype.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/version/sitoolsVersion.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/widgets/sitoolsDatePicker.js"></script>
	
    
    <script type="text/javascript" src="js/def.js"></script>
    <script type="text/javascript" src="js/id.js"></script>
    <script type="text/javascript" src="js/gui.js"></script>
    <script type="text/javascript" src="js/tools.js"></script>
	<script type="text/javascript" src="js/menu/dataView.js"></script>
	<script type="text/javascript" src="js/menu/seeAlso.js"></script>
	<script type="text/javascript" src="js/quickstart/qs.js"></script>
	<script type="text/javascript" src="js/quickstart/qsStart.js"></script>
	<script type="text/javascript" src="js/quickstart/qsProject.js"></script>
	<script type="text/javascript" src="js/quickstart/qsDatasource.js"></script>
	<script type="text/javascript" src="js/quickstart/qsDataset.js"></script>
	<script type="text/javascript" src="js/quickstart/qsSecurity.js"></script>
	<script type="text/javascript" src="js/quickstart/qsForm.js"></script>

	<script type="text/javascript" src="js/commonUtils/FormParametersConfigUtil.js"></script>
	
    <script type="text/javascript" src="js/menu/TreeMenu.js"></script>
    <script type="text/javascript" src="js/usergroups/RegCrudPanel.js"></script>
    <script type="text/javascript" src="js/usergroups/UserCrudPanel.js"></script>
    <script type="text/javascript" src="js/usergroups/GroupCrudPanel.js"></script>
    <script type="text/javascript" src="js/usergroups/RoleCrudPanel.js"></script>
    <script type="text/javascript" src="js/usergroups/RolePropPanel.js"></script>
    <script type="text/javascript" src="js/usergroups/RegPropPanel.js"></script>
    <script type="text/javascript" src="js/usergroups/UserPropPanel.js"></script>
    <script type="text/javascript" src="js/usergroups/GroupPropPanel.js"></script>
    <script type="text/javascript" src="js/usergroups/UsersPanel.js"></script>
    <script type="text/javascript" src="js/usergroups/GroupsPanel.js"></script>
    <script type="text/javascript" src="js/projects/datasetsWin.js"></script>
    <script type="text/javascript" src="js/projects/projectsprop.js"></script>
    <script type="text/javascript" src="js/projects/projectsCrud.js"></script>
    <script type="text/javascript" src="js/collections/CollectionsCrudPanel.js"></script>
    <script type="text/javascript" src="js/collections/CollectionsPropPanel.js"></script>
    <script type="text/javascript" src="js/multiDs/MultiDsCrudPanel.js"></script>
    <script type="text/javascript" src="js/multiDs/MultiDsPropPanel.js"></script>
    <script type="text/javascript" src="js/datasets/predicatsPanel.js"></script>
    <script type="text/javascript" src="js/datasets/selectPredicat.js"></script>
    <script type="text/javascript" src="js/datasets/joinConditionWin.js"></script>
    <script type="text/javascript" src="js/datasets/joinTableWin.js"></script>
    <script type="text/javascript" src="js/datasets/joinPanel.js"></script>
    <script type="text/javascript" src="js/datasets/gridFieldSetup.js"></script>
    <script type="text/javascript" src="js/datasets/datasetCriteria.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/sitoolsGridView.js"></script>
	<script type="text/javascript" src="js/datasets/datasourceUtils/datasourceFactory.js"></script>
    <script type="text/javascript" src="js/datasets/datasourceUtils/jdbcUtils.js"></script>
    <script type="text/javascript" src="js/datasets/datasourceUtils/mongoDbUtils.js"></script>
    <script type="text/javascript" src="js/datasets/datasetViewConfig.js"></script>
    <script type="text/javascript" src="js/datasets/datasetForm.js"></script>
    <script type="text/javascript" src="js/datasets/abstractDatasetProp.js"></script>
    <script type="text/javascript" src="js/datasets/datasetProperties.js"></script>
    <script type="text/javascript" src="js/datasets/datasetSelectTables.js"></script>
    <script type="text/javascript" src="js/datasets/datasetSelectFields.js"></script>
    <script type="text/javascript" src="js/datasets/datasetProp.js"></script>
		
    <script type="text/javascript" src="${appUrl}/common/js/columnRenderer/behaviorEnum.js"></script>
    <script type="text/javascript" src="js/datasets/columnRenderer/datasetLinkPanel.js"></script>
	<script type="text/javascript" src="js/datasets/columnRenderer/urlPanel.js"></script>
	<script type="text/javascript" src="js/datasets/columnRenderer/imagePanel.js"></script>
    <script type="text/javascript" src="js/datasets/columnRendererWin.js"></script>

    <script type="text/javascript" src="js/datasets/datasetUrlWin.js"></script>
    <script type="text/javascript" src="js/datasets/columnProp.js"></script>
    <script type="text/javascript" src="js/datasets/datasetsCrud.js"></script>
    <script type="text/javascript" src="js/datasets/dictionaryWin.js"></script>
    <script type="text/javascript" src="js/datasets/unitWin.js"></script>
    <script type="text/javascript" src="js/datasets/datasetsOpenSearch.js"></script>
    <script type="text/javascript" src="js/dictionary/dictionaryprop.js"></script>
    <script type="text/javascript" src="js/dictionary/dictionaryCrud.js"></script>
    <script type="text/javascript" src="js/dictionary/templateprop.js"></script>
    <script type="text/javascript" src="js/dictionary/templateCrud.js"></script>
    <script type="text/javascript" src="js/dictionary/selectDictionary.js"></script>
    <script type="text/javascript" src="js/datasource/jdbc/databasecrud.js"></script>
    <script type="text/javascript" src="js/datasource/jdbc/databaseprop.js"></script>
    <script type="text/javascript" src="js/datasource/databasetest.js"></script>
    <script type="text/javascript" src="js/datasource/mongoDb/databasecrud.js"></script>
    <script type="text/javascript" src="js/datasource/mongoDb/databaseprop.js"></script>
    <script type="text/javascript" src="js/datasource/mongoDb/databaseExplorer.js"></script>
    <script type="text/javascript" src="js/datasource/mongoDb/collectionExplorer.js"></script>
    <script type="text/javascript" src="js/datasource/mongoDb/recordsPanel.js"></script>
    <script type="text/javascript" src="js/forms/componentsAdminDef/DatasetContext.js"></script>
    <script type="text/javascript" src="js/forms/componentsAdminDef/ProjectContext.js"></script>
    <script type="text/javascript" src="js/forms/componentsAdminDef/ComponentFactory.js"></script>
    <script type="text/javascript" src="js/forms/FormGridComponents.js"></script>
    <script type="text/javascript" src="js/forms/ComponentsDisplayPanel.js"></script>
    <script type="text/javascript" src="js/forms/formsCrud.js"></script>
    <script type="text/javascript" src="js/forms/componentPropPanel.js"></script>
    <script type="text/javascript" src="js/forms/absoluteLayoutProp.js"></script>
	<script type="text/javascript" src="js/forms/setupAdvancedFormPanel.js"></script>
    <script type="text/javascript" src="js/forms/componentsListPanel.js"></script>
    <script type="text/javascript" src="js/forms/formPropPanel.js"></script>
    <script type="text/javascript" src="js/forms/winParent.js"></script>
    <script type="text/javascript" src="js/forms/componentsAdminDef/OneParam/Abstract.js"></script>
    <script type="text/javascript" src="js/forms/componentsAdminDef/OneParam/AbstractWithUnit.js"></script>
    <script type="text/javascript" src="js/forms/componentsAdminDef/MultiParam/Abstract.js"></script>
    <script type="text/javascript" src="js/forms/advancedFormPanel.js"></script>

    <script type="text/javascript" src="js/applications/applicationsRole.js"></script>
    <script type="text/javascript" src="js/applications/roleWin.js"></script>
    <script type="text/javascript" src="js/applications/applicationsprop.js"></script>
    <script type="text/javascript" src="js/applications/applicationsCrud.js"></script>
    <script type="text/javascript" src="js/authorizations/authorizationsCrud.js"></script>
    <script type="text/javascript" src="js/userStorage/userStoragePropPanel.js"></script>
    <script type="text/javascript" src="js/userStorage/userStorageCrudPanel.js"></script>
    <script type="text/javascript" src="js/order/orderprop.js"></script>
    <script type="text/javascript" src="js/order/orderCrud.js"></script>
    <script type="text/javascript" src="js/order/events.js"></script>
    <script type="text/javascript" src="js/graphs/graphsCrud.js"></script>
    <script type="text/javascript" src="js/graphs/graphsDatasetWin.js"></script>
    <script type="text/javascript" src="js/graphs/graphsNodeWin.js"></script>   
    <script type="text/javascript" src="js/datasets/selectColumn.js"></script> 
    <script type="text/javascript" src="js/converters/convertersCrud.js"></script>
    <script type="text/javascript" src="js/converters/convertersProp.js"></script>
    <script type="text/javascript" src="js/filters/filtersCrud.js"></script>
    <script type="text/javascript" src="js/filters/filtersProp.js"></script>
    <script type="text/javascript" src="js/formComponents/formComponentscrud.js"></script>
    <script type="text/javascript" src="js/formComponents/formComponentsprop.js"></script>
    <script type="text/javascript" src="js/datasetViews/datasetViewscrud.js"></script>
    <script type="text/javascript" src="js/datasetViews/datasetViewprop.js"></script>
    <script type="text/javascript" src="js/rssFeed/rssFeedCrud.js"></script>
    <script type="text/javascript" src="js/rssFeed/rssFeedProps.js"></script>
    <script type="text/javascript" src="js/rssFeed/rssFeedItemProps.js"></script>
    <script type="text/javascript" src="js/projects/rssFeedProjects.js"></script>
    <script type="text/javascript" src="js/datasets/rssFeedDatasets.js"></script>
    <script type="text/javascript" src="js/portal/rssFeedPortalCrud.js"></script>
    
    <script type="text/javascript" src="js/applications/plugins/applicationPluginCrud.js"></script>
    <script type="text/javascript" src="js/applications/plugins/applicationPluginProp.js"></script>
    
    <script type="text/javascript" src="js/storage/storagesCrudPanel.js"></script>
    <script type="text/javascript" src="js/storage/storagesPropPanel.js"></script>
    
    <script type="text/javascript" src="js/datasets/services/datasetServicesCrud.js"></script>
	<script type="text/javascript" src="js/datasets/services/datasetServicesProp.js"></script>
	<script type="text/javascript" src="js/datasets/services/datasetServicesConfig.js"></script>
	<script type="text/javascript" src="js/datasets/services/datasetServicesCopyProp.js"></script>
	
    <script type="text/javascript" src="js/resourcesPlugins/resourcesPluginsCrudPanel.js"></script>
    <script type="text/javascript" src="js/resourcesPlugins/resourcesServicesCopyProp.js"></script>
    <script type="text/javascript" src="js/resourcesPlugins/resourcesPluginsProp.js"></script>
    <script type="text/javascript" src="js/resourcesPlugins/enumerationValueTypeSelector.js"></script>
    
    
    <script type="text/javascript" src="js/projects/plugins/projectResourcesCrudPanel.js"></script>
    <script type="text/javascript" src="js/datasets/plugins/datasetResourcesCrud.js"></script>
    <script type="text/javascript" src="js/applications/plugins/applicationResourcesCrud.js"></script>
    
    <script type="text/javascript" src="js/filtersPlugins/filtersPluginsProp.js"></script>
  
    <script type="text/javascript" src="js/filtersPlugins/filtersPluginsSingle.js"></script>
     
    <script type="text/javascript" src="js/fileEditor/ftlEditorCrud.js"></script> 
    <script type="text/javascript" src="js/fileEditor/cssEditorCrud.js"></script> 
    <script type="text/javascript" src="js/fileEditor/licenceEditorProp.js"></script> 
    <script type="text/javascript" src="js/fileEditor/fileEditorProp.js"></script>
	
	<script type="text/javascript" src="js/logs/analogProp.js"></script> 	
    
    <script type="text/javascript" src="js/storage/plugins/storageFiltersCrud.js"></script>
	<script type="text/javascript" src="js/storage/plugins/storageCopyProp.js"></script>

     
    <script type="text/javascript" src="js/units/unitsCrudPanel.js"></script>
    <script type="text/javascript" src="js/units/unitsProp.js"></script>
    
	<script type="text/javascript" src="js/datasets/datasetsDicoMapping.js"></script>
	<script type="text/javascript" src="js/dictionary/dictionaryGridPanel.js"></script> 
         
    <script type="text/javascript" src="js/projects/modules/ProjectModulePropPanel.js"></script>
    <script type="text/javascript" src="js/projects/modules/ProjectModulesCrudPanel.js"></script>
	<script type="text/javascript" src="js/projects/modules/ProjectModuleConfig.js"></script>
	
	<script type="text/javascript" src="js/usergroups/RolesPanel.js"></script>
	
	<script type="text/javascript" src="js/guiServices/guiServicesCrudPanel.js"></script>
	<script type="text/javascript" src="js/guiServices/guiServicesPropPanel.js"></script>
	<script type="text/javascript" src="js/guiServices/guiServicesStore.js"></script>
	<script type="text/javascript" src="js/guiServices/dependenciesPanel.js"></script>
	<script type="text/javascript" src="js/guiServices/guiServicesStore.js"></script>
	
	
	
	
<!-- END_JS_DEV_INCLUDES -->
  
<!-- --------------------------------------------------------------------------------------------------
 						A INCLURE POUR LA VERSION DE DEBUG
--------------------------------------------------------------------------------------------------- -->
<!--
	<script type="text/javascript" src="js/minified/client-admin-all.js"></script>
-->

<!-- --------------------------------------------------------------------------------------------------
 						A INCLURE POUR LA VERSION DE PROD
--------------------------------------------------------------------------------------------------- -->
<!--
	<script type="text/javascript" src="js/minified/client-admin-all-min.js"></script>
-->
    
	<link rel="shortcut icon" href="${appUrl}/common/res/images/icons/logo_fav_icone.ico" type="image/x-icon">
	
  </head>

  <body>
    <script type="text/javascript">
		Ext.onReady(function(){
			i18n.load('${appUrl}/common/res/i18n/'+LOCALE+'/gui.properties', function() {
			    loadUrl.load('${appUrl}/client-admin/siteMap', function () {
					Ext.MessageBox.buttonText.yes = i18n.get('label.yes');
		      		Ext.MessageBox.buttonText.no = i18n.get('label.no');
						Ext.QuickTips.init();
						if (Ext.isEmpty(Ext.util.Cookies.get('userLogin'))) {
							/*new sitools.userProfile.Login({
			        			url:'${appUrl}/login',
			        			handler : initAppli
			        		}).show();*/
							sitools.userProfile.LoginUtils.connect({
								url:'${appUrl}/login',
			        			handler : initAppli
							});
						}
						else {
							initAppli();
						}
				    });

    		});
        });

    </script>
  </body>
</html>
