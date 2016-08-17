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
package fr.cnes.sitools.plugins.filters.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.dto.ExtensionModelDTO;
import fr.cnes.sitools.plugins.filters.model.FilterParameter;

/**
 * Base class for customizable filter model
 * 
 * @author m.marseille (AKKA Technologies)
 */
@XStreamAlias("filterPlugin")
public class FilterModelDTO extends ExtensionModelDTO<FilterParameter> {

  /** serialVersionUID */
  private static final long serialVersionUID = -7520741779129552493L;

  /**
   * Parent of the filter
   */
  private String parent;

  /**
   * Model class name
   */
  private String filterClassName;

  /**
   * Constructor Store the class name
   */
  public FilterModelDTO() {
    setClassName(this.getClass().getCanonicalName());
  }

  /**
   * Sets the value of parent
   * 
   * @param parent
   *          the parent to set
   */
  public final void setParent(String parent) {
    this.parent = parent;
  }

  /**
   * Gets the parent value
   * 
   * @return the parent
   */
  public final String getParent() {
    return parent;
  }

  /**
   * Sets the value of filterClassName
   * 
   * @param filterClassName
   *          the filterClassName to set
   */
  public final void setFilterClassName(String filterClassName) {
    this.filterClassName = filterClassName;
  }

  /**
   * Gets the filterClassName value
   * 
   * @return the filterClassName
   */
  public final String getFilterClassName() {
    return filterClassName;
  }

}
