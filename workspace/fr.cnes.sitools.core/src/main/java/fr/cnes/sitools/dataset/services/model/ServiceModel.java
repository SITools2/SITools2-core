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
package fr.cnes.sitools.dataset.services.model;

import fr.cnes.sitools.plugins.resources.model.DataSetSelectionType;

/**
 * Model class to represent a Service on a dataset. It can be either a GUI service or a Server service. This model class
 * contains the common fields between a GuiServicePluginModel and a ResourceModel
 * 
 * 
 * @author m.gond
 */
public class ServiceModel {
  /** The id */
  private String id;
  /** The type */
  private ServiceEnum type;
  /** The name */
  private String name;
  /** The description */
  private String description;
  /** The category */
  private String category;
  /** The icon */
  private String icon;
  /** The label */
  private String label;
  /** The visibility */
  private boolean visible;
  /** The position */
  private String position;
  /** The dataSetSelection */
  private DataSetSelectionType dataSetSelection;

  /**
   * Gets the id value
   * 
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of id
   * 
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the type value
   * 
   * @return the type
   */
  public ServiceEnum getType() {
    return type;
  }

  /**
   * Sets the value of type
   * 
   * @param type
   *          the type to set
   */
  public void setType(ServiceEnum type) {
    this.type = type;
  }

  /**
   * Gets the description value
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the value of description
   * 
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the category value
   * 
   * @return the category
   */
  public String getCategory() {
    return category;
  }

  /**
   * Sets the value of category
   * 
   * @param category
   *          the category to set
   */
  public void setCategory(String category) {
    this.category = category;
  }

  /**
   * Gets the icon value
   * 
   * @return the icon
   */
  public String getIcon() {
    return icon;
  }

  /**
   * Sets the value of icon
   * 
   * @param icon
   *          the icon to set
   */
  public void setIcon(String icon) {
    this.icon = icon;
  }

  /**
   * Gets the label value
   * 
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the value of label
   * 
   * @param label
   *          the label to set
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Gets the name value
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of name
   * 
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the visible value
   * 
   * @return the visible
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Sets the value of visible
   * 
   * @param visible
   *          the visible to set
   */
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  /**
   * Gets the position value
   * 
   * @return the position
   */
  public String getPosition() {
    return position;
  }

  /**
   * Sets the value of position
   * 
   * @param position
   *          the position to set
   */
  public void setPosition(String position) {
    this.position = position;
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
}
