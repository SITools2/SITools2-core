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
package fr.cnes.sitools.security;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSource;

/**
 * SQL Resources for postgresql
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class PGSQLUsersAndGroupsStoreResource extends JDBCUsersAndGroupsStoreResource {
  /**
   * The SitoolsDataSource
   */
  private static SitoolsSQLDataSource ds;

  /**
   * Constructor
   */
  public PGSQLUsersAndGroupsStoreResource() {
    super();
    initializeMessages(PGSQLUsersAndGroupsStoreResource.class.getName(), this);
  }

  /**
   * A constructor with a datasource parameter to check that the schema defined exists
   * 
   * @param datasource
   *          the datasource
   * @throws SitoolsException
   *           if there are some errors
   */
  public PGSQLUsersAndGroupsStoreResource(SitoolsSQLDataSource datasource) throws SitoolsException {
    super();

    // check that the given schema exists
    try {
      ds = datasource;
      String schema = ds.getSchemaOnConnection();
      if (schema == null) {
        throw new SitoolsException("No schema defined");
      }
      String sql = "select exists (select * from pg_catalog.pg_namespace where nspname = '" + schema + "');";
      ResultSet rs = ds.basicQuery(sql, 1, 1);
      rs.next();
      boolean exists = rs.getBoolean(1);
      if (!exists) {
        throw new SitoolsException("User database connection error, schema : " + schema + " can't be found");
      }
    }
    catch (SQLException e) {
      throw new SitoolsException("User database connection error", e);
    }
    initializeMessages(PGSQLUsersAndGroupsStoreResource.class.getName(), this);
  }

  /**
   * initializeMessages reading the resource bundle file
   * 
   * @param bundleName
   *          resource bundle name
   * @param properties
   *          Class name to be reflected
   */
  public static void initializeMessages(String bundleName, JDBCUsersAndGroupsStoreResource properties) {
    ResourceBundle bundle = ResourceBundle.getBundle(bundleName);
    String schema = ds.getSchemaOnConnection();
    for (Field field : properties.getClass().getFields()) {
      try {
        field.setAccessible(true);
        String query = bundle.getString(field.getName());
        query = query.replace("{SCHEMA}", schema);
        field.set(properties, query);
      }
      catch (IllegalArgumentException e) {
        Logger.getLogger("JDBCUsersAndGroupsStoreResource").severe(e.getMessage());
      }
      catch (IllegalAccessException e) {
        Logger.getLogger("JDBCUsersAndGroupsStoreResource").severe(e.getMessage());
      }
    }
  }

}
