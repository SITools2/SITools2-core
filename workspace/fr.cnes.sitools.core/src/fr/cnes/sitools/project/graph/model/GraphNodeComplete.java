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
package fr.cnes.sitools.project.graph.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.Resource;

/**
 * "Complete" bean for node and leaf description.
 * 
 * @author m.gond (AKKA Technologies)
 */
@XStreamAlias("graphNodeComplete")
public final class GraphNodeComplete {

  /** =========================== ABSTRACT ATTRIBUTES ====================== */
  /**
   * node types constants node
   */
  public static final String NODE_TYPE_NODE = "node";
  /**
   * node types constants dataset
   */
  public static final String NODE_TYPE_DATASET = "dataset";
  /**
   * The text of the node
   */
  private String text;
  /**
   * is this node a leaf
   */
  private boolean leaf;
  /**
   * The type of the node
   */
  private String type;
  /**
   * The description of the node
   */
  private String description;

  /** =========================== NODE ATTRIBUTES ====================== */

  /**
   * the image
   */
  private Resource image;
  /**
   * the children's list
   */
  private List<GraphNodeComplete> children;

  /** =========================== DATASET_NODE ATTRIBUTES ====================== */

  /**
   * The id of the dataset
   */
  private String datasetId;
  /**
   * the number of records associated to that dataset
   */
  private int nbRecord;
  /**
   * The urlImageDataset
   */
  private String imageDs;
  /**
   * the readme part
   */
  private String readme;

  /**
   * if the dataset if authorized or not, attribute not stored
   */
  private volatile String authorized = null;

  /**
   * the dataset status
   */
  private volatile String status = null;

  /**
   * if the dataset is always visible
   */
  private volatile Boolean visible = null;
  
  /**
   * The dataset url
   */
  private String url;
  /** =========================== GETTERS / SETTERS ====================== */

  /**
   * Gets the text value
   * 
   * @return the text
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the value of text
   * 
   * @param text
   *          the text to set
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * Gets the leaf value
   * 
   * @return the leaf
   */
  public boolean isLeaf() {
    return leaf;
  }

  /**
   * Sets the value of leaf
   * 
   * @param leaf
   *          the leaf to set
   */
  public void setLeaf(boolean leaf) {
    this.leaf = leaf;
  }

  /**
   * Gets the type value
   * 
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the value of type
   * 
   * @param type
   *          the type to set
   */
  public void setType(String type) {
    this.type = type;
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
   * Gets the children value
   * 
   * @return the children
   */
  public List<GraphNodeComplete> getChildren() {
    return children;
  }

  /**
   * Sets the value of children
   * 
   * @param children
   *          the children to set
   */
  public void setChildren(List<GraphNodeComplete> children) {
    this.children = children;
  }

  /**
   * Gets the datasetId value
   * 
   * @return the datasetId
   */
  public String getDatasetId() {
    return datasetId;
  }

  /**
   * Sets the value of datasetId
   * 
   * @param datasetId
   *          the datasetId to set
   */
  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }

  /**
   * Gets the nbRecord value
   * 
   * @return the nbRecord
   */
  public int getNbRecord() {
    return nbRecord;
  }

  /**
   * Sets the value of nbRecord
   * 
   * @param nbRecord
   *          the nbRecord to set
   */
  public void setNbRecord(int nbRecord) {
    this.nbRecord = nbRecord;
  }

  /**
   * Gets the readme value
   * 
   * @return the readme
   */
  public String getReadme() {
    return readme;
  }

  /**
   * Sets the value of readme
   * 
   * @param readme
   *          the readme to set
   */
  public void setReadme(String readme) {
    this.readme = readme;
  }

  /**
   * Sets the value of imageDs
   * 
   * @param imageDs
   *          the imageDs to set
   */
  public void setImageDs(String imageDs) {
    this.imageDs = imageDs;
  }

  /**
   * Gets the imageDs value
   * 
   * @return the imageDs
   */
  public String getImageDs() {
    return imageDs;
  }

  /**
   * Sets the value of authorized
   * 
   * @param authorized
   *          the authorized to set
   */
  public void setAuthorized(String authorized) {
    this.authorized = authorized;
  }

  /**
   * Gets the authorized value
   * 
   * @return the authorized
   */
  public String getAuthorized() {
    return authorized;
  }

  /**
   * Gets the status value
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the value of status
   * @param status the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Gets the visible value
   * @return the visible
   */
  public Boolean getVisible() {
    return visible;
  }

  /**
   * Sets the value of visible
   * @param visible the visible to set
   */
  public void setVisible(Boolean visible) {
    this.visible = visible;
  }

  /**
   * Sets the value of url
   * @param url the url to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Gets the url value
   * @return the url
   */
  public String getUrl() {
    return url;
  }
  



}
