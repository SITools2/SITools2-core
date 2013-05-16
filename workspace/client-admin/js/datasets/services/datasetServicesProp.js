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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, ImageChooser, loadUrl, includeJsForceOrder*/
/*
 * @include "../datasets/selectColumn.js"
 */
Ext.namespace('sitools.admin.datasets.services');
/**
 * A window of a resource plugin properties
 * 
 * @param action
 *            create or modify
 * @param parentPanel
 *            the parent panel
 * @param appClassName
 *            the parent className
 * @param urlAllServicesIHM
 *            the url of all services ihm
 * @param urlDatasetServiceIHM
 *            the url of the service ihm
 * @param idParent
 *            the parent id
 * @param parentType
 *            the type of the parent, string used only for i18n label
 * @class sitools.admin.datasets.services.datasetServicesProp
 * @extends Ext.Window
 */
sitools.admin.datasets.services.datasetServicesProp = Ext.extend(Ext.Window, {
    width : 700,
    height : 480,
    modal : true,
    resizable : true,
    classChosen : "",
    datasetServiceIHMId : null,
    modelClassName : null,
    currentRecordId : null,
    initComponent : function () {

        this.guiServiceDatasetURL = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_URL') + '/' + this.idParent + "/services";

        this.title = this.action === "create" ? i18n.get('label.create' + this.parentType + 'Resource') : i18n.get('label.modify' + this.parentType + 'Resource'); 

        var expander = new Ext.ux.grid.RowExpander({
            tpl : new Ext.XTemplate(
                '<tpl>' +
	                '<div class="detail">' +
	                    '<span style="font-weight:bold;">Author :&nbsp;</span>{author}' +
	                '</div>' +
                '</tpl>',
                {
                    compiled : true,
                    descEmpty : function (description) {
                        return Ext.isEmpty(description);
                    }
                }),
            expandOnDblClick : true
        });
        
        this.gridDatasetServices = new Ext.grid.GridPanel({
            viewConfig : {
                forceFit : true
            },
            layout : "fit",
            id : 'gridDatasetServices',
            title : i18n.get('title.datasetServiceIHM'),
            store : new Ext.data.JsonStore({
                root : 'data',
                restful : true,
                proxy : new Ext.data.HttpProxy({
                    url : this.urlAllServicesIHM,
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
                    name : 'label',
                    type : 'string'
                }, {
                    name : 'xtype',
                    type : 'string'
                }, {
                    name : 'author',
                    type : 'string'
                }, {
                    name : 'version',
                    type : 'string'
                }, {
                    name : 'icon',
                    type : 'string'
                }, {
                    name : 'parameters'                    
                }, {
                    name : 'priority',
                    type : 'int'
                }, {
                    name : 'dataSetSelection'
                }, {
                    name : 'dependencies'
                } ],
                listeners :  {
                    load : function (store, records) {
                        this.gridDatasetServices.getSelectionModel().lock();
                        this.loadDependencies(records, function () {
                            this.gridDatasetServices.getSelectionModel().unlock();
                        }, this);
                                              
                    },
                    scope : this
                }
            }),

            cm : new Ext.grid.ColumnModel({
                defaults : {
                    sortable : true
                },
                columns : [ expander, {
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
                    header : i18n.get('label.xtype'),
                    dataIndex : 'xtype',
                    width : 300,
                    sortable : true
                }, {
                    header : i18n.get('label.version'),
                    dataIndex : 'version',
                    width : 200,
                    sortable : false
                } ]
            }),
            plugins : expander            
        });

        var comboSelectionType = new Ext.form.Hidden({
            name : "dataSetSelection"
        });
        

        this.centerPanel = new Ext.Panel({
            layout : 'fit',
            region : 'center'
        });
        
        this.fieldMappingFormPanel = new Ext.FormPanel({
            height : 115,
            frame : true,
            region : 'north',
            defaultType : 'textfield',
			items : [{
                fieldLabel : i18n.get('label.name'),
                name : 'name',
                anchor : '100%'
            }, {
                fieldLabel : i18n.get('label.descriptionAction'),
                name : 'descriptionAction',
                anchor : '100%'
            }, {
                xtype : 'sitoolsSelectImage',
                name : 'icon',
                fieldLabel : i18n.get('label.icon'),
                anchor : '100%', 
                allowBlank : true
            }, comboSelectionType]
        });

        this.dsFieldParametersPanel = new Ext.Panel({
            layout : 'border',
            id : 'dsFieldParametersPanel',
            title : i18n.get('title.formFieldParameters'),
            items : [ this.fieldMappingFormPanel, this.centerPanel ]
        });
        
        this.tabPanel = new Ext.TabPanel({
            height : 450,
            activeTab : 0,
            items : (this.action === "create") ? [ this.gridDatasetServices, this.dsFieldParametersPanel ] : [
                this.dsFieldParametersPanel 
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

        sitools.admin.datasets.services.datasetServicesProp.superclass.initComponent.call(this);
    },

    /**
     * Notify the user if no resource plugin was selected 
     *  when he wants to change of tab
     */
    beforeTabChange : function (self, newTab, currentTab) {
        if (this.action === "create") {
            if (newTab.id === "dsFieldParametersPanel") {
                var rec = this.gridDatasetServices.getSelectionModel().getSelected();
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
                

                if (rec.id === this.currentRecordId) {
                    return;
                }
                this.currentRecordId = rec.id;
                
                this.centerPanel.remove(this.formParametersPanel);
                

                this.formParametersPanel = new sitools.admin.common.FormParametersConfigUtil({
                    rec : rec.data
                });
                
                this.centerPanel.add(this.formParametersPanel);
                this.centerPanel.doLayout();
                
                var form = this.fieldMappingFormPanel.getForm();
                form.findField('name').setValue(rec.data.name);
                form.findField('descriptionAction').setValue(rec.data.descriptionAction);
                form.findField('icon').setValue(rec.data.icon);
                
                var comboSelection = form.findField('dataSetSelection');
                comboSelection.setValue(rec.data.dataSetSelection);
            }
        }
    },
    
    /**
     * If "action" is "modify", load data from record into the form else load empty form
     */
    afterRender : function () {
        if (this.action === "modify") {
            this.gridDatasetServices.getStore().load({
                callback : function () {
                    this.formParametersPanel = new sitools.admin.common.FormParametersConfigUtil({
                        rec : this.record,
                        parametersList : this.record.parameters
                    });
                    this.centerPanel.add(this.formParametersPanel);
                    this.centerPanel.doLayout();
                    this.fillGridAndForm(this.record, this.action);
                },
                scope : this
            });
        } else {
            this.gridDatasetServices.getStore().load();
        }
        
        sitools.admin.datasets.services.datasetServicesProp.superclass.afterRender.apply(this, arguments);
    },

    /**
     * Save the dataset IHM service properties
     */
    onValidate : function () {
        
        if (this.tabPanel.getActiveTab().id === 'gridDatasetServices') {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.checkForm'));
            this.tabPanel.setActiveTab(1);
            return false;
        }
        
        var rec, datasetServiceIhm = {};
        
        if (this.action === "create") {
            rec = this.gridDatasetServices.getSelectionModel().getSelected();
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
            rec = rec.data;
        } else {
            rec = this.record;
        }
        
        Ext.apply(datasetServiceIhm, rec);
        
        if (Ext.isEmpty(this.formParametersPanel) || !this.formParametersPanel.getForm().isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }
        
        var form = this.fieldMappingFormPanel.getForm();
        
        if (!form.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }

        Ext.iterate(form.getValues(), function (key, value) {
            datasetServiceIhm[key] = value;    
        });
        
        datasetServiceIhm.parameters = [];
        

        Ext.iterate(this.formParametersPanel.getParametersValue(), function (item, ind, all) {
            datasetServiceIhm.parameters.push(item);
        });
        Ext.destroyMembers(datasetServiceIhm, 'dependencies');
        
        var method, url;
        if (this.action === "modify") {
            url = this.urlDatasetServiceIHM.replace('{idService}', datasetServiceIhm.id);
            method = "PUT";
        } else {
            // Destroy gui service existing id, delegate the generation of the
            // id to the server
            Ext.destroyMembers(datasetServiceIhm, 'id');
            url = '/sitools/datasets/' + this.idParent + '/services/gui';
            method = "POST";
        }

        Ext.Ajax.request({
            url : url,
            method : method,
            scope : this,
            jsonData : datasetServiceIhm,
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

                new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('label.datasetServiceIHMSaved'),
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
            var store = this.formParametersPanel.getStore();
            var lineNb = store.findExact("name", violation.valueName);
            var rec = store.getAt(lineNb);
            rec.set("violation", violation);
        }
    },

    /**
     * Fill the grid and form with data from datasetServiceIHM
     * 
     * @param datasetServiceIHM, the resource to fill the form with
     * @param action, the mode (create or modify)
     */
    fillGridAndForm : function (datasetServiceIHM, action) {
        if (!Ext.isEmpty(datasetServiceIHM)) {
            var form = this.fieldMappingFormPanel.getForm();
            
            form.findField('name').setValue(datasetServiceIHM.name);
            form.findField('descriptionAction').setValue(datasetServiceIHM.descriptionAction);
            form.findField('icon').setValue(datasetServiceIHM.icon);
            form.findField('dataSetSelection').setValue(datasetServiceIHM.dataSetSelection);
        }
    },
    /**
     * Load the dependencies of the given records
     * 
     * @param value
     *            (Mixed) an array of records or a single record object
     * @param callback
     *            (function) the callback to call at the end
     * @param scope
     *            (Object) the scope for the callback function
     */
    loadDependencies : function (value, callback, scope) {
        var records = value;
        if (!Ext.isArray(records)) {
            records = [ value ];
        }
        var listDependencies = [];
        Ext.each(records, function (rec) {
            if (!Ext.isEmpty(rec.get('dependencies') && !Ext.isEmpty(rec.get('dependencies').js))) {
                listDependencies = listDependencies.concat(rec.get('dependencies').js);
            }
        }, this);
        
        if (!Ext.isEmpty(listDependencies)) {
            includeJsForceOrder(listDependencies, 0, callback, scope);
        }
    }
});


