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
package fr.cnes.sitools.security.authorization.client;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Simple class for client-admin exchange
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
@XStreamAlias("authorize")
public final class RoleAndMethodsAuthorization {

  /** name of the role */
  private String role;

  /** description of the role */
  private String description = null;

  /** All methods */
  @XStreamAlias("ALL")
  private Boolean allMethod = null;
  /** Post method (create) */
  @XStreamAlias("POST")
  private Boolean postMethod = null;
  /** Get method */
  @XStreamAlias("GET")
  private Boolean getMethod = null;
  /** Put method (update) */
  @XStreamAlias("PUT")
  private Boolean putMethod = null;
  /** Delete method */
  @XStreamAlias("DELETE")
  private Boolean deleteMethod = null;
  /** Head method */
  @XStreamAlias("HEAD")
  private Boolean headMethod = null;
  /** Option method */
  @XStreamAlias("OPTIONS")
  private Boolean optionsMethod = null;

  /**
   * Default constructor
   */
  public RoleAndMethodsAuthorization() {
    super();
  }

  /**
   * Gets the role value
   * 
   * @return the role
   */
  public String getRole() {
    return role;
  }

  /**
   * Sets the value of role
   * 
   * @param role
   *          the role to set
   */
  public void setRole(String role) {
    this.role = role;
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
   * Gets the allMethod value
   * 
   * @return the allMethod
   */
  public Boolean getAllMethod() {
    return allMethod;
  }

  /**
   * Sets the value of allMethod
   * 
   * @param allMethod
   *          the allMethod to set
   */
  public void setAllMethod(Boolean allMethod) {
    this.allMethod = allMethod;
  }

  /**
   * Gets the postMethod value
   * 
   * @return the postMethod
   */
  public Boolean getPostMethod() {
    return postMethod;
  }

  /**
   * Sets the value of postMethod
   * 
   * @param postMethod
   *          the postMethod to set
   */
  public void setPostMethod(Boolean postMethod) {
    this.postMethod = postMethod;
  }

  /**
   * Gets the getMethod value
   * 
   * @return the getMethod
   */
  public Boolean getGetMethod() {
    return getMethod;
  }

  /**
   * Sets the value of getMethod
   * 
   * @param getMethod
   *          the getMethod to set
   */
  public void setGetMethod(Boolean getMethod) {
    this.getMethod = getMethod;
  }

  /**
   * Gets the putMethod value
   * 
   * @return the putMethod
   */
  public Boolean getPutMethod() {
    return putMethod;
  }

  /**
   * Sets the value of putMethod
   * 
   * @param putMethod
   *          the putMethod to set
   */
  public void setPutMethod(Boolean putMethod) {
    this.putMethod = putMethod;
  }

  /**
   * Gets the deleteMethod value
   * 
   * @return the deleteMethod
   */
  public Boolean getDeleteMethod() {
    return deleteMethod;
  }

  /**
   * Sets the value of deleteMethod
   * 
   * @param deleteMethod
   *          the deleteMethod to set
   */
  public void setDeleteMethod(Boolean deleteMethod) {
    this.deleteMethod = deleteMethod;
  }

  /**
   * Gets the headMethod value
   * 
   * @return the headMethod
   */
  public Boolean getHeadMethod() {
    return headMethod;
  }

  /**
   * Sets the value of headMethod
   * 
   * @param headMethod
   *          the headMethod to set
   */
  public void setHeadMethod(Boolean headMethod) {
    this.headMethod = headMethod;
  }

  /**
   * Gets the optionsMethod value
   * 
   * @return the optionsMethod
   */
  public Boolean getOptionsMethod() {
    return optionsMethod;
  }

  /**
   * Sets the value of optionsMethod
   * 
   * @param optionsMethod
   *          the optionsMethod to set
   */
  public void setOptionsMethod(Boolean optionsMethod) {
    this.optionsMethod = optionsMethod;
  }

}
