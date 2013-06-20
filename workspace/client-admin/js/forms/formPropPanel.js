/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*
 * @include "absoluteLayoutProp.js"
 * @include "componentsListPanel.js"
 * @include "componentPropPanel.js"
 * @include "ComponentsDisplayPanel.js"
 * @include "FormGridComponents.js"
 */
Ext.namespace('sitools.admin.forms');

/**
 * 
 * @class sitools.admin.forms.formPropPanel
 * @extends Ext.Window
 */
sitools.admin.forms.formPropPanel = Ext.extend(Ext.Window, {
    width : 700,
    height : 580,
    modal : true,
    pageSize : 10,
    autoScroll : true,
    formSize : {
		width : 500, 
		height : 500
    }, 

    initComponent : function () {
        if (this.action == 'modify') {
            this.title = i18n.get('label.modifyForm');
        }
        if (this.action == 'create') {
            this.title = i18n.get('label.createForm');
        }
        this.formComponentsStore = new Ext.data.JsonStore({
            root : 'data',
            fields : [ {
                name : 'id',
                type : 'string'
            }, {
                name : 'label',
                type : 'string'
            }, {
                name : 'type',
                type : 'string'
            }, {
                name : 'code',
                type : 'string'
            }, {
                name : 'values'
            }, {
                name : 'width',
                type : 'int'
            }, {
                name : 'height',
                type : 'int'
            }, {
                name : 'xpos',
                type : 'int'
            }, {
                name : 'ypos',
                type : 'int'
            }, {
                name : 'css',
                type : 'string'
            }, {
                name : 'jsAdminObject',
                type : 'string'
            }, {
                name : 'jsUserObject',
                type : 'string'
            }, {
                name : 'defaultValues'
            }, {
                name : 'valueSelection',
                type : 'string'
            }, {
                name : 'autoComplete', 
                type : 'boolean'
            }, {
                name : 'parentParam'
            }, {
                name : 'dimensionId',
                type : 'string'
            }, {
                name : 'unit'
            }, {
				name : 'extraParams'
            }, {
                name : 'containerPanelId'
            },],
            autoLoad : false
        });
        
        this.formulairePrincipal = new Ext.FormPanel({
            title : i18n.get('label.formInfo'),
            id : "formMainFormId", 
            border : false,
            padding : 10,
            items : [ {
                xtype : 'hidden',
                name : 'id'
            }, {
                xtype : 'textfield',
                name : 'name',
                fieldLabel : i18n.get('label.name'),
                anchor : '100%',
                maxLength : 50, 
                vtype : 'name', 
                allowBlank : false
            }, {
                xtype : 'textfield',
                name : 'description',
                fieldLabel : i18n.get('label.description'),
                anchor : '100%',
                maxLength : 50
            }, {
                xtype : 'textfield',
                name : 'css',
                fieldLabel : i18n.get('label.css'),
                anchor : '100%',
                maxLength : 50
            } ]
        });
        
        var storeColumns = new Ext.data.JsonStore({
//	        root : 'data',
//	        url : loadUrl.get('APP_URL') + loadUrl.get('APP_COLLECTIONS_URL') + "/" + config.collectionId + "/concepts/" + config.dictionaryId, 
//	        proxy : httpProxyConcepts, 
//	        restful : true, 
	        remoteSort : false,
	        fields : [ {
	            name : 'tableName',
	            type : 'string'
	        }, {
	            name : 'columnAlias',
	            type : 'string'
	        }, {
	            name : 'filtrable'
	        }], 
	        autoLoad : false
	    });	
	    var cmColumns = new Ext.grid.ColumnModel({
			columns : [{
				header : i18n.get("label.tableName"), 
				dataIndex : 'tableName', 
				type : 'string'
			}, {
				header : i18n.get("label.columnAlias"), 
				dataIndex : 'columnAlias', 
				type : 'string'
			}]
	    });
	    this.gridColumns = new Ext.grid.GridPanel({
			store : storeColumns, 
			cm : cmColumns, 
			title : i18n.get('label.datasetColumns'), 
			flex : 1, 
			viewConfig : {
				forceFit : true
			}
	    });
		Ext.each(this.datasetColumnModel, function (column) {
            if (column.specificColumnType != 'VIRTUAL') {
                this.gridColumns.getStore().add(new Ext.data.Record(column));
            }
        }, this);
        
        var firstPanel = new Ext.Panel({
			title : i18n.get('label.FormInfo'),
            layout : "vbox", 
            layoutConfig : {
				align : "stretch"
            },
			items : [this.formulairePrincipal, this.gridColumns], 
            listeners : {
				collectionChanged : function (field, newValue, oldValue) {
					this.getBubbleTarget().fireEvent("collectionChanged", field, newValue, oldValue);
				}, 
				dictionaryChanged : function (field, newValue, oldValue) {
					this.getBubbleTarget().fireEvent("dictionaryChanged", field, newValue, oldValue);
				}
            } 
        });
        this.absoluteLayout = new sitools.admin.forms.ComponentsDisplayPanel({
			formComponentsStore : this.formComponentsStore, 
			datasetColumnModel : this.datasetColumnModel,
			context : "dataset", 
			formSize : this.formSize
        });
        
        var absContainer = new Ext.Panel({
			flex : 1, 
			autoScroll : true, 
			items : [this.absoluteLayout]
        });
        
        
        this.componentListPanel = new sitools.admin.forms.componentsListPanel({
            datasetColumnModel : this.datasetColumnModel,
            formComponentsStore : this.formComponentsStore,
            action : 'create', 
            context : "dataset", 
            storeConcepts : this.storeConcepts
        });

        
        var dispPanel = new Ext.Panel({
			layout : "hbox", 
			title : i18n.get('label.disposition'),
			layoutConfig : {
				align : "stretch"
			}, 
			items : [this.componentListPanel, absContainer], 
			listeners : {
				scope : this, 
				activate : function () {
					
					this.absoluteLayout.fireEvent('activate');
										
//					Ext.getCmp('mainpanel').fireEvent('activate');
					
					var absoluteLayout = this.absoluteLayout;
					var displayPanelDropTargetEl =  absoluteLayout.body.dom;
					var formComponentsStore = this.formComponentsStore;
					var datasetColumnModel = this.datasetColumnModel;
					var storeConcepts = this.storeConcepts;

//					var displayPanelDropTarget = new Ext.dd.DropTarget(displayPanelDropTargetEl, {
//						ddGroup     : 'gridComponentsList',
//						overClass : 'not-save-textfield',
//						notifyDrop  : function (ddSource, e, data) {
//							var xyDrop = e.xy;
//							var xyRef = Ext.get(absoluteLayout.body).getXY();
//							
//							var xyOnCreate = {
//								x : xyDrop[0] - xyRef[0], 
//								y : xyDrop[1] - xyRef[1]
//							};
//							// Reference the record (single selection) for readability
//							var rec = ddSource.dragData.selections[0];
//					        var ComponentWin = new sitools.admin.forms.componentPropPanel({
//					            urlAdmin : rec.data.jsonDefinitionAdmin,
//					            datasetColumnModel : datasetColumnModel,
//					            ctype : rec.data.type,
//					            action : "create",
//					            componentDefaultHeight : rec.data.componentDefaultHeight,
//					            componentDefaultWidth : rec.data.componentDefaultWidth,
//					            dimensionId : rec.data.dimensionId,
//					            unit : rec.data.unit,
//					            extraParams : rec.data.extraParams, 
//					            jsAdminObject : rec.data.jsAdminObject, 
//					            jsUserObject : rec.data.jsUserObject, 
//					            context : "dataset", 
//					            xyOnCreate : xyOnCreate, 
//					            storeConcepts : this.storeConcepts, 
//					            absoluteLayout : absoluteLayout, 
//					            record : rec, 
//					            formComponentsStore : formComponentsStore
//					        });
//					        ComponentWin.show();
//						}
//					});
			       
					//this.componentListPanel.dd.addToGroup('gridComponentsTest');
					
				}
			}
			
		});
        
        this.tabPanel = new Ext.TabPanel({
            height : 450,
            activeTab : 0,
            items : [ firstPanel, dispPanel ],
            listeners : {
				scope : this, 
				afterrender : function (panel) {
					panel.setSize(this.body.getSize());
				}
            }
        });
        this.items = [ this.tabPanel ];
		this.buttons = [ {
            text : i18n.get('label.ok'),
            scope : this,
            handler : this.onValidate
        }, {
            text : i18n.get('label.cancel'),
            scope : this,
            handler : function () {
                this.close();
            }
        } ];
		this.listeners = {
			scope : this, 
			resize : function (window, width, height) {
				var size = window.body.getSize();
				this.tabPanel.setSize(size);
			} 
		};
        sitools.admin.forms.formPropPanel.superclass.initComponent.call(this);
    },

    onRender : function () {
    	
        sitools.admin.forms.formPropPanel.superclass.onRender.apply(this, arguments);
        
        if (this.urlFormulaire) {
            // Si l'objet est en modification, on charge l'objet en question
            if (this.action == 'modify') {
                Ext.Ajax.request({
                    url : this.urlFormulaire,
                    method : 'GET',
                    scope : this,
                    success : function (ret) {
                        var Json = Ext.decode(ret.responseText);
                        if (!Json.success) {
                            this.close();
                            Ext.Msg.alert(i18n.get('label.warning'), Json.message);
                            return;
                        }
                        
                        var f = this.findByType('form')[0].getForm();
                        var data = Json.form;
                        if (!Ext.isEmpty(data.width)) {
                            this.formSize.width = data.width;
                        }
                        if (!Ext.isEmpty(data.height)) {
                            this.formSize.height = data.height;
                        }
                        var rec = {};
                        rec.id = data.id;
                        rec.name = data.name;
                        rec.description = data.description;
                        rec.css = data.css;
                        
                        var record = new Ext.data.Record(rec);
                        f.loadRecord(record);

                        if (data.parameters) {
                            var parameters = data.parameters;
                            var i;
                            for (i = 0; i < parameters.length; i++) {
                                this.formComponentsStore.add(new Ext.data.Record({
                                    type : parameters[i].type,
                                    code : parameters[i].code,
                                    label : parameters[i].label,
                                    values : parameters[i].values,
                                    width : parameters[i].width,
                                    height : parameters[i].height,
                                    xpos : parameters[i].xpos,
                                    ypos : parameters[i].ypos,
                                    id : parameters[i].id,
                                    css : parameters[i].css,
                                    jsAdminObject : parameters[i].jsAdminObject,
                                    jsUserObject : parameters[i].jsUserObject,
                                    defaultValues : parameters[i].defaultValues,
                                    valueSelection : parameters[i].valueSelection, 
                                    autoComplete : parameters[i].autoComplete, 
                                    parentParam : parameters[i].parentParam, 
                                    dimensionId : parameters[i].dimensionId, 
                                    unit : parameters[i].unit, 
                                    extraParams : parameters[i].extraParams,
                                    containerPanelId : parameters[i].containerPanelId
                                }));

                            }
                        }
//                        this.setSize(data.width + 25, data.height + 35);
                        this.doLayout();

                    },
                    failure : function (ret) {
                        var data = Ext.decode(ret.responseText);
                        Ext.Msg.alert(i18n.get('label.warning'), data.errorMessage);
                    }

                });
            }
        }
    },
    onValidate : function () {
        var f = this.findByType('form')[0].getForm();
        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }

        var putObject = {};
        Ext.iterate(f.getValues(), function (key, value) {
            putObject[key] = value;
        }, this);

        var width = this.formSize.width;
        var height = this.formSize.height;
                
        putObject.width = width;
        putObject.height = height;

        var store = this.formComponentsStore;

        if (store.getCount() > 0) {
            putObject.parameters = [];
        }
		
        store.each(function (component) {

            putObject.parameters.push({
                type : component.data.type,
                code : component.data.code,
                label : component.data.label,
                values : component.data.values,
                width : component.data.width,
                height : component.data.height,
                xpos : component.data.xpos,
                ypos : component.data.ypos,
                id : component.data.id,
                css : component.data.css,
                jsAdminObject : component.data.jsAdminObject,
                jsUserObject : component.data.jsUserObject,
                defaultValues : component.data.defaultValues,
                valueSelection : component.data.valueSelection, 
                autoComplete : component.data.autoComplete, 
                parentParam : component.data.parentParam, 
                dimensionId : component.data.dimensionId, 
                unit : component.data.unit, 
                extraParams : component.data.extraParams,
                containerPanelId : component.data.containerPanelId
            });
        });
        if (this.action == 'modify') {
            Ext.Ajax.request({
                url : this.urlFormulaire,
                method : 'PUT',
                scope : this,
                jsonData : putObject,
                success : function (ret) {
                    var Json = Ext.decode(ret.responseText);
                    if (!Json.success) {
                        Ext.Msg.alert(i18n.get('label.warning'), Json.message);
                        return;
                    }
                    this.close();
                    this.store.reload();
                },
                failure : alertFailure
            });
        } else {
            Ext.Ajax.request({
                url : this.urlFormulaire,
                method : 'POST',
                scope : this,
                jsonData : putObject,
                success : function (ret) {
                    var Json = Ext.decode(ret.responseText);
                    if (!Json.success) {
                        Ext.Msg.alert(i18n.get('label.warning'), Json.message);
                        return;
                    }
                    this.close();
                    this.store.reload();
                    // Ext.Msg.alert(i18n.get('label.information'),
                    // i18n.get('msg.uservalidate'));
                },
                failure : alertFailure
            });
        }
    }

});

