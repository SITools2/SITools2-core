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