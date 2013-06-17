/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*!
 * Ext JS Library 3.2.1
 * Copyright(c) 2006-2010 Ext JS, Inc.
 * licensing@extjs.com
 * http://www.extjs.com/license
 */
/*global Ext, sitools*/
/**
 * The object that builds main desktop off the application.
 * @param {Ext.app.App} app
 */
Ext.Desktop = function (app) {
	/**
	 * The taskBar object
	 * @type Ext.ux.TaskBar
	 */
	this.taskbar = new Ext.ux.TaskBar(app, true);
	this.xTickSize = this.yTickSize = 1;
	var taskbar = this.taskbar;

	var desktopEl = Ext.get('x-desktop');
	var bureauEl = Ext.get('bureau');
	var topEl = Ext.get('toppanel');
	var taskbarEl = Ext.get('ux-taskbar');
	var shortcuts = Ext.get('x-shortcuts');

	var windowsGroup = new Ext.WindowGroup({
		nbWin : function () {
			return this.accessList.length;
		}
	});
	var activeWindow;

	/**
	 * Minimize a window in the desktop.
	 * @private 
	 * @param {Ext.Window} win the window to minimize
	 */
	function minimizeWin(win) {
	    win.minimized = true;
	    win.hide(win.taskButton.getEl());
	}


	/**
	 * Render the window at the top of the desktop.
	 * @private 
	 * @param {Ext.Window} win the window to minimize
	 */
	function markActive(win) {
		if (activeWindow && activeWindow != win) {
			markInactive(activeWindow);
		}
		taskbar.setActiveButton(win.taskButton);
		activeWindow = win;
		Ext.fly(win.taskButton.el).addClass('active-win');
		win.minimized = false;
	}

	/**
	 * @private 
	 * @param {Ext.Window} win the window to minimize
	 */
	function markInactive(win) {
		if (win == activeWindow) {
			activeWindow = null;
			Ext.fly(win.taskButton.el).removeClass('active-win');
		}
	}

	
	/**
	 * Set the height of the different elements of the desktop, according to the screen height.
	 * @private 
	 */
	function layout() {
		var el = Ext.get("x-main");
		var enteteEl = SitoolsDesk.getEnteteEl();
		var bottom = SitoolsDesk.getBottomEl();
		try {
			el.setHeight(Ext.getBody().getHeight() - enteteEl.getHeight() - bottom.getHeight())
			desktopEl.setHeight(Ext.get("x-desktop-taskbar").getHeight()- taskbarEl.getHeight());
		}
		catch (err) {
			return;
		}
//		bureauEl.setHeight(Ext.lib.Dom.getViewHeight() - taskbarEl.getHeight() - topEl.getHeight())
	}
	
	/**
	 * Remove a window from the desktop.
	 * @private 
	 * @param {Ext.Window} win the window to minimize
	 */
	function removeWin(win) {
		taskbar.removeTaskButton(win.taskButton);
		layout();
	}
	
	Ext.EventManager.onWindowResize(layout);

	this.layout = layout;

	/**
	 * Creates a window. 
	 * @method
	 * @returns {Ext.ux.stateFullWindow} the builded window.
	 */
	this.createWindow = function (config, cls) {
		var win = new (cls || Ext.ux.stateFullWindow)(Ext.applyIf(config || {}, {
		    renderTo : desktopEl,
		    constrain : true, 
		    constrainHeader : true, 
		    draggable : true, 
		    manager : windowsGroup,
		    minimizable : true,
		    maximizable : true, 
	        //DA : forcer le doLayout apres l'affichage 
		    listeners : {
		        show : function (win) {
                    var size = win.getSize();
                    size.width = size.width - 1;
                    
                    win.setSize(size);
		            win.doLayout();
		        }
		    }, 
		    cfgWindow : config.cfgWindow
		}));
		win.dd.xTickSize = this.xTickSize;
		win.dd.yTickSize = this.yTickSize;
		win.resizer.widthIncrement = this.xTickSize;
		win.resizer.heightIncrement = this.yTickSize;
		win.resizer.constrainTo = desktopEl;
		win.show();
		
		win.setPagePosition(config.xPos, config.yPos);
		win.taskButton = taskbar.addTaskButton(win);
		
		win.cmenu = new Ext.menu.Menu({
			items : [

			]
		});

		win.on({
		    'activate' : {
			    fn : markActive
		    },
		    'beforeshow' : {
			    fn : markActive
		    },
		    'deactivate' : {
			    fn : markInactive
		    },
		    'minimize' : {
			    fn : minimizeWin
		    },
		    'close' : {
			    fn : removeWin
		    }
		});

		layout();
		return win;
	};
	/**
	 * Creates a window. 
	 * @method
	 * @returns {Ext.ux.stateFullWindow} the builded window.
	 */
	this.createPanel = function (config, cls) {
		var panel = new (cls || Ext.Panel)(Ext.applyIf(config || {}, {
		    renderTo : desktopEl
		}));
		
		panel.taskButton = taskbar.addTaskButton(panel);
		panel.on({
		    'activate' : {
			    fn : markActive
		    },
		    'beforeshow' : {
			    fn : markActive
		    },
		    'deactivate' : {
			    fn : markInactive
		    }
		});
		layout();
		return panel;
	};
	
	
	/**
	 * @method 
	 * @returns {Ext.WindowGroup} the windowGroup object of this desktop.
	 */
	this.getManager = function () {
		return windowsGroup;
	};

	/**
	 * Returns the window with the id . 
	 * @param {string} id the id to look for
	 * @returns {Ext.Window} win the window
	 */
	this.getWindow = function (id) {
		return windowsGroup.get(id);
	};

	this.getWinWidth = function () {
		var width = Ext.lib.Dom.getViewWidth();
		return width < 200 ? 200 : width;
	};

	this.getWinHeight = function () {
		var height = (Ext.lib.Dom.getViewHeight() - taskbarEl.getHeight());
		return height < 100 ? 100 : height;
	};

	this.getWinX = function (width) {
		return (Ext.lib.Dom.getViewWidth() - width) / 2;
	};

	this.getWinY = function (height) {
		return (Ext.lib.Dom.getViewHeight() - taskbarEl.getHeight() - height) / 2;
	};

	this.setTickSize = function (xTickSize, yTickSize) {
		this.xTickSize = xTickSize;
		if (arguments.length == 1) {
			this.yTickSize = xTickSize;
		} else {
			this.yTickSize = yTickSize;
		}
		windowsGroup.each(function (win) {
			win.dd.xTickSize = this.xTickSize;
			win.dd.yTickSize = this.yTickSize;
			win.resizer.widthIncrement = this.xTickSize;
			win.resizer.heightIncrement = this.yTickSize;
		}, this);
	};

	this.maximize = function () {
		SitoolsDesk.getEnteteComp().fireEvent("maximizeDesktop");
		SitoolsDesk.getBottomComp().fireEvent("maximizeDesktop");
		Ext.DomQuery.select("div[stype=freeDiv]").each(function (freeDiv) {
			freeDiv.style.height = "0px";
			freeDiv.style.width = "0px";
		});
		SitoolsDesk.app.getModulesInDiv().each(function (moduleInDiv) {
			moduleInDiv.fireEvent("maximizeDesktop", moduleInDiv);
		});
		
		//Agrandir la zone desktopAndTaskbar
		this.getDesktopAndTaskBarEl().setHeight(Ext.getBody().getHeight() - SitoolsDesk.getEnteteEl().getHeight());
		this.getDesktopAndTaskBarEl().setWidth(Ext.getBody().getWidth());
		this.getDesktopEl().setHeight(Ext.getBody().getHeight() - SitoolsDesk.getEnteteEl().getHeight() - taskbarEl.getHeight());
		this.getDesktopEl().setWidth(Ext.getBody().getWidth());
		
		if (SitoolsDesk.getDesktop().activePanel) {
			SitoolsDesk.getDesktop().activePanel.fireEvent("resizeDesktop", SitoolsDesk.getDesktop().activePanel);
		}
		
		this.getManager().each(function (win) {
			if (win.maximized) {
				win.fitContainer();
			}
		});

		SitoolsDesk.getDesktop().taskbar.tbPanel.ownerCt.doLayout()
		
	}
	
	this.minimize = function () {
		SitoolsDesk.getEnteteComp().fireEvent("minimizeDesktop");
		SitoolsDesk.getBottomComp().fireEvent("minimizeDesktop");
		
		Ext.DomQuery.select("div[stype=freeDiv]").each(function (freeDiv) {
			freeDiv.style.height = "";
			freeDiv.style.width = "";
		});
		
		//Revenir à la taille initiale de la zone desktopAndTaskbar
		this.getDesktopAndTaskBarEl().dom.style.height="";
		this.getDesktopAndTaskBarEl().dom.style.width="";
		
		this.getDesktopEl().setWidth("");
		
		layout();
		if (SitoolsDesk.getDesktop().activePanel) {
			SitoolsDesk.getDesktop().activePanel.fireEvent("resizeDesktop", SitoolsDesk.getDesktop().activePanel);
		}
		
		this.getManager().each(function (win) {
			if (win.maximized) {
				win.fitContainer();
			}
		});
		SitoolsDesk.getDesktop().taskbar.tbPanel.ownerCt.doLayout()
		
		SitoolsDesk.app.getModulesInDiv().each(function (moduleInDiv) {
			moduleInDiv.fireEvent("minimizeDesktop", moduleInDiv);
		});
		
	}
	/**
	 * called when contextMenu cascade option is pressed.
	 * @method 
	 */
	this.cascade = function () {
		var x = 0, y = 0;
		windowsGroup.each(function (win) {
			if (win.isVisible() && !win.maximized) {
				win.setPosition(x, y);
				x += 20;
				y += 20;
			}
		}, this);
	};

	/**
	 * called when contextMenu tile option is pressed.
	 * @method 
	 */
	this.tile = function () {
		var availWidth = desktopEl.getWidth(true);
		var x = this.xTickSize;
		var y = this.yTickSize;
		var nextY = y;
		windowsGroup.each(function (win) {
			if (win.isVisible() && !win.maximized) {
				var w = win.el.getWidth();

				// Wrap to next row if we are not at the line start and this
				// Window will go off the end
				if ((x > this.xTickSize) && (x + w > availWidth)) {
					x = this.xTickSize;
					y = nextY;
				}

				win.setPosition(x, y);
				x += w + this.xTickSize;
				nextY = Math.max(nextY, y + win.el.getHeight() + this.yTickSize);
			}
		}, this);
	};

	this.contextMenu = new Ext.menu.Menu({
		items : [ {
		    text : 'Tile',
		    handler : this.tile,
		    scope : this, 
		    icon : loadUrl.get('APP_URL') + "/res/images/icons/presentation2.png"
		}, {
		    text : 'Cascade',
		    handler : this.cascade,
		    scope : this, 
		    icon : loadUrl.get('APP_URL') + "/res/images/icons/presentation1.png"
		} ]
	});
	desktopEl.on('contextmenu', function (e) {
		if (e.target.id == "x-desktop"){
			e.stopEvent();
			this.contextMenu.showAt(e.getXY());
		}
	}, this);

	layout();

	if (shortcuts) {
		shortcuts.on('click', function (e, t) {
			if (t = e.getTarget('dt', shortcuts)) {
				e.stopEvent();
				var module = app.getModule(t.id.replace('-shortcut', ''));
				if (module) {
					module.openModule();
				}
			}
		});
	}
	
	this.getDesktopEl = function () {
		return Ext.get('x-desktop');
	}
	this.getDesktopAndTaskBarEl = function () {
		return Ext.get('x-desktop-taskbar');
	}
	
	
	
};
