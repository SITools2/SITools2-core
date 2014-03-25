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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser*/
Ext.namespace('sitools.admin.datasets');


/**
 * Define the window of the dataset Configuration
 * @cfg {String} url the Url to save the data (only when modify)
 * @cfg {String} action (required) "active", "modify" "view"
 * @cfg {Ext.data.Store} store (required) : the datasets store 
 * @cfg {String} urlDatasources The url of the JDBC datasources
 * @cfg {String} urlDatasourcesMongoDB The url of the MongoDB datasources
 * @class sitools.admin.datasets.datasetForm
 * @extends Ext.Panel
 */
Ext.define('sitools.admin.datasets.datasetSelectTables', { 
    extend : 'Ext.Panel',
    initComponent : function () {
		var action = this.action;

        Ext.apply(this, {
             
            title : i18n.get('label.selectTables'), // "select Tables",
            layout : 'fit', 
            id : "selectTablesPanel",
            listeners : {
                scope : this,
                activate : function (panel) {
                    //instanciate the panels at the first passage
					if (Ext.isEmpty(this.items.items)) {
						this.buildPanel();	
						this.loadInitialData();
					}

//					this.storeTablesJDBC.load();
					if (action == 'view') {
						panel.getEl().mask();
					}
                }, 
                datasourceChanged : function () {
					this.datasourceUtils = this.scope.datasourceUtils;
					this.gridTablesBDD = this.datasourceUtils.createGridTablesBDD();
					
					this.displayPanelTables.setFirstGrid(this.gridTablesBDD);	
					this.gridTablesBDD.getStore().load();
                }, 
                initializeDatasource : function () {
					if (Ext.isEmpty(this.items.items)) {
						this.buildPanel();	
						this.loadInitialData();
					}
                }
            }
        });
		
		sitools.admin.datasets.datasetSelectTables.superclass.initComponent.call(this);
    }, 
    loadInitialData : function () {
		var data = this.scope.initialData;
		if (data && data.structures) {
			var structures = data.structures;
			for (var i = 0; i < structures.length; i++) {
				this.storeTablesDataset.add({
					name : structures[i].name,
					alias : structures[i].alias,
					schemaName : structures[i].schemaName
				});

			}
		}
    },
    
    buildPanel : function () {
		this.datasourceUtils = this.scope.datasourceUtils;
		
		this.gridTablesBDD = this.datasourceUtils.createGridTablesBDD();
		this.gridTablesBDD.getStore().load();
		
		var cmTablesDataSet = this.datasourceUtils.getCmTablesDataset();
        
		/**
         * The store that contains the tables of a Dataset.
         * @type Ext.grid.ColumnModel
         */
        this.storeTablesDataset = new sitools.widget.JsonStore({
            id : 'storeTablesDataset',
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'name',
                type : 'string'
            }, {
                name : 'alias',
                type : 'string'
            }, {
                name : 'schemaName',
                type : 'string'
            }]
        });

        /**
         * The grid that displays the tables of a dataset.
         * @type Ext.grid.ColumnModel
         */
        this.gridTablesDataset = Ext.create('Ext.grid.Panel', {
            layout : 'fit', 
            store : this.storeTablesDataset,
            columns : cmTablesDataSet,
            selModel : Ext.create('Ext.selection.RowModel',{
                mode : 'MULTI'
            }),
            autoScroll : true,
            enableDragDrop : true,
            stripeRows : true,
            forceFit : true,
            title : 'Tables Dataset',
            plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
                pluginId : 'tableAliasEditing',
                clicksToEdit: 2
            })]
        });

        this.displayPanelTables = new sitools.component.datasets.selectItems({
			grid1 : this.gridTablesBDD, 
			grid2 : this.gridTablesDataset, 
			defaultRecord : {}
        });
		
        this.add(this.displayPanelTables);
        this.doLayout();
    }, 
    /**
     * Returns true when at least one Table is selected
     * @returns {boolean}
     */
    isFilled : function () {
		return this.gridTablesDataset.getStore().getCount() > 0;
    },
    /**
     * Returns the store of the dataset Tables
     * @returns {Ext.data.Store}
     */
    getStoreSelectedTables : function () {
		return this.gridTablesDataset.getStore();
    }, 
    /**
     * A method to validate the panel. 
     * @returns {} an objec with a "success" attributes
     */
    validatePanel : function () {
		return this.datasourceUtils.validateTablesSelection(this.gridTablesDataset);
    }
});

