/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

Ext.namespace('sitools.user.component.datasets.columnsDefinition');

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
Ext.define('sitools.user.component.datasets.plot.DataPlotter', {
    extend: 'sitools.user.core.PluginComponent',

    js: [
        "/sitools/client-public/js/plot/flotr2/lib/prototype.js",
        "/sitools/client-public/js/plot/flotr2/lib/base64.js",
        "/sitools/client-public/js/plot/flotr2/lib/canvas2image.js",
        "/sitools/client-public/js/plot/flotr2/lib/canvastext.js",
        "/sitools/client-public/js/plot/flotr2/flotr2.min.js"
    ],

    controllers: ['sitools.user.controller.component.datasets.plot.DataPlotterController'],

    /**
     *
     *  @param ranges
     *  @param selectionSize
     *  @param maxWarningRecords
     *  @param componentType
     *  @param datasetName
     *  @param datasetId
     *  @param dataUrl
     *  @param columnModel
     *  @param gridFilters
     *  @param gridFiltersCfg
     *  @param sortInfo
     *  @param formFilters
     *  @param formConceptFilters
     */
    init: function (componentConfig, windowConfig) {

        var windowSettings = {};

        Ext.apply(windowSettings, windowConfig, {
            id: "plot" + componentConfig.datasetId,
            title: "Data plot : " + componentConfig.datasetName,
            iconCls: "plot",
            datasetName: componentConfig.datasetName,
            type: "plot",
            saveToolbar: true,
            winHeight: 600
        });

        Ext.apply(componentConfig, {
            component: this
        });

        var columnDef = Ext.create("sitools.user.view.component.datasets.plot.DataPlotterView", componentConfig);
        this.setComponentView(columnDef);
        this.show(columnDef, windowSettings);
    },

    /**
     * method called when trying to save preference
     *
     * @returns
     */
    _getSettings: function () {
        var view = this.getComponentView();
        return {
            datasetName: view.datasetName,
            leftPanelValues: view.leftPanel.getForm().getValues(),
            preferencesPath: view.preferencesPath,
            preferencesFileName: view.preferencesFileName
        };
    }
});