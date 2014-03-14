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
 /*
  * @include "../datasets/selectColumn.js"
  */
Ext.namespace('sitools.component.filters');

sitools.component.filters.filtersProp = Ext.extend(Ext.Window, {
    width : 700,
    height : 480,
    modal : true,
    resizable : true,

    initComponent : function () {
        this.filtersUrl = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_FILTERS_PLUGINS_URL');
        
        this.title = this.action == "create" ? i18n.get('label.createFilter') : i18n.get('label.modifyFilter'); 
        
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
        
        this.gridFilter = new Ext.grid.GridPanel({
            viewConfig : {
                forceFit : true
            },
            id : 'gridFilter',
            title : i18n.get('title.filterClass'),
            store : new Ext.data.JsonStore({
                root : 'data',
                restful : true,
                proxy : new Ext.data.HttpProxy({
                    url : this.filtersUrl,
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
                    name : 'className',
                    type : 'string'
                }, {
                    name : 'classAuthor',
                    type : 'string'
                }, {
                    name : 'classVersion',
                    type : 'string'
                },
                {
                    name : 'classOwner',
                    type : 'string'
                }],
                autoLoad : true
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
                    header : i18n.get('label.version'),
                    dataIndex : 'classVersion',
                    width : 100,
                    sortable : true
                },
                {
                    header : i18n.get('label.classOwner'),
                    dataIndex : 'classOwner',
                    width : 100,
                    sortable : true
                }]
            }),
            
            listeners : {
                scope : this,
                rowclick :  this.onClassClick
            }, 
            plugins : expander

        });

        this.proxyFieldMapping = new Ext.data.HttpProxy({
            url : '/tmp',
            restful : true,
            method : 'GET'
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
                    }
                }
            },
            layout : 'fit',
            region : 'center',
            store : new Ext.data.JsonStore({
                root : 'filter.parameters',
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
                    name : 'parameterType',
                    type : 'string'
                }, {
                    name : 'attachedColumn',
                    type : 'string'
                }, {
                    name : 'value',
                    type : 'string'
                }, {
                    name : 'valueType',
                    type : 'string'
                }, {
                    name : 'violation'
                }
                ]
            }),
            cm : new Ext.grid.ColumnModel({
                // specify any defaults for each column
                defaults : {
                    sortable : true
                // columns are not sortable by default
                },
                columns : [expanderGridFieldMapping, {
                    header : i18n.get('label.name'),
                    dataIndex : 'name',
                    width : 100,
                    sortable : true
                }, {
                    header : i18n.get('label.type'),
                    dataIndex : 'parameterType',
                    width : 150,
                    sortable : false
                }, {
                    header : i18n.get('label.attachedColumn'),
                    dataIndex : 'attachedColumn',
                    width : 100,
                    sortable : false
                }, {
                    header : i18n.get('label.value'),
                    dataIndex : 'value',
                    width : 80,
                    sortable : false,
                    editable : true,
                    editor : new Ext.form.TextField()
                } ]
            }), 
            bbar : new Ext.ux.StatusBar({
	            id: 'statusBar',
	            hidden : true,
	            iconCls: 'x-status-error',
                text : i18n.get("label.filterErrorValidationNotification")
            }),
            listeners : {
                scope : this,
                celldblclick : function (grid, rowIndex, columnIndex, e) {
                    var storeRecord = grid.getStore().getAt(rowIndex);
                    var rec = storeRecord.data;
                    if (columnIndex == 3 && rec.parameterType != "PARAMETER_INTERN") {
                        var selectColumnWin = new sitools.admin.datasets.selectColumn({
                            field : "attachedColumn",
                            record : storeRecord,
                            parentStore : this.gridFieldMapping.getStore(),
                            parentView : this.gridFieldMapping.getView(),
                            url : this.urlDatasets + "/" + this.datasetId
                        });
                        selectColumnWin.show(ID.BOX.DATASETS);
                    } else if (columnIndex == 4 && rec.parameterType == "PARAMETER_INTERN") {
                        return true;
                    } else {
                        return false;
                    }
                }
            },
            plugins : expanderGridFieldMapping
        });

        // set the search form
        this.fieldMappingFormPanel = new Ext.FormPanel({
            height : 40,
            frame : true,
            defaultType : 'textfield',
            items : [ {
                fieldLabel : i18n.get('label.descriptionAction'),
                name : 'descriptionAction',
                anchor : '100%'
            } ],
            region : 'north'

        });

        this.fieldMappingPanel = new Ext.Panel({
            layout : 'border',
            id : 'gridFieldMapping',
            title : i18n.get('title.fieldMapping'),
            items : [ this.fieldMappingFormPanel, this.gridFieldMapping ]

        });

        this.tabPanel = new Ext.TabPanel({
            height : 450,
            activeTab : 0,
            items : (this.action == "create") ? [ this.gridFilter, this.fieldMappingPanel ] : [ this.fieldMappingPanel ],
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

        sitools.component.filters.filtersProp.superclass.initComponent.call(this);
    },
    
    beforeTabChange : function (self, newTab, currentTab) {
        if (this.action == "create") {
            if (newTab.id == "gridFieldMapping") {
                var rec = this.gridFilter.getSelectionModel().getSelected();
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
            var rec = this.gridFilter.getSelectionModel().getSelected();
            if (!rec) {
//                var tmp = new Ext.ux.Notification({
//                        iconCls : 'x-icon-information',
//                        title : i18n.get('label.information'),
//                        html : i18n.get('warning.noselection'),
//                        autoDestroy : true,
//                        hideDelay : 1000
//                    }).show(document);
                return false;
            }
            var className = rec.data.className;
            this.proxyFieldMapping.setUrl(this.filtersUrl + "/" + className
                    + "/" + this.datasetId);
            var store = this.gridFieldMapping.getStore();
            store.removeAll();
            store.load();
        }

    },
    afterRender : function () {
        sitools.component.filters.filtersProp.superclass.afterRender.apply(this, arguments);

        if (this.action == "modify") {

            var form = this.fieldMappingFormPanel.getForm();
            var rec = {};
            rec.descriptionAction = this.filter.descriptionAction;
            form.loadRecord(new Ext.data.Record(rec));

            var parameters = this.filter.parameters;
            if (parameters !== null) {
                var store = this.gridFieldMapping.getStore();
                var i;
                for (i = 0; i < parameters.length; i++) {
                    rec = new Ext.data.Record(parameters[i]);
                    store.add(rec);
                }
                this.gridFieldMapping.getView().refresh();

            }

        }
    },
    onValidate : function () {
        var rec = this.gridFilter.getSelectionModel().getSelected();
        if (!rec && this.action == "create") {
            var tmp = new Ext.ux.Notification({
                iconCls : 'x-icon-information',
                title : i18n.get('label.information'),
                html : i18n.get('warning.noselection'),
                autoDestroy : true,
                hideDelay : 1000
            }).show(document);
            return false;
        }
        var jsonReturn = {};
        var form = this.fieldMappingFormPanel.getForm();
        if (!form.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }
        if (!Ext.isEmpty(form)) {
            jsonReturn.descriptionAction = form.findField("descriptionAction").getValue();
        } else {
            jsonReturn.descriptionAction = undefined;
        }
        

        var parameters = [];
        if (this.action == "create") {
            rec = this.gridFilter.getSelectionModel().getSelected();
            var classParam = rec.data;

            jsonReturn.className = classParam.className;
            jsonReturn.name = classParam.name;
            jsonReturn.description = classParam.description;
            jsonReturn.classVersion = classParam.classVersion;
            jsonReturn.classAuthor = classParam.classAuthor;
            jsonReturn.classOwner = classParam.classOwner;

        }

        var storeField = this.gridFieldMapping.getStore();

        for (var i = 0; i < storeField.getCount(); i++) {
            var recTmp = storeField.getAt(i).data;
            //not to send the violation object, used only on the client part
            recTmp.violation = undefined;
            parameters.push(recTmp);
        }
        
        jsonReturn.parameters = parameters;
       
        var url = this.urlDatasets + "/" + this.datasetId + this.filterUrlPart;
        var method;
        if (this.action == "modify") {
            url += "/" + this.filter.id;
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
                    html : i18n.get('label.filterSaved'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);
                
                this.parent.getStore().reload();                
                this.close();
                
            }
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

    }

});
