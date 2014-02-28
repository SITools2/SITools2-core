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
/*global Ext, sitools, ID, i18n, showResponse, alertFailure*/
/*
 * @include "../../../client-admin/js/datasets/selectPredicat.js"
 */
Ext.ns("sitools.admin.datasets");

/**
 * @class sitools.admin.datasets.PredicatsPanel
 * @extends Ext.grid.EditorGridPanel
 * @param {string} gridId
 * @param {Ext.data.Store} storeSelectFields
 * @param {string} type
 */
sitools.admin.datasets.PredicatsPanel = function (config) {
	Ext.apply(this, config);
	var myData = [];

	// create the data store
	var storeCritere = new Ext.data.JsonStore({
		fields : [ {
		    name : 'parentheseOuvrante',
		    type : 'text'
		}, {
			name : 'leftAttribute'
		}, {
		    name : 'operateur',
		    type : 'text'
		}, {
			name : 'rightAttribute'
		}, {
			name : 'parentheseFermante'
		}, {
			name : 'opLogique'
		} ]
	});

	Ext.util.Format.comboRenderer = function (combo) {
		return function (value) {
			var record = combo.findRecord(combo.valueField, value);
			return record ? record.get(combo.displayField) : combo.valueNotFoundText;
		};
	};

	// Ici on cr�e les combos pour utilisation dans l'Editor Grid
	var comboOl = new Ext.form.ComboBox({
	    id : "comboOlEditor", 
	    typeAhead : false,
	    triggerAction : 'all',
	    lazyRender : true,
	    mode : 'local',
	    store : new Ext.data.ArrayStore({
	        id : 0,
	        fields : [ 'myId', 'displayText' ],
	        data : [ [ '', '' ], [ 'and', 'and' ], [ 'or', 'or' ] ]
	    }),
	    valueField : 'myId',
	    displayField : 'displayText'
	});

	var comboOp = new Ext.form.ComboBox({
	    id : "comboOpEditor", 
	    typeAhead : false,
	    triggerAction : 'all',
	    lazyRender : true,
	    mode : 'local',
	    store : new Ext.data.ArrayStore({
	        id : 0,
	        fields : [ 'myId', 'displayText' ],
	        data : [ [ 'GT', '>' ], [ 'GTE', '>=' ], [ 'LT', '<' ], [ 'LTE', '<=' ], [ 'LIKE', 'like' ],
	                [ 'EQ', '=' ], [ 'NE', '!=' ] ]
	    }),
	    valueField : 'myId',
	    displayField : 'displayText'
	});

	Ext.util.Format.attributeRenderer = function () {
		return function (value) {
			if (value) {
				var tableName = "";
				if (value.tableAlias) {
					tableName = value.tableAlias + ".";
				}
				else {
					if (value.tableName) {
						tableName = value.tableName + ".";
					}
				}
				return tableName + value.dataIndex;
			}
		};
	};

	var rightAttribute;
	if (this.type == "join") {
		rightAttribute = {
		    id : 'rightAttribute',
		    name : "rightAttribute",
		    header : i18n.get('label.rightAttribute'),
		    width : 100,
		    sortable : false,
		    dataIndex : 'rightAttribute',
		    // editor : comboChamp1,
		    renderer : Ext.util.Format.attributeRenderer()
		};
	} else {
		rightAttribute = {
		    header : i18n.get('label.rightAttribute'),
		    width : 200,
		    sortable : false,
		    dataIndex : 'rightAttribute',
		    editor : new Ext.form.TextField({
			    allowBlank : false
		    })
		};
	}
	var tbar = {
        xtype : 'sitools.widget.GridSorterToolbar',
	    defaults : {
		    scope : this
		},
	    items: []
	};

    
	sitools.admin.datasets.PredicatsPanel.superclass.constructor.call(this, Ext.apply({
	    layout : 'fit', 
	    flex : 0.45, 
	    id : this.gridId,
	    resizable : true,
	    autoHeight : false,
	    viewConfig : {
	    	forceFit : true
	    },
	    autoScroll : true,
	    store : storeCritere,
	    enableColumnHide : false,
	    enableColumnMove : false,
	    enableColumnSort : false,
	    clicksToEdit : 2,
	    tbar : tbar,
	    // Definition des colonnes avec monType
	    
	    columns : [ {
	        header : "",
	        width : 50,
	        sortable : false,
	        dataIndex : 'opLogique',
	        editor : comboOl,
	        renderer : Ext.util.Format.comboRenderer(comboOl)

	    }, {
	        header : "",
	        width : 20,
	        sortable : false,
	        dataIndex : 'parentheseOuvrante'
	    }, {
	        id : 'leftAttribute',
	        name : "leftAttribute",
	        header : i18n.get('header.leftAttribute'),
	        width : 100,
	        sortable : false,
	        dataIndex : 'leftAttribute',
	        // editor : comboChamp1,
	        renderer : Ext.util.Format.attributeRenderer()
	    }, {
	        header : i18n.get('header.operateur'),
	        width : 70,
	        sortable : false,
	        dataIndex : 'operateur',
	        editor : comboOp,
	        renderer : Ext.util.Format.comboRenderer(comboOp)

	    }, rightAttribute, {
	        header : "",
	        width : 20,
	        sortable : false,
	        dataIndex : 'parentheseFermante',
	        monType : 'parenthese'
	    } ],
	    // ajout du plugin pour d�finir la drop Zone
	    stripeRows : true,
	    selModel : Ext.create('Ext.selection.RowModel'),
	    // menu contextuel
	    listeners : {
	        // levee du traitement pour les champs de la grille
	        // scope : this,
	        celldblclick : function (grid, rowIndex, columnIndex, e) {
		        var record;
		        var selectPredicatWin;
		        var fieldName;
		        var data;
				if (columnIndex == 1) {
			        record = grid.getStore().getAt(rowIndex); // Get the Record
			        fieldName = grid.getColumnModel().getDataIndex(columnIndex); // Get field name
			        data = record.get(fieldName);
			        record.set(fieldName, data + '(');
		        }
		        if (columnIndex == 2) {
			        record = grid.getStore().getAt(rowIndex); // Get the
																	// Record
			        selectPredicatWin = new sitools.admin.datasets.selectPredicat({
			            field : 'leftAttribute',
			            recordPredicat : record,
			            storePredicat : this.storeSelectFields,
			            viewPredicat : grid.getView()
			        });
			        selectPredicatWin.show(ID.BOX.DATASETS);
		        }
		        if (columnIndex == 4 && this.type == 'join') {
			        record = grid.getStore().getAt(rowIndex); // Get the
																	// Record
			        selectPredicatWin = new sitools.admin.datasets.selectPredicat({
			            field : 'rightAttribute',
			            recordPredicat : record,
			            storePredicat : this.storeSelectFields,
			            viewPredicat : grid.getView()
			        });
			        selectPredicatWin.show(ID.BOX.DATASETS);
		        }
		        if (columnIndex == 5) {
			        record = grid.getStore().getAt(rowIndex); // Get the
																	// Record
			        fieldName = grid.getColumnModel().getDataIndex(columnIndex); // Get
																						// field
																						// name
			        data = record.get(fieldName);
			        record.set(fieldName, data + ')');
		        }
		        grid.getView().refresh();
	        },
	        contextmenu : function (e) {
		        if (!Ext.isEmpty(Ext.getCmp('gridCritereCtxMenu'))) {
		            Ext.getCmp('gridCritereCtxMenu').destroy();
		        }
		        this.contextMenu = new Ext.menu.Menu({
		            id : 'gridCritereCtxMenu',
		            items : [
		                    {
		                        text : i18n.get('label.ajoutCondition'),
		                        icon : loadUrl.get('APP_URL') + "/res/images/icons/add_condition.png",
		                        listeners : {
		                            scope : this,
		                            click : function () {
			                            var rowIndex = 0;
			                            var selModel = this.getSelectionModel();
			                            if (selModel.hasSelection()) {

				                            rowIndex = this.getStore().indexOf(selModel.getSelected()) + 1;

			                            }
			                            var RecordType = this.getStore().recordType;
			                            var p = new RecordType({
			                                parentheseOuvrante : ' ',
			                                parentheseFermante : ' '
			                            });
			                            this.stopEditing();
			                            storeCritere.insert(rowIndex, p);

		                            }
		                        }
		                    },
		                    {
		                        text : i18n.get('label.suppressionCondition'),
		                        icon : loadUrl.get('APP_URL') + "/res/images/icons/delete_condition.png",
		                        listeners : {
		                            scope : this,
		                            click : function () {
			                            if (!this.getSelectionModel().hasSelection()) {
				                            Ext.Msg.alert(i18n.get('warning.noselection'), i18n
				                                    .get('warning.noselection'));
				                            return;
			                            }
			                            var s = this.getSelectionModel().getSelections();
			                            for (var i = 0, r; r = s[i]; i++) {
				                            storeCritere.remove(r);
			                            }

		                            }
		                        }
		                    },
		                    {
		                        text : i18n.get('label.suppressionParenthese'),
		                        icon : loadUrl.get('APP_URL') + "/res/images/icons/delete_parenthesis.png",
		                        listeners : {
		                            scope : this,
		                            click : function () {
			                            var selModel = this.getSelectionModel();
			                            if (!selModel.hasSelection()) {
				                            Ext.Msg.alert(i18n.get('warning.noselection'), i18n
				                                    .get('warning.noselection'));
				                            return;
			                            }

			                            var selectedRecords = selModel.getSelections();

			                            var i = 0;
			                            selModel.each(function (selected) {
				                            selected.data.parentheseOuvrante = " ";
				                            selected.data.parentheseFermante = " ";
			                            });
			                            this.getView().refresh();

		                            }
		                        }
		                    }
		                ]
		        	});
		        var xy = e.getXY();
		        this.contextMenu.showAt(xy);
		        e.stopEvent();
	        }
	    }

	}));

};

Ext.extend(sitools.admin.datasets.PredicatsPanel, Ext.grid.EditorGridPanel, {});

