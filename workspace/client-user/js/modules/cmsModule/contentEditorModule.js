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
/*global Ext, sitools, i18n, loadUrl, CKEDITOR, document, locale, alertFailure */

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
        
        this.id = 'contentEditorID';
        
        this.templateHtmlFile = "<html><head><title>{text}</title></head><body></body></html>";
        
        this.CONST_URLDATASTORAGE = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_URL');
        
        // DATASTORAGE NAME CONFIGURABLE
        // this.datastorageSrc = "/postelDatastorageDev";
        // this.datastorageDest = "/postelDatastorageProd";
        // this.directoryImageUrl = loadUrl.get('APP_URL') +
        // "/datastorage/user/postel-site/images";
        // this.dynamicUrlDatastorage = loadUrl.get('APP_URL') +
        // "/datastorage/user/postel-site";
        
        Ext.each(this.listProjectModulesConfig, function (config) {
            if (!Ext.isEmpty(config.value)) {
                switch (config.name) {
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
                
                case "allowDataPublish" :
                    // Parse string to boolean
                    this.allowDataPublish = config.value == "true";
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
                allowDataPublish : this.allowDataPublish,
                cms : this,
                textTooltip : textTooltip
            }),
            contextMenu: new sitools.user.modules.cmsContextMenu({
                cms : this
            }),
            root : {
                text : "root",
                nodeType : 'async',
                uuid : 'root'
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
                    if (this.baseAttrs) {
                        Ext.applyIf(attr, this.baseAttrs);
                    }
                    if (this.applyLoader !== false && !attr.loader) {
                        attr.loader = this;
                    }
                    if (Ext.isString(attr.uiProvider)) {
                        attr.uiProvider = this.uiProviders[attr.uiProvider] || eval(attr.uiProvider);
                    }
                    if (Ext.isEmpty(attr.uuid)) {
                        console.log(attr.text);
                        attr.uuid = generateId();
                        this.toReload = true;
                    }
                    if (attr.nodeType) {
                        return new Ext.tree.TreePanel.nodeTypes[attr.nodeType](attr);
                    } else {
                        return attr.leaf ?
                                    new Ext.tree.TreeNode(attr) :
                                    new Ext.tree.AsyncTreeNode(attr);
                    }
                },
                listeners : {
                    scope : this,
                    load : function (treeLoader, node, response) {
                        if (!Ext.isEmpty(this.activeNode)) {
                            if (!Ext.isEmpty(node)) {
                                this.tree.selectPath(node.getPath());
                                this.treeAction(node);
                            } else {
                                Ext.Msg.alert(i18n.get('label.warning'), i18n.get('msg.nodeundefined'));
                            }
                        }
                        if (!Ext.isEmpty(this.tree.loader.toReload) && this.tree.loader.toReload) {
                            this.createJsonTree();
                        }
                        this.lastModified = response.getResponseHeader("Last-Modified"); 
                    },
                    loadException : function (loader, node, response) {
                        if (response.status === 404) {
                            Ext.Msg.show({
                                title : i18n.get('label.warning'),
                                msg : i18n.get('label.noJsonFileFound'),
                                buttons : {
                                    yes : i18n.get('label.yes'),
                                    no : i18n.get('label.no'),
                                    cancel : i18n.get('label.cancel')
                                },
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
                        else if (response.status === 403) {
                            Ext.Msg.alert(i18n.get("warning.serverError"), i18n.get("label.forbiddenResource"));
                        }                        
                        else {
                            Ext.Msg.alert(i18n.get("warning.serverError"), i18n.get("label.invalidResource"));
                        }
                    }
                }
            }),
            listeners : {
                scope : this,
                beforeclick : function (node){
                    var labelSaving = this.saveLabelInfo.getEl().dom;
                    if (labelSaving.isTextModified) {
                        Ext.Msg.show({
                            title : i18n.get('label.warning'),
                            buttons : {
                                yes : i18n.get('label.yes'),
                                no : i18n.get('label.no')
                            },
                            icon : Ext.MessageBox.WARNING,
                            msg : i18n.get('label.confirmQuitEditor'),
                            scope : this,
                            fn : function (btn, text) {
                                if (btn == 'no') {
                                    return false;
                                } else {
                                    labelSaving.innerHTML = "<img src='/sitools/common/res/images/icons/valid.png'/> " + i18n.get('label.textUpToDate') + "";
                                    labelSaving.isTextModified = false;
                                    this.tree.selectPath(node.getPath());
                                    this.treeAction(node);
                                }
                            }
                        });
                        return false;
                    }
                    return true;
                },
                click : function (node) {
                        this.treeAction(node);
                },
                contextmenu : function (node, e) {
                    this.tree.getSelectionModel().select(node, e, true);
                    var c = node.getOwnerTree().contextMenu;
                    if (!node.isLeaf()) {
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
                dragdrop : function (tree, node, dd, e) {
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
        
        this.saveLabelInfo = new Ext.form.Label({
            name : 'saveInfoLabel',
            id : 'saveInfoLabelID',
            isTextModified : false,
            html : "<img src='/sitools/common/res/images/icons/valid.png'/> <b>" + i18n.get('label.textUpToDate') + "</b>"
        });
        
        this.idTextarea = Ext.id();
        this.htmlEditor = new Ext.form.TextArea({
            directoryImageUrl : this.directoryImageUrl,
            id : this.idTextarea,
            name : this.idTextarea,
            region : 'center'
        });
        

        this.viewerEditorPanel = new Ext.ux.ManagedIFrame.Panel({
            id : 'viewerEditorPanel',
            region : 'south',
            autoScroll : true,
            hidden : true
        });
        
        var toolbar = null;
        if (Ext.isEmpty(userLogin)) {
            toolbar = new Ext.Toolbar({
                name : 'warningDateLabel',
                html : "<img src='/sitools/common/res/images/ux/warning.gif'/> " + i18n.get('warning.warnPublicEditor') + ""
            });
        }
        
        this.contentPanel = new Ext.Panel({
            id: 'content-editor',
            layout : 'border',
            region : 'center',
            bbar : [this.saveLabelInfo, '->', this.saveButton],
            items : [this.htmlEditor, this.viewerEditorPanel],
            listeners : {
                scope : this,
                resize : function (panel, newWidth, newHeight, origWidth, origHeight) {
                    if (CKEDITOR.instances[this.idTextarea]) {
                        CKEDITOR.instances[this.idTextarea].resize(newWidth, newHeight - panel.getBottomToolbar().getHeight());
                    }
                }
            },
            tbar : toolbar
        });
        
        
        this.items = [this.tree, this.contentPanel];
        
        this.tbar = {
                xtype : 'toolbar',
                cls : 'services-toolbar',
                height : 15,
                defaults : {
                    scope : this,
                    cls : 'services-toolbar-btn'
                },
                items : []
        };
        
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
                    this.tree.getRootNode().expand(true);
                },
                failure : alertFailure
            });
        }
    },
    
    afterRender : function () {
        sitools.user.modules.contentEditorModule.superclass.afterRender.apply(this, arguments);
        this.ownerCt.addListener('beforeclose', function (panel) {
            var labelSaving = this.saveLabelInfo.getEl().dom;
            if (!labelSaving.isTextModified) {
                return;
            }
            Ext.Msg.show({
                title : i18n.get('label.delete'),
                buttons : {
                    yes : i18n.get('label.yes'),
                    no : i18n.get('label.no')
                },
                icon : Ext.MessageBox.WARNING,
                msg : i18n.get('label.confirmQuitEditor'),
                scope : this,
                fn : function (btn, text) {
                    if (btn == 'yes') {
                        this.ownerCt.doClose();
                    }
                }
            });
            return false;
        }, this);
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
                buttons : {
                    yes : i18n.get('label.yes'),
                    no : i18n.get('label.no')
                },
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
        
        if (this.isDisplayable(nodeLink) && nodeLink.indexOf("http://") == -1) {
            Ext.Ajax.request({
                url : this.newUrl,
                method : 'GET',
                scope : this,
//                params : {
//                    processTemplate : true
//                },
                success : function (ret) {
                    var contentType = ret.getResponseHeader("content-type");
                    if (Ext.isEmpty(CKEDITOR.instances[this.idTextarea])) {
                        var height = this.contentPanel.getHeight() - this.contentPanel.getBottomToolbar().getHeight();
                        CKEDITOR.datastorageUrl = this.dynamicUrlDatastorage; // use to upload file in datastorage
                        CKEDITOR.replace(this.idTextarea, {
                            baseHref : this.dynamicUrlDatastorage + "/",
                            language : locale.getLocale(),
                            manageSaving : this.manageSaving,
//                            saveInfoLabel : this.saveLabelInfo,
                            fullPage : true,
                            on : {
                                instanceReady : function() {
                                    this.resize("100%", height);
                                },
                                key: function() {
                                    this.config.manageSaving(true);
                                }
                            }
                        });
                    }
                    if (contentType.indexOf("text") > -1) {
                        
                        
                        var data = ret.responseText;
                        
                        //add the base name to the header of the IFrame
                        //                if(this.htmlEditor.iframe.contentDocument.getElementsByTagName("base").length == 0){
                        //	                var base = document.createElement("base");
                        //	                base.href = this.dynamicUrlDatastorage + "/";
                        //	                this.htmlEditor.iframe.contentDocument.getElementsByTagName("head")[0].appendChild(base);
                        //                }
//                        this.findByType('htmleditor')[0].setValue(data);
                        var callback = Ext.createDelegate(function () {
                            CKEDITOR.instances[this.idTextarea].setReadOnly(false);
                        }, this);
                        
                        CKEDITOR.instances[this.idTextarea].setData(data, callback);
                    } else {
//                        this.findByType('textarea')[0].setValue(i18n.get("label.cannotEditOtherThanText"));
                        CKEDITOR.instances[this.idTextarea].setData(i18n.get("label.cannotEditOtherThanText"));
                        CKEDITOR.instances[this.idTextarea].setReadOnly(true);
                    }
                    
                    
                },
                failure : function (ret) {
                    var data = ret.responseText;
                    if(!Ext.isEmpty(CKEDITOR.instances[this.idTextarea])){
                        CKEDITOR.instances[this.idTextarea].setData(data);
    //                    this.findByType('textarea')[0].setValue(data);
                        CKEDITOR.instances[this.idTextarea].setReadOnly(true);
                    }
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
        var text = CKEDITOR.instances[this.idTextarea].getData();
        
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
            failure : alertFailure,
            callback : function () {
                this.manageSaving(false);
            }
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
	            "sync" : false,
	            "uuid" : generateId()
	        });
        } else {
            nodeToAdd.appendChild({
                "text" : text,
                "link" : link,
                "leaf" : false,
                "uuid" : generateId(),
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
	            jsonData : this.templateHtmlFile.replace("{text}", text),
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
                jsonData : this.templateHtmlFile.replace("{text}", text),
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
        //check if the json file is up to date using the lastModified date  
        Ext.Ajax.request({
            url : this.tree.loader.url,
            method : 'HEAD',
            scope : this,
            success : function (response, opts) {
                if(response.getResponseHeader("Last-Modified") === this.lastModified) {
                    this.onCreateJsonTree();
                }
                else {
                    Ext.Msg.alert(i18n.get("label.warning"), i18n.get("label.errorSavingJsonTree") + "<br/><hr/><i></i><hr/><br/>"
                            + i18n.get("label.treeNotSaved"), this.refreshTree, this);
                }
            },
            failure : function () {
                Ext.Msg.alert(i18n.get("label.warning"), i18n.get("label.errorSavingJsonTree") + "<br/><hr/><i></i><hr/><br/>"
                        + i18n.get("label.treeNotSaved"), this.refreshTree, this);
            }
        });
    },
    
    onCreateJsonTree : function () {
        try {
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
                success : function (response, opts) {
                    this.lastModified = response.getResponseHeader("Date");
                },
                failure : function (response, opts) {
                    Ext.Msg.alert(i18n.get("label.warning"), i18n.get("label.errorSavingJsonTree") + "<br/><hr/><i> " + response.statusText + " </i><hr/><br/>"
                            + i18n.get("label.treeNotSaved"), this.refreshTree, this);
                }
            });
        } catch (e) {
            Ext.Msg.alert(i18n.get("label.warning"), i18n.get("label.errorSavingJsonTree") + "<br/><hr/><i> " + e + " </i><hr/><br/>"
                    + i18n.get("label.treeNotSaved"), this.refreshTree, this);
            throw e;
        }
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
                link : root.attributes.link,
                uuid : root.attributes.uuid
            };
            parent.push(node);
        } else {
            node = {
                text : root.text,
                leaf : false,
                link : root.attributes.link,
                uuid : root.attributes.uuid,
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
    deleteNode : function (node, deleteFile, saveJson) {
        this.htmlEditor.setValue("");
        this.setEditable(false);
        if (deleteFile) {
	        this.url = this.dynamicUrlDatastorage;
	        
	        var deleteUrl;
	        if (node.isLeaf()) {
	            deleteUrl = this.url + node.attributes.link;
	        }
	        else {
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
	            failure : function (response, opts) {
                    Ext.Msg.alert(response.status + " " + response.statusText, response.responseText);
                }
	        });
        } else {
            node.remove(true);
	        if (saveJson) {
                this.createJsonTree();
            }
        }
        
    },
    

    manageNodes : function (node, action) {
        if (node.isLeaf()) {
            switch (action) {
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
    

    onValid : function (item) {
        if (!item.attributes.sync) {
            item.attributes.sync = true;

            this.changeIcon(item);

            new Ext.ux.Notification({
                iconCls : 'x-icon-information',
                title : i18n.get('label.information'),
                html : i18n.get('label.fileValid'),
                autoDestroy : true,
                hideDelay : 1000
            }).show(document);
        } else {
            new Ext.ux.Notification({
                iconCls : 'x-icon-information',
                title : i18n.get('label.information'),
                html : i18n.get('label.alreadyValid'),
                autoDestroy : true,
                hideDelay : 1000
            }).show(document);
        }
    },
    

    unValid : function (item) {
        if (item.attributes.sync) {
            item.attributes.sync = false;

            this.changeIcon(item);

            new Ext.ux.Notification({
                iconCls : 'x-icon-information',
                title : i18n.get('label.information'),
                html : i18n.get('label.fileInvalid'),
                autoDestroy : true,
                hideDelay : 1000
            }).show(document);
        } else {
            new Ext.ux.Notification({
                iconCls : 'x-icon-information',
                title : i18n.get('label.information'),
                html : i18n.get('label.alreadyInvalid'),
                autoDestroy : true,
                hideDelay : 1000
            }).show(document);
        }
    },
    

    runCopy : function () {
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
            failure : function (response, opts) {
                Ext.Msg.alert(response.status + " " + response.statusText, response.responseText);
            }
        });
    },
    

    createEmptyJson : function (url) {
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
            success : function (ret) {
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
            success : function (response, opts) {
                this.lastModified = response.getResponseHeader("Date");
            },
            failure : function (response, opts) {
                Ext.Msg.alert(response.status + " " + response.statusText, response.responseText);
            }
        });
    },
    
    manageSaving : function (isSaved) {
        this.isTextModified = isSaved;
        
        var label = Ext.getDoc('saveInfoLabelID');
        if (isSaved == false) {
            label.dom.innerHTML = "<img src='/sitools/common/res/images/icons/valid.png'/> " + i18n.get('label.textUpToDate') + "";
            label.dom.isTextModified = false;
        } else {
            label.dom.innerHTML = "<img src='/sitools/common/res/images/ux/warning.gif'/> " + i18n.get('label.textNotUpToDate') + "";
            label.dom.isTextModified = true;
        }
    },

    checkJsonUrlValidation : function () {
        var jsonUrl = this.tree.getLoader().url;
        Ext.Ajax.request({
            url : jsonUrl,
            method : 'GET',
            scope : this,
            failure : function (response, opts) {
                Ext.Msg.show({
                    title : i18n.get('label.warning'),
                    msg : i18n.get('label.noJsonFileFound'),
                    buttons : {
                        yes : i18n.get('label.yes'),
                        no : i18n.get('label.no'),
                        cancel : i18n.get('label.cancel')
                    },
                    fn : function (btnId, text, opt) {
                        if (btnId === "yes") {
                            this.createEmptyJson(jsonUrl);
                        }
                    },
                    animEl : 'elId',
                    scope : this,
                    icon : Ext.MessageBox.QUESTION
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
    refreshTree : function () {
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
//        this.findByType('htmleditor')[0].setReadOnly(!this.editable);
        this.findByType('textarea')[0].setReadOnly(!this.editable);
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
            html : "This module can manage 2 datastorages. " +
            "All files are edited in the development datastorage (Datastorage Source), and then copied to the production datastorage (Datastorage Destination)<br/>"            
        }
    }, {
        jsObj : "Ext.form.TextField", 
        config : {
            id : "nameDatastorageId",
            fieldLabel : i18n.get("label.siteName"),
            allowBlank : true,
            hidden : true,
            width : 200,
            listeners : {
                render : function (c) {
                    Ext.QuickTips.register({
                        target : c,
                        text : "The name of your site"
                    });
                }
            },
            name : "siteName",
            value : undefined
        }
    }, {
        jsObj : "Ext.form.TextField", 
        config : {
            fieldLabel : i18n.get("label.urlDatastorage"),
            allowBlank : false,
            id : "urlDatastorageId",
            hidden : true,
            name : "dynamicUrlDatastorage",
            value : ""
        }
    }, {
        jsObj : "Ext.form.ComboBox", 
        config : {
            fieldLabel : i18n.get("label.nameDatastorageSrc"),
            id : "nameDatastorageSrcId",
            allowBlank : false,
            typeAhead : true,
            triggerAction : 'all',
            editable : false,
            width : 200,
            valueField : 'name',
            displayField : 'name',
            store : new Ext.data.JsonStore({
                root : 'data',
                restful : true,
                url : loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_ADMIN_URL') + '/directories',
                remoteSort : true,
                idProperty : 'id',
                fields : [ {
                    name : 'id',
                    type : 'string'
                }, {
                    name : 'name',
                    type : 'string'
                }, {
                    name : 'attachUrl',
                    type : 'string'
                }]
            }),
            listeners: {
                render: function (c) {
                    Ext.QuickTips.register({
                        target : c,
                        text : "The NAME of the development datastorage to copy content from"
                    });
                },
                select : function (combo, rec, ind) {
                    var urlAttachField = this.ownerCt.getComponent("urlDatastorageId");
                    var siteName = this.ownerCt.getComponent("nameDatastorageId");
                    urlAttachField.setValue(rec.data.attachUrl);
                    siteName.setValue(rec.data.name);
                }
            },
            name : "nameDatastorageSrc",
            value : ""
        }
    }, {
        jsObj : "Ext.form.Checkbox", 
        config : {
            fieldLabel : i18n.get("label.allowDataPublish"),
            checked : false,
            id : "allowDataPublishId",
            listeners : {
                render : function (c) {
                    Ext.QuickTips.register({
                        target : c,
                        text : "Allow to copy/publish files from one datastorage to another. Useless when only one storage."
                    });
                },
                check : function (box, checked) {
                    if (this.ownerCt) {
                        var dest = this.ownerCt.getComponent("nameDatastorageDestId");
                        if (!checked) {
                            dest.clearInvalid();
                            dest.setDisabled(true);
                        }
                        else {
                            dest.clearInvalid();
                            dest.setDisabled(false);
                        }
                    }
                }
            },
            name : "allowDataPublish",
            value : ""
        }
    }, {
        jsObj : "Ext.form.ComboBox", 
        config : {
            fieldLabel : i18n.get("label.nameDatastorageDest"),
            id : "nameDatastorageDestId",
            allowBlank : false,
            typeAhead : true,
            editable : false,
            disabled : true,
            triggerAction : 'all',
            width : 200,
            valueField : 'name',
            displayField : 'name',
            store : new Ext.data.JsonStore({
                root : 'data',
                restful : true,
                url : loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_ADMIN_URL') + '/directories',
                remoteSort : true,
                idProperty : 'id',
                fields : [ {
                    name : 'id',
                    type : 'string'
                }, {
                    name : 'name',
                    type : 'string'
                }]
            }),
            listeners: {
                render : function (c) {
                    var checkbox = this.ownerCt.getComponent("allowDataPublishId");
                    if (!Ext.isEmpty(checkbox)) {
                        this.setDisabled(!checkbox.getValue());
                        Ext.QuickTips.register({
                            target : c,
                            text : "The NAME of the production datastorage to copy the content to"
                        });
                    }
                }
            },
            name : "nameDatastorageDest",
            value : ""
        }
    }];
};

Ext.reg('sitools.user.modules.contentEditorModule', sitools.user.modules.contentEditorModule);
