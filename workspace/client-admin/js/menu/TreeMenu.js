/***************************************
* Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.define('sitools.admin.menu.TreeMenu', {
    extend: 'Ext.tree.Panel',
    alias : 'widget.treemenucontainer',

    
    
    constructor : function (jsonUrl) {
        Ext.apply(this, {
            id : ID.CMP.MENU,
            component : this,
            useArrows : false,
            autoScroll : true,
            animate : true,
            store : Ext.create('Ext.data.TreeStore', {
                fields : [ {
                    name : 'iconMenu'
                }, {
                    name : 'name',
                    mapping : 'nodeName'
                }, {
                    name : 'text',
                    convert : function (value, record) {
                        return i18n.get('label.' + record.get('name'));
                    }
                } ],
                proxy : {
                    type : 'ajax',
                    url : jsonUrl,
                    reader : {
                        root : 'children',
                        type : 'json'
                    }
                }
            }),
            rootVisible : false,
            listeners : {
                itemclick : function (tree, node) {
                    if (node.isLeaf()) {
                        this.component.treeAction(node);
                    }
                },
                afterrender : function (tree) {
                    tree.expandAll();
                }
            }
        });
        this.callParent(this);
        
    },

    /**
     * Action executed ; 'this' refers to this component
     * 
     * @param node
     * @returns
     */
    treeAction : function (node) {
        // Getting nodeName
        var nodeName = node.get('name');
        var nodeId = node.get('id');
        this.openModule(nodeName, nodeId);       
        
        
//        helpUrl = loadUrl.get('APP_URL') + "/client-admin/res/help/" + LOCALE + "/" + nodeName + ".html";
    },
    
    openModule : function (nodeName, nodeId)  {
    	if (!Ext.isDefined(nodeName)) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('msg.nodeundefined'));
            return;
        }

        var currentActivePanel = mainPanel.down("panel [sitoolsType=mainAdminPanel]");
        if (currentActivePanel != null && currentActivePanel.xtype == 'widget.s-' + nodeName) {
            return;
        }
        
        // Displaying Main Panel
        ann(mainPanel, "mainPanel is null");
        
        var pan_config = new sitools.admin.menu.dataView();
        // Loading component 's-nodeName'
        mainPanel.removeAll(true);
        
        mainPanel.add({
            xtype : 'panel',
            width: "100%",
//            bodyCls : 'admin-bg',
            border : false,
            bodyBorder : false,
            items : [{
                xtype : 's-box',
//	                label : i18n.get('label.' + nodeName),
                items : [{
                    xtype : 's-' + nodeName,
                    sitoolsType : "mainAdminPanel",
                    cls : "adminPanel"
                }],
//	                idItem : nodeId
            } ], 
            listeners : {
                resize : function (panel, width, height) {
                    var size = panel.items.items[0].body.getSize();
                    var sBoxTitle = panel.items.items[0].items.items[0].getEl();
                    size = {
                        height : size.height - (sBoxTitle.getHeight() + sBoxTitle.getMargin("t") + sBoxTitle.getMargin("b")), 
                        width : size.width - 8
                    };
                    var mainAdminPanel = panel.down("panel[sitoolsType=mainAdminPanel]");
                    mainAdminPanel.setSize(size);
                }
            }
        });
            
        mainPanel.setIconCls('icon-' + nodeId);
        mainPanel.setTitle(i18n.get('label.' + nodeName));
//        }

        var helpPanel = Ext.create('Ext.Component', {
            bodyCls : 'admin-bg-transparent',
            id : ID.PANEL.HELP,
            width : "100%", 
            flex : 1,
            autoEl : {
                tag : 'iframe',
                src : loadUrl.get('APP_URL') + "/client-admin/res/help/" + LOCALE + "/" + nodeName + ".html"
            }
//            defaultSrc : loadUrl.get('APP_URL') + "/client-admin/res/help/" + LOCALE + "/" + nodeName + ".html"
        });
        
        mainPanel.add( helpPanel );
        mainPanel.doLayout();
        helpPanel.setVisible(SHOW_HELP);
    }

});
