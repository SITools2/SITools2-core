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
Ext.define('sitools.user.controller.core.FixedMode', {

    extend : 'sitools.user.controller.core.NavigationMode',
    
    openComponent : function (view, panelConfig) {
        this.getApplication().getController('DesktopController').createPanel(view, panelConfig);
    },
    
    openModule : function (view, module) {
        var panelConfig = {
    		id: module.get('id'),
            name : module.get('name'),
            title : i18n.get(module.get('title')),
            iconCls : module.get('icon'),
            label : module.get('label')
        };
        this.getApplication().getController('DesktopController').createPanel(view, panelConfig);
    },
    
    getFormOpenMode : function () {
        return sitools.user.component.DatasetOverview;
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