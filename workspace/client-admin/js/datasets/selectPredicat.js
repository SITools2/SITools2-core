/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.namespace('sitools.admin.datasets');

/**
 * @cfg string field the attribute of the record to edit
 * @cfg Ext.data.Record recordPredicat the record to edit
 * @cfg Ext.data.Store storePredicat the store that contains the record
 * @cfg Ext.grid.GridView the view of the parent grid 
 * @class sitools.admin.datasets.selectPredicat
 * @extends Ext.Window
 */
sitools.admin.datasets.selectPredicat = Ext.extend(Ext.Window, {
//sitools.component.datasets.selectPredicat = Ext.extend(Ext.Window, {
    width : 700,
    height : 480,
    modal : true,
    pageSize : 10,
    id : 'selectPredicatId',

    initComponent : function () {
        this.title = i18n.get('label.selectPredicat');

        this.cmSelectPredicat = new Ext.grid.ColumnModel({
            columns : [ {
                id : 'tableAlias',
                header : i18n.get('headers.tableAlias'),
                sortable : true,
                dataIndex : 'tableAlias'
            }, {
                id : 'tableName',
                header : i18n.get('headers.tableName'),
                sortable : true,
                dataIndex : 'tableName'
            }, {
                id : 'name',
                header : i18n.get('headers.alias'),
                sortable : true,
                dataIndex : 'columnAlias'
            } ]
        });

        this.smSelectPredicat = new Ext.grid.RowSelectionModel({
            singleSelect : true
        });

        this.gridSelectPredicat = new Ext.grid.GridPanel({
            title : i18n.get('title.gridSelectPredicat'),
            height : 380,
            autoScroll : true,
            store : this.storePredicat,
            cm : this.cmSelectPredicat,
            sm : this.smSelectPredicat
        });

        this.items = [ {
            xtype : 'panel',
            layout : 'fit',
            items : [ this.gridSelectPredicat ],
            buttons : [ {
                text : i18n.get('label.ok'),
                scope : this,
                handler : this.onValidate

            }, {
                text : i18n.get('label.cancel'),
                scope : this,
                handler : function () {
                    this.close();
                }
            } ]

        } ];
        sitools.admin.datasets.selectPredicat.superclass.initComponent.call(this);
    },
    onValidate : function () {
        var rec = this.gridSelectPredicat.getSelectionModel().getSelected();
        var nomAffiche = rec.data.tableAlias ? rec.data.tableAlias : rec.data.tableName;
        nomAffiche = nomAffiche + "." + rec.data.dataIndex;
        this.recordPredicat.data.nomAffiche = nomAffiche;
        if (this.field == 'leftAttribute') {
            this.recordPredicat.data.leftAttribute = {
                tableName : rec.data.tableName,
                tableAlias : rec.data.tableAlias,
                dataIndex : rec.data.dataIndex,
                schema : rec.data.schemaName,
                columnAlias : rec.data.columnAlias,
                specificColumnType : rec.data.specificColumnType, 
                sqlColumnType : rec.data.sqlColumnType, 
                javaSqlColumnType : rec.data.javaSqlColumnType
            };
        } else {
            this.recordPredicat.data.rightAttribute = {
                tableName : rec.data.tableName,
                tableAlias : rec.data.tableAlias,
                dataIndex : rec.data.dataIndex,
                schema : rec.data.schemaName,
                columnAlias : rec.data.columnAlias,
                specificColumnType : rec.data.specificColumnType, 
                sqlColumnType : rec.data.sqlColumnType, 
                javaSqlColumnType : rec.data.javaSqlColumnType
            };
        }
        // this.recordPredicat.data.dataIndex = rec.data.dataIndex;
        // this.recordPredicat.data.schema = rec.data.schema;

        this.viewPredicat.refresh();
        this.close();

    }

});

Ext.reg('s-datasetspredicat', sitools.admin.datasets.selectPredicat);
