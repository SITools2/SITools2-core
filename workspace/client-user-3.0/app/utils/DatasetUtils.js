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
/*global Ext, sitools, i18n,document */
Ext.namespace("sitools.user.utils");

/**
 * A static method to transform a parameter to a sitools component.
 * @static
 * @param {} parameter the parameter as stored in the formParameter Model
 * @param {string} dataUrl the Url to request eventual data
 * @param {string} formId the main formId
 * @param {} datasetCm the dataset Column model
 * @param {string} context should be dataset or project.
 *  @param {formComponentsPanel} the parent form
 * @return {Ext.Container} the container to include into form
 */
Ext.define('sitools.user.utils.DatasetUtils', {
    singleton: true,

    openDataset: function (url, componentConfig, windowConfig) {
        Ext.Ajax.request({
            method: "GET",
            url: url,
            success: function (ret) {
                var Json = Ext.decode(ret.responseText);
                var dataset = Json.dataset;
                sitools.user.utils.DatasetUtils.showDataset(dataset, componentConfig, windowConfig);
            }
        });
    },

    openForm: function (formId, datasetUrl, componentConfig, windowConfig) {
        var formUrl = datasetUrl + "/forms/" + formId;
        //get dataset
        Ext.Ajax.request({
            method: "GET",
            url: datasetUrl,
            success: function (ret) {
                var Json = Ext.decode(ret.responseText);
                var dataset = Json.dataset;
                Ext.Ajax.request({
                    method: "GET",
                    url: formUrl,
                    success: function (ret) {
                        var Json = Ext.decode(ret.responseText);
                        var form = Json.form;
                        sitools.user.utils.DatasetUtils.showForm(form, dataset, componentConfig, windowConfig);
                    }
                });
            }
        });
    },

    showDataset: function (dataset, componentConfig, windowConfig) {
        var javascriptObject = Desktop.getNavMode().getDatasetOpenMode(dataset);

        if (Ext.isEmpty(componentConfig)) {
            componentConfig = {};
        }

        if (Ext.isEmpty(windowConfig)) {
            windowConfig = {};
        }

        componentConfig = Ext.apply(componentConfig, {
            dataset: dataset,
            preferencesPath: "/" + dataset.name,
            preferencesFileName: "datasetOverview"

        });

        windowConfig = Ext.apply(windowConfig, {
            saveToolbar: true
        });

        var sitoolsController = Desktop.getApplication().getController('core.SitoolsController');
        sitoolsController.openComponent(javascriptObject, componentConfig, windowConfig);
    },

    showForm: function (form, dataset, componentConfig, windowConfig) {

        var javascriptObject = Desktop.getNavMode().getFormOpenMode();

        if (Ext.isEmpty(windowConfig)) {
            windowConfig = {};
        }
        if (Ext.isEmpty(componentConfig)) {
            componentConfig = {};
        }

        Ext.apply(componentConfig, {
            dataset: dataset,
            form: form,
            preferencesPath: "/" + dataset.name + "/forms",
            preferencesFileName: form.name

        });

        windowConfig = Ext.apply(windowConfig, {
            saveToolbar: true
        });

        var sitoolsController = Desktop.getApplication().getController('core.SitoolsController');
        sitoolsController.openComponent(javascriptObject, componentConfig, windowConfig);
    },
    showFeed: function (feed, dataset, componentConfig, windowConfig) {
        if (Ext.isEmpty(componentConfig)) {
            componentConfig = {};
        }

        var url = dataset.sitoolsAttachementForUsers + "/clientFeeds/" + feed.name;

        Ext.apply(componentConfig, {
            parentId: dataset.id,
            parentName: dataset.name,
            feed: feed,
            url: url
        });
        var sitoolsController = Desktop.getApplication().getController('core.SitoolsController');
        sitoolsController.openComponent('sitools.user.component.feeds.FeedComponent', componentConfig, windowConfig);
    },
    showOpensearch: function (dataset, componentConfig, windowConfig) {
        if (Ext.isEmpty(componentConfig)) {
            componentConfig = {};
        }

        Ext.apply(componentConfig, {
            datasetId: dataset.id,
            dataUrl: dataset.sitoolsAttachementForUsers,
            datasetName: dataset.name
        });
        var sitoolsController = Desktop.getApplication().getController('core.SitoolsController');
        sitoolsController.openComponent('sitools.user.component.datasets.opensearch.Opensearch', componentConfig, windowConfig);
    },

    showDefinition: function (dataset, componentConfig, windowConfig) {
        if (Ext.isEmpty(componentConfig)) {
            componentConfig = {};
        }

        Ext.apply(componentConfig, {
            datasetId: dataset.id,
            datasetDescription: dataset.description,
            datasetCm: dataset.columnModel,
            datasetName: dataset.name,
            dictionaryMappings: dataset.dictionaryMappings
        });

        var sitoolsController = Desktop.getApplication().getController('core.SitoolsController');
        sitoolsController.openComponent('sitools.user.component.datasets.columnsDefinition.ColumnsDefinition', componentConfig, windowConfig);
    },


    openDefinition: function (url) {

        Ext.Ajax.request({
            method: "GET",
            url: url,
            success: function (ret) {
                var Json = Ext.decode(ret.responseText);
                var dataset = Json.dataset;
                sitools.user.utils.DatasetUtils.showDefinition(dataset);
            }
        });

    },

    /**
     * A method call when click on dataset Icon. Request the dataset, and open a window depending on type
     *
     * @static
     * @param {string} url the url to request the dataset
     * @param {string} type the type of the component.
     * @param {} extraCmpConfig an extra config to apply to the component.
     */
    clickDatasetIcone: function (url, type, extraCmpConfig, windowConfig) {
        Ext.Ajax.request({
            method: "GET",
            url: url,
            success: function (ret) {
                var Json = Ext.decode(ret.responseText);

                var dataset = Json.dataset;
                var componentCfg, javascriptObject;
                windowConfig = Ext.apply({
                    datasetName: dataset.name,
                    type: type,
                    saveToolbar: true,
                    toolbarItems: []
                });
                switch (type) {
                    case "newColumn" :
                        // Mon code spécifique

                    case "desc" :
                        Ext.apply(windowConfig, {
                            title: i18n.get('label.description') + " : " + dataset.name,
                            id: "desc" + dataset.id,
                            saveToolbar: false,
                            iconCls: "version"
                        });

                        componentCfg = {
                            autoScroll: true,
                            html: dataset.descriptionHTML
                        };
                        var view = Ext.create("Ext.Panel", componentCfg);
                        var sitoolsController = Desktop.getApplication().getController('core.SitoolsController');
                        sitoolsController.openSimpleWindow(view, windowConfig);
                        break;
                    case "data" :
                        sitools.user.utils.DatasetUtils.showDataset(dataset, extraCmpConfig);
                        break;
                    case "forms" :

                        Ext.Ajax.request({
                            method: "GET",
                            url: dataset.sitoolsAttachementForUsers + "/forms",
                            success: function (ret) {
                                try {
                                    var Json = Ext.decode(ret.responseText);
                                    if (!Json.success) {
                                        throw Json.message;
                                    }
                                    if (Json.total === 0) {
                                        throw i18n.get('label.noForms');
                                    }

                                    var javascriptObject = Desktop.getNavMode().getFormOpenMode();
                                    var sitoolsController = Desktop.getApplication().getController('core.SitoolsController');

                                    if (Json.total == 1) {
                                        var form = Json.data[0];
                                        sitools.user.utils.DatasetUtils.showForm(form, dataset);
                                    }
                                    else {

                                        var handler = null;
                                        var menuItem = [{
                                            text: i18n.get('label.openForm'),
                                            plain: false,
                                            canActivate: false,
                                            cls: 'userMenuCls'
                                        }, {
                                            xtype: 'menuseparator',
                                            separatorCls: 'customMenuSeparator'
                                        }];

                                        Ext.each(Json.data, function (form) {
                                            menuItem.push({
                                                text: form.name,
                                                handler: function () {
                                                    sitools.user.utils.DatasetUtils.showForm(form, dataset, extraCmpConfig);
                                                },
                                                icon: loadUrl.get('APP_URL') + "/common/res/images/icons/tree_forms.png"
                                            });

                                        }, this);

                                        var menuForms = Ext.create("Ext.menu.Menu", {
                                            border: false,
                                            plain: true,
                                            closeAction: 'hide',
                                            items: menuItem
                                        });
                                        menuForms.showAt(Ext.EventObject.getXY());
                                    }
                                }
                                catch (err) {
                                    var errorMsg = (Ext.isEmpty(err.message)) ? err : err.message;
                                    popupMessage({
                                        iconCls: 'x-icon-information',
                                        title: i18n.get('label.information'),
                                        html: i18n.get(errorMsg),
                                        autoDestroy: true,
                                        hideDelay: 1000
                                    });
                                    throw err;
                                }
                            }
                        });

                        break;
                    case "feeds" :

                        Ext.Ajax.request({
                            method: "GET",
                            url: dataset.sitoolsAttachementForUsers + "/feeds",
                            success: function (ret) {
                                try {
                                    var Json = Ext.decode(ret.responseText);
                                    if (!Json.success) {
                                        throw Json.message;
                                    }
                                    if (Json.total === 0) {
                                        throw i18n.get('label.noFeeds');
                                    }

                                    if (Json.total == 1) {
                                        var feed = Json.data[0];
                                        sitools.user.utils.DatasetUtils.showFeed(feed, dataset);
                                    }
                                    else {
                                        var handler = null;
                                        var menuItem = []
                                        Ext.each(Json.data, function (feed) {
                                            menuItem.push({
                                                text: feed.name,
                                                handler: function () {
                                                    sitools.user.utils.DatasetUtils.showFeed(feed, dataset);
                                                },
                                                icon: loadUrl.get('APP_URL') + "/common/res/images/icons/rss.png"
                                            });

                                        }, this);
                                        var menuFeeds = Ext.create("Ext.menu.Menu", {
                                            border: false,
                                            plain: true,
                                            width: 260,
                                            closeAction: 'hide',
                                            items: menuItem
                                        });
                                        menuFeeds.showAt(Ext.EventObject.getXY());
                                    }

                                }
                                catch (err) {
                                    var errorMsg = (Ext.isEmpty(err.message)) ? err : err.message;
                                    popupMessage({
                                        iconCls: 'x-icon-information',
                                        title: i18n.get('label.information'),
                                        html: i18n.get(errorMsg),
                                        autoDestroy: true,
                                        hideDelay: 1000
                                    });
                                    throw err;
                                }
                            }
                        });

                        break;
                    case "defi" :
                        sitools.user.utils.DatasetUtils.showDefinition(dataset);
                        break;
                    case "openSearch" :
                        Ext.Ajax.request({
                            method: "GET",
                            url: dataset.sitoolsAttachementForUsers + "/opensearch.xml",
                            success: function (ret) {
                                var xml = ret.responseXML;
                                var dq = Ext.DomQuery;
                                // check if there is a success node
                                // in the xml
                                var success = dq.selectNode('OpenSearchDescription ', xml);
                                if (!success) {
                                    popupMessage({
                                        iconCls: 'x-icon-information',
                                        title: i18n.get('label.information'),
                                        html: i18n.get("label.noOpenSearch"),
                                        autoDestroy: true,
                                        hideDelay: 1000
                                    });
                                    return;
                                }

                                sitools.user.utils.DatasetUtils.showOpensearch(dataset);

                            }
                        });

                        break;
                }
            },
            failure: alertFailure
        });
    }
});
