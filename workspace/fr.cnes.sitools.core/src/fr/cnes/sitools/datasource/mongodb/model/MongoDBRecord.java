/*******************************************************************************
 * Copyright 2011 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Class for definition of a MongoDB database record
 * 
 * 
 * @author m.gond
 */
public class MongoDBRecord {
  /**
   * Record identifier
   */
  @XStreamAlias("uri")
  private String id;
  /**
   * Record values
   */
  private List<MongoDBAttributeValue> attributeValues;

  /**
   * Gets the id value
   * 
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of id
   * 
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the attributeValues value
   * 
   * @return the attributeValues
   */
  public List<MongoDBAttributeValue> getAttributeValues() {
    return attributeValues;
  }

  /**
   * Sets the value of attributeValue
   * 
   * @param attributeValues
   *          the attributeValues to set
   */
  public void setAttributeValues(List<MongoDBAttributeValue> attributeValues) {
    this.attributeValues = attributeValues;
  }

}
