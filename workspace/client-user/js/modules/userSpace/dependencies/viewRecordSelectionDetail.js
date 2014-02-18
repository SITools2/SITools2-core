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
/*global Ext, sitools, i18n, DEFAULT_DATE_FORMAT, sql2ext, loadUrl, getColumnRenderer */
/*
 * @include "../../../components/livegrid/dependencies/contextMenu.js"
 */
Ext.ns("sitools.user.modules.userSpaceDependencies");

/**
 * 
 * @class sitools.user.modules.userSpaceDependencies.viewRecordSelectionDetail
 * @extends Ext.Window
 * @requires sitools.user.component.dataviews.services.serverServicesUtil
 */
sitools.user.modules.userSpaceDependencies.viewRecordSelectionDetail = Ext.extend(Ext.Window, {
    modal : true,
    title : "test",
    height: 400, 
    width : 600, 
    layout : 'fit', 
	datasetId : "",
	projectId : "",
	
    initComponent : function () {
        this.datasetId = this.orderRecord.datasetId;
        this.projectId = this.orderRecord.projectId;
        this.datasetName = this.orderRecord.datasetName;
        var fields = this.getFields(this.orderRecord.colModel);
        
        this.store = new Ext.data.JsonStore({
            fields : fields, 
            data : this.orderRecord.records
        });
        
        var sm = Ext.create('Ext.selection.RowModel',);
        
        this.grid = new Ext.grid.GridPanel({
            layout : 'fit', 
            autoScroll : true, 
            store : this.store,
            cm : getColumnModel(this.orderRecord.colModel),
            sm : sm
        });
        Ext.apply(this.store, {
			getFormParams : function () {
				return null;
			}, 
			dataUrl : this.orderRecord.dataUrl
        });
        this.items = [this.grid];
        this.dataviewUtils = sitools.user.component.dataviews.dataviewUtils;
		sitools.user.modules.userSpaceDependencies.viewRecordSelectionDetail.superclass.initComponent.call(this);

    }, 
    getColumnModel : function (listeColonnes) {
        var columns = [];
        var i = 0;
        if (!Ext.isEmpty(listeColonnes)) {
            Ext.each(listeColonnes, function (item, index, totalItems) {
                if (!Ext.isEmpty(item.notion) && !Ext.isEmpty(item.notion.description)) {
                    item.tooltip = item.notion.description.replace('"', "''");
                }
                var renderer = getColumnRenderer(item);
                var hidden;
                if (Ext.isEmpty(item.visible)) {
                    hidden = item.hidden;
                } else {
                    hidden = !item.visible;
                }
                columns[i] = new Ext.grid.Column({
                    columnAlias : item.columnAlias,
                    dataIndexSitools : item.dataIndex,
                    dataIndex : item.columnAlias,
                    header : item.header,
                    width : item.width,
                    sortable : item.sortable,
                    hidden : hidden,
                    tooltip : item.tooltip,
                    renderer : renderer,
                    schema : item.schema,
                    tableName : item.tableName,
                    tableAlias : item.tableAlias,
                    id : item.id,
                    // urlColumn : item.urlColumn,
                    primaryKey : item.primaryKey,
                    notion : item.notion,
                    previewColumn : item.previewColumn,
                    filter : item.filter,
                    sqlColumnType : item.sqlColumnType, 
                    columnAliasDetail : item.columnAliasDetail,
					columnRenderer : item.columnRenderer, 
					datasetDetailId : item.datasetDetailId, 
					specificColumnType : item.specificColumnType,
                    image : item.image
                });
                i++;
            }, this);
        }

        var cm = new Ext.grid.ColumnModel({
            columns : columns
        });
        return cm;
    }, 
    getFields : function (listeColonnes) {
        /**
         * Used to convert date from Database to Ext Date Object and then return it with the format DEFAULT_DATE_FORMAT
         */
        function dateToExt(v, record) {
			//try to build Date with "Y-m-d g:i:s" format
			var result = Date.parseDate(v, SITOOLS_DATE_FORMAT, true);
			
			if (Ext.isEmpty(result)) {
				return i18n.get("label.invalidDate");
			}
			return result;
//			return result.format(DEFAULT_DATE_FORMAT);
        }
        var fields = [];
        var i = 0;
        if (!Ext.isEmpty(listeColonnes)) {
            Ext.each(listeColonnes, function (item, index, totalItems) {
                fields[i] = new Ext.data.Field({
                    name : item.columnAlias,
                    primaryKey : item.primaryKey,
                    type : sql2ext.get(item.sqlColumnType)
                });
                //Apply the convert function on Date Fields
                if (sql2ext.get(item.sqlColumnType) == 'date') {
					Ext.apply(fields[i], {
						convert : dateToExt
					});
                }
                i++;

            }, this);
        }
        return fields;
    },  
    getSelections : function () {
		return this.grid.getSelectionModel().getSelections();
    }, 
    getRequestParam : function () {
        var request = "", formParams = {};
                // First case : no records selected: build the Query
        // cas de l'affichage d'une sélection utilisateur
        var recSelected;
        if (Ext.isEmpty(this.getSelections())) {
            recSelected = this.grid.getStore().data.items;
        } else {
            recSelected = this.grid.getSelectionModel().getSelections();
        }
	
        formParams = this.dataviewUtils.getFormParamsFromRecsSelected(recSelected);
        // use the form API to request the selected records
        request += "&" + Ext.urlEncode(formParams);

        return request;
    }
});
