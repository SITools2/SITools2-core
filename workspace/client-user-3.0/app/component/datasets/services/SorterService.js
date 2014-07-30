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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl, extColModelToStorage, SitoolsDesk, sql2ext
 */

Ext.namespace('sitools.user.component.datasets.services');

/**
 * Window that contains a tools to sort a store
 * 
 * @cfg {} dataview The view the tool is open in
 * @cfg {} pos The position to apply to the window
 * @cfg {Ext.data.Store} store the store to sort
 * @cfg {} columnModel the dataset ColumnModel to know all columns on wich you
 *      can do a sort.
 * @class sitools.user.component.datasets.services.SorterService
 * @extends Ext.Window
 */
Ext.define('sitools.user.component.datasets.services.SorterService', {
    extend : 'sitools.user.core.Component',
    controllers : ['sitools.user.controller.component.datasets.services.SorterServiceController'],
    alias : 'sitools.user.component.dataviews.services.sorterService',
    
    executeAsService : function (config) {
        var sorterTool = Ext.create("sitools.user.view.component.datasets.services.SorterServiceView", config);
        sorterTool.show();
    }
});

sitools.user.component.datasets.services.SorterService.getParameters = function () {
    return [];
};
