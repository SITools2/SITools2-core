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
            return [{
                jsObj: "Ext.form.field.Text",
                config: {
                    anchor: "100%",
                    fieldLabel: i18n.get("label.GeoJSONPostGisResourceModel"),
                    labelWidth : 150,
                    value: "",
                    name: "/geojson"
                }
            }];
        }
    },

    init: function (config) {

        this.i18nMizarMappingService = I18nRegistry.retrieve(this.pluginName);
        this.mizarView = Ext.ComponentQuery.query('mizarModuleView')[0];

        if (Ext.isEmpty(this.mizarView)) {
            return Ext.Msg.show({
                title: i18n.get('label.warning'),
                msg: this.i18nMizarMappingService.get('label.mizarViewNotOpened'),
                icon: Ext.Msg.WARNING,
                buttons: Ext.Msg.OK
            });
        }


        this.dataset = config.dataview.dataset;
        this.dataview = config.dataview;
        this.datasetCm = config.dataview.columns;
        this.mizarServiceButton = config.serviceView.down('button[idService=' + config.id +']');

        this.isMizarLinked = this.linkMizarWithGrid();

    },

    linkMizarWithGrid : function () {
        this.dataview.getSelectionModel().addListener('select', this.selectRecordOnMap, this);
        this.dataview.getSelectionModel().addListener('deselect', this.deselectRecordOnMap, this);
        mizarWidget.navigation.renderContext.canvas.addEventListener('mouseup', Ext.bind(this.selectRecordInGrid, this));
        popupMessage("", this.i18nMizarMappingService.get('label.mizarSuccessfullyMapped'), null, "x-info");
    },

    selectRecordInGrid: function (event) {
        var pickPoint = mizarWidget.navigation.globe.getLonLatFromPixel(event.layerX, event.layerY);

        var selections = PickingManager.computePickSelection(pickPoint);

        if (selections.length > 0) {
            this.dataview.getSelectionModel().deselectAll();
            Ext.each(selections, function (selection) {

                var primaryKey = this.dataview.getStore().primaryKey;
                var recordIndex = this.dataview.getStore().findBy(function (record) {
                    if (record.get(primaryKey) === selection.feature.id) {
                        return true;
                    }
                }, this);
                var record = this.dataview.getStore().getAt(recordIndex);
                this.dataview.getSelectionModel().select(record, true, true);
            }, this);
        }
    },

    selectRecordOnMap: function (checkModel, recordIndex) {
        var features = mizarWidget.getLayer("RESTO").features;
        var primaryKey = this.dataview.getStore().primaryKey;
        var record = this.dataview.getStore().getAt(recordIndex);

        Ext.each(features, function (feature, index) {
            if (feature.id == record.get(primaryKey)) {
                var coordBary = Utils.computeGeometryBarycenter(feature.geometry);
                var coord = mizarWidget.sky.coordinateSystem.fromGeoToEquatorial(coordBary, null, false);
                this.focusRecordOnMap(coord, feature);
            }
        }, this);
    },

    focusRecordOnMap: function (coordinates, featureToSelect) {
        var formatCoord = coordinates[0] + " " + coordinates[1];

        var highLightFonction = Ext.bind(function () {
            mizarWidget.highlightObservation({
                feature: featureToSelect,
                layer: this.mizarView.geoJsonLayer
            }, {
                color: "green"
            });
        }, this);

        mizarWidget.goTo(formatCoord, highLightFonction);
    },

    deselectRecordOnMap : function () {

    }
});