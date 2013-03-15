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
/*global Ext, sitools, ID, i18n, showResponse, alertFailure, loadUrl*/
/*
 * @include "roleWin.js"
 */
Ext.namespace('sitools.admin.applications');

/**
 * A window to display Roles and authorizations for each options. 
 * @cfg {string} urlAuthorizations  
 * @cfg {Ext.data.Record} applicationRecord
 * @class sitools.admin.applications.applicationsRolePanel
 * @extends Ext.Window
 * @requires sitools.component.applications.rolesPanel
 */
//sitools.component.applications.applicationsRolePanel = Ext.extend(Ext.Window, {
sitools.admin.applications.applicationsRolePanel = Ext.extend(Ext.Window, {
    width : 700,
    height : 480,
    modal : true,
    pageSize : 10,
    dataSets : "",

    initComponent : function () {
        this.title = i18n.get('label.authorizations') + " : " + this.applicationRecord.data.name;
        var storeAuthorizations = new Ext.data.JsonStore({
            id : 'storeAuthorizations',
            root : 'authorization.authorizations',
            url : this.urlAuthorizations,
            idProperty : 'role',
            fields : [ {
                name : 'role',
                type : 'string'
            }, {
                name : 'allMethod',
                type : 'boolean'
            }, {
                name : 'postMethod',
                type : 'boolean'
            }, {
                name : 'getMethod',
                type : 'boolean'
            }, {
                name : 'putMethod',
                type : 'boolean'
            }, {
                name : 'deleteMethod',
                type : 'boolean'
            }, {
                name : 'headMethod',
                type : 'boolean'
            }, {
                name : 'optionsMethod',
                type : 'boolean'
            } ],
            autoLoad : true, 
            listeners : {
				scope : this, 
				update : function (store, record) {
					var grid = this.gridAuthorizations;
					var index = store.indexOf(record);
				
					var value = record.get('allMethod');
				
					for (var i = 2; i < 8; i++) {
						var idPost = grid.getView().getCell(index, i).firstChild.firstChild.id;
						var cmpPost = Ext.getCmp(idPost);
						cmpPost.setEnabled(!value);
					}
				}, 
				load : function (store, records) {
					var grid = this.gridAuthorizations;
					var value, index, idPost, cmpPost;
					
					Ext.each(records, function (record) {
						store.fireEvent("update", store, record);
					}, this);
				}
            }
        });

        this.cbAll = new Ext.grid.CheckColumn({
            header : i18n.get('headers.all'),
            dataIndex : 'allMethod',
            width : 55
        });
        this.cbPost = new Ext.grid.CheckColumn({
            header : i18n.get('headers.post'),
            dataIndex : 'postMethod',
            width : 55
        });
        this.cbGet = new Ext.grid.CheckColumn({
            header : i18n.get('headers.get'),
            dataIndex : 'getMethod',
            width : 55
        });
        this.cbPut = new Ext.grid.CheckColumn({
            header : i18n.get('headers.put'),
            dataIndex : 'putMethod',
            width : 55
        });
        this.cbDelete = new Ext.grid.CheckColumn({
            header : i18n.get('headers.delete'),
            dataIndex : 'deleteMethod',
            width : 55
        });
        this.cbHead = new Ext.grid.CheckColumn({
            header : i18n.get('headers.head'),
            dataIndex : 'headMethod',
            width : 55
        });
        this.cbOptions = new Ext.grid.CheckColumn({
            header : i18n.get('headers.options'),
            dataIndex : 'optionsMethod',
            width : 55
        });

        var cmAuthorizations = new Ext.grid.ColumnModel({
            columns : [ {
                header : i18n.get('headers.role'),
                dataIndex : 'role',
                width : 100
            }, this.cbAll, this.cbGet, this.cbPost, this.cbPut, this.cbDelete, this.cbHead, this.cbOptions ],
            defaults : {
                sortable : true,
                width : 100
            }
        });

        var smAuthorizations = new Ext.grid.RowSelectionModel({
            singleSelect : true
        });

        this.gridAuthorizations = new Ext.grid.EditorGridPanel({
            id : 'gridAuthorizations',
            height : 450,
            store : storeAuthorizations,
            tbar : {
                xtype : 'toolbar',
                defaults : {
                    scope : this
                },
                items : [ {
                    text : i18n.get('label.create'),
                    hidden : this.mode == 'select',
                    icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                    handler : this._onCreateRole
                }, {
                    text : i18n.get('label.remove'),
                    hidden : this.mode == 'select',
                    icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                    handler : this._onDeleteRole
                } ]
            },
            cm : cmAuthorizations,
            sm : smAuthorizations,
            plugins : [ this.cbAll, this.cbGet, this.cbPost, this.cbPut, this.cbDelete, this.cbHead, this.cbOptions ],
            viewConfig : {
                forceFit : true
            }
            
        });
        this.items = [ this.gridAuthorizations ];
        this.buttons = [ {
            text : i18n.get('label.ok'),
            scope : this,
            handler : function () {
                this._onValidate();
            }
        }, {
            text : i18n.get('label.cancel'),
            scope : this,
            handler : function () {
                this.close();
            }
        } ];

        sitools.admin.applications.applicationsRolePanel.superclass.initComponent.call(this);

    },
    /**
     * Adds a record to the role store. 
     */
    _onCreateRole : function () {
        var winRole = new sitools.component.applications.rolesPanel({
            storeRolesApplication : this.gridAuthorizations.getStore()
        });
        winRole.show();
    },
    /**
     * Delete a record to the role store.
     * @return {}
     */
    _onDeleteRole : function () {
        var recs = this.gridAuthorizations.getSelectionModel().getSelections();
        if (recs.length === 0) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        this.gridAuthorizations.getStore().remove(recs);
    },
    /**
     * Send a PUT request to the urlAuthorizations with all roles as defined. 
     */
    _onValidate : function () {
        var putObject = {};
        putObject.id = this.applicationRecord.data.id;
        putObject.url = this.applicationRecord.data.url;
        putObject.name = this.applicationRecord.data.name;
        putObject.description = this.applicationRecord.data.description;

        var store = this.findById('gridAuthorizations').getStore();
        if (store.getCount() > 0) {
            putObject.authorizations = [];
            store.each(function (record) {
                var item = {
                    role : record.data.role,
                    allMethod : record.data.allMethod,
                    postMethod : record.data.postMethod,
                    getMethod : record.data.getMethod,
                    putMethod : record.data.putMethod,
                    deleteMethod : record.data.deleteMethod,
                    headMethod : record.data.headMethod,
                    optionsMethod : record.data.optionsMethod
                };
                putObject.authorizations.push(item);
            });
        }

        Ext.Ajax.request({
            url : this.urlAuthorizations,
            method : 'PUT',
            scope : this,
            jsonData : putObject,
            success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (!Json.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), Json.message);
                    return;
                }
                this.close();
                // this.store.reload();
            },
            failure : alertFailure
        });

    }
});
