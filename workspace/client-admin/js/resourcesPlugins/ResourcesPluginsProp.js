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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, ImageChooser, loadUrl*/
/*
 * @include "../datasets/selectColumn.js"
 */
Ext.namespace('sitools.admin.resourcesPlugins');

Ext.define('sitools.admin.resourcesPlugins.CustomCheckColumn', {
    extend : 'Ext.grid.column.CheckColumn',
    renderer : function (value, meta, rec) {
        var toShow = rec.get("type") === "PARAMETER_USER_INPUT";
        if (toShow) {
            return this.callParent(arguments);
        } else {
            return "";
        }
    }
});


/**
 * A window of a resource plugin properties
 * 
 * @param action
 *            create or modify
 * @param parentPanel
 *            the parent panel
 * @param urlResources
 *            the resources url
 * @param urlResourcesCRUD
 *            the URL of the resource CRUD
 * @param urlParent
 *            the url of the parent Object
 * @param appClassName
 *            the parent className
 * @param idParent
 *            the parent id
 * @param parentType
 *            the type of the parent, string used only for i18n label
 * @class sitools.admin.resourcesPlugins.ResourcesPluginsProp
 * @extends Ext.Window
 */
Ext.define('sitools.admin.resourcesPlugins.ResourcesPluginsProp', {
    extend : 'Ext.Window',
    width : 700,
    height : 480,
    modal : true,
    resizable : true,
    classChosen : "",
    resourcePluginId : null,
    modelClassName : null,
    mixins : {
        utils : "sitools.admin.utils.utils"
    },
    
    requires : ['sitools.admin.datasets.SelectColumn',
                'sitools.admin.dictionary.SelectDictionary',
                'sitools.admin.resourcesPlugins.EnumerationValueTypeSelector',
                'sitools.public.widget.grid.GridSorterToolbar'],

    initComponent : function () {

        this.title = this.action == "create" ? i18n.get('label.create' + this.parentType + 'Resource') : i18n.get('label.modify' + this.parentType + 'Resource'); 

        var expander = {
            ptype: 'rowexpander',
            rowBodyTpl : new Ext.XTemplate(
                '<tpl if="this.descEmpty(description) == false" ><p class="sitoolsDescriptionText"> <b>Description :&nbsp;</b>{description} </p></tpl>',
                {
                    compiled : true,
                    descEmpty : function (description) {
                        return Ext.isEmpty(description);
                    }
                }),
            expandOnDblClick : true
        };
        
        
        this.gridresourcePlugin = Ext.create("Ext.grid.GridPanel", {
            forceFit : true,
            title : i18n.get('title.resourcePlugin' + this.parentType + 'Class'),
            store : Ext.create("Ext.data.JsonStore", {
                autoLoad : true,
                proxy : {
                    startParam : undefined,
                    limitParam : undefined,
                    url : this.urlResources,
                    type : 'ajax',
                    extraParams : {
                        appClassName : this.appClassName,
                        parent : this.idParent
                    },
                    reader : {
                        type : 'json',
                        root : 'data',
                        idProperty : 'id'
                    }
                },            
                fields : [ {
                    name : 'id',
                    type : 'string',
                    convert : function () {
                        return Ext.id();
                    }
                }, {
                    name : 'name',
                    type : 'string'
                }, {
                    name : 'description',
                    type : 'string'
                }, {
                    name : 'resourceClassName',
                    type : 'string'
                }, {
                    name : 'classAuthor',
                    type : 'string'
                }, {
                    name : 'classVersion',
                    type : 'string'
                }, {
                    name : 'parameters'                    
                }, {
                    name : 'className',
                    type : 'string'
                }, {
                    name : 'classOwner',
                    type : 'string'
                }, {
                    name : 'applicationClassName',
                    type : 'string'
                }, {
                    name : 'dataSetSelection',
                    type : 'string'
                } ]
            }),                
            columns : [{
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 100,
                sortable : true
            }, {
                header : i18n.get('label.resourceClassName'),
                dataIndex : 'className',
                width : 300,
                sortable : true
            }, {
                header : i18n.get('label.author'),
                dataIndex : 'classAuthor',
                width : 100,
                sortable : true
            }, {
                header : i18n.get('label.version'),
                dataIndex : 'classVersion',
                width : 100,
                sortable : true
            }, {
                header : i18n.get('label.classOwner'),
                dataIndex : 'classOwner',
                width : 100,
                sortable : true
            }],
            listeners : {
                scope : this,
                itemClick :  this.onClassClick
            },
            plugins : [expander]
        });

        var expanderGridFieldMapping = {
                ptype: 'rowexpander',
                rowBodyTpl : new Ext.XTemplate(
                '<tpl if="this.descEmpty(description) == false" ><p class="sitoolsDescriptionText"> <b>Description :&nbsp;</b>{description} </p></tpl>',
                {
                    compiled : true,
                    descEmpty : function (description) {
                        return Ext.isEmpty(description);
                    }
                }),
            expandOnDblClick : false           
        };

        var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        });
        
        this.gridFieldMapping = Ext.create('Ext.grid.Panel', {
            forceFit : true,
            viewConfig : {
                scope : this,
//                getRowClass : function (record, index, rowParams, store) {
//                    var cls = ''; 
//                    var violation = record.get("violation");
//                    if (!Ext.isEmpty(violation)) {
//                        if (violation.level == "CRITICAL") {
//                            cls = "red-row";
//                        } else if (violation.level == "WARNING") {
//                            cls = "orange-row";
//                        }
//                    }
//                    return cls;
//                }, 
                listeners : {
                    scope : this,
                    refresh : function (view) {
                        var grid = this.gridFieldMapping;
                        var store = grid.getStore();
                        
                        store.each(function (record) {
                            var violation = record.get("violation");
                            if (!Ext.isEmpty(violation)) {
                                var index = store.indexOf(record);
                                //var view = this.scope.gridFieldMapping.getView();
                                var htmlLineEl = view.getNode(index);
                                var el = Ext.get(htmlLineEl);
                                
                                if (violation.level == "CRITICAL") {
                                    el.addCls('red-row');
                                } else if (violation.level == "WARNING") {
                                    el.addCls('orange-row');
                                }
                                
                                var cls = (violation.level == "CRITICAL")
                                        ? "x-form-invalid-tip"
                                        : "x-form-invalid-tip x-form-warning-tip";
                                
                                var ttConfig = {
                                    html : violation.message,
                                    dismissDelay : 0,
                                    target : el,
                                    cls : cls
                                };
        
                                var ttip = new Ext.ToolTip(ttConfig);
                            }
                        });
                        this.showOptions(this.gridFieldMapping);
                    }, 
                    rowupdated : function () {
						this.showOptions(this.gridFieldMapping);
                    }
                }
            },
            id : 'gridFieldMapping',
            layout : 'fit',
            region : 'center',
            title : i18n.get('title.parametersMapping'),
            store : Ext.create("Ext.data.JsonStore", {
                proxy : {
                    type : 'memory',
                    reader : {
                        idProperty : 'name',
                        type : 'json',
                        root : 'resourcePlugin.parameters'
                    }
                },
                fields : [ {
                    name : 'name',
                    type : 'string'
                }, {
                    name : 'description',
                    type : 'string'
                }, {
                    name : 'type',
                    type : 'string'
                }, {
                    name : 'value',
                    type : 'string'
                }, {
                    name : 'valueType',
                    type : 'string'
                }, {
                    name : 'violation'
                }, { 
					name : "userUpdatable", 
					type : "boolean"
                }]
            }), 
            tbar : Ext.create('sitools.public.widget.grid.GridSorterToolbar', {
                gridId : "gridFieldMapping"
            }),
            selModel : Ext.create('Ext.selection.RowModel'),
            bbar : Ext.create('Ext.ux.StatusBar', {
                id: 'statusBar',
                hidden : true,
                iconCls: 'x-status-error',
                text : i18n.get("label.resourcesPluginErrorValidationNotification")
            }),
            columns : [{
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 100
            }/*, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 100,
                sortable : false
            }*/, {
                header : i18n.get('label.type'),
                dataIndex : 'type',
                width : 150
            }, {
                header : i18n.get('label.value'),
                dataIndex : 'value',
                width : 230,
                editable : true,
                editor : {
                    xtype : 'textfield'
                }
            }, Ext.create("sitools.admin.resourcesPlugins.CustomCheckColumn", {
                header : i18n.get('headers.userUpdatable'),
                dataIndex : 'userUpdatable',
                width : 55                
            })],
            listeners : {
                scope : this,
                cellclick : function (grid, td, cellIndex, record, tr, rowIndex, e) {
                    var rec = record.data;
                    if (cellIndex == 3) {
                        if (rec.valueType == "xs:dataset.columnAlias") {
                            var selectColumnWin = Ext.create("sitools.admin.datasets.SelectColumn", {
                                field : "value",
                                record : record,
                                parentStore : this.gridFieldMapping.getStore(),
                                parentView : this.gridFieldMapping.getView(),
                                url : loadUrl.get("APP_URL") + loadUrl.get("APP_DATASETS_URL") + "/" + this.idParent
                            });
                            selectColumnWin.show(this);
                        }
                        else if (rec.valueType == "xs:dictionary") {
                            var selectDictionaryWin = Ext.create("sitools.admin.dictionary.SelectDictionary", {
                                field : "value",
                                record : record,
                                parentStore : this.gridFieldMapping.getStore(),
                                parentView : this.gridFieldMapping.getView(),
                                url : loadUrl.get("APP_URL") + loadUrl.get("APP_DICTIONARIES_URL")
                            });
                            selectDictionaryWin.show(this);
                        }
                        else if (rec.valueType == "xs:boolean") {
                            var selectBooleanWin = Ext.create("sitools.admin.resourcesPlugins.EnumerationValueTypeSelector", {
                                field : "value",
                                record : record,
                                parentView : this.gridFieldMapping.getView(),
                                type : rec.name,
                                enumeration : "[true,false]",
                                value : rec.value
                            });
                            selectBooleanWin.show(this);
                        } 
                        else if (rec.valueType == "xs:image") {
                            
                            var callback = function (data, config) {
                                config.record.data[config.field] = data.url;
                                config.parentView.refresh();                                
                            };
                            
                            var chooser = new ImageChooser({
                                url : loadUrl.get('APP_URL') + loadUrl.get('APP_UPLOAD_URL') + '/?media=json',
                                width : 515,
                                height : 450,
                                field : "value",
                                parentView : this.gridFieldMapping.getView(),
                                record : record,
                                callback : callback
                            });
                            chooser.show(this);
                        }
                        else if (rec.valueType.indexOf("xs:enum") != -1) {
                            var enumType;
                            if (rec.valueType.indexOf("xs:enum-editable-multiple") != -1) {
								enumType = "EEM";
                            }
                            else if (rec.valueType.indexOf("xs:enum-editable") != -1) {
								enumType = "EE";
                            }
                            else if (rec.valueType.indexOf("xs:enum-multiple") != -1) {
								enumType = "EM";
                            }
                            else {
								enumType = "E";
                            }
                            var selectEnumWin = Ext.create("sitools.admin.resourcesPlugins.EnumerationValueTypeSelector", {
                                enumType : enumType, 
                                field : "value",
                                fieldEnum : "valueType", 
                                record : record,
                                parentView : this.gridFieldMapping.getView(),
                                type : rec.name,
                                enumeration : rec.valueType,
                                value : rec.value
                            });
                            selectEnumWin.show(this);
                        } 
                    } else {
                        return false;
                    }
                }, 
                viewready : function (grid) {
					this.showOptions(grid);
				}
            }, 
            plugins : [expanderGridFieldMapping, cellEditing]
        });
        
        var comboBehavior = Ext.create("Ext.form.ComboBox", {
		    typeAhead : false,
			fieldLabel : i18n.get("label.behavior"), 
            name : "behavior", 
            triggerAction : 'all',
		    queryMode : 'local',
		    anchor : "100%", 
		    store : new Ext.data.ArrayStore({
		        id : 0,
		        fields : [ 'myId', 'displayText' ],
		        data : [ 
					[ 'DISPLAY_IN_NEW_TAB', i18n.get('DISPLAY_IN_NEW_TAB') ],
					[ 'DISPLAY_IN_DESKTOP', i18n.get('DISPLAY_IN_DESKTOP') ]
		        ]
		    }),
		    valueField : 'myId',
		    displayField : 'displayText'
		
        });
        
        // set the search form
        this.fieldMappingFormPanel = Ext.create("Ext.FormPanel", {
            height : 95,
            frame : true,
            defaultType : 'textfield',
			items : [{
                fieldLabel : i18n.get('label.name'),
                name : 'name',
                anchor : '100%'
            }, {
                fieldLabel : i18n.get('label.descriptionAction'),
                name : 'descriptionAction',
                anchor : '100%'
            }, comboBehavior],
            region : 'north'
        });

        this.fieldMappingPanel = Ext.create("Ext.Panel", {
            layout : 'border',
            id : 'fieldMappingPanel',
            title : i18n.get('title.fieldMapping'),
            items : [ this.fieldMappingFormPanel, this.gridFieldMapping ], 
            listeners : {
				scope : this,
				activate : function () {
					this.showOptions(this.gridFieldMapping);
				}
            }
        });
        
        this.tabPanel = Ext.create("Ext.TabPanel", {
            height : 450,
            activeTab : 0,
            items : (this.action == "create") ? [ this.gridresourcePlugin, this.fieldMappingPanel ] : [
                this.fieldMappingPanel 
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
        
        this.listeners = {
            scope : this, 
            resize : function (window, width, height) {
                var size = window.body.getSize();
                this.tabPanel.setSize(size);
            }

        };  
        
        this.items = [ this.tabPanel ];

        sitools.admin.resourcesPlugins.ResourcesPluginsProp.superclass.initComponent.call(this);
    },

    /**
     * Notify the user if no resource plugin was selected 
     *  when he wants to change of tab
     */
    beforeTabChange : function (self, newTab, currentTab) {
        if (this.action == "create") {
            if (newTab.id == "fieldMappingPanel") {
                var rec =this.getLastSelectedRecord(this.gridresourcePlugin);
                if (!rec) {
                    new Ext.ux.Notification({
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
    
    /**
     * Load fields mapping form fields and parameters in fonction of the class clicked
     */
    onClassClick : function (self, rec, item, index, e, eOpts) {
        if (this.action == "create") {
            var className = rec.get("className");
            if (className != this.classChosen) {
                var url = this.urlResources + "/" + className;
                var store = this.gridFieldMapping.getStore();
                store.removeAll();
                Ext.Ajax.request({
                    url : url,
                    method : 'GET',
                    scope : this,
                    params : {
                        appClassName : this.appClassName,
                        parent : this.idParent
                    },
                    success : function (ret) {
                        var json = Ext.decode(ret.responseText);
                        if (!json.success) {
                            Ext.Msg.alert(i18n.get('label.warning'),
                                    json.message);
                            return false;
                        }
                        var resourcePlugin = json.resourcePlugin;
                        this.fillGridAndForm(resourcePlugin, this.action);
                        this.classChosen = className;
                    }
                });

            }
//            this.fillGridAndForm(rec.data, this.action);
        }

    },
    
    /**
     * If "action" is "modify", load data from record into the form else load empty form
     */
    afterRender : function () {
        sitools.admin.resourcesPlugins.ResourcesPluginsProp.superclass.afterRender.apply(this, arguments);

        if (this.action == "modify") {
            this.fillGridAndForm(this.record.data, this.action);
        }
    },

    /**
     * Fill the grid and form with data from resourcePlugin
     * 
     * @param resourcePlugin, the resource to fill the form with
     * @param action, the mode (create or modify)
     */
    fillGridAndForm : function (resourcePlugin, action) {
        if (!Ext.isEmpty(resourcePlugin)) {
            
            var rec = {};
            var form = this.fieldMappingFormPanel.getForm();
            rec.name = resourcePlugin.name;
            rec.descriptionAction = resourcePlugin.descriptionAction;
            rec.id = resourcePlugin.id;
            rec.resourceClassName = resourcePlugin.resourceClassName;
            rec.behavior = resourcePlugin.behavior;
            form.setValues(rec);
            
            var parameters = resourcePlugin.parameters;
            var store = this.gridFieldMapping.getStore();
            store.removeAll();
            if (!Ext.isEmpty(parameters)) {
                for (var i = 0; i < parameters.length; i++) {
                    var recTmp = parameters[i];
                    if (action == "create" && Ext.isEmpty(parameters[i].value)) {
                        recTmp.value = "";
                    }
                    store.add(recTmp);
                }
            }
//            store.sort('name', 'ASC');
        }
    },

    /**
     * Save the resource plugin properties
     */
    onValidate : function () {
        
        var rec;
        var jsonReturn = {};        
        
        var parameters = [];
        var resourcePlugin;
        if (this.action == "create") {
            rec = this.getLastSelectedRecord(this.gridresourcePlugin);
            if (!rec) {
                new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('warning.noselection'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);
                return false;
            }
            resourcePlugin = rec.data;
        } else {
            resourcePlugin = this.record.data;
            jsonReturn.id = this.record.data.id;
        }
        
        jsonReturn.classVersion = resourcePlugin.classVersion;
        jsonReturn.classAuthor = resourcePlugin.classAuthor;
        jsonReturn.classOwner = resourcePlugin.classOwner;
        jsonReturn.description = resourcePlugin.description;
        jsonReturn.className = resourcePlugin.className;
        
        jsonReturn.resourceClassName = resourcePlugin.resourceClassName;
        jsonReturn.parent = resourcePlugin.parent;
        
        jsonReturn.applicationClassName = resourcePlugin.applicationClassName;
        
        if (!Ext.isEmpty(resourcePlugin.dataSetSelection)) {
            jsonReturn.dataSetSelection = resourcePlugin.dataSetSelection;
        }
            
        var form = this.fieldMappingFormPanel.getForm();
        if (!this.fieldMappingFormPanel.rendered || !form.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }

        Ext.iterate(form.getValues(), function (key, value) {
            if (!Ext.isEmpty(value)) {
                jsonReturn[key] = value;    
            }
        }); 
        
        var storeField = this.gridFieldMapping.getStore();
        
        var re1 = new RegExp("^/.*$");
        var re2 = new RegExp("^.*//.*$");
        var re3 = new RegExp("^.*[!\"#$%&\'()*+,:;<=>?@\\`|~]+.*$");
        
        for (var i = 0; i < storeField.getCount(); i++) {
            var recTmp = storeField.getAt(i).data;
            recTmp.violation = undefined;
            if (recTmp.type == "PARAMETER_ATTACHMENT") {
				var attach = recTmp.value;
				var ok = re1.test(attach) && !re2.test(attach) && !re3.test(attach);
				if (!ok) {
					return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.invalidAttachment') + " : " + attach);
				}
			}     
            parameters.push(recTmp);
        }
        
        jsonReturn.parameters = parameters;

        var url = this.urlResourcesCRUD, method;
        if (this.action == "modify") {
            url += "/" + jsonReturn.id;
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
                    if (Ext.isEmpty(data.message)) {
                        var violations = data.data;
                        this.notifyViolations(violations);
                        Ext.getCmp("statusBar").show();
                        
                    } else {
                        Ext.Msg.alert(i18n.get('label.warning'),
                                data.message);
                    }
                    return false;
                }

                popupMessage("", i18n.get('label.resourcePlugin' + this.parentType + 'Saved'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
                
                this.parentPanel.getStore().reload();
                this.close();

            },
            failure : alertFailure
        });

    },
    
    /**
     * Notify the user there are violations error
     * @param violations
     */
    notifyViolations : function (violations) {

        for (var i = 0; i < violations.length; i++) {
            var violation = violations[i];
            var store = this.gridFieldMapping.getStore();
            var lineNb = store.findExact("name", violation.valueName);
            var rec = store.getAt(lineNb);
            rec.set("violation", violation);
        }
        this.gridFieldMapping.getView().refresh();
    },

    onClose : function () {

    },
    
    /**
     * Show parameters of a specific application resource 
     *  except parameter of "PARAMETER_USER_INPUT" type
     * 
     * @param grid, the mapping grid
     */
    showOptions : function (grid) {
//		var toShow, rec;
//		var colIndex = this.getColumnIndex(grid, "userUpdatable");
//		for (var i = 0; i < grid.getStore().getCount(); i++) {
//			rec = grid.getStore().getAt(i);
//			toShow = rec.get("type") == "PARAMETER_USER_INPUT";
//			if (!toShow) {
//			    var view = grid.getView();
//					view.getCell(i, colIndex).innerHTML = " ";
//			}
//		}
    },
    
    getColumnIndex : function (grid, colName) {
        return Ext.each(grid.columns, function(column) {
            if(column.text == colName){
                return false;
            }            
        });
    }

});


