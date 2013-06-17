     /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

  /** The x value */
  private double x;
  /** The y value */
  private double y;

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
    this.x = x;
    this.y = y;
  }

  /**
   * Gets the x value
   * 
   * @return the x
   */
  public double getX() {
    return x;
  }

  /**
   * Sets the value of x
   * 
   * @param x
   *          the x to set
   */
  public void setX(double x) {
    this.x = x;
  }

  /**
   * Gets the y value
   * 
   * @return the y
   */
  public double getY() {
    return y;
  }

  /**
   * Sets the value of y
   * 
   * @param y
   *          the y to set
   */
  public void setY(double y) {
    this.y = y;
  }

}
