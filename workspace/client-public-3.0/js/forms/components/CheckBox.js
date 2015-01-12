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
 * @class sitools.public.forms.components.CheckBox
 * @extends Ext.Container
 */
Ext.define('sitools.public.forms.components.CheckBox', {
    extend: 'Ext.Container',
    requires: ['sitools.public.forms.ComponentFactory'],
    alternateClassName: ['sitools.common.forms.components.CheckBox'],

    initComponent: function () {
        this.context = sitools.public.forms.ComponentFactory.getContext(this.context);
        var items = [];
        for (i = 0; i < this.values.length; i++) {
            value = this.values[i];
            items.push({
                value: value.value,
                boxLabel: value.value,
                name: this.code,
                inputValue : value.value,
                checked: value.defaultValue,
                height: 25

            });
        }
        this.cbGroup = Ext.create("Ext.form.CheckboxGroup", {
            allowBlank: true,
            columns: 3,
            flex: 1,
            items: items
        });
        Ext.apply(this, {
            height: this.height,
            width: this.width,
            overCls: 'fieldset-child',
            layout: "hbox",
            stype: "sitoolsFormContainer",
            items: [this.cbGroup]
        });
        this.callParent(arguments);

        if (!Ext.isEmpty(this.label)) {
            this.items.insert(0, Ext.create("Ext.Container", {
                border: false,
                html: this.label,
                width: 100
            }));
        }
    },

    getSelectedValue: function () {
        var values = this.cbGroup.getValue()[this.code];
        if (!Ext.isEmpty(values)) {
            if (Ext.isString(values)) {
                values = [values];
            }
            var selectedValues = [];
            Ext.each(values, function(value) {
                //OLD CODE, WEIRD CODE....
                //if (Ext.isString(values[i].value) && !Ext.isNumber(parseFloat(values[i].value))) {
                //    values[i].value = values[i].value;
                //}
                selectedValues.push(value);
            });
            return selectedValues;
        } else {
            return null;
        }
    },
    getParameterValue: function () {
        var values = this.getSelectedValue();
        if (!Ext.isArray(values)) {
            return null;
        }
        values = values.join("|");
//	    return this.type + "|" + this.code + "|" + values;
        return {
            type: this.type,
            code: this.code,
            value: values
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
