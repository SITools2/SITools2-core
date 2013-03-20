/*******************************************************************************
 * Copyright 2011 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.datasource.mongodb.model;

import java.util.List;

/**
 * Model to represent Collection
 * 
 * 
 * @author m.gond
 */
public class Collection {
  /**
   * The name of the collection
   */
  private String name;
  /**
   * The url of the collection
   */
  private String url;
  /**
   * Collection status
   */
  private List<String> statusDetails;

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
   * Sets the value of url
   * 
   * @param url
   *          the url to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Gets the url value
   * 
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * Gets the statusDetails value
   * 
   * @return the statusDetails
   */
  public List<String> getStatusDetails() {
    return statusDetails;
  }

  /**
   * Sets the value of statusDetails
   * 
   * @param statusDetails
   *          the statusDetails to set
   */
  public void setStatusDetails(List<String> statusDetails) {
    this.statusDetails = statusDetails;
  }

}
