/***************************************
* Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
* 
* This file is part of SITools2.
* 
* SITools2 is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* SITools2 is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
***************************************/
Ext.define('sitools.user.controller.DesktopController', {
   
    extend: 'Ext.app.Controller',
    
    requires : ['Ext.window.MessageBox',
        'Ext.ux.modules.Notepad',
        'Ext.ux.desktop.ShortcutModel', 
        'Ext.ux.modules.Settings'],
    
    views : ["desktop.Desktop"],
    
    init : function () {
        var me = this, desktopCfg;
        me.modules = me.getModules();
        if (me.modules) {
            me.initModules(me.modules);
        }
    
        desktopCfg = me.getDesktopConfig();

        Ext.apply(desktopCfg, {
            renderTo : 'x-desktop'            
        });
        
        me.desktop =  Ext.create('sitools.user.view.desktop.Desktop', desktopCfg);
        
        Ext.EventManager.on(window, 'beforeunload', me.onUnload, me);
    
        me.isReady = true;
        me.fireEvent('ready', me);
        
//        this.control({
//            
//            "menuitem[text='start']" : {
//                click : function () {
//                    var window = Ext.create("sitools.user.view.windows.Window");
//                    window.show();
//                }
//            },
//            "window" : {
//                afterrender : function (panel) {
//                    if (!Ext.isEmpty(panel.grid)) {
//                        panel.grid.getStore().load();
//                    }
//                }
//            }
//        });
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
            app: me
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
    
    getTaskbarConfig: function () {
        var me = this, cfg = {
                app: me
            };
    
            Ext.apply(cfg, me.taskbarConfig);
        return Ext.apply(cfg, {
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