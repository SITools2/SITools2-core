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
package fr.cnes.sitools.dataset.opensearch.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;


/**
 * Model to store OpenSearch columns
 * 
 * @author AKKA Technologies
 */
@XStreamAlias("opensearchColumn")
public final class OpensearchColumn {

  /**
   * The id of the column
   */
  private String idColumn;
  /**
   * The type of the column
   */
  private String type;

  /**
   * Gets the idColumn value
   * 
   * @return the idColumn
   */
  public String getIdColumn() {
    return idColumn;
  }

  /**
   * Sets the value of idColumn
   * 
   * @param idColumn
   *          the idColumn to set
   */
  public void setIdColumn(String idColumn) {
    this.idColumn = idColumn;
  }

  /**
   * Gets the type value
   * 
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the value of type
   * 
   * @param type
   *          the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

}
