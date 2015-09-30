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
Ext.ns("sitools.common.utils");

/**
 * An utility class to use in sitools.
 */
sitools.common.utils.Utils = {
	/**
	 * Transform an Array of Sitools properties (with field name, value) into an object.
	 * @param {Array} array the array to transform
	 * @return {Object} An object containing all properties as attributes.
	 */
	arrayProperties2Object : function (array) {
		var result = {};
		Ext.each(array, function(item){
			if (!Ext.isEmpty(item.name) && !Ext.isEmpty(item.value)) {
				result[item.name] = item.value;
			}
		});
		return result;
	}, 
	/**
	 * Highlight a json string inside a <pre> html tag 
	 */
	syntaxHighlight : function (json) {
        json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
            var cls = 'number';
            if (/^"/.test(match)) {
                if (/:$/.test(match)) {
                    cls = 'key';
                } else {
                    cls = 'string';
                }
            } else if (/true|false/.test(match)) {
                cls = 'boolean';
            } else if (/null/.test(match)) {
                cls = 'null';
            }
            return '<span class="' + cls + '">' + match + '</span>';
        });
    }
}

sitoolsUtils = sitools.common.utils.Utils;
