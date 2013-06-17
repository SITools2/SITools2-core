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
package fr.cnes.sitools.userstorage.model;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Disk space allocated for a user
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
@XStreamAlias("userStorage")
public final class UserStorage implements Serializable {

  /** serialVersionUID */
  private static final long serialVersionUID = 1593134047575467004L;

  /** User identifier */
  private String userId;

  /** Physical properties of the userStorage */
  private DiskStorage storage;

  /** Status of user storage */
  private String status;

  // private boolean listingAllowed;
  // private boolean modifiable;
  // private boolean deeplyAccessible;

  /**
   * Gets the identifier value
   * 
   * @return the identifier
   */
  public String getUserId() {
    return userId;
  }

  /**
   * Sets the value of identifier
   * 
   * @param identifier
   *          the identifier to set
   */
  public void setUserId(String identifier) {
    this.userId = identifier;
  }

  /**
   * Gets the storage value
   * 
   * @return the storage
   */
  public DiskStorage getStorage() {
    return storage;
  }

  /**
   * Sets the value of storage
   * 
   * @param storage
   *          the storage to set
   */
  public void setStorage(DiskStorage storage) {
    this.storage = storage;
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

}
