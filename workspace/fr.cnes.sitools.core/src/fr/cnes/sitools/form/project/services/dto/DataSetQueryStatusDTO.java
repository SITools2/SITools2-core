    /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.form.project.services.dto;

import fr.cnes.sitools.common.model.Resource;

/**
 * DTO to store a result for a dataset count during a multidataset search
 * 
 * 
 * @author m.gond
 */
public class DataSetQueryStatusDTO {

  /** Object identifier. */
  private String id;

  /** Object name. */
  private String name;

  /** Object description. */
  private String description;

  /**
   * the image
   */
  private Resource image;

  /** The number of records */
  private Integer nbRecord;

  /** The url of the dataset */
  private String url;

  /** The status when querying the dataset */
  private DatasetQueryStatus status;
  /** The error message if the status is REQUEST_ERROR */
  private String errorMessage;

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
   * Gets the nbRecord value
   * 
   * @return the nbRecord
   */
  public Integer getNbRecord() {
    return nbRecord;
  }

  /**
   * Sets the value of nbRecord
   * 
   * @param nbRecord
   *          the nbRecord to set
   */
  public void setNbRecord(Integer nbRecord) {
    this.nbRecord = nbRecord;
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
   * Gets the url value
   * 
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * Gets the status value
   * 
   * @return the status
   */
  public DatasetQueryStatus getStatus() {
    return status;
  }

  /**
   * Sets the value of status
   * 
   * @param status
   *          the status to set
   */
  public void setStatus(DatasetQueryStatus status) {
    this.status = status;
  }

  /**
   * Sets the value of errorMessage
   * 
   * @param errorMessage
   *          the errorMessage to set
   */
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  /**
   * Gets the errorMessage value
   * 
   * @return the errorMessage
   */
  public String getErrorMessage() {
    return errorMessage;
  }

}
