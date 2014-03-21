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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl*/
Ext.namespace('sitools.component.forms.componentsAdminDef');

/**
 * Object to build form Components administration objects in dataset Context
 * @class sitools.component.forms.componentsAdminDef.DatasetContext
 * 
 */
sitools.component.forms.componentsAdminDef.DatasetContext = function () {
	this.context = "dataset";
	
	/**
	 * @method buildComboParam1 build a single 
	 * @param {} scope the scope for this method.
	 */
	this.buildComboParam1 = function (scope) {
		scope.storeColumn = new Ext.data.JsonStore({
	        root : 'ColumnModel',
	        idProperty : 'header',
	        remoteSort : false,
	        fields : [ {
	            name : 'id',
	            type : 'string'
	        }, {
	            name : 'dataIndex',
	            type : 'string'
	        }, {
	            name : 'schema',
	            type : 'string'
	        }, {
	            name : 'tableAlias',
	            type : 'string'
	        }, {
	            name : 'tableName',
	            type : 'string'
	        }, {
	            name : 'header',
	            type : 'string'
	        }, {
	            name : 'toolTip',
	            type : 'string'
	        }, {
	            name : 'width',
	            type : 'int'
	        }, {
	            name : 'sortable',
	            type : 'boolean'
	        }, {
	            name : 'visible',
	            type : 'boolean'
	        }, {
	            name : 'filter',
	            type : 'boolean'
	        }, {
	            name : 'columnOrder',
	            type : 'int'
	        }, {
	            name : 'columnType',
	            type : 'string'
	        }, {
	            name : 'notion',
	            type : 'string',
	            mapping : 'notion.name'
	        }, {
	            name : 'notionUrl',
	            type : 'string',
	            mapping : 'notion.url'
	        }, {
	            name : 'columnAlias',
	            type : 'string'
	        }, {
	            name : 'dimensionId',
	            type : 'string'
	        }, {
	            name : 'unitName',
	            type : 'string'
	        }
	
	        ]
	    });	
	    
	    Ext.each(scope.datasetColumnModel, function (column) {
            if (column.specificColumnType != 'VIRTUAL') {
                scope.storeColumn.add(column);
            }
        }, this);
		return new Ext.form.ComboBox({
            fieldLabel : i18n.get('label.column') + "1",
            triggerAction : 'all',
            name : "PARAM1",
            specificType : "mapParam", 
            columnIndex : 1, 
            lazyRender : true,
            mode : 'local',
            store : scope.storeColumn,
            valueField : 'columnAlias',
            displayField : 'header',
            anchor : '100%', 
            allowBlank : false
        });
	};
	
	/**
	 * @method buildCombosConeSearch build 3 combos 
	 * @param {} scope the scope for this method.
	 */
	this.buildCombosConeSearch = function (scope) {
		var labels = ['X/RA', 'Y/DEC', 'Z/ID'];
        for (var i = 1; i <= 3; i++) {
			scope['mapParam' + i] = new Ext.form.ComboBox({
			    fieldLabel : labels[i - 1],
			    triggerAction : 'all',
			    name : "PARAM" + i,
			    specificType : "mapParam", 
			    columnIndex : i, 
			    lazyRender : true,
			    mode : 'local',
			    store : scope.storeColumn,
			    valueField : 'columnAlias',
			    displayField : 'header',
			    anchor : '100%', 
			    allowBlank : false 
			    
			}); 
			if (scope.action == "modify") {
				Ext.apply(scope['mapParam' + i], {
					value : scope.selectedRecord.data.code[i - 1]
				});
			}
	        //this.setHeight(this.getHeight() + 30);
			//this.ownerCt.ownerCt.setHeight(this.ownerCt.ownerCt.getHeight() + 30);

			scope.insert(i, scope['mapParam' + i]);
        }		
	};
	
	this.onChangeColumn = function () {
		var storeColumns = this.storeColumn;
		var columnAlias = this.mapParam1.getValue();
		if (Ext.isEmpty(columnAlias)) {
			this.dimension.setValue(null);
			this.dimension.setDisabled(true);
			return;
		}
		var rec = storeColumns.getAt(storeColumns.find("columnAlias", columnAlias));
		if (Ext.isEmpty(rec)) {
			this.dimension.setValue(null);
			this.dimension.setDisabled(true);
			return;
		}
		this.columnDimensionId = rec.get("dimensionId");
		if (!Ext.isEmpty(this.columnDimensionId)) {
			this.dimension.setValue(this.columnDimensionId);
			this.dimension.setDisabled(false);
		}
		else {
			this.dimension.setValue(null);
			this.dimension.setDisabled(true);
		}
		this.columnUnit = rec.get("unit");
    };
    
    this.activeDimension = function () {
		var storeColumns = this.storeColumn;
		var columnAlias = this.mapParam1.getValue();
		if (!Ext.isEmpty(columnAlias)) {
            var rec = storeColumns.getAt(storeColumns.find("columnAlias", columnAlias));
            if (!Ext.isEmpty(rec)) {
                this.columnDimensionId = rec.get("dimensionId");
                if (!Ext.isEmpty(this.columnDimensionId)) {
                    this.dimension.setDisabled(false);
                }
            }
        }
    };
    
    this.buildUnit = function () {
		return;
    };

};
