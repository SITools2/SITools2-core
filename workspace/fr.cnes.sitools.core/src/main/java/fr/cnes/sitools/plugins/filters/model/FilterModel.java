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

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.ExtensionModel;
import fr.cnes.sitools.common.validator.Validable;
import fr.cnes.sitools.common.validator.Validator;

/**
 * Base class for customizable filter model
 * 
 * @author m.marseille (AKKA Technologies)
 */
@XStreamAlias("filterPlugin")
public class FilterModel extends ExtensionModel<FilterParameter> implements Validable, Serializable {

  /**
   * SeriaVersionID to allow serialization within ObjectRepresentation
   */
  private static final long serialVersionUID = 8852188703992346122L;

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
  public FilterModel() {
    setClassName(this.getClass().getCanonicalName());
  }

  /**
   * Method to add a parameter in subclasses
   * 
   * @param param
   *          the parameter to add
   */
  public final void addParam(FilterParameter param) {
    getParametersMap().put(param.getName(), param);
  }

  /**
   * Return the parameter by name
   * 
   * @param name
   *          the parameter name
   * @return the corresponding parameter
   */
  public final FilterParameter getParameterByName(String name) {
    return getParametersMap().get(name);
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

  @JsonIgnore
  public Validator<FilterModel> getValidator() {
    return null;
  }

}
