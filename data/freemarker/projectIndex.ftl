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
	<link rel="stylesheet" href="bootstrap.css">
    <!--<link rel="stylesheet" type="text/css" href="${appUrl}/cots/extjs/resources/css/ext-all.css">-->
	
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-public/res/css/desktop.css">
	<!--
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-public/res/css/statusbar.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-public/js/widgets/notification/css/Notification.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-public/js/widgets/reorderer/multiple-sorting.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-public/res/css/treegrid.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-public/res/css/spinner.css">

    <link rel="stylesheet" type="text/css" href="${appUrl}/client-user/js/components/dataviews/livegrid/css/ext-ux-livegrid.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-user/js/components/dataviews/livegrid/css/dataView.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-public/js/widgets/gridfilters/css/GridFilters.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-public/js/widgets/gridfilters/css/RangeMenu.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-public/js/widgets/multiSelect/css/multiSelect.css">

	<link rel="stylesheet" type="text/css" href="${appUrl}/client-public/res/css/combo.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-public/res/css/formComponents.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-public/res/css/main.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-public/js/widgets/fileUploadField/fileUploadField.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-public/js/widgets/imageChooser/imageChooser.css">-->
	
	<!-- HTML EDITOR ADVANCED CSS -->
	<!--<link rel="stylesheet" type="text/css" href="${appUrl}/client-public/js/widgets/htmlEditorAdvanced/resources/css/htmleditorplugins.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/cots/extjs/resources/css/xtheme-gray-custom.css">
	
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-user/js/components/dataviews/services/servicesToolbar.css">-->

	
<!-- --------------------------------------------------------------------------------------------------
						LISTE DES FICHIERS A INCLURE POUR LA VERSION DE DEV
--------------------------------------------------------------------------------------------------- -->

    <script src="/sitools/cots/extjs4/ext-4.2.1.883/ext-all-debug.js"></script>
	
	 <script type="text/javascript">
		Ext.Loader.setConfig('disableCaching', false);
	</script>
    <!--<script type="text/javascript" src="${appUrl}/cots/OpenLayers-2.11/OpenLayers.js"></script>
    <script type="text/javascript" src="${appUrl}/cots/GeoExt/script/GeoExt.js"></script>-->
	
	<script type="text/javascript" src="${appUrl}/client-public/js/widgets/ckeditor/ckeditor.js"></script>
	
<!-- BEGIN_JS_DEV_INCLUDES -->
	<script type="text/javascript" src="${appUrl}/client-public/js/utils/reference.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/app/utils/def.js"></script>
	<script src="${appUrl}/client-user/bootstrap.js"></script>
        <!-- </x-bootstrap> -->
        <script src="${appUrl}/client-user/app.js"></script>
<!-- END_JS_DEV_INCLUDES -->
 
<!-- --------------------------------------------------------------------------------------------------
 						A INCLURE POUR LA VERSION DE DEBUG
--------------------------------------------------------------------------------------------------- -->



<!-- --------------------------------------------------------------------------------------------------
 						A INCLURE POUR LA VERSION DE PROD
--------------------------------------------------------------------------------------------------- -->
	


    


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
	
	<link rel="shortcut icon" href="${appUrl}/client-public/res/images/icons/logo_fav_icone.ico" type="image/x-icon">
	
	<!-- CSS client-supervision -->
<!--	<link rel="stylesheet" type="text/css" href="${appUrl}/client-supervision/js/components/corot/dataview/css/dataView.css">-->
<!--	<link rel="stylesheet" type="text/css" href="${appUrl}/client-supervision/js/components/corot/dataview/css/ext-ux-livegrid.css">-->




  </head>

 
 
  <body>
	

	
	<div id="sitools-desktop">
		<#include "${project.ftlTemplateFile}">
	</div>
	
	
	<!--<div id="fisheye-menu-bottom" style="/*z-index: 12001;*/margin-bottom: 30px;"></div>-->

</body>
</html>
