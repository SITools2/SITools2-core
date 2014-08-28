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

Ext.namespace('sitools.user.view.component.personal');

/**
 * All personal user information (space, tasks, commands, profile)
 * @class sitools.user.view.component.personal.UserPersonalView
 * @extends Ext.panel.Panel
 */
Ext.define('sitools.user.view.component.personal.UserPersonalView', {
    extend : 'Ext.panel.Panel',
    alias: 'widget.userPersonal',
    
    border : false,
    layout : {
        type : 'hbox',
        pack : 'start',
        align : 'stretch'
    },
    
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
            action : "onEditProfile",
            comment : ""
        }, {
            xtype : 'button',
            identifier : "userDiskSpace", 
            name: i18n.get('label.userDiskSpace'), 
            icon : '/sitools/common/res/images/icons/menu/dataAccess.png',
            action : "showDisk", 
            comment : Ext.String.format(i18n.get("label.userDiskUse"), freeDisk, totalDisk)
        }, {
            xtype : 'button',
            identifier : "tasks", 
            name: i18n.get("label.Tasks"), 
            icon : "/sitools/common/res/images/icons/menu/applications2.png",
            action : "showTasks", 
            comment : Ext.String.format(i18n.get("label.taskRunning"), userTasksRunning, userTotalTasks)
        }, {
            xtype : 'button',
            identifier : "orders", 
            name: i18n.get("label.orders"), 
            icon : "/sitools/common/res/images/icons/menu/order.png",
            action : "showOrders"
        });
        
	    this.storeAction = Ext.create('Ext.data.JsonStore', {
	        fields : ['name', 'icon', 'action', 'comment', 'identifier'],
	        data : data
	    });
    	
		this.gridAction = Ext.create('Ext.grid.Panel', {
			itemId : 'gridAction',
			width : 170,
			hideHeaders : true,
			border : false,
			bodyBorder : false,
			syncRowHeight : false,
			store: this.storeAction,
            columns: [{
                dataIndex: 'icon',
                width : 40,
                border : false,
                renderer : function (value, meta, record) {
                	return "<img height=24 src='" + value + "'/>";
                }
            }, {
                dataIndex: 'name',
                border : false,
                flex: 1
            }],
            listeners : {
            	scope : this,
            	itemclick : this.actionItemClick
            }
		});
		
		this.contentPanel = Ext.create('Ext.panel.Panel', {
			flex : 1,
			layout : 'fit',
			autoScroll : true,
			border : false
		});
	    
		this.items = [ this.gridAction, {
			xtype : 'splitter',
			style : 'background-color:#EBEBEB;'
		}, this.contentPanel];
		
        this.callParent(arguments);
    },
    
    actionItemClick : function (grid, record, item, index, e) {
    	var data = grid.getSelectionModel().getSelection()[0].data;   
    	eval("this." + data.action).call(this, grid, record, index);
	},
    
    /**
     * Edit the profile of the user depending on the server configuration
    * @param {Ext.DataView} dataView the clicked Dataview
     * @param {numeric} index the index of the clicked node
     * @param {Html Element} node the clicked html element 
     * @param {Ext.event} e The click event
     */
    editProfile : function (grid, record, index) {
        if (userLogin === "public") {
            return;
        }
        
        var callback = Ext.Function.bind(this.onEditProfile, this, [grid, record, index]);        
        sitools.public.utils.LoginUtils.editProfile(callback);
        
    },
    
    /**
     * Open a window in the desktop with the sitools.userProfile.editProfile object. 
     */
    onEditProfile : function (grid, record, index) {

    	var editProfileView = Ext.create('sitools.public.userProfile.editProfile', {
                identifier : userLogin,
                url : loadUrl.get('APP_URL') + '/editProfile/' + userLogin
            });
    	
    	var contentPanel = grid.up('userPersonal').contentPanel;
    	contentPanel.removeAll();
    	contentPanel.add(editProfileView);
    },
    
    /**
     * Open a window in the desktop with the sitools.user.component.entete.userProfile.tasks object. 
     * @param {Ext.DataView} dataView the clicked Dataview
     * @param {numeric} index the index of the clicked node
     * @param {Html Element} node the clicked html element 
     * @param {Ext.event} e The click event
     */
    showTasks : function (grid, record, index) {
        
    	var taskView = Ext.create('sitools.user.view.component.personal.TaskView');
    	
    	var contentPanel = grid.up('userPersonal').contentPanel;
    	contentPanel.removeAll();
    	contentPanel.add(taskView);
    },
    
    /**
     * Open a window in the desktop with the sitools.user.component.entete.userProfile.diskSpace object. 
     * @param {Ext.DataView} dataView the clicked Dataview
     * @param {numeric} index the index of the clicked node
     * @param {Html Element} node the clicked html element 
     * @param {Ext.event} e The click event
     */
    showDisk : function (grid, record, index) {

    	var diskSpaceView = Ext.create('sitools.user.view.component.personal.DiskSpaceView');
    	var contentPanel = grid.up('userPersonal').contentPanel;
    	
    	contentPanel.removeAll();
    	contentPanel.add(diskSpaceView);
    	
    }, 
    
    /**
     * Open a window in the desktop with the sitools.user.component.entete.userProfile.diskSpace object. 
     * @param {Ext.DataView} dataView the clicked Dataview
     * @param {numeric} index the index of the clicked node
     * @param {Html Element} node the clicked html element 
     * @param {Ext.event} e The click event
     */
    showOrders : function (grid, record, index) {

    	var orderView = Ext.create('sitools.user.view.component.personal.OrderView');
    	
    	var contentPanel = grid.up('userPersonal').contentPanel;
    	contentPanel.removeAll();
    	contentPanel.add(orderView);
    	
    },
    
    /**
     * method called when trying to save preference
     * 
     * @returns
     */
    _getSettings : function () {
        return {
            preferencesPath : "/components",
            preferencesFileName : this.id,
            componentClazz : this.componentClazz
        };
    }
   
});