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
 * 			the url of all services ihm
 * @param urlDatasetServiceIHM
 * 			the url of the service ihm
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
    resourcePluginId : null,
    modelClassName : null,

    initComponent : function () {

    	this.guiServiceDatasetURL = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_URL') + '/' + this.idParent + "/services";
    	
        this.title = this.action == "create" ? i18n.get('label.create' + this.parentType + 'Resource') : i18n.get('label.modify' + this.parentType + 'Resource'); 

        var expander = new Ext.ux.grid.RowExpander({
            tpl : new Ext.XTemplate(
                '<tpl>' +
	                '<div class="detail">' +
	                	'<span style="font-weight:bold;">Author :&nbsp;</span>{author}' +
	                	'<br>' +
	                	'<span style="font-weight:bold;">Version :&nbsp;</span>{version}' +
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
            title : i18n.get('title.resourcePlugin' + this.parentType + 'Class'),
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
                    name : 'iconClass',
                    type : 'string'
                }, {
                    name : 'parameters'                    
                }, {
                    name : 'priority',
                    type : 'int'
                }, {
                    name : 'dataSetSelection'
                }, ]
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
            
            listeners : {
                scope : this,
                rowclick :  this.onClassClick
            }, 
            plugins : expander

        });

        
        var comboSelectionType = new Ext.form.ComboBox({
		    typeAhead : false,
			fieldLabel : i18n.get("label.selectionType"), 
            name : "dataSetSelection", 
            triggerAction : 'all',
		    lazyRender : true,
		    mode : 'local',
		    anchor : "100%", 
		    store : new Ext.data.ArrayStore({
		        id : 0,
		        fields : [ 'dataSetSelection' ],
		        data : [ 
					[ 'NONE' ],
					[ 'SINGLE' ],
					[ 'MULTIPLE' ],
					[ 'ALL' ],
					
		        ]
		    }),
		    valueField : 'dataSetSelection',
		    displayField : 'dataSetSelection'
        });
        
        this.formParametersPanel = new sitools.admin.common.FormParametersConfigUtil({
        	region : 'center'
        });
        
        // set the search form
        this.fieldMappingFormPanel = new Ext.FormPanel({
            height : 95,
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
            }, comboSelectionType],
            region : 'north'
        });

        this.dsFieldParametersPanel = new Ext.Panel({
            layout : 'border',
            id : 'dsFieldParametersPanel',
            title : i18n.get('title.formFieldParameters'),
            items : [ this.fieldMappingFormPanel, this.formParametersPanel ]
//            listeners : {
//				scope : this,
//				activate : function () {
//					this.showOptions(this.formParametersPanel);
//				}
//            }
        });
        
        this.tabPanel = new Ext.TabPanel({
            height : 450,
            activeTab : 0,
            items : (this.action == "create") ? [ this.gridDatasetServices, this.dsFieldParametersPanel ] : [
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
        if (this.action == "create") {
            if (newTab.id == "dsFieldParametersPanel") {
                var rec = this.gridDatasetServices.getSelectionModel().getSelected();
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
            var rec = this.gridDatasetServices.getSelectionModel().getSelected();
            if (!rec) {
                return false;
            }
            
            this.formParametersPanel.rec = rec;
            this.formParametersPanel.parametersList = rec.data.parameters;
            this.formParametersPanel.parametersFieldName = 'parameters';
            
            this.fieldMappingFormPanel.getForm().findField('name').setValue(rec.data.name);
            this.fieldMappingFormPanel.getForm().findField('descriptionAction').setValue(rec.data.descriptionAction);
            
            this.formParametersPanel.buildViewConfig(this.formParametersPanel.rec);
            
        }

    },
    
    /**
     * If "action" is "modify", load data from record into the form else load empty form
     */
    afterRender : function () {
        sitools.admin.datasets.services.datasetServicesProp.superclass.afterRender.apply(this, arguments);

        if (this.action == "modify") {
            this.fillGridAndForm(this.record, this.action);
        } else {
            //only need to load the resourcesPlugins for creation
            this.gridDatasetServices.getStore().load({
                params : {
                    appClassName : this.appClassName,
                    parent : this.idParent
                }
            });
        }
    },

    /**
     * Save the dataset IHM service properties
     */
    onValidate : function () {
        
        var rec, datasetServiceIhm = {};
        var jsonReturn = {};        
        
        if (this.action == "create") {
            rec = this.gridDatasetServices.getSelectionModel().getSelected();
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
        } else {
            rec = this.record.data;
        }
        
        Ext.apply(datasetServiceIhm, rec.data);
        
        var form = this.fieldMappingFormPanel.getForm();
        if (!form.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }

        Ext.iterate(form.getValues(), function (key, value) {
            if (!Ext.isEmpty(value)) {
            	datasetServiceIhm[key] = value;    
            }
        });
        
        datasetServiceIhm.parameters = [];
        
        Ext.iterate(this.formParametersPanel.getParametersValue(), function(item, ind, all){
        	datasetServiceIhm.parameters.push(item);
        });
        
        var method;
        if (this.action == "modify") {
            method = "PUT";
        } else {
            method = "POST";
        }

        Ext.Ajax.request({
            url : this.urlDatasetServiceIHM.replace('{idService}', datasetServiceIhm.id),
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
            var store = this.formParametersPanel.getStore();
            var lineNb = store.findExact("name", violation.valueName);
            var rec = store.getAt(lineNb);
            rec.set("violation", violation);
        }
        //this.gridFieldMapping.getView().refresh();
    },

    onClose : function () {

    }
});


