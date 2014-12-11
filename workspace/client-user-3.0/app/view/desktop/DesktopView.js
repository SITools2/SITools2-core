/*!
 * Ext JS Library 4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

/**
 * @class Ext.ux.desktop.DesktopView
 * @extends Ext.panel.Panel
 * <p>This class manages the wallpaper, shortcuts and taskbar.</p>
 */
Ext.define('sitools.user.view.desktop.DesktopView', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.desktop',
    
    requires : [ 'sitools.user.view.header.ButtonTaskBarView', 'sitools.user.view.header.LeftTaskBarView' ],

    uses: [
        'Ext.util.MixedCollection',
        'Ext.menu.Menu',
        'Ext.view.View', // dataview
        'Ext.window.Window',
        'Ext.ux.desktop.TaskBar',
        'Ext.ux.desktop.Wallpaper'
    ],

    activeWindowCls: 'ux-desktop-active-win',
    inactiveWindowCls: 'ux-desktop-inactive-win',
    lastActiveWindow: null,

    border: false,
    html: '&#160;',
    layout: 'fit',

    xTickSize: 1,
    yTickSize: 1,
    
    height : 50,

    app: null,
    
    /**
     * @cfg {Array|Store} shortcuts
     * The items to add to the DataView. This can be a {@link Ext.data.Store Store} or a
     * simple array. Items should minimally provide the fields in the
     * {@link Ext.ux.desktop.ShorcutModel ShortcutModel}.
     */
    shortcuts: null,

    /**
     * @cfg {String} shortcutItemSelector
     * This property is passed to the DataView for the desktop to select shortcut items.
     * If the {@link #shortcutTpl} is modified, this will probably need to be modified as
     * well.
     */
    shortcutItemSelector: 'div.ux-desktop-shortcut',

    /**
     * @cfg {String} shortcutTpl
     * This XTemplate is used to render items in the DataView. If this is changed, the
     * {@link shortcutItemSelect} will probably also need to changed.
     */
    shortcutTpl: [
        '<tpl for=".">',
            '<div class="ux-desktop-shortcut" id="{name}-shortcut">',
                '<div class="ux-desktop-shortcut-icon {iconCls}">',
                    '<img src="',Ext.BLANK_IMAGE_URL,'" title="{name}">',
                '</div>',
                '<span class="ux-desktop-shortcut-text">{name}</span>',
            '</div>',
        '</tpl>',
        '<div class="x-clear"></div>'
    ],

    /**
     * @cfg {Object} taskbarConfig
     * The config object for the TaskBar.
     */
    taskbarConfig: null,

    windowMenu: null,
    
    cls : 'x-panel-body-silver',

    initComponent: function () {
        var me = this;
        me.windowMenu = Ext.create("Ext.menu.Menu", me.createWindowMenu());

        var navBarInstances = Ext.create("Ext.ux.desktop.TaskBar", me.taskbarConfig);
        
        this.navToolbarButtons = Ext.create('sitools.user.view.header.ButtonTaskBarView', {
            desktopController : this.desktopController,
            width : 90 // width without save button
        });
        
        this.leftTaskbar = Ext.create('sitools.user.view.header.LeftTaskBarView', {
        	desktopController : this.desktopController,
        	width : 90,
            observer : this
        });
        
        this.navBarModules = Ext.create('sitools.user.view.header.ModuleToolbar');
        
        this.NavBarsPanel = Ext.create('Ext.panel.Panel', {
        	name : 'navbarPanels',
        	cls : 'allTaskbars-bg',
            border : false,
            layout : {
                type : "hbox",
                align : "stretch"
            },
//            items : [ this.navBarModule, this.taskbar, this.navToolbarButtons ]
            items : [ this.navBarModules, this.navToolbarButtons ]
        });

        me.taskbar = navBarInstances
        me.tbar = this.NavBarsPanel;
        me.bbar = Ext.create('Ext.panel.Panel', {
            cls : 'ux-taskbar moduleInstances-bg',
            border : false,
            layout : {
                type : "hbox",
                align : "stretch"
            },
            items : [this.leftTaskbar, navBarInstances]
        });

        me.taskbar.windowMenu = me.windowMenu;
        me.windows = Ext.create("Ext.util.MixedCollection");
        me.contextMenu = Ext.create("Ext.menu.Menu", me.createDesktopMenu());

        me.items = [
            { xtype: 'wallpaper', id: 'wallpaperId', taskbar : this.taskbar }
        ];

        me.callParent(arguments);

        var wallpaper = me.wallpaper;
        me.wallpaper = me.items.getAt(0);

        if (wallpaper) {
            if (!Ext.isEmpty(Project.preferences) && !Ext.isEmpty(Project.preferences.projectSettings.wallpaper)) {
                var wallpaper = Project.preferences.projectSettings.wallpaper;
                me.setWallpaper(wallpaper.src, wallpaper.stretch);
            } else {
                me.setWallpaper(wallpaper, me.wallpaperStretch);
            }
        }
    },

    afterRender: function () {
        var me = this;
        me.callParent();
        
        me.el.on('contextmenu', me.onDesktopMenu, me);
        this.fitDesktop();
    },
    
    /**
     * Set the height of the different elements of the desktop, according to the screen height.
     * @private 
     */
    fitDesktop : function () {
        var el = Desktop.getMainDesktop();

        var enteteEl = Desktop.getEnteteEl();
        var bottomEl = Desktop.getBottomEl();
        
        var desktopEl = Desktop.getDesktopEl();
        
        el.setHeight(Ext.getBody().getHeight() - enteteEl.getHeight() - bottomEl.getHeight());
        var desktopHeight = Desktop.getDesktopAndTaskBarEl().getHeight();

        desktopEl.setHeight(desktopHeight);
       
        this.setHeight(desktopHeight);
    },

    //------------------------------------------------------
    // Overrideable configuration creation methods

    createDesktopMenu: function () {
        var me = this, ret = {
    		border : false,
            items: me.contextMenuItems || []
        };

        if (Project.getNavigationMode() == 'desktop') {
            if (ret.items.length) {
                ret.items.push('-');
            }

            ret.items.push(
                { text: i18n.get('label.tile'), handler: me.tileWindows, scope: me, minWindows: 1 },
                { text: i18n.get('label.cascade'), handler: me.cascadeWindows, scope: me, minWindows: 1 })
        }

        return ret;
    },

    createWindowMenu: function () {
        var me = this;
        return {
//            defaultAlign: 'bl-l?',
            border : false,
            plain : true,
            items: [
                { text: i18n.get('label.restore'), handler: me.onWindowMenuRestore, scope: me },
                { text: i18n.get('label.minimize'), handler: me.onWindowMenuMinimize, scope: me },
                { text: i18n.get('label.maximize'), handler: me.onWindowMenuMaximize, scope: me },
                '-',
                { text: i18n.get('label.close'), handler: me.onWindowMenuClose, scope: me }
            ],
            listeners: {
                beforeshow: me.onWindowMenuBeforeShow,
                hide: me.onWindowMenuHide,
                scope: me
            }
        };
    },

    //------------------------------------------------------
    // Event handler methods

    onDesktopMenu: function (e) {
        var me = this, menu = me.contextMenu;
        e.stopEvent();
        if (!menu.rendered) {
            menu.on('beforeshow', me.onDesktopMenuBeforeShow, me);
        }
        menu.showAt(e.getXY());
        menu.doConstrain();
    },

    onDesktopMenuBeforeShow: function (menu) {
        var me = this, count = me.windows.getCount();

        menu.items.each(function (item) {
            var min = item.minWindows || 0;
            item.setDisabled(count < min);
        });
    },

    onWindowClose: function(win) {
        var me = this;
        me.windows.remove(win);
        me.taskbar.removeTaskButton(win.taskButton);
        me.updateActiveWindow();
    },

    //------------------------------------------------------
    // Window context menu handlers

    onWindowMenuBeforeShow: function (menu) {
        var items = menu.items.items, win = menu.theWin;
        items[0].setDisabled(win.maximized !== true && win.hidden !== true); // Restore
        items[1].setDisabled(win.minimized === true); // Minimize
        items[2].setDisabled(win.maximized === true || win.hidden === true); // Maximize
    },

    onWindowMenuClose: function () {
        var me = this, win = me.windowMenu.theWin;

        win.show();
        win.close();
    },

    onWindowMenuHide: function (menu) {
        Ext.defer(function() {
            menu.theWin = null;
        }, 1);
    },

    onWindowMenuMaximize: function () {
        var me = this, win = me.windowMenu.theWin;

        win.maximize();
        win.toFront();
    },

    onWindowMenuMinimize: function () {
        var me = this, win = me.windowMenu.theWin;

        if (win.xtype == 'window') {
        	win.minimize();
		} else if (win instanceof Ext.panel.Panel) {
			win.hide();
		}
    },

    onWindowMenuRestore: function () {
        var me = this, win = me.windowMenu.theWin;

        me.restoreWindow(win);
    },

    //------------------------------------------------------
    // Dynamic (re)configuration methods

    getWallpaper: function () {
        return this.wallpaper.wallpaper;
    },

    setTickSize: function(xTickSize, yTickSize) {
        var me = this,
            xt = me.xTickSize = xTickSize,
            yt = me.yTickSize = (arguments.length > 1) ? yTickSize : xt;

        me.windows.each(function(win) {
            var dd = win.dd, resizer = win.resizer;
            dd.xTickSize = xt;
            dd.yTickSize = yt;
            resizer.widthIncrement = xt;
            resizer.heightIncrement = yt;
        });
    },

    setWallpaper: function (wallpaper, stretch) {
        this.wallpaper.setWallpaper(wallpaper, stretch);
        return this;
    },

    //------------------------------------------------------
    // Window management methods

    cascadeWindows: function() {
        var x = 0, y = 0,
            zmgr = this.getDesktopZIndexManager();

        zmgr.eachBottomUp(function(win) {
            if (win.isWindow && win.isVisible() && !win.maximized) {
                win.setPosition(x, y);
                x += 20;
                y += 20;
            }
        });
    },

    createWindow: function(config, cls) {
        var me = this, win, cfg = Ext.applyIf(config || {}, {
                stateful: false,
                isWindow: true,
//                constrainHeader: true,
                constrain : true,
                minimizable: true,
                maximizable: true,
                listeners : {
                	resizeDesktop : function (me, newW, newH) {
                		var deskWidth = Desktop.getDesktopEl().getWidth();
                		var deskHeight = Desktop.getDesktopEl().getHeight() - me.up('desktop').down('taskbar').getHeight() - me.up('desktop').down('moduleToolbar').getHeight();
                		
                		if (me.getWidth() > deskWidth) {
                			me.setWidth(deskWidth);
                		}
                		
                		if (me.getHeight() > deskHeight) {
                			me.setHeight(deskHeight);
                		}
                        
                        var child = me.items.items[0];
                        if (child && child.getEl()) {
                            child.fireEvent("resize", child, me.body.getWidth(), me.body.getHeight(), child.getWidth(), child.getHeight());
                        }
                    }
                }
            });

        cls = cls || Ext.window.Window;
        win = me.add(new cls(cfg));

        me.windows.add(win);

        win.taskButton = me.taskbar.addTaskButton(win);
        win.animateTarget = win.taskButton.el;

        win.on({
            activate: me.updateActiveWindow,
            beforeshow: me.updateActiveWindow,
            deactivate: me.updateActiveWindow,
            minimize: me.minimizeWindow,
            destroy: me.onWindowClose,
            scope: me
        });

        win.on({
            boxready: function () {
                win.dd.xTickSize = me.xTickSize;
                win.dd.yTickSize = me.yTickSize;

                if (win.resizer) {
                    win.resizer.widthIncrement = me.xTickSize;
                    win.resizer.heightIncrement = me.yTickSize;
                }
            },
            single: true
        });

        // replace normal window close w/fadeOut animation:
        win.doClose = function ()  {
            win.doClose = Ext.emptyFn; // dblclick can call again...
            win.el.disableShadow();
            win.el.fadeOut({
                listeners: {
                    afteranimate: function () {
                        win.destroy();
                    }
                }
            });
        };

        return win;
    },
    
    createPanel: function(config, cls) {
        var me = this, win, cfg = Ext.applyIf(config || {}, {
                stateful: false,
                constrain : true,
                border : false,
                bodyBorder : false,
                constrainHeader: true,
                floating : true,
                listeners : {
                	resizeDesktop : function (me, newW, newH) {
                		var deskWidth = Desktop.getDesktopEl().getWidth();
                		var deskHeight = Desktop.getDesktopEl().getHeight() - me.up('desktop').down('taskbar').getHeight() - me.up('desktop').down('moduleToolbar').getHeight();
                		
                		me.setWidth(deskWidth);
                		me.setHeight(deskHeight);
                        
                        var child = me.items.items[0];
                        if (child && child.getEl()) {
                            child.fireEvent("resize", child, me.body.getWidth(), me.body.getHeight(), child.getWidth(), child.getHeight());
                        }
                    }
                }
            });

        cls = cls || Ext.panel.Panel;
        win = me.add(new cls(cfg));

        me.windows.add(win);

        win.taskButton = me.taskbar.addTaskButton(win);
        win.animateTarget = win.taskButton.el;

        win.on({
            activate: me.updateActiveWindow,
            beforeshow: me.updateActiveWindow,
            deactivate: me.updateActiveWindow,
            minimize: me.minimizeWindow,
            destroy: me.onWindowClose,
            scope: me
        });
        
        win.on({
            single: true
        });

        // replace normal window close w/fadeOut animation:
        win.doClose = function ()  {
            win.doClose = Ext.emptyFn; // dblclick can call again...
            win.el.disableShadow();
            win.el.fadeOut({
                listeners: {
                    afteranimate: function () {
                        win.destroy();
                    }
                }
            });
        };

        return win;
    },

    getActiveWindow: function () {
        var win = null,
            zmgr = this.getDesktopZIndexManager();

        if (zmgr) {
            // We cannot rely on activate/deactive because that fires against non-Window
            // components in the stack.

            zmgr.eachTopDown(function (comp) {
            	// have to also manage Panel in fixe mode
                if ((comp.isWindow || comp instanceof Ext.panel.Panel)  && !comp.hidden) {
                    win = comp;
                    return false;
                }
                return true;
            });
        }

        return win;
    },

    getDesktopZIndexManager: function () {
        var windows = this.windows;
        // TODO - there has to be a better way to get this...
        return (windows.getCount() && windows.getAt(0).zIndexManager) || null;
    },

    getWindow: function(id) {
        return this.windows.get(id);
    },

    minimizeWindow: function(win) {
        win.minimized = true;
        win.hide();
    },

    restoreWindow: function (win) {
    	
    	if (win.xtype == 'window' || win instanceof Ext.panel.Panel) {
    		win.show();
    		win.toFront();
    	}
    	
//        if (win.isVisible()) {
//            win.restore();
//            win.toFront();
//        } else {
//            win.show();
//        }
        return win;
    },

    tileWindows: function() {
        var me = this, availWidth = me.body.getWidth(true);
        var x = me.xTickSize, y = me.yTickSize, nextY = y;

        me.windows.each(function(win) {
            if (win.isVisible() && !win.maximized) {
                var w = win.el.getWidth();

                // Wrap to next row if we are not at the line start and this Window will
                // go off the end
                if (x > me.xTickSize && x + w > availWidth) {
                    x = me.xTickSize;
                    y = nextY;
                }

                win.setPosition(x, y);
                x += w + me.xTickSize;
                nextY = Math.max(nextY, y + win.el.getHeight() + me.yTickSize);
            }
        });
    },

    updateActiveWindow: function () {
        var me = this, activeWindow = me.getActiveWindow(), last = me.lastActiveWindow;
        if (activeWindow === last) {
            return;
        }

        if (last) {
            if (!Ext.isEmpty(last.el) && !Ext.isEmpty(last.el.dom)) {
                last.addCls(me.inactiveWindowCls);
                last.removeCls(me.activeWindowCls);
            }
            last.active = false;
        }

        me.lastActiveWindow = activeWindow;

        if (activeWindow) {
            activeWindow.addCls(me.activeWindowCls);
            activeWindow.removeCls(me.inactiveWindowCls);
            activeWindow.minimized = false;
            activeWindow.active = true;
        }

        me.taskbar.setActiveButton(activeWindow && activeWindow.taskButton);
    }
});
