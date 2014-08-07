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
Ext.define('sitools.user.utils.CommonTreeUtils', {
    singleton : true,
    addShowData : function (node, dataset) {
        node.appendChild({
            id : "nodedata" + dataset.id,
            text : i18n.get('label.dataTitle'),
            winTitle : i18n.get('label.dataTitle') + " : " + dataset.name,
            leaf : true,
            type : "data",
            properties : {
                dataset : dataset
            },
            icon : loadUrl.get('APP_URL') + "/common/res/images/icons/tree_datasets.png", 
        });
        
    },

    addShowDefinition : function (node, dataset) {
        node.appendChild({
            id : Ext.id(),
            text : i18n.get('label.definitionTitle'),
            winTitle : i18n.get('label.definitionTitle') + " : " + dataset.name,
            leaf : true,
            type : "defi",
            properties : {
                dataset : dataset
            },
            icon : loadUrl.get('APP_URL') + "/common/res/images/icons/tree_dictionary.png"
        });
    },

    addOpensearch : function (node, dataset) {
        node.appendChild({
            id : Ext.id(),
            text : i18n.get('label.opensearch'),
            leaf : true,
            type : "openSearch",
            properties : {
                dataset : dataset
            },
            icon : loadUrl.get('APP_URL') + "/common/res/images/icons/toolbar_open_search.png"
        });
    },

    addForm : function (node, dataset) {
        node.appendChild({
            text : i18n.get('label.forms'),
            children : [],
            id : Ext.id(),
            expandable : true,
            properties : {
                dataset : dataset
            },
            type : 'listForms',
        });
    },

    
    addFeeds : function (node, dataset) {
        node.appendChild({
            id : Ext.id(),
            text : i18n.get('label.feeds'),
            type : 'listFeeds',
            expandable : true,
            children : [],
            properties : {
                dataset : dataset
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
        var win = desktop.getWindow("wind" + node.get("id"));

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
            switch(node.get("type")) {
            case "data" :
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
                break;
            case "defi":
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
                break;
            case "openSearch" :
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
                break;
            case "form":
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
                break;
            case "feeds":
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
                break;
            }
            SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);

        } else {
            win.toFront();
        }
    },
    
    

    handleBeforeExpandNode : function (node) {
        switch (node.get("type")) {
        case 'listForms':
            commonTreeUtils.handleBeforeExpandForm(node);
            break;
        case 'listFeeds':
            commonTreeUtils.handleBeforeExpandFeeds(node);
            break;
        }

    },
    
    handleBeforeExpandForm : function (node) {
        var url = node.parentNode.get('url');
        var dataset = node.get('properties').dataset;
        Ext.Ajax.request({
            url : url + '/forms?media=json',
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
                        type : "form",
                        text : form.name,
                        node : this,
                        properties : {
                            dataset : dataset,
                            form : form
                        },
                        icon : loadUrl.get('APP_URL') + "/common/res/images/icons/tree_forms.png", 
                    });
                });

            }
        });
    },
    
    handleBeforeExpandFeeds : function (node) {
        var url = node.parentNode.get('url');
        var dataset = node.get('properties').dataset;
        Ext.Ajax.request({
            url : url + '/feeds?media=json',
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
                        type : "feeds",
                        text : feed.name,
                        properties : {
                            dataset : dataset,
                            feed : feed
                        },
                        icon : loadUrl.get('APP_URL') + "/common/res/images/icons/rss.png"
                    });
                });
    
            }
        });
    }
});

commonTreeUtils = sitools.user.utils.CommonTreeUtils;