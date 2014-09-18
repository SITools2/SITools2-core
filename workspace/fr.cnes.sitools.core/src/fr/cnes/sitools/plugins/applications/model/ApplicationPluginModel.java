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
package fr.cnes.sitools.plugins.applications.model;

import java.util.Date;
import java.util.HashMap;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.common.model.ExtensionModel;
import fr.cnes.sitools.persistence.Persistent;

/**
 * The Model for an application plugin
 * 
 * @author m.gond (AKKA Technologies)
 */
@XStreamAlias("ApplicationPluginModel")
public final class ApplicationPluginModel extends ExtensionModel<ApplicationPluginParameter> implements Persistent {
  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = 3337418024690313757L;

  /** the label of the applicationInstance */
  private String label;

  /** url attachment */
  private String urlAttach;

  /** the status of the application */
  private String status;

  /** category of the application plugin */
  private Category category; // = Category.ADMIN_DYNAMIC;

  private Date lastStatusUpdate;

  /**
   * Default constructor
   */
  public ApplicationPluginModel() {
    this.setParametersMap(new HashMap<String, ApplicationPluginParameter>());
    setCategory(Category.USER_DYNAMIC);
  }

  /**
   * Gets the urlAttach value
   * 
   * @return the urlAttach
   */
  public String getUrlAttach() {
    return urlAttach;
  }

  /**
   * Sets the value of urlAttach
   * 
   * @param urlAttach
   *          the urlAttach to set
   */
  public void setUrlAttach(String urlAttach) {
    this.urlAttach = urlAttach;
  }

  /**
   * Sets the value of status
   * 
   * @param status
   *          the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Gets the status value
   * 
   * @return the status
   */
  public String getStatus() {
    return status;
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
   * Gets the label value
   * 
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the value of category
   * 
   * @param category
   *          the category to set
   */
  public void setCategory(Category category) {
    this.category = category;
  }

  /**
   * Gets the category value
   * 
   * @return the category
   */
  public Category getCategory() {
    return category;
  }

  /**
   * Gets the lastStatusUpdate value
   * 
   * @return the lastStatusUpdate
   */
  public Date getLastStatusUpdate() {
    return lastStatusUpdate;
  }

  /**
   * Sets the value of lastStatusUpdate
   * 
   * @param lastStatusUpdate
   *          the lastStatusUpdate to set
   */
  public void setLastStatusUpdate(Date lastStatusUpdate) {
    this.lastStatusUpdate = lastStatusUpdate;
  }

}
