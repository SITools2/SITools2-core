/***************************************
* Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext*/
Ext.namespace('sitools.public.utils');
/**
 * Help Component
 * @class sitools.user.component.help
 * @extends Ext.Panel
 */
Ext.define('sitools.public.utils.Help', {
	extend : 'Ext.window.Window',
	
    /**
	 * the node to activate
	 * @type Ext.tree.TreeNode
	 */
    activeNode : null,
    modal : true,
    maximizable : true,
    width : 800,
    height : 600,
    
    initComponent : function () {
        this.setTitle(i18n.get('label.help'));
        this.url = loadUrl.get('APP_URL') + i18n.get('path.help.userguide');
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
            htmlReaderCfg.src = this.url;
        }

        this.treeStore = Ext.create('Ext.data.TreeStore', {
        	model : 'sitools.user.model.HelpModel',
        	root : {
        		nodeType : 'async',
        		text : "rootHelp"
        	},
        	proxy : {
        		type : 'ajax',
        		url : loadUrl.get('APP_URL') + "/common/res/statics/help.json",
        		reader : {
        			type : 'json'
        		}
        	}
        });
        
        this.tree = Ext.create('Ext.tree.Panel', {
            region : 'west',
            animate : true,
            width : 250,
            rootVisible : false,
            useArrows : true,
            autoScroll : true,
            split : true,
            collapsible : true,
            collapsed : false,
            store : this.treeStore,
            listeners : {
            	scope : this,
            	itemclick : function (view, record, item) {
            		this.treeAction(record);
            	},
            	viewready : function (tree) {
            		tree.getRootNode().expand(true);
            	}
            }
        });

        this.htmlReader = Ext.create('Ext.ux.IFrame', htmlReaderCfg);

        this.items = [ this.tree, this.htmlReader ];
        
        this.callParent(arguments);
    },

    /**
     * Action executed ; 'this' refers to this component
     * 
     * @param node
     * @returns
     */
    treeAction : function (record) {
        // Getting node urlArticle
        var nodeAnchor = record.get('id');

        if (!Ext.isDefined(nodeAnchor)) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('msg.nodeundefined'));
            return;
        }
        this.htmlReader.load(this.url + "#" + nodeAnchor);
    }

});
