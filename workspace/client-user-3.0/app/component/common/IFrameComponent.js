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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl, sql2ext, sitoolsUtils*/

Ext.namespace('sitools.user.component.common');

/**
 * IFrame Component to open an iframe in the desktop context
 * 
 * @class sitools.user.component.common.IFrameComponent
 * @extend sitools.user.core.Component
 */
Ext.define('sitools.user.component.common.IFrameComponent', {
    extend : 'sitools.user.core.Component',
    
    init : function (componentConfig, windowConfig) {
        
    	var windowBaseConfig = {
            title : i18n.get('label.iframe'),
            name : 'iframeComponent', /* REQUIRE */
            iconCls : 'iframeIcon',
            width : 800,
            height : 450
        };
    	Ext.applyIf(windowConfig, windowBaseConfig);
        
        var view = Ext.create('Ext.ux.IFrame', componentConfig);

        this.setComponentView(view);
        this.show(view, windowConfig);
    }

});