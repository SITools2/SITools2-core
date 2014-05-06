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
Ext.ns('sitools.public.forms.components');

/**
 * @requires sitools.common.forms.ComponentFactory
 * @class sitools.public.forms.components.BooleanCheckbox
 * @extends Ext.Container
 */
Ext.define('sitools.public.forms.components.BooleanCheckbox', {
    extend : 'Ext.Container',
    requires : ['sitools.public.forms.ComponentFactory'],
    alternateClassName : ['sitools.common.forms.components.BooleanCheckbox'],

    initComponent : function () {
	    this.context = sitools.public.forms.ComponentFactory.getContext(this.context);
        this.cbGroup = new Ext.form.CheckboxGroup ({
	        allowBlank : true,
	        flex : 1,
	        items : [ {
	            xtype : "checkbox",
	            checked : eval(this.defaultValues[0]),
	            value : true
	        } ]
	    });
	    Ext.apply(this, {
	    	layout : "hbox",
	    	stype : "sitoolsFormContainer",
	    	overCls : 'fieldset-child',
	        items : [this.cbGroup]
	    });
	    sitools.public.forms.components.BooleanCheckbox.superclass.initComponent.apply(
	            this, arguments);
   	    
   	    if (!Ext.isEmpty(this.label)) {
	    	this.items.insert(0, new Ext.Container({
	            border : false,
	            html : this.label,
	            width : 100
	        }));
	    }

	},

    getSelectedValue : function () {
	    if (this.cbGroup.getValue() && this.cbGroup.getValue().length > 0) {
		    return "true";
	    } else {
		    return "false";
	    }

    },
    getParameterValue : function () {
	    var value = this.getSelectedValue();
	    return {
	    	type : this.type, 
	    	code : this.code, 
	    	value : value
	    };
    }

});
