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
/*global Ext, sitools, i18n,document */
Ext.ns("sitools.common.forms");

/**
 * A static method to transform a parameter to a sitools component.
 * @static
 * @param {} parameter the parameter as stored in the formParameter Model
 * @param {string} dataUrl the Url to request eventual data 
 * @param {string} formId the main formId
 * @param {} datasetCm the dataset Column model 
 * @param {string} context should be dataset or project. 
 *  @param {formComponentsPanel} the parent form
 * @return {Ext.Container} the container to include into form
 */
sitools.common.forms.formParameterToComponent = function (parameter, dataUrl, formId, datasetCm, context, form) {

	var valuesToSelect = null;
	this.component = null;
	var value, values, items, i, component, defaultValues, existsWidgetForParameterCode, selectedValue, minValue, maxValue, disabledDates;

	//The name of the constructor
	var constructor = eval(parameter.jsUserObject);
	component = new constructor ({
        parameterId : parameter.id, 
        values : Ext.isArray(parameter.values) ? parameter.values : [],
	    code : parameter.code,
	    type : parameter.type,
	    label : parameter.label,
	    height : parameter.height,
	    widthBox : parameter.width, 
	    valueSelection : parameter.valueSelection, 
        parentParam : parameter.parentParam, 
        dataUrl : dataUrl, 
        autoComplete : parameter.autoComplete, 
        formId : formId, 
        dimensionId : parameter.dimensionId, 
        unit : parameter.unit, 
        css : parameter.css, 
        defaultValues : parameter.defaultValues, 
        extraParams : parameter.extraParams, 
        datasetCm : datasetCm, 
        context : context,
        form : form,
	});
	
	return {
	    parameter : parameter,
	    component : component
	};

};
