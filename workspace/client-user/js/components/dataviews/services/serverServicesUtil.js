/***************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.namespace('sitools.user.component.dataviews.services');

/**
 * Define the contextMenu and the toolbar menu for the Sitools data Views.
 * 
 * @class sitools.user.component.dataviews.ctxMenu
 * @extends Ext.util.Observable
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
sitools.user.component.dataviews.services.serverServicesUtil =  Ext.extend(Ext.util.Observable, {

    constructor : function (config) {
        this.grid = config.grid;
        this.origin = config.origin;
        this.datasetUrl = config.datasetUrl;
        
        this.urlDatasetServiceServer = this.datasetUrl + "/services" + '/server/{idService}';
    },
    
    callServerService : function (idService, selections) {
        this.setSelections(selections);
        Ext.Ajax.request({
            url : this.urlDatasetServiceServer.replace('{idService}', idService),
            method : 'GET',
            scope : this,
            success : function (ret) {
                var json = Ext.decode(ret.responseText);
                if (!json.success) {
                    Ext.Msg.alert(i18n.get("label.warning"),i18n.get("label.resource.not.found"));
                }
                
                var resource = json.resourcePlugin;
//                this.resourcePresent = true;
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
                        url = this.datasetUrl + param.value;
                        break;
                    case "image":
                        icon = param.value;
                        break;
                    }
                }, this);
                
                this.resourceClick(resource, url, methods, runTypeUserInput, parameters);        
            }
        });
        
    },
    
    
    /**
     * Get the number of selected records
     * @return {number}
     */
    getNbRowsSelected : function () {
        if (this.grid.getNbRowsSelected()) {
            return this.grid.getNbRowsSelected();
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
                                iFrame  = new Ext.ux.ManagedIFrame.Panel({
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
                    iFrame  = new Ext.ux.ManagedIFrame.Panel({
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
