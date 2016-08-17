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
    extend: 'Ext.app.Controller',

    views: ['component.form.FormView',
        'component.form.FormContainerView'],

    config: {
        listFormView: Ext.create('Ext.util.MixedCollection')
    },

    init: function () {
        this.control({
            'formContainerView': {
                scope : this,
                componentChanged: this.componentChanged,
                afterrender: this.afterRenderFormContainer
            },

            'formsView': {
                scope : this,
                resize: this.resizeForm,
                render: function (formPanel) {
                    formPanel.getEl().on('keyup', function (e) {
                        if (e.getKey() == e.ENTER) {
                            var formsView = Ext.ComponentQuery.query('formsView#' + e.currentTarget.id)[0];
                            this.onSearch(formsView);
                        }
                    }, this);
                },
                boxready : function (formPanel) {
                    formPanel.focus();
                }
            },

            'formsView button#btnSearchForm': {
                scope : this,
                click: function (btn) {
                    var formsView = btn.up('formsView');
                    this.onSearch(formsView);
                }
            },

            'formsView button#resetSearchForm': {
                click: this.resetSearchForm
            }
        });
    },

    componentChanged: function (formContainer, componentChanged) {
        // look for all the childrens of the component
        var childrens = Ext.ComponentQuery.query(Ext.String.format("component[parentParam={0}]", componentChanged.parameterId), formContainer);
        // For each children, add a query string on the componentChanged
        // value and reset children Value.
        // Also, fire the event ComponentChanged for the children to cascade
        // changes.
        Ext.each(childrens, function (children) {
            if (children.valueSelection == 'D') {
                var cmp = children.down("component[stype=sitoolsFormItem]");
                var store = cmp.getStore();
                var proxy = store.getProxy();

                if (!Ext.isEmpty(componentChanged.getSelectedValue())) {
                    var filter = componentChanged.getParameterValue();
                    proxy.setExtraParam("p[0]", this.paramToAPI(filter));
                } else {
                    proxy.setExtraParam("p[0]", null);
                }
                children.setSelectedValue(null);
                store.load({
                    callback: function () {
                        formContainer.fireEvent('componentChanged', formContainer, children);
                    }
                });

            }
        }, this);
    },

    paramToAPI: function (paramValue) {
        var stringParam = paramValue.type + "|" + paramValue.code + "|" + paramValue.value;
        if (!Ext.isEmpty(paramValue.userDimension) && !Ext.isEmpty(paramValue.userUnit)) {
            stringParam += "|" + paramValue.userDimension + "|" + paramValue.userUnit.unitName;
        }
        return stringParam;
    },

    afterRenderFormContainer: function (formContainer) {
    },

    resizeForm: function (form) {
    },

    resetSearchForm: function (btn) {
        var view = btn.up('formsView');
        var containers = view.query('[stype="sitoolsFormContainer"]');
        Ext.each(containers, function (container) {
            if (Ext.isFunction(container.resetToDefault)) {
                container.resetToDefault();
            }
        }, this);
    },

    onSearch: function (me) {

        var valid = true;

        var cmpList = Ext.ComponentQuery.query("formContainerView", me);

        Ext.each(cmpList, function (componentList) {
            valid = valid && componentList.isComponentsValid();
        }, this);

        if (!valid) {
            me.down('toolbar#formStatusBar').setStatus({
                text: i18n.get('label.checkformvalue'),
                iconCls: 'x-status-error'
            });
            me.down('toolbar#formStatusBar').setVisible(true);
            return;
        } else {
            me.down('toolbar#formStatusBar').setVisible(false);
        }
        //Execute a request to get the dataset config 
        Ext.Ajax.request({
            url: me.dataUrl,
            method: "GET",
            scope: this,
            success: function (ret) {
                var Json = Ext.decode(ret.responseText);
                if (!Json.success) {
                    Ext.Msg.alert(i18n.get('label.warning'), Json.message);
                    return;
                } else {
                    var dataset = Json.dataset;
                    this.doSearch(me, dataset);
                }
            },
            failure: alertFailure
        });
    },

    /**
     * Build the query for the liveGrid and build the livegrid component
     * @param config
     * @returns
     */
    doSearch: function (formView, dataset) {
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

        if (Ext.isFunction(formView.searchAction)) {
            formView.searchAction(formParams, dataset, formView);
        }
        else {
            this.defaultSearchAction(formParams, dataset);
        }

    },

    defaultSearchAction: function (formParams, dataset) {
        var componentConfig = {
            formFilters: formParams,
            dataset: dataset
        };

        sitools.user.utils.DatasetUtils.showDataset(dataset, componentConfig);
    },

    _getSettings: function () {
        return {
            objectName: "forms",
            dataUrl: this.dataUrl,
            dataset: this.dataset,
            formId: this.formId,
            formName: this.formName,
            formParameters: this.formParameters,
            formWidth: this.formWidth,
            formHeight: this.formHeight,
            formCss: this.formCss,
            datasetView: this.datasetView,
            dictionaryMappings: this.dictionaryMappings,
            preferencesPath: this.preferencesPath,
            preferencesFileName: this.preferencesFileName
        };
    },
    /**
     * Build a string using a form param Value.
     * @param {} paramValue An object with attributes : at least type, code, value and optionnal userDimension, userUnit
     * @return {string} something like "TEXTFIELD|ColumnAlias|value"
     */
    paramValueToApi: function (paramValue) {
        var stringParam = paramValue.type + "|" + paramValue.code + "|" + paramValue.value;
        if (!Ext.isEmpty(paramValue.userDimension) && !Ext.isEmpty(paramValue.userUnit)) {
            stringParam += "|" + paramValue.userDimension + "|" + paramValue.userUnit.unitName;
        }
        return stringParam;
    }

});
