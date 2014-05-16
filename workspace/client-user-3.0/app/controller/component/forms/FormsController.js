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

Ext.namespace('sitools.user.controller.modules.forms');

/**
 * Forms Module : Displays All Forms depending on datasets attached to the
 * project.
 * 
 * @class sitools.user.modules.formsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.forms.mainContainer
 */
Ext.define('sitools.user.controller.component.forms.FormsController', {
    extend : 'sitools.user.controller.component.ComponentController',
    
    views : ['component.forms.FormsView',
             'component.forms.FormContainerView'],
    

    init : function () {
        this.control({
            'formContainerView' : {
                componentChanged : this.componentChanged,
                afterrender : this.afterRenderFormContainer
            },
            
            'formsView' : {
                resize : this.resizeForm
            }
        });
    },
    

        openForm : function (form, dataset) {
        // var jsObj = SitoolsDesk.navProfile.getFormOpenMode();
        //      
        // var componentCfg = {
        // dataUrl : dataset.sitoolsAttachementForUsers,
        // dataset : dataset,
        // formId : rec.data.id,
        // formName : rec.data.name,
        // formParameters : rec.data.parameters,
        // formWidth : rec.data.width,
        // formHeight : rec.data.height,
        // formCss : rec.data.css,
        // preferencesPath : "/" + dataset.name + "/forms",
        // preferencesFileName : rec.data.name,
        // formZones : rec.data.zones
        // };
        //
        // SitoolsDesk.navProfile.addSpecificFormParameters(componentCfg,
        // dataset);
        //      
        // SitoolsDesk.addDesktopWindow(windowSettings, componentCfg, jsObj);
            
        var windowSettings = {
            datasetName : dataset.name,
            type : "form",
            title : i18n.get('label.forms') + " : " + dataset.name + "." + form.name,
            id : "form" + dataset.id + form.id,
            saveToolbar : true,
            iconCls : "form"
        };
        var view = Ext.create('sitools.user.view.component.forms.FormsView', {
            title : form.name,
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
        this.open(view, windowSettings);
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
