/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 *
 * This file is part of SITools2.
 *
 * SITools2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * SITools2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * SITools2. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl, extColModelToStorage, SitoolsDesk, sql2ext
 */

Ext.namespace('sitools.extension.component.datasets.services');

/**
 * Window that contains a tools to sort a store
 *
 * @cfg {} dataview The view the tool is open in
 * @cfg {} pos The position to apply to the window
 * @cfg {Ext.data.Store} store the store to sort
 * @cfg {} columnModel the dataset ColumnModel to know all columns on wich you
 *      can do a sort.
 * @class sitools.user.component.datasets.services.SorterService
 * @extends Ext.Window
 */
Ext.define('sitools.extension.component.datasets.services.MizarMappingService', {
    extend: 'sitools.user.core.PluginComponent',
    alias: 'widget.mizarMappingService',

    pluginName: 'mizarMappingService',

    i18nFolderPath: ['/sitools/client-extension/resources/i18n/mizarModule/'],

    statics: {
        getParameters: function () {
            return [
                {
                    jsObj: "Ext.form.field.Text",
                    config: {
                        anchor: "100%",
                        fieldLabel: i18n.get("label.GeoJSONPostGisResourceModel"),
                        labelWidth: 150,
                        value: "/geojson",
                        name: "geojsonResource"
                    }
                },
                {
                    jsObj: "Ext.form.field.Text",
                    config: {
                        anchor: "100%",
                        fieldLabel: i18n.get("label.mocResource"),
                        labelWidth: 150,
                        value: "/moc",
                        name: "mocResource"
                    }
                },
                {
                    jsObj: "Ext.form.field.Text",
                    config: {
                        anchor: "100%",
                        fieldLabel: i18n.get("label.opensearchResource"),
                        labelWidth: 150,
                        value: "/geojson",
                        name: "opensearchResource"
                    }
                }, {
                    jsObj: "Ext.form.field.Number",
                    config: {
                        fieldLabel: i18n.get('label.startingLong'),
                        allowBlank: true,
                        labelWidth: 150,
                        minValue: 0,
                        maxValue: 360,
                        decimalPrecision : 10,
                        listeners: {
                            render: function (c) {
                                Ext.QuickTips.register({
                                    target: c,
                                    text: "The longitutude of the starting point"
                                });
                            }
                        },
                        name: "startingLong",
                        value: undefined
                    }
                }, {
                    jsObj: "Ext.form.field.Number",
                    config: {
                        fieldLabel: i18n.get('label.startingLat'),
                        allowBlank: true,
                        labelWidth: 150,
                        minValue: 0,
                        maxValue: 180,
                        decimalPrecision : 10,
                        listeners: {
                            render: function (c) {
                                Ext.QuickTips.register({
                                    target: c,
                                    text: "The latitude of the starting point"
                                });
                            }
                        },
                        name: "startingLat",
                        value: undefined
                    }
                }, {
                    jsObj: "Ext.form.field.Number",
                    config: {
                        fieldLabel: i18n.get('label.startingZoom'),
                        allowBlank: true,
                        labelWidth: 150,
                        minValue: 0,
                        maxValue: 20,
                        decimalPrecision : 0,
                        listeners: {
                            render: function (c) {
                                Ext.QuickTips.register({
                                    target: c,
                                    text: "The starting zoom level"
                                });
                            }
                        },
                        name: "startingZoom",
                        value: undefined
                    }
                }
            ];
        }
    },

    init: function (config) {

        this.i18nMizarMappingService = I18nRegistry.retrieve(this.pluginName);
        this.mizarView = Ext.ComponentQuery.query('mizarModuleView')[0] || Ext.ComponentQuery.query('mizarViewAndDataModuleView')[0];

        if (Ext.isEmpty(this.mizarView)) {
            return Ext.Msg.show({
                title: i18n.get('label.warning'),
                msg: this.i18nMizarMappingService.get('label.mizarViewNotOpened'),
                icon: Ext.Msg.WARNING,
                buttons: Ext.Msg.OK
            });
        }

        if (config.fromProjectGraph == true) {
            this.dataset = config.dataset;
        } else {
            this.dataset = config.dataview.dataset;
            this.dataview = config.dataview;
            this.datasetStore = config.store;
            this.mizarServiceButton = config.serviceView.down('button[idService=' + config.id + ']');
        }

        var hasGeojson, hasMoc, hasOpensearch;

        //add layer to Mizar
        Ext.each(config.parameters, function (config) {
            switch (config.name) {
                case "geojsonResource" :
                    if(config.value) {
                        this.geojsonResource = config.value;
                        hasGeojson = true;
                    }
                    break;
                case "mocResource" :
                    if (config.value) {
                        this.mocResource = config.value;
                        hasMoc = true;
                    }
                    break;
                case "opensearchResource" :
                    if(config.value) {
                        this.opensearchResource = config.value;
                        hasOpensearch = true;
                    }
                    break;
                case "startingLong" :
                    if (config.value) {
                        this.startingLong = config.value;
                    }
                    break;
                case "startingLat" :
                    if (config.value) {
                        this.startingLat = config.value;
                    }
                    break;
                case "startingZoom" :
                    if (config.value) {
                        this.startingZoom = config.value;
                    }
                    break;
            }
        }, this);

        //service url
        var geojsonUrl = this.dataset.sitoolsAttachementForUsers + this.geojsonResource;
        var mocUrl = this.dataset.sitoolsAttachementForUsers + this.mocResource;
        var opensearchUrl = this.dataset.sitoolsAttachementForUsers + this.opensearchResource;

        if (hasOpensearch) {
            var layer = mizarWidget.getLayer(this.dataset.name);
            if (Ext.isEmpty(layer)) {
                //OPENSEARCH LAYER
                layer = mizarWidget.addLayer({
                    "category": "Image",
                    "type": "DynamicOpenSearch",
                    "name": this.dataset.name,
                    "serviceUrl": opensearchUrl,
                    "visible": true,
                    "pickable": true,
                    "minOrder": 5
                });

                if (!config.fromProjectGraph) {
                    layer.subscribe("tile:removeFeatures", Ext.bind(this.removeFeatureFromGrid, this));
                    layer.subscribe("features:added", Ext.bind(this.synchronizeFeaturesGridMap, this));
                }
            }
            else {
                // show layer
                layer.visible(true);
                //mizarUtils.zoomTo(layer);
            }
        }


        if (hasMoc) {
            layer = mizarWidget.getLayer(this.dataset.name + "_MOC");
            if (Ext.isEmpty(layer)) {

                //MOC LAYER
                mocDesc = {
                    "category": "Other",
                    "type": "Moc",
                    "name": this.dataset.name + "_MOC",
                    "serviceUrl": mocUrl,
                    "visible": "true"
                };
                mizarWidget.addLayer(mocDesc);
            }
            else {
                // show layer
                layer.visible(true);
                //mizarUtils.zoomTo(layer);
            }
        }

        if (hasGeojson) {
            layer = mizarWidget.getLayer(this.dataset.name + "_GEOJSON");
            if (Ext.isEmpty(layer)) {
                //GEOJSON LAYER
                var geoJsonLayer = mizarWidget.addLayer({
                    "category": "Other",
                    "type": "GeoJSON",
                    "name": this.dataset.name + "_GEOJSON",
                    "visible": true,
                    "pickable": true
                });

                Ext.Ajax.request({
                    url: geojsonUrl,
                    method: 'GET',
                    success: function (ret) {
                        var response = Ext.decode(ret.responseText);
                        MizarGlobal.jsonProcessor.handleFeatureCollection(geoJsonLayer, response);
                        geoJsonLayer.addFeatureCollection(response);
                        //mizarUtils.zoomTo(geoJsonLayer);
                    }
                });
            }
        }

        if (!Ext.isEmpty(this.startingLong) && !Ext.isEmpty(this.startingLat) && !Ext.isEmpty(this.startingZoom)) {
            var startingPoint = [this.startingLong, this.startingLat];
            mizarWidget.navigation.zoomTo(startingPoint, this.startingZoom);
        }

        if (config.fromProjectGraph == false) {
            this.isMizarLinked = this.linkMizarWithGrid();
        } else {
            popupMessage("", this.i18nMizarMappingService.get('label.mizarSuccessfullyMapped'), null, "x-info");
        }

    },

    synchronizeFeaturesGridMap: function (featureData) {
        var tab = [];
        this.dataview.suspendLayouts();
        Ext.each(featureData.features, function (feature) {
            tab.push(feature.properties);
        }, this);
        this.datasetStore.loadData(tab, true);
        this.dataview.resumeLayouts(true);

        if (!Ext.isEmpty(this.currentGridSelections)) {
            this._selectOnGrid(this.currentGridSelections);
            delete this.currentGridSelections;
        }
    },

    removeFeatureFromGrid : function (featuresId) {
        this.dataview.suspendLayouts();
        Ext.each(featuresId, function (featureId) {
            var feature = this.datasetStore.getById(featureId);
            if (!Ext.isEmpty(feature)) {
                this.datasetStore.remove(feature);
            }
        }, this);
        this.dataview.resumeLayouts(true);
    },

    linkMizarWithGrid: function () {
        this.dataview.addListener('selectionchange', this.selectRecordOnMap, this);
        mizarWidget.navigation.renderContext.canvas.addEventListener('mousedown', Ext.bind(this.mousedown, this));
        mizarWidget.navigation.renderContext.canvas.addEventListener('mouseup', Ext.bind(this.selectRecordInGrid, this));
        popupMessage("", this.i18nMizarMappingService.get('label.mizarSuccessfullyMapped'), null, "x-info");
    },

    mousedown: function (event) {
        this.timeStart = new Date();
        this.mouseXStart = event.layerX;
        this.mouseYStart = event.layerY;
    },

    selectRecordInGrid: function (event) {
        var epsilon = 5;
        var timeEnd = new Date();
        var diff = timeEnd - this.timeStart;

        if (diff > 500 || Math.abs(this.mouseXStart - event.layerX) > epsilon || Math.abs(this.mouseYStart - event.layerY) > epsilon) {
            //deselect everything to cope with Mizar Behavior
            this.dataview.getSelectionModel().deselectAll();
            return;
        }

        this.timeStart = undefined;
        this.mouseXStart = undefined;
        this.mouseYStart = undefined;

        var pickPoint = mizarWidget.navigation.globe.getLonLatFromPixel(event.layerX, event.layerY);

        var selections = MizarGlobal.pickingManager.computePickSelection(pickPoint);
        var recordsToSelectInGrid = [];

        if (selections.length > 0) {
            var primaryKey = this.dataview.getStore().primaryKey;
            Ext.each(selections, function (selection) {
                var record = this.dataview.getStore().getById(selection.feature.id);
                if (!Ext.isEmpty(record)) {
                    recordsToSelectInGrid.push(record);
                }
            }, this);
        }

        this.currentGridSelections = recordsToSelectInGrid;
        this._selectOnGrid(recordsToSelectInGrid);
    },

    selectRecordOnMap: function (selectionModel, recordsIndex) {
        MizarGlobal.pickingManager.doClearSelection();
        if (Ext.isEmpty(recordsIndex)) {
            return;
        }
        MizarGlobal.pickingManager.deactivate();
        var layer = mizarWidget.getLayer(this.dataset.name);
        if (Ext.isEmpty(layer)) {
            return;
        }

        var features = layer.features;
        var primaryKey = this.dataview.getStore().primaryKey;
        var isAllSelected = this.dataview.isAllSelected();


        var mizarFeatureMap = new Ext.util.MixedCollection({
            'getKey': function (obj) {
                return obj.id;
            }
        });
        mizarFeatureMap.addAll(features);

        var featuresSelected = [];
        if (isAllSelected) {
            mizarFeatureMap.each(function (feature) {
                this._selectOnMap(feature, layer);
                featuresSelected = mizarFeatureMap.items;
            }, this);
        }
        else {
            Ext.each(recordsIndex, function (record) {
                //var record = this.dataview.getStore().getAt(recordIndex);
                //if (Ext.isEmpty(record)) {
                //    return;
                //}
                var feature = mizarFeatureMap.get(record.get(primaryKey));
                if (!Ext.isEmpty(feature)) {
                    this._selectOnMap(feature, layer);
                    featuresSelected.push(feature);
                }
            }, this);
        }

        if (!Ext.isEmpty(featuresSelected)) {
            var barycenter = mizarUtils.computeGeometryBarycenter(featuresSelected);
            var coord = mizarWidget.sky.coordinateSystem.fromGeoToEquatorial(barycenter, null, false);

            var callback = function () {
                MizarGlobal.pickingManager.activate();
                MizarGlobal.featurePopup.hide();
            };

            //mizarWidget.navigation.zoomTo(coord, 2.0, 0.1, callback);
            mizarWidget.navigation.moveTo(coord, 0.1, callback);

        } else {
            popupMessage('', i18n.get('label.featureNotFound'));
        }
    },

    _selectOnMap: function (feature, layer) {
        mizarWidget.highlightObservation({
            feature: feature,
            layer: layer
        }, {
            isExclusive: false
        });
    },

    _selectOnGrid : function (recordsToSelectInGrid) {
        this.dataview.getSelectionModel().select(recordsToSelectInGrid, false, true);
        var rowIndex = this.dataview.getStore().indexOf(recordsToSelectInGrid[0]);
        this.dataview.getView().scrollRowIntoView(rowIndex);
    }
});