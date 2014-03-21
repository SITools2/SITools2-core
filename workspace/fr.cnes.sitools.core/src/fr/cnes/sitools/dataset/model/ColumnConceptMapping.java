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
package fr.cnes.sitools.dataset.model;

/**
 * Model class to store Mappings between a column and a list of concepts
 * 
 * 
 * @author m.gond (Akka Technologies)
 */
public class ColumnConceptMapping {

  /** The column identifier */
  private String columnId;

  /** The concepts identifier list */
  private String conceptId;

  /**
   * Gets the columnId value
   * 
   * @return the columnId
   */
  public String getColumnId() {
    return columnId;
  }

  /**
   * Sets the value of columnId
   * 
   * @param columnId
   *          the columnId to set
   */
  public void setColumnId(String columnId) {
    this.columnId = columnId;
  }

  /**
   * Gets the conceptId value
   * 
   * @return the conceptId
   */
  public String getConceptId() {
    return conceptId;
  }

  /**
   * Sets the value of conceptId
   * 
   * @param conceptId
   *          the conceptsId to set
   */
  public void setConceptId(String conceptId) {
    this.conceptId = conceptId;
  }

}
