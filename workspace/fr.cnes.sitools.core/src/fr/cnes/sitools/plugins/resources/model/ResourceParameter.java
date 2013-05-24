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
/**
 * 
 */
package fr.cnes.sitools.plugins.resources.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.ExtensionParameter;

/**
 * Class for parameters of resources
 * 
 * @author m.marseille (AKKA Technologies)
 */
@XStreamAlias("resourceParameter")
public final class ResourceParameter extends ExtensionParameter {

  /**
   * Type of the parameter
   */
  private ResourceParameterType type;

  /**
   * An object can be useful at runtime for dynamic parameters
   */
  private Object valueObject;

  /**
   * To know if the user should update this parameter
   */
  private Boolean userUpdatable;

  /**
   * sequence in order to sort parameters in a collection/map
   */
  private int sequence;

  /**
   * Constructor
   */
  public ResourceParameter() {
    super();
    this.type = ResourceParameterType.PARAMETER_ATTACHMENT;
  }

  /**
   * Constructor by copy
   * 
   * @param clone
   *          ResourceParameter
   */
  public ResourceParameter(ResourceParameter clone) {
    super();
    this.setName(clone.getName());
    this.setDescription(clone.getDescription());
    this.setType(clone.getType());
    this.setValueType(clone.getValueType());
    this.setValue(clone.getValue());
    this.setValueObject(clone.getValueObject());
    this.setUserUpdatable(clone.getUserUpdatable());
    this.setSequence(clone.getSequence());
  }

  /**
   * Constructor
   * 
   * @param name
   *          the name of the parameter
   * @param description
   *          the description of the parameter
   * @param type
   *          the type of the parameter
   */
  public ResourceParameter(String name, String description, ResourceParameterType type) {
    super(name, description);
    this.type = type;
  }

  /**
   * Sets the value of type
   * 
   * @param type
   *          the type to set
   */
  public void setType(ResourceParameterType type) {
    this.type = type;
  }

  /**
   * Gets the type value
   * 
   * @return the type
   */
  public ResourceParameterType getType() {
    return type;
  }

  /**
   * Gets the object value
   * 
   * @return the object
   */
  public Object getValueObject() {
    return valueObject;
  }

  /**
   * Sets the object
   * 
   * @param valueObject
   *          the object to set
   */
  public void setValueObject(Object valueObject) {
    this.valueObject = valueObject;
  }

  /**
   * Gets the userUpdatable value
   * 
   * @return the userUpdatable
   */
  public Boolean getUserUpdatable() {
    return userUpdatable;
  }

  /**
   * Sets the value of userUpdatable
   * 
   * @param userUpdatable
   *          the userUpdatable to set
   */
  public void setUserUpdatable(Boolean userUpdatable) {
    this.userUpdatable = userUpdatable;
  }

  /**
   * Gets the sequence value
   * 
   * @return the sequence
   */
  public int getSequence() {
    return sequence;
  }

  /**
   * Sets the value of sequence
   * 
   * @param sequence
   *          the sequence to set
   */
  public void setSequence(int sequence) {
    this.sequence = sequence;
  }

}
