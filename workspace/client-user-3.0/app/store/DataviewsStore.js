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
/* global Ext, sitools, window */

/**
 * @class sitools.user.store.DataviewsStore
 * @config fields the list of fields for the store
 * @config urlAttach the url attachment
 * @config primaryKey the primaryKey
 */
Ext.define('sitools.user.store.DataviewsStore', {
    extend : 'Ext.data.Store',
    autoLoad : true,
    pageSize : 300,
    buffered : true,

    constructor : function (config) {
        config = Ext.apply({}, config);

        Ext.apply(config, {
            fields : config.fields,
            proxy : {
                type : 'ajax',
                url : config.urlAttach + "/records",
                reader : {
                    type : 'json',
                    id : config.primaryKey,
                    root : 'data'
                }
            },
            listeners : {
                beforeprefetch : function (store, operation) {
                    // set the nocount param to false.
                    // before load is called only when a new action (sort,
                    // filter) is
                    // applied
                    var params = operation.params || {};
                    if (!store.isInSort || store.isNewFilter) {
                        params.nocount = false;
                    } else {
                        params.nocount = true;
                    }

                    operation.params = params;
                    store.isInSort = true;
                    store.isNewFilter = false;

                    if (!Ext.isEmpty(store.filters) && Ext.isFunction(store.filters.getFilterData)) {
                        var params = store.buildQuery(store.filters.getFilterData());
                        Ext.apply(options.params, params);
                    }

                    if ((this.sortInfo || this.multiSortInfo) && this.remoteSort) {
                        var pn = this.paramNames;
                        options.params = Ext.apply({}, options.params);
                        this.isInSort = true;
                        var root = pn.sort;
                        if (this.hasMultiSort) {
                            options.params[pn.sort] = Ext.encode({
                                "ordersList" : this.multiSortInfo.sorters
                            });
                        } else {
                            options.params[pn.sort] = Ext.encode({
                                "ordersList" : [ this.sortInfo ]
                            });
                        }
                    }
                    
                    // adding optional form parameters 
                    if (!Ext.isEmpty(store.formParams)) {
                    	Ext.apply(operation.params, store.formParams);
                    }
                }
            }
        });
        this.callParent([ config ]);
    },
    
    setCustomUrl : function (url) {
        this.getProxy().url = url;
    },
    
    addParamsToUrl : function (params) {
		this.getProxy().url += params;
    }

});