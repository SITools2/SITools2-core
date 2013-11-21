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
    <title>SITOOLS WEB PROJECT CLIENT ${projectName!}</title>

    <link rel="stylesheet" type="text/css" href="${appUrl}/cots/extjs/resources/css/ext-all.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/desktop.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/statusbar.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/js/widgets/notification/css/Notification.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/common/js/widgets/reorderer/multiple-sorting.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/treegrid.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/spinner.css">

    <link rel="stylesheet" type="text/css" href="${appUrl}/client-user/js/components/dataviews/livegrid/css/ext-ux-livegrid.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-user/js/components/dataviews/livegrid/css/dataView.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/js/widgets/gridfilters/css/GridFilters.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/js/widgets/gridfilters/css/RangeMenu.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/js/widgets/multiSelect/css/multiSelect.css">

	<link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/combo.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/formComponents.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/main.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/common/js/widgets/fileUploadField/fileUploadField.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/common/js/widgets/imageChooser/imageChooser.css">
	
	<!-- HTML EDITOR ADVANCED CSS -->
	<link rel="stylesheet" type="text/css" href="${appUrl}/common/js/widgets/htmlEditorAdvanced/resources/css/htmleditorplugins.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/cots/extjs/resources/css/xtheme-gray-custom.css">
	
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-user/js/components/dataviews/services/servicesToolbar.css">

	
<!-- --------------------------------------------------------------------------------------------------
						LISTE DES FICHIERS A INCLURE POUR LA VERSION DE DEV
--------------------------------------------------------------------------------------------------- -->

    <script type="text/javascript" src="${appUrl}/common/js/plot/flotr2/lib/prototype.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/extjs/adapter/prototype/ext-prototype-adapter-debug.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/extjs/adapter/ext/ext-base-debug.js"></script>
	
    <script type="text/javascript" src="${appUrl}/cots/extjs/ext-all-debug.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/OpenLayers-2.11/OpenLayers.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/GeoExt/script/GeoExt.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/widgets/ckeditor/ckeditor.js"></script>
	
<!-- BEGIN_JS_DEV_INCLUDES -->

    <script type="text/javascript" src="${appUrl}/common/js/fileSaver/BlobBuilder.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/fileSaver/FileSaver.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/fileSaver/Canvas-toBlob.js"></script>

	<script type="text/javascript" src="${appUrl}/common/js/widgets/htmlEditorAdvanced/src/Ext.ux.form.HtmlEditor.MidasCommand.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/htmlEditorAdvanced/src/Ext.ux.form.HtmlEditor.Divider.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/htmlEditorAdvanced/src/Ext.ux.form.HtmlEditor.HR.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/htmlEditorAdvanced/src/Ext.ux.form.HtmlEditor.Image.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/htmlEditorAdvanced/src/Ext.ux.form.HtmlEditor.RemoveFormat.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/htmlEditorAdvanced/src/Ext.ux.form.HtmlEditor.IndentOutdent.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/htmlEditorAdvanced/src/Ext.ux.form.HtmlEditor.SubSuperScript.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/htmlEditorAdvanced/src/Ext.ux.form.HtmlEditor.RemoveFormat.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/htmlEditorAdvanced/src/Ext.ux.form.HtmlEditor.FindAndReplace.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/htmlEditorAdvanced/src/Ext.ux.form.HtmlEditor.Table.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/htmlEditorAdvanced/src/Ext.ux.form.HtmlEditor.Word.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/htmlEditorAdvanced/src/Ext.ux.form.HtmlEditor.Link.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/htmlEditorAdvanced/src/Ext.ux.form.HtmlEditor.SpecialCharacters.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/htmlEditorAdvanced/src/Ext.ux.form.HtmlEditor.UndoRedo.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/htmlEditorAdvanced/src/Ext.ux.form.HtmlEditor.Heading.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/htmlEditorAdvanced/src/Ext.ux.form.HtmlEditor.Plugins.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/widgets/htmlEditorAdvanced/src/imageExplorer.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/widgets/htmlEditorAdvanced/src/datasetLink.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/widgets/ckeditor/sitoolsPlugins/datasetBrowser.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/widgets/ckeditor/sitoolsPlugins/moduleBrowser.js"></script>
	
	

    <script type="text/javascript" src="${appUrl}/client-user/js/overrides/openLayers.js"></script>
    <script type="text/javascript" src="${appUrl}/client-user/js/overrides/geoExt.js"></script>
    <script type="text/javascript" src="${appUrl}/client-user/js/env.js"></script>
    <script type="text/javascript" src="${appUrl}/client-user/js/def.js"></script>
    <script type="text/javascript" src="${appUrl}/client-user/js/gui.js"></script>
    <script type="text/javascript" src="${appUrl}/client-user/js/id.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/utils.js"></script>
	
    <script type="text/javascript" src="${appUrl}/common/js/desktop/StartMenu.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/desktop/previewBox.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/desktop/TaskBar.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/desktop/Desktop.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/desktop/App.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/desktop/Module.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/checkcolumn.js"></script>

    <script type="text/javascript" src="${appUrl}/common/js/siteMap.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/sitoolsGridView.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/crypto/base64.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/crypto/MD5.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/crypto/digest.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/utils/console.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/utils/Date.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/utils/reference.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/utils/Utils.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/statusbar.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/userProfile/loginUtils.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/userProfile/login.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/userProfile/register.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/userProfile/resetPassword.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/userProfile/editProfile.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/vtype.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/Ext.ux.Plugin.RemoteComponent.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/multiSelect/Ext.ux.multiselect.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/Ext.ux.stateFullWindow.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/notification/Ext.ux.Notification.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/widgets/Ext.ux.PersistantSelectionGridPanel.js"></script>
	

    <script type="text/javascript" src="${appUrl}/common/js/widgets/sitoolsFilters/sitoolsFilter.js"></script>    
    <script type="text/javascript" src="${appUrl}/common/js/widgets/sitoolsFilters/sitoolsDateFilter.js"></script>    
    <script type="text/javascript" src="${appUrl}/common/js/widgets/sitoolsFilters/sitoolsStringFilter.js"></script>    
    <script type="text/javascript" src="${appUrl}/common/js/widgets/sitoolsFilters/sitoolsNumericFilter.js"></script>    
    <script type="text/javascript" src="${appUrl}/common/js/widgets/sitoolsFilters/sitoolsBooleanFilter.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/sitoolsFilters/sitoolsFiltersCollection.js"></script>    
    <script type="text/javascript" src="${appUrl}/common/js/widgets/rowExpander.js"></script>

    <script type="text/javascript" src="${appUrl}/common/js/widgets/sitoolsItems.js"></script>    

    <script type="text/javascript" src="${appUrl}/common/js/forms/formParameterToComponent.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/forms/DatasetContext.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/forms/ProjectContext.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/forms/ComponentFactory.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/forms/AbstractComponentsWithUnit.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/mif.js"></script>   
	<script type="text/javascript" src="${appUrl}/common/js/treegrid/TreeGridSorter.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/treegrid/TreeGridColumnResizer.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/treegrid/TreeGridNodeUI.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/treegrid/TreeGridLoader.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/treegrid/TreeGridColumns.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/treegrid/TreeGrid.js"></script>

	<script type="text/javascript" src="${appUrl}/common/js/utils/logout.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/feedsReader/feedsReader.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/feedsReader/rss2FeedsReader.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/feedsReader/atom1FeedsReader.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/feedsReader/feedItemDetails.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/utils/logout.js"></script>
       
    
    <script type="text/javascript" src="${appUrl}/client-user/js/desktop/navProfile/desktop.js"></script>
    <script type="text/javascript" src="${appUrl}/client-user/js/desktop/navProfile/fixed.js"></script>
    <script type="text/javascript" src="${appUrl}/client-user/js/desktop/desktop.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/data/featureReader.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/data/formatGeoJson.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/data/protocolHttp.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/data/protocolProxy.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/widgets/gridfilters/filter/Filter.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/widgets/gridfilters/filter/StringFilter.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/widgets/gridfilters/filter/DateFilter.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/widgets/gridfilters/filter/ListFilter.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/widgets/gridfilters/filter/NumericFilter.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/widgets/gridfilters/filter/BooleanFilter.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/widgets/gridfilters/menu/RangeMenu.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/widgets/gridfilters/menu/ListMenu.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/widgets/gridfilters/GridFilters.js"></script>
	
	<script type="text/javascript" src="${appUrl}/client-user/js/components/dataviews/livegrid/Ext.ux.livegrid/Ext.ux.livegrid-all-debug.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/dataviews/livegrid/Ext.ux.livegrid/CheckboxSelectionModel.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/dataviews/dataviewUtils.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/dataviews/storeUtils.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/dataviews/livegrid/storeLiveGrid.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/dataviews/cartoView/featureSelectionModel.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/dataviews/cartoView/mapPanel.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/dataviews/tplView/PagingToolbarTplView.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/dataviews/tplView/StoreTplView.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/dataviews/cartoView/featureStore.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/dataviews/resourcePluginParamsPanel.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/dataviews/goToTaskPanel.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/dataviews/services/serverServicesUtil.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/dataviews/services/menuServicesToolbar.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/dataviews/services/guiServiceController.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/dataviews/services/guiServicesStore.js"></script>
	
	
	
	<script type="text/javascript" src="${appUrl}/client-user/js/components/columnsDefinition/dependencies/columnsDefinition.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/datasetOverView/datasetOverview.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/entete/userProfile/taskDetails.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/entete/userProfile/diskSpace.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/entete/userProfile/tasks.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/entete/userProfile/viewTextPanel.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/entete/userProfile/viewOrder.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/entete/userProfile/orderProp.js"></script>
	

	<script type="text/javascript" src="${appUrl}/client-user/js/components/help/help.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/entete/entete.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/entete/navBar.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/entete/navBarButtons.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/entete/userProfile.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/bottom/bottom.js"></script>
	
	<script type="text/javascript" src="${appUrl}/client-user/js/components/forms/resultsProjectForm.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/forms/overviewResultsProjectForm.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/forms/projectForm.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/forms/formComponentsPanel.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/forms/mainContainer.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/datasetOpensearch/datasetOpensearch.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/datasetOpensearch/openSearchResultFeed.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/viewDataDetail/viewDataDetail.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/viewDataDetail/simpleViewDataDetails.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/opensearchXMLReader/CustomDomQuery.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/opensearchXMLReader/CustomXMLReader.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/version/sitoolsVersion.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/WindowImageViewer.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/spinner.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/spinnerfield.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/widgets/sitoolsDatePicker.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/widgets/imageChooser/imageChooser.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/utils/moduleUtils.js"></script>
		
    <script type="text/javascript" src="${appUrl}/common/js/widgets/fileUploadField/fileUploadField.js"></script>
	
	<script type="text/javascript" src="${appUrl}/client-user/js/sitoolsProject.js"></script>
	
	<script type="text/javascript" src="${appUrl}/common/js/columnRenderer/behaviorEnum.js"></script>
	
<!-- END_JS_DEV_INCLUDES -->
 
<!-- --------------------------------------------------------------------------------------------------
 						A INCLURE POUR LA VERSION DE DEBUG
--------------------------------------------------------------------------------------------------- -->
<!--	
	<script type="text/javascript" src="${appUrl}/common/js/widgets/ckeditor/ckeditor.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/plot/flotr2/lib/prototype.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/extjs/adapter/prototype/ext-prototype-adapter-debug.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/extjs/adapter/ext/ext-base-debug.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/extjs/ext-all-debug.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/OpenLayers-2.11/OpenLayers.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/GeoExt/script/GeoExt.js"></script>
	
	<script type="text/javascript" src="js/minified/client-user-project-all.js"></script> 
	

-->


<!-- --------------------------------------------------------------------------------------------------
 						A INCLURE POUR LA VERSION DE PROD
--------------------------------------------------------------------------------------------------- -->
	
 <!--
 	<script type="text/javascript" src="${appUrl}/common/js/widgets/ckeditor/ckeditor.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/plot/flotr2/lib/prototype.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/extjs/adapter/prototype/ext-prototype-adapter.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/extjs/ext-all.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/OpenLayers-2.11/OpenLayers.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/GeoExt/script/GeoExt.js"></script>

	<script type="text/javascript" src="${appUrl}/client-user/js/minified/client-user-project-all.min.js"></script>

-->

    


	<!-- Opensearch list -->
	<#list appDsOsDTOs as appDsOsDTO>
		<link rel="search" type="application/opensearchdescription+xml" title="${appDsOsDTO.shortName}" href="${appDsOsDTO.url}/opensearch.xml">
	</#list>
	<!-- End of Opensearch list -->
	
	<!-- RSS feed list of news for the project and the datasets -->
	<#list feeds as feed>
		<#if feed.feedType == "atom_1.0">
	  		<link rel="alternate" type="application/atom+xml" title="${feed.title}" href="${feed.url}/clientFeeds/${feed.id}">
		<#else>
	  		<link rel="alternate" type="application/rss+xml" title="${feed.title}" href="${feed.url}/clientFeeds/${feed.id}">
		</#if>  
	</#list>		
	<!-- End RSS feed list of news for the project and the datasets -->
		
	<meta http-equiv="Expires" content="0">
	<meta http-equiv="Pragma" content="no-cache">
	
	<meta NAME="author" content="CNES">
	<meta NAME="description" content="SITools2 is an open source framework for scientific archives. It provides both search capabilities, data access and web services integration.">
	
	<meta NAME="keywords" content="CNES, SITools2, archive, scientific, Data Access Layer, data, information system, ${projectName!} ">
	
	<meta NAME="DC.Title" content="${projectName!}">
	<meta NAME="DC.Creator" content="SITools2 - CNES">
	<!--<meta NAME="DC.Description" content="${projectDescription!}">-->
	
	<link rel="shortcut icon" href="${appUrl}/common/res/images/icons/logo_fav_icone.ico" type="image/x-icon">
	
	<!-- CSS client-supervision -->
<!--	<link rel="stylesheet" type="text/css" href="${appUrl}/client-supervision/js/components/corot/dataview/css/dataView.css">-->
<!--	<link rel="stylesheet" type="text/css" href="${appUrl}/client-supervision/js/components/corot/dataview/css/ext-ux-livegrid.css">-->




  </head>

 
 
  <body class="sitools ${projectCss!}">
	

	
	<div id="sitools-desktop">
		<#include "${project.ftlTemplateFile}">
	</div>
	
	
	<!--<div id="fisheye-menu-bottom" style="/*z-index: 12001;*/margin-bottom: 30px;"></div>-->

</body>
</html>
