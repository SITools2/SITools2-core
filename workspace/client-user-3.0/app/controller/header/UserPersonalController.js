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

Ext.namespace('sitools.user.controller.header');

/**
 * Populate the div x-headers of the sitools Desktop. 
 * @cfg {String} htmlContent html content of the headers, 
 * @cfg {Array} modules the modules list
 * @class sitools.user.component.entete.Entete
 * @extends Ext.Panel
 */
Ext.define('sitools.user.controller.header.UserPersonalController', {
    extend : 'Ext.app.Controller',
    
    views : ['header.UserPersonalView'],
    
    init : function () {
        
    	//TODO pb call two times each event so instanciate two times each component 
//        this.control({
//        	
//            'userPersonalWindow > grid > gridview' : {
//	            itemclick : this.actionItemClick
//            }
//            
//        });
        this.callParent(arguments);
    },
    
    /**
     * Send a request on user task resource to determine how many tasks are launched and finished.
     * Update the task comment div with the formated result. 
     */
    fillDiskInformations : function () {
        var el = Ext.dom.Query.select("div[id='userDiskSpace'] span[class='userButtons-comment']")[0];
        if (Ext.isEmpty(el)) {
            return;
        }
        Ext.Ajax.request({
            method : "GET",

            url : loadUrl.get('APP_URL') + loadUrl.get('APP_USERSTORAGE_USER_URL').replace("{identifier}", userLogin) + "/status", 
            success : function (ret) {
                var json = Ext.decode(ret.responseText);
                if (!json.success) {
                    return;
                }
                var storage = json.userstorage.storage;
                var totalSpace = storage.quota;
                var usedSpace = storage.busyUserSpace;
                var pourcentage = usedSpace / totalSpace * 100;
                var cls = null; 
                if (pourcentage >= 90 && pourcentage < 100) {
                    Ext.get("userDiskSpace").addClass("sitools-userProfile-warning-icon");
                    cls = "sitools-userProfile-warning-text";
                }
                else if (pourcentage > 100) {
                    Ext.get("userDiskSpace").addClass("sitools-userProfile-error-icon");
                    cls = "sitools-userProfile-error-text";
                }
                var str = "";
                if (!Ext.isEmpty(cls)) {
                    str += "<span class='" + cls + "'>";
                }
                str += Ext.String.format(i18n.get('label.diskSpace'), Ext.util.Format.round(pourcentage, 0), Ext.util.Format.fileSize(totalSpace));
                if (!Ext.isEmpty(cls)) {
                    str += "</span>";
                }
                Ext.get(el).update(str);
            }
        });
    },
    
    /**
     * Send a request on user userstorage resource to determine the space allowed and consumed. 
     * Update the diskSpace comment div with the formated result. 
     */
    fillTaskInformations : function () {
        var el = Ext.DomQuery.select("div[id='tasks'] span[class='userButtons-comment']")[0];
        if (Ext.isEmpty(el)) {
            return;
        }
        Ext.Ajax.request({
            method : "GET",
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_USERRESOURCE_ROOT_URL') +  "/" + userLogin + "/tasks", 
            success : function (ret) {
                var json = Ext.decode(ret.responseText);
                if (!json.success) {
                    return;
                }
                var runningTasks = 0, totalTasks = 0;
                Ext.each(json.data, function (task) {
                    if (task.status === "TASK_STATUS_RUNNING" || task.status === "TASK_STATUS_PENDING") {
                        runningTasks++;
                    }
                    totalTasks++;
                });
                if (runningTasks > 0) {
                    el.update(Ext.String.format(i18n.get('label.taskRunning'), runningTasks));
                } else {
                	Ext.get(el).update("");
                }
            }
        });
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

    	var editProfileView = {
        	xtype : 'container',
        	items : [Ext.create('sitools.public.userProfile.editProfile', {
                identifier : userLogin,
                url : loadUrl.get('APP_URL') + '/editProfile/' + userLogin
            })]
        };
    	
    	var contentPanel = grid.up('userPersonalWindow').contentPanel;
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
        
    	var taskView = {
        	xtype : 'container',
        	items : [Ext.create('sitools.user.view.header.userProfile.TaskView')]
        };
    	
    	var contentPanel = grid.up('userPersonalWindow').contentPanel;
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
        var jsObj = sitools.user.component.entete.userProfile.diskSpace;
        var windowConfig = {
            title : i18n.get('label.userSpace'),
            saveToolbar : false, 
            iconCls : "diskSpace"
        };
        SitoolsDesk.addDesktopWindow(windowConfig, {}, jsObj, true);
        this.destroy();
    }, 
    
    /**
     * Open a window in the desktop with the sitools.user.component.entete.userProfile.diskSpace object. 
     * @param {Ext.DataView} dataView the clicked Dataview
     * @param {numeric} index the index of the clicked node
     * @param {Html Element} node the clicked html element 
     * @param {Ext.event} e The click event
     */
    showOrders : function (grid, record, index) {

    	var orderView = {
        	xtype : 'container',
        	items : [Ext.create('sitools.user.view.header.userProfile.OrderView')]
        };
    	
    	var contentPanel = grid.up('userPersonalWindow').contentPanel;
    	contentPanel.removeAll();
    	contentPanel.add(orderView);
    	
    },
    
    actionItemClick : function (grid, record, item, index, e) {
          var data = grid.getSelectionModel().getSelection()[0].data;   
          eval("this." + data.action).call(this, grid, record, index);
	 }
});