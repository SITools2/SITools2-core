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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl, sql2ext, sitoolsUtils*/

Ext.namespace('sitools.user.component.datasets.dataviews');


/**
 * Datasets Module : Displays All Datasets depending on datasets attached to the
 * project.
 *
 * @class sitools.user.modules.datasetsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.datasets.mainContainer
 */
Ext.define('sitools.user.component.datasets.overview.Overview', {
    extend: 'sitools.user.core.Component',

    controllers: ['sitools.user.controller.component.datasets.overview.OverviewController'],

    requires: [],

    config: {
        autoShow: true
    },

    /**
     * @param {Object} componentConfig config object
     *
     * @param {DatasetModel} dataset.dataset the dataset definition
     * @optionnal {Array} dataset.formFilters formFilters
     */
    init: function (componentConfig, windowConfig) {

        var windowSettings = {}, componentSettings = {};

        var dataset = componentConfig.dataset;

        var formId;
        if (!Ext.isEmpty(componentConfig.userPreference) && !Ext.isEmpty(componentConfig.userPreference.formId)) {
            formId = componentConfig.userPreference.formId;
        }

        if(!Ext.isEmpty(componentConfig.form)) {
            formId = componentConfig.form.id;
        }


        Ext.apply(windowSettings, windowConfig, {
            datasetName: dataset.name,
            type: "data",
            title: i18n.get('label.datasets') + " : " + dataset.name,
            iconCls: "dataviews",
            typeWindow: 'data',
            saveToolbar: windowConfig.saveToolbar
        });

        windowSettings.id = "dataset_" + dataset.id + "_" + Ext.id();

        componentSettings = Ext.apply(componentConfig, {
            dataset: dataset,
            outsideConfig: componentConfig,
            //scope : this,
            component: this,
            formId : formId,
            //ranges : componentConfig.ranges,
            //nbRecordsSelection : componentConfig.nbRecordsSelection,
            //isModifySelection : componentConfig.isModifySelection,
            preferencesPath: componentConfig.preferencesPath,
            preferencesFileName: componentConfig.preferencesFileName,
            //origin : 'sitools.user.view.component.datasets.dataviews.LivegridView',
            //dataviewConfig : dataviewConfig
            // searchAction : this.searchAction,
        });

        var view = Ext.create('sitools.user.view.component.datasets.overview.OverviewView', componentSettings);

        this.setComponentView(view);
        if (this.getAutoShow()) {
            this.show(view, windowSettings);
        }
    },

    /**
     * method called when trying to save preference
     *
     * @returns
     */
    _getSettings: function () {
        var view = this.getComponentView();

        var globalSettings = {};
        var formsTabPanel = view.down("tabpanel");
        var datasetView = view.down("component [sitoolsType=datasetView]")

        if (formsTabPanel.items.length > 0) {
            globalSettings.formsActivePanel = formsTabPanel.getActiveTab().formId;
            globalSettings.formsPanelWidth = formsTabPanel.getWidth();

            globalSettings = Ext.apply(globalSettings, formsTabPanel.getActiveTab()._getSettings());
        } else if (datasetView) {
            globalSettings = Ext.apply(globalSettings, datasetView._getSettings());
        }


        return Ext.apply(globalSettings, {
            preferencesPath: view.preferencesPath,
            preferencesFileName: view.preferencesFileName,
            datasetName: view.dataset.name,
            datasetView: this.$className,
            dictionaryMappings: view.dataset.dictionaryMappings,
            componentClazz: view.componentClazz,
            datasetUrl: view.dataset.sitoolsAttachementForUsers,
            origin: view.$className
        });

    }
});