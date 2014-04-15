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
/*global Ext, sitools, i18n, SitoolsDesk, alertFailure, window, loadUrl */

Ext.namespace('sitools.user.component.dataviews');

/**
 * A simple window that displays result of Resource Tasks.
 * @class sitools.user.component.dataviews.goToTaskPanel
 * @extends Ext.Panel
 */
sitools.user.component.dataviews.goToTaskPanel = Ext.extend(Ext.Panel, {
//sitools.user.component.livegrid.goToTaskPanel = Ext.extend(Ext.Window, {
//    modal : true,
    width : "500", 
    buttonAlign : 'left',
    layout : 'fit',
    initComponent : function () {
        
        
        
        this.mainPanel = this.createNewFormComponent(this.task);
 
        this.buttons = ["->",  {
            text : i18n.get('label.goToTask'),
            scope : this,
            handler : this.goToTask
        }, {
            text : i18n.get('label.close'),
            scope : this,
            handler : function () {
                this.ownerCt.close();
            }
        } ];
        
        
        this.items = [this.mainPanel];
        
        sitools.user.component.dataviews.goToTaskPanel.superclass.initComponent.call(this);

    },
    
    refreshTask : function () {
//        var form = this.mainPanel;
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
                this.doLayout();
            },
            failure : alertFailure
        });
    },
    
    createNewFormComponent : function (task) {
      
        var html = Ext.String.format(i18n.get("label.taskLaunched"), task.status);
        html += Ext.String.format("<a href='#'>{0}</a><br>", i18n.get("label.detail"));
        
        if (!Ext.isEmpty(task.urlResult)) {
			html += "<br>" + Ext.String.format(i18n.get("label.taskResult"), task.urlResult);	
			html += Ext.String.format("<a href='#'>{0}</a><br>", i18n.get("label.result"));
        }
        else {
			html += "<br>" + i18n.get("label.refreshTaskWindow");	
			html += Ext.String.format("<a href='#'>{0}</a><br>", i18n.get("label.refresh"));
        }
        
        var panel = new Ext.Panel({
			padding: 5,
			layout : "fit", 
			html : html, 
			listeners : {
				scope : this, 
				afterrender : function (panel) {
					panel.getEl().child('a').on("click", function () {
						this.showTaskDetail(task);
					}, this);
					var resultOrRefreshLink = panel.getEl().child('a').next('a');
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
        
//        var formPanel = new Ext.form.FormPanel({
//            title : i18n.get("label.taskDetails"),
//            padding: 5,
//            items : [ {
//                name : 'statusUrl',
//                xtype : 'textfield',
//                value : task.statusUrl,
//                hidden : true
//            }, {
//                name : 'status',
//                fieldLabel : i18n.get('label.status'),
//                anchor : "100%",
//                xtype : 'textfield',
//                value : task.status
//            }, {
//                name : 'id',
//                fieldLabel : i18n.get('label.id'),
//                anchor : "100%",
//                xtype : 'textfield',
//                value : task.id
//            }, {
//                itemValue : task.statusUrl,
//                fieldLabel : i18n.get('label.url'),
//                xtype : 'box',
//                html : "<a href='#'> " + task.statusUrl + "</a>"
//                ,
//                listeners : {
//                    scope : this,
//                    render : function (cmp) {
//                        cmp.getEl().on('click', function () {
//                            var jsObj = sitools.user.modules.userSpaceDependencies.svaTasksDetails;
//                            var componentCfg = {
//                                sva : task    
//                            };
//                            var windowConfig = {
//                                id : "taskStatusDetails", 
//                                title : i18n.get("label.taskDetails") + ":" + task.id
//                            };
//                            SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
//                        });
//                    }
//                }
//            }
//            ]
//        });
//        
//        if (!Ext.isEmpty(task.urlResult)) {
//            var item = new Ext.BoxComponent({
//                itemValue : task.urlResult,
//                fieldLabel : i18n.get('label.result'),
//                html : "<a href='#'> " + task.urlResult + "</a>",
//                listeners : {
//                    scope : this,
//                    render : function (cmp) {
//                        cmp.getEl().on('click', function () {
//                            var orderUrl = loadUrl.get('APP_URL') + loadUrl.get('APP_ORDERS_USER_URL');
//                            if (cmp.itemValue.indexOf(orderUrl) != -1) {
//                                this._showOrderDetails(cmp.itemValue);
//                            } else if (cmp.itemValue.indexOf("/records") != -1) {
//                                this._showDatasetDetails(cmp.itemValue);
//                            } 
//                            else {
//                                window.open(cmp.itemValue);
//                            }
//                        }, this);
//                    }
//                }
//            });
//            formPanel.add(item);
//        }
//        
//        return formPanel;
        
    },
    
    /**
     * Handler of the button goToTask. 
     * Open the home Module Window with the taskPanel opened.
     */
    goToTask : function () {
        this.ownerCt.close();
		var jsObj = sitools.user.component.entete.userProfile.tasks;
        var windowConfig = {
            title : i18n.get('label.Tasks'),
            saveToolbar : false, 
            iconCls : "tasks"
        };
        SitoolsDesk.addDesktopWindow(windowConfig, {}, jsObj, true);

    },
    /**
     * Open a sitools.user.component.entete.userProfile.orderProp window. 
     * @param {String} url the Url to request the task. 
     */
    _showOrderDetails : function (url) {
        Ext.Ajax.request({
            url : url,
            method : 'GET',
            scope : this,
            success : function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), data.message);
                    return false;
                }
                var rec = new Ext.data.Record(data.order);
                var jsObj = sitools.user.component.entete.userProfile.orderProp;
                var componentCfg = {
                    action : 'detail',
                    orderRec : rec
                };
                var title = i18n.get('label.details') + " : ";
                title += rec.data.userId;
                title += " " + i18n.get('label.the');
                title += " " + rec.data.dateOrder;

                var windowConfig = {
                    id : "showDataDetailId", 
                    title : title,  
                    specificType : "dataDetail", 
                    iconCls : "dataDetail"
                };
                SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
            },
            failure : alertFailure
        });
        
    },
    /**
     * Only in NoSql, open a dataset view 
     * @param {} url
     */
    _showDatasetDetails : function (url) {
        var urlDataset = url.substring(0, url.indexOf("/records"));
        Ext.Ajax.request({
            url : urlDataset,
            method : 'GET',
            scope : this,
            success : function (ret) {
                var data = Ext.decode(ret.responseText);
                if (!data.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), data.message);
                    return false;
                }
                var dataset = new Ext.data.Record(data.dataset).data;
                
                var windowConfig = {
                        title : i18n.get('label.dataTitle') + " : " + dataset.name,
                        datasetName : dataset.name, 
                        datasetDescription : dataset.description,
                        type : "data", 
                        saveToolbar : true, 
                        toolbarItems : [], 
                        iconCls : "dataDetail"
                    };
                
                //open the dataView according to the dataset Configuration.
                var javascriptObject = eval(dataset.datasetView.jsObject);
                //add the toolbarItems configuration
                Ext.apply(windowConfig, {
                    id : "data" + dataset.datasetId
                });
                var componentCfg = {
                    dataUrl : dataset.sitoolsAttachementForUsers,
                    datasetId : dataset.id,
                    datasetCm : dataset.columnModel, 
                    datasetName : dataset.name,
                    dictionaryMappings : dataset.dictionaryMappings, 
	                datasetViewConfig : dataset.datasetViewConfig, 
                    preferencesPath : "/" + dataset.name, 
                    preferencesFileName : "datasetView"
                };
                
                SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);
                
            },
            failure : alertFailure
        });
        
    }, 
    /**
     * Opens a sitools.user.modules.userSpaceDependencies.svaTasksDetails window to see the task Details. 
     * @param {} task
     */
    showTaskDetail : function (task) {
	    var jsObj = sitools.user.component.entete.userProfile.tasksDetails;
	    var componentCfg = {
	        sva : task    
	    };
	    var windowConfig = {
	        id : "taskStatusDetails", 
	        title : i18n.get("label.taskDetails") + ":" + task.id, 
	        iconCls : "dataDetail"
	    };
	    SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
	}, 
	/**
	 * parse the task.urlResult to see if this is an Specialized resource (noSQl or Order). 
	 * If not, open a new Window to get the result of the resource. 
	 * @param {} task
	 */
	showTaskResults : function (task) {
		var orderUrl = loadUrl.get('APP_URL') + loadUrl.get('APP_ORDERS_USER_URL');
        if (task.urlResult.indexOf(orderUrl) != -1) {
            this._showOrderDetails(task.urlResult);
        } else if (task.urlResult.indexOf("/records") != -1) {
            this._showDatasetDetails(task.urlResult);
        } 
        else {
            window.open(task.urlResult);
        }
	}, 
    /**
     * Method called when trying to show this component with fixed navigation
     * 
     * @param {sitools.user.component.viewDataDetail} me the dataDetail view
     * @param {} config config options
     * @returns
     */
    showMeInFixedNav : function (me, config) {
        Ext.apply(config.windowSettings, {
            width : config.windowSettings.winWidth || DEFAULT_WIN_WIDTH,
            height : config.windowSettings.winHeight || DEFAULT_WIN_HEIGHT
        });
        SitoolsDesk.openModalWindow(me, config);
    }, 
    /**
     * Method called when trying to show this component with Desktop navigation
     * 
     * @param {sitools.user.component.viewDataDetail} me the dataDetail view
     * @param {} config config options
     * @returns
     */
    showMeInDesktopNav : function (me, config) {
        Ext.apply(config.windowSettings, {
            width : config.windowSettings.winWidth || DEFAULT_WIN_WIDTH,
            height : config.windowSettings.winHeight || DEFAULT_WIN_HEIGHT
        });
        SitoolsDesk.openModalWindow(me, config);
    }
    

});

