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
package fr.cnes.sitools.dataset.dto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.cnes.sitools.dictionary.model.Concept;

/**
 * DTO to represent a dictionary Mapping
 * 
 * 
 * @author m.gond
 */
public class DictionaryMappingDTO {
  /** The dictionary Id */
  private String dictionaryId;

  /** The dictionary Name */
  private String dictionaryName;

  /** The Mapping */
  private List<ColumnConceptMappingDTO> mapping;

  /** If the dictionary is the default one */
  private boolean defaultDico;

  /**
   * Default constructor
   */
  public DictionaryMappingDTO() {
    mapping = new ArrayList<ColumnConceptMappingDTO>();
  }

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
   * Gets the dictionaryName value
   * 
   * @return the dictionaryName
   */
  public String getDictionaryName() {
    return dictionaryName;
  }

  /**
   * Sets the value of dictionaryName
   * 
   * @param dictionaryName
   *          the dictionaryName to set
   */
  public void setDictionaryName(String dictionaryName) {
    this.dictionaryName = dictionaryName;
  }

  /**
   * Gets the mapping value
   * 
   * @return the mapping
   */
  public List<ColumnConceptMappingDTO> getMapping() {
    return mapping;
  }

  /**
   * Add a mapping
   * 
   * @param colConcept
   *          the ColumnConceptMappingDTO to add
   * @return true if this collection changed as a result of the call
   */
  public boolean addMapping(ColumnConceptMappingDTO colConcept) {
    return getMapping().add(colConcept);
  }

  /**
   * Sets the value of mapping
   * 
   * @param mapping
   *          the mapping to set
   */
  public void setMapping(List<ColumnConceptMappingDTO> mapping) {
    this.mapping = mapping;
  }

  /**
   * Sets the value of defaultDico
   * 
   * @param defaultDico
   *          the defaultDico to set
   */
  public void setDefaultDico(boolean defaultDico) {
    this.defaultDico = defaultDico;
  }

  /**
   * Gets the defaultDico value
   * 
   * @return the defaultDico
   */
  public boolean isDefaultDico() {
    return defaultDico;
  }

  /**
   * Get the list of columnAlias mapped with the following concept name
   * 
   * @param conceptName
   *          the name of the concept
   * @return the list of columnAlias mapped with the following concept name, return an empty list if there is no column
   *         mapped
   */
  public List<String> getListColumnAliasMapped(String conceptName) {
    ArrayList<String> columnsAlias = new ArrayList<String>();
    // gets the column associated to the given concept name
    List<ColumnConceptMappingDTO> colConMap = getMapping();
    for (Iterator<ColumnConceptMappingDTO> iterator = colConMap.iterator(); iterator.hasNext();) {
      ColumnConceptMappingDTO colConDTO = iterator.next();
      if (colConDTO.getConcept().getName().equals(conceptName)) {
        columnsAlias.add(colConDTO.getColumnAlias());
      }
    }
    return columnsAlias;
  }

  /**
   * Get the list of Concept mapped with the following columnAlias
   * 
   * @param columnAlias
   *          the columnAlias of the column
   * @return the list of columns mapped with the following columnAlias, return an empty list if there is no columnAlias
   *         mapped
   */
  public List<Concept> getListConceptMapped(String columnAlias) {
    ArrayList<Concept> concepts = new ArrayList<Concept>();
    // gets the column associated to the given concept name
    List<ColumnConceptMappingDTO> colConMap = getMapping();
    for (Iterator<ColumnConceptMappingDTO> iterator = colConMap.iterator(); iterator.hasNext();) {
      ColumnConceptMappingDTO colConDTO = iterator.next();
      if (colConDTO.getColumnAlias().equals(columnAlias)) {
        concepts.add(colConDTO.getConcept());
      }
    }
    return concepts;
  }

  /**
   * Returns all the ColumnConceptMappingDTO mapped to the specified ConceptName
   * 
   * @param conceptName
   *          the ConceptName
   * @return the list of ColumnConceptMappingDTO mapped to the specified ConceptName
   */
  public List<ColumnConceptMappingDTO> getMappingFromConcept(String conceptName) {
    ArrayList<ColumnConceptMappingDTO> list = new ArrayList<ColumnConceptMappingDTO>();
    list.addAll(getMapping());
    for (Iterator<ColumnConceptMappingDTO> iterator = list.iterator(); iterator.hasNext();) {
      ColumnConceptMappingDTO columnConceptMappingDTO = (ColumnConceptMappingDTO) iterator.next();
      if (!columnConceptMappingDTO.getConcept().getName().equals(conceptName)) {
        iterator.remove();
      }
    }
    return list;
  }
}
