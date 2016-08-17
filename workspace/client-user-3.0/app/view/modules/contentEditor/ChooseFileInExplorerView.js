/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

Ext.namespace('sitools.user.view.modules.contentEditor');
/**
 * 
 * @class sitools.user.modules.cmsContextMenu
 * @cfg cms the parent cms of the toolbar
 * @cfg datastorageUrl the url of the datastorage
 * @cfg node the node selected
 * @cfg action the action to perfom, "create" or "edit" the node
 * @extends Ext.Window
 */
Ext.define('sitools.user.view.modules.contentEditor.ChooseFileInExplorerView', {
    extend : 'Ext.window.Window',

    height : 250,
    width : 460,
    layout : {
        type : 'vbox',
        align : 'stretch'
    },
    
    initComponent : function () {
        this.icon = loadUrl.get('APP_URL') + '/common/res/images/icons/file-add.png';

        this.title = i18n.get("label.properties");
        this.nodeUuid = this.node.get('uuid');
        
        this.browseField = Ext.create('Ext.form.field.Trigger', {
            name : 'link',
            datastorageUrl : this.datastorageUrl,
            hidden : this.action === "create" ,
            allowBlank : this.action === "create",
            fieldLabel : this.action === "edit" ? i18n.get('label.contentLink') : undefined,
            onTriggerClick : function () {
		        if (!this.disabled) {
                    
		            var browser = Ext.create('sitools.user.view.modules.dataStorageExplorer.DataStorageBrowserView', {
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
        
        this.form = Ext.create('Ext.form.Panel', {
            flex : 1,
            defaults : {
                anchor : "100%"
            },
            padding : 10,
            border : false,
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
            },
            {
                fieldLabel : i18n.get('label.contentLink'),
                name : 'createLinkType',
                xtype : 'radiogroup',
                columns : 2,
                items : [{
                    boxLabel : i18n.get("label.newPage"),
                    name : 'linkType',
                    inputValue : "newpage",
                    checked : true
                }, {
                    boxLabel : i18n.get("label.existingResource"),
                    name : 'linkType',
                    inputValue : "existingresource"
                }],
                listeners : {
                    scope : this,
                    change : function (radioGroup, newValue, oldValue) {
                        this.browseField.setVisible(newValue.linkType === "existingresource");
                        this.browseField.allowBlank = (newValue.linkType !== "existingresource");
                    }
                },
                hidden : (this.action === "edit") 
            }, this.browseField]
        });
        
        this.items = [this.form];
        
        this.buttonOK = Ext.create('Ext.Button', {
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
        
       this.callParent(arguments);
    },

    afterRender : function () {
        this.callParent(arguments);
        if (this.action === "edit") {
	        var form = this.form.getForm();
	        var data = this.node.getData();
	        var rec = {};
	        rec.text = data.text;
	        rec.link = data.link;
	        if (this.node.isLeaf()) {
	            rec.type = "page";
	        } else {
	            rec.type = "category";
	        }

//	        form.loadRecord(rec);
	        form.setValues(rec);
        }
    },

    findOldNode : function (nodeUuid) {
        return selectNode = this.cms.tree.getRootNode().findChild('uuid', nodeUuid, true);
    },
    
    onValidate : function () {
        
        var form = this.form.getForm();
        if (!form.isValid()) {
            return;
        }
        var forceRefresh = (this.action === "create");
//        var linkType = form.findField("createLinkType").getValue().getGroupValue();
        var linkType = form.findField("createLinkType").getValue().linkType;

        if(this.action === "create" && linkType === "newpage") {
            var language = this.cms.getChosenLanguage();
            var text = form.findField("text").getValue();
            var link = "/" + language + "/" + text + ".html";

            Ext.Ajax.request({
                url : this.cms.url + link,
                method : "HEAD",
                scope : this,
                success : function () {
                    Ext.Msg.alert(i18n.get("label.warning"), i18n.get("label.cmsfileAlreadyExists"));
                }, failure : function () {
                    this.beforeSave(link, true, forceRefresh);
                }
            });
            
        } else {
            var link = this.browseField.getValue();
            this.beforeSave(link, false, forceRefresh);
        }
    },
    
    beforeSave : function (link, createFile, forceRefresh) {
        Ext.Ajax.request({
            url : this.cms.url,
            method : "HEAD",
            scope : this,
            success : function (response, ops) {
                if(response.getResponseHeader("Last-Modified") === this.cms.lastModified) {
                    this.onSave(link, createFile, false);
                }
                else {
                    this.onSave(link, createFile, forceRefresh);
                }
            }, failure : alertFailure
        });
    },
    
    /**
     * Method to add a file from its link. If createFile is true, also create the file on the server
     * @param {String} link the link of the file
     * @param {boolean} createFile true to create the file on the server, false otherwise
     */
    onSave : function (link, createFile, forceRefresh) {
        if (forceRefresh) {
//            this.cms.refreshTree();

            Ext.Msg.show({
                title : i18n.get('label.info'),
                msg : i18n.get('label.refreshNeededBeforeCreate'),
                buttons : Ext.MessageBox.YESNO,
                fn : function (btnId, textButton, opt) {
                    if (btnId === "yes") {
                        this.performSave(link, createFile);
                        this.cms.refreshTree();
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
    
    performSave : function (link, createFile) {
        var form = this.form.getForm();
        var text = form.findField("text").getValue();
        var typeField = form.findField("type").getValue();
        var leaf = "page" === typeField.type;
        
        this.node = this.findOldNode(this.nodeUuid);
        if(!Ext.isEmpty(this.node)){
            try {
                if (this.action === "create") {
                    this.cms.addNode(this.node, leaf, text, link, createFile, true);
                } else {
                    this.cms.editNode(this.node, text, link, createFile, true);
                }
                this.close();
            } catch(e){
                this.close();
                throw e;
            }
        } else {
            Ext.Msg.alert(i18n.get("label.warning"), i18n.get("label.nodeRemoved") + "<br/>" + i18n.get("label.treeNotSaved"), function () {
                this.close();
                this.cms.refreshTree();
            }, this);
        }
    }
});