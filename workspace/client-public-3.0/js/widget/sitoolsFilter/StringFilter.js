/*******************************************************************************
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
 ******************************************************************************/
/*global Ext, i18n, loadUrl, getDesktop, sitools, SitoolsDesk */
Ext.namespace("sitools.public.widget.sitoolsFilter");

Ext.define('sitools.public.widget.sitoolsFilter.StringFilter', {
    extend : 'sitools.public.widget.sitoolsFilter.Filter',
	
    /**
     * @cfg {String} iconCls
     * The iconCls to be applied to the menu item.
     * Defaults to <tt>'ux-gridfilter-text-icon'</tt>.
     */
    iconCls : 'ux-gridfilter-text-icon',

    emptyText: 'Enter Filter Text...',
    selectOnFocus: true,
    
    /**  
     * @private
     * Template method that is to initialize the filter and install required menu items.
     */
    init : function (config) {
        config = config || {};
        
        this.inputItem = Ext.create("Ext.form.TextField", {
        	anchor : "100%"
        }); 
        
        var image = Ext.create('Ext.Img', {
            src : '/sitools/client-public/res/images/sitoolsFilter/find.png',
            width : 16,
            height : 16,
            margin : '4 5 4 5'
        });
        
        var formPanel = Ext.create("Ext.Container", {
            layout : {
                type :'hbox',
                align :'stretch'                
            },
        	items : [image, this.inputItem], 
        	bodyBorder : false, 
        	border : false
        });
        
        
        
        this.add(formPanel);
    },
    
    /**
     * @private
     * Template method that is to get and return the value of the filter.
     * @return {String} The value of this filter
     */
    getValue : function () {
        if (!Ext.isEmpty(this.inputItem.getValue())) {
	        return [{
	        	"columnAlias" : this.columnAlias, 
	        	"data" : {
	        		"comparison" : "LIKE", 
	        		"value" : this.inputItem.getValue(), 
	        		"type" : "string"
	        	}
	        }];
        }
        else {
        	return [];
        }
    },
    
    /**
     * @private
     * Template method that is to get and return the value of the filter.
     * @return {String} The value of this filter
     */
    getConfig : function () {
     	if (!Ext.isEmpty(this.inputItem.getValue())) {
	 		return {
	    		"columnAlias" : this.columnAlias, 
	    		"value" : this.inputItem.getValue(), 
	    		"type" : "string"
	    	};
    	}
    	else {
    		return null;
    	}       
    },
    /**
     * @private
     * Template method that is to set the value of the filter.
     * @param {Object} value The value to set the filter
     */	
    setValue : function (value) {
        this.inputItem.setValue(value);
    },

    /**
     * Template method that is to validate the provided Ext.data.Record
     * against the filters configuration.
     * @param {Ext.data.Record} record The record to validate
     * @return {Boolean} true if the record is valid within the bounds
     * of the filter, false otherwise.
     */
    validateRecord : function (record) {
        var val = record.get(this.dataIndex);

        if(typeof val != 'string') {
            return (this.getValue().length === 0);
        }

        return val.toLowerCase().indexOf(this.getValue().toLowerCase()) > -1;
    }, 
    _getHeight : function () {
    	return this.height;
    }
    
});
