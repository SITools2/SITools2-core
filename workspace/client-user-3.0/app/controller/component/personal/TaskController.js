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

/**
 * Populate the div x-headers of the sitools Desktop. 
 * @cfg {String} htmlContent html content of the headers, 
 * @cfg {Array} modules the modules list
 * @class sitools.user.component.entete.Entete
 * @extends Ext.Panel
 */
Ext.define('sitools.user.controller.component.personal.TaskController', {
    extend : 'Ext.app.Controller',
    
    views : ['component.personal.TaskView'],
    
    init : function () {
        
        this.control({

            'taskView button#clean' : {
	            click : this._onClean
            },
            'taskView button#delete' : {
	            click : this._onDelete
            },
            'taskView button#viewresult' : {
	            click : this._onViewResult
            },
            'taskView button#finish' : {
	            click : this._onFinish
            },
            'taskView' : {
                afterrender : function (me) {
                    me.store.load({
                        start : 0,
                        limit : me.pageSize
                    });
                }
            },
            'taskView gridpanel#taskList' : {
                itemclick : this._onViewStatusDetails
            },
            'taskView gridpanel#taskList pagingtoolbar' : {
                change : function (pagingToolbar, pageData) {
                    var me = pagingToolbar.up("taskView");
                    me.detailPanel.removeAll();
                    me.detailPanel.setVisible(false);
                }
            }

        });
        this.callParent(arguments);
    },

    _onClean: function (cleanBtn) {
        Ext.Msg.show({
            title: i18n.get('label.delete'),
            buttons: Ext.Msg.YESNO,
            icon: Ext.Msg.QUESTION,
            msg: i18n.get('tasks.delete.all'),
            scope: this,
            fn: function (btn, text) {
                if (btn == 'yes') {
                    this._doClean(cleanBtn);
                }
            }
        });
    },

    _doClean: function (btn) {
        var me = btn.up("taskView");
        Ext.Ajax.request({
            url: me.url,
            method: 'DELETE',
            success: function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (Json.success) {
                    me.store.reload();
                }
                popupMessage(i18n.get('label.information'), Json.message, loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');
            },
            failure: alertFailure
        });
    },

    _onDelete: function (deleteBtn) {
        var me = deleteBtn.up("taskView");
        var rec = me.down("gridpanel#taskList").getSelectionModel().getSelection()[0];
        if (!rec) {
            return false;
        }

        Ext.Msg.show({
            title: i18n.get('label.delete'),
            buttons: Ext.MessageBox.YESNO,
            msg: i18n.get('tasks.delete'),
            scope: this,
            fn: function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete(rec, me);
                }
            }
        });
    },

    doDelete: function (rec, view) {
        Ext.Ajax.request({
            url: view.url + "/" + rec.data.id,
            method: 'DELETE',
            success: function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (Json.success) {
                    view.store.reload();
                }
                popupMessage(i18n.get('label.information'), Json.message, loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');
            },
            failure: alertFailure
        });
    },

    _onViewResult: function (btn) {
        var me = btn.up("taskView");
        var rec = me.down("gridpanel#taskList").getSelectionModel().getSelection()[0];
        if (!rec) {
            return false;
        }

        var urlResult = rec.get('urlResult');

        if (Ext.isEmpty(urlResult)) {
            return popupMessage(i18n.get('label.information'), i18n.get('label.urlResultEmpty'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
        }

        var orderUrl = loadUrl.get('APP_URL') + loadUrl.get('APP_ORDERS_USER_URL');

        if (urlResult.indexOf(orderUrl) != -1) {
            this._showOrderDetails(urlResult, me);
        } else {
            var pathUrl = window.location.protocol + "//" + window.location.host;
            if (urlResult.indexOf("http://") != -1) {
                window.open(urlResult);
            } else {
                window.open(pathUrl + urlResult);
            }
        }
    },

    _showOrderDetails: function (url, view) {
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

                var order = Ext.create("sitools.user.model.OrderModel", data.order);

                var orderDetailView = Ext.create('sitools.user.view.component.personal.OrderDetailView', {
                    action : 'detail',
                    orderRec : order
                });

                view.detailPanel.removeAll();
                view.detailPanel.add(orderDetailView);
                view.detailPanel.setVisible(true);
            },
            failure: alertFailure
        });
    },

    _onFinish: function (btn) {
        var me = btn.up("taskView");
        var rec = me.down("gridpanel#taskList").getSelectionModel().getSelection()[0];
        if (!rec) {
            return false;
        }

        Ext.Ajax.request({
            url: me.url + "/" + rec.get('id') + "?action=finish",
            method: 'PUT',
            scope: this,
            success: function (ret) {
                var data = Ext.decode(ret.responseText);
                if (ret.status == 200) {
                    me.store.reload();
                }
                var msg = Ext.String.format(i18n.get('label.onTaskFinished'), data.TaskModel.status);
                popupMessage(i18n.get('label.information'), msg, loadUrl.get('APP_URL') + '/common/res/images/msgBox/16/icon-info.png');
            },
            failure: alertFailure
        });
    },

    _onViewStatusDetails: function (grid, rec) {
        var me = grid.up("taskView");
        var taskDetailView = Ext.create('sitools.user.view.component.personal.TaskDetailView', {
            task: rec.data
        });

        me.detailPanel.removeAll();

        me.detailPanel.add(taskDetailView);
        me.detailPanel.setVisible(true);
    }



});