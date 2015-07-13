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
package fr.cnes.sitools.datasource.mongodb.business;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Restlet;
import org.restlet.engine.Engine;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

import fr.cnes.sitools.datasource.common.DataSourceType;
import fr.cnes.sitools.datasource.common.SitoolsDataSource;
import fr.cnes.sitools.datasource.common.SitoolsDataSourceModel;
import fr.cnes.sitools.datasource.jdbc.model.Table;
import fr.cnes.sitools.datasource.mongodb.model.MongoDBDataSource;

/**
 * Encapsulation of javax.sql.DataSource for : - Schema connection management - Presentation of generic methods for SQL
 * database consulting
 * 
 * FIXME ne plus utiliser JdbcRowSet implementation de sun >> warning au runtime
 * 
 * @author AKKA
 */
public class SitoolsMongoDBDataSource implements SitoolsDataSource {

  /**
   * Logger
   */
  static final Logger LOG = Engine.getLogger(SitoolsMongoDBDataSource.class.getName());

  /**
   * Encapsulated model.MongoDBDataSource object
   */
  private SitoolsDataSourceModel dsModel = null;
  /**
   * The mongo pool of connexion to a database
   */
  private Mongo mongo;

  /**
   * explorer parent
   */
  private Restlet explorer = null;

  /**
   * CACHE Tables name list
   */
  private List<String> tableNameList = null;

  /**
   * CACHE Table list
   */
  private List<Table> tableList = null;

  /**
   * Object build by DataSource encapsulation
   * 
   * @param key
   *          the key associated to the source
   * @param mongo
   *          the mongo db instance
   * 
   */
  public SitoolsMongoDBDataSource(SitoolsDataSourceModel key, Mongo mongo) {
    this.dsModel = key;
    this.mongo = mongo;
  }

  /**
   * Get the list of fields of a collection
   * 
   * @param collectionName
   *          the name of the mongoDB collection
   * @return name of related columns of the table TODO evolution return List<Column>
   */
  // TODO
  public List<String> getMetadata(String collectionName) {
    List<String> columnNameList = null;
    DB mongoDatabase = null;

    mongoDatabase = getDatabase();

    columnNameList = new ArrayList<String>();
    DBCollection collection = mongoDatabase.getCollection(collectionName);

    DBObject dbObject = collection.findOne();

    columnNameList.addAll(dbObject.keySet());

    return columnNameList;
  }

  // /**
  // * Get the primary key of the table
  // *
  // * @param table
  // * the table to look at
  // * @return a list of primary keys
  // */
  // public List<String> getPrimaryKey(Table table) {
  // return getPrimaryKey(new Structure(table.getName(), table.getSchema()));
  // }

  /**
   * Make the SQL request
   * 
   * @param key
   *          the key
   * @param request
   *          the mongoBD request model
   * @return List of Object
   * 
   * 
   */
  @SuppressWarnings("unchecked")
  public List<Object> distinctQuery(String key, MongoDBRequestModel request) {
    List<Object> results = null;
    try {
      DBObject dbObjectQuery = (DBObject) JSON.parse(request.getFilterString());
      DB database = getDatabase();
      DBCollection collection = database.getCollection(request.getCollectionName());

      results = collection.distinct(key, dbObjectQuery);
      return results;
    }
    catch (Exception ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
    return null;
  }

  /**
   * Execute a query
   * 
   * @param request the mongoBD request model
   * @return DBCursor
   */
  public DBCursor limitedQuery(MongoDBRequestModel request) {
    DBCursor cursor = null;
    try {
      DBObject dbObjectQuery = (DBObject) JSON.parse(request.getFilterString());
      DBObject dbObjectFields = (DBObject) JSON.parse(request.getKeysString());
      DBObject dbObjectSort = (DBObject) JSON.parse(request.getSortString());
      DB database = getDatabase();
      DBCollection collection = database.getCollection(request.getCollectionName());

      cursor = collection.find(dbObjectQuery, dbObjectFields).sort(dbObjectSort);
      cursor.skip(request.getStart());
      cursor.limit(request.getLimit());
      return cursor;
    }
    catch (Exception ex) {
      LOG.log(Level.SEVERE, null, ex);
      closeCursor(cursor);
    }
    return null;
  }

  /**
   * Gets the number of records for a given query
   * 
   * @param json
   *          the json query
   * @param collectionName
   *          the collection name to query
   * @return the number of records for the query, -1 if there is an error
   */
  public int countQuery(String json, String collectionName) {

    DBObject dbObject = (DBObject) JSON.parse(json);
    DB database = getDatabase();
    DBCollection collection = database.getCollection(collectionName);
    return (int) collection.count(dbObject);

  }

  /**
   * Gets the number of records for a given request model
   * 
   * @param request the mongoBD request model
   * @return int
   */
  public int countQuery(MongoDBRequestModel request) {
    return countQuery(request.getFilterString(), request.getCollectionName());

  }

  // /**
  // * Make the SQL request starting at offset and returning maxrows records
  // *
  // *
  // * @param sql
  // * SQL request
  // * @param maxrows
  // * the maximal number of rows
  // * @param offset
  // * the offset in rows
  // * @return ResultSet
  // */
  // public ResultSet limitedQuery(String sql, int maxrows, int offset) {
  // Connection conn = null;
  // ResultSet rs = null;
  // try {
  // String sqlCompleted = addLimitOffset(sql, maxrows, offset);
  // conn = getConnection();
  //
  // if (conn == null) {
  // LOG.log(Level.WARNING, "getConnection failed");
  // return null;
  // }
  //
  // // modif inspiré par le LAM
  // LOG.log(Level.INFO, "Limited query = " + sqlCompleted);
  // // set autocommit false to enable the use of cursors
  // conn.setAutoCommit(false);
  // // Cela permet d'utiliser les mécanismes de streaming de JDBC
  // PreparedStatement prep = conn.prepareStatement(sqlCompleted, ResultSet.TYPE_FORWARD_ONLY,
  // ResultSet.CONCUR_READ_ONLY); // ,
  //
  // if (prep == null) {
  // LOG.log(Level.WARNING, "prepareStatement failed");
  // return null;
  // }
  // int fetchSize = SitoolsSettings.getInstance().getInt("Starter.JDBC_FETCH_SIZE");
  // // modif inspirée par le LAM
  // // On positionne la taille de chaque streaming
  // prep.setFetchSize(fetchSize);
  // prep.setFetchDirection(ResultSet.FETCH_FORWARD);
  //
  // rs = prep.executeQuery();
  //
  // return new DBResultSet(rs, prep, conn);
  //
  // }
  // catch (SQLException ex) {
  // LOG.log(Level.SEVERE, null, ex);
  // closeConnection(conn);
  // closeResultSet(rs);
  // conn = null;
  // }
  // catch (RuntimeException ex) {
  // LOG.log(Level.SEVERE, null, ex);
  // closeConnection(conn);
  // closeResultSet(rs);
  // }
  // catch (Exception ex) {
  // LOG.log(Level.SEVERE, null, ex);
  // closeConnection(conn);
  // closeResultSet(rs);
  // conn = null;
  // }
  // return null;
  // }

  /**
   * Gets the DataSource Model
   * 
   * @return the dsModel
   */
  public SitoolsDataSourceModel getDsModel() {
    return dsModel;
  }

  /**
   * Sets the data source model
   * 
   * @param dsModel
   *          the dsModel to set
   */
  public void setDsModel(SitoolsDataSourceModel dsModel) {
    this.dsModel = dsModel;
  }

  /**
   * Gets the explorer value
   * 
   * @return the explorer
   */
  public Restlet getExplorer() {
    return explorer;
  }

  /**
   * Sets the value of explorer
   * 
   * @param explorer
   *          the explorer to set
   */
  public void setExplorer(Restlet explorer) {
    this.explorer = explorer;
  }

  /**
   * Gets the tableNameList value
   * 
   * @return the tableNameList
   */
  public List<String> getTableNameList() {
    return tableNameList;
  }

  /**
   * Sets the value of tableNameList
   * 
   * @param tableNameList
   *          the tableNameList to set
   */
  public void setTableNameList(List<String> tableNameList) {
    this.tableNameList = tableNameList;
  }

  /**
   * Gets the tableList value
   * 
   * @return the tableList
   */
  public List<Table> getTableList() {
    return tableList;
  }

  /**
   * Sets the value of tableList
   * 
   * @param tableList
   *          the tableList to set
   */
  public void setTableList(List<Table> tableList) {
    this.tableList = tableList;
  }

  /**
   * Close all connections
   */
  public void close() {
    if (mongo != null) {
      mongo.close();
    }
  }

  /**
   * Method to close the result set
   * 
   * @param cursor
   *          the {@link DBCursor} to close
   */
  private void closeCursor(DBCursor cursor) {
    if (cursor != null) {
      cursor.close();
    }
  }

  /**
   * Get a {@link DB} object to the database of the DataSource
   * 
   * @return a {@link DB} object
   */
  public DB getDatabase() {
    MongoDBDataSource datasource = (MongoDBDataSource) dsModel;
    DB database = mongo.getDB(datasource.getDatabaseName());
    if (datasource.isAuthentication() && !database.isAuthenticated()) {
      database.authenticate(dsModel.getUserLogin(), dsModel.getUserPassword().toCharArray());
    }
    return database;

  }

  /**
   * Gets the mongo value
   * 
   * @return the mongo
   */
  public Mongo getMongo() {
    return mongo;
  }

  /**
   * Sets the value of mongo
   * 
   * @param mongo
   *          the mongo to set
   */
  public void setMongo(Mongo mongo) {
    this.mongo = mongo;
  }

  public DataSourceType getDataSourceType() {
    return DataSourceType.MONGODB;
  }

}
