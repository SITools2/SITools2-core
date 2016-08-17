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
package fr.cnes.sitools.service.storage.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.persistence.Persistent;

/**
 * Storage directory
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
@XStreamAlias("directory")
public final class StorageDirectory implements IResource, Persistent {

  /** serialVersionUID */
  private static final long serialVersionUID = 142200684346805628L;

  /** ID of the storage */
  private String id;

  /** Name of the storage */
  private String name;

  /** Description of the storage */
  private String description;

  /** Local path of the storage */
  private String localPath;

  /** Public URL associated with the storage */
  private String publicUrl;

  /** attachment url */
  private String attachUrl;

  /** Indicates if the storage is deeply accessible */
  private boolean deeplyAccessible;

  /** Indicates if listing the storage is allowed */
  private boolean listingAllowed;

  /** Indicates of the storage is modifiable */
  private boolean modifiable;

  /** Indicates if the storage content must be indexed by solr for search capabilities */
  private boolean indexed;

  /** Authorizer instance */
  private String authorizerId = null;

  /**
   * The status
   */
  private String status;

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  /**
   * Get the local path of the storage
   * 
   * @return the localPath
   */
  public String getLocalPath() {
    return localPath;
  }

  /**
   * Set the local path of the storage
   * 
   * @param localPath
   *          the localPath to set
   */
  public void setLocalPath(String localPath) {
    this.localPath = localPath;
  }

  /**
   * Get the public URL associated with the directory
   * 
   * @return the publicUrl
   */
  public String getPublicUrl() {
    return publicUrl;
  }

  /**
   * Set the public URL of the directory
   * 
   * @param publicUrl
   *          the publicUrl to set
   */
  public void setPublicUrl(String publicUrl) {
    this.publicUrl = publicUrl;
  }

  /**
   * Set the id of the directory
   * 
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Set the name of the directory
   * 
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Set the description of the directory
   * 
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Indicates if the directory is deeply accessible
   * 
   * @return the deeplyAccessible
   */
  public boolean isDeeplyAccessible() {
    return deeplyAccessible;
  }

  /**
   * Set if the directory is deeply accessible
   * 
   * @param deeplyAccessible
   *          the deeplyAccessible to set
   */
  public void setDeeplyAccessible(boolean deeplyAccessible) {
    this.deeplyAccessible = deeplyAccessible;
  }

  /**
   * Indicates if the listing is allowed
   * 
   * @return the listingAllowed
   */
  public boolean isListingAllowed() {
    return listingAllowed;
  }

  /**
   * Set if the listing is allowed on the directory
   * 
   * @param listingAllowed
   *          the listingAllowed to set
   */
  public void setListingAllowed(boolean listingAllowed) {
    this.listingAllowed = listingAllowed;
  }

  /**
   * Indicates if the directory is modifiable
   * 
   * @return the modifiable
   */
  public boolean isModifiable() {
    return modifiable;
  }

  /**
   * Set if the directory is modifiable
   * 
   * @param modifiable
   *          the modifiable to set
   */
  public void setModifiable(boolean modifiable) {
    this.modifiable = modifiable;
  }

  /**
   * Set the attachment URL
   * 
   * @param attachUrl
   *          the attachUrl to set
   */
  public void setAttachUrl(String attachUrl) {
    this.attachUrl = attachUrl;
  }

  /**
   * Get the attachment URL
   * 
   * @return the attachUrl
   */
  public String getAttachUrl() {
    return attachUrl;
  }

  /**
   * Get the status
   * 
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Set the status
   * 
   * @param status
   *          the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Gets the authorizerId value
   * 
   * @return the authorizerId
   */
  public String getAuthorizerId() {
    return authorizerId;
  }

  /**
   * Sets the value of authorizerId
   * 
   * @param authorizerId
   *          the authorizerId to set
   */
  public void setAuthorizerId(String authorizerId) {
    this.authorizerId = authorizerId;
  }

  /**
   * Gets the indexed value
   * 
   * @return the indexed
   */
  public boolean isIndexed() {
    return indexed;
  }

  /**
   * Sets the value of indexed
   * 
   * @param indexed
   *          the indexed to set
   */
  public void setIndexed(boolean indexed) {
    this.indexed = indexed;
  }

}
