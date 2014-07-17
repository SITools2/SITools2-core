/***************************************
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
***************************************/
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, loadUrl, ADMIN_PANEL_HEIGHT*/
Ext.namespace('sitools.admin.applications.plugins');

/**
 * @class sitools.admin.applications.plugins.ApplicationResourcesCrud
 * @extends Ext.Panel
 * @requires sitools.admin.resourcesPlugins.ResourcesPluginsCrud
 */
Ext.define('sitools.admin.applications.plugins.ApplicationResourcesCrud', { 
    extend : 'sitools.admin.resourcesPlugins.ResourcesPluginsCrud', 
    alias : 'widget.s-application_resources',
    border : false,
    height : ADMIN_PANEL_HEIGHT,    
    layout : 'fit',

    initComponent : function () {
        Ext.apply(this, {
            urlParents : loadUrl.get('APP_URL') + loadUrl.get('APP_APPLICATIONS_URL'),
            urlParentsParams : '?customizable=true',
            resourcesUrlPart : loadUrl.get('APP_RESOURCES_URL'),
            urlResources : loadUrl.get('APP_URL') + loadUrl.get('APP_PLUGINS_RESOURCES_URL') + '/classes',
            parentType : "application"            
        });
        this.callParent(arguments);
    }

});

