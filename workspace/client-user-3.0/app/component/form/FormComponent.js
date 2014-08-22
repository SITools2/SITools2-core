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
    
    init : function (form, dataset) {
        
    	var windowSettings = {
            datasetName : dataset.name,
            type : "form",
            title : i18n.get('label.forms') + " : " + dataset.name + "." + form.name,
            id : "form" + dataset.id + form.id,
            saveToolbar : true,
            iconCls : "form"
        };
        
        var view = Ext.create('sitools.user.view.component.form.FormView', {
            dataUrl : dataset.sitoolsAttachementForUsers,
            dataset : dataset,
            formId : form.id,
            id : form.id,
            formName : form.name,
            formParameters : form.parameters,
            formZones : form.zones,
            formWidth : form.width,
            formHeight : form.height,
            formCss : form.css,
            preferencesPath : "/" + dataset.name + "/forms",
            preferencesFileName : form.name,
            // searchAction : this.searchAction,
            scope : this
        });

        this.setComponentView(view);
        this.show(view, windowSettings);
    },

    /**
     * method called when trying to save preference
     * 
     * @returns
     */
    _getSettings : function () {
        return {
            preferencesPath : "/modules",
            preferencesFileName : this.id
        };

    }
});