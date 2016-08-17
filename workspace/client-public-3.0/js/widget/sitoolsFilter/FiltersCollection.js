/*******************************************************************************
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
 ******************************************************************************/
/*global Ext, i18n, loadUrl, getDesktop, sitools, SitoolsDesk */
Ext.namespace("sitools.public.widget.sitoolsFilter");

Ext.define('sitools.public.widget.sitoolsFilter.FiltersCollection', {
    extend : 'Ext.util.MixedCollection',
    
    constructor : function (config) {
        this.callParent([config]);
        if (!Ext.isEmpty(config)) {
            this.addAll(config.filters);
        }
    },

    
    getFilterData : function(){
        var filters = [];
        this.each(function (filter, index, length) {
            filters.push(filter);
        });
        return filters;
    }
    
});