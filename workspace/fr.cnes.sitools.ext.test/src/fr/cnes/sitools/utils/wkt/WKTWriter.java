/*******************************************************************************
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
 ******************************************************************************/
package fr.cnes.sitools.utils.wkt;


import fr.cnes.sitools.dataset.model.geometry.LngLatAlt;
import fr.cnes.sitools.dataset.model.geometry.Point;
import fr.cnes.sitools.dataset.model.geometry.Polygon;

import java.util.List;

public class WKTWriter {

    public static String write(Point point) {
        if (point != null) {
            return "POINT(" + point.getLongitude() + " " + point.getLatitude() + ")";
        }
        return null;
    }

//    public static String write(MultiPoint multiPoint) {
//        if (multiPoint != null) {
//            List<LngLatAlt> points = multiPoint.getCoordinates();
//            if (points != null && points.size() > 0) {
//                String wkt = "MULTIPOINT(";
//                boolean first = true;
//                for (LngLatAlt coords : points) {
//                    if (first) {
//                        first = false;
//                    } else {
//                        wkt += ",";
//                    }
//                    wkt += "(" + coords.getLongitude() + " " + coords.getLatitude() + ")";
//                }
//                wkt += ")";
//                return wkt;
//            }
//        }
//        return null;
//    }
//
//
//    public static String write(LineString lineString) {
//        if (lineString != null) {
//            List<LngLatAlt> points = lineString.getCoordinates();
//            if (points != null && points.size() > 0) {
//                return "LINESTRING(" + toWKT(points) + ")";
//            }
//        }
//        return null;
//    }
//
//
//    public static String write(MultiLineString multiLineString) {
//        if (multiLineString != null) {
//            List<List<LngLatAlt>> lines = multiLineString.getCoordinates();
//            if (lines != null && lines.size() > 0) {
//                String wkt = "MULTILINESTRING(";
//                boolean first = true;
//                for (List<LngLatAlt> line : lines) {
//                    if (first) {
//                        first = false;
//                    } else {
//                        wkt += ",";
//                    }
//                    wkt += "(" + toWKT(line) + ")";
//                }
//                wkt += ")";
//                return wkt;
//            }
//        }
//        return null;
//    }

    public static String write(Polygon polygon) {
        if (polygon != null) {
            List<LngLatAlt> exteriorRing = polygon.getExteriorRing();
            if (exteriorRing != null) {
                String wkt = "POLYGON(";
                wkt += "(" + toWKT(exteriorRing) + ")";
                List<List<LngLatAlt>> holes = polygon.getInteriorRings();
                if (holes != null && holes.size() > 0) {
                    for (List<LngLatAlt> hole : holes) {
                        wkt += ",(" + toWKT(hole) + ")";
                    }
                }
                wkt += ")";
                return wkt;
            }
        }
        return null;
    }

//    public static String write(MultiPolygon multiPolygon) {
//        if (multiPolygon != null) {
//            List<List<List<LngLatAlt>>> polygons = multiPolygon.getCoordinates();
//            if (polygons != null && polygons.size() > 0) {
//                String wkt = "MULTIPOLYGON(";
//                boolean firstPolygon = true;
//                for (List<List<LngLatAlt>> polygon : polygons) {
//                    if (firstPolygon) {
//                        firstPolygon = false;
//                        wkt += "(";
//                    } else {
//                        wkt += ",(";
//                    }
//                    boolean firstRing = true;
//                    for (List<LngLatAlt> ring : polygon) {
//                        if (firstRing) {
//                            firstRing = false;
//                        } else {
//                            wkt += ",";
//                        }
//                        wkt += "(" + toWKT(ring) + ")";
//                    }
//                    wkt += ")";
//                }
//                wkt += ")";
//                return wkt;
//            }
//        }
//        return null;
//    }

    /**
     * @param points List of points
     * @return Return String with the following format : "lon lat, lon lat, ..."
     */
    private static String toWKT(List<LngLatAlt> points) {
        String wkt = "";
        boolean first = true;
        for (LngLatAlt coords : points) {
            if (first) {
                first = false;
            } else {
                wkt += ",";
            }
            wkt += coords.getLongitude() + " " + coords.getLatitude();
        }
        return wkt;
    }
}
