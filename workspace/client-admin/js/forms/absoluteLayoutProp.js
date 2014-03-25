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
Ext.namespace("sitools.admin.forms");

/**
 * A simple window to edit Absolute Layout Size.
 * @cfg {numeric} width the initial width
 * @cfg {numeric} height the initial height
 * @cfg {Ext.Panel} absoluteLayout The panel to change size
 * @cfg {Ext.TabPanel} tabPanel The main TabPanel
 * @cfg {Ext.Window} win The window to define a form.
 * @class sitools.admin.forms.absoluteLayoutProp
 * @extends Ext.Window
 */
Ext.define('sitools.admin.forms.absoluteLayoutProp', { 
    extend : 'Ext.Window',
    modal : true,
    initComponent : function () {
        var width = this.formSize.width;
        var height = this.formSize.height;

        var form = new Ext.form.FormPanel({
            labelWidth : 75,
            padding : 5,
            border : false,
            bodyBorder : false,
            items : [{
                xtype : 'textfield',
                id : "absolutePanelWidth",
                value : width,
                fieldLabel : i18n.get('label.width'),
                anchor : "100%"
            }, {
                xtype : 'textfield',
                id : "absolutePanelHeight",
                value : height,
                fieldLabel : i18n.get('label.height'),
                anchor : "100%"
            }]
        });
        
        this.height = 150;
        this.width = 260;

        this.title = i18n.get('label.setSize');
        this.items = [form];
        
        this.bbar = ['->', {
            scope : this,
            text : i18n.get('label.ok'),
            handler : this._onValidate
        }, {
            scope : this,
            text : i18n.get('label.cancel'),
            handler : this._onCancel
        }];
        
        sitools.admin.forms.absoluteLayoutProp.superclass.initComponent.call(this);

    },
    _onValidate : function () {
        var f = this.down('form').getForm();
        var width = parseInt(f.findField('absolutePanelWidth').getValue(), 10);
        var height = parseInt(f.findField('absolutePanelHeight').getValue(), 10);
        var size = {
            width : width,
            height : height
        };
//        this.tabPanel.setSize({
//			width : size.width + 225, 
//			height : size.height
//        });
//        this.tabPanel.doLayout();
        this.absoluteLayout.setSize(size);
        this.absoluteLayout.doLayout();
        this.absoluteLayout.formSize = size;
        this.win.formSize = size;
//        this.win.doLayout();
        this.destroy();

    },
    _onCancel : function () {
        this.destroy();
    }
});
