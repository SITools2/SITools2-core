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
                        this.openModule(moduleRecord, event);
                    }
                }
            },
            
            'moduleToolbar > toolbar button menu menuitem' : {
                click : function (btn, event) {
                    var moduleRecord = btn.module;
                    
                    if (moduleRecord) {
                        this.openModule(moduleRecord, event);
                    }
                }
            },
            
			'moduleDataview' : {
				boxready : function (window) {
					window.center();
				},
				
				afterrender : function (window) {
					focusDataview = function (window) {
						this.focus();
					}
					Ext.defer(focusDataview, 400, window);
				},
				
				render : function (window) {
					window.getEl().on('keypress', function(e) {
	                      if (e.getKey() == e.ECHAP) {
	                    	  var moduleDataview = dataview.up('moduleDataview');
	                    	  moduleDataview.hide();
	                    	  Desktop.getDesktopEl().unmask();
	                      }
	                 });
				}
			},
			
			'moduleDataview dataview' : {
				itemclick : function (dataview, moduleRecord, item, index, event) {
					
					var moduleDataview = dataview.up('moduleDataview');
					
					if (!Ext.isEmpty(moduleRecord.get('properties')) && moduleRecord.get('properties').category) {
						var category = moduleRecord.get('name');
						
						moduleDataview.store.removeAll();
						
						moduleDataview.moduleStore.each(function (module) {
				    		var categoryModule = module.get('categoryModule');
				    		if (category === categoryModule) {
				    			module.set('type', 'module');
				    			moduleDataview.store.add(module);
				    		}
				    	});
						
					} else {
						var type = moduleRecord.get('type');
						var module = Ext.create(moduleRecord.get("xtype"));
						if (type == 'module') {
							if (Ext.isFunction(module.openMe)) {
								module.openMe(moduleDataview, moduleRecord, event);
							} else {
								this.openModule(moduleRecord, event);
								moduleDataview.hide();
								Desktop.getDesktopEl().unmask();
							}
						} else if (type == 'component' && Ext.isFunction(module.openMyComponent)) {
							module.openMyComponent(moduleRecord, event);
							moduleDataview.hide();
							Desktop.getDesktopEl().unmask();
						}
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
//                Ext.each(modules, function (service) {
//                    var dependencies = service.dependencies;
//                    if (!Ext.isEmpty(dependencies)) {
//                        includeJsForceOrder(dependencies.js, 0, Ext.emptyFn);
//
//                        Ext.each(dependencies.css, function (css) {
//                            includeCss(css.url);
//                        });
//                    }
//                });

                this.loadGuiServices();
            }
        })
    },
    
    loadGuiServices : function () {
        Ext.Ajax.request({
            method : 'GET',
            url : Project.getSitoolsAttachementForUsers() + loadUrl.get('APP_GUI_SERVICES_URL'),
            scope : this,
            success : function (response) {
                var services = Ext.decode(response.responseText).data;
		        Ext.each(services, function (service) {
		            var dependencies = service.dependencies;
		            if (!Ext.isEmpty(dependencies)) {
		                includeJsForceOrder(dependencies.js, 0, Ext.emptyFn);
		                
		                Ext.each(dependencies.css, function (css) {
		                    includeCss(css.url);
		                });
		            }
		        });
            },
            callback : function () {
                this.getApplication().noticeProjectLoaded();
            }
        });
    },

    openModule : function (moduleModel, event) {
        if (moduleModel.get("instantiated")) {
            var module = moduleModel.get("instance");
            module.show(module.getViewCmp());
        }
        else {
            var module = Ext.create(moduleModel.data.xtype);
            module.create(this.getApplication(), moduleModel, function() {
                this.init(event);
            }, module);
        }
    },
    
    openComponent : function (componentClazz, componentConfig, windowConfig) {
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
    }
});