/*******************************************************************************
 * Copyright 2010-2015 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

Ext.namespace('sitools.user.component');
/**
 * Help Component
 * @class sitools.user.component.help
 * @extends Ext.Panel
 */
sitools.user.component.help = Ext.extend(Ext.Panel, {
    /**
	 * the node to activate
	 * @type Ext.tree.TreeNode
	 */
    activeNode : null,
    initComponent : function () {
//        this.url = loadUrl.get('APP_URL') + "/client-user/res/help/fr/Client-userUG.html";
        this.url = loadUrl.get('APP_URL') + "/client-user/res/help/fr/User_Guide.html";
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

        this.tree = new Ext.tree.TreePanel({
            region : 'west',
            animate : true,
            width : 200,
            rootVisible : false,
            useArrows : true,
            autoScroll : true,
            split : true,
            collapsible : true,
            collapsed : false,
            title : "menu",
            root : {
                nodeType : 'async',
                text : "rootHelp"
            },
            loader : new Ext.tree.TreeLoader({
                requestMethod : 'GET',
                url : loadUrl.get('APP_URL') + "/client-user/tmp/help.json",
                listeners : {
                    scope : this,
                    load : function () {
                        if (!Ext.isEmpty(this.activeNode)) {
                            var node = this.tree.getNodeById(this.activeNode);
                            if (!Ext.isEmpty(node)) {
                                this.tree.selectPath(node.getPath());
                                this.treeAction(node);
                            } else {
                                Ext.Msg.alert(i18n.get('label.warning'), i18n.get('msg.nodeundefined'));
                            }
                        }
                    }
                }
            }),
            listeners : {
                scope : this,
                click : function (node, e) {
                    this.treeAction(node);
                }
            }

        });

        this.htmlReader = new Ext.ux.ManagedIFrame.Panel(htmlReaderCfg);

        this.items = [ this.tree, this.htmlReader ];
        
        this.tbar = {
                xtype : 'toolbar',
                cls : 'services-toolbar',
                height : 15,
                defaults : {
                    scope : this,
                    cls : 'services-toolbar-btn'
                },
                items : [ ]
            };

        // tree.getRootNode().expand(true);
        sitools.user.component.help.superclass.initComponent.call(this);
    },

    onRender : function () {
        sitools.user.component.help.superclass.onRender.apply(this, arguments);
        this.tree.getRootNode().expand(true);

    },

    /**
     * Action executed ; 'this' refers to this component
     * 
     * @param node
     * @returns
     */
    treeAction : function (node) {
        // Getting node urlArticle
        var nodeAnchor = node.attributes.nodeAnchor;

        if (!Ext.isDefined(nodeAnchor)) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('msg.nodeundefined'));
            return;
        }
        this.htmlReader.setSrc(this.url + "#" + nodeAnchor);
    }

});

Ext.reg('sitools.user.component.help', sitools.user.component.help);


