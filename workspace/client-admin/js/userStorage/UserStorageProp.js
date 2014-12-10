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
Ext.namespace('sitools.admin.userStorage');

/**
 * A Panel to show the data from a specific user Storage
 * 
 * @cfg {String} the action to perform
 * @cfg {String} the url where get the resource
 * @cfg {Ext.data.JsonStore} the store where get the record
 * @class sitools.admin.userStorage.UserStorageProp
 * @extends Ext.Window
 */
Ext.define('sitools.admin.userStorage.UserStorageProp', {
    extend : 'Ext.Window',
	alias : 'widget.s-userStoragesprop',
    width : 700,
    height : 480,
    modal : true,
    pageSize : ADMIN_PANEL_NB_ELEMENTS,
    dataSets : "",
    layout : 'fit',

    initComponent : function () {
        if (this.action == 'modify') {
            this.title = i18n.get('label.modifyUserStorage') + " : " + this.userStorageRec.data.userId;
        }
        if (this.action == 'create') {
            this.title = i18n.get('label.createUserStorage');
        }

        this.items = [{
            xtype : 'form',
            border : false,
            bodyBorder : false,
            padding : 10,
            defaults : [ {
                readOnly : true
            } ],
            items : [ {
                xtype : 'hidden',
                name : 'userId',
                id : 'userValueFieldId'
            }, {
                xtype : 'textfield',
                name : 'displayUser',
                fieldLabel : i18n.get('label.user'),
                anchor : '100%',
                maxLength : 100,
                allowBlank : false,
                blankText  : 'The user field cannot be empty',
                listeners : {
                    scope : this,
                    focus : function (field) {

                        if (this.action == 'create') {
                            /**
                             * Create a {sitools.admin.usergroups.UsersPanel} to select an existing user
                             */
                            var usersWin = new sitools.admin.usergroups.UsersPanel({
                                mode : 'selectUnique',
                                url : loadUrl.get('APP_URL') + loadUrl.get('APP_SECURITY_URL') + '/users',
                                displayField : field,
                                valueField : this.down('form').getForm().findField('userValueFieldId')

                            });
                            usersWin.show();
                        }
                    }
                }
            }, {
                xtype : 'textfield',
                fieldLabel : i18n.get('label.quota') + " ("+ i18n.get('label.quota.unit') +")",
                anchor : '100%',
                name : 'quota'
            }, {
                xtype : 'textfield',
                name : 'userStoragePath',
                fieldLabel : i18n.get('label.userStoragePath'),
                anchor : '100%',
                maxLength : 100,
                readOnly : true,
                disabled : true
            }, {
                xtype : 'numberfield',
                name : 'freeUserSpace',
                fieldLabel : i18n.get('label.freeUserSpace'),
                anchor : '100%',
                maxLength : 100,
                readOnly : true,
                disabled : true
            }, {
                xtype : 'numberfield',
                name : 'busyUserSpace',
                fieldLabel : i18n.get('label.busyUserSpace'),
                anchor : '100%',
                maxLength : 100,
                readOnly : true,
                disabled : true
            }, {
                xtype : 'textfield',
                name : 'status',
                fieldLabel : i18n.get('label.status'),
                anchor : '100%',
                maxLength : 30,
                readOnly : true,
                disabled : true
            }]
        }];
        
        this.buttons = [{
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
        
        sitools.admin.userStorage.UserStorageProp.superclass.initComponent.call(this);
    },
    
    /**
     * Save {sitools.admin.userStorage.UserStorageProp} user Storage in function of the action (create,modify)
     */
    onValidate : function () {
        var f = this.down('form').getForm();
        if (!f.isValid()) {
            Ext.Msg.alert(i18n.get('label.error'), i18n.get('warning.invalidForm'));
            return;
        }
        var putObject = {};
        putObject.userId = f.findField('userValueFieldId').getValue();
        putObject.storage = {};
        putObject.storage.quota = f.findField('quota').getValue();

        if (this.action == 'modify') {
            Ext.Ajax.request({
                url : this.url,
                method : 'PUT',
                scope : this,
                jsonData : putObject,
                success : function (ret) {
                    var data = Ext.decode(ret.responseText);
                    if (data.success === false) {
                        Ext.Msg.alert(i18n.get('label.warning'), i18n.get(data.message));
                    } else {
	                    this.close();
	                    this.store.reload();
                    }
                },
                failure : alertFailure
            });
        }
        if (this.action == 'create') {
            Ext.Ajax.request({
                url : this.url,
                method : 'POST',
                scope : this,
                jsonData : putObject,
                success : function (ret) {
                    var data = Ext.decode(ret.responseText);
                    if (data.success === false) {
                        Ext.Msg.alert(i18n.get('label.warning'), i18n.get(data.message));
                    } else {
                        this.close();
                        this.store.reload();
                    }
                    // Ext.Msg.alert(i18n.get('label.information'),
                    // i18n.get('msg.uservalidate'));
                },
                failure : alertFailure
            });
        }

    },

    /**
     * Delete the user field if the action is modify
     */
    afterRender : function () {
        sitools.admin.userStorage.UserStorageProp.superclass.afterRender.apply(this, arguments);
        if (this.action == 'modify') {
            var f = this.down('form').getForm();
            f.loadRecord(this.userStorageRec);
            f.findField("displayUser").destroy();
        }
    }

});

