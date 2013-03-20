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
package fr.cnes.sitools.dataset.view.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.Dependencies;
import fr.cnes.sitools.common.model.IResource;

/**
 * Form component class
 * 
 * @author AKKA
 * 
 */
@XStreamAlias("datasetView")
public final class DatasetView implements IResource {

  /** Id */
  private String id;
  /** JS object associated */
  private String jsObject;

  /** fileUrl */
  private String fileUrl;

  /** imageUrl */
  private String imageUrl;

  /** priority */
  private Integer priority;

  /** Name */
  private String name;

  /** Description */
  private String description;

  /** dependencies */
  private Dependencies dependencies;

  /**
   * Basic Constructor
   */
  public DatasetView() {
    super();
  }

  /**
   * Gets the jsObject value
   * 
   * @return the jsObject
   */
  public String getJsObject() {
    return jsObject;
  }

  /**
   * Sets the value of jsObject
   * 
   * @param jsObject
   *          the jsObject to set
   */
  public void setJsObject(String jsObject) {
    this.jsObject = jsObject;
  }

  /**
   * Gets the fileUrl value
   * 
   * @return the fileUrl
   */
  public String getFileUrl() {
    return fileUrl;
  }

  /**
   * Sets the value of fileUrl
   * 
   * @param fileUrl
   *          the fileUrl to set
   */
  public void setFileUrl(String fileUrl) {
    this.fileUrl = fileUrl;
  }

  /**
   * Gets the imageUrl value
   * 
   * @return the imageUrl
   */
  public String getImageUrl() {
    return imageUrl;
  }

  /**
   * Sets the value of imageUrl
   * 
   * @param imageUrl
   *          the imageUrl to set
   */
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
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

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    DatasetView other = (DatasetView) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

}
