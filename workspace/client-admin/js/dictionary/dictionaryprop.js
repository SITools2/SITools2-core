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
Ext.namespace('sitools.component.dictionary');

Ext.define('sitools.component.dictionary.dictionaryPropPanel', {
    extend : 'Ext.Window',
	alias : 'widget.s-dictionaryprop',
    width : 700,
    height : 480,
    modal : true,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,    
    templateLoaded : false,
    //the copy of the conceptTemplate contained in the dictionary (only used when action = modify)
    conceptTemplate : null,
    mixins : {
        utils : 'js.utils.utils'
    },

    initComponent : function () {
        this.conceptTemplatesUrl = loadUrl.get('APP_URL') + loadUrl.get('APP_DICTIONARIES_TEMPLATES_URL');
        
        if (this.action == 'modify') {
            this.title = i18n.get('label.modifyDictionary');
        }
        if (this.action == 'create') {
            this.title = i18n.get('label.createDictionary');
        }
        
        this.gridTemplates = Ext.create('Ext.grid.Panel', {
            forceFit : true,
            id : 'gridTemplates',
            title : i18n.get('title.conceptTemplates'),
            store : Ext.create('Ext.data.JsonStore', {
                autoLoad : true,
                proxy : {
                    type : 'ajax',
                    url : this.conceptTemplatesUrl,
                    reader : {
                        type : 'json',
                        root : 'data',
                        idProperty : 'id'
                    }
                },
                fields : [{
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
            columns : [{
                header : i18n.get('label.name'),
                dataIndex : 'name',
                width : 100,
                sortable : true
            }, {
                header : i18n.get('label.description'),
                dataIndex : 'description',
                width : 300,
                sortable : true
            }],
            listeners : {
                scope : this,
                rowclick :  this.onTemplateClick,
                conceptTemplateLoaded : function () {
                    if (this.templateLoaded && !Ext.isEmpty(this.conceptTemplate) && this.action == "modify" && !Ext.isEmpty(this.conceptTemplate.id)) {
	                    var recIndex = this.gridTemplates.getStore().indexOfId(this.conceptTemplate.id);
	                    if (recIndex > -1) {
	                        this.gridTemplates.getSelectionModel().select(recIndex);    
	                    } else {
	                        return popupMessage("", Ext.String.format(i18n.get('label.concept.doesnt.exists'), this.conceptTemplate.name), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
	                    }
	                }
	            }                
            }           
        });
        
        //temporary panel to show the concept tab
        var panelConceptTmp = Ext.create('Ext.panel.Panel', {
            title : i18n.get('title.gridConcept'),
            id : 'gridConceptsSelect', 
            listeners : {
				scope : this, 
				activate : this.onTemplateClick
            }
        });
        
        this.dictionaryFormPanel = Ext.create('Ext.form.Panel', {
            border : false,
			padding : 10,
			id : 'dictionaryFormPanel',
            title : i18n.get('label.dictionaryInfo'),
            height : 400,
			items : [{
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
			}] 
        });
        
        this.mainTabPanel = Ext.create('Ext.tab.Panel', {
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
            templateSelected = this.getLastSelectedRecord(this.gridTemplates).data;
        } else {
            templateSelected = this.conceptTemplate;
        }
        var newConcept = {};
        for (var i = 0; i < templateSelected.properties.length; i++) {
            var property = templateSelected.properties[i];
            newConcept[property.name] = property.value; 
        }
        this.down('dictionaryGridPanel').getStore().add(newConcept);
        
        if (this.down('dictionaryGridPan#[el').getStore().getCount() == 1) {
            this.gridTemplates.getEl().mask();
        }
    },
    onDeleteConcept : function () {
        var grid = this.down('dictionaryGridPanel');
        var rec = this.getLastSelectedRecord(grid);
        if (!rec) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
            return;
        }
        grid.getStore().remove(rec);
        if (this.down('dictionaryGridPanel').getStore().getCount() === 0) {
            this.gridTemplates.getEl().unmask();
        }

    },   
    onValidate : function () {
        var rec = this.getLastSelectedRecord(this.gridTemplates);
       
        if (!rec && this.action == 'modify') {
            Ext.Msg.show({
                title : i18n.get('label.delete'),
                buttons : Ext.Msg.YESNO,
                msg : Ext.String.format(i18n.get('label.concept.doesnt.exists'), this.conceptTemplate.name) +"<br/>"+i18n.get('label.concept.use.old.anyway'),
                scope : this,
                fn : function (btn, text) {
                    if (btn == 'yes') {
                        this.doValidate();
                    }
                }

            });
        } else {
            this.doValidate();
        }
    },
    
    doValidate : function () {
        var f, putObject = {}, store, i;
        var tmp = [];
        
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
            templateSelected = this.getLastSelectedRecord(this.gridTemplates).data;
        } else {
            templateSelected = this.conceptTemplate;
        }
        if (!Ext.isEmpty(templateSelected)) {
            putObject.conceptTemplate = templateSelected;
            
            if (Ext.isEmpty(putObject.conceptTemplate.properties)) {
                putObject.conceptTemplate.properties = [];
            }
        }
        
        var gridConcept = this.down('dictionaryGridPanel');
        
        if (!Ext.isEmpty(gridConcept) && Ext.isFunction(gridConcept.getStore)) {
            store = this.down('dictionaryGridPanel').getStore();
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

    afterRender : function () {
        sitools.component.dictionary.dictionaryPropPanel.superclass.afterRender.apply(this, arguments);
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
                        var f = this.down('form').getForm();
                        var rec = {};
                        rec.id = data.id;
                        rec.name = data.name;
                        rec.description = data.description;
                        
                        f.setValues(rec);
                        
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
                        
                        var store = this.down('dictionaryGridPanel').getStore();
                        
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
                            
                            store.add(conceptOut);
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
        var rec = this.getLastSelectedRecord(this.gridTemplates);
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
        var gridConceptsSelect = Ext.create('sitools.component.dictionary.gridPanel', {
			template : template,
			selModel : Ext.create('Ext.selection.RowModel', {
				mode : 'SINGLE'
			}),
			tbar : Ext.create('sitools.widget.GridSorterToolbar', {
				gridId : "gridConceptsSelect",
				items : [{
					text : i18n.get('label.create'),
					icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_create.png',
					handler : this.onCreateConcept,
					scope : this
				}, {
					text : i18n.get('label.delete'),
					icon : loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/toolbar_delete.png',
					handler : this.onDeleteConcept,
					scope : this
				}]
			}),
			id : 'gridConceptsSelect',
			title : i18n.get('title.gridConcept'),
            editable : true
		});
        
        var tabPanel = this.down('tabpanel');
        tabPanel.remove('gridConceptsSelect');
        
        tabPanel.add(gridConceptsSelect);
        tabPanel.setActiveTab('gridConceptsSelect');
        
    },
    beforeTabChange : function (self, newTab, currentTab) {
        if (newTab.id == "gridConceptsSelect" || newTab.id == "dictionaryFormPanel") {
            var rec = this.getLastSelectedRecord(this.gridTemplates);
            
            if (!rec && Ext.isEmpty(this.conceptTemplate)) {
                popupMessage("", i18n.get('warning.noTemplateselection'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;
                return false;
            }
        }
    }
});    

