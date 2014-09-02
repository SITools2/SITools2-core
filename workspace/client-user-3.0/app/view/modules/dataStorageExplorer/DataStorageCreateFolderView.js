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
 * Datastorage Create a folder
 * 
 * @class sitools.user.view.modules.dataStorageExplorer.DataStorageCreateFolderView
 * @extends Ext.panel.Panel
 */
Ext.define('sitools.user.view.modules.dataStorageExplorer.DataStorageCreateFolderView', {
	extend : 'Ext.menu.Menu',
	alias: 'widget.datastorageCreateFolder',
	
    width : 300,
    height : 70,
    border : false,
    closeAction : 'hide',
    plain : true,

    initComponent : function () {
    	
    	this.items = [{
    		xtype : 'textfield',
    		name : 'folderName',
    		cls : 'menuItemCls',
            fieldLabel : i18n.get('label.folderName'),
            listeners : {
                scope : this,
                afterrender : function (textfield) {
                    Ext.defer(textfield.focus, 100, textfield);
                },
                specialkey : function (field, e) {
                    if (e.getKey() == e.ENTER) {
                        this.onCreate();
                    }
                }
            }
    	}, {
        	xtype : 'menuseparator',
        	separatorCls : 'customMenuSeparator'
		}, {
          text : i18n.get('label.create'),
    	  cls : 'menuItemCls',
          icon : loadUrl.get('APP_URL') + '/client-user/resources/images/cmsModule/createFolder.png',
          scope : this,
          handler : this.onCreate
      }];
    	
        this.callParent(arguments);
    },

    onCreate : function () {
    	var textfield = this.down('textfield');
        if (textfield.isValid()) {
            var fileName = textfield.getValue();
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.lenght);

            this.createUploadedNode(fileName);

            Ext.Ajax.request({
                url : this.url + fileName + "/",
                form : 'formUploadId',
                waitMsg : "wait...",
                method : 'PUT',
                scope : this,
                success : function (response) {
                    popupMessage(i18n.get('label.information'), i18n.get('label.folderCreated'), loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL')+'/res/images/msgBox/16/icon-info.png');;

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
    
    createUploadedNode : function (fileName) {
        this.uploadedFileNode = {
            text : fileName,
            name : fileName,
            type : "FOLDER",
            leaf : false,
            url : this.url + fileName + "/"
        };
    }

});