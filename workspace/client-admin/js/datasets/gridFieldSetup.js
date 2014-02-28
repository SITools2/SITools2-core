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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl, ColumnRendererEnum*/
/*
 * @include "datasetUrlWin.js"
 * @include "unitWin.js"
 */
Ext.namespace('sitools.admin.datasets');

/**
 * @class sitools.admin.datasets.gridFieldSetup
 * @cfg {String} urlDictionary (required) the Url to request directories
 * @cfg {String} action (required) modify, view, or create
 * @cfg {String} urlDataset (required) the url to get the dataset definition 
 */
Ext.define('sitools.admin.datasets.gridFieldSetup', {
    extend : 'Ext.grid.Panel',
    id : 'gridColumnSelect',
    title : i18n.get('title.gridColumn'),
    layout : 'fit', 
    
    initComponent : function () {

        var storeColumn = new Ext.data.JsonStore({
            id : 'storeColumnSelect',
            root : 'ColumnModel',
            idProperty : 'columnAlias',
            remoteSort : false,
            model : 'DatasetModel',
            listeners : {
                add : function (store, records) {
                    Ext.each(records, function (record) {
                        if (record.data.specificColumnType == 'DATABASE') {
                            if (Ext.isEmpty(record.data.header)) {
                                record.data.header = record.data.dataIndex;
                            }
                            if (Ext.isEmpty(record.data.columnAlias)) {
                                record.data.columnAlias = record.data.dataIndex.toLowerCase();
                            }
                            if (sql2ext.get(record.get("sqlColumnType")) == "dateAsString" && Ext.isEmpty(record.data.format)) {
                                record.data.format = SITOOLS_DEFAULT_IHM_DATE_FORMAT;
                            }

                        }
                    });
                }
            }
        });
        
        var visible = new Ext.grid.CheckColumn({
            header : i18n.get('headers.visible'),
            dataIndex : 'visible',
            width : 55,
            helpUrl : loadUrl.get('APP_URL') + "/client-admin/res/help/" + LOCALE + "/dataset/visible.html"
            
        });
        var sortable = new Ext.grid.CheckColumn({
            header : i18n.get('headers.sortable'),
            dataIndex : 'sortable',
            width : 55,
            helpUrl : loadUrl.get('APP_URL') + "/client-admin/res/help/" + LOCALE + "/dataset/sortable.html",
            onMouseDown : function (e, t) {
                if(t.className && t.className.indexOf('x-grid3-cc-'+this.id) != -1){
                    e.stopEvent();
                    var index = this.grid.getView().findRowIndex(t);
                    var record = this.grid.store.getAt(index);
                    if (record.data.specificColumnType != "VIRTUAL") {
                        record.set(this.dataIndex, !record.data[this.dataIndex]);   
                    }
                }
            }
        });
        var primaryKey = new Ext.grid.CheckColumn({
            header : i18n.get('headers.primaryKey'),
            dataIndex : 'primaryKey',
            width : 55,
            helpUrl : loadUrl.get('APP_URL') + "/client-admin/res/help/" + LOCALE + "/dataset/primaryKey.html"
        });
        var comboStoreOrderBy = new Ext.data.ArrayStore({
                fields : [ 'value', 'display' ],
                data : [ [ '', '' ], [ 'ASC', 'ASC' ], [ 'DESC', 'DESC' ] ]
            });
        var comboOrderBy = new Ext.form.ComboBox({
            header : i18n.get('headers.orderBy'),
            store : comboStoreOrderBy, 
            mode : 'local',
            typeAhead : true,
            triggerAction : 'all',
            forceSelection : true,
            selectOnFocus : true,
            dataIndex : 'orderBy',
            lazyRender : true,
            listClass : 'x-combo-list-small',
            valueField : 'value',
            displayField : 'display',
            tpl : '<tpl for="."><div class="x-combo-list-item comboItem">{display}</div></tpl>', 
            width : 55,
            helpUrl : loadUrl.get('APP_URL') + "/client-admin/res/help/" + LOCALE + "/dataset/orderBy.html"
        });

        var comboStore = new Ext.data.ArrayStore({
            fields : [ 'value', 'display', 'tooltip' ],
            data : [ [ '', '' ],
                    [ 'Image', 'Image', i18n.get("label.image.tooltip") ], 
                    [ 'URL', 'URL', i18n.get("label.url.tooltip") ],
                    [ 'DataSetLink', 'DataSetLink', i18n.get("label.datasetlink.tooltip") ],
                    [ 'Other', 'Other', i18n.get("label.other.tooltip") ]]                
        });

        var comboColumnRenderer = new Ext.form.ComboBox({
            disabled : this.action == 'view' ? true : false, 
            store : comboStore,
            mode : 'local',
            typeAhead : true,
            triggerAction : 'all',
            forceSelection : true,
            selectOnFocus : true,
            data : 'light',
            lazyRender : true,
            listClass : 'x-combo-list-small',
            valueField : 'value',
            displayField : 'display',
            tpl : '<tpl for="."><div ext:qtip="{tooltip}" class="x-combo-list-item comboItem">{display}</div></tpl>',
            listeners : {
                scope : this, 
                select : function (combo, record) {
                    var columnRendererType = combo.getValue();
                    //get the last value, which is either the last value selected or the first value.
                    var lastValue = (Ext.isEmpty(combo.lastValue)) ? combo.startValue   : combo.lastValue;
                    var selectedRecord = this.getSelectionModel().getSelected();
                    if (!Ext.isEmpty(columnRendererType)) {                
                        var colWindow = new sitools.admin.datasets.columnRendererWin({
                            selectedRecord : selectedRecord, 
                            gridView : this.getView(),
                            columnRendererType : columnRendererType,
                            datasetColumnStore : storeColumn,
                            lastColumnRendererType : lastValue
                        });
                        colWindow.show();
                    } 
                }
            }
        });

        var filterColumn = new Ext.grid.CheckColumn({
            header : i18n.get('headers.filter'),
            dataIndex : 'filter',
            width : 55,
            helpUrl : loadUrl.get('APP_URL') + "/client-admin/res/help/" + LOCALE + "/dataset/filters.html",
            onMouseDown : function (e, t) {
                if(t.className && t.className.indexOf('x-grid3-cc-'+this.id) != -1){
                    e.stopEvent();
                    var index = this.grid.getView().findRowIndex(t);
                    var record = this.grid.store.getAt(index);
                    if (record.data.specificColumnType != "VIRTUAL") {
                        record.set(this.dataIndex, !record.data[this.dataIndex]);   
                    }
                }
            }
        });

        var cmColumn = new Ext.ux.grid.LockingColumnModel({
            columns : [ {
                header : i18n.get('headers.sqlDefinition'),
                dataIndex : 'dataIndex',
                width : 120,
                locked : true,
                helpUrl : loadUrl.get('APP_URL') + "/client-admin/res/help/" + LOCALE + "/dataset/sqlDefinition.html"
            }, {
                header : i18n.get('headers.tableName'),
                dataIndex : 'tableName',
                width : 80
            }, {
                header : i18n.get('headers.tableAlias'),
                dataIndex : 'tableAlias',
                width : 80
            }, {
                header : i18n.get('headers.columnAlias'),
                dataIndex : 'columnAlias',
                width : 80,
                editor : new Ext.form.TextField({
                    disabled : this.action == 'view' ? true : false, 
                    allowBlank : false,
                    maxLength : 50, 
                    validator : function (v) {
                        var re = new RegExp("^.*[!\"#$%&\'/()*+,:;<=>?@\\`{}|~]+.*$");
                        if (!re.test(v)) {
                            return !re.test(v);
                        }
                        else {
                            return i18n.get('label.invalidColumnAlias');
                        }
                        
                    }
                }),
                helpUrl : loadUrl.get('APP_URL') + "/client-admin/res/help/" + LOCALE + "/dataset/columnAlias.html"
            }, {
                header : i18n.get('headers.format'),
                dataIndex : 'format',
                width : 80,
                editor : new Ext.form.TextField({
                    disabled : this.action == 'view' ? true : false, 
                    allowBlank : true,
                    maxLength : 50, 
                    value : "Y-m-d\\TH:i:s.u"
                }),
                helpUrl : loadUrl.get('APP_URL') + "/client-admin/res/help/" + LOCALE + "/dataset/format.html"
            }, {
                header : i18n.get('headers.unit'),
                dataIndex : 'unit',
                width : 80,
                helpUrl : loadUrl.get('APP_URL') + "client-admin/res/help/" + LOCALE + "/dataset/unit.html", 
                renderer : function (value) {
                    if (Ext.isEmpty(value)) {
                        return "";
                    }
                    else {
                        return value.label; 
                    }
                }
            }, {
                header : i18n.get('headers.header'),
                dataIndex : 'header',
                width : 80,
                editor : new Ext.form.TextField({
                    disabled : this.action == 'view' ? true : false, 
                    allowBlank : false,
                    maxLength : 50
                }),
                helpUrl : loadUrl.get('APP_URL') + "client-admin/res/help/" + LOCALE + "/dataset/headers.html"
            }, {
                header : i18n.get('headers.width'),
                dataIndex : 'width',
                width : 40,
                editor : new Ext.form.TextField({
                    disabled : this.action == 'view' ? true : false, 
                    allowBlank : false,
                    maxLength : 50
                })
            }, sortable, visible, filterColumn, {
                header : i18n.get('headers.orderBy'),
                dataIndex : 'orderBy',
                width : 55,
                editor : comboOrderBy,
                helpUrl : loadUrl.get('APP_URL') + "/client-admin/res/help/" + LOCALE + "/dataset/orderBy.html"
            }, primaryKey, {
                header : i18n.get('headers.previewUrl'),
                dataIndex : 'columnRendererCategory',
                width : 120,
                editor : comboColumnRenderer,
                helpUrl : loadUrl.get('APP_URL') + "/client-admin/res/help/" + LOCALE + "/dataset/previewUrl.html"
            },
            {
                header : i18n.get('headers.tooltip'),
                dataIndex : 'toolTip',
                width : 80,
                helpUrl : loadUrl.get('APP_URL') + "client-admin/res/help/" + LOCALE + "/dataset/tooltip.html",
                editor : new Ext.form.TextField({
                    disabled : this.action == 'view' ? true : false, 
                    maxLength : 50
                })
            }],
            defaults : {
                sortable : false,
                width : 80
            }
        });

        var smColumn = Ext.create('Ext.selection.RowModel',{
            singleSelect : true
        });

        var menuActions = new Ext.menu.Menu({
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.assignUnit'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/refresh_clue.png',
                handler : this.onAssignUnit
            }, {
                text : i18n.get('label.deleteUnit'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/refresh_clue.png',
                handler : this.onDeleteUnit
            }]
        });
        var tbar = {
            xtype : 'sitools.widget.GridSorterToolbar',
            defaults : {
                scope : this
            },
            items : [ {
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
                handler : this.onCreateColumn
            }, {
                text : i18n.get('label.modify'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_edit.png',
                handler : this.onModifyColumn
            }, {
                text : i18n.get('label.delete'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
                handler : this.onDeleteColumn
            }, {
                text : i18n.get('label.action'), 
                menu : menuActions
            }]
        };

        this.store = storeColumn;
        this.tbar = tbar;
        this.columns = cmColumn;
        this.selModel = smColumn;
//        this.plugins = [ sortable, visible, filterColumn, primaryKey ];
        
        this.listeners = {
            scope : this, 
            beforeedit : function (e) {
                //Créer l'éditeur en fonction du type 
                if (e.column == 4) {
                    var grid = e.grid;
                    var rec = e.record;
                    if (sql2ext.get(rec.get("sqlColumnType")) != "dateAsString") {
                        return false;
                    }
                }
                return true;
            }, 
            activate : function (p) {
                this.datasourceUtils = this.scope.datasourceUtils;
            }
        };
        
        sitools.admin.datasets.gridFieldSetup.superclass.initComponent.call(this);
    },
    
    /**
     * Open a sitools.admin.datasets.unitWin to assign a unit to the selected column.
     * @method
     */
    onAssignUnit : function () {
        var grid = this;
        var rec = grid.getSelectionModel().getSelected();
        if (!rec) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
            return;
        }

        var unitWin = new sitools.admin.datasets.unitWin({
            recordColumn : rec,
            viewColumn : grid.getView(),
            urlDimension : this.urlDimension
        });
        unitWin.show(ID.BOX.DATASETS);
    },
    /**
     * Open a {@link sitools.admin.datasets.columnsPropPanel sitools.admin.datasets.columnsPropPanel} to create a new Column
     * @method
     */
    onCreateColumn : function () {
        var winPropColumn = new sitools.admin.datasets.columnsPropPanel({
            action : 'create',
            store : this.getStore(), 
            datasourceUtils : this.datasourceUtils
        });
        winPropColumn.show();
        // this.getStore().add(new Ext.data.Record());
    },
    /**
     * Open a {@link sitools.admin.datasets.columnsPropPanel sitools.admin.datasets.columnsPropPanel} to edit a Column
     * @method
     */
    onModifyColumn : function () {
        var rec = this.getSelectionModel().getSelected();
        if (!rec) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
            return;
        }
        
        if (rec.get('specificColumnType') === "DATABASE" && this.datasourceUtils.isJdbc) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.cannotUpdateDatabaseColumn'));
            return;
        }
        
        var winPropColumn = new sitools.admin.datasets.columnsPropPanel({
            action : 'modify',
            store : this.getStore(),
            recordColumn : rec, 
            datasourceUtils : this.datasourceUtils
        });
        winPropColumn.show();
        // this.getStore().add(new Ext.data.Record());
    },
    /**
     * delete a column.
     * @method
     */
    onDeleteColumn : function () {
        var grid = this;
        var rec = grid.getSelectionModel().getSelected();
        if (!rec) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
            return;
        }
        grid.getStore().remove(rec);
        grid.getView().refresh();

    },
    /**
     * Remove a unit from a selected Column.
     * @method
     */
    onDeleteUnit : function () {
        var grid = this;
        var rec = grid.getSelectionModel().getSelected();
        if (!rec) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
            return;
        }
        // clear the notion
        rec.set("unit", null);
        rec.set("dimensionId", null);
    }, 
    /**
     * Remove all records and adds records to the store with a columnModel.
     * @param Ext.grid.ColumnModel columnModel
     */
    getData : function (columnModel) {
        var i;
        this.getStore().removeAll();
        for (i = 0; i < columnModel.length; i++) {
            var name;
            
            var columnRenderer = columnModel[i].columnRenderer;
            var columnRendererCategory = "";
            if (!Ext.isEmpty(columnRenderer)) {
                columnRendererCategory = ColumnRendererEnum
                                .getColumnRendererCategoryFromBehavior(columnRenderer.behavior);   
            }
            
            this.getStore().add({
                id : columnModel[i].id,
                dataIndex : columnModel[i].dataIndex,
                header : columnModel[i].header,
                toolTip : columnModel[i].toolTip,
                width : columnModel[i].width,
                sortable : columnModel[i].sortable,
                orderBy : columnModel[i].orderBy, 
                visible : columnModel[i].visible,
                filter : columnModel[i].filter,
                sqlColumnType : columnModel[i].sqlColumnType,
                columnOrder : columnModel[i].columnOrder,
                primaryKey : columnModel[i].primaryKey,
                columnRenderer : columnRenderer,
                columnRendererCategory : columnRendererCategory,                
                schemaName : columnModel[i].schema,
                tableName : columnModel[i].tableName,
                tableAlias : columnModel[i].tableAlias,
                specificColumnType : columnModel[i].specificColumnType,
                columnAlias : columnModel[i].columnAlias, 
                javaSqlColumnType : columnModel[i].javaSqlColumnType, 
                columnClass : columnModel[i].columnClass,
                format : columnModel[i].format, 
                dimensionId : columnModel[i].dimensionId, 
                unit : columnModel[i].unit
            });

        }
    }, 
    
    /**
     * validate the fourth tab of the window.
     * @return an object with attributes : 
     *  - success : boolean
     *  - message : a message if success == false
     */
    gridFieldSetupValidation : function () {
        var result = {
            success : true, 
            message : ""
        };
        var store = this.getStore();
        var nbPrimaryKey = 0;
        var nbOrderBy = 0;
        
        if (store.getCount() > 0) {
            var i;
            for (i = 0; i < store.getCount(); i++) {
                var rec = store.getAt(i).data;
                if (rec.columnAlias.toLowerCase() != rec.columnAlias) {
                    result = {
                        success : false, 
                        message : String.format(i18n.get('label.columnAliasMaj'), rec.columnAlias)
                    };
                    return result;
                }
                if (!Ext.isEmpty(rec.primaryKey) && rec.primaryKey) {
                    nbPrimaryKey++;
                    if (Ext.isEmpty(rec.filter) || !rec.filter) {
                        result = {
                            success : false, 
                            message : String.format(i18n.get('label.columnPKNoFilter'), rec.columnAlias)
                        };
                        return result;
                    }
                }
                if (!Ext.isEmpty(rec.orderBy) && rec.orderBy) {
                    nbOrderBy++;
                }
                
            }
            if (nbPrimaryKey != 1) {
                result = {
                    success : false, 
                    message : nbPrimaryKey + i18n.get('label.wrongPrimaryKey')
                };
                return result;
            }
            if (nbOrderBy < 1) {
                result = {
                    success : false, 
                    message : i18n.get('label.noOrderBy')
                };
                return result;
            }
        }
        var chekForDoublon = this.checkForDoublon();
        if (chekForDoublon.found) {
            result = {
                success : false, 
                message : i18n.get('label.doublonFound') + chekForDoublon.doublon
            };
        }
        return result;
    }, 
    /**
     * Check the uniqueness of the ColumnAlias 
     * @returns {} an object with attributes
     *  - found : Boolean true if at least one duplicate
     *  - doublon : the name of the first duplicate columnAlias found
     * 
     */
    checkForDoublon : function () {
        var doublon = null, i;
        var store = this.getStore();
        var found = false;
        for (i = 0; i < store.getCount() && !found; i++) {
            var rec1 = store.getAt(i).data;
            found = false;
            for (var j = 0; j < store.getCount() && doublon === null && !found; j++) {
                var rec2 = store.getAt(j).data;
                if (rec1.columnAlias == rec2.columnAlias && rec1 != rec2) {
                    found = true;
                    doublon = rec1.columnAlias;
                }
            }
        }
        return {
            found : found, 
            doublon : doublon
        };
    }
});

//Ext.extend(sitools.admin.datasets.gridFieldSetup, Ext.ux.grid.LockingEditorGridPanel, {
//    
//});
