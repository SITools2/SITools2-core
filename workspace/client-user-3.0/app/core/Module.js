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

Ext.namespace('sitools.user.core');
/**
 * Abstract Module class
 * @class sitools.user.core.Module
 */
Ext.define('sitools.user.core.Module', {
    mixins: {
        observable: 'Ext.util.Observable',
        plugin : 'sitools.user.core.SitoolsPlugin'
    },
    
    config : {
        moduleModel : null,
        viewCmp : null,
        application : null,
        controllers : [] 
    },
    
    /**
     * Show the given view
     * 
     * @param view
     *            the view to show
     */
    show : function (view) {
        this.setViewCmp(view);
        view.type = "module";
        var project = Ext.getStore("ProjectStore").getProject();
        var navMode = this.getApplication().getController('core.NavigationModeFactory').getNavigationMode(project.get("navigationMode"));
        
        navMode.openModule(view, this.getModuleModel());
        view.fireEvent("registermodule", this, view);
    },
    
    /**
     * Method to override to initialize the module
     */
    init : Ext.emptyFn,

    /**
     * Initialize the module
     * 
     * @param application
     *            the application
     * @moduleModel moduleModel
     */
    create : function (application, moduleModel, callback, scope) {
        this.setModuleModel(moduleModel);
        this.setApplication(application);
        // initialize all controllers
        if (!Ext.isEmpty(this.getControllers())) {
            Ext.each(this.getControllers(), function (controller) {
                this.getApplication().getController(controller);
            }, this);
        }
        
        //load javascripts, this css then internationalization
        this.loadJs(callback, scope);
    },
    
    createViewForDiv : Ext.emptyFn,
    
    /**
     * method called when trying to save preference
     * @returns
     */
    _getSettings : Ext.emptyFn,
    
    /**
     * use to open a component linked to the module
     */
    openMyComponent : null,
    
    /**
     * use to delegate opening to the module itself
     */
    openMe : null


});
