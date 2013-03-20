/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, loadUrl*/
Ext.namespace('sitools.admin.applications.plugins');

/**
 * @class sitools.admin.applications.plugins.applicationResourcesCrudPanel
 * @extends Ext.Panel
 * @requires sitools.admin.resourcesPlugins.resourcesPluginsCrudPanel
 */
//sitools.component.applicationResources.applicationResourcesCrudPanel = Ext.extend(Ext.Panel, {
sitools.admin.applications.plugins.applicationResourcesCrudPanel = Ext.extend(Ext.Panel, {
    
    border : false,
    height : 300,    
    layout : 'fit',

    initComponent : function () {
        var resourcePluginapplication = new sitools.admin.resourcesPlugins.resourcesPluginsCrudPanel({
            urlParents : loadUrl.get('APP_URL') + loadUrl.get('APP_APPLICATIONS_URL'),
            urlParentsParams : '?customizable=true',
            resourcesUrlPart : loadUrl.get('APP_RESOURCES_URL'),
            urlResources : loadUrl.get('APP_URL') + loadUrl.get('APP_PLUGINS_RESOURCES_URL') + '/classes',
            parentType : "application"            
        });

        this.items = [ resourcePluginapplication ];

        sitools.admin.applications.plugins.applicationResourcesCrudPanel.superclass.initComponent.call(this);

    }

});

Ext.reg('s-application_resources', sitools.admin.applications.plugins.applicationResourcesCrudPanel);
