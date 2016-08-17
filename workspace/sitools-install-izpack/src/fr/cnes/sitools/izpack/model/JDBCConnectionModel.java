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
package fr.cnes.sitools.izpack.model;


/**
 * Model to store informations about JDBC connection
 * 
 * @author m.gond
 * 
 */
public interface JDBCConnectionModel {

  /**
   * Gets the dbUrl value
   * 
   * @return the dbUrl
   */
  public String getDbUrl();

  /**
   * Gets the dbUser value
   * 
   * @return the dbUser
   */
  public String getDbUser();

  /**
   * Gets the dbPassword value
   * 
   * @return the dbPassword
   */
  public String getDbPassword();

  /**
   * Gets the password used for first connection
   * 
   * @return the password used for first connection
   */
  public String getDbConnectionPassword();

  /**
   * Gets the dbDriverClassName value
   * 
   * @return the dbDriverClassName
   */
  public String getDbDriverClassName();

  /**
   * Gets the dbType value
   * 
   * @return the dbType
   */
  public String getDbType();

  /**
   * Gets the dbSchema value
   * 
   * @return the dbSchema
   */
  public String getDbSchema();

}
