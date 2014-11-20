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

/**
 * Abstract PluginComponent class
 * @class sitools.user.core.PluginComponent
 * @extends sitools.user.core.Component
 */
Ext.define('sitools.user.core.PluginComponent', {
    mixins: {
        observable: 'Ext.util.Observable',
        plugin : 'sitools.user.core.SitoolsPlugin'
    },
    extend : 'sitools.user.core.Component',
    
    /**
     * Initialize the module
     * 
     * @param application
     *            the application
     * @moduleModel moduleModel
     */
    create : function (application, callback, scope) {
        this.callParent([application]);
        //load javascripts, this css then internationalization
        this.loadJs(callback, scope);
    }
});
