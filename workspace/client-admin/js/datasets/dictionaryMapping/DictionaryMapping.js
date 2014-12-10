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
Ext.namespace('sitools.admin.datasets.dictionaryMapping');

/**
 * A window to determine the mapping between dataset columns and dictionnary concept
 * @cfg string url The Url to request the dataset.
 * @cfg boolean masked false to allowed modification true to disable any modification.
 * @class sitools.admin.datasets.DicoMapping
 * @extends Ext.Window
 */
Ext.define('sitools.admin.datasets.dictionaryMapping.DictionaryMapping', { 
    extend : 'Ext.Window',
	alias : 'widget.s-DicoMapping',
    width : 800,
    height : 680,
    modal : true,
    resizable : true,    
    urlDataset  : null,
    dictionaryId : null,
    isModified : false,
    urlMappingPart : "/mappings",
    layout : {
        type : 'vbox',
        align : 'stretch',
        pack : 'start'
    },
    mixins : {
        utils : 'sitools.admin.utils.utils'
    },
    
    initComponent : function () {
        this.urlDictionaries = loadUrl.get('APP_URL') + loadUrl.get('APP_DICTIONARIES_URL');
        
        this.title = i18n.get("title.dicoMapping");
        this.urlDataset = this.url;
        
        /** ###################################### */
        /** Dataset column grid */
        /** ###################################### */
        
        var storeDatasetColumns = Ext.create('Ext.data.JsonStore', {
			remoteSort : false,
            autoLoad : true,
            proxy : {
                type : 'ajax',
                url : this.urlDataset,
                reader : {
                    type : 'json',
                    root : 'dataset.columnModel',
                    idProperty : 'id'
                }
            },
			fields : [{
				name : 'id',
				type : 'string'
			}, {
				name : 'columnAlias',
				type : 'string'
			}]
		});
        
        var cmDatasetColumns = {
            items : [{
                header : i18n.get('label.columnAlias'),
                dataIndex : 'columnAlias',                
                sortable : true
            }]
        };
        
        this.gridDatasetColumn = Ext.create('Ext.grid.Panel', {
            store : storeDatasetColumns,
            columns : cmDatasetColumns,
            flex : 1,
            title : i18n.get("label.datasetColumn"),
            forceFit : true,
            layout : 'fit',
            listeners : {
                scope : this,
                mappingsSelected : function (grid, columnsId) {
                    var selModel = grid.getSelectionModel();
                    selModel.deselectAll();
                    var records = [];
                    Ext.each(columnsId, function (columnId) {
                        records.push(grid.getStore().getById(columnId));                         
                    });
                    selModel.select(records);           
                }
            }
//            ,
//            selModel : Ext.create('Ext.selection.RowModel',{
//                listeners : {
//                    scope : this,
//                    rowselect : this.gridColumnRowSelectionModelListener,
//                    rowdeselect : this.gridColumnRowSelectionModelListener
//                }
//            })
        });
        
        /** ###################################### */
        /** Dictionnaries combo box and GRID */
        /** ###################################### */
        
        var storeDictionaries = Ext.create('Ext.data.JsonStore', {
            fields : [ 'id', 'name', 'conceptTemplate' ],
            autoLoad : true,
            proxy : {
                type : 'ajax',
                url : this.urlDictionaries,
                reader : {
                    type : 'json',
                    root : "data"
                }
            }
        });
        
        
        this.comboDictionaries = Ext.create('Ext.form.field.ComboBox', {
            store : storeDictionaries,
            displayField : 'name',
            valueField : 'id',
            typeAhead : true,
            queryMode : 'local',
            forceSelection : true,
            triggerAction : 'all',
            emptyText : i18n.get('label.selectDictionary'),
            selectOnFocus : true,
            listeners : {
                scope : this,
                select : function (combo, rec, index) {
                    this.isModified = false;
                    this.createDictionaryConceptGrid(rec);
                    
                }
            }
        });
        //temporary definition of gridDictionaryConcept to display the title
        this.gridDictionaryConcept = Ext.create('Ext.panel.Panel', {
            id : 'gridDictionaryConcept',
            flex : 2,
            title : i18n.get("label.dictionaryConcepts"),
            layout : 'fit'
        });
        
        this.checkboxDefaultDictionary = Ext.create('Ext.form.field.Checkbox', {
            name : 'defaultDico',
            boxLabel: i18n.get("label.defaultDictionary"),
            anchor : '100%',
            maxLength : 100,
            disabled : this.masked            
        });
        
        this.choicePanel = Ext.create('Ext.panel.Panel', {
            items : [this.gridDatasetColumn, this.gridDictionaryConcept],
            flex : 1,
            layout : {
                type : 'hbox',
                align : 'stretch',
                pack : 'start'
            },
            tbar : {
	            xtype : 'toolbar',
	            defaults : {
	                scope : this, 
	                xtype : 's-menuButton'
	            },
	            items : [this.comboDictionaries, '-', 
                    {
                        xtype: 'tbspacer',
                        width : '5'
                        
                    }, this.checkboxDefaultDictionary, '-'
                    , {
                    text : i18n.get('label.save'),
                    icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/save.png',
                    handler : function () {
                        this.onValidate(false);
                    },
                    disabled : this.masked
                }, {
                    text : i18n.get('label.delete'),
                    icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                    handler : this.onDelete,
                    disabled : this.masked
                }]
            },
            listeners : {
                scope : this,
                afterrender : function () {
                    if (this.masked) {
                        this.choicePanel.body.mask();
                    }
                }
            }
            
        });
        
        
        //temporary definition of gridMapping to diplay the title
        this.gridMapping = Ext.create('Ext.panel.Panel', {
            flex : 1,
            title : i18n.get("label.mappingConceptColumn"),
			listeners : {
				scope : this,
				afterrender : function () {
					if (this.masked) {
						this.gridMapping.getEl().mask();
					}
				}
			}
        });
        
        this.items = [this.choicePanel, this.gridMapping];       
        
        this.buttons = [ {
            text : i18n.get('label.saveAndClose'),
            scope : this,
            handler : this.onValidateAndQuit,
            disabled : this.masked
        }, {
            text : i18n.get('label.close'),
            scope : this,
            handler : function () {
                this.close();
            }
        }];
        
        this.callParent(arguments);
    },
    
    //##############################################
    //Create Grids
    //##############################################
    
    /**
     * Create the dictionnary concept grid
     * @param Ext.data.Record rec
     */
    createDictionaryConceptGrid : function (rec) {
        //refresh dico grid
        rec = rec[0];
        this.dictionaryId = rec.data.id;
        var templateConcept = rec.data.conceptTemplate;
        
        this.gridDictionaryConcept = Ext.create('sitools.admin.dictionary.GridPanel', {
            template : templateConcept,
            editable : false,
            url : this.urlDictionaries + "/" + this.dictionaryId,
            id : 'gridDictionaryConcept',
            selModel : Ext.create('Ext.selection.RowModel'),
            flex : 2,
            title : i18n.get("label.dictionaryConcepts"),
            enableColumnResize : true,            
            listeners : {
                scope : this,
                mappingsSelected : function (grid, conceptsId) {
                    var selModel = grid.getSelectionModel();
                    selModel.deselectAll();
                    var records = [];
                    Ext.each(conceptsId, function (conceptId) {
                        records.push(grid.getStore().getById(conceptId));                         
                    });
                    selModel.select(records);           
                }           
            }
        });
        
        this.choicePanel.remove('gridDictionaryConcept');
        this.choicePanel.add(this.gridDictionaryConcept);
        
        var store = this.gridDictionaryConcept.getStore();
        store.removeAll();
        store.on("load", function () {
                //refresh mapping grid
                this.createMappingGrid();
                this.loadMapping();
                this.doLayout();
            }, this);
            
        store.loadConcepts();
    },
    
    
    /**
     * Create the mapping grid 
     * @method
     */
    createMappingGrid : function () {
        var store = this.gridDictionaryConcept.getStore();
        var fieldsDicoConcept = store.model.getFields();
        
        var fields = [];
        Ext.each(fieldsDicoConcept, function (record) {
            fields.push({
                name : record.name,
                type : record.type.type
            });
        });
        
        fields.push({
            name : 'columnAlias',
            type : 'string'
        });
        fields.push({
            name : 'columnId',
            type : 'string'
        });
        fields.push({
            name : 'idMapping',
            type : 'string'
        });
        fields.push({
            name : 'conceptId',
            type : 'string'
        });
        
        var storeMapping = Ext.create('Ext.data.Store', {
            fields : fields,
            sorters : [{
                property: 'columnAlias',
                direction: 'ASC'
            }],
//            sortInfo: {field: 'columnAlias', direction: 'ASC'},
            listeners : {
                scope : this,
                add : function (store, records, index) {
                    if (this.isModified &&  !this.comboDictionaries.disabled) {
                        this.comboDictionaries.disable();
                    }
                },
                remove : function (store, records, index) {
                    if (this.isModified &&  !this.comboDictionaries.disabled) {
                        this.comboDictionaries.disable();
                    }
                } 
            },
            groupField: 'columnAlias'
        });
        
        storeMapping.model.idProperty = "idMapping";
        
        var cmConfig = this.gridDictionaryConcept.columns;
        var cmConfigNew = [];
        Ext.each(cmConfig, function (column) {
           cmConfigNew.push(column.initialConfig); 
        });
        
        
//        Ext.each(cmConfigNew, function (col) {
//			col.resizable = true;
//		});
        
        cmConfigNew.push({
            header : i18n.get('label.columnAlias'),
            dataIndex : 'columnAlias',
            width : 100,
            sortable : true,
            hidden : true,
            resizable : true
        });
        
        this.remove(this.gridMapping);
        
        var groupingFeature = Ext.create('Ext.grid.feature.Grouping', {
            startCollapsed : false,
            groupHeaderTpl: 'Column alias: {name} ({rows.length} Item{[values.rows.length > 1 ? "s" : ""]})'
        });
        
        this.gridMapping = Ext.create('Ext.grid.Panel', {
            store : storeMapping,
            columns : cmConfigNew,
            flex : 1,
            title : i18n.get("label.mappingConceptColumn"),
            forceFit : true,
            layout : 'fit',
            tbar : {
                xtype : 'toolbar',
                defaults : {
                    scope : this, 
                    xtype : 's-menuButton'
                },
                items : [ {
                    text : i18n.get('label.map'),
                    icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create.png',
                    handler : this.onAddMapping
                }, {
                    text : i18n.get('label.unmap'),
                    icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
                    handler : this.onRemoveMapping
                }]
            },
            features : [groupingFeature],
//            view : new Ext.grid.GroupingView({
//                groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})',
//                autoFill : true
//            }),
            listeners : {
                scope : this,
                afterrender : function () {
                    if (this.masked) {
                        this.gridMapping.getEl().mask();
                    }
                }
            },
//            listeners : {
//                scope : this,
//                columnSelected : function (grid, columnsId) {
//                    var selModel = grid.getSelectionModel();
//                    selModel.deselectAll();
//                    var records = [];
//                    Ext.each(columnsId, function (columnId) {
//                        var index = grid.getStore().find("columnId", columnId);
//                        if (index != -1) {
//                            records.push(grid.getStore().getAt(index));
//                        }
//                    });
//                    selModel.selectRecords(records);   
//                }
//            },
            selModel : Ext.create('Ext.selection.RowModel', {
                mode : "SINGLE",
				listeners : {
					scope : this,
					selectionchange : this.gridMappingRowSelectionModelListener
				}
			})
        });
        this.add(this.gridMapping);
        
        
    },
    /**
     * @method called when select or deselect a mapping.
     * @param Ext.grid.RowSelectionModel sm
     * @param Number rowIndex
     * @param Ext.data.Record record
     */
    gridMappingRowSelectionModelListener : function (sm, records) {
        var columnsId = [];
        var conceptsId = [];
        Ext.each(records, function (rec) {
            columnsId.push(rec.get("columnId"));
            conceptsId.push(rec.get("conceptId"));
        });
        this.gridDatasetColumn.fireEvent('mappingsSelected',    this.gridDatasetColumn, columnsId);
        this.gridDictionaryConcept.fireEvent('mappingsSelected',    this.gridDictionaryConcept, conceptsId);
    },
    
//    gridColumnRowSelectionModelListener : function (sm, rowIndex, record) {
//        var records = sm.getSelections();
//        var columnsId = [];
//        Ext.each(records, function (rec) {
//            columnsId.push(rec.get("id"));
//        });
//        this.gridMapping.fireEvent('columnSelected', this.gridMapping, columnsId);        
//    },
    //##############################################
    //Actions
    //##############################################
    /**
     * @method
     * Execute onValidate
     */
    onValidateAndQuit : function () {
        this.onValidate(true);
    },
    /**
     * build the json object and call the request to save the mapping.
     * @param boolean quit
     */
    onValidate : function (quit) {
        var jsonObject = {};
        if (Ext.isEmpty(this.dictionaryId)) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noSelection.dicoMapping'));
            return;
        }
        jsonObject.dictionaryId = this.dictionaryId;
        jsonObject.defaultDico = this.checkboxDefaultDictionary.getValue();
        jsonObject.mapping = [];
        var storeMapping = this.gridMapping.getStore();
        storeMapping.each(function (mapping) {
            var rec = {
                columnId : mapping.get("columnId"),
                conceptId : mapping.get("conceptId")
            };
            
            jsonObject.mapping.push(rec);
        });
        
	    Ext.Ajax.request({
			url : this.urlDataset + this.urlMappingPart + "/"
					+ this.dictionaryId,
			method : 'PUT',
			jsonData : jsonObject,
			scope : this,
			success : function (ret) {
				var json = Ext.decode(ret.responseText);
				if (!json.success) {
					Ext.Msg.alert(i18n.get('label.warning'),
							json.message);
					return;
				}
                
                this.loadMappingData(json.dictionaryMapping);
                
                
				
                popupMessage(i18n.get("label.information"), i18n.get("dictionary.mapping.saved"), 
                        loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
                
                if (quit) {
                    this.close();
                }
                return;
			},
			failure : alertFailure
		});
    },
    
    /**
     * check if the delete Action is possible and call doDelete().
     * @method
     */
    onDelete : function () {
        if (Ext.isEmpty(this.dictionaryId)) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
            return;
        }
	    Ext.Msg.show({
	        title : i18n.get('label.delete'),
	        buttons : Ext.Msg.YESNO,
	        msg : i18n.get('dictionaryMapping.delete'),
	        scope : this,
	        fn : function (btn, text) {
	            if (btn == 'yes') {
                    this.doDelete();
	                
	            }
	        }
	    });
    },
    /**
     * Invoke a delete method on the mapping.
     * @method
     */
    doDelete : function () {
        Ext.Ajax.request({
            url : this.urlDataset + this.urlMappingPart + "/"
                + this.dictionaryId,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                var json = Ext.decode(ret.responseText);
                if (!json.success) {
                    Ext.Msg.alert(i18n.get('label.warning'),
                            json.message);
                    return;
                }
                popupMessage({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get("dictionary.mapping.deleted"),
                    autoDestroy : true,
                    hideDelay : 1000
                });
                this.loadMappingData();
            },
            failure : alertFailure
        });  
    },
    
    /**
     * Load the data already saved for that dictionary 
     */
    loadMapping : function () {
        Ext.Ajax.request({
            url : this.urlDataset + this.urlMappingPart + "/"
                    + this.dictionaryId,
            method : 'GET',            
            scope : this,
            success : function (ret) {
                var json = Ext.decode(ret.responseText);
                if (json.success) {
                    //fill the mapping grid
                    var dictionaryMapping = json.dictionaryMapping;
                    this.loadMappingData(dictionaryMapping);
                } else {
                    this.checkboxDefaultDictionary.setValue(false);
                    return;
                }
            },
            failure : alertFailure
        });
    },
      
    /**
     * load the gridMappingStore with the dictionnary mapping info
     * @param {} dictionaryMapping
     */
    loadMappingData : function (dictionaryMapping) {
        this.isModified = false;
        this.comboDictionaries.enable();
        
        var storeColumn = this.gridDatasetColumn.getStore();
        var storeConcept = this.gridDictionaryConcept.getStore();
        
        var storeGridMapping = this.gridMapping.getStore();
        storeGridMapping.removeAll();
        
        if (!Ext.isEmpty(dictionaryMapping)) {
            var defaultDico = (dictionaryMapping.defaultDico === true) ? true : false;
            this.checkboxDefaultDictionary.setValue(defaultDico);
            var mappings = dictionaryMapping.mapping;
            if (Ext.isEmpty(mappings)) {
                return;
            }
            Ext.each(mappings, function (mapDetails) {
                var column = storeColumn.getById(mapDetails.columnId);
                var concept = storeConcept.getById(mapDetails.conceptId);
                
                var idNewRec = column.get('columnAlias') + concept.get('id');
                
                var rec = {
                        idMapping : idNewRec,
                        columnAlias : column.get('columnAlias'),
                        columnId : column.get('id'),
                        conceptId : concept.get('id'),
                        id : idNewRec
                };
                Ext.iterate(concept.data, function (key, value) {
                    if (key !== "id") {
                        rec[key] = value;
                    }
                });
                storeGridMapping.add(rec);                     
                
            });
            storeGridMapping.sort();
        }
    },
    
    /**
     * Called when click on add mapping button.
     * @method
     * 
     */
    onAddMapping : function () {
        // first let's loop on the column grid to get all the selected rows
        var columnSelected = this.getLastSelectedRecord(this.gridDatasetColumn);
        
        //then loop on the concept grid to get all the selected rows
        var conceptSelected = this.getLastSelectedRecord(this.gridDictionaryConcept);
        
        var storeGridMapping = this.gridMapping.getStore();
        //calculate all the combination and create records on the mapping grid
        Ext.each(columnSelected, function (column) {
            Ext.each(conceptSelected, function (concept) {
                var idNewRec = column.get('columnAlias') + concept.get('id');
                if (Ext.isEmpty(storeGridMapping.getById(idNewRec))) {
                    var rec = {
	                    idMapping : idNewRec,
	                    columnAlias : column.get('columnAlias'),
                        columnId : column.get('id'),
                        conceptId : concept.get('id')
	                };
                    Ext.iterate(concept.data, function (key, value) {
                        if(key !== "id") {
                            rec[key] = value;
                        }
                    });
                    this.isModified = true;
                    storeGridMapping.add(rec);
                }
            }, this);
        }, this);
        if (this.isModified) {
            storeGridMapping.sort();
        }
    },
    /**
     * called when click on remove mapping button.
     * @method
     */
    onRemoveMapping : function () {
        var grid = this.gridMapping;
        var mappingSelected = grid.getSelectionModel().getSelection();
        if (mappingSelected.length === 0) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
            return;
        }
        this.isModified = true;
        grid.getStore().remove(mappingSelected);  
        
        this.gridDatasetColumn.getSelectionModel().deselectAll();
        this.gridDictionaryConcept.getSelectionModel().deselectAll();
        grid.getStore().sort();
    }   
    
});

