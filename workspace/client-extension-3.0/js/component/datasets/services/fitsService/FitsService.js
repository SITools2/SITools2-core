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

Ext.namespace('sitools.extension.component.datasets.services.fitsService');

/**
 *
 * @class sitools.extension.component.datasets.services.FitsService
 * @extends sitools.user.core.PluginComponent
 */
Ext.define('sitools.extension.component.datasets.services.fitsService.FitsService', {
    extend: 'sitools.user.core.PluginComponent',
    alias: 'widget.fitsService',

    pluginName : 'fitsService',

    js : [
        '/sitools/client-public/js/utils/prototyp.js',
        '/sitools/client-extension/resources/libs/fitsViewer/fits.js',
        '/sitools/client-extension/resources/libs/fitsViewer/binaryajax.js',
        '/sitools/client-extension/resources/libs/fitsViewer/excanvasCompiled.js',
        '/sitools/client-extension/resources/libs/fitsViewer/FitsLoader.js',
        '/sitools/client-extension/resources/libs/fitsViewer/astroFits.js',
        '/sitools/client-extension/resources/libs/fitsViewer/vec3.js',
        '/sitools/client-extension/resources/libs/fitsViewer/wcs.js'
    ],

    css: ['/sitools/client-extension/resources/css/fitsViewer/fits.css'],

    i18nFolderPath : ['/sitools/client-extension/resources/i18n/fitsViewer/'],

    statics: {
        getDefaultParameters: function () {
            return [{
                name: "featureType",
                value: "Image"
            }, {
                name: "columnAlias",
                value: ""
            }];
        },
        getParameters: function () {
            var urlDataset = Ext.getCmp("dsFieldParametersPanel").urlDataset;
            return [{
                jsObj: "Ext.form.ComboBox",
                config: {
                    fieldLabel: i18n.get('headers.previewUrl'),
                    width: 200,
                    typeAhead: true,
                    queryMode: 'local',
                    forceSelection: true,
                    triggerAction: 'all',
                    valueField: 'display',
                    displayField: 'display',
                    value: 'Image',
                    store: Ext.create('Ext.data.ArrayStore', {
                        autoLoad: true,
                        fields: ['value', 'display', 'tooltip'],
                        data: [
                            ['', ''],
                            ['Image', 'Image',
                                i18n.get("label.image.tooltip")],
                            ['URL', 'URL', i18n.get("label.url.tooltip")],
                            ['DataSetLink', 'DataSetLink',
                                i18n.get("label.datasetlink.tooltip")]]
                    }),
                    listeners: {
                        scope: this,
                        change: function (combo, newValue, oldValue) {
                        }
                    },
                    name: "featureType",
                    id: "featureType"
                }
            }, {
                jsObj: "Ext.form.ComboBox",
                config: {
                    fieldLabel: i18n.get('label.columnImage'),
                    width: 200,
                    typeAhead: true,
                    queryMode: 'local',
                    forceSelection: true,
                    triggerAction: 'all',
                    store: Ext.create('Ext.data.JsonStore', {
                        autoLoad: true,
                        fields: ['columnAlias'],
                        proxy: {
                            type: 'ajax',
                            url: urlDataset,
                            reader: {
                                type: 'json',
                                root: "dataset.columnModel"
                            }
                        },
                        listeners: {
                            load: function (store) {
                                store.add({
                                    columnAlias: ""
                                });
                            }
                        }
                    }),
                    valueField: 'columnAlias',
                    displayField: 'columnAlias',
                    listeners: {
                        render: function (c) {
                            Ext.QuickTips.register({
                                target: c,
                                text: i18n.get('label.columnImageTooltip')
                            });
                        }
                    },
                    name: "columnAlias",
                    id: "columnAlias",
                    value: ""
                }
            }];
        }
    },

    init: function (config) {
        var selected;
        if (Ext.isEmpty(config.record)) {
            selected = config.dataview.getSelections()[0];
        } else {
            selected = config.record;
        }

        this.i18nFitsService = I18nRegistry.retrieve('fitsService');

        if (Ext.isEmpty(selected.data.fits)) {
            return Ext.Msg.alert(i18n.get('label.info'), this.i18nFitsService.get('label.noFitsFile'));
        }

        config.record = selected;
        config.i18nFitsService = this.i18nFitsService;


        var fitsServiceComponent = Ext.create('sitools.extension.component.datasets.fitsService.FitsServiceComponent');
        fitsServiceComponent.create(this.getApplication());
        fitsServiceComponent.init(config);
    }
});