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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl*/
/*
 * @include "../../sitoolsProject.js"
 * @include "../../desktop/desktop.js"
 * @include "../../components/forms/forms.js"
 * @include "../../components/forms/projectForm.js"
 */

/**
 * Forms Module : 
 * Displays All Forms depending on datasets attached to the project.
 * @class sitools.user.modules.projectServices
 * @extends Ext.grid.GridPanel
 */

Ext.define('sitools.user.modules.ProjectGraphModule', {
	extend : 'sitools.user.core.Module',
    
	statics : {
	    getParameters : function () {
	        return [{
                jsObj : "Ext.form.Label",
                config : {
                    text : i18n.get("label.projectGraphModuleConfiguration.help"),
                    name : 'info'

                },
                getValue : function () {
                   return this.text;
                },
                setValue : function (value) {
                    this.setText(value);
                }
	        },
            {
	            jsObj : "Ext.grid.Panel",
	            config : {
	                name : "columns",
                    flex : 1,
                    padding : 5,
                    forceFit : true,
                    store : Ext.create("Ext.data.JsonStore", {
                        fields : [ 'columnName', 'label', "selected" ],
                        proxy : {
                            type : 'memory'
                        },
                        data : [{
                            label : i18n.get("label.records"),
                            columnName : 'records',
                            selected : true
                        }, {
                            label : i18n.get("label.image"),
                            columnName : 'image',
                            selected : true
                        }, /*{
                            label : i18n.get("label.records"),
                            columnName : 'description',
                            selected : true
                        }, */{
                            label : i18n.get("label.descriptionMini"),
                            columnName : 'description',
                            selected : true
                        }, {
                            label : i18n.get("label.data"),
                            columnName : 'data',
                            selected : true
                        }, {
                            label : i18n.get("label.definitionMini"),
                            columnName : 'definition',
                            selected : true
                        }, {
                            label : i18n.get("label.feeds"),
                            columnName : 'feeds',
                            selected : true
                        },{
                            label : i18n.get("label.opensearchMini"),
                            columnName : 'opensearch',
                            selected : true
                        }],
                        listeners : {
                            add : function (store, records) {
                                Ext.each(records, function (rec) {
                                    var columnRenderer = rec.get("columnRenderer");
                                    if (!Ext.isEmpty(columnRenderer)) {
                                        var featureType = ColumnRendererEnum
                                                .getColumnRendererCategoryFromBehavior(columnRenderer.behavior);
                                        if (rec.data.isDataExported && (featureType === "Image" || featureType === "URL")) {
                                            rec.data.isDataExported = true;
                                        } else {
                                            rec.data.isDataExported = false;
                                        }
                                    }
                                });
                            }
                        }
                    }),
                    columns : [ {
                        header : i18n.get('label.columnName'),
                        dataIndex : 'label',
                        sortable : true,
                    }, {
                        xtype : 'checkcolumn',
                        header : i18n.get('headers.visible'),
                        dataIndex : 'selected',
                        editable : true,
                        width : 50
                    } ],
        	        getValue : function () {
        	            var columnsDefinition = [];
        	            var store = this.getStore();
        	            
        	            store.each(function (record) {
        	                var rec = {};
        	                rec.columnName = record.get("columnName");
        	                rec.selected = record.get("selected");
        	                columnsDefinition.push(rec);
        	            });
        	            var columnsString = Ext.JSON.encode(columnsDefinition);
        	            return columnsString;
        	        },
        	        setValue : function (value) {
        	            var columnsToExport = Ext.JSON.decode(value);
        	            Ext.each(columnsToExport, function (column) {
        	                this.getStore().findRecord("columnName", column.columnName).set("selected", column.selected);
        	            }, this);
        	        }
	            }
            }, {
                    jsObj : "Ext.grid.Panel",
                    config : {
                        name : "additionalColumns",
                        title : 'Additional Columns',
                        flex : 1,
                        padding : 5,
                        forceFit : true,
                        tbar : [{
                            xtype : 'button',
                            text : 'Add',
                            handler : function (btn) {
                                var grid = btn.up('grid');
                                grid.getStore().insert(grid.getStore().getCount(), {});

                                var rowIndex = grid.getStore().getCount() -1;

                                grid.getView().focusRow(rowIndex);

                                grid.getPlugin('cellEditing').startEditByPosition({
                                    row: rowIndex,
                                    column: 0
                                });

                            }
                        }, {
                            xtype : 'button',
                            text : 'Remove',
                            handler : function (btn) {
                                var grid = btn.up('grid');

                                var recs = grid.getSelectionModel().getSelection();
                                if (Ext.isEmpty(recs)) {
                                    popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
                                    return;
                                }
                                grid.getStore().remove(recs);

                            }
                        }],
                        plugins : [Ext.create('Ext.grid.plugin.CellEditing', {
                            clicksToEdit: 1,
                            pluginId : 'cellEditing'
                        })],
                        store : Ext.create("Ext.data.JsonStore", {
                            fields: ['columnName', 'label', 'selected', 'xtype'],
                            proxy: {
                                type: 'memory'
                            },
                        }),
                        columns : [{
                            header : i18n.get('label.columnName'),
                            dataIndex : 'label',
                            sortable : true,
                            editor : {
                                xtype : 'textfield',
                                allowBlank : false
                            }
                        }, {
                            header : i18n.get('label.xtype'),
                            dataIndex : 'xtype',
                            sortable : true,
                            editor : {
                                xtype : 'textfield',
                                allowBlank : false
                            }
                        }, {
                            xtype : 'checkcolumn',
                            header : i18n.get('headers.visible'),
                            dataIndex : 'selected',
                            editable : true,
                            width : 50
                        } ],
                        getValue : function () {
                            var columnsDefinition = [];
                            var store = this.getStore();

                            store.each(function (record) {
                                var rec = {};
                                rec.label = record.get("label");
                                rec.selected = record.get("selected");
                                rec.xtype = record.get("xtype");
                                columnsDefinition.push(rec);
                            });
                            var columnsString = Ext.JSON.encode(columnsDefinition);
                            return columnsString;
                        },
                        setValue : function (value) {
                            var columnsToExport = Ext.JSON.decode(value);
                            this.getStore().add(columnsToExport);
                            //Ext.each(columnsToExport, function (column) {
                            //    this.getStore().findRecord("label", column.label).set("selected", column.selected);
                            //    this.getStore().findRecord("label", column.label).set("xtype", column.xtype);
                            //}, this);
                        }
                    }
                }];
	    }
	},
	
    controllers : ['sitools.user.controller.modules.projectGraph.ProjectGraphController'],

    init : function (moduleModel) {
        var view = Ext.create('sitools.user.view.modules.projectGraph.ProjectGraphView',{
            moduleModel : this.getModuleModel()
        });
        
        this.show(view);

        this.callParent(arguments);
    },
    
    createViewForDiv : function () {
        return Ext.create('sitools.user.view.modules.projectGraph.ProjectGraphView', {
            inscrusted : true,
            moduleModel : this.getModuleModel()
        });
    },
    
    

    /**
     * method called when trying to save preference
     * 
     * @returns
     */
    _getSettings : function () {
        return {
            preferencesPath : "/modules",
            preferencesFileName : this.id
        };

    }
});

