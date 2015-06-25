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
/*global Ext, sitools, i18n, commonTreeUtils, projectGlobal, showResponse, document, SitoolsDesk, alertFailure, loadUrl*/
/*
 * @include "../../components/forms/forms.js"  
 */

Ext.namespace('sitools.user.modules');

/**
 * Displays a graph Module treeGrid
 * @class sitools.user.modules.projectGraphTree
 * @extends Ext.ux.tree.TreeGrid
 * @requires sitools.user.component.columnsDefinition
 */
sitools.user.modules.projectGraphTree = Ext.extend(Ext.ux.tree.TreeGrid, {
    enableHdMenu : false,
    enableSort : false,
    expanded : true,
    lines : true,
    useArrows : false,
    animate : true,
    columnResize : false,
    headerEllipsisSize : 12,
    layout : 'fit',
    initComponent : function () {
        this.columns = [ {
            dataIndex : 'text',
            width : 200,
            header : ' '
        }, {
            width : 100,
            dataIndex : 'datasetId', // TODO a     
            //cls : "grid-column-color",
            // changer avec
            // la variable
            // nbRecord
            tpl : new Ext.XTemplate('{[type=""]}', '<tpl if="this.exists(type) && type==\'dataset\'">{nbRecord} {[i18n.get("label.records")]}</tpl>', {
                exists : function (o) {
                    return typeof o !== 'undefined' && o !== null && o !== '';
                },
                // XTemplate configuration:
                compiled : true,
                disableFormats : true
            }),
            align : 'right',
            header : Ext.util.Format.ellipsis(i18n.get("label.records"),this.headerEllipsisSize)
        }, {
            width : 80,
            dataIndex : 'imageDs',
            header : i18n.get("label.image"),
            align : 'center',
            tpl : new Ext.XTemplate('{[type="",imageDs=""]}', '<tpl if="this.exists(type) && type==\'dataset\'  && this.exists(imageDs) && authorized==\'true\'"><img class="imageDsNode" src="{imageDs}"></img></tpl>', {
                exists : function (o) {
                    return typeof o !== 'undefined' && o !== null && o !== '';
                },
                // XTemplate configuration:
                compiled : true,
                disableFormats : true
            })
        }, {
            width : 90,
            dataIndex : 'readme',
            //cls : "grid-column-color",
            header : Ext.util.Format.ellipsis(i18n.get("label.descriptionMini"), this.headerEllipsisSize),
            tpl : new Ext.XTemplate('{[readme=""]}',
                                            '<tpl if="this.exists_description_node(readme,type)"><img ext:qtip="{readme}" src="' + loadUrl.get('APP_URL')
                                                    + '/common/res/images/icons/description_project_small.png"></tpl>',
                                            '<tpl if="this.exists_description_dataset(readme,type) && authorized==\'true\'">{url:this.getIcone}</tpl>', {
                exists_description_dataset : function (o, type) {
					return typeof o !== 'undefined'
							&& o !== null && o !== ''
							&& type === "dataset";
				},
				exists_description_node : function (o, type) {
					return typeof o !== 'undefined'
							&& o !== null && o !== ''
							&& type === "node";
				},
                getIcone : function (value) {
					return "<a href='#' onClick='sitools.user.clickDatasetIcone(\"" + value
                                                            + "\", \"desc\"); return false;'><img src='" + loadUrl.get('APP_URL')
                                                            + "/common/res/images/icons/help.png'></img></a>";
                },
                // XTemplate configuration:
                compiled : true,
                disableFormats : false
            }), 
            align : 'center'
        }, {
            width : 90,
            header : Ext.util.Format.ellipsis(i18n.get("label.data"),this.headerEllipsisSize),
            tpl : new Ext.XTemplate('{[datasetId=""]}', '<tpl if="this.exists(datasetId) && authorized==\'true\'">{url:this.getIcone}</tpl>', {
                exists : function (o) {
                    return typeof o !== 'undefined' && o !== null && o !== '';
                },
                getIcone : function (value) {
					return "<a href='#' onClick='sitools.user.clickDatasetIcone(\"" + value
                                                            + "\", \"data\"); return false;'><img src='" + loadUrl.get('APP_URL')
                                                            + "/common/res/images/icons/tree_datasets.png'></img></a>";
                },
                // XTemplate configuration:
                compiled : true,
                disableFormats : false
            }), 
            align : 'center'
        }, {
            width : 90,
            header : Ext.util.Format.ellipsis(i18n.get("label.definitionMini"),this.headerEllipsisSize),
            tpl : new Ext.XTemplate('{[datasetId=""]}', '<tpl if="this.exists(datasetId) && authorized==\'true\'">{url:this.getIcone}</tpl>', {
                exists : function (o) {
                    return typeof o !== 'undefined' && o !== null && o !== '';
                },
                getIcone : function (value) {
					return "<a href='#' onClick='sitools.user.clickDatasetIcone(\"" + value
                                                            + "\", \"defi\"); return false;'><img src='" + loadUrl.get('APP_URL')
                                                            + "/common/res/images/icons/tree_dictionary.png'></a>";
                },
                // XTemplate configuration:
                compiled : true,
                disableFormats : false
            }), 
            align : 'center'
        }, {
            width : 90,
            header : Ext.util.Format.ellipsis(i18n.get("label.feeds"),this.headerEllipsisSize),
            //cls : "grid-column-color",
            tpl : new Ext.XTemplate('{[datasetId=""]}', '<tpl if="this.exists(datasetId) && authorized==\'true\'">{url:this.getIcone}</tpl>', {
                exists : function (o) {
                    return typeof o !== 'undefined' && o !== null && o !== '';
                },
                getIcone : function (value) {
					return "<a href='#' onClick='sitools.user.clickDatasetIcone(\"" + value
                                                            + "\", \"feeds\"); return false;'><img src='" + loadUrl.get('APP_URL')
                                                            + "/common/res/images/icons/rss.png'></a>";
                },
                // XTemplate configuration:
                compiled : true,
                disableFormats : false
            }), 
            align : 'center'
        }, {
            width : 90,
            header : Ext.util.Format.ellipsis(i18n.get("label.opensearchMini"),this.headerEllipsisSize),
            tpl : new Ext.XTemplate('{[datasetId=""]}', '<tpl if="this.exists(datasetId) && authorized==\'true\'">{url:this.getIcone}</tpl>', {
                exists : function (o) {
                    return typeof o !== 'undefined' && o !== null && o !== '';
                },
                getIcone : function (value) {
					return "<a href='#' onClick='sitools.user.clickDatasetIcone(\"" + value
                                                            + "\", \"openSearch\"); return false;'><img src='" + loadUrl.get('APP_URL')
                                                            + "/common/res/images/icons/toolbar_open_search.png'></a>";
                },
                // XTemplate configuration:
                compiled : true,
                disableFormats : false
            }), 
            align : 'center'
        } ];

        SitoolsDesk.navProfile.manageProjectGraphColumns(this.columns);
        
        this.tbar = {
            xtype : 'toolbar',
            cls : 'services-toolbar',
            defaults : {
                scope : this,
                cls : 'services-toolbar-btn'
            },
            items : [ {
                scope : this,
                text : i18n.get('label.refresh'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_refresh.png',
                handler : this._onRefresh,
                xtype : 's-menuButton'
            } ]
        };
        var projectAttachment = projectGlobal.sitoolsAttachementForUsers;
        this.loader = new Ext.ux.tree.TreeGridLoader({
            requestMethod : 'GET',
            url : projectAttachment + "/graph",
            root : 'graph.nodeList',
            createNode : function (attr) {

                if (attr.type !== undefined && attr.type == "dataset") {
                    // attr.nodeType = 'templateTreeNode';
                    attr.icon = attr.authorized === "true" ? loadUrl.get('APP_URL') + "/common/res/images/icons/tree_datasets.png" : loadUrl.get('APP_URL') + "/common/res/images/icons/cadenas.png";
                    attr.leaf = true;

                }
                if (attr.type !== undefined && attr.type == "node") {
                    // if(attr.image!=undefined)
                    attr.icon = attr.image.url;
                    /*
                     * else attr.icon = undefined;
                     */
                    attr.iconCls = "graphNodeType";
                    attr.readme = attr.description;
                }		
                return Ext.ux.tree.TreeGridLoader.prototype.createNode.call(this, attr);
            }
        });

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

                var getData = Ext.decode(response.responseText);
                var datasetsList = getData.data;
                if (datasetsList !== null) {
                    // creates an index of datasets
                    this.datasets = {};
                    var i;
                    for (i = 0; i < datasetsList.length; i++) {
						this.datasets[datasetsList[i].id] = datasetsList[i];
                    }
                }

            }

        });

        sitools.user.modules.projectGraphTree.superclass.initComponent.call(this);

    },
    _onRefresh : function () {
        var root = this.getRootNode();
        this.loader.load(root);
    }, 
    /**
     * method called when trying to save preference
     * @returns
     */
    _getSettings : function () {
		return {
            preferencesPath : "/modules", 
            preferencesFileName : this.id
        };

    }

});

Ext.reg('sitools.user.modules.projectGraphTree', sitools.user.modules.projectGraphTree);
