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
package fr.cnes.sitools.common.model;

/**
 * 
 * Base class for extension parameters
 *
 * @author m.marseille (AKKA Technologies)
 */
public abstract class ExtensionParameter {
  
  /**
   * Name of the parameter;
   */
  private String name;

  /**
   * Description of the parameter
   */
  private String description;


  /**
   * Value of the parameter if internal
   */
  private String value;
  
  /**
   * Type of the value
   */
  private String valueType;

  /**
   * Constructor
   */
  public ExtensionParameter() {
    this("", "");
  }

  /**
   * Constructor
   * 
   * @param n
   *          name of the parameter
   * @param d
   *          description of the parameter
   */
  public ExtensionParameter(String n, String d) {
    super();
    this.name = n;
    this.description = d;
    this.valueType = "xs:string";
    this.value = "";
  }


  /**
   * Sets the value of name
   * 
   * @param name
   *          the name to set
   */
  public final void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the value of description
   * 
   * @param description
   *          the description to set
   */
  public final void setDescription(String description) {
    this.description = description;
  }

  /**
   * Get the parameter name
   * 
   * @return the name of the parameter
   */
  public final String getName() {
    return this.name;
  }

  /**
   * Get the parameter description
   * 
   * @return the description of the parameter
   */
  public final String getDescription() {
    return this.description;
  }

  /**
   * Get the value of the internal parameter
   * 
   * @return the value of the parameter
   */
  public final String getValue() {
    return this.value;
  }
  
  /**
   * Set the value
   * @param value the value to set
   */
  public final void setValue(String value) {
    this.value = value;
  }
  

  /**
   * Gets the valueType value
   * 
   * @return the valueType
   */
  public final String getValueType() {
    return valueType;
  }

  /**
   * Sets the value of valueType
   * @param valueType the valueType to set
   */
  public final void setValueType(String valueType) {
    this.valueType = valueType;
  }
  

}
