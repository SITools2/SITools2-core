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

Ext.namespace('sitools.user.view.modules.dataStorageExplorer');
/**
 * 
 * @class sitools.user.modules.cmsContextMenu
 * @cfg datastorageUrl the url of the datastorage
 * @cfg field the field 
 * @cfg formatValue (function) the function called to format the value before it is set to the field
 * @extends Ext.Window
 */
Ext.define('sitools.user.view.modules.dataStorageExplorer.DataStorageBrowserView', {
    extend : 'Ext.window.Window',
    alias : 'widget.datastorageBrowserView',
    
    height : 600,
    width : 400,
    layout : {
        type : 'vbox',
        align : 'stretch'
    },
    
    initComponent : function () {
        
        this.title = i18n.get("label.createExploreDatastorage") + " : " + this.datastorageUrl;
        
        this.tbar = [ '->', {
            xtype : 'label',
            text : i18n.get('label.uploadFile') + ' :'
        }, {
            xtype : 'button',
            iconAlign : 'right',
            iconCls : 'upload-icon',
            tooltip : i18n.get('label.uploadFile'),
            scope : this,
            handler : function (btn, e) {
                var node = this.tree.getSelectionModel().getSelection()[0];
                if (node != undefined){
                    var urlUpload;
                    if (node.isLeaf()){
                        urlUpload = node.parentNode.get('url');
                    }
                    else if (node.get('cls')){
                        urlUpload = node.get('url');
                    }
                    else if (node.isRoot){
                        urlUpload = this.datastorageUrl;
                    }

                    var callbackUpload = function (createdNode) {
                        this.appendChild(createdNode);
                    }

                    var uploadWin = Ext.create('sitools.user.view.modules.dataStorageExplorer.DataStorageUploadFileView', {
                        urlUpload : urlUpload,
                        callback : callbackUpload,
                        scope : node
                    }).show();
                    uploadWin.showAt(e.getX(), e.getY(), true);
                }
                else {
                    Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.noneNodeSelected'));
                }
            }
        }];

        this.treeStore = Ext.create('sitools.user.store.DataStorageTreeStore');

        this.tree = Ext.create('Ext.tree.Panel', {
            flex : 1,
            animate : true,
            width : 300,
            expanded : true,
            autoScroll : true,
            containerScroll : true,
            layout : "fit",
            store : this.treeStore,
//            loader : new Ext.tree.TreeLoader({
//                baseParams : {
//                    index : ""
//                },
//                preloadChildren : true,
//                requestMethod : 'GET',
//                url : this.datastorageUrl,
//                createNode : function(attr){
//                    var isPdf = function (text) {
//                        var imageRegex = /\.(pdf)$/;
//                        return (text.match(imageRegex));
//                    };
//
//                    var url = attr.url;
//                    var appUrl = loadUrl.get('APP_URL');
//                    var index = url.indexOf(appUrl);
//                    if (index !== -1) {
//                        url = url.substring(index, url.length);
//                    }
//                    attr.url = url;
//
//                    var listeners = {
//                        scope : this,
//                        beforeappend : function (tree, parent, item ){
//                            if (item.isLeaf()){
//                                if (isPdf(item.attributes.text)) {
//                                    item.setIcon(loadUrl.get('APP_URL') + '/common/res/images/icons/icon-pdf-small.png');
//                                }
//                                else {
//                                    item.setIcon(loadUrl.get('APP_URL') + '/cots/'+EXT_JS_FOLDER+'/resources/images/default/tree/leaf.gif');
//                                }
//                            }
//                            return true;
//                        }
//                    };
//                    Ext.apply(attr, {
//                        listeners : listeners
//                    });
//                    // apply baseAttrs, nice idea Corey!
//                    if(this.baseAttrs){
//                        Ext.applyIf(attr, this.baseAttrs);
//                    }
//                    if(this.applyLoader !== false && !attr.loader){
//                        attr.loader = this;
//                    }
//                    if(Ext.isString(attr.uiProvider)){
//                       attr.uiProvider = this.uiProviders[attr.uiProvider] || eval(attr.uiProvider);
//                    }
//                    if(attr.nodeType){
//                        return new Ext.tree.TreePanel.nodeTypes[attr.nodeType](attr);
//                    }else{
//                        return attr.leaf ?
//                                    new Ext.tree.TreeNode(attr) :
//                                    new Ext.tree.AsyncTreeNode(attr);
//                    }
//                }
//            }),
            rootVisible : true,
            root : {
                text : "root",
                leaf : false,
                url : this.datastorageUrl
            },
            listeners : {
                scope : this,

                beforeitemexpand : function (node, opts) {

                    node.removeAll();
                    var reference = new Reference(node.get('url'));
                    var url = reference.getFile();
                    Ext.Ajax.request({
                        params: {
                            index: ""
                        },
                        url: url,
                        headers: {
                            'Accept': 'application/json+sitools-directory'
                        },
                        method: 'GET',
                        scope: this,
                        success: function (ret) {
                            try {
                                var Json = Ext.decode(ret.responseText);

                                //first append the folders
                                Ext.each(Json, function (child) {
                                    if (Ext.isEmpty(child.leaf)) {
                                        child.leaf = false;
                                    }
                                    child.children = [];
                                    if (!child.leaf) {
                                        var nodeAdded = this.appendChild(child, node);
                                        child.id = nodeAdded.id;
                                    }
                                }, this);

                                //then append the files
                                Ext.each(Json, function (child) {
                                    if (child.leaf) {
                                        var nodeAdded = this.appendChild(child, node);
                                        child.id = nodeAdded.id;
                                    }
                                }, this);

                                return true;

                            } catch (err) {
                                throw err;
                            }
                        }
                    });
                }

//                beforeload : function (node) {
//                    return node.isRoot || Ext.isDefined(node.attributes.children);
//                },
//                beforeexpandnode : function (node) {
//                    node.removeAll();
//                    var reference = new Reference(node.attributes.url);
//                    var url = reference.getFile();
//                    Ext.Ajax.request({
//                        params : {
//                            index : ""
//                        },
//                        url : url,
//                        headers : {
//                            'Accept' : 'application/json+sitools-directory'
//                        },
//                        method : 'GET',
//                        scope : this,
//                        success : function (ret) {
//                            try {
//                                var Json = Ext.decode(ret.responseText);
//                                Ext.each(Json, function (child) {
//                                    var nodeAdded = node.appendChild({
//                                        cls : child.cls,
//                                        text : child.text,
//                                        url : child.url,
//                                        leaf : Boolean(child.leaf),
//                                        size : child.size,
//                                        lastmod : child.lastmod,
//                                        children : []
//                                    });
//                                    child.id = nodeAdded.id;
//                                });
//
//                                this.tree.expandPath(node.getPath());
//                                return true;
//                            } catch (err) {
//                                return false;
//                            }
//                        },
//                        failure : function (ret) {
//                            return null;
//                        }
//                    });
//                    return true;
//                }
            }
        });
        
        this.items = [this.tree];
        
        this.buttons = [{
            text : i18n.get('label.ok'),
            scope : this,
            handler : this.onValidate
        }, {
            text : i18n.get('label.cancel'),
            scope : this,
            handler : function () {
                this.close();
            }
        }];
    
        this.callParent(arguments);
    },

    onRender : function () {
        this.callParent(arguments);
        this.tree.getRootNode().expand();
    },
    
    onValidate : function () {
        var node = this.tree.getSelectionModel().getSelection()[0];

        if (Ext.isEmpty(node)) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.noSelection'));
            return;
        }
        if (!node.isLeaf()) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.notSupportedYet'));
            return;
        }
        
        var value = node.get('url');
        if (!Ext.isEmpty(this.formatValue) && Ext.isFunction(this.formatValue)) {
            value = this.formatValue(value);
        }
        
        this.field.setValue(value);
        this.close();
    },

    appendChild : function (child, parent) {
        var reference = new Reference(child.url);
        var url = reference.getFile();

        var name = decodeURIComponent(child.text);
        var text = name;
        if (child.leaf) {
            text += "<span style='font-style:italic'> (" + Ext.util.Format.fileSize(child.size) + ")</span>";
        }

        var result = parent.appendChild({
            commonStoreId : Ext.id(),
            cls : child.cls,
            text : text,
            name : name,
            url : url,
            leaf : child.leaf,
            size : child.size,
            lastmod : child.lastmod,
            expandable : !child.leaf
        });

        return result;
    }
});