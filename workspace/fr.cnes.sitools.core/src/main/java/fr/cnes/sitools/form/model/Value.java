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
package fr.cnes.sitools.form.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Value of a parameter
 * 
 * @author BAILLAGOU
 * @version 1.0 01-09-2010 08:59:07
 */
@XStreamAlias("valueItem")
public final class Value {

  /**
   * Code of a parameter
   */
  private String code;

  /**
   * Say yes if selected
   */
  private boolean selected;

  /**
   * Value
   */
  private String value;

  /**
   * List of things where the parameters is available
   */
  private List<Value> availableFor;

  /**
   * Default value of an form parameter
   */
  private Boolean defaultValue;

  /**
   * Constructor
   */
  public Value() {
    super();
  }

  /**
   * Get the ID
   * 
   * @return the id
   */
  public String getCode() {
    return code;
  }

  /**
   * Set the ID
   * 
   * @param id
   *          the id to set
   */
  public void setCode(final String id) {
    this.code = id;
  }

  /**
   * Get if selected
   * 
   * @return the selected
   */
  public boolean isSelected() {
    return selected;
  }

  /**
   * Set if selected
   * 
   * @param selected
   *          the selected to set
   */
  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  /**
   * Get the value
   * 
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * Set the value
   * 
   * @param value
   *          the value to set
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Get the list
   * 
   * @return a list of available for things
   */
  public List<Value> getAvailableFor() {
    return availableFor;
  }

  /**
   * Set the list of available for things
   * 
   * @param availableFor
   *          the list
   */
  public void setAvailableFor(List<Value> availableFor) {
    this.availableFor = availableFor;
  }

  /**
   * Gets default value
   * 
   * @return the defaultValue
   */
  public Boolean getDefaultValue() {
    return defaultValue;
  }

  /**
   * Sets default value
   * 
   * @param defaultValue
   *          the defaultValue to set
   */
  public void setDefaultValue(Boolean defaultValue) {
    this.defaultValue = defaultValue;
  }

}
