/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, projectGlobal, commonTreeUtils, showResponse, document, i18n, loadUrl*/
/*
 * @include "../../env.js" 
 */
Ext.namespace('sitools.user.modules');

/**
 * Dataset Explorer Module.
 * Displays each dataset of the Project.
 * Uses commonTreeUtils object for action on the tree
 * @class sitools.user.modules.datasetExplorer
 * @extends Ext.tree.TreePanel
 */
sitools.user.modules.datasetExplorer = function (config) {
    this.datasets = [];
    /**
     * INDEX JPB var projectId = Ext.util.Cookies.get('projectId'); if
     * (Ext.isEmpty(projectId)){ Ext.Msg.alert(i18n.get ('warning.noProject'));
     * return; }
     */

//    var projectId = projectGlobal.getProjectId();
    var projectAttachment = projectGlobal.sitoolsAttachementForUsers;
    var treeUtils = commonTreeUtils;

    var conn = Ext.Ajax;
    conn.request({
        url : projectAttachment + '/datasets?media=json',
        method : 'GET',
        scriptTag : true,
        scope : this,
        success : function (response) {
            if (!showResponse(response, false)) {
                return;
            }
            var config = Ext.decode(response.responseText);
            var i = 0;
            Ext.each(config.data, function (dataset) {
                if (dataset.authorized !== "false") {
	                this.datasets.push({
	                    text : dataset.name,
	                    listeners : {
	                        scope : this,
	                        beforeexpand : function (node) {
	                            node.removeAll(true);
                                if (dataset.status != "ACTIVE") {
                                    var notify = new Ext.ux.Notification({
                                        iconCls : 'x-icon-information',
                                        title : i18n.get('label.information'),
                                        html : i18n.get('warning.wrongStatus'),
                                        autoDestroy : true,
                                        hideDelay : 1000
                                    });
                                    notify.show(document);
                                    return true;
                                }
                                conn.request({
	                                //url : '/sitools/datasets/' + dataset.id + '?media=json',
                                    url : dataset.url + '?media=json',
	                                scope : this,
	                                success : function (response) {
	                                    var dataset = Ext.decode(response.responseText).dataset;
	                                    commonTreeUtils.addShowData(node, dataset);
	                                    SitoolsDesk.navProfile.manageDatasetExplorerShowDefinitionAndForms(commonTreeUtils, node, dataset);
	                                    
	                                    conn.request({
	                                        url : dataset.sitoolsAttachementForUsers + "/opensearch.xml",
	                                        scope : this,
	                                        success : function (response) {
	                                            var xml = response.responseXML;
	                                            var dq = Ext.DomQuery;
	                                            // check if there is a success node
	                                            // in the xml
	                                            var success = dq.selectNode('OpenSearchDescription ', xml);
	
	                                            if (success !== undefined) {
	                                                commonTreeUtils.addOpensearch(node, dataset);
	                                            }
	                                            return true;
	                                        }
	                                    });
//	                                    commonTreeUtils.addForm(node, dataset);
	                                    commonTreeUtils.addFeeds(node, dataset);
	                                }
	                            });
	                            return true;
	                        }
	                    },
	                    children : [ {
	                        text : ""
	                    } ]
	                });
                }
                else {
					this.datasets.push({
	                    text : dataset.name,
	                    leaf : false, 
	                    icon : loadUrl.get('APP_URL') + "/common/res/images/icons/cadenas.png", 
						authorized : false
					});
                }
                i++;
            }, this);
            this.fireEvent('datasetLoaded');
        },
        failure : function () {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noProject'));
        }
    });
    this.rootNode = new Ext.tree.AsyncTreeNode({
        nodeType : 'async',
        text : 'dataSets',
        leaf : false,
        draggable : false,
        children : this.datasets,
        expanded : false,
        listeners : {
            scope : this,
            beforeexpand : function () {
                this.rootNode.removeAll(true);
                this.rootNode.appendChild(this.datasets);
                return true;
            }
        }
    });
    
    this.tbar = {
            xtype : 'toolbar',
            cls : 'services-toolbar',
            height : 15,
            defaults : {
                scope : this,
                cls : 'services-toolbar-btn'
            },
            items : [ ]
        };

    sitools.user.modules.datasetExplorer.superclass.constructor.call(this, Ext.apply({
        expanded : true,
        useArrows : true,
        autoScroll : true,
        animate : true,
        loader : new Ext.tree.TreeLoader(),
        root : this.rootNode,
        rootVisible : true,
//        tbar : [],
        listeners : {
            // scope : SitoolsDesk.app,
            beforeload : function (node) {
                return node.isRoot || Ext.isDefined(node.attributes.children);
            },
            click : function (node) {
                if (node.isLeaf() && node.attributes.authorized !== false) {
                    treeUtils.treeAction(node);
                }
            },
            datasetLoaded : function (){
                this.getRootNode().expand();
            }
        }
    }));

};

Ext.extend(sitools.user.modules.datasetExplorer, Ext.tree.TreePanel, {
    /**
     * method called when trying to save preference
     * @returns
     */
    _getSettings : function () {
		return {
            preferencesPath : "/modules", 
            preferencesFileName : this.id
        };

    }});

Ext.reg('sitools.user.modules.datasetExplorer', sitools.user.modules.datasetExplorer);
