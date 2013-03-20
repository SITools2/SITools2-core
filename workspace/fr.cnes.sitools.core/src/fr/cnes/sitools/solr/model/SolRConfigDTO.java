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
package fr.cnes.sitools.solr.model;

import java.io.Serializable;

/**
 * SolR configuration model root Contains the models for schema.xml and db-data-config.xml
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */

public final class SolRConfigDTO implements Serializable {
  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = 815663822283046178L;
  /**
   * Name of the SolR index
   */
  private String indexName;

  /**
   * Schema.xml model DTO
   */
  private SchemaConfigDTO schemaConfigDTO;
  /**
   * data-config.xml model DTO
   */
  private DataConfigDTO dataConfigDTO;
  
  /**
   * returnedFieldDTO specific for RSS return
   */
  private RssXSLTDTO rssXSLTDTO;

  /**
   * Gets the schemaConfigDTO value
   * 
   * @return the schemaConfigDTO
   */
  public SchemaConfigDTO getSchemaConfigDTO() {
    return schemaConfigDTO;
  }

  /**
   * Sets the value of schemaConfigDTO
   * 
   * @param schemaConfigDTO
   *          the schemaConfigDTO to set
   */

  public void setSchemaConfigDTO(SchemaConfigDTO schemaConfigDTO) {
    this.schemaConfigDTO = schemaConfigDTO;
  }

  /**
   * Gets the dataConfigDTO value
   * 
   * @return the dataConfigDTO
   */

  public DataConfigDTO getDataConfigDTO() {
    return dataConfigDTO;
  }

  /**
   * Sets the value of dataConfigDTO
   * 
   * @param dataConfigDTO
   *          the dataConfigDTO to set
   */

  public void setDataConfigDTO(DataConfigDTO dataConfigDTO) {
    this.dataConfigDTO = dataConfigDTO;
  }

  /**
   * Gets the indexName value
   * 
   * @return the indexName
   */
  public String getIndexName() {
    return indexName;
  }

  /**
   * Sets the value of indexName
   * 
   * @param indexName
   *          the indexName to set
   */
  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }

  /**
   * Gets the returnedFieldDTO value
   * 
   * @return the returnedFieldDTO
   */
  public RssXSLTDTO getRssXSLTDTO() {
    return rssXSLTDTO;
  }

  /**
   * Sets the value of returnedFieldDTO
   * 
   * @param returnedFieldDTO
   *          the returnedFieldDTO to set
   */
  public void setRssXSLTDTO(RssXSLTDTO returnedFieldDTO) {
    this.rssXSLTDTO = returnedFieldDTO;
  }

}
