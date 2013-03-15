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
package fr.cnes.sitools.common.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Class to store simple url
 * 
 * 
 * @author m.gond
 */
@XStreamAlias("url")
public class Url {

  /** url */
  private String url = null;

  /**
   * Default constructor
   */
  public Url() {
    super();
  }

  /**
   * Create a new Url with the given String url
   * 
   * @param url
   *          the url String
   */
  public Url(String url) {
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
   * Sets the value of url
   * 
   * @param url
   *          the url to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

}
