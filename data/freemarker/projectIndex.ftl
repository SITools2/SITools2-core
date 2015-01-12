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
	
	<!-- CSS stylesheets  -->
	<link rel="stylesheet" href="bootstrap.css">
	
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-public/res/css/desktop.css">
	
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-user/resources/css/footer.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-user/resources/css/userProfilePopup.css"></link>
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-user/resources/css/header.css"></link>
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-user/resources/css/modulesDataview.css"></link>
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-user/resources/css/taskbars.css"></link>
		
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-public/res/css/main.css"></link>
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-public/res/css/feed.css"></link>
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-public/res/css/statusbar.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-public/res/css/formComponents.css"></link>

	<link rel="stylesheet" type="text/css" href="${appUrl}/client-public/js/widget/imageChooser/imageChooser.css"></link>
	
	
	<!-- Javascript sources  -->
	<script type="text/javascript" src="${appUrl}/client-public/js/utils/prototyp.js"></script>
	
<!-- --------------------------------------------------------------------------------------------------
						LISTE DES FICHIERS A INCLURE POUR LA VERSION DE DEV
--------------------------------------------------------------------------------------------------- -->
<!-- BEGIN_JS_DEV_INCLUDES -->
	<script type="text/javascript" src="${appUrl}/client-public/cots/OpenLayers-2.13.1/OpenLayers.debug.js"></script>
	<script type="text/javascript" src="${appUrl}/client-public/cots/extjs4/ext-4.2.1.883/ext-all-debug.js"></script>
	<script type="text/javascript" src="${appUrl}/client-public/cots/geoext2-2.0.2/geoext2.js"></script>
	<script type="text/javascript" src="${appUrl}/client-public/js/widget/ckeditor/ckeditor.js"></script>		
	<script src="${appUrl}/client-user/bootstrap.js"></script>
	<script type="text/javascript" src="app.js"></script>	
<!-- END_JS_DEV_INCLUDES -->
  
<!-- --------------------------------------------------------------------------------------------------
 						A INCLURE POUR LA VERSION DE DEBUG
--------------------------------------------------------------------------------------------------- -->
<!--
	
	<script type="text/javascript" src="${appUrl}/client-public/cots/OpenLayers-2.13.1/OpenLayers.debug.js"></script>
	<script type="text/javascript" src="${appUrl}/client-public/cots/extjs4/ext-4.2.1.883/ext-all-debug.js"></script>
	<script type="text/javascript" src="${appUrl}/client-public/cots/geoext2-2.0.2/geoext2.js"></script>
	
	<script src="${appUrl}/client-user/bootstrap.js"></script>
	<script type="text/javascript" src="${appUrl}/client-public/js/widget/ckeditor/ckeditor.js"></script>
	<script type="text/javascript" src="app.js"></script>
	<script type="text/javascript" src="dist/app.all.js"></script>	
-->


<!-- --------------------------------------------------------------------------------------------------
 						A INCLURE POUR LA VERSION DE PROD
--------------------------------------------------------------------------------------------------- -->
<!-- WITHOUT_PLUGIN_BEGIN_PROD
	<script type="text/javascript" src="${appUrl}/client-public/cots/OpenLayers-2.13.1/OpenLayers.js"></script>
	<script type="text/javascript" src="${appUrl}/client-public/cots/extjs4/ext-4.2.1.883/ext-all.js"></script>
	<script type="text/javascript" src="${appUrl}/client-public/cots/geoext2-2.0.2/geoext2.js"></script>

	<script src="${appUrl}/client-user/bootstrap.js"></script>
	<script type="text/javascript" src="${appUrl}/client-public/js/widget/ckeditor/ckeditor.js"></script>
	<script type="text/javascript" src="app.js"></script>
	<script type="text/javascript" src="dist/app.min.js"></script>	
<!-- WITHOUT_PLUGIN_END_PROD -->

<!-- BEGIN_PROD
	<script type="text/javascript" src="${appUrl}/client-public/cots/OpenLayers-2.13.1/OpenLayers.js"></script>
	<script type="text/javascript" src="${appUrl}/client-public/cots/extjs4/ext-4.2.1.883/ext-all.js"></script>
	<script type="text/javascript" src="${appUrl}/client-public/cots/geoext2-2.0.2/geoext2.js"></script>

	<script src="${appUrl}/client-user/bootstrap.js"></script>
	<script type="text/javascript" src="${appUrl}/client-public/js/widget/ckeditor/ckeditor.js"></script>
	<script type="text/javascript" src="app.js"></script>
	<script type="text/javascript" src="dist/app.withPlugins.min.js"></script>	
END PROD -->

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

	<link rel="shortcut icon" href="${appUrl}/client-public/res/images/icons/logo_fav_icone.ico" type="image/x-icon">

	<script type="text/javascript">
		Ext.Loader.setConfig('disableCaching', false);
	</script>

  </head>
 
  <body>
	<div id="sitools-desktop">
		<#include "${project.ftlTemplateFile}">
	</div>
</body>
</html>
