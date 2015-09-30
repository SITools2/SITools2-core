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

Ext.namespace('sitools.user.component.dataviews.tplView');

/**
 * Defines a toolbar for the specific view {sitools.user.component.dataviews.tplView.TplView}
 * Redefine method doLoad
 * @class sitools.user.component.dataviews.tplView.dataViewPagingToolbar
 * @extends Ext.PagingToolbar
 */
sitools.user.component.dataviews.tplView.dataViewPagingToolbar = Ext.extend(Ext.PagingToolbar, {
//sitools.user.component.dataViewPagingToolbar = Ext.extend(Ext.PagingToolbar, {
	doLoad : function (start) {
        var lastOptions = this.store.lastOptions || {};
        var o = lastOptions.params, pn = this.getParams();
        o[pn.start] = start;
        o[pn.limit] = this.pageSize;
        if (this.fireEvent('beforechange', this, o) !== false) {
            this.store.load({params : o});
        }
        //Fire Event on plot window if opened ! 
        var plotComp = Ext.getCmp("plot" + this.ownerCt.datasetId);
        if (plotComp) {
            var rightPanel = plotComp.findById('plot-right-panel');
            var success = rightPanel.fireEvent('buffer', this.store);
        }
        
	}
});
