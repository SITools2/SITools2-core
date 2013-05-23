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
package fr.cnes.sitools.datasource.jdbc.business;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.restlet.Restlet;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.datasource.common.DataSourceType;
import fr.cnes.sitools.datasource.common.SitoolsDataSource;
import fr.cnes.sitools.datasource.common.SitoolsDataSourceModel;
import fr.cnes.sitools.datasource.jdbc.dbexplorer.DBResultSet;
import fr.cnes.sitools.datasource.jdbc.model.JDBCDataSource;
import fr.cnes.sitools.datasource.jdbc.model.Structure;
import fr.cnes.sitools.datasource.jdbc.model.Table;

/**
 * Encapsulation of javax.sql.DataSource for : - Schema connection management - Presentation of generic methods for SQL
 * database consulting
 * 
 * FIXME ne plus utiliser JdbcRowSet implementation de sun >> warning au runtime
 * 
 * @author AKKA
 */
public class SitoolsSQLDataSource implements DataSource, SitoolsDataSource {

  /**
   * Logger
   */
  static final Logger LOG = Logger.getLogger(SitoolsSQLDataSource.class.getName());

  /**
   * Encapsulated model.JDBCDataSource object
   */
  private SitoolsDataSourceModel dsModel = null;

  /**
   * Encapsulated javax.sql.DataSource object
   */
  private DataSource ds = null;

  /**
   * Filter on a particular schema
   */
  private String schemaOnConnection = null;

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
   * @param ds
   *          javax.sql.DataSource
   * @param schemaOnConnection
   *          the schema connection
   * @param key
   *          the key associated to the source
   */
  public SitoolsSQLDataSource(JDBCDataSource key, DataSource ds, String schemaOnConnection) {
    // this.dsModel = new JDBCDataSource();
    this.dsModel = key;
    this.ds = ds;
    this.schemaOnConnection = schemaOnConnection;
  }

  /**
   * Get the meta data
   * 
   * @deprecated - use getMetadata(Table) instead
   * @param tableName
   *          the table name used
   * @return List<String> columns names
   */
  @Deprecated
  public List<String> getMetadata(String tableName) {
    return getMetadata(new Structure("", tableName));
  }

  /**
   * Get the meta data from the table itself
   * 
   * @param table
   *          the table used
   * @return a list of meta data
   */
  // public List<String> getMetadata(Table table) {
  // return getMetadata(new Structure("", table.getName()));
  // }

  /**
   * Get the list of columns of a table
   * 
   * @param table
   *          Table name
   * @return name of related columns of the table TODO evolution return List<Column>
   */
  public List<String> getMetadata(Structure table) {
    List<String> columnNameList = null;
    Connection conn = null;
    ResultSet rs = null;
    try {
      conn = getConnection();

      columnNameList = new ArrayList<String>();
      DatabaseMetaData metaData = conn.getMetaData();
      rs = metaData.getColumns(null, null, Wrapper.getReference(table), null);
      while (rs.next()) {
        columnNameList.add(rs.getString("COLUMN_NAME"));
      }
    }
    catch (SQLException ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
    finally {
      closeConnection(conn);
      closeResultSet(rs);
    }
    return columnNameList;
  }

  /**
   * Retrieves primary key of a table
   * 
   * @param table
   *          Table name
   * @return primary keys related to the table
   * @deprecated user getPrimaryKey(Table) instead
   */
  @Deprecated
  public List<String> getPrimaryKey(String table) {
    return getPrimaryKey(new Structure(table, schemaOnConnection));
  }

  /**
   * Get the primary key of the table
   * 
   * @param table
   *          the table to look at
   * @return a list of primary keys
   */
  public List<String> getPrimaryKey(Table table) {
    return getPrimaryKey(new Structure(table.getName(), table.getSchema()));
  }

  /**
   * Retrieves primary keys of a table
   * 
   * @param table
   *          Table object where name and schema are known
   * @return primary keys of the table as a list
   */
  public List<String> getPrimaryKey(Structure table) {

    List<String> columnNameList = new ArrayList<String>();
    Connection conn = null;
    ResultSet rs = null;
    try {
      conn = getConnection();

      DatabaseMetaData metaData = conn.getMetaData();
      rs = metaData.getPrimaryKeys(null, table.getSchemaName(), table.getName());
      while (rs.next()) {
        columnNameList.add(rs.getString("COLUMN_NAME"));
      }
    }
    catch (SQLException ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
    finally {
      closeConnection(conn);
      closeResultSet(rs);
    }
    return columnNameList;
  }

  /**
   * Return the list of tables in the database
   * 
   * @return the list of table names
   * @deprecated use getTables(String schemaPattern) instead
   */
  @Deprecated
  public List<String> getMetadata() {
    if (tableNameList != null) {
      return tableNameList;
    }
    Connection conn = null;
    ResultSet rs = null;
    try {
      conn = getConnection();

      tableNameList = new ArrayList<String>();
      DatabaseMetaData metaData = conn.getMetaData();
      rs = metaData.getTables(null, schemaOnConnection, null, new String[] {"TABLE"});
      while (rs.next()) {
        tableNameList.add(rs.getString("TABLE_NAME"));
      }
    }
    catch (SQLException ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
    finally {
      closeConnection(conn);
      closeResultSet(rs);
    }
    return tableNameList;
  }

  /**
   * Return the list of tables in the database
   * 
   * @param schemaPattern
   *          the schema pattern to access tables
   * @return the list of table names
   */
  public List<Table> getTables(String schemaPattern) {
    String schema = (schemaPattern == null) ? schemaOnConnection : schemaPattern;

    // Oracle : pour supprimer le caractère vide dans chaine
    if (null == schema || schema.equals("")) {
      schema = null;
    }

    ArrayList<Table> tables = new ArrayList<Table>();

    Connection conn = null;
    ResultSet rs = null;
    try {
      conn = getConnection();

      DatabaseMetaData metaData = conn.getMetaData();

      rs = metaData.getTables(null, schema, null, new String[] {"TABLE", "VIEW"});
      while (rs.next()) {
        tables.add(new Table(rs.getString("TABLE_NAME"), rs.getString("TABLE_SCHEM")));
      }
    }
    catch (SQLException ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
    finally {
      closeConnection(conn);
      closeResultSet(rs);
    }
    return tables;
  }

  /**
   * Permits to precise parameters of connection, such as schema
   * 
   * @return java.sql.Connection
   * @throws SQLException
   *           when query fails
   */
  @Override
  public Connection getConnection() throws SQLException {
    return ds.getConnection();
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return this.ds.getLogWriter();
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return this.ds.getLoginTimeout();
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    this.ds.setLogWriter(out);
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    this.ds.setLoginTimeout(seconds);

  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return this.ds.getConnection(username, password);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return ds.isWrapperFor(iface);
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return ds.unwrap(iface);
  }

  /**
   * Make the SQL request
   * 
   * @param sql
   *          SQL request
   * @param maxrows
   *          maximal number of rows
   * @param fetchSize
   *          fetching size
   * @return ResultSet
   * 
   * 
   */
  public ResultSet basicQuery(String sql, int maxrows, int fetchSize) {
    Connection conn = null;
    ResultSet rs = null;
    try {

      conn = getConnection();

      PreparedStatement prep = conn
        .prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      if (maxrows > -1) {
        prep.setMaxRows(maxrows);
      }
      if (fetchSize > -1) {
        prep.setFetchSize(fetchSize);
      }

      rs = prep.executeQuery();

      return new DBResultSet(rs, prep, conn);

    }
    catch (SQLException ex) {
      LOG.log(Level.SEVERE, null, ex);
      closeConnection(conn);
      closeResultSet(rs);
      conn = null;
    }
    catch (RuntimeException ex) {
      LOG.log(Level.SEVERE, null, ex);
      closeConnection(conn);
      closeResultSet(rs);
    }
    catch (Exception ex) {
      LOG.log(Level.SEVERE, null, ex);
      closeConnection(conn);
      closeResultSet(rs);
    }
    return null;
  }

  /**
   * To overload for each type of the database FIXME pas standard JDBC - à surcharger pour chaque type de BD
   * 
   * @param sql
   *          SQL request
   * @param maxrows
   *          maximal number of rows
   * @param offset
   *          pagination position start
   * @return request with pagination clause
   */
  public String addLimitOffset(String sql, int maxrows, int offset) {
    return sql + " LIMIT " + maxrows + " OFFSET " + offset;
  }

  /**
   * Make the SQL request starting at offset and returning maxrows records
   * 
   * 
   * @param sql
   *          SQL request
   * @param maxrows
   *          the maximal number of rows
   * @param offset
   *          the offset in rows
   * @return ResultSet
   */
  public ResultSet limitedQuery(String sql, int maxrows, int offset) {
    Connection conn = null;
    ResultSet rs = null;
    try {
      String sqlCompleted = addLimitOffset(sql, maxrows, offset);
      conn = getConnection();

      if (conn == null) {
        LOG.log(Level.WARNING, "getConnection failed");
        return null;
      }

      // modif inspiré par le LAM
      LOG.log(Level.INFO, "Limited query = " + sqlCompleted);
      // set autocommit false to enable the use of cursors
      conn.setAutoCommit(false);
      // Cela permet d'utiliser les mécanismes de streaming de JDBC
      PreparedStatement prep = conn.prepareStatement(sqlCompleted, ResultSet.TYPE_FORWARD_ONLY,
        ResultSet.CONCUR_READ_ONLY); // ,

      if (prep == null) {
        LOG.log(Level.WARNING, "prepareStatement failed");
        return null;
      }
      int fetchSize = SitoolsSettings.getInstance().getInt("Starter.JDBC_FETCH_SIZE");
      // modif inspirée par le LAM
      // On positionne la taille de chaque streaming
      prep.setFetchSize(fetchSize);
      prep.setFetchDirection(ResultSet.FETCH_FORWARD);

      rs = prep.executeQuery();

      return new DBResultSet(rs, prep, conn);

    }
    catch (SQLException ex) {
      LOG.log(Level.SEVERE, null, ex);
      closeConnection(conn);
      closeResultSet(rs);
      conn = null;
    }
    catch (RuntimeException ex) {
      LOG.log(Level.SEVERE, null, ex);
      closeConnection(conn);
      closeResultSet(rs);
    }
    catch (Exception ex) {
      LOG.log(Level.SEVERE, null, ex);
      closeConnection(conn);
      closeResultSet(rs);
      conn = null;
    }
    return null;
  }

  /**
   * Gets the DataSource Model
   * 
   * @return the dsModel
   */
  @Override
  public SitoolsDataSourceModel getDsModel() {
    return dsModel;
  }

  /**
   * Sets the data source model
   * 
   * @param dsModel
   *          the dsModel to set
   */
  @Override
  public void setDsModel(SitoolsDataSourceModel dsModel) {
    this.dsModel = dsModel;
  }

  /**
   * Gets the DataSource value
   * 
   * @return the DataSource
   */
  public DataSource getDs() {
    return ds;
  }

  /**
   * Sets the value of DataSource
   * 
   * @param ds
   *          the DataSource to set
   */
  public void setDs(DataSource ds) {
    this.ds = ds;
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
  @Override
  public void close() {
    if (ds instanceof BasicDataSource) {
      BasicDataSource bds = (BasicDataSource) ds;
      try {
        bds.close();
      }
      catch (SQLException e) {
        LOG.log(Level.INFO, null, e);
      }
    }
  }

  /**
   * Method to close the connection
   * 
   * @param conn
   *          the connection to close
   */
  private void closeConnection(Connection conn) {
    if (conn != null) {
      try {
        conn.close();
      }
      catch (SQLException e) {
        LOG.severe(e.getMessage());
      }
    }
  }

  /**
   * Method to close the result set
   * 
   * @param rs
   *          the result set to close
   */
  private void closeResultSet(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      }
      catch (SQLException e) {
        LOG.severe(e.getMessage());
      }
    }
  }

  @Override
  public DataSourceType getDataSourceType() {
    return DataSourceType.SQL;
  }

  /**
   * Return the parent Logger of all the Loggers used by this data source. This should be the Logger farthest from the
   * root Logger that is still an ancestor of all of the Loggers used by this data source. Configuring this Logger will
   * affect all of the log messages generated by the data source. In the worst case, this may be the root Logger.
   * 
   * @return the parent Logger for this data source
   * @throws SQLFeatureNotSupportedException
   *           if the data source does not use <code>java.util.logging<code>.
   */
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return LOG;
  }

}
