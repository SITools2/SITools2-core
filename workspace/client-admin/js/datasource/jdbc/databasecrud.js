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
Ext.namespace('sitools.admin.datasource.jdbc');

/**
 * Displays all databases defined. 
 * @requires sitools.admin.datasource.jdbc.DataBasePropPanel
 * @requires sitools.admin.datasource.jdbc.DataBaseTest
 * @class sitools.admin.datasource.jdbc.DataBaseCrudPanel
 * @extends Ext.grid.GridPanel
 */
Ext.define('sitools.admin.datasource.jdbc.DataBaseCrudPanel', { 
    extend : 'Ext.grid.Panel',
	alias : 'widget.s-databaseJDBC',
    border : false,
    height : 300,
    id : ID.BOX.DATABASE,
    selModel : Ext.create('Ext.selection.RowModel',{
        mode : 'SINGLE'
    }),
    pageSize : 10,
    forceFit : true,

    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASOURCES_URL');

        // create the restful Store
        // Method url action
        // POST /datasources create
        // GET /datasources read
        // PUT /datasources/[id] update
        // DELETE /datasources/[id] delete
        this.store = Ext.create('Ext.data.JsonStore', {
            remoteSort : true,
            pageSize : this.pageSize,
            proxy : {
                type : 'ajax',
                url : this.url,
                reader : {
                    root : 'data',
                    idProperty : 'id'
                }
            },
            fields : [{
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'url',
                type : 'string'
            }, {
                name : 'status',
                type : 'string'
            }]
        });

        this.columns = {
            defaults : {
                sortable : true
            },
            items : [{
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 100,
                sortable : true,
                renderer : function (value, meta, record) {
                    meta.style = "font-weight: bold;";
                    return value;
                }
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 205,
                sortable : false
            }, {
                header : i18n.get('label.url'),
                dataIndex : 'url',
                width : 350,
                sortable : false
            }, {
                header : i18n.get('label.status'),
                dataIndex : 'status',
                width : 90,
                sortable : true,
                renderer : function (value, meta, record, index, colIndex, store) {
                    meta.tdCls += value;
                    return value;
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
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                handler : this._onCreate,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_edit.png',
                handler : this._onModify,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this._onDelete,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.active'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_active.png',
                handler : this._onActive,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.disactive'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_disactive.png',
                handler : this._onDisactive,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.testCnx'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_test_connection.png',
                handler : this._onTest,
                xtype : 's-menuButton'
            }, '->', {
                xtype : 's-filter',
                emptyText : i18n.get('label.search'),
                store : this.store,
                pageSize : this.pageSize
            } ]
        };

        this.listeners = {
            scope : this, 
            itemdblclick : this._onModify
        };
        sitools.admin.datasource.jdbc.DataBaseCrudPanel.superclass.initComponent.call(this);
    },

    onRender : function () {
        sitools.admin.datasource.jdbc.DataBaseCrudPanel.superclass.onRender.apply(this, arguments);
        this.store.load({
            params : {
                start : 0,
                limit : this.pageSize
            }
        });
    },

    _onCreate : function () {
        var dbp = new sitools.admin.datasource.jdbc.DataBasePropPanel({
            url : this.url,
            action : 'create',
            store : this.store
        });
        dbp.show(ID.BOX.DATABASE);
    },

    _onModify : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
        }

        if (rec.data.status == i18n.get('status.active')) {
            this._onView();
            return;
        }
        var dbp = new sitools.admin.datasource.jdbc.DataBasePropPanel({
            url : this.url + '/' + rec.data.id,
            action : 'modify',
            store : this.store
        });
        dbp.show(ID.BOX.DATABASE);
    },
    
    _onView : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
        }
        var up = new sitools.admin.datasource.jdbc.DataBasePropPanel({
            url : this.url + '/' + rec.data.id,
            action : 'view',
            store : this.store
        });
        up.show();
    },

    _onDelete : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return false;
        }

        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            icon : Ext.Msg.QUESTION,
            msg : String.format(i18n.get('databaseCrud.delete'), rec.data.name),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec);
                }
            }

        });

    },
    doDelete : function (rec) {
        // var rec = this.getSelectionModel().getSelected();
        // if (!rec) return false;
        Ext.Ajax.request({
            url : this.url + "/" + rec.data.id,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                var jsonResponse = Ext.decode(ret.responseText);
                
                popupMessage("",  
                        String.format(i18n.get(jsonResponse.message), rec.data.name),
                        loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png');
                
                if (jsonResponse.success) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },
    _onActive : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
        }

        Ext.Ajax.request({
            url : this.url + '/' + rec.data.id + '/start',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                var jsonResponse = Ext.decode(ret.responseText);
                
                popupMessage("",  
                        String.format(i18n.get(jsonResponse.message), rec.data.name),
                        loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_active.png');
                
                if (jsonResponse.success) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },

    _onDisactive : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
        }

        Ext.Ajax.request({
            url : this.url + '/' + rec.data.id + '/stop',
            method : 'PUT',
            scope : this,
            success : function (ret) {
                var jsonResponse = Ext.decode(ret.responseText);
                
                popupMessage("",  
                        String.format(i18n.get(jsonResponse.message), rec.data.name),
                        loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_disactive.png');
                
                if (jsonResponse.success) {
                    this.store.reload();
                }
            },
            failure : alertFailure
        });
    },

    _onTest : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
        }

        var dbt = new sitools.admin.datasource.DataBaseTest({
            url : this.url + '/' + rec.data.id + '/test'
        });
        dbt.show();
    }

});

