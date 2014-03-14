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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp*/
Ext.namespace('sitools.admin.datasource.mongoDb');

/**
 * Open A window with a status bar, and test database. Displays the result with a status bar. 
 * @cfg {} collection An object representing the collection mongo DB
 * @cfg {} node The ExtJs Node containing metadata.  
 * @class sitools.admin.datasource.mongoDb.RecordsPanel
 * @extends Ext.grid.GridPanel
 */
sitools.admin.datasource.mongoDb.RecordsPanel = Ext.extend(Ext.grid.GridPanel, {
    initComponent : function () {
		var tmp = this.metadatas2ExtObject(this.node) || {};
		
		var cm = tmp.columnModel || new Ext.grid.ColumnModel({columns : []});;
		
		var store = tmp.store;
		store.load({
			params : {
				start : 0,
				limit : 300
			}
		});
		sitools.admin.datasource.mongoDb.RecordsPanel.superclass.initComponent.call(Ext.apply(this, {
			cm : cm, 
			store : store, 
			loadMask : true, 
			title : i18n.get('label.records'), 
			bbar: new Ext.PagingToolbar({
		        store: store,       // grid and PagingToolbar using same store
		        displayInfo: true,
		        pageSize: 300,
		        prependButtons: true
		    }), 
		    listeners : {
				scope : this, 
				render : function (grid) {
					grid.getEl().mask(i18n.get("label.loading"), "ext-el-mask-msg x-mask-loading");
				}
			}
		}));
		
    }, 
    /**
     * Build Store & columnModel according to the metadatas.
     * @param {Ext.tree.TreeNode} metadatas the treeNode containing metadatas
     * @returns {} An object with 2 attributes : store, columnModel to build the grid.
     */
    metadatas2ExtObject : function (metadatas) {
		if (Ext.isEmpty(metadatas.childNodes)) {
			return null;
		}
		var columns = [], fields = [], column, field;
		Ext.each(metadatas.childNodes, function (firstLevelItem) {
			column = !Ext.isEmpty(firstLevelItem.leaf) && firstLevelItem.leaf ? this.metadata2Column(firstLevelItem) : this.metadata2ActionColumn(firstLevelItem);
			field = {
				name : firstLevelItem.text
			};
			fields.push(field);
			columns.push(column);
		}, this);
		return {
			columnModel : new Ext.grid.ColumnModel({
				columns : columns, 
				defaults : [{
					width : 100
				}]
			}), 
			store : new Ext.data.JsonStore({
				fields : fields, 
				url : this.collection.url + "/records", 
				autoLoad : false, 
				restful : true, 
				root : "data", 
				totalProperty : "total", 
				listeners : {
					scope : this, 
					load : function (store) {
						if (this.getEl()) {
							this.getEl().unmask();
						}
					}
				}
			})
		};
		
		
    },
    /**
     * Transform a TreeNode with children into an ActionColumn 
     * @param {Ext.tree.TreeNode} metadata the metadata 
     * @returns {Ext.grid.ActionColumn} the action column 
     */
    metadata2ActionColumn : function (metadata) {
		var column = new Ext.grid.Column({
			scope : this, 
//			icon : "/sitools/common/res/images/icons/tree_databases.png", 
			header : metadata.text, 
			dataIndex : metadata.text, 
			width : 70, 
			processEvent : function (name, evt, grid, rowIndex, colIndex) {
				if (Ext.getCmp("jsonDetailWin")) {
					Ext.getCmp("jsonDetailWin").destroy();
				}
				var column = grid.getColumnModel().columns[colIndex];
				var value = grid.getStore().getAt(rowIndex).get(column.dataIndex);
				grid.getSelectionModel().selectRow(rowIndex);
				var win = new Ext.Window({
					id : "jsonDetailWin", 
					title : i18n.get('label.detail') + " " + column.header + " object", 
					modal : true, 
					autoScroll : true, 
					html : String.format("<pre>{0}</pre>", sitools.common.utils.Utils.syntaxHighlight(JSON.stringify(value, undefined, 4))), 
					height : 400, 
					x : evt.getXY()[0], 
					y : evt.getXY()[1]
				});
				win.show();
				win.toFront();
		    }, 
		    renderer : function (value, metaData, record, rowIndex, colIndex, store) {
		    	var i = 0;
				for (var attr in value) {
					i++;
				}
				return "<div style='float: left; width:50px;'>[" + i + " " + i18n.get("label.key") + "] " + "</div><div style='width:18px;'><img src=/sitools/common/res/images/icons/detailJson.png></div>";
		    }
		});
		return column;
    },
    /**
     * Transform a TreeNode without children into an Column 
     * @param {Ext.tree.TreeNode} metadata the metadata 
     * @returns {Ext.grid.Column} the column 
     */
    metadata2Column : function (metadata) {
		var column = new Ext.grid.Column({
			header : metadata.text, 
			dataIndex : metadata.text
		});
		// Verrue pour traiter l'identifiant MongoDB :
		// le champ _id est retourné sous la forme d'une string ou d'un objet Mongo {"$oid" : String Id}
		if (metadata.text === "_id") {
			Ext.apply(column, {
				renderer : function (value) {
					if (Ext.isString(value)) {
						return value;
					}
					if (Ext.isObject(value) && value.$oid) {
						return value.$oid;
					}
					return value;
				}
			});
		}
		return column;
    }
});
