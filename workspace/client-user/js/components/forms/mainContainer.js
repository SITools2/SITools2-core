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
/*global Ext, sitools, i18n, userLogin, DEFAULT_WIN_HEIGHT, DEFAULT_WIN_WIDTH, getDesktop, projectGlobal, SitoolsDesk, DEFAULT_PREFERENCES_FOLDER*/
/*global loadUrl*/
/*
 * @include "formComponentsPanel.js"
 */
Ext.namespace('sitools.user.component.forms');

/**
 * The global Panel. A panel with a formComponentsPanel and the buttons. 
 * @cfg {string} dataUrl Attachement Url of the dataset
 * @cfg {string} datasetId datasetId 
 * @cfg {string} datasetName Dataset Name
 * @cfg {Ext.grid.ColumnModel} datasetCm Column model of the dataset
 * @cfg {string} formId Form Id
 * @cfg {string} formName Form Name
 * @cfg {Array} formParameters Array of form parameters
 * @cfg {Array} formZones Array of form Zones containing parameters
 * @cfg {number} formWidth Form Width 
 * @cfg {number} formHeight Form Height 
 * @cfg {string} formCss Name of a specific css class to apply to form
 * @cfg {string} datasetView Name of the datasetView Object
 * @cfg {Array} dictionaryMappings the Mapping between dataset column Model and concepts
 * @class sitools.user.component.forms.mainContainer
 * @extends Ext.Panel
 * @requires sitools.user.component.formComponentsPanel
 */
sitools.user.component.forms.mainContainer = function (config) {
//sitools.component.users.datasets.forms = function (config) {
    Ext.apply(this, config);
    this.componentType = "form";
    
    var panelIdObject = {};
    
    // New Form model with zones
    if (!Ext.isEmpty(this.formZones)){
        Ext.each(this.formZones, function(formParam) { 
            var containerId = formParam.id;
            if (Ext.isEmpty(panelIdObject[containerId])){
                panelIdObject[containerId] = [];
            }
            panelIdObject[containerId].push(formParam);
        });
    } else { // old form model
        Ext.each(config.formParameters, function(formParam) { 
            var containerId = formParam.containerPanelId;
            if (Ext.isEmpty(panelIdObject[containerId])){
                panelIdObject[containerId] = [];
            }
            panelIdObject[containerId].push(formParam);
        });
    }
    
    var items = [];
    var globalParams = {};
    
    Ext.iterate(panelIdObject, function(key, formParams){
    	var componentList = new  sitools.user.component.formComponentsPanel({
            border: true,
            css : config.formCss, 
            formId : config.formId,
            id : key
    	});

        if (!Ext.isEmpty(this.formZones)) {
            globalParams.formZones = formParams;
        } else {
            globalParams.oldParameters = formParams;
        }

        componentList.datasetCm = config.dataset.columnModel;
        componentList.loadParameters(globalParams, config.dataUrl, "dataset");

		items.push(componentList);
    }, this);
    
    this.zonesPanel = new Ext.Panel({
        width : config.formWidth,
        height : config.formHeight,
        css : config.formCss, 
        formId : config.formId,
        items : [items]
    });
    
    if (Ext.isEmpty(config.dataset)) {
	    Ext.Ajax.request({
			url : config.dataUrl, 
			method : "GET", 
			scope : this, 
			success : function (ret) {
				if (showResponse(ret)) {
	                var json = Ext.decode(ret.responseText);
//	                this.componentList.datasetCm = json.dataset.columnModel;
//					this.componentList.loadParameters(config.formParameters, config.dataUrl, "dataset");
					this.datasetId = json.dataset.id;
					this.datasetName = json.dataset.name;
					this.datasetCm = json.dataset.columnModel;
		            this.datasetView = json.dataset.datasetView;
					this.dictionaryMappings = json.dataset.dictionaryMappings;
	            }
			}
		});
    }
    else {
//		this.componentList.datasetCm = config.dataset.columnModel;
//		this.componentList.loadParameters(config.formParameters, config.dataUrl, "dataset");
		this.datasetId = config.dataset.id;
		this.datasetName = config.dataset.name;
		this.datasetCm = config.dataset.columnModel;
        this.datasetView = config.dataset.datasetView;
		this.dictionaryMappings = config.dataset.dictionaryMappings;
    }
    
    sitools.user.component.forms.mainContainer.superclass.constructor.call(this, Ext.apply({
        height : config.formHeight,
        width : config.formWidth,
        autoScroll : true,
        bodyBorder : false,
        border : false,
        iconCls : 'z-btn-search',
        items : [this.zonesPanel],
        buttons : [ 

//      ************************* Reset Form button ************************** //
	{
            text : i18n.get("label.reset"),
            scope : this,
            iconCls : 'x-btn-reset',
            handler : function () {
                var containers = this.find("stype", 'sitoolsFormContainer');
                Ext.each(containers, function (container) {
                    if (Ext.isFunction(container.resetToDefault)) {
                        container.resetToDefault();
                    }
                }, this);

            }
        },
//      ********************************************************************** //

	{
            text : i18n.get('label.search'),
            scope : this,
            iconCls : 'x-btn-search',
            handler : function () {
                this.onSearch(config);
            }
        }
	],
        listeners : {
			scope : this, 
			resize : function () {
				if (!Ext.isEmpty(this.zonesPanel.getEl())) {
					var cmpChildSize = this.zonesPanel.getSize();
					var size = this.body.getSize();
					var xpos = 0, ypos = 0;
					if (size.height > cmpChildSize.height) {
						ypos = (size.height - cmpChildSize.height) / 2;
					}
					if (size.width > cmpChildSize.width) {
						xpos = (size.width - cmpChildSize.width) / 2;
					}
					this.zonesPanel.setPosition(xpos, ypos);
				}
				
			}
        },          
        bbar : new Ext.ux.StatusBar({
            text : i18n.get('label.ready'),
            iconCls : 'x-status-valid',
            hidden : true
        })
    }));

};
Ext.extend(sitools.user.component.forms.mainContainer, Ext.Panel, {
    onSearch : function (config) {
        
        var valid = true;
        
        this.zonesPanel.items.each(function(componentList){
            valid = valid && componentList.isComponentsValid();            
        },this);
        
        if (!valid) {
            this.getBottomToolbar().setStatus({
                text : i18n.get('label.checkformvalue'),
                iconCls : 'x-status-error'
            });
            this.getBottomToolbar().setVisible(true);    
            return;
        } else {
            this.getBottomToolbar().setVisible(false);
        }
		//Execute a request to get the dataset config 
		Ext.Ajax.request({
			url : config.dataUrl, 
			method : "GET", 
			scope : this, 
			success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (!Json.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), Json.message);
                    return;
                } else {
					var dataset = Json.dataset;
					this.doSearch(config, dataset);
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
    doSearch : function (config, dataset) {
        var containers = this.find("stype", 'sitoolsFormContainer');
        var formParams = [];
        var glue = "";
        var i = 0;
        Ext.each(containers, function (container) {
            // var f = form.getForm();

            if (Ext.isFunction(container.getParameterValue)) {
	            var param = container.getParameterValue();
	            if (!Ext.isEmpty(param)) {
	                formParams.push(this.paramValueToApi(param));
	            }
            }
        }, this);

        var desktop = getDesktop();
        var win = desktop.getWindow("windResultForm" + config.formId);
        if (win) {
            win.close();
        }
        if (Ext.isFunction(this.searchAction)) {
        	this.searchAction(formParams, dataset, this.scope);
        }
        else {
        	this.defaultSearchAction(formParams, dataset);
        }
        
    },
    
    defaultSearchAction : function (formParams, dataset) {
        var jsObj = eval(dataset.datasetView.jsObject);
        var componentCfg = {
            dataUrl : dataset.sitoolsAttachementForUsers,
            datasetId : dataset.id,
            datasetCm : dataset.columnModel,
            datasetName : dataset.name, 
            formParams : formParams, 
            dictionaryMappings : dataset.dictionaryMappings, 
			datasetViewConfig : dataset.datasetViewConfig, 
            preferencesPath : "/" + dataset.name, 
            preferencesFileName : "datasetView"
        };
        var windowConfig = {
//            id : "windResultForm" + config.formId, 
            title : i18n.get('label.dataTitle') + " : " + dataset.name, 
            datasetName : dataset.name, 
            type : "data", 
            saveToolbar : true, 
            iconCls : "dataviews"
        };
        SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
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

