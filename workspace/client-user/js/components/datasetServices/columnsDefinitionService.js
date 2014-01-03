/*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * 
 * This file is part of SITools2.
 * 
 * SITools2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SITools2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SITools2. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE,
 * ImageChooser, loadUrl, extColModelToStorage, SitoolsDesk
 */

Ext.namespace('sitools.user.component.dataviews.services');

/**
 * GUI Service to download a data selection from the dataset
 * 
 * @required datasetId
 * @required datasetUrl
 * @cfg {Ext.data.JsonStore} the store where nodes are saved
 * @class sitools.user.component.dataviews.services.addSelectionService
 * @extends Ext.Window
 */

sitools.user.component.dataviews.services.columnsDefinitionService = {};

sitools.user.component.dataviews.services.columnsDefinitionService.getParameters = function() {
	return [];
};

sitools.user.component.dataviews.services.columnsDefinitionService.executeAsService = function(
		config) {
	var windowConfig = {
		title : i18n.get('label.definitionTitle') + " : "
				+ config.dataview.datasetName,
		datasetName : config.dataview.datasetName,
		iconCls : "semantic",
		datasetDescription : config.datasetDescription,
		type : "defi",
		saveToolbar : true,
		toolbarItems : []
	};

	var javascriptObject = sitools.user.component.columnsDefinition;

	Ext.apply(windowConfig, {
				id : "defi" + config.dataview.datasetId
			});
	var componentCfg = {
		datasetId : config.dataview.datasetId,
		datasetCm : config.dataview.datasetCm,
		datasetName : config.dataview.datasetName,
		dictionaryMappings : config.dataview.dictionaryMappings,
		preferencesPath : "/" + config.dataview.datasetName,
		preferencesFileName : "semantic"
	};

	SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);
};