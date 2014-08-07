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
/*global Ext, sitools, i18n, alert, showResponse, alertFailure, SitoolsDesk, window, userLogin, loadUrl*/

Ext.namespace('sitools.user.view.header.userProfile');

Ext.define('sitools.user.view.header.userProfile.TaskView', {
	extend : 'Ext.grid.Panel',
    border : false,
    sm : Ext.create('Ext.selection.RowModel'),
//    layout : {
//        type : 'vbox',
//        align : 'stretch'
//    },
    layout : 'fit',
    forceFit : true,
    pageSize : 10,

    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_USERRESOURCE_ROOT_URL') + '/' + userLogin + '/tasks';
       
        this.store = Ext.create('Ext.data.JsonStore', {
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
            fields : [{
                name : 'id',
                type : 'string'
            }, {
                name : 'status',
                type : 'string'
            }, {
                name : 'modelId',
                type : 'string'
            }, {
                name : 'customStatus',
                type : 'string'
            }, {
                name : 'timestamp',
                type : 'string'
            }, {
                name : 'statusUrl',
                type : 'string'
            }, {
                name : 'urlResult',
                type : 'string'
            }, {
                name : 'userId',
                type : 'string'
            }, {
                name : 'startDate',
                type : 'string'
            }, {
                name : 'endDate',
                type : 'string'
            }, {
                name : 'runType',
                type : 'string'
            }, {
                name : 'modelName',
                type : 'string'
            }]
        });

        this.columns = {
            defaults : {
                sortable : true
            },
            items : [{
                header : i18n.get('label.taskId'),
                dataIndex : 'id',
                width : 220,
                hidden : true
            }, {
                header : i18n.get('label.startDate'),
                dataIndex : 'startDate',
                width : 170
            }, {
                header : i18n.get('label.endDate'),
                dataIndex : 'endDate',
                width : 170
            },  {
                header : i18n.get('label.status'),
                dataIndex : 'status',
                width : 180
            }, {
                header : i18n.get('label.customStatus'),
                dataIndex : 'customStatus',
                width : 100
            }]
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
            cls : 'services-toolbar',
            defaults : {
                scope : this,
                cls : 'services-toolbar-btn'
            },
            items : [{
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this._onDelete,
            }, {
                text : i18n.get('label.clean'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_clean.png',
                handler : this._onClean,
            }, {
                text : i18n.get('label.viewResult'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/view_result.png',
                handler : this._onViewResult,
            }, {
                text : i18n.get('label.viewStatusDetails'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_details.png',
                handler : this._onViewStatusDetails,
            }, {
                text : i18n.get('label.setFinish'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/set_finish.png',
                handler : this._onFinish,
            }]
        };

        this.callParent(arguments);
    },

    afterRender : function () {
        this.callParent(arguments);
        this.store.load({
            params : {
                start : 0,
                limit : this.pageSize
            }
        });
    },

    _onDelete : function () {
        var rec = this.getSelectionModel().getSelection()[0];
        if (!rec) {
            return false;
        }
        
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : {
                yes : i18n.get('label.yes'),
                no : i18n.get('label.no')
            },
            msg : i18n.get('tasks.delete'),
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
    },
    _onViewResult : function () {
        var rec = this.getSelectionModel().getSelection()[0];
        if (!rec) {
            return false;
        }
        if (!Ext.isEmpty(rec.data.urlResult)) {
            var orderUrl = loadUrl.get('APP_URL') + loadUrl.get('APP_ORDERS_USER_URL');
            if (rec.data.urlResult.indexOf(orderUrl) != -1) {
                this._showOrderDetails(rec.data.urlResult);
            } else {
                var pathUrl = window.location.protocol + "//" + window.location.host;
                if (rec.data.urlResult.indexOf("http://") != -1) {
					window.open(rec.data.urlResult);
				} else {
					window.open(pathUrl + rec.data.urlResult);
				}
                
            }
        }
    },
    _onFinish : function () {
        var rec = this.getSelectionModel().getSelection()[0];
        if (!rec) {
            return false;
        }
        Ext.Ajax.request({
            url : this.url + "/" + rec.data.id + "?action=finish",
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

    _onClean : function () {
        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : {
                yes : i18n.get('label.yes'),
                no : i18n.get('label.no')
            },
            msg : i18n.get('tasks.delete.all'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this._doClean();
                }
            }
        });
    },

    _doClean : function () {
        Ext.Ajax.request({
            url : this.url,
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
    
    _onViewStatusDetails : function () {
        
        var rec = this.getSelectionModel().getSelection()[0];
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');;
        }
        
        var jsObj = sitools.user.component.entete.userProfile.tasksDetails;
        var componentCfg = {
            sva : rec.data    
        };
        var windowConfig = {
            id : "taskStatusDetails", 
            title : i18n.get("label.taskDetails") + ":" + rec.data.id, 
            iconCls : "dataDetail"
        };
        SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);        
        
    },
    
    _showOrderDetails : function (url) {
        Ext.Ajax.request({
            url : url,
            method : 'GET',
            scope : this,
            success : function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), data.message);
                    return false;
                }
                var rec = new Ext.data.Record(data.order);
                var jsObj = sitools.user.component.entete.userProfile.orderProp;
                var componentCfg = {
                    action : 'detail',
                    orderRec : rec
                };
                var title = i18n.get('label.details') + " : ";
                title += rec.data.userId;
                title += " " + i18n.get('label.the');
                title += " " + rec.data.dateOrder;

                var windowConfig = {
                    id : "showDataDetailId", 
                    title : title,  
                    specificType : "dataDetail", 
                    iconCls : "dataDetail"
                };
                SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
            },
            failure : alertFailure
        });
    }

});