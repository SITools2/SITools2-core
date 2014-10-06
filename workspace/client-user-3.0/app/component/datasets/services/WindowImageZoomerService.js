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
/*global Ext, sitools, ID, i18n, document, showResponse, alertFailure, LOCALE, ImageChooser, loadUrl, extColModelToStorage, SitoolsDesk, sql2ext
 */

Ext.namespace('sitools.user.component.datasets.services');

/**
 * A specific window to display preview Images in user desktop.
 * 
 * @class sitools.user.component.datasets.services.WindowImageZoomerService
 * @config {boolean} resizeImage true to resize the image according to the
 *         desktop, image ratio is also keeped. false to keep the image size
 *         with scrollbars if needed. Default to false
 * @extends Ext.Window
 */
Ext.define('sitools.user.component.datasets.services.WindowImageZoomerService', {
    extend : 'sitools.user.core.Component',
    controllers : [],
    alias : 'widget.windowImageZoomerSerice',
    
    statics : {
        getParameters : function () {
            return [{
                jsObj : "Ext.form.ComboBox",
                config : {
                    fieldLabel : i18n.get('headers.previewUrl'),
                    width : 200,
                    typeAhead : true,
                    queryMode : 'local',
                    forceSelection : true,
                    triggerAction : 'all',
                    valueField : 'display',
                    displayField : 'display',
                    value : 'Image',
                    store : Ext.create('Ext.data.ArrayStore', {
                        autoLoad : true,
                        fields : [ 'value', 'display', 'tooltip' ],
                        data : [ [ '', '' ], [ 'Image', 'Image', i18n.get("label.image.tooltip") ], [ 'URL', 'URL', i18n.get("label.url.tooltip") ],
                                [ 'DataSetLink', 'DataSetLink', i18n.get("label.datasetlink.tooltip") ] ]
                    }),
                    listeners : {
                        scope : this,
                        change : function (combo, newValue, oldValue) {
                        }
                    },
                    name : "featureType",
                    id : "featureType"
                }
            }, {
                jsObj : "Ext.form.ComboBox",
                config : {
                    fieldLabel : i18n.get('label.columnImage'),
                    width : 200,
                    typeAhead : true,
                    queryMode : 'local',
                    forceSelection : true,
                    triggerAction : 'all',
                    store : Ext.create('Ext.data.JsonStore', {
                        fields : [ 'columnAlias' ],
                        proxy : {
                            type : 'ajax',  
	                        url : Ext.getCmp("dsFieldParametersPanel").urlDataset,
                            reader : {
                                type : 'json',
		                        root : "dataset.columnModel"
                            }
                        },
                        autoLoad : true,
                        listeners : {
                            load : function (store) {
                                store.add({
                                    'columnAlias' : ""
                                });
                            }

                        }
                    }),
                    valueField : 'columnAlias',
                    displayField : 'columnAlias',
                    listeners : {
                        render : function (c) {
                            Ext.QuickTips.register({
                                target : c,
                                text : i18n.get('label.columnImageTooltip')
                            });
                        }
                    },
                    name : "columnAlias",
                    id : "columnAlias",
                    value : ""
                }
            }, {
                jsObj : "Ext.form.ComboBox",
                config : {
                    fieldLabel : i18n.get('label.thumbnailColumnImage'),
                    width : 200,
                    typeAhead : true,
                    queryMode : 'local',
                    forceSelection : true,
                    triggerAction : 'all',
                    store : Ext.create('Ext.data.JsonStore', {
                        fields : [ 'columnAlias' ],
                        proxy : {
                            type : 'ajax',
	                        url : Ext.getCmp("dsFieldParametersPanel").urlDataset,
                            reader : {
                                type : 'json',
                                root : "dataset.columnModel"
                            }
                        },
                        autoLoad : true,
                        listeners : {
                            load : function (store) {
                                store.add({
                                    'columnAlias' : ""
                                });
                            }

                        }
                    }),
                    valueField : 'columnAlias',
                    displayField : 'columnAlias',
                    listeners : {
                        render : function (c) {
                            Ext.QuickTips.register({
                                target : c,
                                text : i18n.get('label.thumbnailColumnImageTooltip')
                            });
                        }
                    },
                    name : "thumbnailColumnImage",
                    id : "thumbnailColumnImage",
                    value : ""
                }
            }, {
                jsObj : "Ext.form.TextField",
                config : {
                    fieldLabel : i18n.get("label.sizeLimitWidth"),
                    allowBlank : false,
                    width : 200,
                    listeners : {
                        render : function (c) {
                            Ext.QuickTips.register({
                                target : c,
                                text : i18n.get('label.sizeLimitWidthTooltip')
                            });
                        }
                    },
                    name : "sizeLimitWidth",
                    value : "500"
                }
            }, {
                jsObj : "Ext.form.TextField",
                config : {
                    fieldLabel : i18n.get('label.sizeLimitHeight'),
                    allowBlank : false,
                    width : 200,
                    listeners : {
                        render : function (c) {
                            Ext.QuickTips.register({
                                target : c,
                                text : i18n.get('label.sizeLimitHeightTooltip')
                            });
                        }
                    },
                    name : "sizeLimitHeight",
                    value : "500"
                }
            }, {
                jsObj : "Ext.form.TextField",
                config : {
                    fieldLabel : i18n.get('label.zoomFactor'),
                    allowBlank : false,
                    width : 200,
                    listeners : {
                        render : function (c) {
                            Ext.QuickTips.register({
                                target : c,
                                text : i18n.get('label.zoomFactorTooltip')
                            });
                        }
                    },
                    name : "zoomFactor",
                    value : "20"
                }
            }, {
                jsObj : "Ext.form.TextField",
                config : {
                    fieldLabel : i18n.get('label.maxZoom'),
                    allowBlank : false,
                    width : 200,
                    listeners : {
                        render : function (c) {
                            Ext.QuickTips.register({
                                target : c,
                                text : i18n.get('label.maxZoomTooltip')
                            });
                        }
                    },
                    name : "maxZoom",
                    value : "10000"
                }
            }];
        },
        
        getDefaultParameters : function () {
		    return [ {
		        name : "featureType",
		        value : "Image"
		    }, {
		        name : "columnAlias",
		        value : ""
		    }, {
		        name : "sizeLimitWidth",
		        value : "500"
		    }, {
		        name : "sizeLimitHeight",
		        value : "500"
		    }, {
		        name : "zoomFactor",
		        value : "20"
		    }, {
		        name : "maxZoom",
		        value : "10000"
		    } ];
		}
    },
    
    executeAsService : function (config) {
         if (Ext.isEmpty(config.parameters)) {
            config.parameters = sitools.user.view.component.datasets.services.WindowImageZoomerView.getDefaultParameters();
        }
        
        var windowZoomer = Ext.create("sitools.user.view.component.datasets.services.WindowImageZoomerView", config);
        windowZoomer.show();
    }
});