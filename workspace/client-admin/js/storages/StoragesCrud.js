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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp,loadUrl*/
/*
 * @include "../applications/applicationsRole.js"
 */
Ext.namespace('sitools.admin.storages');

/**
 * A Panel to show all the Storages in Sitools2
 * 
 * @cfg {String} the url of data storage directory
 * @cfg {String} the urlAuthorizations of authorizations
 * @cfg {String} the urlFilters of classes filters
 * @cfg {String} the urlParents of instances filters
 * @cfg {Ext.data.JsonStore} the store where saved the user storage data
 * @class sitools.admin.storages.StoragesCrud
 * @extends Ext.grid.GridPanel
 */
Ext.define('sitools.admin.storages.StoragesCrud', { 
    extend : 'Ext.grid.Panel',
	alias : 'widget.s-storages',
    border : false,
    height : ADMIN_PANEL_HEIGHT,
    id : ID.BOX.STORAGES,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    forceFit : true,
    mixins : {
        utils : "sitools.admin.utils.utils"
    },
    
    requires : ['sitools.admin.filtersPlugins.FiltersPluginsSingle',
                'sitools.admin.storages.StoragesProp',
                'sitools.admin.applications.ApplicationsRole',
                'sitools.admin.storages.plugins.StorageCopyProp'],

    initComponent : function () {
        
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_ADMIN_URL') + '/directories';
        this.urlAuthorizations = loadUrl.get('APP_URL') + loadUrl.get('APP_AUTHORIZATIONS_URL');
        this.urlFilters = loadUrl.get('APP_URL') + loadUrl.get('APP_PLUGINS_FILTERS_CLASSES_URL');
        this.urlParents = loadUrl.get('APP_URL') + loadUrl.get('APP_PLUGINS_FILTERS_INSTANCES_URL');
        
        this.store = Ext.create('Ext.data.JsonStore', {
            remoteSort : true,
            pageSize : this.pageSize,
            proxy : {
                type : 'ajax',
                url : this.url,
                reader : {
                    type : 'json',
                    root : 'data',
                    idProperty : 'id'
                }
            },
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'localPath',
                type : 'string'
            }, {
                name : 'publicUrl',
                type : 'string'
            }, {
                name : 'attachUrl',
                type : 'string'
            }, {
                name : 'deeplyAccessible',
                type : 'boolean'
            }, {
                name : 'listingAllowed',
                type : 'boolean'
            }, {
                name : 'modifiable',
                type : 'boolean'
            }, {
                name : 'status',
                type : 'string'
            } ]
        });
        
        var deeplyAccessible = {
            xtype : 'checkcolumn',
            header : i18n.get('label.deeplyAccessible'),
            dataIndex : 'deeplyAccessible',
            width : 80, 
            tooltip : i18n.get('label.deeplyAccessible'),
            processEvent: function () { return false; }
        };
        
        var listingAllowed = {
            xtype : 'checkcolumn',
            header : i18n.get('label.listingAllowed'),
            dataIndex : 'listingAllowed',
            width : 55, 
            tooltip : i18n.get('label.listingAllowed'),
            processEvent: function () { return false; }
        };
        
        var modifiable = {
            xtype : 'checkcolumn',
            header : i18n.get('label.modifiable'),
            dataIndex : 'modifiable',
            width : 55, 
            tooltip : i18n.get('label.modifiable'),
            processEvent: function () { return false; }
        };

        this.columns = {
            defaults : {
                sortable : false
            // columns are not sortable by default
            },              
            items : [{
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 50,
                sortable : true,
                renderer : function (value, meta, record) {
                    meta.style = "font-weight: bold;";
                    return value;
                }
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 120
            }, {
                header : i18n.get('label.localPath'),
                dataIndex : 'localPath',
                width : 150
            }, {
                header : i18n.get('label.attachUrl'),
                dataIndex : 'attachUrl',
                width : 150
            }, deeplyAccessible, listingAllowed, modifiable, {
                header : i18n.get('label.status'),
                dataIndex : 'status',
                width : 50,
                renderer : function (value, meta, record, index, colIndex, store) {
                    meta.tdCls += value;
                    return value;
                }
            }]
        };

        this.bbar = {
            xtype : 'pagingtoolbar',
            store : this.store,
            displayInfo : true,
            displayMsg : i18n.get('paging.display'),
            emptyMsg : i18n.get('paging.empty')
        };

        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create.png',
                handler : this.onCreate,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_edit.png',
                handler : this.onModify,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                handler : this.onDelete,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.authorizations'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_autorizations.png',
                handler : this.onDefineRole,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.customfilter'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/filters.png',
                xtype : 's-menuButton', 
                handler : this.onDefineFilter 
				
            }, {
                text : i18n.get('label.active'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_active.png',
                handler : this.onActive,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.disactive'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_disactive.png',
                handler : this.onDisactive,
                xtype : 's-menuButton'
            },{
                text : i18n.get('label.storageCopy'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/converter.png',
                handler : this.onCopy,
                xtype : 's-menuButton'
            },
            // { text: i18n.get('label.members'), icon:
            // 'res/images/icons/toolbar_group_add.png', handler: this.onMembers
            // },
            '->', {
                xtype : 's-filter',
                emptyText : i18n.get('label.search'),
                store : this.store,
                pageSize : this.pageSize
            } ]
        };

        this.listeners = {
            scope : this, 
            itemdblclick : this.onModify
        };
        
        this.selModel = Ext.create('Ext.selection.RowModel', {
            mode : "SINGLE"
        });
        
        this.callParent(arguments);
    },

    /**
     * do a specific render to load storages from the store.
     */
    afterRender : function () {
        this.callParent(arguments);
        this.store.load({
            start : 0,
            limit : this.pageSize,
            action : 'read'
        });
    },
    
    /**
     * Open a {sitools.admin.applications.ApplicationsRole} role panel to add a role authorization to the selected storage
     */
    onDefineRole : function () {
        var rec = this.getLastSelectedRecord();

        if (Ext.isEmpty(rec)) {
            return;
        }

        var up = Ext.create("sitools.admin.applications.ApplicationsRole", {
            urlAuthorizations : this.urlAuthorizations + "/" + rec.data.id,
            applicationRecord : rec
        });
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }

        if (rec.data.status == "STARTED") {
			return Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.wrongStatus'));
        }
        up.show(ID.BOX.STORAGES);
    },

    /**
     * Open a {sitools.admin.filtersPlugins.FiltersPluginsSingle} filter plugin panel to add a filter plugin to the selected storage
     */
    onDefineFilter : function (item, e) {
        var rec = this.getLastSelectedRecord();
        
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.noselection'));
        }
        if (rec.data.status == "STARTED") {
			return Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.wrongStatus'));
        }
        
        Ext.Ajax.request({
            url : this.url + "/" + rec.data.id + "/filter",
            method : "GET",
            scope : this,
            success : function (ret) {
                var data = Ext.decode(ret.responseText), action;
                if (data.success) {
                    action = "editDelete";
                } else {
                    action = "create";
                }
                var filterPlugin = data.filterPlugin;

                var up = Ext.create("sitools.admin.filtersPlugins.FiltersPluginsSingle", {
                    action : action,
                    parentPanel : this,
                    urlFilters : this.urlFilters,
                    urlParent : this.urlParents + "/" + rec.data.id,
                    parentType : 'storage',
                    filterPlugin : filterPlugin
                });
                up.show(ID.BOX.STORAGES);
            },
            failure : alertFailure
        });
    },
    
    /**
     * Open a {sitools.admin.storages.StoragesProp} storage property panel to create a new storage
     */
    onCreate : function () {
        var up = Ext.create("sitools.admin.storages.StoragesProp", {
            url : this.url,
            action : 'create',
            store : this.getStore()
        });
        up.show(ID.BOX.STORAGES);
    },

    /**
     * Open a {sitools.admin.storages.StoragesProp} storage property panel to create a new storage
     */
    onModify : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        var up = Ext.create("sitools.admin.storages.StoragesProp", {
            url : this.url + '/' + rec.data.id,
            action : 'modify',
            store : this.getStore()
        });
        up.show(ID.BOX.STORAGES);
    },

    /**
     * Diplay confirm delete Msg box and call the method doDelete
     */
    onDelete : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return false;
        }
        Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : Ext.String.format(i18n.get('storageCrud.delete'), rec.get('name')),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec);
                }
            }

        });

    },
    
    /**
     * done the delete of the passed record
     * @param rec the record to delete
     */
    doDelete : function (rec) {
        // var rec = this.getLastSelectedRecord();
        // if (!rec) return false;
        Ext.Ajax.request({
            url : this.url + "/" + rec.data.id,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });

    },
    
    /**
     * Activate the selected storage and set his status to "started"
     */
    onActive : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Ajax.request({
            url : this.url + "/" + rec.data.id + "?action=start",
            method : 'PUT',
            scope : this,
            success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },
    
    /**
     * Deactivate the selected storage and set his status to "stopped"
     */
    onDisactive : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Ajax.request({
            url : this.url + "/" + rec.data.id + "?action=stop",
            method : 'PUT',
            scope : this,
            success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });

    },
    
    onCopy : function (){
    	var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        var up = Ext.create("sitools.admin.storages.plugins.StorageCopyProp", {
            urlDirectories : this.url,
            idSrc : rec.data.id, 
            store : this.getStore()
        });
        up.show(ID.BOX.STORAGES);
    	
    }

});

