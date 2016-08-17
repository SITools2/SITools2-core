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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl, sql2ext, sitoolsUtils*/

/**
 * Dataset Overview controller
 *
 * @class sitools.user.controller.component.datasets.overview.OverviewController
 * @extends Ext.app.Controller
 */
Ext.define('sitools.user.controller.component.datasets.overview.OverviewController', {
    extend: 'Ext.app.Controller',

    views: ['component.datasets.overview.OverviewView'],

    requires: [],

    init: function () {
        this.control({

            "overviewView": {
                afterrender: function (overviewView) {
                    if(overviewView.getForceShowDataset()){
                        this.displayDatasetWithoutForm(overviewView);
                    }
                    else {
                        this.loadForms(overviewView);
                    }
                },

                formsLoaded: function (overviewView, forms) {
                    if (Ext.isEmpty(forms)) {
                        this.displayDatasetWithoutForm(overviewView);
                    }
                    else {
                        this.displayDatasetWithForm(overviewView, forms);
                    }
                }
            },
            "overviewView tabpanel#tabPanel" : {
            	tabchange : function ( tabPanel, newCard, oldCard, eOpts ) {
            		this.fitCurrentFormWidth(tabPanel.up("overviewView"));
            	}
            	
            } 
        });
    },

    loadForms: function (overviewView) {

        var dataset = overviewView.getDataset();

        Ext.Ajax.request({
            method: "GET",
            scope: this,
            url: dataset.sitoolsAttachementForUsers + '/forms',
            success: function (response) {
                var json = Ext.decode(response.responseText);
                var eventToFire = 'formsLoaded';

                if (!json.success) {
                    Ext.Msg.alert(i18n.get('label.error'), i18n
                        .get('warning.datasetRequestError'));
                    return false;
                } else {
                    var forms = json.data;
                    overviewView.fireEvent("formsLoaded", overviewView, forms);
                }
            },
            failure: alertFailure
        });
    },


    displayDatasetWithForm : function (overviewView, forms) {
        this.displayForms(overviewView, forms);
        this.displayDescription(overviewView, overviewView.down("container#mainPanel"));

        overviewView.down("container#eastPanel").setVisible(false);
        overviewView.down("container#mainPanel").setVisible(true);
        overviewView.down("container#westPanel").setVisible(true);

        overviewView.down("splitter#eastSplitter").setVisible(false);
        overviewView.down("splitter#westSplitter").setVisible(true);
        
        this.fitCurrentFormWidth(overviewView);
    },

    displayDatasetWithoutForm: function (overviewView) {
        this.displaySemantic(overviewView);
        this.displayDescription(overviewView, overviewView.down("container#eastPanel"));
        this.displayDataset(overviewView);

        overviewView.down("container#westPanel").setVisible(false);

        overviewView.down("splitter#eastSplitter").setVisible(true);
        overviewView.down("splitter#westSplitter").setVisible(false);

    },

    displayForms : function (overviewView, forms) {
        var dataset = overviewView.getDataset();

        var tabPanel = Ext.create("Ext.tab.Panel", {
            layout : 'fit',
            flex : 1,
            itemId : 'tabPanel'
        });

        //if a form is given as a parameter we must show it
        var formParamId = overviewView.getFormId();
        var activeFormIndex = -1;
        Ext.each(forms, function(form, index) {

            var componentConfig = {
                dataset: dataset,
                form: form,
                preferencesPath: "/" + dataset.name + "/forms",
                preferencesFileName: form.name,
                title : form.name,
                searchAction : Ext.bind(this.searchAction, this)
            };

            var component = Ext.create("sitools.user.component.form.FormComponent", {
                autoShow: false
            });

            component.create(Desktop.getApplication(), function () {
                component.init(componentConfig, {});

                var formCmp = component.getComponentView();
                tabPanel.add(formCmp);

                if(!Ext.isEmpty(formParamId) && form.id === formParamId) {
                    activeFormIndex = index;
                }
            }, component);
        }, this);

        if(activeFormIndex === -1) {
            activeFormIndex = 0;
            if (!Ext.isEmpty(formParamId)) {
                popupMessage({
                    buttons: Ext.MessageBox.OK,
                    icon: Ext.MessageBox.INFO,
                    modal: true,
                    closable: false,
                    title: i18n.get('label.info'),
                    msg: i18n.get('label.cannotFindFormIdDisplayFirstOne')
                });
            }
        } else {

        }

        tabPanel.setActiveTab(activeFormIndex);

        var westPanel = overviewView.down("container#westPanel");
        westPanel.removeAll();
        westPanel.add(tabPanel);

    },



    displaySemantic: function (overviewView) {

        var dataset = overviewView.getDataset();

        var config = {
            datasetId: dataset.id,
            datasetDescription: dataset.description,
            datasetCm: dataset.columnModel,
            datasetName: dataset.name,
            dictionaryMappings: dataset.dictionaryMappings,
            preferencesPath: "/" + dataset.name,
            preferencesFileName: "semantic"
        };

        var semantic = Ext.create("sitools.user.view.component.datasets.columnsDefinition.ColumnsDefinitionView", config);

        var semanticPanel = overviewView.down("container#semantic");
        semanticPanel.removeAll();
        semanticPanel.add(semantic);
        semanticPanel.setVisible(true);
    },

    displayDescription: function (overviewView, parentPanel) {
        var dataset = overviewView.getDataset();

        if (!Ext.isEmpty(dataset.descriptionHTML)) {

            var descriptionPanel = Ext.create("Ext.panel.Panel", {
                title: i18n.get('label.description'),
                autoScroll: true,
                layout: "fit",
                itemId : 'description',
                flex : 1,
                html : dataset.descriptionHTML
            });
            parentPanel.insert(0, descriptionPanel);
        } else {
            overviewView.down("container#semantic").flex = 1;
            overviewView.down("container#semantic").setHeight(null);
        }
    },

    displayDataset: function (overviewView, config) {
        var dataset = overviewView.getDataset();
        var parentPanel = overviewView.down("container#mainPanel");

        var javascriptObject = dataset.datasetView.jsObject;

        var componentConfig = (Ext.isEmpty(config)) ? {} : config;

        componentConfig = Ext.apply(componentConfig, {
            dataset: dataset,
            preferencesPath: "/" + dataset.name,
            preferencesFileName: "datasetOverview",
            title: dataset.name
        });

        if (overviewView.getForceShowDataset()) {
            componentConfig = Ext.apply(componentConfig, overviewView.getOutsideConfig());
        }

        var component = Ext.create(javascriptObject, {
            autoShow: false
        });

        component.create(Desktop.getApplication(), function () {
            component.init(componentConfig, {});

            var dataview = component.getComponentView();
            parentPanel.removeAll();
            parentPanel.add(dataview);

        }, component);

    },

    searchAction : function(formParams, dataset, formView) {
        var config = {
            formFilters : formParams
        };
        var overviewView = formView.up("overviewView");
        this.displayDataset(overviewView, config);

        overviewView.down("container#mainPanel").setVisible(true);
        overviewView.down("container#eastPanel").setVisible(false);

        overviewView.down("splitter#eastSplitter").setVisible(false);
        overviewView.down("splitter#westSplitter").setVisible(true);
    },
    
    fitCurrentFormWidth : function(overviewView) {
    	var tabPanel = overviewView.down("tabpanel#tabPanel");
		overviewView.down("container#westPanel").setWidth(tabPanel.getActiveTab().form.width + 30);
    }
});