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

import fr.cnes.sitools.collections.model.Collection;
import fr.cnes.sitools.dictionary.model.Dictionary;
import fr.cnes.sitools.form.model.AbstractFormModel;
import fr.cnes.sitools.form.project.model.FormParameter;
import fr.cnes.sitools.form.project.model.FormProject;
import fr.cnes.sitools.form.project.model.FormPropertyParameter;

/**
 * DTO object from FormProjects
 * 
 * 
 * @author m.gond
 */
public class FormProjectDTO extends AbstractFormModel {

  /**
   * the Dictionnary
   */
  private Dictionary dictionary = null;

  /**
   * The collection of dataset of the Form
   */
  private Collection collection = null;

  /** List of dataset properties to use in searchs */
  private List<FormPropertyParameter> properties = null;

  /** The url of the service returning a list of dataset corresponding to some properties */
  private String urlServicePropertiesSearch = null;
  /** The url of the service performing the multidataset search */
  private String urlServiceDatasetSearch = null;
  /** Maximal number of datasets authorized for the search request */
  private Integer nbDatasetsMax = null;

  /**
   * Constructor
   */
  public FormProjectDTO() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * Sets the value of properties
   * 
   * @param properties
   *          the properties to set
   */
  public void setProperties(List<FormPropertyParameter> properties) {
    this.properties = properties;
  }

  /**
   * Gets the properties value
   * 
   * @return the properties
   */
  public List<FormPropertyParameter> getProperties() {
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
   * Gets the dictionary value
   * 
   * @return the dictionary
   */
  public Dictionary getDictionary() {
    return dictionary;
  }

  /**
   * Sets the value of dictionary
   * 
   * @param dictionary
   *          the dictionary to set
   */
  public void setDictionary(Dictionary dictionary) {
    this.dictionary = dictionary;
  }

  /**
   * Gets the collection value
   * 
   * @return the collection
   */
  public Collection getCollection() {
    return collection;
  }

  /**
   * Sets the value of collection
   * 
   * @param collection
   *          the collection to set
   */
  public void setCollection(Collection collection) {
    this.collection = collection;
  }

  /**
   * Create a FormProjectDTO from a FormProject
   * 
   * 
   * @param formProject
   *          the {@link FormProject}
   * @param dictionary
   *          the {@link Dictionary} to use
   * @param collection
   *          the {@link Collection} to use
   * @return a new FormProjectDTO
   */
  public static FormProjectDTO fromObjectToDto(FormProject formProject, Dictionary dictionary, Collection collection) {
    FormProjectDTO dto = new FormProjectDTO();
    dto.setId(formProject.getId());
    dto.setParent(formProject.getParent());
    dto.setName(formProject.getName());
    dto.setDescription(formProject.getDescription());
    dto.setParameters(formProject.getParameters());
    dto.setWidth(formProject.getWidth());
    dto.setHeight(formProject.getHeight());
    dto.setCss(formProject.getCss());
    dto.setParentUrl(formProject.getParentUrl());

    dto.setProperties(formProject.getProperties());
    dto.setUrlServicePropertiesSearch(formProject.getUrlServicePropertiesSearch());
    dto.setUrlServiceDatasetSearch(formProject.getUrlServiceDatasetSearch());

    dto.setDictionary(dictionary);
    dto.setCollection(collection);

    dto.setNbDatasetsMax(formProject.getNbDatasetsMax());

    return dto;

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
}
