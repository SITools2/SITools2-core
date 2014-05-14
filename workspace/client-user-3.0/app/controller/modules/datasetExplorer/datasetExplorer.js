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
    extend : 'sitools.user.controller.modules.Module',
    alias : 'sitools.user.modules.datasetExplorer',
    requires : ['sitools.user.utils.CommonTreeUtils'],

    store : [ 'DatasetTreeStore' ],

    views : [ 'modules.datasetExplorer.DatasetExplorer' ],

    init : function () {
        this.control({
            'treepanel#datasetExplorer' : {
                beforeitemexpand : function (node, opts) {
                    console.log("beforeExpand");
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
                                console.log("treePanel add nodes");
                                var dataset = Ext.decode(response.responseText).dataset;
                                commonTreeUtils.addShowData(node, dataset);
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
                        alert("TODO open " + node.get("type") + node.get("name"));
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

    },

    onLaunch : function () {
        var project = Ext.getStore('ProjectStore').getProject();
        var store = this.getStore("DatasetTreeStore");
        
        var view = Ext.create('sitools.user.view.modules.datasetExplorer.DatasetExplorer', {
            store : store
        });
        
        view.setRootNode({
            text : 'datasets',
            leaf : 'false'
        });  
        
        Ext.Ajax.request({
            url : project.get('sitoolsAttachementForUsers') + '/datasets',
            method : 'GET',
            scope : this,
            success : function (response) {
                var datasets = Ext.decode(response.responseText).data;
                Ext.each(datasets, function(dataset){
                    view.getRootNode().appendChild(dataset);
                });
            }
        });
        
        this.setViewCmp(view);

        this.open();

        this.callParent(arguments);
    },

    initModule : function (moduleModel) {
        this.callParent(arguments);
    },

    /**
     * method called when trying to save preference
     * 
     * @returns
     */
    _getSettings : function () {
        return {
            preferencesPath : "/modules",
            preferencesFileName : this.id
        };

    }
});
