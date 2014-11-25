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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse, Desktop*/

Ext.namespace('sitools.user.core');
/**
 * Core class containing all Sitools proper informations
 */
Ext.define('sitools.user.core.Desktop', {
	singleton : true,
	alternateClassName : ['Desktop'],
	
	requires : ['sitools.user.utils.DatasetUtils'],

	config : {
		application : null,
		activePanel : null,
		modulesInDiv : [],
		
		sitoolsDesktop : null,
		mainDesktop : null,
		
		desktopEl : null,
		desktopAndTaskBarEl : null,
		
		enteteEl : null,
		enteteComp : null,
		
		bottomEl : null,
		bottomComp : null,
		
		taskbarEl : null,
		
		shortcutsEl : null,
		
		desktopMaximized : false,
		showDesktopAction : 'minimize'
	},
	
	getNavMode : function () {
		return this.getApplication().getController('core.NavigationModeFactory').getNavigationMode(Project.getNavigationMode());
	},
	
	init : function () {
	    Ext.apply(this, {
	        sitoolsDesktop : Ext.get("sitools-desktop"),
	        mainDesktop : Ext.get("x-main"),
        
	        desktopEl : Ext.get('x-desktop'),
	        desktopAndTaskBarEl : Ext.get('x-desktop-taskbar'),
        
            enteteEl : Ext.get('x-headers'),
            enteteComp : Ext.getCmp('headersCompId'),
            
            bottomEl : Ext.get('x-bottom'),
            bottomComp : Ext.getCmp('bottomCompId'),
            
            taskbarEl : Ext.get('ux-taskbar'),
            
            shortcutsEl : Ext.get('x-shortcuts')
	    });
	},
	
	 /**
     * Load the module Window corresponding to the project Preference. 1 - load
     * the module Windows 2 - load the Component windows (actually only "data",
     * "form" && "formProject" type window)
     */
    loadPreferences : function () {
    	if (Ext.isEmpty(Project.preferences)) {
    		return;
    	}
    	
    	if (Project.preferences.projectSettings.desktopMaximizedMode) {
    		this.getApplication().getController('DesktopController').maximize();
			Desktop.setDesktopMaximized(true);
    	}
    	
    	// Chargement des composants ouverts.
        Ext.each(Project.preferences.windowSettings, function (pref) {
            // 1° cas : les fenêtres de modules
            if (Ext.isEmpty(pref.windowSettings.typeWindow)) {
            	
            	var moduleId = pref.windowSettings.moduleId;
            	var module = Ext.StoreManager.lookup('ModulesStore').getById(moduleId);
            	
            	
            	if (!Ext.isEmpty(module)) {
            		Ext.apply(module.data, {
            			defaultHeight : pref.windowSettings.size.height,
            			defaultWidth : pref.windowSettings.size.width,
            			x : pref.windowSettings.position.x,
            			y : pref.windowSettings.position.y
            		});
            		
            		Desktop.getApplication().getController('core.SitoolsController').openModule(module);
            	}
            	else if (!Ext.isEmpty(pref.windowSettings.componentId)) {
            		
            		var cmpSettings = pref.componentSettings;
            		Desktop.getApplication().getController('core.SitoolsController').openComponent(cmpSettings.componentClazz, cmpSettings, pref.windowSettings);

            	}
//                var moduleId = pref.windowSettings.moduleId;

//                var module = SitoolsDesk.app.getModule(moduleId);
//                if (!Ext.isEmpty(module) && Ext.isEmpty(module.divIdToDisplay)) {
//                	
//                    var win = module.openModule();
//                    var pos = pref.windowSettings.position;
//                    var size = pref.windowSettings.size;
//
//                    // TODO, refactoring, set size in openmodule method.... like
//                    // for typeWindow=data (dataset window)
//                    if (pos !== null && size !== null) {
//                        pos = Ext.decode(pos);
//                        size = Ext.decode(size);
//
//                        win.setPosition(pos[0], pos[1]);
//                        win.setSize(size);
//
////                        getDesktop().layout();
//                    }
//                }
            }
            // les autres fenêtres : on nne traite que les cas
            // windowSettings.typeWindow == "data"
            else {
                var type = pref.windowSettings.typeWindow;
                var componentCfg, jsObj, windowSettings;
                var datasetUrl = pref.componentSettings.datasetUrl;
                if (type === "data") {
                    sitools.user.utils.DatasetUtils.openDataset(datasetUrl, pref.componentSettings, pref.windowSettings);
                }
                if (type === "formProject") {
                    jsObj = sitools.user.component.forms.projectForm;
                    componentCfg = {
                        formId : pref.componentSettings.formId,
                        formName : pref.componentSettings.formName,
                        formParameters : pref.componentSettings.formParameters,
                        formWidth : pref.componentSettings.formWidth,
                        formHeight : pref.componentSettings.formHeight,
                        formCss : pref.componentSettings.formCss,
                        properties : pref.componentSettings.properties,
                        urlServicePropertiesSearch : pref.componentSettings.urlServicePropertiesSearch,
                        urlServiceDatasetSearch : pref.componentSettings.urlServiceDatasetSearch,
                        preferencesPath : pref.componentSettings.preferencesPath,
                        preferencesFileName : pref.componentSettings.preferencesFileName,
                        formZones : pref.componentSettings.formZones
                    };
                    windowSettings = {
                        type : "formProject",
                        title : i18n.get('label.forms') + " : " + pref.componentSettings.formName,
                        id : "formProject" + pref.componentSettings.formId,
                        saveToolbar : true,
                        datasetName : pref.componentSettings.formName,
                        winWidth : 600,
                        winHeight : 600,
                        iconCls : "form"
                    };
                    SitoolsDesk.addDesktopWindow(windowSettings, componentCfg, jsObj);

                }
                
                if (type === "form") {
                    var datasetUrl = pref.componentSettings.dataUrl;
                    sitools.user.utils.DatasetUtils.openForm(pref.componentSettings.formId, datasetUrl, pref.componentSettings, pref.windowSettings);
                }
            }
        });
    },
    
    loadModulesInDiv : function () {
		Ext.each(Project.modulesInDiv, function (module) {
			
			var contentEl = Ext.get(module.get('divIdToDisplay'));
			if (Ext.isEmpty(contentEl)) {
				Ext.Msg.alert(i18n.get('label.error'), Ext.String.format(i18n.get('label.invalidModuleDivId'),
								module.name, module.divIdToDisplay));
			} else {
				
				var moduleComponent = Ext.create(module.get('xtype'));
				moduleComponent.create(Desktop.getApplication(), module.data);
				var view = moduleComponent.createViewForDiv();
				
				module = Ext.create('Ext.panel.Panel', {
					id : module.id,
					border : false,
					cls : "moduleInDiv",
					layout : 'fit',
					renderTo : module.get('divIdToDisplay'),
					height : contentEl.getHeight(),
					width : contentEl.getWidth(),
                    items : [ view ],
//                    items : [{
//                    	layout : 'fit',
//                    	xtype : module.get('xtype'),
//                    	listProjectModulesConfig : module.listProjectModulesConfig,
//                    	moduleProperties : module.properties
//                    }],
					listeners : {
						resize : function (me) {
							if (!Desktop.getDesktopMaximized() && module.container) {
								me.setSize(Ext.get(module.container).getSize());
							}
						},
						maximizeDesktop : function (me) {
							me.hide();
						},
						minimizeDesktop : function (me) {
						    if (module.container) {
                                me.setSize(Ext.get(module.container).getSize());
                            }
							me.show();
						}
					}

				});
				Desktop.modulesInDiv.push(module);
			}
		});
	},

	saveWindowSettings : function (forPublicUser) {
		var desktopSettings = this.getNavMode().getDesktopSettings(forPublicUser);

		userPreferences = {};
		userPreferences.windowSettings = desktopSettings;
		var projectSettings = {};
//		if (!Ext.isEmpty(SitoolsDesk.getDesktop().activePanel)) {
//			projectSettings.activeModuleId = SitoolsDesk.getDesktop().activePanel.id;
//		}
		projectSettings.desktopMaximizedMode = this.getDesktopMaximized();
		projectSettings.navigationMode = Project.getNavigationMode();

		userPreferences.projectSettings = projectSettings;
		
		if (forPublicUser) {
			PublicStorage.set("desktop", "/" + DEFAULT_PREFERENCES_FOLDER + "/" + Project.getProjectName(),
					userPreferences);
		} else {
			UserStorage.set("desktop", "/" + DEFAULT_PREFERENCES_FOLDER + "/" + Project.getProjectName(),
					userPreferences);
		}
	},
	
	clearDesktop : function () {
		Desktop.setShowDesktopAction('minimize');
		Desktop.restoreAllWindows();
		Desktop.getApplication().getController('DesktopController').desktopView.windows.each(function (window) {
			if (window instanceof Ext.window.Window || window instanceof Ext.panel.Panel) {
				window.close();
			}
		});
	},
	
	showDesktop : function () {
		var windowCollection = Desktop.getApplication().getController('DesktopController').desktopView.windows;
		if (windowCollection.items.length == 0) {
			return;
		}
		
		if (Desktop.getShowDesktopAction() == 'minimize') {
			Desktop.minifyAllWindows();
			Desktop.setShowDesktopAction('restore');
		} else {
			Desktop.restoreAllWindows();
			Desktop.setShowDesktopAction('minimize');
		}
	},
	
	minifyAllWindows : function () {
		Desktop.getApplication().getController('DesktopController').desktopView.windows.each(function (window) {
			if (window.xtype == 'window') {
				window.minimize();
			} else if (window instanceof Ext.panel.Panel) {
				window.hide();
			}
		});
	},
	
	restoreAllWindows : function () {
		Desktop.getApplication().getController('DesktopController').desktopView.windows.each(function (window) {
			if (window.xtype == 'window' || window instanceof Ext.panel.Panel) {
				window.show();
			}
		});
	},
	
	activeNextPanel : function () {
		var desktopController = this.getApplication().getController('DesktopController');
		var nextButton = desktopController.desktopView.taskbar.getNextButton();
		
		if (nextButton) {
			nextButton.fireEvent('click', nextButton);
		}
	},
	
	activePreviousPanel : function () {
		var desktopController = this.getApplication().getController('DesktopController');
		var previousButton = desktopController.desktopView.taskbar.getPreviousButton();
		
		if (previousButton) {
			previousButton.fireEvent('click', previousButton);
		}
		
	}
});

