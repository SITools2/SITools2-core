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
/**
 * @class Ext.ux.desktop.TaskBar
 * @extends Ext.toolbar.Toolbar
 */
Ext.define('Ext.ux.desktop.TaskBar', {
    extend: 'Ext.toolbar.Toolbar',

    requires: [
        'Ext.button.Button',
        'Ext.resizer.Splitter',
        'Ext.menu.Menu'
    ],

    alias: 'widget.taskbar',

    cls: 'ux-taskbar moduleInstances-bg',

    /**
     * The toggled button in the taskbar
     */
    activeButton: null,
    border : false,
    bodyBorder : false,
    
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
            height : 40,
            cls: 'ux-desktop-windowbar',
            border : false,
            items: [],
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
    
    onPanelBtnClick : function (btn) {
        var win = btn.win;

        if (win.minimized || win.hidden) {
            win.show(null, function() {
                btn.enable();
            });
            
        } else if (win.active) {
            win.on('hide', function() {
                btn.enable();
            }, null, {single: true});
            
        } else {
            win.toFront();
        }
    },

    addTaskButton: function (win) {
    	
    	var clickEvent = this.onPanelBtnClick;
    	if (win instanceof Ext.window.Window) {
    		clickEvent = this.onWindowBtnClick; // click event in fixe mode
    	}
    	
        var config = {
            iconCls: win.iconCls,
            enableToggle: true,
            allowDepress : false,
            toggleGroup: 'all',
            width: 150,
//            margins: '0 2 0 3',
            text: Ext.util.Format.ellipsis(win.title, 20),
            listeners: {
                click: clickEvent,
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

    removeTaskButton : function (btn) {
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

    setActiveButton : function (btn) {
        if (btn) {
            btn.toggle(true);
            this.activeButton = btn;
        } else {
            this.windowBar.items.each(function (item) {
                if (item.isButton) {
                    item.toggle(false);
                }
            });
        }
    },
    
    getActiveButton : function () {
    	return this.activeButton;
    },
    
    getNextButton : function () {
    	var nextIndex;
    	
    	var activeIndex = this.windowBar.items.indexOf(this.activeButton);
    	
    	if (this.windowBar.items.last() == this.activeButton) {
    		return; // do nothing when last button
    	} else {
    		nextIndex = activeIndex + 1;
    	}
    	
    	return this.windowBar.items.getAt(nextIndex);
    },
    
    getPreviousButton : function () {
    	var previousIndex;
    	
    	var activeIndex = this.windowBar.items.indexOf(this.activeButton);
    	
    	if (this.windowBar.items.first() == this.activeButton) {
    		return; // do nothing when first button
    	} else {
    		previousIndex = activeIndex - 1;
    	}
    	
    	return this.windowBar.items.getAt(previousIndex);
    }
});
