     /*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.dataset.opensearch.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.common.model.Resource;

/**
 * DataSet OpenSearch functionalities : field index
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 *         <a
 *         href="http://www.opensearch.org/Specifications/OpenSearch/1.1# OpenSearch_description_document">http://www.
 *         opensearch.org/Specifications/OpenSearch/1.1# OpenSearch_description_document</a><br/>
 * 
 */
@XStreamAlias("opensearch")
public final class Opensearch implements Serializable, IResource {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -2956733020988611432L;

  /**
   * OpenSearch functionality identifier
   */
  private String id = null;

  /**
   * Request name
   */
  private String name = null;

  /**
   * Request description
   */
  private String description = null;

  /**
   * Indexed columns list. Stores columns id only.
   */
  @XStreamAlias("indexedColumns")
  private List<OpensearchColumn> indexedColumns = new ArrayList<OpensearchColumn>();

  /**
   * Mapping DataSet columns, standards fields RSS results / Atom
   */
  private String titleField = null;

  /**
   * RSS / Atom , description field
   */
  private String descriptionField = null;

  /**
   * RSS / Atom, link field
   */
  private String linkField = null;

  /** true if the linkField is absolute, false otherwise */
  private boolean linkFieldRelative;
  /**
   * RSS / Atom, guid field
   */
  private String guidField = null;

  /**
   * RSS / Atom, pubDate field
   */
  private String pubDateField = null;

  // link >> url du record

  /**
   * Index file path
   */
  private String indexPath = null;

  /**
   * Request resource path
   */
  private String requestPath = null;

  /**
   * Enabling / disabling management
   */
  private String status = null;

  /**
   * DataSet id
   */
  private String parent = null;

  /**
   * Image = resource
   */
  private Resource image;

  /**
   * Default search field
   */
  private String defaultSearchField;
  /**
   * unique key for index
   */
  private String uniqueKey;

  /**
   * Name of columns used for keywords auto-completion
   */
  @XStreamAlias("keywordColumns")
  private ArrayList<String> keywordColumns;

  /**
   * Last import date
   */
  private Date lastImportDate;
  /**
   * Error message of the last execution
   */
  private String errorMsg;

  /**
   * The parent dataset's url
   */
  private String parentUrl;

  /**
   * Default constructor
   */
  public Opensearch() {
    super();
  }

  /**
   * Gets the requestPath value
   * 
   * @return the requestPath
   */
  public String getRequestPath() {
    return requestPath;
  }

  /**
   * Sets the value of requestPath
   * 
   * @param requestPath
   *          the requestPath to set
   */
  public void setRequestPath(String requestPath) {
    this.requestPath = requestPath;
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
   * Gets the defaultSearchField value
   * 
   * @return the defaultSearchField
   */
  public String getDefaultSearchField() {
    return defaultSearchField;
  }

  /**
   * Sets the value of defaultSearchField
   * 
   * @param defaultSearchField
   *          the defaultSearchField to set
   */
  public void setDefaultSearchField(String defaultSearchField) {
    this.defaultSearchField = defaultSearchField;
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
   * Sets the value of uniqueKey
   * 
   * @param uniqueKey
   *          the uniqueKey to set
   */
  public void setUniqueKey(String uniqueKey) {
    this.uniqueKey = uniqueKey;
  }

  /**
   * Gets the keywordColumns value
   * 
   * @return the keywordColumns
   */
  public ArrayList<String> getKeywordColumns() {
    return keywordColumns;
  }

  /**
   * Sets the value of keywordColumns
   * 
   * @param keywordColumns
   *          the keywordColumns to set
   */
  public void setKeywordColumns(ArrayList<String> keywordColumns) {
    this.keywordColumns = keywordColumns;
  }

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
   * Gets the indexedColumns value
   * 
   * @return the indexedColumns
   */
  public List<OpensearchColumn> getIndexedColumns() {
    return indexedColumns;
  }

  /**
   * Sets the value of indexedColumns
   * 
   * @param indexedColumns
   *          the indexedColumns to set
   */
  public void setIndexedColumns(List<OpensearchColumn> indexedColumns) {
    this.indexedColumns = indexedColumns;
  }

  /**
   * Gets the titleField value
   * 
   * @return the titleField
   */
  public String getTitleField() {
    return titleField;
  }

  /**
   * Sets the value of titleField
   * 
   * @param titleField
   *          the titleField to set
   */
  public void setTitleField(String titleField) {
    this.titleField = titleField;
  }

  /**
   * Gets the descriptionField value
   * 
   * @return the descriptionField
   */
  public String getDescriptionField() {
    return descriptionField;
  }

  /**
   * Sets the value of descriptionField
   * 
   * @param descriptionField
   *          the descriptionField to set
   */
  public void setDescriptionField(String descriptionField) {
    this.descriptionField = descriptionField;
  }

  /**
   * Gets the LinkField value
   * 
   * @return the LinkField
   */
  public String getLinkField() {
    return linkField;
  }

  /**
   * Sets the value of LinkField
   * 
   * @param linkField
   *          the LinkField to set
   */
  public void setLinkField(String linkField) {
    this.linkField = linkField;
  }

  /**
   * Gets the GuidField value
   * 
   * @return the GuidField
   */
  public String getGuidField() {
    return guidField;
  }

  /**
   * Sets the value of GuidField
   * 
   * @param guidField
   *          the GuidField to set
   */
  public void setGuidField(String guidField) {
    this.guidField = guidField;
  }

  /**
   * Gets the PubDateField value
   * 
   * @return the PubDateField
   */
  public String getPubDateField() {
    return pubDateField;
  }

  /**
   * Sets the value of PubDateField
   * 
   * @param pubDateField
   *          the PubDateField to set
   */
  public void setPubDateField(String pubDateField) {
    this.pubDateField = pubDateField;
  }

  /**
   * Gets the indexPath value
   * 
   * @return the indexPath
   */
  public String getIndexPath() {
    return indexPath;
  }

  /**
   * Sets the value of indexPath
   * 
   * @param indexPath
   *          the indexPath to set
   */
  public void setIndexPath(String indexPath) {
    this.indexPath = indexPath;
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
   * Gets the parent value
   * 
   * @return the parent
   */
  public String getParent() {
    return parent;
  }

  /**
   * Sets the value of parent
   * 
   * @param parent
   *          the parent to set
   */
  public void setParent(String parent) {
    this.parent = parent;
  }

  /**
   * Sets the value of lastImportDate
   * 
   * @param lastImportDate
   *          the lastImportDate to set
   */
  public void setLastImportDate(Date lastImportDate) {
    this.lastImportDate = lastImportDate;
  }

  /**
   * Gets the lastImportDate value
   * 
   * @return the lastImportDate
   */
  public Date getLastImportDate() {
    return lastImportDate;
  }

  /**
   * Sets the value of errorMsg
   * 
   * @param errorMsg
   *          the errorMsg to set
   */
  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  /**
   * Gets the errorMsg value
   * 
   * @return the errorMsg
   */
  public String getErrorMsg() {
    return errorMsg;
  }

  /**
   * Sets the value of parentUrl
   * 
   * @param parentUrl
   *          the parentUrl to set
   */
  public void setParentUrl(String parentUrl) {
    this.parentUrl = parentUrl;
  }

  /**
   * Gets the parentUrl value
   * 
   * @return the parentUrl
   */
  public String getParentUrl() {
    return parentUrl;
  }

  /**
   * Sets the value of linkFieldRelative
   * 
   * @param linkFieldRelative
   *          the linkFieldAbsolute to set
   */
  public void setLinkFieldRelative(boolean linkFieldRelative) {
    this.linkFieldRelative = linkFieldRelative;
  }

  /**
   * Gets the linkFieldAbsolute value
   * 
   * @return the linkFieldAbsolute
   */
  public boolean isLinkFieldRelative() {
    return linkFieldRelative;
  }

}
