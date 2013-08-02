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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl, extColModelToStorage*/

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
                    this.selectionColumns = config.value.split(',');
                    break;
                }
            }
        }, this);

        this.title = "Selection Order for : " + this.dataview.datasetName;

        this.items = [ {
            xtype : 'form',
            padding : '5px 5px 5px 5px',
            items : [ {
                xtype : 'textfield',
                fieldLabel : i18n.get('label.selectionName'),
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
        this._addSelection(this.dataview.getSelections(), this.dataview, this.datasetId);
    },

    /**
     * Create an entry in the user storage with all the selected records.
     * @param {Array} selections An array of selected {Ext.data.Record} records 
     * @param {Ext.grid.GridPanel} grid the grid  
     * @param {string} datasetId
     * @param {string} orderName the name of the future file.
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
        var orderRecord = {};
        orderRecord.records = [];

        var colModelTmp = extColModelToStorage(grid.getColumnModel());
        var colModel = [];

        Ext.each(colModelTmp, function (col) {
            Ext.each(this.selectionColumns, function (selectCol) {
                if (col.dataIndex == selectCol) {
                    colModel.push(col);
                }
            }, this);
        }, this);

        Ext.each(selections, function (rec) {
            var data = {};
            Ext.each(colModel, function (column) {
                if (!column.hidden || column.primaryKey) {
                    data[column.columnAlias] = rec.get(column.columnAlias);
                }
            });
            orderRecord.records.push(data);
        });
        orderRecord.colModel = colModel;

        orderRecord.selectionName = this.selectionName;
        orderRecord.datasetId = this.dataview.datasetId;
        orderRecord.projectId = this.dataview.projectId;
        orderRecord.dataUrl = this.dataview.dataUrl;
        orderRecord.datasetName = this.dataview.datasetName;
        orderRecord.nbRecords = orderRecord.records.length;

        var now = new Date();
        var orderDate = new Date(now.getFullYear(), now.getMonth(), now.getDate(), now.getHours(), now.getMinutes(), now.getSeconds());
        orderDate = orderDate.format(SITOOLS_DEFAULT_IHM_DATE_FORMAT);
        //        var orderDate = date.getMonth() + "/" + date.getDate() + "/" + date.getFullYear();
        orderRecord.orderDate = orderDate;

        if (this.cartSelectionFile) {
            this.cartSelectionFile.cartSelections.push(orderRecord);
            Ext.apply(putObject, this.cartSelectionFile);
        } else {
            putObject.cartSelections = [];
            putObject.cartSelections.push(orderRecord);
        }

        userStorage.set(this.user + "_CartSelections.json", "/" + DEFAULT_ORDER_FOLDER + "/records", putObject);
    }
});
Ext.reg('sitools.user.component.dataviews.services.addToCartService', sitools.user.component.dataviews.services.addToCartService);

sitools.user.component.dataviews.services.addToCartService.getParameters = function () {
    return [ {
        jsObj : "Ext.grid.GridPanel",
        config : {
            fieldLabel : i18n.get('label.exportcolumns'),
            height : 300,
            store : new Ext.data.JsonStore({
                fields : [ 'dataIndex' ],
                idProperty : 'dataIndex',
                url : Ext.getCmp("dsFieldParametersPanel").urlDataset,
                root : "dataset.columnModel",
                autoLoad : true
            }),
            listeners : {
                render : function (c) {
                    Ext.QuickTips.register({
                        target : c,
                        text : i18n.get('label.sizeLimitWidthTooltip')
                    });
                }
            },
            view : new Ext.grid.GridView({
                forceFit : true,
                listeners : {
                    refresh : function (view) {
                        if (!Ext.isEmpty(view.grid.value)) {
                            var recordsToSelect = [];
                            var arrayOfColumns = view.grid.value.split(',');
                            
                            Ext.each(arrayOfColumns, function (column, index) {
                                var rec = this.store.getById(column);
                                recordsToSelect.push(rec);
                            }, view.grid);
                            
                            view.grid.selModel.selectRecords(recordsToSelect);
                        }
                    }
                }
            }),
            colModel : new Ext.grid.ColumnModel({
                columns : [ {
                    header : i18n.get('label.selectColumns'),
                    dataIndex : 'dataIndex',
                    sortable : true
                } ]
            }),
            selModel : new Ext.grid.CheckboxSelectionModel({
                handleMouseDown : function (g, rowIndex, e) {
                    var view = this.grid.getView();
                    var isSelected = this.isSelected(rowIndex);
                    if (isSelected) {
                        this.deselectRow(rowIndex);
                    } else if (!isSelected || this.getCount() > 1) {
                        this.selectRow(rowIndex, true);
                        view.focusRow(rowIndex);
                    }
                },
                singleSelect : false
            }),
            getValue : function () {
                var concatvalue = '';
                Ext.each(this.getSelectionModel().getSelections(), function (object, index) {
                    if (Ext.isEmpty(concatvalue))
                        concatvalue = object.data.dataIndex;
                    else
                        concatvalue = concatvalue + ',' + object.data.dataIndex;
                });
                return concatvalue;
            },
            setValue : function (value) {
                this.value = value;
            },
            name : "exportcolumns",
            value : ""
        }
    } ];
};
/**
 * @static
 * Implementation of the method executeAsService to be able to launch this window as a service.
 * @param {Object} config contains all the service configuration 
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