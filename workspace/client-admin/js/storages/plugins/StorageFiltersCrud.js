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
Ext.namespace('sitools.admin.storages.plugins');

Ext.define('sitools.admin.storages.plugins.StorageFiltersCrud', { extend : 'Ext.panel.Panel',
    alias : 'widget.s-storage_filters',
    border : false,
    height : ADMIN_PANEL_HEIGHT,    
    layout : 'fit',
    
    requires : ['sitools.admin.filtersPlugins.filtersPluginsCrud'],

    initComponent : function () {
        var filterPluginstorage = Ext.create("sitools.admin.filtersPlugins.filtersPluginsCrud", {
            urlParents : loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_ADMIN_URL') + '/directories',
            filtersUrlPart : "/filters",
            urlFilters : loadUrl.get('APP_URL') + loadUrl.get('APP_PLUGINS_FILTERS_CLASSES_URL'),
            parentType : "storage"            
        });

        this.items = [ filterPluginstorage ];

        sitools.admin.storage.plugins.storageFilters.storageFiltersCrud.superclass.initComponent.call(this);

    }

});
