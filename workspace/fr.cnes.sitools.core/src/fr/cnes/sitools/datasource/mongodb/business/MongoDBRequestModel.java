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
package fr.cnes.sitools.datasource.mongodb.business;

/**
 * Model object of a MongoDB request
 * 
 * 
 * @author m.gond
 */
public class MongoDBRequestModel {

  /**
   * String filter
   */
  private String filterString;

  /**
   * String key
   */
  private String keysString;

  /**
   * int start
   */
  private int start;

  /**
   * int limit
   */
  private int limit;

  /**
   * String name of the collection
   */
  private String collectionName;

  /**
   * String sort
   */
  private String sortString;

  /**
   * Gets the filterString value
   * 
   * @return the filterString
   */
  public String getFilterString() {
    return filterString;
  }

  /**
   * Sets the value of filterString
   * 
   * @param filterString
   *          the filterString to set
   */
  public void setFilterString(String filterString) {
    this.filterString = filterString;
  }

  /**
   * Gets the keysString value
   * 
   * @return the keysString
   */
  public String getKeysString() {
    return keysString;
  }

  /**
   * Sets the value of keysString
   * 
   * @param keysString
   *          the keysString to set
   */
  public void setKeysString(String keysString) {
    this.keysString = keysString;
  }

  /**
   * Gets the start value
   * 
   * @return the start
   */
  public int getStart() {
    return start;
  }

  /**
   * Sets the value of start
   * 
   * @param start
   *          the start to set
   */
  public void setStart(int start) {
    this.start = start;
  }

  /**
   * Gets the limit value
   * 
   * @return the limit
   */
  public int getLimit() {
    return limit;
  }

  /**
   * Sets the value of limit
   * 
   * @param limit
   *          the limit to set
   */
  public void setLimit(int limit) {
    this.limit = limit;
  }

  /**
   * Gets the collectionName value
   * 
   * @return the collectionName
   */
  public String getCollectionName() {
    return collectionName;
  }

  /**
   * Sets the value of collectionName
   * 
   * @param collectionName
   *          the collectionName to set
   */
  public void setCollectionName(String collectionName) {
    this.collectionName = collectionName;
  }

  /**
   * Get the current request as a String object
   * 
   * @return the current request as a String
   */
  public String toStringRequest() {
    return getFilterString() + "," + getKeysString();
  }

  /**
   * Gets the sortString value
   * 
   * @return the sortString
   */
  public String getSortString() {
    return sortString;
  }

  /**
   * Sets the value of sortString
   * 
   * @param sortString
   *          the sortString to set
   */
  public void setSortString(String sortString) {
    this.sortString = sortString;
  }

}
