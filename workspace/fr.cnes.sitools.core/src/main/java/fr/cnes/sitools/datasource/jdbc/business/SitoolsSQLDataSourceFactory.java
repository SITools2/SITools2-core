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
package fr.cnes.sitools.datasource.jdbc.business;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.restlet.Context;

import fr.cnes.sitools.datasource.jdbc.model.JDBCDataSource;

/**
 * DataSources configuration / lookup via JNDI
 * 
 * Use of connection pools ApacheDbcp
 * 
 * @see http://commons.apache.org/dbcp/guide/jndi-howto.html
 * 
 *      TODO rendre plus parametrable pour que les DataSources puissent être précisément créés au demarrage du serveur
 *      Restlet à partir de la lecture du fichier de configuration
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class SitoolsSQLDataSourceFactory {

  /** Singleton instance */
  private static SitoolsSQLDataSourceFactory instance = null;

  /**
   * DataSources list
   */
  private static Map<String, SitoolsSQLDataSource> dataSources = new ConcurrentHashMap<String, SitoolsSQLDataSource>();

  /**
   * Private constructor for utility class
   */
  private SitoolsSQLDataSourceFactory() {
    super();
  }

  /**
   * Get an instance of SitoolsDatasource
   * 
   * @return an instance of SitoolsDatasource
   */
  public static synchronized SitoolsSQLDataSourceFactory getInstance() {
    if (instance == null) {
      instance = new SitoolsSQLDataSourceFactory();
    }
    return instance;
  }

  /**
   * Local creation of a DataSource
   * 
   * @param driver
   *          the database driver
   * @param connectURI
   *          the URI to connect
   * @param userName
   *          the database user name
   * @param password
   *          the password
   * @param schemaOnConnection
   *          the schema on connection
   * @return SitoolsDataSource a standard data source for SITools
   */
  public SitoolsSQLDataSource setupDataSource(String driver, String connectURI, String userName, String password,
      String schemaOnConnection) {

    String key = connectURI + "@" + userName;
    SitoolsSQLDataSource foundDatasource = dataSources.get(key);
    if (foundDatasource == null) {

      BasicDataSource ds = new BasicDataSource();
      // OSGi
      ds.setDriverClassLoader(getClass().getClassLoader());
      ds.setDriverClassName(driver);
      ds.setUsername(userName);
      ds.setPassword(password);
      ds.setUrl(connectURI);
      ds.setMaxTotal(10);
      ds.setInitialSize(1);
      // test that the connection is alive on each request. If not It will be dropped from the pool and another
      // connection will be created
      if (!"org.hsqldb.jdbcDriver".equals(driver)) {
        ds.setTestOnBorrow(true);
        ds.setValidationQuery("SELECT 1");
      }
      JDBCDataSource jdbcDS = new JDBCDataSource();
      jdbcDS.setName(key);
      jdbcDS.setDriverClass(driver);
      foundDatasource = new SitoolsSQLDataSource(jdbcDS, ds, schemaOnConnection);
      dataSources.put(key, foundDatasource);
    }
    return foundDatasource;
  }

  /**
   * Setup a dataSource for "users". Usage is for all users for consultation functions.
   * 
   * @param dataSource
   *          the DataSource to update
   * @return SitoolsDataSource the new DataSource
   */
  public SitoolsSQLDataSource setupDataSourceForUsers(JDBCDataSource dataSource) {
    String key = dataSource.getId();
    SitoolsSQLDataSource foundDatasource = dataSources.get(key);
    if (foundDatasource == null) {

      BasicDataSource ds = new BasicDataSource();
      // OSGi
      ds.setDriverClassLoader(getClass().getClassLoader());
      ds.setDriverClassName(dataSource.getDriverClass());
      ds.setUsername(dataSource.getUserLogin());
      ds.setPassword(dataSource.getUserPassword());
      ds.setUrl(dataSource.getUrl());
      ds.setMaxTotal(dataSource.getMaxActive());
      ds.setInitialSize(dataSource.getInitialSize());
      ds.setDefaultReadOnly(true);
      // test that the connection is alive on each request. If not It will be dropped from the pool and another
      // connection will be created
      ds.setTestOnBorrow(true);
      ds.setValidationQuery("SELECT 1");
      if ((dataSource.getSchemaOnConnection() != null) && !dataSource.getSchemaOnConnection().equals("")) {
        ds.setDefaultCatalog(dataSource.getSchemaOnConnection());
      }
      foundDatasource = new SitoolsSQLDataSource(dataSource, ds, dataSource.getSchemaOnConnection());
      dataSources.put(key, foundDatasource);
    }
    return foundDatasource;
  }

  /**
   * removeDataSource
   * 
   * @param dsName
   *          the name of the DataSource to remove
   */
  public static void removeDataSource(String dsName) {
    SitoolsSQLDataSource foundDatasource = dataSources.get(dsName);
    if (foundDatasource != null) {
      foundDatasource.close();
      dataSources.remove(dsName);
    }
  }

  /**
   * Get the DataSource by name
   * 
   * @param dsName
   *          the identifier of the DataSource
   * @return SitoolsDataSource
   */
  public static SitoolsSQLDataSource getDataSource(String dsName) {
    return dataSources.get(dsName);
  }

  /**
   * Retrieval of a DataSource as a JNDI resource
   * 
   * Lookup the DataSource, which will be backed by a pool that the application server provides. DataSource instances
   * are also a good candidate for caching as an instance variable, as JNDI lookups can be expensive as well.
   * 
   * @param dsName
   *          JNDI resource name
   * @return DataSource
   */
  public static DataSource getJNDIDataSource(String dsName) {
    SitoolsSQLDataSource foundDataSource = dataSources.get(dsName);
    if (foundDataSource != null) {
      return foundDataSource;
    }

    DataSource ds = null;
    try {
      InitialContext ctx = new InitialContext();

      ds = (DataSource) ctx.lookup(dsName);
      JDBCDataSource jdbcDS = new JDBCDataSource();
      jdbcDS.setName(dsName);
      dataSources.put(dsName, new SitoolsSQLDataSource(jdbcDS, ds, null));

    }
    catch (NamingException e) {
      Context.getCurrentLogger().log(Level.INFO, null, e);

    }
    return ds;
  }

  /**
   * Get an array of all DataSources
   * 
   * @return an Array of DataSource
   */
  public static ArrayList<SitoolsSQLDataSource> getAll() {
    ArrayList<SitoolsSQLDataSource> result = new ArrayList<SitoolsSQLDataSource>();
    for (SitoolsSQLDataSource ds : dataSources.values()) {
      result.add(ds);
    }
    return result;
  }
}
