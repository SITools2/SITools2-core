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
package fr.cnes.sitools.datasource.jdbc.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Class for attribute value
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
@XStreamAlias("attribute")
public final class AttributeValue {
  /**
   * Attribute name
   */
  private String name;

  /**
   * Attribute value
   */
  private Object value;

  /**
   * Constructor (full)
   * 
   * @param name String
   * @param value Object
   */
  public AttributeValue(String name, Object value) {
    super();
    this.name = name;
    this.value = value;
  }

  /**
   * Default constructor
   */
  public AttributeValue() {
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
   * Gets the value value
   * 
   * @return the value
   */
  public Object getValue() {
    return value;
  }

  /**
   * Sets the value of value
   * 
   * @param value
   *          the value to set
   */
  public void setValue(Object value) {
    this.value = value;
  }

}
