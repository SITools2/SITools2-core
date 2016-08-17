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
Ext.namespace('sitools.public.utils');

/**
 * An utility class to use for specific sitools dates.
 */
Ext.define("sitools.public.utils.Date", {
    singleton : true,
	/**
	 * The regExp to test if a string can be transformed to a date 
	 */
	regToday : new RegExp("^\{\\$TODAY\} *(\\+ *[0-9]*|\\- *[0-9]*)?$"),
	/**
	 * in the template {$TODAY} + x, determine the x unit (currently it is a Day)
	 * @type 
	 */
	timeInterval : Ext.Date.DAY, 
	/**
	 * Transform a String value containing {$TODAY} into a valid date.
	 * @param {String} val the string value
	 * @return {Date} the date Value.
	 */
	stringWithTodayToDate : function(val) {
		if (Ext.isDate(val)) {
			return val;
		}
		if (!this.regToday.test(val)) {
			return null;
		}
		
		var regNbJour = new RegExp("[0-9]+", "g");
		var regOp = new RegExp("[+]|[-]");
		var nbJour = parseFloat(regNbJour.exec(val));
		var op = regOp.exec(val);
		
		var result = new Date();
		if (Ext.isEmpty(nbJour) && !Ext.isEmpty(op)) {
			return null;
		}
		if (!Ext.isEmpty(nbJour) && !Ext.isEmpty(op)) {
			if (op == "-") {
				nbJour = nbJour * -1;
			}
			result = Ext.Date.add(result, this.timeInterval, nbJour); 
		}
		return result;

	}, 
	/**
	 * Test if a date object is valid or not.
	 * @param {Date} d date value
	 * @return {Boolean} valid or not valid
	 */
	isValidDate : function (d) {
		if ( Object.prototype.toString.call(d) !== "[object Date]" )
			return false;
		return !isNaN(d.getTime());
	}
});

