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
/* global Ext, sitools, i18n,document */
Ext.namespace('sitools.public.userProfile');
/*
 * config { url + handler }
 */
Ext.define('sitools.public.userProfile.ResetPasswordPanel', {
    extend : 'Ext.panel.Panel',
	layout : "fit",
    width: 500,
    initComponent : function () {
        this.captchaUrl = loadUrl.get('APP_URL') + '/captcha?width=300&height=50';

        this.bbar = Ext.create('sitools.public.widget.StatusBar', {
            text : i18n.get('label.ready'),
            iconCls : 'x-status-valid'
        });

        this.captcha = Ext.create('Ext.Img', {
            itemId : 'captchaBox',
            src: this.captchaUrl + '&_dc=' + new Date().getTime(),
            height : 50,
            width : 200
        });
        
        this.form = Ext.create('Ext.form.Panel', {
            border : false,
            buttonAlign : 'center',
            height : 200,
            padding : 5,
            width: 400,
            defaults : {
                labelWidth : 150
            },
            items : [{
                xtype : 'textfield',
                fieldLabel : i18n.get('label.password'),
                anchor : '100%',
                inputType : 'password',
                name : 'secret',
                value : '',
                vtype : 'passwordComplexity',
                id : 'passwordField'
            }, {
                xtype : 'textfield',
                fieldLabel : i18n.get('label.confirmPassword'),
                anchor : '100%',
                inputType : 'password',
                initialPassField : 'passwordField',
                vtype : 'password',
                name : 'confirmSecret',
                submitValue : false,
                value : ''
            }, {
                fieldLabel : i18n.get('label.captcha'),
                xtype : 'fieldcontainer',
                layout : {
                    type : 'hbox'
                },
                items : [this.captcha,{
                    xtype: 'button',
                    itemId : 'reload',
                    text: i18n.get('label.captchaReload'),
                    icon : loadUrl.get('APP_URL') + '/client-public/common/res/images/icons/refresh.png',
                    arrowAlign : 'right',
                    reloadUrl : this.captchaUrl,
                    width: 100,
                    height : 30,
                    margin : '10 0 10 2',
                    handler : function (button) {
                        Ext.util.Cookies.clear('captcha');
                        var box = button.up("form").down("image");
                        box.setSrc(this.reloadUrl + '&_dc=' + new Date().getTime());
                        box.getEl().slideIn('l');
                    }
                }]
            }, {
                xtype: 'textfield',
                fieldLabel: i18n.get('label.fieldCaptcha'),
                name: 'captcha',
                id: 'captcha',
                allowBlank: false,
                anchor: '100%'
            }],
            buttons: {
                xtype: 'toolbar',
                style: 'background-color:white;',
                items: [{
                    text: i18n.get('label.saveEdit'),
                    handler: this.saveEdit,
                    scope: this,
                    style: 'background-color:white;'
                }]
            }
        });
        
        this.items = [this.form];
        
        this.callParent(arguments);
        
    },

    saveEdit : function () {
        var f = this.form.getForm();

        if (!f.isValid()) {
            this.down('statusbar').setStatus({
                text : i18n.get('warning.checkForm'),
                iconCls : 'x-status-error'
            });
            return;
        }

        var putObject = new Object();
        Ext.iterate(f.getValues(), function (key, value) {
            if (key != 'captcha') {
                putObject[key] = value;
            }
        }, this);
        
        
        var cook = Ext.util.Cookies.get('captcha');
        var capt = f.findField('captcha').getValue();
        
        this.body.mask();
        this.down('statusbar').showBusy();

        Ext.Ajax.request({
            url : loadUrl.get('APP_URL') + this.resourceUrl,
            params : {
                cdChallengeMail : this.challengeToken,
                "captcha.id" : cook,
                "captcha.key" : capt
            },
            method : 'PUT',
            jsonData : putObject,
            scope : this,
            success : function (response, opts) {
                var json = Ext.decode(response.responseText);
                if (json.success) {
                    this.ownerCt.close();

                    Ext.Msg.alert(i18n.get("label.information"), i18n.get("label.passwordChanged.success"), function() {
                        var link = loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PORTAL_URL') + "/index.html";
                        window.open(link, "_self");
                    });
                    
                } else {
                    this.body.unmask();
                     this.down('statusbar').setStatus({
                        text : json.message,
                        iconCls : 'x-status-error'
                    });

                }
                if (this.handler !== null && this.handler !== undefined) {
                    this.handler.call(this.scope || this, putObject);
                }
            },
            failure : function (response, opts) {
                var txt;
                if (response.status == 200) {
                    var ret = Ext.decode(response.responseText).message;
                    txt = i18n.get('msg.error') + ': ' + ret;
                } else if (response.status == 403){
                    txt = i18n.get('msg.wrongCaptcha');                 
                }else {
                    txt = i18n.get('warning.serverError') + ': ' + response.statusText;
                }
                this.body.unmask();
                 this.down('statusbar').setStatus({
                    text : txt,
                    iconCls : 'x-status-error'
                });
                this.reloadCaptcha();
                
            }
        });
    }, 
    
    reloadCaptcha : function () {
        Ext.util.Cookies.clear('captcha');
        var box = this.down("form image");
        var button = this.down("button#reload");
        box.setSrc(button.reloadUrl + '&_dc=' + new Date().getTime());
        box.getEl().slideIn('l');

        //clear the captcha value
        this.down("form textfield#captcha").setValue("");
    }
});
