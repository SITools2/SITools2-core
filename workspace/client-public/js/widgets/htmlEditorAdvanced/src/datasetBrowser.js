/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
/* global Ext, sitools, i18n, loadUrl */

Ext.namespace('sitools.widget.HtmlEditor');
/**
 * datasetLink widget
 * 
 * @class sitools.widget.HtmlEditor.datasetBrowser
 * @extends Ext.util.Observable
 */
sitools.widget.HtmlEditor.datasetBrowser = function(config) {
	this.datasets = [];
	this.browseField = config.field;
	/**
	 * INDEX JPB var projectId = Ext.util.Cookies.get('projectId'); if
	 * (Ext.isEmpty(projectId)){ Ext.Msg.alert(i18n.get ('warning.noProject'));
	 * return; }
	 */

	// var projectId = projectGlobal.getProjectId();
	var projectAttachment = projectGlobal.sitoolsAttachementForUsers;
	var treeUtils = commonTreeUtils;

	var conn = Ext.Ajax;
	conn.request({
		url : projectAttachment + '/datasets?media=json',
		method : 'GET',
		scriptTag : true,
		scope : this,
		success : function(response) {
			if (!showResponse(response, false)) {
				return;
			}
			var config = Ext.decode(response.responseText);
			var i = 0;
			Ext.each(config.data, function(dataset) {
				if (dataset.authorized !== "false") {
					this.datasets.push({
						text : dataset.name,
						listeners : {
							scope : this,
							beforeexpand : function(node) {
								node.removeAll(true);
								if (dataset.status != "ACTIVE") {
									var notify = new Ext.ux.Notification({
												iconCls : 'x-icon-information',
												title : i18n
														.get('label.information'),
												html : i18n
														.get('warning.wrongStatus'),
												autoDestroy : true,
												hideDelay : 1000
											});
									notify.show(document);
									return true;
								}
								conn.request({
									// url : '/sitools/datasets/' + dataset.id +
									// '?media=json',
									url : dataset.url + '?media=json',
									scope : this,
									success : function(response) {
										var dataset = Ext.decode(response.responseText).dataset;
										commonTreeUtils.addShowData(node,dataset);
										SitoolsDesk.navProfile.manageDatasetExplorerShowDefinitionAndForms(commonTreeUtils, node, dataset);

										conn.request({
											url : dataset.sitoolsAttachementForUsers
													+ "/opensearch.xml",
											scope : this,
											success : function(response) {
												var xml = response.responseXML;
												var dq = Ext.DomQuery;
												// check if there is a success
												// node
												// in the xml
												var success = dq
														.selectNode(
																'OpenSearchDescription ',
																xml);

												if (success !== undefined) {
//													commonTreeUtils.addOpensearch(node, dataset);
												}
												return true;
											}
										});
//										commonTreeUtils.addFeeds(node, dataset);
									}
								});
								return true;
							}
						},
						children : [{
									text : ""
								}]
					});
				} else {
					this.datasets.push({
								text : dataset.name,
								leaf : false,
								icon : loadUrl.get('APP_URL')
										+ "/common/res/images/icons/cadenas.png",
								authorized : false
							});
				}
				i++;
			}, this);
			this.fireEvent('datasetLoaded');
		},
		failure : function() {
			Ext.Msg.alert(i18n.get('label.warning'), i18n.get('warning.noProject'));
		}
	});
	this.rootNode = new Ext.tree.AsyncTreeNode({
				nodeType : 'async',
				text : 'dataSets',
				leaf : false,
				draggable : false,
				children : this.datasets,
				expanded : false,
				listeners : {
					scope : this,
					beforeexpand : function() {
						this.rootNode.removeAll(true);
						this.rootNode.appendChild(this.datasets);
						return true;
					}
				}
			});
			
	sitools.widget.HtmlEditor.datasetBrowser.superclass.constructor.call(this,
			Ext.apply({
						expanded : true,
						useArrows : true,
						autoScroll : true,
						animate : true,
						loader : new Ext.tree.TreeLoader(),
						root : this.rootNode,
						rootVisible : true,
						layout : 'fit',
						listeners : {
							beforeload : function(node) {
								return node.isRoot || Ext.isDefined(node.attributes.children);
							},
							datasetLoaded : function() {
								this.getRootNode().expand();
							}
						},
						bbar : [{
							xtype : 'button',
							text : i18n.get('label.select'),
							scope : this,
							handler : this.onValidate
						}]
					}));

};

Ext.extend(sitools.widget.HtmlEditor.datasetBrowser, Ext.tree.TreePanel, {
			/**
			 * method called when trying to save preference
			 * 
			 * @returns
			 */
			_getSettings : function() {
				return {
					preferencesPath : "/modules",
					preferencesFileName : this.id
				};
			},

			onValidate : function() {
				var selNode = this.selModel.getSelectedNode();
				if (selNode.isLeaf() && selNode.attributes.type != "defi"){
					var urlLink, displayValue;
					
						urlLink = selNode.attributes.dataUrl;
						displayValue = selNode.attributes.winTitle;
						this.browseField.setValue(displayValue);
					if (selNode.attributes.type == "data"){
						this.browseField.dataLinkComponent = String.format("<a href='#' onclick='parent.sitools.user.component.dataviews.dataviewUtils.showDetailsData(\"\",\"\",\"{0}\"); return false;'>", urlLink);
					}
					else if (selNode.attributes.type == "form"){
						this.browseField.dataLinkComponent = String.format("<a href='#' onclick='parent.SitoolsDesk.showFormFromEditor(\"{0}\\/forms\"); return false;'>", urlLink);
					}
					this.ownerCt.close();
				}
				else {
					Ext.Msg.alert(i18n.get('label.info'), i18n.get('label.selectNode'));
				}
			}

		});

Ext.reg('sitools.widget.HtmlEditor.datasetBrowser',
		sitools.widget.HtmlEditor.datasetBrowser);
