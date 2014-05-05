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
Ext.namespace('sitools.admin.datasets.opensearch');


Ext.define('sitools.admin.datasets.opensearch.relativeLink', { 
    extend : 'Ext.window.Window',
    modal : true,
    width : 200,
    
    initComponent : function () {
        this.title = i18n.get('label.detailColumnDefinition');
        
        this.formRelative = Ext.create('Ext.form.Panel', {
            padding : 10,
            labelWidth: 150,
            border : false,
            bodyBorder : false,
            items : [{
                xtype : 'checkbox',
                name : 'relative',
                fieldLabel : i18n.get('label.isRelative'),
                anchor : '100%',
                maxLength : 100
            }]
        });
        
        this.buttons =  [{
            text : i18n.get('label.ok'),
            scope : this,
            handler : this.onValidate

        }, {
            text : i18n.get('label.cancel'),
            scope : this,
            handler : function () {
                this.close();
            }
        }];
        
        this.items = [this.formRelative];
        
        sitools.admin.datasets.opensearch.relativeLink.superclass.initComponent.call(this);
    },
    afterRender : function () {
        sitools.admin.datasets.opensearch.relativeLink.superclass.afterRender.apply(this, arguments);
        if (!Ext.isEmpty(this.selectedRecord) && !Ext.isEmpty(this.selectedRecord.data)) {
            var rec = {};
            var form = this.formRelative.getForm();
            rec.relative = this.selectedRecord.data.linkFieldRelative;
            
            form.setValues(rec);
        }
    },
    
    onValidate : function () {
        var form = this.formRelative.getForm();
        var relative = form.findField("relative").getValue();
        this.selectedRecord.data.linkFieldRelative = relative;
        this.close();
    }
    
});