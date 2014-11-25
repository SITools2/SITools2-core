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
Ext.define('sitools.user.controller.core.FixedMode', {

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
        this.getApplication().getController('DesktopController').createPanel(view, windowConfig);
    },
    
    openModule : function (view, module) {
        var windowConfig = {
    		id: module.get('id'),
            name : module.get('name'),
            title : i18n.get(module.get('title')),
            iconCls : module.get('icon'),
            label : module.get('label'),
            specificType : 'moduleWindow'
        };
        
        Ext.apply(windowConfig, this.getStatefullWindowConfig());
        this.getApplication().getController('DesktopController').createPanel(view, windowConfig);
    },
    
    getFormOpenMode : function () {
        return sitools.user.component.DatasetOverview;
    },
    
    getDesktopSettings : function (forPublicUser) {
        var desktopSettings = [];
        
        var activePanel = Ext.WindowManager.getActive();
        
    	Ext.WindowManager.each(function (window) {
            var componentSettings;
            if (!Ext.isEmpty(window.specificType) && (window.specificType === 'componentWindow' || window.specificType === 'moduleWindow')) {
                if (Ext.isFunction(window.saveSettings) && window != Ext.WindowManager.getActive()) {
                    var component = window.items.items[0];

                    componentSettings = component._getSettings();
                    desktopSettings.push(window.saveSettings(componentSettings, forPublicUser));
                }
            }
        });
    	
    	if (!Ext.isEmpty(activePanel.specificType) && (activePanel.specificType === 'componentWindow' || activePanel.specificType === 'moduleWindow')) {
            if (Ext.isFunction(activePanel.saveSettings)) {
            	 var component = activePanel.items.items[0];

                 componentSettings = component._getSettings();
                 desktopSettings.push(activePanel.saveSettings(componentSettings, forPublicUser));
            	
            }
    	}
    	
        return desktopSettings;
    },
    
    minimize : function (desktopView) {
    	desktopView.windows.each(function (win) {
			
			win.setHeight(Desktop.getDesktopEl().getHeight() - desktopView.down('taskbar').getHeight() - desktopView.down('moduleToolbar').getHeight());
			win.setWidth(Desktop.getDesktopEl().getWidth());
			
		}, this);
    },
    
    maximize : function (desktopView) {
    	desktopView.windows.each(function (win) {
		
			win.setHeight(Desktop.getDesktopEl().getHeight() - desktopView.down('taskbar').getHeight() - desktopView.down('moduleToolbar').getHeight());
			win.setWidth(Desktop.getDesktopEl().getWidth());
		
    	}, this);
    },
    
    getStatefullWindowConfig : function () {
		return {
			saveSettings : function (componentSettings, forPublicUser) {
			    if (Ext.isEmpty(userLogin)) {
				    Ext.Msg.alert(i18n.get('label.warning', 'label.needLogin'));
				    return;
			    }
			    
			    // TODO find a better way to set the right Y position
			    var position = {
		    		x : this.getX(),
		    		y : this.getY()
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
	},
	
	getStatefullComponentConfig : function () {
		return {
			saveSettings : function (componentSettings, forPublicUser) {
			    if (Ext.isEmpty(userLogin)) {
				    Ext.Msg.alert(i18n.get('label.warning', 'label.needLogin'));
				    return;
			    }
			    
			    // TODO find a better way to set the right Y position
			    var position = {
		    		x : this.getX(),
		    		y : this.getY()
			    };
			    
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
        
        var cleanDesktopButton = Ext.create('Ext.menu.Item', {
            action : "minimize",
            text : i18n.get('label.removeActiveModule'),
            iconCls : 'delete_button_small_img',
            cls : 'navBarButtons-icon',
            cls : 'menuItemCls',
            handler : function (btn) {
            	Desktop.clearDesktop();
            }
        });
        
        var showDesktopButton = Ext.create('Ext.menu.Item', {
            action : "minimize",
            text : i18n.get("label.showDesktopButton"),
            iconCls : 'desktop_button_small_img',
            cls : 'navBarButtons-icon',
            cls : 'menuItemCls',
            handler : function (btn) {
            	Desktop.showDesktop();
            }
        });
        
        var moreButton = Ext.create('Ext.Button', {
            iconCls : 'more_button_img',
            cls : 'navBarButtons-icon',
            scale : "medium", 
            arrowCls : null,
            menu : {
            	xtype : 'menu',
            	border : false,
            	plain : true,
            	items : [cleanDesktopButton, showDesktopButton]
            },
            listeners : {
				afterrender : function (btn) {
					var label = i18n.get('label.moreAction');
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
        buttons.push(moreButton);
        
        buttons.push('-');
        
    	var previousButton = Ext.create('Ext.button.Button', {
            scope : this, 
            handler : function () {
            	Desktop.activePreviousPanel();
            }, 
            scale : "medium", 
//            cls : 'sitools_button_main',
            cls : 'navBarButtons-icon',
            iconCls : 'previous_button_img',
            listeners : {
				afterrender : function (btn) {
					var label = i18n.get('label.previousWindow');
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
    	buttons.push(previousButton);

    	
        var nextButton = Ext.create('Ext.button.Button', {
            scope : this, 
            handler : function () {
            	Desktop.activeNextPanel();
            }, 
            scale : "medium",
//            cls : 'sitools_button_main',
            cls : 'navBarButtons-icon',
            iconCls : 'next_button_img',
            listeners : {
				afterrender : function (btn) {
					var label = i18n.get('label.nextWindow');
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
        buttons.push(nextButton);
            
        var removeCurrentButton = Ext.create('Ext.button.Button', {
            scope : this,
            handler : function () {
            	var desktopView = Desktop.getApplication().getController('DesktopController').desktopView;
            	var active;
            	if (active = desktopView.getActiveWindow()) {
            		active.close();
            	}
            }, 
            scale : "medium",
//            cls : 'sitools_button_main',
            cls : 'navBarButtons-icon',
            iconCls : 'delete_button_img',
            listeners : {
				afterrender : function (btn) {
					var label = i18n.get('label.removeCurrentPanel');
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
        buttons.push(removeCurrentButton);
        
        return buttons;
	},
	
    /**
     * @return the specific JS View to display dataset
     */
    getDatasetOpenMode : function (dataset) {
//    	 TODO implement DatasetOverview
//    	return sitools.user.component.DatasetOverview;
    	
    	return dataset.datasetView.jsObject;
    },
	
	/**
     * Specific multiDataset search context methods
     */
    multiDataset : {
    	
        /**
         * Returns the right object to show multiDs results
         * @returns
         */
        getObjectResults : function () {
            return sitools.user.view.component.form.OverviewResultProjectForm;
        }, 
        
        /**
         * Handler of the button show data in the {sitools.user.component.forms.resultsProjectForm} object 
         * @param {Ext.grid.GridPanel} grid the grid results
         * @param {int} rowIndex the index of the clicked row
         * @param {int} colIndex the index of the column
         * @returns
         */
        showDataset : function (grid, rowIndex, colIndex) {
            var rec = grid.getStore().getAt(rowIndex);
            if (Ext.isEmpty(rec)) {
                return;
            }
            if (rec.get('status') == "REQUEST_ERROR") {
                return;
            }
            
            Ext.Ajax.request({
                method : "GET", 
                url : rec.get('url'), 
                scope : this, 
                success : function (ret) {
                    var Json = Ext.decode(ret.responseText);
                    if (showResponse(ret)) {
                        var dataset = Json.dataset;
                        var componentCfg, JsObj;
                        
                        JsObj = eval(dataset.datasetView.jsObject);
                        componentCfg = {
                            title : dataset.name, 
                            closable : true, 
                            dataUrl : dataset.sitoolsAttachementForUsers,
                            datasetId : dataset.Id,
                            datasetCm : dataset.columnModel, 
                            datasetName : dataset.name,
                            dictionaryMappings : dataset.dictionaryMappings,
                            datasetViewConfig : dataset.datasetViewConfig, 
                            sitoolsAttachementForUsers : dataset.sitoolsAttachementForUsers, 
                            formMultiDsParams : this.formMultiDsParams
                        };
                        var dataview = new JsObj(componentCfg);
                        
                        this.ownerCt.southPanel.add(dataview);
                        this.ownerCt.southPanel.setVisible(true);
                        this.ownerCt.southPanel.expand();
                        this.ownerCt.southPanel.setActiveTab(this.ownerCt.southPanel.items.length - 1);
                        this.ownerCt.doLayout();
                    }
                }, 
                failure : alertFailure
            });

        }
        
    }
	
});