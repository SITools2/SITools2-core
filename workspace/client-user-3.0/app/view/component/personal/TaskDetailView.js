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
/*global Ext, sitools, i18n, alert, showResponse, alertFailure, SitoolsDesk, window, userLogin, loadUrl, DEFAULT_WIN_WIDTH, DEFAULT_WIN_HEIGHT */

Ext.namespace('sitools.user.component.entete.userProfile');

/**
 * @class sitools.user.component.entete.userProfile.tasksDetails
 * @extends Ext.FormPanel
 */
Ext.define('sitools.user.view.component.personal.TaskDetailView', {
	extend : 'Ext.form.Panel',
	alias : 'widget.taskDetailView',
    
    flex : 1,
    border : false,
    padding : 15,
    
	initComponent : function () {
        
		this.fieldDefaults = {
            labelWidth : 130,
            disabled : true,
            disabledCls : 'x-item-disabled-custom',
            labelStyle : 'font-weight:bold;',
            anchor : '100%',
            htmlEncode : true
		};
		
        var itemsForm = [{
        	xtype : 'textfield',
        	name : i18n.get('label.userId'),
        	fieldLabel : i18n.get('label.userId'),
        	value : this.task.userId
        }, {
        	xtype : 'textfield',
        	name : i18n.get('label.statusUrl'),
        	fieldLabel : i18n.get('label.statusUrl'),
        	value : this.task.statusUrl,
        	fieldCls : 'linkStyle',
        	hidden : (Ext.isEmpty(this.task.statusUrl))
        }, {
        	xtype : 'textfield',
            itemId : 'urlResult',
        	name : i18n.get('label.task.urlResult'),
        	fieldLabel : i18n.get('label.urlResult'),
        	value : this.task.urlResult,
        	fieldCls : 'linkStyle',
        	hidden : (Ext.isEmpty(this.task.urlResult)),
        	listeners: {
        		afterrender : function( component ) {
	                component.inputEl.on('click', function( event, el ) {
                       var textfield = Ext.ComponentQuery.query('textfield#urlResult');
	                   var value = textfield.getValue();
                        window.open(value, '_blank');
	                });
	            }
            }
        }];
        
//        Ext.iterate(this.task, function (key, value) {
//            if (value != undefined && value != ""){
//                itemsForm.push({
//                    xtype : 'textfield',
//                    name : key,
//                    labelWidth : 130,
//                    fieldLabel : i18n.get('label.' + key),
//                    disabled : true,
//                    disabledCls : 'x-item-disabled-custom',
//                    labelStyle : 'font-weight:bold;',
//                    anchor : '100%',
//                    value : value                
//                });
//            }
//        });
        
		this.items = itemsForm;
		
		this.callParent(arguments);
	},
	
	openLink : function (textfield) {
		var value = textfield.getValue();
		window.open(value, '_blank');
	}
});
