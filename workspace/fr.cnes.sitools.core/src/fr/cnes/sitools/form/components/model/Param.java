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
package fr.cnes.sitools.form.components.model;

/**
 * the parameters name available
 * 
 * @author c.mozdzierz
 * 
 */
public enum Param {

  /**
   * The list of parameters
   */
  COMPARISON("[data][comparison]"),
  /**
   * Type parameters
   */
  TYPE("[data][type]"),
  /**
   * Value parameters
   */
  VALUE("[data][value]"),
  /**
   * Field parameters
   */
  COLUMN_ALIAS("[columnAlias]"),
  /**
   * Table name parameters
   */
  TABLENAME("[tableName]"),
  /**
   * Table alias parameter
   */
  TABLEALIAS("[tableAlias]");

  /**
   * The label of the parameter
   */
  private String label;

  /**
   * the constructor
   * 
   * @param param
   *          the label
   */
  private Param(String param) {
    this.label = param;
  }

  /**
   * Get the label
   * 
   * @return the label
   */
  public String value() {
    return label;
  }

}
