/***************************************
* Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl 
 showHelp*/
/*
 * @include "../ComponentFactory.js"
 * @include "Abstract.js"
 * @include "../ComponentFactory.js"
 * @include "../DatasetContext.js"
 * @include "../ProjectContext.js"
 */
Ext.namespace('sitools.admin.forms.componentsAdminDef.oneParam');

/**
 * Another Abstract formPanel to define one param components with unit. 
 * @class sitools.admin.forms.componentsAdminDef.oneParam.AbstractWithUnit
 * @extends sitools.admin.forms.componentsAdminDef.oneParam.Abstract
 */
Ext.define('sitools.admin.forms.componentsAdminDef.oneParam.AbstractWithUnit', { 
    extend : 'sitools.admin.forms.componentsAdminDef.oneParam.Abstract',
    
    initComponent : function () {
        sitools.admin.forms.componentsAdminDef.oneParam.AbstractWithUnit.superclass.initComponent.call(this);
        this.storeDimension = Ext.create("Ext.data.JsonStore", {
            proxy : {
                type : 'ajax',
                url : loadUrl.get('APP_URL') + loadUrl.get('APP_DIMENSIONS_ADMIN_URL') + '/dimension',
                reader : {
                    type : 'json',
                    root : "data" 
                }                
            },
            fields : [{
                name : "id", 
                type : "string"
            }, {
                name : "name", 
                type : "string"
            }, {
                name : "description", 
                type : "string"
            }, {
                name : "dimensionHelperName", 
                type : "string"
            }], 
            autoLoad : false, 
            listeners : {
                scope : this, 
                load : this._onDimensionLoad
            }
        }); 
        this.on("beforerender", this.onBeforeRender, this);
        this.mapParam1.on("select", this.onChangeColumn, this);
        
    },
    onBeforeRender : function () {
        if (!this.loaded) {
            this.storeDimension.load();
        }    
    },
    _onDimensionLoad : function () {
        this.dimension = Ext.create("Ext.form.ComboBox", {
            fieldLabel : i18n.get('label.dimension'),
            store : this.storeDimension,
            displayField : "name",
            queryMode : 'local',
            forceSelection : false,
            triggerAction : 'all',
            emptyText : i18n.get('label.selectDimension'),
            selectOnFocus : true,
            valueField : "id",
            name : 'dimensionId',
            anchor : '100%', 
            disabled : true, 
            value : Ext.isEmpty(this.selectedRecord) ? null : this.selectedRecord.data.dimensionId
        });
		//determine if the dimension must be active
        this.context.activeDimension.call(this);
        
        this.add(this.dimension);
        this.doLayout();
        
        this.context.buildUnit.call(this);
    }, 
    onChangeColumn : function () {
		this.context.onChangeColumn.call(this);
    }
});
