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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, ImageChooser, loadUrl*/
/*
 * @include "../datasets/selectColumn.js"
 */
Ext.namespace('sitools.admin.resourcesPlugins');
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
 * @class sitools.admin.resourcesPlugins.resourcesPluginsProp
 * @extends Ext.Window
 */
//sitools.component.resourcesPlugins.resourcesPluginsProp = Ext.extend(Ext.Window, {
sitools.admin.resourcesPlugins.resourcesPluginsProp = Ext.extend(Ext.Window, {
    width : 700,
    height : 480,
    modal : true,
    resizable : true,
    classChosen : "",
    resourcePluginId : null,
    modelClassName : null,

    initComponent : function () {

        this.title = this.action == "create" ? i18n.get('label.create' + this.parentType + 'Resource') : i18n.get('label.modify' + this.parentType + 'Resource'); 

        var expander = new Ext.ux.grid.RowExpander({
            tpl : new Ext.XTemplate(
                '<tpl if="this.descEmpty(description)" ><div></div></tpl>',
                '<tpl if="this.descEmpty(description) == false" ><div class="sitoolsDescription"><div class="sitoolsDescriptionHeader">Description :&nbsp;</div><p class="sitoolsDescriptionText"> {description} </p></div></tpl>',
                {
                    compiled : true,
                    descEmpty : function (description) {
                        return Ext.isEmpty(description);
                    }
                }),
            expandOnDblClick : true
        });
        
        
        this.gridresourcePlugin = new Ext.grid.GridPanel({
            viewConfig : {
                forceFit : true
            },
            layout : "fit",
            id : 'gridresourcePlugin',
            title : i18n.get('title.resourcePlugin' + this.parentType + 'Class'),
            store : new Ext.data.JsonStore({
                root : 'data',
                restful : true,
                proxy : new Ext.data.HttpProxy({
                    url : this.urlResources,
                    restful : true,
                    method : 'GET'
                }),
                remoteSort : false,
                sortInfo : {
					field : "name", 
					direction : "ASC"
                }, 
                idProperty : 'id',
                fields : [ {
                    name : 'id',
                    type : 'string'
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

            cm : new Ext.grid.ColumnModel({
                // specify any defaults for each column
                defaults : {
                    sortable : true
                // columns are not sortable by default
                },
                columns : [ expander, {
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
                } ]
            }),
            
            listeners : {
                scope : this,
                rowclick :  this.onClassClick
            }, 
            plugins : expander

        });

        this.proxyFieldMapping = new Ext.data.HttpProxy({
            url : "/tmp",
            restful : true,
            method : 'GET'
        });

        
        var userUpdatable = new Ext.grid.CheckColumn({
	        header : i18n.get('headers.userUpdatable'),
	        dataIndex : 'userUpdatable',
	        width : 55
	    });
        
        var expanderGridFieldMapping = new sitools.widget.ViolationRowExpander({
            tpl : new Ext.XTemplate(
                '<tpl if="this.descEmpty(description)" ><div></div></tpl>',
                '<tpl if="this.descEmpty(description) == false" ><div class="sitoolsDescription"><div class="sitoolsDescriptionHeader">Description :&nbsp;</div><p class="sitoolsDescriptionText"> {description} </p></div></tpl>',
                {
                    compiled : true,
                    descEmpty : function (description) {
                        return Ext.isEmpty(description);
                    }
                }),
            expandOnDblClick : false           
        });

        this.gridFieldMapping = new Ext.grid.EditorGridPanel({
            viewConfig : {
                forceFit : true,
                scope : this,
                getRowClass : function (record, index, rowParams, store) {
                    var cls = ''; 
                    var violation = record.get("violation");
                    if (!Ext.isEmpty(violation)) {
                        if (violation.level == "CRITICAL") {
                            cls = "red-row";
                        } else if (violation.level == "WARNING") {
                            cls = "orange-row";
                        }
                    }
                    return cls;
                }, 
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
                                var htmlLineEl = view.getRow(index);
                                var el = Ext.get(htmlLineEl);
                                
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
            store : new Ext.data.JsonStore({
                root : 'resourcePlugin.parameters',
                proxy : this.proxyFieldMapping,
                restful : true,
                remoteSort : false,
                idProperty : 'name',
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
            tbar : {
                xtype : 'sitools.widget.GridSorterToolbar',
                defaults : {
                    scope : this
                }
            },
            sm : new Ext.grid.RowSelectionModel(),
            bbar : new Ext.ux.StatusBar({
                id: 'statusBar',
                hidden : true,
                iconCls: 'x-status-error',
                text : i18n.get("label.resourcesPluginErrorValidationNotification")
            }),
            cm : new Ext.grid.ColumnModel({
                // specify any defaults for each column
                defaults : {
                    sortable : false
                // columns are not sortable by default
                },
                columns : [expanderGridFieldMapping, {
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
                    editor : new Ext.form.TextField()
                }, userUpdatable ]
            }),
            listeners : {
                scope : this,
                celldblclick : function (grid, rowIndex, columnIndex, e) {
                    var storeRecord = grid.getStore().getAt(rowIndex);
                    var rec = storeRecord.data;
                    if (columnIndex == 3) {
                        if (rec.valueType == "xs:dataset.columnAlias") {
                            var selectColumnWin = new sitools.admin.datasets.selectColumn({
                                field : "value",
                                record : storeRecord,
                                parentStore : this.gridFieldMapping.getStore(),
                                parentView : this.gridFieldMapping.getView(),
                                url : loadUrl.get("APP_URL") + loadUrl.get("APP_DATASETS_URL") + "/" + this.idParent
                            });
                            selectColumnWin.show(ID.BOX.DATASETS);
                        }
                        else if (rec.valueType == "xs:dictionary") {
                            var selectDictionaryWin = new sitools.component.dictionary.selectDictionary({
                                field : "value",
                                record : storeRecord,
                                parentStore : this.gridFieldMapping.getStore(),
                                parentView : this.gridFieldMapping.getView(),
                                url : loadUrl.get("APP_URL") + loadUrl.get("APP_DICTIONARIES_URL")
                            });
                            selectDictionaryWin.show(ID.BOX.DATASETS);
                        }
                        else if (rec.valueType == "xs:boolean") {
                            var selectBooleanWin = new sitools.admin.resourcesPlugins.enumerationValueTypeSelector({
                                field : "value",
                                record : storeRecord,
                                parentView : this.gridFieldMapping.getView(),
                                type : rec.name,
                                enumeration : "[true,false]",
                                value : rec.value
                            });
                            selectBooleanWin.show(ID.BOX.DATASETS);
                        } 
                        else if (rec.valueType == "xs:image") {
                            
                            // console.dir (this);
                            var chooser = new ImageChooser({
                                url : loadUrl.get('APP_URL') + loadUrl.get('APP_UPLOAD_URL') + '/?media=json',
                                width : 515,
                                height : 450,
                                field : "value",
                                parentView : this.gridFieldMapping.getView(),
                                record : storeRecord
                            });
                            chooser.show(document, function (data, config) {
                                config.record.data[config.field] = data.url;
                                config.parentView.refresh();                                
                            });
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
                            var selectEnumWin = new sitools.admin.resourcesPlugins.enumerationValueTypeSelector({
                                enumType : enumType, 
                                field : "value",
                                fieldEnum : "valueType", 
                                record : storeRecord,
                                parentView : this.gridFieldMapping.getView(),
                                type : rec.name,
                                enumeration : rec.valueType,
                                value : rec.value
                            });
                            selectEnumWin.show(ID.BOX.DATASETS);
                        } 
                    } else {
                        return false;
                    }
                }, 
                viewready : function (grid) {
					this.showOptions(grid);
				}
            }, 
            plugins : [userUpdatable, expanderGridFieldMapping]
        });
        
        var comboBehavior = new Ext.form.ComboBox({
		    typeAhead : false,
			fieldLabel : i18n.get("label.behavior"), 
            name : "behavior", 
            triggerAction : 'all',
		    lazyRender : true,
		    mode : 'local',
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
        this.fieldMappingFormPanel = new Ext.FormPanel({
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

        this.fieldMappingPanel = new Ext.Panel({
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
        
        
        

        this.tabPanel = new Ext.TabPanel({
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

        sitools.admin.resourcesPlugins.resourcesPluginsProp.superclass.initComponent.call(this);
    },

    /**
     * Notify the user if no resource plugin was selected 
     *  when he wants to change of tab
     */
    beforeTabChange : function (self, newTab, currentTab) {
        if (this.action == "create") {
            if (newTab.id == "gridFieldMapping") {
                var rec = this.gridresourcePlugin.getSelectionModel().getSelected();
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
    
    /**
     * Load fields mapping form fields and parameters in fonction of the class clicked
     */
    onClassClick : function (self, rowIndex, e) {
        if (this.action == "create") {
            var rec = this.gridresourcePlugin.getSelectionModel().getSelected();
            if (!rec) {
                return false;
            }
            var className = rec.data.className;
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
        sitools.admin.resourcesPlugins.resourcesPluginsProp.superclass.afterRender.apply(this, arguments);

        if (this.action == "modify") {
            this.fillGridAndForm(this.record.data, this.action);
        } else {
            //only need to load the resourcesPlugins for creation
            this.gridresourcePlugin.getStore().load({
                params : {
                    appClassName : this.appClassName,
                    parent : this.idParent
                }
            });
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
            form.loadRecord(new Ext.data.Record(rec));
            
            var parameters = resourcePlugin.parameters;
            var store = this.gridFieldMapping.getStore();
            store.removeAll();
            if (!Ext.isEmpty(parameters)) {
                for (var i = 0; i < parameters.length; i++) {
                    var recTmp = new Ext.data.Record(parameters[i]);
                    if (action == "create" && Ext.isEmpty(parameters[i].value)) {
                        recTmp.set("value", "");
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
            rec = this.gridresourcePlugin.getSelectionModel().getSelected();
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

                var tmp = new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('label.resourcePlugin' + this.parentType + 'Saved'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);

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
		var toShow, rec;
		var colIndex = grid.getColumnModel().findColumnIndex("userUpdatable");
		for (var i = 0; i < grid.getStore().getCount(); i++) {
			rec = grid.getStore().getAt(i);
			toShow = rec.get("type") == "PARAMETER_USER_INPUT";
			if (!toShow) {
				try {
					grid.getView().getCell(i, colIndex).innerHTML = " ";
				}
				catch (err) {
					return;
				}
			}
		}
    }

});


