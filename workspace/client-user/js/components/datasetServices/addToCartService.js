/*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE,ImageChooser, loadUrl, extColModelToStorage*/

Ext.namespace('sitools.user.component.dataviews.services');

/**
 * GUI Service to download a data selection from the dataset
 * 
 * @required datasetId
 * @required datasetUrl
 * @cfg {Ext.data.JsonStore} the store where nodes are saved
 * @class sitools.user.component.dataviews.services.addToCartService
 * @extends Ext.Window
 */
sitools.user.component.dataviews.services.addToCartService = Ext.extend(Ext.Window, {
    width : 300,
    modal : true,
    initComponent : function () {

        (Ext.isEmpty(userLogin)) ? this.user = "public" : this.user = userLogin;

        Ext.each(this.parameters, function (config) {
            if (!Ext.isEmpty(config.value)) {
                switch (config.name) {
                case "exportcolumns":
                    this.selectionColumns = Ext.util.JSON.decode(config.value);
                    break;
                }
            }
        }, this);

        this.title = i18n.get('label.orderForDataset') + this.dataview.datasetName;

        this.items = [ {
            xtype : 'form',
            padding : '5px 5px 5px 5px',
            items : [ {
                xtype : 'textfield',
                fieldLabel : i18n.get('label.orderName'),
                name : 'selectionName',
                anchor : '90%'
            } ]
        } ];

        this.buttons = [ {
            text : i18n.get('label.ok'),
            scope : this,
            handler : function () {
                this.addToCart();
                this.close();
            }
        }, {
            text : i18n.get('label.cancel'),
            scope : this,
            handler : function () {
                this.close();
            }
        } ];

        sitools.user.component.dataviews.services.addToCartService.superclass.initComponent.call(this);
    },

    getCartSelectionFile : function (response) {
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

    addToCart : function () {
        this.selectionName = this.findByType('form')[0].getForm().getFieldValues().selectionName;

        userStorage.get(this.user + "_CartSelections.json", "/" + DEFAULT_ORDER_FOLDER + "/records", this, this.getCartSelectionFile, Ext.emptyFn,
                this.saveSelection);
    },

    saveSelection : function () {
        // if (this.dataview.getSelections().length <= 300 &&
        // this.dataview.selModel.getAllSelections().length <= 300) {
        this._addSelection(this.dataview.getSelections(), this.dataview, this.datasetId);
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
    _addSelection : function (selections, grid, datasetId) {
        var primaryKey = "";
        var rec = selections[0];
        Ext.each(rec.fields.items, function (field) {
            if (field.primaryKey) {
                primaryKey = field.name;
            }
        }, rec);
        if (Ext.isEmpty(primaryKey) || Ext.isEmpty(rec.get(primaryKey))) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noPrimaryKey'));
            return;
        }

        var putObject = {};
        var globalOrder = {};
        var recordsOrder = {};
        recordsOrder.records = [];

        var colModelTmp = extColModelToStorage(grid.getColumnModel());
        var colModel = [];

        // On stocke seulement les colonnes configurées dans le Gui service
        Ext.each(colModelTmp, function (col) {
            Ext.each(this.selectionColumns, function (selectCol) {
                if (col.dataIndex == selectCol || col.dataIndexSitools == selectCol) {
                    colModel.push(col);
                }
            }, this);
        }, this);

        globalOrder.selectionName = this.selectionName;
        globalOrder.selectionId = Ext.id();
        globalOrder.datasetId = this.dataview.datasetId;
        globalOrder.projectId = this.dataview.projectId;
        globalOrder.dataUrl = this.dataview.dataUrl;
        globalOrder.datasetName = this.dataview.datasetName;

        var orderDate = new Date();
        var orderDateStr = orderDate.format(SITOOLS_DATE_FORMAT);
        globalOrder.orderDate = orderDateStr;

        Ext.each(selections, function (rec) {
            var data = {};
            Ext.each(colModel, function (column) {
                if (!column.hidden || column.primaryKey) {
                    data[column.columnAlias] = rec.get(column.columnAlias);
                }
            });
            recordsOrder.records.push(data);
        });

        globalOrder.nbRecords = recordsOrder.records.length;
        recordsOrder.selectionId = globalOrder.selectionId;

        globalOrder.colModel = colModel;

        if (this.cartSelectionFile) {
            this.cartSelectionFile.cartSelections.push(globalOrder);
            Ext.apply(putObject, this.cartSelectionFile);
        } else {
            putObject.cartSelections = [];
            putObject.cartSelections.push(globalOrder);
        }

        userStorage.set(this.user + "_CartSelections.json", "/" + DEFAULT_ORDER_FOLDER + "/records", putObject, this.createRecordsSelectionFile(recordsOrder));
    },

    createRecordsSelectionFile : function (recordsOrder) {
        userStorage.set(this.user + "_" + recordsOrder.selectionId + "_records.json", "/" + DEFAULT_ORDER_FOLDER + "/records", recordsOrder);
    }

});
Ext.reg('sitools.user.component.dataviews.services.addToCartService', sitools.user.component.dataviews.services.addToCartService);

sitools.user.component.dataviews.services.addToCartService.getParameters = function () {
    var checkColumn = new Ext.grid.CheckColumn({
        header : i18n.get('headers.exportData'),
        dataIndex : 'isDataExported',
        editable : true,
        width : 50,
        renderer : function(v, p, record) {
            p.css += ' x-grid3-check-col-td'; 
            var cmp = Ext.getCmp(this.id); 
            if (cmp.enabled &&
                    ColumnRendererEnum.getColumnRendererCategoryFromBehavior(record.data.columnRenderer.behavior) == "Image") {
            	return String.format('<div id="{2}" class="x-grid3-check-col{0} x-grid3-check-col-enabled {1}">&#160;</div>', v ? '-on' : '', cmp.createId(), this.id);
            }
            else {
            	return String.format('<div id="{2}" class="x-grid3-check-disabled-col{0} {1}">&#160;</div>', v ? '-on' : '', cmp.createId(), this.id);
            }
        }
    });
    return [ {
        jsObj : "sitools.component.datasets.selectItems",
        config : {
            height : 250,
            layout : {
                type : 'hbox',
                pack : 'start',
                align : 'stretch'
            },
            grid1 : new Ext.grid.GridPanel({
                flex : 1,
                margins : {
                    top : 0,
                    right : 5,
                    bottom : 0,
                    left : 0
                },
                viewConfig : {
                    forceFit : true
                },
                store : new Ext.data.JsonStore({
                    fields : [ 'dataIndex', 'columnAlias', 'primaryKey', 'columnRenderer' ],
                    idProperty : 'columnAlias',
                    url : Ext.getCmp("dsFieldParametersPanel").urlDataset,
                    root : "dataset.columnModel",
                    autoLoad : true
                }),
                colModel : new Ext.grid.ColumnModel({
                    columns : [ {
                        header : i18n.get('label.selectColumns'),
                        dataIndex : 'columnAlias',
                        sortable : true
                    } ]
                })
            }),
            grid2 : new Ext.grid.GridPanel({
                flex : 1,
                margins : {
                    top : 0,
                    right : 0,
                    bottom : 0,
                    left : 10
                },
                viewConfig : {
                    forceFit : true,
                },
                store : new Ext.data.JsonStore({
                    fields : [ 'dataIndex', 'columnAlias', 'primaryKey', 'columnRenderer', 'isDataExported' ],
                    idProperty : 'dataIndex',
                    listeners : {
                        add : function (store, records) {
                            Ext.each(records, function (rec) {
                                if (rec.data.columnRenderer) {
                                    if (rec.data.isDataExported && ColumnRendererEnum.getColumnRendererCategoryFromBehavior(rec.data.columnRenderer.behavior) == "Image") {
                                        rec.data.isDataExported = true;
                                    }
                                    else {
                                        rec.data.isDataExported = false;
                                    }
                                }
                            });
                        }
                    }
                }),
                colModel : new Ext.grid.ColumnModel({
                    columns : [ {
                        header : i18n.get('label.exportColumns'),
                        dataIndex : 'columnAlias',
                        sortable : true
                    }, checkColumn ]
                }),
                plugins : [ checkColumn ]
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
                var columnsString = Ext.util.JSON.encode(columnsToExport);
                return columnsString;
            },
            setValue : function (value) {
                var columnsToExport = Ext.util.JSON.decode(value);
                Ext.each(columnsToExport, function (column) {
                    var recordColumn = new Ext.data.Record(column);
                    this.grid2.getStore().add(recordColumn);
                }, this);
                this.value = value;
            }
        }
    } ];
};
/**
 * @static Implementation of the method executeAsService to be able to launch
 *         this window as a service.
 * @param {Object}
 *            config contains all the service configuration
 */
sitools.user.component.dataviews.services.addToCartService.executeAsService = function (config) {
    var selections = config.dataview.getSelections();
    var rec = selections[0];
    if (Ext.isEmpty(rec)) {
        Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noRecordsSelected'));
        return;
    }
    var addSelectionService = new sitools.user.component.dataviews.services.addToCartService(config);
    addSelectionService.show();
};