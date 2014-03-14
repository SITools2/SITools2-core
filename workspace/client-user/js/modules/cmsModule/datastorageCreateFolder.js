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
/*global Ext, sitools, i18n, loadUrl */

Ext.namespace('sitools.user.modules');
/**
 * 
 * @class sitools.user.modules.dpWorkspaceCreateFolder
 * @cfg urlUpload the url where upload the choosen file
 * @extends Ext.Window
 */
sitools.user.modules.datastorageCreateFolder = Ext.extend(Ext.Window, {

    id : 'winCreateFolderID',
    width : 300,
    height : 110,
    modal : true,

    initComponent : function () {
        this.title = i18n.get('label.createFolder');

        this.formPanel = new Ext.FormPanel({
            formId : 'formUploadId',
            autoHeight : true,
            bodyStyle : 'padding: 10px 10px 0 10px;',
            labelWidth : 100,
            defaults : {
                anchor : '90%',
                allowBlank : false,
                msgTarget : 'side'
            },
            items : [ {
                xtype : 'textfield',
                id : 'folderName',
                fieldLabel : i18n.get('label.folderName'),
                listeners : {
                    scope : this,
                    afterrender : function (textfield) {
                        Ext.defer(textfield.focus, 500, textfield);
                    },
                    specialkey : function (field, e) {
                        if (e.getKey() == e.ENTER) {
                            this.onCreate();
                        }
                    }
                }
            }],
            buttons : [ {
                text : i18n.get('label.create'),
                icon : loadUrl.get('APP_URL') + '/client-user/js/modules/cmsModule/res/icons/createFolder.png',
                scope : this,
                handler : this.onCreate
            } ]
        });

        this.items = [ this.formPanel ];

        sitools.user.modules.datastorageCreateFolder.superclass.initComponent.call(this);
    },

    onCreate : function () {
        if (this.formPanel.getForm().isValid()) {
            var f = this.formPanel.getForm();
            var fileName = f.findField('folderName').getValue();
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.lenght);

            this.createUploadedNode(fileName);

            Ext.Ajax.request({
                url : this.url + fileName + "/",
                form : 'formUploadId',
                waitMsg : "wait...",
                method : 'PUT',
                scope : this,
                success : function (response) {
                    new Ext.ux.Notification({
                        iconCls : 'x-icon-information',
                        title : i18n.get('label.information'),
                        html : i18n.get('label.folderCreated'),
                        autoDestroy : true,
                        hideDelay : 3000
                    }).show();
                    Ext.getCmp('winCreateFolderID').close();

                    // call callback in order to add the
                    // uploadedFile to the parent component
                    this.callback.call(this.scope, this.uploadedFileNode);

                },
                failure : function (response) {
                    Ext.Msg.alert(i18n.get('label.error'));
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