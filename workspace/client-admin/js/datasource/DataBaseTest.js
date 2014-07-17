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
Ext.namespace('sitools.admin.datasource');

/**
 * Open A window with a status bar, and test database. Displays the result with a status bar. 
 * @cfg {string} url the url to request to test database
 * @cfg {} data the private data of current database. 
 * @class sitools.admin.datasource.DataBaseTest
 * @extends Ext.Window
 */
Ext.define('sitools.admin.datasource.DataBaseTest', { 
    extend : 'Ext.Window',
    width : 400,
    height : 210,
    modal : true,
    closable : false,
    layout : 'fit',
    buttonAlign : 'center',

    initComponent : function () {
        
        this.title = i18n.get('label.databaseInfo');
        this.bbar = new Ext.ux.StatusBar({
            text : i18n.get('label.ready'),
            id : 'sbDBTest',
            iconCls : 'x-status-valid'
        });
        
        this.items = [{
            xtype : 'panel',
            baseCls : 'x-plain',
            layout : 'fit',
            items : [ {
                xtype : 'textarea',
                id : 'DBText'
            } ]
        }];
        
        this.buttons = [{
            text : i18n.get('label.ok'),
            scope : this,
            handler : function () {
                this.close();
            }
        }];

        sitools.admin.datasource.DataBaseTest.superclass.initComponent.call(this);
    },

    onRender : function () {
        sitools.admin.datasource.DataBaseTest.superclass.onRender.apply(this, arguments);
        Ext.getCmp('sbDBTest').showBusy();
        Ext.Ajax.request({
            url : this.url,
            method : 'PUT',
            scope : this,
            jsonData : this.data,
            success : function (ret) {
                var rep = Ext.decode(ret.responseText);
                var status = rep.success ? i18n.get('msg.success') : i18n.get('msg.failure');
                var msg = rep.data ? rep.data.join('\n') : rep.message;
                Ext.getCmp('sbDBTest').setStatus({
                    text : status,
                    iconCls : rep.success ? 'x-status-valid' : 'x-status-error'
                });
                Ext.getCmp('DBText').setValue(msg);
            },
            failure : function (ret) {
                var rep = i18n.get('warning.serverError');
                try {
                    rep = Ext.decode(ret.responseText).logs.join('\n');
                } catch (Exception) {
                }
                Ext.getCmp('sbDBTest').setStatus({
                    text : ret.statusText,
                    iconCls : 'x-status-error'
                });
                Ext.getCmp('DBText').setValue(rep);
            }
        });
    }

});
