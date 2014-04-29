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
Ext.namespace('sitools.component.formComponents');

Ext.define('sitools.component.formComponents.FormComponentsCrudPanel', { 
    extend : 'Ext.grid.Panel',
	alias : 'widget.s-formComponents',
    border : false,
    height : ADMIN_PANEL_HEIGHT,
    id : ID.BOX.FORMCOMPONENTS,
    selModel : Ext.create('Ext.selection.RowModel',{
        mode : 'SINGLE'
    }),
    pageSize : 10,
    forceFit : true,
    mixins : {
        utils : "js.utils.utils"
    },

    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_FORMCOMPONENTS_URL');
        
        this.store = Ext.create("Ext.data.JsonStore", {
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
            remoteSort : true,
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'type',
                type : 'string'
            }, {
                name : 'componentDefaultHeight',
                type : 'string'
            }, {
                name : 'componentDefaultWidth',
                type : 'string'
            }, {
                name : 'jsAdminObject',
                type : 'string'
            }, {
                name : 'jsUserObject',
                type : 'string'
            }, {
                name : 'imageUrl',
                type : 'string'
            }, {
				name : "priority", 
				type : "integer"
            }]
        });

        this.columns = {
            // specify any defaults for each column
            defaults : {
                sortable : true
            // columns are not sortable by default
            },
            items : [ {
                header : i18n.get('label.type'),
                dataIndex : 'type',
                width : 100,
                sortable : true
            }, {
                header : i18n.get('label.jsAdminObject'),
                dataIndex : 'jsAdminObject',
                width : 350,
                sortable : false
            }, {
                header : i18n.get('label.jsUserObject'),
                dataIndex : 'jsUserObject',
                width : 350,
                sortable : false
            }, {
                header : i18n.get('label.priority'),
                dataIndex : 'priority',
                width : 50,
                sortable : false
            } ]
        };

        this.bbar = {
            xtype : 'pagingtoolbar',
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
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create.png',
                handler : this._onCreate,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_edit.png',
                handler : this._onModify,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                handler : this._onDelete,
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
        sitools.component.formComponents.FormComponentsCrudPanel.superclass.initComponent.call(this);
    },

    onRender : function () {
        sitools.component.formComponents.FormComponentsCrudPanel.superclass.onRender.apply(this, arguments);
        this.store.load({
            start : 0,
            limit : this.pageSize
        });
    },

    _onCreate : function () {
        var dbp = new sitools.component.formComponents.FormComponentsPropPanel({
            url : this.url,
            action : 'create',
            store : this.store
        });
        dbp.show(ID.BOX.FORMCOMPONENTS);
    },

    _onModify : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }

        var dbp = new sitools.component.formComponents.FormComponentsPropPanel({
            url : this.url + '/' + rec.data.id,
            action : 'modify',
            store : this.store
        });
        dbp.show(ID.BOX.FORMCOMPONENTS);
    },

    _onDelete : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return false;
        }

        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('formComponentsCrud.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec);
                }
            }

        });

    },
    doDelete : function (rec) {
        // var rec = this.getLastSelectedRecord();
        // if (!rec) return false;
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
