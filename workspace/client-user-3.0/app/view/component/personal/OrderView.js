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
/*global Ext, sitools, i18n, alert, showResponse, alertFailure, SitoolsDesk, loadUrl*/

Ext.namespace('sitools.user.view.component.personal');

Ext.define('sitools.user.view.component.personal.OrderView', {
	extend : 'Ext.panel.Panel',
	
	requires : ['sitools.user.model.OrderModel'],
	
    border : false,
    selModel : Ext.create('Ext.selection.RowModel'),

    pageSize : 10,
    layout : {
        type : 'vbox',
        align : 'stretch'
    },
    padding : 10,

    initComponent : function () {
    	
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_ORDERS_USER_URL');
        
        this.store = Ext.create('Ext.data.JsonStore', {
        	model : 'sitools.user.model.OrderModel',
        	proxy : {
        		type : 'ajax',
        		url : this.url,
        		reader : {
        			type : 'json',
        			root : 'data',
        			idProperty : 'id'
        		}
        	},
            remoteSort : true
        });

        var columns = {
            defaults : {
                sortable : true
            },
            items : [ {
                header : i18n.get('label.orderNumber'),
                dataIndex : 'id',
                width : 200
            }, {
                header : i18n.get('label.orderDate'),
                dataIndex : 'dateOrder',
                width : 200
            }, {
                header : i18n.get('label.status'),
                dataIndex : 'status',
                width : 80
            } ]
        };

        var bbar = {
            xtype : 'pagingtoolbar',
            pageSize : this.pageSize,
            store : this.store,
            displayInfo : true,
            displayMsg : i18n.get('paging.display'),
            emptyMsg : i18n.get('paging.empty'),
            listeners : {
                scope : this,
                change : function (pagingToolbar, pageData) {
                    this.detailPanel.removeAll();
                    this.detailPanel.setVisible(false);
                }
            }
        };

        var tbar = {
            xtype : 'toolbar',
            cls : 'services-toolbar',
            defaults : {
                scope : this,
                cls : 'services-toolbar-btn'
            },
            items : [{
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this._onDelete
            }]
        };

        this.gridPanel = Ext.create('Ext.grid.Panel', {
            flex : 1,
            tbar : tbar,
            bbar : bbar,
            columns : columns,
            store : this.store,
            border : false,
            forceFit : true,
            autoScroll : true,
            listeners : {
                scope : this,
                itemclick : this._onDetail
            },
            viewConfig : {
                listeners : {
                    scope : this,
                    refresh : function (view) {
                        this.getEl().unmask();
                    }
                }
            }
        });

        this.detailPanel = Ext.create('Ext.panel.Panel', {
            height : 300,
            hidden : true,
            split : true,
            autoScroll : true,
            border : false
        });

        this.items = [this.gridPanel, this.detailPanel];

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

    _onDetail : function () {
        var rec = this.gridPanel.getSelectionModel().getSelection()[0];
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');
        }

        var orderDetailView = Ext.create('sitools.user.view.component.personal.OrderDetailView', {
            action : 'detail',
            orderRec : rec
        });

        this.detailPanel.removeAll();
        this.detailPanel.add(orderDetailView);
        this.detailPanel.setVisible(true);
    },

    _onDelete : function () {
        var rec = this.gridPanel.getSelectionModel().getSelection()[0];
        if (!rec) {
            return false;
        }
        
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            icon : Ext.Msg.QUESTION,
            msg : Ext.String.format(i18n.get('orderCrud.delete'), rec.get('id')),
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
    }
});
