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
/*global Ext, sitools, i18n,document */
Ext.namespace("sitools.user.utils");

/**
 * A static method to transform a parameter to a sitools component.
 * @static
 * @param {} parameter the parameter as stored in the formParameter Model
 * @param {string} dataUrl the Url to request eventual data 
 * @param {string} formId the main formId
 * @param {} datasetCm the dataset Column model 
 * @param {string} context should be dataset or project. 
 *  @param {formComponentsPanel} the parent form
 * @return {Ext.Container} the container to include into form
 */
Ext.define('sitools.user.utils.DatasetUtils', {
    singleton : true,
    
    openDataset : function (url, componentConfig) {
    	Ext.Ajax.request({
    		method : "GET", 
    		url : url, 
    		success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                var dataset = Json.dataset;
	            var javascriptObject;
	            
	            var windowConfig = {
	                datasetName : dataset.name, 
	                saveToolbar : true, 
	                toolbarItems : []
	            };
	            
                javascriptObject = Desktop.getNavMode().getDatasetOpenMode(dataset);
            
                var datasetViewComponent  = Ext.create(javascriptObject);
                datasetViewComponent.create(Desktop.getApplication());
                
                Ext.apply(windowConfig, {
                	winWidth : 900, 
                	winHeight : 400,
                	title : i18n.get('label.dataTitle') + " : " + dataset.name, 
                	id : dataset.id, 
                	iconCls : "dataviews"
                });
                
                componentConfig.dataset = dataset;
                
//                Ext.apply(dataset, extraCmpConfig);
//                datasetViewComponent.init(componentConfig, windowConfig);
                
                var sitoolsController = Desktop.getApplication().getController('core.SitoolsController'); 
            	sitoolsController.openComponent(javascriptObject, componentConfig, windowConfig);
    		}
    	});
    },
    
    openDefinition : function (url) {
    	
    	Ext.Ajax.request({
    		method : "GET", 
    		url : url, 
    		success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                
                var dataset = Json.dataset;
	            
	            var columnDefinition  = Ext.create("sitools.user.component.datasets.columnsDefinition.ColumnsDefinition");
	            columnDefinition.create(Desktop.getApplication());
	            var configService = {
	            		datasetId : dataset.id,
	            		datasetDescription : dataset.description,
	            		datasetCm : dataset.columnModel,
	            		datasetName : dataset.name,
	            		dictionaryMappings : dataset.dictionaryMappings,
	            		preferencesPath : "/" + dataset.name,
	            		preferencesFileName : "semantic"
	            };
	            columnDefinition.init(configService);
    		}
    	});
    	
    },
    
    /**
     * A method call when click on dataset Icon. Request the dataset, and open a window depending on type
     * 
     * @static
     * @param {string} url the url to request the dataset
     * @param {string} type the type of the component.
     * @param {} extraCmpConfig an extra config to apply to the component.
     */
    clickDatasetIcone : function (url, type, extraCmpConfig) {
    	Ext.Ajax.request({
    		method : "GET", 
    		url : url, 
    		success : function (ret) {
                var Json = Ext.decode(ret.responseText);
                
                var dataset = Json.dataset;
	            var componentCfg, javascriptObject;
	            var windowConfig = {
	                datasetName : dataset.name, 
	                type : type, 
	                saveToolbar : true, 
	                toolbarItems : []
	            };
                switch (type) {
                
				case "desc" : 
					Ext.apply(windowConfig, {
						title : i18n.get('label.description') + " : " + dataset.name, 
						id : "desc" + dataset.id, 
						saveToolbar : false, 
						iconCls : "version"
					});
					
//    					componentCfg = {
//    						autoScroll : true,
//    						html : dataset.descriptionHTML
//    					};
                    
					Ext.applyIf(dataset, extraCmpConfig);
					
                    javascriptObject = Ext.panel.Panel;
	                var descriptionViewComponent  = Ext.create(javascriptObject);
	                descriptionViewComponent.create(Desktop.getApplication());
                    
                    descriptionViewComponent.init(dataset, windowConfig);
                    
//    					SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);
					
					break;
					
				case "data" : 
					
                    javascriptObject = Desktop.getNavMode().getDatasetOpenMode(dataset);
                    var dataViewComponent  = Ext.create(javascriptObject);
                    dataViewComponent.create(Desktop.getApplication());
                
	                Ext.apply(windowConfig, {
	                    winWidth : 900, 
	                    winHeight : 400,
                        title : i18n.get('label.dataTitle') + " : " + dataset.name, 
                        id : type + dataset.id, 
                        iconCls : "dataviews"
	                });
                    
	                var componentConfig = {
                		dataset : dataset
	                };
	                Ext.applyIf(componentConfig, extraCmpConfig);
	                
	                var sitoolsController = Desktop.getApplication().getController	('core.SitoolsController'); 
	            	sitoolsController.openComponent(javascriptObject, componentConfig, windowConfig);
	                
					break;
				case "forms" : 
		            var menuForms = new Ext.menu.Menu();
		            Ext.Ajax.request({
						method : "GET", 
						url : dataset.sitoolsAttachementForUsers + "/forms", 
						success : function (ret) {
//							try {
								var Json = Ext.decode(ret.responseText);
								if (! Json.success) {
									throw Json.message;
								}
								if (Json.total === 0) {
									throw i18n.get('label.noForms');
								}
                                
				                javascriptObject = Desktop.getNavMode().getFormOpenMode();
                                 var formViewComponent  = Ext.create(javascriptObject);
                                formViewComponent.create(Desktop.getApplication());
                                
								if (Json.total == 1) {
						            var form = Json.data[0];
						            Ext.apply(windowConfig, {
						                title : i18n.get('label.forms') + " : " + dataset.name + "." + form.name, 
						                iconCls : "forms"
						            });
						            
					                Ext.apply(windowConfig, {
					                    id : type + dataset.id + form.id
					                });
                                    
					                componentCfg = {
					                    dataUrl : dataset.sitoolsAttachementForUsers,
					                    dataset : dataset, 
					                    formId : form.id,
					                    formName : form.name,
					                    formParameters : form.parameters,
					                    formZones : form.zones,
					                    formWidth : form.width,
					                    formHeight : form.height, 
					                    formCss : form.css, 
				                        preferencesPath : "/" + dataset.name + "/forms", 
				                        preferencesFileName : form.name
					                };
                                    
					                Ext.applyIf(dataset, extraCmpConfig);
					                Ext.applyIf(dataset, componentCfg);
                                    
                                    formViewComponent.init(dataset, windowConfig);
//    									SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);

				                }
								else {
									
									var handler = null;
									Ext.each(Json.data, function (form) {
										handler = function (form, dataset) {
											Ext.apply(windowConfig, {
												title : i18n.get('label.forms') + " : " + dataset.name + "." + form.name, 
												iconCls : "forms"
								            });
								
							                Ext.apply(windowConfig, {
							                    id : type + dataset.id + form.id
							                });
							                componentCfg = {
							                    dataUrl : dataset.sitoolsAttachementForUsers,
							                    formId : form.id,
							                    formName : form.name,
							                    formParameters : form.parameters,
							                    formWidth : form.width,
							                    formHeight : form.height, 
							                    formCss : form.css, 
							                    dataset : dataset
							                };
                                            
							                Ext.applyIf(dataset, extraCmpConfig);
                                            Ext.applyIf(dataset, componentCfg);
                                            
                                            formViewComponent.init(dataset, windowConfig);
//    											SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);
										};
										menuForms.add({
											text : form.name, 
											handler : function () {
												handler(form, dataset);
											}, 
											icon : loadUrl.get('APP_URL') + "/common/res/images/icons/tree_forms.png"
										});
						                
									}, this);
									menuForms.showAt(Ext.EventObject.xy);
								}
//							}
//							catch (err) {
//                                popupMessage(i18n.get('label.information'), i18n.get(err), null, 'x-icon-information');
//							}
						}
		            });

					break;
				case "feeds" : 
		            var menuFeeds = new Ext.menu.Menu();
		            Ext.Ajax.request({
						method : "GET", 
						url : dataset.sitoolsAttachementForUsers + "/feeds", 
						success : function (ret) {
							try {
								var Json = Ext.decode(ret.responseText);
								if (! Json.success) {
									throw Json.message;
								}
								if (Json.total === 0) {
									throw i18n.get('label.noFeeds');
								}
				                javascriptObject = sitools.widget.FeedGridFlux;
								if (Json.total == 1) {
						            var feed = Json.data[0];
						            Ext.apply(windowConfig, {
						                title : i18n.get('label.feeds') + " : (" + dataset.name + ") " + feed.title, 
						                id : type + dataset.id + feed.id, 
						                iconCls : "feedsModule"
						            });
						
					                componentCfg = {
					                    datasetId : dataset.id,
					                    urlFeed : dataset.sitoolsAttachementForUsers + "/clientFeeds/" + feed.name,
					                    feedType : feed.feedType, 
					                    datasetName : dataset.name,
					                    feedSource : feed.feedSource,
					                    autoLoad : true
					                };
						            Ext.applyIf(componentCfg, extraCmpConfig);
									SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);

				                }
								else {
									var handler = null;
									Ext.each(Json.data, function (feed) {
										handler = function (feed, dataset) {
											Ext.apply(windowConfig, {
												title : i18n.get('label.feeds') + " : (" + dataset.name + ") " + feed.title, 
												id : type + dataset.id + feed.id, 
												iconCls : "feedsModule"
								            });
								
							                
							                componentCfg = {
							                    datasetId : dataset.id,
							                    urlFeed : dataset.sitoolsAttachementForUsers + "/clientFeeds/" + feed.name,
							                    feedType : feed.feedType, 
							                    datasetName : dataset.name,
							                    feedSource : feed.feedSource,
							                    autoLoad : true
							                };
							                Ext.applyIf(componentCfg, extraCmpConfig);
											SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);
										};
										menuFeeds.addItem({
											text : feed.name, 
											handler : function () {
												handler(feed, dataset);
											}, 
											icon : loadUrl.get('APP_URL') + "/common/res/images/icons/rss.png"
										});
						                
									}, this);
									menuFeeds.showAt(Ext.EventObject.xy);
								}
					            
				
								
							}
							catch (err) {
								var tmp = new Ext.ux.Notification({
						            iconCls : 'x-icon-information',
						            title : i18n.get('label.information'),
						            html : i18n.get(err),
						            autoDestroy : true,
						            hideDelay : 1000
						        }).show(document);
							}
						}
		            });

					break;
				case "defi" : 
		            Ext.apply(windowConfig, {
		                title : i18n.get('label.definitionTitle') + " : " + dataset.name, 
		                id : type + dataset.id, 
		                iconCls : "semantic"
		            });
		
	                javascriptObject = sitools.user.component.columnsDefinition;
	                
	                componentCfg = {
	                    datasetId : dataset.id,
	                    datasetCm : dataset.columnModel, 
	                    datasetName : dataset.name,
                        dictionaryMappings : dataset.dictionaryMappings, 
                        preferencesPath : "/" + dataset.name, 
                        preferencesFileName : "semantic"
	                };
	                Ext.applyIf(componentCfg, extraCmpConfig);
					SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);

					break;
				case "openSearch" : 
		            Ext.Ajax.request({
						method : "GET", 
						url : dataset.sitoolsAttachementForUsers + "/opensearch.xml", 
						success : function (ret) {
                            var xml = ret.responseXML;
                            var dq = Ext.DomQuery;
                            // check if there is a success node
                            // in the xml
                            var success = dq.selectNode('OpenSearchDescription ', xml);
							if (!success) {
								var tmp = new Ext.ux.Notification({
						            iconCls : 'x-icon-information',
						            title : i18n.get('label.information'),
						            html : i18n.get("label.noOpenSearch"),
						            autoDestroy : true,
						            hideDelay : 1000
						        }).show(document);
								return;
							}
							
							Ext.apply(windowConfig, {
				                title : i18n.get('label.opensearch') + " : " + dataset.name, 
				                id : type + dataset.id, 
				                iconCls : "openSearch"
				            });
				
			                javascriptObject = sitools.user.component.datasetOpensearch;
			                
			                componentCfg = {
			                    datasetId : dataset.id,
			                    dataUrl : dataset.sitoolsAttachementForUsers, 
			                    datasetName : dataset.name, 
		                        preferencesPath : "/" + dataset.name, 
		                        preferencesFileName : "openSearch"
			                };
			                Ext.applyIf(componentCfg, extraCmpConfig);
							SitoolsDesk.addDesktopWindow(windowConfig, componentCfg, javascriptObject);
                            
                        }
		            });

					break;
				}
    		}, 
    		failure : alertFailure
    	});
    }
});
