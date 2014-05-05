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
Ext.namespace('sitools.admin.order');

Ext.define('sitools.admin.order.events', { 
    extend : 'Ext.Window',
    width : 350,
    height : 250,
    modal : true,
    closable : true,
    baseUrl : this.baseUrl,
    layout : 'fit',

    initComponent : function () {
        
        this.title = i18n.get('label.orders');
        
        this.items = [ {
            xtype : 'panel',
            height : 200,
            items : [ {
                xtype : 'form',
                border : false,
                padding : 10,
                items : [ {
                    xtype : 'textarea',
                    name : 'description',
                    fieldLabel : i18n.get('label.description'),
                    anchor : '100%',
                    maxLength : 100
                }, {
                    xtype : 'textarea',
                    name : 'message',
                    fieldLabel : i18n.get('label.message'),
                    anchor : '100%',
                    maxLength : 100
                }]
            }]
        }];
        
        this.buttons = [{
            text : i18n.get('label.ok'),
            scope : this,
            handler : this._onValidate
        }, {
            text : i18n.get('label.cancel'),
            scope : this,
            handler : function () {
                this.close();
            }
        }];

        sitools.admin.order.events.superclass.initComponent.call(this);

    },
    _onValidate : function () {
        var url = this.baseUrl + "/" + this.action;
        var f = this.down('form').getForm();
        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('warning.invalidForm'));
            return;
        }
        var putObject = {};
        Ext.iterate(f.getValues(), function (key, value) {
            putObject[key] = value;
        }, this);
        Ext.Ajax.request({
            url : url,
            method : 'PUT',
            scope : this,
            jsonData : putObject,
            success : function (ret) {
                this.store.reload();
                this.close();
            },
            failure : alertFailure
        });

    }

});
