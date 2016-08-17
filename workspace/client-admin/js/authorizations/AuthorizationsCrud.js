/***************************************
* Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.namespace('sitools.admin.authorizations');

/**
 * 
 * @class sitools.admin.authorizations.AuthorizationsCrud
 * @extends Ext.grid.GridPanel
 */
Ext.define('sitools.admin.authorizations.AuthorizationsCrud', {
    extend : 'Ext.grid.Panel', 
	alias : 'widget.s-authorizations',
    border : false,
    height : ADMIN_PANEL_HEIGHT,
    selModel : Ext.create('Ext.selection.RowModel'),
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    forceFit : true,
    id : ID.BOX.AUTHORIZATION,
    
    requires : ['sitools.admin.applications.ApplicationsRole'],

    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_AUTHORIZATIONS_URL');
        this.urlAuthorizations = loadUrl.get('APP_URL') + loadUrl.get('APP_AUTHORIZATIONS_URL');
        
        this.store = Ext.create('Ext.data.JsonStore', {
            remoteSort : true,
            pageSize : this.pageSize,
            proxy : {
                type : 'ajax',
                url : this.url,
                extraParams : {
                    type : "class"
                },
                reader : {
                    type : 'json',
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
                name : 'authorizations'
            }]
        });

        this.columns = {
            defaults : {
                sortable : true
            },
            items : [{
                header : i18n.get('label.id'),
                dataIndex : 'id',
                width : 100,
                hidden : true
            }, {
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 150,
                renderer : function (value, meta, record) {
                    meta.style = "font-weight: bold;";
                    return value;
                }
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 450,
                sortable : false
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
                text : i18n.get('label.authorizations'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_autorizations.png',
                handler : this.onDefineRole,
                xtype : 's-menuButton'
            }, '->', {
                xtype : 's-filter',
                emptyText : i18n.get('label.search'),
                store : this.store,
                pageSize : this.pageSize
            } ]
        };
        this.selModel = Ext.create('Ext.selection.RowModel');

        this.listeners = {
            scope : this, 
            itemdblclick : this.onDefineRole
        };

        this.callParent(arguments);
    },

    onRender : function () {
        this.callParent(arguments);
        this.store.load({
            params : {
                start : 0,
                limit : this.pageSize
            }
        });
    },

    onDefineRole : function () {
        var rec = this.getSelectionModel().getLastSelected();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        var up = Ext.create("sitools.admin.applications.ApplicationsRole", {
            urlAuthorizations : this.urlAuthorizations + "/" + rec.data.id,
            applicationRecord : rec
        });
        up.show(ID.BOX.AUTHORIZATION);
    },

    onDelete : function () {
        var rec = this.getSelectionModel().getLastSelected();
        if (!rec) {
            return false;
        }
        Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : {
                yes : i18n.get('label.yes'),
                no : i18n.get('label.no')
            },
            msg : i18n.get('applicationsCrud.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec);
                }
            }

        });

    },
    doDelete : function (rec) {
        Ext.Ajax.request({
            url : this.url + "/" + rec.data.id,
            method : 'DELETE',
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

