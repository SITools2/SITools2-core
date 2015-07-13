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
package fr.cnes.sitools.solr.model;

import java.io.Serializable;

/**
 * 
 * Configuration Data Code SOLR
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class DataConfigDTO implements Serializable {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = 270639430113244255L;
  /**
   * document name
   */
  private String document = null;
 
  /**
   * Default constructor
   */
  public DataConfigDTO() {
    super();
  }

  /**
   * Gets the document value
   * 
   * @return the document
   */
  public String getDocument() {
    return document;
  }

  /**
   * Sets the value of document
   * 
   * @param document
   *          the document to set
   */
  public void setDocument(String document) {
    this.document = document;
  }

}
