/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl*/
/*
 * @include "absoluteLayoutProp.js"
 * @include "componentsListPanel.js"
 */
Ext.namespace('sitools.admin.forms');

/**
 * A window that contains a definition of Form Component. 
 * @cfg {string} collectionId the collectionId 
 * @cfg {Ext.data.Record} record The record to edit.
 * @cfg {} xyOnCreate An object containing xy position to create the new component.
 * @class sitools.admin.forms.componentPropPanel
 * @extends Ext.Window
 */
Ext.define('sitools.admin.forms.componentPropPanel', { 
    extend : 'Ext.Window',
    modal : true,
    pageSize : 10,

    initComponent : function () {
        this.title = i18n.get('label.componentProperties');
        var specificComponentString, config = {}, JsObj;
        if (this.action == 'modify') {
            var rec = this.record;
            if (!rec) {
                return Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noselection'));
            }
            JsObj = eval(rec.data.jsAdminObject);
            config = {
				datasetColumnModel : this.datasetColumnModel,
				winPropComponent : this,
				action : this.action, 
				selectedRecord : rec, 
				jsUserObject : rec.data.jsUserObject, 
				ctype : rec.data.type, 
				componentDefaultHeight : rec.data.height, 
				componentDefaultWidth : rec.data.width, 
				dimensionId : rec.data.dimensionId, 
				unit : rec.data.unit, 
				extraParams : rec.data.extraParams, 
				context : this.context, 
				storeConcepts : this.storeConcepts, 
				formComponentsStore : this.formComponentsStore,
				containerPanelId : this.containerPanelId
            };
        } else {
            JsObj = eval(this.jsAdminObject);
            config = {
				datasetColumnModel : this.datasetColumnModel,
				winPropComponent : this,
				action : this.action, 
				ctype : this.ctype, 
				jsAdminObject : this.jsAdminObject, 
				jsUserObject : this.jsUserObject, 
				componentDefaultHeight : this.componentDefaultHeight, 
				componentDefaultWidth : this.componentDefaultWidth, 
				extraParams : this.extraParams, 
				context : this.context, 
				storeConcepts : this.storeConcepts, 
				formComponentsStore : this.formComponentsStore, 
				xyOnCreate : this.xyOnCreate,
				containerPanelId : this.containerPanelId
            };
        }
        
        var specificComponent = new JsObj(config);
        this.componentPropPanel = new Ext.Panel({
            layout : 'fit',
            padding : 10,
            autoScroll : true,
            items : [ specificComponent ],
            buttons : [ {
                text : i18n.get('label.addColumn'),
                hidden : !Ext.isFunction(specificComponent.addColumn),
                handler : function () {
                    specificComponent.addColumn();
                }
            }, {
                text : i18n.get('label.ok'),
                scope : this,
                handler : this.onValidate
            }, {
                text : i18n.get('label.cancel'),
                scope : this,
                handler : this._close
            } ]
        });

        this.items = [ this.componentPropPanel ];
        sitools.admin.forms.componentPropPanel.superclass.initComponent.call(this);
    },
    afterRender : function () {
        this.height = this.specificHeight;
        this.width = this.specificWidth;
        sitools.admin.forms.componentPropPanel.superclass.afterRender.call(this);
    },

    onValidate : function () {
        var component = this.findById('sitools.component.forms.definitionId');
        if (component._onValidate(this.action, this.formComponentsStore)) {
            this.close();
        }
		this.absoluteLayout.fireEvent("activate");
        
    },
    _close : function () {
        this.close();
    }

});
