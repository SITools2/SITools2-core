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
/*global Ext, sitools, window, showVersion, publicStorage, userLogin, projectGlobal, SitoolsDesk, showResponse, i18n, extColModelToJsonColModel, loadUrl*/

/**
 * @cfg {Array} modules la liste des modules
 * @class sitools.user.component.entete.NavBar
 * @extends Ext.Toolbar
 */
Ext.define('sitools.user.view.header.ModuleTaskBarView', {
    extend : 'Ext.Toolbar',
    alias: 'widget.moduleTaskBar',
    
    initComponent : function () {
        var items = [];
//        var categories = this.categorizeModules();

        var homeButton = Ext.create('Ext.Button', {
        	itemId : 'sitoolsButton',
            scale : "medium",
            cls : 'sitools_button_main',
            iconCls : 'sitools_button_img',
            listeners : {
				afterrender : function (btn) {
					var label = i18n.get('label.mainMenu');
					var tooltipCfg = {
							html : label,
							target : btn.getEl(),
							anchor : 'bottom',
							anchorOffset : 10,
							showDelay : 20,
							hideDelay : 50,
							dismissDelay : 0
					};
					Ext.create('Ext.tip.ToolTip', tooltipCfg);
				}
			}
        });
        items.push(homeButton);
        items.push('-');
        
        if (Project.getNavigationMode() == 'fixed') { // adding navigation button
        	this.width = 180;
        	
        	var previousButton = Ext.create('Ext.button.Button', {
                scope : this, 
                handler : function () {
                	Desktop.activePreviousPanel();
                }, 
                scale : "medium", 
                cls : 'sitools_button_main',
                iconCls : 'previous_button_img',
                listeners : {
    				afterrender : function (btn) {
    					var label = i18n.get('label.previous');
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
            items.push(previousButton);

        	
            var nextButton = Ext.create('Ext.button.Button', {
                scope : this, 
                handler : function () {
                	Desktop.activeNextPanel();
                }, 
                scale : "medium",
                cls : 'sitools_button_main',
                iconCls : 'next_button_img',
                listeners : {
    				afterrender : function (btn) {
    					var label = i18n.get('label.next');
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
            items.push(nextButton);
        }
        
        var cleanDesktopButton = Ext.create('Ext.menu.Item', {
            action : "minimize",
            text : i18n.get('label.removeActiveModule'),
            iconCls : 'delete_button_img',
            cls : 'menuItemCls',
            handler : function (btn) {
            	Desktop.clearDesktop();
            }
        });
        
        var showDesktopButton = Ext.create('Ext.menu.Item', {
            action : "minimize",
            text : i18n.get("label.showDesktopButton"),
            iconCls : 'desktop_button_img',
            cls : 'menuItemCls',
            handler : function (btn) {
            	Desktop.showDesktop();
            }
        });
        
        var moreButton = Ext.create('Ext.Button', {
            id : 'btn-more',
            iconCls : 'more_button_img',
            cls : (Project.getNavigationMode() == 'fixed') ? 'sitools_button more_button_fixedMode' : 'sitools_button',
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
        items.push(moreButton);
        
        this.callParent(Ext.apply(this, {
            enableOverflow : true,
            items : items,
            cls : 'sitoolsTaskbar-bg',
            border : false
        }));
    },
    /**
     * From the modules attribute, return an array of categories. Each items of
     * the array could be either - { modules : [module] } - { category :
     * categoryName, modules : [modules] }
     * 
     * @returns {Array}
     */
    categorizeModules : function () {
        function getCategoryIndex (category, categoryList) {
            var idx = -1;
            for ( var i = 0; i < categoryList.length; i++) {
                if (categoryList[i].category === category) {
                    return i;
                }
            }
            return idx;
        }
        var categoryModules = [];
        this.modules.each(function (module) {
            var categoryModule = module.get('categoryModule'); 
            if (Ext.isEmpty(categoryModule)) {
                categoryModules.push({
                    modules : [ module ]
                });
            } else {
                var idx = getCategoryIndex(categoryModule, categoryModules);
                if (idx >= 0) {
                    categoryModules[idx].modules.push(module);
                } else {
                    categoryModules.push({
                        category : categoryModule,
                        modules : [ module ]
                    });
                }
            }

        });
        return categoryModules;
    }
});
