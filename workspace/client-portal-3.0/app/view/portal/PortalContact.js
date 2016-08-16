Ext.namespace('sitools.clientportal.view.portal');

Ext.define('sitools.clientportal.view.portal.PortalContact', {
    extend : 'Ext.window.Window',
    alias: 'widget.contact',
    layout: 'fit',
    width: 500,
    height: 440,
    border: 1,
    margin: 1,
    resizable: false,
    modal: true,
    autoShow: true,
    autoRender: true,
    initComponent: function () {
        this.title = i18n.get('label.contact');
        this.captchaUrl = loadUrl.get('APP_URL') + '/captcha?width=300&height=50';
        this.captcha = Ext.create('Ext.Img', {
            itemId : 'captchaBox',
            src: this.captchaUrl + '&_dc=' + new Date().getTime(),
            height : 50,
            width : 200         
        });
        
        this.bbar = Ext.create('sitools.public.widget.StatusBar', {
            text: i18n.get('label.ready'),
            iconCls: 'x-status-valid'
        });
        this.iconCls = 'contactIcon';
        this.items = [{
            xtype: 'form',
            url: '/contact',
            layout: 'anchor',
            defaults: {
                anchor: '100%'
            },
            bodyPadding : 15,
            border : false,
            bodyBorder : false,
            items: [{
                xtype: 'textfield',
                name: 'name',
                fieldLabel: 'Nom',
                allowBlank: false
            },{
                xtype: 'textfield',
                name: 'email',
                fieldLabel: 'Email',
                allowBlank: false,
                vtype: 'email'
            },{
                xtype: 'textareafield',
                name: 'message',
                fieldLabel: 'Message',
                allowBlank: false,
                rows: 9
            }, {
                fieldLabel : i18n.get('label.captcha'),
                xtype : 'fieldcontainer',
                layout : {
                    type : 'hbox'
                },
                items : [this.captcha, {
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
                itemId: 'captcha',
                allowBlank: false
            }],
            buttons: {
                xtype: 'toolbar',
                style : 'background-color:white;',
                items : [{
                    text: i18n.get('label.submit'),
                    name: 'contactSubmitBtn'
                },{
                    text: i18n.get('label.cancel'),
                    handler: function() {
                        this.up('window').close();
                    }
                }]
            }
        }];
        this.callParent(arguments);
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
