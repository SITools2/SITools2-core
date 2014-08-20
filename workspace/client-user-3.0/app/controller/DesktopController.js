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
    	this.id = 'DesktopControllerId';
    	
        var me = this, desktopCfg;

        desktopCfg = me.getDesktopConfig();

        Ext.apply(desktopCfg, {
            renderTo : 'x-desktop'
        }, {
        	desktopController : me
        });

        me.desktopView = Ext.create('sitools.user.view.desktop.DesktopView', desktopCfg);

        Ext.EventManager.on(window, 'beforeunload', me.onUnload, me);
        Ext.EventManager.onWindowResize(this.fireResize, this);

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
                bodyBorder : false,
                hideMode : 'offsets',
                layout : 'fit',
                specificType : 'moduleWindow',
                items : [ view ]
            });
            
            Ext.apply(windowConfig, this.getStatefullWindowConfig(this));
            
            win = desktopView.createWindow(windowConfig);
        }
        
        Desktop.setActivePanel(win);
        win.on('beforeclose', function (winToClose) {
        	Desktop.setActivePanel(null);
        }, this);
        
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

            wallpaper : 'resources/wallpapers/space.jpg',
            wallpaperStretch : false
        });
    },

    getTaskbarConfig : function () {
        var me = this, cfg = {
            app : me
        };

        Ext.apply(cfg, me.taskbarConfig);
        return cfg;
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
    	var footerController = this.getApplication().getController('footer.FooterController');
		
    	
//		Desktop.getEnteteComp().fireEvent("maximizeDesktop");
//		Desktop.getBottomComp().fireEvent("maximizeDesktop");
		
		headerController.onMaximizeDesktop();
    	footerController.onMaximizeDesktop();
		
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
		
		var desktopMaxHeight = Ext.getBody().getHeight()/* - Desktop.getEnteteEl().getHeight() - this.desktopView.taskbar.getHeight()*/;
		var desktopMaxWidth = Ext.getBody().getWidth();
		
		var desktopTaskBarMaxHeight = Ext.getBody().getHeight()/* - Desktop.getEnteteEl().getHeight() - this.desktopView.taskbar.getHeight()*/;
			
		//Agrandir la zone desktopAndTaskbar
		Desktop.getDesktopAndTaskBarEl().setHeight(Ext.getBody().getHeight()/* - Desktop.getEnteteEl().getHeight()*/);
		Desktop.getDesktopAndTaskBarEl().setWidth(Ext.getBody().getWidth());
		Desktop.getDesktopEl().setHeight(desktopMaxHeight);
		Desktop.getDesktopEl().setWidth(desktopMaxWidth);
		
		Desktop.getBottomEl().setHeight(desktopTaskBarMaxHeight);
		
		// useless for the moment
		//Desktop.getTaskbarEl().setY(Ext.getBody().getHeight() - Desktop.getEnteteEl().getHeight() - Desktop.getTaskbarEl().getHeight());
		
		Desktop.getMainDesktop().setHeight(desktopMaxHeight);
		
		this.desktopView.fitDesktop();
		
		if (Desktop.getActivePanel() && Desktop.getActivePanel().maximized) {
			Desktop.getActivePanel().setHeight(desktopMaxHeight);
			Desktop.getActivePanel().setWidth(desktopMaxWidth);
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
    	var footerController = this.getApplication().getController('footer.FooterController');

    	
    	headerController.onMinimizeDesktop();
    	footerController.onMinimizeDesktop();
		
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
		
		
//		SitoolsDesk.getDesktop().taskbar.tbPanel.ownerCt.doLayout()
		
		this.desktopView.fitDesktop();
		
		this.desktopView.windows.each(function (win) {
			if (win.getHeight() > Desktop.getDesktopEl().getHeight()) {
				win.setHeight(Desktop.getDesktopEl().getHeight() - this.desktopView.taskbar.getHeight());
			}
			if (win.getWidth() > Desktop.getDesktopEl().getWidth()) {
				win.setWidth(Desktop.getDesktopEl().getWidth());
			}
		}, this);
		
		if (!Ext.isEmpty(Desktop.getModulesInDiv())) {
			Desktop.getModulesInDiv().each(function (moduleInDiv) {
				moduleInDiv.fireEvent("minimizeDesktop", moduleInDiv);
			});
		}
	},
	
	/**
	 * Called on a desktop Resize. 
	 * It will redefine the height and size of desktop Element. 
	 * Fires events for each component so that they can resize according to their container
	 * Fires event resizeDesktop on activePanel
	 * Fires event resize on entete and bottom component.
	 * Fires event resize on each module representation included in a specific Div  
	 */
	fireResize : function (newW, newH) {
		
		if (Desktop.getDesktopMaximized() == true) {
			Desktop.getDesktopAndTaskBarEl().setHeight(Ext.getBody().getHeight() - Desktop.getEnteteEl().getHeight());
			Desktop.getDesktopAndTaskBarEl().setWidth(Ext.getBody().getWidth());
	
			Desktop.getDesktopAndTaskBarEl().setHeight(Ext.getBody().getHeight() - Desktop.getEnteteEl().getHeight());
			Desktop.getDesktopEl().setHeight(Ext.getBody().getHeight() - Desktop.getEnteteEl().getHeight());
			Desktop.getDesktopEl().setWidth(Ext.getBody().getWidth());
		}
		this.desktopView.fitDesktop();
		
		this.desktopView.windows.each(function (win) {
			if (win.getWidth() > Desktop.getDesktopEl().getWidth() || win.getHeight() > Desktop.getDesktopEl().getHeight()) {
				win.fireEvent("resizeDesktop", win, newW, newH);
			}
		});
		
		var headerController = this.getApplication().getController('header.HeaderController');
    	var footerController = this.getApplication().getController('footer.FooterController');
		
    	var headerView = headerController.getHeaderView();
    	var footerView = footerController.getFooterView();
    	
    	headerView.fireEvent("resize", headerView, newW, newH);
    	
//    	headerView.NavBarsPanel.setWidth(Ext.getBody().getWidth());
    	
    	headerView.fireEvent("windowResize", headerView, newW, newH);

    	footerView.fireEvent("resize", footerView, newW, newH);
		
    	footerView.fireEvent("windowResize", footerView, newW, newH);
		
//		for (var int = 0; int < projectGlobal.modulesInDiv.length; int++) {
//			var module = projectGlobal.modulesInDiv[int];
//			var panel = Ext.getCmp(module.id);
//			panel.fireEvent("resize", panel, newW, newH);
//		}
	},
	
	getStatefullWindowConfig : function (desktopController) {
		return {
			saveSettings : function (componentSettings, forPublicUser) {
			    if (Ext.isEmpty(userLogin)) {
				    Ext.Msg.alert(i18n.get('label.warning', 'label.needLogin'));
				    return;
			    }
			    
			    // TODO find a better way to set the right Y position
			    var position = {
		    		x : this.getX(),
		    		y : this.getY() - desktopController.getApplication().getController('header.HeaderController').HeaderView.getHeight()
			    };
			    
			    var size = {
		    		height : this.getHeight(),
		    		width : this.getWidth()
			    };

			    var putObject = {};

			    // putObject['datasetId'] = datasetId;
			    // putObject['componentType'] = componentType;
			    putObject.componentSettings = componentSettings;

			    putObject.windowSettings = {};
			    putObject.windowSettings.size = size;
			    putObject.windowSettings.position = position;
			    putObject.windowSettings.specificType = this.specificType;
			    putObject.windowSettings.moduleId = this.getId();
			    putObject.windowSettings.typeWindow = this.typeWindow;
			    putObject.windowSettings.maximized = this.maximized;
			    
			    var baseFilePath = "/" + DEFAULT_PREFERENCES_FOLDER + "/" + Project.getProjectName();
			    
			    var filePath = componentSettings.preferencesPath;
			    var fileName = componentSettings.preferencesFileName;
			    if (Ext.isEmpty(filePath) || Ext.isEmpty(fileName)) {
			    	return;
			    }
			    
			    filePath = baseFilePath + filePath;
			    
			    if (forPublicUser) {
			    	PublicStorage.set(fileName, filePath, putObject);
			    }
			    else {
			    	UserStorage.set(fileName, filePath, putObject);
			    }
			    return putObject;
		    }
		};
	}
});