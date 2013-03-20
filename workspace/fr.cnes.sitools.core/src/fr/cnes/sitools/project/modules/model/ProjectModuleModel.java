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
package fr.cnes.sitools.project.modules.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.Dependencies;
import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.role.model.Role;

/**
 * Class for client module description
 * 
 * @author AKKA Technologies
 * 
 */
@XStreamAlias("projectModule")
public final class ProjectModuleModel implements IResource {

  /** The id */
  private String id;

  /** Name */
  private String name;

  /** Description */
  private String description;

  /** The label of the module */
  private String label;

  /** Author */
  private String author;

  /** Version */
  private String version;

  /** fileUrl */
  private String url;

  /** imageUrl */
  private String imagePath;

  /** title */
  private String title;

  /** default width of widget */
  private int defaultWidth;

  /** default height of widget */
  private int defaultHeight;

  /** default x position */
  private int x;

  /** default y position */
  private int y;

  /** icon */
  private String icon;

  /** specificType */
  private String specificType;

  /** xtype */
  private String xtype;

  /** dependencies */
  private Dependencies dependencies;

  /** priority */
  private Integer priority; // global ordering of all modules...

  /** Visible at the beginning */
  private Boolean visible;

  /** Opened module for public users */
  @Deprecated
  private Boolean publicOpened;

  /** the list of roles */
  private List<Role> listRoles;

  /**
   * Basic Constructor
   */
  public ProjectModuleModel() {
    super();
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
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
   * Gets the url value
   * 
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * Sets the value of url
   * 
   * @param url
   *          the url to set
   */
  public void setUrl(String url) {
    this.url = url;
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
   * Gets the defaultWidth value
   * 
   * @return the defaultWidth
   */
  public int getDefaultWidth() {
    return defaultWidth;
  }

  /**
   * Sets the value of defaultWidth
   * 
   * @param defaultWidth
   *          the defaultWidth to set
   */
  public void setDefaultWidth(int defaultWidth) {
    this.defaultWidth = defaultWidth;
  }

  /**
   * Gets the defaultHeight value
   * 
   * @return the defaultHeight
   */
  public int getDefaultHeight() {
    return defaultHeight;
  }

  /**
   * Sets the value of defaultHeight
   * 
   * @param defaultHeight
   *          the defaultHeight to set
   */
  public void setDefaultHeight(int defaultHeight) {
    this.defaultHeight = defaultHeight;
  }

  /**
   * Gets the x value
   * 
   * @return the x
   */
  public int getX() {
    return x;
  }

  /**
   * Sets the value of x
   * 
   * @param x
   *          the x to set
   */
  public void setX(int x) {
    this.x = x;
  }

  /**
   * Gets the y value
   * 
   * @return the y
   */
  public int getY() {
    return y;
  }

  /**
   * Sets the value of y
   * 
   * @param y
   *          the y to set
   */
  public void setY(int y) {
    this.y = y;
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
   * Sets the value of dependencies
   * 
   * @param dependencies
   *          the dependencies to set
   */
  public void setDependencies(Dependencies dependencies) {
    this.dependencies = dependencies;
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
   * Gets the imagePath value
   * 
   * @return the imagePath
   */
  public String getImagePath() {
    return imagePath;
  }

  /**
   * Sets the value of imagePath
   * 
   * @param imagePath
   *          the imagePath to set
   */
  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  /**
   * Gets the title value
   * 
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the value of title
   * 
   * @param title
   *          the title to set
   */
  public void setTitle(String title) {
    this.title = title;
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
   * Gets the specificType value
   * 
   * @return the specificType
   */
  public String getSpecificType() {
    return specificType;
  }

  /**
   * Sets the value of specificType
   * 
   * @param specificType
   *          the specificType to set
   */
  public void setSpecificType(String specificType) {
    this.specificType = specificType;
  }

  /**
   * Gets the visible value
   * 
   * @return the visible
   */
  public Boolean getVisible() {
    return visible;
  }

  /**
   * Sets the value of visible
   * 
   * @param visible
   *          the visible to set
   */
  public void setVisible(Boolean visible) {
    this.visible = visible;
  }

  /**
   * Gets the publicOpened value
   * 
   * @return the publicOpened
   */
  @Deprecated
  public Boolean getPublicOpened() {
    return publicOpened;
  }

  /**
   * Sets the value of publicOpened
   * 
   * @param publicOpened
   *          the publicOpened to set
   */
  @Deprecated
  public void setPublicOpened(Boolean publicOpened) {
    this.publicOpened = publicOpened;
  }

  /**
   * Gets the listRoles value
   * 
   * @return the listRoles
   */
  public List<Role> getListRoles() {
    return listRoles;
  }

  /**
   * Sets the value of listRoles
   * 
   * @param listRoles
   *          the listRoles to set
   */
  public void setListRoles(List<Role> listRoles) {
    this.listRoles = listRoles;
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

}
