/***************************************
* Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.namespace('sitools.admin.filters');

Ext.define('sitools.admin.filters.FiltersProp', { 
    extend : 'Ext.Window',
    width : 700,
    height : 480,
    modal : true,
    resizable : true,
    border : false,
    mixins : {
        utils : "sitools.admin.utils.utils"
    },

    initComponent : function () {
        this.filtersUrl = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASETS_FILTERS_PLUGINS_URL');
        
        this.title = this.action == "create" ? i18n.get('label.createFilter') : i18n.get('label.modifyFilter'); 
        
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
        
        this.gridFilter = Ext.create("Ext.grid.GridPanel",{
            forceFit : true,
            title : i18n.get('title.filterClass'),
            store : Ext.create("Ext.data.JsonStore", {
                restful : true,
                proxy : {
                    type : 'ajax',
                    startParam : undefined,
                    limitParam : undefined,
                    url : this.filtersUrl,
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
                    name : 'classVersion',
                    type : 'string'
                },
                {
                    name : 'classOwner',
                    type : 'string'
                }],
                autoLoad : true
            }),
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
            plugins : [expander],
            selModel : Ext.create('Ext.selection.RowModel',{
                mode: 'SINGLE'
            })
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
        
        this.gridFieldMapping = Ext.create('Ext.grid.Panel', {
            forceFit : true,
            border : false,
            padding : "10 5 10 5",
            features: [Ext.create('Ext.grid.feature.Grouping', {
            		depthToIndent : 35
            	})
    		],
            viewConfig : {
                scope : this,
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
		
		                        Ext.create("Ext.ToolTip", ttConfig);
                            }
                        });
                    }
                }
            },
            layout : 'fit',
            region : 'center',
            store : Ext.create("Ext.data.JsonStore", {
                remoteSort : false,
                groupField : 'parameterType',
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
                sortable : false,
                renderer : function (value, metadata, record, rowIndex, colIndex, store) {
                	if (record.get('parameterType') == "PARAMETER_INTERN") {
                		metadata.innerCls = "disabled";
                	}
                	return value;
                }
            }, {
                header : i18n.get('label.value') + '<img title="Editable" height=14 widht=14 src="/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/toolbar_edit.png"/>',
                dataIndex : 'value',
                width : 80,
                sortable : false,
                editable : true,
                editor : {
                    xtype : 'textfield'
                },
                renderer : function (value, metadata, record, rowIndex, colIndex, store) {
                	var paramType = record.get('parameterType');
                	if (paramType == "PARAMETER_IN" 
                		|| paramType == "PARAMETER_OUT"
            			|| paramType == "PARAMETER_INOUT") {
                		
                		metadata.innerCls = "disabled";
                	}
                	return value;
                }
            }],
            bbar : Ext.create("Ext.ux.StatusBar", {
	            id: 'statusBar',
	            hidden : true,
	            iconCls: 'x-status-error',
                text : i18n.get("label.filterErrorValidationNotification")
            }),
            listeners : {
                scope : this,
                cellclick : function (grid, td, columnIndex, record, tr, rowIndex) {
                    var rec = record.data;
                    var storeRecord = grid.getStore().getAt(rowIndex);
                    if (columnIndex == 3 && rec.parameterType != "PARAMETER_INTERN") {
                        var selectColumnWin = Ext.create("sitools.admin.datasets.SelectColumn", {
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
            plugins : [cellEditing , expanderGridFieldMapping]
        });

        // set the search form
        this.fieldMappingFormPanel = Ext.create("Ext.FormPanel", {
            height : 40,
            border : false,
            bodyBorder : false,
            padding : 5,
            defaultType : 'textfield',
            items : [ {
                fieldLabel : i18n.get('label.descriptionAction'),
                name : 'descriptionAction',
                anchor : '100%'
            } ],
            region : 'north'

        });

        this.fieldMappingPanel = Ext.create("Ext.Panel", {
            layout : 'border',
            id : 'gridFieldMapping',
            title : i18n.get('title.fieldMapping'),
            bodyStyle : 'background-color:white;',
            border : false,
            items : [ this.fieldMappingFormPanel, this.gridFieldMapping ]

        });

        this.tabPanel = Ext.create("Ext.TabPanel", {
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

        this.callParent(arguments);
    },
    
    beforeTabChange : function (self, newTab, currentTab) {
        if (this.action == "create") {
            if (newTab.id == "gridFieldMapping") {
                var rec = this.getLastSelectedRecord(this.gridFilter);
                if (!rec) {
                    popupMessage(i18n.get('label.information'), i18n.get('warning.noselection'), null, 'x-info');
                    return false;
                }      
            }
        }

    },
    
    onClassClick : function (self, rec, item, index, e, eOpts) {
        if (this.action == "create") {
            if (!rec) {
                return false;
            }
            var className = rec.get("className");
            
            var store = this.gridFieldMapping.getStore();
            store.setProxy({
                type : 'ajax', 
                url : this.filtersUrl + "/" + className + "/" + this.datasetId,
                reader : {
                    type : 'json',
                    root : 'filter.parameters',
                    idProperty : 'name'
                }
            });
            store.removeAll();
            store.load();
        }

    },
    afterRender : function () {
        this.callParent(arguments);

        if (this.action == "modify") {

            var form = this.fieldMappingFormPanel.getForm();
            var rec = {};
            rec.descriptionAction = this.filter.descriptionAction;
            form.setValues(rec);

            var parameters = this.filter.parameters;
            if (parameters !== null) {
                var store = this.gridFieldMapping.getStore();
                var i;
                for (i = 0; i < parameters.length; i++) {
                    rec = parameters[i];
                    store.add(rec);
                }
                this.gridFieldMapping.getView().refresh();

            }

        }

        this.getEl().on('keyup', function (e) {
            if (e.getKey() == e.ENTER) {
                this.onValidate();
            }
        }, this);
    },
    onValidate : function () {
        var rec = this.getLastSelectedRecord(this.gridFilter);
        if (!rec && this.action == "create") {
            popupMessage(i18n.get('label.information'), i18n.get('warning.noselection'), null, 'x-info');
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
            //not to send the violation object, used only on the client part
            recTmp.violation = undefined;
            delete recTmp.id;
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
                popupMessage(i18n.get('label.information'), i18n.get('label.filterSaved'), null, 'x-info');
                Ext.Msg.alert(i18n.get("label.information"), i18n.get("label.restartDsNeededFilter"));
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
