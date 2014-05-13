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
Ext.define('sitools.user.view.header.ModulesTaskBar', {
    extend : 'Ext.Toolbar',
    alias: 'widget.moduleTaskBar',
    
    initComponent : function () {
        var items = [];
        var categories = this.categorizeModules();

        var homeButton = Ext.create('Ext.Button', {
            handler : function () {
                projectGlobal.getPreferences(function () {
                    SitoolsDesk.removeActivePanel();
                    SitoolsDesk.removeAllWindows();
                    SitoolsDesk.loadPreferences();
                });
            },
            scale : "medium",
            icon : "/sitools/common/res/images/icons/button-home.png",
            iconCls : 'navBarButtons-icon',
            tooltip : {
                html : i18n.get("label.homeButton"),
                anchor : 'bottom',
                trackMouse : false
            },
            template : new Ext.Template('<table cellspacing="0" class="x-btn {3}" style="padding-left:5px;"><tbody><tr>', '<td><i>&#160;</i></td>',
                    '<td><em class="{5} unselectable="on">', '<button type="{1}" style="height:28px; width:28px;">{0}</button>', '</em></td>',
                    '<td><i>&#160;</i></td>', "</tr></tbody></table>")
        });
        items.push(homeButton);
        items.push('|');

        Ext.each(categories, function (category) {
            var modules = category.modules;

            // Le module n'appartient pas à une catégorie: inclusion en tant que
            // bouton dans le menu.
            if (Ext.isEmpty(category.category)) {
                var module = modules[0].getData();
//                var xtype = module.xtype;
                if (Ext.isEmpty(module.divIdToDisplay)) {
                    var item = {
                        text : i18n.get(module.label),
                        iconCls : module.icon,
                        scope : module,
                        tooltip : {
                            html : i18n.get(module.description),
                            anchor : 'bottom',
                            trackMouse : false
                        },
                        cls : "x-navBar-items",
                        clickEvent : 'mousedown',
                        template : new Ext.Template('<table cellspacing="0" class="x-btn {3}"><tbody><tr>',
                                '<td class="ux-taskbutton-left"><i>&#160;</i></td>', '<td class="ux-taskbutton-center"><em class="{5} unselectable="on">',
                                '<button class="x-btn-text {2}" type="{1}" style="height:28px;">{0}</button>', '</em></td>',
                                '<td class="ux-taskbutton-right"><i>&#160;</i></td>', "</tr></tbody></table>")

                    };
                    var xtype = module.xtype;
//                        var func = xtype + ".openModule";
//                      if (Ext.isFunction(eval(func))) {
//                      handler = eval(func);
//                  } else {
//                      handler = moduleInCategory.openModule
//                  }
                    items.push(Ext.create("Ext.button.Button", item));
                    items.push('|');
                }
            }
            // Le module est dans une catégorie : On crée un menu contenant tous
            // les modules de la catégorie
            else {
                var menuItems = [];
                Ext.each(category.modules, function (moduleInCategory) {
                    moduleInCategory = moduleInCategory.getData();
                        if (Ext.isEmpty(moduleInCategory)) {
                            return;
                        }

                        if (Ext.isEmpty(moduleInCategory.divIdToDisplay)) {
                            var item = {
                                text : i18n.get(moduleInCategory.label),

                                iconCls : moduleInCategory.icon,
                                scope : this
                            };

                            // Test spécifique pour savoir si on doit inclure un
                            // sous menu :
                            var xtype = moduleInCategory.xtype;
                            if (Ext.isEmpty(xtype)) {
                                return;
                            }
//                            var Func = eval(xtype + ".getStaticParameters");
//                            if (Ext.isFunction(Func)) {
//                                var staticParameters = Func();
//                                if (staticParameters && staticParameters.showAsMenu) {
//                                    Ext.apply(item, {
//                                        menu : {
//                                            xtype : moduleInCategory.xtype,
//                                            cls : "sitools-navbar-menu"
//                                        }
//                                    });
//                                } else {
//                                    Ext.apply(item, {
//                                        handler : moduleInCategory.openModule
//                                    });
//                                }
//                            }

//                            func = xtype + ".openModule";
//                            if (Ext.isFunction(eval(func))) {
//                                handler = eval(func);
//                            } else {
//                                handler = moduleInCategory.openModule
//                            }
                            menuItems.push(Ext.create("Ext.button.Button", item));

                        }

                });
                if (!Ext.isEmpty(menuItems)) {
                    var menu = Ext.create('Ext.menu.Menu', {
                        items : menuItems,
                        cls : "sitools-navbar-menu"
                    });
                    items.push({
                        text : category.category,
                        menu : menu,
                        cls : "x-navBar-items",
                        template : new Ext.Template('<table cellspacing="0" class="x-btn {3}"><tbody><tr>',
                                '<td class="ux-taskbutton-center"><em class="{2} unselectable="on">',
                                '<button class="x-btn-text {2}" type="{1}" style="height:28px;">{0}</button>', '</em></td>', "</tr></tbody></table>")

                    });
                    items.push('|');
                }
            }

        });

        this.callParent(Ext.apply(this, {
            id : "navBarId",
            enableOverflow : true,
            // defaults : {
            // overCls : "x-navBar-items-over",
            // ctCls : "x-navBar-items-ct"
            // },
            items : items,
            // cls : "x-navBar",
            // overCls : "x-navBar-over",
            // ctCls : "x-navBar-ct",
            flex : 1,
            listeners : {
                scope : this,
                afterRender : function (me) {
                    this.observer.fireEvent("navBarRendered", me);
                }
            },
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
