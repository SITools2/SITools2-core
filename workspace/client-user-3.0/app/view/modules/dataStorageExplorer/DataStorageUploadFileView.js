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

Ext.namespace('sitools.user.view.modules.dataStorageExplorer');
/**
 * 
 * @class sitools.user.modules.datastorageUploadFile
 * @cfg urlUpload the url where upload the choosen file
 * @extends Ext.Window
 */
Ext.define('sitools.user.view.modules.dataStorageExplorer.DataStorageUploadFileView', {
	extend : 'Ext.menu.Menu',
	alias: 'widget.datastorageUploadFile',
	
    width : 320,
    height : 90,
    border : false,
    closeAction : 'hide',
    plain : true,
    style : 'box-shadow: rgb(43, 43, 43) 0px 0px 6px;',
    
    initComponent : function () {

    	this.items = [{
            xtype : 'fileuploadfield',
            name : 'form-file',
            cls : 'menuItemCls',
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
        		    var wuf = this.down('checkbox');
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
        }, {
        	xtype : 'checkbox',
        	name : 'wantUnzipFile',
        	boxLabel  : 'unzip',
        	checked : false,
        	hidden : true, 
        	inputValue : "unzip"
        }, {
        	xtype : 'menuseparator',
        	separatorCls : 'customMenuSeparator'
		}, {
            text : i18n.get('label.uploadFile'),
            cls : 'menuItemCls',
            icon : loadUrl.get('APP_URL') + '/client-user/resources/images/cmsModule/add.png',
            scope : this,
            handler : this.upload
        }];
    	
       this.callParent(arguments);
    },

    upload : function () {
    	var textfield = this.down('fileuploadfield');
        if (textfield.isValid()) {
           
            var fileName = textfield.getValue();
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.lenght);

            var wantedUnzipFile = this.down('checkbox').getValue();
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
                    popupMessage(i18n.get('label.information'), i18n.get('label.fileUploaded'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;

                    this.close();

                    // call callback in order to add the
                    // uploadedFile to the parent component
                    this.callback.call(this.scope, this.uploadedFileNode);

                },
                failure : function (response) {
                    Ext.Msg.alert(i18n.get('label.error'), response.responseText);
                }
            });
        }
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