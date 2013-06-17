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
package fr.cnes.sitools.dataset.filter.dto;

import java.util.List;

/**
 * A class to represent the order of filters in the FilterChainedModel
 * 
 * @author m.gond (AKKA Technologies)
 */
public class FilterChainedOrderDTO {

  /** The id of the ConverterChainedModel */
  private String id;

  /** List of ids */
  private List<String> idOrder;

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
   * Gets the idOrder value
   * 
   * @return the idOrder
   */
  public List<String> getIdOrder() {
    return idOrder;
  }

  /**
   * Sets the value of idOrder
   * 
   * @param idOrder
   *          the idOrder to set
   */
  public void setIdOrder(List<String> idOrder) {
    this.idOrder = idOrder;
  }

}
