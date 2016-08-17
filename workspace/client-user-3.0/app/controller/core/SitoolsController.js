/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, i18n, loadUrl, getDesktop, sitools, SitoolsDesk */
Ext.define('sitools.user.controller.core.SitoolsController', {

    extend : 'Ext.app.Controller',

    stores : [ 'ProjectStore', 'ModulesStore' ],
    
    

    init : function () {
        var me = this, desktopCfg;

        this.control({
            
            'moduleTaskBar #sitoolsButton' : {
				click : function (btn) {
					btn.toggle();
					Ext.create('sitools.user.view.header.ModuleDataView').show();
//					this.dvModules.show();
				}
			},
			
            'moduleToolbar > toolbar >button' : {
                click : function (btn, event) {
                    var moduleRecord = btn.module;
                    
                    if (moduleRecord) {
                        this.openModule(moduleRecord, {
                            event : event,
                            triggerComponent : btn
                        });
                    }
                }
            },
            
            'moduleToolbar > toolbar > button > menu > menuitem' : {
                click : function (btn, event) {
                    var moduleRecord = btn.module;
                    
                    if (moduleRecord) {
                        this.openModule(moduleRecord, {
                            event : event,
                            triggerComponent : btn
                        });
                    }
                }
            },
            
			"component [type='module']" : {
			    registermodule : function (module, view) {
			        module.moduleModel.set("instantiated", true);
			        module.moduleModel.set("viewClassType", view.$className);
			        module.moduleModel.set("instance", module);
			    }
			},
			"panel,window" : {
			    beforeclose : function (viewContainer) {
                    if (viewContainer.specificType === "moduleWindow") {
                        var view = viewContainer.down('component[type="module"]');
                        
                        var store = this.getStore("ModulesStore");
                        var module = store.findRecord("viewClassType", view.$className);
                        
                        module.set("instantiated", false);
                        module.set("viewClassType", null);
                        module.set("instance", null);
                    }
                }
			}
        });
        
        
        this.getApplication().on('projectInitialized', this.loadProject, this);
        this.getApplication().on('footerLoaded', Desktop.loadPreferences, this);
        this.getApplication().on('footerLoaded', Desktop.loadModulesInDiv, this);
    },
    
    // 8
    loadProject : function () {
        var url = Project.getSitoolsAttachementForUsers();
        var store = this.getStore("ProjectStore");
        store.setCustomUrl(url);
        store.load({
            scope : this,
            callback : function (records, operation, success) {
            	this.loadModules();
            }
        });
    },
    
    // 9
    loadModules : function () {
    	var store = this.getStore("ModulesStore");
    	var url = Project.getSitoolsAttachementForUsers() + loadUrl.get('APP_PROJECTS_MODULES_URL');
        store.setCustomUrl(url);
        
        store.load({
        	scope : this,
        	callback : function (modules, operation, success) {
                this.getApplication().noticeProjectLoaded();
            }
        })
    },

    openModule : function (moduleModel, componentConfig) {
        if (moduleModel.get("instantiated")) {
            var module = moduleModel.get("instance");
            module.show(module.getViewCmp());
        }
        else {
            var module = Ext.create(moduleModel.data.xtype);
            module.create(this.getApplication(), moduleModel, function() {
                this.init(componentConfig);
            }, module);
        }
    },
    
    openComponent : function (componentClazz, componentConfig, windowConfig) {
        
        if (Ext.isEmpty(windowConfig) ||  windowConfig.saveToolbar === false) {
            this.doOpenComponent(componentClazz, componentConfig, windowConfig);
            return;
        }

        //construction de l'url pour les préférences utilisateur. 
        var baseFilePath = "/" + DEFAULT_PREFERENCES_FOLDER + "/" + Project.getProjectName();

        var filePath = componentConfig.preferencesPath;
        var fileName = componentConfig.preferencesFileName;
        if (Ext.isEmpty(filePath) || Ext.isEmpty(fileName)) {
            this.doOpenComponent(componentClazz, componentConfig, windowConfig);
            return;
        }
        filePath = baseFilePath + filePath;

        //Méthod to call if no userPreferences found
        var doOpenPublic = function (componentClazz, componentConfig, windowConfig) {
            var successPublic = function (response, opts) {
                try {
                    var json = Ext.decode(response.responseText);

                    Ext.apply(windowConfig, json.windowSettings);
                    Ext.apply(componentConfig, 
                        {
                            userPreference : json.componentSettings
                        });
                    this.doOpenComponent(componentClazz, componentConfig, windowConfig);
                } catch (err) {
                    this.doOpenComponent(componentClazz, componentConfig, windowConfig);
                    throw err;
                }
            };

            var failurePublic = function (response, opts) {
                this.doOpenComponent(componentClazz, componentConfig, windowConfig);
            };

            PublicStorage.get(fileName, filePath, this, successPublic,
                    failurePublic);
        };

        if (Ext.isEmpty(userLogin)) {
            Ext.callback(doOpenPublic, this, [componentClazz, componentConfig, windowConfig]);
        } else {
            //Méthode appelée si l'on trouve des préférences pour le user
            var successMethod = function (response, opts) {
                try {
                    var json = Ext.decode(response.responseText);

                    Ext.apply(windowConfig, json.windowSettings);
                    Ext.apply(componentConfig, 
                        {
                            userPreference : json.componentSettings
                        });
                } catch (err) {
                    doOpenPublic(componentClazz, componentConfig, windowConfig);
                    return;
                }
                this.doOpenComponent(componentClazz, componentConfig, windowConfig);
            };
            //Si pas de préférences trouvées, on utilise addWinPublic
            var failureMethod = function (response, opts) {
                Ext.callback(doOpenPublic, this, [componentClazz, componentConfig, windowConfig]);
            };

            UserStorage.get(fileName, filePath, this, successMethod,
                    failureMethod);
        }
    },
    
    
    
    doOpenComponent : function (componentClazz, componentConfig, windowConfig) {
        var component = Ext.create(componentClazz);
        component.create(this.getApplication(), function() {
            component.init(componentConfig, windowConfig);
        }, component);
        return component;
    },
    
    onOpenModule : function (button, e, opts) {
        // get the module from the button
        var module = button.module;
        this.openModule(module);
    },
    
    openSimpleWindow : function (view, windowConfig) {
        var project = Ext.getStore("ProjectStore").getProject();
        var navMode = Desktop.getApplication().getController('core.NavigationModeFactory').getNavigationMode(project.get("navigationMode"));
        navMode.openComponent(view, windowConfig);
    }
});