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
        'sitools.public.utils.Locale',
        'sitools.public.utils.UserStorage',
        'sitools.public.utils.PopupMessage',

        /* WIDGETS */
        'sitools.public.widget.StatusBar',
        'sitools.public.crypto.Base64',
        'sitools.public.version.Version',

    ],

    controllers : ['sitools.clientportal.controller.portal.PortalController'],

    init: function () {

        if (!Ext.isEmpty(Ext.util.Cookies.get('userLogin'))) {
            var auth = Ext.util.Cookies.get('hashCode');

            Ext.Ajax.defaultHeaders = {
                "Authorization": auth,
                "Accept": "application/json",
                "X-User-Agent": "Sitools"
            };
            taskCheckSessionExpired.delay(COOKIE_DURATION * 60 * 1000);

        } else {
            Ext.Ajax.defaultHeaders = {
                "Accept": "application/json",
                "X-User-Agent": "Sitools"
            };
        }
        this.initSiteMap();
    },

    // 1
    initSiteMap: function () {
        loadUrl.load('/sitools/client-user/siteMap', this.initLanguages, this);
    },

    // 2
    initLanguages: function () {
        locale.initLocale();
        locale.load(loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/statics/langues.json', this.initi18n, this);
    },

    // 3
    initi18n: function () {
        i18n.load(
            loadUrl.get("APP_URL") + loadUrl.get("APP_CLIENT_PUBLIC_URL") + '/res/i18n/' + locale.getLocale() + '/gui.properties',
            function () {
                if(!checkCookieDuration()){
                    Ext.Msg.show({
                        title: i18n.get('label.warning'),
                        msg : i18n.get("label.wrongcookieduration.configuration")
                    });
                    this.removeMask();
                    return;
                }
                this.initSql2ext();
            }
            , this);
    },

    // 4
    initSql2ext: function () {
        sql2ext.load(loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/conf/sql2ext.properties', this.initUser, this);
    },

    // 5
    initUser: function () {
        var storeUser = Ext.create('sitools.user.store.UserStore', {
            storeId: 'UserStore'
        });

        storeUser.setCustomUrl(loadUrl.get('APP_URL') + loadUrl.get('APP_USER_ROLE_URL'));

        storeUser.load({
            scope: this,
            callback: function (records, operation, success) {
                if (Ext.isEmpty(records)) {
                    storeUser.add({
                        firstName: "public",
                        identifier: "public",
                        email: "&nbsp;"
                    });
                }
                this.initProjects();
            }
        });
    },

    // 6
    initProjects : function () {
        Ext.Ajax.request({
            method : 'GET',
            url : loadUrl.get('APP_URL') + loadUrl.get('APP_PORTAL_URL') + '/projects?media=json',
            scope : this,
            success : function (response) {
                try {
                    this.projects = Ext.decode(response.responseText).data;
                } catch (err) {
                    Ext.Msg.alert(i18n.get('label.error'), err);
                }
            },
            callback : function () {
                this.loadPortalView();
            },
            failure : function (response) {
                Ext.Msg.alert('Status', i18n.get('warning.serverError'));
            }
        });
    },

    // 7
    loadPortalView : function () {
        Ext.create('sitools.clientportal.view.portal.PortalView', {
            projects : this.projects,
            languages : locale.languages
        });
    }
});
