/*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * 
 * This file is part of SITools2.
 * 
 * SITools2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SITools2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SITools2. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/* global Ext, sitools, i18n, loadUrl */

Ext.namespace('sitools.widget.sitoolsEditorPlugins');
/**
 * datasetLink widget
 * 
 * @class sitools.widget.sitoolsEditorPlugins.documentBrowser
 * @extends Ext.util.Observable
 */
sitools.widget.sitoolsEditorPlugins.documentBrowser = Ext.extend(Ext.Window, {
	alias : 'sitools.widget.sitoolsEditorPlugins.documentBrowser',
    width : 500,
    height : 500,
    layout : 'fit',
    initComponent : function () {
        
        this.title = i18n.get('label.importDocTitle');
        
        this.documentUrl = this.datastorageUrl + '/documents/';
        
        this.store = new Ext.data.JsonStore({
            url: this.documentUrl,
            root: 'items',
            fields: [
                'name', 'url',
                {name:'size', type: 'float'},
                {name:'lastmod', type:'date', dateFormat:'timestamp'}
            ],
            listeners: {
                scope : this,
                load : function (store, records, options) {
                    Ext.each(records, function(record){
                        var url = new Reference(record.get('url'));
                        var recordUrl = url.getFile();
                        record.set("url", recordUrl);
                    });
                },
                exception : function (misc) { // s'il y a une exception, on essaye de créer le dossier /documents
                    Ext.Ajax.request({
                        url : this.documentUrl,
                        method : 'PUT',
                        success : function (ret) {
                        },
                        failure : alertFailure
                    });
                }
            }
        });
        this.store.load();
        
        this.grid = new Ext.grid.GridPanel({
            region : 'center',
            padding:'5',
            store: this.store,
            title: i18n.get('label.selectDocToImport'),
            sm: Ext.create('Ext.selection.RowModel',{singleSelect:true}),
            colModel: new Ext.grid.ColumnModel({
                columns: [{
                        width: 30, 
                        sortable: true,
                        scope : this,
                        renderer : function (value) {
                            var lastDot = value.lastIndexOf(".");
                            var icon = this.getIcon(value.slice(lastDot + 1, value.length));
                            if (icon == null) {
                                return "";
                            }
                            return "<img src=" + icon + " />";
                        }
                    }, {
                        header: i18n.get('headers.name'),
                        dataIndex: 'name',
                        width: 150, 
                        sortable: true
                    }, {
                        header: i18n.get('label.size'),
                        dataIndex: 'size',
                        width: 120, 
                        renderer : function (value) {
                            if(value < 1024) {
                                return value + " bytes";
                            } else {
                                return (Math.round(((value*10) / 1024))/10) + " KB";
                            }
                        }
                    }, {
                        header: i18n.get('label.lastmod'),
                        xtype: 'datecolumn',
                        dataIndex: 'lastmod',
                        width: 120, 
                        renderer : function (value) {
                            return new Date(value).format("d/m/Y g:i a");
                        }
                    }]
            }),
            viewConfig: {
                forceFit: true,
                autoFill : true
            }
        });
        
        this.formPanel = new Ext.FormPanel({
            fileUpload : true,
            collapsible : true,
            formId : 'formUploadId',
            autoHeight : true,
            bodyStyle : 'padding: 10px 10px 0 10px;',
            height : 100,
            region : 'north',
            labelWidth : 100,
            defaults : {
                anchor : '100%',
                allowBlank : true,
                msgTarget : 'side'
            },
            items : [{
                xtype : 'fileuploadfield',
                fieldLabel: i18n.get('label.file'),
                id : 'form-file',
                emptyText : i18n.get('label.selectFile'),
                name : 'image',
                buttonText : '',
                buttonCfg : {
                    iconCls : 'upload-icon'
                }
            }],
            buttons : [{
                text : i18n.get('label.uploadFile'),
                scope : this,
                handler : function () {
                    if (this.formPanel.getForm().isValid()) {
                        var f = this.formPanel.getForm();
                        var fileName = f.findField('form-file').getValue();
                        fileName = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.lenght);

                        Ext.Ajax.request({
                            url : this.documentUrl,
                            form : 'formUploadId',
                            isUpload : true,
                            waitMsg : "wait...",
                            method : 'POST',
                            scope : this,
                            success : function (response) {
                            },
                            failure : function (response) {
                                Ext.Msg.alert(i18n.get('label.error'));
                            },
                            callback : function () {
                                this.grid.getStore().reload();
                            }
                        });
                    }
                }
            }]
        });

        this.container = new Ext.Panel({
            padding:'5',
            layout: 'border',
            defaults: {margins:'0 0 5 0'},
            items:[this.grid, this.formPanel],
            bbar : ['->' ,{
                text : i18n.get('label.insert'),
                scope : this,
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/valid.png',
                handler : this.setDocumentToInsert
            }]
        });
        
        this.listeners = {
            scope : this,
            activate : function (win) {
                this.bringToFront(win);
            },
            show : function (win) {
                this.bringToFront(win);
            }
        };
        
        this.items = [this.container];
        
        sitools.widget.sitoolsEditorPlugins.documentBrowser.superclass.initComponent.call(this);
    },
    
    setDocumentToInsert : function () {
        var document = this.grid.getSelectionModel().getSelected();
        
        if (Ext.isEmpty(document)) {
            return;
        }
        
        var textfield = this.dialog.getContentElement('mainFrameDocument', 'textDocUrlID');
        var docUrl = this.documentUrl + document.data.name;
        
        
        textfield.documentName = document.data.name;
        textfield.documentUrl = docUrl;
        textfield.setValue("Document : " + document.data.name);
        textfield.documentComponent = String.format("parent.sitools.user.component.bottom.Bottom.showFooterLink(\"{0}\", \"{1}\"); return false;", docUrl, document.data.name);
        
        this.close();
    },
    
    bringToFront : function (win){
        if (win && !Ext.isEmpty(win.zindex) && win.isVisible()) {
            win.focus();
            win.setZIndex(win.zindex);
        }
    },
    
    getIcon : function (ext) {
        if (ext == "doc" || ext == "docx") {
            return loadUrl.get('APP_URL') + '/common/res/images/icons/word.png';
        } else if (ext == "xls" || ext == "xlsx") {
            return loadUrl.get('APP_URL') + '/common/res/images/icons/excel.png';
        } else if (ext == "ppt" || ext == "pptx") {
            return loadUrl.get('APP_URL') + '/common/res/images/icons/powerpoint.png';
        } else if (ext == "pdf" || ext == "PDF") {
            return loadUrl.get('APP_URL') + '/common/res/images/icons/icon-pdf-small.png';
        } else if (ext == "py") {
            return loadUrl.get('APP_URL') + '/common/res/images/icons/python.png';
        } else {
            return null;
        }
    }
    
});
