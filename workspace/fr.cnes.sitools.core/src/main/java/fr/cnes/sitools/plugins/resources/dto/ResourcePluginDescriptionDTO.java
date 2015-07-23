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
package fr.cnes.sitools.plugins.resources.dto;

import fr.cnes.sitools.common.model.AbstractPluginsDescriptionDTO;
import fr.cnes.sitools.plugins.resources.model.DataSetSelectionType;
import fr.cnes.sitools.plugins.resources.model.ResourceBehaviorType;

/**
 * DTO for ResourcePlugin
 * 
 * 
 * @author m.gond
 */
public class ResourcePluginDescriptionDTO extends AbstractPluginsDescriptionDTO {

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
   * Default constructor
   */
  public ResourcePluginDescriptionDTO() {
    super();

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
   * @return the behavior
   */
  public ResourceBehaviorType getBehavior() {
    return behavior;
  }

  /**
   * Sets the value of behavior
   * @param behavior the behavior to set
   */
  public void setBehavior(ResourceBehaviorType behavior) {
    this.behavior = behavior;
  }

}
