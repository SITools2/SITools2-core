/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
    
Ext.define('sitools.user.controller.core.NavigationMode', {
    
    extend : 'Ext.app.Controller',
    
    openComponent : Ext.emptyFn,
    openModule : Ext.emptyFn,
    getDesktopSettings : Ext.emptyFn,
    createIconsLeftTaskbar : Ext.emptyFn,
    
    getFormOpenMode : function (navMode) {
        switch (navMode) {
        case "desktop":
            return this.getApplication().getController('sitools.user.controller.core.DesktopMode').getFormOpenMode();
        case "fixed":
            return this.getApplication().getController('sitools.user.controller.core.FixedMode').getFormOpenMode();
        }
    }
});