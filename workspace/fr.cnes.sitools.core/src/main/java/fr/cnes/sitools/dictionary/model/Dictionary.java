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
package fr.cnes.sitools.dictionary.model;

import java.io.Serializable;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.IResource;

/**
 * Class for dictionary definition
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
@XStreamAlias("dictionary")
public final class Dictionary implements IResource, Serializable {

  /**
   * UUID for serialization
   */
  private static final long serialVersionUID = -6703906662311078977L;

  /**
   * Object identifier
   */
  private String id;

  /**
   * Object name
   */
  private String name;

  /**
   * Object description
   */
  private String description;

  /** The template for the concepts */
  private ConceptTemplate conceptTemplate;

  /**
   * Concept list
   */
  private List<Concept> concepts;

  /**
   * Default constructor
   */
  public Dictionary() {
    super();
  }

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
   * Gets the name value
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of name
   * 
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the description value
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the value of description
   * 
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the conceptTemplate value
   * 
   * @return the conceptTemplate
   */
  public ConceptTemplate getConceptTemplate() {
    return conceptTemplate;
  }

  /**
   * Sets the value of conceptTemplate
   * 
   * @param conceptTemplate
   *          the conceptTemplate to set
   */
  public void setConceptTemplate(ConceptTemplate conceptTemplate) {
    this.conceptTemplate = conceptTemplate;
  }

  /**
   * Gets the concepts value
   * 
   * @return the concepts
   */
  public List<Concept> getConcepts() {
    return concepts;
  }

  /**
   * Sets the value of concepts
   * 
   * @param concepts
   *          the concepts to set
   */
  public void setConcepts(List<Concept> concepts) {
    this.concepts = concepts;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Dictionary other = (Dictionary) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

}
