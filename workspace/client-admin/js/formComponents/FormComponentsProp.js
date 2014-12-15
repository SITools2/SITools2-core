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
 showHelp, includeJs*/
Ext.namespace('sitools.admin.formComponents');

Ext.define('sitools.admin.formComponents.FormComponentsProp', {
    extend : 'Ext.window.Window',
	alias : 'widget.s-formComponentsprop',
    width : 700,
    height : 480,
    modal : true,
    id : ID.COMPONENT_SETUP.FORMCOMPONENTS,
    layout : 'fit',

    initComponent : function () {

        if (this.action == 'create') {
            this.title = i18n.get('label.createFormComponents');
        } else if (this.action == 'modify') {
            this.title = i18n.get('label.modifyFormComponents');
        }
        this.items = [ {
            xtype : 'panel',
            layout : 'fit',
            items : [ {
                xtype : 'form',
                border : false,
                defaults : {
                    labelWidth : 180
                },
                padding : 10,
                items : [ {
                    xtype : 'textfield',
                    name : 'id',
                    hidden : true
                }, {
                    xtype : 'textfield',
                    name : 'type',
                    fieldLabel : i18n.get('label.type'),
                    anchor : '100%',
                    allowBlank : false
                }, {
                    xtype : 'textfield',
                    name : 'componentDefaultHeight',
                    fieldLabel : i18n.get('label.defaultHeight'),
                    anchor : '100%',
                    allowBlank : false
                }, {
                    xtype : 'textfield',
                    name : 'componentDefaultWidth',
                    fieldLabel : i18n.get('label.defaultWidth'),
                    anchor : '100%',
                    allowBlank : false
                }, {
                    xtype : 'textfield',
                    name : 'jsAdminObject',
                    fieldLabel : i18n.get('label.jsAdminObject'),
                    anchor : '100%',
                    allowBlank : false
                }, {
                    xtype : 'textfield',
                    name : 'jsUserObject',
                    fieldLabel : i18n.get('label.jsUserObject'),
                    anchor : '100%',
                    allowBlank : false
                }, {
                    xtype : 'sitoolsSelectImage',
                    name : 'imageUrl',
                    fieldLabel : i18n.get('label.image'),
                    anchor : '100%',
                    allowBlank : false
                }, {
                        xtype : 'numberfield',
                        name : 'priority',
                        id : 'priorityId',
                        fieldLabel : i18n.get('label.priority'),
                        minValue : 0,
                        maxValue : 10,
                        allowDecimals : false,
                        incrementValue : 1,
                        accelerate : true,
                        anchor : "50%",
                        allowBlank : false
                    }]
            } ],
            buttons: {
                xtype: 'toolbar',
                style: 'background-color:white;',
                items: [{
                    text: i18n.get('label.ok'),
                    scope: this,
                    handler: this._onValidate
                }, {
                    text: i18n.get('label.cancel'),
                    scope: this,
                    handler: function () {
                        this.close();
                    }
                }]
            }
        } ];
        this.callParent(arguments);
    },
    onRender : function () {
        this.callParent(arguments);
        if (this.action == 'modify') {
            var f = this.down('form').getForm();
            Ext.Ajax.request({
                url : this.url,
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var data = Ext.decode(ret.responseText);
                    f.setValues(data.formComponent);
                },
                failure : alertFailure
            });
        }
    },

    _onValidate : function () {
        var frm = this.down('form').getForm();
        if (!frm.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return false;
        }
        var met = this.action == 'modify' ? 'PUT' : 'POST';
        var jsonObject = frm.getFieldValues();

        Ext.Ajax.request({
            url : this.url,
            method : met,
            scope : this,
            jsonData : jsonObject,
            success : function (ret) {
                this.store.reload();
                this.close();
            },
            failure : alertFailure
        });
    }
});

