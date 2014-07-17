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
/*global Ext, sitools, ID, i18n, showResponse, alertFailure, loadUrl*/
/*
 * @include "roleWin.js"
 */
Ext.namespace('sitools.admin.applications');

/**
 * A specific checkColumn that is displayed only when allMethod is not checked
 */
Ext.define('sitools.admin.applications.CustomCheckColumn', {
    extend : 'Ext.grid.column.CheckColumn',
    alias : 'widget.appCheckColumn',
    renderer : function (value, meta, rec) {
        var toShow = !rec.get("allMethod");
        if (toShow) {
            return this.callParent(arguments);
        } else {
            return "";
        }
    }
});

/**
 * A window to display Roles and authorizations for each options. 
 * @cfg {string} urlAuthorizations  
 * @cfg {Ext.data.Record} applicationRecord
 * @class sitools.admin.applications.ApplicationsRole
 * @extends Ext.Window
 * @requires sitools.admin.applications.Roles
 */
Ext.define('sitools.admin.applications.ApplicationsRole', { 
    extend : 'Ext.Window', 
    width : 700,
    height : 480,
    modal : true,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    
    requires : ["sitools.admin.applications.Roles",
                'sitools.admin.authorizations.AuthorizationsModel'],
    
    initComponent : function () {
        this.title = i18n.get('label.authorizations') + " : " + this.applicationRecord.data.name;
        
        this.storeAuthorizations = Ext.create('Ext.data.JsonStore', {
            model : 'sitools.admin.authorizations.AuthorizationsModel',
            autoLoad : true,
            proxy : {
                type : 'ajax',
                url : this.urlAuthorizations,
                reader : {
                    type : 'json',
                    root : 'authorization.authorizations'
                }
            }
        });

        this.gridAuthorizations = Ext.create('Ext.grid.Panel', {
            height : 450,
            store : this.storeAuthorizations,
            forceFit : true,
            tbar : {
                xtype : 'toolbar',
                defaults : {
                    scope : this
                },
                items : [ {
                    text : i18n.get('label.create'),
                    hidden : this.mode === 'select',
                    icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create.png',
                    handler : this._onCreateRole
                }, {
                    text : i18n.get('label.remove'),
                    hidden : this.mode === 'select',
                    icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                    handler : this._onDeleteRole
                } ]
            },
            columns : [{
                header : i18n.get('headers.role'),
                dataIndex : 'role',
                width : 100
            }, {
                xtype: 'checkcolumn',
                header: 'allMethod',
                dataIndex: 'allMethod',
                width: 55
            }, {
                xtype: 'appCheckColumn',
                header : i18n.get('headers.post'),
                dataIndex: 'postMethod',
                width: 55
            }, {
                xtype: 'appCheckColumn',
                header : i18n.get('headers.get'),
                dataIndex: 'getMethod',
                width: 55
            }, {
                xtype: 'appCheckColumn',
                header : i18n.get('headers.put'),
                dataIndex: 'putMethod',
                width: 55
            }, {
                xtype: 'appCheckColumn',
                header : i18n.get('headers.delete'),
                dataIndex: 'deleteMethod',
                width: 55
            }, {
                xtype: 'appCheckColumn',
                header : i18n.get('headers.head'),
                dataIndex: 'headMethod',
                width: 55
            }, {
                xtype: 'appCheckColumn',
                header : i18n.get('headers.options'),
                dataIndex: 'optionsMethod',
                width: 55
            }]
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

        this.callParent(arguments);

    },
    /**
     * Adds a record to the role store. 
     */
    _onCreateRole : function () {
        var winRole = Ext.create("sitools.admin.applications.Roles", {
            storeRolesApplication : this.gridAuthorizations.getStore()
        });
        winRole.show();
    },
    /**
     * Delete a record to the role store.
     * @return {}
     */
    _onDeleteRole : function () {
        var recs = this.gridAuthorizations.getSelectionModel().getSelection();
        if (recs.length === 0) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
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

        var store = this.gridAuthorizations.getStore();
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
