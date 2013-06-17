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
package fr.cnes.sitools.datasource.common;

import fr.cnes.sitools.common.model.Resource;

/**
 * Abstract class to factories DataSourceModel attribute
 * 
 * 
 * @author m.gond
 */
public abstract class SitoolsDataSourceModel {

  /**
   * Object identifier
   */
  private String id;
  /**
   * Object name
   */
  private String name;
  /**
   * Object description
   */
  private String description;
  /**
   * Resource status
   */
  private String status;
  /**
   * JDBC database URL
   */
  private String url;
  /**
   * Login for user common access (pool + read-only)
   */
  private String userLogin;
  /**
   * Password for common user access (pool + read-only)
   * 
   * TODO Crypter le mot de passe utilisateur d'accès à la datasource.
   */
  private String userPassword;
  /**
   * RESTlet server attachment for users (optional)
   */
  private String sitoolsAttachementForUsers;
  /**
   * Connection pool configuration - number max of connections
   */
  private int maxActive = 3;
  /**
   * The driver class String
   */
  private String driverClass;

  /**
   * Defaut constructor
   */
  public SitoolsDataSourceModel() {
    super();
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
   * Gets the URL value
   * 
   * @return the URL
   */
  public String getUrl() {
    return url;
  }

  /**
   * Sets the value of URL
   * 
   * @param url
   *          the URL to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Gets the userLogin value
   * 
   * @return the userLogin
   */
  public String getUserLogin() {
    return userLogin;
  }

  /**
   * Sets the value of userLogin
   * 
   * @param userLogin
   *          the userLogin to set
   */
  public void setUserLogin(String userLogin) {
    this.userLogin = userLogin;
  }

  /**
   * Gets the userPassword value
   * 
   * @return the userPassword
   */
  public String getUserPassword() {
    return userPassword;
  }

  /**
   * Sets the value of userPassword
   * 
   * @param userPassword
   *          the userPassword to set
   */
  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }

  /**
   * Gets the sitoolsAttachementForUsers value
   * 
   * @return the sitoolsAttachementForUsers
   */
  public String getSitoolsAttachementForUsers() {
    return sitoolsAttachementForUsers;
  }

  /**
   * Sets the value of sitoolsAttachementForUsers
   * 
   * @param sitoolsAttachementForUsers
   *          the sitoolsAttachementForUsers to set
   */
  public void setSitoolsAttachementForUsers(String sitoolsAttachementForUsers) {
    this.sitoolsAttachementForUsers = sitoolsAttachementForUsers;
  }

  /**
   * Gets the maxActive value
   * 
   * @return the maxActive
   */
  public int getMaxActive() {
    return maxActive;
  }

  /**
   * Sets the value of maxActive
   * 
   * @param maxActive
   *          the maxActive to set
   */
  public void setMaxActive(int maxActive) {
    this.maxActive = maxActive;
  }

  /**
   * Utility to wrap the MongoDBDataSource to a generic resource
   * 
   * @return Resource
   */
  public abstract Resource wrap();

  /**
   * Gets the driverClass value
   * 
   * @return the driverClass
   */
  public String getDriverClass() {
    return driverClass;
  }

  /**
   * Sets the value of driverClass
   * 
   * @param driverClass
   *          the driverClass to set
   */
  public void setDriverClass(String driverClass) {
    this.driverClass = driverClass;
  }

}