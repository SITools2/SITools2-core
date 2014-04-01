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
/* global Ext, sitools, getDesktop, i18n, viewer */
Ext.ns('sitools.widget');

/**
 * A specific window to display preview Images in user desktop.
 * 
 * @class sitools.user.component.dataviews.services.WindowImageZoomer
 * @config {boolean} resizeImage true to resize the image according to the
 *         desktop, image ratio is also keeped. false to keep the image size
 *         with scrollbars if needed. Default to false
 * @extends Ext.Window
 */
Ext.define('sitools.user.component.dataviews.services.WindowImageZoomer', {
    extend : 'Ext.window.Window',
    alias : 'sitools.user.component.dataviews.services.WindowImageZoomer',

    resizeImage : false,
    maximizable : true,
    modal : true,
    minHeight : 0,
    minWidth : 0,
    padding : '2px 2px 2px 2px',

    initComponent : function (config) {
        viewer.onload = function (self) {
            viewer.toolbarextjs(self);
            viewer.preview(self);
        };

        if (Ext.isEmpty(this.parameters)) {
            this.parameters = sitools.user.component.dataviews.services.WindowImageZoomer.getDefaultParameters();
        }

        Ext.each(this.parameters, function (config) {
            if (!Ext.isEmpty(config.value)) {
                switch (config.name) {
                case "columnAlias":
                    this.columnImage = config.value;
                    break;

                case "thumbnailColumnImage":
                    this.thumbnailColumnImage = config.value;
                    break;

                case "sizeLimitWidth":
                    this.sizeLimitWidth = config.value;
                    break;

                case "sizeLimitHeight":
                    this.sizeLimitHeight = config.value;
                    break;

                case "zoomFactor":
                    this.zoomFactor = config.value + "%";
                    break;

                case "maxZoom":
                    this.maxZoom = config.value + "%";
                    break;
                }
            }
        }, this);

        var rec;
        if (Ext.isEmpty(this.record)) {
            rec = this.dataview.getSelections()[0];
        } else {
            rec = this.record;
        }

        if (Ext.isEmpty(this.columnImage)) {
            this.columnImage = this.columnAlias;
        }

        this.src = rec.get(this.columnImage);
        if (!Ext.isEmpty(this.thumbnailColumnImage)) {
            this.previewSrc = rec.get(this.thumbnailColumnImage);
        } else {
            this.previewSrc = this.src;
        }
        this.title = this.columnImage;

        var id = Ext.id();
        this.panel = new Ext.Panel({
            bodyCfg : {
                tag : 'img',
                src : this.src,
                id : id
            },
            listeners : {
                scope : this,
                render : function () {
                    this.panel.body.on('load', this.onImageLoad, this, {
                        single : true
                    });
                }
            }
        });

        this.items = [ this.panel ];

        this.listeners = {
            scope : this,

            resize : function (window, width, height) {
                if (!Ext.isEmpty(this.viewer)) {
                    var frameElementWidth = width - this.getAdditionalWidth();
                    var frameElementHeight = height - this.getAdditionalHeight();
                    this.viewer.setFrameProp([ frameElementWidth + "px", frameElementHeight + "px" ]);
                    this.viewer.reset();
                    this.viewer.fireEvent("resize");
                }
            }
        };

        // this.items = [this.panel];
        this.autoScroll = true;
        this.constrain = true;

        sitools.user.component.dataviews.services.WindowImageZoomer.superclass.initComponent.apply(this, arguments);
    },

    /**
     * Method called when the image is loaded. It is in charge of resizing the
     * image according to the desktop size
     */
    onImageLoad : function () {
        var img = this.panel.getEl().child("img");
        this.viewer = new viewer({
            image : img.dom,
            frame : [ this.sizeLimitWidth + "px", this.sizeLimitHeight + "px" ],
            zoomFactor : this.zoomFactor,
            maxZoom : this.maxZoom,
            previewSrc : this.previewSrc
        });
        this.updateWindowSize(this.sizeLimitHeight, this.sizeLimitWidth)
        this.doLayout();
    },

    getAdditionalHeight : function () {
        var height = 4;
        var enteteEl = this.getEl().child(".x-window-tl");
        height += enteteEl.getHeight();
        if (!Ext.isEmpty(this.viewer) && !Ext.isEmpty(this.viewer.toolbarHeight)) {
            height += this.viewer.toolbarHeight;
        }

        return height;

    },

    getAdditionalWidth : function () {
        return 4;
    },

    updateWindowSize : function (frameHeight, frameWidth) {
        var height = parseInt(frameHeight);
        // add the padding
        height += this.getAdditionalHeight();
        var width = parseInt(frameWidth);
        width += this.getAdditionalWidth();
        this.setSize(width, height);
    }

});

sitools.user.component.dataviews.services.WindowImageZoomer.getParameters = function () {
    return [
            {
                jsObj : "Ext.form.ComboBox",
                config : {
                    fieldLabel : i18n.get('headers.previewUrl'),
                    width : 200,
                    typeAhead : true,
                    mode : 'local',
                    forceSelection : true,
                    triggerAction : 'all',
                    valueField : 'display',
                    displayField : 'display',
                    value : 'Image',
                    store : new Ext.data.ArrayStore({
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
                    mode : 'local',
                    forceSelection : true,
                    triggerAction : 'all',
                    store : new Ext.data.JsonStore({
                        fields : [ 'columnAlias' ],
                        url : Ext.getCmp("dsFieldParametersPanel").urlDataset,
                        root : "dataset.columnModel",
                        autoLoad : true,
                        listeners : {
                            load : function (store) {
                                store.add(new Ext.data.Record({
                                    'columnAlias' : ""
                                }));
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
                    mode : 'local',
                    forceSelection : true,
                    triggerAction : 'all',
                    store : new Ext.data.JsonStore({
                        fields : [ 'columnAlias' ],
                        url : Ext.getCmp("dsFieldParametersPanel").urlDataset,
                        root : "dataset.columnModel",
                        autoLoad : true,
                        listeners : {
                            load : function (store) {
                                store.add(new Ext.data.Record({
                                    'columnAlias' : ""
                                }));
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
            } ];
};

sitools.user.component.dataviews.services.WindowImageZoomer.getDefaultParameters = function () {
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
};

sitools.user.component.dataviews.services.WindowImageZoomer.executeAsService = function (config) {
    var windowImageZoomer = new sitools.user.component.dataviews.services.WindowImageZoomer(config);
    windowImageZoomer.show();
};
