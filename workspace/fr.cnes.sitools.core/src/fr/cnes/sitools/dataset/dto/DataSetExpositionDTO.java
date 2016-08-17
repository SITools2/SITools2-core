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
package fr.cnes.sitools.dataset.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.view.model.DatasetView;
import fr.cnes.sitools.util.Property;

/**
 * DTO object for a DataSet object. Contains only the informations needed for a client
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class DataSetExpositionDTO {

  /** Identifier of the DataSet */
  private String id;

  /** DataSet name */
  private String name;

  /** DataSet activation status (activated or not) */
  private String status;

  /** DataSet description */
  private String description;

  /**
   * Attachment for users exposition TODO >> Provisoire en attendant les expositions
   */
  private String sitoolsAttachementForUsers = null;

  /** List of columns defining the DataSet */
  @XStreamAlias("columnModel")
  private List<Column> columnModel = new ArrayList<Column>();

  /** HTML Description of the DataSet **/
  private String descriptionHTML;

  /** HTML Description of the DataSet **/
  private DatasetView datasetView;

  /** Dictionary mapping */
  private List<DictionaryMappingDTO> dictionaryMappings = new ArrayList<DictionaryMappingDTO>();

  /** List of dataset View Config */
  private List<Property> datasetViewConfig = new ArrayList<Property>();

  /** The Image */
  private Resource image;

  /**
   * Total number of results for information
   */
  private int nbRecords;

  /** Date d'expiration */
  @XStreamAlias("expirationDate")
  private Date expirationDate;
  
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
   * Gets the status value
   * 
   * @return the status
   */
  public String getStatus() {
    return status;
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
   * Gets the sitoolsAttachementForUsers value
   * 
   * @return the sitoolsAttachementForUsers
   */
  public String getSitoolsAttachementForUsers() {
    return sitoolsAttachementForUsers;
  }

  /**
   * Sets the value of sitoolsAttachementForUsers
   * 
   * @param sitoolsAttachementForUsers
   *          the sitoolsAttachementForUsers to set
   */
  public void setSitoolsAttachementForUsers(String sitoolsAttachementForUsers) {
    this.sitoolsAttachementForUsers = sitoolsAttachementForUsers;
  }

  /**
   * Gets the columnModel value
   * 
   * @return the columnModel
   */
  public List<Column> getColumnModel() {
    return columnModel;
  }

  /**
   * Sets the value of columnModel
   * 
   * @param columnModel
   *          the columnModel to set
   */
  public void setColumnModel(List<Column> columnModel) {
    this.columnModel = columnModel;
  }

  /**
   * Sets the value of descriptionHTML
   * 
   * @param descriptionHTML
   *          the descriptionHTML to set
   */
  public void setDescriptionHTML(String descriptionHTML) {
    this.descriptionHTML = descriptionHTML;
  }

  /**
   * Gets the descriptionHTML value
   * 
   * @return the descriptionHTML
   */
  public String getDescriptionHTML() {
    return descriptionHTML;
  }

  /**
   * Gets the datasetView value
   * 
   * @return the datasetView
   */
  public DatasetView getDatasetView() {
    return datasetView;
  }

  /**
   * Sets the value of datasetView
   * 
   * @param datasetView
   *          the datasetView to set
   */
  public void setDatasetView(DatasetView datasetView) {
    this.datasetView = datasetView;
  }

  /**
   * Gets the dictionaryMappings value
   * 
   * @return the dictionaryMappings
   */
  public List<DictionaryMappingDTO> getDictionaryMappings() {
    return dictionaryMappings;
  }

  /**
   * Sets the value of dictionaryMappings
   * 
   * @param dictionaryMappings
   *          the dictionaryMappings to set
   */
  public void setDictionaryMappings(List<DictionaryMappingDTO> dictionaryMappings) {
    this.dictionaryMappings = dictionaryMappings;
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
   * Gets the description value
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the datasetViewConfig value
   * 
   * @return the datasetViewConfig
   */
  public List<Property> getDatasetViewConfig() {
    return datasetViewConfig;
  }

  /**
   * Sets the value of datasetViewConfig
   * 
   * @param datasetViewConfig
   *          the datasetViewConfig to set
   */
  public void setDatasetViewConfig(List<Property> datasetViewConfig) {
    this.datasetViewConfig = datasetViewConfig;
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
   * Gets the nbRecords value
   * 
   * @return the nbRecords
   */
  public int getNbRecords() {
    return nbRecords;
  }

  /**
   * Sets the value of nbRecords
   * 
   * @param nbRecords
   *          the nbRecords to set
   */
  public void setNbRecords(int nbRecords) {
    this.nbRecords = nbRecords;
  }

  /**
   * Gets the expirationDate value
   * @return the expirationDate
   */
  public Date getExpirationDate() {
    return expirationDate;
  }

  /**
   * Sets the value of expirationDate
   * @param expirationDate the expirationDate to set
   */
  public void setExpirationDate(Date expirationDate) {
    this.expirationDate = expirationDate;
  }
}
