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
Ext.namespace('sitools.public.forms.components');

/**
 * Abstract Class to build Container to display a Cone Search. 
 * subclasses : 
 * 		- sitools.public.forms.components.ConeSearchCartesien
 *      - sitools.public.forms.components.ConeSearchPGSphere
 * @requires sitools.common.forms.ComponentFactory
 * @class sitools.public.forms.components.AbstractConeSearch
 * @extends sitools.public.forms.AbstractWithUnit
 */
Ext.define('sitools.public.forms.components.AbstractConeSearch', {
    extend : 'sitools.public.forms.AbstractWithUnit',
    requires : ['sitools.public.forms.ComponentFactory'],
    alternateClassName : ['sitools.common.forms.components.AbstractConeSearch'],
    
    initComponent : function () {
        this.context = sitools.public.forms.ComponentFactory.getContext(this.context);
        //formattage de extraParams : 
        var extraParams = {};
        Ext.each(this.extraParams, function (param) {
            extraParams[param.name]= param.value;
        }, this);
        
        this.extraParams = extraParams;
        var unit;
        //the administrator defines a dimension for this component
        if (!Ext.isEmpty(this.dimensionId)) {
            var unit = Ext.create("Ext.Button", {
                scope : this, 
                text : i18n.get('label.degree'),
				itemId : 'unitButton',
                width : 100,
                handler : function (b, e) {
                    //inherited method 
                    this.loadUnits(b);
                }
            });
        }
        else {
            unit = Ext.create("Ext.Container", {
				html : i18n.get('label.degree'),
        		width : 100
	    	});
            
        }
        
        //Build the defaults Values
        var defaultRa = null;
        var defaultDec = null;
        var defaultThirdValue = null;
        if (Ext.isArray(this.defaultValues) && this.defaultValues.length == 3) {
        	defaultRa = this.defaultValues[0];
        	defaultDec = this.defaultValues[1];
        	defaultThirdValue = this.defaultValues[2];
        }
        
	    this.raParam = Ext.create("Ext.form.NumberField", {
	        fieldLabel : "RA", 
	        allowBlank : true,
	        decimalPrecision : 20, 
	        anchor : "98%", 
	        value : defaultRa, 
	        flex : 1, 
	        labelSeparator : ""
	        
	    });
	    this.decParam = Ext.create("Ext.form.NumberField", {
	        fieldLabel : "DEC", 
	        allowBlank : true,
	        decimalPrecision : 20, 
	        anchor : "98%", 
	        value : defaultDec, 
	        flex : 1, 
	        labelSeparator : ""
	    });
	    
		this.thirdParam = Ext.create("Ext.form.NumberField", {
			fieldLabel : this.getLabelThirdParam(), 
			allowBlank : true,
			decimalPrecision : 20, 
	        value : defaultThirdValue, 
	        flex : 1, 
	        labelSeparator : ""
		});

		var thirdCont = Ext.create("Ext.form.FieldContainer", {
			labelWidth : 100,
            layout: {
                type : 'hbox',
                defaultMargins : {top: 0, right: 5, bottom: 0, left: 0}
            },
            defaults : {
                flex : 1
            }, 
			items : [this.thirdParam, unit], 
        	labelSeparator : ""
		});
		
		//build the resolver Name
		this.targetName = Ext.create("Ext.form.TextField", {
			flex : 1, 
			fieldLabel : i18n.get("label.targetName"),
			listeners : {
			    scope : this,
			    change : function (field, newValue, oldValue) {
			        this.nameResolverButton.setDisabled(Ext.isEmpty(newValue));
			    }
			}
		});
		
		this.nameResolverButton = Ext.create("Ext.Button", {
            scope : this,
            id : 'resolveNameBtn',
            handler : this.resolveTargetName, 
            text : i18n.get('label.resolveName'), 
            width : 100,
            disabled : true
        });
		
		var targetCmp = Ext.create("Ext.form.FieldSet", {
			title : i18n.get('label.resolverName'), 
			items : [{
				xtype : 'fieldcontainer',
                layout: {
                    type : 'hbox',
                    defaultMargins : {top: 0, right: 5, bottom: 0, left: 0}
                },
                defaults : {
                    flex : 1
                }, 
                items : [this.targetName, this.nameResolverButton]
			}]
		});
		
		//Load the 3 fields into a form layout in the main container items
		var items = [{
        	layout : "form", 
        	items : [ this.raParam, this.decParam, thirdCont], 
        	flex : 3
        }];
        
        //insert first the name Resolver if needed
        if (this.extraParams.showTargetName && this.extraParams.showTargetName != "false") {
        	items[0].items.unshift(targetCmp);
        }
		
	    Ext.apply(this, {
	        autoEl: 'div', 
	        layout : 'hbox', 
	        defaults : {
	        	xtype : 'container',
	        	autoEl : 'div'
	        },
	        items : items,
	        overCls : 'fieldset-child',
	        stype : "sitoolsFormContainer"
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
	/**
	 * Return true if values are specified 
	 * @return {Boolean}
	 */
    isValueDefined : function () {
	    if (this.raParam.getValue() && this.decParam.getValue() && this.thirdParam.getValue()) {
		    return true;
	    } else {
		    return false;
	    }
    },
	/**
	 * Return the ConeSearch value
	 * @return {}
	 */
    getSelectedValue : function () {
	    return {
	        raParam : this.raParam.getValue(),
	        decParam : this.decParam.getValue(),
	        thirdParam : this.thirdParam.getValue()
	    };
    },
    
    /**
     * Method to be called by the form 
     * returns an object with type, code& value attributes
     * @return {}
     */
    getParameterValue : function () {
	    var value = this.getSelectedValue();
	    if (Ext.isEmpty(value) || Ext.isEmpty(value.raParam) || Ext.isEmpty(value.decParam) || Ext.isEmpty(value.thirdParam)) {
		    return null;
	    }
	   	return {
	    	type : this.type, 
	    	code : this.code, 
	    	value : value.raParam + "|" + value.decParam + "|" + value.thirdParam, 
	    	userDimension : this.userDimension, 
	    	userUnit : this.userUnit
	    };
	    
    },
    /**
     * Name Resolver action. 
     * Send a query as defined by the admin. 
     * @param type
     * 		the type
     */
    resolveTargetName : function () {
    	
    	var baseUrl = this.extraParams.resolveNameUrl;
    	var url = baseUrl + "/" + this.targetName.getValue() + "/" + this.extraParams.coordSystem;
		
    	if (!Ext.isEmpty(this.extraParams.resolverName)){
    		url += "?nameResolver=" + this.extraParams.resolverName;
    	}
    	
    	this.getEl().mask();
    	Ext.Ajax.request({
    		url : url, 
    		scope : this, 
    		method : "GET", 
    		success : function (ret) {
    			try {
    				var json = Ext.decode(ret.responseText);
    				
    				if (json.totalResults > 1){
	    				this.choosePropertyType(json);
    				}
    				else {
    					this.fillUpRADEC(json.features[0].geometry.coordinates);
    				}
    			}
    			catch(err) {
    				Ext.Msg.alert(i18n.get('label.error'), i18n.get("label.unableToParseRequest"));
    				return;
    			}
    		}, 
    		failure : alertFailure, 
    		callback : function () {
    			this.getEl().unmask();
    		}
    	})
    },
    
    /**
     * Choose a property type. 
     * Define a type to fill up RA/DEC 
     */
    choosePropertyType : function (json) {
    	
    	var position = Ext.getCmp('resolveNameBtn').getPosition();
    	
    	var tabTypes = [];
    	
    	tabTypes.push({
            	xtype : 'label',
            	text : 'Select a Type : ',
	            style : 'padding:3px; margin:auto; font-style:italic;'
            });
    	
    	
    	Ext.each(json.features, function(feat, ind, all){
    		if(feat.properties.type){
    			tabTypes.push({
    				xtype : 'button',
    				text : feat.properties.type,
    				style : 'padding:3px; margin:auto;',
    				scope : this,
	            	handler : function(b, e){
	            		this.fillUpRADEC(feat.geometry.coordinates);
	            		Ext.getCmp('nameResolverTypeWin').close();
	            }
    			});
    		}
    	}, this);
    	
    	var popup = new Ext.Window({
    		id  : 'nameResolverTypeWin',
            x : position[0] + 4,
            y : position[1] + 18,
            title : '',
            bodyStyle : 'background-color : #E4E8EC;',
            frame:false,
            closable : false,
            resizable : false,
            draggable : true,
            items : tabTypes,
            listeners : {
            	deactivate : function (t){
            		t.close();
            	}
            }
        }).show();
    },
    
    fillUpRADEC : function (coord){
    	this.raParam.setValue(coord[0]);
    	this.decParam.setValue(coord[1]);
    },
    
    isValid : function () {
        if(!this.isEmpty(this.raParam) || !this.isEmpty(this.decParam) || !this.isEmpty(this.decParam)) {
            var valid = true;
            valid &= this.checkTextFieldIsNotEmpty(this.raParam);
            valid &= this.checkTextFieldIsNotEmpty(this.decParam);
            valid &= this.checkTextFieldIsNotEmpty(this.thirdParam);
            return valid;
        } else {
            return true;
        }
    },
    
    //private
    checkTextFieldIsNotEmpty : function (field){
        var valid = true;
        if(this.isEmpty(field)){
            field.markInvalid();
            valid = false;
        }
        return valid;        
    },
    
    isEmpty : function (field) {
        return Ext.isEmpty(field.getValue())
    },

//  *** Reset function for RESET button ***//
	resetToDefault: function () {
		this.raParam.reset();
		this.decParam.reset();
		this.thirdParam.reset();
		this.targetName.reset();
	}
//  **************************************//
    
	
});


