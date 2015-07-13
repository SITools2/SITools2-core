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
package fr.cnes.sitools.util;

import java.io.Serializable;

/**
 * Basic Key Value Class
 * 
 * 
 * @author d.arpin (AKKA Technologies)
 */
public class Property implements Serializable {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -5002601828479391524L;

  /**
   * The Property Name
   */
  private String name;

  /**
   * The Property Value
   */
  private String value;

  /**
   * Scope
   */
  private String scope;

  /**
   * Basic Constructor
   */
  public Property() {
    super();
  }

  /**
   * Complete Constructor
   * 
   * @param newName
   *          name
   * @param newValue
   *          value
   * @param newScope
   *          new scope
   */
  public Property(String newName, String newValue, String newScope) {
    name = newName;
    value = newValue;
    scope = newScope;
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
  public String getValue() {
    return value;
  }

  /**
   * Sets the value of value
   * 
   * @param value
   *          the value to set
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Gets the scope value
   * 
   * @return the scope
   */
  public String getScope() {
    return scope;
  }

  /**
   * Sets the value of scope
   * 
   * @param scope
   *          the scope to set
   */
  public void setScope(String scope) {
    this.scope = scope;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((scope == null) ? 0 : scope.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Property other = (Property) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    }
    else if (!name.equals(other.name)) {
      return false;
    }
    if (scope == null) {
      if (other.scope != null) {
        return false;
      }
    }
    else if (!scope.equals(other.scope)) {
      return false;
    }
    if (value == null) {
      if (other.value != null) {
        return false;
      }
    }
    else if (!value.equals(other.value)) {
      return false;
    }
    return true;
  }

}
