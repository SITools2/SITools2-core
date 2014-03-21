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
Ext.ns("sitools.widget");
sitools.widget.DateFilter = Ext.extend(sitools.widget.Filter, {
	
    /**
     * @cfg {String} iconCls
     * The iconCls to be applied to the menu item.
     * @cfg {String} format
     * The date format to use
     * Defaults to <tt>'ux-gridfilter-text-icon'</tt>.
     */
    iconCls : 'ux-gridfilter-text-icon',

    emptyText: 'Enter Filter Text...',
    selectOnFocus: true,
    flex : 0.9, 
    style: {
        "padding-left" : '10px'
    },
    height : 60,
    
    /**  
     * @private
     * Template method that is to initialize the filter and install required menu items.
     */
    init : function (config) {
        config = config || {};
        this.dateFormat = Ext.isEmpty(config.format)?SITOOLS_DEFAULT_IHM_DATE_FORMAT:config.format; 
        this.inputFrom = new Ext.form.DateField({
        	anchor : "100%", 
        	fieldLabel : "ux-rangemenu-gte", 
        	format : this.dateFormat, 
        	labelStyle : "height: 10px", 
        	width : 150
        }); 
        this.inputTo = new Ext.form.DateField({
        	anchor : "100%", 
        	fieldLabel : "ux-rangemenu-lte", 
        	format : this.dateFormat, 
        	labelStyle : "height: 10px", 
        	width : 150
            
        }); 
        var formPanel = new Ext.form.FormPanel({
        	items : [this.inputFrom, this.inputTo], 
        	labelWidth : 21, 
        	bodyBorder : false, 
        	border : false,
        	layout : 'form',
        	layoutConfig : {
        		fieldTpl : new Ext.Template(
        	
				    '<div class="x-form-item {itemCls}" tabIndex="-1">',
				        '<label for="{id}" style="{labelStyle}" class="x-form-item-label {label}"></label>',
				        '<div class="x-form-element" id="x-form-el-{id}" style="{elementStyle}">',
				        '</div><div class="{clearCls}"></div>',
				    '</div>'
				)
        	}
        })
        
        
        this.add(formPanel);
    },
    
    /**
     * @private
     * Template method that is to get and return the value of the filter.
     * @return {String} The value of this filter
     */
    getValue : function () {
        var result = [];
        if (!Ext.isEmpty(this.inputFrom.getValue())) {
        	result.push({
	        	"columnAlias" : this.columnAlias, 
	        	"data" : {
	        		"comparison" : "gte", 
	        		"value" : this.inputFrom.getValue().format(SITOOLS_DATE_FORMAT), 
	        		"type" : "date"
	        	}
	        });
        }
        if (!Ext.isEmpty(this.inputTo.getValue())) {
        	result.push({
	        	"columnAlias" : this.columnAlias, 
	        	"data" : {
	        		"comparison" : "lte", 
	        		"value" : this.inputTo.getValue().format(SITOOLS_DATE_FORMAT), 
	        		"type" : "date"
	        	}
        	});
        }
        return result;
    },
    
    getConfig : function () {
    	if (!Ext.isEmpty(this.inputFrom.getValue()) || !Ext.isEmpty(this.inputTo.getValue())) {
	 		return {
	    		"columnAlias" : this.columnAlias, 
	    		"value" : {
	    			"from" : this.inputFrom.getValue(), 
	    			"to" : this.inputTo.getValue() 
	    		}, 
	    		"type" : "date"
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
        this.inputFrom.setValue(value.from);
        this.inputTo.setValue(value.to);
        
    } , 
    _getHeight : function () {
    	return this.height;
    }
});
