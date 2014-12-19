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
            if (Ext.isEmpty(total)) {
                return this.total;
            }
            this.total = total;
            return total;
        };
    }
});

/**
 *
 * Abstract DataviewStore
 *
 * @class sitools.user.store.dataviews.AbstractDataviewsStore
 * @config fields the list of fields for the store
 * @config urlAttach the url attachment
 * @config primaryKey the primaryKey
 */
Ext.define('sitools.user.store.dataviews.AbstractDataviewStore', {
    autoLoad : true,
    pageSize : 300,
    remoteSort : true,

    config : {
        // Object definition of the filters
        formFilters : null,
        // Object of string parameters corresponding to the filters definition
        formParams : null,
        // list of value object filters added by the filter service
        gridFilters : null,
        // list of config object filters added by the filter service
        gridFiltersCfg : null,
        // Object definition of the concept filters (used by formProject (multidataset forms))
        formConceptFilters : null,
        // Object of string parameters corresponding to the concept filters definition
        formConceptParams : null,
        // Object definition of the ranges to query (prior to a selection, used for plot for instance)
        ranges : null,
        // Object definition of the sort
        sortInfo : null
    },
    
    paramPrefix : "filter",

    enrichDataviewStoreConfig : function (config) {
        if (!Ext.isEmpty(this.getFormFilters())) {
            this.setFormParams(this.buildFormParamsUrl(this.getFormFilters()));
        }

        if (!Ext.isEmpty(this.getFormConceptFilters())) {
            this.setFormConceptParams(this.buildFormParamsUrl(this.getFormConceptFilters(), "c"));
        }

        if(!Ext.isEmpty(this.getSortInfo())) {
            this.sorters = this.convertSorters(this.getSortInfo());
        }

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
                        proxy.exceptionResponse = response;
                    }
                }
            }
        });
    },

    convertSorters : function (sorters) {
        var min = [],
            length = sorters.length,
            i = 0;

        for (; i < length; i++) {
            min[i] = {
                property : sorters[i].field,
                direction: sorters[i].direction
            };
        }
        return min;
    },

    onBeforeLoad : function (store, operation) {
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

        this.appendOperationParam(operation,store);
    },

    appendOperationParam : function (operation, store) {
        // adding optional form parameters
        if (!Ext.isEmpty(store.getFormParams())) {
            Ext.apply(operation.params, store.getFormParams());
        }

        // adding optional form parameters
        if (!Ext.isEmpty(store.getFormConceptParams())) {
            Ext.apply(operation.params, store.getFormConceptParams());
        }

        if (!Ext.isEmpty(store.gridFilters)) {
            var params = this.buildQuery(store.gridFilters);
            Ext.apply(operation.params, params);
        }

        if (!Ext.isEmpty(store.getRanges())) {
            var ranges = Ext.JSON.encode(this.getRanges());
            Ext.apply(operation.params, {
                ranges : ranges
            });
        }
    },


    setCustomUrl : function (url) {
        this.getProxy().url = url;
    },
    
    addParamsToUrl : function (params) {
		this.getProxy().url += params;
    },
    
    buildQuery : function (filters, filterType) {
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
    },

    /**
     * Build a string using a form param Value. 
     * @param {} paramValue An object with attributes : at least type, code, value and optionnal userDimension, userUnit
     * @return {string} something like "TEXTFIELD|ColumnAlias|value"
     */
    paramValueToApi : function (paramValue) {
        var code ="";
        if(!Ext.isEmpty(paramValue.dictionary)) {
            code += paramValue.dictionary + ",";
        }
        code += paramValue.code;

        var stringParam = paramValue.type + "|" + code + "|" + paramValue.value;
        if (!Ext.isEmpty(paramValue.userDimension) && !Ext.isEmpty(paramValue.userUnit)) {
            stringParam += "|" + paramValue.userDimension + "|" + paramValue.userUnit.unitName; 
        }  
        return stringParam;
    },
    
    /**
     * Build a string path from the given formFilters
     * @param formFilters Array List of form filter or form concept filters
     * @param paramType String type of parameter, p for form filter, c for form concept filters (default to p)
     */
    buildFormParamsUrl : function (formFilters, paramType) {
        var type = (Ext.isEmpty(paramType))?"p":paramType;
        var formParams = {};
        Ext.each(formFilters, function (filter, index, arrayParams) {
            formParams[type + "[" + index + "]"] = this.paramValueToApi(filter);
        }, this);
        return formParams;
    }
});