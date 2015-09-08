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

    views: ['sitools.extension.view.modules.mizarModule.MizarModuleView',
            'sitools.extension.view.modules.mizarModule.MizarViewAndDataModuleView'],

    config: {
        div: "#mizarModule"
    },
    init: function () {
        this.control({
            'mizarModuleView': {
                scope : this,
                afterrender : function (mizarView) {
                    var div = this.getDiv();
                    var options = {
                        "nameResolver": {
                            "zoomFov": 2
                        },
                        debug: true,
                        "sitoolsBaseUrl": window.location.origin + loadUrl.get('APP_URL')
                    };

                    if (Ext.isEmpty(mizarView.configFile)) {
                        this.initMizar(div, options, mizarView);
                    }
                    else {
                        Ext.Ajax.request({
                            url: mizarView.configFile,
                            method: 'GET',
                            scope: this,
                            success: function (response) {
                                var data = Ext.decode(response.responseText);
                                options.backgroundSurveys = data;
                                this.initMizar(div, options, mizarView);
                                var container = mizarView.up("component[specificType=moduleWindow]");
                                this.fitCanvasToDiv(container);
                            }
                        });
                    }

                    mizarView.up("component[specificType=moduleWindow]").on("beforedestroy", this.destroyMizar, this);
                    mizarView.up("component[specificType=moduleWindow]").on("resize", this.fitCanvasToDiv, this);
                }
            },
            'mizarViewAndDataModuleView > mizarModuleView #mizarModule' : {
                boxready : function (mizarView) {
                    var container = mizarView.up("component[specificType=moduleWindow]");
                    this.fitCanvasToDiv(container);
                }
            },
            'mizarViewAndDataModuleView > simpleGridView' : {
                scope : this,
                resize : function (simpleGridView) {
                    var mizarViewAndDataModuleView = simpleGridView.up('mizarViewAndDataModuleView');
                    this.fitCanvasToDiv(mizarViewAndDataModuleView);
                },
                collapse : function (simpleGridView) {
                    var mizarViewAndDataModuleView = simpleGridView.up('mizarViewAndDataModuleView');
                    this.fitCanvasToDiv(mizarViewAndDataModuleView);
                },
                expand : function (simpleGridView) {
                    var mizarViewAndDataModuleView = simpleGridView.up('mizarViewAndDataModuleView');
                    this.fitCanvasToDiv(mizarViewAndDataModuleView);
                }
            }
        });
    },

    initMizar: function (div, options, mizarView) {

        mizarWidget = new MizarWidget(div, options);

        this.mizarWidget = mizarWidget;

        // Set different GUIs
        this.mizarWidget.setAngleDistanceGui(JSON.parse(mizarView.angleDistance));
        this.mizarWidget.setSampGui(JSON.parse(mizarView.samp));
        this.mizarWidget.setShortenerUrlGui(JSON.parse(mizarView.shortenerUrl));
        this.mizarWidget.set2dMapGui(JSON.parse(mizarView.twoDMap));
        this.mizarWidget.setReverseNameResolverGui(JSON.parse(mizarView.reverseNameResolver));
        this.mizarWidget.setNameResolverGui(JSON.parse(mizarView.nameResolver));
        this.mizarWidget.setShowCredits(JSON.parse(mizarView.showCredits));

        var startingPoint = [mizarView.startingLong, mizarView.startingLat];
        mizarWidget.navigation.zoomTo(startingPoint, mizarView.startingZoom);

        //Unprotected setter properties !!!!! Cannot be called with false value
        if(JSON.parse(mizarView.category)) {
            this.mizarWidget.setCategoryGui(JSON.parse(mizarView.category));
        }

        if(JSON.parse(mizarView.compass)) {
            this.mizarWidget.setCompassGui(JSON.parse(mizarView.compass));
        }

        if(JSON.parse(JSON.parse(mizarView.imageViewer))) {
            this.mizarWidget.setImageViewerGui(JSON.parse(mizarView.imageViewer));
        }
    },

    fitCanvasToDiv : function (moduleContainer) {

        var mizarDiv = moduleContainer.down("mizarModuleView");

        var globWebCanvas = Ext.get('GlobWebCanvas');
        if (!Ext.isEmpty(globWebCanvas)) {
            globWebCanvas.set({
                "height": mizarDiv.getHeight(),
                "width": mizarDiv.getWidth()
            });
            this.mizarWidget.sky.refresh();
        }
    },

    destroyMizar : function (moduleContainer) {

        var layers = this.mizarWidget.getLayers();

        for (var i = 0; i < layers.length; i++) {
            console.dir(layers[i]);
            try {
                this.mizarWidget.removeLayer(layers[i]);
            }catch(e) {
                console.dir(e);
            }
        }

        MizarGlobal.nameResolverView.remove();
        MizarGlobal.reverseNameResolverView.remove();
        MizarGlobal.nameResolver.remove();
        MizarGlobal.layerManagerView.remove();


        Ext.get("selectedFeatureDiv").destroy();


        this.mizarWidget.sky.destroy();
    }
});