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
	    
	    

	    this.columns = {
            items : [ {
                xtype : 'treecolumn', 
                width : 200,
                dataIndex : 'text'
            }, {
                text : i18n.get("label.records"),
                dataIndex : "nbRecord",
                width : 100,
            }, {
                text : i18n.get("label.image"),
                dataIndex : "imageDs",
                width : 100,
                renderer : function (value, metadata, record, rowIndex, colIndex, store, view) {
                    var me = view.up("projectGraph");
                    if(me.isNodeADataset(record) && !Ext.isEmpty(value)) {
                        return Ext.String.format('<img class="imageDsNode" src="{0}"></img>', value);
                    }                    
                }
            }, {
                text : i18n.get("label.descriptionMini"),
                dataIndex : "readme",
                width : 90,
                renderer : function (value, metadata, record, rowIndex, colIndex, store, view) {
                    var me = view.up("projectGraph");
                    if(me.isNodeANode(record) && !Ext.isEmpty(value)) {
                        return Ext.String.format('<img data-qtip="{0}" src="{1}"/>', value, loadUrl.get('APP_URL') + '/common/res/images/icons/description_project_small.png');
                    }
                    
                    if(me.isNodeADataset(record) && !Ext.isEmpty(value)) {
                        var url = record.get("url");
                        return Ext.String.format("<a href='#' onClick='sitools.user.utils.DatasetUtils.clickDatasetIcone(\"{0}\", \"desc\"); return false;'><img src='{1}'></img></a>", url, loadUrl.get('APP_URL') + "/common/res/images/icons/help.png");
                    }                    
                }
            }],
            defaults : {
                menuDisabled : true
            }
        };
	    
		this.callParent(arguments);
	},
	
	//@private
	isNodeADataset : function (record) {
	    return record.get("type") === "dataset" && record.get("authorized") === "true";
	},
	
	//@private
	isNodeANode : function (record) {
	    return record.get("type") === "node";
	}
	
});

