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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl*/
Ext.namespace('sitools.component.dictionary');

sitools.component.dictionary.dictionaryPropPanel = Ext.extend(Ext.Window, {
    width : 700,
    height : 480,
    modal : true,
    pageSize : 10,    
    templateLoaded : false,
    //the copy of the conceptTemplate contained in the dictionary (only used when action = modify)
    conceptTemplate : null,
    

    initComponent : function () {
        this.conceptTemplatesUrl = loadUrl.get('APP_URL') + loadUrl.get('APP_DICTIONARIES_TEMPLATES_URL');
        
        if (this.action == 'modify') {
            this.title = i18n.get('label.modifyDictionary');
        }
        if (this.action == 'create') {
            this.title = i18n.get('label.createDictionary');
        }        
        this.gridTemplates = new Ext.grid.GridPanel({
            viewConfig : {
                forceFit : true
            },
            id : 'gridTemplates',
            title : i18n.get('title.conceptTemplates'),
            store : new Ext.data.JsonStore({
                root : 'data',
                restful : true,
                proxy : new Ext.data.HttpProxy({
                    url : this.conceptTemplatesUrl,
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
                    name : 'properties'
                }],
                autoLoad : true,
                listeners : {
                    scope : this,
                    load : function () {
                        this.templateLoaded = true;
                        if (this.action == "modify") {
                            this.gridTemplates.fireEvent("conceptTemplateLoaded");
                        }
                    }
                }
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
                    width : 300,
                    sortable : true
                } ]
            }),
            
            listeners : {
                scope : this,
                rowclick :  this.onTemplateClick,
                conceptTemplateLoaded : function () {
                    if (this.templateLoaded && !Ext.isEmpty(this.conceptTemplate) && this.action == "modify" && !Ext.isEmpty(this.conceptTemplate.id)) {
	                    var recIndex = this.gridTemplates.getStore().indexOfId(this.conceptTemplate.id);
	                    if (recIndex > -1) {
	                        this.gridTemplates.getSelectionModel().selectRow(recIndex);    
	                    }
	                }
	            }                
            }           
            
        });
        
        //temporary panel to show the concept tab
        var panelConceptTmp = new Ext.Panel({
            title : i18n.get('title.gridConcept'),
            id : 'gridConceptsSelect', 
            listeners : {
				scope : this, 
				activate : this.onTemplateClick
            }
        });
        
        this.dictionaryFormPanel = new Ext.FormPanel({
            border : false,
			padding : 10,
			id : 'dictionaryFormPanel',
            title : i18n.get('label.dictionaryInfo'),
            height : 400,
			items : [ {
			    xtype : 'hidden',
			    name : 'id'
			}, {
			    xtype : 'textfield',
			    name : 'name',
			    fieldLabel : i18n.get('label.name'),
			    anchor : '100%', 
			    allowBlank : false
			}, {
			    xtype : 'textfield',
			    name : 'description',
			    fieldLabel : i18n.get('label.description'),
			    anchor : '100%'
			} ] 
        });
        
        this.mainTabPanel = new Ext.TabPanel({
            xtype : 'tabpanel',
            height : 450,
            activeTab : 0,
            id : 'tabPanelDictionary',
            items : [ this.gridTemplates, this.dictionaryFormPanel, panelConceptTmp ],
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
        this.items = [ this.mainTabPanel];
        this.listeners = {
			scope : this, 
			resize : function (window, width, height) {
				var size = window.body.getSize();
				this.mainTabPanel.setSize(size);
			}

        };

        sitools.component.dictionary.dictionaryPropPanel.superclass.initComponent.call(this);
    },

    onUpload : function () {
        // TODO gerer l'upload de fichier.
        Ext.Msg.alert('upload non impl&eacute;ment&eacute;');
    },
    onCreateConcept : function () {
        //create a new concept with the default values, specified in the template selected
        var templateSelected;
        if (Ext.isEmpty(this.conceptTemplate)) {
            templateSelected = this.gridTemplates.getSelectionModel().getSelected().data;
        } else {
            templateSelected = this.conceptTemplate;
        }
        var newConcept = {};
        for (var i = 0; i < templateSelected.properties.length; i++) {
            var property = templateSelected.properties[i];
            newConcept[property.name] = property.value; 
        }
        this.findById('gridConceptsSelect').getStore().add(new Ext.data.Record(newConcept));
        
        if (this.findById('gridConceptsSelect').getStore().getCount() == 1) {
            this.gridTemplates.getEl().mask();
        }
    },
    onDeleteConcet : function () {
        var grid = this.findById('gridConceptsSelect');
        var rec = grid.getSelectionModel().getSelected();
        if (!rec) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
            return;
        }
        grid.getStore().remove(rec);
        if (this.findById('gridConceptsSelect').getStore().getCount() === 0) {
            this.gridTemplates.getEl().unmask();
        }

    },   
    onValidate : function () {
        var rec = this.gridTemplates.getSelectionModel().getSelected();
        var tmp;
        if (!rec) {
            tmp = new Ext.ux.Notification({
                iconCls : 'x-icon-information',
                title : i18n.get('label.information'),
                html : i18n.get('warning.noTemplateselection'),
                autoDestroy : true,
                hideDelay : 1000
            }).show(document);
            return false;
        }

        var f, putObject = {}, store, i;
        tmp = [];
        
        f = this.dictionaryFormPanel.getForm();

        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }
        Ext.iterate(f.getFieldValues(), function (key, value) {
            if (key == 'image') {
                // TODO : definir une liste de mediaType et type
                putObject.image = {};
                putObject.image.url = value;
                putObject.image.type = "Image";
                putObject.image.mediaType = "Image";
            } else {
                putObject[key] = value;
            }
        }, this);
        
        var templateSelected;
        if (Ext.isEmpty(this.conceptTemplate)) {
            templateSelected = this.gridTemplates.getSelectionModel().getSelected().data;
        } else {
            templateSelected = this.conceptTemplate;
        }
        if (!Ext.isEmpty(templateSelected)) {
            putObject.conceptTemplate = templateSelected;
            
            if (Ext.isEmpty(putObject.conceptTemplate.properties)) {
				putObject.conceptTemplate.properties = [];
			}
        }
        
        
        
        var gridConcept = this.findById('gridConceptsSelect');
        if (!Ext.isEmpty(gridConcept) && Ext.isFunction(gridConcept.getStore)) {
	        store = this.findById('gridConceptsSelect').getStore();
	        if (store.getCount() > 0) {
	            putObject.concepts = [];
	                           
	            for (i = 0; i < store.getCount(); i++) {
	                var recConcept = store.getAt(i).data;
	                
	                var concept = {
	                    id : recConcept.id,
	                    name : recConcept.name,
	                    description : recConcept.description,
	                    properties : []
	                };
	                
	                for (var key in recConcept) {
	                    if (key != "id" && key != "name"
	                        && key != "description") {
	                        concept.properties.push({
	                            name : key,
	                            value : recConcept[key]
	                        });
	                    }    
	                }
	                
	                putObject.concepts.push(concept);
	            }
	        }
        }

            
        var method = (this.action == 'modify') ? "PUT" : "POST";
        
        Ext.Ajax.request({
            url : this.url,
            method : method,
            scope : this,
            jsonData : putObject,
            success : function (ret) {
                this.close();
                this.store.reload();
                // Ext.Msg.alert(i18n.get('label.information'),
                // i18n.get('msg.uservalidate'));
            },
            failure : alertFailure
        });
    },

    onRender : function () {
        sitools.component.dictionary.dictionaryPropPanel.superclass.onRender.apply(this, arguments);
        if (this.url) {
            // var gs = this.groupStore, qs = this.quotaStore;
            var i;
            if (this.action == 'modify') {
                Ext.Ajax.request({
                    url : this.url,
                    method : 'GET',
                    scope : this,
                    success : function (ret) {
                        var data = Ext.decode(ret.responseText).dictionary;

                        //load the form
                        var f = this.findById('dictionaryFormPanel').getForm();
                        var rec = {};
                        rec.id = data.id;
                        rec.name = data.name;
                        rec.description = data.description;
                        var record = new Ext.data.Record(rec);
                        f.loadRecord(record);
                        
                        if (!Ext.isEmpty(data.conceptTemplate)) {
                            //save the conceptTemplate id
                            this.conceptTemplate = data.conceptTemplate;
                        }
                        
                        this.gridTemplates.fireEvent("conceptTemplateLoaded");
                        
                        //first lets create the grid
                        this.createConceptGrid(data.conceptTemplate);
                        
                        //load the concepts                        
                        if (!data.concepts) {
                            return;
                        }
                        
                        var store = this.findById('gridConceptsSelect').getStore();
                        
                        var nbConcepts = 0;
                        for (i = 0; i < data.concepts.length; i++) {
                            nbConcepts++;
                            var conceptIn = data.concepts[i];
                            var conceptOut = {
                                id : conceptIn.id,
                                name : conceptIn.name,
                                description : conceptIn.description
                            };
                            
                            for (var j = 0; j < conceptIn.properties.length; j++) {
                                var property = conceptIn.properties[j];
                                conceptOut[property.name] = property.value;                                
                            }
                            
                            rec = new Ext.data.Record(conceptOut);
                            store.add(rec);
                        }
                        if (nbConcepts > 0) {
                            this.gridTemplates.getEl().mask();
                        }

                    },
                    failure : function (ret) {
                        var data = Ext.decode(ret.responseText);
                        Ext.Msg.alert(i18n.get('label.warning'), data.errorMessage);
                    }
                });
            }
        }
    },
    
    onTemplateClick : function (self, rowIndex,  e) {
        var rec = this.gridTemplates.getSelectionModel().getSelected();
		if (!rec) {
			return false;
		}
        var currentConceptTemplateId = rec.data.id;
        if (Ext.isEmpty(this.conceptTemplate) || this.conceptTemplate.id != currentConceptTemplateId) {
            this.createConceptGrid(rec.data);
            this.conceptTemplate = rec.data;
        }
		   
    },
    
    
    createConceptGrid : function (template) {
        var gridConceptsSelect = new sitools.component.dictionary.gridPanel({
			template : template,
			sm : new Ext.grid.RowSelectionModel({
				singleSelect : true
			}),
			tbar : {
				xtype : 'sitools.widget.GridSorterToolbar',
				gridId : "gridConceptsSelect",
				items : [{
					text : i18n.get('label.create'),
					icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_create.png',
					handler : this.onCreateConcept,
					scope : this
				}, {
					text : i18n.get('label.delete'),
					icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_delete.png',
					handler : this.onDeleteConcet,
					scope : this
				}]
			},
			id : 'gridConceptsSelect',
			title : i18n.get('title.gridConcept'),
            editable : true
		});
        
        var tabPanel = this.get("tabPanelDictionary");
        tabPanel.remove('gridConceptsSelect');
        
        tabPanel.add(gridConceptsSelect);
        
        this.doLayout();
    },
    beforeTabChange : function (self, newTab, currentTab) {
//        if (this.action == "create") {
        if (newTab.id == "gridConceptsSelect" || newTab.id == "dictionaryFormPanel") {
            var rec = this.gridTemplates.getSelectionModel().getSelected();
            if (!rec && Ext.isEmpty(this.conceptTemplate)) {
                var tmp = new Ext.ux.Notification({
                        iconCls : 'x-icon-information',
                        title : i18n.get('label.information'),
                        html : i18n.get('warning.noTemplateselection'),
                        autoDestroy : true,
                        hideDelay : 1000
                    }).show(document);
                return false;
            }
        }
//        }
    }
});    



Ext.reg('s-dictionaryprop', sitools.component.dictionary.dictionaryPropPanel);
