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
package fr.cnes.sitools.solr.model;

import java.io.Serializable;

/**
 * Represent the list of returnedField specific for a RSS return
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
public final class RssXSLTDTO implements Serializable {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -7034132049029892248L;
  /**
   * title field
   */
  private String title;
  /**
   * Description field
   */
  private String description;
  /**
   * link field
   */
  private String link;
  /**
   * guid field
   */
  private String guid;
  /**
   * pudDate field
   */
  private String pubDate;
  /**
   * datasetName
   */
  private String datasetName;
  /**
   * datasetDescription
   */
  private String datasetDescription;
  /**
   * datasetUri
   */
  private String datasetURI;

  /**
   * Unique key value
   */
  private String uniqueKey;

  /**
   * index name
   * 
   */
  private String indexName;
  /**
   * The feedUrl
   */
  private String feedUrl;

  // private String pkvalue = "$pkvalue";

  /**
   * Gets the title value
   * 
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the value of title
   * 
   * @param title
   *          the title to set
   */
  public void setTitle(String title) {
    this.title = title;
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
   * Gets the link value
   * 
   * @return the link
   */
  public String getLink() {
    return link;
  }

  /**
   * Sets the value of link
   * 
   * @param link
   *          the link to set
   */
  public void setLink(String link) {
    this.link = link;
  }

  /**
   * Gets the guid value
   * 
   * @return the guid
   */
  public String getGuid() {
    return guid;
  }

  /**
   * Sets the value of guid
   * 
   * @param guid
   *          the guid to set
   */
  public void setGuid(String guid) {
    this.guid = guid;
  }

  /**
   * Gets the pudDate value
   * 
   * @return the pudDate
   */
  public String getPubDate() {
    return pubDate;
  }

  /**
   * Sets the value of pudDate
   * 
   * @param pubDate
   *          the pubDate to set
   */
  public void setPubDate(String pubDate) {
    this.pubDate = pubDate;
  }

  /**
   * Gets the datasetName value
   * 
   * @return the datasetName
   */
  public String getDatasetName() {
    return datasetName;
  }

  /**
   * Sets the value of datasetName
   * 
   * @param datasetName
   *          the datasetName to set
   */
  public void setDatasetName(String datasetName) {
    this.datasetName = datasetName;
  }

  /**
   * Gets the datasetDescription value
   * 
   * @return the datasetDescription
   */
  public String getDatasetDescription() {
    return datasetDescription;
  }

  /**
   * Sets the value of datasetDescription
   * 
   * @param datasetDescription
   *          the datasetDescription to set
   */
  public void setDatasetDescription(String datasetDescription) {
    this.datasetDescription = datasetDescription;
  }

  /**
   * Gets the datasetURI value
   * 
   * @return the datasetURI
   */
  public String getDatasetURI() {
    return datasetURI;
  }

  /**
   * Sets the value of datasetURI
   * 
   * @param datasetURI
   *          the datasetURI to set
   */
  public void setDatasetURI(String datasetURI) {
    this.datasetURI = datasetURI;
  }

  /**
   * Sets the value of indexName
   * 
   * @param indexName
   *          the indexName to set
   */
  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }

  /**
   * Gets the indexName value
   * 
   * @return the indexName
   */
  public String getIndexName() {
    return indexName;
  }

  /**
   * Sets the value of uniqueKey
   * 
   * @param uniqueKey
   *          the uniqueKey to set
   */
  public void setUniqueKey(String uniqueKey) {
    this.uniqueKey = uniqueKey;
  }

  /**
   * Gets the uniqueKey value
   * 
   * @return the uniqueKey
   */
  public String getUniqueKey() {
    return uniqueKey;
  }

  /**
   * Sets the value of feedUrl
   * @param feedUrl the feedUrl to set
   */
  public void setFeedUrl(String feedUrl) {
    this.feedUrl = feedUrl;
  }

  /**
   * Gets the feedUrl value
   * @return the feedUrl
   */
  public String getFeedUrl() {
    return feedUrl;
  }

  // /**
  // * Sets the value of pkvalue
  // * @param pkvalue the pkvalue to set
  // */
  // public void setPkvalue(String pkvalue) {
  // this.pkvalue = pkvalue;
  // }
  //
  // /**
  // * Gets the pkvalue value
  // * @return the pkvalue
  // */
  // public String getPkvalue() {
  // return pkvalue;
  // }

}
