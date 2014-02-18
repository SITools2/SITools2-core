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
 * DEFAULT_ORDER_FOLDER, ImageChooser, loadUrl, extColModelToStorage,
 * userStorage
 */

Ext.namespace('sitools.user.component.dataviews.services');

/**
 * GUI Service to download a data selection from the dataset
 * 
 * @required datasetId
 * @required datasetUrl
 * @cfg {Ext.data.JsonStore} the store where nodes are saved
 * @class sitools.user.component.dataviews.services.downloadService
 * @extends Ext.Window
 */
Ext.define('sitools.user.component.dataviews.services.downloadService', {
        extend : 'Ext.window.Window',
        alias : 'sitools.user.component.dataviews.services.downloadService',
        
		width : 300,
		modal : true,
		initComponent : function() {
			this.title = "Download Order for : "
					+ this.dataview.datasetName;

			this.items = [{
						xtype : 'form',
						labelWidth : 75,
						padding : '5px 5px 5px 5px',
						items : [{
									xtype : 'textfield',
									fieldLabel : i18n.get('label.name'),
									name : 'downloadName',
									anchor : '100%'
								}]
					}];

			this.buttons = [{
				text : i18n.get('label.ok'),
				scope : this,
				handler : function() {
					var downloadName = this.findByType('form')[0].getForm()
							.getFieldValues().downloadName;
					this._onDownload(this.datasetId, this.dataview,
							downloadName);
					this.close();
				}
			}, {
				text : i18n.get('label.cancel'),
				scope : this,
				handler : function() {
					this.close();
				}
			}];

			sitools.user.component.dataviews.services.downloadService.superclass.initComponent
					.call(this);
		},

		/**
		 * Create an entry in the userSpace with the request of the current
		 * grid.
		 * 
		 * @param {string}
		 *            datasetId
		 * @param {Ext.grid.GridPanel}
		 *            grid The current grid.
		 * @param {string}
		 *            orderName the future file name
		 */
		_onDownload : function(datasetId, grid, orderName) {
			var putObject = {};
			putObject.orderRequest = {};
			putObject.orderRequest.datasetId = datasetId;
			putObject.orderRequest.datasetUrl = grid.dataUrl;

			var filters = grid.getFilters();
			if (!Ext.isEmpty(filters)) {
				putObject.orderRequest.filters = filters
						.getFilterData(filters);
			}

			var storeSort = grid.getStore().getSortState();
			if (!Ext.isEmpty(storeSort)) {
				putObject.orderRequest.sort = storeSort;
			}

			var filtersCfg = grid.getStore().filtersCfg;
			if (!Ext.isEmpty(filtersCfg)) {
				putObject.orderRequest.filtersCfg = filtersCfg;
			}

			var colModel = Ext.util.JSON.encode(extColModelToStorage(grid
					.getColumnModel()));
			putObject.orderRequest.colModel = colModel;
			putObject.orderRequest.datasetId = datasetId;
			putObject.orderRequest.projectId = this.dataview.projectId;
			putObject.orderRequest.formParams = grid.getStore()
					.getFormParams();
			userStorage.set(orderName + ".json", "/" + DEFAULT_ORDER_FOLDER
							+ "/request", putObject);
		}
});

sitools.user.component.dataviews.services.downloadService.getParameters = function() {
	return [];
};

sitools.user.component.dataviews.services.downloadService.executeAsService = function(
		config) {
	var downloadService = new sitools.user.component.dataviews.services.downloadService(config);
	downloadService.show();
};