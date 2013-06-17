/*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 * Help Module
 * @class sitools.user.modules.contentEditorModule
 * @cfg {Array} listProjectModulesConfig, the list of paramaters to set the module with
 * @extends Ext.Panel
 */
sitools.user.modules.contentEditorModule = Ext.extend(Ext.Panel, {
    /**
     * the node to activate
     * @type Ext.tree.TreeNode
     */
    activeNode : null,
    initComponent : function () {
        
        this.templateHtmlFile = '<base href="'+loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_URL')+'${directory.attachUrl}/">' +
                '<meta http--equiv="content-type" content="text/html;charset=UTF-8">' +
                '<title></title>';
        
        
        this.CONST_URLDATASTORAGE = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_URL');
        
        // DATASTORAGE NAME CONFIGURABLE
        // this.datastorageSrc = "/postelDatastorageDev";
        // this.datastorageDest = "/postelDatastorageProd";
        // this.directoryImageUrl = loadUrl.get('APP_URL') +
        // "/datastorage/user/postel-site/images";
        // this.dynamicUrlDatastorage = loadUrl.get('APP_URL') +
        // "/datastorage/user/postel-site";
        
        Ext.each(this.listProjectModulesConfig, function (config) {
            if (config.value != "" && config.value !== undefined) {
                switch (config.name){
                case "dynamicUrlDatastorage" :
                    this.dynamicUrlDatastorage = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_URL') + config.value;
                    break;
                    
                case "nameDatastorageSrc" :
                    this.datastorageSrc = "/" + config.value;
                    break;
                    
                case "nameDatastorageDest" :
                    this.datastorageDest = "/" + config.value;
                    break;
                    
                /*case "imageDatastorageDirectory" :
                    this.directoryImageUrl = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_URL') + config.value;
                    break;*/    
                    
                case "siteName" :
                    this.siteName = config.value;
                    break;    
                }
            }
        }, this);
        
        
        this.jsonUrlTemplate = this.dynamicUrlDatastorage + '/json/data_{locale}.json';
        
        var localeStr = locale.getLocale();
        var jsonUrl = this.jsonUrlTemplate.replace("{locale}", localeStr);
        
        this.directoryImageUrl = this.dynamicUrlDatastorage + "/images";
        
        
        this.mediaType = "text/html";
        
        this.layout = "border";
        var htmlReaderCfg = {
            defaults : {
                padding : 10
            },
            layout : 'fit',
            region : 'center'
        };

        if (!Ext.isEmpty(this.cfgCmp) && !Ext.isEmpty(this.cfgCmp.activeNode)) {
            this.activeNode = this.cfgCmp.activeNode;
        } else {
            htmlReaderCfg.defaultSrc = this.url;
        }
        
        var textTooltip = String.format(i18n.get('label.runCopyInfo'), this.datastorageSrc, this.datastorageDest);
        
        this.tree = new Ext.tree.TreePanel({
            id : 'treepanel',
            region : 'west',
            animate : true,
            width : 220,
            rootVisible : false,
            autoScroll : true,
            split : true,
            collapsible : true,
            enableDD : true,
            title : i18n.get('label.sitePlan'),
            tbar : new sitools.user.modules.cmsTreeToolbar({
                cms : this,
                textTooltip : textTooltip
            }),
            contextMenu: new sitools.user.modules.cmsContextMenu({
                cms : this
            }),
            root : {
                text : "root",
                nodeType : 'async'
            },
            loader : new Ext.tree.TreeLoader({
                requestMethod : 'GET',
                url : jsonUrl,
                createNode : function (attr) {
                    var listeners = {
                        scope : this,
                        beforeappend : function (tree, parent, item) {
                            if (item.isLeaf()) {
                                if (item.attributes.sync) {
                                    item.setIcon(loadUrl.get('APP_URL') + '/common/res/images/icons/valid.png');
                                }
                                else {
                                    item.setIcon(loadUrl.get('APP_URL') + '/common/res/images/icons/unvalid.png');
                                }
                            }
                            return true;
                        }
                    };
                    Ext.apply(attr, {
                        listeners : listeners
                    });
                    // apply baseAttrs, nice idea Corey!
                    if(this.baseAttrs){
                        Ext.applyIf(attr, this.baseAttrs);
                    }
                    if(this.applyLoader !== false && !attr.loader){
                        attr.loader = this;
                    }
                    if(Ext.isString(attr.uiProvider)){
                       attr.uiProvider = this.uiProviders[attr.uiProvider] || eval(attr.uiProvider);
                    }
                    if(attr.nodeType){
                        return new Ext.tree.TreePanel.nodeTypes[attr.nodeType](attr);
                    }else{
                        return attr.leaf ?
                                    new Ext.tree.TreeNode(attr) :
                                    new Ext.tree.AsyncTreeNode(attr);
                    }
                },
                listeners : {
                    scope : this,
                    load : function (node) {
                        if (!Ext.isEmpty(this.activeNode)) {
                            if (!Ext.isEmpty(node)) {
                                this.tree.selectPath(node.getPath());
                                this.treeAction(node);
                            } else {
                                Ext.Msg.alert(i18n.get('label.warning'), i18n.get('msg.nodeundefined'));
                            }
                        }
                    },
                    loadException : function (loader, node, response) {
                        Ext.Msg.show({
							title : i18n.get('label.warning'),
							msg : i18n.get('label.noJsonFileFound'),
							buttons : Ext.Msg.YESNOCANCEL,
							fn : function (btnId, text, opt) {
								if (btnId === "yes") {
									this.createEmptyJson(loader.url);
								}
							},
							animEl : 'elId',
							scope : this,
							icon : Ext.MessageBox.QUESTION
						});
                    }
                }
            }),
            listeners : {
                scope : this,
                click : function (node) {
//                    if (node.isLeaf()){
                        this.treeAction(node);
//                    }
                },
                contextmenu : function(node, e) {
                    this.tree.getSelectionModel().select(node, e, true);
                    var c = node.getOwnerTree().contextMenu;
                    if (!node.isLeaf()){
                        c.getComponent('manage-node').setVisible(false);
                    }
                    else {
                        c.getComponent('manage-node').setVisible(true);
                    }
                    c.contextNode = node;
                    c.showAt(e.getXY());
                },
                render : function () {
                    this.tree.getRootNode().expand(true);
                },
                dragdrop : function (tree, node, dd, e){
                	this.createJsonTree();	
                }
            }
        });

        Ext.QuickTips.init();
        
        this.saveButton = new Ext.Button({
                xtype : 'button',
                text : i18n.get('label.saveFile'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/save.png',
                scope : this,
                handler : this.onSave
            });
        
        this.htmlEditor = new Ext.form.HtmlEditor({
            directoryImageUrl : this.directoryImageUrl,
            plugins : Ext.ux.form.HtmlEditor.plugins(),
            id : 'htmlEditorId',
            region : 'center',
            listeners : { 
                scope : this,
                afterrender : function () {
                    var tbar = this.htmlEditor.getToolbar();
                    tbar.enableOverflow = true;
                }
            }
        });
        
        this.viewerEditorPanel = new Ext.ux.ManagedIFrame.Panel({
        	id : 'viewerEditorPanel',
            region : 'south',
            autoScroll : true,
            hidden : true
        });
            
        this.contentPanel = new Ext.Panel({
            id:'content-editor',
            layout : 'border',
            region : 'center',
            bbar : ['->', this.saveButton],
            items : [this.htmlEditor, this.viewerEditorPanel]
        });
        
        this.items = [this.tree, this.contentPanel];
        
        sitools.user.modules.contentEditorModule.superclass.initComponent.call(this);
    },
    
    onRender : function () {
        sitools.user.modules.contentEditorModule.superclass.onRender.apply(this, arguments);
        this.setEditable(false);
        if (this.url) {
            Ext.Ajax.request({
                url : this.url,
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var data = ret.responseText;
                    this.findByType('htmleditor')[0].setValue(data);
                    this.tree.getRootNode().expand(true);
                },
                failure : alertFailure
            });
        }
    },
    
    /**
     * Action executed ; 'this' refers to this component
     * 
     * @param node
     * @returns
     */
    treeAction : function (node) {
        // Getting node urlArticle
        var nodeLink = node.attributes.link;

        if (Ext.isEmpty(nodeLink)) {
            Ext.Msg.show({
                title : i18n.get('label.warning'),
                msg : i18n.get('label.noLinkDefineEditNode'),
                buttons : Ext.Msg.YESNO,
                scope : this,
                fn : function (btnId, textButton, opt) {
                    if (btnId === "yes") {
                        var windowLink = new sitools.user.modules.chooseFileInExplorer({
	                        datastorageUrl : this.dynamicUrlDatastorage,
	                        node : node,
	                        cms : this,
	                        action : "edit"
                        });
                        windowLink.show();
                    }
                }
            });
            return;
        }
        
        if (nodeLink.indexOf("http://") == -1) {
            this.url = this.dynamicUrlDatastorage;
            this.newUrl = this.url + nodeLink;
            this.setEditable(true);
            this.hideViewer();
            
        } else {
            this.newUrl = nodeLink;  
            this.setEditable(false);
        }
        
        if (this.isDisplayable(nodeLink) && nodeLink.indexOf("http://") == -1){
            Ext.Ajax.request({
                url : this.newUrl,
                method : 'GET',
                scope : this,
                params : {
                    processTemplate : true
                },
                success : function (ret) {
                    var contentType = ret.getResponseHeader("content-type");
                    if (contentType.contains("text")) {
                        
                        
                        var data = ret.responseText;
                        
                        //add the base name to the header of the IFrame
                        //                if(this.htmlEditor.iframe.contentDocument.getElementsByTagName("base").length == 0){
                        //	                var base = document.createElement("base");
                        //	                base.href = this.dynamicUrlDatastorage + "/";
                        //	                this.htmlEditor.iframe.contentDocument.getElementsByTagName("head")[0].appendChild(base);
                        //                }
                        this.findByType('htmleditor')[0].setValue(data);
                        this.setEditable(true);
                    } else {
                        this.findByType('htmleditor')[0].setValue(i18n.get("label.cannotEditOtherThanText"));
                        this.setEditable(false);
                    }
                    
                    
                },
                failure : function(ret) {
                    var data = ret.responseText;
                    this.findByType('htmleditor')[0].setValue(data);
                    this.setEditable(false);
                }
            });
            
            this.hideViewer();
        }
        else {
        	this.displayViewer(this.newUrl);
        }
    },
    
    changeIcon : function (node) {
        if (node.isLeaf()) {
            if (node.attributes.sync) {
                node.setIcon(loadUrl.get('APP_URL') + '/common/res/images/icons/valid.png');
            }
            else {
                node.setIcon(loadUrl.get('APP_URL') + '/common/res/images/icons/unvalid.png');
            }
        }
    },
    
    onSave : function () {
        var text = this.htmlEditor.getValue();
        
        if (text.indexOf("<base href=\"" + this.dynamicUrlDatastorage + "/\"") !== -1) {
            text = text.replace("<base href=\"" + this.dynamicUrlDatastorage + "/\"",
					"<base href=\"" + loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_URL') + "${directory.attachUrl}/\"");
        }
        
        Ext.Ajax.request({
            url : this.newUrl,
            method : 'PUT',
            scope : this,
            headers : {
                'Content-Type' : this.mediaType
            },
            jsonData : text,
            success : function (ret) {
                new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('label.changeSaved'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);
            },
            failure : alertFailure
        });
    },
    /**
     * Add a new node to the tree
     * @param {TreeNode} the parent or the sibling node to add the leaf
     * @param {boolean} leaf if the node to create is a leaf or not
     * @param {text} the text of the node
     * @param {link} the link of the node
     * @param {boolean} createFile true to create the corresponding file, false otherwise
     * @param {boolean} saveJson createFile true to save to save the json, false otherwise
     */
    addNode : function (node, leaf, text, link, createFile, saveJson) {
        this.url = this.dynamicUrlDatastorage;
        
        var nodeToAdd;
        if (node.isLeaf()) {
            nodeToAdd = node.parentNode;
        }
        else {
            nodeToAdd = node;
        }
        
//        if (!link.match(".html") && !(link.indexOf("http://") === 0)) {
//            link += ".html";
//        } 
        
        if (leaf) {
	        nodeToAdd.appendChild({
	            "text" : text,
	            "link" : link,
	            "leaf" : true,
	            "icon" : loadUrl.get('APP_URL') + '/common/res/images/icons/valid.png',
	            "sync" : false
	        });
        } else {
            nodeToAdd.appendChild({
                "text" : text,
                "link" : link,
                "leaf" : false,
                "children" : []
            });
        }
        
        if (createFile) {
	        Ext.Ajax.request({
	            url : this.url + link,
	            method : 'PUT',
	            scope : this,
	            headers : {
	                'Content-Type' : this.mediaType
	            },
	            jsonData : this.templateHtmlFile+ text,
	            success : function (ret) {
	                new Ext.ux.Notification({
	                    iconCls : 'x-icon-information',
	                    title : i18n.get('label.information'),
	                    html : i18n.get('label.fileCreated'),
	                    autoDestroy : true,
	                    hideDelay : 1000
	                }).show();
	            },
	            failure : alertFailure
	        });
        }
        if (saveJson) {
            this.createJsonTree();
        }
    },
    
    editNode : function (nodeToEdit, text, link, createFile, saveJson) {
        
        nodeToEdit.setText(text);
        
//        if (!link.match(".html") && !(link.indexOf("http://") === 0)) {
//            link += ".html";
//        }
        
        nodeToEdit.attributes.link = link;
        nodeToEdit.attributes.text = text;
        
        
        if (createFile) {
            Ext.Ajax.request({
                url : this.url + link,
                method : 'PUT',
                scope : this,
                headers : {
                    'Content-Type' : this.mediaType
                },
                jsonData : this.templateHtmlFile + text,
                success : function (ret) {
                    new Ext.ux.Notification({
                        iconCls : 'x-icon-information',
                        title : i18n.get('label.information'),
                        html : i18n.get('label.fileCreated'),
                        autoDestroy : true,
                        hideDelay : 1000
                    }).show();
                },
                failure : alertFailure
            });
        }
        if (saveJson) {
            this.createJsonTree();
        }
        
    },
    
    createJsonTree : function () {
        var root = this.tree.getRootNode();
        var tree = [];

        var childs = root.childNodes;
        var i;

        for (i = 0; i < childs.length; i++) {
            this.getAllNodes(childs[i], tree);
        }
        var jsonUrl = this.tree.getLoader().url;
        
        // Save json changed
        Ext.Ajax.request({
            url : jsonUrl,
            method : 'PUT',
            scope : this,
            headers : {
                'Content-Type' : 'application/json'
            },
            jsonData : tree,
            failure : function (response, opts){
                Ext.Msg.alert(response.status + " " +response.statusText, response.responseText);
            }
        });
    },

    getAllNodes : function (root, parent) {
        var node = {};
        if (Ext.isEmpty(root)) {
            return;
        } else if (root.isLeaf()) {
            node = {
                text : root.text,
                leaf : root.leaf,
                icon : root.attributes.icon,
                sync : root.attributes.sync,
                link : root.attributes.link
            };
            parent.push(node);
        } else {
            node = {
                text : root.text,
                leaf : false,
                link : root.attributes.link,
                children : []
            };
            parent.push(node);

            // we call recursively getAllNodes to get all childNodes
            var childs = root.childNodes;
            var i;
            for (i = 0; i < childs.length; i++) {
                this.getAllNodes(childs[i], node.children);
            }
        }
    },
    /**
     * Delete a node in the tree. If deleteFile is true, also delete the file on the server
     * @param {TreeNode} node the node to delete
     * @param {boolean} deleteFile true to delete the file on the server, false otherwise
     * @param {boolean} saveJson createFile true to save to save the json, false otherwise
     */
    deleteNode : function (node, deleteFile, saveJson){
        this.htmlEditor.setValue("");
        this.setEditable(false);
        if(deleteFile){
	        this.url = this.dynamicUrlDatastorage;
	        
	        var deleteUrl;
	        if (node.isLeaf()) {
	            deleteUrl = this.url + node.attributes.link;
	        }
	        else{
	            deleteUrl = this.url + node.attributes.link;
	        }
	        Ext.Ajax.request({
	            url : deleteUrl + "?recursive=true",
	            method : 'DELETE',
	            scope : this,
	            success : function (ret) {
	                node.remove(true);
                    if (saveJson) {
                        this.createJsonTree();
                    }
	
	                new Ext.ux.Notification({
	                    iconCls : 'x-icon-information',
	                    title : i18n.get('label.information'),
	                    html : i18n.get('label.fileDeleted'),
	                    autoDestroy : true,
	                    hideDelay : 1000
	                }).show(document);
	            },
	            failure : function (response, opts){
	                Ext.Msg.alert(response.status + " " +response.statusText, response.responseText);
	            }
	        });
        } else {
            node.remove(true);
	        if (saveJson) {
                this.createJsonTree();
            }
        }
        
    },
    
    manageNodes : function (node, action){
        if (node.isLeaf()){
            switch (action){
            case "valid":
                this.onValid(node);
                break;
            
            case "unvalid":
                this.unValid(node);
                break;
            }
        }
        this.createJsonTree();
    },
    
    onValid : function (item){
        if (!item.attributes.sync){
            item.attributes.sync = true;
            
            this.changeIcon(item);
            
            new Ext.ux.Notification({
                iconCls : 'x-icon-information',
                title : i18n.get('label.information'),
                html : i18n.get('label.fileValid'),
                autoDestroy : true,
                hideDelay : 1000
            }).show(document);
        }
        else {
            new Ext.ux.Notification({
                iconCls : 'x-icon-information',
                title : i18n.get('label.information'),
                html : i18n.get('label.alreadyValid'),
                autoDestroy : true,
                hideDelay : 1000
            }).show(document);
        }
    },
    
    unValid : function (item){
        if (item.attributes.sync){
            item.attributes.sync = false;
            
            this.changeIcon(item);
            
            new Ext.ux.Notification({
                iconCls : 'x-icon-information',
                title : i18n.get('label.information'),
                html : i18n.get('label.fileInvalid'),
                autoDestroy : true,
                hideDelay : 1000
            }).show(document);
        }
        else {
            new Ext.ux.Notification({
                iconCls : 'x-icon-information',
                title : i18n.get('label.information'),
                html : i18n.get('label.alreadyInvalid'),
                autoDestroy : true,
                hideDelay : 1000
            }).show(document);
        }
    },
    
    runCopy : function (){
        var copyUrl = this.CONST_URLDATASTORAGE + "/copy" + this.datastorageSrc + this.datastorageDest;
        
        Ext.Ajax.request({
            url : copyUrl,
            method : 'PUT',
            scope : this,
            success : function (ret) {
                new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('label.datastorage.copied'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);
            },
            failure : function (response, opts){
                Ext.Msg.alert(response.status + " " +response.statusText, response.responseText);
            }
        });
    },
    
    createEmptyJson : function (url){
        var json = [];
        
        var rootNode = {
            "text" : this.siteName,
            "id": this.siteName + "Id",
            "leaf": false,
            "link": "",
            "children" : []
        };
        
        json.push(rootNode);
        
        Ext.Ajax.request({
            url : url,
            method : 'PUT',
            scope : this,
            jsonData : json,
            success : function (ret){
                new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('label.jsonFileCreated'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);
                this.tree.getLoader().load(this.tree.getRootNode());
            },
            headers : {
                'Content-Type' : 'application/json'
            },
            failure : function (response, opts){
                Ext.Msg.alert(response.status + " " +response.statusText, response.responseText);
            }
        });
    },
    
    
    checkJsonUrlValidation : function (){
        var jsonUrl = this.tree.getLoader().url;
        Ext.Ajax.request({
            url : jsonUrl,
            method : 'GET',
            scope : this,
            failure : function (response, opts){
                Ext.Msg.show({
                       title: i18n.get('label.warning'),
                       msg: i18n.get('label.noJsonFileFound'),
                       buttons: Ext.Msg.YESNOCANCEL,
                       fn: function (btnId, text, opt){
                               if (btnId == "yes"){
                                   this.createEmptyJson(jsonUrl);
                               }
                           },
                       animEl: 'elId',
                       scope : this,
                       icon: Ext.MessageBox.QUESTION
                });
            }
        });
    },
    
    displayViewer : function (url) {
    	this.viewerEditorPanel.remove();
        this.viewerEditorPanel.setSrc(this.newUrl);
        
        this.viewerEditorPanel.setHeight(this.contentPanel.getHeight() - 30);
        this.viewerEditorPanel.setVisible(true);
        this.viewerEditorPanel.expand(true);
        
        this.contentPanel.doLayout();
    },
    
    hideViewer : function () {
    	this.viewerEditorPanel.remove();
    	this.viewerEditorPanel.setVisible(false);
        this.contentPanel.doLayout();
    },
    
    /**
     * Refresh the tree, reload the tree and print show it again
     */    
    refreshTree : function (){
        this.tree.getLoader().load(this.tree.getRootNode(), function () {
            this.tree.getRootNode().expand(true);                        
        }, this);  
        this.setEditable(false);
    },
    /**
     * Set if the file is editable or not
     * @param {} editable
     */
    setEditable : function (editable) {
        this.editable = editable;
        this.findByType('htmleditor')[0].setReadOnly(!this.editable);
        this.saveButton.setDisabled(!this.editable);        
    },
    /**
     * Get the language chosen
     * @return {String} the language chosen
     */
    getChosenLanguage : function () {
        return this.tree.getTopToolbar().comboLanguage.getValue();
    },
    
    /**
     * Change the language, reload the tree with the new language
     * @param {String} language the language locale
     */
    changeLanguage : function (language) {
        var urlJson = this.jsonUrlTemplate.replace("{locale}", language);
        this.tree.getLoader().url = urlJson;
        this.refreshTree();        
    },
    
    isDisplayable : function (text) {
        var imageRegex = /\.(pdf|png|jpg|jpeg|gif|bmp|JPG|JPEG)$/;
        return !(text.match(imageRegex));      
    }, 
    /**
     * method called when trying to save preference
     * @returns
     */
    _getSettings : function () {
		return {
            preferencesPath : "/modules", 
            preferencesFileName : this.id
        };

    }
     
});

/**
 * @static
 * Implementation of the method getParameters to be able to load view Config Module panel.
 * @return {Array} the parameters to display into administration view. 
 */
sitools.user.modules.contentEditorModule.getParameters = function () {
    
    return [{
        jsObj : "Ext.form.Label", 
        config : {
            html : "This module needs 2 datastorages. "+
            "All files are edited in the development datastorage, and then copied to the production datastorage<br/>"            
        }
    },  {
        jsObj : "Ext.form.TextField", 
        config : {
            fieldLabel : i18n.get("label.siteName"),
            allowBlank : false,
            width : 200,
            listeners: {
                render: function(c) {
                  Ext.QuickTips.register({
                    target: c,
                    text: "The name of your site"
                  });
                }
            },
            name : "siteName",
            value : ""
        }
    }, {
        jsObj : "Ext.form.TextField", 
        config : {
            fieldLabel : i18n.get("label.urlDatastorage"),
            allowBlank : false,
            width : 200,
            listeners: {
                render: function(c) {
                  Ext.QuickTips.register({
                    target: c,
                    text: "The URL of the development datastorage"
                  });
                }
            },
            name : "dynamicUrlDatastorage",
            value : ""
        }
    },
    {
        jsObj : "Ext.form.TextField", 
        config : {
            fieldLabel : i18n.get("label.nameDatastorageSrc"),
            allowBlank : false,
            width : 200,
            listeners: {
                render: function(c) {
                  Ext.QuickTips.register({
                    target: c,
                    text: "The NAME of the development datastorage to copy content from"
                  });
                }
            },
            name : "nameDatastorageSrc",
            value : ""
        }
    },
    {
        jsObj : "Ext.form.TextField", 
        config : {
            fieldLabel : i18n.get("label.nameDatastorageDest"),
            allowBlank : false,
            width : 200,
            listeners: {
                render: function(c) {
                  Ext.QuickTips.register({
                    target: c,
                    text: "The NAME of the production datastorage to copy the content to"
                  });
                }
            },
            name : "nameDatastorageDest",
            value : ""
        }
    }/*,
    {
        jsObj : "Ext.form.TextField", 
        config : {
            fieldLabel : i18n.get("label.imageDatastorageDirectory"),
            width : 200,
            listeners: {
                render: function(c) {
                  Ext.QuickTips.register({
                    target: c,
                    text: "The url of the image directory attachment"
                  });
                }
            },
            name : "imageDatastorageDirectory",
            value : ""
        }
    }*/];
};

Ext.reg('sitools.user.modules.contentEditorModule', sitools.user.modules.contentEditorModule);
