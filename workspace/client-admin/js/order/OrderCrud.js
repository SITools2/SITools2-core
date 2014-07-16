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
Ext.namespace('sitools.admin.order');

Ext.define('sitools.admin.order.orderCrud', { 
    extend : 'Ext.grid.Panel',
	alias : 'widget.s-order',
    border : false,
    height : ADMIN_PANEL_HEIGHT,
    id : ID.BOX.ORDER,
    mixins : {
        utils : 'sitools.admin.utils.utils'
    },
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    forceFit : true,
    
    requires : ['sitools.admin.order.orderProp',
                'sitools.admin.order.events'],
    
    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_ORDERS_ADMIN_URL');
        
        this.store = Ext.create('Ext.data.JsonStore', {
            pageSize : this.pageSize,
            remoteSort : true,
            proxy : {
                type : 'ajax',
                url : this.url,
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
                name : 'userId',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'resourceCollection'
            }, {
                name : 'resourceDescriptor',
                type : 'string'
            }, {
                name : 'status',
                type : 'string'
            }, {
                name : 'dateOrder',
                type : 'string'
            }, {
                name : 'events'
            }, {
                name : 'adminResourceCollection'
            }]
        });

        this.columns = {
            items : [{
                header : i18n.get('label.userLogin'),
                dataIndex : 'userId',
                width : 100
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 350
            }, {
                header : i18n.get('label.status'),
                dataIndex : 'status',
                width : 80
            }, {
                header : i18n.get('label.dateOrder'),
                dataIndex : 'dateOrder',
                width : 150
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
            items : [{
                text : i18n.get('label.details'),
                // icon: 'res/images/icons/toolbar_project_add.png',
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_details.png',
                handler : this._onDetail,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.active'),
                // icon: 'res/images/icons/toolbar_project_add.png',
                icon : 'res/images/icons/icons_perso/toolbar_beginning.png',
                handler : this._onActive,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.done'),
                // icon: 'res/images/icons/toolbar_project_edit.png',
                icon : 'res/images/icons/icons_perso/toolbar_end.png',
                handler : this._onDone,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                // icon: 'res/images/icons/toolbar_project_edit.png',
                icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                handler : this.onDelete,
                xtype : 's-menuButton'
            }, '->', {
                xtype : 's-filter',
                emptyText : i18n.get('label.search'),
                store : this.store,
                pageSize : this.pageSize
            }]
        };

        this.listeners = {
            scope : this, 
            itemdblclick : this._onDetail
        };
        sitools.admin.order.orderCrud.superclass.initComponent.call(this);
    },

    onRender : function () {
        sitools.admin.order.orderCrud.superclass.onRender.apply(this, arguments);
        this.store.load({
            params : {
                start : 0,
                limit : this.pageSize
            }
        });
    },

    _onDetail : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        var up = new sitools.admin.order.orderProp({
            url : this.url,
            action : 'detail',
            store : this.getStore(),
            orderRec : rec
        });
        up.show(ID.BOX.ORDER);
    },

    _onActive : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        var up = Ext.create("sitools.admin.order.events", {
            baseUrl : this.url + "/" + rec.data.id,
            action : 'active',
            store : this.getStore(),
            orderRec : rec
        });
        up.show(ID.BOX.ORDER);
    },

    _onDone : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
        }
        var up = Ext.create("sitools.admin.order.events", {
            baseUrl : this.url + "/" + rec.data.id,
            action : 'done',
            store : this.getStore(),
            orderRec : rec
        });
        up.show(ID.BOX.ORDER);
    },
    
    onDelete : function () {
        var rec = this.getLastSelectedRecord();
        if (!rec) {
            return false;
        }
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('orderCrud.delete'),
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
