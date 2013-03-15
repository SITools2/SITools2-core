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
package fr.cnes.sitools.datasource.mongodb.business;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;

import fr.cnes.sitools.datasource.mongodb.model.MongoDBDataSource;

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
public final class SitoolsMongoDBDataSourceFactory {

  /** Singleton instance */
  private static SitoolsMongoDBDataSourceFactory instance = null;

  /**
   * DataSources list
   */
  private static Map<String, SitoolsMongoDBDataSource> dataSources = new ConcurrentHashMap<String, SitoolsMongoDBDataSource>();

  /**
   * Private constructor for utility class
   */
  private SitoolsMongoDBDataSourceFactory() {
    super();
  }

  /**
   * Get an instance of SitoolsDatasource
   * 
   * @return an instance of SitoolsDatasource
   */
  public static synchronized SitoolsMongoDBDataSourceFactory getInstance() {
    if (instance == null) {
      instance = new SitoolsMongoDBDataSourceFactory();
    }
    return instance;
  }

  /**
   * Setup a dataSource for "users". Usage is for all users for consultation functions.
   * 
   * @param dataSource
   *          the DataSource to update
   * @return SitoolsDataSource the new DataSource
   */
  public SitoolsMongoDBDataSource setupDataSourceForUsers(MongoDBDataSource dataSource) {
    String key = dataSource.getId();
    SitoolsMongoDBDataSource foundDatasource = dataSources.get(key);
    if (foundDatasource == null) {

      Mongo mongo = null;
      try {
        MongoOptions options = new MongoOptions();
        ServerAddress address = new ServerAddress(dataSource.getUrl(), dataSource.getPortNumber());
        options.setConnectionsPerHost(dataSource.getMaxActive());
        mongo = new Mongo(address, options);        
      }
      catch (UnknownHostException e) {
        e.printStackTrace();
      }
      catch (MongoException e) {
        e.printStackTrace();
      }

      foundDatasource = new SitoolsMongoDBDataSource(dataSource, mongo);
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
    SitoolsMongoDBDataSource foundDatasource = dataSources.get(dsName);
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
  public static SitoolsMongoDBDataSource getDataSource(String dsName) {
    return dataSources.get(dsName);
  }

  // /**
  // * Retrieval of a DataSource as a JNDI resource
  // *
  // * Lookup the DataSource, which will be backed by a pool that the application server provides. DataSource instances
  // * are also a good candidate for caching as an instance variable, as JNDI lookups can be expensive as well.
  // *
  // * @param dsName
  // * JNDI resource name
  // * @return DataSource
  // */
  // public static DataSource getJNDIDataSource(String dsName) {
  // MongoDBSitoolsDataSource foundDataSource = dataSources.get(dsName);
  // if (foundDataSource != null) {
  // return foundDataSource;
  // }
  //
  // DataSource ds = null;
  // try {
  // InitialContext ctx = new InitialContext();
  //
  // ds = (DataSource) ctx.lookup(dsName);
  // MongoDBDataSource mongoDs = new MongoDBDataSource();
  // mongoDs.setName(dsName);
  // dataSources.put(dsName, new MongoDBSitoolsDataSource(mongoDs, ds, null));
  //
  // }
  // catch (NamingException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // return ds;
  // }

  /**
   * Get an array of all DataSources
   * 
   * @return an Array of DataSource
   */
  public static ArrayList<SitoolsMongoDBDataSource> getAll() {
    ArrayList<SitoolsMongoDBDataSource> result = new ArrayList<SitoolsMongoDBDataSource>();
    for (SitoolsMongoDBDataSource ds : dataSources.values()) {
      result.add(ds);
    }
    return result;
  }
}
