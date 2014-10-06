/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
 * global Ext, sitools, ID, i18n, document, showResponse, alertFailure,
 * LOCALE,ImageChooser, loadUrl, extColModelToStorage
 */

Ext.namespace('sitools.user.component.datasets.services');

/**
 * GUI Service to display dataset current selection for ordering
 * 
 * @required datasetId
 * @required datasetUrl
 * @cfg {Ext.data.JsonStore} the store where nodes are saved
 * @class sitools.user.component.dataviews.services.displaySelectionCartService
 * @extends Ext.Window
 */
Ext.define('sitools.user.component.datasets.services.DisplaySelectionCartService', {
	extend : 'sitools.user.core.Component',
	alias : 'widget.displaySelectionCartService',
    
	getCartSelectionFile : function(response) {
		if (Ext.isEmpty(response.responseText)) {
			return;
		}
		try {
			var json = Ext.decode(response.responseText);
			this.cartSelectionFile = json;
		} catch (err) {
			return;
		}
	},

	getCart : function() {
		UserStorage.get(this.user + "_CartSelections.json",
				getCartFolder(Project.projectName), this,
				this.getCartSelectionFile, Ext.emptyFn,
				this.displaySelectionCart);
	},

	displaySelectionCart : function() {
		var selection = {};

		if (this.cartSelectionFile) {
			Ext.each(this.cartSelectionFile.selections, function(sel) {
						if (sel.selectionName === this.dataview.name) {
							selection = sel;
							return false;
						}
					}, this);
		}

		if (Ext.isEmpty(selection.ranges)) {
			return Ext.Msg.alert(i18n.get('label.information'), i18n.get('label.noSelectionArticles'));
		}
//
//		if (Ext.isFunction(this.dataview.ownerCt.close)) {
//			this.dataview.ownerCt.close();
//		} else {
//			this.dataview.ownerCt.ownerCt.destroy();
//		}
		var params = {
			ranges : selection.ranges,
			startIndex : selection.startIndex,
			nbRecordsSelection : selection.nbRecords,
			filters : selection.filters,
			filtersCfg : selection.filtersCfg,
			storeSort : selection.storeSort,
			formParams : selection.formParams,
			isModifySelection : true
		};
		
//		sitools.user.clickDatasetIcone(selection.dataUrl, 'data', params);
		
		var datasetViewComponent = Ext.create(this.dataview.componentClazz);
        datasetViewComponent.create(this.getApplication());
        
        var dataset = this.dataview.dataset;
        dataset.formParams = params;
        
        datasetViewComponent.init(dataset, params);

		// this.dataview.store.load({
		// params : {
		// start : startIndex,
		// limit : DEFAULT_LIVEGRID_BUFFER_SIZE
		// },
		// scope : this
		// });
	},
	
	executeAsService : function(config) {

		if (Ext.isEmpty(userLogin)) {
			popupMessage(i18n.get('label.information'), i18n.get('label.needToBeLogged', 'x-icon-information'));
			return;
		}

		Ext.apply(this, config);
		(Ext.isEmpty(userLogin)) ? this.user = "public" : this.user = userLogin;
		this.getCart();
	}
});

sitools.user.component.datasets.services.DisplaySelectionCartService.getParameters = function() {
	return [];
};
