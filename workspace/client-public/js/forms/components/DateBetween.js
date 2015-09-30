/***************************************
* Copyright 2010-2015 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 * @class sitools.common.forms.components.DateBetween
 * @extends Ext.Container
 */
sitools.common.forms.components.DateBetween = Ext.extend(Ext.Container, {
//sitools.component.users.SubSelectionParameters.SingleSelection.DateBetween = Ext.extend(Ext.Container, {
    regToday : new RegExp("^\{\\$TODAY\}"), 
    initComponent : function () {
        this.context = new sitools.common.forms.ComponentFactory(this.context);
        //formattage de extraParams : 
        var extraParams = {};
        Ext.each(this.extraParams, function (param) {
            extraParams[param.name]= param.value;
        }, this);
        

    	this.showTime = extraParams.showTime;
        if (Ext.isString(this.showTime)) {
        	this.showTime = extraParams.showTime == "true";
        }
        this.truncateDefaultValue = extraParams.truncateDefaultValue;
        if (Ext.isString(this.truncateDefaultValue)) {
        	this.truncateDefaultValue = extraParams.truncateDefaultValue == "true";
        }
        this.format = extraParams.format;
    	
        var dateFormat = this.format;
        
        var valueFrom = this.getDefaultValue(this.defaultValues[0]) ;
		var valueTo = this.getDefaultValue(this.defaultValues[1]) ;
		
        this.fieldFrom = new Ext.form.DateField({
            allowBlank : true,
            format : dateFormat, 
            flex : 1,
            //height : this.height,
            value : valueFrom, 
            showTime : this.showTime,
            listeners : {
                scope : this,
                change : function (field, newValue, oldValue) {
                    this.fieldTo.setMinValue(newValue);
                    this.fieldTo.validate();
                }
            }
        });
        this.fieldTo = new Ext.form.DateField({
            allowBlank : true,
            format : dateFormat, 
            flex : 1,
            //height : this.height,
            value : valueTo, 
            showTime : this.showTime,
            minValue : valueFrom
        });
        Ext.apply(this, {
            layout : 'hbox',
            overCls : 'fieldset-child',
            stype : "sitoolsFormContainer",

            items : [this.fieldFrom, this.fieldTo ]
        });
        sitools.common.forms.components.DateBetween.superclass.initComponent.apply(
                this, arguments);
   	    if (!Ext.isEmpty(this.label)) {
	    	var labels = this.label.split("|") || [];
	    	switch (labels.length) {
	    		case 0 : 
	    			break;
	    		case 1 : 
	    			this.items.insert(0, new Ext.Container({
			            border : false,
			            html : labels[0],
			            width : 100
			        }));
			        break;
			    case 2 : 
		        	this.items.insert(0, new Ext.Container({
			            border : false,
			            html : labels[0],
			            width : 50
			        }));
		        	this.items.insert(2, new Ext.Container({
			            border : false,
			            html : labels[1],
			            width : 50, 
			            style : {
			            	"padding-left" : "10px"
			            }
			        }));
			        break;
			    case 3 : 
		        	this.items.insert(0, new Ext.Container({
			            border : false,
			            html : labels[0],
			            width : 50
			        }));
		        	this.items.insert(1, new Ext.Container({
			            border : false,
			            html : labels[1],
			            width : 50, 
			            style : {
			            	"padding-left" : "10px"
			            }
			        }));
			        this.items.insert(3, new Ext.Container({
			            border : false,
			            html : labels[2],
			            width : 50, 
			            style : {
			            	"padding-left" : "10px"
			            }
			        }));
			        break;
	    	}
	    }

    },

    /**
     * The code of the parameter to notify changed event.
     */
    code : null,

    /**
     * The left bound of the period.
     */
    fieldFrom : null,

    /**
     * The right bound of the period.
     */
    fieldTo : null,

    notifyValueSelected : function () {
        this.parent.notifyValueChanged(this.code);
    },

    isValueDefined : function () {
        if (this.fieldFrom.getValue() && this.fieldTo.getValue()) {
            return true;
        } else {
            return false;
        }
    },

    getSelectedValue : function () {
        return {
            from : this.fieldFrom.getValue(),
            to : this.fieldTo.getValue()
        };
    },
    
    getParameterValue : function () {
        var value = this.getSelectedValue();
        if (Ext.isEmpty(value) || Ext.isEmpty(value.from) || Ext.isEmpty(value.to)) {
            return null;
        }
        var format = SITOOLS_DATE_FORMAT;
	   	return {
	    	type : this.type, 
	    	code : this.code, 
	    	value : value.from.format(format) + "|" + value.to.format(format)
	    };

//        return this.type + "|" + this.code + "|" + value.from.format(format) + "|" + value.to.format(format) ;
    }, 
    
    /**
     * return a date truncated or not depending on this.truncateDefaultValue
     * @param {String} val A string representing date a SITOOLS_DATE_FORMAT or containg {$TODAY}
     * @return {Date} the date 
     */
    getDefaultValue : function (val) {
		var result;
		if (Ext.isEmpty(val)) {
			return null;
		}
		else {
			if (Ext.isDate(Date.parseDate(val, SITOOLS_DATE_FORMAT))) {
				result = Date.parseDate(val, SITOOLS_DATE_FORMAT);
			}
			else {
				if (this.regToday.test(val)) {
		        	try {
		        		result = sitools.common.utils.Date.stringWithTodayToDate(val);
		        	}
		        	catch (err) {
		        		return null;
		        	}
		        	
		        }
			}
		}
		
		if (this.truncateDefaultValue) {
			return result.clearTime();
		}
		else {
			return result;
		}
    },
    
    isValid : function () {
        return (this.fieldFrom.isValid() && this.fieldTo.isValid());
    },

//  *** Reset function for RESET button ***//
    // Alessandro's contribution from IAS
    resetToDefault : function () {
	this.fieldFrom.reset();
	this.fieldTo.reset();
    }
//  **************************************//

});

