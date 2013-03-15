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
package fr.cnes.sitools.form.dataset.dto;

import java.util.List;

/**
 * DTO value description
 * 
 * @author AKKA (OpenWiz)
 */
public final class ValueDTO {

  /**
   * Comment for <code>code</code>
   */
  private String code;

  /**
   * Comment for <code>selected</code>
   */
  private boolean selected;

  /**
   * Comment for <code>value</code>
   */
  private String value;

  /**
   * Comment for <code>availableFor</code>
   */
  private List<String> availableFor;

  /**
   * Comment for defaultValue
   */
  private Boolean defaultValue;

  /**
   * Return the code of the value
   * 
   * @return the code
   */
  public String getCode() {
    return code;
  }

  /**
   * Set the code of the value
   * 
   * @param code
   *          the code to set
   */
  public void setCode(final String code) {
    this.code = code;
  }

  /**
   * Get if the DTO is selected or not
   * 
   * @return the selected
   */
  public boolean isSelected() {
    return selected;
  }

  /**
   * Set if the DTO is selected or not
   * 
   * @param selected
   *          the selected to set
   */
  public void setSelected(final boolean selected) {
    this.selected = selected;
  }

  /**
   * Get the DTO value
   * 
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * Set the DTO value
   * 
   * @param value
   *          the value to set
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Get the list where the DTO is available
   * 
   * @return the availableFor
   */
  public List<String> getAvailableFor() {
    return availableFor;
  }

  /**
   * Sets the list where the DTO is available
   * 
   * @param availableFor
   *          the availableFor to set
   */
  public void setAvailableFor(List<String> availableFor) {
    this.availableFor = availableFor;
  }

  /**
   * Gets the default value
   * 
   * @return the defaultValue
   */
  public Boolean getDefaultValue() {
    return defaultValue;
  }

  /**
   * Sets the default value
   * 
   * @param defaultValue
   *          the defaultValue to set
   */
  public void setDefaultValue(Boolean defaultValue) {
    this.defaultValue = defaultValue;
  }

}
