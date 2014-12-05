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
/*global Ext, sitools, window */

/**
 * @class sitools.user.store.dataviews.DataviewStore
 * @config fields the list of fields for the store
 * @config urlAttach the url attachment
 * @config primaryKey the primaryKey
 */
Ext.define('sitools.user.store.dataviews.DataviewStore', {
    extend : 'Ext.data.Store',

    mixins : {
        "dataviewStore" : "sitools.user.store.dataviews.AbstractDataviewStore"
    },

    constructor : function (config) {
        config = Ext.apply({}, config);
        Ext.apply(this, config);
        var me = this;

        this.mixins.dataviewStore.enrichDataviewStoreConfig.call(this, config);
        this.callParent([config]);

        //remove the event on beforeLoad because it is not called with buffered store
        me.on("beforeLoad", this.onBeforeLoad, this);
    }
});