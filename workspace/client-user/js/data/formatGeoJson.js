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

/*global Ext, sitools, OpenLayers*/

Ext.ns("sitools.user.data");

sitools.user.data.FormatGeoJson = Ext.extend(OpenLayers.Format.GeoJSON, {
    totalProperty : null, 
	/**
     * APIMethod: read
     * Deserialize a GeoJSON string.
     *
     * Parameters:
     * json - {String} A GeoJSON string
     * type - {String} Optional string that determines the structure of
     *     the output.  Supported values are "Geometry", "Feature", and
     *     "FeatureCollection".  If absent or null, a default of
     *     "FeatureCollection" is assumed.
     * filter - {Function} A function which will be called for every key and
     *     value at every level of the final result. Each value will be
     *     replaced by the result of the filter function. This can be used to
     *     reform generic objects into instances of classes, or to transform
     *     date strings into Date objects.
     *
     * Returns: 
     * {Object} The return depends on the value of the type argument. If type
     *     is "FeatureCollection" (the default), the return will be an array
     *     of <OpenLayers.Feature.Vector>. If type is "Geometry", the input json
     *     must represent a single geometry, and the return will be an
     *     <OpenLayers.Geometry>.  If type is "Feature", the input json must
     *     represent a single feature, and the return will be an
     *     <OpenLayers.Feature.Vector>.
     */
    read: function (json, type, filter) {
        type = (type) ? type : "FeatureCollection";
        var results = null;
        var obj = null;
        if (typeof json == "string") {
            obj = OpenLayers.Format.JSON.prototype.read.apply(this,
                                                              [json, filter]);
        } else { 
            obj = json;
        }    
        if (!obj) {
            OpenLayers.Console.error("Bad JSON: " + json);
        } else if (typeof(obj.type) != "string") {
            OpenLayers.Console.error("Bad GeoJSON - no type: " + json);
        } else if (this.isValidType(obj, type)) {
            switch (type) {
                case "Geometry" :
                    try {
                        results = this.parseGeometry(obj);
                    } catch (err) {
                        OpenLayers.Console.error(err);
                    }
                    break;
                case "Feature":
                    try {
                        results = this.parseFeature(obj);
                        results.type = "Feature";
                    } catch(err) {
                        OpenLayers.Console.error(err);
                    }
                    break;
                case "FeatureCollection":
                    // for type FeatureCollection, we allow input to be any type
                    results = [];
                    switch(obj.type) {
                        case "Feature":
                            try {
                                results.push(this.parseFeature(obj));
                            } catch(err) {
                                results = null;
                                OpenLayers.Console.error(err);
                            }
                            break;
                        case "FeatureCollection":
                            for(var i=0, len=obj.features.length; i<len; ++i) {
                                try {
                                    results.push(this.parseFeature(obj.features[i]));
                                } catch(err) {
                                    results = null;
                                    OpenLayers.Console.error(err);
                                }
                            }
                            break;
                        default:
                            try {
                                var geom = this.parseGeometry(obj);
                                results.push(new OpenLayers.Feature.Vector(geom));
                            } catch(err) {
                                results = null;
                                OpenLayers.Console.error(err);
                            }
                    }
                break;
            }
        }
        return {
			features : results, 
			totalResults : obj[this.totalProperty]
        };
    }
});
