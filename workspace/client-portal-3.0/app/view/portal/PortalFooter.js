Ext.namespace('sitools.clientportal.view.portal');
Ext.define('sitools.clientportal.view.portal.PortalFooter', {
    extend : 'Ext.panel.Panel',
    region: 'center',
    height: 70,
    bodyStyle: "background-image:url(/sitools/client-portal/resources/images/footer.png) !important",
    border: false,
    items: [{
        xtype: 'toolbar',
        cls: 'bg-transparent-3',
        items: [{
            xtype: 'button',
            id: 'footerContactBtn',
            text: i18n.get('label.contact'),
            iconCls: 'contactIcon',
            textAlign: 'left',
            handler: function() {
                Ext.create('sitools.clientportal.view.portal.PortalContact', {});
            }
        }, {
            xtype: 'button',
            text: 'Lien',
            icon : loadUrl.get('APP_URL') + '/client-public/res/images/icons/logo_fav_icone.png',
            textAlign: 'left',
            handler: function (button, event) {
                Ext.util.openLink('/sitools/client-portal/resources/html/fr/link.html');
            }
        }, {
            xtype: 'button',
            text: 'Aide',
            icon : loadUrl.get('APP_URL') + '/client-public/res/images/icons/wadl.png',
            textAlign: 'left',
            handler: function (button, event) {
                Ext.util.openLink('/sitools/client-portal/resources/html/fr/help.html');
            }
        }]
    }]
});

