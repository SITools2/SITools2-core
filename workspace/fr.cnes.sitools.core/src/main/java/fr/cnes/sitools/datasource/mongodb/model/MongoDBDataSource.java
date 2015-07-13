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
package fr.cnes.sitools.datasource.mongodb.model;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.datasource.common.SitoolsDataSourceModel;

/**
 * Class for definition of a MongoDB data source
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
@XStreamAlias("mongodbdatasource")
public final class MongoDBDataSource extends SitoolsDataSourceModel implements Serializable, IResource {
  /** serialVersionUID */
  private static final long serialVersionUID = -1456109441164598853L;
  /**
   * /** The name of the database to connect to
   */
  private String databaseName;
  /**
   * port number
   */
  private Integer portNumber;

  /** true if the server is in authentication mode, false otherwise */
  private boolean authentication;

  /**
   * Default constructor
   */
  public MongoDBDataSource() {
    super();
  }

  /**
   * Gets the databaseName value
   * 
   * @return the databaseName
   */
  public String getDatabaseName() {
    return databaseName;
  }

  /**
   * Sets the value of databaseName
   * 
   * @param databaseName
   *          the databaseName to set
   */
  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

  /**
   * Gets the portNumber value
   * 
   * @return the portNumber
   */
  public Integer getPortNumber() {
    return portNumber;
  }

  /**
   * Sets the value of portNumber
   * 
   * @param portNumber
   *          the portNumber to set
   */
  public void setPortNumber(Integer portNumber) {
    this.portNumber = portNumber;
  }

  /**
   * Gets the authentication value
   * 
   * @return the authentication
   */
  public boolean isAuthentication() {
    return authentication;
  }

  /**
   * Sets the value of authentication
   * 
   * @param authentication
   *          the authentication to set
   */
  public void setAuthentication(boolean authentication) {
    this.authentication = authentication;
  }

  @Override
  public Resource wrap() {
    Resource resource = new Resource();
    resource.setId(this.getId());
    resource.setName(this.getName());
    resource.setDescription(this.getDescription());
    resource.setType("MongoDBDataSource");
    resource.setUrl(this.getSitoolsAttachementForUsers());
    return resource;
  }

}
