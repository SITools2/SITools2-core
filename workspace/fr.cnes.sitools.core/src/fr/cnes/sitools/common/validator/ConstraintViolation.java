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
package fr.cnes.sitools.common.validator;

/**
 * ConstraintViolation default implementation
 * 
 * 
 * @author m.gond
 */
public final class ConstraintViolation {
  /**
   * The message
   */
  private String message;
  /**
   * The invalidValue (value)
   */
  private String invalidValue;
  /**
   * The constraintViolationLevel
   */
  private ConstraintViolationLevel level;

  /**
   * The name of the value (the key of the column used)
   */
  private String valueName;

  /**
   * Gets the message of the violation
   * 
   * @return the message of the violation
   */
  public String getMessage() {
    return message;
  }

  /**
   * Gets the invalid value of the violation
   * 
   * @return the invalid value of the violation
   */
  public String getInvalidValue() {
    return invalidValue;
  }

  /**
   * Get the level of violation
   * 
   * @return the level of violation
   */
  public ConstraintViolationLevel getLevel() {
    return level;
  }

  /**
   * Sets the value of message
   * 
   * @param message
   *          the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Sets the value of invalidValue
   * 
   * @param invalidValue
   *          the invalidValue to set
   */
  public void setInvalidValue(String invalidValue) {
    this.invalidValue = invalidValue;
  }

  /**
   * Sets the value of level
   * 
   * @param level
   *          the level to set
   */
  public void setLevel(ConstraintViolationLevel level) {
    this.level = level;
  }

  /**
   * Sets the value of valueName
   * 
   * @param valueName
   *          the valueName to set
   */
  public void setValueName(String valueName) {
    this.valueName = valueName;
  }

  /**
   * Gets the valueName value
   * 
   * @return the valueName
   */
  public String getValueName() {
    return valueName;
  }

}
