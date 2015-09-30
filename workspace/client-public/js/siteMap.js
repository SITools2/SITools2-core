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
/* global Ext, ann */
Ext.namespace('sitools.siteMap');

var loadUrl = {

    map : [],
    /**
     * Load a properties file and and the name/values in a associative array ;
     * Executing this function on multiple properties file increase the size of
     * the array Results can be displayed in the help panel with the display()
     * function
     * 
     * @param url
     *            URL of the i18n file
     * @param callback
     *            No args function that will be executed
     * @returns void
     */
    load : function (url, callback, scope) {

        var siteMapRef = this;
        siteMapRef.transformsPropertiesToMap(url, callback, scope);

    },
    /**
     * Transforms a xml Text to a map
     * 
     * @param text
     *            raw properties file
     * @returns a map (associative array) TODO check when the raw properties
     *          file is rotten
     */
    transformsPropertiesToMap : function (url, callback, scope) {

        var store = new Ext.data.Store({
            proxy : new Ext.data.HttpProxy({
                url : url,
                restful : true
            }),
            reader : new Ext.data.XmlReader({
                record : 'url'
            }, [ {
                name : 'name',
                mapping : 'name'
            }, {
                name : 'loc',
                mapping : 'loc'
            } ])
        });
        var localMap = this.map;

        store.load({
            scope : scope,
            callback : function (r, options, success) {
                var i = 0;
                while (i != undefined) {
                    var rec = r[i];
                    if (rec != undefined) {
                        var url = rec.data.loc;
                        var name = rec.data.name;
                        localMap[name] = url;
                        i++;
                    } else {
                        i = undefined;
                    }
                }
                callback.call(this);
            }
        });
    },
    /**
     * return the url value
     * 
     * @param name
     * @returns
     */
    get : function (entry) {
        return !Ext.isEmpty(this.map[entry]) ? this.map[entry] : entry;
    }
};

/**
 * To be defined
 */
var componentManager = {

    loadedComponents : [],

    load : function (name) {

    }

};

var data = {
    ret : null,
    /**
     * Fetch a html file in the url, and display its content into the helpPanel. *
     * 
     * @param url
     * @returns
     */
    get : function (url, cbk) {
        Ext.Ajax.request({
            method : 'GET',
            url : url,
            success : function (response, opts) {
                cbk(Ext.decode(response.responseText));
            },
            failure : function (response, opts) {
                Ext.Msg.alert("Warning", "Error! Can't get data with url :" + url);
            }
        });
        return this.ret;
    }

};

Ext.applyIf(Array.prototype, {
    clone : function () {
        return [].concat(this);
    }
});
