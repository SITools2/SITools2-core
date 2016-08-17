/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/* global Ext, sitools, ID, i18n, showResponse, alertFailure,clog,window,Base64 */
Ext.namespace('sitools.public.userProfile');

/*
 * defurl: default page url to load if click on Cancel button 
 * url: url to request if click on Login button 
 * handler: if request is OK then is called
 * register: url to set to Register button 
 * reset: url to set to Reset Password button
 * unblacklist : url to set to UnBlacklist button
 */

Ext.define('sitools.public.userProfile.Login', {
    extend: 'Ext.window.Window',
    alias: 'widget.s-login',
    id: 'winLogin',
    width: 420,
    height: 200,
    draggable: false,
    resizable: false,
    closable: false,
    modal: true,
    layout: 'fit',

    initComponent: function () {
        this.title = i18n.get('label.login');
        this.iconCls = 'userPersonalIcon';

        this.combo = Ext.create('Ext.form.field.ComboBox', {
            typeAhead: true,
            triggerAction: 'all',
            forceSelection: true,
            allowBlank: false,
            queryMode: 'local',
            store: Ext.create('Ext.data.ArrayStore', {
                id: 0,
                fields: ['myId', 'displayText'],
                data: [[1, i18n.get('label.userPortal')], [2, i18n.get('label.administration')]]
            }),
            valueField: 'myId',
            displayField: 'displayText',
            anchor: '80%',
            value: 1,
            fieldLabel: i18n.get('label.target'),
            hideLabel: true
        });
        if (this.chooseLocation) {
            this.combo.setVisible(true);
            this.combo.hideLabel = false;
        } else {
            this.combo.setVisible(false);
        }

        this.items = [{
            xtype: 'form',
            border: false,
            buttonAlign: 'center',
            id: 'frmLogin',
            labelWidth: 100,
            padding: "10px 10px 0px 50px",
            items: [{
                xtype: 'textfield',
                fieldLabel: i18n.get('label.login'),
                name: 'login',
                id: 'logId',
                allowBlank: false,
                anchor: '80%',
                listeners: {
                    scope: this,
                    afterrender: function (textfield) {
                        Ext.defer(textfield.focus, 500, textfield);
                    }
                }
            }, {
                xtype: 'textfield',
                fieldLabel: i18n.get('label.password'),
                name: 'password',
                id: 'pwdId',
                allowBlank: false,
                inputType: 'password',
                anchor: '80%',
                listeners: {
                    scope: this,
                    specialkey: function (field, e) {
                        if (e.getKey() == e.ENTER) {
                            this.getAuth();
                        }
                    }
                }
            }, this.combo],
            buttons: {
                xtype: 'toolbar',
                style: 'background-color:white;',
                items: [{
                    text: i18n.get('label.login'),
                    iconCls: 'loginIcon',
                    handler: this.getAuth,
                    scope: this,
                    border: false,
                    bodyBorder: false,
                    plain: true,
                    cls: 'x-custom-button-color'
                }, {
                    text: i18n.get('label.register'),
                    hidden: !this.register,
                    iconCls: 'registerIcon',
                    scope: this,
                    border: false,
                    bodyBorder: false,
                    cls: 'x-custom-button-color',
                    handler: function () {
                        this.close();
                        var register = Ext.create('sitools.public.userProfile.Register', {
                            closable: this.closable,
                            url: this.register,
                            login: this.url,
                            handler: this.handler,
                            back: this
                        });
                        register.show();
                    }
                }]
            }
        }];

        this.bbar = Ext.create('sitools.public.widget.StatusBar', {
            text: i18n.get('label.ready'),
            id: 'sbWinLogin',
            iconCls: 'x-status-valid',
//            height : 40,
            items: [{
                icon: loadUrl.get('APP_URL') + '/client-public/common/res/images/icons/wadl.png',
                iconAlign: 'right',
                text: i18n.get("label.needHelp"),
                hidden: (!this.reset || !this.unblacklist),
                scope: this,
                handler: function () {
                    this.close();
                    var reset = Ext.create('sitools.public.userProfile.lostPassword', {
                        closable: this.closable,
                        urlResetPassword: this.reset,
                        urlUnblacklist: this.unblacklist,
                        handler: this.handler,
                        back: this
                    });
                    reset.show();
                }
            }]
        });

        this.callParent(arguments);
    },

    getAuth: function () {
        /*
         * var usr = Ext.getCmp('logId').getValue(); var pwd =
         * Ext.getCmp('pwdId').getValue(); var tok = usr + ':' + pwd; var hash =
         * Base64.encode(tok); var auth = 'Basic ' + hash;
         * Ext.util.Cookies.set('hashCode', auth);
         * Ext.apply(Ext.Ajax.defaultHeaders, { "Authorization" : auth });
         * this.login();
         */

        Ext.util.Cookies.set('A1', "");
        Ext.util.Cookies.set('userLogin', "");
        Ext.util.Cookies.set('scheme', "");
        Ext.util.Cookies.set('algorithm', "");
        Ext.util.Cookies.set('realm', "");
        Ext.util.Cookies.set('nonce', "");
        Ext.util.Cookies.set('hashCode', "");
        Ext.apply(Ext.Ajax.defaultHeaders, {
            "Authorization": ""
        });

        Ext.Ajax.request({
            url: this.url,
            method: 'GET',
            scope: this,
            success: function (response, opts) {
                var Json = Ext.decode(response.responseText);
                var date = new Date();
                if (!Ext.isEmpty(Json.data)) {
                    if (Json.data.scheme == 'HTTP_Digest') {
                        var auth = new Digest({
                            usr: Ext.getCmp('logId').getValue(),
                            pwd: Ext.getCmp('pwdId').getValue(),
                            realm: Json.data.realm
                        });
                        var A1 = auth.getA1();

                        date.setMinutes(date.getMinutes() + 1);
                        // stockage en cookie du mode d'authorization
                        Ext.util.Cookies.set('A1', A1);
                        Ext.util.Cookies.set('userLogin', auth.usr, date);
                        Ext.util.Cookies.set('scheme', Json.data.scheme);
                        Ext.util.Cookies.set('algorithm', Json.data.algorithm);
                        Ext.util.Cookies.set('realm', auth.realm);
                        Ext.util.Cookies.set('nonce', Json.data.nonce);

                    } else if (Json.data.scheme == "HTTP_Basic") {
                        var usr = Ext.getCmp('logId').getValue();
                        var pwd = Ext.getCmp('pwdId').getValue();
                        var tok = usr + ':' + pwd;
                        var hash = Base64.encode(tok);
                        var auth = 'Basic ' + hash;

                        date.setMinutes(date.getMinutes() + 1);
                        // stockage en cookie du mode d'authorization
                        Ext.util.Cookies.set('userLogin', usr, date);
                        Ext.util.Cookies.set('scheme', Json.data.scheme);
                        Ext.util.Cookies.set('hashCode', auth, date);
                    }
                }

                this.login();
            },
            failure: alertFailure
        });

    },
    login: function () {
        if (!Ext.getCmp('frmLogin').getForm().isValid()) {
            Ext.getCmp('sbWinLogin').setStatus({
                text: i18n.get('warning.checkForm'),
                iconCls: 'x-status-error'
            });
            return;
        }

        Ext.getCmp('winLogin').body.mask();
        Ext.getCmp('sbWinLogin').showBusy();
        Ext.Ajax.request({
            url: this.url,
            method: 'GET',
            scope: this,
            doNotHandleRequestexception: true,
            success: function (response, opts) {
                try {
                    var Json = Ext.decode(response.responseText);
                    if (Json.success) {
                        Ext.apply(Ext.Ajax.defaultHeaders, {
                            "Authorization": Ext.util.Cookies.get('hashCode')
                        });

                        Ext.getCmp('winLogin').close();
                        // this.handler.call(this.scope || this);
                        if (this.chooseLocation) {
                            if (this.combo.getValue() == 1) {
                                window.location.href = loadUrl.get('APP_URL') + '/login-redirect?kwd=/client-user/index.html';
                                // window.location.href =
                                // "/sitools/client-user/index.html?authorization="
                                // + hash;
                            } else {
                                Ext.Ajax.request({
                                    url: loadUrl.get('APP_URL') + '/login-redirect?kwd=/client-admin',
                                    method: "GET",
                                    success: function (response) {
                                        Ext.Msg.alert('error login.js redirect with authorization');
                                    }
                                });
                            }
                        } else {
                            window.location.reload();
                        }

                    } else {
                        sitools.public.utils.Logout.logout(false);

                        var txt = i18n.get('warning.serverError') + ': ' + Json.message;
                        Ext.getCmp('winLogin').body.unmask();
                        Ext.getCmp('sbWinLogin').setStatus({text: txt, iconCls: 'x-status-error'});

                    }
                } catch (err) {
                    Ext.Msg.alert(i18n.get('label.error'), err);
                }
            },
            callback: function (options, success, response) {
                if(success) {
                    var ret = Ext.decode(response.responseText);
                    var scope = (this.scope)?this.scope:this;
                    Ext.callback(this.callback, scope, [ret.success]);
                }
            },
            failure: function (response, opts) {
                sitools.public.utils.Logout.logout(false);

                var txt;
                if (response.status == 200) {
                    var ret = Ext.decode(response.responseText).error;
                    txt = i18n.get('msg.error') + ': ' + ret;
                } else if (response.status == 403) {
                    txt = i18n.get('warning.accountLocked');
                }
                else {
                    txt = i18n.get('warning.serverError') + ': ' + response.statusText;
                }
                Ext.getCmp('winLogin').body.unmask();
                Ext.getCmp('sbWinLogin').setStatus({
                    text: txt,
                    iconCls: 'x-status-error'
                });
            }
        });
    }
});
