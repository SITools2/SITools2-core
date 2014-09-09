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
 * Specific JsonReader to handle totalProperty reading.
 * Total property is saved after the first page is read.
 * Then we use that totalProperty until another first is read. 
 * 
 * @private
 */
Ext.define('sitools.user.store.DataviewsJsonReader', {
	
	extend : 'Ext.data.reader.Json',
	alias : 'reader.dataviewsreader',
	
	constructor : function () {
    	this.callParent(arguments);
    	this.extractTotal = this.getTotal;
        
        this.getTotal = function (data) {
        	var total = this.extractTotal(data);
        	if(Ext.isEmpty(total)) {
        		return this.total;
        	}
        	this.total = total;
        	return total;
        }
    }
});

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
    
    paramPrefix : "filter",

    constructor : function (config) {
        config = Ext.apply({}, config);

        Ext.apply(config, {
            fields : config.fields,
            proxy : {
                type : 'ajax',
                url : config.urlAttach + "/records",
                reader : {
                    type : 'dataviewsreader',
                    id : config.primaryKey,
                    root : 'data'
                },
                encodeSorters: function(sorters) {
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
                    
                    if (!Ext.isEmpty(store.servicefilters) && Ext.isFunction(store.servicefilters.getFilterData)) {
                        var params = this.buildQuery(store.servicefilters.getFilterData());
                        Ext.apply(operation.params, params);
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
    },
    
    buildQuery : function (filters) {
        if (Ext.isEmpty(filters)) {
            return;
        }
        var p = {}, i, f, root, dataPrefix, key, tmp,
            len = filters.length;

        for (i = 0; i < len; i++) {
            f = filters[i];
            root = [this.paramPrefix, '[', i, ']'].join('');
            p[root + '[columnAlias]'] = f.columnAlias;

            dataPrefix = root + '[data]';
            for (key in f.data) {
                p[[dataPrefix, '[', key, ']'].join('')] = f.data[key];
            }
        }
        return p;
    }
});