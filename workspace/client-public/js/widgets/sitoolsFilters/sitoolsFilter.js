/** 
 * @class Ext.ux.grid.filter.Filter
 * @extends Ext.util.Observable
 * Abstract base class for filter implementations.
 */

Ext.ns("sitools.widget");

sitools.widget.Filter = Ext.extend(Ext.Container, {
    /**
     * @cfg {String} columnAlias 
     * The {@link Ext.data.Store} columnAlias of the field this filter represents.
     */
    columnAlias : null,
    
	layout : "vbox",
	layoutConfig : {
		align : "left", 
		pack : "center"
	},
	specificType : "filter",
	
    constructor : function (config) {
        Ext.apply(this, config);
        this.layout = 'hbox';    
		this.columnAlias = config.columnAlias;
		this.specificType = "filter";
        sitools.widget.Filter.superclass.constructor.call(this);

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

