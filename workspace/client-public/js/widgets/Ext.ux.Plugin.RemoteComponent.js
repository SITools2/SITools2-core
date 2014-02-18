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

/**  (c) 2007-2008 Timo Michna / www.matikom.de
*  All rights reserved
*
*  This script is free software; you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation; either version 3 of the License, or
*  (at your option) any later version.
*
*  The GNU General Public License can be found at
*  http://www.gnu.org/copyleft/gpl.html.
*
*
*  This script is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  This copyright notice MUST APPEAR in all copies of the script!
***************************************************************/

/***************************************************************
*  For commercial use, ask the author for permission and different license
***************************************************************/


Ext.namespace('Ext.ux');
Ext.namespace('Ext.ux.Plugin');

Ext.ux.Plugin.LiteRemoteComponent = function (config){
	var defaultType = config.xtype || 'panel';
    var callback = function(res){ 
		this.container.add(Ext.ComponentMgr.create(Ext.decode(res.responseText), defaultType)).show();
		this.container.doLayout() ;
	};
    return{
		init : function (container){
			this.container = container;
			Ext.Ajax.request(Ext.apply(config, {success: callback, scope: this}));
    	}
	}
};

/**
 * @author Timo Michna / matikom
 * @class Ext.ux.Plugin.RemoteComponent
 * @extends Ext.util.Observable
 * @constructor
 * @param {Object} config
 * @version 0.3.0
 * Plugin for Ext.Container/Ext.Toolbar Elements to dynamically 
 * add Components from a remote source to the Element�s body.  
 * Loads configuration as JSON-String from a remote source. 
 * Creates the Components from configuration.
 * Adds the Components to the Container body.
 * Additionally to its own config options the class accepts all the 
 * configuration options required to configure its internal Ext.Ajax.request().
 */
Ext.ux.Plugin.RemoteComponent = function (config){

   /**
    * @cfg {String} breakOn 
	* set to one of the plugins events, to stop any 
    * further processing of the plugin, when the event fires.
    */
   /**
    * @cfg {mixed} loadOn 
	* Set to one of the Containers events {String}, to defer 
    * further processing of the plugin to when the event fires.
	* Set as an object literal {event: 'event', scope: 'scope'}
    * to listen for a different components (not the container) event.
    * Set to an numeric Array to listen to different events or components.
    * Use String or Literal style in numeric Array. Plugin will load by
	* the first occurence of any of the events. 
    */
   /**
	* @cfg {String} xtype 
	* Default xtype for loaded toplevel component.
	* Overwritten by config.xtype or xtype declaration 
	* Defaults to 'panel'
	* in loaded toplevel component.
	*/
   /**
	* @cfg {Boolean} purgeSubscribers 
	* set to 'true' to avoid unsubstribing all listeners after successfull process chain 
	* Defaults to false
	*/
   /**
	* @cfg {Mixed el} mask 
	* The element or DOM node, or its id to mask with loading indicator  
	*/
   /**
	* @cfg {Object} maskConfig 
	* Configuration for LoadMask.
	* only effective if config option 'mask' is set.    
	*/
	var defaultType = config.xtype || 'panel';
	Ext.applyIf(config, {
		purgeSubscribers:true
	});
	this.initialConfig = config;
    Ext.apply(this, config);
    //this.purgeSubscribers = config.purgeSubscribers || true;
    this.addEvents({
	    /**
	     * @event beforeload
	     * Fires before AJAX request. Return false to stop further processing.
	     * @param {Object} config
	     * @param {Ext.ux.Plugin.RemoteComponent} this
	     */
        'beforeload' : true,
	    /**
	     * @event beforecreate
	     * Fires before creation of new Components from AJAX response. 
		 * Return false to stop further processing.
	     * @param {Object} JSON-Object decoded from AJAX response
	     * @param {Ext.ux.Plugin.RemoteComponent} this
	     */
        'beforecreate' : true,
	    /**
	     * @event beforeadd
	     * Fires before adding the new Components to the Container. 
		 * Return false to stop further processing.
	     * @param {Object} new Components created from AJAX response.
	     * @param {Ext.ux.Plugin.RemoteComponent} this
	     */
        'beforeadd' : true,
	    /**
	     * @event beforecomponshow
	     * Fires before show() is called on the new Components. 
		 * Return false to stop further processing.
	     * @param {Object} new Components created from AJAX response.
	     * @param {Ext.ux.Plugin.RemoteComponent} this
	     */
        'beforecomponshow': true,
	    /**
	     * @event beforecontainshow
	     * Fires before show() is called on the Container. 
		 * Return false to stop further processing.
	     * @param {Object} new Components created from AJAX response.
	     * @param {Ext.ux.Plugin.RemoteComponent} this
	     */
        'beforecontainshow': true,
	    /**
	     * @event success
	     * Fires after full process chain. 
		 * Return false to stop further processing.
	     * @param {Object} new Components created from AJAX response.
	     * @param {Ext.ux.Plugin.RemoteComponent} this
	     */
        'success': true
    });
	Ext.ux.Plugin.RemoteComponent.superclass.constructor.call(this, config);
	// set breakpoint 
	if(config.breakOn){
	 	this.on(config.breakOn, function(){return false;});
	}
   /**
    * private
    * method adds component to container.
    * Creates Components from responseText and  
    * and populates Components in Container.
    * @param {Object} JSON Config for new component.
    */
	var renderComponent = function(JSON){
		if(this.fireEvent('beforeadd', JSON, this)){
			//this.container.initComponent();
			var component = this.container.add(JSON);
			
			component.fireEvent ('bodyResize', this);
			//alert (this.container.ownerCt.height());
			if(this.fireEvent('beforecomponshow', component, this)){
				return component;	
			} 				
		} 
	}.createDelegate(this);
   /**
    * private
    * Callback method for successful Ajax request.
    * Creates Components from responseText and  
    * and populates Components in Container.
    * @param {Object} response object from successful AJAX request.
    */
    var callback = function(res){ 
        var JSON = Ext.decode(res.responseText);
		if(this.fireEvent('beforecreate', JSON, this)){
			var component = null;
			//JSON = JSON instanceof Array ? JSON[0] : JSON;
			if(JSON instanceof Array){
				Ext.each(JSON, function(j, i){
						component = renderComponent(j).show();;
				});			
			}else{
				component = renderComponent(JSON).show();
			}
			if(this.fireEvent('beforecontainshow', component, this)){
				this.container.ownerCt.doLayout();
				this.fireEvent('success', component, this);
			} 				
		}   
		if(this.purgeSubscribers){
			this.purgeListeners();				
		}
	}.createDelegate(this);
   /**
    * public
    * Processes the AJAX request.
    * Generally only called internal. Can be called external,
    * when processing has been stopped or defered by config
    * options breakOn or loadOn.
    */
	this.load = function(){
		if(this.fireEvent('beforeload', config, this)){
			if(config.mask){
				var mask = new Ext.LoadMask(Ext.getDom(config.mask), Ext.apply({msg:'loading components...'}, config.maskConfig || {}));	
				mask.show();
				this.on('success', mask.hide, mask);
			}
			Ext.Ajax.request(Ext.apply(config, {success: callback, scope: this}));				
		} 
	};
   /**
    * public
    * Initialization method called by the Container.
    */
    this.init = function (container){
		container.on('beforedestroy', function(){this.purgeListeners();}, this);
		this.container = container;
		if(config.loadOn){		 	
			if(config.loadOn instanceof Array){
				Ext.each(config.loadOn, function(l, i, a){
					var evt = l.event || l.loadOn;
					var defer = function (){
						this.load();
						Ext.each(a, function(lo){
							(lo.scope || container).un(evt, defer, this);	
						}.createDelegate(this));
					}.createDelegate(this);
					(l.scope || container).on(evt, defer, this);					
				}.createDelegate(this));
			}else{
				(config.loadOn.scope || container).on((config.loadOn.event || config.loadOn), this.load, this, {single:true});							
			}
		}else{
			this.load();	
		}           
    };
};
Ext.define('Ext.ux.Plugin.RemoteComponent', {extend : 'Ext.util.Observable'});
