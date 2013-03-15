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
package fr.cnes.sitools.client.model;

/**
 * Object to represent informations about OpenSearch used only to create the index.html page.
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
public final class AppDatasetOpensearchDTO {

  /**
   * URL to access the OpenSearch search engine.
   */
  private String url;
  /**
   * ShortName of the OpenSearch description file.
   */
  private String shortName;
  /**
   * Name of the DataSet.
   */
  private String datasetName;

  /**
   * DTO for OpenSearch
   */
  public AppDatasetOpensearchDTO() {
    super();
    this.url = new String();
    this.shortName = new String();
  }

  /**
   * Set the URL.
   * 
   * @param url
   *          the URL
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Get the URL of the DataSet application.
   * 
   * @return URL of the DataSet application
   */
  public String getUrl() {
    return url;
  }

  /**
   * Set the shortName of the OpenSearch description.
   * 
   * @param shortName
   *          the short name
   */
  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  /**
   * Set the shortName of the OpenSearch description.
   * 
   * @return shortName : the shortName of the OpenSearch description
   */
  public String getShortName() {
    return shortName;
  }

  /**
   * Set the value of datasetName.
   * 
   * @param datasetName
   *          the datasetName to set
   */
  public void setDatasetName(String datasetName) {
    this.datasetName = datasetName;
  }

  /**
   * Get the datasetName value.
   * 
   * @return the datasetName
   */
  public String getDatasetName() {
    return datasetName;
  }
}
