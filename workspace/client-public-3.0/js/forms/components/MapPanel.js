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
/*global Ext, sitools*/
/*
 * @include "../ComponentFactory.js"
 */
Ext.ns('sitools.public.forms.components');

/**
 * A Map Panel to request on a specific Layer.
 * @requires sitools.common.forms.ComponentFactory
 * @class sitools.public.forms.components.mapPanel
 * @extends Ext.Container
 */
Ext.define('sitools.public.forms.components.MapPanel', {
    extend: 'Ext.Container',
    requires: ['sitools.public.forms.ComponentFactory'],
    alternateClassName: ['sitools.common.forms.components.mapPanel'],

    defaultLayers: ["basic"],
    defaultWmsUrl: "http://vmap0.tiles.osgeo.org/wms/vmap0",

    initComponent: function () {
        this.context = sitools.public.forms.ComponentFactory.getContext(this.context);
        var layersName = [], wmsUrl = "";

        if (Ext.isArray(this.extraParams)) {
            Ext.each(this.extraParams, function (param) {
                switch (param.name) {
                    case "url" :
                        wmsUrl = param.value;
                        break;
                    case "layer" :
                        layersName.push(param.value);
                        break;
                }
            });
        }

        this.map = new OpenLayers.Map();
        var layer, layerName;
        if (Ext.isEmpty(wmsUrl)) {
            layersName = this.defaultLayers;
            wmsUrl = this.defaultWmsUrl;
        }

        for (var index = 0; index < layersName.length; index++) {
            layerName = layersName[index] || layerName;
            layer = new OpenLayers.Layer.WMS(
                layerName,
                wmsUrl,
                {layers: layerName},
                {isBaseLayer: index == 0}
            );
            this.map.addLayer(layer);
        }

        var vectorLayer = new OpenLayers.Layer.Vector("Vector Layer", {});

        this.map.addLayer(vectorLayer);

        this.map.addControl(new OpenLayers.Control.NavToolbar());
        this.map.addControl(new OpenLayers.Control.MousePosition());

        var action, actions = {}, toolbarItems = [];

        action = Ext.create("GeoExt.Action", {
            text: i18n.get('label.navigate'),
            iconCls: 'btn-navigate',
            control: new OpenLayers.Control.Navigation(),
            map: this.map,
            // button options
            tooltip: i18n.get('label.navigate'),
            toggleGroup: "draw",
            enableToggle: true,
            allowDepress: false,
            pressed: true,
            // check item options
            checked: true,
            group: "draw"
        });
        actions["nav"] = action;
        toolbarItems.push(Ext.create('Ext.button.Button', action));


        var ctrl_draw = new OpenLayers.Control.DrawFeature(
            vectorLayer, OpenLayers.Handler.RegularPolygon, {
                callbacks: {
                    "create": function () {
                        this.layer.removeAllFeatures();
                    }
                },
                handlerOptions: {
                    sides: 4,
                    irregular: true
                }
            }
        );
        this.map.addControl(ctrl_draw);

        action = Ext.create("GeoExt.Action", {
            text: i18n.get('label.drawPoly'),
            iconCls: 'btn-drawPoly',
            control: ctrl_draw,
            map: this.map,
            width: 150,
            // button options
            toggleGroup: "draw",
            enableToggle: true,
            allowDepress: false,
            tooltip: i18n.get('label.drawPoly'),
            // check item options
            group: "draw"
        });
        actions["draw_poly"] = action;
        toolbarItems.push(Ext.create('Ext.button.Button', action));

        action = Ext.create("GeoExt.Action", {
            text: i18n.get('label.clearPoly'),
            tooltip: i18n.get('label.clearPoly'),
            iconCls: 'btn-clearPoly',
            width: 150,
            map: this.map,
            scope: this,
            handler: function (btn, e) {
                btn.map.getLayersByName("Vector Layer")[0].removeAllFeatures();
            }
        });
        actions["clear_poly"] = action;
        toolbarItems.push(Ext.create('Ext.button.Button', action));

        var ctrl_drag = new OpenLayers.Control.DragFeature(vectorLayer);

        this.map.addControl(ctrl_drag);
        ctrl_drag.activate();
        this.mapPanel = Ext.create("GeoExt.panel.Map", {
            height: 300,
            width: 300,
            map: this.map,
            center: new OpenLayers.LonLat(5, 45),
            zoom: 4,
            tbar: Ext.create("Ext.toolbar.Toolbar", {
                items: toolbarItems
            })
        });

        Ext.apply(this, {
            layout: "hbox",
            stype: "sitoolsFormContainer",
            overCls: 'fieldset-child',
            items: [this.mapPanel],
            listeners: {
                scope: this,
                resize: function (mainContainer, adjWidth, adjHeight) {
                    this.mapPanel.setSize({
                        height: adjHeight,
                        width: adjWidth
                    })
                }
            }
        });

        if (this.action === "create") {
            for (var i = 0; i < this.map.controls.length; i++) {
                if (this.map.controls[i].displayClass == "olControlNavigation") {
                    this.map.controls[i].deactivate();
                }
            }
        }

        this.callParent(arguments);
    },

    getSelectedValue: function () {
        try {
            var layer = this.mapPanel.map.getLayersBy("name", "Vector Layer")[0];
            return layer.features[0].geometry.getBounds();
        }
        catch (err) {
            return null;
        }
    },

    getParameterValue: function () {
        var value = this.getSelectedValue();
        if (Ext.isEmpty(value)) {
            return null;
        }
        return {
            type: this.type,
            code: this.code,
            value: value.left + "," + value.bottom + "," + value.right + "," + value.top
        };

    },
    //  *** Reset function for RESET button ***//
    resetToDefault: function () {
        this.map.getLayersByName("Vector Layer")[0].removeAllFeatures();
    }
    //  ***************************************//

});
