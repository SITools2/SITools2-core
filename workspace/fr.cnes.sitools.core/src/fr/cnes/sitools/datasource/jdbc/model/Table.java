/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Base class for database table representation
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
@XStreamAlias("table")
public final class Table {

  /**
   * URL for table resource exposition
   */
  private String url;

  /**
   * Table name
   */
  private String name;

  /**
   * Table alias
   */
  private String alias;

  /**
   * Schema name
   */
  private String schema;

  /**
   * Attribute list
   */
  private List<Attribute> attributes = null;

  /**
   * Default constructor
   */
  public Table() {
    super();
  }

  /**
   * Constructor with name
   * 
   * @param name
   *          the table name
   */
  public Table(String name) {
    super();
    this.name = name;
  }

  /**
   * Constructor with name and schema and alias
   * 
   * @param name
   *          table name
   * @param schema
   *          database schema
   * @param alias
   *          table alias
   */
  public Table(String name, String schema, String alias) {
    this.name = name;
    this.schema = schema;
    this.alias = alias;
  }

  /**
   * Constructor with name and schema
   * 
   * @param name
   *          table name
   * @param schema
   *          table schema
   */
  public Table(String name, String schema) {
    this.name = name;
    this.schema = schema;
  }

  /**
   * Gets the URL value
   * 
   * @return the URL
   */
  public String getUrl() {
    return url;
  }

  /**
   * Sets the value of URL
   * 
   * @param url
   *          the URL to set
   */
  public void setUrl(String url) {
    this.url = url;
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
   * Gets the schema value
   * 
   * @return the schema
   */
  public String getSchema() {
    return schema;
  }

  /**
   * Get the alias
   * 
   * @return the alias
   */
  public String getAlias() {
    return alias;
  }

  /**
   * Set the alias
   * 
   * @param alias
   *          the alias to set
   */
  public void setAlias(String alias) {
    this.alias = alias;
  }

  /**
   * Sets the value of schema
   * 
   * @param schema
   *          the schema to set
   */
  public void setSchema(String schema) {
    this.schema = schema;
  }

  /**
   * Sets the value of attributes
   * 
   * @param attributes
   *          the attributes to set
   */
  public void setAttributes(List<Attribute> attributes) {
    this.attributes = attributes;
  }

  /**
   * Get table attributes
   * 
   * @return a list of attributes
   */
  public List<Attribute> getAttributes() {
    if (attributes == null) {
      attributes = new ArrayList<Attribute>();
    }
    return attributes;
  }

  /**
   * Get the table reference
   * 
   * @return table reference schema
   */
  @JsonIgnore
  public String getReference() {
    return (schema != null) ? (schema + "." + name) : name;
  }

  /**
   * Get the table from a reference
   * 
   * @return the table referenced
   */
  @JsonIgnore
  public String getFROMReference() {
    String result = "";
    result = (schema != null) ? (schema + ".\"" + name + "\"") : name;
    result += (alias != null) ? " as " + alias : "";
    return result;
  }

}
