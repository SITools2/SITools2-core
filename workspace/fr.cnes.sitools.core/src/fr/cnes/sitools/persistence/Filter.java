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
package fr.cnes.sitools.persistence;

/**
 * Basic filter definition for Sitools objects querying
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class Filter {

  /**
   * query parameter
   */
  private String query = null;

  /**
   * Sort - Field name
   */
  private String sort = null;

  /**
   * Sort - Order ASC / DESC
   */
  private String order = "ASC";

  /**
   * Default constructor
   */
  public Filter() {
    super();
  }

  /**
   * Gets the query value
   * 
   * @return the query
   */
  public String getQuery() {
    return query;
  }

  /**
   * Sets the value of query
   * 
   * @param query
   *          the query to set
   */
  public void setQuery(String query) {
    this.query = query;
  }

  /**
   * Gets the sort value
   * 
   * @return the sort
   */
  public String getSort() {
    return sort;
  }

  /**
   * Sets the value of sort
   * 
   * @param sort
   *          the sort to set
   */
  public void setSort(String sort) {
    this.sort = sort;
  }

  /**
   * Gets the order value
   * 
   * @return the order
   */
  public String getOrder() {
    return order;
  }

  /**
   * Sets the value of order
   * 
   * @param order
   *          the order to set
   */
  public void setOrder(String order) {
    this.order = order;
  }

}
