/*******************************************************************************
 * Copyright 2011 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.guiservice.model;

import java.io.Serializable;

import fr.cnes.sitools.common.model.Dependencies;
import fr.cnes.sitools.common.model.IResource;

/**
 * Gui service model object
 * 
 * 
 * @author m.gond
 */
public class GuiServiceModel implements IResource, Serializable {

  /** serialVersionUID */
  private static final long serialVersionUID = 1L;
  /** The id */
  private String id;
  /** The name */
  private String name;
  /** The description */
  private String description;
  /** The label of the module */
  private String label;
  /** Author */
  private String author;
  /** Version */
  private String version;
  /** The css class of the icon */
  private String iconClass;
  /** xtype */
  private String xtype;
  /** dependencies */
  private Dependencies dependencies;
  /** priority */
  private Integer priority; // global ordering of all gui service...

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
   * Gets the author value
   * 
   * @return the author
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Sets the value of author
   * 
   * @param author
   *          the author to set
   */
  public void setAuthor(String author) {
    this.author = author;
  }

  /**
   * Gets the version value
   * 
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets the value of version
   * 
   * @param version
   *          the version to set
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Gets the iconClass value
   * 
   * @return the iconClass
   */
  public String getIconClass() {
    return iconClass;
  }

  /**
   * Sets the value of iconClass
   * 
   * @param iconClass
   *          the iconClass to set
   */
  public void setIconClass(String iconClass) {
    this.iconClass = iconClass;
  }

  /**
   * Gets the xtype value
   * 
   * @return the xtype
   */
  public String getXtype() {
    return xtype;
  }

  /**
   * Sets the value of xtype
   * 
   * @param xtype
   *          the xtype to set
   */
  public void setXtype(String xtype) {
    this.xtype = xtype;
  }

  /**
   * Gets the dependencies value
   * 
   * @return the dependencies
   */
  public Dependencies getDependencies() {
    return dependencies;
  }

  /**
   * Sets the value of dependencies
   * 
   * @param dependencies
   *          the dependencies to set
   */
  public void setDependencies(Dependencies dependencies) {
    this.dependencies = dependencies;
  }

  /**
   * Gets the priority value
   * 
   * @return the priority
   */
  public Integer getPriority() {
    return priority;
  }

  /**
   * Sets the value of priority
   * 
   * @param priority
   *          the priority to set
   */
  public void setPriority(Integer priority) {
    this.priority = priority;
  }

}
