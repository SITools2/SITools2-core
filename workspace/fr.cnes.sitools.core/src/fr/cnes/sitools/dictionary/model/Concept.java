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
package fr.cnes.sitools.dictionary.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Entry in a dictionary
 * 
 * @author jp.boignard (AKKA Technologies)
 */
@XStreamAlias("concept")
public class Concept extends ConceptTemplate {

  /**
   * Serial
   */
  private static final long serialVersionUID = 123456789L;

  /** Dictionary identifier */
  private String dictionaryId;

  /** Default constructor */
  public Concept() {
    super();
  }

  /**
   * Get the dictionary identifier
   * 
   * @return the dictionary identifier
   */
  public String getDictionaryId() {
    return dictionaryId;
  }

  /**
   * Set the dictionary identifier
   * 
   * @param dictionaryId
   *          the dictionary identifier to set
   */
  public void setDictionaryId(String dictionaryId) {
    this.dictionaryId = dictionaryId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((dictionaryId == null) ? 0 : dictionaryId.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Concept other = (Concept) obj;
    if (dictionaryId == null) {
      if (other.dictionaryId != null) {
        return false;
      }
    }
    else if (!dictionaryId.equals(other.dictionaryId)) {
      return false;
    }
    return super.equals(obj);
  }

}
