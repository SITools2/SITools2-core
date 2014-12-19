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
 *
 * Cartoview store used for cartoView
 *
 * @class sitools.user.store.dataviews.map.ProtocolProxy
 */
Ext.define('sitools.user.store.dataviews.map.ProtocolProxy', {
    extend: 'GeoExt.data.proxy.Protocol',

    requires : [
        'sitools.user.store.dataviews.map.FormatGeoJson',
        'sitools.user.store.dataviews.map.ProtocolHttp',
        'sitools.user.store.dataviews.map.SitoolsFeatureReader'],

    constructor : function (config) {
        Ext.apply(this, {
            api: {
                create: Ext.emptyFn,
                destroy: Ext.emptyFn,
                read: Ext.emptyFn,
                update: Ext.emptyFn
            },
            protocol: new sitools.user.store.dataviews.map.ProtocolHttp({
                url: config.url,
                format : new sitools.user.store.dataviews.map.FormatGeoJson({
                    totalProperty: config.totalProperty
                })
            }),
            reader: {
                type: 'sitoolsfeaturereader',
                idProperty: 'identifier',
                totalProperty: config.totalProperty,
                root : 'features'
            },
            encodeSorters: function (sorters) {
                var min = [],
                    length = sorters.length,
                    i = 0;

                for (; i < length; i++) {
                    min[i] = {
                        field : sorters[i].property,
                        direction: sorters[i].direction
                    };
                }
                return this.applyEncoding(min);
            },
            listeners : {
                scope : this,
                exception : function(proxy, response, operation) {
                    proxy.exceptionResponse = response.priv;
                }
            }
        });

        this.callParent([config]);
    },

    /**
     * Handle response from the protocol.
     *
     * @param {Object} o
     * @param {OpenLayers.Protocol.Response} response
     * @private
     */
    loadResponse: function(o, response) {
        var me = this;
        var operation = o.operation;
        var scope = o.request.scope;
        var callback = o.request.callback;
        if (response.success()) {
            var result = o.reader.read(response);
            Ext.apply(operation, {
                response: response,
                resultSet: result
            });

            operation.commitRecords(result.records);
            operation.setCompleted();
            operation.setSuccessful();
        } else {
            me.setException(operation, response);
            me.fireEvent('exception', this, response, operation);
        }
        if (typeof callback == 'function') {
            callback.call(scope || me, operation);
        }
    }
});