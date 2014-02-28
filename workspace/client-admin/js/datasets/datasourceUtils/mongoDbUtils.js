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
/*global Ext, sitools, ID, i18n, SITOOLS_DATE_FORMAT, SITOOLS_DEFAULT_IHM_DATE_FORMAT, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl*/
Ext.namespace('sitools.admin.datasets.datasourceUtils');


/**
 * @class sitools.admin.datasets.DatasourceFactory
 */
//sitools.component.datasets.abstractDatasetWin = {
Ext.define('sitools.admin.datasets.datasourceUtils.mongoDbUtils', { extend : 'Ext.util.Observable', 
	isJdbc : false,
	isMongoDb : true, 
	constructor : function (config) {
		Ext.apply(this, config.scope);
		sitools.admin.datasets.datasourceUtils.jdbcUtils.superclass.constructor.call(this);
	}, 

	logInfo : function () {
		console.log('mongoDb');
	}, 
    /**
     * Get the url of the datasource
     * @return {string}
     */
	getDataSourceUrl : function () {
		var record = this.formulairePrincipal.getDataSourceCombo().getStore().getById(this.formulairePrincipal.getDataSourceCombo().getValue());
		if (!record) {
			return false;
		}
		return record.data.sitoolsAttachementForUsers + "/collections";
	}, 
	
	getFieldsBDDSelected : function (tree) {
		return tree.getSelectionModel().getSelectedNodes();
	}, 
	/**
	 * create an empty Panel
	 * @returns
	 */
	createGridFieldsBDD : function () {
		var metadataPanel = new Ext.Panel();
		return metadataPanel;

	}, 
	
	
	/**
	 * Creates the real object 
	 * @returns
	 */
	loadColumnsBDD : function () {
        var store = this.panelSelectTables.getStoreSelectedTables();

        var dataSourceUrl = this.getDataSourceUrl();
        
        if (!dataSourceUrl) {
            return false;
        }
		
        if (store._getDirty() || store.getModifiedRecords().length > 0) {
            if (store.getCount() > 0) {
                var url = dataSourceUrl + "/" + store.getAt(0).data.name;
                var treePanel = new sitools.admin.datasource.mongoDb.CollectionExplorer({
					collection : {
						name : store.getAt(0).data.name, 
						url : url
					}
				});
                this.panelSelectFields.setFirstGrid(treePanel);
            }
        }
	}, 
	
	
	createGridTablesBDD : function () {
        /**
         * Proxy used to request a datasource
         * @type Ext.data.HttpProxy
         */
        var httpProxyJDBC = new Ext.data.HttpProxy({
            url : loadUrl.get('APP_URL'),
            restful : true,
            method : 'GET'
        });

        /**
         * This store contains all tables of a datasource.
         * @type Ext.data.JsonStore
         */
        var storeTablesMongo = new Ext.data.JsonStore({
            root : "mongodbdatabase.collections",
            datasourceUtils : this, 
            fields : [ {
                name : 'url'
            }, {
                name : 'name'
            }],
            proxy : httpProxyJDBC,
            listeners : {
                beforeload : function () {
                    var dataSourceUrl = this.datasourceUtils.getDataSourceUrl();
                    this.proxy.setUrl(dataSourceUrl);
                    //TODO : changer ça
//                    this.httpProxyColumns.setUrl(dataSourceUrl);
                }
            }
        });

        /**
         * The columnModel of the grid that displays the tables of a datasource.
         * @type Ext.grid.ColumnModel
         */
        var cmTablesMongo = new Ext.grid.ColumnModel({
            columns : [ {
                id : 'name',
                header : i18n.get('headers.name'),
                width : 160,
                sortable : true,
                dataIndex : 'name'
            } ]
        });

        /**
         * The grid that displays the tables of a datasource.
         * @type Ext.grid.ColumnModel
         */
        return new Ext.grid.GridPanel({
            layout : 'fit', 
            store : storeTablesMongo,
            cm : cmTablesMongo,
            selModel : Ext.create('Ext.selection.RowModel',{}),
            enableDragDrop : true,
            stripeRows : true,
            title : 'Tables Mongo',
            id : 'Tables_Mongo'
        });
	}, 
    /**
     * Validate the tables Selection
     * @returns {} an object with boolean success attribute. 
     */
    validateTablesSelection : function (grid) {
	    if (grid.getStore().getCount() > 1) {
			return {
				success : false, 
				message : i18n.get('label.tooManyTablesSelected')
			};
	    }
		return {
			success : true
		};
    }, 
    /**
     * Returns the column Model for Dataset Tables grid
     * @returns {Ext.grid.ColumnModel} columnModel
     */
    getCmTablesDataset : function () {
        return new Ext.grid.ColumnModel({
            columns : [ {
                id : 'name',
                header : i18n.get('headers.name'),
                width : 160,
                sortable : true,
                dataIndex : 'name'
            }]
        });
    }, 
    /**
     * Returns the column Model for Dataset Fields grid
     * @returns {Ext.grid.ColumnModel} columnModel
     */
    getCmFieldsDataset : function () {
        return new Ext.grid.ColumnModel({
	        columns : [{
	            id : 'tableName',
	            header : i18n.get('headers.tableName'),
	            sortable : true,
	            dataIndex : 'tableName'
	        }, {
	            id : 'name',
	            header : i18n.get('headers.name'),
	            sortable : true,
	            dataIndex : 'dataIndex'
	        } ]
	    });
    }, 
    /**
     * Returns an array of possible types for dataset Columns
     * @returns []
     */
    getColumnsType : function () {
    	return [ [ 'DATABASE', i18n.get('label.database') ], [ 'VIRTUAL', i18n.get('label.virtual') ] ];
    }

	


});