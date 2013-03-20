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
 * @deprecated
 * @class sitools.admin.datasets.dictionaryWin
 * @extends Ext.Window
 */
//sitools.component.datasets.dictionaryWin = Ext.extend(Ext.Window, {
sitools.admin.datasets.dictionaryWin = Ext.extend(Ext.Window, {
    width : 700,
    height : 480,
    modal : true,
    pageSize : 10,

    initComponent : function () {
        this.title = i18n.get('label.dictionary');

        this.httpProxy = new Ext.data.HttpProxy({
            url : this.urlDictionary,
            restful : true,
            method : 'GET'
        });
        this.storeNotion = new Ext.data.JsonStore({
            id : 'storeNotionSelect',
            root : 'dictionary.notions',
            idProperty : 'id',
            proxy : this.httpProxy,
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'unit',
                type : 'string'
            }, {
                name : 'description',
                type : 'string'
            }, {
                name : 'type',
                type : 'string'
            }, {
                name : 'dictionaryId',
                type : 'string'
            } ]
        });

        this.cmNotion = new Ext.grid.ColumnModel({
            columns : [ {
                header : i18n.get('headers.id'),
                dataIndex : 'id',
                width : 100
            }, {
                header : i18n.get('headers.name'),
                dataIndex : 'name',
                width : 100
            }, {
                header : i18n.get('headers.unit'),
                dataIndex : 'unit',
                width : 100
            }, {
                header : i18n.get('headers.type'),
                dataIndex : 'type',
                width : 50
            }, {
                header : i18n.get('headers.description'),
                dataIndex : 'description',
                width : 100
            } ],
            defaults : {
                sortable : true,
                width : 100
            }
        });

        this.smNotion = new Ext.grid.RowSelectionModel({
            singleSelect : true
        });

        this.gridNotion = new Ext.grid.EditorGridPanel({
            id : 'gridNotionSelect',
            title : i18n.get('title.gridNotion'),
            layout : 'fit',
            height : 350,
            autoScroll : true,
            store : this.storeNotion,
            cm : this.cmNotion,
            sm : this.smNotion
        });

        var storeDictionary = new Ext.data.JsonStore({
            fields : [ 'id', 'name' ],
            url : this.urlDictionary,
            root : "data",
            autoLoad : true
        });
        this.comboDictionary = new Ext.form.ComboBox({
            store : storeDictionary,
            displayField : 'name',
            valueField : 'id',
            typeAhead : true,
            mode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            emptyText : 'Select a dictionary...',
            selectOnFocus : true,
            listeners : {
                scope : this,
                select : function (combo, rec, index) {
                    this.loadNotions(rec.data.id);
                }

            }
        });

        this.items = [ {
            xtype : 'panel',
            height : 450,
            title : i18n.get('label.dictionarySelect'),
            items : [ this.comboDictionary, this.gridNotion ],
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
        sitools.admin.datasets.dictionaryWin.superclass.initComponent.call(this);
    },
    loadNotions : function (dictionaryId) {
        // alert (dictionaryId);
        this.httpProxy.setUrl(this.urlDictionary + dictionaryId);
        this.gridNotion.getStore().load({
            callback : function () {
                this.gridNotion.getView().refresh();
            },
            scope : this
        });
    },
    onValidate : function () {
        var rec = this.gridNotion.getSelectionModel().getSelected();
        this.recordColumn.data.notion = rec.data;
        this.recordColumn.data.notion.url = this.urlDictionary + this.comboDictionary.getValue() + "/notions/" + rec.data.id;
        //this.recordColumn.data.notionDescription = rec.data.description;
        this.viewColumn.refresh();
        this.close();

    }

});

Ext.reg('s-datasetsdictionary', sitools.admin.datasets.dictionaryWin);
