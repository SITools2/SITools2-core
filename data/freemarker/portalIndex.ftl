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

	<meta http-equiv="Expires" content="0">
	<meta http-equiv="Pragma" content="no-cache">
	
	<meta NAME="author" content="CNES">
	<meta NAME="description" content="SITools2 is an open source framework for scientific archives. It provides both search capabilities, data access and web services integration.">
	
	<meta NAME="keywords" content="CNES, SITools2, open source, web service, archive,  scientific, Data Access Layer, information system, data server, tool, ${projectList}, open search, interoperability">
	
	<meta NAME="DC.Title" content="SITools2 - Scientific archive">
	<meta NAME="DC.Creator" content="SITools2 - CNES">
	<meta NAME="DC.Description" content="${projectList}">
	
	<!-- RSS feed list of news for the project and the datasets -->
	<#list feeds as feed>
		<#if feed.feedType == "atom_1.0">
	  		<link rel="alternate" type="application/atom+xml" title="${feed.title}" href="${feed.url}/clientFeeds/${feed.id}">
		<#else>
	  		<link rel="alternate" type="application/rss+xml" title="${feed.title}" href="${feed.url}/clientFeeds/${feed.id}">
		</#if>  
	</#list>		
	<!-- End RSS feed list of news for the project and the datasets -->
	
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>SITOOLS WEB CLIENT</title>

    <link rel="stylesheet" type="text/css" href="${appUrl}/cots/extjs/resources/css/ext-all.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/desktop.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/statusbar.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-user/res/css/portal.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/main.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/js/widgets/notification/css/Notification.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/common/js/widgets/reorderer/multiple-sorting.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/common/res/css/combo.css">
    
	<!-- First of javascript includes must be an adapter... -->
    <!--script type="text/javascript" src="/cots/extjs/adapter/ext/ext-base-debug.js"></script-->
    <script type="text/javascript" src="${appUrl}/cots/extjs/adapter/ext/ext-base.js"></script>

    <!-- Need the Ext itself, either debug or production version. -->
    <script type="text/javascript" src="${appUrl}/cots/extjs/ext-all-debug.js"></script>
    <!--script type="text/javascript" src="${appUrl}/cots/extjs/ext-all.js"></script-->

<!-- --------------------------------------------------------------------------------------------------
						LISTE DES FICHIERS A INCLURE POUR LA VERSION DE DEV
--------------------------------------------------------------------------------------------------- -->
<!-- BEGIN_JS_DEV_INCLUDES -->
	<script type="text/javascript" src="${appUrl}/common/js/crypto/base64.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/crypto/MD5.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/crypto/digest.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/utils/console.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/statusbar.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/userProfile/loginUtils.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/userProfile/login.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/userProfile/register.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/userProfile/resetPassword.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/userProfile/editProfile.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/vtype.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/mif.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/utils/logout.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/feedsReader/feedsReader.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/feedsReader/rss2FeedsReader.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/feedsReader/atom1FeedsReader.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/feedsReader/feedItemDetails.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/widgets/notification/Ext.ux.Notification.js"></script>
   
    <script type="text/javascript" src="${appUrl}/common/js/widgets/Ext.ux.Plugin.RemoteComponent.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/portal/Portal.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/portal/PortalColumn.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/portal/Portlet.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/siteMap.js"></script>
	
    <script type="text/javascript" src="js/env.js"></script>
    <script type="text/javascript" src="js/def.js"></script>
    <script type="text/javascript" src="js/gui.js"></script>
    <script type="text/javascript" src="js/id.js"></script>
	<script type="text/javascript" src="js/portal/portal.js"></script>
	<script type="text/javascript" src="js/portal/portlets/feedReaderPortal.js"></script>
	
	<script type="text/javascript" src="js/portal/portlets/portalOpensearch.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/datasetOpensearch/openSearchResultFeed.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/opensearchXMLReader/CustomDomQuery.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/opensearchXMLReader/CustomXMLReader.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/viewDataDetail/viewDataDetail.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/viewDataDetail/simpleViewDataDetails.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/version/sitoolsVersion.js"></script>

<!-- END_JS_DEV_INCLUDES -->
  
<!-- --------------------------------------------------------------------------------------------------
 						A INCLURE POUR LA VERSION DE DEBUG
--------------------------------------------------------------------------------------------------- -->
<!--	<script type="text/javascript" src="js/minified/client-user-portal-all.js"></script> -->
	
<!-- --------------------------------------------------------------------------------------------------
 						A INCLURE POUR LA VERSION DE PROD
--------------------------------------------------------------------------------------------------- -->
<!--
	<script type="text/javascript" src="js/minified/client-user-portal-all.min.js"></script> 
-->	
	<link rel="shortcut icon" href="${appUrl}/common/res/images/icons/logo_fav_icone.ico" type="image/x-icon">
	
  </head>

  <body>
	
	<script type="text/javascript">
    	Ext.onReady(function(){
			portalApp.initAppliPortal({
	    		siteMapRes : '${appUrl}/client-user'
			});
        });
    </script>

</body>
</html>
