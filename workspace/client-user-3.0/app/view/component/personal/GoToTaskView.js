/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, i18n, SitoolsDesk, alertFailure, window, loadUrl */

Ext.namespace('sitools.user.view.component.personal');

/**
 * A simple window that displays result of Resource Tasks.
 * @class sitools.user.view.component.personal.GoToTaskView
 * @extends Ext.panel.Panel
 */
Ext.define('sitools.user.view.component.personal.GoToTaskView', {
	extend : 'Ext.panel.Panel',
	
	border : false,
	layout : {
		type : 'vbox',
		align : 'stretch'
	},
	
    initComponent : function () {
    	
        this.mainPanel = this.createNewFormComponent(this.task);

        this.bbar = ["->",  {
            text : i18n.get('label.goToTask'),
            scope : this,
            handler : this.goToTask
        }, {
            text : i18n.get('label.close'),
            scope : this,
            handler : function () {
                this.up().close();
            }
        } ];
        
        this.items = [this.mainPanel];
        
        this.callParent(arguments);
    },
    
    refreshTask : function () {
        var url = this.task.statusUrl;
        Ext.Ajax.request({
            url : url,
            method : "GET",
            scope : this,
            success : function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), data.message);
                    return false;
                }
                this.task = data.TaskModel;
                this.mainPanel = this.createNewFormComponent(this.task);
                this.removeAll();
                this.add(this.mainPanel);
            },
            failure : alertFailure
        });
    },
    
    createNewFormComponent : function (task) {
      
        var html = Ext.String.format(i18n.get("label.taskLaunched"), task.status);

        if (Ext.isEmpty(task.urlResult)) {
			html += "<br>" + i18n.get("label.refreshTaskWindow");
			html += Ext.String.format("<a href='#'>{0}</a><br>", i18n.get("label.refresh"));

			//html += "<br>" + Ext.String.format(i18n.get("label.taskResult"), task.urlResult);
			//html += Ext.String.format("<a href='#'>{0}</a><br>", i18n.get("label.result"));
        } else {
            html += Ext.String.format("<a href='#'>{0}</a><br>", i18n.get("label.detail"));
        }

        var panel = Ext.create('Ext.panel.Panel', {
			padding: 15,
			height : 100,
			border : false,
			html : html, 
			listeners : {
				scope : this, 
				boxready : function (panel) {
					panel.getEl().down('a').on("click", function () {
						//this.showTaskDetail(task);
						this.showTaskResults(task);
					}, this);
					
					var resultOrRefreshLink = panel.getEl().down('a');
					if (!Ext.isEmpty(task.urlResult)) {
						resultOrRefreshLink.on("click", function () {
							this.showTaskResults(task);
						}, this);
					}
					else {
						resultOrRefreshLink.on("click", function () {
							this.refreshTask();
						}, this);
					}
				}
			}
        });
        return panel;
    },
    
    /**
     * Handler of the button goToTask. 
     * Open the home Module Window with the taskPanel opened.
     */
    goToTask : function () {
        this.up().close();
        
        var sitoolsController = Desktop.getApplication().getController('core.SitoolsController');
        var userPersonalComponent = sitoolsController.openComponent('sitools.user.component.personal.UserPersonalComponent', {
            action : 'tasks'
        }, {});
    },

    _showOrderDetails: function (url, view) {

        if(this.down("orderDetailView")){
            return;
        }

        Ext.Ajax.request({
            url: url,
            method: 'GET',
            scope: this,
            success: function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), data.message);
                    return false;
                }

                var order = Ext.create("sitools.user.model.OrderModel", data.order);

                var orderDetailView = Ext.create('sitools.user.view.component.personal.OrderDetailView', {
                    action : 'detail',
                    orderRec : order
                });

                this.addBottomPanel(orderDetailView);

            },
            failure: alertFailure
        });
    },
    
    /**
     * Opens a sitools.user.modules.userSpaceDependencies.svaTasksDetails window to see the task Details. 
     * @param {} task
     */
    showTaskDetail : function (task) {
    	
    	if (this.down('taskDetailView')) {
    		return;
    	}
    	
    	var taskDetailView = Ext.create('sitools.user.view.component.personal.TaskDetailView', {
    		task : task
    	});

        this.addBottomPanel(taskDetailView);
    	
	},

    addBottomPanel : function (view) {
        var infoPanel;
        if (this.down("panel#infoPanel")) {
            infoPanel = this.down("panel#infoPanel");
        } else {
            infoPanel = Ext.create("Ext.panel.Panel", {
                itemId: 'infoPanel',
                flex: 1,
                layout: 'fit',
                border: false,
                bodyBorder: false
            });
            this.add(infoPanel);
        }

        infoPanel.removeAll();
        infoPanel.add(view);

    },
	
	/**
	 * parse the task.urlResult to see if this is an Specialized resource (noSQl or Order). 
	 * If not, open a new Window to get the result of the resource. 
	 * @param {} task
	 */
	showTaskResults : function (task) {
		var orderUrl = loadUrl.get('APP_URL') + loadUrl.get('APP_ORDERS_USER_URL');

        if (Ext.isEmpty(task.urlResult)) {
            return;
        }

        if (task.urlResult.indexOf(orderUrl) != -1) {
            this._showOrderDetails(task.urlResult);
        } /*else if (task.urlResult.indexOf("/records") != -1) {
            this._showDatasetDetails(task.urlResult);
        }*/
        else {
            window.open(task.urlResult);
        }
	}
});

