<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>

  <head>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>SITOOLS WEB PROJECT CLIENT ${projectName!}</title>

    <link rel="stylesheet" type="text/css" href="${appUrl}/cots/extjs/resources/css/ext-all.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/desktop.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/statusbar.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/FisheyeMenu.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/js/components/commons/widgets/notification/css/Notification.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/treegrid.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/spinner.css">

    <link rel="stylesheet" type="text/css" href="${appUrl}/client-user/js/components/livegrid/dependencies/css/ext-ux-livegrid.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-user/js/components/livegrid/dependencies/css/dataView.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/js/components/commons/widgets/gridfilters/css/GridFilters.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/js/components/commons/widgets/gridfilters/css/RangeMenu.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/js/components/commons/widgets/multiSelect/css/multiSelect.css">

	<link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/combo.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/formComponents.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/main.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/common/js/components/commons/widgets/fileUploadField/fileUploadField.css">
<!-- --------------------------------------------------------------------------------------------------
						LISTE DES FICHIERS A INCLURE POUR LA VERSION DE DEV
--------------------------------------------------------------------------------------------------- -->

    <script type="text/javascript" src="${appUrl}/common/js/components/commons/plot/flotr/lib/prototype-1.6.0.2.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/extjs/adapter/prototype/ext-prototype-adapter-debug.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/extjs/adapter/ext/ext-base-debug.js"></script>
	
	
    <script type="text/javascript" src="${appUrl}/cots/extjs/ext-all-debug.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/OpenLayers-2.11/OpenLayers.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/GeoExt/script/GeoExt.js"></script>

<!-- BEGIN_JS_DEV_INCLUDES -->
    <script type="text/javascript" src="${appUrl}/client-user/js/env.js"></script>
    <script type="text/javascript" src="${appUrl}/client-user/js/def.js"></script>
    <script type="text/javascript" src="${appUrl}/client-user/js/gui.js"></script>
    <script type="text/javascript" src="${appUrl}/client-user/js/id.js"></script>
	
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/fisheye/Ext.ux.FisheyeMenu.js"></script>
  	<script type="text/javascript" src="${appUrl}/common/js/components/commons/fisheye/Ext.ux.FisheyeMenuExtention.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/desktop/StartMenu.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/desktop/TaskBar.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/desktop/Desktop.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/desktop/App.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/desktop/Module.js"></script>
	
    <script type="text/javascript" src="${appUrl}/common/js/siteMap.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/sitoolsGridView.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/crypto/base64.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/crypto/MD5.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/crypto/digest.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/utils/console.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/statusbar.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/login.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/register.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/resetPassword.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/editProfile.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/vtype.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/Ext.ux.Plugin.RemoteComponent.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/multiSelect/Ext.ux.multiselect.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/Ext.ux.stateFullWindow.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/notification/Ext.ux.Notification.js"></script>

    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/sitoolsFilters/sitoolsFilter.js"></script>    
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/sitoolsFilters/sitoolsDateFilter.js"></script>    
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/sitoolsFilters/sitoolsStringFilter.js"></script>    
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/sitoolsFilters/sitoolsNumericFilter.js"></script>    
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/sitoolsFilters/sitoolsBooleanFilter.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/sitoolsFilters/sitoolsFiltersCollection.js"></script>    

    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/sitoolsItems.js"></script>    

    <script type="text/javascript" src="${appUrl}/common/js/components/commons/forms/formParameterToComponent.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/forms/components/AbstractComponentsWithUnit.js"></script>
     <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/mif.js"></script>   
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/treegrid/TreeGridSorter.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/treegrid/TreeGridColumnResizer.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/treegrid/TreeGridNodeUI.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/treegrid/TreeGridLoader.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/treegrid/TreeGridColumns.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/treegrid/TreeGrid.js"></script>

	<script type="text/javascript" src="${appUrl}/common/js/components/commons/utils/logout.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/feedsReader/feedsReader.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/feedsReader/rss2FeedsReader.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/feedsReader/atom1FeedsReader.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/feedsReader/feedItemDetails.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/utils/logout.js"></script>
       
    
    <script type="text/javascript" src="${appUrl}/client-user/js/desktop/desktop.js"></script>

	<script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/gridfilters/filter/Filter.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/gridfilters/filter/StringFilter.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/gridfilters/filter/DateFilter.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/gridfilters/filter/ListFilter.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/gridfilters/filter/NumericFilter.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/gridfilters/filter/BooleanFilter.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/gridfilters/menu/RangeMenu.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/gridfilters/menu/ListMenu.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/gridfilters/GridFilters.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/livegrid/dependencies/CheckboxPlugin.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/livegrid/dependencies/Ext.ux.livegrid/Ext.ux.livegrid-all-debug.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/livegrid/dependencies/dataviewUtils.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/livegrid/dependencies/contextMenu.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/livegrid/dependencies/storeLiveGrid.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/columnsDefinition/dependencies/columnsDefinition.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/livegrid/dependencies/goToSvaTaskWindow.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/livegrid/dependencies/resourcePluginParamsWindow.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/livegrid/dependencies/goToTaskWindow.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/modules/userSpace/dependencies/svaTaskDetails.js"></script>

	
	
	<script type="text/javascript" src="${appUrl}/client-user/js/components/forms/forms.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/datasetOpensearch/datasetOpensearch.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/datasetOpensearch/openSearchResultFeed.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/viewDataDetail/viewDataDetail.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/viewDataDetail/simpleViewDataDetails.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/opensearchXMLReader/CustomDomQuery.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/opensearchXMLReader/CustomXMLReader.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/plot/flotr/lib/base64.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/plot/flotr/lib/canvas2image.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/plot/flotr/lib/canvastext.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/plot/flotr/flotr.debug-0.2.0-alpha.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/plot/dataPlotter.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/WindowImageViewer.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/version/sitoolsVersion.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/spinner.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/spinnerfield.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/sitoolsDatePicker.js"></script>
	
	
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/fileUploadField/fileUploadField.js"></script>
    
	<script type="text/javascript" src="${appUrl}/client-user/js/sitoolsProject.js"></script>

<!-- END_JS_DEV_INCLUDES -->
 
<!-- --------------------------------------------------------------------------------------------------
 						A INCLURE POUR LA VERSION DE DEBUG
--------------------------------------------------------------------------------------------------- -->
<!--	
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/plot/flotr/lib/prototype-1.6.0.2.js"></script>
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
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/plot/flotr/lib/prototype-1.6.0.2.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/extjs/adapter/prototype/ext-prototype-adapter.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/extjs/ext-all.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/OpenLayers-2.11/OpenLayers.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/GeoExt/script/GeoExt.js"></script>

	<script type="text/javascript" src="${appUrl}/client-user/js/minified/client-user-project-all-min.js"></script>
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

 
 
  <body class="${projectCss!}">
	
	<div id="x-desktop">
		<div id="toppanel">
		</div>
		<div id="bureau">
		</div>
	</div>
	 
	<div id="ux-taskbar">
		<div id="ux-taskbar-start"></div>
		<div id="ux-taskbuttons-panel"></div>
		<div class="x-clear"></div>
	</div>
	

	<div id="fisheye-menu-bottom" style="/*z-index: 12001;*/margin-bottom: 30px;"></div>

</body>
</html>
