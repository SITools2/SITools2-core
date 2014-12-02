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

Ext.namespace('sitools.user.view.component.personal');

Ext.define('sitools.user.view.component.personal.TaskView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.taskView',

    requires: ['sitools.user.model.TaskModel'],

    border: false,
    forceFit: true,
    pageSize: 10,
    autoScroll: true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    padding: 10,

    initComponent: function () {

        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_USERRESOURCE_ROOT_URL') + '/' + userLogin + '/tasks';

        this.store = Ext.create('Ext.data.JsonStore', {
            model: 'sitools.user.model.TaskModel',
            proxy: {
                type: 'ajax',
                url: this.url,
                reader: {
                    type: 'json',
                    root: 'data',
                    idProperty: 'id'
                }
            },
            remoteSort: true
        });

        var selModel = Ext.create('Ext.selection.RowModel', {
            mode: 'SINGLE',
            listeners: {
                select: function () {
                    var buttons = Ext.ComponentQuery.query('taskView > grid > toolbar[name=topToolbar] > button');
                    Ext.each(buttons, function (button) {
                        button.setDisabled(false);
                    });
                }
            }
        });

        var columns = [{
            header: i18n.get('label.taskId'),
            dataIndex: 'id',
            width: 220,
            hidden: true
        }, {
            header: i18n.get('label.startDate'),
            dataIndex: 'startDate',
            width: 170
        }, {
            header: i18n.get('label.endDate'),
            dataIndex: 'endDate',
            width: 170
        }, {
            header: i18n.get('label.status'),
            dataIndex: 'status',
            width: 180
        }, {
            header: i18n.get('label.customStatus'),
            dataIndex: 'customStatus',
            width: 100
        }];

        var bbar = {
            xtype: 'pagingtoolbar',
            pageSize: this.pageSize,
            store: this.store,
            displayInfo: true,
            displayMsg: i18n.get('paging.display'),
            emptyMsg: i18n.get('paging.empty'),
            listeners : {
                scope : this,
                change : function (pagingToolbar, pageData) {
                    this.detailPanel.removeAll();
                    this.detailPanel.setVisible(false);
                }
            }
        };

        var tbar = {
            xtype: 'toolbar',
            name: 'topToolbar',
            enableOverflow: true,
            items: [{
                text: i18n.get('label.clean'),
                icon: loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_clean.png',
                scope: this,
                handler: this._onClean
            }, {
                text: i18n.get('label.delete'),
                icon: loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                scope: this,
                disabled: true,
                handler: this._onDelete
            }, {
                text: i18n.get('label.viewResult'),
                icon: loadUrl.get('APP_URL') + '/common/res/images/icons/view_result.png',
                scope: this,
                disabled: true,
                handler: this._onViewResult
            }, {
                text: i18n.get('label.setFinish'),
                icon: loadUrl.get('APP_URL') + '/common/res/images/icons/set_finish.png',
                scope: this,
                disabled: true,
                handler: this._onFinish
            }]
        };

        this.gridPanel = Ext.create('Ext.grid.Panel', {
            flex: 1,
            forceFit : true,
            tbar: tbar,
            bbar: bbar,
            store: this.store,
            border : false,
            selModel: selModel,
            columns: columns,
            listeners : {
                scope : this,
                itemclick : this._onViewStatusDetails
            }
        });

        this.detailPanel = Ext.create('Ext.panel.Panel', {
            height: 200,
            hidden: true,
            border : false,
            autoScroll : true
        });

        this.items = [this.gridPanel, this.detailPanel];

        this.callParent(arguments);
    },

    afterRender: function () {
        this.callParent(arguments);
        this.store.load({
            params: {
                start: 0,
                limit: 10
            }
        });
    },

    _onDelete: function () {
        var rec = this.gridPanel.getSelectionModel().getSelection()[0];
        if (!rec) {
            return false;
        }

        var tot = Ext.Msg.show({
            title: i18n.get('label.delete'),
            buttons: Ext.MessageBox.YESNO,
            msg: i18n.get('tasks.delete'),
            scope: this,
            fn: function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec);
                }
            }
        });
    },

    doDelete: function (rec) {
        Ext.Ajax.request({
            url: this.url + "/" + rec.data.id,
            method: 'DELETE',
            scope: this,
            success: function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (Json.success) {
                    this.store.reload();
                }
                popupMessage(i18n.get('label.information'), Json.message, loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');
            },
            failure: alertFailure
        });
    },

    _onViewResult: function () {
        var rec = this.gridPanel.getSelectionModel().getSelection()[0];
        if (!rec) {
            return false;
        }

        var urlResult = rec.get('urlResult');

        if (Ext.isEmpty(urlResult)) {
            return popupMessage(i18n.get('label.information'), i18n.get('label.urlResultEmpty'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
        }

        var orderUrl = loadUrl.get('APP_URL') + loadUrl.get('APP_ORDERS_USER_URL');

        if (urlResult.indexOf(orderUrl) != -1) {
            this._showOrderDetails(urlResult);
        } else {
            var pathUrl = window.location.protocol + "//" + window.location.host;
            if (urlResult.indexOf("http://") != -1) {
                window.open(urlResult);
            } else {
                window.open(pathUrl + urlResult);
            }
        }
    },

    _onFinish: function () {
        var rec = this.gridPanel.getSelectionModel().getSelection()[0];
        if (!rec) {
            return false;
        }

        Ext.Ajax.request({
            url: this.url + "/" + rec.get('id') + "?action=finish",
            method: 'PUT',
            scope: this,
            success: function (ret) {
                var data = Ext.decode(ret.responseText);
                if (ret.status == 200) {
                    this.store.reload();
                }
                var msg = Ext.String.format(i18n.get('label.onTaskFinished'), data.TaskModel.status);
                popupMessage(i18n.get('label.information'), msg, loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');
            },
            failure: alertFailure
        });
    },

    _onClean: function () {
        var tot = Ext.Msg.show({
            title: i18n.get('label.delete'),
            buttons: Ext.Msg.YESNO,
            icon: Ext.Msg.QUESTION,
            msg: i18n.get('tasks.delete.all'),
            scope: this,
            fn: function (btn, text) {
                if (btn == 'yes') {
                    this._doClean();
                }
            }
        });
    },

    _doClean: function () {
        Ext.Ajax.request({
            url: this.url,
            method: 'DELETE',
            scope: this,
            success: function (ret) {
                Ext.decode(ret.responseText);
                if (Json.success) {
                    this.store.reload();
                }
                popupMessage(i18n.get('label.information'), ret.responseText, loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');
            },
            failure: alertFailure
        });
    },

    _onViewStatusDetails: function () {

        var rec = this.gridPanel.getSelectionModel().getSelection()[0];
        if (!rec) {
            return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');
        }

        var taskDetailView = Ext.create('sitools.user.view.component.personal.TaskDetailView', {
            task: rec.data
        });

        this.detailPanel.removeAll();

        this.detailPanel.add(taskDetailView);
        this.detailPanel.setVisible(true);

    },

    _showOrderDetails: function (url) {
        Ext.Ajax.request({
            url: url,
            method: 'GET',
            scope: this,
            success: function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), data.message);
                    return false;
                }
                var rec = new Ext.data.Record(data.order);
                var jsObj = sitools.user.component.entete.userProfile.orderProp;
                var componentCfg = {
                    action: 'detail',
                    orderRec: rec
                };
                var title = i18n.get('label.details') + " : ";
                title += rec.data.userId;
                title += " " + i18n.get('label.the');
                title += " " + rec.data.dateOrder;

                var windowConfig = {
                    id: "showDataDetailId",
                    title: title,
                    specificType: "dataDetail",
                    iconCls: "dataDetail"
                };
                SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
            },
            failure: alertFailure
        });
    }

});
