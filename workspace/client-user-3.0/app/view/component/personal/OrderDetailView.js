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
/*global Ext, sitools, i18n, userLogin, DEFAULT_ORDER_FOLDER, SitoolsDesk, projectGlobal, DEFAULT_PREFERENCES_FOLDER, loadUrl, viewFileContent*/

Ext.namespace('sitools.user.view.component.personal');

Ext.define('sitools.user.view.component.personal.OrderDetailView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.orderDetailView',

    pageSize: 10,
    dataSets: "",
    border: false,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    padding: 10,

    initComponent: function () {

        this.title = i18n.get('label.details');
        this.icon = '/sitools/common/res/images/icons/toolbar_details.png';

        var eventsStore = Ext.create('Ext.data.JsonStore', {
            proxy: {
                type: 'memory'
            },
            fields: [{
                name: 'eventDate',
                type: 'string'
            }, {
                name: 'description',
                type: 'string'
            }, {
                name: 'message',
                type: 'string'
            }],
            data: this.orderRec.data.events
        });

        var eventsCm = {
            items: [{
                header: i18n.get("headers.date"),
                dataIndex: 'eventDate'
            },
                {
                    header: i18n.get("headers.description"),
                    dataIndex: 'description'
                }, {
                    header: i18n.get("headers.message"),
                    dataIndex: 'message'
                }]
        };

        this.eventsGrid = Ext.create('Ext.grid.Panel', {
            flex: 1,
            padding: 15,
            title: i18n.get('label.events'),
            store: eventsStore,
            columns: eventsCm,
            forceFit: true

        });

        var resourceCollectionStore = Ext.create('Ext.data.JsonStore', {
            fields: [{
                name: 'resourceCollection',
                type: 'string'
            }]
        });

        if (!Ext.isEmpty(this.orderRec.data.resourceCollection)) {
            Ext.each(this.orderRec.data.resourceCollection, function (resource) {
                resourceCollectionStore.add({
                    resourceCollection: resource
                });
            });
        }

        var resourceCollectionCm = {
            items: [{
                header: i18n.get("headers.resource"),
                dataIndex: 'resourceCollection'
            }]
        };

        this.resourceCollectionGrid = Ext.create('Ext.grid.Panel', {
            flex: 1,
            padding: 15,
            title: i18n.get('label.resourceCollection'),
            store: resourceCollectionStore,
            hidden: (resourceCollectionStore.getCount() == 0) ? true : false,
            columns: resourceCollectionCm,
            forceFit: true,
            hideHeaders: true,
            listeners: {
                scope: this,
                itemclick: this.openResource
            }
        });

        this.formPanel = Ext.create('Ext.form.Panel', {
            border: false,
            padding: 10,
            fieldDefaults: {
                labelWidth: 140
            },
            flex: 1,
            items: [{
                xtype: 'hidden',
                name: 'orderId',
                id: 'userValueFieldId'
            }, {
                xtype: 'textfield',
                name: 'userId',
                fieldLabel: i18n.get('label.userLogin'),
                anchor: '100%',
                readOnly: true
            }, {
                xtype: 'textfield',
                name: 'description',
                fieldLabel: i18n.get('label.description'),
                anchor: '100%',
                readOnly: true
            }, {
                xtype: 'textfield',
                name: 'resourceDescriptor',
                fieldLabel: i18n.get('label.url'),
                anchor: '100%',
                readOnly: true
            }, {
                xtype: 'textfield',
                name: 'dateOrder',
                fieldLabel: i18n.get('label.dateOrder'),
                anchor: '100%',
                readOnly: true
            }]
        });

        this.items = [this.formPanel, this.resourceCollectionGrid, this.eventsGrid];

        this.callParent(arguments);
    },

    afterRender: function () {
        this.callParent(arguments);

        var f = this.down('form').getForm();
        f.loadRecord(this.orderRec);
    },

    orderRequest: function (orderRequest, nomFichier) {
        Ext.Ajax.request({
            scope: this,
            method: 'GET',
            url: orderRequest.datasetUrl,
            success: function (response, opts) {
                try {
                    var json = Ext.decode(response.responseText);
                    if (!json.success) {
                        throw (json.message);
                    }

                    var componentCfg = {
                        dataUrl: json.dataset.sitoolsAttachementForUsers,
                        datasetId: orderRequest.datasetId,
                        datasetCm: Ext.decode(orderRequest.colModel),
                        filters: orderRequest.filters,
                        dictionaryMappings: json.dataset.dictionaryMappings,
                        datasetViewConfig: json.dataset.datasetViewConfig,
                        preferencesPath: "/" + json.dataset.name,
                        preferencesFileName: "datasetView"
                        // ,
                        // sorters : orderRequest.sort.sorters
                    };
                    if (!Ext.isEmpty(orderRequest.formParams)) {
                        componentCfg.formParams = orderRequest.formParams;
                    }

                    var jsObj = eval(json.dataset.datasetView.jsObject);

                    var windowConfig = {
                        id: "winOrderId" + json.dataset.id,
                        title: nomFichier,
                        datasetName: json.dataset.name,
                        saveToolbar: true,
                        type: "data"
                    };
                    SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);

                } catch (err) {
                    Ext.Msg.alert(i18n.get('label.error'), err);
                }
            },
            failure: function (response, opts) {
                Ext.Msg.alert(response);
            }
        });
    },

    orderRecord: function (orderRecord, nomFichier) {
        var winOrderDetail = new sitools.user.component.entete.userProfile.viewRecordSelectionDetail({
            orderRecord: orderRecord,
            nomFichier: nomFichier
        });
        winOrderDetail.show();
    },

    openResource: function (grid, record, item, rowIndex) {

        var tabTmp = record.data.resourceCollection.split('/');
        var nomFichier = tabTmp[tabTmp.length - 1];
        tabTmp = nomFichier.split('.');
        var extension = tabTmp[tabTmp.length - 1];

        if (extension != "json") {
            var url = record.data.resourceCollection;
            viewFileContent(url, nomFichier);
        } else {
            Ext.Ajax.request({
                url: record.data.resourceCollection,
                method: 'GET',
                scope: this,
                success: function (ret) {
                    var Json;
                    if (extension == "json") {
                        //If it is a Json file, try to decode it and use it into a specific grid
                        try {
                            Json = Ext.decode(ret.responseText);
                            if (!Ext.isEmpty(Json.orderRequest)) {
                                this.orderRequest(Json.orderRequest, nomFichier);
                            }
                            else if (!Ext.isEmpty(Json.orderRecord)) {
                                this.orderRecord(Json.orderRecord, nomFichier);
                            }
                            else {
                                var windowConfig = {
                                    id: "winPreferenceDetailId",
                                    title: nomFichier
                                };
                                var jsObj = Ext.Panel;
                                var componentCfg = {
                                    layout: 'fit',
                                    autoScroll: true,
                                    html: ret.responseText
                                };
                                SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
                            }
                        } catch (err) {
                            Ext.Msg.alert(err);
                        }
                    }
                },
                failure: function (ret) {
                    Ext.Msg.alert(i18n.get('label.error'), ret.responseText);
                }
            });
        }
    }
});