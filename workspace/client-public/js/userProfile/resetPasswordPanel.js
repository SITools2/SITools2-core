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
Ext.namespace('sitools.userProfile');
/*
 * config { url + handler }
 */
Ext.define('sitools.userProfile.resetPasswordPanel', {
    extend : 'Ext.Panel',
	padding : "10px 10px 0px 60px",
	frame : true,
	layout : "fit", 

    initComponent : function () {
        this.captchaUrl = loadUrl.get('APP_URL') + '/captcha?width=300&height=50';

    	this.bodyStyle = "background-image: url("+loadUrl.get('APP_URL')+"/common/res/images/ux/login-big.gif);" +
    			"background-position: top left;" +
    			"background-repeat: no-repeat;";

//        this.title = i18n.get('label.editProfile');
        this.bbar = new Ext.ux.StatusBar({
            text : i18n.get('label.ready'),
            iconCls : 'x-status-valid',
            id : 'sbWinRegister'
        });
        
        this.captcha = new Ext.BoxComponent({
            id : 'captchaBox',
            autoEl: {
                tag: 'img',
                src: this.captchaUrl + '&_dc=' + new Date().getTime()
            },
            fieldLabel : i18n.get('label.captcha'),
            height : 50,
            width : 300,
            anchor: '100%'
        });
        
        
        this.form = new Ext.form.FormPanel({
            border : false,
            buttonAlign : 'center',
            id : 'frmEditProfile',
            labelWidth : 120,
            height : 200,
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
            }, 
                this.captcha,
            {
                xtype: 'button',
                text: i18n.get('label.captchaReload'),
                icon : loadUrl.get('APP_URL') + '/common/res/images/icons/refresh.png',
                x : 150,
                arrowAlign : 'right',
                reloadUrl : this.captchaUrl,
                handler : this.reloadCaptcha,
                scope : this
            }, {
                xtype: 'textfield',
                fieldLabel: i18n.get('label.fieldCaptcha'),
                name: 'captcha',
                id: 'captcha',
                allowBlank: false,
                anchor: '100%'
            }],
            buttons : [ {
                text : i18n.get('label.saveEdit'),
                handler : this.saveEdit,
                scope : this
            }]
        });
        
        this.items = [this.form];
        
        sitools.userProfile.resetPasswordPanel.superclass.initComponent.call(this);
        
    },

    saveEdit : function () {
        var f = this.form.getForm();

        if (!f.isValid()) {
            Ext.getCmp('sbWinRegister').setStatus({
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
        Ext.getCmp('sbWinRegister').showBusy();

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
                        var link = loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_USER_URL') + "/index.html";
                        window.open(link, "_self");
                    });
                    
                } else {
                    this.body.unmask();
                     Ext.getCmp('sbWinRegister').setStatus({
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
                 Ext.getCmp('sbWinRegister').setStatus({
                    text : txt,
                    iconCls : 'x-status-error'
                });
                this.reloadCaptcha();
                
            }
        });
    }, 
    reloadCaptcha : function () {
        Ext.util.Cookies.clear('captcha');
        var box = Ext.get('captchaBox');
        box.dom.src = this.captchaUrl + '&_dc=' + new Date().getTime();
        box.slideIn('l');
        
        var f = this.form.getForm();
        var capt = f.findField('captcha').setValue("");

        
    }

});
