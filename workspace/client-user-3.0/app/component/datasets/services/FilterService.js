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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl, extColModelToStorage, SitoolsDesk, sql2ext*/

Ext.namespace('sitools.user.component.dataviews.services');

/**
 * Service used to build a window to define filter on a store using Filter API.  
 * @class sitools.widget.filterTool
 * @extends Ext.Window
 */
Ext.define('sitools.user.component.datasets.services.FilterService', {
    alias : 'sitools.user.component.dataviews.services.filterService',
    extend : 'sitools.user.core.Component',
    controllers : ['sitools.user.controller.component.datasets.services.FilterServiceController'],
    
    requires : ['sitools.user.view.component.datasets.services.FilterServiceView'],
    statics : {
        getParameters : function () {
            return [];
        }
    },
    
    init : function (config) {
        var filterTool = Ext.create("sitools.user.view.component.datasets.services.FilterServiceView", config);
        filterTool.show();
    }
    
});
