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
package fr.cnes.sitools.datasource.jdbc.model;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.datasource.common.SitoolsDataSourceModel;

/**
 * Class for definition of a JDBC data source
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
@XStreamAlias("jdbcdatasource")
public final class JDBCDataSource extends SitoolsDataSourceModel implements Serializable, IResource {

  /** serialVersionUID */
  private static final long serialVersionUID = -1456109441164598853L;

  /**
   * Connection pool configuration - number of connection when start
   */
  private int initialSize = 1;

  /**
   * schema to use for filtering database structures
   */
  private String schemaOnConnection;

  /**
   * Default constructor
   */
  public JDBCDataSource() {
    super();
  }

  /**
   * Gets the initialSize value
   * 
   * @return the initialSize
   */
  public int getInitialSize() {
    return initialSize;
  }

  /**
   * Sets the value of initialSize
   * 
   * @param initialSize
   *          the initialSize to set
   */
  public void setInitialSize(int initialSize) {
    this.initialSize = initialSize;
  }

  /**
   * Gets the schemaOnConnection value
   * 
   * @return the schemaOnConnection
   */
  public String getSchemaOnConnection() {
    return schemaOnConnection;
  }

  /**
   * Sets the value of schemaOnConnection
   * 
   * @param schemaOnConnection
   *          the schemaOnConnection to set
   */
  public void setSchemaOnConnection(String schemaOnConnection) {
    this.schemaOnConnection = schemaOnConnection;
  }

  /**
   * Utility to wrap the JDBCDataSource to a generic resource
   * 
   * @return Resource
   */
  public Resource wrap() {
    Resource resource = new Resource();
    resource.setId(this.getId());
    resource.setName(this.getName());
    resource.setDescription(this.getDescription());
    resource.setType("JDBCDataSource");
    resource.setUrl(this.getSitoolsAttachementForUsers());
    return resource;
  }

}
