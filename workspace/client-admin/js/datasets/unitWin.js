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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp*/
Ext.namespace('sitools.admin.datasets');

/**
 * @cfg Ext.data.Record recordColumn The record to edit
 * @cfg Ext.grid.View viewColumn The grid view to refresh
 * @cfg string urlDimension The Url of the dimension to get all the units. 
 * @class sitools.admin.datasets.unitWin
 * @extends Ext.Window
 */
Ext.define('sitools.admin.datasets.unitWin', { extend : 'Ext.Window',
	alias : 'widget.s-datasetsUnits',
    width : 600,
    height : 400,
    modal : true,
    pageSize : 10,

    initComponent : function () {
        this.title = i18n.get('label.units');

        this.httpProxy = new Ext.data.HttpProxy({
            url : this.urlDimension,
            restful : true,
            method : 'GET'
        });
        this.storeUnits = new Ext.data.JsonStore({
            id : 'storeUnitSelect',
            root : 'dimension.sitoolsUnits',
            idProperty : 'id',
            proxy : this.httpProxy,
            fields : [ {
                name : 'label',
                type : 'string'
            }, {
                name : 'unitName',
                type : 'string'
            }]
        });

        this.cmUnits = new Ext.grid.ColumnModel({
            columns : [ {
                header : i18n.get('headers.label'),
                dataIndex : 'label',
                width : 100
            }],
            defaults : {
                sortable : true,
                width : 100
            }
        });

        this.smUnits = Ext.create('Ext.selection.RowModel',{
            singleSelect : true
        });

        this.gridUnits = new Ext.grid.GridPanel({
            id : 'gridUnitId',
            title : i18n.get('title.unitList'),
            autoScroll : true,
            store : this.storeUnits,
            cm : this.cmUnits,
            selModel : this.smUnits, 
            region : "center"
        });

        var storeDimensions = new Ext.data.JsonStore({
            fields : [ 'id', 'name', 'description' ],
            url : this.urlDimension,
            root : "data",
            autoLoad : true
        });
        this.cmDimensions = new Ext.grid.ColumnModel({
            columns : [ {
                header : i18n.get('headers.name'),
                dataIndex : 'name',
                width : 100
            }, {
                header : i18n.get('headers.description'),
                dataIndex : 'description',
                width : 100
            }],
            defaults : {
                sortable : true,
                width : 100
            }
        });

        this.smDimensions = Ext.create('Ext.selection.RowModel',{
            singleSelect : true
        });

        this.gridDimensions = new Ext.grid.GridPanel({
            id : 'gridViewDimensionsId',
            title : i18n.get('title.DimensionsList'),
            region : "west",
            autoScroll : true,
            store : storeDimensions,
            cm : this.cmDimensions,
            selModel : this.smDimensions, 
            width : 200, 
            collapsible : true, 
            resizable : true, 
            listeners : {
				scope : this, 
				rowclick : function (grid, rowIndex) {
					var rec = grid.getStore().getAt(rowIndex);
					var dimensionId = rec.get('id');
					this.loadUnits(dimensionId);
				}
            }
        });
		this.layout = "border";
        this.items = [this.gridUnits, this.gridDimensions ];
        this.buttons = [ {
            text : i18n.get('label.ok'),
            scope : this,
            handler : this.onValidate

        }, {
            text : i18n.get('label.cancel'),
            scope : this,
            handler : function () {
                this.close();
            }
        } ];
        sitools.admin.datasets.unitWin.superclass.initComponent.call(this);
    },
    loadUnits : function (dimensionId) {
        // alert (dictionaryId);
        this.httpProxy.setUrl(this.urlDimension + "/" + dimensionId);
        this.storeUnits.load({
            callback : function () {
                this.gridUnits.getView().refresh();
            },
            scope : this
        });

    },
    onValidate : function () {
        var recUnit = this.gridUnits.getSelectionModel().getSelected();
        if (Ext.isEmpty(recUnit)) {
			Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.noSelection'));
			return;
        }
        var recDimension = this.gridDimensions.getSelectionModel().getSelected();
        
        this.recordColumn.data.unit = recUnit.data;
        this.recordColumn.data.dimensionId = recDimension.data.id;
        //this.recordColumn.data.notionDescription = rec.data.description;
        this.viewColumn.refresh();
        this.close();

    }

});

