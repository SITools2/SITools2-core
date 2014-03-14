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

sitools.component.logs.analogProp = Ext.extend(Ext.Panel, {
    
    border : false,
    height : 480,
    id : "analogBoxId",
    width : 700,
    
    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_ADMINISTRATOR_URL') + '/plugin/analog';
        this.layout = 'fit';
        
        this.tbar = new Ext.Toolbar({
            items : [ {
                xtype : 'button',
                text : i18n.get('label.logGenerate'),
                scope : this,
                icon : '/sitools/common/res/images/icons/tree_application_plugin.png',
                handler : this.refresh
            } ]
        });
        
        var htmlReaderCfg = {
                defaultSrc : this.url + '?_dc=' + new Date().getTime(),
                id : 'log',
                layout : "fit",
                padding : 10
            };
        this.panLog = new Ext.ux.ManagedIFrame.Panel(htmlReaderCfg);
        this.items = [this.panLog];
        
        sitools.component.logs.analogProp.superclass.initComponent.call(this);
        
        
    },

    onGenerate : function () {
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
                var temp = new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('label.logsChanged'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);
                
                this.panLog.setSrc(this.url + '?_dc=' + new Date().getTime()); 
                                  
            },
            failure : alertFailure,
            callback : function () {
                Ext.getCmp('analogBoxId').getEl().unmask();
            }
        });
    },
    
    refresh : function () {
        var myMask = new Ext.LoadMask(Ext.getCmp('analogBoxId').getEl(), {
            msg : "Generating"
        });
        myMask.show();
        this.onGenerate();
    }
    

});

Ext.reg('s-analog', sitools.component.logs.analogProp);