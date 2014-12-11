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
package fr.cnes.sitools.form.components.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import fr.cnes.sitools.common.model.IResource;

/**
 * Form component class
 * 
 * @author AKKA
 * 
 */
@XStreamAlias("formComponent")
public final class FormComponent implements IResource {

  /**
   * ID of the form component
   */
  private String id;

  /**
   * Type of the FC
   */
  private String type;

  /**
   * Default height
   */
  private String componentDefaultHeight;

  /**
   * Default width
   */
  private String componentDefaultWidth;

  /**
   * JS object associated for administration
   */
  private String jsAdminObject;

  /**
   * JS object associated for user Presentation
   */
  private String jsUserObject;

  /**
   * fileUrlAdmin
   */
  @Deprecated
  private String fileUrlAdmin;
  
  /**
   * fileUrlUser
   */
  @Deprecated
  private String fileUrlUser;
  
  /**
   * imageUrl
   */
  private String imageUrl;
  
  /**
   * priority
   */
  private Integer priority;
  
  /** Hidden field for IResource */
  @XStreamOmitField
  @JsonIgnore
  private String name;
  
  /** Hidden field for IResource */
  @XStreamOmitField
  @JsonIgnore
  private String description;

  /**
   * Constructor
   */
  public FormComponent() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * Get the ID
   * 
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Set the ID
   * 
   * @param id
   *          the id to set
   */
  public void setId(final String id) {
    this.id = id;
  }

  /**
   * Get the type
   * 
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Set the type
   * 
   * @param type
   *          the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Get the JS object
   * 
   * @return the jsAdminObject
   */
  public String getJsAdminObject() {
    return jsAdminObject;
  }

  /**
   * Set the JS object
   * 
   * @param javascriptObject
   *          the jsAdminObject to set
   */
  public void setJsAdminObject(String javascriptObject) {
    this.jsAdminObject = javascriptObject;
  }

  /**
   * Get the default height
   * 
   * @return the componentDefaultHeight
   */
  public String getComponentDefaultHeight() {
    return componentDefaultHeight;
  }

  /**
   * Set the default height
   * 
   * @param componentDefaultHeight
   *          the componentDefaultHeight to set
   */
  public void setComponentDefaultHeight(String componentDefaultHeight) {
    this.componentDefaultHeight = componentDefaultHeight;
  }

  /**
   * Get the default width
   * 
   * @return the componentDefaultWidth
   */
  public String getComponentDefaultWidth() {
    return componentDefaultWidth;
  }

  /**
   * Set the default height
   * 
   * @param componentDefaultWidth
   *          the componentDefaultWidth to set
   */
  public void setComponentDefaultWidth(String componentDefaultWidth) {
    this.componentDefaultWidth = componentDefaultWidth;
  }

  /**
   * Get the imageUrl
   * 
   * @return the imageUrl
   */
  public String getImageUrl() {
    return imageUrl;
  }

  /**
   * Set the imageUrl
   * 
   * @param imageUrl
   *          the imageUrl to set
   */
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  /**
   * Gets the jsUserObject value
   * @return the jsUserObject
   */
  public String getJsUserObject() {
    return jsUserObject;
  }

  /**
   * Sets the value of jsUserObject
   * @param jsUserObject the jsUserObject to set
   */
  public void setJsUserObject(String jsUserObject) {
    this.jsUserObject = jsUserObject;
  }

  /**
   * Gets the fileUrlAdmin value
   * @return the fileUrlAdmin
   */
  @Deprecated
  public String getFileUrlAdmin() {
    return fileUrlAdmin;
  }

  /**
   * Sets the value of fileUrlAdmin
   * @param fileUrlAdmin the fileUrlAdmin to set
   */
  @Deprecated
  public void setFileUrlAdmin(String fileUrlAdmin) {
    this.fileUrlAdmin = fileUrlAdmin;
  }

  /**
   * Gets the fileUrlUser value
   * @return the fileUrlUser
   */
  @Deprecated
  public String getFileUrlUser() {
    return fileUrlUser;
  }

  /**
   * Sets the value of fileUrlUser
   * @param fileUrlUser the fileUrlUser to set
   */
  @Deprecated
  public void setFileUrlUser(String fileUrlUser) {
    this.fileUrlUser = fileUrlUser;
  }

  /**
   * Gets the priority value
   * @return the priority
   */
  public Integer getPriority() {
    return priority;
  }

  /**
   * Sets the value of priority
   * @param priority the priority to set
   */
  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  
}
