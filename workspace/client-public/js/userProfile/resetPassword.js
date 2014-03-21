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
Ext.namespace('sitools.userProfile');

/**
 * A specific window to reset user password 
 * @cfg {boolean} closable Window config
 * @cfg {string} url The url to send reset request
 * @cfg {} handler A method to call after success 
 * @class sitools.userProfile.resetPassword
 * @extends Ext.Window
 */
sitools.userProfile.resetPassword = Ext.extend(Ext.Window, {
    id : 'winPassword',
    layout : 'hbox',
    width : 420,
    resizable : false,
    // closable: true,
    modal : true,

    initComponent : function () {
        this.title = i18n.get("label.needHelp");
        this.bbar = new Ext.ux.StatusBar({
            text : i18n.get('label.ready'),
            id : 'sbWinPassword',
            iconCls : 'x-status-valid'
        });
        this.items = [ {
            xtype : 'form',
//            frame : false,
            border : false,
            buttonAlign : 'center',
            id : 'frmResetPassword',
            bodyStyle : 'padding:10px 10px 0px 60px; background:url("'+loadUrl.get('APP_URL')+'/common/res/images/ux/login-big.gif") no-repeat;',
            width : 400,
            labelWidth : 120,
            items : [
              {
                xtype : 'radiogroup',
                // Put all controls in a single column with width 100%
                columns : 1,
                items : [ {
                    boxLabel : i18n.get("label.resetPassword"),
                    inputValue : 'password',
                    name : 'action',
                    checked : true
                }, {
                    boxLabel : i18n.get('label.unlockAccount'),
                    name : 'action',
                    inputValue : 'blacklist',
                } ]
            },
            {
                xtype : 'textfield',
                fieldLabel : i18n.get('label.login'),
                name : 'identifier',
                id : 'regLogin',
                allowBlank : false,
                anchor : '100%'
            }, {
                xtype : 'textfield',
                fieldLabel : i18n.get('label.email'),
                id : 'regEmail',
                name : 'email',
                vtype : 'uniqueemail',
                allowBlank : false,
                validationEvent : '',
                anchor : '100%'
            }, {
                xtype : 'textfield',
                fieldLabel : i18n.get('label.emailConfirm'),
                id : 'regEmailConfirm',
                name : 'emailConfirm',
                vtype : 'uniqueemail',
                allowBlank : false,
                validationEvent : '',
                anchor : '100%'
            } ],
            buttons : [ {
                text : i18n.get("label.ok"),
                handler : this.reset,
                scope : this
            }, {
                text : i18n.get("label.back"),
                scope : this,
                icon: loadUrl.get('APP_URL') + '/common/res/images/icons/refresh.png',
                handler : this.openLoginWindow
            } ]
        } ];
        sitools.userProfile.resetPassword.superclass.initComponent.call(this);
    },
    reset : function () {
        var f = Ext.getCmp('frmResetPassword').getForm();
        if (f.findField('email').getValue() != f.findField('emailConfirm').getValue()) {
            Ext.getCmp('sbWinPassword').setStatus({
                text : i18n.get('warning.checkForm'),
                iconCls : 'x-status-error'
            });
            return;
        }

        if (!f.isValid()) {
            Ext.getCmp('sbWinPassword').setStatus({
                text : i18n.get('warning.checkForm'),
                iconCls : 'x-status-error'
            });
            return;
        }
        var putObject = new Object();
        Ext.iterate(f.getValues(), function (key, value) {
            if (key != 'emailConfirm' && key!='action') {
                putObject[key] = value;
            }
            if (key == 'action' && value == "password") {
                this.url = this.urlResetPassword;
            }
            if (key == 'action' && value == "blacklist") {
                this.url = this.urlUnblacklist;
            }
        }, this);

        Ext.getCmp('winPassword').body.mask();
        Ext.getCmp('sbWinPassword').showBusy();
        Ext.Ajax.request({
            url : this.url,
            method : 'PUT',
            jsonData : putObject,
            scope : this,
            success : function (response, opts) {
                var json = Ext.decode(response.responseText);
                if (json.success) {
                    Ext.getCmp('winPassword').body.unmask();
                    Ext.getCmp('sbWinPassword').setStatus({
                        text : json.message,
                        iconCls : 'x-status-valid'
                    });
                    
                    var notify = new Ext.ux.Notification({
                        iconCls : 'x-icon-information',
                        title : i18n.get('label.information'),
                        html : i18n.get('label.passwordSent') + json.message,
                        autoDestroy : true,
                        hideDelay : 1300
                    });
                    notify.show(document);
//                    if (this.handler !== null && this.handler !== undefined) {
//                        this.handler.call(this.scope || this);
//                    }
                    this.openLoginWindow();
                } else {
                    Ext.getCmp('winPassword').body.unmask();
                    Ext.getCmp('sbWinPassword').setStatus({
                        text : json.message,
                        iconCls : 'x-status-error'
                    });

                }
            },
            failure : function (response, opts) {
                var txt;
                if (response.status == 200) {
                    var ret = Ext.decode(response.responseText).message;
                    txt = i18n.get('msg.error') + ': ' + ret;
                } else {
                    txt = i18n.get('warning.serverError') + ': ' + response.statusText;
                }
                Ext.getCmp('winPassword').body.unmask();
                Ext.getCmp('sbWinPassword').setStatus({
                    text : txt,
                    iconCls : 'x-status-error'
                });
            }
        });
    },
    openLoginWindow : function () {
        Ext.getCmp('winPassword').close();
        var login = new sitools.userProfile.Login(this.back);
        login.show();
    }
});
