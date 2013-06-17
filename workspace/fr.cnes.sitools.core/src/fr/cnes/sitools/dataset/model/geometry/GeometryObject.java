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

import java.util.ArrayList;
import java.util.List;

/**
 * Object to store Geometry Object in predicats
 * 
 * 
 * @author m.gond
 */
public class GeometryObject {

  /** The type of the Geometry */
  private GeometryType type;

  /** List of coordinates */
  private List<Point> points;

  /** radius for Circle type */
  private double radius;

  /**
   * Default constuctor
   */
  public GeometryObject() {
    super();
  }

  /**
   * Create a new {@link GeometryObject} with empty points list and a type
   * 
   * @param type
   *          the type of the {@link GeometryObject}
   */
  public GeometryObject(GeometryType type) {
    super();
    this.type = type;
    this.points = new ArrayList<Point>();
  }

  /**
   * Create a new {@link GeometryObject} with type and list of points
   * 
   * @param type
   *          the type of the {@link GeometryObject}
   * @param points
   *          the {@link List} of {@link Point}
   */
  public GeometryObject(GeometryType type, List<Point> points) {
    super();
    this.type = type;
    this.points = points;
  }

  /**
   * Create a new {@link GeometryObject} with type and list of points
   * 
   * @param type
   *          the type of the {@link GeometryObject}
   * @param points
   *          the {@link List} of {@link Point}
   * @param radius
   *          the radius
   */
  public GeometryObject(GeometryType type, List<Point> points, double radius) {
    super();
    this.type = type;
    this.points = points;
    this.radius = radius;
  }

  /**
   * Gets the type value
   * 
   * @return the type
   */
  public GeometryType getType() {
    return type;
  }

  /**
   * Sets the value of type
   * 
   * @param type
   *          the type to set
   */
  public void setType(GeometryType type) {
    this.type = type;
  }

  /**
   * Gets the points value
   * 
   * @return the points
   */
  public List<Point> getPoints() {
    return points;
  }

  /**
   * Sets the value of points
   * 
   * @param points
   *          the points to set
   */
  public void setPoints(List<Point> points) {
    this.points = points;
  }

  /**
   * Gets the radius value
   * 
   * @return the radius
   */
  public double getRadius() {
    return radius;
  }

  /**
   * Sets the value of radius
   * 
   * @param radius
   *          the radius to set
   */
  public void setRadius(double radius) {
    this.radius = radius;
  }

}
