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
package fr.cnes.sitools.dataset.converter.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.ExtensionParameter;

/**
 * A converter parameter contains informations about a parameter in a converter
 * 
 * @author AKKA
 * 
 */
@XStreamAlias("converterParameter")
public final class ConverterParameter extends ExtensionParameter {

  /**
   * Type of the parameter
   */
  private ConverterParameterType parameterType;

  /**
   * Column mapped by the parameter
   */
  private String attachedColumn;

  /**
   * Constructor
   */
  public ConverterParameter() {
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
  public ConverterParameter(String n, String d, ConverterParameterType pt) {
    super(n, d);
    this.parameterType = pt;
  }

  /**
   * Gets the parameter_type value
   * 
   * @return the parameter_type
   */
  public ConverterParameterType getParameterType() {
    return parameterType;
  }

  /**
   * Sets the value of parameter_type
   * 
   * @param paramType
   *          the parameter_type to set
   */
  public void setParameterType(ConverterParameterType paramType) {
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

}
