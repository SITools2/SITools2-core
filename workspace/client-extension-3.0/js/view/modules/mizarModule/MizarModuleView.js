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
/*global Ext, sitools, i18n, document, projectGlobal, SitoolsDesk, userLogin, DEFAULT_PREFERENCES_FOLDER, loadUrl*/


Ext.namespace('sitools.extension.view.modules.mizarModule');


Ext.define('sitools.extension.view.modules.mizarModule.MizarModuleView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.mizarModuleView',
    layout: 'fit',
    requires: ['sitools.extension.model.MizarModuleModel'],

    initComponent: function () {

        Ext.each(this.moduleModel.listProjectModulesConfigStore.data.items, function (config) {
            switch (config.get('name')) {
                case "configFile" :
                    this.configFile = config.get('value');
                    break;
            }
        }, this);

        this.items = [{
            layout: 'fit',
            region: 'center',
            id: "mizarModule",
            autoEl: {
                tag: 'div'
            },
            xtype: 'box'
        }
        ];

        this.callParent(arguments);

    },

    afterRender: function () {
        this.callParent(arguments);

        var options = {
            "nameResolver": {
                "zoomFov": 2
            }
            //"sitoolsBaseUrl": "http://localhost:8182/sitools"
        };
        mizarWidget = new MizarWidget("#mizarModule", options);

        mizarWidget.setNameResolverGui(true);
        mizarWidget.setReverseNameResolverGui(true);
        mizarWidget.set2dMapGui(true);
        mizarWidget.setCompassGui(true);
        mizarWidget.setCategoryGui(true);
        mizar.setShowCredits(false);

        $('#toggleCompass').click(function () {
            if ($(this).is(":checked")) {
                mizarWidget.setCompassGui(true);
            }
            else {
                mizarWidget.setCompassGui(false);
            }
        });

        this.geoJsonLayer = mizarWidget.addLayer({
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
    },

    /**
     * method called when trying to save preference
     *
     * @returns
     */
    _getSettings: function () {
        return {
            preferencesPath: "/modules",
            preferencesFileName: this.id,
            xtype: this.$className
        };

    }

});

