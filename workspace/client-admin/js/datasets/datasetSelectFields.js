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
 * @class sitools.admin.datasets.datasetSelectFields
 * @extends Ext.Panel
 */
Ext.define('sitools.admin.datasets.datasetSelectFields', { 
    extend : 'Ext.Panel',
    initComponent : function () {
		var action = this.action;
        
        Ext.apply(this, {
            title : i18n.get('label.selectFields'), 
            layout : 'fit', 
            id : "selectTablesFields",
            listeners : {
                scope : this,
                activate : function (panel) {
                    //instanciate the panels if not yet done
					if (Ext.isEmpty(this.items.items) || this.items.getCount() === 0) {
						this.buildPanel();	
						this.loadInitialData();
					}
					this.scope.datasourceUtils.loadColumnsBDD();

					if (action == 'view') {
						panel.getEl().mask();
					}
                }, 
                datasourceChanged : function () {
					this.datasourceUtils = this.scope.datasourceUtils;
					this.gridTablesBDD = this.datasourceUtils.getGridTablesBDD();
					
					this.displayPanelTables.setFirstGrid(this.gridTablesBDD);	
					this.gridTablesBDD.getStore().load();
                }, 
                initializeDatasource : function () {
					if (Ext.isEmpty(this.items.items) || this.items.getCount() === 0) {
						this.buildPanel();	
						this.loadInitialData();
					}
                }
            }
        });
        
		
		
		sitools.admin.datasets.datasetSelectTables.superclass.initComponent.call(this);
    }, 
    loadInitialData : function () {
		//nothing to do...
		return; 
    }, 
    buildPanel : function () {
		this.datasourceUtils = this.scope.datasourceUtils;
		
		this.gridFieldsBDD = this.datasourceUtils.createGridFieldsBDD();
//		this.datasourceUtils.loadColumnsBDD();
		
        var cmFieldsDataset = this.datasourceUtils.getCmFieldsDataset();
        
        /**
         * The grid that displays the Fields of a dataset.
         * @type Ext.grid.ColumnModel
         */
        this.gridFieldsDataset = Ext.create('Ext.grid.Panel', {
            title : 'Columns Dataset',
			layout : 'fit', 
            store : this.gridFieldsDataset,
            columns : cmFieldsDataset,
            autoScroll : true,
            enableDragDrop : true,
            stripeRows : true,
            forceFit : true,
            selModel : Ext.create('Ext.selection.RowModel',{
                mode : 'MULTI'
            }),
            plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
                pluginId : 'fieldAliasEditing',
                clicksToEdit: 2
            })]
        });

        var defaultRecord = [ {
            name : 'width',
            value : this.scope.defaultColumnWidth
        }, {
            name : 'visible',
            value : this.scope.defaultColumnVisible
        }, {
            name : 'sortable',
            value : this.scope.defaultColumnSortable
        }, {
            name : 'filter',
            value : this.scope.defaultColumnFiltrable
        }, {
            name : 'specificColumnType',
            value : 'DATABASE'
        } ];
        
        this.displayPanelFields = new sitools.component.datasets.selectItems({
			grid1 : this.gridFieldsBDD, 
			grid2 : this.gridFieldsDataset, 
			defaultRecord : defaultRecord, 
			listeners : {
                activate : function (panel) {
                    if (action == 'view') {
						panel.getEl().mask();
					}
                }
			}, 
            scope : this.scope
        });
		
        this.add(this.displayPanelFields);
        this.datasourceUtils.loadColumnsBDD();
        this.doLayout();
    }, 
    isFilled : function () {
    	return this.gridFieldsDataset.getStore().getCount() > 0;
    }, 
    getBDDPanel : function () {
		return this.gridFieldsBDD;
    }, 
    setFirstGrid : function (panel) {
		if (!Ext.isEmpty(this.displayPanelFields)) {
			this.displayPanelFields.setFirstGrid(panel);
		}
    }

});


