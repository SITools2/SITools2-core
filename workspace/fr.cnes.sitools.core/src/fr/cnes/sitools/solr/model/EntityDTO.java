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
package fr.cnes.sitools.solr.model;

import java.io.Serializable;
import java.util.List;

/**
 * DTO to represent an entity field
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
public final class EntityDTO implements Serializable {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = 2542591258787086854L;
  /**
   * Entity name
   */
  private String name;
  /**
   * Entity query
   */
  private String query;
  /**
   * List of fields associated to the Entity
   */
  private List<FieldDTO> fields = null;

  /**
   * Default constructor
   */
  public EntityDTO() {
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
   * Gets the query value
   * 
   * @return the query
   */
  public String getQuery() {
    return query;
  }

  /**
   * Sets the value of query
   * 
   * @param query
   *          the query to set
   */
  public void setQuery(String query) {
    this.query = query;
  }

  /**
   * Gets the fields value
   * 
   * @return the fields
   */
  public List<FieldDTO> getFields() {
    return fields;
  }

  /**
   * Sets the value of fields
   * 
   * @param fields
   *          the fields to set
   */
  public void setFields(List<FieldDTO> fields) {
    this.fields = fields;
  }

}
