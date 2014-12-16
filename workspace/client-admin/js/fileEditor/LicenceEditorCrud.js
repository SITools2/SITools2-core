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

Ext.namespace('sitools.admin.fileEditor');

Ext.define('sitools.admin.fileEditor.LicenceEditorCrud', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.s-licenceEditor',

    border : false,
    height : 480,
    id : ID.BOX.FILEEDITORLICENCE,
    width : 700,
    layout : 'fit',
    
    initComponent : function () {
        this.url = loadUrl.get('APP_URL') + loadUrl.get('APP_ADMINISTRATOR_URL');

        this.bbar = Ext.create('Ext.toolbar.Toolbar', {
            border : false,
            layout : {
                type : 'hbox',
                pack : 'center',
                align : 'middle'
            },
            items : [{
                xtype : 'button',
                text : i18n.get('label.save'),
                scope : this,
                scale : 'medium',
                icon : '/sitools' + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/images/icons/save.png',
                handler : this.onValidate
            }]
        });

        this.fileEditor = Ext.create('Ext.form.field.TextArea', {
            border : false
        });
        this.items = [this.fileEditor];
        
        this.callParent(arguments);
    },
    
    afterRender : function () {
        this.callParent(arguments);

        if (this.url) {
            Ext.Ajax.request({
                url : this.url + '/cgu.html',
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var data = ret.responseText;
                    CKEDITOR.imagesUrl = loadUrl.get('APP_URL') + loadUrl.get('APP_UPLOAD_URL') + '/?media=json', // use to choose or upload images
                    CKEDITOR.replace(this.fileEditor.id, {
                        customConfig: 'config-basic-plus.js',
                        fullPage : true,
                        height : 330
                    });
                    this.fileEditor.setValue(data);
                    CKEDITOR.instances[this.fileEditor.id].setData(data); 
                },
                failure : alertFailure
            });
        }
    },
    
    onValidate : function () {
        var text = CKEDITOR.instances[this.fileEditor.id].getData();
        Ext.Ajax.request({
            url : this.url + '/cgu.html',
            method : 'PUT',
            scope : this,
          
            headers : {
                'Content-Type' : 'text/html'
            },
            jsonData : text,
            success : function (ret) {
                popupMessage(i18n.get('label.information'), i18n.get('label.changeSaved'), null, 'x-info');
            },
            failure : alertFailure
        });
    }
});

