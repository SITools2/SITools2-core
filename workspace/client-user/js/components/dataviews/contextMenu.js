/***************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, i18n, extColModelToStorage, projectId, userStorage, window, extColModelToSrv, userLogin, alertFailure, DEFAULT_LIVEGRID_BUFFER_SIZE, projectGlobal, SitoolsDesk, DEFAULT_ORDER_FOLDER, DEFAULT_PREFERENCES_FOLDER, loadUrl */

/*
 * @include "../../viewDataDetail/viewDataDetail.js"
 * @include "resourcePluginParamsPanel.js"
 * @include "goToTaskPanel.js"
 * @include "../../../env.js"
 * @include "../../../sitoolsProject.js"
 * @include "../../../def.js"
 */
Ext.namespace('sitools.user.component.dataviews');

/**
 * Define the contextMenu and the toolbar menu for the Sitools data Views.
 * 
 * @class sitools.user.component.dataviews.ctxMenu
 * @extends Ext.menu.Menu
 * @cfg {} grid The object that calls ContextMenu 
 * @cfg {Array} selections The selected Records
 * @cfg {} event The Html Event
 * @cfg {string} dataUrl The url Attachement of the dataset
 * @cfg {string} datasetId Dataset Id
 * @cfg {string} datasetName Dataset Name
 * @cfg {string} origin A string representing the caller of the contextMenu
 * @cfg {string} urlDetail the Url to request the Detailed record.
 * @requires sitools.user.component.viewDataDetail
 * @requires sitools.user.component.dataviews.resourcePluginParamsPanel
 * @requires sitools.user.component.dataviews.goToTaskPanel
 */
sitools.user.component.dataviews.ctxMenu = Ext.extend(Ext.menu.Menu, {
//sitools.user.component.livegrid.ctxMenu = Ext.extend(Ext.menu.Menu, {
    /**
     * the url to request the API records
     * @type {string}
     */
    dataUrl : null,
    /**
     * The caller of the context Menu
     * @type {Ext.grid.GridPanel}
     */
    grid : null,
    /**
     * The dataset Name
     * @type {string}
     */
    datasetName : null,
    /**
     * The dataset Id
     * @type {string}
     */
    datasetId : null,
    /**
     * The string name of the object that call contextMenu
     * @type {string}
     */
    origin : null,
    /**
     * The rightClick event
     * @type 
     */
    event : null,
    /**
     * The url to request a single record
     * @type {string}
     */
    urlDetail : null, 

    constructor : function (config) {
        this.dataUrl = config.dataUrl;
        this.grid = config.grid;
        this.datasetId = config.datasetId;
        this.datasetName = config.datasetName;
        this.origin = config.origin;
        this.event = config.event;
        this.urlDetail = config.urlDetail;
        this.config = config;
        
        if (this.origin == "sitools.user.modules.projectServices") {
            this.selections = this.grid.getSelectionModel().getSelected();
        }

        sitools.user.component.dataviews.ctxMenu.superclass.constructor.call(this, config);

        /* RESOURCE PART */
        if (!this.resourceStore) {
            this.resourceStore = new Ext.data.JsonStore({
                root : "data",
                fields : [ {
                    name : "parameters"
                }, {
                    name : "name",
                    type : "string"
                }, {
                    name : "description",
                    type : "string"
                }, {
                    name : "dataSetSelection",
                    type : "string"
                }, {
                    name : "behavior",
                    type : "string"
                } ],
                url : this.dataUrl + "/services",
                listeners : {
                    load : function (store, records, options) {
                        Ext.each(records, function (rec) {
                            if (rec.get("dataSetSelection") === "NONE") {
                                store.remove(rec);
                            }
                        }, this);
                    }
                }
            });
        }
        this.resourceStore.on('beforeload', this.onBeforeLoad, this);
        this.resourceStore.on('load', this.onLoad, this);
        /* END OF RESOURCE PART */

        this.on('show', this.onMenuLoad, this);

    },
	/**
	 * Event on show ContextMenu : Load the resources store
	 */
    onMenuLoad : function () {
        this.resourceStore.load();
    },
	/**
	 * Define the beforeload event of the resources and resource store :
	 * Call updateMenuItems.
	 */
    onBeforeLoad : function () {
        /* RESOURCE PART */
        this.resourceStore.baseParams = this.baseParams;
        /* END OF RESOURCE PART */

        this.updateMenuItems(false);
    },

    /**
     * Define the beforeload event of the resources store :
     * call updateMenuItems
     * @param {Ext.Data.Store} store
     * @param [] records Array of {Ext.data.Record}
     */
    onLoad : function (store, records) {
        this.updateMenuItems(true, records);
    },
	/**
	 * Build the contextMenu : add the items entry for resources
	 * @param {boolean} loadedState
	 * @param []records
	 */
    updateMenuItems : function (loadedState, records) {
        this.selections = this.grid.getSelections();

        this.removeAll();
        this.el.sync();

        this.menuResources = new Ext.menu.Menu();
        this.resourcePresent = false;

        if (loadedState) {
            this.resourceStore.each(function (resRec) {
                var resource = resRec.data;
                this.resourcePresent = true;
                // get resource caracteristics from the parameters
                var parameters = resource.parameters;
                var url, runTypeUserInput, icon, methods;
                parameters.each(function (param) {
                    switch (param.name) {
                    case "methods":
                        methods = param.value;
                        break;
                    case "runTypeUserInput":
                        runTypeUserInput = param.value;
                        break;
                    case "url":
                        url = this.dataUrl + param.value;
                        break;
                    case "image":
                        icon = param.value;
                        break;
                    }
                }, this);

                this.menuResources.add({
                    text : resource.name,
                    cls : 'x-btn-text-icon',
                    icon : icon,
                    listeners : {
                        scope : this,
                        click : function () {
                            this.resourceClick(resource, url, methods, runTypeUserInput, parameters);
                        }
                    }
                });

            }, this);

        } else {
            this.menuResources.add('<span class="loading-indicator">' + this.loadingText + '</span>');
        }


        this.add({
            text : i18n.get('label.addSelection'),
            listeners : {
                scope : this,
                click : function (button, e) {
                    e.stopEvent();
                    this.orderAction = "add";
                    this._onOrder();
                }
            },
            icon : loadUrl.get('APP_URL') + "/common/res/images/icons/add_selection.png"
        });

        this.add({
            text : i18n.get('label.export.all.CSV'),
            listeners : {
                scope : this,
                click : function () {
                    this._onExportCsv();
                }
            },
            icon : loadUrl.get('APP_URL') + "/common/res/images/icons/csv.png"
        });

        if (this.origin === "Ext.ux.livegrid" || this.origin === "sitools.user.component.dataviews.tplView.TplView") {
            this.add({
                text : i18n.get('label.downloadFile'),
                listeners : {
                    scope : this,
                    click : function () {
                        this.orderAction = "download";
                        this._onOrder();
                    }
                },
                icon : loadUrl.get('APP_URL') + "/common/res/images/icons/add_request.png"
            });
        }

        this.add({
            text : i18n.get('label.details'),
            listeners : {
                scope : this,
                click : function (menuItem, e) {
                    e.stopEvent();
                    e.stopPropagation();
                    this._onDetails();
                }
            },
            icon : loadUrl.get('APP_URL') + "/common/res/images/icons/toolbar_details.png"
        });
        /* RESOURCE PART */
        if (this.resourcePresent) {
            this.add({
                text : i18n.get('label.resources'),
                menu : this.menuResources,
                icon : loadUrl.get('APP_URL') + "/common/res/images/icons/tree_datasets_resource.png"
            });
        }
        /* END OF RESOURCE PART */

    },

    /**
     * Called when user click on option "add to my selections" and "add request"
     * Build a Window to specify a name for the future file, 
     * check that there is no records in the pending selections.
     */
    _onOrder : function () {
        if (this.orderAction === "add" && Ext.isEmpty(this.selections)) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
            return;
        }
        if (this.origin === "Ext.ux.livegrid") {
            if (this.grid.selModel.getPendingSelections().length > 0) {
                Ext.Msg.alert(i18n.get('label.warning'), i18n.get("livegrid.selection.invalide.txt"));
                return;
            }
        }
        var title = "";
        if (this.orderAction === "add") {
            title = i18n.get('label.addSelection');
        } else {
            title = i18n.get('label.addRequest');
        }
        var winProperty = new Ext.Window({
            title : title,
            width : 300,
            modal : true,
            items : [ {
                xtype : 'form',
                labelWidth : 75,
                items : [ {
                    xtype : 'textfield',
                    fieldLabel : i18n.get('label.name'),
                    name : 'orderName',
                    anchor : '100%'
                } ],
                buttons : [ {
                    text : i18n.get('label.ok'),
                    scope : this,
                    handler : function () {
                        var orderName = winProperty.findByType('form')[0].getForm().getFieldValues().orderName;
                        if (this.orderAction === "add") {
                            this._addSelection(this.selections, this.grid, this.datasetId, orderName);
                        } else {
                            this._onDownload(this.datasetId, this.grid, orderName);
                        }
                        winProperty.close();
                    }
                }, {
                    text : i18n.get('label.cancel'),
                    handler : function () {
                        winProperty.close();
                    }
                } ]
            } ]
        });
        winProperty.show();
    },
    /**
     * Create an entry in the user storage with all the selected records.
     * @param {Array} selections An array of selected {Ext.data.Record} records 
     * @param {Ext.grid.GridPanel} grid the grid  
     * @param {string} datasetId
     * @param {string} orderName the name of the future file.
     */
    _addSelection : function (selections, grid, datasetId, orderName) {
        var primaryKey = "";
        var rec = selections[0];
        Ext.each(rec.fields.items, function (field) {
            if (field.primaryKey) {
                primaryKey = field.name;
            }
        }, rec);
        if (Ext.isEmpty(primaryKey) || Ext.isEmpty(rec.get(primaryKey))) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noPrimaryKey'));
            return;
        }
        var putObject = {};
        putObject.orderRecord = {};

        putObject.orderRecord.records = [];
        var colModel = extColModelToStorage(grid.getColumnModel());
        Ext.each(selections, function (rec) {
            var data = {};
            Ext.each(colModel, function (column) {
                if (!column.hidden || column.primaryKey) {
                    data[column.columnAlias] = rec.get(column.columnAlias);
                }
            });
            putObject.orderRecord.records.push(data);
        });
        putObject.orderRecord.colModel = colModel;
        putObject.orderRecord.datasetId = datasetId;
        putObject.orderRecord.projectId = projectId;
        putObject.orderRecord.dataUrl = this.dataUrl;
        putObject.orderRecord.datasetName = this.datasetName;

        userStorage.set(orderName + ".json", "/" + DEFAULT_ORDER_FOLDER + "/records", putObject);
    },
    /**
     * Create an external link to get the CSV file generated.
     * @return {Boolean}
     */
    _onExportCsv : function () {
        var csvUrl = this.selections;
        var pathUrl = window.location.protocol + "//" + window.location.host + this.dataUrl + "/records";
        var request = "";
        try {
			request = this.grid.getRequestParam();
        }
        catch (err) {
			Ext.Msg.alert(i18n.get('label.error'), String.format(i18n.get('label.notImplementedService'), err));
			return false;
        }

        pathUrl += "?media=csv" + request + "&limit=" + this.grid.store.totalLength;
        window.open(pathUrl);
    },
    /**
     * Create an entry in the userSpace with the request of the current grid.
     * @param {string} datasetId
     * @param {Ext.grid.GridPanel} grid The current grid.
     * @param {string} orderName the future file name
     */
    _onDownload : function (datasetId, grid, orderName) {
        var putObject = {};
        putObject.orderRequest = {};
        putObject.orderRequest.datasetId = datasetId;
        putObject.orderRequest.datasetUrl = grid.dataUrl;

        var filters = grid.getFilters();
        if (!Ext.isEmpty(filters)) {
            putObject.orderRequest.filters = filters.getFilterData(filters);
        }

        var storeSort = grid.getStore().getSortState();
        if (!Ext.isEmpty(storeSort)) {
            putObject.orderRequest.sort = storeSort;
        }

        // if the filter config isn't empty ( download from dataview ) we store
        // it
        var filtersCfg = grid.getStore().filtersCfg;
        if (!Ext.isEmpty(filtersCfg)) {
            putObject.orderRequest.filtersCfg = filtersCfg;
        }

        var colModel = Ext.util.JSON.encode(extColModelToStorage(grid.getColumnModel()));
        putObject.orderRequest.colModel = colModel;
        putObject.orderRequest.datasetId = datasetId;
        putObject.orderRequest.projectId = projectId;
        putObject.orderRequest.formParams = grid.getStore().getFormParams();
        userStorage.set(orderName + ".json", "/" + DEFAULT_ORDER_FOLDER + "/request", putObject);
    },
    /**
     * Show a {sitools.user.component.viewDataDetail} window. 
     */
    _onDetails : function () {
        var rec = this.grid.getSelections()[0];
        if (Ext.isEmpty(rec)) {
            Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
            return;
        }
        var componentCfg = {
            baseUrl : this.urlDetail + "/records/",
            grid : this.grid,
            fromWhere : this.origin,
            datasetId : this.datasetId,
            selections : this.grid.getSelections(),
            datasetName : this.datasetName,
            datasetUrl : this.urlDetail, 
            preferencesPath : "/" + this.datasetName, 
            preferencesFileName : "dataDetails"
        };
        var jsObj = sitools.user.component.viewDataDetail;
        var windowConfig = {
            id : "dataDetail" + this.datasetId,
            title : i18n.get('label.viewDataDetail') + " : " + this.datasetName,
            iconCls : "dataDetail", 
            datasetName : this.datasetName,
            saveToolbar : true,
            type : "dataDetail",
            toolbarItems : [ {
            	iconCls : 'arrow-back',
                handler : function () {
                    this.ownerCt.ownerCt.items.items[0].goPrevious();
                }
            }, {
            	iconCls : 'arrow-next',
                handler : function () {
                    this.ownerCt.ownerCt.items.items[0].goNext();
                }
            } ]
        };
        SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj, true);
    },
    /**
     * Get the number of selected records
     * @return {number}
     */
    getNbRowsSelected : function () {
        if (this.grid.getNbRowsSelected()) {
			return this.grid.getNbRowsSelected();
//			return this.grid.getSelectionModel().getAllSelections(false).length;
        }
        else {
			return null;
        }
    },
    /**
     * Get the number of grid records
     * @return {number}
     */
    getNbGridRecords : function () {
		return this.grid.getStore().totalLength;
    }, 
    /**
     * Sets the grid attribute
     * @param {} cmp
     */
    setGrid : function (cmp) {
        this.grid = cmp;
    },
    /**
     * sets the selections attribute
     * @param {} selections
     */
    setSelections : function (selections) {
        this.selections = selections;
    },
	/**
	 * Method called when a resource item is clicked.
	 * 
	 * @param {} resource The resource description
	 * @param {string} url the url to call to execute query
	 * @param {Array} methods A list of permissed methods. 
	 * @param {} runType The runType of the resource.
	 * @param {Array} parameters An array of parameters.
	 */
    resourceClick : function (resource, url, methods, runType, parameters) {
        this.checkResourceParameters(resource, url, methods, runType, parameters);
    },
    
    /**
     * handle the click on a Resource after the parameters have been checked
     * 
     * @param {} resource The resource description
     * @param {string} url the url to call to execute query
     * @param {Array} methods A list of permissed methods. 
     * @param {} runType The runType of the resource.
     * @param {Array} parameters An array of parameters.
     */
    handleResourceClick : function (resource, url, methods, runType, parameters) {
        //check that the number of records allowed is not reached
        var showParameterBox = false;
        var params = [];
        Ext.each(parameters, function (parameter) {
            if (parameter.type === "PARAMETER_USER_INPUT" && parameter.userUpdatable) {
                showParameterBox = true;
            }
            params[parameter.name] = parameter.value;   
        });
        if (methods.split("|") && methods.split("|").length > 1) {
            showParameterBox = true;
        }
        var resourceWindow;
        if (showParameterBox && this.origin !== "sitools.user.modules.projectServices") {
            var windowConfig = {
                title : i18n.get("label.resourceReqParam"), 
                iconCls : "datasetRessource"
            };
            var jsObj = sitools.user.component.dataviews.resourcePluginParamsPanel;
            var componentCfg = {
                resource : resource,
                url : url,
                methods : methods,
                runType : runType,
                parameters : parameters,
                contextMenu : this,
                withSelection : (this.getNbRowsSelected() !== 0)
            };
            SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
        }
        else if (showParameterBox) {
            var windowConfig = {
                title : i18n.get("label.resourceReqParam"), 
                iconCls : "datasetRessource"
            };
            var jsObj = sitools.user.component.dataviews.resourcePluginParamsPanel;
            var componentCfg = {
                resource : resource,
                url : url,
                methods : methods,
                runType : runType,
                parameters : parameters,
                contextMenu : this,
                withSelection : false
            };
            SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
        }
        else {
            this.onResourceCallClick(resource, url, methods, runType, null, params);
        }
    },

    /**
     * the action used when click on a Resource menu. Request Resource tasks to the URL
     * datasetUrlAttachment + "/sva/" + sva.id + "/tasks If there is no selected
     * record : Build the request with the actual params of the grid : sort,
     * filters, formParams, ColModel else Build the request with a list of
     * records primaryKeyValue by using the formRequest API LISTBOXMULTIPLE|
     * 
     * @param {} resource the resource object.  
     * @param url :
     *            the Url to request the data
     * @param method :
     *            the method used to request the data
     * @param grid :
     *            the liveGrid
     * @param method :
     *            the method for the resource : POST or GET
     * @param userSyncChoice :
     *            the user choice : "sync" || "async"
     * @param limit :
     *            the request limit
     * @param userParameters :
     *            a list of key/value object
     * 
     */
    onResourceCallClick : function (resource, url, method, userSyncChoice, limit, userParameters) {
        if ((method === "POST" || method === "PUT" || method === "DELETE") && userSyncChoice === "TASK_RUN_SYNC") {
			Ext.Msg.alert(i18n.get('label.error'), String.format(i18n.get("error.invalidMethodOrSyncRessourceCall"), method, userSyncChoice));
			return;
        }

        var request = "";
        if (this.origin !== "sitools.user.modules.projectServices") {
			try {
                request = this.grid.getRequestParam();
			}
			catch (err) {
				Ext.Msg.alert(i18n.get('label.error'), String.format(i18n.get('label.notImplementedService'), err));
				return false;
			}
        }

        url += "?1=1" + request;
        if (!Ext.isEmpty(limit)) {
            url += "&limit=" + limit;
        }
        if (! Ext.isEmpty(userParameters)) {
			Ext.iterate(userParameters, function (key, value) {
				url += "&" + key + "=" + value; 
			});
        }

        // If Get => the Resource MUST be synchrone and then send a representation
        if (method === "GET") {
            switch (resource.behavior) {
            case "DISPLAY_IN_NEW_TAB" : 
				window.open(url);
	            Ext.getBody().unmask();
	            break;
            case "DISPLAY_IN_DESKTOP" :
                var windowConfig = {
                    title : i18n.get('downloadedResource'), 
                    iconCls : "datasetRessource"
                };
                var jsObj = Ext.ux.ManagedIFrame.Panel;
                var componentCfg = {
                    layout : 'fit',
					defaultSrc: url, 
					autoScroll : true
                };
                Ext.Ajax.request({
					method : "HEAD", 
					url : url, 
					success : function (ret) {
						var headerFile = "";
						try {
							headerFile = ret.getResponseHeader("Content-Type").split(";")[0].split("/")[0];
						}
						catch (err) {
							headerFile = "";	
						}
						var contentDisp = ret.getResponseHeader("Content-Disposition");
						
						if (headerFile === "text" && Ext.isEmpty(contentDisp)) {
							SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
						}
						else {
							var iFrame = Ext.getCmp("tempMifDownload");
							if (Ext.isEmpty(iFrame)) {
								iFrame	= new Ext.ux.ManagedIFrame.Panel({
				                    layout : 'fit',
									defaultSrc: url, 
									autoScroll : true, 
									renderTo : Ext.getBody(), 
									id : "tempMifDownload"
				                });
							}
							else {
								iFrame.setSrc(url);	
							}
						}
					}, 
					failure : alertFailure
                });
                
				break;
            case "DOWNLOAD" :
				//générer un panel caché
				var iFrame = Ext.getCmp("tempMifDownload");
				if (Ext.isEmpty(iFrame)) {
					iFrame	= new Ext.ux.ManagedIFrame.Panel({
	                    layout : 'fit',
						defaultSrc: url, 
						autoScroll : true, 
						renderTo : Ext.getBody(), 
						id : "tempMifDownload"
	                });
				}
				else {
					iFrame.setSrc(url);	
				}
				break;
            }
            return;
        } else {
			this._executeRequestForResource(url, method);
        }
    },
	/**
	 * Execute the resource.
	 * @param {string} url the url to request
	 * @param {string} method The method (GET, POST, PUT, DELETE)
	 * @param {} postObject The object that will be passed with the request.
	 */
    _executeRequestForResource : function (url, method, postObject) {
        var config = {
            method : method,
            url : url,
            scope : this,
            success : function (response, opts) {
                // try {
                var json = Ext.decode(response.responseText);
                if (!json.success) {
                    Ext.Msg.show({
						title : i18n.get('label.error'),
						msg : json.message,
						width : 300,
						buttons : Ext.MessageBox.OK
					});
                    return;
                }
                var task = json.TaskModel;
                if (!Ext.isEmpty(task.urlResult)) {
                    window.open(task.urlResult);
                } else {
                    var componentCfg = {
		                task : task
		            };
		            var jsObj = sitools.user.component.dataviews.goToTaskPanel;
		
		            var windowConfig = {
		                title : i18n.get('label.info'),
		                saveToolbar : false, 
		                iconCls : "datasetRessource"	                
		            };
		            SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj, true);
                    
                }
                

            },
            failure : function (response, opts) {
                Ext.Msg.show({
						title : i18n.get('label.error'),
						msg : response.responseText,
						width : 300,
						buttons : Ext.MessageBox.OK
					});
            },
            callback : function () {
                if (Ext.getBody().isMasked()) {
                    Ext.getBody().unmask();
                }
            }

        };
        if (!Ext.isEmpty(postObject)) {
            Ext.apply(config, postObject);
        }
        Ext.Ajax.request(config);
    },
    /**
	 * check that the request is compatible with the resource parameters.
     * If the parameters are ok, call the method handleResourceClick to handle the resource call
	 * @param {} resource The resource description
     * @param {string} url the url to call to execute query
     * @param {Array} methods A list of permissed methods. 
     * @param {} runType The runType of the resource.
     * @param {Array} parameters An array of parameters.
	 * 
	 */
	checkResourceParameters : function (resource, url, methods, runType, parameters) {
        var ok = true;
        //in the case of a OrderResource, let's check that the number of records is not superior to too_many_selected_threshold => stop the resource execution
        var maxThreshold = this.getParameterFromName(parameters, "too_many_selected_threshold");
        var nbRows;
		if (!Ext.isEmpty(maxThreshold)) {
            var maxThresholdTextParam, maxTresholdText;
            //get the number of rows either from a selection or all the rows
            nbRows = (Ext.isEmpty(this.getNbRowsSelected()) || this.getNbRowsSelected() === 0) ? this.getNbGridRecords() : this.getNbRowsSelected();
            if (!Ext.isEmpty(maxThreshold.value) && parseInt(maxThreshold.value) !== -1 && nbRows > parseInt(maxThreshold.value)) {
				maxThresholdTextParam = this.getParameterFromName(parameters, "too_many_selected_threshold_text");
				maxTresholdText = i18n.get("label.defaultMaxThresholdText");
				if (!Ext.isEmpty(maxThresholdTextParam)) {
					maxTresholdText = maxThresholdTextParam.value;
				}
				Ext.Msg.alert(i18n.get("label.error"), maxTresholdText);
				return;
            }
		}
        //in the case of a OrderResource, let's check that the number of records is not superior to max_warning_threshold => ask the user to continue or stop the resource execution
        var warningThreshold = this.getParameterFromName(parameters, "max_warning_threshold");
        if (!Ext.isEmpty(warningThreshold)) {
            //get the number of rows either from a selection or all the rows
            nbRows = (Ext.isEmpty(this.getNbRowsSelected()) || this.getNbRowsSelected() === 0) ? this.getNbGridRecords() : this.getNbRowsSelected();
            if (!Ext.isEmpty(warningThreshold.value) &&  parseInt(warningThreshold.value) !== -1 && nbRows >  parseInt(warningThreshold.value)) {
	            var warningThresholdTextParam = this.getParameterFromName(parameters, "max_warning_threshold_text");
	            var warningTresholdText = i18n.get("label.defaultWarningThresholdText");
	            if (!Ext.isEmpty(warningThresholdTextParam)) {
					warningTresholdText = warningThresholdTextParam.value;
				} 
	            Ext.Msg.show({
		            title : i18n.get('label.warning'),
		            buttons : Ext.Msg.YESNO,
		            msg : warningTresholdText,
		            scope : this,
		            fn : function (btn, text) {
		                if (btn === 'yes') {
	                        this.handleResourceClick(resource, url, methods, runType, parameters);
		                }
		            }
	            }); 
	            return;     
            }
        }
        this.handleResourceClick(resource, url, methods, runType, parameters);
	},
    /**
     * get the parameter with the given name from the given list of parameter
     * @param {Array} parameters the Array of parameters
     * @param {string} name the name of the Parameter to search
     * @return {Object} a Parameter with the given name or null if the parameter is not found 
     */
    getParameterFromName : function (parameters, name) {
		var paramOut = null;
		for (var index = 0; index < parameters.length && paramOut === null; index++) {
			if (parameters[index].name === name) {
				paramOut = parameters[index];
			}
		}
		return paramOut;

	}
        

});
