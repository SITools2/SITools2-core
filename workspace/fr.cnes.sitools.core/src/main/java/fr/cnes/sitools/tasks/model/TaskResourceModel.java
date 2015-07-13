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
package fr.cnes.sitools.tasks.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;

/**
 * The Model for TaskResource
 * 
 * 
 * @author m.gond
 */
public class TaskResourceModel extends ResourceModel {
  /** RunType parameter name Administration */
  public static final String RUN_TYPE_PARAM_NAME_ADMINISTATION = "runTypeAdministration";
  /** RunType parameter name Client */
  public static final String RUN_TYPE_PARAM_NAME_USER_INPUT = "runTypeUserInput";
  /** Resource implementation class name */
  public static final String RESOURCE_IMPL_PARAM_NAME = "resourceImplClassName";

  /**
   * Default constructor
   */
  public TaskResourceModel() {
    super();
    ResourceParameter runTypeAdministration = new ResourceParameter(RUN_TYPE_PARAM_NAME_ADMINISTATION,
        "The intern run type", ResourceParameterType.PARAMETER_INTERN);
    runTypeAdministration.setValue(TaskRunTypeAdministration.TASK_DEFAULT_RUN_SYNC.toString()); // default value
    String valueTypeRunType = "xs:enum[";
    boolean start = true;
    for (TaskRunTypeAdministration item : TaskRunTypeAdministration.values()) {
      if (!start) {
        valueTypeRunType += ",";
      }
      else {
        start = false;
      }
      valueTypeRunType += item;
    }
    valueTypeRunType += "]";
    runTypeAdministration.setValueType(valueTypeRunType);
    this.addParam(runTypeAdministration);

    ResourceParameter runTypeUserInput = new ResourceParameter(RUN_TYPE_PARAM_NAME_USER_INPUT,
        "The user input run type", ResourceParameterType.PARAMETER_USER_INPUT);
    start = false;
    valueTypeRunType = "xs:enum[";
    for (TaskRunTypeUserInput item : TaskRunTypeUserInput.values()) {
      if (!start) {
        valueTypeRunType += ",";
      }
      else {
        start = false;
      }
      valueTypeRunType += item;
    }
    valueTypeRunType += "]";
    runTypeUserInput.setValueType(valueTypeRunType);
    this.addParam(runTypeUserInput);

    ResourceParameter resourceImplClassName = new ResourceParameter(RESOURCE_IMPL_PARAM_NAME,
        "The name of the resource implementation", ResourceParameterType.PARAMETER_INTERN);
    resourceImplClassName.setValue(""); // default value
    this.addParam(resourceImplClassName);

  }

  /**
   * Gets the runTypeAdministration value
   * 
   * @return the runType
   */
  @JsonIgnore
  public TaskRunTypeAdministration getRunTypeAdministration() {
    ResourceParameter runType = this.getParameterByName(RUN_TYPE_PARAM_NAME_ADMINISTATION);
    if (runType != null) {
      return TaskRunTypeAdministration.valueOf(runType.getValue());
    }
    else {
      return null;
    }
  }

  /**
   * Sets the value of runTypeAdministration
   * 
   * @param runType
   *          the runType to set
   */
  @JsonIgnore
  public void setRunTypeAdministration(TaskRunTypeAdministration runType) {
    ResourceParameter runTypeParam = this.getParameterByName(RUN_TYPE_PARAM_NAME_ADMINISTATION);
    if (runTypeParam != null) {
      runTypeParam.setValue(runType.toString());
    }
  }

  /**
   * Gets the runTypeClient value
   * 
   * @return the runType
   */
  @JsonIgnore
  public TaskRunTypeAdministration getRunTypeClient() {
    ResourceParameter runType = this.getParameterByName(RUN_TYPE_PARAM_NAME_USER_INPUT);
    if (runType != null) {
      return TaskRunTypeAdministration.valueOf(runType.getValue());
    }
    else {
      return null;
    }
  }

  /**
   * Sets the value of runTypeClient
   * 
   * @param runType
   *          the runType to set
   */
  @JsonIgnore
  public void setRunTypeClient(TaskRunTypeAdministration runType) {
    ResourceParameter runTypeParam = this.getParameterByName(RUN_TYPE_PARAM_NAME_USER_INPUT);
    if (runTypeParam != null) {
      runTypeParam.setValue(runType.toString());
    }
  }

  /**
   * Gets the the name of the class used as implementation of the task resource
   * 
   * @return the name of the class used as implementation of the task resource
   */
  @JsonIgnore
  public String getResourceImplClassName() {
    ResourceParameter res = this.getParameterByName(RESOURCE_IMPL_PARAM_NAME);
    if (res != null) {
      return res.getValue();
    }
    else {
      return null;
    }
  }

  /**
   * Sets the name of the class used as implementation of the task resource
   * 
   * @param resourceImplClassName
   *          the name of the class to set
   */
  @JsonIgnore
  public void setResourceImplClassName(String resourceImplClassName) {
    ResourceParameter resParam = this.getParameterByName(RESOURCE_IMPL_PARAM_NAME);
    if (resParam != null) {
      resParam.setValue(resourceImplClassName);
    }
  }

}
