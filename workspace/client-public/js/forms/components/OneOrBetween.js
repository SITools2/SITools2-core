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
/*global Ext, sitools, i18n*/
/*
 * @include "../AbstractComponentsWithUnit.js"
 * @include "../ComponentFactory.js"
 */
Ext.ns('sitools.common.forms.components');

/**
 * A number between form component. 
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
 * @class sitools.common.forms.components.OneOrBetween
 * @extends sitools.common.forms.AbstractWithUnit
 */
sitools.common.forms.components.OneOrBetween = Ext.extend(sitools.common.forms.AbstractWithUnit, {
//sitools.component.users.SubSelectionParameters.SingleSelection.OneOrBetween = Ext.extend(sitools.common.forms.AbstractWithUnit, {
    /**
	 * the first value.
	 */
    fieldOne : null,

    /**
	 * The left bound of the period.
	 */
    fieldFrom : null,

    /**
	 * The right bound of the period.
	 */
    fieldTo : null,


    initComponent : function () {
        this.context = new sitools.common.forms.ComponentFactory(this.context);
        
        //formattage de extraParams : 
	    var unit = this.getUnitComponent();
        
	    this.fieldOne = new Ext.form.NumberField({
	        allowBlank : true,
	        //height : this.height,
	        value : this.defaultValues[0],
	        flex : 2,
	        listeners :
            {
                scope : this,
		        change : function () {
			        this.fieldTo.setValue("");
			        this.fieldFrom.setValue("");
		        }
	        }, 
	        decimalPrecision : 20
	    });
        
	    this.fieldFrom = new Ext.form.NumberField({
	        allowBlank : true,
	        //height : this.height,
	        flex : 2,
	        validator : function (value) {
		        if (Ext.isEmpty(this.ownerCt.fieldTo.getValue())) {
			        return true;
		        }
		        if (value > this.ownerCt.fieldTo.getValue()) {
			        return "invalid Value";
		        } else {
			        return true;
		        }
	        },
	        listeners : {
		        change : function () {
			        this.ownerCt.fieldOne.setValue("");
		        }
	        }, 
	        decimalPrecision : 20
	    });
	    this.fieldTo = new Ext.form.NumberField({
	        allowBlank : true,
	        //height : this.height,
	        flex : 2,
	        validator : function (value) {
		        if (value < this.ownerCt.fieldFrom.getValue()) {
			        return "invalid Value";
		        } else {
			        return true;
		        }
	        },
	        listeners : {
		        change : function () {
			        this.ownerCt.fieldOne.setValue("");
		        }
	        }, 
	        decimalPrecision : 20
	    });
	    var items = [this.fieldOne, new Ext.Container({
            border : false,
            html : i18n.get('label.or'),
            width : 35
        }), new Ext.Container({
            border : false,
            html : i18n.get('label.min'),
            width : 35
        }), this.fieldFrom, new Ext.Container({
            border : false,
            html : i18n.get('label.max'),
            width : 35
        }), this.fieldTo];
        if (!Ext.isEmpty(unit)) {
        	items.push(unit);
        }
	    Ext.apply(this, {
	        layout : 'hbox',
	        columns : Ext.isEmpty(unit) ? 6 : 7,
	        fieldLabel : this.label,
	        overCls : 'fieldset-child',
	        stype : "sitoolsFormContainer",

	        items : items
	    });
	     sitools.common.forms.components.OneOrBetween.superclass.initComponent
                                .apply( this, arguments);
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
     * Return if at least one value is defined.
     * @method
     * @return {Boolean} 
     */
    isValueDefined : function () {
	    var value = this.getSelectedValue();
	    return !(Ext.isEmpty(value) || (Ext.isEmpty(value.from) && Ext.isEmpty(value.to) && Ext.isEmpty(value.one)));
    },

    /**
     * Get the selected Value
     * @return {} the selected Value {
     * 	one : one value, 
     *  from : from value, 
     * 	to : to value
     * }
     */
    getSelectedValue : function () {
	    return {
	        one : this.fieldOne.getValue(),
	        from : this.fieldFrom.getValue(),
	        to : this.fieldTo.getValue()
	    };
    },

    /**
     * Returns the value to request corresponding to the Filter API.
     * @return {String} parameter filter value
     */
    getParameterValue : function () {
	    var value = this.getSelectedValue();
	    if (!this.isValueDefined()) {
		    return null;
	    }

	    var result = this.type + "|" + this.code + "|" + value.one + "|" + value.from+ "|" + value.to;
	    if (!Ext.isEmpty(this.userDimension) && !Ext.isEmpty(this.userUnit)){
	    	result += "|" + this.userDimension + "|" + this.userUnit;
	    }
	   	return {
	    	type : this.type, 
	    	code : this.code, 
	    	value : value.one + "|" + value.from + "|" + value.to, 
	    	userDimension : this.userDimension, 
	    	userUnit : this.userUnit
	    };
    }
});
