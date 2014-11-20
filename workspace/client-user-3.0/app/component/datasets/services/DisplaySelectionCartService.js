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
    
	getCart : function() {
		UserStorage.get(this.user + "_CartSelections.json",
				getCartFolder(Project.projectName), this,
				this.displaySelectionCart, this.noSelectionArticle);
	},
	
	noSelectionArticle : function () {
	    Ext.Msg.alert(i18n.get('label.information'), i18n.get('label.noSelectionArticles'));
	},

	displaySelectionCart : function(response) {
	    
	    if (Ext.isEmpty(response.responseText)) {
	        return Ext.Msg.alert(i18n.get('label.information'), i18n.get('label.noSelectionArticles'));
        }
        
	    var json = Ext.decode(response.responseText);
        var cartSelectionFile = json;
	    
		var selection = {};

		if (cartSelectionFile) {
			Ext.each(cartSelectionFile.selections, function(sel) {
				if (sel.selectionName === this.dataview.dataset.name) {
					selection = sel;
					return false;
				}
			}, this);
		}

		if (Ext.isEmpty(selection.ranges)) {
			return Ext.Msg.alert(i18n.get('label.information'), i18n.get('label.noSelectionArticles'));
		}

		this.dataview.up("component[specificType=componentWindow]").close();
		
		var url = selection.dataUrl;
		var params = {
			ranges : selection.ranges,
			startIndex : selection.startIndex,
			nbRecordsSelection : selection.nbRecords,
			gridFilters : selection.gridFilters,
			gridFiltersCfg : selection.gridFiltersCfg,
			sortInfo : selection.sortInfo,
			formParams : selection.formParams,
			isModifySelection : true
		};
		
		sitools.user.utils.DatasetUtils.clickDatasetIcone(url, 'data', params);
	},
	
	init : function(config) {

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
