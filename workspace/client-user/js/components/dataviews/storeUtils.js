/*******************************************************************************
 * Copyright 2010-2015 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

/*global Ext, sitools, i18n, sql2ext, extColModelToSrv, window,
 extColModelToJsonColModel, DEFAULT_NEAR_LIMIT_SIZE,
 DEFAULT_LIVEGRID_BUFFER_SIZE, SITOOLS_DEFAULT_IHM_DATE_FORMAT,
 DEFAULT_PREFERENCES_FOLDER, SitoolsDesk, getDesktop, userLogin, projectGlobal, ColumnRendererEnum, SITOOLS_DATE_FORMAT
*/
Ext.namespace('sitools.user.component.dataviews');

/**
 * A Simple Object to publish common methods to use stores dataviews in Sitools2.
 * @type 
 */
sitools.user.component.dataviews.storeUtils = {
    /**
	 * @param {Array}
	 *            ColumnModel of the grid
	 * 
	 * @returns {Array} The fields used to build the grid
	 */
	getFields : function (listeColonnes) {
        var fields = [];
        var i = 0;
        if (!Ext.isEmpty(listeColonnes)) {
            Ext.each(listeColonnes, function (item, index, totalItems) {
                fields[i] = new Ext.data.Field({
                    name : item.columnAlias,
                    primaryKey : item.primaryKey,
                    type : sql2ext.get(item.sqlColumnType)
                });
                if (sql2ext.get(item.sqlColumnType) === 'boolean') {
					Ext.apply(fields[i], {
						convert : function (value, record) {
							if (value == "f" || value == "false" || value === 0) {
								return 0;
							}
							if (value == "t" || value == "true" || value == 1) {
								return 1;
							}
							return value;
						}
					});
                }
                i++;

            }, this);
        }
        return fields;
    }, 
    /**
	 * @param {Array}
	 *            ColumnModel of the grid
	 * 
	 * @returns {String} The columnAlias of the primaryKey
	 */
    getPrimaryKey : function (listeColonnes) {
        var i = 0, primaryKey = "";
        if (!Ext.isEmpty(listeColonnes)) {
            Ext.each(listeColonnes, function (item, index, totalItems) {
                if (!Ext.isEmpty(item.primaryKey)) {
                    if (item.primaryKey) {
                        primaryKey = item.columnAlias;
                    }
                }
            }, this);
        }
        return primaryKey;
    },
    getFormParams : function (store) {
        return store.formParams;
    },
    /**
     * Build a string using a form param Value. 
     * @param {} paramValue An object with attributes : at least type, code, value and optionnal userDimension, userUnit
     * @return {string} something like "TEXTFIELD|ColumnAlias|value"
     */
    paramValueToApi : function (paramValue) {
		var stringParam = paramValue.type + "|" + paramValue.code + "|" + paramValue.value;
        if (!Ext.isEmpty(paramValue.userDimension) && !Ext.isEmpty(paramValue.userUnit)) {
			stringParam += "|" + paramValue.userDimension + "|" + paramValue.userUnit.unitName; 
        }  
        return stringParam;
    }

};