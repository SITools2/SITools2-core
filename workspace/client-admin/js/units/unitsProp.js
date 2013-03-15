/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 showHelp, ann, mainPanel, helpUrl:true, loadUrl*/
Ext.namespace('sitools.admin.units');

/**
 * A Panel to show dimension data from a specific dimension
 * 
 * @cfg {String} the action to perform
 * @cfg {sitools.admin.units.unitsCrudPanel} the parent
 * @cfg {String} the urlAdmin
 * @class sitools.admin.units.unitsProp
 * @extends Ext.Window
 */
//sitools.component.units.unitsProp = Ext.extend(Ext.Window, {
    sitools.admin.units.unitsProp = Ext.extend(Ext.Window, {
    width : 700,
    height : 480,
    modal : true,
    resizable : false,
    classChosen : "",
    unitsId : null,    
    helperName : null,

    initComponent : function () {

        this.title = this.action == "create" ? i18n.get('label.createUnits') : i18n.get('label.modifyUnits'); 

        this.gridHelper = new Ext.grid.GridPanel({
            viewConfig : {
                forceFit : true
            },
            id : 'gridHelper',
            title : i18n.get('title.unitsHelperClass'),
            store : new Ext.data.JsonStore({
                root : 'data',
                restful : true,
                proxy : new Ext.data.HttpProxy({
                    url : this.urlAdmin + "/unithelpers",
                    restful : true,
                    method : 'GET'
                }),
                remoteSort : false,
                idProperty : 'id',
                fields : [ {
                    name : 'helperName',
                    type : 'string'
                } ],
                autoLoad : true
            }),

            cm : new Ext.grid.ColumnModel({
                // specify any defaults for each column
                defaults : {
                    sortable : true
                // columns are not sortable by default
                },
                columns : [ {
                    header : i18n.get('label.helperName'),
                    dataIndex : 'helperName',
                    width : 100,
                    sortable : true
                } ]
            })/*,
            listeners : {
                scope : this,
                cellclick :  this.onClassClick
            }*/

        });

        this.formPanel = new Ext.FormPanel({
            padding : 10,
            id : 'formPanel',
            defaultType : 'textfield',
            title : i18n.get('title.unitsDetails'),
            items : [ {
                fieldLabel : i18n.get('label.name'),
                name : 'name',
                anchor : '100%', 
                allowBlank : false
            }, {
                fieldLabel : i18n.get('label.description'),
                name : 'description',
                anchor : '100%'               
            } ]
        });
        
        
        // ---------------- CONVERTERS GRID
        // Creation de la grid des tables d'une datasource
        this.storeConvertersFromHelper = new Ext.data.JsonStore({
            root : "data.converters",
            fields : [ {
                name : 'name',
                type : 'string'
            }]
        });
        this.cmConvertersFromHelper = new Ext.grid.ColumnModel({
            columns : [ {
                id : 'name',
                header : i18n.get('headers.name'),
                width : 500,
                sortable : true,
                dataIndex : 'name'
            } ]
        });

        this.gridConvertersFromHelper = new Ext.grid.GridPanel({
            layout : 'fit', 
            store : this.storeConvertersFromHelper,
            cm : this.cmConvertersFromHelper,
            sm : new Ext.grid.RowSelectionModel({}),
            enableDragDrop : true,
            stripeRows : true,
            title : i18n.get("title.convertersFromHelper")            
        });

        // Creation de la grid des tables du dataset
        var cmConvertersForDimension = new Ext.grid.ColumnModel({
            columns : [ {
                id : 'name',
                header : i18n.get('headers.name'),
                width : 500,
                sortable : true,
                dataIndex : 'name'
            } ]
        });

        this.storeConvertersForDimension = new Ext.data.JsonStore({
            fields : [ {
                name : 'name',
                type : 'string'
            } ]
        });

        this.gridConverters = new Ext.grid.EditorGridPanel({
            layout : 'fit', 
            store : this.storeConvertersForDimension,
            cm : cmConvertersForDimension,
            sm : new Ext.grid.RowSelectionModel({}),
            autoScroll : true,
            enableDragDrop : true,
            stripeRows : true,
            title : i18n.get("title.convertersForDimension")
        });

        var displayPanelConverters = new sitools.component.datasets.selectItems({
			grid1 : this.gridConvertersFromHelper, 
			grid2 : this.gridConverters, 
			defaultRecord : {}
        });
        
        this.gridChooseConverters = new Ext.Panel({
            title : i18n.get('label.gridConverters'),
            layout : 'fit', 
            items : [ displayPanelConverters ],            
            listeners : {
                scope : this,
                activate : function () {
                    this.loadConverters();
                }
            }
        });
        
        
        
        // colonne avec checkbox pour choisir quelles colonnes indexer
        /*var indexed = new Ext.grid.CheckColumn({
            header : i18n.get('header.selected'),
            dataIndex : 'selected',
            width : 60
        });
        
        // définition des plugins nécessaires (colonnes avec checkbox )
        var plugins = [ indexed ];
        
        this.gridConverters = new Ext.grid.EditorGridPanel({
            viewConfig : {
                forceFit : true
            },
            plugins : plugins,
            id : 'gridFieldMapping',
            layout : 'fit',
            title : i18n.get('title.parametersMapping'),            
            store : new Ext.data.JsonStore({
                idProperty : 'name',
                fields : [ {
                    name : 'name',
                    type : 'string'
                }, {
                    name : 'selected',
                    type : 'boolean'
                }],
                autoLoad : false
            }), 
            cm : new Ext.grid.ColumnModel({
                // specify any defaults for each column
                defaults : {
                    sortable : true
                // columns are not sortable by default
                },
                columns : [ {
                    header : i18n.get('label.name'),
                    dataIndex : 'name',
                    width : 100,
                    sortable : true
                }, indexed ]
            })
        });*/
        //------------------- UNITS GRID 
        this.gridUnit = new Ext.grid.EditorGridPanel({
            id : 'gridUnit',
            title : i18n.get('title.gridUnit'),
            store : new Ext.data.JsonStore({
                idProperty : 'name',
                fields : [ {
                    name : 'label',
                    type : 'string'
                }, {
                    name : 'unitName',
                    type : 'string'
                }]
            }),
            tbar : {
	            xtype : 'toolbar',
	            defaults : {
	                scope : this
	            },
	            items : [ {
	                text : i18n.get('label.create'),
	                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
	                handler : this.onCreateUnit
	            }, {
	                text : i18n.get('label.delete'),
	                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
	                handler : this.onDeleteUnit
	            } ]
	        },
            cm : new Ext.grid.ColumnModel({
                // specify any defaults for each column
                defaults : {
                    sortable : true
                // columns are not sortable by default
                },
                columns : [ {
                    header : i18n.get('label.name'),
                    dataIndex : 'label',
                    width : 250,
                    editor : new Ext.form.TextField({
                        allowBlank : false
                    })
                }, {
                    header : i18n.get('label.unit'),
                    dataIndex : 'unitName',
                    width : 250,
                    editor : new Ext.form.TextField({
                        allowBlank : false
                    })                  
                } ],
                autoLoad : false
            }),
            sm : new Ext.grid.RowSelectionModel({
                singleSelect : true
            })
        });

        this.tabPanel = new Ext.TabPanel({
            height : 450,
            activeTab : 0,
            items : (this.action == "create") ? [ this.gridHelper, this.formPanel, this.gridChooseConverters, this.gridUnit ] : [
                this.formPanel, this.gridChooseConverters, this.gridUnit 
            ],
            buttons : [ {
                text : i18n.get('label.ok'),
                scope : this,
                handler : this.onValidate
            }, {
                text : i18n.get('label.cancel'),
                scope : this,
                handler : function () {
                    this.close();
                }
            } ],
            listeners : {
                scope : this,
                beforetabchange : this.beforeTabChange
            }
        });
        this.items = [ this.tabPanel ];
        sitools.admin.units.unitsProp.superclass.initComponent.call(this);
    },

    beforeTabChange : function (self, newTab, currentTab) {
        if (this.action == "create") {
            if (newTab.id == "fieldMappingFormPanel" || newTab.id == "gridFieldMapping") {
                var rec = this.gridHelper.getSelectionModel().getSelected();
                if (!rec) {
                    var tmp = new Ext.ux.Notification({
                        iconCls : 'x-icon-information',
                        title : i18n.get('label.information'),
                        html : i18n.get('warning.noselection'),
                        autoDestroy : true,
                        hideDelay : 1000
                    }).show(document);
                    return false;
                }
            }
        }

    },
    /*onClassClick : function (self, rowIndex, columnIndex, e) {
        if (this.action == "create") {
            var rec = this.gridHelper.getSelectionModel().getSelected();
            if (!rec) {
                var tmp = new Ext.ux.Notification({
                        iconCls : 'x-icon-information',
                        title : i18n.get('label.information'),
                        html : i18n.get('warning.noselection'),
                        autoDestroy : true,
                        hideDelay : 1000
                    }).show(document);
                return false;
            }
            
            //fill in the converter grid
            var converterStore = this.gridConverters.getStore();
            converterStore.removeAll();
            Ext.each(rec.data.converters, function (record) {
                var conv = {};
                conv.name = record;
                conv.selected = true;                
                converterStore.add(new Ext.data.Record(conv));
            });
        }

    },*/

    /**
     * Set the dimension information if the action is "modify"
     */
    afterRender : function () {
        sitools.admin.units.unitsProp.superclass.afterRender.apply(this, arguments);

        if (this.action == "modify") {
            this.unitsId = this.record.data.id;
            Ext.Ajax.request({
                url : this.urlAdmin + "/dimension/" + this.unitsId,
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var json = Ext.decode(ret.responseText);
                    if (!json.success) {
                        Ext.Msg.alert(i18n.get('label.warning'), json.message);
                        return false;
                    }
                    this.fillFormAndGrids(json.dimension);
                },
                failure : alertFailure
            });
        }
    },

    /**
     * Fill the properties fields of the dimension
     * @param unit to fill the dimension with 
     */
    fillFormAndGrids : function (unit) {
        var form = this.formPanel.getForm();
        if (!Ext.isEmpty(unit)) {
            //helperName
            this.helperName = unit.dimensionHelperName;
            
            //form
            var rec = {};
            rec.name = unit.name;
            rec.description = unit.description;
            form.loadRecord(new Ext.data.Record(rec));
            
            //converters 
            var converters = unit.unitConverters;
            if (!Ext.isEmpty(converters)) {
                var storeConverter = this.gridConverters.getStore();
                for (var i = 0; i < converters.length; i++) {
                    var converter = {};
                    converter.name = converters[i];
                    storeConverter.add(new Ext.data.Record(converter));
                }
            }
            
            //units
            //converters 
            var units = unit.sitoolsUnits;
            if (!Ext.isEmpty(units)) {
                var storeUnit = this.gridUnit.getStore();
                for (var j = 0; j < units.length; j++) {
                    var unitTmp = {};
                    unitTmp.label = units[j].label;
                    unitTmp.unitName = units[j].unitName;
                    storeUnit.add(new Ext.data.Record(unitTmp));
                }
            }
        }
    },
    
    /**
     * Load Converters into store
     */
    loadConverters : function () {
        var helperName;
        if (this.action == "create") {
			var record = this.gridHelper.getSelectionModel().getSelected();
			if (!record) {
				var tmp = new Ext.ux.Notification({
						iconCls : 'x-icon-information',
						title : i18n.get('label.information'),
						html : i18n.get('warning.noselection'),
						autoDestroy : true,
						hideDelay : 1000
					}).show(document);
				return false;
			}
	        
			helperName = record.data.helperName;
        } else {
            helperName = this.helperName;
        }
		var url = this.urlAdmin	+ "/unithelpers/" + helperName;
        this.gridConvertersFromHelper.getStore().removeAll();
        Ext.Ajax.request({
			url : url,
			method : "GET",
			scope : this,
			success : function (ret) {
				var data = Ext.decode(ret.responseText);
				if (!data.success) {
					Ext.Msg.alert(i18n.get('label.warning'),
							data.message);

					return false;
				}
                
                var converters = data.dimensionHelper.converters;
                if (!Ext.isEmpty(converters)) {
	                var storeConverter = this.gridConvertersFromHelper.getStore();
	                for (var i = 0; i < converters.length; i++) {
	                    var converter = {};
	                    converter.name = converters[i];
	                    storeConverter.add(new Ext.data.Record(converter));
	                }
	            }
                
			},
			failure : alertFailure
		});
	},

	/**
	 * Create dimension from selected converters
	 */
    onValidate : function () {
        
        var rec;
        var jsonReturn = {};
        if (this.action == "create") {
            rec = this.gridHelper.getSelectionModel().getSelected();
            if (!rec) {
                var tmp = new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('warning.noselection'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);
                return false;
            }
            jsonReturn.dimensionHelperName = rec.data.helperName;    
        } else {
            jsonReturn.id = this.unitsId;
            jsonReturn.dimensionHelperName = this.helperName;
        }
        
        
        var form = this.formPanel.getForm();
        if (!form.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }
        
        Ext.iterate(form.getFieldValues(), function (key, value) {
            jsonReturn[key] = value;
        });
    
        //converters
        jsonReturn.unitConverters = [];
        var storeConverters = this.gridConverters.getStore();

        for (var i = 0; i < storeConverters.getCount(); i++) {
            var recTmp = storeConverters.getAt(i);
            jsonReturn.unitConverters.push(recTmp.data.name);
        }
        
        //units
        jsonReturn.units = [];
        var storeUnits = this.gridUnit.getStore();

        for (i = 0; i < storeUnits.getCount(); i++) {
            var unit = storeUnits.getAt(i).data;
            jsonReturn.units.push(unit); 
        }
        
        var url = this.urlAdmin + "/dimension", method;
        if (this.action == "modify") {
            url += "/" + this.unitsId;
            method = "PUT";
        } else {

            method = "POST";
        }

        Ext.Ajax.request({
            url : url,
            method : method,
            scope : this,
            jsonData : jsonReturn,
            success : function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
					Ext.Msg.alert(i18n.get('label.warning'),
							data.message);

					return false;
				}

                var tmp = new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('label.unitsSaved'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);

                this.parent.getStore().reload();
                this.close();
            },
            failure : alertFailure
        });

    },

    onClose : function () {

    },
    
    /**
     * Create a new {Ext.data.Record} record to add a unit property
     */
    onCreateUnit : function () {
        this.gridUnit.getStore().add(new Ext.data.Record());
    },
    
    /**
     * Delete the selected unit property
     */
    onDeleteUnit : function () {
        var grid = this.gridUnit;
        var rec = grid.getSelectionModel().getSelected();
        if (!rec) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
            return;
        }
        grid.getStore().remove(rec);

    }

});