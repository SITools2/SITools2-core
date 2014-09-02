/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * 
 * This file is part of SITools2.
 * 
 * SITools2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SITools2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SITools2. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/* global Ext, sitools */
/*
 * @include "../ComponentFactory.js"
 */
Ext.ns('sitools.public.forms.components');

/**
 * @requires sitools.common.forms.ComponentFactory
 * @class sitools.public.forms.components.TextField
 * @extends Ext.Container
 */
Ext.define('sitools.public.forms.components.TextField', {
    extend : 'Ext.container.Container',
    requires : [ 'sitools.public.forms.ComponentFactory' ],
    alternateClassName : [ 'sitools.common.forms.components.TextField' ],

    initComponent : function () {
        this.context = sitools.public.forms.ComponentFactory.getContext(this.context);
        // alert (this.valueSelection);
        var store;
        var params = {
            colModel : [ this.code[0] ],
            distinct : true
        };
        store = Ext.create('Ext.data.JsonStore', {
            autoLoad : false,
            proxy : {
                type : 'ajax',
                url : this.dataUrl + '/records',
                reader : {
                    type : 'json',
                    root : 'data'
                }
            },
            baseParams : params,
            fields : [ {
                name : 'id',
                mapping : this.code[0]
            }, {
                name : 'value',
                mapping : this.code[0]
            } ],

        });

        this.tf = Ext.create('Ext.form.field.ComboBox', {
            store : store,
            stype : "sitoolsFormItem",
            valueField : 'id',
            displayField : 'value',
            typeAhead : false,
            hideTrigger : true,
            mode : 'remote',
            allowBlank : true,
            editable : true,
            autoSelect : false,
            // width : this.width - 110,
            value : this.defaultValues[0],

            /**
             * The code of the parameter to notify changed event.
             */
            code : this.code,
            flex : 1,
            listeners : {
                scope : this,
                beforequery : function () {
                    if (Ext.isEmpty(this.dataUrl)) {
                        return false;
                    }
                    return this.autoComplete;
                }
            }
        });
        Ext.apply(this, {
            layout : "hbox",
            stype : "sitoolsFormContainer",
            items : [ this.tf ]
        });
        
        this.callParent(arguments);
//        sitools.public.forms.components.TextField.superclass.initComponent.apply(this, arguments);
        
        if (!Ext.isEmpty(this.label)) {
            this.items.insert(0, Ext.create('Ext.container.Container', {
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
        if (this.tf.getValue() && this.tf.getValue() !== "") {
            return true;
        } else {
            return false;
        }
    },
    getSelectedValue : function () {
        if (this.tf.getValue() && this.tf.getValue() !== "") {
            return this.tf.getValue();
        } else {
            return null;
        }
    },
    setSelectedValue : function (value) {
        this.tf.setValue(value);
    },
    getParameterValue : function () {
        var value = this.getSelectedValue();
        if (Ext.isEmpty(value)) {
            return null;
        }
        return {
            type : this.type,
            code : this.code.join(','),
            value : value
        };
    }
});
