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
 * global Ext, sitools, i18n, window, userLogin, projectGlobal, SitoolsDesk,
 * DEFAULT_PREFERENCES_FOLDER, loadUrl, onRequestFeedException
 */
/*
 * @include "../viewDataDetail/simpleViewDataDetails.js" @include
 * "../viewDataDetail/viewDataDetail.js"
 */

Ext.namespace('sitools.user.component');

/**
 * Component used to display opensearch results param : url : the url of the RSS
 * feed
 * 
 * @cfg {string} urlFeed the url of the feed
 * @cfg {string} input The input value to set
 * @cfg {boolean} autoLoad If the store needs to be loaded on start
 * @cfg {string} dataUrl the url of the dataset
 * @cfg {boolean} pagging true to activate the pagging, false otherwise
 * @cfg {} dsInfo informations about the dataset
 * @cfg {} exceptionHttpHandler the handler for httpProxy errors
 * @requires sitools.user.component.simpleViewDataDetail
 * @requires sitools.user.component.viewDataDetail
 * @class sitools.user.component.openSearchResultFeed
 * @extends Ext.grid.GridPanel
 */
sitools.user.component.openSearchResultFeed = function(config) {
	// sitools.component.users.datasets.openSearchResultFeed = function (config)
	// {
	this.pageSize = 10;
	var urlParam = config.urlFeed;
	this.input = config.input;
	this.uriRecords = config.dataUrl + "/records";
	var pagging = config.pagging;
	var url = (urlParam === undefined) ? "/tmp" : urlParam;

	var exceptionHttpHandler = (Ext.isEmpty(config.exceptionHttpHandler))
			? onRequestFeedException
			: config.exceptionHttpHandler;

	this.httpProxy = new Ext.data.HttpProxy({
				url : url,
				restful : true,
				listeners : {
					scope : this,
					exception : exceptionHttpHandler
				}
			});

	this.xmlReader = new sitools.component.users.datasets.XmlReader({
				record : 'item',
				totalProperty : 'opensearch:totalResults'
			}, ['title', 'link', 'guid', 'pubDate', 'description']);

	this.store = new Ext.data.Store({
				proxy : this.httpProxy,
				reader : this.xmlReader,
				autoLoad : config.autoLoad,
				paramNames : {
					start : 'start',
					limit : 'rows'
				},
				listeners : {
					scope : this,
					load : function(self, records, index) {
						if (!pagging && !Ext.isEmpty(this.displayNbResults)) {
							this.displayNbResults
									.setText('Total number of results : '
											+ this.store.getTotalCount());
							// this.getBottomToolbar().doLayout();
						}
						return true;
					},
					exception : function(proxy, type, action, options,
							response, arg) {
						var data = Ext.decode(response.responseText);
						if (!data.success) {
							this.input.markInvalid(i18n.get(data.message));
							this.store.removeAll();
						}
						return true;
					}
				}

			});

	this.store.setDefaultSort('pubDate', "DESC");

	// if (config.autoLoad !== null && config.autoLoad !== undefined &&
	// config.autoLoad) {
	// this.store.load();
	// }

	var columns = [{
				id : 'title',
				header : "Title",
				dataIndex : 'title',
				sortable : true,
				renderer : this.formatTitle
			}, {
				id : 'last',
				header : "Date",
				dataIndex : 'pubDate',
				renderer : this.formatDate,
				sortable : true

			}];

	if (pagging) {
		this.bbar = {
			xtype : 'paging',
			pageSize : this.pageSize,
			store : this.store,
			displayInfo : true,
			displayMsg : i18n.get('paging.display'),
			emptyMsg : i18n.get('paging.empty'),
			totalProperty : 'totalCount'
		};
	} else {
		this.displayNbResults = new Ext.form.Label({
					text : 'Total number of results : '
				});
		this.bbar = {
			items : ['->', this.displayNbResults

			]
		};
	}

	function clickOnRow(self, rowIndex, e) {
		e.stopEvent();
		var rec = self.store.getAt(rowIndex);
		var guid = rec.get("guid");
		if (Ext.isEmpty(guid)) {
			Ext.Msg.alert(i18n.get('label.warning'), i18n
							.get('warning.noGuidFieldDefined')
							+ "<br/>"
							+ i18n.get('warning.noPrimaryKeyDefinedOSNotice'));
			return;
		}
		// si on est pas sur le bureau
		if (Ext.isEmpty(window) || Ext.isEmpty(window.SitoolsDesk)) {
			var component = new sitools.user.component.simpleViewDataDetail({
						fromWhere : "openSearch",
						urlDataDetail : guid
					});
			var win = new Ext.Window({
						stateful : false,
						title : i18n.get('label.viewDataDetail'),
						width : 400,
						height : 600,
						shim : false,
						animCollapse : false,
						constrainHeader : true,
						layout : 'fit'
					});
			win.add(component);
			win.show();
		} else {
			var componentCfg = {
				grid : this,
				fromWhere : "openSearch",
				datasetId : config.datasetId,
				datasetUrl : config.dataUrl,
				datasetName : config.datasetName,
				preferencesPath : "/" + config.datasetName,
				preferencesFileName : "dataDetails"
			};
			var jsObj = sitools.user.component.viewDataDetail;

			var windowConfig = {
				id : "dataDetail" + config.datasetId,
				title : i18n.get('label.viewDataDetail') + " : "
						+ config.datasetName,
				datasetName : config.datasetName,
				iconCls : "openSearch",
				saveToolbar : true,
				type : "dataDetail",
				toolbarItems : [{
							iconCls : 'arrow-back',
							handler : function() {
								this.ownerCt.ownerCt.items.items[0]
										.goPrevious();
							}
						}, {
							iconCls : 'arrow-next',
							handler : function() {
								this.ownerCt.ownerCt.items.items[0].goNext();
							}
						}]
			};
			SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, jsObj,
					true);
		}

	}

	sitools.user.component.openSearchResultFeed.superclass.constructor.call(
			this, {
				columns : columns,
				// hideHeaders : true,
				// region : 'center',
				layout : 'fit',
				flex : 1,
				store : this.store,
				loadMask : {
					msg : i18n.get("label.loadingFeed")
				},
				sm : Ext.create('Ext.selection.RowModel',{
							singleSelect : true
						}),
				autoExpandColumn : 'title',
				viewConfig : {
					forceFit : true,
					enableRowBody : true,
					showPreview : true,
					getRowClass : this.applyRowClass
				},
				listeners : {
					rowdblclick : clickOnRow
				}
			});

	this.updateStore = function(url) {
		this.httpProxy.setUrl(url, true);
		this.store.load();
	};

};

Ext.extend(sitools.user.component.openSearchResultFeed, Ext.grid.GridPanel, {
	alias : 'sitools.user.component.openSearchResultFeed',
	componentType : "feeds",
	// within this function "this" is actually the GridView
	applyRowClass : function(record, rowIndex, p, ds) {
		if (this.showPreview) {
			var xf = Ext.util.Format;
			p.body = '<p class=sous-titre-flux>'
					+ xf.ellipsis(xf.stripTags(record.data.description), 200)
					+ '</p>';
			return 'x-grid3-row-expanded';
		}
		return 'x-grid3-row-collapsed';
	},

	formatDate : function(date) {
		if (!date) {
			return '';
		}
		var now = new Date();
		var d = now.clearTime(true);
		if (date instanceof Date) {
			var notime = date.clearTime(true).getTime();
			if (notime == d.getTime()) {
				return 'Today ' + date.dateFormat('g:i a');
			}
			d = d.add('d', -6);
			if (d.getTime() <= notime) {
				return date.dateFormat('D g:i a');
			}
			return date.dateFormat('n/j g:i a');
		} else {
			return date;
		}
	},

	/**
	 * Specific renderer for title Column
	 * 
	 * @param {}
	 *            value
	 * @param {}
	 *            p
	 * @param {Ext.data.Record}
	 *            record
	 * @return {string}
	 */
	formatTitle : function(value, p, record) {
		var link = record.data.link;
		var xf = Ext.util.Format;
		var res = "";
		if (link !== undefined && link !== "") {
			res = String
					.format(
							'<div class="topic"><a href="{0}" title="{1}"><span class="rss_feed_title">{2}</span></a></div>',
							link, value, xf.ellipsis(xf.stripTags(value), 30));
		} else {
			res = String
					.format(
							'<div class="topic"><span class="rss_feed_title">{0}</span></div>',
							xf.ellipsis(xf.stripTags(value), 30));
		}

		return res;
	}

});

