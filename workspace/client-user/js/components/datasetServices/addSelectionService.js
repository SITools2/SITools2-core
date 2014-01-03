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
 * ImageChooser, loadUrl, extColModelToStorage
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
sitools.user.component.dataviews.services.addSelectionService = Ext.extend(
		Ext.Window, {
			width : 300,
			modal : true,
			initComponent : function() {

				this.title = "Selection Order for : "
						+ this.dataview.datasetName;

				this.items = [{
							xtype : 'form',
							labelWidth : 75,
							padding : '5px 5px 5px 5px',
							items : [{
										xtype : 'textfield',
										fieldLabel : i18n.get('label.name'),
										name : 'selectionName',
										anchor : '100%'
									}]
						}];

				this.buttons = [{
					text : i18n.get('label.ok'),
					scope : this,
					handler : function() {
						var selectionName = this.findByType('form')[0]
								.getForm().getFieldValues().selectionName;
						this._addSelection(this.dataview.getSelections(),
								this.dataview, this.datasetId, selectionName);
						this.close();
					}
				}, {
					text : i18n.get('label.cancel'),
					scope : this,
					handler : function() {
						this.close();
					}
				}];

				sitools.user.component.dataviews.services.addSelectionService.superclass.initComponent
						.call(this);
			},

			/**
			 * Create an entry in the user storage with all the selected
			 * records.
			 * 
			 * @param {Array}
			 *            selections An array of selected {Ext.data.Record}
			 *            records
			 * @param {Ext.grid.GridPanel}
			 *            grid the grid
			 * @param {string}
			 *            datasetId
			 * @param {string}
			 *            orderName the name of the future file.
			 */
			_addSelection : function(selections, grid, datasetId, orderName) {
				var primaryKey = "";
				var rec = selections[0];
				Ext.each(rec.fields.items, function(field) {
							if (field.primaryKey) {
								primaryKey = field.name;
							}
						}, rec);
				if (Ext.isEmpty(primaryKey) || Ext.isEmpty(rec.get(primaryKey))) {
					Ext.Msg.alert(i18n.get('label.warning'), i18n
									.get('warning.noPrimaryKey'));
					return;
				}
				var putObject = {};
				putObject.orderRecord = {};

				putObject.orderRecord.records = [];
				var colModel = extColModelToStorage(grid.getColumnModel());
				Ext.each(selections, function(rec) {
							var data = {};
							Ext.each(colModel, function(column) {
										if (!column.hidden || column.primaryKey) {
											data[column.columnAlias] = rec
													.get(column.columnAlias);
										}
									});
							putObject.orderRecord.records.push(data);
						});
				putObject.orderRecord.colModel = colModel;
				putObject.orderRecord.datasetId = this.dataview.datasetId;
				putObject.orderRecord.projectId = this.dataview.projectId;
				putObject.orderRecord.dataUrl = this.dataview.dataUrl;
				putObject.orderRecord.datasetName = this.dataview.datasetName;
				putObject.orderRecord.nbRecords = putObject.orderRecord.records.length;

				userStorage.set(orderName + ".json", "/" + DEFAULT_ORDER_FOLDER
								+ "/records", putObject);
			}

		});
Ext.reg('sitools.user.component.dataviews.services.addSelectionService',
		sitools.user.component.dataviews.services.addSelectionService);

sitools.user.component.dataviews.services.addSelectionService.getParameters = function() {
	return [];
};
/**
 * @static Implementation of the method executeAsService to be able to launch
 *         this window as a service.
 * @param {Object}
 *            config contains all the service configuration
 */
sitools.user.component.dataviews.services.addSelectionService.executeAsService = function(
		config) {
	var selections = config.dataview.getSelections();
	var rec = selections[0];
	if (Ext.isEmpty(rec)) {
		Ext.Msg.alert(i18n.get('label.warning'), i18n
						.get('warning.noRecordsSelected'));
		return;
	}
	var addSelectionService = new sitools.user.component.dataviews.services.addSelectionService(config);
	addSelectionService.show();
};