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
/*
 * @include "DatasetContext.js"
 * @include "ProjectContext.js" 
 */
Ext.namespace('sitools.common.forms');

/**
 * Basic factory to return DatasetFactory or ProjectFactory according with context. 
 * @param {String} context must be "dataset" or project"
 * @class sitools.common.forms.ComponentFactory
 * @return {}
 */
sitools.common.forms.ComponentFactory = function (context) {
//sitools.user.forms.components.ComponentFactory = function (context) {
	if (context == "dataset") {
		return new sitools.common.forms.DatasetContext(); 
	}
	if (context == "project") {
		return new sitools.common.forms.ProjectContext(); 
	}
};
