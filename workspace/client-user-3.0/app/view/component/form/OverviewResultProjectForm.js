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
/*global Ext, MULTIDS_TIME_DELAY, sitools, i18n, commonTreeUtils, projectGlobal, showResponse, document, SitoolsDesk, alertFailure, loadUrl*/
/*
 * @include "../../components/forms/forms.js"  
 */

Ext.namespace('sitools.user.view.component.form');

/**
 * Displays The result of a multiDatasets Reasearch. 
 * @cfg {string} urlTask The url to request 
 * @cfg {string} formId The form Id
 * @cfg {string} formName The form Name
 * @cfg {Array} formMultiDsParams an array of formParams (represent concepts selection)
 * @cfg {Array} datasets Array of Datasets Ids
 * @class sitools.user.component.forms.overviewResultsProjectForm
 * @extends Ext.Panel
 */

Ext.define('sitools.user.view.component.form.OverviewResultProjectForm', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.overviewResultProjectForm',
	
    initComponent : function () {

		var results = Ext.create('sitools.user.view.component.form.ResultProjectForm', {
			region : 'center'
		});
//		Ext.apply(results, {
//			region : "center"
//		});
		
		var description = i18n.get('label.descriptionMultiDS');
		
		this.southPanel = Ext.create('Ext.tab.Panel', {
        	title : i18n.get('label.results'), 
			region : "south", 
        	height : 300, 
        	split : true, 
        	autoScroll : false, 
        	collapsible : false, 
        	collapsed : true
		});
		Ext.apply(this, {
			layout : "border", 
			items : [results, this.southPanel]
		});
		
		if (description !== "label.descriptionMultiDS") {
			this.items.unshift({
				xtype : 'panel',
				height : 100, 
				html : description, 
				padding : "10px", 
				region : "north", 
				collapsible : true, 
				split : true, 
				autoScroll : true, 
				title : i18n.get('label.description')
			});
		}

		this.callParent(arguments);
    } 
});