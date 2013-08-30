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
/*global alertFailure, showResponse, loadUrl, showVersion, userPreferences:true, modulesExistants:true, utils_logout, sitools, userStorage, publicStorage, DEFAULT_PREFERENCES_FOLDER, 
 getDesktop, Ext, window, i18n, SitoolsDesk, userLogin, projectGlobal, createModule, sql2ext, dules, DEFAULT_WIN_HEIGHT, DEFAULT_WIN_WIDTH, 
 loadPreferences, getConfigAndCreateModule, desktopReady, publicStorage, document, locale*/
/*
 * @include "../../../client-public/js/desktop/App.js"
 * @include "../../../client-public/js/desktop/Desktop.js"
 * @include "../sitoolsProject.js"
 */
/**
 * <a href="http://sourceforge.net/tracker/?func=detail&aid=3358501&group_id=531341&atid=2158259">[3358501]</a><br/>
 * 2011/08/03 D.Arpin {Some windows did not have saveSettings Method. In this case, don't save this window Settings}
 */
Ext.namespace('Ext.ux', "sitools.user.desktop");
/**
 * Main Application of sitools desktop
 * When instanciate, it will : 
 *  - build an instance of Ext.app.App ()
 *  - launch initProject on projectGlobal object.
 *  
 * @requires Ext.app.App
 * @class sitools.user.Desktop.App
 */
sitools.user.desktop.App = function () {
	/**
	 * Initialize every modules that should be displayed in a specific Div. 
	 * For each of them, creates a Ext.Panel renderTo the div defined in project administration.
	 */
	function initModulesDiv(modules) {
		Ext.each(projectGlobal.modulesInDiv, function (module) {
			var contentEl = Ext.get(module.divIdToDisplay);
			if (Ext.isEmpty(contentEl)) {
				Ext.Msg.alert(i18n.get('label.error'), String.format(i18n
										.get('label.invalidModuleDivId'),
								module.name, module.divIdToDisplay));
			} else {
				module = new Ext.Panel({
					id : module.id,
					border : false,
					cls : "sitools-module-panel",
					layout : 'fit',
					renderTo : module.divIdToDisplay,
					forceLayout : true,
					height : contentEl.getHeight(),
					width : contentEl.getWidth(),
                    items : [ {
                        layout : 'fit',
                        xtype : module.xtype,
                        listProjectModulesConfig : module.listProjectModulesConfig,
                        moduleProperties : module.properties
                    } ],
					listeners : {
						resize : function (me) {
							if (!SitoolsDesk.desktopMaximizeMode && module.container) {
								me.setSize(Ext.get(module.container).getSize());
							}

						},
						maximizeDesktop : function (me) {
							me.hide();
							me.doLayout();
						},
						minimizeDesktop : function (me) {
						    if (module.container) {
                                me.setSize(Ext.get(module.container).getSize());
                            }
							me.show();
							me.doLayout();
						}
					}

				});
				SitoolsDesk.app.modulesInDiv.push(module);
			}
		});
	}

	/**
	 * Initialize The footer : 
	 * Build a {sitools.user.component.bottom.Bottom}
	 */
	function initBottom() {
		var modules = projectGlobal.modules;
        var bottom = new sitools.user.component.bottom.Bottom({
            id : "bottomCompId"
        });
	}

	/**
	 * Initialize The headers : 
	 * Build a {sitools.user.component.entete.Entete} and render it to x-headers div.
	 */
	function initEntete() {
		var entete = new sitools.user.component.entete.Entete({
				renderTo : "x-headers",
				id : "headersCompId",
				htmlContent : projectGlobal.htmlHeader,
				modules : projectGlobal.modules,
				listeners : {
					resize : function (me) {
						me.setSize(SitoolsDesk.getEnteteEl().getSize());
					}
				}
			});

	}
	/**
	 * <p>Create each module.</p> 
	 * <p>1 - request the project to get All modules defined. </p>
	 * <p>2 - As callback, create a module for each module of the project. 
	 *     In case user is logged, will check if the module is in the preference list, before adding module.</p>
	 */
	function callbackRESTCreateProject() {
		// tableau de modules a passer a l'application
		var modules = [];

		// Check for user authorization
		var isAuthorized = false;
		Ext.Ajax.request({
			scope : this,
			url : projectGlobal.sitoolsAttachementForUsers,
			method : 'GET',
			success : function(response) {
				var data = Ext.decode(response.responseText);
				isAuthorized = true;
				if (data.project.maintenance) {
					desktopReady.call(this);
					Ext.get('ux-taskbar').mask();
					var alertWindow = new Ext.Window({
							title : i18n.get('label.maintenance'),
							width : 600,
							height : 400,
							autoScroll : true,
							closable : false,
							items : [{
									xtype : 'panel',
									layout : 'fit',
									autoScroll : true,
									html : data.project.maintenanceText,
									padding : "5"
								}],
							modal : true
					    });
					alertWindow.show();
					return;
				}
				//we get the configured modules, now we have to create each Module object. 
				projectGlobal.modules = data.project.modules;
				projectGlobal.modulesInDiv = [];

				//Mise a l'écart des modules qui s'affichent dans une div
				Ext.each(projectGlobal.modules, function (module) {
							if (!Ext.isEmpty(module.divIdToDisplay)) {
								projectGlobal.modulesInDiv.push(module);
							}
						}, this);

				SitoolsDesk.modulesACharger = projectGlobal.modules.size();		
				
				if (SitoolsDesk.modulesACharger === 0) {
					Ext.Msg.alert(i18n.get("label.warning"), i18n
									.get("label.noModules"));
					SitoolsDesk.fireEvent('allJsIncludesDone', this);
				}
				
				getListOfModulesAndCreateModules(projectGlobal.sitoolsAttachementForUsers, projectGlobal.modules);		
				
			},
			failure : function (response) {
				Ext.get('ux-taskbar').mask();
				Ext.Msg.alert('Status', i18n.get('warning.not.authorized'),
						function () {
							window.location = loadUrl.get('APP_URL') + loadUrl.get('APP_CLIENT_USER_URL');
						});
				return;
			}
		});
	}

	/**
	 * Called on a desktop Resize. 
	 * It will redefine the height and size of desktop Element. 
	 * Fires events for each component so that they can resize according to their container
	 * Fires event resizeDesktop on activePanel
	 * Fires event resize on entete and bottom component.
	 * Fires event resize on each module representation included in a specific Div  
	 */
	function fireResize(newW, newH) {
		var desktop = SitoolsDesk.getDesktop();
		if (SitoolsDesk.desktopMaximizeMode) {
			desktop.getDesktopAndTaskBarEl().setHeight(Ext.getBody()
					.getHeight()
					- this.getEnteteEl().getHeight());
			desktop.getDesktopAndTaskBarEl().setWidth(Ext.getBody()
					.getWidth());
			desktop.getDesktopEl().setHeight(Ext.getBody().getHeight()
					- this.getEnteteEl().getHeight()
					- desktop.taskbar.tbPanel.getHeight());
			desktop.getDesktopEl().setWidth(Ext.getBody().getWidth());
		}
		if (!Ext.isEmpty(desktop.activePanel)) {
			desktop.activePanel.fireEvent("resizeDesktop",
					desktop.activePanel);
		}
		SitoolsDesk.getEnteteComp().fireEvent("resize",
				SitoolsDesk.getEnteteComp(), newW, newH);
		SitoolsDesk.getEnteteComp().fireEvent("windowResize",
				SitoolsDesk.getEnteteComp(), newW, newH);
		SitoolsDesk.getBottomComp().fireEvent("resize",
				SitoolsDesk.getBottomComp(), newW, newH);
		SitoolsDesk.getBottomComp().fireEvent("windowResize",
				SitoolsDesk.getBottomComp(), newW, newH);
		for (var int = 0; int < projectGlobal.modulesInDiv.length; int++) {
			var module = projectGlobal.modulesInDiv[int];
			var panel = Ext.getCmp(module.id);
			panel.fireEvent("resize", panel, newW, newH);
		}
	}

	/**
	 * Method called to instanciate the right profile object to get specific methods accdording to the navigation profile. 
	 * @returns
	 */
	function initNavigationMode() {
		if (projectGlobal.navigationMode === "desktop") {
			SitoolsDesk.navProfile = sitools.user.desktop.navProfile.desktop;
		} else {
			SitoolsDesk.navProfile = sitools.user.desktop.navProfile.fixed;
		}
	}

	/**
	 * Method called on allJsIncludesDone event. 
	 * After that, we are sure that every needed JS is loaded. 
	 * First load the user preferences
	 */
	function _onAllJsIncludesDone() {
		initNavigationMode();
//		SitoolsDesk.loadPreferences(this);

		this.fireEvent('modulesLoaded');
	}
	
    /**
     * Initialize every modules that should be displayed in a specific Div. 
     * For each of them, creates a Ext.Panel renderTo the div defined in project administration.
     */
    function checkModules(modules, callback) {
        var errorModules = [];
        Ext.each(projectGlobal.modules, function (module) {
            var moduleName = module.name;
            
            var xtype = module.xtype;            
            var func = xtype + ".openModule";
            try {
                if (!Ext.isEmpty(xtype) && Ext.isFunction(eval(func))) {
                    eval(func);
                }
                
            } catch (err) {
                errorModules.push(moduleName);
            }
        }, this);
        if (!Ext.isEmpty(errorModules)) {
            var moduleNames = "";
            Ext.each(errorModules, function (moduleName) {
                moduleNames += "<br/> - " + moduleName;
            });
            var msg = String.format(i18n.get("label.cannotLoadModules"), moduleNames);
            Ext.Msg.alert(i18n.get("label.warning"), msg, function () {
                onAllInit.call(this);
            }, this);
            var window = Ext.MessageBox.getDialog(i18n.get("label.warning"));
            var pos = window.getPosition();
            pos[1] = pos[1] - 200;
            window.setPosition(pos);
        }
        else {
            onAllInit.call(this);
        }
    }
	

	/**
	 * Method called on moduleLoaded event.
	 * Add home and remove panel button to taskbar or navbar
	 * (depending desktop or fixed mode)
	 */
	function initTaskAndNavBar() {
		SitoolsDesk.navProfile.taskbar.initTaskbar();
		SitoolsDesk.navProfile.initNavbar();
	}

	function onAllInit() {
	    SitoolsDesk.loadPreferences(this);
        initEntete();
        initBottom();
        initTaskAndNavBar();
        initModulesDiv(projectGlobal.modules);

        if (projectGlobal.preferences
                && projectGlobal.preferences.projectSettings
                && projectGlobal.preferences.projectSettings.desktopMaximizeMode) {
            getDesktop().maximize();
        }
      //when preferences are loaded fireEvent Ready.
        this.fireEvent('ready');
    }
	
	/**
	 * Called on modulesLoaded event. 
	 * After modules are loaded and built, initialize the headers and footer components
	 * Load every modules defined to be displayed in a specific div. 
	 */
	function _onModulesLoaded() {
	    checkModules.call(this, projectGlobal.modules, onAllInit);
	}
	
	/**
	 * Initialize the project. 
	 * Load sql2Ext settings, 
	 * Call the projectGloabal initProject method
	 */
	function initProject() {
		//Add the app listeners
		SitoolsDesk.app.addListener("allJsIncludesDone", _onAllJsIncludesDone);
		SitoolsDesk.app.addListener("ready", desktopReady);
		SitoolsDesk.app.addListener("modulesLoaded", _onModulesLoaded);

        sql2ext.load(loadUrl.get('APP_URL') + "/client-user/conf/sql2ext.properties");

		//handle windowResize event 
		Ext.EventManager.onWindowResize(fireResize, SitoolsDesk);

		projectGlobal.initProject(callbackRESTCreateProject);

		// Ext.QuickTips.init();

		// Apply a set of config properties to the singleton
        Ext.apply(Ext.QuickTips.getQuickTip(), {
            maxWidth : 200,
            minWidth : 100,
            showDelay : 50,
            trackMouse : true
        });
	}

	/**
	 * Called when deletePrefButton is pressed. 
	 * Remove the public Preferences.
	 */
	function deletePublicPref() {
		publicStorage.remove();
	}

	/**
	 * Called when login is pressed.
	 * Show a {sitools.userProfile.Login} window
	 */
	function _onLogin() {
	    sitools.userProfile.LoginUtils.connect({
            closable : true,
            url : loadUrl.get('APP_URL') + '/login',
            register : loadUrl.get('APP_URL') + '/inscriptions/user',
            reset : loadUrl.get('APP_URL') + '/resetPassword'
        });
	}

	/**
	 * Called when logout is pressed.
	 */
	function _onLogout() {
	    sitools.userProfile.LoginUtils.logout();
	}

	/**
	 * private 
	 * Create the component according to the componentCfg and JsObj param. 
	 * Uses the navProfile to create either a window or a panel. 
	 * 
	 * @param {} windowSettings  
	 *      {string} id (required) : windowId
	 *      {string} title (required) : windowTitle, 
	 *      {string} datasetName (required) : datasetName, 
	 *      {string} moduleId : String
	 *      {} position : [xpos, ypos]
	 *      {} size : {
	 *          width : w
	 *          height : h
	 *      }
	 *      {string} specificType : sitoolsSpecificType
	 *      [Ext.Button] toolbarItems 
	 * @param {} componentCfg  Object containing the configuration
	 * @param {string} JsObj the name of the Javascript Object used to build the component inside the window
	 * @param {boolean} reloadComp true to reload the window (only in desktop navigation moede)
	 * @returns
	 */
	function addWinData(windowSettings, componentCfg, JsObj, reloadComp) {
		var desktop = getDesktop();
		var win = desktop.getWindow(windowSettings.id);
		if (win) {
			if (reloadComp) {
				win.removeAll();
				win.add(new JsObj(componentCfg));
				win.doLayout();
			}
			if (win.minimized) {
				win.show();
			}
			win.toFront();
			return;
		}
		//Create the component according to the navigation profile.
		try {
            SitoolsDesk.navProfile.createComponent({
                componentCfg : componentCfg,
                windowSettings : windowSettings,
                reloadComp : reloadComp,
                JsObj : JsObj
            });

		} catch (r) {
			Ext.Msg.alert(i18n.get("label.warning"), i18n
							.get("label.nocomponentfound"));
			throw (r);
		}
	}

	/**
	 * Unmask desktop Elements
	 */
	function desktopReady() {
		if (Ext.getBody().isMasked()) {
			Ext.getBody().unmask();
		}
		

		if (!Ext.isEmpty(SitoolsDesk.getEnteteComp())) {
		    Ext.get("ux-taskbar").setVisible(true);
		    SitoolsDesk.getEnteteComp().fireEvent("desktopReady", SitoolsDesk.getEnteteComp());
		}
		
        document.onkeypress = function (event) {
            if (event.keyCode === event.DOM_VK_F1) {
                // cancel browser app event handler for F1 key
                event.stopPropagation();
                event.preventDefault();
            }
        };
	}

	/**
	 * Mask all the desktop element. 
	 */
	function maskDesktop() {
		Ext.getBody().mask(i18n.get("label.loadingSitools"));
		Ext.get("ux-taskbar").hide();
	}

	/**
	 * Build a Ext.app.Module according to the config. 
	 * Includes All Css and Js defined in the module configuration. 
	 * Especially defined the default method openModule called to open a Module. 
	 * 
	 * @param {} the module Configuration. 
	 * @returns {Ext.app.Module} 
	 */
	function createModule(config) {
		function includeCss(url) {
			var headID = document.getElementsByTagName("head")[0];
			var newCss = document.createElement('link');
			newCss.type = 'text/css';
			newCss.rel = 'stylesheet';
			newCss.href = url;
			newCss.media = 'screen';
			// pas possible de monitorer l'evenement onload sur une balise link
			headID.appendChild(newCss);
		}

		function includeJs(ConfUrls, indexAInclure) {
			//Test if all inclusions are done for this module
			if (indexAInclure < ConfUrls.length) {
				// if not : include the Js Script
				var DSLScript = document.createElement("script");
				DSLScript.type = "text/javascript";
				DSLScript.onload = includeJs.createDelegate(this, [ConfUrls,
								indexAInclure + 1]);
				DSLScript.onreadystatechange = includeJs.createDelegate(this, 
				        [ConfUrls, indexAInclure + 1]);
				//keep loading even if there is an error
				DSLScript.onerror = includeJs.createDelegate(this, 
				        [ConfUrls, indexAInclure + 1]);

				DSLScript.src = ConfUrls[indexAInclure].url;

				var headID = document.getElementsByTagName('head')[0];
				headID.appendChild(DSLScript);
			} else {
				//if all includes are done, Add 1 to the modulesCharges 
				SitoolsDesk.modulesCharges++;
				//test if all modules are loaded.
				if (SitoolsDesk.modulesCharges === SitoolsDesk.modulesACharger) {
					//End of loading all Javascript files.  
					SitoolsDesk.app.fireEvent('allJsIncludesDone', this);
				}
			}
		}
		
		var module = new Ext.app.Module(Ext.apply(config, {
			init : function () {
				// s'il y a des dependances
				if (config.dependencies) {
					if (config.dependencies.css) {
						Ext.each(config.dependencies.css, function (dependenceCss) {
									includeCss(dependenceCss.url);
								});
					}

					if (config.dependencies.js) {
						includeJs(config.dependencies.js, 0);
					}

				} else {
					SitoolsDesk.app.modulesCharges++;
				}

			},

			getWindow : function () {
				return Ext.getCmp(config.id);
			},
			openModule : function () {
				var desktop = getDesktop();
				return SitoolsDesk.navProfile.openModule(module);
			}
		}));
		return module;

	}
	/**
	 * Request the module configuration and call createModule method. 
	 * @warning Old version, do not use
	 */
	function getConfigAndCreateModule(config) {
		Ext.Ajax.request({
			method : "GET",
			url : loadUrl.get('APP_URL') + loadUrl.get('APP_PROJECTS_MODULES_URL') + "/" + config.id,
			scope : this,
			success : function (response) {
				var json = Ext.decode(response.responseText);
				if (json.success) {
					var configModule = Ext.applyIf(config, json.projectModule || {});
					var module = createModule(configModule);
					SitoolsDesk.app.modules.push(module);
				} else {
					Ext.Msg.alert(i18n.get('label.error'), String.format(i18n
											.get('label.undefinedModule'),
									config.name));
					//if all includes are done, Add 1 to the modulesCharges 
					SitoolsDesk.modulesCharges++;
					//test if all modules are loaded.
					if (SitoolsDesk.modulesCharges === SitoolsDesk.modulesACharger) {
						//End of loading all Javascript files.  
						SitoolsDesk.app.fireEvent('allJsIncludesDone', this);
					}
				}
			},
			failure : alertFailure
		});
	}
	
	function findProjectModuleConfig(configs, id) {
        var configOut = null;
        Ext.each(configs, function (conf) {
            if (conf.id === id) {
                configOut = conf;
                return;
            }
        }, this);
        return configOut;
    }
	
	/**
	 * Gets the list of modules for the project and creates all the modules
	 * @recommanded New version of getConfigAndCreateModule
	 */
	function getListOfModulesAndCreateModules(projectAttachment, configs) {
		Ext.Ajax.request({
			method : "GET",
			url : projectAttachment + "/projectModules",
			scope : this,
			success : function (response) {
				var json = Ext.decode(response.responseText);
				Ext.each(json.data, function (projectModule) {
					var projectConfig = findProjectModuleConfig(configs, projectModule.id);
					if (!Ext.isEmpty(projectConfig)) {
						var configModule = Ext.applyIf(projectConfig, projectModule	|| {});
						var module = createModule(configModule);						
						SitoolsDesk.app.modules.push(module);
					}
					else {
						Ext.Msg.alert(i18n.get('label.error'), String.format(i18n
											.get('label.undefinedModule'),
									projectModule.name));		
									
						//if all includes are done, Add 1 to the modulesCharges 
						SitoolsDesk.modulesCharges++;
						//test if all modules are loaded.
						if (SitoolsDesk.modulesCharges === SitoolsDesk.modulesACharger) {
							//End of loading all Javascript files.  
							SitoolsDesk.app.fireEvent('allJsIncludesDone', this);
						}
					}					
				}, this);
			},
			failure : alertFailure
		});		
	}
	

	

	/**
	 * Instanciation of a Ext.app.App object. 
	 * Overrides some methods. 
	 * @returns {Ext.app.App} the application. 
	 */
	function createApplication() {
		return new Ext.app.App({
			//initialize app
			init : function () {
				Ext.QuickTips.init();
				Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
				//WTF with IE...
				if (Ext.isIE) {
					Ext.Msg.confirm(i18n.get('label.warning'), i18n
									.get('label.IEWarning'),
							function (buttonId) {
								if (buttonId === "yes") {
									maskDesktop();
									initProject();
								} else {
									window.location
											.replace("https://www.google.com/chrome/index.html");
								}
							});

				} else {
					maskDesktop();
					initProject();
				}

			},
			//overrides getModules
			getModules : function () {
				return this.modules;
			},
			//overrides getModulesInDiv
			getModulesInDiv : function () {
				return this.modulesInDiv;
			},
			//overrides
			findModule : function (moduleId) {
				var result = null;
				Ext.each(this.modules.concat(this.modulesInDiv), function (module) {
					if (module.id === moduleId) {
						result = module;
					}
				});
				return result;
			},
			//add some method 
			saveWindowSettings : function (forPublicUser) {
				var desktopSettings = SitoolsDesk.navProfile
						.getDesktopSettings(forPublicUser);

				userPreferences = {};
				userPreferences.windowSettings = desktopSettings;
				var projectSettings = {};
//				if (!Ext.isEmpty(SitoolsDesk.getDesktop().activePanel)) {
//					projectSettings.activeModuleId = SitoolsDesk.getDesktop().activePanel.id;
//				}
				projectSettings.desktopMaximizeMode = SitoolsDesk.desktopMaximizeMode;
				projectSettings.navigationMode = projectGlobal.navigationMode;

				userPreferences.projectSettings = projectSettings;
				if (forPublicUser) {
					publicStorage.set("desktop", "/"
									+ DEFAULT_PREFERENCES_FOLDER + "/"
									+ projectGlobal.projectName,
							userPreferences);
				} else {
					userStorage.set("desktop", "/" + DEFAULT_PREFERENCES_FOLDER
									+ "/" + projectGlobal.projectName,
							userPreferences);
				}
			}
		});
	}

	/**
	 * Find the portal preferences. 
	 * @param portalPrefCb A method to call after portal Preferences Request. 
	 */
	function getPortalPreferences(portalPrefCb) {
		if (!Ext.isEmpty(userLogin)) {
			var portalPrefSuccess = function (response) {
				if (Ext.isEmpty(response.responseText)) {
					return;
				}
				try {
					var json = Ext.decode(response.responseText);
					if (!Ext.isEmpty(json.language)) {
						this.app.language = json.language;
					}
				} catch (err) {
				    return;
				}
			};

			var filePath = "/" + DEFAULT_PREFERENCES_FOLDER + '/portal';

			userStorage.get("portal", filePath, this, portalPrefSuccess,
					Ext.emptyFn, portalPrefCb);
		} else {
			portalPrefCb.call(this);
		}

	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - //

	return {
		maxSizeHistory : 5,

		history : [],

		/**
		 * the Navigation profile. 
		 */
		navProfile : null,

		activeComponent : null,
		/**
		 * True if desktop is maximized, false otherwize
		 */
		desktopMaximizeMode : false,

		/**
		 * {integer} modulesCharges The number of loaded Modules
		 */
		modulesCharges : 0,

		/**
		 * {integer} modulesACharger The number of modules to load at start.
		 */
		modulesACharger : 0,
		/**
		 * the sitools Ext.app.App instance
		 * @type Ext.app.App
		 */
		app : this.app,

		/**
		 * Method called to historize the navigation in the desktop.  
		 * @param {} panel The panel to be historized.
		 * @returns
		 */
		addToHistory : function (panel) {
			panel.rendered = false;
			SitoolsDesk.history.push(panel);
		},

		/**
		 * The entry Point of SITools application. 
		 * Create the Ext.app.App stored as SitoolsDesk.app. 
		 * Load the context files (loadUrl, i18n, ...)
		 * After All, call SitoolsDesk.app.initApp() to instanciate an Ext.Desktop.
		 * @returns
		 */
		initDesktopApplication : function () {
			// START HERE !!!
			Ext.QuickTips.init();
			
			if (!Ext.isEmpty(Ext.util.Cookies.get('userLogin'))) {
				var auth = Ext.util.Cookies.get('hashCode');

				Ext.Ajax.defaultHeaders = {
					"Authorization" : auth,
					"Accept" : "application/json",
					"X-User-Agent" : "Sitools"
				};
			} else {
				Ext.Ajax.defaultHeaders = {
					"Accept" : "application/json",
					"X-User-Agent" : "Sitools"
				};
			}

			//Instanciation de l'objet Ext.app.App. 
			this.app = createApplication();

			//récupération de la langue contenue dans le cookie, ou à défaut, au niveau du navigateur. 
			this.app.language = locale.getLocale();

			/* Définition des appels en cascade :
			 *  - méthode N°1 : loadUrl.load (Chargements des urls dynamiques)
			 *  - méthode N°2 : getPortalPreferences (Chargement des préférences du portail)
			 *  - méthode N°3 : i18n.load (Chargement du fichier de langue défini)
			 *  - méthode N°4 : projectGlobal.getUserRoles (Récupération du rôle de l'utilisateur)
			 *  - méthode N°5 : SitoolsDesk.app.initApp (Instancie l'objet Ext.Desktop et appel à SitoolsDesk.app.init())
			 *  
			 */
			var initApplication = function () {
				SitoolsDesk.app.initApp();
			};
			var i18nCb = function () {
				projectGlobal.getUserRoles(initApplication);
			};
			var portalPrefCb = function () {
				i18n.load('/sitools/res/i18n/' + SitoolsDesk.app.language + '/gui.properties', i18nCb);
			};
			var callbackSiteMap = function () {
				getPortalPreferences(portalPrefCb);
			};
			loadUrl.load('/sitools/client-user/siteMap', callbackSiteMap);

		},

		/**
		 * public dynamically add a new application to the desktop
		 * 
		 * 1. moduleFactory.createModule 
		 * 2. ajout du module dans le fisheyeMenu
		 */
		addApplication : function (composant) {
			modulesExistants = SitoolsDesk.app.getModules();
			if (!modulesExistants) {
				modulesExistants = [];
			}

			var nouveauModule = getConfigAndCreateModule(composant, this);
			modulesExistants.push(nouveauModule);

			SitoolsDesk.app.addModule(nouveauModule);
		},

		/**
		 * public dynamically remove an application from the desktop
		 * 
		 * On supprime son icone du menu demarrer et du fisheyeMenu ainsi que de
		 * la liste des modules existants
		 */
		removeApplication : function (idApplication) {
			var moduleToRemove = SitoolsDesk.app.getModule(idApplication);
			modulesExistants = SitoolsDesk.app.getModules();
			SitoolsDesk.app.removeModule(moduleToRemove);
		},

		/**
		 * Load the module Window corresponding to the project Preference. 
		 * 1 - load the module Windows
		 * 2 - load the Component windows (actually only "data", "form"  && "formProject" type window) 
		 */
		loadPreferences : function (scope) {
			if (!Ext.isEmpty(projectGlobal.preferences) && !Ext.isEmpty(projectGlobal.preferences.projectSettings)) {
				// Ne charge les préférences sauvegardées seulement si le navigationMode sauvegardé et le même que le courant
				if (projectGlobal.navigationMode == projectGlobal.preferences.projectSettings.navigationMode){
					SitoolsDesk.navProfile.loadPreferences(scope);
				}
				else {
					Ext.Msg.show({
						buttons :  Ext.MessageBox.OK,
						icon :  Ext.MessageBox.INFO,
						modal : true,
						closable : false,
						title : i18n.get('label.info'),
						msg : i18n.get('label.saveNavigationModeDoesntMatch')
					});
				}
			}
		},

		/**
		 * @method
		 * public load the preferences for a window if the user is logged and then build the window 
		 * 
		 * @param {} windowSettings Window Settings object build with attributes
		 *      {string} id (required) : windowId
		 *      {string} title (required) : windowTitle, 
		 *      {string} type (required if saveToolbar) : the type of the window, will determine the userStorage path
		 *          [forms, data]
		 *      {string} datasetName (required if saveToolbar) : name of the dataset, will determine the userStorage name
		 *      {string} urlPreferences (required if saveToolbar) : the url to request to get the userPreferences
		 *      {boolean} saveToolbar  : true if the saveSettings toolbar should be displayed
		 *          default false
		 * 
		 * @param {} component : the items to add to the Window
		 * @param {string} JsObj : the name of the Javascript Object used to build the component inside the window
		 */
		addDesktopWindow : function (windowSettings, component, JsObj,
				reloadComp) {
			if (Ext.isEmpty(windowSettings.saveToolbar)) {
				windowSettings.saveToolbar = false;
			}

			if (Ext.isEmpty(windowSettings.title)) {
				throw ("NoWinTitle");
			}

			if (Ext.isEmpty(reloadComp)) {
				reloadComp = false;
			}

			//If Window does not have save abilities.
			if (windowSettings.saveToolbar === false) {
				addWinData(windowSettings, component, JsObj, reloadComp);
				return;
			}

			//construction de l'url pour les préférences utilisateur. 
			var baseFilePath = "/" + DEFAULT_PREFERENCES_FOLDER + "/" + projectGlobal.projectName;

			var filePath = component.preferencesPath;
			var fileName = component.preferencesFileName;
			if (Ext.isEmpty(filePath) || Ext.isEmpty(fileName)) {
				addWinData(windowSettings, component, JsObj, reloadComp);
				return;
			}
			filePath = baseFilePath + filePath;

			//Méthod to call if no userPreferences found
			var addWinPublic = function (windowSettings, component, JsObj,
					reloadComp) {
				var successPublic = function (response, opts) {
					try {
						var json = Ext.decode(response.responseText);

						Ext.apply(windowSettings, json.windowSettings);
						Ext.apply(component, 
					        {
								userPreference : json.componentSettings
							});
						addWinData(windowSettings, component, JsObj, reloadComp);
					} catch (err) {
						addWinData(windowSettings, component, JsObj, reloadComp);
					}
				};

				var failurePublic = function (response, opts) {
					addWinData(windowSettings, component, JsObj, reloadComp);
				};

				publicStorage.get(fileName, filePath, this, successPublic,
						failurePublic);
			};

			if (Ext.isEmpty(userLogin)) {
				addWinPublic(windowSettings, component, JsObj, reloadComp);
			} else {
				//Méthode appelée si l'on trouve des préférences pour le user
				var successMethod = function (response, opts) {
					try {
						var json = Ext.decode(response.responseText);

						Ext.apply(windowSettings, json.windowSettings);
						Ext.apply(component, 
					        {
								userPreference : json.componentSettings
							});
						addWinData(windowSettings, component, JsObj, reloadComp);
					} catch (err) {
						addWinPublic(windowSettings, component, JsObj,
								reloadComp);
					}
				};
				//Si pas de préférences trouvées, on utilise addWinPublic
				var failureMethod = function (response, opts) {
					addWinPublic(windowSettings, component, JsObj, reloadComp);
				};

				userStorage.get(fileName, filePath, this, successMethod,
						failureMethod);
			}
		},
		/**
		 * Returns the Ext.Desktop instance. 
		 * @returns {Ext.Desktop}
		 */
		getDesktop : function () {
			return this.app.desktop;
		},
		/**
		 * Returns the desktop Header Element. 
		 * @returns {Ext.Element}
		 */
		getEnteteEl : function () {
			return Ext.get('x-headers');
		},
		/**
		 * Returns the panel charged of displaying headers of Sitools.
		 * @returns {sitools.user.component.entete.Entete} The headers component
		 */
		getEnteteComp : function () {
			return Ext.getCmp("headersCompId");
		},
		/**
		 * Returns the footer Element. 
		 * @returns {Ext.Element}
		 */
		getBottomEl : function () {
			return Ext.get('x-bottom');
		},
		/**
		 * Returns the panel charged of displaying bottom of Sitools.
		 * @returns {sitools.user.component.bottom.Bottom} The footer component
		 */
		getBottomComp : function () {
			return Ext.getCmp("bottomCompId");
		},
		/**
		 * Remove the active Panel in desktop (the module representation incrusted in background of the desktop)
		 * 
		 */
		removeActivePanel : function () {
			if (this.getDesktop().activePanel) {
				var panel = this.getDesktop().activePanel;
				this.getDesktop().activePanel = null;
				panel.hide();
				return;
			}
		},
		/**
		 * Close every window in desktop and panel in fixed mode
		 */
		removeAllWindows : function (quiet) {
			SitoolsDesk.navProfile.removeAllWindows(quiet);
		},
		/**
		 * Minify every window in desktop. 
		 */
		minifyAllWindows : function () {
			SitoolsDesk.getDesktop().getManager().each(function (win) {
						if (Ext.isFunction(win.minimize)) {
							win.minimize();
						} else {
							win.destroy();
						}

					});
		},
		/**
		 * Open and shows all hidden windows that have a taskButton  
		 * @returns
		 */
		openAllWindows : function () {
			SitoolsDesk.getDesktop().getManager().each(function (win) {
						if (!win.isVisible() && win.taskButton) {
							win.show();
						}
					});
		},

		/**
		 * Open a modal window in the desktop. 
		 * @param {Ext.Panel} panel the component to add to the modal window 
		 * @param {} config a config object containg windowSettings
		 * @returns
		 */
		openModalWindow : function (panel, config) {
			var windowConfig = config.windowSettings || {};
			Ext.apply(windowConfig, {
					modal : true,
					layout : 'fit'
				});

			if (!Ext.isEmpty(windowConfig.toolbarItems)) {
				Ext.apply(windowConfig, {
					tbar : {
						items : windowConfig.toolbarItems,
						cls : 'toolbar'
					},
					listeners : {
						afterrender : function (me) {
							if (me.getHeight() > Ext.getBody()
									.getHeight()) {
								me.setHeight(Ext.getBody().getHeight());
							}
							if (me.getWidth() > Ext.getBody()
									.getWidth()) {
								me.setWidth(Ext.getBody().getWidth());
							}
							me.doLayout();

						}
					}
				});
			}

			Ext.applyIf(windowConfig, {
				width : DEFAULT_WIN_WIDTH,
				height : DEFAULT_WIN_HEIGHT
			});
			var win = new Ext.Window(windowConfig);
			win.show();
			win.add(panel);
			win.setZIndex(12000);

			win.doLayout();
		},
		/**
		 * Open the sitools.user.component.help window
		 * @param {Ext.Button} b The pressed btn
		 * @param {Ext.event} e the click Event. 
		 * @returns
		 */
		showHelp : function (b, e) {
			var help = Ext.getCmp("helpWindow");
			if (!Ext.isEmpty(help)) {
				help.show();
				help.setZIndex(12000);
				return;
			}
			var componentCfg = {};
			help = new sitools.user.component.help();
			var config = {};
			config.windowSettings = {
				id : "helpWindow",
				title : i18n.get('label.help'),
				saveToolbar : false,
				closeAction : 'hide'
			};
			SitoolsDesk.openModalWindow(help, config);
		},
		
		showFormFromEditor : function (formUrl) {
	        Ext.Ajax.request({
	            url : formUrl,
	            method : 'GET', 
	            success : function (response) {
	                try {
	                    var json = Ext.decode(response.responseText);
	                    if (! json.success) {
	                        Ext.Msg.alert(i18n.get('label.error'), json.message);
	                        return;
	                    }
	                    var rec = {};
	                    rec.data = json.data[0];
	                    
	                    Ext.Ajax.request({
				            url : rec.data.parentUrl,
				            method : 'GET', 
				            success : function (response) {
				                try {
                                    var json2 = Ext.decode(response.responseText);
                                    var dataset = json2.dataset;

                                    var jsObj = SitoolsDesk.navProfile.getFormOpenMode();

                                    var componentCfg = {
                                        dataUrl : rec.data.parentUrl,
                                        dataset : dataset,
                                        formId : rec.data.id,
                                        formName : rec.data.name,
                                        formParameters : rec.data.parameters,
                                        formWidth : rec.data.width,
                                        formHeight : rec.data.height,
                                        formCss : rec.data.css,
                                        preferencesPath : "/" + dataset.name + "/forms",
                                        preferencesFileName : rec.data.name
                                    };

                                    SitoolsDesk.navProfile.addSpecificFormParameters(componentCfg, dataset);

                                    var windowSettings = {
                                        datasetName : dataset.name,
                                        type : "form",
                                        title : i18n.get('label.forms') + " : " + dataset.name + "." + rec.data.name,
                                        id : "form" + dataset.id + rec.data.id,
                                        saveToolbar : true,
                                        iconCls : "form"
                                    };

                                    SitoolsDesk.addDesktopWindow(windowSettings, componentCfg, jsObj);
                                    return;
                                } catch (err) {
                                    Ext.Msg.alert(i18n.get('label.error'), err);
                                    return;
                                }
				            }
                        });
	                }
	                catch (err) {
	                    Ext.Msg.alert(i18n.get('label.error'), err);
	                    return;
	                }
	                
	            }, 
	            failure : function () {
	                Ext.Msg.alert(i18n.get('label.error'), i18n.get('label.noActiveDatasetFound'));
	                return;
	            }
	        });
		}
	};
};
