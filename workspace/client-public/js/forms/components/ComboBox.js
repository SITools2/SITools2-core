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
 * @class sitools.common.forms.components.ComboBox
 * @extends Ext.Container
 */
Ext.define('sitools.common.forms.components.ComboBox', {
    extend : 'Ext.Container',
    initComponent : function () {
		this.context = new sitools.common.forms.ComponentFactory(this.context);
        var defaultValue = "", value, items=[];
		for (i = 0; i < this.values.length; i++) {
			value = this.values[i];
			items.push([ value.value, value.value ]);
			if (value.defaultValue) {
				defaultValue = value.value;
			}
		}

		var store;
	    if (this.valueSelection == 'S') {
		    store = new Ext.data.ArrayStore({
		        fields : [ 'id', 'value' ],
		        sortInfo: {
		            field: 'value',
		            direction: 'ASC'
		        },
		        data : items
		    });
	    } else {
	        var params = {
		        colModel : [ this.code ],
		        distinct : true
		    };

	        
		    store = new Ext.data.JsonStore({
		        fields : [ {
		            name : 'id',
		            mapping : this.code
		        }, {
		            name : 'value',
		            mapping : this.code
		        } ],
		        sortInfo: {
                    field: 'value',
                    direction: 'ASC'
                },
		        autoLoad : !Ext.isEmpty(this.dataUrl) ? true : false,
		        root : 'data',
		        restful : true,
		        url : this.dataUrl + "/records",
		        baseParams : params, 
		        listeners : {
		            load : function () {
		                this.insert (0, [new Ext.data.Record({id : "", value : ""})]);    
		            }
		        }
		    });
		    
		    
	    }
	    this.combo = new Ext.form.ComboBox ({
	        store : store,
	        parameterId : this.parameterId, 
	        sParentParam : this.parentParam, 
	        valueField : 'id',
	        displayField : 'value',
	        typeAhead : true,
	        queryMode : 'local',
	        triggerAction : 'all',
	        selectOnFocus : true,
	        allowBlank : true,
	        editable : this.valueSelection=="S" ? true : false,
	        autoSelect : false,
	        flex : 1, 
	        height : this.height,
	        value : defaultValue,
			stype : "sitoolsFormItem", 
	        /**
			 * The Parent Window.
			 */
	        parent : "panelResultForm" + this.formId, 

	        /**
			 * The code of the parameter to notify changed event.
			 */
	        code : this.code,
	        anchor : '90%', 
	        listeners : {
	            scope : this, 
	            'select' : function () {
                    this.form.fireEvent('componentChanged', this.form, this);
	            }
	        }
	    });
	    Ext.apply(this, {
	    	stype : "sitoolsFormContainer",
	        layout : "hbox",
	        overCls : 'fieldset-child',
	    	items : [this.combo]
	    });
	    sitools.common.forms.components.ComboBox.superclass.initComponent.apply(this,
	            arguments);
   	    if (!Ext.isEmpty(this.label)) {
	    	this.items.insert(0, new Ext.Container({
	            border : false,
	            html : this.label,
	            width : 100
	        }));
	    }

    },

    notifyValueSelected : function () {
	    this.parent.notifyValueChanged(this.code);
    },

    isValueDefined : function () {
	    if (this.combo.getValue() && this.combo.getValue() !== "") {
		    return true;
	    } else {
		    return false;
	    }
    },
    getSelectedValue : function () {
	    if (this.combo.getValue() && this.combo.getValue() !== "") {
		    return this.combo.getValue();
	    } else {
		    return null;
	    }
    },
    setSelectedValue : function (value) {
	    this.combo.setValue(value);
    },
    getParameterValue : function () {
	    var value = this.getSelectedValue();
	    if (Ext.isEmpty(value)) {
		    return null;
	    }
//	    if (Ext.isString(value) && ! Ext.isNumber(parseFloat(value))){
//	        value = "" + value + "";
//	    }
//	    return this.type + "|" + this.code + "|" + value;
	   	return {
	    	type : this.type, 
	    	code : this.code, 
	    	value : value
	    };
	    
    },
    
//  *** Reset function for RESET button ***//
    resetToDefault : function () {
        this.combo.reset();
        this.form.fireEvent('componentChanged', this.form, this); 
    }
//  **************************************//
});
