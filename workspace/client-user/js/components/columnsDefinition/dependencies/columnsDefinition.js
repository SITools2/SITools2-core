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
/* global Ext, sitools, showResponse, i18n, extColModelToJsonColModel, loadUrl */

Ext.namespace('sitools.user.component');

// sitools.component.users.datasets.columnsDefinition = function (config) {
/**
 * A Panel that displays a grid with columns definition.
 * 
 * @cfg {string} datasetId The datasetId
 * @cfg {Ext.grid.ColumnModel} datasetCm The dataset Column Model
 * @cfg {string} datasetName The dataset Name
 * @cfg {Array} dictionnaryMappings The dataset Dictionnary Mapping
 * @class sitools.user.component.columnsDefinition
 * @extends Ext.Panel
 */
sitools.user.component.columnsDefinition = function(config) {

	Ext.apply(this, config);
	this.dictionaryMappings = config.dictionaryMappings[0];

	var fields = [{
				name : 'columnAlias',
				type : 'string'
			}, {
				name : 'unit',
				type : 'string'
			}];

	var columns = [{
				header : i18n.get('label.columnAlias'),
				dataIndex : 'columnAlias',
				width : 100,
				sortable : true
			}, {
				header : i18n.get('label.unit'),
				dataIndex : 'unit',
				width : 100,
				sortable : true
			}];

	if (!Ext.isEmpty(this.dictionaryMappings)
			&& !Ext.isEmpty(this.dictionaryMappings.mapping)
			&& !Ext.isEmpty(this.dictionaryMappings.mapping[0])) {

		var conceptAsTemplate = this.dictionaryMappings.mapping[0].concept;
		// columns
		columns.push({
					header : i18n.get("headers.name"),
					dataIndex : 'name',
					width : 100
				});
		columns.push({
					header : i18n.get("headers.description"),
					dataIndex : 'description',
					width : 120
				});

		// fields
		fields.push({
					name : 'name',
					type : 'string'
				});
		fields.push({
					name : 'description',
					type : 'string'
				});

		for (var i = 0; i < conceptAsTemplate.properties.length; i++) {
			var property = conceptAsTemplate.properties[i];
			columns.push({
						header : property.name,
						dataIndex : property.name,
						width : 80
					});

			fields.push({
						name : property.name,
						type : 'string'
					});
		}
	}

	var reader = new Ext.data.JsonReader({
				fields : fields,
				idProperty : 'columnAlias'
			});

	this.grid = new Ext.grid.GridPanel({
		store : new Ext.data.GroupingStore({
					idProperty : 'id',
					fields : fields,
					autoload : false,
					groupField : false,
					reader : reader,
					sortInfo : {
						field : 'columnAlias',
						direction : 'ASC'
					},
					remoteSort : false
				}),
		cm : new Ext.grid.ColumnModel({
					columns : columns
				}),
		view : new Ext.grid.GroupingView({
			groupTextTpl : '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})',
			autoFill : true,
			forceFit : true

		}),
		layout : 'fit'
	});

	this.layout = "fit";
	this.items = [this.grid];

	sitools.user.component.columnsDefinition.superclass.constructor.call(this);
};

Ext.extend(sitools.user.component.columnsDefinition, Ext.Panel, {
	componentType : "defi",
	_getSettings : function() {
		var colModel = [];
		return {
			colModel : extColModelToJsonColModel(this.grid.getColumnModel().config),
			datasetName : this.datasetName,
			preferencesPath : this.preferencesPath,
			preferencesFileName : this.preferencesFileName
		};
	},

	/**
	 * overrides onRender method : Adds records in the store for each columns.
	 */
	onRender : function() {
		sitools.user.component.columnsDefinition.superclass.onRender.apply(
				this, arguments);

		var store = this.grid.getStore();
		var concepts, record;
		Ext.each(this.datasetCm, function(column) {
			concepts = this.getConcepts(column);
			if (!Ext.isEmpty(concepts)) {
				Ext.each(concepts, function(concept) {
							var rec = {
								columnAlias : column.columnAlias,
								unit : column.unit && column.unit.label
							};
							rec = Ext.apply(rec, {
										id : concept.id,
										name : concept.name,
										description : concept.description
									});

							for (var j = 0; j < concept.properties.length; j++) {
								var property = concept.properties[j];
								rec[property.name] = property.value;
							}
							record = new Ext.data.Record(rec);
							store.add(record);
						});
			} else {
				var rec = {
					columnAlias : column.columnAlias
				};
				record = new Ext.data.Record(rec);
				store.add(record);
			}

		}, this);
	},

	/**
	 * @param {Ext.grid.Column}
	 *            column
	 * @return {Array} the concepts of the column
	 */
	getConcepts : function(column) {
		if (Ext.isEmpty(this.dictionaryMappings)) {
			return;

		}
		var mapping = this.dictionaryMappings.mapping;
		var concepts = [], map;
		for (var i = 0; i < mapping.length; i++) {
			map = mapping[i];
			if (column.columnAlias == map.columnAlias) {
				concepts.push(map.concept);
			}
		}
		return concepts;
	},
	/**
	 * Enable or disable grouping View
	 */
	toggleGroup : function() {
		var store = this.grid.getStore();
		if (store.groupField === false) {
			store.groupBy("columnAlias");
			var indexColumnAlias = this.grid.getColumnModel()
					.findColumnIndex("columnAlias");
			this.grid.getColumnModel().setHidden(indexColumnAlias, true);
			this.grid.getView().enableGrouping = true;
			this.grid.getView().refresh();
		} else {
			store.clearGrouping();
			this.grid.getColumnModel().setHidden(0, false);
		}
	},
	/**
	 * Method called when trying to show this component with fixed navigation
	 * 
	 * @param {sitools.user.component.columnsDefinition}
	 *            me the semantic view
	 * @param {}
	 *            config config options
	 * @returns
	 */
	showMeInFixedNav : function(me, config) {
		Ext.apply(config.windowSettings, {
					width : 400,
					height : 400
				});
		SitoolsDesk.openModalWindow(me, config);
	}

});

Ext.reg('sitools.user.component.columnsDefinition',
		sitools.user.component.columnsDefinition);
