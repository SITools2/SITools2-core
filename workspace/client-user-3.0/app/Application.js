Ext.define('sitools.user.Application', {
    name : 'sitools.user',

    requires : [ 'Ext.container.Viewport', 
                 
                         /* UTILS */
                 'sitools.user.utils.Project', 
                 'sitools.public.utils.i18n',
                 'sitools.public.utils.loadUrl',
                 'sitools.public.utils.sql2ext',
                 'sitools.public.utils.Locale'],

    extend : 'Ext.app.Application',

    controllers : ["SitoolsController", "DesktopController", 'header.HeaderController', 'footer.FooterController' ],

    isReady : false,
    modules : null,
    useQuickTips : true,
    
    config : {
        ready : false,
        loaded : false
    },
    
    init : function () {
        var me = this, desktopCfg;
        this.addMask();
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
    
    // 1
    initSiteMap : function () {
        loadUrl.load('/sitools/client-user/siteMap', this.initLanguages, this);
    },

    // 2
    initLanguages : function () {
        locale.initLocale();
        locale.load(loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL') + '/res/statics/langues.json', this.initi18n, this);
    },
    
    // 3
    initi18n : function () {
        i18n.load(loadUrl.get("APP_URL") + loadUrl.get("APP_CLIENT_PUBLIC_URL") + '/res/i18n/' + locale.getLocale() + '/gui.properties', this.initSql2ext, this);
    },

    // 4
    initSql2ext : function () {
        this.updateMaskText();
        sql2ext.load(loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_PUBLIC_URL') +  '/conf/sql2ext.properties', this.initUser, this);
    },
    
    // 5
    initUser : function () {
        var storeUser = Ext.create('sitools.user.store.UserStore', {
            storeId : 'UserStore'
        });
        
//        var url = sitools.user.utils.Project.getSitoolsAttachementForUsers();
//        storeUser.setCustomUrl(loadUrl.get('APP_URL') + loadUrl.get('APP_USER_ROLE_URL'));
        storeUser.load({
            scope: this,
            callback: function(records, operation, success) {
                if (Ext.isEmpty(records)) {
                    storeUser.add({
                        firstName : "public", 
                        identifier : "public", 
                        email : "&nbsp;"
                    });
                }
                this.initProject();
            }
        });
    },
    
    // 6
    initProject : function () {
        sitools.user.utils.Project.init(this.projectInitialized, this);
    },
    
    
    projectInitialized : function () {
        this.setReady(true);
        this.fireEvent('projectInitialized');
    },
    
    noticeProjectLoaded : function () {
        this.setLoaded(true);
        this.fireEvent('projectLoaded');
        this.removeMask();
    },
    
    
    addMask : function () {
        this.splashScreen = Ext.getBody().mask('', 'splashscreen');
        this.splashScreen.addCls('splashscreen');
        Ext.DomHelper.insertFirst(Ext.query('.x-mask-msg')[0],{
            cls : 'x-splash-icon'
        });
    },
    updateMaskText : function () {
        Ext.dom.Query.selectNode('.x-mask-msg-text').innerHTML = i18n.get("label.loadingSitools");
    },
    removeMask : function () {
        this.splashScreen.fadeOut({
            duration : 1000,
            remove : true
        });
        
        this.splashScreen.next().fadeOut({
            duration : 1000,
            remove : true
        });
    }
   

});
