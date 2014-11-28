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

Ext.namespace('sitools.user.core.Component');
/**
 * Abstract Component class
 * @class sitools.user.core.Component
 */
Ext.define('sitools.user.core.Component', {
    extend : 'Ext.util.Observable',

    config : {
        componentView : null,
        project : null,
        controllers : [],
        application : null
    },
    
    /**
     * Show the following view with the following windowSettings
     */
    show : function (view, windowSettings) {
    	
    	if (Ext.isEmpty(windowSettings.name)) {
    		windowSettings.name = "name_" + Ext.id()
    	}
    	view.componentClazz = this.$className;
    	
        var navMode = this.getApplication().getController('core.NavigationModeFactory').getNavigationMode(this.getProject().get("navigationMode"));
        navMode.openComponent(view, windowSettings);
    },
    
    /**
     * Initialize the component
     * 
     * @param application
     *            the application
     */
    create : function (application, callback, scope) {
        this.setApplication(application);
        this.setProject(Ext.getStore("ProjectStore").getProject());
        // initialize all controllers
        if (!Ext.isEmpty(this.getControllers())) {
            Ext.each(this.getControllers(), function (controller) {
                this.getApplication().getController(controller).onLaunch();
            }, this);
        }
        if (!Ext.isEmpty(callback)) {
            Ext.callback(callback, scope);
        }
    },
    
    getFormOpenMode : function () {
        this.getApplication().getController('core.NavigationMode').getFormOpenMode(this.getProject().get("navigationMode"));
    },
    
    init : Ext.emptyFn,
    
    /**
     * method called when trying to save preference
     * @returns
     */
    _getSettings : Ext.emptyFn
});
