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
package fr.cnes.sitools.dataset.database.mongodb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Context;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.Multisort;
import fr.cnes.sitools.dataset.model.Operator;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.datasource.mongodb.business.MongoDBRequestModel;
import fr.cnes.sitools.datasource.mongodb.business.SitoolsMongoDBDataSource;
import fr.cnes.sitools.util.DateUtils;

/**
 * Create a request NOSQL
 * 
 * @author m.gond (Akka Technologies
 * */
public class MongoDBDatabaseRequest implements DatabaseRequest {
  /** the request parameters */
  protected DatabaseRequestParameters params;

  /** The result set */
  protected DBCursor rs = null;

  /** The result if the query is distinct */
  protected List<Object> listDistinct = null;

  /** The primaryKeys */
  protected List<String> primaryKeys;

  /** The nbSendedResults */
  protected int nbSendedResults = 0;

  /** The current Object */
  protected BasicDBObject currentObject = null;

  /** The nbResultsToSend */
  protected int nbResultsToSend = 0;

  /** Resource logger Context / Application ? ok */
  private Logger logger = Context.getCurrentLogger();

  /** The nbTotalResults */
  private int nbTotalResults = 0;

  /** The datasource */
  private SitoolsMongoDBDataSource datasource = null;

  /** The maxResultsToSend */
  private int maxResultsToSend = 500;

  /**
   * Create a {@code SQLDatabaseRequest}
   * 
   * @param params
   *          the request parameters
   */
  public MongoDBDatabaseRequest(DatabaseRequestParameters params) {
    this.params = params;
    primaryKeys = this.getPrimaryKeys();
    nbResultsToSend = params.getPaginationExtend();
    maxResultsToSend = params.getMaxrows();
    datasource = (SitoolsMongoDBDataSource) params.getDb();
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
    throw new SitoolsException("Unsuported request, use MongoDBDistinctRequest");
    // try {
    // String sql = this.getDistinctRequestAsString();
    // if (sql == null) {
    // return;
    // }
    // this.logger.log(Level.INFO, "SQL = " + sql);
    // if (nbResultsToSend == DatabaseRequestParameters.RETURN_ALL) {
    // nbResultsToSend = getMaxResultsToSend();
    // }
    // else if (nbResultsToSend == DatabaseRequestParameters.RETURN_FIRST_PAGE) {
    // nbResultsToSend = getPageSize();
    // }
    // rs = params.getDb().basicQuery(sql, nbResultsToSend, nbResultsToSend);
    // int startIndex = params.getStartIndex();
    // if (rs != null) {
    // // Moves the cursor
    // rs.beforeFirst();
    // int count = 0;
    // while (rs.next()) {
    // count++;
    // }
    // nbTotalResults = count;
    // nbResultsToSend = getMaxResultsToSend(); //
    // Integer.parseInt(SitoolsSettings.getInstance().getString("AbstractDatabaseRequest.MAX_ROWS"));
    // nbResultsToSend = nbResultsToSend > nbTotalResults - startIndex ? nbTotalResults - startIndex : nbResultsToSend;
    // rs.beforeFirst();
    // }
    // else {
    // throw new ResourceException(Status.SERVER_ERROR_SERVICE_UNAVAILABLE, "dataset.records.createSQLRequest");
    // }
    // }
    // catch (NumberFormatException e) {
    // throw new SitoolsException("ERROR " + e.getMessage(), e);
    // }
    // catch (SQLException e) {
    // throw new SitoolsException("ERROR " + e.getMessage(), e);
    // }
  }

  @Override
  public void createRequest() throws SitoolsException {

    try {

      MongoDBRequestModel mongoRequest = createMongoDBRequestModel();
      this.logger.log(Level.INFO, "COLLECTION = " + mongoRequest.getCollectionName());
      this.logger.log(Level.INFO, "JSON = " + mongoRequest.getFilterString());
      this.logger.log(Level.INFO, "KEYS : " + mongoRequest.getKeysString());
      this.logger.log(Level.INFO, "SORT :" + mongoRequest.getSortString());
      this.logger.log(Level.INFO, "START :" + mongoRequest.getStart() + " LIMIT " + mongoRequest.getLimit());

      rs = datasource.limitedQuery(mongoRequest);
      if (rs == null) {
        throw new SitoolsException("Error while querying datasource");
      }

    }
    catch (NumberFormatException e) {
      throw new SitoolsException("ERROR " + e.getMessage(), e);
    }

  }

  /**
   * Create a MongoDBRequestModel Object
   * 
   * @return MongoDBRequestModel
   * @throws SitoolsException
   *           throws SitoolsException
   */
  public MongoDBRequestModel createMongoDBRequestModel() throws SitoolsException {
    String collectionName = params.getStructures().get(0).getName();

    MongoDBRequestModel mongoRequest = new MongoDBRequestModel();
    mongoRequest.setCollectionName(collectionName);

    mongoRequest.setStart(params.getStartIndex());

    String jsonFilter = getRequestAsString();
    mongoRequest.setFilterString(jsonFilter);
    mongoRequest.setLimit(nbResultsToSend);

    String jsonKeys = getKeysAsString();
    mongoRequest.setKeysString(jsonKeys);

    String jsonOrderBy = getSortAsString();
    mongoRequest.setSortString(jsonOrderBy);

    return mongoRequest;
  }

  @Override
  public int calculateTotalCountFromBase() throws SitoolsException {

    String collectionName = params.getStructures().get(0).getName();
    String jsonFilter = getRequestAsString();

    this.logger.log(Level.INFO, "JSON COUNT = " + jsonFilter);

    return totalCount(jsonFilter, collectionName);

  }

  /**
   * Compute the number of Records in the current request
   * 
   * @param json
   *          the filter json string of the request
   * @param collectionName
   *          the name of the collection
   * @return the number of total results
   * @throws SitoolsException
   *           if a database access error occurs or this method is called on a closed result set
   */
  private int totalCount(String json, String collectionName) throws SitoolsException {
    return datasource.countQuery(json, collectionName);
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

    if (rs == null) {
      return false;
    }
    try {
      if (rs.hasNext() && nbSendedResults++ < nbResultsToSend) {
        currentObject = (BasicDBObject) rs.next();
        return true;
      }
      return false;
    }
    catch (MongoException e) {
      throw new SitoolsException(e.getMessage(), e);
    }

  }

  @Override
  @Deprecated
  public boolean isLastResult() throws SitoolsException {

    return rs.hasNext() || nbSendedResults >= nbResultsToSend;

  }

  @Override
  public Record getRecord() throws SitoolsException {
    Record record = new Record(buildURI());
    setAttributeValues(record, currentObject);
    return record;

  }

  @Override
  public final String buildURI() throws SitoolsException {

    List<String> selectedPrimaryKey = getSelectedPrimaryKey();
    if ((selectedPrimaryKey == null) || (selectedPrimaryKey.size() <= 0)) {
      // logger.warning("selectedPrimaryKey null or empty !");
      return null;
    }

    String uri = params.getBaseRef() + "/";
    for (String it : selectedPrimaryKey) {
      uri += currentObject.getString(it) + ";";
    }
    return (uri.equals("")) ? null : uri.substring(0, uri.lastIndexOf(";"));

  }

  /**
   * Fill in attributes for a given Record
   * 
   * @param record
   *          the <code>Record</code>
   * @param dbObject
   *          the dbObject containing the attributeValue
   */
  public final void setAttributeValues(Record record, BasicDBObject dbObject) {
    List<Column> columns = params.getSqlVisibleColumns();
    for (Column column : columns) {
      String dataIndex = column.getDataIndex();
      Object value = getValue(dbObject, dataIndex);
      // Object obj = dbObject.get(key);
      record.getAttributeValues().add(new AttributeValue(column.getColumnAlias(), value));
    }
  }

  /**
   * Get the value corresponding to dataIndex in dbObject. Even if the dataIndex contains . inside, it will go inside
   * the {@link DBObject}
   * 
   * 
   * @param dbObject
   *          the {@link DBObject}
   * @param dataIndex
   *          the dataIndex to find
   * @return the value
   */
  private Object getValue(DBObject dbObject, String dataIndex) {
    int index = dataIndex.indexOf(".");
    String key = dataIndex;
    boolean returnValue = true;
    if (index != -1) {
      key = dataIndex.substring(0, index);
      returnValue = false;
    }

    Object value = dbObject.get(key);

    if (value == null) {
      return null;
    }
    else if (returnValue) {
      value = getFormatedValue(value);
      return value;
    }
    else {
      if (value instanceof DBObject) {
        DBObject dbValue = (DBObject) value;
        return getValue(dbValue, dataIndex.substring(index + 1));
      }
      else {
        return null;
      }
    }
  }

  /**
   * Format the given Object with the Sitools standards
   * 
   * @param value
   *          the value to format
   * @return a formated object
   */
  protected Object getFormatedValue(Object value) {
    if (value != null) {
      if (value instanceof Date) {
        value = DateUtils.format((Date) value, DateUtils.SITOOLS_DATE_FORMAT);
      }
    }
    return value;
  }

  /**
   * Close the current MongoDB Cursor
   * 
   * @throws SitoolsException
   *           if there is an error while closing the MongoDB cursor
   */
  @Override
  public void close() throws SitoolsException {

    if (rs != null) {
      rs.close();
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

  /**
   * Sets the value of maxResultsToSend
   * 
   * @param maxResultsToSend
   *          the maxResultsToSend to set
   */
  public final void setMaxResultsToSend(int maxResultsToSend) {
    this.maxResultsToSend = maxResultsToSend;
  }

  @Override
  public final String getDistinctRequestAsString() throws SitoolsException {
    String json = "";
    if (params.getColumnFromDistinctQuery() == null) {
      return null;
    }
    // List<Column> columns = params.getDataset().getColumnModel();
    List<Column> columns = params.getSqlVisibleColumns();
    List<Predicat> predicatsLocal = params.getPredicats();

    // TODO voir comment mettre RequestMongoDB dans une factory avec les autres RequestSQL
    RequestMongoDB request = new RequestMongoDB();

    json = request.getFilterClause(predicatsLocal, columns);

    return json;
  }

  @Override
  public final String getRequestAsString() throws SitoolsException {
    String json;

    // TODO check collectionName
    String collectionName = params.getStructures().get(0).getName();

    List<Column> columns = params.getDataset().getColumnModel();
    List<Predicat> predicats = params.getPredicats();

    int startIndex = params.getStartIndex();

    // TODO voir comment mettre RequestMongoDB dans une factory avec les autres RequestSQL
    RequestMongoDB request = new RequestMongoDB();

    json = request.getFilterClause(predicats, columns);

    nbResultsToSend = params.getPaginationExtend();
    nbTotalResults = params.isCountDone() ? totalCount(json, collectionName) : -1;
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
      nbResultsToSend = getPageSize(); //
      Integer.parseInt(SitoolsSettings.getInstance().getString("AbstractDatabaseRequest.MAX_ROWS"));
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
    // request.addOrderBy(params.getDataset());

    // JCM
    return json;
  }

  /**
   * Get the list of keys (fields) as a String
   * 
   * @return the list of keys as a String
   * 
   */
  public final String getKeysAsString() {
    RequestMongoDB request = new RequestMongoDB();
    // List<Column> columns = params.getDataset().getColumnModel();
    List<Column> columns = params.getSqlVisibleColumns();
    return request.getAttributes(columns);
  }

  /**
   * Get the Sorting order as a String
   * 
   * @return the sorting order as a String
   */
  private String getSortAsString() {
    RequestMongoDB request = new RequestMongoDB();
    // ORDER BY parameter is the first primary key by default
    String orderBy = "";
    Multisort multisort = params.getOrderBy();
    if (multisort == null || multisort.getOrdersList() == null || multisort.getOrdersList().length == 0) {
      orderBy = request.getOrderBy(params.getDataset());
    }
    else {
      orderBy = request.getOrderBy(params.getOrderBy());
    }
    return orderBy;
  }

  @Override
  public void checkRequest() throws SitoolsException {
    List<Predicat> predicats = params.getPredicats();
    Map<String, List<Predicat>> orderedPredicat = orderPredicat(predicats);
    for (Entry<String, List<Predicat>> entry : orderedPredicat.entrySet()) {
      List<Predicat> preds = entry.getValue();
      List<Operator> operators = new ArrayList<Operator>();
      for (Predicat predicat : preds) {
        if (operators.contains(predicat.getCompareOperator())) {
          throw new SitoolsException("An operator cannot exist more than once when filtering on a column");
        }
        if (predicat.getCompareOperator().equals(Operator.EQ) && preds.size() > 1) {
          throw new SitoolsException(
              "Equal operator have to be alone when filtering on a column, other operator are not supported in that case");
        }
        else {
          operators.add(predicat.getCompareOperator());
        }
      }
    }
  }

  /**
   * Order the list of {@link Predicat} by Column dataIndex.
   * 
   * @param predicats
   *          the list of {@link Predicat}
   * @return a {@link Map} with the column dataIndex as Key and a List of {@link Predicat} as value
   */
  private Map<String, List<Predicat>> orderPredicat(List<Predicat> predicats) {

    Map<String, List<Predicat>> orderedPredicat = new HashMap<String, List<Predicat>>();

    for (Predicat predicat : predicats) {
      String key = predicat.getLeftAttribute().getDataIndex();
      List<Predicat> list = orderedPredicat.get(key);
      if (list == null) {
        list = new ArrayList<Predicat>();
        orderedPredicat.put(key, list);
      }
      list.add(predicat);
    }

    return orderedPredicat;
  }

}
