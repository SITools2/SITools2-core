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

Ext.define('sitools.public.utils.i18n', {
    singleton : true,
    map : [],
    /**
     * Load a properties file and and the name/values in a associative array ;
     * Executing this function on multiple properties file increase the size of
     * the array Results can be displayed in the help panel with the display()
     * function
     * 
     * @param url URL of the i18n file
     * @param callback No args function that will be executed
     * @returns void
     */
    load : function (url, callback) {
    
        var i18nRef = this;
        Ext.Ajax.request({
            method : 'GET',
            url : url,
            // params:'formLogin', using autorization instead
            success : function (response, opts) {
                console.ann(response.responseText, "no response is sent");
                i18nRef.map = i18nRef.transformsPropertiesToMap(response.responseText);
                callback();
            },
            failure : function (response, opts) {
                Ext.Msg.alert("Error! Can't read i18n file with url :" + url);
            }
        });

    },
    /**
     * Transforms a properties Text to a map
     * 
     * @param text
     *            raw properties file
     * @returns a map (associative array) TODO check when the raw properties
     *          file is rotten
     */
    transformsPropertiesToMap : function (text) {
        var array = text.split('\n');
        var localMap = [];
        for (var i = 0; i < array.length; i++) {
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
     * @param name
     * @returns
     */
    get : function (entry) {
        return !Ext.isEmpty(this.map[entry])?this.map[entry]:entry;
    }
});

i18n = sitools.public.utils.i18n;

///**
// * To be defined
// */
//var componentManager = {
//	
//		loadedComponents : [],
//		
//		load  : function (name) {
//			
//		}
//		
//	};
//
//var data = {
//		ret : null,
//		/**
//		 * Fetch a html file in the url, and display its content into the helpPanel. *
//		 * 
//		 * @param url
//		 * @returns
//		 */
//		get : function (url, cbk) {
//			Ext.Ajax.request({
//				method : 'GET',
//				url : url,
//				success : function (response, opts) {
//					cbk(Ext.decode(response.responseText));
//				},
//				failure : function (response, opts) {
//					Ext.Msg.alert("Warning", "Error! Can't get data with url :" + url);
//				}
//			});
//			return this.ret;
//		}
//
//	};
//    
//    
//Ext.applyIf(Array.prototype, {
//	clone : function() {
//		return [].concat(this);
//	}
//});
