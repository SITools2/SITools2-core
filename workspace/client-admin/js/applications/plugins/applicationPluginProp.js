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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, ImageChooser*/
Ext.namespace('sitools.admin.applications.plugins');

/**
 * A Window to define application plugin properties.
 * @cfg {string} action Should be create or modify
 * @cfg {} parent The componennt caller 
 * @cfg {string} urlList the url of application plugins 
 * @cfg {string} urlAdmin 
 * @class sitools.admin.applications.plugins.applicationPluginProp
 * @extends Ext.Window
 */
Ext.define('sitools.admin.applications.plugins.applicationPluginProp', { 
    extend : 'Ext.Window',
    width : 700,
    height : 480,
    modal : true,
    resizable : true,
    classChosen : "",
    applicationPluginId : null,
    mixins : {
        utils : "sitools.admin.utils.utils"
    },
    
    requires : ['sitools.admin.dictionary.selectDictionary',
                'sitools.admin.resourcesPlugins.enumerationValueTypeSelector'],

    initComponent : function () {

		this.title = this.action == "create" ? i18n.get('label.createApplicationPlugin') : i18n.get('label.modifyApplicationPlugin'); 

		this.crudStore = this.store;
        
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
        
        this.gridapplicationPlugin = new Ext.grid.GridPanel({
            forceFit : true,
            id : 'gridapplicationPlugin',
            title : i18n.get('title.applicationPluginClass'),
            store : Ext.create("Ext.data.JsonStore", {
                autoLoad : true,
                proxy : {
                    url : this.urlList,
                    type : "ajax",
                    reader : {
                        type : 'json',
                        root : 'data',
                        idProperty : 'id'
                    }
                },
                remoteSort : false,
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
                    name : 'className',
                    type : 'string'
                }, {
                    name : 'classAuthor',
                    type : 'string'
                }, {
                    name : 'classOwner',
                    type : 'string'
                }, {
                    name : 'classVersion',
                    type : 'string'
                }, {
                    name : 'classOwner',
                    type : 'string'
                }, {
                    name : 'violation'
                }]
            }),
            columns : [{
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 100,
                sortable : true
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 300,
                sortable : true
            }, {
                header : i18n.get('label.className'),
                dataIndex : 'className',
                width : 300,
                sortable : true
            }, {
                header : i18n.get('label.author'),
                dataIndex : 'classAuthor',
                width : 100,
                sortable : true
            }, {
                header : i18n.get('label.classOwner'),
                dataIndex : 'classOwner',
                width : 100,
                sortable : false
            }, {
                header : i18n.get('label.version'),
                dataIndex : 'classVersion',
                width : 100,
                sortable : true
            }],
            listeners : {
                scope : this,
                itemclick :  this.onClassClick
            }, 
            plugins : [expander]

        });

        this.fieldMappingFormPanel = Ext.create('Ext.form.Panel', {
            padding : 10,
            id : 'fieldMappingFormPanel',
            defaultType : 'textfield',
            title : i18n.get('title.applicationPluginDetails'),
            items : [ {
                fieldLabel : i18n.get('label.label'),
                name : 'label',
                anchor : '100%'
            }, {
                fieldLabel : i18n.get('label.userAttach'),
                name : 'urlAttach',
                anchor : '100%',
                vtype : "attachment",
                allowBlank: false
            } ]
        });

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
                    }
                }
            },
            id : 'gridFieldMapping',
            layout : 'fit',
            title : i18n.get('title.parametersMapping'),
            store : new Ext.data.JsonStore({
                remoteSort : false,
                fields : [ {
                    name : 'name',
                    type : 'string'
                }, {
                    name : 'description',
                    type : 'string'
                }, {
                    name : 'value',
                    type : 'string'
                }, {
                    name : 'valueType',
                    type : 'string'
                } ]
            }), 
            bbar : Ext.create('Ext.ux.StatusBar', {
                id: 'statusBar',
                hidden : true,
                iconCls: 'x-status-error',
                text : i18n.get("label.applicationPluginErrorValidationNotification")
            }),
            columns : [{
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 100,
                sortable : true
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 200,
                sortable : false
            }, {
                header : i18n.get('label.value') + ' <img title="Editable" height=14 widht=14 src="/sitools'+ loadUrl.get('APP_CLIENT_PUBLIC_URL') +'/res/images/icons/toolbar_edit.png"/>',
                dataIndex : 'value',
                width : 100,
                sortable : false,
                editable : true,
                editor : {
                    xtype : 'textfield'
                }
            }],
            listeners : {
                scope : this,
                celldblclick : function (view, td, cellIndex, storeRecord, tr, rowIndex) {
                    var rec = storeRecord.data;
                    
                    if (this.gridFieldMapping.columns[cellIndex].text == "Value") {
                        if (rec.valueType == "xs:dictionary") {
                            var selectDictionaryWin = new sitools.admin.dictionary.selectDictionary({
                                field : "value",
                                record : storeRecord,
                                parentStore : this.gridFieldMapping.getStore(),
                                parentView : this.gridFieldMapping.getView(),
                                url : loadUrl.get("APP_URL") + loadUrl.get("APP_DICTIONARIES_URL")
                            });
                            selectDictionaryWin.show(ID.BOX.DATASETS);
                        }
                        else if (rec.valueType == "xs:boolean") {
                            var selectBooleanWin = Ext.create("sitools.admin.resourcesPlugins.enumerationValueTypeSelector",{
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
                                record : storeRecord,
                                callback : callback
                            });
                            chooser.show(document);
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
                            var selectEnumWin = Ext.create("sitools.admin.resourcesPlugins.enumerationValueTypeSelector", {
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
                        
                        Ext.defer(function () {
                            Ext.WindowManager.sendToBack(this);
                        }, 100, this);
                    } else {
                        return false;
                    }
                }
            },
            plugins : [cellEditing]
        });

        this.tabPanel = new Ext.TabPanel({
            activeTab : 0,
            items : (this.action == "create") ? [ this.gridapplicationPlugin, this.fieldMappingFormPanel, this.gridFieldMapping ] : [
				this.fieldMappingFormPanel, this.gridFieldMapping 
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

        sitools.admin.applications.plugins.applicationPluginProp.superclass.initComponent.call(this);
    },

    /**
     * Method called on main tabPanel beforeTabChange event 
     * @param {Ext.TabPanel} self main Tab Panel 
     * @param {Ext.Panel} newTab new Tab 
     * @param {Ext.Panel} currentTab current Tab
     * @return {Boolean}
     */
    beforeTabChange : function (self, newTab, currentTab) {
        if (this.action == "create") {
            if (newTab.id == "fieldMappingFormPanel" || newTab.id == "gridFieldMapping") {
                var rec = this.getLastSelectedRecord(this.gridapplicationPlugin);
                if (!rec) {
                    popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
//                    var tmp = new Ext.ux.Notification({
//                        iconCls : 'x-icon-information',
//                        title : i18n.get('label.information'),
//                        html : i18n.get('warning.noselection'),
//                        autoDestroy : true,
//                        hideDelay : 1000
//                    }).show(document);
                    return false;
                }
            }
        }

    },
    /**
     * Method called on grid click
     * @param {} self
     * @param {} rowIndex
     * @param {} e
     * @return {Boolean}
     */
    onClassClick : function (self, rec, item, index, e, eOpts) {
        if (this.action == "create") {
            var className = rec.data.className;
			if (className != this.classChosen) {
			    var store = this.gridFieldMapping.getStore();
				store.setProxy({
				    type : 'ajax',
				    url : this.urlList + "/" + className,
				    reader: {
				        type : "json",
				        root : 'ApplicationPlugin.model.parameters',
				        idProperty : 'name',
				    }
				});
				this.classChosen = className;
				store.removeAll();
				store.load();
			}
		}

    },

    /**
     * Requests the selected plugin Application properties
     */
    afterRender : function () {
        sitools.admin.applications.plugins.applicationPluginProp.superclass.afterRender.apply(this, arguments);

        if (this.action == "modify") {
            this.applicationPluginId = this.record.data.id;
            Ext.Ajax.request({
                url : this.urlAdmin + "/" + this.applicationPluginId,
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var json = Ext.decode(ret.responseText);
                    if (!json.success) {
                        Ext.Msg.alert(i18n.get('label.warning'), json.message);
                        return false;
                    }
                    this.fillFormAndGrid(json.ApplicationPluginModel);
                },
                failure : alertFailure
            });
        }
    },
	/**
	 * Fill the properties from the requested object.
	 * @param {} applicationPlugin
	 */
    fillFormAndGrid : function (applicationPlugin) {
        var form = this.fieldMappingFormPanel.getForm();
        if (!Ext.isEmpty(applicationPlugin)) {
            var rec = {};
            rec.label = applicationPlugin.label;
            rec.urlAttach = applicationPlugin.urlAttach;
            
            form.setValues(rec);
            
            var parameters = applicationPlugin.parameters;
            if (!Ext.isEmpty(parameters)) {
                var store = this.gridFieldMapping.getStore();
                for (var i = 0; i < parameters.length; i++) {
                    var recTmp = parameters[i];
                    store.add(recTmp);
                }
            }
        }
    },

    /**
     * Called on save Button : Validate fields and send POST or PUT request depending on action 
     * @return {Boolean}
     */
    onValidate : function () {
        
        var rec;
        var jsonReturn = {};
        if (this.action == "create") {
            rec = this.getLastSelectedRecord(this.gridapplicationPlugin);
            if (!rec) {
                return popupMessage("", i18n.get('warning.noselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');
//                var tmp = new Ext.ux.Notification({
//                    iconCls : 'x-icon-information',
//                    title : i18n.get('label.information'),
//                    html : i18n.get('warning.noselection'),
//                    autoDestroy : true,
//                    hideDelay : 1000
//                }).show(document);
                return false;
            }
            jsonReturn.name = rec.data.name;
            jsonReturn.description = rec.data.description;            
            
        }
        var form = this.fieldMappingFormPanel.getForm();
        if (!form.isValid()) {
            var temp = new Ext.ux.Notification({
				iconCls : 'x-icon-information',
				title : i18n.get('label.information'),
				html : i18n.get('warning.invalidForm'),
				autoDestroy : true,
				hideDelay : 1000
			}).show(document);
			return false;
        }
        
        Ext.iterate(form.getFieldValues(), function (key, value) {
            jsonReturn[key] = value;

        });

        var parameters = [];
        var applicationPlugin;
        if (this.action == "create") {
            rec = this.getLastSelectedRecord(this.gridapplicationPlugin);
            applicationPlugin = rec.data;
            
//            jsonReturn.name = applicationPlugin.name;
//            jsonReturn.description = applicationPlugin.description;

        } else {
            applicationPlugin = this.record.data;
            jsonReturn.id = this.applicationPluginId;
//            jsonReturn.status = applicationPlugin.status;
            
        }
        
        jsonReturn.className = applicationPlugin.className;
        jsonReturn.classVersion = applicationPlugin.classVersion;
        jsonReturn.classAuthor = applicationPlugin.classAuthor;
        jsonReturn.classOwner = applicationPlugin.classOwner;
        

        var storeField = this.gridFieldMapping.getStore();

        for (var i = 0; i < storeField.getCount(); i++) {
            var recTmp = storeField.getAt(i).data;
            recTmp.violation = undefined;
            delete recTmp.id;
            parameters.push(recTmp);
        }
        
        jsonReturn.parameters = parameters;

        // var rec2 = new Ext.data.Record(jsonReturn);
        // if (this.action == "create") {
        // this.crudStore.add(rec2);
        //
        // } else {
        // var record = this.crudStore.getAt(this.index);
        // record.set("parameters", parameters);
        // record.set("descriptionAction", jsonReturn.descriptionAction);
        // }
        var url = this.urlAdmin, method;
        if (this.action == "modify") {
            url += "/" + this.applicationPluginId;
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
                    html : i18n.get('label.applicationPluginSaved'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);

                this.parent.getStore().reload();
                this.close();

            },
            failure : alertFailure
        });

    },
    /**
     * Notify violations. 
     * @param {Array} violations
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
    }
});
