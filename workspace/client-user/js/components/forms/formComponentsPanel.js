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
 * @include "../../../../client-public/js/forms/formParameterToComponent.js"
 */
Ext.namespace('sitools.user.component');

/**
 * The container (absolute Layout) that contains all the containers with each parameter
 * @cfg {number} width The width Form
 * @cfg {number} height the Height Form
 * @cfg {string} css A specific Css Class
 * @cfg {string} formId the Form Id
 * @cfg {Ext.grid.ColumnModel} datasetCm The dataset ColumnModel
 * @class sitools.user.component.formComponentsPanel
 * @extends Ext.Panel
 */
sitools.user.component.formComponentsPanel = Ext.extend(Ext.Panel, {
//sitools.component.users.datasets.formsContainer = Ext.extend(Ext.Panel, {
    
    initComponent : function () {
//        this.indexes = new Array();
//        this.loadParameters (this.formParameters,
//        this.formParameters[0].code);

        Ext.apply(this, {
//            title: this.formName,
//            id : "panelResultForm" + this.formId, 
            bodyCls : this.css,
            height : this.height,
            width : this.width,
//            border : false,
//            bodyBorder : false,
//            collapsible: true,
            layout : "form",
            labelWidth : 100,
            autoHeight : true,
//            width:600,
            padding : 10,
            items : [],

            /** The parameters retrieved from server. * */
            parameters : []
        });
        this.addEvents(
        /**
         * @event added
         * Fires when a parameter changes the value
         * @param formContainer : this
         * @param componentChanged : the component that triggered the event
         */
            'componentChanged'
        );
        
        this.on('componentChanged', function (formContainer, componentChanged) {
            //look for all the childrens of the component 
            var childrens = formContainer.find("parentParam", componentChanged.parameterId);
            //For each children, add a query string on the componentChanged value and reset children Value. 
            //Also, fire the event ComponentChanged for the children to cascade changes. 
            Ext.each(childrens, function (children) {
                if (children.valueSelection == 'D') {
                    var store = children.find("stype",  "sitoolsFormItem")[0].store;
                
                    var baseParams = store.baseParams;
                    
                    if (!Ext.isEmpty(componentChanged.getSelectedValue())) {
                        var filter = componentChanged.getParameterValue();
                        baseParams["p[0]"] = this.paramToAPI(filter);
                    }
                    else {
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
        });
        this.listeners = {
            scope : this,
            afterrender : function () {
                try {
	                var cmpChildSize = this.getSize();
	                var size = this.ownerCt.ownerCt.body.getSize();
	                var xpos = 0, ypos = 0;
	                if (size.height > cmpChildSize.height) {
	                    ypos = (size.height - cmpChildSize.height) / 2;
	                }
	                if (size.width > cmpChildSize.width) {
	                    xpos = (size.width - cmpChildSize.width) / 2;
	                }
	                this.setPosition(xpos, ypos);
                }
                catch (err) {
                	return;
                }
                this.doLayout();
            }
        };
        sitools.user.component.formComponentsPanel.superclass.initComponent.apply(this, arguments);
    },
    /**
     * Construct a container for each parameter
     * @param {Array} parameters array of parameters
     * @param {string} dataUrl the Url to request the data
     * @param {string} context the context should be "dataset" or "project"
     */
    loadParameters : function (parameters, dataUrl, context) {
        
        if (!Ext.isEmpty(parameters.formZones)){
            Ext.each(parameters.formZones, function(zone){
                var zoneFieldset = new Ext.form.FieldSet({
                    title : (!Ext.isEmpty(zone.title) ? zone.title : zone.id),
                    itemId : zone.id,
                    height : zone.height,
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
                    var containerItems = [ sitools.common.forms.formParameterToComponent(param, dataUrl, this.formId, this.datasetCm, context, this).component];

                    var container = new Ext.Container({
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
                
                var containerItems = [ sitools.common.forms.formParameterToComponent(parameter, dataUrl, this.formId, this.datasetCm, context, this).component];
                
                var container = new Ext.Container({
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
        var containers = this.find("stype", 'sitoolsFormContainer');
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

Ext.reg('sitools.user.component.formComponentsPanel', sitools.user.component.formComponentsPanel);
