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
            cls : 'sitools_button',
            iconCls : 'sitools_button_img',
            listeners : {
				afterrender : function (btn) {
					var label = i18n.get('label.mainMenu');
					var tooltipCfg = {
							html : label,
							target : btn.getEl(),
							anchor : 'bottom',
							anchorOffset : -5,
							showDelay : 20,
							hideDelay : 50,
							dismissDelay : 0
					};
					Ext.create('Ext.tip.ToolTip', tooltipCfg);
				}
			}
        });
        items.push(homeButton);
        
        var cleanDesktopButton = Ext.create('Ext.Button', {
            action : "minimize",
            id : 'btn-cleanDesktop',
            iconCls : 'delete_button_img',
            cls : 'sitools_button',
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
							anchorOffset : -10,
							showDelay : 20,
							hideDelay : 50,
							dismissDelay : 0
					};
					Ext.create('Ext.tip.ToolTip', tooltipCfg);
				}
			}
        });
        items.push(cleanDesktopButton);
        
        var showDesktopButton = Ext.create('Ext.Button', {
            action : "minimize",
            id : 'btn-showDesk',
            iconCls : 'desktop_button_img',
            cls : 'sitools_button',
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
							anchorOffset : -10,
							showDelay : 20,
							hideDelay : 50,
							dismissDelay : 0
					};
					Ext.create('Ext.tip.ToolTip', tooltipCfg);
				}
			}
        });
        items.push(showDesktopButton);
        
        
//        items.push('|');

//        Ext.each(categories, function (category) {
//            var modules = category.modules;
//
//            // Le module n'appartient pas à une catégorie: inclusion en tant que
//            // bouton dans le menu.
//            if (Ext.isEmpty(category.category)) {
////                var module = modules[0].getData();
//                var module = modules[0];
////                var xtype = module.xtype;
//                if (Ext.isEmpty(module.data.divIdToDisplay)) {
//                    var item = {
//                        text : i18n.get(module.data.label),
//                        iconCls : module.data.icon,
//                        scope : module,
//                        module : module,
//                        tooltip : {
//                            text : i18n.get(module.data.description),
//                            anchor : 'bottom',
//                            trackMouse : false
//                        },
//                        cls : "x-navBar-items",
//                        clickEvent : 'mousedown',
//                        template : new Ext.Template('<table cellspacing="0" class="x-btn {3}"><tbody><tr>',
//                                '<td class="ux-taskbutton-left"><i>&#160;</i></td>', '<td class="ux-taskbutton-center"><em class="{5} unselectable="on">',
//                                '<button class="x-btn-text {2}" type="{1}" style="height:28px;">{0}</button>', '</em></td>',
//                                '<td class="ux-taskbutton-right"><i>&#160;</i></td>', "</tr></tbody></table>")
//
//                    };
//                    var xtype = module.data.xtype;
////                        var func = xtype + ".openModule";
////                      if (Ext.isFunction(eval(func))) {
////                      handler = eval(func);
////                  } else {
////                      handler = moduleInCategory.openModule
////                  }
//                    items.push(Ext.create("Ext.button.Button", item));
//                }
//            }
//            // Le module est dans une catégorie : On crée un menu contenant tous
//            // les modules de la catégorie
//            else {
//                var menuItems = [];
//                Ext.each(category.modules, function (moduleInCategory) {
//                    moduleInCategory = moduleInCategory.getData();
//                        if (Ext.isEmpty(moduleInCategory)) {
//                            return;
//                        }
//
//                        if (Ext.isEmpty(moduleInCategory.divIdToDisplay)) {
//                            var item = {
//                                text : i18n.get(moduleInCategory.label),
//
//                                iconCls : moduleInCategory.icon,
//                                scope : this
//                            };
//
//                            // Test spécifique pour savoir si on doit inclure un
//                            // sous menu :
//                            var xtype = moduleInCategory.xtype;
//                            if (Ext.isEmpty(xtype)) {
//                                return;
//                            }
////                            var Func = eval(xtype + ".getStaticParameters");
////                            if (Ext.isFunction(Func)) {
////                                var staticParameters = Func();
////                                if (staticParameters && staticParameters.showAsMenu) {
////                                    Ext.apply(item, {
////                                        menu : {
////                                            xtype : moduleInCategory.xtype,
////                                            cls : "sitools-navbar-menu"
////                                        }
////                                    });
////                                } else {
////                                    Ext.apply(item, {
////                                        handler : moduleInCategory.openModule
////                                    });
////                                }
////                            }
//
////                            func = xtype + ".openModule";
////                            if (Ext.isFunction(eval(func))) {
////                                handler = eval(func);
////                            } else {
////                                handler = moduleInCategory.openModule
////                            }
//                            menuItems.push(Ext.create("Ext.button.Button", item));
//
//                        }
//
//                });
//                if (!Ext.isEmpty(menuItems)) {
//                    var menu = Ext.create('Ext.menu.Menu', {
//                        items : menuItems,
//                        cls : "sitools-navbar-menu"
//                    });
//                    items.push({
//                        text : category.category,
//                        menu : menu,
//                        cls : "x-navBar-items",
//                        template : new Ext.Template('<table cellspacing="0" class="x-btn {3}"><tbody><tr>',
//                                '<td class="ux-taskbutton-center"><em class="{2} unselectable="on">',
//                                '<button class="x-btn-text {2}" type="{1}" style="height:28px;">{0}</button>', '</em></td>', "</tr></tbody></table>")
//
//                    });
//                }
//            }
//
//        });

        this.callParent(Ext.apply(this, {
            enableOverflow : true,
            items : items,
            cls : 'sitoolsTaskbar-bg',
            border : false
//            id : "navBarId",
            // defaults : {
            // overCls : "x-navBar-items-over",
            // ctCls : "x-navBar-items-ct"
            // },
            // cls : "x-navBar",
            // overCls : "x-navBar-over",
            // ctCls : "x-navBar-ct",
//            flex : 1,
//            listeners : {
//                scope : this,
//                afterRender : function (me) {
//                    this.observer.fireEvent("navBarRendered", me);
//                }
//            }
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
