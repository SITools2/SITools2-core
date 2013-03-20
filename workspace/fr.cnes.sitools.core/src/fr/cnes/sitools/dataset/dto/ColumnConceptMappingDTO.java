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
package fr.cnes.sitools.dataset.dto;

import fr.cnes.sitools.dictionary.model.Concept;

/**
 * DTO to store a mapping between a column and a concept
 * 
 * 
 * @author m.gond
 */
public class ColumnConceptMappingDTO {
  /** The client side column identifier */
  private String columnAlias;

  /** The concept */
  private Concept concept;

  /**
   * Gets the columnAlias value
   * 
   * @return the columnAlias
   */
  public String getColumnAlias() {
    return columnAlias;
  }

  /**
   * Sets the value of columnAlias
   * 
   * @param columnAlias
   *          the columnAlias to set
   */
  public void setColumnAlias(String columnAlias) {
    this.columnAlias = columnAlias;
  }

  /**
   * Gets the concept value
   * 
   * @return the concept
   */
  public Concept getConcept() {
    return concept;
  }

  /**
   * Sets the value of concept
   * 
   * @param concept
   *          the concept to set
   */
  public void setConcept(Concept concept) {
    this.concept = concept;
  }

}
