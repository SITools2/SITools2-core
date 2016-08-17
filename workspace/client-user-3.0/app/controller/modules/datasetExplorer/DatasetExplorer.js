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
                'sitools.user.component.datasets.columnsDefinition.ColumnsDefinition',
                'sitools.user.component.form.FormComponent',
                'sitools.user.component.feeds.FeedComponent',
                'sitools.user.component.datasets.opensearch.Opensearch'],

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
                        var dataset = node.get("properties").dataset;
                        switch(node.get("type")) {
                        case "data" :
                            sitools.user.utils.DatasetUtils.showDataset(dataset);
                            break;
                        case "defi" : 
                            sitools.user.utils.DatasetUtils.showDefinition(dataset);
                            break;
                        case 'form' :
                            var form = node.get("properties").form;
                            sitools.user.utils.DatasetUtils.showForm(form, dataset);
                            break;
                        case 'feeds' :
                            var feed = node.get("properties").feed;
                            sitools.user.utils.DatasetUtils.showFeed(feed, dataset);
                            break;
                            
                        case 'openSearch' : 
                            sitools.user.utils.DatasetUtils.showOpensearch(dataset);
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
