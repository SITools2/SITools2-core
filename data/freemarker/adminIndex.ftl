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
	<!--  <link rel="stylesheet" type="text/css" href="${appUrl}/client-public/cots/extjs/resources/css/ext-all.css" ></link> -->
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-public/cots/extjs4/ext-4.2.1.883/resources/css/ext-all-neptune-debug.css"></link>

   	<!--  ext.ux  -->
       
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-public/res/css/statusbar.css"></link>
    <!--<link rel="stylesheet" type="text/css" href="${appUrl}/client-public/res/css/spinner.css"></link>-->
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-public/js/widget/imageChooser/imageChooser.css"></link>
    <link rel="stylesheet" type="text/css" href="res/css/commons.css"></link>
    
    
    <!-- Sitools specific css -->
    <link rel="stylesheet" type="text/css" href="res/css/main.css"></link>
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-public/js/ux/notification/css/Notification.css"></link>
    
    <link rel="stylesheet" type="text/css" href="res/css/icons.css"></link>
    <!--
    <link rel="stylesheet" type="text/css" href="res/css/animated-dataview.css"></link>
    <link rel="stylesheet" type="text/css" href="res/css/animated-seeAlso.css"></link>
    -->
   	<link rel="stylesheet" type="text/css" href="${appUrl}/client-public/res/css/combo.css"></link>
    <link rel="stylesheet" type="text/css" href="${appUrl}/client-public/res/css/main.css"></link>
	
	<link rel="stylesheet" type="text/css" href="res/css/quickStart.css"></link>
	<link rel="stylesheet" type="text/css" href="${appUrl}/client-public/js/ux/slider/css/SliderRange.css"></link>
  
<!-- --------------------------------------------------------------------------------------------------------------------------
					IMPORT DES LIBRAIRIES JS
-------------------------------------------------------------------------------------------------------------------------- -->
	<script type="text/javascript" src="${appUrl}/client-public/cots/extjs4/ext-4.2.1.883/ext-all-debug.js"></script>
	
	<!--<script type="text/javascript" src="${appUrl}/client-public/cots/OpenLayers-2.11/OpenLayers.js"></script>-->
	<!-- <script type="text/javascript" src="${appUrl}/client-public/cots/GeoExt/script/GeoExt.js"></script> -->
	
<!-- --------------------------------------------------------------------------------------------------
						LISTE DES FICHIERS A INCLURE POUR LA VERSION DE DEV
--------------------------------------------------------------------------------------------------- -->
<!-- BEGIN_JS_DEV_INCLUDES -->
		<script type="text/javascript" src="${appUrl}/client-public/js/widget/ckeditor/ckeditor.js"></script>		
		<script type="text/javascript" src="app.js"></script>	
<!-- END_JS_DEV_INCLUDES -->
  
<!-- --------------------------------------------------------------------------------------------------
 						A INCLURE POUR LA VERSION DE DEBUG
--------------------------------------------------------------------------------------------------- -->
<!--
	<script type="text/javascript" src="${appUrl}/client-public/js/widget/ckeditor/ckeditor.js"></script>
	<script type="text/javascript" src="dist/app.all.js"></script>
	<script type="text/javascript" src="app.js"></script>
-->

<!-- --------------------------------------------------------------------------------------------------
 						A INCLURE POUR LA VERSION DE PROD
--------------------------------------------------------------------------------------------------- -->
<!-- BEGIN_PROD
	<script type="text/javascript" src="${appUrl}/client-public/js/widget/ckeditor/ckeditor.js"></script>
	<script type="text/javascript" src="dist/app.min.js"></script>
	<script type="text/javascript" src="app.js"></script>
END PROD -->
    
	<link rel="shortcut icon" href="${appUrl}/client-public/res/images/icons/logo_fav_icone.ico" type="image/x-icon">
	
	<script type="text/javascript">
		Ext.Loader.setConfig('disableCaching', false);
	</script>
	
  </head>

  <body>
    
  </body>
</html>
