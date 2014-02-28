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
Ext.define('sitools.admin.datasets.datasourceUtils.jdbcUtils',  { extend: 'Ext.util.Observable', 
	isJdbc : true,
	isMongoDb : false, 
	constructor : function (config) {
		Ext.apply(this, config.scope);
		sitools.admin.datasets.datasourceUtils.jdbcUtils.superclass.constructor.call(this);
	}, 
	
	logInfo : function () {
		console.log('jdbc');
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
		return record.data.sitoolsAttachementForUsers;
	}, 
	
	getFieldsBDDSelected : function (grid) {
		return grid.getSelectionModel().getSelections();
	}, 
	
	createGridFieldsBDD : function () {
		/**
         * The proxy used to get the description of a JDBC table
         * @type Ext.data.HttpProxy
         */
        var httpProxyFields = new Ext.data.HttpProxy({
            url : "/",
            restful : true,
            method : 'GET'
        });

        var storeFields = new Ext.data.JsonStore({
            proxy : httpProxyFields,
            datasourceUtils : this, 
            root : "table.attributes",
            autoLoad : false,
            fields : [ {
                name : 'name'
            }, {
                name : 'tableName'
            }, {
                name : 'schemaName'
            }, {
                name : 'structure'
            }, {
                name : 'nomAffiche'
            }, {
                name : 'sqlColumnType',
                mapping : 'type'
            }, {
                name : 'javaSqlColumnType',
                mapping : 'javaSqlType'
            }, {
                name : 'columnClass'
            }],
            listeners : {
                scope : this,
                beforeload : function (store) {                    
                    var dataSourceUrl = this.datasourceUtils.getDataSourceUrl();
	                if (!this.dataSourceUrl) {
                        return false;
                    }
                    this.proxy.setUrl(dataSourceUrl);
                },
                add : function (store, records) {
                    Ext.each(records, function (record) {
                        record.data.structure = {
                            tableName : record.data.tableName,
                            tableAlias : record.data.tableAlias,
                            schemaName : record.data.schemaName,
                            dataIndex : record.data.dataIndex                            
                        };
                        var tmp = record.data.tableAlias ? record.data.tableAlias : record.data.tableName;
                        tmp += "." + record.data.dataIndex;
                        record.data.nomAffiche = tmp;
                    });
                }
            }

        });

        var cmFields = new Ext.grid.ColumnModel({
            columns : [ {
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


        /**
         * The grid used to display the columns of selected tables
         * @type Ext.grid.GridPanel
         */
        return new Ext.grid.GridPanel({
			layout : 'fit', 
            store : storeFields,
            cm : cmFields,
            enableDragDrop : true,
            stripeRows : true,
            title : 'Columns Table'
        });

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
        var storeTablesJDBC = new Ext.data.JsonStore({
            root : "database.tables",
            datasourceUtils : this, 
            fields : [ {
                name : 'url'
            }, {
                name : 'schemaName',
                mapping : 'schema'
            }, {
                name : 'name'
            } ],
            proxy : httpProxyJDBC,
            listeners : {
                beforeload : function () {
                    var dataSourceUrl = this.datasourceUtils.getDataSourceUrl();
                    this.proxy.setUrl(dataSourceUrl);
                }
            }
        });

        /**
         * The columnModel of the grid that displays the tables of a datasource.
         * @type Ext.grid.ColumnModel
         */
        var cmTablesJDBC = new Ext.grid.ColumnModel({
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
            store : storeTablesJDBC,
            cm : cmTablesJDBC,
            selModel : Ext.create('Ext.selection.RowModel',{}),
            enableDragDrop : true,
            stripeRows : true,
            title : 'Tables JDBC',
            id : 'Tables_JDBC'
        });
	}, 
    /**
     * called to refresh the store of the gridColumnTables. 
     * For each record in the gridTablesDataset, il will request the datasource to get the definition of a JDBC Table
     * @method
     */
	loadColumnsBDD : function () {
        var store = this.panelSelectTables.getStoreSelectedTables();

        var dataSourceUrl = this.getDataSourceUrl();
        
        if (!dataSourceUrl) {
            return false;
        }
		
        if (store._getDirty() || store.getModifiedRecords().length > 0) {
            if (this.panelSelectFields.getBDDPanel()) {
				var storeFields = this.panelSelectFields.getBDDPanel().getStore();
				storeFields.removeAll();
            }

            store.each(function (rec) {
                Ext.Ajax.request({
                    url : dataSourceUrl + "/" + rec.data.name,
                    method : 'GET',
                    params : {
                        tableName : rec.data.name,
                        tableAlias : rec.data.alias,
                        schemaName : rec.data.schemaName
                    },
                    scope : this,
                    success : function (ret, options) {
                        var Json = Ext.decode(ret.responseText);
                        if (Json.success) {
//                            var store = this.panelSelectFields.getBDDPanel().getStore();
                            var columns = Json.table;
                            Ext.each(columns.attributes, function (column, index, columns) {
                                this.panelSelectFields.getBDDPanel().getStore().add({
                                    dataIndex : column.name,
                                    schemaName : options.params.schemaName,
                                    tableName : options.params.tableName,
                                    tableAlias : options.params.tableAlias,
                                    sqlColumnType : column.type,
                                    javaSqlColumnType : column.javaSqlType,
                                    columnClass : column.columnClass
                                });
                            }, this);

                        } else {
                            Ext.Msg.alert(i18n.get('label.warning'), Json.message);
                        }
                    },
                    failure : alertFailure
                });
            }, this);
            store._setDirty(false);
            store.commitChanges();
        }
    }, 
    /**
     * Validate the tables Selection
     * @returns {} an object with boolean success attribute. 
     */
    validateTablesSelection : function (grid) {
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
            }, {
                id : 'alias',
                header : i18n.get('headers.tableAlias'),
                width : 80,
                sortable : true,
                dataIndex : 'alias',
                editor : new Ext.form.TextField({
		            disabled : this.action === 'view' ? true : false
                }),
                renderer : function (value, metadata, record, rowIndex, colIndex, store) {
                    if (!this.editable) {
                        metadata.css = 'uneditable-cell';
                        metadata.attr = 'ext:qtip="'+i18n.get("label.cannotChangeTableAlias")+'"';
                    }
                    return value;
                }
            } ]
        });
    }, 
    
    /**
     * Returns the column Model for Dataset Fields grid
     * @returns {Ext.grid.ColumnModel} columnModel
     */
    getCmFieldsDataset : function () {
        return new Ext.grid.ColumnModel({
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
    	return [ [ 'SQL', i18n.get('label.sql') ], [ 'VIRTUAL', i18n.get('label.virtual') ] ];
    }
	

});