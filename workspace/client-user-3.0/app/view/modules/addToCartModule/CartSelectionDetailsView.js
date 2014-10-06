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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse, loadUrl, userLogin*/

Ext.namespace('sitools.user.view.modules.addToCartModule');
/**
 * CartSelectionDetails Module
 * @class sitools.user.modules.cartSelectionDetails
 * @param {Ext.data.Record} selection : the current selection
 * @param {columnModel} columnModel : the column model of the dataset
 * @param {Array} records : the records of the current selection
 * @param {File} cartOrderFile : the all Cart Selection file
 * @param {sitools.user.modules.addToCartModule} cartModule : the cart Module
 * @extends grid.GridPanel
 */
Ext.define('sitools.user.view.modules.addToCartModule.CartSelectionDetailsView', {
    extend : 'Ext.grid.Panel',
    alias : 'widget.cartSelectionDetails',
    
    disableSelection : true,
    initComponent : function () {

        this.selectionId = this.selection.data.selectionId;
        
        this.AppUserStorage = loadUrl.get('APP_USERSTORAGE_USER_URL').replace('{identifier}', userLogin) + "/files";
        this.recordsFileUrl = loadUrl.get('APP_URL') + this.AppUserStorage + "/"
            + DEFAULT_ORDER_FOLDER + "/records/" + userLogin + "_" + this.selectionId + "_records.json";
        
        this.forceFit = (this.columnModel.length > 8) ? false : true,
        this.viewConfig = {
            listeners : {
                scope : this,
                refresh : function (view) {
                    this.getEl().unmask();
                }
            }
        };
       
        var fields = [];
        var columns = [];
        
        Ext.each(this.columnModel, function (item, index, totalItems) {
            if (Ext.isEmpty(item.columnRenderer) ||  ColumnRendererEnum.NO_CLIENT_ACCESS != item.columnRenderer.behavior) {
                var field = {
            		name : item.columnAlias
        		};
                fields.push(field);
                item.dataIndex = item.columnAlias;
                
                item.renderer = function (value, metadata, record, rowIndex, colIndex, store) {
                    if(value.length > 10) {
                        metadata.attr = 'ext:qtip="' + value + '" ext:qwidth="auto"';
                    }
                    return value;
                };
//                columns.push(Ext.create('Ext.grid.column.Column', item));
                columns.push(Ext.create('Ext.grid.column.Column', item));
            }
        });
        
//        this.colModel = new Ext.grid.ColumnModel({
//            columns : columns
//        });
                
        this.colModel = columns;
        
        this.primaryKey = this.getPrimaryKeyFromCm(this.colModel);
        this.sm = null;
        
        this.params = Ext.Object.fromQueryString(this.selections);
        
        var colModel = extColModelToSrv(this.columnModel);
        this.params.colModel = Ext.JSON.encode(colModel);
        
        this.store = Ext.create('Ext.data.JsonStore', {
            proxy : {
            	type : 'ajax',
                method : 'GET',
                url : this.url,
                reader : {
                	type : 'json',
                	root : 'data'
                }
            },
            restful : true,
            fields : fields
            
        });

        this.bbar = {
            xtype : 'pagingtoolbar',
            pageSize : 300,
            store : this.store,
            displayInfo : true,
            displayMsg : i18n.get('paging.display'),
            emptyMsg : i18n.get('paging.empty')
        };
        
        this.callParent(arguments);
    },
    
    onRender : function () {
    	this.callParent(arguments);
        this.loadRecordsFile();
    },
    
    afterRender : function () {
    	this.callParent(arguments);
        this.getEl().mask(i18n.get('label.loadingArticles'));
    },
    
    loadRecordsFile : function () {
    	var params = Ext.apply({
    		start : 0,
            limit : 300
    	}, this.params);
    	
        this.store.load({
            params : params
        });
    },
    
    onRefresh : function () {
//        this.store.removeAll();
//        this.store.loadData(this.records);
//        this.cartModule.fireEvent('onDetail');
        this.cartModule.store.reload();
        this.getEl().unmask();
    },
    
    getPrimaryKeyFromCm : function (colModel) {
        var primaryKey = null;
        Ext.each(colModel.config, function (col) {
            if (col.primaryKey) {
                primaryKey = col.dataIndex;
                return false;
            }
        });
        return primaryKey;
    }
    
});
