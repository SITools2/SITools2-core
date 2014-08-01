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
 * Abstract Module class
 * @class sitools.user.controller.modules.Module
 * @extends Ext.Panel
 */
Ext.define('sitools.user.core.Component', {
    mixins: {
        observable: 'Ext.util.Observable'
    },
    
    config : {
        componentView : null,
        project : Ext.getStore("ProjectStore").getProject(),
        controllers : [],
        application : null
    },
    
    /**
     * Show the following view with the following windowSettings
     */
    show : function (view, windowSettings) {
        var navMode = this.getApplication().getController('core.NavigationModeFactory').getNavigationMode(this.getProject().get("navigationMode"));
        navMode.openComponent(view, windowSettings);
    },
    
    /**
     * Initialize the module
     * 
     * @param application
     *            the application
     * @moduleModel moduleModel
     */
    create : function (application) {
        this.setApplication(application);
        // initialize all controllers
        if (!Ext.isEmpty(this.getControllers())) {
            Ext.each(this.getControllers(), function (controller) {
                this.getApplication().getController(controller).onLaunch();
            }, this);
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