Ext.define('sitools.clientportal.Application', {
    name : 'clientportal',
    extend : 'Ext.app.Application',

    controllers : ['sitools.clientportal.controller.portal.PortalController'],

    useQuickTips: true,
    
    requires : [
                /* UTILS */
        'sitools.public.utils.loadUrl',
        'sitools.public.utils.i18n',
        'sitools.public.utils.sql2ext',
        'sitools.public.utils.console',
        'sitools.public.utils.LoginUtils',
        
                /* WIDGETS */
        'sitools.public.widget.StatusBar',
        
        'sitools.public.crypto.Base64',
        
        'sitools.public.version.Version'
    ],
    
    launch : function() {
//        Ext.create('sitools.clientportal.view.portal.Portal');
        Ext.create('sitools.clientportal.controller.portal.PortalController');
    }
    
});
