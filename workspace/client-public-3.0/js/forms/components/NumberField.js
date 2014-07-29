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
/*global Ext, sitools*/
/*
 * @include "../AbstractComponentsWithUnit.js"
 * @include "../ComponentFactory.js"
 */
Ext.ns('sitools.public.forms.components');

/**
 * A number Field form component. 
 * @cfg {string} parameterId Id of the future component.
 * @cfg {Array} code Array of string representing columns alias attached to this component
 * @cfg {string} type Defines wich unique type of component it is.
 * @cfg {string} label The label of the form component.
 * @cfg {numeric} height height of the future component.
 * @cfg {numeric} widthBox width of the future component.
 * @cfg {string} valueSelection "S" for specific Selection, "D" for data selection.
 * @cfg {string} dataUrl the url to request the data in case of valueSelection == "D".
 * @cfg {boolean} autoComplete for TEXTFIELD, autoComplete configuration of future Ext.form.Textfield.
 * @cfg {string} formId The form id that contains this component.
 * @cfg {string} dimensionId The sitools units dimension id.
 * @cfg {string} css An optional css to add to this component.
 * @cfg {Array} defaultValues Array of default values.
 * @cfg {} datasetCm the dataset ColumnModel object
 * @requires sitools.common.forms.ComponentFactory
 * @class sitools.public.forms.components.NumberField
 * @extends sitools.public.forms.AbstractWithUnit
 */
Ext.define('sitools.public.forms.components.NumberField', {
    extend : 'sitools.public.forms.AbstractWithUnit',
    requires : ['sitools.public.forms.ComponentFactory'],
    alternateClassName : ['sitools.common.forms.components.NumberField'],

    /**
	 * The numeric field
	 */
    field : null,

    initComponent : function () {
        this.context = sitools.public.forms.ComponentFactory.getContext(this.context);
        
                
        var extraParams = {};
        Ext.each(this.extraParams, function (param) {
            extraParams[param.name]= param.value;
        }, this);
        
        this.extraParams = extraParams;
	    
	    var unit = this.getUnitComponent();
        
	    this.field = new Ext.form.NumberField({
	        allowBlank : true,
	        flex : 1,
	        //height : this.height,
	        value : this.defaultValues[0],
	        decimalPrecision : 20
	    });
	    var items = [this.field];
        
        if (!Ext.isEmpty(unit)) {
        	items.push(unit);
        }
	    
	    Ext.apply(this, {
	        layout : 'hbox',
	        stype : "sitoolsFormContainer",
	        overCls : 'fieldset-child',
			defaults : {
	        	xtype : 'container',
	        	autoEl : 'div'
	        },
	        items : items
	    });
	    sitools.public.forms.components.NumberField.superclass.initComponent.apply(
	            this, arguments);
   	    if (!Ext.isEmpty(this.label)) {
	    	this.items.insert(0, new Ext.Container({
	            border : false,
	            html : this.label,
	            width : 100
	        }));
	    }

    },

    /**
     * Notify the parent of any change
     * @method
     */
    notifyValueSelected : function () {
	    this.parent.notifyValueChanged(this.code);
    },

    /**
     * Return if the value is defined.
     * @method
     * @return {Boolean} 
     */
    isValueDefined : function () {
	    if (this.fieldFrom.getValue()) {
		    return true;
	    } else {
		    return false;
	    }
    },

    /**
     * Get the selected Value
     * @return {Numeric} the selected Value
     */
    getSelectedValue : function () {
	    return this.field.getValue();
    },
    
    /**
     * Returns the value to request corresponding to the Filter API.
     * @return {String} parameter filter value
     */
    getParameterValue : function () {
	    var value = this.getSelectedValue();
	    if (Ext.isEmpty(value) || Ext.isEmpty(value)) {
		    return null;
	    }
	    var result = this.type + "|" + this.code + "|" + value;
	    if (!Ext.isEmpty(this.userDimension) && !Ext.isEmpty(this.userUnit)){
	    	result += "|" + this.userDimension + "|" + this.userUnit;
	    }
	   	return {
	    	type : this.type, 
	    	code : this.code, 
	    	value : value, 
	    	userDimension : this.userDimension, 
	    	userUnit : this.userUnit
	    };
    }
});
