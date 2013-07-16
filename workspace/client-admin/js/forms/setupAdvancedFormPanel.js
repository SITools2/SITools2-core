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
Ext.namespace("sitools.admin.forms");

/**
 * A simple window to edit Absolute Layout Size.
 * @cfg {sitools.admin.forms.ComponentsDisplayPanel} parentContainer
 * @cfg {Integer} the last zone position
 * @class sitools.admin.forms.setupAdvancedFormPanel
 * @extends Ext.Window
 */
sitools.admin.forms.setupAdvancedFormPanel = Ext.extend(Ext.Window, {
    modal : true,
    height : 180,
    width : 260,
    initComponent : function () {

        this.currentPosition++; 
        
        var form = new Ext.form.FormPanel({
            labelWidth : 75,
            padding : 5,
            items : [ {
                xtype : 'textfield',
                id : "title",
                fieldLabel : i18n.get('label.panelTitle'),
                anchor : "100%"
            }, {
                xtype : 'textfield',
                id : "height",
                fieldLabel : i18n.get('label.height'),
                anchor : "100%"
            }, {
                xtype : 'spinnerfield',
                id : 'position', 
                fieldLabel : i18n.get('label.position'),
                minValue : 0,
                maxValue : 10,
                value : this.currentPosition,
                allowDecimals : false,
                incrementValue : 1,
                accelerate : true,
                anchor : "100%", 
                allowBlank : false
            }],
            buttons : [ {
                scope : this,
                text : i18n.get('label.ok'),
                handler : this._onValidate
            }, {
                scope : this,
                text : i18n.get('label.cancel'),
                handler : this._onCancel
            } ]
        });
        
        this.title = i18n.get('label.setupAdvancedFormPanel');
        this.items = [ form ];
        
        sitools.admin.forms.setupAdvancedFormPanel.superclass.initComponent.call(this);

    },
    _onValidate : function () {
        var f = this.findByType('form')[0].getForm();
        
        var title = f.findField('title').getValue();
        var height = parseInt(f.findField('height').getValue(), 10);
        var position = parseInt(f.findField('position').getValue(), 10);
        
        var aPanel = new sitools.admin.forms.advancedFormPanel({
            title: title,
            frame : true,
            height: height,
            ddGroup : 'gridComponentsList',
            datasetColumnModel : this.parentContainer.datasetColumnModel,
            storeConcepts : this.parentContainer.storeConcepts, 
            formComponentsStore : this.parentContainer.formComponentsStore,
            absoluteLayout : this.parentContainer
        });
        
        this.parentContainer.y = this.parentContainer.y + 200;
        if (this.parentContainer.y > 500) {
            this.parentContainer.formSize.height = this.parentContainer.y;
        }
        var size = {
            width : this.parentContainer.formSize.width,
            height : this.parentContainer.formSize.height
        };
        this.parentContainer.setSize(size);
        
        this.parentContainer.zoneStore.add(new Ext.data.Record({
            id : aPanel.id,
            height : aPanel.height,
            position : position
        }));
        
        this.parentContainer.insert(position, aPanel);
        
        this.parentContainer.doLayout();
        this.destroy();

    },
    _onCancel : function () {
        this.destroy();
    }
});
