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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse */

Ext.namespace('sitools.user.controller.modules.datasetExplorer');
/**
 * datasetExplorer Module
 * 
 * @class sitools.user.modules.datasetExplorer
 * @extends Ext.Panel
 */
Ext.define('sitools.user.controller.modules.datasetExplorer.DatasetExplorer', {
    extend : 'Ext.app.Controller',
    alias : 'sitools.user.modules.datasetExplorer',
    requires : ['sitools.user.utils.CommonTreeUtils',
                'sitools.user.component.datasets.columnDefinition.ColumnsDefinition',
                'sitools.user.component.form.FormComponent',
                'sitools.user.component.feeds.FeedComponent'],

    views : [ 'modules.datasetExplorer.DatasetExplorer' ],

    init : function () {
        this.control({
            'treepanel#datasetExplorer' : {
                beforeitemexpand : function (node, opts) {
                    if (node.isRoot()) {
//                        this.getStore('DatasetTreeStore').reload();
                        return;
                    }
                    
                    if(node.get("type") == "DataSet"){
                    
                        node.removeAll();
    
                        Ext.Ajax.request({
                            url : node.get('url'),
                            scope : this,
                            success : function (response) {
                                var dataset = Ext.decode(response.responseText).dataset;
                                commonTreeUtils.addShowData(node, dataset);
                                commonTreeUtils.addShowDefinition(node, dataset);
    //                            SitoolsDesk.navProfile.manageDatasetExplorerShowDefinitionAndForms(commonTreeUtils, node, dataset);
    
                                Ext.Ajax.request({
                                    url : dataset.sitoolsAttachementForUsers + "/opensearch.xml",
                                    scope : this,
                                    success : function (response) {
                                        var xml = response.responseXML;
                                        var dq = Ext.DomQuery;
                                        // check if there is a success node
                                        // in the xml
                                        var success = dq.selectNode('OpenSearchDescription ', xml);
    
                                        if (!Ext.isEmpty(success)) {
                                            commonTreeUtils.addOpensearch(node, dataset);
                                        }
                                    }
                                });
                                commonTreeUtils.addForm(node, dataset);
                                commonTreeUtils.addFeeds(node, dataset);
                            }
                        });
                    }else {
                        commonTreeUtils.handleBeforeExpandNode(node);
                    }
                    return true;

                },
                
                itemclick : function ( tree, node, item, index, e, eOpts ) {
                    if(node.isLeaf()) {
                        
                        switch(node.get("type")) {
                        case "data" :
                            var dataset = node.get("properties").dataset;
                            var datasetViewComponent  = Ext.create(dataset.datasetView.jsObject);
                            datasetViewComponent.create(this.getApplication());
                            datasetViewComponent.init(dataset);
                            break;
                        case "defi" : 
                            var columnDefinition  = Ext.create("sitools.user.component.datasets.columnDefinition.ColumnsDefinition");
                            columnDefinition.create(this.getApplication());
                            var dataset = node.get("properties").dataset;
                            var configService = {
                                datasetId : dataset.id,
                                datasetDescription : dataset.description,
                                datasetCm : dataset.columnModel,
                                datasetName : dataset.name,
                                dictionaryMappings : dataset.dictionaryMappings,
                                preferencesPath : "/" + dataset.name,
                                preferencesFileName : "semantic"
                            };
                            columnDefinition.init(configService);
                            break;
                        case 'form' :
                            var dataset = node.get("properties").dataset;
                            var form = node.get("properties").form;
                            var formComponent = Ext.create('sitools.user.component.form.FormComponent');
                            formComponent.create(this.getApplication());
                            formComponent.init(form, dataset);
                            break;
                        case 'feeds' :
                            var dataset = node.get("properties").dataset;
                            var feed = node.get("properties").feed;
                            var feedComponent = Ext.create('sitools.user.component.feeds.FeedComponent');
                            feedComponent.create(this.getApplication());
                            
                            var url = dataset.sitoolsAttachementForUsers + "/clientFeeds/" + feed.name;
                            
                            var configFeed = {
                                parentId : dataset.id,
                                parentName : dataset.name,
                                feed : feed,
                                url : url
                            }
                            
                            feedComponent.init(configFeed);
                            break;
                        }
                    }
                }
            }

        });

        this.listen({
            store : {
                '#DatasetTreeStore' : {
                    append : function (rootNode, node, opts) {
                        if (node.get('type') == "DataSet") {
                            node.set("leaf", false);
                            node.set("children", []);
                            node.set("text", node.get("name"));
                        }
                    }
                }
            }
        });

    }
});
