Ext.ns("sitools.widget");
sitools.widget.NumericFilter = Ext.extend(sitools.widget.Filter, {
	
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
    height : 60,
    
    /**  
     * @private
     * Template method that is to initialize the filter and install required menu items.
     */
    init : function (config) {
        config = config || {};
        
        this.inputFrom = new Ext.form.NumberField({
        	anchor : "100%", 
        	fieldLabel : "ux-rangemenu-gte", 
        	labelStyle : "height: 10px", 
        	width : 150
        }); 
        this.inputTo = new Ext.form.NumberField({
        	anchor : "100%", 
        	fieldLabel : "ux-rangemenu-lte", 
        	labelStyle : "height: 10px", 
        	width : 150
        });        
         
        var formPanel = new Ext.form.FormPanel({
        	items : [this.inputTo, this.inputFrom], 
        	labelWidth : 21, 
        	bodyBorder : false, 
        	border : false,
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
	        		"value" : this.inputFrom.getValue(), 
	        		"type" : "numeric"
	        	}
	        });
        }
        if (!Ext.isEmpty(this.inputTo.getValue())) {
        	result.push({
	        	"columnAlias" : this.columnAlias, 
	        	"data" : {
	        		"comparison" : "lte", 
	        		"value" : this.inputTo.getValue(), 
	        		"type" : "numeric"
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
	    		"type" : "numeric"
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
