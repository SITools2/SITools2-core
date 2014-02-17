/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
* 
* This file is part of SITools2.
* 
* SITools2 is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* SITools2 is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
***************************************/
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, ann, mainPanel, helpUrl:true, loadUrl, SHOW_HELP*/
Ext.namespace('sitools.admin.menu');

/**
 * Build the sitools tree menu
 * @class sitools.admin.menu.TreeMenu
 */
// TODO ExtJS3 Object > Ext.Object ? 
Ext.define('sitools.admin.menu.TreeMenu', { extend: 'Ext.Object',

    constructor : function (jsonUrl) {
        var tree = new Ext.tree.TreePanel({
            id : ID.CMP.MENU,
            component : this,
            useArrows : false,
            autoScroll : true,
            animate : true,
            root : {
                nodeType : 'async'
            },
            loader : new Ext.tree.TreeLoader({
                requestMethod : 'GET',
                url : jsonUrl
            }),
            rootVisible : false,
            listeners : {
                beforeload : function (node) {
                    node.setText(i18n.get('label.' + node.attributes.nodeName));
                    return node.isRoot || Ext.isDefined(node.attributes.children);
                },
                load : function (node) {
                    node.eachChild(function (item) {
                        item.setText(i18n.get('label.' + item.attributes.nodeName));
                        return true;
                    });
                },
                click : function (node) {
                    if (node.isLeaf()) {
                        this.component.treeAction(node);
                    }
                }
            }
        });
        tree.getRootNode().expand(true);
        return tree;
    },

    /**
     * Action executed ; 'this' refers to this component
     * 
     * @param node
     * @returns
     */
    treeAction : function (node) {
        // Getting nodeName
        var nodeName = node.attributes.nodeName;
        var nodeId = node.attributes.id;

        if (!Ext.isDefined(nodeName)) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('msg.nodeundefined'));
            return;
        }

        if (!node.attributes.mvc && !Ext.ComponentMgr.isRegistered('s-' + nodeName)) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('label.component') + ' \'' + 's-' + nodeName + '\' ' + i18n.get('msg.undefined'));
            return;
        }

        // Displaying Main Panel
        ann(mainPanel, "mainPanel is null");

        
        var pan_config = new sitools.admin.menu.dataView();
        // Loading component 's-nodeName'
        mainPanel.removeAll();
        
        // delte the current MVC module if it exists
//        sitools.util.applicationModulesManager.destroyCurrentModule();
        
//        if (node.attributes.mvc) {
//            
//            var className = node.attributes.classname;
//            
//            var module = sitools.util.applicationModulesManager.registerModule(className);
//            
//            var view = module.getView('crud');
//            view.sitoolsType = "mainAdminPanel";
//            
//	        mainPanel.add(
//	            {
//	                width: "100%", 
//	                items : [ {
//	                    xtype : 's-box',
//	                    label : i18n.get('label.' + nodeName),
//	                    items : [ 
//	                        view
//	                    ],
//	                    idItem : nodeId
//	                } ], 
//	                listeners : {
//	                    resize : function (panel, width, height) {
//	                        var size = panel.items.items[0].body.getSize();
//	                        var sBoxTitle = panel.items.items[0].items.items[0].getEl();
//	                        size = {
//	                            height : size.height - (sBoxTitle.getHeight() + sBoxTitle.getMargins("t") + sBoxTitle.getMargins("b")), 
//	                            width : size.width - 8
//	                        };
//	                        var mainAdminPanel = panel.find("sitoolsType", "mainAdminPanel");
//	                        mainAdminPanel[0].setSize(size);
//	                    }
//	                }
//	         });            
//        } else {
            mainPanel.add(
	        {
	            width: "100%",
	            bodyCssClass : 'admin-bg',
	            items : [ {
	                xtype : 's-box',
	                label : i18n.get('label.' + nodeName),
	                items : [ {
	                    xtype : 's-' + nodeName, 
	                    sitoolsType : "mainAdminPanel"
	                } ],
	                idItem : nodeId
	            } ], 
	            listeners : {
	                resize : function (panel, width, height) {
	                    var size = panel.items.items[0].body.getSize();
	                    var sBoxTitle = panel.items.items[0].items.items[0].getEl();
	                    size = {
	                        height : size.height - (sBoxTitle.getHeight() + sBoxTitle.getMargins("t") + sBoxTitle.getMargins("b")), 
	                        width : size.width - 8
	                    };
	                    var mainAdminPanel = panel.find("sitoolsType", "mainAdminPanel");
	                    mainAdminPanel[0].setSize(size);
	                }
	            }
	        });
//        }

        var helpPanel = new Ext.ux.ManagedIFrame.Panel({
            bodyCssClass : 'admin-bg-transparent',
            id : ID.PANEL.HELP,
            width : "100%", 
            flex : 1,
            // autoScroll:true,
            defaultSrc : loadUrl.get('APP_URL') + "/client-admin/res/help/" + LOCALE + "/" + nodeName + ".html"
        });
        mainPanel.add(
            helpPanel
        );
        mainPanel.doLayout();
        helpPanel.setVisible(SHOW_HELP);
        
        helpUrl = loadUrl.get('APP_URL') + "/client-admin/res/help/" + LOCALE + "/" + nodeName + ".html";
    }

});
