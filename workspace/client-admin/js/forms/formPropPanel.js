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
        this.zoneStore = new Ext.data.JsonStore({
            root : 'zone',
            fields : [ {
                id : 'id',
                type : 'string'
            }, {
                name : 'title',
                type : 'string'
            }, {
                name : 'height',
                type : 'int'
            }, {
                name : 'css',
                type : 'string'
            }, {
                name : 'position',
                type : 'string'
            }, {
                name : 'collapsible',
                type : 'boolean'
            }, {
                name : 'collapsed',
                type : 'boolean'
            }, {
                name : 'params'
            }]
        });
        this.formComponentsStore = new Ext.data.JsonStore({
            root : 'data',
            autoLoad : false,
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
            }]
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
	        autoLoad : false,
	        fields : [ {
	            name : 'tableName',
	            type : 'string'
	        }, {
	            name : 'columnAlias',
	            type : 'string'
	        }, {
	            name : 'filtrable'
	        }]
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
        	zoneStore : this.zoneStore,
        	formComponentsStore : this.formComponentsStore, 
			datasetColumnModel : this.datasetColumnModel,
			context : "dataset",
			formSize : this.formSize,
			action : this.action
        });
        
        var absContainer = new Ext.Panel({
            title : i18n.get('label.disposition'),
			flex : 1, 
			autoScroll : true,
			tbar : new Ext.Toolbar({
                items : [{
                    scope : this,
                    text : i18n.get('label.changeFormSize'),
                    icon : loadUrl.get('APP_URL') + '/common/res/images/icons/sva_exe_synchrone.png',
                    handler : this._sizeUp
                }, {
                    scope : this,
                    text : i18n.get('label.addAdvancedCritera'),
                    icon : loadUrl.get('APP_URL') + '/common/res/images/icons/tree_forms.png',
                    handler : this._addPanel
                }]
    
            }),
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
			autoScroll : true,
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
                        
                        this.absoluteLayout.setSize(this.formSize);
                        
                        var rec = {};
                        rec.id = data.id;
                        rec.name = data.name;
                        rec.description = data.description;
                        rec.css = data.css;
                        
                        var record = new Ext.data.Record(rec);
                        f.loadRecord(record);
                        
                        var globalParameters = {};
                        if (!Ext.isEmpty(data.parameters)) {
                            globalParameters.oldParams = data.parameters;
                        }
                        else {
                            globalParameters.formZones = data.zones;
                        }
                        
                        if (!Ext.isEmpty(globalParameters.formZones)) {
                            Ext.each(globalParameters.formZones, function (zone) {
                                this.zoneStore.add(new Ext.data.Record({
                                    id : zone.id,
                                    title : zone.title,
                                    height : zone.height,
                                    collapsible : zone.collapsible,
                                    collapsed : zone.collapsed,
                                    css : zone.css,
                                    position : zone.position,
                                    params : zone.params
                                }));

                                if (!Ext.isEmpty(zone.params)) {
                                    Ext.each(zone.params, function (param) {
                                        this.formComponentsStore.add(new Ext.data.Record({
                                            type : param.type,
                                            code : param.code,
                                            label : param.label,
                                            values : param.values,
                                            width : param.width,
                                            height : param.height,
                                            xpos : param.xpos,
                                            ypos : param.ypos,
                                            id : param.id,
                                            css : param.css,
                                            jsAdminObject : param.jsAdminObject,
                                            jsUserObject : param.jsUserObject,
                                            defaultValues : param.defaultValues,
                                            valueSelection : param.valueSelection,
                                            autoComplete : param.autoComplete,
                                            parentParam : param.parentParam,
                                            dimensionId : param.dimensionId,
                                            unit : param.unit,
                                            extraParams : param.extraParams,
                                            containerPanelId : param.containerPanelId
                                        }));
                                    }, this);
                                }
                            }, this);
                        } else if (!Ext.isEmpty(globalParameters.oldParams)) {
                            var idGen = Ext.id();
                            this.zoneStore.add(new Ext.data.Record({
                                id : idGen,
                                title : data.name,
                                height : data.height,
                                css : data.css,
                                position : 0,
                                params : globalParameters.oldParams
                            }));
                            
                            if (!Ext.isEmpty(globalParameters.oldParams)) {
                                var parameters = globalParameters.oldParams;
                                for (var i = 0; i < parameters.length; i++) {
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
                                        containerPanelId : idGen
                                    }));
                                }
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
    
    _sizeUp : function () {
        var panelProp = new sitools.admin.forms.absoluteLayoutProp({
            absoluteLayout : this.absoluteLayout,
            tabPanel : this.absoluteLayout.ownerCt.ownerCt.ownerCt,
            win : this,
            formSize : this.formSize
        });
        panelProp.show();
    },
    
    _addPanel : function () {
        var setupAdvancedPanel = new sitools.admin.forms.setupAdvancedFormPanel({
            parentContainer : this.absoluteLayout,
            currentPosition : this.absoluteLayout.position
        });
        setupAdvancedPanel.show();
       
        this.absoluteLayout.doLayout();

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

        if (this.zoneStore.getCount() > 0) {
            putObject.zones = [];
        }
        
//        this.zoneStore.each(function (rec) {
//            putObject.zones.push({
//                id : rec.data.id,
//                title : rec.data.title,
//                height : rec.data.height,
//                css : rec.data.css,
//                position : rec.data.position,
//                params : rec.data.params
//            });
//        });
//        
        proppanel = this;
        
        this.zoneStore.each(function (component) {
        	
            var paramObject = {};
            var paramstore = proppanel.formComponentsStore;
            if (paramstore.getCount() > 0) {
                paramObject = [];
                paramstore.each(function (param) {
                    if (param.data.containerPanelId == component.data.id){
                        paramObject.push({
                            type : param.data.type,
                            code : param.data.code,
                            label : param.data.label,
                            values : param.data.values,
                            width : param.data.width,
                            height : param.data.height,
                            xpos : param.data.xpos,
                            ypos : param.data.ypos,
                            id : param.data.id,
                            css : param.data.css,
                            jsAdminObject : param.data.jsAdminObject,
                            jsUserObject : param.data.jsUserObject,
                            defaultValues : param.data.defaultValues,
                            valueSelection : param.data.valueSelection, 
                            autoComplete : param.data.autoComplete, 
                            parentParam : param.data.parentParam, 
                            dimensionId : param.data.dimensionId, 
                            unit : param.data.unit, 
                            extraParams : param.data.extraParams,
                            containerPanelId : param.data.containerPanelId
                        });
                    }
                });
                
                if (!Ext.isEmpty(paramObject)){
                    putObject.zones.push({
                        id : component.data.id,
                        height : component.data.height,
                        position : component.data.position,
                        css : component.data.css,
                        collapsible : component.data.collapsible,
                        collapsed : component.data.collapsed,
                        title : component.data.title,
                        params : paramObject
                    });
                }
            }
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

