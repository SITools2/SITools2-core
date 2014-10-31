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

Ext.namespace('sitools.user.view.modules.contentEditor');
/**
 * Help Module
 * @class sitools.user.modules.contentEditorModule
 * @cfg {Array} listProjectModulesConfig, the list of paramaters to set the module with
 * @extends Ext.Panel
 */
Ext.define('sitools.user.view.modules.contentEditor.ContentEditorView', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.contentEditorView',
    requires : ['sitools.user.model.DataStorageExplorerTreeModel'],
    
    /**
     * the node to activate
     * @type Ext.tree.TreeNode
     */
    activeNode : null,
    
    CHECK_TREE_DELAY : 10,
    border : false,
    toReload : false, // personal store param

    initComponent : function () {
        
        this.itemId = 'contentEditorID';
        
        this.templateHtmlFile = "<html><head><title>{text}</title></head><body></body></html>";
        
        this.CONST_URLDATASTORAGE = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_URL');
        
        // DATASTORAGE NAME CONFIGURABLE
        // this.datastorageSrc = "/postelDatastorageDev";
        // this.datastorageDest = "/postelDatastorageProd";
        // this.directoryImageUrl = loadUrl.get('APP_URL') +
        // "/datastorage/user/postel-site/images";
        // this.dynamicUrlDatastorage = loadUrl.get('APP_URL') +
        // "/datastorage/user/postel-site";
        
        Ext.each(this.moduleModel.listProjectModulesConfigStore.data.items, function (config) {
            switch (config.get('name')) {
            case "dynamicUrlDatastorage" :
                this.dynamicUrlDatastorage = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_URL') + config.get('value');
                break;

            case "nameDatastorageSrc" :
                this.datastorageSrc = "/" + config.get('value');
                break;

            case "nameDatastorageDest" :
                this.datastorageDest = "/" + config.get('value');
                break;

            /*case "imageDatastorageDirectory" :
                this.directoryImageUrl = loadUrl.get('APP_URL') + loadUrl.get('APP_DATASTORAGE_URL') + config.get('value');
                break;*/

            case "siteName" :
                this.siteName = config.get('value');
                break;

            case "allowDataPublish" :
                // Parse string to boolean
                this.allowDataPublish = config.get('value') == "true";
                break;
            }
        }, this);

        if (Ext.isEmpty(this.siteName)) {
            this.siteName = i18n.get('label.webSitePlan');
        }

        this.jsonUrlTemplate = this.dynamicUrlDatastorage + '/json/data_{locale}.json';
        
        var localeStr = locale.getLocale();
        this.url = this.jsonUrlTemplate.replace("{locale}", localeStr);
        
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
        
        var textTooltip = Ext.String.format(i18n.get('label.runCopyInfo'), this.datastorageSrc, this.datastorageDest);
        
        this.treeTopBar = Ext.create('sitools.user.view.modules.contentEditor.ContentEditorTreeTopBar', {
            allowDataPublish : this.allowDataPublish,
            cms : this,
            textTooltip : textTooltip
        });
        
        this.treeBottomBar = Ext.create('sitools.user.view.modules.contentEditor.ContentEditorTreeBBar');

        this.manageNodeToolbar = Ext.create('sitools.user.view.modules.contentEditor.ContentEditorNodeManagerToolbar', {
            cms : this
        });
        
        this.treeStore = Ext.create('sitools.user.store.ContentEditorTreeStore');
        this.treeStore.setCustomUrl(this.url);

        
        this.tree = Ext.create('Ext.tree.Panel', {
        	title : i18n.get('label.sitePlan'),
        	store : this.treeStore,
            region : 'west',
            animate : true,
            width : 320,
            autoScroll : true,
            collapsible : true,
            border : false,
            enableDD : true,
            forceFit : true,
            rowLines : true,
            split : true,
            tbar : {
                xtype : 'toolbar',
                layout : {
                    type : 'vbox'
                },
                border : false,
                items : [this.treeTopBar, this.manageNodeToolbar] 
            },
            bbar : this.treeBottomBar,
            rootVisible : false,
            root : {
                text : "root",
                url : this.url,
                id: generateId(),
	            leaf: false,
	            link: "",
	            children : []
            },
            listeners : {
                scope : this,
                beforeclick : function (node) {
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
                itemclick : function (tree, node) {
                    this.treeAction(node);
                    this.manageNodeToolbar.manage(node);
                },
                itemcontextmenu : function (tree, node, item, index, e) {
                    e.stopEvent();
//                    this.tree.getSelectionModel().select(node, e, true);
//                    var c = node.getOwnerTree().contextMenu;
//                    if (!node.isLeaf()) {
//                        c.getComponent('manage-node').setVisible(false);
//                    }
//                    else {
//                        c.getComponent('manage-node').setVisible(true);
//                    }
//                    c.contextItem = item;
//                    c.showAt(e.getXY());
                },
                render : function () {
                    this.tree.getRootNode().expand(true);
                },
                dragdrop : function (tree, node, dd, e) {
                    this.createJsonTree();
                }
            }
        });
        
        this.checkTreeUpToDateTask = new Ext.util.DelayedTask(function () {
            if (Ext.isEmpty(this.itemId) || this.itemId != 'contentEditorID' || Ext.isEmpty(this.tree)) {
                this.checkTreeUpToDateTask.cancel();
                return;
            }
            Ext.Ajax.request({
                url : this.url,
                method : 'HEAD',
                scope : this,
                success : function (response, opts) {
                    var lastMod = response.getResponseHeader("Last-Modified");
                    if (lastMod !== this.lastModified) {
                        this.treeBottomBar.setTreeUpToDate(false, lastMod);
                    } else {
                        this.treeBottomBar.setTreeUpToDate(true, lastMod);
                    }
                    this.checkTreeUpToDateTask.cancel();
                    this.checkTreeUpToDateTask.delay(this.CHECK_TREE_DELAY * 1000);
                },
                failure : function () {
                    this.checkTreeUpToDateTask.cancel();
                }
            });
        }, this);
        
        this.saveButton = Ext.create('Ext.button.Button', {
                xtype : 'button',
                text : i18n.get('label.saveFile'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/save.png',
                scope : this,
                handler : this.onSave
        });
        
        this.saveLabelInfo = Ext.create('Ext.form.Label', {
            name : 'saveInfoLabel',
            id : 'saveInfoLabelID',
            isTextModified : false,
            html : "<img src='/sitools/common/res/images/icons/valid.png'/> <b>" + i18n.get('label.textUpToDate') + "</b>"
        });
        
        this.itemIdTextarea = Ext.id();
        this.htmlEditor = Ext.create('Ext.form.field.TextArea', {
            directoryImageUrl : this.directoryImageUrl,
            border : false,
            id : this.itemIdTextarea,
            name : this.itemIdTextarea,
            region : 'center'
        });
        
        var viewerIFrame = Ext.create('Ext.ux.IFrame', {
            itemId : 'viewerIFrame'
        });

        this.viewerEditorPanel = Ext.create('Ext.panel.Panel', {
            id : 'viewerEditorPanel',
            region : 'south',
            autoScroll : true,
            hidden : true,
            items : [viewerIFrame]
        });
        
        var toolbar = undefined;
        if (Ext.isEmpty(userLogin)) {
            toolbar = Ext.create('Ext.toolbar.Toolbar', {
                border : false,
                height : 40,
                items : [{
                    xtype : 'label',
                    name : 'warningDateLabel',
                    html : "<img src='/sitools/common/res/images/ux/warning.gif'/> " + i18n.get('warning.warnPublicEditor') + ""
                }]
            });
        }
        
        this.contentPanel = Ext.create('Ext.panel.Panel', {
            itemId: 'contentPanel',
            border : false,
            bodyBorder : false,
            layout : 'border',
            region : 'center',
            bbar : Ext.create('Ext.toolbar.Toolbar', {
                itemId : 'contentPanelBbar',
                items : [this.saveLabelInfo, '->', this.saveButton]
            }),
            items : [this.htmlEditor, this.viewerEditorPanel],
            listeners : {
                scope : this,
                resize : function (panel, newWidth, newHeight, origWidth, origHeight) {
                    if (CKEDITOR.instances[this.itemIdTextarea]) {
                        CKEDITOR.instances[this.itemIdTextarea].resize(newWidth, newHeight - panel.down('toolbar[itemId="contentPanelBbar"]').getHeight());
                    }
                }
            },
            tbar : toolbar
        });
        
        this.items = [this.tree, this.contentPanel];
        
        this.callParent(arguments);
    },
    
    onRender : function () {
    	this.callParent(arguments);

        this.refreshTree();
    },

    afterRender : function () {
    	this.callParent(arguments);
        
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
    treeAction : function (record) {
        
        // Getting node urlArticle
        var nodeLink = record.get('link');

        if (Ext.isEmpty(nodeLink)) {
            var msg = Ext.Msg.show({
                title : i18n.get('label.warning'),
                msg : i18n.get('label.noLinkDefineEditNode'),
                buttons : Ext.MessageBox.YESNO,
//                buttons : {
//                    yes : i18n.get('label.yes'),
//                    no : i18n.get('label.no')
//                },
                scope : this,
                fn : function (btnId, textButton, opt) {
                    if (btnId === "yes") {
                        var windowLink = Ext.create('sitools.user.view.modules.contentEditor.ChooseFileInExplorerView', {
	                        datastorageUrl : this.dynamicUrlDatastorage,
	                        node : record,
	                        cms : this,
	                        action : "edit"
                        });
                        windowLink.show();
                    }
                }
            });
            Ext.defer(function () {
                Ext.WindowManager.bringToFront(msg);
            }, 500);
            return;
        }
        
        if (nodeLink.indexOf("http://") == -1) {
            this.newUrl = this.dynamicUrlDatastorage + nodeLink;
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
                    if (Ext.isEmpty(CKEDITOR.instances[this.itemIdTextarea])) {
                        var contentPanelBbar = this.contentPanel.down('toolbar[itemId="contentPanelBbar"]');
                        var height = this.contentPanel.getHeight() - contentPanelBbar.getHeight();
                        CKEDITOR.datastorageUrl = this.dynamicUrlDatastorage; // use to upload file in datastorage
                        CKEDITOR.replace(this.itemIdTextarea, {
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
                        var callback = Ext.bind(function () {
                            CKEDITOR.instances[this.itemIdTextarea].setReadOnly(false);
                        }, this);
                        
                        CKEDITOR.instances[this.itemIdTextarea].setData(data, callback);
                    } else {
//                        this.findByType('textarea')[0].setValue(i18n.get("label.cannotEditOtherThanText"));
                        CKEDITOR.instances[this.itemIdTextarea].setData(i18n.get("label.cannotEditOtherThanText"));
                        CKEDITOR.instances[this.itemIdTextarea].setReadOnly(true);
                    }
                },
                failure : function (ret) {
                    var data = ret.responseText;
                    if(!Ext.isEmpty(CKEDITOR.instances[this.itemIdTextarea])){
                        CKEDITOR.instances[this.itemIdTextarea].setData(data);
    //                    this.findByType('textarea')[0].setValue(data);
                        CKEDITOR.instances[this.itemIdTextarea].setReadOnly(true);
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
            if (node.get('sync')) {
                node.set('icon', loadUrl.get('APP_URL') + '/common/res/images/icons/valid.png');
            }
            else {
                node.set('icon', loadUrl.get('APP_URL') + '/common/res/images/icons/unvalid.png');
            }
        }
    },
    
    onSave : function () {
        var text = CKEDITOR.instances[this.itemIdTextarea].getData();
        
        this.contentPanel.getEl().mask(i18n.get('label.saving'));
        
        Ext.Ajax.request({
            url : this.newUrl,
            method : 'PUT',
            scope : this,
            headers : {
                'Content-Type' : this.mediaType
            },
            jsonData : text,
            success : function (ret) {
                popupMessage(i18n.get('label.information'), i18n.get('label.changeSaved'), null, 'x-icon-information');
            },
            failure : alertFailure,
            callback : function () {
                this.contentPanel.getEl().unmask();
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
	            "sync" : true,
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
	            url : this.dynamicUrlDatastorage + link,
	            method : 'PUT',
	            scope : this,
	            headers : {
	                'Content-Type' : this.mediaType
	            },
	            jsonData : this.templateHtmlFile.replace("{text}", text),
	            success : function (ret) {
                    popupMessage(i18n.get('label.information'), i18n.get('label.fileCreated'), null, 'x-icon-information');
	            },
	            failure : alertFailure
	        });
        }
        if (saveJson) {
            this.createJsonTree();
//            this.onCreateJsonTree();
        }
    },
    
    editNode : function (nodeToEdit, text, link, createFile, saveJson) {
        nodeToEdit.set('text', text);
        
//        if (!link.match(".html") && !(link.indexOf("http://") === 0)) {
//            link += ".html";
//        }
        
        nodeToEdit.set('link',link);
        nodeToEdit.set('text', text);
        
        if (createFile) {
            Ext.Ajax.request({
                url : this.dynamicUrlDatastorage + link,
                method : 'PUT',
                scope : this,
                headers : {
                    'Content-Type' : this.mediaType
                },
                jsonData : this.templateHtmlFile.replace("{text}", text),
                success : function (ret) {
                    popupMessage(i18n.get('label.information'), i18n.get('label.fileCreated'), null, 'x-icon-information');
                },
                failure : alertFailure
            });
        }
        if (saveJson) {
            this.createJsonTree();
//            this.onCreateJsonTree();
        }
    },
    
    createJsonTree : function () {
        //check if the json file is up to date using the lastModified date  
        Ext.Ajax.request({
            url : this.url,
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
//            callback : function (options, success, reponse) {
//                this.onCreateJsonTree();
//            },
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
            
            // Save json changed
            Ext.Ajax.request({
                url : this.url,
                method : 'PUT',
                scope : this,
                headers : {
                    'Content-Type' : 'application/json'
                },
                jsonData : tree,
                success : function (response, opts) {
                    this.lastModified = response.getResponseHeader("Date");
                    this.treeBottomBar.setTreeUpToDate(true, this.lastModified);
                },
                failure : function (response, opts) {
                    Ext.Msg.alert(i18n.get("label.warning"), i18n.get("label.errorSavingJsonTree") + "<br/><hr/><i> " + response.statusText + " </i><hr/><br/>"
                            + i18n.get("label.treeNotSaved"), this.refreshTree, this);
                },
                callback : function () {
//                    this.tree.getRootNode().expand(true);
		            this.refreshTree();
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
                text : root.get('text'),
                leaf : root.get('leaf'),
                icon : root.get('icon'),
                sync : root.get('sync'),
                link : root.get('link'),
                uuid : root.get('uuid')
            };
            parent.push(node);
        } else {
            node = {
                text : root.get('text'),
                leaf : false,
                link : root.get('link'),
                uuid : root.get('uuid'),
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

	        var deleteUrl =  this.dynamicUrlDatastorage + node.get('link');
//	        if (node.isLeaf()) {
//	            deleteUrl = this.url + node.attributes.link;
//	        }
//	        else {
//	            deleteUrl = this.url + node.attributes.link;
//	        }
	        Ext.Ajax.request({
	            url : deleteUrl + "?recursive=true",
	            method : 'DELETE',
	            scope : this,
	            success : function (ret) {
	                node.remove(true);
                    if (saveJson) {
                        this.createJsonTree();
                    }

                    popupMessage(i18n.get('label.information'), i18n.get('label.fileDeleted'), null, 'x-icon-information');
	            },
	            failure : function (response, opts) {
                    Ext.Msg.alert(response.status + " " + response.statusText, response.responseText);
                }
	        });
        } else {
            node.remove();
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
//        this.onCreateJsonTree();
    },
    
    onValid : function (item) {
        if (!item.get('sync')) {
            item.set('sync', true);

            this.changeIcon(item);

            popupMessage(i18n.get('label.information'), i18n.get('label.fileValid'), null, 'x-icon-information');
        } else {
            popupMessage(i18n.get('label.information'), i18n.get('label.alreadyValid'), null, 'x-icon-information');
        }
    },
    
    unValid : function (item) {
        if (item.get('sync')) {
            item.set('sync', false);

            this.changeIcon(item);
            popupMessage(i18n.get('label.information'), i18n.get('label.fileInvalid'), null, 'x-icon-information');
        } else {
            popupMessage(i18n.get('label.information'), i18n.get('label.alreadyInvalid'), null, 'x-icon-information');
        }
    },

    runCopy : function () {
        var copyUrl = this.CONST_URLDATASTORAGE + "/copy" + this.datastorageSrc + this.datastorageDest;
        
        Ext.Ajax.request({
            url : copyUrl,
            method : 'PUT',
            scope : this,
            success : function (ret) {
                popupMessage(i18n.get('label.information'), i18n.get('label.datastorage.copied'), null, 'x-icon-information');
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
            "id": generateId(),
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
            headers : {
                'Content-Type' : 'application/json'
            },
            success : function (response, opts) {
                this.lastModified = response.getResponseHeader("Date");
                popupMessage(i18n.get('label.information'), i18n.get('label.jsonFileCreated'), null, 'x-icon-information');
//                this.tree.store.load(this.tree.getRootNode());
                this.refreshTree();
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
        Ext.Ajax.request({
            url : this.url,
            method : 'GET',
            scope : this,
            failure : function (response, opts) {
                var msg = Ext.Msg.show({
                    title : i18n.get('label.warning'),
                    msg : i18n.get('label.noJsonFileFound'),
                    buttons : {
                        yes : i18n.get('label.yes'),
                        no : i18n.get('label.no'),
                        cancel : i18n.get('label.cancel')
                    },
                    fn : function (btnId, text, opt) {
                        if (btnId === "yes") {
                            this.createEmptyJson(this.url);
                        }
                    },
                    animEl : 'elId',
                    scope : this,
                    icon : Ext.Msg.QUESTION
                });
                Ext.defer(function () {
                    Ext.WindowManager.bringToFront(msg);
                }, 500);
            }
        });
    },
    
    displayViewer : function (url) {
        this.viewerEditorPanel.remove();
        if (this.isPdf(this.newUrl) && this.islocal(this.newUrl)) {
            this.newUrl = Ext.urlAppend(this.newUrl, "dc=" + new Date().getTime());
        }
        var iframe = this.viewerEditorPanel.down();
        iframe.setSrc(this.newUrl);
        this.viewerEditorPanel.setHeight(this.contentPanel.getHeight() - 30);
        this.viewerEditorPanel.setVisible(true);
        this.viewerEditorPanel.expand(true);
        
    },
    
    hideViewer : function () {
        this.viewerEditorPanel.remove();
        this.viewerEditorPanel.setVisible(false);
    },
    
    /**
     * Refresh the tree, reload the tree and print show it again
     */    
    refreshTree : function () {
        this.tree.getRootNode().removeAll();
        this.tree.store.load({
            callback : function (nodes, operation) {
                
                if (operation.success) {
                    this.lastModified = operation.response.getResponseHeader("Last-Modified");
                    this.checkTreeUpToDateTask.delay(this.CHECK_TREE_DELAY * 1000);
                    this.treeBottomBar.setTreeUpToDate(true, this.lastModified);
                    
                    this.setEditable(false);
                    this.tree.getRootNode().expand(true);
                } else {
                     if (operation.error.status === 404) {
                        var msg = Ext.Msg.show({
                            title : i18n.get('label.warning'),
                            msg : i18n.get('label.noJsonFileFound'),
                            buttons : Ext.Msg.YESNO,
                            fn : function (btnId, text, opt) {
                                if (btnId === "yes") {
                                    this.createEmptyJson(this.url);
                                }
                            },
                            animEl : 'elId',
                            scope : this,
                            icon : Ext.MessageBox.QUESTION
                        });
                        Ext.defer(function () {
                            Ext.WindowManager.bringToFront(msg);
                        }, 500);
                    } else if (operation.error.status === 403) {
                        var msg = Ext.Msg.alert(i18n.get("warning.serverError"), i18n.get("label.forbiddenResource"));
                        Ext.defer(function () {
                            Ext.WindowManager.bringToFront(msg);
                            this.getEl().mask();
                        }, 500, this);
                    } else {
                        var msg = Ext.Msg.alert(i18n.get("warning.serverError"), i18n.get("label.invalidResource"));
                        Ext.defer(function () {
                            Ext.WindowManager.bringToFront(msg);
                        }, 500);
                    }
                }
            },
            scope : this
        });  
//        this.tree.store.load(this.tree.getRootNode(), function () {
//            this.tree.getRootNode().expand(true);                        
//        }, this);  
        
    },
    /**
     * Set if the file is editable or not
     * @param {} editable
     */
    setEditable : function (editable) {
        this.editable = editable;
        this.down('textarea').setReadOnly(!this.editable);
        this.saveButton.setDisabled(!this.editable);        
    },
    /**
     * Get the language chosen
     * @return {String} the language chosen
     */
    getChosenLanguage : function () {
        return this.tree.down('contentEditorTreeTopBar combo').getValue();
    },
    
    /**
     * Change the language, reload the tree with the new language
     * @param {String} language the language locale
     */
    changeLanguage : function (language) {
        var urlJson = this.jsonUrlTemplate.replace("{locale}", language);
        this.url = urlJson;
        this.treeStore.setCustomUrl(urlJson);
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
            preferencesFileName : this.itemId
        };

    },
    
    isPdf : function (text) {
        var imageRegex = /\.(pdf)$/;
        if (!Ext.isEmpty(text)) {
            return (text.match(imageRegex));
        } else {
            return false;
        }
    },
    
    islocal : function (url) {
        return (url.indexOf("http://") == -1 && url.indexOf("https://") == -1); 
    }
});
