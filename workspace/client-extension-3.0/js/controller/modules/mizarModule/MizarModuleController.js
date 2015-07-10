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
                afterrender: function () {
                    var div = this.getDiv();
                    var options = {
                        "nameResolver": {
                            "zoomFov": 2
                        }
                        //"sitoolsBaseUrl": "http://localhost:8182/sitools"
                    };

                    if (Ext.isEmpty(this.configFile)) {
                        this.initMizar(div, options);
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
                }
            }
        });
    },

    initMizar: function (div, options) {

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

        oslayer = this.mizarWidget.addLayer({
            "category": "Other",
            "type": "GeoJSON",
            "name": "RESTO",
            //"serviceUrl": "http://localhost:8182/proxy_resto",
            //"serviceUrl": "http://localhost:8182/sitools/datastorage/user/storage",
            "data": {
                "type": "JSON",
                "url": "http://localhost:8182/features/geojson"
            },
            //"availableServices": [ "OpenSearch" ],
            "visible": true,
            "pickable": true,
            "color": "purple"
            //"minOrder": 3
        });
    },
});