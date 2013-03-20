/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, i18n, extColModelToStorage, projectId, userStorage, window,   
GeoExt, userLogin, alertFailure, DEFAULT_LIVEGRID_BUFFER_SIZE, projectGlobal, SitoolsDesk, DEFAULT_ORDER_FOLDER, DEFAULT_PREFERENCES_FOLDER, getColumnModel */

Ext.namespace('sitools.user.component.dataviews.cartoView');

/**
 */
sitools.user.component.dataviews.cartoView.featureSelectionModel = function (config) {
    sitools.user.component.dataviews.cartoView.featureSelectionModel.superclass.constructor.call(this, config);
    this.addEvents('gridFeatureSelected');
    
};

Ext.extend(sitools.user.component.dataviews.cartoView.featureSelectionModel, GeoExt.grid.FeatureSelectionModel, {
	handleMouseDown : function (g, rowIndex, e) {
		sitools.user.component.dataviews.cartoView.featureSelectionModel.superclass.handleMouseDown.call(this, g, rowIndex, e);
		this.fireEvent("gridFeatureSelected", g, rowIndex, e);
	}
});
