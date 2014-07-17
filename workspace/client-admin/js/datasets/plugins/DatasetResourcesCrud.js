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
/*
 * @include "../../resourcesPlugins/resourcesPluginsCrud.js"
 */
Ext.namespace('sitools.admin.datasets.plugins');

/**
 * A panel to managed Dataset resources.
 * @requires sitools.admin.resourcesPlugins.ResourcesPluginsCrud
 * @class sitools.admin.datasets.plugins.resourcesCrud
 * @extends Ext.Panel
 */
Ext.define('sitools.admin.datasets.plugins.datasetResourcesCrud', { 
    extend : 'Ext.Panel',
    alias : 'widget.s-dataset_resources',
    border : false,
    height : ADMIN_PANEL_HEIGHT,    
    layout : 'fit',
    
    requires : ['sitools.admin.resourcesPlugins.ResourcesPluginsCrud'],

    initComponent : function () {
        var resourcePlugindataset = Ext.create("sitools.admin.resourcesPlugins.ResourcesPluginsCrud", {
            urlParents : loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_URL'),
            resourcesUrlPart : loadUrl.get('APP_RESOURCES_URL'),
            urlResources : loadUrl.get('APP_URL') + loadUrl.get('APP_PLUGINS_RESOURCES_URL') + '/classes',
            parentType : "dataset",
            appClassName : "fr.cnes.sitools.dataset.DataSetApplication"
        });

        this.items = [ resourcePlugindataset ];

        sitools.admin.datasets.plugins.datasetResourcesCrud.superclass.initComponent.call(this);

    }

});

