/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

Ext.namespace('sitools.component.fileEditor');

sitools.component.fileEditor.fileEditorProp = Ext.extend(Ext.Window, {
    
    width : 700,
    height : 480,
    mediaType : null,
    
    initComponent : function () {
        this.title = i18n.get('label.modifyFile') + this.fileName;
        this.layout = 'fit';
        this.modal = this.modalType;
        var buttons;
        if (this.modal === true) {
            buttons = [ [{
                text : i18n.get('label.save'),
                scope : this,
                handler : this.onValidate
            }, {
                text : i18n.get('label.cancel'),
                scope : this,
                handler : function () {
                    this.close();
                }
            } ] ];
        } else {
            buttons = [{
                text : i18n.get('label.cancel'),
                scope : this,
                handler : function () {
                    this.close();
                }
            } ];

        }
        
        this.items = [{
            xtype : 'panel',
            layout : 'fit',
            items : [{
                xtype : 'htmleditor',
                id : 'fileEditor',
                readOnly : this.editable,
                autoScroll : true,
                listeners : {
                    afterrender : function () {
                        this.syncValue();
                        this.toggleSourceEdit(true);
                    }
                }
            }],
            buttons : buttons
        }];
        
        
        
        this.listeners = {
                scope : this, 
                activate : function () {
                    this.findByType('htmleditor')[0].syncValue();
                    this.findByType('htmleditor')[0].toggleSourceEdit(true);
                }
            };
        
        sitools.component.fileEditor.fileEditorProp.superclass.initComponent.call(this);
        
        
    },
    
    onRender : function () {
        sitools.component.fileEditor.fileEditorProp.superclass.onRender.apply(this, arguments);
        
        if (this.url) {
            Ext.Ajax.request({
                url : this.url,
                method : 'GET',
                scope : this,
                success : function (ret) {
                    var data = ret.responseText;
                    this.findByType('htmleditor')[0].setValue(data);
                },
                failure : alertFailure
            });
        }
    },
    
    onValidate : function () {
        var text = this.findByType('htmleditor')[0].getValue();
        Ext.Ajax.request({
            url : this.url,
            method : 'PUT',
            scope : this,
            headers : {
                'Content-Type' : this.mediaType
            },
            jsonData : text,
            success : function (ret) {
                var temp = new Ext.ux.Notification({
                    iconCls : 'x-icon-information',
                    title : i18n.get('label.information'),
                    html : i18n.get('label.changeSaved'),
                    autoDestroy : true,
                    hideDelay : 1000
                }).show(document);
                this.close();
            },
            failure : alertFailure
        });
    }
    

});

Ext.reg('s-fileEditorProp', sitools.component.fileEditor.fileEditorProp);