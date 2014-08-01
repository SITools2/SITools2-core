/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * 
 * This file is part of SITools2.
 * 
 * SITools2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SITools2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SITools2. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
Ext.define('sitools.user.controller.DesktopController', {

    extend : 'Ext.app.Controller',

    requires : [ 'Ext.window.MessageBox', 
                 'Ext.ux.modules.Settings' ],

    views : [ 'desktop.DesktopView' ],

    init : function () {
        var me = this, desktopCfg;

        desktopCfg = me.getDesktopConfig();

        Ext.apply(desktopCfg, {
            renderTo : 'x-desktop'
        });

        me.desktopView = Ext.create('sitools.user.view.desktop.DesktopView', desktopCfg);

        Ext.EventManager.on(window, 'beforeunload', me.onUnload, me);

        me.isReady = true;
        me.fireEvent('ready', me);
    },

    createWindow : function (view, windowConfig) {
        var desktopView = this.getDesktopView();
        var win = desktopView.getWindow(windowConfig.id) || desktopView.getWindow(windowConfig.name);
        if (!win) {
            Ext.applyIf(windowConfig, {
                id : windowConfig.name,
                title : i18n.get(windowConfig.label),
                width : 600,
                height : 400,
                animCollapse : false,
                border : false,
                hideMode : 'offsets',
                layout : 'fit',
                items : [ view ]
            });
            win = desktopView.createWindow(windowConfig);
        }
        win.show();
    },

    /**
     * This method returns the configuration object for the TaskBar. A derived
     * class can override this method, call the base version to build the config
     * and then modify the returned object before returning it.
     */
    getTaskbarConfig : function () {
        var me = this, cfg = {
            app : me
        };
        Ext.apply(cfg, me.taskbarConfig);
        return cfg;
    },

    onReady : function (fn, scope) {
        if (this.isReady) {
            fn.call(scope, this);
        } else {
            this.on({
                ready : fn,
                scope : scope,
                single : true
            });
        }
    },

    getDesktopView : function () {
        return this.desktopView;
    },

    onUnload : function (e) {
        if (this.fireEvent('beforeunload', this) === false) {
            e.stopEvent();
        }
    },

    getDesktopConfig : function () {
        var me = this, cfg = {
            app : me,
            taskbarConfig : me.getTaskbarConfig()
        };

        Ext.apply(cfg, me.desktopConfig);
        return Ext.apply(cfg, {

            // cls: 'ux-desktop-black',

            contextMenuItems : [ {
                text : 'Change Settings',
                handler : me.onSettings,
                scope : me
            } ],

            wallpaper : 'resources/wallpapers/Blue-Sencha.jpg',
            wallpaperStretch : false
        });
    },

    getTaskbarConfig : function () {
        var me = this, cfg = {
            app : me
        };

        Ext.apply(cfg, me.taskbarConfig);
        return Ext.apply(cfg, {
            trayItems : [ {
                xtype : 'trayclock',
                flex : 1
            } ]
        });
    },

    onLogout : function () {
        Ext.Msg.confirm('Logout', 'Are you sure you want to logout?');
    },

    onSettings : function () {
        var dlg = Ext.create('Ext.ux.modules.Settings', {
            desktop : this.desktopView
        });
        dlg.show();
    },
    
    /**
	 * Set the height of the different elements of the desktop, according to the screen height.
	 * @private 
	 */
	layout : function () {
		var el = Desktop.getMainDesktop();
		var enteteEl = Desktop.getEnteteEl();
		var bottom = Desktop.getBottomEl();
		try {
			el.setHeight(Ext.getBody().getHeight() - enteteEl.getHeight() - bottom.getHeight())
			Desktop.getDesktopEl().setHeight(Desktop.getDesktopAndTaskBarEl().getHeight()- Desktop.getTaskbarEl().getHeight());
		}
		catch (err) {
			return;
		}
		
//		windowsGroup.each(function (win) {
//            if (win.maximized) {
//                win.fitContainer();
//            }
//            else {
//            	if (win.fitToDesktop) {
//                win.fitToDesktop();    
//            	} else {
//            		console.log("NOT A WINDOW: " + win);
//            	}
//            }
//        });
		
//		bureauEl.setHeight(Ext.lib.Dom.getViewHeight() - taskbarEl.getHeight() - topEl.getHeight())
	},
    
	maximize : function () {
    	var headerController = this.getApplication().getController('header.HeaderController');
		
//		Desktop.getEnteteComp().fireEvent("maximizeDesktop");
//		Desktop.getBottomComp().fireEvent("maximizeDesktop");
		
		headerController.onMaximizeDesktop();
//    	headerController.onMinimizeDesktop();
		
		var arrayFreeDiv = Ext.DomQuery.select("div[stype=freeDiv]");
		if (!Ext.isEmpty(arrayFreeDiv)) {
			Ext.each(arrayFreeDiv, function (freeDiv) {
				freeDiv.style.height = "0px";
				freeDiv.style.width = "0px";
			});
			
		}
		
		if (!Ext.isEmpty(Desktop.getModulesInDiv())) {
			Desktop.getModulesInDiv().each(function (moduleInDiv) {
				moduleInDiv.fireEvent("maximizeDesktop", moduleInDiv);
			});
		}
		
		//Agrandir la zone desktopAndTaskbar
		Desktop.getDesktopAndTaskBarEl().setHeight(Ext.getBody().getHeight() - Desktop.getEnteteEl().getHeight());
		Desktop.getDesktopAndTaskBarEl().setWidth(Ext.getBody().getWidth());
		Desktop.getDesktopEl().setHeight(Ext.getBody().getHeight() - Desktop.getEnteteEl().getHeight() - Desktop.getTaskbarEl().getHeight());
		Desktop.getDesktopEl().setWidth(Ext.getBody().getWidth());
		
		Desktop.getBottomEl().setHeight(Ext.getBody().getHeight() - Desktop.getEnteteEl().getHeight() - Desktop.getTaskbarEl().getHeight());
		Desktop.getTaskbarEl().setY(Ext.getBody().getHeight() - Desktop.getEnteteEl().getHeight() - Desktop.getTaskbarEl().getHeight());
		
		
		if (Desktop.getActivePanel()) {
			Desktop.getActivePanel().fireEvent("resizeDesktop", SitoolsDesk.getActivePanel());
		}
		
//		this.getManager().each(function (win) {
//			if (win.maximized) {
//				win.fitContainer();
//			}
//		});

//		SitoolsDesk.getDesktop().taskbar.tbPanel.ownerCt.doLayout();
		
	},
	
    minimize : function () {
    	var headerController = this.getApplication().getController('header.HeaderController');
    	
    	headerController.onMinimizeDesktop();
//		SitoolsDesk.getBottomComp().fireEvent("minimizeDesktop");
		
		var arrayFreeDiv = Ext.dom.Query.select("div[stype=freeDiv]");
		
		if (!Ext.isEmpty(arrayFreeDiv)) {
			Ext.each(arrayFreeDiv, function (freeDiv) {
				freeDiv.style.height = "";
				freeDiv.style.width = "";
			});
		}
		
		//Revenir à la taille initiale de la zone desktopAndTaskbar
		Desktop.getDesktopAndTaskBarEl().dom.style.height = "";
		Desktop.getDesktopAndTaskBarEl().dom.style.width = "";
		
		Desktop.getDesktopEl().setWidth("");
		
		this.layout();
		if (Desktop.getActivePanel()) {
			Desktop.getActivePanel().fireEvent("resizeDesktop", Desktop.getActivePanel());
		}
		
//		SitoolsDesk.getDesktop().taskbar.tbPanel.ownerCt.doLayout()
		
		if (!Ext.isEmpty(Desktop.getModulesInDiv())) {
			Desktop.getModulesInDiv().each(function (moduleInDiv) {
				moduleInDiv.fireEvent("minimizeDesktop", moduleInDiv);
			});
		}
	}
});