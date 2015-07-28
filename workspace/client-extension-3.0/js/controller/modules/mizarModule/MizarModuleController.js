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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl, sql2ext, sitoolsUtils*/

Ext.namespace('sitools.extension.controller.modules.mizarModule');

/**
 * MizarModuleController
 * .
 *
 * @class sitools.extension.controller.modules.mizarModule.MizarModuleController
 * @extends Ext.app.Controller
 */
Ext.define('sitools.extension.controller.modules.mizarModule.MizarModuleController', {
    extend: 'Ext.app.Controller',

    views: ['sitools.extension.view.modules.mizarModule.MizarModuleView'],

    config: {
        div: "#mizarModule"
    },
    init: function () {
        this.control({
            'mizarModuleView': {
                scope : this,
                boxready: function (mizarView) {
                    var div = this.getDiv();
                    var options = {
                        "nameResolver": {
                            "zoomFov": 2
                        }
                        //"sitoolsBaseUrl": "http://localhost:8182/sitools"
                    };

                    if (Ext.isEmpty(this.configFile)) {
                        this.initMizar(div, options, mizarView);
                    }
                    else {
                        Ext.Ajax.request({
                            url: this.configFile,
                            method: 'GET',
                            scope: this,
                            success: function (response) {
                                var data = Ext.decode(response.responseText);
                                options.backgroundSurveys = data;
                                this.initMizar(div, options);
                            }
                        });
                    }
                },
                destroy : function () {
                    this.mizarWidget.sky.destroy();
                    var layersToDestroy = this.mizarWidget.getLayers();
                    Ext.each(layersToDestroy, function (layer) {
                        this.mizarWidget.removeLayer(layer);
                    });
                },
                resize : this.fitCanvasToDiv
            }
        });
    },

    initMizar: function (div, options, mizarView) {

        mizarWidget = new MizarWidget(div, options);

        this.mizarWidget = mizarWidget;

        this.mizarWidget.setNameResolverGui(true);
        this.mizarWidget.setReverseNameResolverGui(true);
        this.mizarWidget.set2dMapGui(true);
        this.mizarWidget.setCompassGui(true);
        this.mizarWidget.setCategoryGui(true);
        mizar.setShowCredits(false);

        $('#toggleCompass').click(function () {
            if ($(this).is(":checked")) {
                this.mizarWidget.setCompassGui(true);
            }
            else {
                this.mizarWidget.setCompassGui(false);
            }
        });

        this.geoJsonLayer = this.mizarWidget.addLayer({
            "category": "Other",
            "type": "GeoJSON",
            "name": "RESTO",
            //"serviceUrl": "http://localhost:8182/proxy_resto",
            //"serviceUrl": "http://localhost:8182/sitools/datastorage/user/storage",
            "data": {
                "type": "JSON",
                "url": "http://localhost:8182/resto_features/geojson"
            },
            //"availableServices": [ "OpenSearch" ],
            "visible": true,
            "pickable": true,
            "color": "purple"
            //"minOrder": 3
        });

        this.mizarWidget.sky.subscribe("endLoad", function (featureData) {
            alert('toto quoi');
        });
        this.mizarWidget.subscribe("backgroundLayer:add", function (featureData) {
            alert('toto quoi');
        });

        mizarView.geoJsonLayer = this.geoJsonLayer;

        var applyGuiOptions = Ext.bind(function () {

            // Set different GUIs
            this.mizarWidget.setAngleDistanceGui(JSON.parse(mizarView.angleDistance));
            this.mizarWidget.setSampGui(JSON.parse(mizarView.samp));
            this.mizarWidget.setShortenerUrlGui(JSON.parse(mizarView.shortenerUrl));
            this.mizarWidget.set2dMapGui(JSON.parse(mizarView.twoDMap));
            this.mizarWidget.setReverseNameResolverGui(JSON.parse(mizarView.reverseNameResolver));
            this.mizarWidget.setNameResolverGui(JSON.parse(mizarView.nameResolver));
            this.mizarWidget.setCategoryGui(JSON.parse(mizarView.category));
            this.mizarWidget.setCompassGui(JSON.parse(mizarView.compass));
            this.mizarWidget.setShowCredits(JSON.parse(mizarView.showCredits));
            this.mizarWidget.setImageViewerGui(JSON.parse(mizarView.imageViewer));

            var startingPoint = [mizarView.startingLong, mizarView.startingLat];
            mizarWidget.navigation.zoomTo(startingPoint, mizarView.startingZoom);

        }, this);


        //$("#mizarModule").on("load", applyGuiOptions);
    },

    fitCanvasToDiv : function (mizarView, width, height, oldWidth, oldHeight) {

        var globWebCanvas = Ext.get('GlobWebCanvas');
        globWebCanvas.set({
            "height":height,
            "width":width
        });
        this.mizarWidget.sky.refresh();
    },

    destroyMizar : function () {
        Ext.each(this.mizarWidget.getLayers(), function (layer) {
           layer.removeAllFeatures();
           layer.detach();
        });
        this.mizarWidget.sky.destroy();
    }
});