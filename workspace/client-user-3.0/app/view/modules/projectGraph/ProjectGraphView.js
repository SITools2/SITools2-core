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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl*/

/**
 * @class sitools.user.view.modules.projectGraph.ProjectGraphView
 * @extends Ext.grid.GridPanel
 */

Ext.define('sitools.user.view.modules.projectGraph.ProjectGraphView', {
	extend : 'Ext.tree.Panel',
	alias : 'widget.projectGraph',

	layout : 'fit',
	border : false,
	bodyBorder : false,
	rootVisible : false,
	
	root : {
	  id : Ext.id()  
	},
	
	initComponent : function () {
	    this.store = Ext.create("sitools.user.store.ProjectGraphTreeStore");
	    var columnsConfig = Ext.create("Ext.util.MixedCollection");
	    
	    this.moduleModel.listProjectModulesConfig().each(function (config) {
            if (!Ext.isEmpty(config.get("value"))) {
                switch (config.get("name")) {
                case "columns" :
                    var columnsConf = Ext.JSON.decode(config.get("value"));
                    Ext.each(columnsConf, function (column) {
                        columnsConfig.add(column.columnName, column.selected);
                    });
                    break;
                }
            }
        }, this);
	    
	    var inscrusted = this.incrusted;

	    this.columns = {
            items : [ {
                xtype : 'treecolumn', 
                dataIndex : 'text',
                flex : 3
            }, {
                text : i18n.get("label.records"),
                dataIndex : "nbRecord",
                name : "records",
                flex : 1,
                renderer : function (value, metadata, record, rowIndex, colIndex, store, view) {
                    if (value == 0) {
                        return "";
                    }
                    else {
                        return value;
                    }
                }
            }, {
                text : i18n.get("label.image"),
                dataIndex : "imageDs",
                name : "image",
                flex : 1,
                renderer : function (value, metadata, record, rowIndex, colIndex, store, view) {
                    var me = view.up("projectGraph");
                    if(me.isNodeADataset(record) && !Ext.isEmpty(value)) {
                        return Ext.String.format('<img class="imageDsNode" src="{0}"/>', value);
                    }                    
                }
            }, {
                text : i18n.get("label.descriptionMini"),
                dataIndex : "readme",
                name : "description",
                flex : 1,
                renderer : function (value, metadata, record, rowIndex, colIndex, store, view) {
                    var me = view.up("projectGraph");
                    if(me.isNodeANode(record) && !Ext.isEmpty(value)) {
                        return Ext.String.format('<img data-qtip="{0}" src="{1}"/>', value, loadUrl.get('APP_URL') + '/common/res/images/icons/description_project_small.png');
                    }
                    
                    if(me.isNodeADataset(record) && !Ext.isEmpty(value)) {
                        var url = record.get("url");
                        return me.getClickDatasetIconString(url, "desc", "help.png");
                    }                    
                }
            }, {
                text : i18n.get("label.data"),
                flex : 1,
                name : "data",
                dataIndex : "datasetId",
                renderer : function (value, metadata, record, rowIndex, colIndex, store, view) {
                    var me = view.up("projectGraph");
                    if(me.isNodeADataset(record) && !Ext.isEmpty(value)) {
                        var url = record.get("url");
                        return me.getClickDatasetIconString(url, "data", "tree_datasets.png");
                    }                    
                }
            }, {
                text : i18n.get("label.definitionMini"),
                flex : 1,
                name : "definition",
                dataIndex : "datasetId",
                renderer : function (value, metadata, record, rowIndex, colIndex, store, view) {
                    var me = view.up("projectGraph");
                    if(me.isNodeADataset(record) && !Ext.isEmpty(value)) {
                        var url = record.get("url");
                        return me.getClickDatasetIconString(url, "defi", "tree_dictionary.png");
                    }                    
                }
            }, {
                text : i18n.get("label.feeds"),
                flex : 1,
                name : "feeds",
                dataIndex : "datasetId",
                renderer : function (value, metadata, record, rowIndex, colIndex, store, view) {
                    var me = view.up("projectGraph");
                    if(me.isNodeADataset(record) && !Ext.isEmpty(value)) {
                        var url = record.get("url");
                        return me.getClickDatasetIconString(url, "feeds", "rss.png");
                    }                    
                }
            }, {
                text : i18n.get("label.opensearchMini"),
                flex : 1,
                name : 'opensearch',
                dataIndex : "datasetId",
                renderer : function (value, metadata, record, rowIndex, colIndex, store, view) {
                    var me = view.up("projectGraph");
                    if(me.isNodeADataset(record) && !Ext.isEmpty(value)) {
                        var url = record.get("url");
                        return me.getClickDatasetIconString(url, "openSearch", "toolbar_open_search.png");
                    }                    
                }
            }],
            defaults : {
                menuDisabled : true,
                sortable : false
            }
        };
	    
	    if(!Ext.isEmpty(columnsConfig)){
	        Ext.each(this.columns.items, function(column) {
	            if (columnsConfig.containsKey(column.name)) {
	                column.hidden = !columnsConfig.get(column.name);
	            }
	        });
	    }
	    
		this.callParent(arguments);
	},
	
	//@private
	isNodeADataset : function (record) {
	    return record.get("type") === "dataset" && record.get("authorized") === "true";
	},
	
	//@private
	isNodeANode : function (record) {
	    return record.get("type") === "node";
	},
	
	getClickDatasetIconString : function (url, type, imgSrc) {
	    var imageUrl = Ext.String.format("{0}/common/res/images/icons/{1}", loadUrl.get('APP_URL'), imgSrc);
	    return Ext.String.format("<a href='#' onClick='sitools.user.utils.DatasetUtils.clickDatasetIcone(\"{0}\", \"{1}\"); return false;'><img src='{2}'/></a>", url, type, imageUrl);
	}
	
});

