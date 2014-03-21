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
/*global Ext, sitools, i18n, loadUrl */

Ext.namespace('sitools.user.modules');
/**
 * 
 * @class sitools.user.modules.datastorageUploadFile
 * @cfg urlUpload the url where upload the choosen file
 * @extends Ext.Window
 */
sitools.user.modules.datastorageUploadFile = Ext.extend(Ext.Window, {

    id : 'winUploadId',
    width : 320,
    height : 125,
    modal : true,

    initComponent : function () {
        this.title = i18n.get('label.uploadFile');

        this.formPanel = new Ext.form.FormPanel({
            fileUpload : true,
            formId : 'formUploadId',
            autoHeight : true,
            bodyStyle : 'padding: 10px 10px 0 10px;',
            labelWidth : 100,
            defaults : {
                anchor : '100%',
                allowBlank : false,
                msgTarget : 'side'
                },                
            items : [ {
                xtype : 'fileuploadfield',
                id : 'form-file',
                emptyText : i18n.get('label.selectFile'),
                fieldLabel : i18n.get('label.file'),
                name : 'image',
                buttonText : '',
                buttonCfg : {
                    iconCls : 'upload-icon'
                },
                listeners : {
                	scope : this,
                	fileselected: function (field) {
                		     var f = this.formPanel.getForm();
                    		 var wuf = f.findField('wantUnzipFile');
                    		 var fileName = field.value;
                    		 var tabTmp = fileName.split('.');
                             var extension = tabTmp[tabTmp.length - 1];
                             
                    		    if (extension == "zip") {
                    		    	wuf.setValue(false);
                    		    	wuf.setVisible(true);
                    		    } else {
                    		    	wuf.setValue(false);
                    		    	wuf.setVisible(false);
                    		    };
                    	    }
                     	}
            }
            , {
            	xtype : 'checkbox',
            	name : 'wantUnzipFile',
            	boxLabel  : 'unzip',
            	checked : false,
            	hidden : true, 
            	inputValue : "unzip"
            } 
            ],
            buttons : [ {
                text : i18n.get('label.uploadFile'),
                scope : this,
                handler : function () {
                	var f = this.formPanel.getForm();
                    if (f.isValid()) {
                       
                        var fileName = f.findField('form-file').getValue();
                        fileName = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.lenght);

                        var wantedUnzipFile = f.findField('wantUnzipFile').getValue();
                        // var additif = (wantedUnzipFile) ? "?unzip=true" : "";
                        
                        this.createUploadedNode(fileName);

                        Ext.Ajax.request({
                            url : this.urlUpload,
                            form : 'formUploadId',
                            isUpload : true,
                            waitMsg : "wait...",
                            method : 'POST',
                            scope : this,
                            extraParams : {
                            	unzip : wantedUnzipFile
                            },
                            success : function (response) {
                                new Ext.ux.Notification({
                                    iconCls : 'x-icon-information',
                                    title : i18n.get('label.information'),
                                    html : i18n.get('label.fileUploaded'),
                                    autoDestroy : true,
                                    hideDelay : 3000
                                }).show();
                                Ext.getCmp('winUploadId').close();

                                // call callback in order to add the
                                // uploadedFile to the parent component
                                this.callback.call(this.scope, this.uploadedFileNode);

                            },
                            failure : function (response) {
                                Ext.Msg.alert(i18n.get('label.error'));
                            }
                        });
                    }
                }
            } ]
        }
        
        
        );

        this.items = [ this.formPanel ];

        sitools.user.modules.datastorageUploadFile.superclass.initComponent.call(this);
    },

    isImage : function (text) {
        var imageRegex = /\.(png|jpg|jpeg|gif|bmp|tif|JPG|JPEG)$/;
        return (text.match(imageRegex));
    },

    createUploadedNode : function (fileName) {
        this.uploadedFileNode = {
            text : fileName,
            leaf : true,
            url : this.urlUpload + fileName
        };
    }

});