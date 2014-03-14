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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, ImageChooser, loadUrl*/
/*
 * @include "../datasets/selectColumn.js"
 */
Ext.namespace('sitools.component.sva');

sitools.component.sva.svaProp = Ext.extend(Ext.Window, {
    width : 700,
    height : 480,
    modal : true,
    resizable : false,
    classChosen : "",
    defaultRunType : "SVA_FORCE_RUN_SYNC",
    defaultMethod : "POST",

    initComponent : function () {
        this.svaUrl = loadUrl.get('APP_URL') + loadUrl.get('APP_SVA_PLUGINS_URL');
		
        this.title = this.action == "create" ? i18n.get('label.createSVA') : i18n.get('label.modifySVA'); 

        this.crudStore = this.store;
        
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

        this.gridSva = new Ext.grid.GridPanel({
            id : 'gridSva',
            title : i18n.get('title.svaClass'),
            store : new Ext.data.JsonStore({
                root : 'data',
                restful : true,
                proxy : new Ext.data.HttpProxy({
                    url : this.svaUrl,
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
                }/*, {
                    header : i18n.get('label.description'),
                    dataIndex : 'description',
                    width : 300,
                    sortable : true
                }*/, {
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

        var runTypeStore = new Ext.data.ArrayStore({
            fields : [ {
                name : 'runType'
            }, {
                name : 'label'
            } ],
            data : [ [ 'SVA_FORCE_RUN_SYNC', 'SVA_FORCE_RUN_SYNC' ], [ 'SVA_FORCE_RUN_ASYNC', 'SVA_FORCE_RUN_ASYNC' ],
                    [ 'SVA_DEFAULT_RUN_SYNC', 'SVA_DEFAULT_RUN_SYNC' ], [ 'SVA_DEFAULT_RUN_ASYNC', 'SVA_DEFAULT_RUN_ASYNC' ] ]
        });

        this.fieldMappingFormPanel = new Ext.FormPanel({
            padding : 10,
            id : 'fieldMappingFormPanel',
            defaultType : 'textfield',
            title : i18n.get('title.svaDetails'),
            items : [ {
                fieldLabel : i18n.get('label.label'),
                name : 'label',
                anchor : '100%',
                allowBlank: false
            }, {
                xtype : 'combo',
                mode : 'local',
                triggerAction : 'all',
                editable : false,
                name : 'runType',
                fieldLabel : i18n.get('label.runType'),
                width : 100,
                store : runTypeStore,
                valueField : 'runType',
                displayField : 'label',
                anchor : "100%",
                value : "SVA_FORCE_RUN_SYNC"
            }, {
                xtype : 'sitoolsSelectImage',
                name : 'image',
                fieldLabel : i18n.get('label.image'),
                anchor : '100%',
                growMax : 400
            }, {
                xtype : 'radiogroup',
                fieldLabel : i18n.get('label.method'),
                name : 'method',
                columns : 1,
                items : [ {
                    boxLabel : i18n.get("headers.post"),
                    name : 'method',
                    inputValue : "POST",
                    checked : true
                }, {
                    boxLabel : i18n.get("headers.get"),
                    name : 'method',
                    inputValue : "GET"
                } ]
            }, {
                fieldLabel : i18n.get('label.urlAttach'),
                name : 'urlAttach',
                anchor : '100%',
                readOnly : true,
                hidden : (this.action == "create"),
                hideLabel : (this.action == "create")
            }]

        });

        this.proxyFieldMapping = new Ext.data.HttpProxy({
            url : "/tmp",
            restful : true,
            method : 'GET'
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
            id : 'gridFieldMapping',
            layout : 'fit',
            title : i18n.get('title.parametersMapping'),
            store : new Ext.data.JsonStore({
                root : 'sva.parameters',
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
                } ]
            }),
            cm : new Ext.grid.ColumnModel({
                // specify any defaults for each column
                defaults : {
                    sortable : true
                // columns are not sortable by default
                },
                columns : [ {
                    header : i18n.get('label.name'),
                    dataIndex : 'name',
                    width : 100,
                    sortable : true
                }, {
                    header : i18n.get('label.description'),
                    dataIndex : 'description',
                    width : 250,
                    sortable : false
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
                text : i18n.get("label.svaErrorValidationNotification")
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
            }
        });

        // this.fieldMappingPanel = new Ext.Panel({
        // id : 'gridFieldMapping',
        // title : i18n.get('title.fieldMapping'),
        // items : [ this.fieldMappingFormPanel, this.gridFieldMapping ],
        // layout : 'fit',
        // layoutConfig : {
        // align : 'stretch',
        // pack : 'start'
        // }
        //
        // });

        this.tabPanel = new Ext.TabPanel({
            height : 450,
            activeTab : 0,
            items : (this.action == "create") ? [ this.gridSva, this.fieldMappingFormPanel, this.gridFieldMapping ] : [ this.fieldMappingFormPanel,
                    this.gridFieldMapping ],
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

        this.items = [ this.tabPanel ];

        sitools.component.sva.svaProp.superclass.initComponent.call(this);
    },
    onUpload : function () {
        // TODO gerer l'upload de fichier.
        function validate(data, config) {

            config.fieldUrl.setValue(data.url);
        }
        var chooser = new ImageChooser({
            url : loadUrl.get('APP_URL') + '/client-admin/res/json/componentList.json',
            width : 515,
            height : 350,
            fieldUrl : this.ownerCt.items.items[0]
        });
        chooser.show(document, validate);
        // Ext.Msg.alert('upload non impl&eacute;ment&eacute;');
    },

    beforeTabChange : function (self, newTab, currentTab) {
        if (this.action == "create") {
            if (newTab.id == "fieldMappingFormPanel" || newTab.id == "gridFieldMapping") {
	            var rec = this.gridSva.getSelectionModel().getSelected();
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
    
    onClassClick : function (self, rowIndex,  e) {
		if (this.action == "create") {
			var rec = this.gridSva.getSelectionModel().getSelected();
			if (!rec) {
//				var tmp = new Ext.ux.Notification({
//						iconCls : 'x-icon-information',
//						title : i18n.get('label.information'),
//						html : i18n.get('warning.noselection'),
//						autoDestroy : true,
//						hideDelay : 1000
//				    }).show(document);
				return false;
			}

			var className = rec.data.className;
			if (className != this.classChosen) {
				// this.proxyFieldMapping.setUrl(this.svaUrl + "/" +
				// className + "/" + this.datasetId);
				var url = this.svaUrl + "/" + className + "/" + this.datasetId;
				var store = this.gridFieldMapping.getStore();
				store.removeAll();
				Ext.Ajax.request({
					url : url,
					method : 'GET',
					scope : this,
					success : function (ret) {
						var json = Ext.decode(ret.responseText);
						if (!json.success) {
							Ext.Msg.alert(i18n.get('label.warning'),
									json.message);
							return false;
						}
						var sva = json.sva;
						// fill the parameters
						var parameters = sva.parameters;
						if (parameters !== null) {
							var store = this.gridFieldMapping.getStore();
							for (var i = 0; i < parameters.length; i++) {
								var recTmp = new Ext.data.Record(parameters[i]);
//                                recTmp.set("value", "");
//                                recTmp.set("attachedColumn", "");
								store.add(recTmp);
							}
						}
						// fill the default values
						var form = this.fieldMappingFormPanel.getForm();
						var rec = {};
						rec.runType = (Ext.isEmpty(sva.runType))
								? this.defaultRunType
								: sva.runType;
						rec.method = (Ext.isEmpty(sva.method))
								? this.defaultMethod
								: sva.method;
						form.loadRecord(new Ext.data.Record(rec));
						this.classChosen = className;
					}
				});

			}

		}

	},

    afterRender : function () {
        sitools.component.sva.svaProp.superclass.afterRender.apply(this, arguments);

        if (this.action == "modify") {

            Ext.Ajax.request({
                url : this.urlDatasets + "/" + this.datasetId + this.svaUrlPart + "/" + this.svaId,
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var json = Ext.decode(ret.responseText);
                    if (!json.success) {
                        Ext.Msg.alert(i18n.get('label.warning'), json.message);
                        return false;
                    }
                    this.fillFormAndGrid(json.sva);
                },
                failure : alertFailure
            });
        }
    },

    fillFormAndGrid : function (sva) {
        var form = this.fieldMappingFormPanel.getForm();
        if (!Ext.isEmpty(sva)) {
            var rec = {};
            rec.runType = sva.runType;
            rec.label = sva.label;
            rec.image = sva.image.url;
            rec.method = sva.method;
            rec.urlAttach = sva.urlAttach;
            form.loadRecord(new Ext.data.Record(rec));

            var parameters = sva.parameters;
            if (parameters !== null) {
                var store = this.gridFieldMapping.getStore();
                for (var i = 0; i < parameters.length; i++) {
                    var recTmp = new Ext.data.Record(parameters[i]);
                    store.add(recTmp);
                }
            }
        }
    },

    onValidate : function () {
        var rec;
        if (this.action == "create") {
            rec = this.gridSva.getSelectionModel().getSelected();
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
        var jsonReturn = {};
        var form = this.fieldMappingFormPanel.getForm();
        if (!form.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }
        Ext.iterate(form.getFieldValues(), function (key, value) {
            if (key == 'image') {
                // TODO : definir une liste de mediaType et type
                jsonReturn.image = {};
                jsonReturn.image.url = value;
                jsonReturn.image.type = "Image";
                jsonReturn.image.mediaType = "Image";
            } else {
                if (key == "method") {
                    jsonReturn[key] = value.getGroupValue();
                } else if (key != "urlAttach") {
                    jsonReturn[key] = value;
                }

            }

        });

        var parameters = [];
        if (this.action == "create") {
            rec = this.gridSva.getSelectionModel().getSelected();
            var sva = rec.data;
            jsonReturn.className = sva.className;
            jsonReturn.name = sva.name;
            jsonReturn.description = sva.description;
            jsonReturn.classVersion = sva.classVersion;
            jsonReturn.classAuthor = sva.classAuthor;
            jsonReturn.classOwner = sva.classOwner;

        } else {
            jsonReturn.id = this.svaId;
            jsonReturn.status = this.status;

        }

        var storeField = this.gridFieldMapping.getStore();

        for (var i = 0; i < storeField.getCount(); i++) {
            var recTmp = storeField.getAt(i).data;
            recTmp.violation = undefined;
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
        var url = this.urlDatasets + "/" + this.datasetId + this.svaUrlPart, method;
        if (this.action == "modify") {
            url += "/" + this.svaId;
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
                    html : i18n.get('label.svaSaved'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);

                this.parent.getStore().reload();
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

    }

});
