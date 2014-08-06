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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl, extColModelToStorage, SitoolsDesk, sql2ext*/

Ext.namespace('sitools.user.component.dataviews.services');

/**
 * Service used to build a window to define filter on a store using Filter API.  
 * @class sitools.widget.filterTool
 * @extends Ext.Window
 */
Ext.define('sitools.user.view.component.datasets.columnsDefinition.ColumnsDefinition', {
    extend : 'Ext.grid.GridPanel',
    alias : 'sitools.user.component.columnsDefinition',
    
    layout : 'fit',
    
    componentType : "defi",
    
    width : 400,
    height : 500,
    
    initComponent : function () {
    
    	this.dictionaryMappings = this.dictionaryMappings[0];
    
    	var fields = [{
			name : 'columnAlias',
			type : 'string'
		}, {
			name : 'unit',
			type : 'string'
		}];
    
    	var columns = [{
			header : i18n.get('label.columnAlias'),
			dataIndex : 'columnAlias',
			width : 100,
			sortable : true
		}, {
			header : i18n.get('label.unit'),
			dataIndex : 'unit',
			width : 100,
			sortable : true
		}];
    
    	if (!Ext.isEmpty(this.dictionaryMappings)
    			&& !Ext.isEmpty(this.dictionaryMappings.mapping)
    			&& !Ext.isEmpty(this.dictionaryMappings.mapping[0])) {
    
    		var conceptAsTemplate = this.dictionaryMappings.mapping[0].concept;
    		// columns
    		columns.push({
    					header : i18n.get("headers.name"),
    					dataIndex : 'name',
    					width : 100
    				});
    		columns.push({
    					header : i18n.get("headers.description"),
    					dataIndex : 'description',
    					width : 120
    				});
    
    		// fields
    		fields.push({
    					name : 'name',
    					type : 'string'
    				});
    		fields.push({
    					name : 'description',
    					type : 'string'
    				});
    
    		for (var i = 0; i < conceptAsTemplate.properties.length; i++) {
    			var property = conceptAsTemplate.properties[i];
    			columns.push({
    						header : property.name,
    						dataIndex : property.name,
    						width : 80
    					});
    
    			fields.push({
    						name : property.name,
    						type : 'string'
    					});
    		}
    	}
    
    	this.store = Ext.create('Ext.data.JsonStore', {
            remoteSort : false,
            proxy : {
                type : 'memory',
                reader : {
                    type : 'json'
                }
            },
            fields : fields,
            sorters : [ {
                field : 'columnAlias',
                direction : 'ASC'
            }]
        });
    	
    	this.columns = columns;
    
    	this.callParent(arguments);
    },
    
    _getSettings : function() {
        var colModel = [];
        return {
            colModel : extColModelToJsonColModel(this.grid.getColumnModel().config),
            datasetName : this.datasetName,
            preferencesPath : this.preferencesPath,
            preferencesFileName : this.preferencesFileName
        };
    },

    /**
     * overrides onRender method : Adds records in the store for each columns.
     */
    afterRender : function() {
        this.callParent(arguments);
        
        var store = this.getStore();
        var concepts, record;
        Ext.each(this.datasetCm, function(column) {
            concepts = this.getConcepts(column);
            if (!Ext.isEmpty(concepts)) {
                Ext.each(concepts, function(concept) {
                            var rec = {
                                columnAlias : column.columnAlias,
                                unit : column.unit && column.unit.label
                            };
                            rec = Ext.apply(rec, {
                                        id : concept.id,
                                        name : concept.name,
                                        description : concept.description
                                    });

                            for (var j = 0; j < concept.properties.length; j++) {
                                var property = concept.properties[j];
                                rec[property.name] = property.value;
                            }
                            store.add(rec);
                        });
            } else {
                var rec = {
                    columnAlias : column.columnAlias
                };
                store.add(rec);
            }

        }, this);
    },

    /**
     * @param {Ext.grid.Column}
     *            column
     * @return {Array} the concepts of the column
     */
    getConcepts : function(column) {
        if (Ext.isEmpty(this.dictionaryMappings)) {
            return;

        }
        var mapping = this.dictionaryMappings.mapping;
        var concepts = [], map;
        for (var i = 0; i < mapping.length; i++) {
            map = mapping[i];
            if (column.columnAlias == map.columnAlias) {
                concepts.push(map.concept);
            }
        }
        return concepts;
    },
    /**
     * Method called when trying to show this component with fixed navigation
     * 
     * @param {sitools.user.component.columnsDefinition}
     *            me the semantic view
     * @param {}
     *            config config options
     * @returns
     */
    showMeInFixedNav : function(me, config) {
        Ext.apply(config.windowSettings, {
                    width : 400,
                    height : 400
                });
        SitoolsDesk.openModalWindow(me, config);
    }

});
