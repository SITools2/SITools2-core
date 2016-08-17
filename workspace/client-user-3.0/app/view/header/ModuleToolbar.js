/***************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 * @class sitools.user.view.header.ModuleDataView
 * @extends Ext.panel.Panel
 */
Ext.define('sitools.user.view.header.ModuleToolbar', {
    extend : 'Ext.toolbar.Toolbar',
    alias: 'widget.moduleToolbar',
    
//    height : 25,
    border : false,
//    shadow : false,
    flex: 1,
    cls: 'ux-taskbar moduleTaskbar-bg',
    layout: { overflowHandler: 'Scroller' },
    
    initComponent : function () {
        
    	this.moduleStore = Ext.data.StoreManager.lookup("ModulesStore");
    	
//    	this.store = Ext.create('sitools.user.store.DataviewModulesStore');
    	
        var categories = this.categorizeModules();
        var items = ['|'];
        
        Ext.each(categories, function (category) {
            var modules = category.modules;
            //Le module n'appartient pas à une catégorie: inclusion en tant que bouton dans le menu.
            if (Ext.isEmpty(category.category)) {
                var module = modules[0];
                var xtype = module.get('xtype');
                try {
                    if (Ext.isEmpty(module.get('divIdToDisplay'))) {
                        var item = {
                            xtype : 'button',
//                            width : 150,
                            itemId : module.get('id'),
                            text : i18n.get(module.get('label')),
                            iconCls : module.get('icon'),
                            cls : 'module_button',
                            module : module,
                            listeners : {
				                afterrender : function (btn) {
                                    var description = btn.module.get('description');
				                    var label = i18n.get(description);
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
                        };
                        items.push(item);
                        items.push('|');
                    }
                }
                catch(err) {
                    //Nothing to do
                    var tmp = null;
                }
            }
            //Le module est dans une catégorie : On crée un menu contenant tous les modules de la catégorie
            else {
                var menuItems = [];
                Ext.each(category.modules, function (moduleInCategory) {
                    if (Ext.isEmpty(moduleInCategory)) {
                        return;
                    }
                    
                    if (Ext.isEmpty(moduleInCategory.get('divIdToDisplay'))) {
                        var item = {
                            xtype : 'menuitem',
                            itemId : moduleInCategory.get('id'),
                            text : i18n.get(moduleInCategory.get('label')),
                            iconCls : moduleInCategory.get('icon'),
                            cls : 'menuItemCls',
                            module : this
                        };
                        menuItems.push(item);
                        menuItems.push({
			                xtype : 'menuseparator',
			                separatorCls : 'customMenuSeparator'
			            });

//                            //Test spécifique pour savoir si on doit inclure un sous menu :
//                            var xtype = moduleInCategory.get('xtype');
//                            if (Ext.isEmpty(xtype)) {
//                                return;
//                            }
//                            var Func = eval(xtype + ".getStaticParameters");
//                            if (Ext.isFunction(Func)) {
//                                var staticParameters = Func();
//                                if (staticParameters && staticParameters.showAsMenu) {
//                                    Ext.apply(item, {
//                                        menu : {
//                                            xtype : moduleInCategory.get('xtype'),
//                                            cls : "sitools-navbar-menu"
//                                        }
//                                    });
//                                }
//                                else {
//                                    Ext.apply(item, {
//                                        handler : moduleInCategory.openModule
//                                    });
//                                }
//                            }
                    }
                });
                 var item = {
                    xtype : 'button',
                    text : category.category,
                    cls : 'module_button',
                    menu : Ext.create('Ext.menu.Menu', {
                        border : false,
                        plain : true,
                        items : menuItems                 
                    })
                };
                items.push(item);
                items.push('|');
            }
        });
        
        this.windowBar = new Ext.toolbar.Toolbar({
            flex: 1,
            height : 25,
            cls: 'ux-desktop-windowbar',
            items: items,
            layout: { overflowHandler: 'Scroller' }
        });
        
        this.items = [this.windowBar];
        this.callParent();
    },
    
    /**
     * From the modules attribute, return an array of categories. 
     * Each items of the array could be either 
     *  - {
     *      modules : [module]
     *    }
     *  - {
     *      category : categoryName, 
     *      modules : [modules]
     *    }
     * @returns {Array}
     */
    categorizeModules : function () {
        
        function getCategoryIndex(category, categoryList) {
            var idx = -1;
            for (var i = 0; i < categoryList.length; i++) {
                if (categoryList[i].category === category) {
                    return i;
                }
            }
            return idx;
        }
        
        var categoryModules = [];
        this.moduleStore.each(function (module) {
        	if (Ext.isEmpty(module.get('divIdToDisplay'))) {
        	
	            if (Ext.isEmpty(module.get('categoryModule'))) {
	                categoryModules.push({
	                    modules : [module]
	                });
	            }
	            else {
	                var idx = getCategoryIndex(module.get('categoryModule'), categoryModules);
	                if (idx >= 0) {
	                    categoryModules[idx].modules.push(module);
	                }
	                else {
	                    categoryModules.push({
	                        category : module.get('categoryModule'),
	                        modules : [module]
	                    });
	                }
	            }
        	}
            
        });
        return categoryModules;
    }
    
});
