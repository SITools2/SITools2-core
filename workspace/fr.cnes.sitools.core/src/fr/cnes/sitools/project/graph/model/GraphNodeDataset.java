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
package fr.cnes.sitools.project.graph.model;

/**
 * GraphNodeDataset
 * 
 * @author m.gond (AKKA Technologies)
 */
public abstract class GraphNodeDataset extends AbstractGraphNode {
  /**
   * The id of the dataset
   */
  private String datasetId;
  /**
   * the number of records associated to that dataset
   */
  private String nbRecord;
  /**
   * The urlImageDataset
   */
  private String urlImageDataset;
  /**
   * the readme part
   */
  private String readme;

  /**
   * Gets the datasetId value
   * 
   * @return the datasetId
   */
  public final String getDatasetId() {
    return datasetId;
  }

  /**
   * Sets the value of datasetId
   * 
   * @param datasetId
   *          the datasetId to set
   */
  public final void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  /**
   * Gets the nbRecord value
   * 
   * @return the nbRecord
   */
  public final String getNbRecord() {
    return nbRecord;
  }

  /**
   * Sets the value of nbRecord
   * 
   * @param nbRecord
   *          the nbRecord to set
   */
  public final void setNbRecord(String nbRecord) {
    this.nbRecord = nbRecord;
  }

  /**
   * Gets the urlImageDataset value
   * 
   * @return the urlImageDataset
   */
  public final String getUrlImageDataset() {
    return this.urlImageDataset;
  }

  /**
   * Sets the value of urlImageDataset
   * 
   * @param urlImageDataset
   *          the urlImageDataset to set
   */
  public final void setUrlImageDataset(String urlImageDataset) {
    this.urlImageDataset = urlImageDataset;
  }

  /**
   * Gets the readme value
   * 
   * @return the readme
   */
  public final String getReadme() {
    return readme;
  }

  /**
   * Sets the value of readme
   * 
   * @param readme
   *          the readme to set
   */
  public final void setReadme(String readme) {
    this.readme = readme;
  }

}
