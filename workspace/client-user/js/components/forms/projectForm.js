/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, SITOOLS_DATE_FORMAT, SITOOLS_DEFAULT_IHM_DATE_FORMAT, i18n, userLogin, DEFAULT_WIN_HEIGHT, DEFAULT_WIN_WIDTH, getDesktop, projectGlobal, SitoolsDesk, DEFAULT_PREFERENCES_FOLDER, alertFailure*/
/*global loadUrl*/
/*
 * @include "formComponentsPanel.js"
 * @include "resultsProjectForm.js"
 */
Ext.namespace('sitools.user.component.forms');

/**
 * The global Panel. A panel with a formComponentsPanel and the buttons. 
 * @cfg {string} formId Id of the selected Form
 * @cfg {string} formName Name of the selected Form 
 * @cfg {Array} formParameters Array of all form Parameters
 * @cfg {number} formWidth Form Width 
 * @cfg {number} formHeight Form Height 
 * @cfg {string} formCss Name of a specific css class to apply to form 
 * @cfg {Array} properties An array of Properties. 
 * @cfg {string} urlServicePropertiesSearch The url to request properties
 * @cfg {string} urlServiceDatasetSearch the url to request for Multids Search
 * @cfg {string} dictionaryName the Name of the dictionary attached to the form
 * @class sitools.user.component.forms.projectForm
 * @extends Ext.Panel
 * @requires sitools.user.component.formComponentsPanel
 */
sitools.user.component.forms.projectForm = Ext.extend(Ext.Panel, {
    initComponent : function () {
        var config = this; 
        this.componentType = "formProject";
    
        /**
         * The panel that displays all form components as defined by the administrator. 
         */
        this.componentList = new sitools.user.component.formComponentsPanel({
            width : config.formWidth,
            height : config.formHeight, 
            css : config.formCss, 
            formId : config.formId
        });
        if (!Ext.isEmpty(config.formParameters)) {
            this.componentList.loadParameters(config.formParameters, config.dataUrl, "project");
        }
    
        var displayComponentPanel = new Ext.Panel({
            title : i18n.get('label.formConcepts'), 
            region : "center", 
            flex : 2, 
            autoScroll : true,
            items : [ this.componentList ],
            layout : "absolute",
            listeners : {
                scope : this, 
                resize : function () {
                    if (!Ext.isEmpty(this.componentList.getEl())) {
                        var cmpChildSize = this.componentList.getSize();
                        var size = this.body.getSize();
                        var xpos = 0, ypos = 0;
                        if (size.height > cmpChildSize.height) {
                            ypos = (size.height - cmpChildSize.height) / 2;
                        }
                        if (size.width > cmpChildSize.width) {
                            xpos = (size.width - cmpChildSize.width) / 2;
                        }
                        this.componentList.setPosition(xpos, ypos);
                        
                    }
                }
            }
        });
        
        /**
         * The panel that displays Property search
         * Each property adds a formField with the buildPropertyField method
         */
        this.propertyPanel = new Ext.form.FormPanel({
            title : i18n.get("label.defineProperties"), 
            padding : 10,
            labelWidth : 100, 
            flex : 2,
            autoScroll : true, 
            defaults : {
                labelSeparator : ""
            },
            buttons : [{
                text : i18n.get('label.refreshDatasets'), 
                scope : this, 
                handler : this.propertySearch
            }]
        });
        if (!Ext.isEmpty(this.properties)) {
            Ext.each(this.properties, function (prop) {
                var field = this.buildPropertyField(prop);
                this.propertyPanel.add(field);
            }, this);
        }
        
        
        var storeDatasets = new Ext.data.JsonStore({
            restful  : true, 
            fields : [{
                name : "id", 
                type : "string"
            }, {
                name : "name", 
                type : "string"
            }, {
                name : "visible", 
                type : "boolean"
            }], 
            url : projectGlobal.sitoolsAttachementForUsers + this.urlServicePropertiesSearch, 
            root : "collection.dataSets", 
            listeners : {
                load : function (store, recs) {
                    Ext.each(recs, function (rec) {
                        rec.set("visible", true);
                    });
                }
            }, 
            autoLoad : true
        });
        
        var visible = new Ext.grid.CheckColumn({
            header : i18n.get('headers.visible'),
            dataIndex : 'visible',
            width : 55
        });
        
        var cmDatasets = new Ext.grid.ColumnModel({
            columns : [ {
                header : i18n.get('headers.name'),
                dataIndex : 'name',
                width : 120
            }, visible]
        });
        
        var smDatasets = new Ext.grid.RowSelectionModel({
            singleSelect : true
        });
        
        /**
         * The dataset list. 
         * It is updated when user pressed on refresh dataset button.
         */
        this.datasetPanel = new Ext.grid.EditorGridPanel({
            title : i18n.get('label.defineDatasets'), 
            store : storeDatasets, 
            cm : cmDatasets, 
            sm : smDatasets, 
            flex : 1, 
            autoScroll : true, 
            viewConfig : {
                forceFit : true 
            }, 
            plugins : [visible]
        });
        
        var firstPanel = new Ext.Panel({
            height : 300, 
            items : [this.propertyPanel, this.datasetPanel], 
            layout : "hbox", 
            collapsedTitle : i18n.get('label.advancedSearch'),
            region : "north",
            collapsible : true, 
            collapsed : true, 
            flex : 2, 
            layoutConfig : {
                align : "stretch"   
            }
        });
        /**
         * A simple button to launch the main request on each selected dataset. 
         */
        this.searchButton = new Ext.Button({
            text : i18n.get('label.search'),
            scope : this,
            handler : function (button) {
                this.onSearch(button);
            }
        });
        
        Ext.apply(this, {
            height : config.formHeight,
            layout : "border", 
            layoutConfig : {
                align : "stretch"
            },
            items : [ firstPanel, displayComponentPanel ],
            buttons : [this.searchButton], 
            listeners : {
                scope : this, 
                propertyChanged : function () {
                    var properties = this.propertyPanel.items.items;
                    var params = {};
                    var j = 0;
                    var k = {};
                    for (var i = 0; i < properties.length; i++) {
                        var prop = properties[i];
                        if (!Ext.isEmpty(prop.getAPIValue())) {
                            params["k[" + j + "]"] = prop.getAPIValue();
                            j++;
                        }
                    }
                    this.datasetPanel.getStore().load({
                        params : params
                    });
                }, 
                multiDsSearchDone : function () {
                    this.searchButton.setDisabled(false);
                }

            },            
            bbar : new Ext.ux.StatusBar({
                text : i18n.get('label.ready'),
                iconCls : 'x-status-valid',
                hidden : true
            })
        });
        sitools.user.component.forms.projectForm.superclass.initComponent.call(this);
    }, 
    /**
     * Build the query for the multiDs search
     * @param {Ext.Button} button The button that launch the request (to be disabled)
     * @returns
     */
    onSearch : function (button) {
        button.setDisabled(true);
        var containers = this.find("stype", 'sitoolsFormContainer');
        var formMultiDsParams = [];
        var glue = "";
        var i = 0;
        var datasets = [];
        this.datasetPanel.getStore().each(function (rec) {
            if (rec.get("visible")) {
                datasets.push(rec.get('id'));
            }
        });
        if (datasets.length <= 0) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.atLeastOneDataset'));
            button.setDisabled(false);
            return;
        }
        
        if (! Ext.isEmpty(this.nbDatasetsMax) && datasets.length > this.nbDatasetsMax) {          
            Ext.Msg.alert(i18n.get('label.error'), String.format(i18n.get('label.toManyDatasetsAllowed'), this.nbDatasetsMax));
            button.setDisabled(false);
            return;
        }
        
        var valid = this.componentList.isComponentsValid();
        if (!valid) {
            this.getBottomToolbar().setStatus({
                text : i18n.get('label.checkformvalue'),
                iconCls : 'x-status-error'
            });
            this.getBottomToolbar().setVisible(true);    
            button.setDisabled(false);
            return;
        } else {
            this.getBottomToolbar().setVisible(false);
        }
        
        Ext.each(containers, function (container) {
            // var f = form.getForm();

            if (Ext.isFunction(container.getParameterValue)) {
                var param = container.getParameterValue();
                
                if (!Ext.isEmpty(param)) {
                    formMultiDsParams.push(this.paramValueToApi(param));
                }
            }
        }, this);
        
        var urlService = projectGlobal.sitoolsAttachementForUsers + this.urlServiceDatasetSearch;
        
        var params = {};
        params.datasetsList = datasets.join("|");

        i = 0;
        
        if (!Ext.isEmpty(formMultiDsParams)) {
            Ext.each(formMultiDsParams, function (param) {
                params["c[" + i + "]"] = param;
                i += 1;
            }, this);
        }

        //Launch the first POST Request on service: 
        Ext.Ajax.request({
            method : "POST", 
            params : params, 
            //Just to be sure that params are passed with the url request
            jsonData : {}, 
            scope : this, 
            url : urlService, 
            success : function (response) {
                try {
                    var json = Ext.decode(response.responseText);
                    if (! json.success) {
                        Ext.Msg.alert(i18n.get('label.error'), json.message);
                        return;
                    }
                    var jsObj = SitoolsDesk.navProfile.multiDataset.getObjectResults();
                    var componentCfg = {
                        urlTask : json.TaskModel.statusUrl,
                        formId : this.formId,
                        formMultiDsParams : formMultiDsParams,
                        datasets : datasets, 
                        formName : this.formName, 
                        callerId : this.id
                    };

                    var windowConfig = {
                        id : "windMultiDsResultForm" + this.formId, 
                        title : i18n.get('label.MultiDsResultForm') + " : " + this.formName, 
                        saveToolbar : false, 
                        iconCls : "dataviews"
                    };
                    SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj);
                    
                }
                catch (err) {
                    Ext.Msg.alert(i18n.get('label.error'), err);
                    return;
                }
            }, 
                
            failure : alertFailure
        });
        var desktop = getDesktop();
        var win = desktop.getWindow("windMultiDsResultForm" + this.formId);
        if (win) {
            win.close();
        }

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
            preferencesFileName : this.preferencesFileName
            
        };
    }, 
    /**
     * Build a string using a form param Value. 
     * @param {} paramValue An object with attributes : at least type, code, value and optionnal userDimension, userUnit
     * @return {string} something like "TEXTFIELD|ColumnAlias|value"
     */
    paramValueToApi : function (paramValue) {
        var stringParam = paramValue.type + "|" + this.dictionaryName + "," + paramValue.code + "|" + paramValue.value;
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
     * Build for a properties a new formField depending on property type. 
     * The property type could be one of : 
     *  - TEXTFIELD, 
     *  - NUMERIC_FIELD, 
     *  - NUMERIC_BETWEEN, 
     *  - DATE_BETWEEN
     * @param {} prop the Json definition of a property. 
     * @return {Ext.form.Field} a simple or composite field. 
     */
    buildPropertyField : function (prop) {
        var field;
        switch (prop.type) {
        case "TEXTFIELD" : 
            field = {
                xtype : "textfield", 
                name : prop.name, 
                anchor : '98%', 
                enableKeyEvents : true, 
                fieldLabel : prop.name, 
                getAPIValue : function () {
                    if (Ext.isEmpty(this.getValue())) {
                        return null;
                    }
                    return String.format("{0}|{1}|{2}", prop.type, prop.name, this.getValue());
                }
            };          
            break;
        case "NUMBER_FIELD" : 
            field = {
                xtype : "numberfield", 
                name : prop.name, 
                anchor : '98%', 
                enableKeyEvents : true, 
                fieldLabel : prop.name, 
                getAPIValue : function () {
                    if (Ext.isEmpty(this.getValue())) {
                        return null;
                    }
                    return String.format("{0}|{1}|{2}", prop.type, prop.name, this.getValue());
                }
            };          
            break;
        case "NUMERIC_BETWEEN" : 
            field = {
                xtype: 'compositefield',
                defaults: {
                    flex: 1
                },
                msgTarget: 'under',
                anchor : '98%', 
                items: [
                    {
                        xtype: 'numberfield',
                        name : prop.name + "deb", 
                        enableKeyEvents : true
                    },
                    {
                        xtype: 'numberfield',
                        name : prop.name + "fin"
                
                    }
                ],
                fieldLabel : prop.name, 
                getAPIValue : function () {
                    var deb = this.items.itemAt(0).getValue();
                    var fin = this.items.itemAt(1).getValue();
                    if (Ext.isEmpty(deb) || Ext.isEmpty(fin)) {
                        return null;
                    }
                    return String.format("{0}|{1}|{2}|{3}", prop.type, prop.name, deb, fin);
                }
            };          
            break;
        case "DATE_BETWEEN" : 
            field = {
                xtype: 'compositefield',
                defaults: {
                    flex: 1
                },
                msgTarget: 'under',
                anchor : '98%', 
                items: [
                    {
                        xtype: 'datefield',
                        name : prop.name + "deb", 
                        enableKeyEvents : true, 
                        format : SITOOLS_DEFAULT_IHM_DATE_FORMAT, 
                        showTime : true
                    },
                    {
                        xtype: 'datefield',
                        name : prop.name + "fin", 
                        format : SITOOLS_DEFAULT_IHM_DATE_FORMAT, 
                        showTime : true
                
                    }
                ],
                fieldLabel : prop.name, 
                getAPIValue : function () {
                    var deb, fin;
                    try {
                        deb = this.items.itemAt(0).getValue().format(SITOOLS_DATE_FORMAT);
                        fin = this.items.itemAt(1).getValue().format(SITOOLS_DATE_FORMAT);
                    
                    }
                    catch (err) {
                        return null;
                    }
                    if (Ext.isEmpty(deb) || Ext.isEmpty(fin)) {
                        return null;
                    }
                    return String.format("{0}|{1}|{2}|{3}", prop.type, prop.name, deb, fin);
                }
            };          
            break;
        }
        return field;
    }, 
    /**
     * Method called when user pressed on refresh Datasets button. 
     * Course properties and creates the parameters of the query to search the list of datasets
     */
    propertySearch : function () {
        var properties = this.propertyPanel.items.items;
        var params = {};
        var j = 0;
        var k = {};
        for (var i = 0; i < properties.length; i++) {
            var prop = properties[i];
            if (!Ext.isEmpty(prop.getAPIValue())) {
                params["k[" + j + "]"] = prop.getAPIValue();
                j++;
            }
        }
        this.datasetPanel.getStore().load({
            params : params
        });
        this.datasetPanel.getView().refresh();
    }
    
});

