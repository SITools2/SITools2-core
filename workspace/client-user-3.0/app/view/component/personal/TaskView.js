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
            pageSize: this.pageSize,
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
            store: this.store,
            displayInfo: true,
            displayMsg: i18n.get('paging.display'),
            emptyMsg: i18n.get('paging.empty')
        };

        var tbar = {
            xtype: 'toolbar',
            name: 'topToolbar',
            enableOverflow: true,
            items: [{
                text: i18n.get('label.clean'),
                icon: loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_clean.png',
                itemId: 'clean'
            }, {
                text: i18n.get('label.delete'),
                icon: loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                disabled: true,
                itemId: 'delete'
            }, {
                text: i18n.get('label.setFinish'),
                icon: loadUrl.get('APP_URL') + '/common/res/images/icons/set_finish.png',
                disabled: true,
                itemId: 'finish'
            } /*{
             text: i18n.get('label.viewResult'),
             icon: loadUrl.get('APP_URL') + '/common/res/images/icons/view_result.png',
             disabled: true,
             itemId : 'viewresult'
             },*/]
        };

        this.gridPanel = Ext.create('Ext.grid.Panel', {
            flex: 1,
            forceFit: true,
            tbar: tbar,
            bbar: bbar,
            store: this.store,
            border: false,
            selModel: selModel,
            columns: columns,
            itemId: 'taskList'
        });

        this.detailPanel = Ext.create('Ext.panel.Panel', {
            height: 200,
            hidden: true,
            border: false,
            autoScroll: true
        });

        var splitter = Ext.create("Ext.resizer.Splitter", {
            style: 'background-color:#EBEBEB;'
        });

        this.items = [this.gridPanel, splitter, this.detailPanel];

        this.callParent(arguments);
    }


});
