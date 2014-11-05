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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse */

Ext.namespace('sitools.user.controller.modules.dataStorageExplorer');
/**
 * dataStorageExplorer Module
 * 
 * @class sitools.user.modules.dataStorageExplorer
 * @extends Ext.Panel
 */
Ext.define('sitools.user.controller.modules.dataStorageExplorer.DataStorageExplorerBrowserController', {
    extend : 'Ext.app.Controller',
    alias : 'sitools.user.modules.dataStorageExplorer',

    views : [ 'sitools.user.view.modules.dataStorageExplorer.DataStorageBrowserView'],

    init : function () {
        this.control({
        	"datastorageBrowserView" : {
        		render : function (view) {
			        view.tree.getRootNode().expand();
        		}        		
        	},
            "datastorageBrowserView treepanel" : {
            	itemclick : function ( tree, record, item, index, e, eOpts ) {
            		tree.up("datastorageBrowserView").down('button#upload').setVisible(!record.isRoot() && !record.isLeaf());
            		tree.up("datastorageBrowserView").down('label').setVisible(!record.isRoot()&& !record.isLeaf());
            	},
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
            },
            "datastorageBrowserView button#cancel" : {
            	click : function (btn, e) {
            		btn.up("datastorageBrowserView").close();
            	}
            },
            "datastorageBrowserView button#validate" : {
            	click : this.onValidate
            },
            "datastorageBrowserView button#upload" : {
            	click : function (btn, e) {
            		var self = btn.up("datastorageBrowserView");
                    var node = self.tree.getSelectionModel().getSelection()[0];
                    if (node != undefined){
                        var urlUpload;
                        if (node.isLeaf()){
                            urlUpload = node.parentNode.get('url');
                        }
                        else if (node.get('cls')){
                            urlUpload = node.get('url');
                        }
                        else if (node.isRoot){
//                            urlUpload = self.datastorageUrl;
                        	Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.cannotuploadtoroot'));
                        	return;
                        }

                        var callbackUpload = function (createdNode) {
                            self.appendChild(createdNode);
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
            }
        });
    },
    
    onValidate : function (btn) {
    	var self = btn.up("datastorageBrowserView");
        var node = self.tree.getSelectionModel().getSelection()[0];

        if (Ext.isEmpty(node)) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.noSelection'));
            return;
        }
        if (!node.isLeaf()) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.notSupportedYet'));
            return;
        }
        
        var value = node.get('url');
        if (!Ext.isEmpty(self.formatValue) && Ext.isFunction(self.formatValue)) {
            value = self.formatValue(value);
        }
        
        self.field.setValue(value);
        self.close();
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
