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
 showHelp, loadUrl*/

Ext.namespace('sitools.component.logs');

Ext.define('sitools.admin.logs.AnalogProp', { 
    extend : 'Ext.panel.Panel',

    alias : 'widget.s-analog',
    height : 480,
    width : 700,
    border : false,
    
    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_ADMINISTRATOR_URL') + '/plugin/analog';
        this.layout = 'fit';
        
        this.tbar = {
            xtype : 'toolbar',
            defaults : {
                scope : this
            },
            items : [ {
                xtype : 'button',
                text : i18n.get('label.logGenerate'),
                scope : this,
                icon : '/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/icons/tree_application_plugin.png',
                handler : this.onGenerate
            } ]
        };
        
        this.items = [this.createLogPanel()];
        
        this.callParent(arguments);
    },

    onGenerate : function () {
        this.getEl().mask('Generating');

        Ext.Ajax.request({
            url : this.url,
            method : 'PUT',
            scope : this,
            success : function (ret) {
                var json = Ext.decode(ret.responseText);
                if (!json.success) {
                    Ext.Msg.alert(i18n.get("label.warning"), json.message);
                    return;
                }
                popupMessage(i18n.get('label.information'), i18n.get('label.logsChanged'), null, 'x-info');

                this.removeAll();
                this.add(this.createLogPanel());
                
            },
            failure : alertFailure,
            callback : function () {
                this.getEl().unmask();
            }
        });
    },
    
    createLogPanel : function () {
        return Ext.create('Ext.panel.Panel', {
            items: {
                xtype : 'component',
                autoEl: {
                    tag: 'iframe',
                    border : false,
                    layout : 'fit',
                    src : this.url + '?_dc=' + new Date().getTime()
                }
            },
            id : 'log',
            layout : "fit",
            padding : 10
        });
    }
});
