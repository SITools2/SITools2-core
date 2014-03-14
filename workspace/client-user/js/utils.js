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

/**
 * Object to expose common tree Utils Methods
 * @requires sitools.user.component.datasetOpensearch
 * @requires sitools.user.component.forms.mainContainer
 */
/*global Ext, i18n, loadUrl, getDesktop, sitools, SitoolsDesk */
var commonTreeUtils = {
    addShowData : function (node, dataset) {
        node.appendChild({
            id : "nodedata" + dataset.id,
            text : i18n.get('label.dataTitle'),
            winTitle : i18n.get('label.dataTitle') + " : " + dataset.name,
            leaf : true,
            type : "data",
            datasetId : dataset.id,
            columnModel : dataset.columnModel,
            datasetName : dataset.name,
            datasetDescription : dataset.description,
            dataUrl : dataset.sitoolsAttachementForUsers, 
            dictionaryMappings : dataset.dictionaryMappings,
            icon : loadUrl.get('APP_URL') + "/common/res/images/icons/tree_datasets.png", 
            datasetView : dataset.datasetView, 
            datasetViewConfig : dataset.datasetViewConfig, 
            sitoolsAttachementForUsers : dataset.sitoolsAttachementForUsers
        });
        
    },

    addShowDefinition : function (node, dataset) {
        node.appendChild({
            text : i18n.get('label.definitionTitle'),
            winTitle : i18n.get('label.definitionTitle') + " : " + dataset.name,
            leaf : true,
            type : "defi",
            datasetId : dataset.id,
            columnModel : dataset.columnModel,
            datasetName : dataset.name, 
            dictionaryMappings : dataset.dictionaryMappings,
            icon : loadUrl.get('APP_URL') + "/common/res/images/icons/tree_dictionary.png"
        });
    },

    addOpensearch : function (node, dataset) {
        node.appendChild({
            text : i18n.get('label.opensearch'),
            winTitle : i18n.get('label.opensearch') + " : " + dataset.name,
            leaf : true,
            type : "openSearch",
            datasetId : dataset.id,
            columnModel : dataset.columnModel,
            datasetName : dataset.name,
            dataUrl : dataset.sitoolsAttachementForUsers, 
            icon : loadUrl.get('APP_URL') + "/common/res/images/icons/toolbar_open_search.png"
        });
    },

    addForm : function (node, dataset) {
        node.appendChild({
            text : i18n.get('label.forms'),
            leaf : false,
            children : [ {
                leaf : true
            } ],
            datasetId : dataset.id,
            columnModel : dataset.columnModel,
            datasetName : dataset.name,
            listeners : {
                scope : this,
                beforeexpand : function (node) {
                    var conn = new Ext.data.Connection();
                    conn.request({
                        url : dataset.sitoolsAttachementForUsers + '/forms?media=json',
                        success : function (response) {
                            node.removeAll(true);
                            var forms = Ext.decode(response.responseText);
                            if (!forms.success) {
                                Ext.msg.alert(i18n.get('label.warning'), forms.message);
                                return;
                            }
                            Ext.each(forms.data, function (form) {
                                node.appendChild({
                                    leaf : true,
                                    winTitle : i18n.get('label.forms') + " : " + dataset.name + "." + form.name,
                                    dataset : dataset, 
                                    datasetId : dataset.id,
                                    columnModel : dataset.columnModel,
                                    datasetName : dataset.name,
                                    dataUrl : dataset.sitoolsAttachementForUsers,
                                    type : "form",
                                    text : form.name,
                                    formId : form.id,
                                    formName : form.name,
                                    formParameters : form.parameters,
                                    formZones : form.zones,
                                    formWidth : form.width,
                                    formHeight : form.height,
                                    formCss : form.css, 
                                    node : this, 
                                    icon : loadUrl.get('APP_URL') + "/common/res/images/icons/tree_forms.png", 
                                    datasetView : dataset.datasetView

                                });
                            });

                        }
                    });
                }
            }
        });
    },

    addFeeds : function (node, dataset) {
        node.appendChild({
            text : i18n.get('label.feeds'),
            leaf : false,
            children : [ {
                leaf : true
            } ],
            datasetId : dataset.id,
            datasetName : dataset.name,
            listeners : {
                scope : this,
                beforeexpand : function (node) {
                    var conn = new Ext.data.Connection();
                    conn.request({
                        url : dataset.sitoolsAttachementForUsers + '/feeds?media=json',
                        success : function (response) {
                            node.removeAll(true);
                            var feeds = Ext.decode(response.responseText);
                            if (!feeds.success) {
                                Ext.msg.alert(i18n.get('label.warning'), feeds.message);
                                return;
                            }
                            Ext.each(feeds.data, function (feed) {
                                node.appendChild({
                                    leaf : true,
                                    winTitle : i18n.get('label.feeds') + " : (" + dataset.name + ") " + feed.title,
                                    type : "feeds",
                                    text : feed.name,
                                    datasetId : dataset.id,
                                    feedId : feed.name,
                                    dataUrl : dataset.sitoolsAttachementForUsers,
                                    feedType : feed.feedType,
                                    feedSource : feed.feedSource,
                                    datasetName : dataset.name, 
                                    icon : loadUrl.get('APP_URL') + "/common/res/images/icons/rss.png"
                                    
                                });
                            });

                        }
                    });
                }
            }
        });
    },

    /**
     * Build the component regarding to the node, 
     * Load a window with this component into the SitoolsDesk
     * @param node
     */
    treeAction : function (node) {
        var desktop = getDesktop();
        var win = desktop.getWindow("wind" + node.id);

        if (!win) {
            var componentCfg, javascriptObject;
            var windowConfig = {
				datasetId : node.attributes.datasetId,
				title : node.attributes.winTitle, 
                datasetName : node.attributes.datasetName, 
                datasetDescription : node.attributes.datasetDescription,
                type : node.attributes.type, 
                saveToolbar : true, 
                toolbarItems : []
            };

            if (node.attributes.type === "data") {
                //open the dataView according to the dataset Configuration.
//                javascriptObject = eval(node.attributes.datasetView.jsObject);
				javascriptObject = eval(SitoolsDesk.navProfile.getDatasetOpenMode(node.attributes));
				
				Ext.apply(windowConfig, {
					winWidth : 900, 
					winHeight : 400, 
					iconCls : "dataviews"
				});
                componentCfg = {
                    dataUrl : node.attributes.dataUrl,
                    datasetId : node.attributes.datasetId,
                    datasetCm : node.attributes.columnModel, 
                    datasetName : node.attributes.datasetName,
                    dictionaryMappings : node.attributes.dictionaryMappings,
                    datasetViewConfig : node.attributes.datasetViewConfig, 
                    preferencesPath : "/" + node.attributes.datasetName, 
                    preferencesFileName : "datasetOverview", 
                    sitoolsAttachementForUsers : node.attributes.sitoolsAttachementForUsers
                };
                
            }
            if (node.attributes.type === "defi") {
                javascriptObject = sitools.user.component.columnsDefinition;
                Ext.apply(windowConfig, {
                    id : node.attributes.type + node.attributes.datasetId, 
					iconCls : "semantic"
                });
                componentCfg = {
                    datasetId : node.attributes.datasetId,
                    datasetCm : node.attributes.columnModel, 
                    datasetName : node.attributes.datasetName,
                    dictionaryMappings : node.attributes.dictionaryMappings, 
                    preferencesPath : "/" + node.attributes.datasetName, 
                    preferencesFileName : "semantic"
                };
            }
            if (node.attributes.type === "openSearch") {
                javascriptObject = sitools.user.component.datasetOpensearch;
                Ext.apply(windowConfig, {
                    id : node.attributes.type + node.attributes.datasetId, 
					iconCls : "openSearch"
                });
                componentCfg = {
                    datasetId : node.attributes.datasetId,
                    dataUrl : node.attributes.dataUrl, 
                    datasetName : node.attributes.datasetName, 
                    preferencesPath : "/" + node.attributes.datasetName, 
                    preferencesFileName : "openSearch"
                };
            }
            if (node.attributes.type === "form") {
                javascriptObject = sitools.user.component.forms.mainContainer;
                Ext.apply(windowConfig, {
                    id : node.attributes.type + node.attributes.datasetId + node.attributes.formId, 
					iconCls : "forms"
                });
                componentCfg = {
                    dataUrl : node.attributes.dataUrl,
                    dataset : node.attributes.dataset, 
                    formId : node.attributes.formId,
                    formName : node.attributes.formName,
                    formParameters : node.attributes.formParameters,
                    formZones : node.attributes.formZones,
                    formWidth : node.attributes.formWidth,
                    formHeight : node.attributes.formHeight, 
                    formCss : node.attributes.formCss, 
                    preferencesPath : "/" + node.attributes.datasetName + "/forms", 
                    preferencesFileName : node.attributes.formName
                };
            }
            if (node.attributes.type === "feeds") {
                javascriptObject = sitools.widget.FeedGridFlux;
                var url = node.attributes.dataUrl + "/clientFeeds/" + node.attributes.feedId;
                Ext.apply(windowConfig, {
                    id : node.attributes.type + node.attributes.datasetId + node.attributes.feedId, 
                    iconCls : "feedsModule"
                });
                componentCfg = {
                    datasetId : node.attributes.datasetId,
                    urlFeed : url,
                    feedType : node.attributes.feedType, 
                    datasetName : node.attributes.datasetName,
                    feedSource : node.attributes.feedSource,
                    autoLoad : true
                };

            }
            SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);

        } else {
            win.toFront();
        }
    }
};