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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, loadUrl*/
Ext.namespace('sitools.admin.projectResources');

/**
 * A Panel to display project resource plugin informations
 * 
 * @class sitools.admin.projectResources.projectResourcesCrudPanel
 * @extends Ext.Panel
 */
Ext.define('sitools.admin.projectResources.projectResourcesCrudPanel', { 
    extend : 'Ext.panel.Panel',
    alias : 'widget.s-project_resources',
    border : false,
    height : ADMIN_PANEL_HEIGHT,    
    layout : 'fit',

    initComponent : function () {
        var resourcePluginProject = Ext.create("sitools.admin.resourcesPlugins.resourcesPluginsCrudPanel", {
            urlParents : loadUrl.get('APP_URL') + loadUrl.get('APP_PROJECTS_URL'),
            resourcesUrlPart : loadUrl.get('APP_RESOURCES_URL'),
            urlResources : loadUrl.get('APP_URL') + loadUrl.get('APP_PLUGINS_RESOURCES_URL') + '/classes',
            parentType : "project",
            appClassName : "fr.cnes.sitools.project.ProjectApplication" 
        });

        this.items = [ resourcePluginProject ];

        sitools.admin.projectResources.projectResourcesCrudPanel.superclass.initComponent.call(this);

    }

});


