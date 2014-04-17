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
/*global Ext, sitools, ID, i18n, document, showResponse, loadUrl*/
/*
 * @include "../datasets/selectColumn.js"
 */
Ext.namespace('sitools.admin.converters');

/**
 * A converter properties panel. 
 * Administrator should create or update converters on a specific Dataset. 
 * @cfg {string} action Should be 'create' or 'modify'
 * @cfg {} parent Component caller. 
 * @cfg {string} datasetId The Dataset Id 
 * @cfg {string} converterUrlPart The string to add to convert dataset url to converters url
 * @cfg {string} urlDatasets the url of the datasets collection resource. 
 * @class sitools.admin.converters.convertersProp
 * @extends Ext.Window
 */
Ext.define('sitools.admin.converters.convertersProp', {
    extend : 'Ext.Window', 
    width : 700,
    height : 480,
    modal : true,
    resizable : true,
    mixins : {
        utils : "js.utils.utils"
    },

    initComponent : function () {
        this.convertersPluginUrl = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_CONVERTERS_PLUGINS_URL');
		
        this.title = this.action == "create" ? i18n.get('label.createConverter') : i18n.get('label.modifyConverter'); 
        
        var expander = {
            ptype: 'rowexpander',
            rowBodyTpl : Ext.create("Ext.XTemplate", 
                '<tpl if="this.descEmpty(description) == false" ><p class="sitoolsDescriptionText"> <b>Description :&nbsp;</b>{description} </p></tpl>',
                {
                    compiled : true,
                    descEmpty : function (description) {
                        return Ext.isEmpty(description);
                    }
                }),
            expandOnDblClick : true
        };

        this.storeGridConverter = Ext.create('Ext.data.JsonStore', {
            proxy : {
                type : 'ajax',
                url : this.convertersPluginUrl,
                reader : {
                    root : 'data',
                    type : 'json',
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
                name : 'classVersion',
                type : 'string'
            }, {
                name : 'classOwner',
                type : 'string'
            }],
            autoLoad : true
        });
        
        this.gridConverter = Ext.create('Ext.grid.Panel', {
            forceFit : true,
            id : 'gridConverter',
            title : i18n.get('title.converterClass'),
            selModel : Ext.create('Ext.selection.RowModel'),
            store : this.storeGridConverter,
            columns : [{
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
            }],
            listeners : {
                scope : this,
                itemClick :  this.onClassClick
            }, 
            plugins : [expander]
        });

        
        var expanderGridFieldMapping = {
            ptype: 'rowexpander',
            rowBodyTpl : Ext.create("Ext.XTemplate", 
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
        
        this.storeFieldMapping = Ext.create('Ext.data.JsonStore', {
            remoteSort : false,
            fields : [{
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
            }]
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
//                    } else {
//                        cls = "row-valid";
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
                                // var view =
                                // this.scope.gridFieldMapping.getView();
                                var htmlLineEl = view.getNode(index);
                                var el = Ext.get(htmlLineEl);

                                if (violation.level == "CRITICAL") {
                                    el.addCls('red-row');
                                } else if (violation.level == "WARNING") {
                                    el.addCls('orange-row');
                                }

                                var cls = (violation.level == "CRITICAL") ? "x-form-invalid-tip" : "x-form-invalid-tip x-form-warning-tip";

                                var ttConfig = {
                                    html : violation.message,
                                    dismissDelay : 0,
                                    target : el,
                                    cls : cls
                                };
                                Ext.create("Ext.ToolTip", ttConfig);
                            }
                        });
                    }
                }
            },
            layout : 'fit',
            region : 'center',
            store : this.storeFieldMapping,
            columns : [{
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
                    header : i18n.get('label.value') + '<img title="Editable" height=14 widht=14 src="/sitools/common/res/images/icons/toolbar_edit.png"/>',
                    dataIndex : 'value',
                    width : 80,
                    sortable : false,
                    editor: {
                        xtype: 'textfield'
                    }
            }],
            bbar : Ext.create('Ext.ux.StatusBar', {
                id: 'statusBar',
                hidden : true,
                iconCls: 'x-status-error',
                text : i18n.get("label.converterErrorValidationNotification")
            }),
            listeners : {
                scope : this,
                cellclick : function (grid, td, columnIndex, record, tr, rowIndex) {
                    var storeRecord = grid.getStore().getAt(rowIndex);
                    var rec = storeRecord.data;
                    if (columnIndex == 3 && rec.parameterType != "CONVERTER_PARAMETER_INTERN") {
                        var selectColumnWin = Ext.create('sitools.admin.datasets.selectColumn', {
                            field : "attachedColumn",
                            record : storeRecord,
                            parentStore : this.gridFieldMapping.getStore(),
                            parentView : this.gridFieldMapping.getView(),
                            url : this.urlDatasets + "/" + this.datasetId
                        });
                        selectColumnWin.show(ID.BOX.DATASETS);
                    } else if (columnIndex == 4 && rec.parameterType == "CONVERTER_PARAMETER_INTERN") {
                        return true;
                    } else {
                        return false;
                    }
                }
            },
            plugins : [expanderGridFieldMapping, cellEditing],
        });

        // set the search form
        this.fieldMappingFormPanel = Ext.create('Ext.form.Panel', {
            height : 40,
            defaultType : 'textfield',
            padding : 5,
            items : [{
                fieldLabel : i18n.get('label.descriptionAction'),
                name : 'descriptionAction',
                anchor : '90%'
            } ],
            region : 'north'

        });

        this.fieldMappingPanel = Ext.create('Ext.Panel', {
            layout : 'border',
            id : 'gridFieldMapping',
            title : i18n.get('title.fieldMapping'),
            items : [ this.fieldMappingFormPanel, this.gridFieldMapping ]

        });

        this.tabPanel = Ext.create('Ext.TabPanel', {
            height : 450,
            activeTab : 0,
            items : (this.action == "create") ? [ this.gridConverter, this.fieldMappingPanel ] : [ this.fieldMappingPanel ],
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

        sitools.admin.converters.convertersProp.superclass.initComponent.call(this);
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
            if (newTab.id == "gridFieldMapping") {
                var rec = this.getLastSelectedRecord(this.gridConverter);
                if (!rec) {
                    Ext.create("Ext.ux.Notification", {
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
     * Method called on grid click
     * @param {} self
     * @param {} rowIndex
     * @param {} e
     * @return {Boolean}
     */
    onClassClick : function (self, rec, item, index, e, eOpts) {
		if (this.action == "create") {
			if (!rec) {
				return false;
			}
			var className = rec.get("className");
			
			var store = this.gridFieldMapping.getStore();
			store.setProxy({
                type : 'ajax',
                url : this.convertersPluginUrl + "/" + className + "/" + this.datasetId,
                reader : {
                    type : 'json',
                    root : 'converter.parameters',
                    idProperty : 'name'
                }
            });			
			
			store.removeAll();
			store.load();
		}

	},

    /**
     * load the selected converter properties
     */
    afterRender : function () {
        sitools.admin.converters.convertersProp.superclass.afterRender.apply(this, arguments);

        if (this.action == "modify") {

            var form = this.fieldMappingFormPanel.getForm();
            var rec = {};
            rec.descriptionAction = this.converter.descriptionAction;
            form.setValues(rec);

            var parameters = this.converter.parameters;
            if (parameters !== null) {
                var store = this.gridFieldMapping.getStore();
                for (var i = 0; i < parameters.length; i++) {
                    var recTmp = parameters[i];
                    store.add(recTmp);
                }
                this.gridFieldMapping.getView().refresh();

            }

        }
    },
    /**
     * Called on save Button : Validate fields and send POST or PUT request depending on action 
     * @return {Boolean}
     */
    onValidate : function () {
        var rec = this.getLastSelectedRecord(this.gridConverter);
        if (!rec && this.action == "create") {
            Ext.create("Ext.ux.Notification", {
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
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.invalidForm'));
            return;
        }
        if (!Ext.isEmpty(form)) {
            jsonReturn.descriptionAction = form.findField("descriptionAction").getValue();
        } else {
            jsonReturn.descriptionAction = undefined;
        }
        
        
        var parameters = [];
        if (this.action == "create") {
            var classParam = rec.getData();

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
            recTmp.violation = undefined;
            delete recTmp.id;
            parameters.push(recTmp);
        }
        
        jsonReturn.parameters = parameters;
	   
        var url = this.urlDatasets + "/" + this.datasetId + this.converterUrlPart;
        var method;
        if (this.action == "modify") {
            url += "/" + this.converter.id;
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
                Ext.create("Ext.ux.Notification", {
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('label.converterSaved'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);
	            
                
                //var idConvChained = data.converterChainedModel.id;
                this.parent.getStore().reload();                
                this.close();
                
            }
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
    },

    onClose : function () {

    }

});
