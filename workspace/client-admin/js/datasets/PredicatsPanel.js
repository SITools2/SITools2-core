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
Ext.define('sitools.admin.datasets.PredicatsPanel', {
    extend : 'Ext.grid.Panel',
    layout : 'fit', 
    flex : 0.80,
//    resizable : true,
    autoHeight : false,
    forceFit : true,
    autoScroll : true,
    enableColumnHide : false,
    enableColumnMove : false,
    enableColumnSort : false,
    stripeRows : true,
    mixins : {
        utils : 'sitools.admin.utils.utils'
    },
    
    requires : ['sitools.admin.datasets.selectPredicat'],
    
    
    initComponent : function () {
        this.selModel = Ext.create('Ext.selection.RowModel');
        
        id : this.gridId;

        this.store = Ext.create('Ext.data.JsonStore', {
            proxy : {
                type : 'memory'
            },
            fields : [{
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
            }]
        });

        Ext.util.Format.comboRenderer = function (combo) {
            return function (value) {
                var record = combo.findRecord(combo.valueField, value);
                return record ? record.get(combo.displayField) : combo.valueNotFoundText;
            };
        };

        // Ici on créé les combos pour utilisation dans l'Editor Grid
        var comboOl = {
            xtype : 'combobox',
            id : "comboOlEditor", 
            typeAhead : false,
            triggerAction : 'all',
            queryMode : 'local',
            store : Ext.create('Ext.data.ArrayStore', {
//                proxy : {
//                    type : 'memory'
//                },
//                id : 0,
                fields : [ 'myId', 'displayText' ],
                data : [ [ '', '' ], [ 'and', 'and' ], [ 'or', 'or' ] ]
            }),
            valueField : 'myId',
            displayField : 'displayText'
        };

        var comboOp = {
            xtype : 'combobox',
            id : "comboOpEditor", 
            typeAhead : false,
            triggerAction : 'all',
            queryMode : 'local',
            store : Ext.create('Ext.data.ArrayStore', {
//                id : 0,
                fields : [ 'myId', 'displayText' ],
                data : [ [ 'GT', '>' ], [ 'GTE', '>=' ], [ 'LT', '<' ], [ 'LTE', '<=' ], [ 'LIKE', 'like' ],
                        [ 'EQ', '=' ], [ 'NE', '!=' ] ]
            }),
            valueField : 'myId',
            displayField : 'displayText'
        };

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
                editor : {
                    xtype : 'textfield',
                    allowBlank : false
                }
            };
        }
        
        this.columns = [{
            text : "",
            width : 50,
            sortable : false,
            dataIndex : 'opLogique',
            editor : comboOl,
//            renderer : Ext.util.Format.comboRenderer(comboOl)

        }, {
            text : "",
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
            text : i18n.get('header.operateur'),
            width : 70,
            sortable : false,
            dataIndex : 'operateur',
            editor : comboOp,
//            renderer : Ext.util.Format.comboRenderer(comboOp)

        }, rightAttribute, {
            text : "",
            width : 20,
            sortable : false,
            dataIndex : 'parentheseFermante',
            monType : 'parenthese'
        }];
        
        // menu contextuel
        this.listeners = {
            // levee du traitement pour les champs de la grille
            // scope : this,
            celldblclick : function (view, td, columnIndex, record, tr, rowIndex, e) {
                var selectPredicatWin;
                var fieldName;
                var data;
                if (columnIndex == 1) {
                    data = record.get('parentheseOuvrante');
                    record.set('parentheseOuvrante', data + '(');
                }
                if (columnIndex == 2) {

                    selectPredicatWin = Ext.create("sitools.admin.datasets.selectPredicat", {
                        field : 'leftAttribute',
                        recordPredicat : record,
                        storePredicat : this.storeSelectFields,
                        viewPredicat : view
                    });
                    selectPredicatWin.show(ID.BOX.DATASETS);
                }
                if (columnIndex == 4 && this.type == 'join') {

                    selectPredicatWin =  Ext.create("sitools.admin.datasets.selectPredicat", {
                        field : 'rightAttribute',
                        recordPredicat : record,
                        storePredicat : this.storeSelectFields,
                        viewPredicat : view
                    });
                    selectPredicatWin.show(ID.BOX.DATASETS);
                }
                if (columnIndex == 5) {
                    data = record.get('parentheseFermante');
                    record.set('parentheseFermante', data + ')');
                }
                view.refresh();
            }
        };
        
        this.tbar = {
            xtype : 'sitools.public.widget.grid.GridSorterToolbar',
            defaults : {
                scope : this
            },
            items: [{
                text : i18n.get('label.ajoutCondition'),
                icon : loadUrl.get('APP_URL') + "/res/images/icons/add_condition.png",
                listeners : {
                    scope : this,
                    click : function () {
                        var rowIndex = 0;
                        var selModel = this.getSelectionModel();
                        if (selModel.hasSelection()) {
                            rowIndex = this.getStore().indexOf(this.getLastSelectedRecord()) + 1;
                        }
                        
//                        var RecordType = this.getStore().recordType;
//                        var p = new RecordType({
//                            parentheseOuvrante : ' ',
//                            parentheseFermante : ' '
//                        });
//                        this.stopEditing();
                        
                        this.store.insert(rowIndex, {});

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
                        var recs = this.getSelectionModel().getSelection();
                        this.store.remove(recs);

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
                            Ext.Msg.alert(i18n.get('warning.noselection'), i18n.get('warning.noselection'));
                            return;
                        }

                        var selectedRecords = selModel.getSelection();

                        Ext.each(selectedRecords, function (record) {
                            record.set('parentheseOuvrante', " ");
                            record.set('parentheseFermante', " ");
                        });
                        
                        this.getView().refresh();
                    }
                }
            }]
        };
        
        this.plugins = [Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        })];
        
        this.callParent(arguments);
        
    }
	
});


