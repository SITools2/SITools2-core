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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WKTReader {

    /**
     * Parse WKT POINT geometry
     *
     * @param wkt Expected format is "POINT(longitude latitude)" expressed in WSG84
     * @return Point object if wkt is well formed or null otherwise
     */
    public static Point parsePoint(String wkt) {
        if (wkt != null) {
            String str = extractFromGeometry(TypeGeometry.POINT, wkt);

            List<LngLatAlt> points = getLngLats(str);
            if (points.size() > 0) {
                return new Point(points.get(0));
            }
        }
        return null;
    }

//    /**
//     * Parse WKT MULTIPOINT geometry
//     *
//     * @param wkt Expected format is "MULTIPOINT ((lon lat), (lon lat), ... )" expressed in WSG84
//     * @return List of Point object if wkt is well formed or null otherwise
//     */
//    public static MultiPoint parseMultiPoint(String wkt) {
//        if (wkt != null) {
//            String str = extractFromGeometry(TypeGeometry.MULTIPOINT, wkt);
//            String[] pointsStr = str.split(",");
//
//            MultiPoint multiPoint = new MultiPoint();
//
//            for (String aPointsStr : pointsStr) {
//                Point p = parsePoint(aPointsStr);
//                if (p != null) {
//                    multiPoint.add(p.getCoordinates());
//                }
//            }
//            return multiPoint;
//        }
//        return null;
//    }
//
//    /**
//     * @param wkt Expected format is "LINESTRING (lon lat, lon lat, ..)" expressed in WSG84
//     * @return LineString object if wkt is well formed or null otherwise
//     */
//    public static LineString parseLineString(String wkt) {
//        if (wkt != null) {
//            String str = extractFromGeometry(TypeGeometry.LINESTRING, wkt);
//
//            return getLineString(str);
//        }
//        return null;
//    }
//
//    /**
//     * @param wkt Expected format is "MULTILINESTRING ((lon lat, lon lat, ..), (lon lat, lon lat, ..), ...)" expressed in WSG84
//     * @return List of LineString object if wkt is well formed or null otherwise
//     */
//    public static MultiLineString parseMultiLineString(String wkt) {
//        if (wkt != null) {
//            String str = extractFromGeometry(TypeGeometry.MULTILINESTRING, wkt);
//
//            MultiLineString multiLineString = new MultiLineString();
//
//            String[] linesStr = extractFromList(str);
//
//            for (String aLinesStr : linesStr) {
//                LineString line = getLineString(aLinesStr);
//                if (line != null) {
//                    multiLineString.add(line.getCoordinates());
//                }
//            }
//
//            return multiLineString;
//        }
//        return null;
//    }

    /**
     * @param wkt Expected format is "POLYGON ((lon lat, lon lat, ..)) or POLYGON ((lon lat, lon lat, ..), (lon lat, lon lat, ..))"
     *            expressed in WSG84
     * @return Polygon object if wkt is well formed or null otherwise
     */
    public static Polygon parsePolygon(String wkt) {
        if (wkt != null) {
            String str = extractFromGeometry(TypeGeometry.POLYGON, wkt);

            return getPolygon(str);
        }
        return null;
    }


//    /**
//     * @param wkt Expected format is "MULTIPOLYGON (((lon lat, lon lat, ..)), ((lon lat, lon lat, ..)), ...)
//     *            or MULTIPOLYGON (((lon lat, lon lat, ..), (lon lat, lon lat, ..))), ... )"
//     *            expressed in WSG84
//     * @return Polygon object if wkt is well formed or null otherwise
//     */
//    public static MultiPolygon parseMultiPolygon(String wkt) {
//        if (wkt != null) {
//            String str = extractFromGeometry(TypeGeometry.MULTIPOLYGON, wkt);
//
//            String[] polyStr = extractFromDoubleList(str);
//
//            MultiPolygon multiPolygon = new MultiPolygon();
//            for (String aPolyStr : polyStr) {
//                Polygon polygon;
//                if (aPolyStr.contains(")")) {
//                    // Case of Polygon with interior and exterior ring
//                    polygon = parsePolygon(aPolyStr);
//                } else {
//                    // Case of Polygon with only exterior ring
//                    polygon = new Polygon(getLngLats(aPolyStr));
//                }
//                if (polygon != null) {
//                    multiPolygon.add(polygon);
//                }
//            }
//            return multiPolygon;
//        }
//        return null;
//    }

    /**
     * @param str String with the following format : "lon lat, lon lat, ..."
     * @return List of LngLatAlt object resulting of the parsing process
     */
    private static List<LngLatAlt> getLngLats(String str) {

        List<LngLatAlt> points = new ArrayList<LngLatAlt>();

        Pattern pattern = Pattern.compile("(?:([-]?\\d+\\.?\\d*) \\s*([-]?\\d+\\.?\\d*))");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            double longitude, latitude;
            try {
                longitude = Double.parseDouble(matcher.group(1));
                latitude = Double.parseDouble(matcher.group(2));
            } catch (NumberFormatException ex) {
                // Malformed number inside
                return null;
            }
            points.add(new LngLatAlt(longitude, latitude));
        }

        return points;
    }

//    /**
//     * @param str String with the following format : "lon lat, lon lat, ..."
//     * @return LineString object if wkt is well formed or null otherwise
//     */
//    private static LineString getLineString(String str) {
//        List<LngLatAlt> points = getLngLats(str);
//
//        if (points != null) {
//            LineString lineString = new LineString();
//
//            for (LngLatAlt point : points) {
//                lineString.add(point);
//            }
//            return lineString;
//        }
//        return null;
//    }

    /**
     * @param str String with the following format : "(lon lat, lon lat, ..)" or "(lon lat, lon lat, ..), (lon lat, lon lat, ..)"
     * @return Polygon object if wkt is well formed or null otherwisePolygon object if wkt is well formed or null otherwise
     */
    private static Polygon getPolygon(String str) {
        String[] rings = extractFromList(str);

        assert rings.length > 0;
        rings[0] = rings[0].replaceAll("\\(", "").trim();
        List<LngLatAlt> pointsExterior = getLngLats(rings[0]);

        if (pointsExterior != null) {
            Polygon polygon = new Polygon(pointsExterior);

            if (rings.length > 1) {
                rings[1] = rings[1].replaceAll("\\(\\)", "").trim();
                List<LngLatAlt> pointsInterior = getLngLats(rings[1]);

                polygon.addInteriorRing(pointsInterior);
            }

            return polygon;
        }
        return null;
    }

    /**
     * If wkt  has the following form : "TYPE ( ... ) or ( ... )", Then return the content "..."
     * Otherwise it returns wkt string trimed and uppercased.
     *
     * @param type WKT Geometry type
     * @param wkt WKT String
     * @return Remove the "TYPE ( ... )" or enclosing parenthesis.
     */
    private static String extractFromGeometry(TypeGeometry type, String wkt) {

        // Normalize the WKT string
        wkt = wkt.trim().toUpperCase();

        if (type != null) {

            Pattern pattern = Pattern.compile("(?:" + type.toString().toUpperCase() + ")?\\s*\\((.*)\\)");
            Matcher matcher = pattern.matcher(wkt);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return wkt;
    }

    /**
     * @param str String with the following format : "(lon lat, lon lat, ..)" or "(lon lat, lon lat, ..), (lon lat, lon lat, ..)"
     * @return array of String with the following format : "lon lat, lon lat, .."
     */
    private static String[] extractFromList(String str) {

        String[] results = str.split("\\)(\\s*,\\s*)?");

        // Remove all remaining leading '(' character from the results
        for (int i = 0; i < results.length; i++) {
            results[i] = results[i].replaceAll("\\(", "").trim();
        }

        return results;
    }

    /**
     * @param str String with the following format : "((lon lat, lon lat, ..)") or "(lon lat, lon lat, ..), (lon lat, lon lat, ..)"
     * @return array of String with the following format : "lon lat, lon lat, .."
     */
    private static String[] extractFromDoubleList(String str) {

        String[] results = str.split("\\)\\)\\s*,\\s*");

        // Remove all remaining leading '(' character from the results
        for (int i = 0; i < results.length; i++) {

            if (!results[i].contains(")")) {
                // Case of Polygon with only exterior ring, we have to remove the 2 leading "("
                results[i] = results[i].replaceAll("\\(", "").trim();
            }
            //else : Case of Polygon with interior and exterior rings : we keep the current result as
            //       "((lon lat, lon lat, ..), (lon lat, lon lat, ..))"
        }

        return results;
    }
}

