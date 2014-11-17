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
        
        this.tbar = Ext.create('Ext.toolbar.Toolbar',{
        		items : [ '->', '', {
		            xtype : 'label',
		            text : i18n.get('label.uploadFile') + ' :',
		            hidden : true
		        }, {
		            xtype : 'button',
		            iconAlign : 'right',
		            iconCls : 'upload-icon',
		            tooltip : i18n.get('label.uploadFile'),
		            itemId : 'upload',
		            hidden : true
        	}],
        	height : 37
        });

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
            rootVisible : true,
            root : {
                text : "root",
                leaf : false,
                url : this.datastorageUrl
            }
        });
        
        this.items = [this.tree];
        
        this.buttons = [{
            text : i18n.get('label.ok'),
            itemId : 'validate'
        }, {
            text : i18n.get('label.cancel'),
            itemId : 'cancel'            
        }];
    
        this.callParent(arguments);
    }
});