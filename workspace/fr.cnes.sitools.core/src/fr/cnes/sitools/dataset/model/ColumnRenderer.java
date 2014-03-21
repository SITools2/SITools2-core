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
package fr.cnes.sitools.dataset.model;

import fr.cnes.sitools.common.model.Resource;

/**
 * Model Object to represent a ColumnRenderer for a specific column
 * 
 * 
 * @author m.gond
 */
public class ColumnRenderer {
  /** The behavior of the ColumnRenderer */
  private BehaviorEnum behavior;
  /** The image */
  private Resource image;
  /** The text for the link */
  private String linkText;
  /** The column Alias */
  private String columnAlias;
  /** The url of the dataset for a datasetLink */
  private String datasetLinkUrl;
  /** If it is displayable in the desktop */
  private Boolean displayable;
  
  /**
   * Tooltip
   */
  private String toolTip;
  /**
   * Default constructor
   * 
   */
  public ColumnRenderer() {
  }

  /**
   * Constructor with behavior
   * 
   * @param behavior
   *          the behavior
   */
  public ColumnRenderer(BehaviorEnum behavior) {
    this.setBehavior(behavior);
  }

  /**
   * Sets the value of behavior
   * 
   * @param behavior
   *          the behavior to set
   */
  public void setBehavior(BehaviorEnum behavior) {
    this.behavior = behavior;
  }

  /**
   * Gets the behavior value
   * 
   * @return the behavior
   */
  public BehaviorEnum getBehavior() {
    return behavior;
  }

  /**
   * Gets the image value
   * 
   * @return the image
   */
  public Resource getImage() {
    return image;
  }

  /**
   * Sets the value of image
   * 
   * @param image
   *          the image to set
   */
  public void setImage(Resource image) {
    this.image = image;
  }

  /**
   * Gets the linkText value
   * 
   * @return the linkText
   */
  public String getLinkText() {
    return linkText;
  }

  /**
   * Sets the value of linkText
   * 
   * @param linkText
   *          the linkText to set
   */
  public void setLinkText(String linkText) {
    this.linkText = linkText;
  }

  /**
   * Gets the columnAlias value
   * 
   * @return the columnAlias
   */
  public String getColumnAlias() {
    return columnAlias;
  }

  /**
   * Sets the value of columnAlias
   * 
   * @param columnAlias
   *          the columnAlias to set
   */
  public void setColumnAlias(String columnAlias) {
    this.columnAlias = columnAlias;
  }

  /**
   * Gets the datasetLinkUrl value
   * 
   * @return the datasetLinkUrl
   */
  public String getDatasetLinkUrl() {
    return datasetLinkUrl;
  }

  /**
   * Sets the value of datasetLinkUrl
   * 
   * @param datasetLinkUrl
   *          the datasetLinkUrl to set
   */
  public void setDatasetLinkUrl(String datasetLinkUrl) {
    this.datasetLinkUrl = datasetLinkUrl;
  }

  /**
   * Sets the value of displayable
   * @param displayable the displayable to set
   */
  public void setDisplayable(Boolean displayable) {
    this.displayable = displayable;
  }

  /**
   * Gets the displayable value
   * @return the displayable
   */
  public Boolean getDisplayable() {
    return displayable;
  }

  /**
   * Gets the toolTip value
   * @return the toolTip
   */
  public String getToolTip() {
    return toolTip;
  }

  /**
   * Sets the value of toolTip
   * @param toolTip the toolTip to set
   */
  public void setToolTip(String toolTip) {
    this.toolTip = toolTip;
  }
}
