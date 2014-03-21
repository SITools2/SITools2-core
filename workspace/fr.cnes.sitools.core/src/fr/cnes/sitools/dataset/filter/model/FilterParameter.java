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
package fr.cnes.sitools.dataset.filter.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.ExtensionParameter;

/**
 * A converter parameter contains informations about a parameter in a filter
 * 
 * @author AKKA
 * 
 */
@XStreamAlias("filterParameter")
public final class FilterParameter extends ExtensionParameter {

  /**
   * Type of the parameter
   */
  private FilterParameterType parameterType;

  /**
   * Column mapped by the parameter
   */
  private String attachedColumn;

  /**
   * Constructor
   */
  public FilterParameter() {
    super();
  }

  /**
   * Constructor
   * 
   * @param n
   *          name of the parameter
   * @param d
   *          description of the parameter
   * @param pt
   *          type of the parameter
   */
  public FilterParameter(String n, String d, FilterParameterType pt) {
    super(n, d);
    this.parameterType = pt;
  }

  /**
   * Sets the value of parameter_type
   * 
   * @param paramType
   *          the parameter_type to set
   */
  public void setParameterType(FilterParameterType paramType) {
    this.parameterType = paramType;
  }

  /**
   * Gets the attachedColumn value
   * 
   * @return the attachedColumn
   */
  public String getAttachedColumn() {
    return attachedColumn;
  }

  /**
   * Sets the value of attachedColumn
   * 
   * @param attachedColumn
   *          the attachedColumn to set
   */
  public void setAttachedColumn(String attachedColumn) {
    this.attachedColumn = attachedColumn;
  }

  /**
   * Attach the given column name to parameter
   * 
   * @param cn
   *          the column name
   */
  public void attachColumnName(String cn) {
    this.attachedColumn = cn;
  }

  /**
   * Get the parameter type
   * 
   * @return the parameter type
   */
  public FilterParameterType getParameterType() {
    return this.parameterType;
  }
 
  
}
