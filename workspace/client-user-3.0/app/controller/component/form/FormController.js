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
Ext.define('sitools.user.controller.component.form.FormController', {
	extend : 'Ext.app.Controller',
    
    views : ['component.form.FormView',
             'component.form.FormContainerView'],
    
    config : {
        listFormView : Ext.create('Ext.util.MixedCollection')
    },
             
    init : function () {
        this.control({
            'formContainerView' : {
                componentChanged : this.componentChanged,
                afterrender : this.afterRenderFormContainer
            },
            
            'formsView' : {
                resize : this.resizeForm
            },
            
            'formsView #btnSearchForm' : {
                click : this.onSearch
            }
        });
    },
    
    componentChanged : function (formContainer, componentChanged) {
        console.log(componentChanged);
        // look for all the childrens of the component
        var childrens = formContainer.find("parentParam", componentChanged.parameterId);
        // For each children, add a query string on the componentChanged
        // value and reset children Value.
        // Also, fire the event ComponentChanged for the children to cascade
        // changes.
        Ext.each(childrens, function (children) {
            if (children.valueSelection == 'D') {
                var store = children.find("stype", "sitoolsFormItem")[0].store;

                var baseParams = store.baseParams;

                if (!Ext.isEmpty(componentChanged.getSelectedValue())) {
                    var filter = componentChanged.getParameterValue();
                    baseParams["p[0]"] = this.paramToAPI(filter);
                } else {
                    baseParams["p[0]"] = null;
                }
                store.baseParams = baseParams;
                children.setSelectedValue(null);
                store.reload({
                    callback : function () {
                        formContainer.fireEvent('componentChanged', formContainer, children);
                    }
                });

            }
        }, this);
    },
    
    afterRenderFormContainer : function (formContainer) {
        try {
            var cmpChildSize = formContainer.getSize();
            var size = formContainer.ownerCt.ownerCt.body.getSize();
            var xpos = 0, ypos = 0;
            if (size.height > cmpChildSize.height) {
                ypos = (size.height - cmpChildSize.height) / 2;
            }
            if (size.width > cmpChildSize.width) {
                xpos = (size.width - cmpChildSize.width) / 2;
            }
            formContainer.setPosition(xpos, ypos);
        } catch (err) {
            return;
        }
        formContainer.doLayout();
    },
    
    resizeForm : function (form) {
        if (!Ext.isEmpty(form.zonesPanel.getEl())) {
            var cmpChildSize = form.zonesPanel.getSize();
            var size = form.body.getSize();
            var xpos = 0, ypos = 0;
            if (size.height > cmpChildSize.height) {
                ypos = (size.height - cmpChildSize.height) / 2;
            }
            if (size.width > cmpChildSize.width) {
                xpos = (size.width - cmpChildSize.width) / 2;
            }
            form.zonesPanel.setPosition(xpos, ypos);
        }
    },
    
    onSearch : function (btn) {
        
        var valid = true;
        
        me = btn.up('formsView');
        
        me.zonesPanel.items.each(function(componentList) {
            valid = valid && componentList.isComponentsValid();            
        },this);
        
        if (!valid) {
            me.down('toolbar').setStatus({
                text : i18n.get('label.checkformvalue'),
                iconCls : 'x-status-error'
            });
            me.down('toolbar').setVisible(true);    
            return;
        } else {
            me.down('toolbar').setVisible(false);
        }
        //Execute a request to get the dataset config 
        Ext.Ajax.request({
            url : me.dataUrl, 
            method : "GET",
            scope : this,
            success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (!Json.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), Json.message);
                    return;
                } else {
                    var dataset = Json.dataset;
                    this.doSearch(me, dataset);
                }
            },
            failure : alertFailure
        });
    }, 
    
    /**
     * Build the query for the liveGrid and build the livegrid component
     * @param config
     * @returns
     */
    doSearch : function (formView, dataset) {
        var containers = formView.query('[stype="sitoolsFormContainer"]');
        var formParams = [];
        var glue = "";
        var i = 0;
        Ext.each(containers, function (container) {
            if (Ext.isFunction(container.getParameterValue)) {
                var param = container.getParameterValue();
                if (!Ext.isEmpty(param)) {
                    formParams.push(param);
                }
            }
        }, this);

//        var allObjectParams = {};
//        Ext.each(formParams, function (param, index, arrayParams) {
//        	allObjectParams["p[" + index + "]"] = param;
//        });
        
        if (Ext.isFunction(this.searchAction)) {
            this.searchAction(formParams, dataset, this.scope);
        }
        else {
            this.defaultSearchAction(formParams, dataset);
        }
        
    },
    
    defaultSearchAction : function (formParams, dataset) {
        var windowConfig = {
//            id : "windResultForm" + config.formId,
            title : i18n.get('label.dataTitle') + " : " + dataset.name, 
            datasetName : dataset.name, 
            type : "data", 
            saveToolbar : true, 
            iconCls : "dataviews"
        };
        
        var componentsConfig = {
        	formFilters : formParams,
        	dataset : dataset
        };
        
        var sitoolsController = Desktop.getApplication().getController('core.SitoolsController'); 
        sitoolsController.openComponent(dataset.datasetView.jsObject, componentsConfig, windowConfig);
    }, 
    
    _getSettings : function () {
        return {
            objectName : "forms", 
            dataUrl : this.dataUrl,
            dataset : this.dataset,
            formId : this.formId,
            formName : this.formName,
            formParameters : this.formParameters,
            formWidth : this.formWidth,
            formHeight : this.formHeight, 
            formCss : this.formCss, 
            datasetView : this.datasetView,
            dictionaryMappings : this.dictionaryMappings, 
            preferencesPath : this.preferencesPath, 
            preferencesFileName : this.preferencesFileName
        };
    }, 
    /**
     * Build a string using a form param Value. 
     * @param {} paramValue An object with attributes : at least type, code, value and optionnal userDimension, userUnit
     * @return {string} something like "TEXTFIELD|ColumnAlias|value"
     */
    paramValueToApi : function (paramValue) {
        var stringParam = paramValue.type + "|" + paramValue.code + "|" + paramValue.value;
        if (!Ext.isEmpty(paramValue.userDimension) && !Ext.isEmpty(paramValue.userUnit)) {
            stringParam += "|" + paramValue.userDimension + "|" + paramValue.userUnit.unitName; 
        }  
        return stringParam;
    }
    
});
