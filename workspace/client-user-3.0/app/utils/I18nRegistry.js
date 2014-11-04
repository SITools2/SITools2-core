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
/*global Ext*/
Ext.namespace('sitools.user.utils');

/**
 * Registry of i18n object to deal with multiple internationalization files
 */
Ext.define('sitools.user.utils.I18nRegistry', {
    singleton : true,
    map : Ext.create("Ext.util.MixedCollection"),
    
    /**
     * register a new i18n instance with given name and from the given url
     * 
     * @param name the name of the I18n instance
     * @param url URL of the i18n file
     * @param callback No args function that will be executed
     * @returns false if an instance with the given name already exists, true otherwise
     * 
     * @see sitools.public.utils.i18n
     */
    register : function (name, url, callback, scope) {
    	if(Ext.isEmpty(this.retrieve(name))) {
	    	var i18nLocal = Ext.create("sitools.public.utils.i18n");
	    	this.map.add(name, i18nLocal);
	    	i18nLocal.load(url, callback, scope);
	    	return true;
    	} else {
    		return false;
    	}
    },

    retrieve : function (name) {
        return this.map.get(name);
    }
    
    remove : function (name) {
    	return this.map.remove(name);
    }
});

I18nRegistry = sitools.user.utils.I18nRegistry;