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

sitools.util.EventBus = Ext.extend(Ext.util.Observable, {
 
    constructor: function(config) {
        sitools.util.EventBus.superclass.constructor.call(this, config);

        this.bus = {};

        var me = this;
        Ext.override(Ext.Component, {
            fireEvent: function(ev) {
                if (Ext.util.Observable.prototype.fireEvent.apply(this, arguments) !== false) {
                    return me.dispatch.call(me, ev, this, arguments);
                }
                return false;
            }
        });
    },

    dispatch: function(ev, target, args) {
        var bus = this.bus,
            selectors = bus[ev],
            selector, controllers, id, events, event, i, ln;
        
        if (selectors) {
            // Loop over all the selectors that are bound to this event
            for (selector in selectors) {               
                // Check if the target matches the selector
//                if (target.is(selector)) {
                if (this.match(Ext.ComponentMgr.getBySitoolsSelector(selector),
						target)) {                             
                    // Loop over all the controllers that are bound to this selector
                    controllers = selectors[selector];
                    for (id in controllers) {
                        // Loop over all the events that are bound to this selector on this controller
                        events = controllers[id];
                        for (i = 0, ln = events.length; i < ln; i++) {
                            event = events[i];
                            
                            // Fire the event!
                            if(event.fireEvent(ev, args)===false){
                                return false;
                            }
                            
                        }
                    }
                }
            }
        }
    },
    
    control: function(selectors, controller) {
        var bus = this.bus;
        
        Ext.objectEach(selectors, function(selector, listeners) {
            Ext.objectEach(listeners, function(ev, listener) {
                var options = {},
                    scope = controller,
//                    event = Ext.create('Ext.util.Event', controller, ev);
                    event = new Ext.util.Observable();
                    
                    
//                    .Ext.create('Ext.util.Event', controller, ev);

                // Normalize the listener
                if (Ext.isObject(listener)) {
                    options = listener;
                    listener = options.fn;
                    scope = options.scope || controller;
                    delete options.fn;
                    delete options.scope;
                }

                event.addListener(ev, listener, scope, options);

                // Create the bus tree if it is not there yet
                bus[ev] = bus[ev] || {};
                bus[ev][selector] = bus[ev][selector] || {};
                bus[ev][selector][controller.id] = bus[ev][selector][controller.id] || [];

                // Push our listener in our bus
                bus[ev][selector][controller.id].push(event);
            });
        });
    },
    
    uncontrol : function (controller) {
		var bus = this.bus,
        selectors,
        selector, controllers, id, events, event, i, ln;
        for (ev in bus) {
        	selectors = bus[ev];
	        if (selectors) {
	            // Loop over all the selectors that are bound to this event
	            for (selector in selectors) {               
	                // Check if the target matches the selector
	//              // Loop over all the controllers that are bound to this selector
	                controllers = selectors[selector];
	                for (id in controllers) {
	                	if(id == controller.id){
		                    // Loop over all the events that are bound to this selector on this controller
		                    events = controllers[id];
		                    for (i = 0, ln = events.length; i < ln; i++) {
		                        event = events[i];
		                        console.log("destroy event : " + ev + " on controller : " + controller.id);
		                    	Ext.destroy(event);
		                    }
	                    	bus[ev][selector][controller.id] = [];

	                	}
	                }	                
	            }
	        }
        }
    },
    
    
    match : function (components, component) {
        var result = false;
        if (!Ext.isEmpty(components)) {
	        Ext.each(components, function (cmp) {
	            if(cmp.getId() == component.getId()){
	                result = true;
	                return;
	            }
	        });	        
        }
        return result;
    }
});

sitools.util.EventBus = new sitools.util.EventBus();

Ext.objectEach =  function(object, fn, scope) {
	for (var property in object) {
        if (object.hasOwnProperty(property)) {
            if (fn.call(scope || object, property, object[property], object) === false) {
                return;
            }
        }
    }
};



Ext.ComponentMgr.getBySitoolsSelector  = function (selector) {
    var result = []; 
    Ext.ComponentMgr.all.each(function(component){
        if(component.sitoolsSelectorType == selector){
            result.push(component);             
        }
    });
    return result;
};





