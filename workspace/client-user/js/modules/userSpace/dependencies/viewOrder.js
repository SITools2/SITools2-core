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

Ext.namespace('sitools.user.modules.userSpaceDependencies');

sitools.user.modules.userSpaceDependencies.viewOrderPanel = Ext.extend(Ext.grid.GridPanel, {
    border : false,
    sm : Ext.create('Ext.selection.RowModel',),
    layout : {
        type : 'vbox',
        // flex : 1,
        align : 'stretch'
    },
    height : 430,
    pageSize : 10,
    // loadMask: true,

    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_ORDERS_USER_URL');
        this.store = new Ext.data.JsonStore({
            root : 'data',
            restful : true,
            url : this.url,
            remoteSort : true,
            idProperty : 'id',
            fields : [ {
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
            } ]

        });

        this.cm = new Ext.grid.ColumnModel({
            // specify any defaults for each column
            defaults : {
                sortable : true
            // columns are not sortable by default
            },
            columns : [ {
                header : i18n.get('label.orderNumber'),
                dataIndex : 'id',
                width : 200
            }, {
                header : i18n.get('label.status'),
                dataIndex : 'status',
                width : 80
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
                text : i18n.get('label.details'),
                // icon: 'res/images/icons/toolbar_project_add.png',
                icon : loadUrl.get('APP_URL') + '/client-admin/res/images/icons/icons_perso/toolbar_details.png',
                handler : this._onDetail,
                xtype : 's-menuButton'
            }, {
                text : i18n.get('label.delete'),
                // icon: 'res/images/icons/toolbar_project_add.png',
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this._onDelete,
                xtype : 's-menuButton'
            } ]
        };

        sitools.user.modules.userSpaceDependencies.viewOrderPanel.superclass.initComponent.call(this);
    },

    onRender : function () {
        sitools.user.modules.userSpaceDependencies.viewOrderPanel.superclass.onRender.apply(this, arguments);
        this.store.load({
            params : {
                start : 0,
                limit : this.pageSize
            }
        });

    },

    _onDetail : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
        }
        var jsObj = sitools.user.modules.userSpaceDependencies.orderProp;
        var componentCfg = {
            // url:this.url,
            action : 'detail',
            // store: this.getStore(),
            orderRec : rec
        };
        var title = i18n.get('label.details') + " : ";
        title += rec.data.userId;
        title += " " + i18n.get('label.the');
        title += " " + rec.data.dateOrder;

        var windowConfig = {
            id : "showDataDetailId", 
            title : title,  
//            size : {
//                width : 700,
//                height : 480
//            },
            specificType : "dataDetail"
        };
        SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
    },

    _onDelete : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            return false;
        }
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : {
                yes : i18n.get('label.yes'),
                no : i18n.get('label.no')
            },
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
    }

});
