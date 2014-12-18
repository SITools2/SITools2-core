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
/**
 * @requires sitools.common.forms.ComponentFactory
 * @class sitools.public.forms.components.Radio
 * @extends Ext.Container
 */
Ext.define('sitools.public.forms.components.Radio', {
    extend: 'Ext.Container',
    requires: ['sitools.public.forms.ComponentFactory'],
    alternateClassName: ['sitools.common.forms.components.Radio'],

    initComponent: function () {
        this.context = sitools.public.forms.ComponentFactory.getContext(this.context);
        var value;
        var items = [];
        for (i = 0; i < this.values.length; i++) {
            value = this.values[i];
            items.push({
                value: value.value,
                inputValue: value.value,
                boxLabel: value.value,
                name: this.code,
                checked: value.defaultValue
            });
        }

        this.radioGroup = Ext.create("Ext.form.RadioGroup", {
            allowBlank: true,
            //height : this.height,
            items: items,
            flex: 1,
            /**
             * The code of the parameter to notify changed event.
             */
            code: this.code[0],
            listeners: {
                scope: this,
                change: function () {
                    this.form.fireEvent('componentChanged', this.form, this);
                }
            }


        });
        Ext.apply(this, {
            layout: "hbox",
            stype: "sitoolsFormContainer",
            overCls: 'fieldset-child',
            items: [this.radioGroup]
        });
        sitools.public.forms.components.Radio.superclass.initComponent.apply(this,
            arguments);
        if (!Ext.isEmpty(this.label)) {
            this.items.insert(0, new Ext.Container({
                border: false,
                html: this.label,
                width: 100
            }));
        }

    },

    /* notifyValueChanged : function () {
     this.parent.notifyValueChanged(this.code);
     },
     */
    isValueDefined: function () {
        if (this.radioGroup.getValue()[this.code]) {
            return true;
        } else {
            return false;
        }
    },

    getSelectedValue: function () {
        var value = this.radioGroup.getValue()[this.code];
        if (!Ext.isEmpty(value)) {
            return value;
        }
        else {
            return null;
        }
    },
    getParameterValue: function () {
        var value = this.getSelectedValue();
        if (Ext.isEmpty(value)) {
            return null;
        }
//        if (Ext.isString(value) && ! Ext.isNumber(parseFloat(value))){
//            value = "\"" + value + "\"";
//        }
        return {
            type: this.type,
            code: this.code,
            value: value
        };
    },

    //  *** Reset function for RESET button ***//
    resetToDefault: function () {
        this.radioGroup.reset();
        this.form.fireEvent('componentChanged', this.form, this);
    }
    //  ***************************************//


});
