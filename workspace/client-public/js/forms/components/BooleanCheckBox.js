/***************************************
* Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 * @class sitools.common.forms.components.BooleanCheckbox
 * @extends Ext.Container
 */
sitools.common.forms.components.BooleanCheckbox = Ext.extend(Ext.Container, {
//sitools.component.users.SubSelectionParameters.SingleSelection.BooleanCheckbox = Ext.extend(Ext.Container, {

    initComponent : function () {
	    this.context = new sitools.common.forms.ComponentFactory(this.context);
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
	        items : [this.cbGroup]
	    });
	    sitools.common.forms.components.BooleanCheckbox.superclass.initComponent.apply(
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
