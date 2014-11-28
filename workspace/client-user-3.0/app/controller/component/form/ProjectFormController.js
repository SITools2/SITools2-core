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
 * global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin,
 * DEFAULT_PREFERENCES_FOLDER, loadUrl
 */
/*
 * @include "../../sitoolsProject.js" @include "../../desktop/desktop.js"
 * @include "../../components/forms/forms.js" @include
 * "../../components/forms/projectForm.js"
 */

Ext.namespace('sitools.user.controller.modules.form');

/**
 * Forms Module : Displays All Forms depending on datasets attached to the
 * project.
 * 
 * @class sitools.user.modules.formsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.forms.mainContainer
 */
Ext.define('sitools.user.controller.component.form.ProjectFormController', {
	extend : 'Ext.app.Controller',
    
    views : ['component.form.ProjectFormView',
             'component.form.OverviewResultProjectForm',
             'component.form.ResultProjectForm'],
    
    init : function () {
        this.control({
            'projectformview #btnSearchForm' : {
                click : this.onSearch
            },

            'projectformview button#refreshDatasets' : {
                click : this.propertySearch
            },

            'resultProjectForm actioncolumn#showDefinition' : {
                click : function (view, cell, rowIndex, col, e) {
                    var grid = view.up("grid")
                    var rec = grid.getStore().getAt(rowIndex);
                    if (Ext.isEmpty(rec)) {
                        return;
                    }
                    var url = rec.get('url');
                    sitools.user.utils.DatasetUtils.openDefinition(url);
                }
            },

            'resultProjectForm actioncolumn#showData' : {
                click : function (view, cell, rowIndex, col, e) {
                    var grid = view.up("grid");
                    var rec = grid.getStore().getAt(rowIndex);
                    Desktop.getNavMode().multiDataset.showDataset(grid, rec, grid.formConceptFilters);
                }
            }
        });

        this.listen({
            store : {
                '#resultProjectFormStore' : {
                    load: function (store, recs, success ,options) {
                        var grid = store.grid;
                        if(Ext.isEmpty(store.task)) {
                            store.task = Ext.create("Ext.util.DelayedTask", function () {
                                return;
                            });
                        }
                        store.task.cancel();
                        if (store.getProxy().reader.jsonData.TaskModel.status == "TASK_STATUS_RUNNING" ||
                            store.getProxy().reader.jsonData.TaskModel.status == "TASK_STATUS_PENDING") {
                            grid.down('toolbar').setStatus({
                                // text: ret.error ? ret.error :
                                // i18n.get('warning.serverUnreachable'),
                                text: i18n.get('label.loading'),
                                iconCls: 'x-status-busy'
                            });

                            store.task.delay(MULTIDS_TIME_DELAY, function () {
                                store.load();
                            });
                        }
                        else {
                            Ext.Ajax.request({
                                scope: this,
                                url: grid.urlTask,
                                method: "DELETE",
                                success: function (ret) {
                                    //								var callerCmp = Ext.getCmp(grid.callerId);
                                    //								callerCmp.fireEvent("multiDsSearchDone");
                                },
                                failure: alertFailure
                            });
                            if (store.getProxy().reader.jsonData.TaskModel.status == "TASK_STATUS_FAILURE") {
                                grid.down('toolbar').setStatus({
                                    text: store.getProxy().reader.jsonData.TaskModel.customStatus,
                                    iconCls: 'x-status-error'
                                });
                            }
                            else {
                                grid.down('toolbar').setStatus({
                                    text: i18n.get("label.requestDone"),
                                    iconCls: 'x-status-valid'
                                });
                            }
                        }
                        store.each(function (record) {
                            var error = record.get("errorMessage");
                            if (!Ext.isEmpty(error)) {
                                var index = store.indexOf(record);
                                var htmlLineEl = grid.getView().getNode(index);

                                Ext.create("Ext.tip.ToolTip", {
                                    html: error,
                                    dismissDelay: 0,
                                    target: htmlLineEl,
                                    bodyCls: "x-tip-body-form-invalid"
                                });

                            }
                        }, this);
                    }
                }
            }
        })
    },

    /**
     * Build the query for the multiDs search
     * @param {Ext.Button} button The button that launch the request (to be disabled)
     * @returns
     */
    onSearch : function (button) {
//        button.setDisabled(true);

        var projectformview = button.up('projectformview');
        var containers = projectformview.down('[stype="sitoolsFormContainer"]');

        var formConceptFilters = [];
        var glue = "";
        var i = 0;
        var datasets = [];
        projectformview.datasetPanel.getStore().each(function (rec) {
            if (rec.get("visible")) {
                datasets.push(rec.get('id'));
            }
        });
        if (datasets.length <= 0) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.atLeastOneDataset'));
            button.setDisabled(false);
            return;
        }

        if (projectformview.nbDatasetsMax > 0 && datasets.length > projectformview.nbDatasetsMax) {
            Ext.Msg.alert(i18n.get('label.error'), Ext.String.format(i18n.get('label.toManyDatasetsAllowed'), projectformview.nbDatasetsMax));
            button.setDisabled(false);
            return;
        }
        var valid = true;

        projectformview.zonesPanel.items.each(function(componentList) {
            valid = valid && componentList.isComponentsValid();
        },projectformview);

        if (!valid) {
            projectformview.down('toolbar').setStatus({
                text : i18n.get('label.checkformvalue'),
                iconCls : 'x-status-error'
            });
            projectformview.down('toolbar').setVisible(true);
            button.setDisabled(false);
            return;
        } else {
            projectformview.down('toolbar').setVisible(false);
        }

        var formConceptParams = {};
        i = 0;
        Ext.each(containers, function (container) {
            if (Ext.isFunction(container.getParameterValue)) {
                var param = container.getParameterValue();
                if (!Ext.isEmpty(param)) {
                    //Object filter definition
                    param.dictionary = projectformview.dictionaryName;
                    formConceptFilters.push(param);
                    //url param filter definition
                    formConceptParams["c[" + i + "]"] = this.paramValueToApi(param);
                    i += 1;
                }
            }
        }, this);
        var urlService = Project.sitoolsAttachementForUsers + projectformview.urlServiceDatasetSearch;

        var params = Ext.clone(formConceptParams);
        params.datasetsList = datasets.join("|");

        //Launch the first POST Request on service:
        Ext.Ajax.request({
            method : "POST",
            params : params,
            //Just to be sure that params are passed with the url request
            jsonData : {},
            scope : this,
            url : urlService,
            success : function (response) {
                //try {
                    var json = Ext.decode(response.responseText);
                    if (! json.success) {
                        Ext.Msg.alert(i18n.get('label.error'), json.message);
                        return;
                    }

//                    var jsObj = SitoolsDesk.navProfile.multiDataset.getObjectResults();
                    var jsObj = Desktop.getNavMode().multiDataset.getObjectResults();

                    var componentCfg = {
                        urlTask : json.TaskModel.statusUrl,
                        formId : projectformview.formId,
                        formConceptFilters : formConceptFilters,
                        formConceptParams : formConceptParams,
                        datasets : datasets,
                        formName : projectformview.formName, 
                        callerId : this.id
                    };

                    var windowConfig = {
                        id : "windMultiDsResultForm" + projectformview.formId, 
                        title : i18n.get('label.MultiDsResultForm') + " : " + projectformview.formName, 
                        saveToolbar : false, 
                        iconCls : "dataviews"
                    };

                    var viewResultForm = Ext.create(jsObj, componentCfg);
                    var sitoolsController = Desktop.getApplication().getController	('core.SitoolsController');
                    //On ouvre le composant
                    sitoolsController.openSimpleWindow(viewResultForm, windowConfig);

                    //
                    //
                    //Desktop.getNavMode().openComponent(viewResultForm, windowConfig);

                //}
                //catch (err) {
                //    Ext.Msg.alert(i18n.get('label.error'), err);
                //    return;
                //}
            },
                
            failure : alertFailure
        });
        
//        var desktop = getDesktop();
//        var win = desktop.getWindow("windMultiDsResultForm" + this.formId);
//        if (win) {
//            win.close();
//        }

    },
    /**
     * A method to save all the window settings to be abble to reload it when desktop is reloaded. 
     * @return {}
     */
    _getSettings : function () {
        return {
            objectName : "projectForm", 
            formId : this.formId,
            formName : this.formName,
            formParameters : this.formParameters,
            formWidth : this.formWidth,
            formHeight : this.formHeight, 
            formCss : this.formCss, 
            properties : this.properties, 
            urlServicePropertiesSearch : this.urlServicePropertiesSearch, 
            urlServiceDatasetSearch : this.urlServiceDatasetSearch, 
            componentType : this.componentType, 
            dictionaryName : this.dictionaryName,
            preferencesPath : this.preferencesPath, 
            preferencesFileName : this.preferencesFileName,
            formZones : this.zones
            
        };
    }, 
    /**
     * Build a string using a form param Value. 
     * @param {} paramValue An object with attributes : at least type, code, value and optionnal userDimension, userUnit
     * @return {string} something like "TEXTFIELD|ColumnAlias|value"
     */
    paramValueToApi : function (paramValue) {
        var stringParam = paramValue.type + "|" + paramValue.dictionary + "," + paramValue.code + "|" + paramValue.value;
        if (!Ext.isEmpty(paramValue.userDimension) && !Ext.isEmpty(paramValue.userUnit)) {
            stringParam += "|" + paramValue.userDimension + "|" + paramValue.userUnit.unitName; 
        }  
        return stringParam;
    }, 
    /**
     * Returns the search Button. 
     * @return {}
     */
    getSearchButton : function () {
        return this.searchButton;
    },

    /**
     * Method called when user pressed on refresh Datasets button.
     * Course properties and creates the parameters of the query to search the list of datasets
     */
    propertySearch : function (btn) {
        var form = btn.up("form#propertyPanelId");
        var view = form.up("projectformview");
        var params = {};
        var j = 0;
        form.items.each(function(prop) {
            if (!Ext.isEmpty(prop.getAPIValue())) {
                params["k[" + j + "]"] = prop.getAPIValue();
                j++;
            }
        });

        view.datasetPanel.getStore().load({
            params : params
        });
        view.datasetPanel.getView().refresh();
    }
    
});
