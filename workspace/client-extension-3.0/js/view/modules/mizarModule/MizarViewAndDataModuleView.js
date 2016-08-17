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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl*/


Ext.namespace('sitools.extension.view.modules.mizarModule');


Ext.define('sitools.extension.view.modules.mizarModule.MizarViewAndDataModuleView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.mizarViewAndDataModuleView',
    layout: 'fit',
    requires: ['sitools.extension.model.MizarModuleModel'],

    border : false,

    initComponent: function () {

        this.i18nMizarModule = I18nRegistry.retrieve('mizarViewAndDataModule');

        Ext.each(this.moduleModel.listProjectModulesConfigStore.data.items, function (config) {
            switch (config.get('name')) {
                case "datasetName" :
                    this.datasetName = config.get('value');
                    break;
                case "datasetAttachment" :
                    this.datasetAttachment = config.get('value');
                    break;
                case "datasetWidth" :
                    this.datasetWidth = config.get('value');
                    break;
                case "startingLong" :
                    this.startingLong = config.get('value');
                    break;
                case "startingLat" :
                    this.startingLat = config.get('value');
                    break;
                case "startingZoom" :
                    this.startingZoom = config.get('value');
                    break;
                case "angleDistance" :
                    this.angleDistance = config.get('value');
                    break;
                case "samp" :
                    this.samp = config.get('value');
                    break;
                case "shortenerUrl" :
                    this.shortenerUrl = config.get('value');
                    break;
                case "2dMap" :
                    this.twoDMap = config.get('value');
                    break;
                case "reverseNameResolver" :
                    this.reverseNameResolver = config.get('value');
                    break;
                case "nameResolver" :
                    this.nameResolver = config.get('value');
                    break;
                case "category" :
                    this.category = config.get('value');
                    break;
                case "compass" :
                    this.compass = config.get('value');
                    break;
                case "showCredits" :
                    this.showCredits = config.get('value');
                    break;
                case "imageViewer" :
                    this.imageViewer = config.get('value');
                    break;
                case "configFile" :
                    this.configFile = config.get('value');
                    break;
            }
        }, this);

        this.layout = {
            type: 'hbox',
            align: 'stretch'
        };

        this.listeners = {
            scope: this,
            boxready : this.loadDataset
        };

        this.items = [];

        this.callParent(arguments);

    },

    loadDataset: function () {
        Ext.Ajax.request({
            method: 'GET',
            url: this.datasetAttachment,
            scope: this,
            success: function (response) {
                var dataset = Ext.decode(response.responseText).dataset;
                if (!Ext.isEmpty(dataset)) {
                    this.buildViews(dataset);
                }
            }
        });
    },

    buildViews: function (dataset) {

        var componentConfig = {
            dataset: dataset
        };
        var windowConfig = {};

        var simpleGrid = Ext.create('sitools.user.component.datasets.dataviews.SimpleGrid', {
            autoShow: false
        });
        simpleGrid.init(componentConfig, windowConfig);

        this.livegridView = simpleGrid.getComponentView();
        this.livegridView.title = Ext.String.format(this.i18nMizarModule.get('label.dataFrom'), this.datasetName);
        this.livegridView.width = this.datasetWidth + "%";
        this.livegridView.border = false;
        this.livegridView.collapsible = true;
        this.livegridView.collapseDirection = "left";

        var splitter = Ext.create("Ext.resizer.Splitter", {
            style : 'background-color:#EBEBEB;',
        });

        this.mizarView = Ext.create('sitools.extension.view.modules.mizarModule.MizarModuleView', {
            border : false,
            flex : 1,
            moduleModel: this.moduleModel
        });

        this.add(this.livegridView, splitter, this.mizarView);
    },

    /**
     * method called when trying to save preference
     *
     * @returns
     */
    _getSettings: function () {
        return {
            preferencesPath: "/modules",
            preferencesFileName: this.id,
            xtype: this.$className
        };
    }

});

