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
 * 
 * Class for definition of a generic structure from a data source.
 * Multiple types : JDBC Table / JDBC View ...
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
@XStreamAlias("structure")
public final class Structure {
  /** Table alias */
  private String alias = null;

  /** Structure type (view - table) */
  private String type = null;

  /** Database schema */
  private String schemaName = null;

  /** Structure name */
  private String name = null;

  /**
   * Default constructor
   */
  public Structure() {
    super();
  }

  /**
   * Constructor
   * 
   * @param alias
   *          alias
   * @param table
   *          table name
   */
  public Structure(String alias, String table) {
    super();
    this.alias = alias;
    this.name = table;
  }

  /**
   * Gets the alias value
   * 
   * @return the alias
   */
  public String getAlias() {
    return alias;
  }

  /**
   * Sets the value of alias
   * 
   * @param alias
   *          the alias to set
   */
  public void setAlias(String alias) {
    this.alias = alias;
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
   * Gets the schemaName value
   * 
   * @return the schemaName
   */
  public String getSchemaName() {
    return schemaName;
  }

  /**
   * Sets the value of schemaName
   * 
   * @param schemaName
   *          the schemaName to set
   */
  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
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

}
