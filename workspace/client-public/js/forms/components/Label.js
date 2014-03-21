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
Ext.ns('sitools.common.forms.components');

/**
 * 
 * @class sitools.common.forms.components.Label
 * @extends Ext.Container
 */
sitools.common.forms.components.Label = Ext.extend(Ext.Container, {
//sitools.component.users.SubSelectionParameters.noSelection.Label = Ext.extend(Ext.Container, {

    initComponent : function () {
	    Ext.apply(this, {
	        html : this.label,
	        cls : this.css,
	        overCls : 'fieldset-child'
	    });
	    sitools.common.forms.components.Label.superclass.initComponent.apply(this, arguments);
    },
    getParameterValue : function () {
	    return null;
    }
});
