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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp*/
/*
 * @include "../ComponentFactory.js"
 */
Ext.namespace('sitools.admin.forms.componentsAdminDef.oneParam');

/**
 * An abstract form to build one Param components
 * @class sitools.admin.forms.componentsAdminDef.oneParam.Abstract
 * @extends Ext.form.FormPanel
 */
Ext.define('sitools.admin.forms.componentsAdminDef.oneParam.Abstract', {
    extend : 'sitools.admin.forms.componentsAdminDef.Abstract',
    initComponent : function () {
        this.callParent(arguments);
        this.context = sitools.admin.forms.componentsAdminDef.ComponentFactory.getContext(this.context);
        this.buttonAdd = Ext.create("Ext.Button", {
			text : i18n.get('label.addColumn'), 
			scope : this, 
			handler : function (button) {
				this.addColumn(button);
			}
		});
		this.buttonDel = Ext.create("Ext.Button", {
			text : i18n.get('label.deleteColumn'), 
			scope : this, 
			handler : function (button) {
				this.deleteColumn(button);
			}
		});
        this.labelParam1 = Ext.create("Ext.form.TextField", {
            fieldLabel : i18n.get('label.label'),
            name : 'LABEL_PARAM1',
            anchor : '100%', 
            tooltip : i18n.get("label.formLabel")
        });
		
		/**
		 * The combo that contains the list of Columns or shared Concepts. 
		 * it is required to build this mapParam1 for all forms components. 
		 */
		this.mapParam1 = this.context.buildComboParam1(this);
		
        
	    this.css = Ext.create("Ext.form.TextField", {
            fieldLabel : i18n.get('label.css'),
            name : 'CSS',
            anchor : '100%'
        });
        this.componentDefaultHeight = Ext.create("Ext.form.TextField", {
            fieldLabel : i18n.get('label.height'),
            name : 'componentDefaultHeight',
            anchor : '100%',
            value : this.componentDefaultHeight, 
            allowBlank : false
        });
        this.componentDefaultWidth = Ext.create("Ext.form.TextField", {
            fieldLabel : i18n.get('label.width'),
            name : 'componentDefaultWidth',
            anchor : '100%',
            value : this.componentDefaultWidth, 
            allowBlank : false
        });
        this.add([this.labelParam1, this.mapParam1, this.css]);
		if (this.action == "create") {
            this.add([this.componentDefaultHeight, this.componentDefaultWidth]);
		}
    },
    afterRender : function () {
        this.callParent(arguments);
        if (this.action == 'modify') {
            this.labelParam1.setValue(this.selectedRecord.data.label);
            this.css.setValue(this.selectedRecord.data.css);
            var codes = this.selectedRecord.data.code;

            this.mapParam1.setValue(this.selectedRecord.data.code[0]);
        }
        
    },
    _onValidate : function (action, formComponentsStore) {
        /**
         * Chaque classe étandant cet objet doit redéfinir cette méthode
         */
        Ext.Msg.alert(i18n.get('label.warning'), i18n.get('msg.OnvalidateNotDefined'));
    }
});
