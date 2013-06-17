/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.namespace('sitools.admin.datasource.mongoDb');


/**
 * Displays all databases defined. 
 * @requires sitools.admin.datasource.mongoDb.DataBasePropPanel
 * @requires sitools.admin.datasource.mongoDb.DataBaseTest
 * @class sitools.admin.datasource.mongoDb.DataBaseCrudPanel
 * @extends Ext.grid.GridPanel
 */
sitools.admin.datasource.mongoDb.DataBaseCrudPanel = Ext.extend(Ext.grid.GridPanel, {
//sitools.admin.datasource.DataBaseCrudPanel = Ext.extend(Ext.grid.GridPanel, {

    border : false,
    height : 300,
    id : ID.BOX.DATABASE,
    sm : new Ext.grid.RowSelectionModel({
        singleSelect : true
    }),
    pageSize : 10,
    // loadMask: true,

    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASOURCES_MONGODB_URL');

        // create the restful Store
        // Method url action
        // POST /datasources create
        // GET /datasources read
        // PUT /datasources/[id] update
        // DELETE /datasources/[id] delete
        this.store = new Ext.data.JsonStore({
            root : 'data',
            restful : true,
            remoteSort : true,
            autoSave : false,
            url : this.url,
            idProperty : 'id',
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
                name : 'url',
                type : 'string'
            }, {
                name : 'sitoolsAttachementForUsers',
                type : 'numeric'
            }, {
                name : 'portNumber',
                type : 'numeric'
            }, {
                name : 'databaseName',
                type : 'string'
            }, {
                name : 'status',
                type : 'string'
            }, {
                name : 'authentication',
                type : 'boolean'
            }, {
                name : 'userLogin',
                type : 'string'
            }, {
                name : 'userPassword',
                type : 'string'
            } ]
        });

        this.cm = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : true
            // columns are not sortable by default
            },
            columns : [ {
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 100,
                sortable : true
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
                sortable : true
            }, {
                xtype: 'actioncolumn',
                width: 30,
                items: [{
                    getClass: function (v, meta, rec) {  // Or return a class from a function
                        if (rec.get("status") === "ACTIVE") {
	                        this.items[0].tooltip = i18n.get('label.databaseExplorer');
	                        return 'sitools-explore-datasource';
                        } else {
                            return '';
                        }
					},
					handler: function (grid, rowIndex, colIndex) {
						var rec = grid.getStore().getAt(rowIndex);
						var win = new sitools.admin.datasource.mongoDb.DataBaseExplorer({
							database : rec.data
						});
						win.show();
					}
                }]
            } ]
        });

        this.bbar = {
            xtype : 'paging',
            pageSize : this.pageSize,
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
        this.view = new Ext.grid.GridView({
            forceFit : true
        });

        this.listeners = {
            scope : this, 
            rowDblClick : this._onModify
        };
        sitools.admin.datasource.mongoDb.DataBaseCrudPanel.superclass.initComponent.call(this);
    },

    onRender : function () {
        sitools.admin.datasource.mongoDb.DataBaseCrudPanel.superclass.onRender.apply(this, arguments);
        this.store.load({
            params : {
                start : 0,
                limit : this.pageSize
            }
        });
    },

    _onCreate : function () {
        var dbp = new sitools.admin.datasource.mongoDb.DataBasePropPanel({
            url : this.url,
            action : 'create',
            store : this.store
        });
        dbp.show(ID.BOX.DATABASE);
    },

    _onModify : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }

        if (rec.data.status === i18n.get('status.active')) {
            this._onView();
            return;
        }
        var dbp = new sitools.admin.datasource.mongoDb.DataBasePropPanel({
            url : this.url + '/' + rec.id,
            action : 'modify',
            store : this.store
        });
        dbp.show(ID.BOX.DATABASE);
    },
    
    _onView : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var up = new sitools.admin.datasource.mongoDb.DataBasePropPanel({
            url : this.url + '/' + rec.id,
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
            msg : i18n.get('databaseCrud.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn === 'yes') {
                    this.doDelete(rec);
                }
            }

        });

    },
    doDelete : function (rec) {
        // var rec = this.getSelectionModel().getSelected();
        // if (!rec) return false;
        Ext.Ajax.request({
            url : this.url + "/" + rec.id,
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
    _onActive : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }

        Ext.Ajax.request({
            url : this.url + '/' + rec.id + '/start',
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

    _onDisactive : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }

        Ext.Ajax.request({
            url : this.url + '/' + rec.id + '/stop',
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

    _onTest : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }

        var dbt = new sitools.admin.datasource.DataBaseTest({
            url : this.url + '/' + rec.id + '/test'
        });
        dbt.show();
    }

});

Ext.reg('s-databaseMongoDb', sitools.admin.datasource.mongoDb.DataBaseCrudPanel);
