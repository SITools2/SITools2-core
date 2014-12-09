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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl, extColModelToStorage, SitoolsDesk, sql2ext
 */

Ext.namespace('sitools.user.component.datasets.services');

/**
 * A specific checkColumn that is displayed only the good featureType is
 * available
 */
Ext.define('sitools.user.component.datasets.services.AddToCartCustomCheckColumn', {
    extend : 'Ext.grid.column.CheckColumn',
    alias : 'widget.addToCartServiceCheckColumn',
    renderer : function (value, meta, rec) {
        var columnRenderer = rec.get("columnRenderer");
        var featureType = ColumnRendererEnum.getColumnRendererCategoryFromBehavior(columnRenderer.behavior);
        if (featureType === "Image" || featureType == "URL") {
            return this.callParent(arguments);
        } else {
            return "";
        }
    }
});


/**
 * GUI Service to download a data selection from the dataset
 * 
 * @required datasetId
 * @required datasetUrl
 * @cfg {Ext.data.JsonStore} the store where nodes are saved
 * @class sitools.user.component.dataviews.services.addToCartService
 * @extends Ext.Window
 */
Ext.define('sitools.user.component.datasets.services.AddToCartService', {
    extend : 'sitools.user.core.Component',
//    controllers : ['sitools.user.controller.component.datasets.services.AddToCartServiceController'],
    alias : 'sitools.user.component.dataviews.services.addToCartService',
    requires : ['sitools.public.widget.datasets.columnRenderer.behaviorEnum'],
    
    statics : {
        getParameters : function () {
            return [
                    {
                        jsObj : "Ext.form.Label",
                        config : {
                            name : "descriptionLabel",
                            listeners : {
                                render : function (label) {
                                    label.setText(label.value);
                                }
                            },
                            getValue : function () {
                                return this.value;
                            },
                            setValue : function (value) {
                                this.setText(value);
                            },
                            value : "Select columns you want to export in the cart module. All columns with a Feature Type can be downloadable (download the real data and not the link) by checking 'Export Data' column."

                        }
                    },
                    {
                        jsObj : "sitools.public.widget.datasets.selectItems",
                        config : {
                            height : 250,
                            padding : 10,
                            layout : {
                                type : 'hbox',
                                pack : 'start',
                                align : 'stretch',
                                defaultMargins : {
                                    top : 10,
                                    right : 0,
                                    bottom : 0,
                                    left : 0
                                }
                            },
                            grid1 : Ext.create("Ext.grid.GridPanel", {
                                border : false,
                                bodyBorder : false,
                                flex : 1,
                                forceFit : true,
                                selModel : Ext.create('Ext.selection.RowModel', {
                                    mode : 'MULTI'
                                }),
                                store : Ext.create("Ext.data.JsonStore", {
                                    type : 'json',
                                    fields : [ 'dataIndex', 'columnAlias', 'primaryKey', 'columnRenderer', 'featureType' ],
                                    autoLoad : true,
                                    proxy : {
                                        type : 'ajax',
                                        url : Ext.getCmp("dsFieldParametersPanel").urlDataset,
                                        reader : {
                                            type : 'json',
                                            idProperty : 'columnAlias',
                                            root : "dataset.columnModel"
                                        }
                                    },
                                    listeners : {
                                        load : function (store, records) {
                                            Ext.each(records, function (rec) {
                                                var columnRenderer = rec.get("columnRenderer");
                                                if (!Ext.isEmpty(columnRenderer)) {
                                                    rec.set("featureType", ColumnRendererEnum
                                                            .getColumnRendererCategoryFromBehavior(columnRenderer.behavior));
                                                }
                                            });
                                        }
                                    }
                                }),
                                columns : [ {
                                    header : i18n.get('label.selectColumns'),
                                    dataIndex : 'columnAlias',
                                    sortable : true
                                }, {
                                    header : i18n.get('headers.previewUrl'),
                                    dataIndex : 'featureType',
                                    sortable : true
                                } ]
                            }),
                            grid2 : Ext.create("Ext.grid.GridPanel", {
                                border : false,
                                bodyBorder : false,
                                flex : 1,
                                forceFit : true,
                                selModel : Ext.create('Ext.selection.RowModel', {
                                    mode : 'MULTI'
                                }),
                                store : Ext.create("Ext.data.JsonStore", {
                                    fields : [ 'dataIndex', 'columnAlias', 'primaryKey', 'columnRenderer', 'isDataExported' ],
                                    idProperty : 'columnAlias',
                                    proxy : {
                                        type : 'memory'
                                    },
                                    listeners : {
                                        add : function (store, records) {
                                            Ext.each(records, function (rec) {
                                                var columnRenderer = rec.get("columnRenderer");
                                                if (!Ext.isEmpty(columnRenderer)) {
                                                    var featureType = ColumnRendererEnum
                                                            .getColumnRendererCategoryFromBehavior(columnRenderer.behavior);
                                                    if (rec.data.isDataExported && (featureType === "Image" || featureType === "URL")) {
                                                        rec.data.isDataExported = true;
                                                    } else {
                                                        rec.data.isDataExported = false;
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }),
                                columns : [ {
                                    header : i18n.get('label.exportColumns'),
                                    dataIndex : 'columnAlias',
                                    sortable : true
                                }, {
                                    xtype : 'addToCartServiceCheckColumn',
                                    header : i18n.get('headers.exportData'),
                                    tooltip : i18n.get('headers.helpExportData'),
                                    dataIndex : 'isDataExported',
                                    editable : true,
                                    width : 50
                                } ]
                            }),
                            name : "exportcolumns",
                            value : [],
                            getValue : function () {
                                var columnsToExport = [];
                                var store = this.grid2.getStore();

                                store.each(function (record) {
                                    var rec = {};
                                    rec.columnAlias = record.data.columnAlias;
                                    rec.isDataExported = record.data.isDataExported;
                                    rec.columnRenderer = record.data.columnRenderer;
                                    columnsToExport.push(rec);

                                });
                                var columnsString = Ext.JSON.encode(columnsToExport);
                                return columnsString;
                            },
                            setValue : function (value) {
                                var columnsToExport = Ext.JSON.decode(value);
                                Ext.each(columnsToExport, function (column) {
                                    this.grid2.getStore().add(column);
                                }, this);
                                this.value = value;
                            }
                        }
                    } ];
        }
    },
    
    init : function (config) {
    	var selections = config.dataview.getSelections();

        if (Ext.isEmpty(userLogin)) {
            return popupMessage({
                title : i18n.get('label.info'),
                html : i18n.get('label.needToBeLogged'),
                buttons : Ext.MessageBox.OK,
                icon : Ext.MessageBox.INFO
            });
        }
        Ext.apply(this, config);

        this.cartSelectionFile = [];

        (Ext.isEmpty(userLogin)) ? this.user = "public" : this.user = userLogin;

        var rec = selections[0];
        if (Ext.isEmpty(rec)) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noRecordsSelected'));
            return;
        }

        Ext.each(this.parameters, function (config) {
            if (!Ext.isEmpty(config.value)) {
                switch (config.name) {
                case "exportcolumns":
                    this.selectionColumns = Ext.JSON.decode(config.value);
                    break;
                }
            }
        }, this);

        this.selectionName = config.dataview.dataset.name;
        this.config = config;
        this.addToCart();
    },
    

    addToCart : function () {

        var projectName = Ext.getStore('ProjectStore').getProject().get("name");

        UserStorage.get(this.user + "_CartSelections.json", getCartFolder(projectName), this, this.getCartSelectionFile,
                this.checkCartSelectionFile, this.saveSelection);
    },
    
    getCartSelectionFile : function(response) {

		if (Ext.isEmpty(response.responseText)) {
			return;
		}
		try {
			var json = Ext.decode(response.responseText);
			this.cartSelectionFile = json;
		} catch (err) {
			return;
		}
	},

	checkCartSelectionFile : function() {
		if (Ext.isEmpty(this.cartSelectionFile)) {
			this.cartSelectionFile = {};
		}
	},


	saveSelection : function() {
		// if (this.dataview.getSelections().length <= 300 &&
		// this.dataview.selModel.getAllSelections().length <= 300) {
		this._addSelection(this.dataview.getSelections(), this.dataview,
				this.datasetId);
		// } else {
		// return Ext.Msg.show({
		// title : i18n.get('label.warning'),
		// msg : i18n.get('warning.tooMuchRecords'),
		// buttons : Ext.MessageBox.OK,
		// icon : Ext.MessageBox.WARNING
		// });
		// }
	},

	/**
	 * Create an entry in the user storage with all the selected records.
	 * 
	 * @param {Array}
	 *            selections An array of selected {Ext.data.Record} records
	 * @param {Ext.grid.GridPanel}
	 *            grid the grid
	 * @param {string}
	 *            datasetId
	 * @param {string}
	 *            orderName the name of the future file.
	 */
	_addSelection : function(selections, grid, datasetId) {
		var primaryKey = grid.component.getPrimaryKey();
        var rec = selections[0];
        if(Ext.isNumber(rec)){
            rec = grid.store.getAt(selections[0]);
        }
		if (Ext.isEmpty(primaryKey) || Ext.isEmpty(rec.get(primaryKey))) {
			Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noPrimaryKey'));
			return;
		}

		var project = Ext.getStore('ProjectStore').getProject();
		var cartFolder = getCartFolder(project.get("name"));

		var colModelTmp = extColModelToJsonColModel(this.config.columnModel);
		var colModel = [];
		var dataToExport = [];

		// Stores only column configured in the Gui service
		Ext.each(this.selectionColumns, function(selectCol) {
			if (selectCol.isDataExported) {
				dataToExport.push(selectCol.columnAlias);
			}
			Ext.each(colModelTmp, function(col) {
				if (col.columnAlias == selectCol.columnAlias) {
					var newcol = {
						columnAlias : col.columnAlias,
						header : col.header
					};
					colModel.push(newcol);
				}
			}, this);
		}, this);

		var dataset = this.config.dataview.dataset;
		
		var orderDate = new Date();
		var orderDateStr = Ext.Date.format(orderDate, SITOOLS_DATE_FORMAT);
		
		globalOrder = {
				selectionName : this.selectionName,
				selectionId : dataset.name,
				datasetId : dataset.id,
				projectId : project.get("id"),
				dataUrl : dataset.sitoolsAttachementForUsers,
				datasetName : dataset.name,
				selections : this.dataview.getRequestUrlWithoutColumnModel(),
				ranges : Ext.JSON.encode(this.dataview.getSelectionsRange()),
				dataToExport : dataToExport,
				nbRecords : this.dataview.getNbRowsSelected(),
				orderDate : orderDateStr,
				colModel : colModel
		};
		
		var sortersAndFilters = this.createSortersAndFilters(this.dataview);
		
		Ext.apply(globalOrder, sortersAndFilters);

		var index = "";
		if (Ext.isEmpty(this.cartSelectionFile.selections)) {
			//If no selections has been defined create a new array to add the current one
			this.cartSelectionFile.selections = [];
		} else {
			// Try to find an existing selection for the current one 
			index = Ext.each(this.cartSelectionFile.selections, function(sel) {
				if (sel.selectionId === dataset.name) {
					return false;
				}
			}, this);
		}

		if (Ext.isEmpty(index) || !Ext.isNumber(index)) {
			this.cartSelectionFile.selections.push(globalOrder);
			var putObject = {};
			putObject.selections = [];
			putObject.selections = this.cartSelectionFile.selections;

			UserStorage.set(this.user + "_CartSelections.json",	cartFolder, putObject);
		} else {
			Ext.Msg.show({
				title : i18n.get('label.warning'),
				buttons : Ext.Msg.YESNO,
				icon : Ext.MessageBox.WARNING,
				msg : i18n.get('warning.selectionAlreadyExist'),
				scope : this,
				fn : function(btn, text) {
					if (btn == 'yes') {
						this.cartSelectionFile.selections[index] = globalOrder;
						var putObject = {};
						putObject.selections = [];
						putObject.selections = this.cartSelectionFile.selections;

						UserStorage.set(this.user + "_CartSelections.json",
								cartFolder,
								putObject, this.closeDataviewIfModify, this);
					}
				}
			});
		}
	},

	/**
	 * Get filters, sort, formParams to the current order selection
	 * 
	 * @param {Object}
	 *            order
	 * @param {Ext.grid.GridPanel}
	 *            grid The current grid.
	 * 
	 */
	createSortersAndFilters : function(grid) {
		var order = {};
		// Grid filters
		var gridFilters = grid.getRequestGridFilterParams();
		if (!Ext.isEmpty(gridFilters)) {
			order.gridFilters = gridFilters.filter;
			order.gridFiltersCfg = grid.store.getGridFiltersCfg();
		}
		
		//Sorters
		var storeSort = grid.getSortParams();
		if (!Ext.isEmpty(storeSort)) {
			order.sortInfo = storeSort.sort;
		}

		// Form filters
		var formFilters = grid.getRequestFormFilters();
		if (!Ext.isEmpty(formFilters)) {
			order.formFilters = formFilters;
		}

		// Form concept filters
		var formConceptFilters = grid.getRequestFormConceptFilters();
		if (!Ext.isEmpty(formConceptFilters)) {
			order.formConceptFilters = formConceptFilters;
		}

        return order;
	},

	closeDataviewIfModify : function() {
		if (this.dataview.isModifySelection) {
			this.dataview.up("component[specificType=componentWindow]").close();

			var cartModules = Ext.ComponentQuery.query("addToCartModule");
			if (!Ext.isEmpty(cartModules)) {
				cartModules[0].fireEvent("refresh");
			}
			
		}
	}
});