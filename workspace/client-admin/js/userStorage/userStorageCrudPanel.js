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
 showHelp, loadUrl*/
Ext.namespace('sitools.admin.userStorage');

/**
 * A Panel to show all the user Storage in Sitools2
 * 
 * @cfg {String} the url where get the resource
 * @cfg {Ext.data.JsonStore} the store where saved the user storage data
 * @class sitools.admin.userStorage.userStorageCrudPanel
 * @extends Ext.grid.GridPanel
 */
Ext.define('sitools.admin.userStorage.userStorageCrudPanel', { 
    extend :'Ext.grid.Panel',
	alias : 'widget.s-userStorage',
    border : false,
    height : ADMIN_PANEL_HEIGHT,
    id : ID.BOX.USERSTORAGE,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    forceFit : true,
    mixins : {
        utils : "js.utils.utils"
    },
    
    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_USERSTORAGE_URL') + '/users';
        
        this.store = Ext.create('Ext.data.JsonStore', {
            pageSize : this.pageSize,
            proxy : {
                type : 'ajax',
                url : this.url,
                simpleSortMode : true,
                reader : {
                    type : 'json',
                    root : 'data',
                    idProperty : 'userId'
                }
            },
            remoteSort : true,
            fields : [ {
                name : 'userId',
                type : 'string'
            }, {
                name : 'status',
                type : 'string',
                mapping : 'status'
            }, {
                name : 'freeUserSpace',
                type : 'float',
                mapping : 'storage.freeUserSpace'
            }, {
                name : 'busyUserSpace',
                type : 'float',
                mapping : 'storage.busyUserSpace'
            }, {
                name : 'quota',
                type : 'float',
                mapping : 'storage.quota'
            }, {
                name : 'userStoragePath',
                type : 'string',
                mapping : 'storage.userStoragePath'
            } ]
        });

        /**
         * {Ext.grid.ColumnModel} the columns definition for the store
         */
        this.columns = {
            // specify any defaults for each column
            defaults : {
                sortable : false
            // columns are not sortable by default
            },
            items : [ {
                header : i18n.get('label.userLogin'),
                dataIndex : 'userId',
                sortable : true,
                width : 100,
                renderer : function (value, meta, record) {
                    meta.style = "font-weight: bold;";
                    return value;
                }
            }, {
                header : i18n.get('label.userStoragePath'),
                dataIndex : 'userStoragePath',
                width : 160
            }, {
                header : i18n.get('label.freeUserSpace'),
                dataIndex : 'freeUserSpace',
                width : 80,
                renderer : function (value) {
					return Ext.util.Format.fileSize(value);
				}
            }, {
                header : i18n.get('label.busyUserSpace'),
                dataIndex : 'busyUserSpace',
                width : 80,
                renderer : function (value) {
					return Ext.util.Format.fileSize(value);
				}
            }, {
                header : i18n.get('label.quota'),
                dataIndex : 'quota',
                width : 80,
                renderer : function (value) {
					return Ext.util.Format.fileSize(value);
				}
            }, {
                header : i18n.get('label.usedPourcentage'),
                width : 80,
                renderer : function (value, metaData, record, rowIndex, colIndex, store) {
					var totalSpace = record.get("quota");
	                var usedSpace = record.get("busyUserSpace");
	                var pourcentage = usedSpace / totalSpace * 100;
                    if (pourcentage >= 90 && pourcentage < 100) {
	                    metaData.css = "sitools-userProfile-warning-text";
	                }
	                else if (pourcentage > 100) {
	                    metaData.css = "sitools-userProfile-error-text";
	                }
					return pourcentage + "%";
				}
            }, {
                header : i18n.get('label.status'),
                dataIndex : 'status',
                sortable : true,
                width : 100,
                renderer : function (value, meta, record, index, colIndex, store) {
                    meta.tdCls += value.toUpperCase();
                    return value.toUpperCase();
                }
            } ]
        };

        this.bbar = {
            xtype : 'pagingtoolbar',
            store : this.store,
            displayInfo : true,
            displayMsg : i18n.get('paging.display'),
            emptyMsg : i18n.get('paging.empty')
        };

        this.tbar = {
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
                text : i18n.get('label.active'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_active.png',
                handler : this._onActive,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.disactive'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_disactive.png',
                handler : this._onDisactive,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.clean'),
                icon : 'res/images/icons/icons_perso/toolbar_clean.png',
                handler : this._onClean,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.refresh'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_refresh.png',
                handler : this._onRefresh,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.notify'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/notify.png',
                handler : this._onNotify,
                xtype : 's-menuButton'
            } ]
        };

        this.listeners = {
            scope : this, 
            itemdblclick : this.onModify
        };
        
        this.selModel = Ext.create('Ext.selection.RowModel',{
            mode : "SINGLE"
        });
        
        sitools.admin.userStorage.userStorageCrudPanel.superclass.initComponent.call(this);
    },

    /**
     * do a specific render to load informations from the store. 
     */
    afterRender : function () {
        sitools.admin.userStorage.userStorageCrudPanel.superclass.afterRender.apply(this, arguments);
        this.store.load({
            start : 0,
            limit : this.pageSize,
            action : 'read'
        });
    },

    /**
     * Create a {sitools.admin.userStorage.userStoragePropPanel} user Storage to add to the storage
     */
    onCreate : function () {
        var up = Ext.create("sitools.admin.userStorage.userStoragePropPanel", {
            url : this.url,
            action : 'create',
            store : this.getStore()
        });
        up.show(ID.BOX.USERSTORAGE);
    },

    /**
     * Modify the selected {sitools.admin.userStorage.userStoragePropPanel} user Storage
     */
    onModify : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        var up = Ext.create("sitools.admin.userStorage.userStoragePropPanel", {
            url : this.url + '/' + rec.get("id"),
            userStorageRec : rec,
            action : 'modify',
            store : this.getStore()
        });
        up.show(ID.BOX.USERSTORAGE);
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
            msg : Ext.String.format(i18n.get('userStorageCrud.delete'), rec.data.userId),
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
        Ext.Ajax.request({
            url : this.url + "/" + rec.get("id"),
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });

    },
    
    /**
     * Activate the selected {sitools.admin.userStorage.userStoragePropPanel} user Storage
     */
    _onActive : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.get("id") + '/start',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },

    /**
     * Deactivate the selected {sitools.admin.userStorage.userStoragePropPanel} user Storage
     */
    _onDisactive : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.get("id") + '/stop',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },
    
    /**
     * Delete all files from the selected {sitools.admin.userStorage.userStoragePropPanel} user Storage
     */
    _onClean : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : Ext.String.format(i18n.get('userStorageCrud.clean'), rec.data.userId),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doClean(rec);
                }
            }

        });
    },
    doClean : function (rec) {
        // var rec = this.getSelectionModel().getSelected();
        // if (!rec) return false;
        Ext.Ajax.request({
            url : this.url + '/' + rec.get("id") + '/clean',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },
    
    /**
     * Refresh the selected {sitools.admin.userStorage.userStoragePropPanel} user Storage
     */
    _onRefresh : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.data.id + '/refresh',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },
    
    /**
     * Notify the selected {sitools.admin.userStorage.userStoragePropPanel} user Storage by e-mail
     */
    _onNotify : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
        }
        Ext.Ajax.request({
            url : this.url + '/' + rec.data.id + '/notify',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                if (showResponse(ret)) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    }

});

