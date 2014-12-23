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
Ext.namespace('sitools.user.controller.core');

Ext.define('sitools.user.controller.core.DesktopMode', {

    extend : 'sitools.user.controller.core.NavigationMode',
    
    openComponent : function (view, windowConfig) {
    	Ext.applyIf(windowConfig, {
    		x : (!Ext.isEmpty(windowConfig.position)) ? windowConfig.position.x : undefined,
    		y : (!Ext.isEmpty(windowConfig.position)) ? windowConfig.position.y : undefined,
    		width : (!Ext.isEmpty(windowConfig.size)) ? windowConfig.size.width : undefined,
    		height : (!Ext.isEmpty(windowConfig.size)) ? windowConfig.size.height : undefined,
    		specificType : 'componentWindow'
    	});
    	
    	Ext.apply(windowConfig, this.getStatefullComponentConfig());
        this.getApplication().getController('DesktopController').createWindow(view, windowConfig);
    },
    
    openModule : function (view, module) {
        var windowConfig = {
    		id: module.get('id'),
            name : module.get('name'),
            title : i18n.get(module.get('title')),
            width : module.get('defaultWidth'),
            height : module.get('defaultHeight'),
            iconCls : module.get('icon'),
            label : module.get('label'),
            x : module.get('x'),
            y : module.get('y'),
            specificType : 'moduleWindow'
        };
        
        Ext.apply(windowConfig, this.getStatefullWindowConfig());
        this.getApplication().getController('DesktopController').createWindow(view, windowConfig);
    },
    
    getFormOpenMode : function () {
        return "sitools.user.component.form.FormComponent";
    },
    
    getDesktopSettings : function (forPublicUser) {
        var desktopSettings = [];
    	Ext.WindowManager.each(function (window) {
            var componentSettings;
            if (!Ext.isEmpty(window.specificType) && (window.specificType === 'componentWindow' || window.specificType === 'moduleWindow')) {
                // Bug 3358501 : add a test on Window.saveSettings.
                if (Ext.isFunction(window.saveSettings)) {
//                    var component = window.get(0);
                    var component = window.items.items[0];

                    componentSettings = component._getSettings();
                    desktopSettings.push(window.saveSettings(componentSettings, forPublicUser));
                }
            }
        });
        return desktopSettings;
    },
    
    minimize : function (desktopView) {
		desktopView.windows.each(function (win) {
			
			if (win.maximized) {
				win.setHeight(Desktop.getDesktopEl().getHeight() - desktopView.down('taskbar').getHeight() - desktopView.down('moduleToolbar').getHeight());
				win.setWidth(Desktop.getDesktopEl().getWidth());
			}
			
			if (win.getHeight() > Desktop.getDesktopEl().getHeight()) {
				win.setHeight(Desktop.getDesktopEl().getHeight() - desktopView.down('taskbar').getHeight() - desktopView.down('moduleToolbar').getHeight());
			}
			if (win.getWidth() > Desktop.getDesktopEl().getWidth()) {
				win.setWidth(Desktop.getDesktopEl().getWidth());
			}
			
		}, this);
    },
    
    maximize : function (desktopView) {
    	desktopView.windows.each(function (win) {
		
			if (win.maximized) {
				win.setHeight(Desktop.getDesktopEl().getHeight() - desktopView.down('taskbar').getHeight() - desktopView.down('moduleToolbar').getHeight());
				win.setWidth(Desktop.getDesktopEl().getWidth());
			}
			
			if (win.getHeight() > Desktop.getDesktopEl().getHeight()) {
				win.setHeight(Desktop.getDesktopEl().getHeight() - desktopView.down('taskbar').getHeight() - desktopView.down('moduleToolbar').getHeight());
			}
			if (win.getWidth() > Desktop.getDesktopEl().getWidth()) {
				win.setWidth(Desktop.getDesktopEl().getWidth());
			}
		
    	}, this);
    },
    
    getStatefullWindowConfig : function () {
		return {
			saveSettings : function (componentSettings, forPublicUser) {
			    if (Ext.isEmpty(userLogin)) {
				    Ext.Msg.alert(i18n.get('label.warning', 'label.needLogin'));
				    return;
			    }
			    
			    var winPosition = this.getPosition(true); 
			    var position = {
					x : winPosition[0],
					y : winPosition[1]
				}
			    
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
			    
			    //save the parameters to the current module
			    var module = Ext.StoreManager.lookup('ModulesStore').getById(this.getId());
            	
            	if (!Ext.isEmpty(module)) {
            		Ext.apply(module.data, {
            			defaultHeight : putObject.windowSettings.size.height,
            			defaultWidth : putObject.windowSettings.size.width,
            			x : putObject.windowSettings.position.x,
            			y : putObject.windowSettings.position.y
            		});
            	}
			    
			    return putObject;
		    }
		};
	},
	
	getStatefullComponentConfig : function () {
		return {
			saveSettings : function (componentSettings, forPublicUser) {
			    if (Ext.isEmpty(userLogin)) {
				    Ext.Msg.alert(i18n.get('label.warning', 'label.needLogin'));
				    return;
			    }
			    
			    var winPosition = this.getPosition(true); 

			    var position = {
					x : winPosition[0],
					y : winPosition[1]
				}
			    
			    var size = {
		    		height : this.getHeight(),
		    		width : this.getWidth()
			    };

			    var putObject = {};
			    putObject.componentSettings = componentSettings;

			    putObject.windowSettings = {};
			    putObject.windowSettings.size = size;
			    putObject.windowSettings.position = position;
			    putObject.windowSettings.specificType = this.specificType;
			    putObject.windowSettings.componentId = this.getId();
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
	},
	
	createButtonsLeftTaskbar : function () {
		var buttons = [];
		
//		var homeButton = Ext.create('Ext.Button', {
//        	itemId : 'sitoolsButton',
//            scale : "medium",
////            cls : 'sitools_button_main',
//            cls : 'navBarButtons-icon',
//            iconCls : 'main_button_img',
//            listeners : {
//				afterrender : function (btn) {
//					var label = i18n.get('label.mainMenu');
//					var tooltipCfg = {
//							html : label,
//							target : btn.getEl(),
//							anchor : 'bottom',
//							anchorOffset : 10,
//							showDelay : 20,
//							hideDelay : 50,
//							dismissDelay : 0
//					};
//					Ext.create('Ext.tip.ToolTip', tooltipCfg);
//				}
//			}
//        });
//		buttons.push(homeButton);
        
        var showDesktopButton = Ext.create('Ext.Button', {
            action : "minimize",
            iconCls : 'desktop_button_img',
            cls : 'navBarButtons-icon',
            scale : "medium",
            handler : function (btn) {
            	Desktop.showDesktop();
            },
             listeners : {
                afterrender : function (btn) {
                    var label = i18n.get("label.showDesktopButton");
                    var tooltipCfg = {
                            html : label,
                            target : btn.getEl(),
                            anchor : 'bottom',
                            anchorOffset : 5,
                            showDelay : 20,
                            hideDelay : 50,
                            dismissDelay : 0
                    };
                    Ext.create('Ext.tip.ToolTip', tooltipCfg);
                }
            }
        });
        buttons.push(showDesktopButton);
        
        var cleanDesktopButton = Ext.create('Ext.Button', {
            action : "minimize",
            iconCls : 'delete_button_img',
            cls : 'navBarButtons-icon',
            scale : "medium", 
            handler : function (btn) {
            	Desktop.clearDesktop();
            },
             listeners : {
                afterrender : function (btn) {
                    var label = i18n.get('label.removeActiveModule');
                    var tooltipCfg = {
                            html : label,
                            target : btn.getEl(),
                            anchor : 'bottom',
                            anchorOffset : 5,
                            showDelay : 20,
                            hideDelay : 50,
                            dismissDelay : 0
                    };
                    Ext.create('Ext.tip.ToolTip', tooltipCfg);
                }
            }
        });
        buttons.push(cleanDesktopButton);
        
        
//        var moreButton = Ext.create('Ext.Button', {
//            iconCls : 'more_button_img',
//            cls : 'navBarButtons-icon',
//            scale : "medium", 
//            arrowCls : null,
//            menu : {
//            	xtype : 'menu',
//            	border : false,
//            	plain : true,
//            	items : [cleanDesktopButton, showDesktopButton]
//            },
//            listeners : {
//				afterrender : function (btn) {
//					var label = i18n.get('label.moreAction');
//					var tooltipCfg = {
//							html : label,
//							target : btn.getEl(),
//							anchor : 'bottom',
//							anchorOffset : 5,
//							showDelay : 20,
//							hideDelay : 50,
//							dismissDelay : 0
//					};
//					Ext.create('Ext.tip.ToolTip', tooltipCfg);
//				}
//			}
//        });
//        buttons.push(moreButton);
        
        return buttons;
	},
	
    /**
     * @return the specific JS View to display dataset
     */
    getDatasetOpenMode : function (dataset) {
        return dataset.datasetView.jsObject;
    },
	
	/**
     * Specific multiDataset search context methods
     */
    multiDataset : {
    	
        /**
         * Returns the right object to show multiDs results
         * 
         * @returns
         */
        getObjectResults : function () {
            return "sitools.user.view.component.form.ResultProjectForm";
        },
        
        /**
         * Handler of the button show data in the
         * {sitools.user.component.forms.resultsProjectForm} object
         * 
         * @param {Ext.grid.GridPanel}
         *            grid the grid results
         * @param {int}
         *            rowIndex the index of the clicked row
         * @param {int}
         *            colIndex the index of the column
         * @returns
         */
		showDataset : function (grid, rec, formConceptFilters) {
            if (Ext.isEmpty(rec)) {
                return;
            }
            if (rec.get('status') == "REQUEST_ERROR") {
                return;
            }
            
            var url = rec.get("url");
            sitools.user.utils.DatasetUtils.openDataset(url, {
				formConceptFilters : formConceptFilters
            });
        }
    }
	
});