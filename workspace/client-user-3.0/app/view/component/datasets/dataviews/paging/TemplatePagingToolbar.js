/***************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl*/

Ext.namespace('sitools.user.view.component.datasets.dataviews.paging');

Ext.define('sitools.user.view.component.datasets.dataviews.paging.TemplatePagingToolbar', {
    extend: 'Ext.toolbar.Paging',

    alias: 'widget.templatePagingToolbar',

    //onLoad: function (start) {
    //    var lastOptions = this.store.lastOptions || {};
    //    var o = lastOptions.params, pn = this.store.getFormParams();
    //    o[pn.start] = start;
    //    o[pn.limit] = this.pageSize;
    //    if (this.fireEvent('beforechange', this, o) !== false) {
    //        this.store.load({params: o});
    //    }
    //    //Fire Event on plot window if opened !
    //    var plotComp = Ext.getCmp("plot" + this.ownerCt.datasetId);
    //    if (plotComp) {
    //        var rightPanel = plotComp.findById('plot-right-panel');
    //        var success = rightPanel.fireEvent('buffer', this.store);
    //    }
    //}
});