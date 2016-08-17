/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 ******************************************************************************/
/*global Ext, i18n, loadUrl, getDesktop, sitools, SitoolsDesk */
Ext.namespace("sitools.public.widget.sitoolsFilter");

Ext.define('sitools.public.widget.sitoolsFilter.Filter', {
    extend : 'Ext.Container',
    /**
     * @cfg {String} columnAlias 
     * The {@link Ext.data.Store} columnAlias of the field this filter represents.
     */
    columnAlias : null,
    
	layout : {
	    type : "vbox",
	    align : "left", 
	    pack : "center"
	},
	specificType : "filter",
	
    constructor : function (config) {
        Ext.apply(this, config);
		this.columnAlias = config.columnAlias;
		this.specificType = "filter";
        this.callParent([config]);

        this.init(config);
        if(config && config.value){
            this.setValue(config.value);
            delete config.value;
        }
    },

    /**
     * Template method to be implemented by all subclasses that is to
     * initialize the filter and install required menu items.
     * Defaults to Ext.emptyFn.
     */
    init : Ext.emptyFn,
    
    /**
     * Template method to be implemented by all subclasses that is to
     * get and return the value of the filter.
     * Defaults to Ext.emptyFn.
     * @return {Object} The 'serialized' form of this filter
     * @methodOf Ext.ux.grid.filter.Filter
     */
    getValue : Ext.emptyFn,
    
    /**
     * Template method to be implemented by all subclasses that is to
     * set the value of the filter and fire the 'update' event.
     * Defaults to Ext.emptyFn.
     * @param {Object} data The value to set the filter
     * @methodOf Ext.ux.grid.filter.Filter
     */	
    setValue : Ext.emptyFn,
    
    /**
     * Template method to be implemented by all subclasses that is to
     * validates the provided Ext.data.Record against the filters configuration.
     * Defaults to <tt>return true</tt>.
     * @param {Ext.data.Record} record The record to validate
     * @return {Boolean} true if the record is valid within the bounds
     * of the filter, false otherwise.
     */
    validateRecord : function(){
        return true;
    }, 
    
    _getHeight : Ext.emptyFn,
    
    getFilterData : function (){
        return this.getValue();
    }


});

