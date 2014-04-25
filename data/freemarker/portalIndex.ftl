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

	<link rel="stylesheet" href="${appUrl}/client-portal/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-public/cots/extjs4/ext-4.2.1.883/resources/css/ext-all-neptune-debug.css"></link>
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-portal/resources/css/portal.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-public/res/css/main.css">
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-public/res/css/statusbar.css"></link>
	
	<script src="${appUrl}/client-public/cots/extjs4/ext-4.2.1.883/ext-all-debug.js"></script>
	
	<script src="${appUrl}/client-portal/bootstrap.js"></script>
	<script src="${appUrl}/client-portal/app.js"></script>
	
	<script src="${appUrl}/client-public/js/utils/def.js"></script>
	<script src="${appUrl}/client-public/js/utils/id.js"></script>

   
	<link rel="shortcut icon" href="${appUrl}/common/res/images/icons/logo_fav_icone.ico" type="image/x-icon">
	
  </head>

  <body></body>
  
</html>
