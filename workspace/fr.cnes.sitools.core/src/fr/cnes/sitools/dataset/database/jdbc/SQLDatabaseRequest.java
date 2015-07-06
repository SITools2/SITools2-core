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
package fr.cnes.sitools.dataset.database.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.database.common.RequestFactory;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.Multisort;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.dataset.model.Sort;
import fr.cnes.sitools.dataset.model.structure.SitoolsStructure;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSource;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.util.DateUtils;

/**
 * Create a request SQL
 * 
 * @author c.mozdzierz <a
 *         href="https://sourceforge.net/tracker/?func=detail&atid=2158259&aid=3355053&group_id=531341">[#3355053]</a><br/>
 *         2011/07/06 d.arpin {Change the name and the return of the method getColumnFromQuery ->
 *         getColumnFromDistinctQuery} <br/>
 */
public class SQLDatabaseRequest implements DatabaseRequest {
  /** the request parameters */
  protected DatabaseRequestParameters params;

  /** The result set */
  protected ResultSet rs = null;

  /** The primaryKeys */
  protected List<String> primaryKeys;

  /** The nbSendedResults */
  protected int nbSendedResults = 0;

  /** Resource logger Context / Application ? ok */
  private Logger logger = Context.getCurrentLogger();

  /** The nbTotalResults */
  private int nbTotalResults = 0;

  /** The nbResultsToSend */
  private int nbResultsToSend = 0;

  /** The Datasource **/
  private SitoolsSQLDataSource datasource = null;

  /**
   * Create a {@code SQLDatabaseRequest}
   * 
   * @param params
   *          the request parameters
   */
  public SQLDatabaseRequest(DatabaseRequestParameters params) {
    this.params = params;
    primaryKeys = this.getPrimaryKeys();
    nbResultsToSend = params.getPaginationExtend();
    datasource = (SitoolsSQLDataSource) this.params.getDb();
  }


  @Override
  public List<String> getPrimaryKeys() {
    List<String> pks = new ArrayList<String>();
    List<Column> columns = params.getDataset().getColumnModel();
    for (Column column : columns) {
      if (column.isPrimaryKey()) {
        pks.add(column.getColumnAlias());
      }
    }
    return pks;
  }

  @Override
  public List<String> getSelectedPrimaryKey() {
    if (params.getDistinct()) {
      return null;
    }
    List<String> pks = new ArrayList<String>();
    List<Column> columns = params.getDataset().getColumnModel();
    for (Column column : columns) {
      if (column.isPrimaryKey()) {
        pks.add(column.getColumnAlias());

      }
    }
    return pks;
  }

  @Override
  public void createDistinctRequest() throws SitoolsException {
    try {
      String sql = this.getDistinctRequestAsString();
      if (sql == null) {
        return;
      }
      this.logger.log(Level.INFO, "SQL = " + sql);
      if (nbResultsToSend == DatabaseRequestParameters.RETURN_ALL) {
        nbResultsToSend = getMaxResultsToSend();
      }
      else if (nbResultsToSend == DatabaseRequestParameters.RETURN_FIRST_PAGE) {
        nbResultsToSend = getPageSize();
      }
      rs = datasource.basicQuery(sql, nbResultsToSend, nbResultsToSend);
      int startIndex = params.getStartIndex();
      if (rs != null) {
        // Moves the cursor
        rs.beforeFirst();
        int count = 0;
        while (rs.next()) {
          count++;
        }
        nbTotalResults = count;
        nbResultsToSend = getMaxResultsToSend(); // Integer.parseInt(SitoolsSettings.getInstance().getString("AbstractDatabaseRequest.MAX_ROWS"));
        nbResultsToSend = nbResultsToSend > nbTotalResults - startIndex ? nbTotalResults - startIndex : nbResultsToSend;
        rs.beforeFirst();
      }
      else {
        throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE, "dataset.records.createSQLRequest");
      }
    }
    catch (NumberFormatException e) {
      throw new SitoolsException("ERROR " + e.getMessage(), e);
    }
    catch (SQLException e) {
      throw new SitoolsException("ERROR " + e.getMessage(), e);
    }
  }

  @Override
  public void createRequest() throws SitoolsException {

    try {

      int startIndex = params.getStartIndex();

      String sql = getRequestAsString();

      this.logger.log(Level.INFO, "SQL = " + sql);
      rs = datasource.limitedQuery(sql, nbResultsToSend, startIndex);
      if (rs == null) {
        throw new SitoolsException("Error while querying datasource");
      }

    }
    catch (NumberFormatException e) {
      throw new SitoolsException("ERROR " + e.getMessage(), e);
    }

  }

  @Override
  public int calculateTotalCountFromBase() throws SitoolsException {
    String sql;
    if (!params.getDistinct()) {
      List<Column> columns = params.getDataset().getColumnModel();
      List<Predicat> predicats = params.getPredicats();
      // List<Structure> structures = params.getStructures();
      SitoolsStructure structure = params.getDataset().getStructure();

      RequestSql request = RequestFactory.getRequest(params.getDb().getDsModel().getDriverClass());
      sql = "";
      if ("S".equals(params.getDataset().getQueryType())) {
        sql += " " + params.getDataset().getSqlQuery() + request.getWhereClause(predicats, columns);
      }
      else {
        sql += " FROM " + request.getFromClauseAdvanced(structure);
        sql += " WHERE 1=1 " + request.getWhereClause(predicats, columns);
      }
      this.logger.log(Level.INFO, "SQL = " + sql);
    }
    else {
      sql = this.getDistinctRequestAsString();
    }

    if (sql == null) {
      return 0;
    }
    else {
      return totalCount(sql);
    }

  }

  /**
   * Compute the number of Records in the current request
   * 
   * @param sql
   *          the request without the select clause
   * @return the number of total results
   * @throws SitoolsException
   *           if a database access error occurs or this method is called on a closed result set
   */
  private int totalCount(String sql) throws SitoolsException {
    ResultSet resultSet = null;
    try {
      RequestSql request = RequestFactory.getRequest(params.getDb().getDsModel().getDriverClass());

      resultSet = datasource.limitedQuery(
          "SELECT count(*) from (SELECT " + request.getAttributes(params.getSqlVisibleColumns()) + sql
              + ") as datasetQuery", 1, 0);

      if (resultSet == null) {
        throw new SitoolsException("Error while querying datasource");
      }
      int count = 0;
      if (resultSet != null) {
        if (resultSet.next()) {
          count = resultSet.getInt(1);
        }
      }
      return count;
    }
    catch (SQLException ex) {
      this.logger.log(Level.SEVERE, null, ex);
      throw new SitoolsException(ex.getMessage(), ex);
    }
    finally {
      if (resultSet != null) {
        try {
          resultSet.close();
        }
        catch (Exception e) {
          logger.log(Level.INFO, null, e);
        }
      }
    }
  }

  @Override
  public int getTotalCount() {
    return nbTotalResults;
  }

  @Override
  public int getCount() {
    return nbResultsToSend;
  }

  @Override
  public final int getStartIndex() {
    return params.getStartIndex();
  }

  @Override
  public boolean nextResult() throws SitoolsException {
    try {
      if (rs == null) {
        return false;
      }
      return rs.next() && nbSendedResults++ < nbResultsToSend;
    }
    catch (SQLException e) {
      logger.warning(e.getMessage());
      throw new SitoolsException("ERROR_FETCHING_RESULTSET", e);
    }

  }

  @Override
  @Deprecated
  public boolean isLastResult() throws SitoolsException {
    try {
      return rs.isLast() || nbSendedResults >= nbResultsToSend;
    }
    catch (SQLException e) {
      throw new SitoolsException("ERROR_FETCHING_LAST");
    }

  }

  @Override
  public Record getRecord() throws SitoolsException {
    try {
      Record record = new Record(buildURI());
      setAttributeValues(record, rs);
      return record;
    }
    catch (SQLException e) {
      throw new SitoolsException("ERROR_BUILDING_RECORD", e);
    }
  }

  @Override
  public final String buildURI() throws SitoolsException {
    try {

      List<String> selectedPrimaryKey = getSelectedPrimaryKey();
      if ((selectedPrimaryKey == null) || (selectedPrimaryKey.size() <= 0)) {
        // logger.warning("selectedPrimaryKey null or empty !");
        return null;
      }

      String uri = params.getBaseRef() + "/";
      for (String it : selectedPrimaryKey) {
        uri += rs.getString(it) + ";";
      }
      return (uri.equals("")) ? null : uri.substring(0, uri.lastIndexOf(";"));
    }
    catch (SQLException e) {
      logger.log(Level.INFO, null, e);
      throw new SitoolsException("ERROR_BUILDING_URI", e);
    }
  }

  /**
   * Fill in attributes for a given Record
   * 
   * @param record
   *          the <code>Record</code>
   * @param rs
   *          the resultset
   * @throws SQLException
   *           if a database access error occurs or this method is called on a closed result set
   */
  public static final void setAttributeValues(Record record, ResultSet rs) throws SQLException {
    ResultSetMetaData resultMeta = rs.getMetaData();
    int columnCount = resultMeta.getColumnCount();
    int columnType;
    for (int i = 1; i <= columnCount; i++) {
      Object obj;
      // pour éviter bug double précision avec Postgresql
      columnType = resultMeta.getColumnType(i);
      Date date;
      switch (columnType) {
        case java.sql.Types.DOUBLE:
          obj = rs.getDouble(resultMeta.getColumnLabel(i));
          break;
        case java.sql.Types.FLOAT:
        case java.sql.Types.REAL:
          obj = rs.getFloat(resultMeta.getColumnLabel(i));
          break;
        case java.sql.Types.DATE:
          date = rs.getDate(resultMeta.getColumnLabel(i));
          if (date != null) {
            obj = DateUtils.format(date, DateUtils.SITOOLS_DATE_FORMAT);
          }
          else {
            obj = null;
          }
          break;
        case java.sql.Types.TIMESTAMP:
          date = rs.getTimestamp(resultMeta.getColumnLabel(i));
          if (date != null) {
            obj = DateUtils.format(date, DateUtils.SITOOLS_DATE_FORMAT);
          }
          else {
            obj = null;
          }
          break;
        case java.sql.Types.TIME:
          date = rs.getTime(resultMeta.getColumnLabel(i));
          if (date != null) {
            obj = DateUtils.format(date, DateUtils.SITOOLS_TIME_FORMAT);
          }
          else {
            obj = null;
          }
          break;
        default:
          obj = rs.getString(resultMeta.getColumnLabel(i));
          break;
      }
      if (obj != null && !rs.wasNull()) {
        record.getAttributeValues().add(new AttributeValue(resultMeta.getColumnLabel(i), obj.toString()));
      }
      else {
        record.getAttributeValues().add(new AttributeValue(resultMeta.getColumnLabel(i), null));
      }
    }
  }

  /**
   * Close the current JDBC request
   * 
   * @throws SitoolsException
   *           if there is an error while closing the JDBC request
   */
  @Override
  public void close() throws SitoolsException {
    try {
      if (rs != null) {
        rs.close();
      }
    }
    catch (SQLException e) {
      throw new SitoolsException("ERROR_CLOSING_CONNECTION", e);
    }
  }

  /**
   * If the count must be done or not
   * 
   * @return true if the count is done, false otherwise
   */
  @Override
  public final boolean isCountDone() {
    return params.isCountDone();
  }

  /**
   * Gets the maxResultsToSend value
   * 
   * @see AbstractDatabaseRequest.MAX_ROWS property in the sitools.properties file
   * @see Developer Guide
   * 
   * @return the maxResultsToSend
   */
  public final int getMaxResultsToSend() {
    return Integer.parseInt(SitoolsSettings.getInstance().getString("AbstractDatabaseRequest.MAX_ROWS"));
  }

  /**
   * Get the number of records in the current request when no limit parameter is set
   * 
   * @see AbstractDatabaseRequest.PAGE_SIZE property in the sitools.properties file
   * @see Developer Guide
   * 
   * @return the number of records in the current request when no limit parameter is set
   */
  public final int getPageSize() {
    return Integer.parseInt(SitoolsSettings.getInstance().getString("AbstractDatabaseRequest.PAGE_SIZE"));
  }

  @Override
  public final String getDistinctRequestAsString() {
    if (params.getColumnFromDistinctQuery() == null) {
      return null;
    }
    List<Column> columns = params.getDataset().getColumnModel();
    List<Predicat> predicatsLocal = params.getPredicats();
    // List<Structure> structuresLocal = params.getStructures();
    SitoolsStructure structure = params.getDataset().getStructure();

    RequestSql request = RequestFactory.getRequest(params.getDb().getDsModel().getDriverClass());
    String sql = "";
    if ("S".equals(params.getDataset().getQueryType())) {
      sql += " " + params.getDataset().getSqlQuery();
    }
    else {
      sql += " FROM " + request.getFromClauseAdvanced(structure);
      sql += " WHERE 1=1 " + request.getWhereClause(predicatsLocal, columns);
    }
    this.logger.log(Level.INFO, "SQL = " + sql);
    // ORDER BY parameter is the first primary key by default
    String orderBy = "";
    Multisort orders = params.getOrderBy();
    orderBy = request.getOrderBy(orders, columns);
    ArrayList<Column> columnsQuery = new ArrayList<Column>();
    columnsQuery.add(params.getColumnFromDistinctQuery());
    sql = "SELECT DISTINCT " + request.getAttributes(columnsQuery) + sql + orderBy;
    return sql;
  }

  @Override
  public final String getRequestAsString() throws SitoolsException {
    String sql;
    String distinct;

    List<Column> columns = params.getDataset().getColumnModel();
    List<Predicat> predicats = params.getPredicats();
    // List<Structure> structures = params.getStructures();
    SitoolsStructure structure = params.getDataset().getStructure();

    int startIndex = params.getStartIndex();

    RequestSql request = RequestFactory.getRequest(params.getDb().getDsModel().getDriverClass());
    sql = "";
    distinct = (params.getDataset().isDistinct() == true) ? "DISTINCT " : "";
    
    if ("S".equals(params.getDataset().getQueryType())) {
      sql += " " + params.getDataset().getSqlQuery() + request.getWhereClause(predicats, columns);
    }
    else {
      sql += " FROM " + request.getFromClauseAdvanced(structure);
      sql += " WHERE 1=1 " + request.getWhereClause(predicats, columns);
    }
    this.logger.log(Level.INFO, "SQL = " + sql);

    nbResultsToSend = params.getPaginationExtend();
    nbTotalResults = params.isCountDone() ? totalCount(sql) : -1;
    // Integer.parseInt(SitoolsSettings.getInstance().getString("AbstractDatabaseRequest.MAX_ROWS"));
    // Number of results to send
    if (nbResultsToSend >= 0) {
      this.logger.log(Level.INFO, "nbResultsToSend: " + nbResultsToSend);
      this.logger.log(Level.INFO, "nb tot: " + nbTotalResults);
      this.logger.log(Level.INFO, "startIndex: " + startIndex);
      if (nbTotalResults != -1) {
        nbResultsToSend = Math.min(nbTotalResults - startIndex, nbResultsToSend);
      }
      this.logger.log(Level.INFO, "nb res: " + nbResultsToSend);
      nbSendedResults = 0;
    }
    else if (nbResultsToSend == DatabaseRequestParameters.RETURN_FIRST_PAGE) {
      // Max
      nbResultsToSend = getPageSize(); // Integer.parseInt(SitoolsSettings.getInstance().getString("AbstractDatabaseRequest.MAX_ROWS"));
      nbResultsToSend = nbResultsToSend > nbTotalResults - startIndex ? nbTotalResults - startIndex : nbResultsToSend;
    }
    else if (nbResultsToSend == DatabaseRequestParameters.RETURN_ALL) {
      // Max
      nbResultsToSend = getMaxResultsToSend(); // ;
      nbResultsToSend = nbResultsToSend > nbTotalResults - startIndex ? nbTotalResults - startIndex : nbResultsToSend;
    }
    else {
      throw new RuntimeException("Number of result to send invalid");
    }
    // ORDER BY parameter is the first primary key by default
    Multisort orders = params.getOrderBy();
    String orderBy = request.getOrderBy(orders, columns);
    if ("".equals(orderBy) || orderBy == null) {
      orderBy = request.getOrderBy(params.getDataset());
    }

    // JCM
    if (sql.contains("ORDER BY")) {
      sql = "SELECT " + distinct + request.getAttributes(params.getSqlVisibleColumns()) + sql;
    }
    else {
      sql = "SELECT " + distinct + request.getAttributes(params.getSqlVisibleColumns()) + sql + orderBy;
    }

    return sql;
  }

  @Override
  public void checkRequest() throws SitoolsException {
    // does nothing
  }

}
