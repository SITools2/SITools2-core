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
 * global Ext, sitools, showResponse, i18n, extColModelToJsonColModel, loadUrl,
 * alertFailure
 */

Ext.namespace('sitools.user.component');

// sitools.component.users.datasets.columnsDefinition = function (config) {
/**
 * A Panel that displays a grid with columns definition.
 * 
 * @cfg {string} datasetId The datasetId
 * @class sitools.user.component.DatasetOverview
 * @extends Ext.Panel
 */
sitools.user.component.DatasetOverview = function(config) {
	this.DEFAULT_HEIGHT = 600;
	this.DEFAULT_WIDTH = 800;
	this.DEFAULT_WIDTH_FORMS_PANEL = 400;
	this.DEFAULT_HEIGHT_SEMANTIC_PANEL = 200;
	this.DEFAULT_WIDTH_EAST_PANEL = 200;

	Ext.apply(this, config);

	this.formsTabPanel = new Ext.TabPanel({
				items : []
			});

	this.formsContainerPanel = new Ext.Panel({
				title : "forms",
				layout : "fit",
				region : "west",
				collapsible : true,
				split : true,
				// customFormWidth existe quand des préférences utilisateur sont
				// sauvegardés
				width : (this.customFormWidth)
						? this.customFormWidth
						: this.DEFAULT_WIDTH_FORMS_PANEL,
				items : [this.formsTabPanel]
			});

	this.semanticPanel = new Ext.Panel({
				title : i18n.get('label.semantic'),
				layout : "fit",
				region : "south",
				html : " ",
				collapsible : true,
				split : true,
				hidden : true,
				height : this.DEFAULT_HEIGHT_SEMANTIC_PANEL
			});

	this.descriptionPanel = new Ext.Panel({
				title : i18n.get('label.description'),
				autoScroll : true,
				layout : "fit",
				region : "center",
				html : " "
			});

	this.mainPanel = new Ext.Panel({
				layout : "fit",
				region : "center",
				border : false,
				items : [{
							layout : "border",
							items : [this.descriptionPanel, this.semanticPanel]
						}]
			});

	this.eastPanel = new Ext.Panel({
				layout : "fit",
				region : "east",
				hidden : true,
				collapsible : true,
				split : true,
				items : [],
				width : this.DEFAULT_WIDTH_EAST_PANEL
			});

	sitools.user.component.DatasetOverview.superclass.constructor.call(this, {
				layout : "border",
				items : [this.formsContainerPanel, this.mainPanel,
						this.eastPanel],
				listeners : {
					scope : this,
					beforerender : function() {
						this.loadDataset();
						return true;
					},
					datasetLoaded : function() {
						this.loadForms();
					},
					formsLoaded : function() {
						this.loadSemantic();
					},
					semanticLoaded : function() {
						this.loadDescription();
					},
					descriptionLoaded : function() {
						// this.unMask();
						return;
					}
				}
			});
};

Ext.extend(sitools.user.component.DatasetOverview, Ext.Panel, {
	alias : 'sitools.user.component.DatasetOverview',
	componentType : "datasetOverview",
	_getSettings : function() {
		var colModel = {};
		var globalSettings = {};

		if (this.formsTabPanel.items.length > 0) {
			globalSettings.formsActivePanel = this.formsTabPanel.items
					.findIndex('id', this.formsTabPanel.activeTab.id);
			globalSettings.formsPanelWidth = this.formsTabPanel.getWidth();

			globalSettings = Ext.apply(globalSettings,
					this.formsTabPanel.activeTab._getSettings());
		} else if (this.dataview) {
			globalSettings = Ext.apply(globalSettings, this.dataview
							._getSettings());
		}

		return Ext.apply(globalSettings, {
			sitoolsAttachementForUsers : this.sitoolsAttachementForUsers,
			datasetUrl : this.sitoolsAttachementForUsers,
			datasetName : this.dataset.name,
			datasetId : this.dataset.id,
			// formsPanelWidth : 50,
			formsPanelCollapsed : true
				// formsActivePanel : 1
			});
	},
	loadDataset : function() {
		Ext.Ajax.request({
					method : "GET",
					scope : this,
					url : this.sitoolsAttachementForUsers,
					success : function(response) {
						var json = Ext.decode(response.responseText);
						if (!json.success) {
							Ext.Msg
									.alert(
											i18n.get('label.error'),
											i18n
													.get('warning.datasetRequestError'));
							return false;
						} else {
							this.dataset = json.dataset;
							this.fireEvent("datasetLoaded");
						}

					},
					failure : alertFailure
				});
	},
	loadForms : function() {
		Ext.Ajax.request({
			method : "GET",
			scope : this,
			url : this.dataset.sitoolsAttachementForUsers + '/forms',
			success : function(response) {
				var json = Ext.decode(response.responseText);
				var eventToFire = 'formsLoaded';

				if (!json.success) {
					Ext.Msg.alert(i18n.get('label.error'), i18n
									.get('warning.datasetRequestError'));
					return false;
				} else {
					this.forms = json.data;
					if (!Ext.isEmpty(this.forms)) {
						// If forms exist : fire loadSemantic event
						var eventToFire = 'semanticLoaded';
						var activeFormExists = false;
						Ext.each(this.forms, function(form) {
							if (form.id == this.formId) {
								activeFormExists = true;
							}
							var panel = new sitools.user.component.forms.mainContainer(
									{
										title : form.name,
										dataUrl : this.dataset.sitoolsAttachementForUsers,
										dataset : this.dataset,
										formId : form.id,
										id : form.id,
										formName : form.name,
										formParameters : form.parameters,
										formZones : form.zones,
										formWidth : form.width,
										formHeight : form.height,
										formCss : form.css,
										preferencesPath : "/"
												+ this.dataset.name + "/forms",
										preferencesFileName : form.name,
										searchAction : this.searchAction,
										scope : this
									});
							this.formsTabPanel.add(panel);
						}, this);
						this.formsTabPanel.doLayout();
						if (this.formId) {
							if (activeFormExists) {
								this.formsTabPanel.setActiveTab(this.formId);
							} else {
								Ext.Msg.show({
									buttons : Ext.MessageBox.OK,
									icon : Ext.MessageBox.INFO,
									modal : true,
									closable : false,
									title : i18n.get('label.info'),
									msg : i18n
											.get('label.cannotFindFormIdDisplayFirstOne')
								});
								this.formsTabPanel.setActiveTab(0);
							}
						} else {
							this.formsTabPanel.setActiveTab(0);
						}
					} else {
						this.semanticPanel.show();

						var jsObj = eval(this.dataset.datasetView.jsObject);
						var componentCfg = {
							dataUrl : this.dataset.sitoolsAttachementForUsers,
							datasetId : this.dataset.id,
							datasetCm : this.dataset.columnModel,
							datasetName : this.dataset.name,
							dictionaryMappings : this.dataset.dictionaryMappings,
							datasetViewConfig : this.dataset.datasetViewConfig,
							preferencesPath : "/" + this.dataset.name,
							preferencesFileName : "datasetView"
						};
						this.dataview = new jsObj(componentCfg);
						this.formsContainerPanel.removeAll();
						this.formsContainerPanel.hide();
						this.eastPanel.setVisible(true);
						this.eastPanel.add(this.mainPanel.items.items);

						this.mainPanel.removeAll();
						this.mainPanel.add(this.dataview);
						this.doLayout();

					}
					this.mainPanel.doLayout();
					this.fireEvent(eventToFire);
				}
			},
			failure : alertFailure
		});
	},
	loadSemantic : function() {
		var panel = new sitools.user.component.columnsDefinition({
					datasetId : this.dataset.id,
					datasetCm : this.dataset.columnModel,
					datasetName : this.dataset.name,
					dictionaryMappings : this.dataset.dictionaryMappings
				});
		this.semanticPanel.removeAll();
		this.semanticPanel.add(panel);
		this.semanticPanel.doLayout();
		this.fireEvent('semanticLoaded');
	},
	loadDescription : function() {
		var contentTarget = this.descriptionPanel.getContentTarget();
		if (!Ext.isEmpty(this.dataset.descriptionHTML)) {
			contentTarget.update(Ext.DomHelper
					.markup(this.dataset.descriptionHTML));
		}
		this.fireEvent("descriptionLoaded");
	},
	searchAction : function(formParams, dataset, scope) {
		var jsObj = eval(dataset.datasetView.jsObject);
		var componentCfg = {
			dataUrl : dataset.sitoolsAttachementForUsers,
			datasetId : dataset.id,
			datasetCm : dataset.columnModel,
			datasetName : dataset.name,
			formParams : formParams,
			dictionaryMappings : dataset.dictionaryMappings,
			datasetViewConfig : dataset.datasetViewConfig,
			preferencesPath : "/" + dataset.name,
			preferencesFileName : "datasetView",
			userPreference : {
				datasetName : this.dataset.name,
				colModel : extColModelToJsonColModel(this.dataset.columnModel),
				datasetView : "datasetOverview",
				datasetUrl : this.sitoolsAttachementForUsers,
				dictionaryMappings : this.dataset.dictionaryMappings
			}
		};
		scope.dataview = new jsObj(componentCfg);
		// scope.formsContainerPanel.collapse();
		// scope.semanticPanel.collapse();
		scope.mainPanel.removeAll();
		scope.mainPanel.add(scope.dataview);
		scope.doLayout();

	}
});

