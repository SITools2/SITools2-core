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

Ext.namespace('sitools.user.controller.modules.datasets.dataviews');

/**
 * Datasets Module : Displays All Datasets depending on datasets attached to the
 * project.
 * 
 * @class sitools.user.modules.datasetsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.datasets.mainContainer
 */
Ext.define('sitools.user.component.form.FormComponent', {
    extend : 'sitools.user.core.Component',
    
    controllers : ['sitools.user.controller.component.form.FormController'],

    config : {
        autoShow : true
    },
    /**
     * @param ComponentConfig : Object containing form description and dataset description 
     */
    init : function (componentConfig, windowConfig) {
        
        var windowSettings = {}, componentSettings = {};
        
        var form = componentConfig.form;
        var dataset = componentConfig.dataset;
        
        Ext.apply(windowSettings, windowConfig, {
            datasetName : dataset.name,
            type : "form",
            title : i18n.get('label.forms') + " : " + dataset.name + "." + form.name,
            id : "form" + dataset.id + form.id,
            saveToolbar : true,
            iconCls : "form",
            typeWindow : 'form'
        });

        componentSettings = Ext.apply(componentConfig, {
            dataUrl : dataset.sitoolsAttachementForUsers,
            dataset : dataset,
            formId : form.id,
            formName : form.name,
            formParameters : form.parameters,
            formZones : form.zones,
            formWidth : form.width,
            formHeight : form.height,
            formCss : form.css,
            preferencesPath : "/" + dataset.name + "/forms",
            preferencesFileName : form.name,
            scope : this,
            component : this
        });
        
        var view = Ext.create('sitools.user.view.component.form.FormView', componentSettings);

        this.setComponentView(view);
        if(this.getAutoShow()) {
            this.show(view, windowSettings);
        }
    },
    
    _getSettings : function () {
        var view = this.getComponentView();
        return {
            objectName : "forms", 
            dataUrl : view.dataUrl,
            dataset : view.dataset,
            formId : view.formId,
            id : view.id,
            formName : view.formName,
            formParameters : view.formParameters,
            formZones : view.formZones,
            formWidth : view.formWidth,
            formHeight : view.formHeight, 
            formCss : view.formCss, 
            datasetView : view.datasetView,
            dictionaryMappings : view.dictionaryMappings, 
            preferencesPath : view.preferencesPath, 
            preferencesFileName : view.preferencesFileName
        };
    }
});