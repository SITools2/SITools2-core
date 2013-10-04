/*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.form.project.dto;

import java.util.List;

import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.form.dataset.dto.FormDTO;
import fr.cnes.sitools.form.dataset.dto.ParametersDTO;
import fr.cnes.sitools.form.project.model.FormProject;

/**
 * DTO object from FormProjects
 * 
 * 
 * @author m.gond
 */
public class FormProjectAdminDTO extends FormDTO {

  /**
   * the Dictionnary
   */
  private Resource dictionary = null;

  /**
   * The collection of dataset of the Form
   */
  private Resource collection;

  /** List of dataset properties to use in searchs */
  private List<FormPropertyParameterDTO> properties;

  /** The url of the service returning a list of dataset corresponding to some properties */
  private String urlServicePropertiesSearch;
  /** The url of the service performing the multidataset search */
  private String urlServiceDatasetSearch;

  /** The id of the resource for the searchPropertiesSearch service */
  private String idServicePropertiesSearch;
  /** The id of the resource for the DatasetSearch service */
  private String idServiceDatasetSearch;

  /** Maximal number of datasets authorized for the search request */
  private Integer nbDatasetsMax;

  /**
   * Constructor
   */
  public FormProjectAdminDTO() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * Sets the value of properties
   * 
   * @param properties
   *          the properties to set
   */
  public void setProperties(List<FormPropertyParameterDTO> properties) {
    this.properties = properties;
  }

  /**
   * Gets the properties value
   * 
   * @return the properties
   */
  public List<FormPropertyParameterDTO> getProperties() {
    return properties;
  }

  /**
   * Gets the urlServicePropertiesSearch value
   * 
   * @return the urlServicePropertiesSearch
   */
  public String getUrlServicePropertiesSearch() {
    return urlServicePropertiesSearch;
  }

  /**
   * Sets the value of urlServicePropertiesSearch
   * 
   * @param urlServicePropertiesSearch
   *          the urlServicePropertiesSearch to set
   */
  public void setUrlServicePropertiesSearch(String urlServicePropertiesSearch) {
    this.urlServicePropertiesSearch = urlServicePropertiesSearch;
  }

  /**
   * Gets the urlServiceDatasetSearch value
   * 
   * @return the urlServiceDatasetSearch
   */
  public String getUrlServiceDatasetSearch() {
    return urlServiceDatasetSearch;
  }

  /**
   * Sets the value of urlServiceDatasetSearch
   * 
   * @param urlServiceDatasetSearch
   *          the urlServiceDatasetSearch to set
   */
  public void setUrlServiceDatasetSearch(String urlServiceDatasetSearch) {
    this.urlServiceDatasetSearch = urlServiceDatasetSearch;
  }

  /**
   * Sets the value of nbDatasetsMax
   * 
   * @param nbDatasetsMax
   *          the nbDatasetsMax to set
   */
  public void setNbDatasetsMax(Integer nbDatasetsMax) {
    this.nbDatasetsMax = nbDatasetsMax;
  }

  /**
   * Gets the nbDatasetsMax value
   * 
   * @return the nbDatasetsMax
   */
  public Integer getNbDatasetsMax() {
    return nbDatasetsMax;
  }

  /**
   * Gets the dictionary value
   * 
   * @return the dictionary
   */
  public Resource getDictionary() {
    return dictionary;
  }

  /**
   * Sets the value of dictionary
   * 
   * @param dictionary
   *          the dictionary to set
   */
  public void setDictionary(Resource dictionary) {
    this.dictionary = dictionary;
  }

  /**
   * Gets the collection value
   * 
   * @return the collection
   */
  public Resource getCollection() {
    return collection;
  }

  /**
   * Sets the value of collection
   * 
   * @param collection
   *          the collection to set
   */
  public void setCollection(Resource collection) {
    this.collection = collection;
  }

  /**
   * Wrap a {@link FormProjectAdminDTO} into a {@link FormProject} object
   * 
   * @param formProjectDTOInput
   *          the {@link FormProjectAdminDTO} to wrap
   * @return the {@link FormProject} wrapped for a {@link FormProjectAdminDTO}
   */
  public static FormProject dtoToFormProject(FormProjectAdminDTO formProjectDTOInput) {
    FormProject form = new FormProject();

    form.setDictionary(formProjectDTOInput.getDictionary());
    form.setCollection(formProjectDTOInput.getCollection());
    form.setIdServicePropertiesSearch(formProjectDTOInput.getIdServicePropertiesSearch());
    form.setIdServiceDatasetSearch(formProjectDTOInput.getIdServiceDatasetSearch());
    form.setUrlServicePropertiesSearch(formProjectDTOInput.getUrlServicePropertiesSearch());
    form.setUrlServiceDatasetSearch(formProjectDTOInput.getUrlServiceDatasetSearch());
    form.setNbDatasetsMax(formProjectDTOInput.getNbDatasetsMax());

    form.setId(formProjectDTOInput.getId());
    form.setParent(formProjectDTOInput.getParent());
    form.setName(formProjectDTOInput.getName());
    form.setDescription(formProjectDTOInput.getDescription());
    form.setParameters(ParametersDTO.dtoToParameters(formProjectDTOInput.getParameters()));
    form.setZones(dtoToZones(formProjectDTOInput.getZones()));
    form.setWidth(formProjectDTOInput.getWidth());
    form.setHeight(formProjectDTOInput.getHeight());
    form.setCss(formProjectDTOInput.getCss());
    form.setProperties(FormPropertyParameterDTO.dtoToproperties(formProjectDTOInput.getProperties()));
    return form;

  }

  /**
   * Gets the idServicePropertiesSearch value
   * 
   * @return the idServicePropertiesSearch
   */
  public String getIdServicePropertiesSearch() {
    return idServicePropertiesSearch;
  }

  /**
   * Sets the value of idServicePropertiesSearch
   * 
   * @param idServicePropertiesSearch
   *          the idServicePropertiesSearch to set
   */
  public void setIdServicePropertiesSearch(String idServicePropertiesSearch) {
    this.idServicePropertiesSearch = idServicePropertiesSearch;
  }

  /**
   * Gets the idServiceDatasetSearch value
   * 
   * @return the idServiceDatasetSearch
   */
  public String getIdServiceDatasetSearch() {
    return idServiceDatasetSearch;
  }

  /**
   * Sets the value of idServiceDatasetSearch
   * 
   * @param idServiceDatasetSearch
   *          the idServiceDatasetSearch to set
   */
  public void setIdServiceDatasetSearch(String idServiceDatasetSearch) {
    this.idServiceDatasetSearch = idServiceDatasetSearch;
  }

}
