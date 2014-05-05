/*******************************************************************************
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
 ******************************************************************************/
/*global Ext, sitools, ID, i18n, showResponse, alertFailure*/
Ext.define('sitools.public.widget.item.DateFieldWithToday', {
    alternateClassName : ['sitools.widget.DateFieldWithToday'],
    extend : 'Ext.form.field.Date',
    requires : ['sitools.public.utils.Date'],    
    regToday : new RegExp("^\{\\$TODAY\}"), 
    invalidTextWithToday : "Impossible to make a date with {0}. A valid example is {$TODAY} + 1", 
    parseDate : function (value) {
        if(!value || Ext.isDate(value)){
            return value;
        }
        //Ajout d'un test sur la valeur pour sortir s'il y a la valeur {$TODAY}
        if (this.regToday.test(value)) {
            return value;
        }
        var v = this.safeParse(value, this.format),
            af = this.altFormats,
            afa = this.altFormatsArray;

        if (!v && af) {
            afa = afa || af.split("|");

            for (var i = 0, len = afa.length; i < len && !v; i++) {
                v = this.safeParse(value, afa[i]);
            }
        }
        return v;
    },
    getErrors : function (value) {
        var errors = Ext.form.DateField.superclass.getErrors.apply(this, arguments);

        value = this.formatDate(value || this.processRawValue(this.getRawValue()));

        if (value.length < 1) { // if it's blank and textfield didn't flag it then it's valid
             return errors;
        }

        var svalue = value;
        // Ne pas parser la date en objet Date si {$TODAY} est présent
        var time = false;
        if (this.regToday.test(value)) {
            try {
                value = sitools.public.utils.Date.stringWithTodayToDate(value);
                if (!sitools.public.utils.Date.isValidDate(value)) {
                    throw "";
                }
            }
            catch (err) {
                errors.push(Ext.String.format(this.invalidTextWithToday, svalue));
                return errors;  
            }
            
        }
        else {
            value = this.parseDate(value);
        }
        
        if (!value) {
            errors.push(Ext.String.format(this.invalidText, svalue, this.format));
            return errors;
        }

        time = value.getTime();
        
        if (this.minValue && time < this.minValue.clearTime().getTime()) {
            errors.push(Ext.String.format(this.minText, this.formatDate(this.minValue)));
        }

        if (this.maxValue && time > this.maxValue.clearTime().getTime()) {
            errors.push(Ext.String.format(this.maxText, this.formatDate(this.maxValue)));
        }

        if (this.disabledDays) {
            var day = value.getDay();

            for(var i = 0; i < this.disabledDays.length; i++) {
                if (day === this.disabledDays[i]) {
                    errors.push(this.disabledDaysText);
                    break;
                }
            }
        }

        var fvalue = this.formatDate(value);
        if (this.disabledDatesRE && this.disabledDatesRE.test(fvalue)) {
            errors.push(Ext.String.format(this.disabledDatesText, fvalue));
        }

        return errors;
        
    }, 
    setValue : function (date) {
        if (this.regToday.test(date)) {
            return Ext.form.DateField.superclass.setValue.call(this, date);
        }
        else {
            return Ext.form.DateField.superclass.setValue.call(this, this.formatDate(this.parseDate(date)));
        
        }
        
    }
});