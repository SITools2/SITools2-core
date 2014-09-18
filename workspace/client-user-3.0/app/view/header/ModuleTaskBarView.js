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
    	
    	if (Project.getNavigationMode() == 'fixed') {
    		this.width = 170;
    	}
    	
        var items =  Desktop.getNavMode().createButtonsLeftTaskbar();
//        var categories = this.categorizeModules();

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
