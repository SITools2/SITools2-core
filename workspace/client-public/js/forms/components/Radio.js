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
 * @include "../ComponentFactory.js"
 */
Ext.ns('sitools.common.forms.components');

/**
 * @requires sitools.common.forms.ComponentFactory
 * @class sitools.common.forms.components.Radio
 * @extends Ext.Container
 */
Ext.define('sitools.common.forms.components.Radio', {
    extend : 'Ext.Container',

    initComponent : function () {
		this.context = new sitools.common.forms.ComponentFactory(this.context);
        var value;
		var items = [];
		for (i = 0; i < this.values.length; i++) {
			value = this.values[i];
			items.push({
			    value : value.value,
			    boxLabel : value.value,
			    name : this.code[0],
			    checked : value.defaultValue
			});
		}

    	this.radioGroup = new Ext.form.RadioGroup({
	        allowBlank : true,
	        //height : this.height,
	        items : items, 
	        flex : 1,
	        /**
			 * The code of the parameter to notify changed event.
			 */
	        code : this.code[0], 
	        listeners : {
	        	scope : this, 
	        	change : function () {
                    this.form.fireEvent('componentChanged', this.form, this);
	        	}
	        }
	        

	    }); 
    	Ext.apply(this, {
	    	layout : "hbox",
	    	stype : "sitoolsFormContainer",
	    	overCls : 'fieldset-child',
	        items : [this.radioGroup]
	    });
	    sitools.common.forms.components.Radio.superclass.initComponent.apply(this,
	            arguments);
   	    if (!Ext.isEmpty(this.label)) {
	    	this.items.insert(0, new Ext.Container({
	            border : false,
	            html : this.label,
	            width : 100
	        }));
	    }

    },

   /* notifyValueChanged : function () {
	    this.parent.notifyValueChanged(this.code);
    },
*/
    isValueDefined : function () {
	    if (this.radioGroup.getValue()) {
		    return true;
	    } else {
		    return false;
	    }
    },

    getSelectedValue : function () {
	    if (this.radioGroup.getValue()) {
		    return this.radioGroup.getValue().value;
	    } else {
		    return null;
	    }
    },
    getParameterValue : function () {
	    var value = this.getSelectedValue();
	    if (Ext.isEmpty(value)) {
		    return null;
	    }
//        if (Ext.isString(value) && ! Ext.isNumber(parseFloat(value))){
//            value = "\"" + value + "\"";
//        }
	    return {
	    	type : this.type, 
	    	code : this.code, 
	    	value : value
	    };
    }



});
