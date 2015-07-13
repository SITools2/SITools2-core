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
package fr.cnes.sitools.plugins.filters.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.ExtensionParameter;

/**
 * Class for parameters of resources
 *
 * @author m.marseille (AKKA Technologies)
 */
@XStreamAlias("filterParameter")
public final class FilterParameter extends ExtensionParameter {
  
  /**
   * Type of the parameter
   */
  private FilterParameterType type;
  
  /**
   * Constructor
   */
  public FilterParameter() {
    super();
    this.type = FilterParameterType.PARAMETER_ATTACHMENT;
  }
  
  /**
   * Constructor
   * @param name the name of the parameter
   * @param description the description of the parameter
   * @param type the type of the parameter
   */
  public FilterParameter(String name, String description, FilterParameterType type) {
    super(name, description);
    this.type = type;
  }
  
  /**
   * Sets the value of type
   * @param type the type to set
   */
  public void setType(FilterParameterType type) {
    this.type = type;
  }

  /**
   * Gets the type value
   * @return the type
   */
  public FilterParameterType getType() {
    return type;
  }

}
