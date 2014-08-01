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
 * @cfg {sitools.admin.forms.ComponentsDisplayPanel} parentContainer
 * @cfg {Integer} the last zone position
 * @class sitools.admin.forms.SetupAdvancedFormPanel
 * @extends Ext.Window
 */
Ext.define('sitools.admin.forms.SetupAdvancedFormPanel', {
    extend : 'Ext.Window',
    modal : true,
    height : 230,
    width : 260,
    layout : 'fit',
    initComponent : function () {

        this.title = i18n.get('label.setupAdvancedFormPanel');
        this.fielsetPosition = this.parentContainer.zoneStore.getCount() + 1;
        
        this.form = Ext.create("Ext.form.FormPanel", {
            padding : 5,
            border : false,
            bodyBorder : false,
            items : [ {
                xtype : 'textfield',
                name : 'title',
                id : "title",
                fieldLabel : i18n.get('label.titleCriteria'),
                anchor : "100%",
                allowBlank : false
            }, {
                xtype : 'textfield',
                name : 'height',
                id : "height",
                fieldLabel : i18n.get('label.height'),
                value : 200,
                anchor : "100%"
            }, {
                xtype : 'textfield',
                fieldLabel : i18n.get('label.css'),
                name : 'css',
                id : 'cssFieldset',
                anchor : "100%"
            }, {
                xtype : 'checkbox',
                fieldLabel : i18n.get('label.collapsible'),
                name : 'collapsible',
                id : 'collapseFieldset',
                anchor : "100%"
            }, {
                xtype : 'checkbox',
                fieldLabel : i18n.get('label.collapsed'),
                name : 'collapsed',
                id : 'isCollapseFieldset',
                anchor : "100%"
            }   /*{
                xtype : 'numberfield',
                id : 'position', 
                fieldLabel : i18n.get('label.position'),
                minValue : 0,
                maxValue : 10,
                value : currentPosition,
                allowDecimals : false,
                incrementValue : 1,
                accelerate : true,
                anchor : "100%", 
                allowBlank : false
            }*/]
        });
        
        this.items = [ this.form ];
        
        
        this.bbar = [ '->' , {
                scope : this,
                text : i18n.get('label.ok'),
                handler : this._onValidate
            }, {
                scope : this,
                text : i18n.get('label.cancel'),
                handler : this._onCancel
            }];
        
        this.callParent(arguments);

    },
    
    afterRender : function () {
        this.callParent(arguments);
        if (this.action === 'modify') {
            this.form.getForm().setValues(this.zone);
        }
    },
    
    _onValidate : function () {
        var f = this.form.getForm();
        if (!f.isValid()) {
            return;
        }
        
        var title = f.findField('title').getValue();
        var isCollapsible = f.findField('collapseFieldset').getValue();
        var isCollapse = f.findField('isCollapseFieldset').getValue();
        var cssFieldset = f.findField('cssFieldset').getValue();
        var height = parseInt(f.findField('height').getValue(), 10);
        
        var rec = {
            title : title,
            height : height,
            css : cssFieldset,
            collapsible : isCollapsible,
            collapsed : isCollapse,
            position : this.fielsetPosition
        };
        
        if (this.action === 'modify') {
            var zoneToRemove;
            if (!Ext.isEmpty(this.zone.containerPanelId)){
            	zoneToRemove = this.parentContainer.zoneStore.find('containerPanelId', this.zone.containerPanelId);
            } else {
            	zoneToRemove = this.parentContainer.zoneStore.find('position', 0);
            }
            var zoneRec = this.parentContainer.zoneStore.getAt(zoneToRemove);
            
            Ext.apply(zoneRec.data, rec, {containerPanelId : this.zone.containerPanelId});
        }
        else {
            rec.containerPanelId = Ext.id();
            this.parentContainer.zoneStore.add(rec);
        }
        
        this.destroy();
//        this.parentContainer.doLayout();
        this.parentContainer.fireEvent('activate');
    },
    
    _onCancel : function () {
        this.destroy();
    }
});
