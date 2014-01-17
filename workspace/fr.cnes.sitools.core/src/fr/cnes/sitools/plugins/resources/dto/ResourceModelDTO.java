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
package fr.cnes.sitools.plugins.resources.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import fr.cnes.sitools.common.dto.ExtensionModelDTO;
import fr.cnes.sitools.plugins.resources.model.DataSetSelectionType;
import fr.cnes.sitools.plugins.resources.model.ResourceBehaviorType;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;

/**
 * DTO for ResourceModel
 * 
 * 
 * @author m.gond
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ResourceModelDTO extends ExtensionModelDTO<ResourceParameter> {

  /** serialVersionUID */
  private static final long serialVersionUID = -7835909007056431242L;

  /**
   * Parent of the resource
   */
  private String parent;

  /**
   * Model class name
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
   * Gets the parent value
   * 
   * @return the parent
   */
  public String getParent() {
    return parent;
  }

  /**
   * Sets the value of parent
   * 
   * @param parent
   *          the parent to set
   */
  public void setParent(String parent) {
    this.parent = parent;
  }

  /**
   * Gets the resourceClassName value
   * 
   * @return the resourceClassName
   */
  public String getResourceClassName() {
    return resourceClassName;
  }

  /**
   * Sets the value of resourceClassName
   * 
   * @param resourceClassName
   *          the resourceClassName to set
   */
  public void setResourceClassName(String resourceClassName) {
    this.resourceClassName = resourceClassName;
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
   * Sets the value of applicationClassName
   * 
   * @param applicationClassName
   *          the applicationClassName to set
   */
  public void setApplicationClassName(String applicationClassName) {
    this.applicationClassName = applicationClassName;
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
   * Sets the value of dataSetSelection
   * 
   * @param dataSetSelection
   *          the dataSetSelection to set
   */
  public void setDataSetSelection(DataSetSelectionType dataSetSelection) {
    this.dataSetSelection = dataSetSelection;
  }

  /**
   * Gets the behavior value
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
   * Transform a ResourceModel to a ResourceModelDTO
   * 
   * @param resource
   *          the ResourceModel to transform
   * @return the ResourceModelDTO corresponding to the ResourceModel
   */
  public static ResourceModelDTO resourceModelToDTO(ResourceModel resource) {
    ResourceModelDTO current = new ResourceModelDTO();
    // common ExtensionModelDTO attributes
    current.setId(resource.getId());
    current.setName(resource.getName());
    current.setDescription(resource.getDescription());
    current.setClassAuthor(resource.getClassAuthor());
    current.setClassVersion(resource.getClassVersion());
    current.setClassName(resource.getClassName());
    current.setClassOwner(resource.getClassOwner());
    // current.setCurrentClassAuthor(resource.getCurrentClassVersion());
    // current.setCurrentClassVersion(resource.getCurrentClassVersion());

    List<ResourceParameter> parameters = new ArrayList<ResourceParameter>(resource.getParametersMap().values());
    Collections.sort(parameters, new Comparator<ResourceParameter>() {

      @Override
      public int compare(ResourceParameter o1, ResourceParameter o2) {
        return new Integer(o1.getSequence()).compareTo(o2.getSequence());
      }
    });

    current.setParameters(parameters);
    current.setDescriptionAction(resource.getDescriptionAction());
    // specific ResourceModelDTO attributes
    current.setApplicationClassName(resource.getApplicationClassName());
    current.setDataSetSelection(resource.getDataSetSelection());
    current.setParent(resource.getParent());
    current.setResourceClassName(resource.getResourceClassName());

    current.setDataSetSelection(resource.getDataSetSelection());
    current.setBehavior(resource.getBehavior());

    return current;
  }
}
