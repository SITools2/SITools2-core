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
/*
 * global Ext, utils_logout, sitools, SitoolsDesk, window, userLogin,
 * showResponse, projectGlobal, userStorage, DEFAULT_PREFERENCES_FOLDER, i18n,
 * extColModelToJsonColModel, loadUrl
 */

Ext.namespace('sitools.user.view.header');

/**
 * @cfg {String} buttonId the id of the button that displays the window
 * @class sitools.user.component.entete.UserProfile
 * @extends Ext.Window
 */
Ext.define('sitools.user.view.header.UserPersonalView', {
    extend : 'Ext.window.Window',
    alias: 'widget.userPersonalWindow',
    
    width : 320,
    height : 150,
    border : false,
    bodyBorder : false,
    header : false,
    layout : 'fit',
    resizable : false,
    draggable : false,
    
    initComponent : function () {
        
		var freeDisk = 0;
		var totalDisk = 0;
		var userTasksRunning = 0;
		var userTotalTasks = 0;
        var data = [];
        
    	data.push({
            xtype : 'button',
            identifier : "editProfile", 
            name: i18n.get("label.editProfile"), 
            icon : '/sitools/common/res/images/icons/menu/regcrud.png', 
            action : "editProfile",
            comment : ""
        }, {
            xtype : 'button',
            width : 30,
            height : 30,
            identifier : "userDiskSpace", 
            name: i18n.get('label.userDiskSpace'), 
            icon : '/sitools/common/res/images/icons/menu/dataAccess.png',
            action : "showDisk", 
            comment : Ext.String.format(i18n.get("label.userDiskUse"), freeDisk, totalDisk)
        }, {
            xtype : 'button',
            width : 30,
            height : 30,
            identifier : "tasks", 
            name: i18n.get("label.Tasks"), 
            icon : "/sitools/common/res/images/icons/menu/applications2.png",
            action : "showTasks", 
            comment : Ext.String.format(i18n.get("label.taskRunning"), userTasksRunning, userTotalTasks)
        }, {
            xtype : 'button',
            width : 30,
            height : 30,
            identifier : "orders", 
            name: i18n.get("label.orders"), 
            icon : "/sitools/common/res/images/icons/menu/order.png",
            action : "showOrders"
        });
        
	    var store = Ext.create('Ext.data.JsonStore', {
	        fields : ['name', 'icon', 'action', 'comment', 'identifier'],
	        data : data
	    });
	    
	    var tpl = new Ext.XTemplate('<tpl for=".">',
	            '<div class="userButtons" id="{identifier}">',
	            '<div class="userButtons-thumb"><img src="{icon}" title="{name}"></div>',
	            '<span class="userButtons-name">{name}</span>', 
	            '<span class="userButtons-comment">{comment}</span></div>',
	        '</tpl>',
	        '<div class="x-clear"></div>'
	    );
	    
	    var dataview = Ext.create('Ext.view.View', {
	        store: store,
	        cls : "userButtonsDataview", 
	        tpl: tpl,
	        border : false,
	        overItemCls: 'userButtonsPointer',
	        emptyText: 'No images to display', 
	        itemSelector: 'div.userButtons'
	    });
    	
	    this.items = [dataview];
	    
        this.callParent(arguments);
    }
   
});