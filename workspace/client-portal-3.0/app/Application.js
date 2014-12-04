Ext.define('sitools.clientportal.Application', {
    name: 'clientportal',
    extend: 'Ext.app.Application',

    controllers: ['sitools.clientportal.controller.portal.PortalController',
        'sitools.user.controller.component.datasets.opensearch.OpensearchController'],

    useQuickTips: true,

    requires: [

        /* UTILS */
        'sitools.public.utils.LoginDef',
        'sitools.public.utils.def',
        'sitools.public.widget.vtype',
        'sitools.public.utils.loadUrl',
        'sitools.public.utils.i18n',
        'sitools.public.utils.sql2ext',
        'sitools.public.utils.console',
        'sitools.public.utils.LoginUtils',
        'sitools.public.utils.Logout',
        'sitools.public.utils.UserStorage',
        'sitools.public.utils.PopupMessage',

        /* WIDGETS */
        'sitools.public.widget.StatusBar',
        'sitools.public.crypto.Base64',
        'sitools.public.version.Version',

        /* USER */

    ],

    launch: function () {
        Desktop = undefined;
        Ext.create('sitools.clientportal.controller.portal.PortalController');
    }

});
