/***************************************
* Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
extColModelToSrv, userLogin, alertFailure, DEFAULT_LIVEGRID_BUFFER_SIZE, projectGlobal, SitoolsDesk, DEFAULT_ORDER_FOLDER, DEFAULT_PREFERENCES_FOLDER, getColumnModel */
/*
 * @include "../../../env.js"
 * @include "Ext.ux.livegrid/Ext.ux.livegrid-all-debug.js"
 * @include "../../../def.js"
 */ 
Ext.namespace('sitools.user.component.dataviews.cartoView');

/**
 * @class sitools.user.component.dataviews.cartoView.featureStore
 * @extends Ext.ux.grid.livegrid.Store
 * @cfg [] datasetCm The Dataset columnModel,
 * @cfg {string} urlRecords The url to request the API
 * @cfg {string} sitoolsAttachementForUsers the dataset Attachement
 * @cfg {} userPreference Object containing all userPreference for this dataset
 * @cfg {numeric} bufferSize the buffer Size of the store
 * @cfg [] formParams an array of all formParams to apply to the store
 * @cfg [] formMultiDsParams an array of all formParams to apply to the store
 * @cfg {} mainView the View of the grid 
 * @cfg {string} datasetId the DatasetId
 * @requires Ext.ux.grid.livegrid.JsonReader
 * @requires sql2ext
 * @return {Boolean}
 */
sitools.user.component.dataviews.cartoView.featureStore = function (config) {
	this.storeUtils = sitools.user.component.dataviews.storeUtils;
    if (Ext.isEmpty(config)) {
		return false;
    }

    /*
	 * Building the params used to request the data :
	 */
    var params;
    // sending the columnModel to the server
    if (this.userPreference) {
        colModel = extColModelToSrv(config.datasetCm);
        params = {
            colModel : Ext.util.JSON.encode(colModel)
        };
    } else {
        params = {};
    }
    

    var i = 0;
    // sending the formParams to the server
    this.formParams = {};
    if (!Ext.isEmpty(config.formParams)) {
        Ext.each(config.formParams, function (param) {
            this.formParams["p[" + i + "]"] = param;
            i += 1;
        }, this);
        Ext.apply(params, this.formParams);
    }
    // sending the formParams to the server
    i = 0;
    if (!Ext.isEmpty(config.formMultiDsParams)) {
        Ext.each(config.formMultiDsParams, function (param) {
            this.formParams["c[" + i + "]"] = param;
            i += 1;
        }, this);
        Ext.apply(params, this.formParams);
    }

    var reader = config.reader || new sitools.user.data.featureReader({
        totalProperty : "totalResults"
    }, config.fields);

    Ext.apply(config, {
        bufferSize : DEFAULT_LIVEGRID_BUFFER_SIZE,
        restful : true,
        reader : reader, 
        totalProperty : "totalResults", 
        storeUtils : sitools.user.component.dataviews.storeUtils, 
        url : config.urlRecords,
        dataUrl : config.urlRecords,
        baseParams : params
    });
    
    sitools.user.component.dataviews.cartoView.featureStore.superclass.constructor.call(this, config);
	this.load({
		params : {
			start : 300, 
			limit : DEFAULT_LIVEGRID_BUFFER_SIZE
		}
	});
};

Ext.extend(sitools.user.component.dataviews.cartoView.featureStore, GeoExt.data.FeatureStore, {
    
    paramPrefix : "filter",
    
    getFormParams : function () {
		return this.storeUtils.getFormParams(this);
	}, 
	loadRecords : function (o, options, success) {
		this.bufferRange = [ -1, -1 ];
		if (o && options && options.params) {
        	this.bufferRange = [ options.params.start,
                    Math.max(0, Math.min((options.params.start + options.params.limit) - 1, o.totalRecords - 1)) ];
		}
        sitools.user.component.dataviews.cartoView.featureStore.superclass.loadRecords.call(this, o, options, success);
	}, 
//	load : function (options) {
//		sitools.user.component.dataviews.cartoView.featureStore.superclass.load.call(this, options);
//	}, 
    /**
     * Override
     */
	singleSort: function(fieldName, dir) {
        var field = this.fields.get(fieldName);
        if (!field) {
            return false;
        }

        var name       = field.name,
            sortInfo   = this.sortInfo || null,
            sortToggle = this.sortToggle ? this.sortToggle[name] : null;

        if (!dir) {
            if (sortInfo && sortInfo.field == name) { 
                dir = (this.sortToggle[name] || 'ASC').toggle('ASC', 'DESC');
            } else {
                dir = field.sortDir;
            }
        }

        this.sortToggle[name] = dir;
        this.sortInfo = {field: name, direction: dir};
        this.hasMultiSort = false;

        if (this.remoteSort) {
        	//DA : disable this
        	this.load(this.lastOptions);
//        	if (!this.load(this.lastOptions)) {
//                if (sortToggle) {
//                    this.sortToggle[name] = sortToggle;
//                }
//                if (sortInfo) {
//                    this.sortInfo = sortInfo;
//            }
        } else {
            this.applySort();
            this.fireEvent('datachanged', this);
        }
        return true;
    },
    
    /**
     * Sort by multiple fields in the specified order.
     * 
     * @param {Array}
     *            An Array of field sort specifications, or, if ascending sort
     *            is required on all columns, an Array of field names. A field
     *            specification looks like:
     * 
     * <pre><code>
     * {
     *     ordersList : [ {
     *         field : firstname,
     *         direction : ASC
     *     }, {
     *         field : name
     *         direction : DESC
     *     } ]
     * }
     * 
     * </code>
     * 
     */
    multiSort : function (sorters, direction) {
        this.hasMultiSort = true;
        direction = direction || "ASC";

        if (this.multiSortInfo && direction == this.multiSortInfo.direction) {
            direction = direction.toggle("ASC", "DESC");
        }

        this.multiSortInfo = {
            sorters : sorters,
            direction : direction
        };

        if (this.remoteSort) {
            // this.singleSort(sorters[0].field, sorters[0].direction);
            this.load(this.lastOptions);

        } else {
            this.applySort();
            this.fireEvent('datachanged', this);
        }
    },
    getSortState : function () {
        return this.hasMultiSort ? this.multiSortInfo : this.sortInfo;
    },

    // application du tri multiple sur le store
    load : function (options) {
        options = Ext.apply({}, options);
        this.storeOptions(options);
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

        try {
            return this.execute('read', null, options);
        } catch (e) {
            this.handleException(e);
            return false;
        }
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
