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
package fr.cnes.sitools.datasource.jdbc.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Class for definition of a table attribute
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
@XStreamAlias("attribute")
public final class Attribute {
  /**
   * Attribute name
   */
  private String name;

  /**
   * Attribute type
   */
  private String type;

  /**
   * Attribute size
   */
  private int size;

  /**
   * Type java.sql.Types
   */
  private short javaSqlType;

  /**
   * Constructor (full)
   * 
   * @param name
   *          name
   * @param type
   *          type
   * @param size
   *          size
   */
  public Attribute(String name, String type, int size) {
    super();
    this.name = name;
    this.type = type;
    this.size = size;
  }

  /**
   * Constructor (full)
   * 
   * @param name
   *          name
   * @param type
   *          type
   * @param size
   *          size
   * @param javaSqlType
   *          javaSqlType
   */
  public Attribute(String name, String type, int size, short javaSqlType) {
    super();
    this.name = name;
    this.type = type;
    this.size = size;
    this.setJavaSqlType(javaSqlType);
  }

  /**
   * Default constructor
   */
  public Attribute() {
    super();
  }

  /**
   * Gets the name value
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of name
   * 
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the type value
   * 
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the value of type
   * 
   * @param type
   *          the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Gets the size value
   * 
   * @return the size
   */
  public int getSize() {
    return size;
  }

  /**
   * Sets the value of size
   * 
   * @param size
   *          the size to set
   */
  public void setSize(int size) {
    this.size = size;
  }

  /**
   * Sets the value of javaSqlType
   * @param javaSqlType the javaSqlType to set
   */
  public void setJavaSqlType(short javaSqlType) {
    this.javaSqlType = javaSqlType;
  }

  /**
   * Gets the javaSqlType value
   * @return the javaSqlType
   */
  public short getJavaSqlType() {
    return javaSqlType;
  }

}
