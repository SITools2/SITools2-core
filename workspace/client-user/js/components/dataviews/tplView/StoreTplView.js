/***************************************
* Copyright 2010-2015 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
* 
* This file is part of SITools2.
* 
* SITools2 is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* SITools2 is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
***************************************/
/*global Ext, sitools, i18n, userLogin, DEFAULT_ORDER_FOLDER, document, alertFailure, SITOOLS_DATE_FORMAT, SITOOLS_DEFAULT_IHM_DATE_FORMAT, sql2ext, 
getDesktop, getColumnModel, extColModelToStorage, SitoolsDesk, projectGlobal, DEFAULT_PREFERENCES_FOLDER, DEFAULT_LIVEGRID_BUFFER_SIZE, loadUrl, ColumnRendererEnum*/
/*
 * @include "contextMenu.js"
 * @include "../../viewDataDetail/viewDataDetail.js"
 * @include "../../plot/dataPlotter.js"
 * @include "contextMenu.js"
 * @include "storeLiveGrid.js"
 */
Ext.namespace('sitools.user.component.dataviews.tplView');

/**
 * Define the store for the dataview. 
 * Redefine the getAt function.
 * @class sitools.user.component.dataviews.tplView.StoreTplView
 * @extends sitools.user.component.dataviews.livegrid.StoreLiveGrid
 * @requires sitools.user.component.columnsDefinition
 */
sitools.user.component.dataviews.tplView.StoreTplView = Ext.extend(sitools.user.component.dataviews.livegrid.StoreLiveGrid, {
//sitools.user.component.dataViewStore = Ext.extend(sitools.user.component.dataviews.livegrid.StoreLiveGrid, {
	
    getAt : function (index) {
		return this.data.itemAt(index);
    } 
    
});
