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
 * 
 * @class sitools.user.modules.cmsContextMenu
 * @cfg datastorageUrl the url of the datastorage
 * @cfg field the field 
 * @cfg formatValue (function) the function called to format the value before it is set to the field
 * @extends Ext.Window
 */
sitools.user.modules.datastorageBrowser  = Ext.extend(Ext.Window, {
    
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
                handler : function () {
                    var node = this.tree.getSelectionModel().getSelectedNode();
                    if (node != undefined){
                        var urlUpload;
				        if (node.attributes.leaf == "true"){
				            urlUpload = node.parentNode.attributes.url;
				        }
				        else if (node.attributes.cls){
				            urlUpload = node.attributes.url;
				        }
				        else if (node.isRoot){
				            urlUpload = this.datastorageUrl;
				        }
				        
				        var callbackUpload = function (createdNode) {
					        this.appendChild(createdNode);
				        }
				        
				        var uploadWin = new sitools.user.modules.datastorageUploadFile({
				        	urlUpload : urlUpload,
				        	callback : callbackUpload,
				        	scope : node
				        }).show();
				        
                    }
                    else {
                        Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.noneNodeSelected'));
                    }
                }
            }];
            
        this.tree = new Ext.tree.TreePanel({
            flex : 1,
            animate : true,
            width : 300,
            expanded : true,
            autoScroll : true,
            containerScroll : true,
            layout : "fit",
            loader : new Ext.tree.TreeLoader({
                baseParams : {
                    index : ""
                },
                preloadChildren : true,
                requestMethod : 'GET',
                url : this.datastorageUrl,
                createNode : function(attr){
                    var isPdf = function (text) {
                        var imageRegex = /\.(pdf)$/;
                        return (text.match(imageRegex));      
                    };
                    
                    var url = attr.url;
                    var appUrl = loadUrl.get('APP_URL');
                    var index = url.indexOf(appUrl);
                    if (index !== -1) {
                        url = url.substring(index, url.length);
                    }
                    attr.url = url;
                    
                    var listeners = {
                        scope : this,
                        beforeappend : function (tree, parent, item ){
                            if (item.isLeaf()){
                                if (isPdf(item.attributes.text)) {
                                    item.setIcon(loadUrl.get('APP_URL') + '/common/res/images/icons/icon-pdf-small.png');
                                }
                                else {
                                    item.setIcon(loadUrl.get('APP_URL') + '/cots/extjs/resources/images/default/tree/leaf.gif');
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
                }
            }),
            rootVisible : true,
            root : {
                text : "root",
                leaf : false,
                url : this.datastorageUrl
            },
            listeners : {
                scope : this,
                beforeload : function (node) {
                    return node.isRoot || Ext.isDefined(node.attributes.children);
                },    
                beforeexpandnode : function (node) {
                    node.removeAll();
                    var reference = new Reference(node.attributes.url);
                    var url = reference.getFile();
                    Ext.Ajax.request({
                        params : {
                            index : ""
                        },
                        url : url,
                        headers : {
                            'Accept' : 'application/json+sitools-directory'
                        },
                        method : 'GET',
                        scope : this,
                        success : function (ret) {
                            try {
                                var Json = Ext.decode(ret.responseText);
                                Ext.each(Json, function (child) {
                                    var nodeAdded = node.appendChild({
                                        cls : child.cls,
                                        text : child.text,
                                        url : child.url,
                                        leaf : Boolean(child.leaf),
                                        size : child.size,
                                        lastmod : child.lastmod,
                                        children : []
                                    });
                                    child.id = nodeAdded.id;
                                });
                                
                                this.tree.expandPath(node.getPath());
                                return true;
                            } catch (err) {
                                return false;
                            }
                        },
                        failure : function (ret) {
                            return null;
                        }
                    });
                    return true;                    
                }
            }
        });
        
        this.items = [this.tree];
        
        this.buttons = [ {
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
    
        
        sitools.user.modules.datastorageBrowser.superclass.initComponent.call(this);
    },
    
    onRender : function () {
        sitools.user.modules.datastorageBrowser.superclass.onRender.apply(this, arguments);
        this.tree.getRootNode().expand();
    },
    
    onValidate : function () {
        var node = this.tree.getSelectionModel().getSelectedNode();
        if (Ext.isEmpty(node)) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.noSelection'));
            return;
        }
        if (!node.isLeaf()) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.notSupportedYet'));
            return;
        }
        
        var value = node.attributes.url;
        if (!Ext.isEmpty(this.formatValue) && Ext.isFunction(this.formatValue)) {
            value = this.formatValue(value);
        }
        
        this.field.setValue(value);
        this.close();
    }
     
});