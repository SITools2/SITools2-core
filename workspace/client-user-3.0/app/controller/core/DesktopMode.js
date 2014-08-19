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
/*global Ext, i18n, loadUrl, getDesktop, sitools, SitoolsDesk */
Ext.namespace('sitools.user.controller.core');

Ext.define('sitools.user.controller.core.DesktopMode', {

    extend : 'sitools.user.controller.core.NavigationMode',
    
    openComponent : function (view, windowConfig) {
        this.getApplication().getController('DesktopController').createWindow(view, windowConfig);
    },
    
    openModule : function (view, module) {
        var windowConfig = {
    		id: module.get('id'),
            name : module.get('name'),
            title : i18n.get(module.get('title')),
            width : module.get('defaultWidth'),
            height : module.get('defaultHeight'),
            iconCls : module.get('icon'),
            label : module.get('label'),
            x : module.get('x'),
            y : module.get('y')
        };
        
        this.getApplication().getController('DesktopController').createWindow(view, windowConfig);
    },
    
    getFormOpenMode : function () {
        return sitools.user.view.component.forms.FormsView;
    },
    
    getDesktopSettings : function (forPublicUser) {
        var desktopSettings = [];
    	Ext.WindowManager.each(function (window) {
            var componentSettings;
            if (!Ext.isEmpty(window.specificType) && (window.specificType === 'componentWindow' || window.specificType === 'moduleWindow')) {
                // Bug 3358501 : add a test on Window.saveSettings.
                if (Ext.isFunction(window.saveSettings)) {
//                    var component = window.get(0);
                    var component = window.items.items[0];

                    componentSettings = component._getSettings();
                    componentSettings.preferencesFileName = this.name;
                    desktopSettings.push(window.saveSettings(componentSettings, forPublicUser));
                }
            }
        });
        return desktopSettings;
    }
});