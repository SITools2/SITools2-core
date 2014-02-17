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
Ext.namespace('sitools.admin.projects.modules');

/**
 * A Panel to show project module parameters which are configurable for a specific project
 * 
 * @class sitools.admin.projects.modules.ProjectModuleConfig
 * @extends Ext.Window
 */
Ext.define('sitools.admin.projects.modules.ProjectModuleConfig', { extend : 'Ext.Window',
	alias : 'widget.s-projectmoduleconfig',
    width : 500,
    height : 400,
    modal : true,
    id : ID.BOX.PROJECTMODULECONFIG,
    layout : 'fit',
    initComponent : function () {
		
        this.buttons = [ {
            text : i18n.get('label.ok'),
            id : "btnValidateId",
            handler : this._onValidate,
            scope : this
        }, {
            text : i18n.get('label.cancel'),
            handler : function () {
                this.close();
            },
            scope : this
        } ];
        
        this.formPanel = new sitools.admin.common.FormParametersConfigUtil({
			rec : this.module.data,
			parametersList : this.module.data.listProjectModulesConfig,
			parametersFieldName : 'listProjectModulesConfig'
		});
		
		
        this.items = [this.formPanel];
        
        sitools.admin.projects.modules.ProjectModuleConfig.superclass.initComponent.call(this);
    },
    
    _onValidate : function () {
        if (this.formPanel.getForm().isValid()){
            this.module.set( 'listProjectModulesConfig', this.formPanel.getParametersValue());
            this.close();
        } 
        else {
            Ext.getCmp('bbarFormParam').setStatus({
                text : i18n.get('label.checkformvalue'),
                iconCls : 'x-status-error'
            });
        }
	}
    
});

