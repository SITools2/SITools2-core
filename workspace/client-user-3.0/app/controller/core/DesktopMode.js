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
            name : module.get('name'),
            title : i18n.get(module.get('title')),
            width : module.get('defaultWidth'),
            height : module.get('defaultHeight'),
            icon : module.get('icon'),
            label : module.get('label'),
            x : module.get('x'),
            y : module.get('y')
        };
        
        this.getApplication().getController('DesktopController').createWindow(view, windowConfig);
    },
    
    getFormOpenMode : function () {
        return sitools.user.view.component.forms.FormsView;
    }
});