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

Ext.namespace('sitools.user.view.component.form');

/**
 * Forms Module : Displays All Forms depending on datasets attached to the
 * project.
 * 
 * @class sitools.user.modules.formsModule
 * @extends Ext.grid.GridPanel
 * @requires sitools.user.component.forms.mainContainer
 */
Ext.define('sitools.user.view.component.form.FormContainerView', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.formContainerView',
    layout : 'fit',

    initComponent : function () {

        Ext.apply(this, {
            title: this.formName,
            id : "panelResultForm" + this.formId,
            bodyCls : this.css,
            height : this.formHeight,
            width : this.width,
            layout : "form",
            labelWidth : 100,
            autoHeight : true,
            padding : 10,
            items : [],

            /** The parameters retrieved from server. * */
            parameters : []
        });
        
        this.addEvents(
        /**
         * @event added Fires when a parameter changes the value
         * @param formContainer :
         *            this
         * @param componentChanged :
         *            the component that triggered the event
         */
        'componentChanged');

//        this.on('componentChanged', function (formContainer, componentChanged) {
//            // look for all the childrens of the component
//            var childrens = formContainer.find("parentParam", componentChanged.parameterId);
//            // For each children, add a query string on the componentChanged
//            // value and reset children Value.
//            // Also, fire the event ComponentChanged for the children to cascade
//            // changes.
//            Ext.each(childrens, function (children) {
//                if (children.valueSelection == 'D') {
//                    var store = children.find("stype", "sitoolsFormItem")[0].store;
//
//                    var baseParams = store.baseParams;
//
//                    if (!Ext.isEmpty(componentChanged.getSelectedValue())) {
//                        var filter = componentChanged.getParameterValue();
//                        baseParams["p[0]"] = this.paramToAPI(filter);
//                    } else {
//                        baseParams["p[0]"] = null;
//                    }
//                    store.baseParams = baseParams;
//                    children.setSelectedValue(null);
//                    store.reload({
//                        callback : function () {
//                            formContainer.fireEvent('componentChanged', formContainer, children);
//                        }
//                    });
//
//                }
//            }, this);
//        });
        
        this.callParent(arguments);
    },
    
    /**
     * Construct a container for each parameter
     * @param {Array} parameters array of parameters
     * @param {string} dataUrl the Url to request the data
     * @param {string} context the context should be "dataset" or "project"
     */
    loadParameters : function (parameters, dataUrl, context) {
        
        if (!Ext.isEmpty(parameters.formZones)) {
            Ext.each(parameters.formZones, function (zone) {
                var zoneFieldset = Ext.create('sitools.public.ux.form.ToolFieldSet', {
                    title : (!Ext.isEmpty(zone.title) ? zone.title : zone.id),
                    itemId : zone.id,
                    height : zone.height,
                    width : this.formWidth,
                    position : zone.position,
                    style : 'background-color:#FCFCFC;',
                    cls : zone.css,
                    animCollapse : true,
                    collapsible: zone.collapsible,
                    isCollapsed : zone.collapsed,
                    formId : this.formId,
                    datasetCm : this.datasetCm,
                    layout : 'absolute',
                    listeners : {
                        render : function (fieldset) {
                            if (fieldset.isCollapsed){
                                fieldset.collapse(true);
                            }
                        }
                    }
                });
                
                Ext.each(zone.params, function (param){
                    var y = Ext.isEmpty(param.ypos) ? y + 50 : param.ypos;
                    var x = Ext.isEmpty(param.xpos) ? x : param.xpos;
                    var containerItems = [ sitools.user.utils.FormUtils.formParameterToComponent(param, dataUrl, this.formId, this.datasetCm, context, this).component];

                    var container = Ext.create('Ext.container.Container', {
                        width : param.width,
                        height : param.height,
                        x : x,
                        y : y,
                        bodyCls : "noborder",
                        cls : param.css,
                        items : containerItems
                    });
                    zoneFieldset.add(container);
                    
                }, this);
                
                this.add(zoneFieldset);
                
            }, this);
        } else {
//            this.layout = 'absolute';
            Ext.each(parameters.oldParameters, function (parameter) {
                var y = Ext.isEmpty(parameter.ypos) ? y + 50 : parameter.ypos;
                var x = Ext.isEmpty(parameter.xpos) ? x : parameter.xpos;
                
                var containerItems = [ sitools.user.utils.FormUtils.formParameterToComponent(parameter, dataUrl, this.formId, this.datasetCm, context, this).component];
                
                var container = Ext.create('Ext.container.Container', {
                    width : parameter.width,
                    height : parameter.height,
                    x : x,
                    y : y,
                    bodyCls : "noborder",
                    cls : parameter.css,
                    items : containerItems
                });
                this.add(container);
            }, this);
        }
    }, 
    paramToAPI : function (paramValue) {
        var stringParam = paramValue.type + "|" + paramValue.code + "|" + paramValue.value;
        if (!Ext.isEmpty(paramValue.userDimension) && !Ext.isEmpty(paramValue.userUnit)) {
            stringParam += "|" + paramValue.userDimension + "|" + paramValue.userUnit.unitName; 
        }  
        return stringParam;
    },
    /**
     * Check that all the components are valid
     * @return {boolean} true if all components are valid, false otherwise
     */
    isComponentsValid : function () {
        var valid = true;
        var containers = this.down('[stype="sitoolsFormContainer"]');
        Ext.each(containers, function (container) {
            if(Ext.isFunction(container.isValid)) {
                if(!container.isValid()){
                    valid = false;
                    return;
                }
            }
        });
        return valid;
    }
});
