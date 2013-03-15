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
    <link rel="stylesheet" type="text/css" href="${appUrl}/common/js/components/commons/widgets/notification/css/Notification.css">
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
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/mif.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/utils/logout.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/feedsReader/feedsReader.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/feedsReader/rss2FeedsReader.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/feedsReader/atom1FeedsReader.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/feedsReader/feedItemDetails.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/notification/Ext.ux.Notification.js"></script>
   
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/Ext.ux.Plugin.RemoteComponent.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/portal/Portal.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/portal/PortalColumn.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/portal/Portlet.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/widgets/ext-basex.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/siteMap.js"></script>
	
    <script type="text/javascript" src="js/env.js"></script>
    <script type="text/javascript" src="js/def.js"></script>
    <script type="text/javascript" src="js/gui.js"></script>
    <script type="text/javascript" src="js/id.js"></script>
	<script type="text/javascript" src="js/portal/portal.js"></script>
	<script type="text/javascript" src="js/portal/portlets/feedReaderPortal.js"></script>
	
	<script type="text/javascript" src="js/portal/portlets/portalOpensearch.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/datasetOpensearch/openSearchResultFeed.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/opensearchXMLReader/CustomDomQuery.js"></script>
	<script type="text/javascript" src="${appUrl}/common/js/components/commons/opensearchXMLReader/CustomXMLReader.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/viewDataDetail/viewDataDetail.js"></script>
	<script type="text/javascript" src="${appUrl}/client-user/js/components/viewDataDetail/simpleViewDataDetails.js"></script>
    <script type="text/javascript" src="${appUrl}/common/js/components/commons/version/sitoolsVersion.js"></script>

<!-- END_JS_DEV_INCLUDES -->
  
<!-- --------------------------------------------------------------------------------------------------
 						A INCLURE POUR LA VERSION DE DEBUG
--------------------------------------------------------------------------------------------------- -->
<!--	<script type="text/javascript" src="js/minified/client-user-portal-all.js"></script> -->
	
<!-- --------------------------------------------------------------------------------------------------
 						A INCLURE POUR LA VERSION DE PROD
--------------------------------------------------------------------------------------------------- -->
<!--	<script type="text/javascript" src="js/minified/client-user-portal-all-min.js"></script> -->
	
	<link rel="shortcut icon" href="${appUrl}/common/res/images/icons/logo_fav_icone.ico" type="image/x-icon">
	
  </head>

  <body>
	
	<script type="text/javascript">
    	Ext.onReady(function(){
			portal.initAppliPortal({
	    		siteMapRes : '${appUrl}/client-user'
			});
        });
    </script>

</body>
</html>
