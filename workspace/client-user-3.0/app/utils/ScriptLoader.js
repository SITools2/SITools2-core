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
/* global Ext */
Ext.namespace('sitools.user.utils');

/**
 * Registry of i18n object to deal with multiple internationalization files
 */
Ext.define('sitools.user.utils.ScriptLoader', {
    singleton : true,
    scriptLoaded : Ext.create("Ext.util.MixedCollection"),
    
    
    loadScript : function (url, callback, onError, scope) {
    	if(Ext.isEmpty(this.scriptLoaded.get(url))) {
	    	Ext.Loader.loadScript({
	    		url : url,
	    		onLoad : function () {
	    			this.scriptLoaded.add(url, true);
	    			Ext.callback(callback, scope, arguments);
	    		},
	    		onError : onError,
	    		scope : this
	    	});
    	} else {
    		Ext.callback(callback, scope, arguments);
    	}
    },
    
    loadScripts : function (urls, callback, onError, scope) {
    	this.doLoadScripts(urls, callback, onError, scope, 0);
    },
    
    /**
	 * @private
	 * 
	 */
    doLoadScripts : function (urls, callback, onError, scope, indexAInclure) {
    	if(Ext.isEmpty(indexAInclure)){
    		return;
    	}
    	
    	if(indexAInclure >= urls.length) {
    		Ext.callback(callback, scope, arguments);
    		return;
    	}
    	
    	var futureIndexAInclure = indexAInclure+1;
    	
    	this.loadScript(urls[indexAInclure], function() {
    		this.doLoadScripts(urls, callback, onError, scope, futureIndexAInclure);
    	}, onError, this);
    } 
    
});

ScriptLoader = sitools.user.utils.ScriptLoader;