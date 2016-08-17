/***************************************
* Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
        this.cbGroup = Ext.create("Ext.form.CheckboxGroup", {
	        allowBlank : true,
	        flex : 1,
	        items : [ {
	            xtype : "checkbox",
	            checked : this.defaultValues[0] === "true",
	            value : true,
				name : this.code
	        } ]
	    });
	    Ext.apply(this, {
	    	layout : "hbox",
	    	stype : "sitoolsFormContainer",
	    	overCls : 'fieldset-child',
	        items : [this.cbGroup]
	    });
	    this.callParent(arguments);
   	    
   	    if (!Ext.isEmpty(this.label)) {
	    	this.items.insert(0, Ext.create("Ext.Container", {
	            border : false,
	            html : this.label,
	            width : 100
	        }));
	    }

	},

    getSelectedValue : function () {
	    if (!Ext.isEmpty(this.cbGroup.getValue()[this.code])) {
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
    },

//  *** Reset function for RESET button ***//
 // Alessandro's contribution from IAS
	resetToDefault: function () {
		this.cbGroup.reset();
		this.form.fireEvent('componentChanged', this.form, this);
	}
//  **************************************//

});
