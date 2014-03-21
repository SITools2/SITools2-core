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
package fr.cnes.sitools.plugins.resources.model;

import java.util.HashMap;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.restlet.Context;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.ExtensionModel;
import fr.cnes.sitools.common.validator.Validable;
import fr.cnes.sitools.common.validator.Validator;

/**
 * Base class for Parameterized resources model
 * 
 * @author m.marseille (AKKA Technologies)
 */
@XStreamAlias("resourcePlugin")
public class ResourceModel extends ExtensionModel<ResourceParameter> implements Validable {

  /**
   * Parent of the resource
   */
  private String parent;

  /**
   * The name of the class implementing the Resource
   */
  private String resourceClassName;

  /**
   * The name of the application class authorized to attach this ressource
   */
  private String applicationClassName;

  /**
   * The type of selection authorized on a dataset
   */
  private DataSetSelectionType dataSetSelection = DataSetSelectionType.NONE;

  /**
   * The behavior when using the resource in Sitools IHM
   */
  private ResourceBehaviorType behavior = ResourceBehaviorType.DISPLAY_IN_NEW_TAB;

  /**
   * Constructor Store the class name
   */
  public ResourceModel() {
    super();
    this.setParametersMap(new HashMap<String, ResourceParameter>());
    setClassName(this.getClass().getCanonicalName());
    ResourceParameter urlAttach = new ResourceParameter("url", "attachment url",
        ResourceParameterType.PARAMETER_ATTACHMENT);
    urlAttach.setValue("/plugin"); // default value
    this.addParam(urlAttach);

    /** List of implemented methods separate by | */
    ResourceParameter implementedMethods = new ResourceParameter("methods",
        "List of methods implemented for this resource, separate by |", ResourceParameterType.PARAMETER_INTERN);
    String valueType = "xs:enum-multiple[GET, POST, PUT, DELETE]";
    implementedMethods.setValueType(valueType);
    implementedMethods.setValue("GET"); // default value
    this.addParam(implementedMethods);

    ResourceParameter fileName = new ResourceParameter(
        "fileName",
        "The name of the file to generate. Fill it to download a file. Leave it Empty to view resource Representation.",
        ResourceParameterType.PARAMETER_USER_INPUT);
    fileName.setValueType("xs:template");
    fileName.setValue(""); // default value
    this.addParam(fileName);

    ResourceParameter image = new ResourceParameter("image", "The image url", ResourceParameterType.PARAMETER_INTERN);
    image.setValue(""); // default value
    image.setValueType("xs:image");
    this.addParam(image);

  }

  /**
   * Override this method to add parameters when the Context is needed Those parameters will be seen by the
   * administrator when configuring a ResourceModel. WARNING: This method is only called for administration purpose.
   * 
   * 
   * @param context
   *          The Context contains some attributes : ContextAttributes.SETTINGS : The SitoolsSettings parent : the
   *          parent application id appClassName : the parent application class name
   */
  public void initParametersForAdmin(Context context) {
  }

  /**
   * Method to add a parameter to the ResourceModel. The name of the parameter in used as the Map's Key
   * 
   * @param param
   *          the parameter to add
   */
  public final void addParam(ResourceParameter param) {
    getParametersMap().put(param.getName(), param);
    param.setSequence(getParametersMap().size());
  }

  /**
   * Return a parameter by its name or null if the parameter cannot be found
   * 
   * 
   * @param name
   *          the parameter name
   * @return the corresponding parameter or null if it cannot be found
   */
  public final ResourceParameter getParameterByName(String name) {
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
   * Sets the name of the class implementing the Resource
   * 
   * @param resourceClassName
   *          the resourceClassName to set
   */
  public final void setResourceClassName(String resourceClassName) {
    this.resourceClassName = resourceClassName;
  }

  /**
   * Gets the name of the class implementing the Resource
   * 
   * @return the name of the class implementing the Resource
   */
  public final String getResourceClassName() {
    return resourceClassName;
  }

  @Override
  @JsonIgnore
  public Validator<ResourceModel> getValidator() {
    return null;
  }

  /**
   * Method to complete the initial default url attachment
   * 
   * @param path
   *          the path for completion
   */
  public final void completeAttachUrlWith(String path) {
    ResourceParameter param = this.getParameterByName("url");
    param.setValue(param.getValue() + path);
  }

  /**
   * Sets the class name of the application to which the Resource can be attached The class name must be complete (with
   * full package declaration)
   * 
   * @param applicationClassName
   *          the applicationClassName to set
   */
  public void setApplicationClassName(String applicationClassName) {
    this.applicationClassName = applicationClassName;
  }

  /**
   * Gets the applicationClassName value
   * 
   * @return the applicationClassName
   */
  public String getApplicationClassName() {
    return applicationClassName;
  }

  /**
   * Sets the value of dataSetSelection. It is use to tell whether or not this Resource can be applied on selection of
   * records in the client interface.
   * <p>
   * It is only used for datasets resources and in the client interface. Only SINGLE, MULTIPLE, ALL resources are
   * displayed in the client interface
   * </p>
   * 
   * @param dataSetSelection
   *          the dataSetSelection to set
   */
  public void setDataSetSelection(DataSetSelectionType dataSetSelection) {
    this.dataSetSelection = dataSetSelection;
  }

  /**
   * Gets the dataSetSelection value
   * 
   * @return the dataSetSelection
   */
  public DataSetSelectionType getDataSetSelection() {
    return dataSetSelection;
  }

  /**
   * Gets the behavior value.It is use to tell the client interface behavior after calling the resource.
   * 
   * @return the behavior
   */
  public ResourceBehaviorType getBehavior() {
    return behavior;
  }

  /**
   * Sets the value of behavior
   * 
   * @param behavior
   *          the behavior to set
   */
  public void setBehavior(ResourceBehaviorType behavior) {
    this.behavior = behavior;
  }

  /**
   * Gets the image value
   * 
   * @return the image
   */
  @JsonIgnore
  public String getImage() {
    ResourceParameter image = this.getParameterByName("image");
    if (image != null) {
      return image.getValue();
    }
    else {
      return null;
    }
  }

  /**
   * Sets the value of image
   * 
   * @param image
   *          the image to set
   */
  @JsonIgnore
  public void setImage(String image) {
    ResourceParameter imageParam = this.getParameterByName("image");
    if (imageParam != null) {
      imageParam.setValue(image);
    }
  }

}
