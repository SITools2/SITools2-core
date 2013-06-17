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
package fr.cnes.sitools.dataset.model;

import java.util.List;

/**
 * A Model class to store Dictionary mapping. It contains a Dictionary identifier and a list of mappings. The mapping
 * contains a column and a list of concepts from the dictionary;
 * 
 * 
 * @author m.gond (Akka Technologies)
 */
public final class DictionaryMapping {

  /** The dictionary Id */
  private String dictionaryId;

  /** The Mapping */
  private List<ColumnConceptMapping> mapping;
  
  /** If the dictionary is the default one */
  private boolean defaultDico;

  /**
   * Gets the dictionaryId value
   * 
   * @return the dictionaryId
   */
  public String getDictionaryId() {
    return dictionaryId;
  }

  /**
   * Sets the value of dictionaryId
   * 
   * @param dictionaryId
   *          the dictionaryId to set
   */
  public void setDictionaryId(String dictionaryId) {
    this.dictionaryId = dictionaryId;
  }

  /**
   * Gets the mapping value
   * 
   * @return the mapping
   */
  public List<ColumnConceptMapping> getMapping() {
    return mapping;
  }

  /**
   * Sets the value of mapping
   * 
   * @param mapping
   *          the mapping to set
   */
  public void setMapping(List<ColumnConceptMapping> mapping) {
    this.mapping = mapping;
  }

  /**
   * Sets the value of defaultDico
   * @param defaultDico the defaultDico to set
   */
  public void setDefaultDico(boolean defaultDico) {
    this.defaultDico = defaultDico;
  }

  /**
   * Gets the defaultDico value
   * @return the defaultDico
   */
  public boolean isDefaultDico() {
    return defaultDico;
  }

}
