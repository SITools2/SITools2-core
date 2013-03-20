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
package fr.cnes.sitools.form.project.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.form.model.AbstractFormModel;

/**
 * FormProject component class
 * 
 * @author AKKA
 * 
 */
@XStreamAlias("FormProject")
public final class FormProject extends AbstractFormModel {

  /**
   * the Dictionnary
   */
  private Resource dictionary = null;
  /**
   * The list of parameters of the Form
   */
  private List<FormParameter> parameters;
  /**
   * The collection of dataset of the Form
   */
  private Resource collection = null;

  /** List of dataset properties to use in searchs */
  private List<FormPropertyParameter> properties = null;

  /** The url of the service returning a list of dataset corresponding to some properties */
  private String urlServicePropertiesSearch = null;
  /** The url of the service performing the multidataset search */
  private String urlServiceDatasetSearch = null;
  /** The id of the resource for the searchPropertiesSearch service */
  private String idServicePropertiesSearch = null;
  /** The id of the resource for the DatasetSearch service */
  private String idServiceDatasetSearch = null;
  /** Maximal number of datasets authorized for the search request */
  private Integer nbDatasetsMax = null;

  /**
   * Constructor
   */
  public FormProject() {
    super();
    // TODO Auto-generated constructor stub
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
   * Gets the parameters value
   * 
   * @return the parameters
   */
  public List<FormParameter> getParameters() {
    return parameters;
  }

  /**
   * Sets the value of parameters
   * 
   * @param parameters
   *          the parameters to set
   */
  public void setParameters(List<FormParameter> parameters) {
    this.parameters = parameters;
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
   * Sets the value of idServicePropertiesSearch
   * 
   * @param idServicePropertiesSearch
   *          the idServicePropertiesSearch to set
   */
  public void setIdServicePropertiesSearch(String idServicePropertiesSearch) {
    this.idServicePropertiesSearch = idServicePropertiesSearch;
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
   * Sets the value of idServiceDatasetSearch
   * 
   * @param idServiceDatasetSearch
   *          the idServiceDatasetSearch to set
   */
  public void setIdServiceDatasetSearch(String idServiceDatasetSearch) {
    this.idServiceDatasetSearch = idServiceDatasetSearch;
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
   * Sets the value of nbDatasetsMax
   * @param nbDatasetsMax the nbDatasetsMax to set
   */
  public void setNbDatasetsMax(Integer nbDatasetsMax) {
    this.nbDatasetsMax = nbDatasetsMax;
  }

  /**
   * Gets the nbDatasetsMax value
   * @return the nbDatasetsMax
   */
  public Integer getNbDatasetsMax() {
    return nbDatasetsMax;
  }

}
