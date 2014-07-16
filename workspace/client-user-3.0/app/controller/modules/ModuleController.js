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

/*global Ext, sitools, i18n, projectGlobal, alertFailure, showResponse*/

Ext.namespace('sitools.user.controller.modules');
/**
 * Abstract Module class
 * @class sitools.user.controller.modules.Module
 * @extends Ext.Panel
 */
Ext.define('sitools.user.controller.modules.ModuleController', {
    extend : 'Ext.app.Controller',
    
    config : {
        moduleModel : null,
        viewCmp : null
    },
    
    open : function (view) {
        var project = Ext.getStore("ProjectStore").getProject();
        var navMode = this.getApplication().getController('core.NavigationModeFactory').getNavigationMode(project.get("navigationMode"));
        navMode.openModule(view, this.getModuleModel());        
    },
    
    initModule : function (moduleModel) {
        this.setModuleModel(moduleModel);
    },
    
    /**
     * method called when trying to save preference
     * @returns
     */
    _getSettings : Ext.emptyFn
});
