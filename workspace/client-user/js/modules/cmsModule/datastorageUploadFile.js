/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
sitools.user.modules.datastorageUploadFile  = Ext.extend(Ext.Window, {
    
    id : 'winUploadId',
	width: 320,
	height : 125,
	modal : true,
    
    initComponent : function () {
		this.title = i18n.get('label.uploadFile');
        
		this.formPanel = new Ext.FormPanel({
                fileUpload: true,
                formId : 'formUploadId', 
                autoHeight: true,
                bodyStyle: 'padding: 10px 10px 0 10px;',
                labelWidth: 100,
                defaults: {
                    anchor: '100%',
                    allowBlank: false,
                    msgTarget: 'side'
                },
                items: [{
                    xtype: 'fileuploadfield',
                    id: 'form-file',
                    emptyText: i18n.get('label.selectFile'),
                    fieldLabel: i18n.get('label.file'),
                    name: 'image',
                    buttonText: ''
                        ,
                    buttonCfg: {
                        iconCls: 'upload-icon'
                    }
                }],
                buttons: [{
                    text: i18n.get('label.uploadFile'),
                    scope : this, 
                    handler : function () {
                        if (this.formPanel.getForm().isValid()) {
                            var f = this.formPanel.getForm();
                            var fileName = f.findField('form-file').getValue();
                            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.lenght);
                            
                            this.createUploadedNode(fileName);
                            console.log(this.urlUpload);
                            Ext.Ajax.request({
                                url : this.urlUpload,
                                form : 'formUploadId',
//                                isUpload : true, 
                                waitMsg : "wait...", 
                                method : 'POST',
                                scope : this,
                                success : function (response) {
                                	var status = response.responseText.search("403");
                                	console.dir(response);
                                	if (status != -1){
                                		Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.uploadForbidden'));                                		
                                	} else if (response.responseText == "") {
                                        new Ext.ux.Notification({
                                            iconCls: 'x-icon-information',
                                            title: i18n.get('label.information'),
                                            html: i18n.get('label.fileUploaded'),
                                            autoDestroy: true,
                                            hideDelay:  3000
                                        }).show(); 
                                        Ext.getCmp('winUploadId').close();
                                        
                                        // call callback in order to add the uploadedFile to the parent component
                                        this.callback.call(this.scope, this.uploadedFileNode);
                                    } else {
                                		Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.unknownError'));                                		
                                    }
                                }, 
                                failure : function (response) {
                                    Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.unknownError'));
                                }
                            });
                        }
                    }
                }]
            });
            
		this.items = [this.formPanel];
        
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