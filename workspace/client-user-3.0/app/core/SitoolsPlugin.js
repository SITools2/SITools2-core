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
 * Abstract class to collection methods to instantiate a plugin in sitools 
 * @class sitools.user.core.PluginComponent
 */
Ext.define('sitools.user.core.SitoolsPlugin', {
    
    config : {
        pluginName : null,
        js : [],
        css : [],
        i18nFolderPath : null,
        // origin of the plugin (core or extension)
        origin : null
    },
    
    loadJs : function (callback, scope) {
        if (!Ext.isEmpty(this.getJs()) && this.getJs().length > 0) {
            var urls = [];
            Ext.each(this.getJs(), function (url) {
                urls.push(url);
            }, this);

            ScriptLoader.loadScripts(urls, function () {
                this.loadResources(callback, scope);
            }, function () {
                alert("Cannot load all js dependencies");
            }, this);
        } else {
            this.loadResources(callback, scope);
        }
    },
    

    loadResources : function (callback, scope) {
        if (!Ext.isEmpty(this.getCss()) && this.getCss().length > 0) {
            var urls = [];
            Ext.each(this.getCss(), function (url) {
                urls.push(url);
            }, this);

            Ext.each(urls, function (css) {
                includeCss(css);
            });

        }

        var registered = false;
        if(!Ext.isEmpty(this.getPluginName()) && !Ext.isEmpty(this.getI18nFolderPath())) {
            var guiUrl = this.getI18nFolderPath() + locale.getLocale() + '/gui.properties';
            
            registered = I18nRegistry.register(this.getPluginName(), guiUrl, function () {
                Ext.callback(callback, scope);
            }, function () {
                Ext.callback(callback, scope);
            }, this);
        }

        if (!registered) {
            Ext.callback(callback, scope);
        }
    }
});
