/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

Ext.namespace('sitools.public.widget.ckeditor.sitoolsPlugins');
/**
 * Insert a link from a document file in HTML
 * 
 * @class sitools.public.widget.ckeditor.sitoolsPlugins.DocumentBrowser
 * @param String datastorageUrl
 * @param dialog dialog from CKEDITOR
 * @param editor editor from CKEDITOR
 * @extends Ext.window.Window
 */
Ext.define('sitools.public.widget.ckeditor.sitoolsPlugins.DocumentBrowser', {
    extend : 'Ext.window.Window',
    alias : 'widget.documentBrowser',
    
    width : 500,
    height : 500,
    layout : 'fit',
    modal : true,
    
    initComponent : function () {
        
    	this.dialog.hide();
    	
        this.title = i18n.get('label.importDocTitle');
        this.documentUrl = this.datastorageUrl + '/documents/';
        
        this.store = Ext.create('Ext.data.JsonStore', {
            proxy : {
                type : 'ajax',
                url: this.documentUrl,
                reader : {
                    type : 'json',
		            root: 'items'
                }
            },
            fields: [ 'name', 'url',
                {name:'size', type: 'float'},
                {name:'lastmod', type:'date', dateFormat:'timestamp'} ],
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
        
        this.grid = Ext.create('Ext.grid.Panel', {
            region : 'center',
            store: this.store,
            title: i18n.get('label.selectDocToImport'),
            forceFit: true,
            border : false,
            selModel: Ext.create('Ext.selection.RowModel', {
                singleSelect:true
            }),
            colModel: {
                items: [{
                    dataIndex: 'url',
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
                    width: 120, 
                    sortable: true
                }, {
                    header: i18n.get('label.size'),
                    dataIndex: 'size',
                    width: 60, 
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
                    width: 100, 
                    renderer : function (value) {
                        return Ext.Date.format(new Date(value), SITOOLS_DEFAULT_IHM_DATE_FORMAT);
                    }
                }]
            }
        });
        
        this.formPanel = Ext.create('Ext.form.Panel', {
            fileUpload : true,
            formId : 'formUploadId',
            autoHeight : true,
            bodyStyle : 'padding: 10px 10px 0 10px;',
            height : 150,
            region : 'north',
            labelWidth : 100,
            border : false,
            defaults : {
                anchor : '100%',
                allowBlank : true,
                msgTarget : 'side'
            },
            items : [{
            	xtype : 'label',
            	html : i18n.get('label.infoDocumentBrowser1')
            }, {
            	xtype : 'label',
            	html : i18n.get('label.infoDocumentBrowser2')
            }, {
            	xtype : 'label',
            	html : i18n.get('label.infoDocumentBrowser3')
            }, {
                xtype : 'fileuploadfield',
                name : 'form-file',
                cls : 'menuItemCls',
                allowBlank: false,
                labelWidth : 80,
                emptyText : i18n.get('label.selectFile'),
                fieldLabel : i18n.get('label.file'),
                name : 'image',
                anchor: '100%',
                buttonText : '',
                buttonConfig : {
                    iconCls : 'upload-icon'
                }
            }],
            buttons : [{
                text : i18n.get('label.uploadFile'),
                scope : this,
                handler : function () {
                    if (this.formPanel.getForm().isValid()) {
                        
                        var form = this.formPanel.getForm();
                        var fileName = this.formPanel.down('fileuploadfield').getValue();
                        fileName = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.lenght);

                        this.grid.getEl().mask(i18n.get("label.fileUploading"));
                        
                        Ext.Ajax.request({
                        	url :  this.documentUrl,
                        	method : 'HEAD',
                        	scope : this,
                        	success : function (response) {
                        		this.submittingForm();
                        	},
                        	failure : function (response) {
                        		this.creatingDocumentDirectory();
                        	}
                        });
                    }
                }
            }]
        });

        this.container = Ext.create('Ext.panel.Panel', {
            layout: 'border',
            defaults: {margins:'0 0 5 0'},
            items:[this.grid, this.formPanel],
            border : false,
            bbar : ['->' ,{
                text : i18n.get('label.insert'),
                scope : this,
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/valid.png',
                handler : this.setDocumentToInsert
            }]
        });
        
        this.listeners = {
    		scope : this,
            afterrender : function (win) {
                 Ext.defer(function () {
                     win.setZIndex(24000);
                     Ext.WindowManager.bringToFront(win);
                 }, 600);
             },
             close : function () {
            	 if (!this.dialogVisible) {
            		 this.dialog.show();
            	 }
             }
         };
        
        this.items = [this.container];
        this.callParent(arguments);
    },
    
    setDocumentToInsert : function () {
        var document = this.grid.getSelectionModel().getSelection()[0];
        
        if (Ext.isEmpty(document)) {
            return;
        }
        
        this.dialog.show();
        this.dialogVisible = true;
        
        var textfield = this.dialog.getContentElement('mainFrameDocument', 'textDocUrlID');
        var docUrl = this.documentUrl + document.data.name;
        
        
        var lastDot = document.data.name.lastIndexOf(".");
        var extension = document.data.name.slice(lastDot + 1, document.data.name.length);
        
        var openable = false;
        if (extension == "pdf")
            openable = true;
        
        textfield.documentName = document.data.name;
        textfield.documentUrl = docUrl;
        textfield.setValue("Document : " + document.data.name);
        textfield.documentComponent = Ext.String.format("parent.sitools.user.utils.DataviewUtils.showDisplayableUrl(\"{0}\", {1}, \"{2}\"); return false;", docUrl, openable, "{title : '"+document.data.name+"'}");
        
        this.close();
    },
    
    creatingDocumentDirectory : function () {
    	Ext.Ajax.request({
			url : this.documentUrl,
			method : 'PUT',
			scope : this,
			success : function (response) {
				this.submittingForm();
			},
			failure : function (response) {
				Ext.Msg.show({
					title : i18n.get('label.info'),
					msg : i18n.get('label.cannotCreateDocumentDirectory'),
					icon : Ext.Msg.ERROR,
					buttons : Ext.Msg.OK
				});
			}
		})
    },
    
    submittingForm : function () {
    	
    	this.formPanel.getForm().submit({
            url : this.documentUrl,
//            waitMsg : i18n.get("label.wait") + "...",
//            waitTitle : i18n.get("label.fileUploading"),
            scope : this,
            success : function (form, action) {
                this.grid.getStore().reload();
                this.grid.getEl().unmask();
            },
            failure : function (response) {
                this.grid.getStore().reload();
                this.grid.getEl().unmask();
            }
        });
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
