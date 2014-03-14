/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * 
 * This file is part of SITools2.
 * 
 * SITools2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SITools2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SITools2. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*global Ext, sitools, i18n, loadUrl */

Ext.namespace('sitools.user.modules');
/**
 * 
 * @class sitools.user.modules.cmsContextMenu
 * @cfg cms the parent cms of the toolbar
 * @cfg datastorageUrl the url of the datastorage
 * @cfg node the node selected
 * @cfg action the action to perfom, "create" or "edit" the node
 * @extends Ext.Window
 */
sitools.user.modules.chooseFileInExplorer = Ext.extend(Ext.Window, {
    
    
    
    height : 250,
    width : 410,
    layout : {
        type : 'vbox',
        align : 'stretch'
    },
    
    initComponent : function () {
        
        this.title = i18n.get("label.addContent");
        
        this.nodeText = this.node.text;
        
        var browseField = new Ext.form.TriggerField({
            name : 'link',
            fieldLabel : i18n.get('label.content'),
            datastorageUrl : this.datastorageUrl,
            onTriggerClick : function () {
		        if (!this.disabled) {
                    
		            var browser = new sitools.user.modules.datastorageBrowser({
                        datastorageUrl : this.datastorageUrl,
                        field : this,
                        formatValue : function (value) {
                            var indexDatastorageUrl = value.indexOf(this.datastorageUrl);
					        if (indexDatastorageUrl !== -1) {
					            value = value.substr(indexDatastorageUrl + this.datastorageUrl.length, value.length);
					        }
                            return value;
                        }
                        
		            });
                    browser.show();
		        }
		    }
        });
        
        
        this.form = new Ext.form.FormPanel({
            flex : 1,
            defaults : {
                anchor : "100%"
            },
            padding : 5,
            items : [{
                
                fieldLabel : i18n.get('label.contentTitle'),
                name : 'text',
                xtype : 'textfield',
                allowBlank : false                
                        
            }, {
                fieldLabel : i18n.get('label.type'),
                name : 'type',
                xtype : 'radiogroup',
                columns : 2,
                items : [ {
                    boxLabel : i18n.get("label.category"),
                    name : 'type',
                    inputValue : "category",
                    checked : true
                }, {
                    boxLabel : i18n.get("label.page"),
                    name : 'type',
                    inputValue : "page"
                }],
                disabled : (this.action === "edit") 
            }, browseField]
        });
        
        
        this.items = [this.form];
        
        this.buttonOK = new Ext.Button({
            text : i18n.get('label.ok'),
            scope : this,
	        disabled : false,
            handler : this.onValidate
        });
        


        this.buttons = [ this.buttonOK, {
            text : i18n.get('label.cancel'),
            scope : this,
            handler : function () {
                this.close();
            }
        } ];
    
        
        sitools.user.modules.chooseFileInExplorer.superclass.initComponent.call(this);
    },
    
    onRender : function () {
        sitools.user.modules.chooseFileInExplorer.superclass.onRender.apply(this, arguments);
        if (this.action === "edit") {
	        var form = this.form.getForm();
	        var attributes = this.node.attributes;
	        var rec = {};
	        rec.text = attributes.text;
	        rec.link = attributes.link;
	        if (this.node.isLeaf()) {
	            rec.type = "page";
	        } else {
	            rec.type = "category";
	        }
	        var record = new Ext.data.Record(rec);
	        form.loadRecord(record);   
        }
    },
    

    findOldNode : function (nodeName) {
        return selectNode = this.cms.tree.getRootNode().findChild('text', nodeName, true);
    },
    
    onValidate : function () {
        
        var form = this.form.getForm();
        if (!form.isValid()) {
            return;
        }
        var forceRefresh = (this.action === "create");
        var link = form.findField("link").getValue();
        if (Ext.isEmpty(link)) {
            Ext.Msg.show({
                title : i18n.get('label.warning'),
                msg : i18n.get('label.noLinkDefineCreateFile'),
                buttons : {
                    yes : i18n.get('label.yes'),
                    no : i18n.get('label.no'),
                    cancel : i18n.get('label.cancel')
                },
                fn : function (btnId, textButton, opt) {
                    if (btnId === "yes") {
                        var language = this.cms.getChosenLanguage();
                        var form = this.form.getForm();
                        var text = form.findField("text").getValue();
                        var link = "/" + language + "/" + text + ".html";
                        this.onSave(link, true, forceRefresh);
                    } 
                    else if (btnId === "no") {
                        this.onSave(undefined, false, forceRefresh);
                    }                    
                    
                },
                animEl : 'elId',
                scope : this,
                icon : Ext.MessageBox.QUESTION
            });
        } else {
            this.onSave(link, false, forceRefresh);
        }
        
        
    },
    /**
     * Method to add a file from its link. If createFile is true, also create the file on the server
     * @param {String} link the link of the file
     * @param {boolean} createFile true to create the file on the server, false otherwise
     */
    onSave : function (link, createFile, forceRefresh) {
        if (forceRefresh) {
            this.cms.refreshTree();
                Ext.Msg.show({
	                title : i18n.get('label.info'),
	                msg : i18n.get('label.refreshNeededBeforeCreate'),
	                buttons : {
	                    yes : i18n.get('label.yes'),
	                    no : i18n.get('label.no')
	                },
	                fn : function (btnId, textButton, opt) {
	                    if (btnId === "yes") {
	                        this.performSave(link, createFile);
	                    }
	                    
	                },
	                animEl : 'elId',
	                scope : this,
	                icon : Ext.MessageBox.INFO,
                    width : 300
	           });
        }
        else {
            this.performSave(link, createFile);
        }
    },
    
    performSave : function (link, createFile){
        var form = this.form.getForm();
        var text = form.findField("text").getValue();
        var type = form.findField("type").getValue().getGroupValue();
        var leaf = "page" === type;
        
        this.node = this.findOldNode(this.nodeText);
        
        if (this.action === "create") {
            this.cms.addNode(this.node, leaf, text, link, createFile, true);
        } else {
            this.cms.editNode(this.node, text, link, createFile, true);
        }
        this.close();
    }
    
});
    




    

