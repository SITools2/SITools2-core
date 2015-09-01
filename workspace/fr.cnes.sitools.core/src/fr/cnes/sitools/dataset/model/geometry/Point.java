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
package fr.cnes.sitools.dataset.model.geometry;

/**
 * Class to store 2D point object
 *
 *
 * @author m.gond
 */
public class Point {

    private LngLatAlt coordinates;

    /**
     * Default constructor
     */
    public Point() {
        super();

    }

    /**
     * Constructor with x and y value
     *
     * @param x
     *          the x coordinates
     * @param y
     *          the y coordinates
     */
    public Point(double x, double y) {
        super();
        this.coordinates = new LngLatAlt(x, y);
    }

    /**
     * Constructor with x and y value and altitude value
     *
     * @param x
     *          the x coordinates
     * @param y
     *          the y coordinates
     * @param altitude
     *          the alitude
     */
    public Point(double x, double y, double altitude) {
        super();
        this.coordinates = new LngLatAlt(x, y, altitude);
    }

    public Point(LngLatAlt coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Gets the x value
     *
     * @return the x
     */
    public double getX() {
        return this.coordinates.getLongitude();
    }

    /**
     * Sets the value of x
     *
     * @param x
     *          the x to set
     */
    public void setX(double x) {
        this.coordinates.setLongitude(x);
    }

    /**
     * Gets the y value
     *
     * @return the y
     */
    public double getY() {
        return coordinates.getLatitude();
    }

    /**
     * Sets the value of y
     *
     * @param y
     *          the y to set
     */
    public void setY(double y) {
        this.coordinates.setLatitude(y);
    }

    public double getAltitude() {
        return coordinates.getAltitude();
    }

    public void setAltitude(double altitude) {
        this.coordinates.setAltitude(altitude);
    }

    public double getLongitude() {
        return this.getX();
    }

    public double getLatitude() {
        return this.getY();
    }

    public void setLongitude(double longitude) {
        this.setX(longitude);
    }

    public void setLatitude(double latitude) {
        this.setY(latitude);
    }
}
