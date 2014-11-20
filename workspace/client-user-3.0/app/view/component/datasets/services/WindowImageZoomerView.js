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
Ext.namespace('sitools.user.view.component.datasets.services');

/**
 * A specific window to display preview Images in user desktop.
 * 
 * @class sitools.user.component.dataviews.services.WindowImageZoomer
 * @config {boolean} resizeImage true to resize the image according to the
 *         desktop, image ratio is also keeped. false to keep the image size
 *         with scrollbars if needed. Default to false
 * @extends Ext.Window
 */
Ext.define('sitools.user.view.component.datasets.services.WindowImageZoomerView', {
    extend : 'Ext.window.Window',
    alias : 'widget.windowImageZoomerView',

    border : false,
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

        // done before in the service
//        if (Ext.isEmpty(this.parameters)) {
//            this.parameters = sitools.user.component.datasets.services.WindowImageZoomerView.getDefaultParameters();
//        }

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
            var recIndex = this.dataview.getSelections()[0];
            rec = this.dataview.getStore().getAt(recIndex);
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
        this.panel = Ext.create('Ext.Component', {
            border : false,
            autoEl : {
                tag : 'img',
	            border : false,
                src : this.src,
                name : 'img_' + this.id
            },
            listeners : {
                scope : this,
                render : function (me) {
                    me.getEl().on('load', this.onImageLoad, this, {
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

        this.callParent(arguments);
    },

    /**
     * Method called when the image is loaded. It is in charge of resizing the
     * image according to the desktop size
     */
    onImageLoad : function () {
//        var img = this.panel.getEl().child("img");
        var img = Ext.DomQuery.select("div > img[name='" + 'img_' + this.id + "']")[0];
        
        this.viewer = new viewer({
            image : img,
            frame : [ this.sizeLimitWidth + "px", this.sizeLimitHeight + "px" ],
            zoomFactor : this.zoomFactor,
            maxZoom : this.maxZoom,
            previewSrc : this.previewSrc
        });
        this.updateWindowSize(this.sizeLimitHeight, this.sizeLimitWidth)
    },

    getAdditionalHeight : function () {
        var height = 45;
//        var enteteEl = this.getEl().child(".x-window-tl");
        var enteteEl = this.getEl().down("div[id='" + this.id + "_header" + "']");
        
        height += enteteEl.getHeight();
        if (!Ext.isEmpty(this.viewer) && !Ext.isEmpty(this.viewer.toolbarHeight)) {
            height += this.viewer.toolbarHeight;
        }

        return height;

    },

    getAdditionalWidth : function () {
        return 45;
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
