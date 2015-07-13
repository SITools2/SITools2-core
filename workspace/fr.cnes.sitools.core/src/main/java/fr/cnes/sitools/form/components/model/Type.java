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
package fr.cnes.sitools.form.components.model;

/**
 * Parameter types
 * 
 * @author AKKA
 * 
 */
public enum Type {
  /**
   * Numeric type
   */
  NUMERIC("numeric"),
  /**
   * String type
   */
  STRING("string");

  /**
   * Label of the
   */
  private String label;

  /**
   * Set the type
   * 
   * @param type
   *          to set
   */
  private Type(String type) {
    this.label = type;
  }

  /**
   * Get the value
   * 
   * @return the value
   */
  public String value() {
    return label;
  }

}
