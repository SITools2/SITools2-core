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
/*global Ext, ann*/
Ext.namespace('sitools.public.utils');

Ext.define('sitools.public.utils.sql2ext', {
    singleton : true,
    map : [],
    load : function (url, callback, scope) {

        var i18nRef = this;
        Ext.Ajax.request({
            method : 'GET',
            url : url,
            success : function (response, opts) {
                i18nRef.map = i18nRef.transformsPropertiesToMap(response.responseText);
                Ext.callback(callback, scope);
            },
            failure : function (response, opts) {
                alert("Error! Can't read i18n file with url :" + url);
            }
        });

    },
    /**
     * Transforms a properties Text to a map
     * 
     * @param text
     *            raw properties file
     * @returns a map (associative array) TODO check when the raw properties
     */
    transformsPropertiesToMap : function (text) {
        var array = text.split('\n');
        var localMap = [];
        var i;
        for (i = 0; i < array.length; i++) {
            var string = array[i];
            var indexOfEqualsSign = string.indexOf('=');
            if (indexOfEqualsSign >= 1) {
                var key = string.substring(0, indexOfEqualsSign).replace('\r', '');
                var value = string.substring(indexOfEqualsSign + 1).replace('\r', '');
                localMap[key] = value;
            }
        }
        return localMap;
    },
    /**
     * return the i18n value
     * 
     * @param name
     * @returns
     */
    get : function (entry) {
        return !Ext.isEmpty(this.map[entry]) ? this.map[entry] : 'auto';
    }
});

sql2ext = sitools.public.utils.sql2ext;