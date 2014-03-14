/***************************************
* Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
Ext.namespace('sitools.user.component.dataviews.livegrid');

// Surcharge de l'objet Store de la liveGrid pour gestion du multiSort.
Ext.override(Ext.ux.grid.livegrid.Store, {
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
    }

});

/**
 * @class sitools.user.component.dataviews.livegrid.StoreLiveGrid
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
sitools.user.component.dataviews.livegrid.StoreLiveGrid = function (config) {
	this.storeUtils = sitools.user.component.dataviews.storeUtils;
    if (Ext.isEmpty(config)) {
		return false;
    }
	/**
	 * Construction of the column Model : user preferences have priority on the
	 * initial definition of the model column in the dataset
	 */
    var colModel;
    if (!Ext.isEmpty(config.userPreference) && config.userPreference.datasetView == "Ext.ux.livegrid" && !Ext.isEmpty(config.userPreference.colModel)) {
		colModel = Ext.applyIf(config.userPreference.colModel, config.datasetCm);
    }
    else {
		colModel = config.datasetCm; 
    }
    var cm = getColumnModel(colModel);
    /**
	 * the fields of the store
	 */
    var map = this.storeUtils.getFields(config.datasetCm);
    var primaryKey = this.storeUtils.getPrimaryKey(config.datasetCm);
    /*
	 * JSON Reader : BufferedJsonReader derives from Ext.data.JsonReader and
	 * allows to pass a version value representing the current state of the
	 * underlying data repository. Version handling on server side is totally up
	 * to the user. The version property should change whenever a record gets
	 * added or deleted on the server side, so the store can be notified of
	 * changes between the previous and current request. If the store notices a
	 * version change, it will fire the version change event. Speaking of data
	 * integrity: If there are any selections pending, the user can react to
	 * this event and cancel all pending selections.
	 */
    var bufferedReaderSimple = new Ext.ux.grid.livegrid.JsonReader({
        idProperty : primaryKey,
        root : 'data',
        versionProperty : 'version',
        totalProperty : 'total'
    }, map);

    /*
	 * Building the params used to request the data :
	 */
    var params;
    // sending the columnModel to the server
    if (this.userPreference) {
        colModel = extColModelToSrv(cm);
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

    Ext.apply(config, {
        autoLoad : true,
        bufferSize : DEFAULT_LIVEGRID_BUFFER_SIZE,
        restful : true,
        reader : bufferedReaderSimple,
        storeUtils : sitools.user.component.dataviews.storeUtils, 
        url : config.urlRecords,
        dataUrl : config.sitoolsAttachementForUsers,
        baseParams : params,
        listeners : {
            scope : this,
            exception : function (dp, type, action, options, response, arg) {
                // load the alert & close the window.
                this.removeAll();

                Ext.Msg.show({
                    title : i18n.get('label.error'),
                    msg : response.responseText,
                    buttons : Ext.Msg.OK,
                    width : 400
                });
                this.fireEvent("load", this, []);
            }
        }
    });
    sitools.user.component.dataviews.livegrid.StoreLiveGrid.superclass.constructor.call(this, config);
};
Ext.extend(sitools.user.component.dataviews.livegrid.StoreLiveGrid, Ext.ux.grid.livegrid.Store, {
    
    paramPrefix : "filter",
    
    getFormParams : function () {
		return this.storeUtils.getFormParams(this);
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
