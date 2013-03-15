/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, 
 showHelp, loadUrl*/
Ext.namespace('sitools.util');

sitools.util.abstractController = Ext.extend(Ext.util.Observable, {

    views : null,
    
    stores : null,
    
    constructor : function (config) {
    	this.id = config.id;
    	
        this.views = new Ext.util.MixedCollection(false, function (el){
        	return el.name;
        });
        this.views.addAll(config.views);
        
         this.stores = new Ext.util.MixedCollection(false, function (el){
        	return el.name;
        });
        this.stores.addAll(config.stores);
        
        this.events = {};
        sitools.util.abstractController.superclass.constructor.call(this, config);
    },
    
    control : function (selectors) {
        sitools.util.EventBus.control(selectors, this);       
    },
    
    getView : function(name) {
     	return this.views.get(name);
     },
     
     getStore : function (name){
     	return this.stores.get(name);
     },
     
     registerView : function (view){
     	this.views.replace(view);
     }
});


