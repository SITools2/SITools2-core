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
package fr.cnes.sitools.userstorage.model;

import java.util.Date;

/**
 * Class for information about disk space allowed to a user
 * 
 * @author AKKA
 * 
 */
public final class DiskStorage {

  /**
   * Directory
   */
  private String userStoragePath;

  /**
   * Free space in the quota
   */
  private Long freeUserSpace;

  /**
   * Busy space
   */
  private Long busyUserSpace;

  /**
   * Quota >> total space for a user
   */
  private Long quota;

  /**
   * The date of the last update (refresh)
   */
  private Date lastUpdate;

  /**
   * Default constructor
   */
  public DiskStorage() {
    super();
  }

  /**
   * Gets the freeUserSpace value
   * 
   * @return the freeUserSpace
   */
  public Long getFreeUserSpace() {
    return freeUserSpace;
  }

  /**
   * Sets the value of freeUserSpace
   * 
   * @param freeUserSpace
   *          the freeUserSpace to set
   */
  public void setFreeUserSpace(Long freeUserSpace) {
    this.freeUserSpace = freeUserSpace;
  }

  /**
   * Gets the busyUserSpace value
   * 
   * @return the busyUserSpace
   */
  public Long getBusyUserSpace() {
    return busyUserSpace;
  }

  /**
   * Sets the value of busyUserSpace
   * 
   * @param busyUserSpace
   *          the busyUserSpace to set
   */
  public void setBusyUserSpace(Long busyUserSpace) {
    this.busyUserSpace = busyUserSpace;
  }

  /**
   * Gets the quota value
   * 
   * @return the quota
   */
  public Long getQuota() {
    return quota;
  }

  /**
   * Sets the value of quota
   * 
   * @param quota
   *          the quota to set
   */
  public void setQuota(Long quota) {
    this.quota = quota;
  }

  /**
   * Gets the userStoragePath value
   * 
   * @return the userStoragePath
   */
  public String getUserStoragePath() {
    return userStoragePath;
  }

  /**
   * Sets the value of userStoragePath
   * 
   * @param userStoragePath
   *          the userStoragePath to set
   */
  public void setUserStoragePath(String userStoragePath) {
    this.userStoragePath = userStoragePath;
  }

  /**
   * Gets the lastUpdate value
   * 
   * @return the lastUpdate
   */
  public Date getLastUpdate() {
    return lastUpdate;
  }

  /**
   * Sets the value of lastUpdate
   * 
   * @param lastUpdate
   *          the lastUpdate to set
   */
  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

}
