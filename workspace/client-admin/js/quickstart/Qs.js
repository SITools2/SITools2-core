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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure*/
Ext.namespace('sitools.admin.quickstart');

Ext.define('sitools.admin.quickstart.Qs', {
    extend: 'Ext.panel.Panel',
    widget: 'widget.qs',

    layout: 'border',
    bodyCls: 'quickStart',

    initComponent: function () {

        this.stepsBtn = {
            xtype: 'toolbar',
            layout: {
                type: 'hbox',
                pack: 'center',
                align: 'middle'
            },
            items: [{
                id: 'qsStart',
                value: 'QsStart',
                icon: '/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/logo_fav_icone.png',
                scope: this,
                enableToogle: true,
                toggleGroup: 'steps',
                toggleHandler: this.manageSteps,
                allowDepress: false,
                pressed: true,
                listeners: {
                    afterrender: function (b) {
                        new Ext.ToolTip({
                            target: 'qsStart',
                            anchor: 'top',
                            showDelay: 0,
                            hideDelay: 0,
                            html: "Start"
                        });
                    }
                }
            }, {
                id: 'qsProject',
                value: 'QsProject',
                icon: '/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/tree_projects.png',
                scope: this,
                enableToogle: true,
                toggleGroup: 'steps',
                toggleHandler: this.manageSteps,
                allowDepress: false,
                listeners: {
                    afterrender: function (b) {
                        new Ext.ToolTip({
                            target: 'qsProject',
                            anchor: 'top',
                            showDelay: 0,
                            hideDelay: 0,
                            html: "Project"
                        });
                    }
                }
            }, {
                id: 'qsDatasource',
                value: 'QsDatasource',
                icon: '/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/tree_databases.png',
                scope: this,
                enableToogle: true,
                toggleGroup: 'steps',
                toggleHandler: this.manageSteps,
                allowDepress: false,
                listeners: {
                    afterrender: function (b) {
                        new Ext.ToolTip({
                            target: 'qsDatasource',
                            anchor: 'top',
                            showDelay: 0,
                            hideDelay: 0,
                            html: "Datasource"
                        });
                    }
                }
            }, {
                id: 'qsDataset',
                value: 'QsDataset',
                icon: '/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/tree_datasets.png',
                scope: this,
                enableToogle: true,
                toggleGroup: 'steps',
                toggleHandler: this.manageSteps,
                allowDepress: false,
                listeners: {
                    afterrender: function (b) {
                        new Ext.ToolTip({
                            target: 'qsDataset',
                            anchor: 'top',
                            showDelay: 0,
                            hideDelay: 0,
                            html: "Dataset"
                        });
                    }
                }
            }, {
                id: 'qsForm',
                value: 'QsForm',
                icon: '/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/tree_forms.png',
                scope: this,
                enableToogle: true,
                toggleGroup: 'steps',
                toggleHandler: this.manageSteps,
                allowDepress: false,
                listeners: {
                    afterrender: function (b) {
                        new Ext.ToolTip({
                            target: 'qsForm',
                            anchor: 'top',
                            showDelay: 0,
                            hideDelay: 0,
                            html: "Form"
                        });
                    }
                }
            }, {
                id: 'qsSecurity',
                value: 'QsSecurity',
                icon: '/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/tree_userman.png',
                scope: this,
                enableToogle: true,
                toggleGroup: 'steps',
                toggleHandler: this.manageSteps,
                allowDepress: false,
                listeners: {
                    afterrender: function (b) {
                        new Ext.ToolTip({
                            target: 'qsSecurity',
                            anchor: 'top',
                            showDelay: 0,
                            hideDelay: 0,
                            html: "Security"
                        });
                    }
                }
            }, { xtype: 'tbspacer', width : 100 } , {xtype: 'label', html: '<i>Show QuickStart on startup<i>'},
            {
                xtype: 'checkbox',
                listeners: {
                    change: function (combo, newValue, oldValue) {
                        Ext.util.Cookies.set('showQuickStart', newValue);
                    },
                    afterrender: function (cb) {
                        cb.setValue(Ext.util.Cookies.get('showQuickStart'));
                    }
                }
            }]
        };

        this.currentPanel = Ext.create('sitools.admin.quickstart.QsStart', {
            qs: this
        });

        this.welcomePanel = Ext.create('Ext.panel.Panel', {
            border : false,
            region: 'center',
            id: "startPanel",
            layout: 'fit',
            bodyCls: 'quickStart',
            items: [this.currentPanel],
            bbar: this.stepsBtn
        });

        this.items = [this.welcomePanel];

        this.callParent(arguments);
    },

    manageSteps: function (btn, pressed) {
        if (pressed) {
            var xtype = "sitools.admin.quickstart." + btn.value;
            this.currentPanel = Ext.create(xtype, {
                qs: this
            });

            this.welcomePanel.removeAll();
            this.welcomePanel.add(this.currentPanel);
        }
    },

    openFeature: function (nodeId) {
        var containerP = mainPanel.down('[name=containerPanel]');
        var tree = treePanel.items.items[0];

        var node = tree.getStore().getNodeById(nodeId);
        tree.getSelectionModel().select(node);

        containerP.removeAll();

        containerP.add({
            width: "100%",
            bodyCls: 'admin-bg',
//                icon : node.raw.icon,
            items: [{
                xtype: 's-box',
                items: [{
                    xtype: 's-' + node.raw.nodeName,
                    sitoolsType: "mainAdminPanel"
                }]
            }],
            listeners: {
                resize: function (panel, width, height) {
                    var size = panel.items.items[0].body.getSize();
                    var sBoxTitle = panel.items.items[0].items.items[0].getEl();
                    size = {
                        height: size.height - (sBoxTitle.getHeight() + sBoxTitle.getMargin("t") + sBoxTitle.getMargin("b")),
                        width: size.width - 8
                    };
                    var mainAdminPanel = panel.down('[sitoolsType=mainAdminPanel]');
                    mainAdminPanel.setSize(size);
                }
            }
        });
    }
});

