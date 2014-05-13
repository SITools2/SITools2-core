Ext.define('sitools.user.Application', {
    name : 'sitools.user',

    requires : [ 'Ext.container.Viewport', 
                 'sitools.user.utils.Project', 
                 'sitools.public.utils.i18n',
                 'sitools.public.utils.loadUrl' ],

    extend : 'Ext.app.Application',

    controllers : ["SitoolsController", "DesktopController", 'header.HeaderController' ],

    isReady : false,
    modules : null,
    useQuickTips : true,
    
    config : {
        ready : true
    },
    
    init : function () {
        alert("init");
        var me = this, desktopCfg;
        if (me.useQuickTips) {
            Ext.QuickTips.init();
        }
        
        if (!Ext.isEmpty(Ext.util.Cookies.get('userLogin'))) {
            var auth = Ext.util.Cookies.get('hashCode');

            Ext.Ajax.defaultHeaders = {
                "Authorization" : auth,
                "Accept" : "application/json",
                "X-User-Agent" : "Sitools"
            };
            taskCheckSessionExpired.delay(COOKIE_DURATION * 60 * 1000);
            
        } else {
            Ext.Ajax.defaultHeaders = {
                "Accept" : "application/json",
                "X-User-Agent" : "Sitools"
            };
        }
        
        this.initSiteMap();
    },

    initi18n : function () {
        i18n.load(loadUrl.get("APP_URL") + loadUrl.get("APP_CLIENT_PUBLIC_URL") + '/res/i18n/' + 'en' + '/gui.properties', this.initProject, this);
    },

    initSiteMap : function () {
        loadUrl.load('/sitools/client-user/siteMap', this.initi18n, this);

    },

    initProject : function () {
        sitools.user.utils.Project.init(this.projectReady, this);
    },
    
    projectReady : function () {
        this.setReady(true);
        this.fireEvent('projectInitialized');
    }

});
