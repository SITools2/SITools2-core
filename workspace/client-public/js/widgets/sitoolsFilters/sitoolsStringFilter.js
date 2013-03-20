Ext.ns("sitools.widget");
sitools.widget.StringFilter = Ext.extend(sitools.widget.Filter, {
	
    /**
     * @cfg {String} iconCls
     * The iconCls to be applied to the menu item.
     * Defaults to <tt>'ux-gridfilter-text-icon'</tt>.
     */
    iconCls : 'ux-gridfilter-text-icon',

    emptyText: 'Enter Filter Text...',
    selectOnFocus: true,
    flex : 0.9, 
    style: {
        "padding-left" : '10px'
    },
    
    height : 30, 
    
    /**  
     * @private
     * Template method that is to initialize the filter and install required menu items.
     */
    init : function (config) {
        config = config || {};
        
        this.inputItem = new Ext.form.TextField({
        	anchor : "100%", 
        	fieldLabel : "ux-gridfilter-text-icon", 
        	labelStyle : "height: 10px", 
        	width : 150
        }); 
        var formPanel = new Ext.form.FormPanel({
        	items : [this.inputItem], 
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
