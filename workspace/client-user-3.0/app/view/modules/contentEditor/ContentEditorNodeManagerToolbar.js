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

Ext.namespace('sitools.user.view.modules.contentEditor');
/**
 * Context menu for the tree of the CMS
 * @class sitools.user.view.modules.contentEditor.ContentEditorContextMenu
 * @cfg cms the cms where the menu is from
 * @extends Ext.menu.Menu
 */
Ext.define('sitools.user.view.modules.contentEditor.ContentEditorNodeManagerToolbar', {
	extend : 'Ext.toolbar.Toolbar',
    border : false,

    initComponent : function () {
        
        this.items = [{
            xtype : 'buttongroup',
            columns : 1,
            frame : false,
            layout : {
                type : 'vbox',
                align : 'stretchmax',
                defaultMargins : {top: 3, right: 0, bottom: 0, left: 0}
            },
            items : [{
            text : i18n.get('label.addContent'),
            itemId : 'addContentBtnId',
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/add-html.png',
            scope : this,
            handler : function () {
                var node = this.cms.tree.getSelectionModel().getSelection()[0];
                if (Ext.isEmpty(node)) {
                    return;
                }
                var windowLink = Ext.create('sitools.user.view.modules.contentEditor.ChooseFileInExplorerView', {
                    datastorageUrl : this.cms.dynamicUrlDatastorage,
                    node : node,
                    cms : this.cms,
                    action : "create"
                });
                windowLink.show();
            }
	        }, {
	            text: i18n.get('label.properties'),
                itemId : 'propertiesBtnId',
	            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_edit.png',
	            scope : this,
	            handler : function () {
	                var node = this.cms.tree.getSelectionModel().getSelection()[0];
	                if (Ext.isEmpty(node)) {
	                    return;
	                }
	                var windowLink = Ext.create('sitools.user.view.modules.contentEditor.ChooseFileInExplorerView', {
	                    datastorageUrl : this.cms.dynamicUrlDatastorage,
	                    node : node,
	                    cms : this.cms,
	                    action : "edit"
	                });
	                windowLink.show();
	            }
	        }]
	        }, {
	            xtype : 'buttongroup',
	            columns : 1,
	            frame : false,
	            layout : {
	                type : 'vbox',
	                align : 'stretchmax',
	                defaultMargins : {top: 3, right: 0, bottom: 0, left: 0}
	            },
	            items : [{
	             itemId: 'manage-node',
	             text: i18n.get('label.manage'),
	             icon : loadUrl.get('APP_URL') + '/common/res/images/icons/icon-manage.png',
                 disabled : true,
	             menu : {
	                 border : false,
	                 plain : true,
	                 items : [{
	                    text : i18n.get('label.valid'),
	                    cls : 'menuItemCls',
	                    icon : loadUrl.get('APP_URL') + '/common/res/images/icons/valid.png',
	                    action : "valid",
	                    scope : this,
	                    handler : function (me) {
	                        var node = this.cms.tree.getSelectionModel().getSelection()[0];
	                        if (Ext.isEmpty(node)) {
	                            return;
	                        }
	                        this.cms.manageNodes(node, me.action);
	                    }
	                },
	                {
	                    text : i18n.get('label.unvalid'),
	                    cls : 'menuItemCls',
	                    icon : loadUrl.get('APP_URL') + '/common/res/images/icons/unvalid.png',
	                    action : "unvalid",
	                    scope : this,
	                    handler : function (me){
	                        var node = this.cms.tree.getSelectionModel().getSelection()[0];
	                        this.cms.manageNodes(node, me.action);
	                    }
	                }]
	             }
	        }, {
	            text : i18n.get('label.delete'),
                itemId : 'deleteBtnId',
	            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/delete.png',
	            scope : this,
	            handler : function (){
	                var node = this.cms.tree.getSelectionModel().getSelection()[0];
	                if (Ext.isEmpty(node)) {
	                    return;
	                }
	                Ext.Msg.confirm(i18n.get('label.info'), i18n.get('label.sureDelete') + '<b>' + node.get('text') + '</b>' + " ?",
	                    function (btn) {
	                        if (btn == 'yes'){
	                            var ind = this.cms.tree.getRootNode().indexOf(node);
	                            if (ind != 0){
	                                this.cms.deleteNode(node, false, true);
	                            }
	                            else {
	                                Ext.Msg.alert(i18n.get('label.info'), i18n.get('label.cannotDeleteRootNode'));
	                            }
	                        }
	                    }, this);
	               }
	        }]
        }];
        
        this.callParent(arguments);
    },
    
    manage : function (node) {
        var me = this;
        var manageBtn = me.down('buttongroup > button[itemId="manage-node"]');
        if (node.isLeaf()) {
            manageBtn.setDisabled(false);
        } else {
            manageBtn.setDisabled(true);
        }
        
        var deleteBtn = me.down('buttongroup > button[itemId="deleteBtnId"]');
        var rootNode = me.cms.tree.getRootNode();
        
        if (rootNode.getChildAt(0) === node) {
            deleteBtn.setDisabled(true);
        } else {
            deleteBtn.setDisabled(false);
        }
    }
});