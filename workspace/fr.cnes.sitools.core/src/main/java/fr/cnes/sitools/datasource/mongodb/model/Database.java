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
package fr.cnes.sitools.datasource.mongodb.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Base class for database representation
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
@XStreamAlias("database")
public final class Database {

  /**
   * TODO Quel nom donner a la database ? - le nom d'attachement ? pas top
   */
  private String url;

  /**
   * Collection list
   */
  private List<Collection> collections = new ArrayList<Collection>();

  /**
   * Default constructor
   */
  public Database() {
    super();
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
   * Gets the collections value
   * 
   * @return the collections
   */
  public List<Collection> getCollections() {
    return collections;
  }

  /**
   * Sets the value of collections
   * 
   * @param collections
   *          the collections to set
   */
  public void setCollections(List<Collection> collections) {
    this.collections = collections;
  }

}
