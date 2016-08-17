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
package fr.cnes.sitools.client.model;

import java.util.Date;

/**
 * Class for url coming from sitools.properties
 * 
 * @author b.fiorito (AKKA Technologies)
 */
public class Url {

  /** url */
  private String url = null;

  /** name */
  private String name = null;

  /** date modification */
  private Date lastmod = null;

  /**
   * Constructor with key and value
   * 
   * @param key2
   *          the key
   * @param value
   *          the value
   */
  public Url(String key2, String value) {
    name = key2;
    url = value;
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
   * Gets the lastmod value
   * 
   * @return the lastmod
   */
  public Date getLastmod() {
    return lastmod;
  }

  /**
   * Sets the value of lastmod
   * 
   * @param lastmod
   *          the lastmod to set
   */
  public void setLastmod(Date lastmod) {
    this.lastmod = lastmod;
  }

}
