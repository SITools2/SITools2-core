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
package fr.cnes.sitools.solr.model;

import java.util.List;

import fr.cnes.sitools.datasource.jdbc.model.JDBCDataSource;

/**
 * Datasource DTO class
 *
 * @author b.fiorito
 */
@SuppressWarnings("serial")
public class DBConfigDTO extends DataConfigDTO {
  /**
   * data source definition
   */
  private JDBCDataSource datasource;
  /**
   * EntitityDTO list
   */
  private List<EntityDTO> entities;

  /**
   * Default constructor
   */
  public DBConfigDTO() {
    super();
  }

  /**
   * 
   * Set the datasource and entities
   * 
   * @param datasource
   *          the JDBCdatasource
   * @param entities
   *          list of entities fields
   */
  public DBConfigDTO(JDBCDataSource datasource, List<EntityDTO> entities) {
    this.datasource = datasource;
    this.entities = entities;
  }

  /**
   * Gets the datasource value
   * 
   * @return the datasource
   */
  public JDBCDataSource getDatasource() {
    return datasource;
  }

  /**
   * Sets the value of datasource
   * 
   * @param datasource
   *          the datasource to set
   */
  public void setDatasource(JDBCDataSource datasource) {
    this.datasource = datasource;
  }

  /**
   * Gets the entities value
   * 
   * @return the entities
   */
  public List<EntityDTO> getEntities() {
    return entities;
  }

  /**
   * Sets the value of entities
   * 
   * @param entities
   *          the entities to set
   */
  public void setEntities(List<EntityDTO> entities) {
    this.entities = entities;
  }
}