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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, locale, ImageChooser, 
 showHelp, loadUrl*/

/**
 * Object to expose methods to build form Components user objects in dataset Context
 * @class sitools.common.forms.DatasetContex
 * 
 */
Ext.define('sitools.public.forms.DatasetContext', {
    alternateClassName : ['sitools.common.forms.DatasetContext'],
//sitools.user.forms.components.DatasetContext = function () {
	context : "dataset",
	
    /**
     * Return the unit corresponding to a given column Alias
     * @param {} scope the initial config
     * @return {} the unit founded
     */
	getRefUnit : function (scope) {
    	var columnAlias = scope.code[0];
    	if (Ext.isEmpty(scope.datasetCm)) {
    		return;
    	}
    	var result;
    	Ext.each(scope.datasetCm, function(column){
    		if (column.columnAlias == columnAlias) {
    			result = column.unit;
    		}
    	});
    	return result;
    }
});
