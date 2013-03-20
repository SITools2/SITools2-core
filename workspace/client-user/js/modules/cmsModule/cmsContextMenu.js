/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 * Context menu for the tree of the CMS
 * @class sitools.user.modules.cmsContextMenu
 * @cfg cms the cms where the menu is from
 * @extends Ext.menu.Menu
 */
sitools.user.modules.cmsContextMenu = Ext.extend(Ext.menu.Menu, {
    
    initComponent : function () {
        
        this.items = [
	        {
		        text: i18n.get('label.addContent'),
		        icon : loadUrl.get('APP_URL') + '/common/res/images/icons/add-html.png',
                scope : this,
		        handler : function () {
		            var windowLink = new sitools.user.modules.chooseFileInExplorer({
		                datastorageUrl : this.cms.dynamicUrlDatastorage,
		                node : this.cms.tree.getSelectionModel().getSelectedNode(),
		                cms : this.cms,
                        action : "create"
		            });
		            windowLink.show();
		        }
	        },
            {
                text: i18n.get('label.properties'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/toolbar_edit.png',
                scope : this,
                handler : function () {
                    var windowLink = new sitools.user.modules.chooseFileInExplorer({
                        datastorageUrl : this.cms.dynamicUrlDatastorage,
                        node : this.cms.tree.getSelectionModel().getSelectedNode(),
                        cms : this.cms,
                        action : "edit"
                    });
                    windowLink.show();
                }
            }, {
	             id: 'manage-node',
	             text: i18n.get('label.manage'),
	             icon : loadUrl.get('APP_URL') + '/common/res/images/icons/icon-manage.png',
	             menu : {
	                 items : [{
	                    text : i18n.get('label.valid'),
	                    icon : loadUrl.get('APP_URL') + '/common/res/images/icons/valid.png',
	                    action : "valid",
	                    scope : this,
	                    handler : function (me){
	                        var node = this.cms.tree.getSelectionModel().getSelectedNode();
	                        this.cms.manageNodes(node, me.action);
	                        this.hide();
	                    }
	                },
	                {
	                    text : i18n.get('label.unvalid'),
	                    icon : loadUrl.get('APP_URL') + '/common/res/images/icons/unvalid.png',
	                    action : "unvalid",
	                    scope : this,
	                    handler : function (me){
	                        var node = this.cms.tree.getSelectionModel().getSelectedNode();
	                        this.cms.manageNodes(node, me.action);
	                        this.hide();
	                    }
	                }]
	             }
        },
        {
            text : i18n.get('label.delete'),
            icon : loadUrl.get('APP_URL') + '/common/res/images/icons/delete.png',
            scope : this,
            handler : function (){
                var node = this.cms.tree.getSelectionModel().getSelectedNode();
                Ext.Msg.confirm(i18n.get('label.info'), i18n.get('label.sureDelete') + '<b>' + node.attributes.text + '</b>' + " ?",
                        function (btn){
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
                this.hide();
            }
        }];
        sitools.user.modules.cmsContextMenu.superclass.initComponent.call(this);
    }
    
    
    
    
    
});