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
/*global Ext, sitools, SITOOLS_DATE_FORMAT, SITOOLS_DEFAULT_IHM_DATE_FORMAT, i18n, userLogin, DEFAULT_WIN_HEIGHT, DEFAULT_WIN_WIDTH, getDesktop, projectGlobal, SitoolsDesk, DEFAULT_PREFERENCES_FOLDER, alertFailure*/
/*global loadUrl*/
/*
 * @include "formComponentsPanel.js"
 * @include "resultsProjectForm.js"
 */
Ext.namespace('sitools.user.view.component.form');

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
Ext.define('sitools.user.view.component.form.ProjectFormView', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.projectformview',
    layout : {
        type : 'border',
        align : 'stretch'
    },

    initComponent : function () {

        this.height = this.formHeight;
        
        var panelIdObject = {};

        // New Form model with zones
        if (!Ext.isEmpty(this.formZones)) {
            Ext.each(this.formZones, function (formParam) {
                var containerId = formParam.containerPanelId;
                if (Ext.isEmpty(panelIdObject[containerId])) {
                    panelIdObject[containerId] = [];
                }
                panelIdObject[containerId].push(formParam);
            });
        } else { // old form model
            Ext.each(this.formParameters, function (formParam) {
                var containerId = formParam.containerPanelId;
                if (Ext.isEmpty(panelIdObject[containerId])) {
                    panelIdObject[containerId] = [];
                }
                panelIdObject[containerId].push(formParam);
            });
        }

        var items = [];
        var globalParams = {};

        Ext.iterate(panelIdObject, function (key, formParams) {
            var componentList = Ext.create('sitools.user.view.component.form.FormContainerView', {
                border : true,
                css : this.formCss,
                formId : this.formId,
                id : key,
                formWidth : this.formWidth,
                height : this.formHeight
            });

            if (!Ext.isEmpty(this.formZones)) {
                globalParams.formZones = formParams;
            } else {
                globalParams.oldParameters = formParams;
            }

            componentList.loadParameters(globalParams, this.dataUrl, "dataset");
            items.push(componentList);
        }, this);

        /**
         * The panel that displays all form components as defined by the administrator. 
         */
        this.zonesPanel = Ext.create('Ext.panel.Panel', {
            width : this.formWidth,
            height : this.formHeight,
            css : this.formCss,
            formId : this.formId,
            items : [ items ]
        });

        var displayComponentPanel = Ext.create('Ext.panel.Panel', {
            itemId : 'displayPanelId',
            title : i18n.get('label.formConcepts'),
            region : "center",
            flex : 2,
            autoScroll : true,
            items : this.zonesPanel,
            layout : "absolute"
        });

        /**
         * The panel that displays Property search
         * Each property adds a formField with the buildPropertyField method
         */
        this.propertyPanel = Ext.create('Ext.form.Panel', {
            itemId : 'propertyPanelId',
            title : i18n.get("label.defineProperties"),
            padding : 10,
            labelWidth : 100,
            flex : 2,
            autoScroll : true,
            defaults : {
                labelSeparator : ""
            },
            buttons : [ {
                text : i18n.get('label.refreshDatasets')
            } ]
        });
        if (!Ext.isEmpty(this.properties)) {
            Ext.each(this.properties, function (prop) {
                var field = this.buildPropertyField(prop);
                this.propertyPanel.add(field);
            }, this);
        }

        var project = Ext.getStore('ProjectStore').getProject();
        
        var storeDatasets = Ext.create('Ext.data.JsonStore', {
            autoLoad : true,
            proxy : {
                type : 'ajax',
                url : project.get('sitoolsAttachementForUsers') + this.urlServicePropertiesSearch,
                reader : {
                    type : 'json',
                    root : "collection.dataSets"
                }
            },
            fields : [{
                name : "id",
                type : "string"
            }, {
                name : "name",
                type : "string"
            }, {
                name : "visible",
                type : "boolean"
            } ],
            listeners : {
                load : function (store, recs) {
                    Ext.each(recs, function (rec) {
                        rec.set("visible", true);
                    });
                }
            }
        });

        var visible = Ext.create('Ext.grid.column.CheckColumn', {
            header : i18n.get('headers.visible'),
            dataIndex : 'visible',
            width : 55
        });

        var cmDatasets = {
            items : [ {
                header : i18n.get('headers.name'),
                dataIndex : 'name',
                width : 120
            }, visible ]
        };

        var smDatasets = Ext.create('Ext.selection.RowModel', {
            mode : 'SINGLE'
        });

        /**
         * The dataset list. 
         * It is updated when user pressed on refresh dataset button.
         */
        this.datasetPanel = Ext.create('Ext.grid.Panel', {
            title : i18n.get('label.defineDatasets'),
            store : storeDatasets,
            columns : cmDatasets,
            selModel : smDatasets,
            flex : 1,
            autoScroll : true,
            forceFit : true
        });

        var firstPanel = Ext.create('Ext.panel.Panel', {
            height : 300,
            items : [ this.propertyPanel, this.datasetPanel ],
            collapsedTitle : i18n.get('label.advancedSearch'),
            region : "north",
            collapsible : true,
            collapsed : true,
            flex : 2,
            layout : {
                type : 'hbox',
                align : 'stretch'
            }
        });
        /**
         * A simple button to launch the main request on each selected dataset. 
         */
        this.searchButton = Ext.create('Ext.button.Button', {
            itemId : 'btnSearchForm',
            text : i18n.get('label.search')
        });

        this.bbar = Ext.create('sitools.public.widget.StatusBar', {
            text : i18n.get('label.ready'),
            iconCls : 'x-status-valid',
            hidden : true
        })
        
        this.items = [ firstPanel, displayComponentPanel ];
        this.buttons = [ this.searchButton ];
        
        Ext.apply(this, {
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
        });
        
        this.callParent(arguments);
    }
});
