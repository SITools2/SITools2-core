    /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.properties.model;

import fr.cnes.sitools.util.Property;

/**
 * Model object to store a Property but specific to a DataSet. It adds a type attribute of type DataSetPropertyType
 * 
 * 
 * @author m.gond
 */
public class SitoolsProperty extends Property {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -6588187166370526524L;

  /** The type of the property */
  private SitoolsPropertyType type;

  /**
   * Default constuctor
   */
  public SitoolsProperty() {
    super();
  }

  /**
   * Constructor with name, value, scope and type attributes
   * 
   * @param newName
   *          name
   * @param newValue
   *          value
   * @param newScope
   *          new scope
   * @param newType
   *          the DataSetPropertyType
   */
  public SitoolsProperty(String newName, String newValue, String newScope, SitoolsPropertyType newType) {
    super(newName, newValue, newScope);
    this.setType(newType);

  }

  /**
   * Sets the value of type
   * 
   * @param type
   *          the type to set
   */
  public void setType(SitoolsPropertyType type) {
    this.type = type;
  }

  /**
   * Gets the type value
   * 
   * @return the type
   */
  public SitoolsPropertyType getType() {
    return type;
  }

}
