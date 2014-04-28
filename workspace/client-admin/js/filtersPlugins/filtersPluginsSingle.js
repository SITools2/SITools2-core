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
Ext.namespace('sitools.component.filtersPlugins');
/**
 * @param action
 *            create or modify
 * @param parentPanel
 *            the parent panel
 * @param urlfilters
 *            the filters url
 * @param urlParent
 *            the parent object url
 * @param parentType
 *            the type of the parent, string used only for i18n label
 * @class sitools.component.filtersPlugins.filtersPluginsProp
 * @extends Ext.Window
 */
Ext.define('sitools.component.filtersPlugins.filtersPluginsSingle', { 
    extend : 'Ext.Window',
    width : 700,
    height : 500,
    modal : true,
    resizable : true,
    classChosen : "",
    filterPluginId : null,
    modelClassName : null,

    initComponent : function () {

        this.title = this.action == "create" ? i18n.get('label.create' + this.parentType + 'Filter') : i18n.get('label.modify' + this.parentType + 'Filter'); 

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
        
        this.gridfilterPlugin = new Ext.grid.GridPanel({
            forceFit : true,
            id : 'gridfilterPlugin',
            title : i18n.get('title.filterPlugin' + this.parentType + 'Class'),
            store : new Ext.data.JsonStore({
                root : 'data',
                restful : true,
                proxy : new Ext.data.HttpProxy({
                    url : this.urlFilters,
                    restful : true,
                    method : 'GET'
                }),
                
                remoteSort : false,
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
                    name : 'filterClassName',
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
                }
                ]
            }),
            
            columns : [{
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 100,
                sortable : true
            }, {
                header : i18n.get('label.filterClassName'),
                dataIndex : 'filterClassName',
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
                rowclick :  this.onClassClick
            }, 
            plugins : expander
        });

      
        this.proxyFieldMapping = new Ext.data.HttpProxy({
            url : "/tmp",
            restful : true,
            method : 'GET'
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
                    }
                }
            },
            id : 'gridFieldMapping',
            layout : 'fit',
            region : 'center',
            title : i18n.get('title.parametersMapping'),
            store : new Ext.data.JsonStore({
                root : 'filterPlugin.parameters',
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
                }]
            }), 
            bbar : Ext.create('Ext.ux.StatusBar', {
                id: 'statusBar',
                hidden : true,
                iconCls: 'x-status-error',
                text : i18n.get("label.filtersPluginErrorValidationNotification")
            }),
            columns : [{
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 100,
                sortable : true
            }, {
                header : i18n.get('label.type'),
                dataIndex : 'type',
                width : 150,
                sortable : false
            }, {
                header : i18n.get('label.value') + '<img title="Editable" height=14 widht=14 src="/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/toolbar_edit.png"/>',
                dataIndex : 'value',
                width : 230,
                sortable : false,
                editable : true,
                editor : {
                    xtype : 'textfield'
                }
            }],
            plugins : [expanderGridFieldMapping, cellEditing]
        });
        
        
        // set the search form
        this.fieldMappingFormPanel = new Ext.FormPanel({
            height : 65,
//            frame : true,
            defaultType : 'textfield',
            items : [{
                fieldLabel : i18n.get('label.name'),
                name : 'name',
                anchor : '100%'
            }, {
                fieldLabel : i18n.get('label.descriptionAction'),
                name : 'descriptionAction',
                anchor : '100%'
            } ],
            region : 'north'
        });

        this.fieldMappingPanel = new Ext.Panel({
            layout : 'border',
            id : 'fieldMappingPanel',
            title : i18n.get('title.fieldMapping'),
            items : [ this.fieldMappingFormPanel, this.gridFieldMapping ]
        });
        
        this.tabPanel = new Ext.TabPanel({
            activeTab : 0,
            items : (this.action == "create") ? [ this.gridfilterPlugin, this.fieldMappingPanel ] : [
                this.fieldMappingPanel 
            ],
            buttons : [ {
                text : this.action == "editDelete" ? i18n.get('label.ok') : i18n.get('label.add'),
                scope : this,
                handler : this.onValidate
            }, {
                text : i18n.get('label.delete'),
                scope : this,
                handler : this.onDelete, 
                hidden : this.action == "create"
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

        sitools.component.filtersPlugins.filtersPluginsSingle.superclass.initComponent.call(this);
    },

    beforeTabChange : function (self, newTab, currentTab) {
        if (this.action == "create") {
            if (newTab.id == "fieldMappingPanel") {
                var rec = this.gridfilterPlugin.getSelectionModel().getSelected();
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
    onClassClick : function (self, rowIndex, e) {
        if (this.action == "create") {
            var rec = this.gridfilterPlugin.getSelectionModel().getSelected();
            if (!rec) {
                return false;
            }
            
            this.fillGridAndForm(rec.data, this.action);
        }

    },

    afterRender : function () {
        sitools.component.filtersPlugins.filtersPluginsSingle.superclass.afterRender.apply(this, arguments);

        if (this.action == "editDelete") {
            this.fillGridAndForm(this.filterPlugin, this.action);
        } else {
            //only need to load the filtersPlugins for creation
            this.gridfilterPlugin.getStore().load();
        }
        this.tabPanel.setHeight(this.body.getHeight());
    },
    fillGridAndForm : function (filterPlugin, action) {
        if (!Ext.isEmpty(filterPlugin)) {
            
            var rec = {};
            var form = this.fieldMappingFormPanel.getForm();
            rec.name = filterPlugin.name;
            rec.descriptionAction = filterPlugin.descriptionAction;
            rec.id = filterPlugin.id;
            rec.filterClassName = filterPlugin.filterClassName;
            form.setValues(rec);
            
            var parameters = filterPlugin.parameters;
            var store = this.gridFieldMapping.getStore();
            store.removeAll();
            if (!Ext.isEmpty(parameters)) {
                for (var i = 0; i < parameters.length; i++) {
                    var recTmp = parameters[i];
                    if (action == "editDelete" && Ext.isEmpty(parameters[i].value)) {
                        recTmp.value = "";
                    }
                    store.add(recTmp);
                }
            }
        }
    },

    onValidate : function () {
        
        var rec;
        var jsonReturn = {};        
        
        var parameters = [];
        var filterPlugin;
        if (this.action == "create") {
            rec = this.gridfilterPlugin.getSelectionModel().getSelected();
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
            filterPlugin = rec.data;
        } else {
            filterPlugin = this.filterPlugin;
            jsonReturn.id = this.filterPlugin.id;
        }
        
        jsonReturn.classVersion = filterPlugin.classVersion;
        jsonReturn.classAuthor = filterPlugin.classAuthor;
        jsonReturn.description = filterPlugin.description;
        jsonReturn.filterClassName = filterPlugin.filterClassName;
        jsonReturn.parent = filterPlugin.parent;
        jsonReturn.className = filterPlugin.className;
        jsonReturn.classOwner = filterPlugin.classOwner;
            
        var form = this.fieldMappingFormPanel.getForm();
        Ext.iterate(form.getValues(), function (key, value) {
            if (!Ext.isEmpty(value)) {
                jsonReturn[key] = value;    
            }
        }); 
        
        var storeField = this.gridFieldMapping.getStore();
        
        var re1 = new RegExp("^/.*$");
        var re2 = new RegExp("^.*//.*$");
        var re3 = new RegExp("^.*[!\"#$%&\'()*+,:;<=>?@\\`{}|~]+.*$");
        
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

        var url = this.urlParent, method;
        if (this.action == "editDelete") {
            url += "/filter";
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
                    html : i18n.get('label.filterPlugin' + this.parentType + 'Saved'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);

                this.parentPanel.getStore().reload();
                this.close();

            },
            failure : alertFailure
        });

    },
    
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

    onDelete : function () {

        var tot = Ext.Msg.show({
            title : i18n.get('label.delete'),
            buttons : Ext.Msg.YESNO,
            msg : i18n.get('filtersPlugins' + this.parentType + 'Crud.delete'),
            scope : this,
            fn : function (btn, text) {
                if (btn == 'yes') {
                    this.doDelete();
                }
            }

        });
    },

    doDelete : function (rec) {
        Ext.Ajax.request({
            url : this.urlParent,
            method : 'DELETE',
            scope : this,
            success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (showResponse(ret)) {
                    this.close();
                }
                
            },
            failure : alertFailure
        });
        
    }

});
