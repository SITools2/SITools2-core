Ext.define('clientuser.Application', {
    name : 'clientuser',

    requires : [ 
        'Ext.container.Viewport',
        'Ext.ux.desktop.Desktop',
        'Ext.window.MessageBox',
        'Ext.ux.modules.Notepad',
        'Ext.ux.desktop.ShortcutModel', 
        'Ext.ux.modules.Settings' 
    ],

    extend : 'Ext.app.Application',

    controllers : [ "DesktopController" ],

    isReady: false,
    modules: null,
    useQuickTips: true,
    
    launch : function() {
        var me = this, desktopCfg;
    
        if (me.useQuickTips) {
            Ext.QuickTips.init();
        }
    
        me.modules = me.getModules();
        if (me.modules) {
            me.initModules(me.modules);
        }
    
        desktopCfg = me.getDesktopConfig();
        console.log(desktopCfg);
        Ext.apply(desktopCfg, {
            region : "center"
        });
        me.desktop =  Ext.create('Ext.ux.desktop.Desktop', desktopCfg);
        
        me.viewport = Ext.create('Ext.container.Viewport', {
            layout: 'fit',
            items: [me.desktop ]
        });
    
        Ext.EventManager.on(window, 'beforeunload', me.onUnload, me);
    
        me.isReady = true;
        me.fireEvent('ready', me);
    },
    
    
    
    createWindow: function(module) {
        var window = module.createWindow();
        window.show();
    },
    
    /**
     * This method returns the configuration object for the TaskBar. A derived class
     * can override this method, call the base version to build the config and then
     * modify the returned object before returning it.
     */
    getTaskbarConfig: function () {
        var me = this, cfg = {
            app: me,
            startConfig: me.getStartConfig()
        };
    
        Ext.apply(cfg, me.taskbarConfig);
        return cfg;
    },
    
    initModules : function(modules) {
        var me = this;
        Ext.each(modules, function (module) {
            module.app = me;
        });
    },
    
    getModule : function(name) {
        var ms = this.modules;
        for (var i = 0, len = ms.length; i < len; i++) {
            var m = ms[i];
            if (m.id == name || m.appType == name) {
                return m;
            }
        }
        return null;
    },
    
    onReady : function(fn, scope) {
        if (this.isReady) {
            fn.call(scope, this);
        } else {
            this.on({
                ready: fn,
                scope: scope,
                single: true
            });
        }
    },
    
    getDesktop : function() {
        return this.desktop;
    },
    
    onUnload : function(e) {
        if (this.fireEvent('beforeunload', this) === false) {
            e.stopEvent();
        }
    },
    
    
    getModules : function(){
        return [
            Ext.create('Ext.ux.modules.Notepad')
    
        ];
    },
    
    getDesktopConfig: function () {
        var me = this, cfg = {
                app: me,
                taskbarConfig: me.getTaskbarConfig()
            };
    
        Ext.apply(cfg, me.desktopConfig);
        return  Ext.apply(cfg, {
            
            //cls: 'ux-desktop-black',
    
            contextMenuItems: [
                { text: 'Change Settings', handler: me.onSettings, scope: me }
            ],
    
            shortcuts: Ext.create('Ext.data.Store', {
                model: 'Ext.ux.desktop.ShortcutModel',
                data: [
    
                    { name: 'Notepad', iconCls: 'notepad-shortcut', module: 'notepad' }
                ]
            }),
    
            wallpaper: 'resources/wallpapers/Blue-Sencha.jpg',
            wallpaperStretch: false
        });
    },
    
    // config for the start menu
    getStartConfig : function() {
        var me = this,
            cfg = {
                app: me,
                menu: []
            },
            launcher;
    
        Ext.apply(cfg, me.startConfig);
    
        Ext.each(me.modules, function (module) {
            launcher = module.launcher;
            if (launcher) {
                launcher.handler = launcher.handler || Ext.bind(me.createWindow, me, [module]);
                cfg.menu.push(module.launcher);
            }
        });
    
        cfg.menu.push({text: 'start'});
        
        return Ext.apply(cfg, {
            title: 'Don Griffin',
            iconCls: 'user',
            height: 300,
            toolConfig: {
                width: 100,
                items: [
                    {
                        text:'Settings',
                        iconCls:'settings',
                        handler: me.onSettings,
                        scope: me
                    },
                    '-',
                    {
                        text:'Logout',
                        iconCls:'logout',
                        handler: me.onLogout,
                        scope: me
                    }
                ]
            }
        });
    },
    
    getTaskbarConfig: function () {
        var me = this, cfg = {
                app: me,
                startConfig: me.getStartConfig()
            };
    
            Ext.apply(cfg, me.taskbarConfig);
        return Ext.apply(cfg, {
            quickStart: [
                { name: 'Accordion Window', iconCls: 'accordion', module: 'acc-win' },
                { name: 'Grid Window', iconCls: 'icon-grid', module: 'grid-win' }
            ],
            trayItems: [
                { xtype: 'trayclock', flex: 1 }
            ]
        });
    },
    
    onLogout: function () {
        Ext.Msg.confirm('Logout', 'Are you sure you want to logout?');
    },
    
    onSettings: function () {
        var dlg = Ext.create('Ext.ux.modules.Settings', {
            desktop: this.desktop
        });
        dlg.show();
    }
});
