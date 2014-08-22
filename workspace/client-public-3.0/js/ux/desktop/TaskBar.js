/*!
 * Ext JS Library 4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

/**
 * @class Ext.ux.desktop.TaskBar
 * @extends Ext.toolbar.Toolbar
 */
Ext.define('Ext.ux.desktop.TaskBar', {
    // This must be a toolbar. we rely on acquired toolbar classes and inherited toolbar methods for our
    // child items to instantiate and render correctly.
    extend: 'Ext.toolbar.Toolbar',

    requires: [
        'Ext.button.Button',
        'Ext.resizer.Splitter',
        'Ext.menu.Menu'
    ],

    alias: 'widget.taskbar',

    cls: 'ux-taskbar moduleTaskbar-bg',

    /**
     * @cfg {String} startBtnText
     * The text for the Start Button.
     */
    startBtnText: 'Start',
    border : false,
    initComponent: function () {
        var me = this;
        me.flex = 1;
        
        me.windowBar = new Ext.toolbar.Toolbar(me.getWindowBarConfig());

        me.items = [
            me.windowBar
        ];

        me.callParent();
    },

    afterLayout: function () {
        var me = this;
        me.callParent();
        me.windowBar.el.on('contextmenu', me.onButtonContextMenu, me);
    },

    getWindowBarConfig: function () {
        return {
            flex: 1,
            height : 25,
            cls: 'ux-desktop-windowbar',
            items: [ '&#160;' ],
            layout: { overflowHandler: 'Scroller' }
        };
    },

    getWindowBtnFromEl: function (el) {
        var c = this.windowBar.getChildByElement(el);
        return c || null;
    },

    onButtonContextMenu: function (e) {
        var me = this, t = e.getTarget(), btn = me.getWindowBtnFromEl(t);
        if (btn) {
            e.stopEvent();
            me.windowMenu.theWin = btn.win;
            me.windowMenu.showBy(t);
        }
    },

    onWindowBtnClick: function (btn) {
        var win = btn.win;

        if (win.minimized || win.hidden) {
            btn.disable();
            win.show(null, function() {
                btn.enable();
            });
        } else if (win.active) {
            btn.disable();
            win.on('hide', function() {
                btn.enable();
            }, null, {single: true});
            win.minimize();
        } else {
            win.toFront();
        }
    },

    addTaskButton: function(win) {
        var config = {
            iconCls: win.iconCls,
            enableToggle: true,
            toggleGroup: 'all',
            width: 45,
//            margins: '0 2 0 3',
//            text: Ext.util.Format.ellipsis(win.title, 20),
            listeners: {
                click: this.onWindowBtnClick,
                scope: this
            },
            win: win
        };

        var cmp = this.windowBar.add(config);
        cmp.toggle(true);
        
        var tooltipCfg = {
        	html : win.title,
        	target : cmp.getEl(),
        	anchor : 'bottom',
        	showDelay : 20,
        	hideDelay : 50,
        	dismissDelay : 0
        };
        Ext.create('Ext.tip.ToolTip', tooltipCfg);
        
        return cmp;
    },

    removeTaskButton: function (btn) {
        var found, me = this;
        me.windowBar.items.each(function (item) {
            if (item === btn) {
                found = item;
            }
            return !found;
        });
        if (found) {
            me.windowBar.remove(found);
        }
        return found;
    },

    setActiveButton: function(btn) {
        if (btn) {
            btn.toggle(true);
        } else {
            this.windowBar.items.each(function (item) {
                if (item.isButton) {
                    item.toggle(false);
                }
            });
        }
    }
});

///**
// * @class Ext.ux.desktop.TrayClock
// * @extends Ext.toolbar.TextItem
// * This class displays a clock on the toolbar.
// */
//Ext.define('Ext.ux.desktop.TrayClock', {
//    extend: 'Ext.toolbar.TextItem',
//
//    alias: 'widget.trayclock',
//
//    cls: 'ux-desktop-trayclock',
//
//    html: '&#160;',
//
//    timeFormat: 'g:i A',
//
//    tpl: '{time}',
//
//    initComponent: function () {
//        var me = this;
//
//        me.callParent();
//
//        if (typeof(me.tpl) == 'string') {
//            me.tpl = new Ext.XTemplate(me.tpl);
//        }
//    },
//
//    afterRender: function () {
//        var me = this;
//        Ext.Function.defer(me.updateTime, 100, me);
//        me.callParent();
//    },
//
//    onDestroy: function () {
//        var me = this;
//
//        if (me.timer) {
//            window.clearTimeout(me.timer);
//            me.timer = null;
//        }
//
//        me.callParent();
//    },
//
//    updateTime: function () {
//        var me = this, time = Ext.Date.format(new Date(), me.timeFormat),
//            text = me.tpl.apply({ time: time });
//        if (me.lastText != text) {
//            me.setText(text);
//            me.lastText = text;
//        }
//        me.timer = Ext.Function.defer(me.updateTime, 10000, me);
//    }
//});
