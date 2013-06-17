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
package fr.cnes.sitools.security.model;

import fr.cnes.sitools.dataset.model.DataSet;

/**
 * Dataset privileges
 * @author AKKA
 *
 */
public final class DataSetPrivilege {
  
  /** User */
  private User user;
  
  /** Dataset */
  private DataSet dataSet;
  
  /** Policy */
  private DataPolicy policy;

  /**
   * Gets the user value
   * 
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * Sets the value of user
   * 
   * @param user
   *          the user to set
   */
  public void setUser(User user) {
    this.user = user;
  }

  /**
   * Gets the dataSet value
   * 
   * @return the dataSet
   */
  public DataSet getDataSet() {
    return dataSet;
  }

  /**
   * Sets the value of dataSet
   * 
   * @param dataSet
   *          the dataSet to set
   */
  public void setDataSet(DataSet dataSet) {
    this.dataSet = dataSet;
  }

  /**
   * Gets the policy value
   * 
   * @return the policy
   */
  public DataPolicy getPolicy() {
    return policy;
  }

  /**
   * Sets the value of policy
   * 
   * @param policy
   *          the policy to set
   */
  public void setPolicy(DataPolicy policy) {
    this.policy = policy;
  }

}
