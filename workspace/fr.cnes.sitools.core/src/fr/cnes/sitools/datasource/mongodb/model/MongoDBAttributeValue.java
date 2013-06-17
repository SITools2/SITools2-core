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
package fr.cnes.sitools.datasource.mongodb.model;

import java.util.List;

/**
 * Attribute Value class specific to {@link MongoDBRecord}. Meaning Record from MongoDB datasource
 * 
 * 
 * @author m.gond
 */
public class MongoDBAttributeValue {

  /**
   * Attribute name
   */
  private String name;

  /**
   * Attribute value
   */
  private Object value;

  /**
   * The type of the object
   */
  private String type;

  /**
   * The list of Children
   */
  private List<MongoDBAttributeValue> children;

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
   * Gets the children value
   * 
   * @return the children
   */
  public List<MongoDBAttributeValue> getChildren() {
    return children;
  }

  /**
   * Sets the value of children
   * 
   * @param children
   *          the children to set
   */
  public void setChildren(List<MongoDBAttributeValue> children) {
    this.children = children;
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
