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
package fr.cnes.sitools.dataset.model;

/**
 * MultiSort Definition on a DataSet. Class used to serialize a multiple sort on a DataSet
 * 
 * @author d.arpin (AKKA Technologies)
 * 
 */
public final class Multisort {
  /**
   * Array of single Sort
   */
  private Sort[] ordersList;

  /**
   * Default Constructor
   */
  public Multisort() {
  }
  /**
   * Complete Constructor
   * @param ordersList
   *          the ordersList to set
   */
  public void setOrdersList(Sort[] ordersList) {
    if (ordersList != null) {
      this.ordersList = ordersList.clone();
    }
  }

  /**
   * Gets the value of orederList
   * @return the ordersList
   */
  public Sort[] getOrdersList() {
    return ordersList;
  }


}
