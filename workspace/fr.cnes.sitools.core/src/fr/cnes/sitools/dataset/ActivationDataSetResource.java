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
package fr.cnes.sitools.dataset;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import com.mongodb.DBCursor;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.database.common.RequestFactory;
import fr.cnes.sitools.dataset.database.jdbc.RequestSql;
import fr.cnes.sitools.dataset.database.mongodb.RequestMongoDB;
import fr.cnes.sitools.dataset.database.mongodb.RequestNoSQL;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.dataset.model.structure.SitoolsStructure;
import fr.cnes.sitools.datasource.common.SitoolsDataSource;
import fr.cnes.sitools.datasource.common.SitoolsDataSourceFactory;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSource;
import fr.cnes.sitools.datasource.jdbc.model.Structure;
import fr.cnes.sitools.datasource.mongodb.business.MongoDBRequestModel;
import fr.cnes.sitools.datasource.mongodb.business.SitoolsMongoDBDataSource;
import fr.cnes.sitools.notification.model.Notification;

/**
 * Class for management of specific actions on a DataSet
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class ActivationDataSetResource extends AbstractDataSetResource {

  @Override
  public void sitoolsDescribe() {
    setName("ActivationDataSetResource");
    setDescription("Resource to perform several actions on a dataset, in order to activating the linked DatasetApplication.");
    setNegotiated(false);
  }

  /**
   * Actions on PUT
   * 
   * @param representation
   *          could be null.
   * @param variant
   *          MediaType of response
   * @return Representation response
   */
  @Put
  public Representation action(Representation representation, Variant variant) {
    Response response = null;
    Representation rep = null;
    synchronized (store) {
      try {
        do {
          // on charge le dataset
          DataSet ds = store.retrieve(getDatasetId());
          if (ds == null) {
            trace(Level.INFO, "Cannot perform action on the dataset " + getDatasetId());
            response = new Response(false, "DATASET_NOT_FOUND");
            break;
          }

          if (this.getReference().toString().endsWith("start")) {
            response = this.startDataset(ds);
            break;
          }

          if (this.getReference().toString().endsWith("stop")) {
            response = this.stopDataset(ds);
            break;
          }

          if (this.getReference().toString().endsWith("getSqlString")) {
            try {
              response = new Response(true, getRequestString(ds));
              trace(Level.INFO, "View the request string of the dataset " + ds.getName());
            }
            catch (Exception e) {
              trace(Level.INFO, "Cannot view the request string of the dataset " + ds.getName());
              getLogger().log(Level.INFO, null, e);
              response = new Response(false, "dataset.stop.error");
            }
            break;
          }
          if (this.getReference().toString().endsWith("refreshNotion")) {
            try {
              if (ds.getDirty()) {
                store.update(ds);
                response = new Response(true, ds, DataSet.class, "dataset");
              }
              else {
                response = new Response(false, "dataset.not.dirty");
              }

            }
            catch (Exception e) {
              getLogger().log(Level.INFO, null, e);
              response = new Response(false, "dataset.stop.error");
            }
            break;
          }

        } while (false);

        // Response
        if (response == null) {
          response = new Response(false, "dataset.action.error");
        }
      }
      finally {
        rep = getRepresentation(response, variant);
      }
    }

    return rep;
  }

  /**
   * Start the dataset
   * 
   * @param ds
   *          the dataset
   * @return the response
   */
  private Response startDataset(DataSet ds) {
    Response response;
    do {
      if ("ACTIVE".equals(ds.getStatus())) {
        trace(Level.INFO, "Cannot start the dataset " + ds.getName());
        response = new Response(true, "dataset.update.blocked");
        break;
      }

      try {
        // get total results
        List<Column> columns = ds.getColumnModel();
        List<Predicat> predicats = ds.getPredicat();
        List<Structure> structures = ds.getStructures();

        try {
          boolean ok = testRequest(ds);
          if (!ok) {
            trace(Level.INFO, "Cannot start the dataset " + ds.getName());
            response = new Response(false, "Cannot start the dataset, please check the log for more details");
            break;
          }

          int nbTotalResults = getTotalResults(ds, structures, columns, predicats);
          ds.setNbRecords(nbTotalResults);
        }
        catch (Exception e) {
          trace(Level.INFO, "Cannot start the dataset " + ds.getName());
          getLogger().warning(e.getMessage());
          response = new Response(false, "Cannot start the dataset, please check the log for more details");
          break;
        }

        ds.setExpirationDate(new Date(new GregorianCalendar().getTime().getTime()));

        DataSet dsResult = store.update(ds);

        application.attachDataSet(dsResult);

        // get the modifications done by the starting of the application
        dsResult = store.retrieve(getDatasetId());

        response = new Response(true, dsResult, DataSet.class, "dataset");
        response.setMessage("dataset.update.success");

        // Notify observers
        Notification notification = new Notification();
        notification.setObservable(dsResult.getId());
        notification.setStatus(dsResult.getStatus());
        notification.setEvent("DATASET_STATUS_CHANGED");
        notification.setMessage("dataset.update.success");
        getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);
        trace(Level.INFO, "Start the dataset " + ds.getName());
      }
      catch (Exception e) {
        trace(Level.INFO, "Cannot start the dataset " + ds.getName());
        getLogger().log(Level.INFO, null, e);
        response = new Response(false, "dataset.update.error");
      }
    } while (false);
    return response;
  }

  /**
   * Stop the dataset
   * 
   * @param ds
   *          the {@link DataSet}
   * @return the {@link Response}
   */
  private Response stopDataset(DataSet ds) {
    Response response;
    do {
      if (!"ACTIVE".equals(ds.getStatus())) {

        // Par mesure de securite
        try {
          application.detachDataSet(ds);
        }
        catch (Exception e) {
          getLogger().log(Level.INFO, null, e);
        }
        trace(Level.INFO, "Cannot stop the dataset " + ds.getName());
        response = new Response(true, "dataset.stop.blocked");
        break;
      }

      try {
        application.detachDataSet(ds);
        DataSet dsResult = store.retrieve(getDatasetId());

        response = new Response(true, dsResult, DataSet.class, "dataset");
        response.setMessage("dataset.stop.success");

        // Notify observers
        Notification notification = new Notification();
        notification.setObservable(dsResult.getId());
        notification.setStatus(dsResult.getStatus());
        notification.setEvent("DATASET_STATUS_CHANGED");
        notification.setMessage("dataset.stop.success");
        getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);
        trace(Level.INFO, "Stop the dataset " + ds.getName());
      }
      catch (Exception e) {
        trace(Level.INFO, "Cannot stop the dataset " + ds.getName());
        getLogger().log(Level.INFO, null, e);
        response = new Response(false, "dataset.stop.error");
      }
    } while (false);
    return response;
  }

  @Override
  public void describePut(MethodInfo info, String path) {

    if (path.endsWith("start")) {
      info.setIdentifier("start");
      info.setDocumentation(" PUT /" + path
          + " : Performs a start action on the dataset making the related DatasetApplication ACTIVE.");
    }
    else if (path.endsWith("stop")) {
      info.setIdentifier("stop");
      info.setDocumentation(" PUT /" + path
          + " : Performs a stop action on the dataset making the related DatasetApplication INACTIVE.");
    }
    else if (path.endsWith("getSqlString")) {
      info.setIdentifier("getSqlString");
      info.setDocumentation(" PUT /" + path
          + " : Builds and returns the underlying datasource SQL request string for the dataset.");
    }
    else if (path.endsWith("refreshNotion")) {
      info.setIdentifier("refreshNotion");
      info.setDocumentation(" PUT /"
          + path
          + " : If dataset is dirty, updates the mapping of dataset columns with dictionary notions and sets dirty status to false. This action is performed automatically when activating the dataset (start action).");
    }

    addStandardGetRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("datasetId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the dataset");
    info.getRequest().getParameters().add(pic);
    addStandardSimpleResponseInfo(info);
  }

  /**
   * Retrieve the number total of results
   * 
   * @param ds
   *          the dataset
   * @param structures
   *          list of generic structures
   * @param columns
   *          database columns
   * @param predicats
   *          the where clause
   * @return totalResults the number of total result
   * @throws SitoolsException
   *           throws SitoolsException
   */
  protected int getTotalResults(DataSet ds, List<Structure> structures, List<Column> columns, List<Predicat> predicats)
      throws SitoolsException {

    int totalResults;

    SitoolsDataSource datasource = SitoolsDataSourceFactory.getDataSource(ds.getDatasource().getId());
    switch (datasource.getDataSourceType()) {
      case SQL:
        totalResults = getSQLTotalResults(ds, structures, columns, predicats);
        break;
      case MONGODB:
        totalResults = getMongoDBTotalResults(ds, structures, columns, predicats);
        break;
      default:
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Datasource not supported");

    }

    return totalResults;
  }

  /**
   * Execute a select count(*) request with the predicates filter.
   * 
   * @param ds
   *          DataSet
   * @param structures
   *          ArrayList<Structure>
   * @param columns
   *          ArrayList<Column>
   * @param predicats
   *          ArrayList<Predicat>
   * @return total count of records according to the predicates filter.
   * @throws SitoolsException
   *           throws SitoolsException
   * 
   */
  protected int getSQLTotalResults(DataSet ds, List<Structure> structures, List<Column> columns,
      List<Predicat> predicats) throws SitoolsException {

    ResultSet resultSet = null;
    try {
      SitoolsSQLDataSource datasource = (SitoolsSQLDataSource) SitoolsDataSourceFactory.getDataSource(ds
          .getDatasource().getId());
      RequestSql request = RequestFactory.getRequest(datasource.getDsModel().getDriverClass());
      if (structures.size() == 0) {
        return -1;
      }
      String sql = "SELECT count(1) ";

      if ("S".equals(ds.getQueryType())) {
        sql += " " + ds.getSqlQuery();
      }
      else {
        sql += " FROM " + request.getFromClauseAdvanced(ds.getStructure());
        sql += " WHERE 1=1 " + request.getWhereClause(predicats, columns);
      }

      int nbTotalResults = 0;
      resultSet = datasource.limitedQuery(sql, 1, 0);
      if (resultSet != null) {
        if (resultSet.next()) {
          nbTotalResults = resultSet.getInt(1);
        }
      }
      return nbTotalResults;
    }
    catch (SQLException ex) {
      throw new SitoolsException(ex.getMessage(), ex);
    }
    finally {
      if (resultSet != null) {
        try {
          resultSet.close();
        }
        catch (Exception e) {

          getLogger().log(Level.INFO, null, e);
        }
      }
    }
  }

  /**
   * Retrieve the number total of results for MongoDB database
   * 
   * @param ds
   *          the dataset
   * @param structures
   *          list of generic structures
   * @param columns
   *          database columns
   * @param predicats
   *          the where clause
   * @return totalResults the number of total result
   * @throws SitoolsException
   *           throws SitoolsException
   */
  private int getMongoDBTotalResults(DataSet ds, List<Structure> structures, List<Column> columns,
      List<Predicat> predicats) throws SitoolsException {

    try {
      SitoolsMongoDBDataSource datasource = (SitoolsMongoDBDataSource) SitoolsDataSourceFactory.getDataSource(ds
          .getDatasource().getId());

      MongoDBRequestModel reqMongo = getMongoDBRequest(ds, datasource);
      if (reqMongo == null) {
        return -1;
      }

      return datasource.countQuery(reqMongo);
    }
    catch (Exception e) {
      throw new SitoolsException(e.getMessage(), e);
    }
  }

  /**
   * Gets a SQL String for validating the DataSet definition
   * 
   * @param ds
   *          DataSet
   * @return String
   * @throws SitoolsException
   *           throws SitoolsException
   */
  protected boolean testRequest(DataSet ds) throws SitoolsException {

    boolean ok;

    SitoolsDataSource datasource = SitoolsDataSourceFactory.getDataSource(ds.getDatasource().getId());
    switch (datasource.getDataSourceType()) {
      case SQL:
        ok = testSqlRequest(ds, datasource);
        break;
      case MONGODB:
        ok = testMongoDBRequest(ds, datasource);
        break;
      default:
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Datasource not supported");

    }

    return ok;
  }

  /**
   * Retrieve the String request
   * 
   * @param ds
   *          the dataset
   * @return the STRING request
   */
  private String getRequestString(DataSet ds) {
    String request = "";
    SitoolsDataSource datasource = SitoolsDataSourceFactory.getDataSource(ds.getDatasource().getId());
    switch (datasource.getDataSourceType()) {
      case SQL:
        request = getSqlRequest(ds, datasource);
        break;
      case MONGODB:
        request = getMongoDBRequest(ds, datasource).toStringRequest();
        break;
      default:
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Datasource not supported");

    }
    return request;
  }

  /**
   * Retrieve the SQL String request
   * 
   * @param ds
   *          the dataset
   * @param datasource
   *          the SitoolsDatasource
   * @return the STRING request
   */
  private String getSqlRequest(DataSet ds, SitoolsDataSource datasource) {
    List<Column> columns = ds.getColumnModel();
    List<Predicat> predicats = ds.getPredicat();
    List<Structure> structures = ds.getStructures();
    SitoolsStructure structure = ds.getStructure();
    String distinct = (ds.isDistinct()) ? "DISTINCT " : "";
    RequestSql request = RequestFactory.getRequest(datasource.getDsModel().getDriverClass());
    if (structures.size() == 0) {
      return null;
    }
    String sql = "SELECT " + distinct + request.getAttributes(ds.getDefaultColumnVisible());
    if ("S".equals(ds.getQueryType())) {
      sql += " " + ds.getSqlQuery();
    }
    else {
      sql += " FROM " + request.getFromClauseAdvanced(structure);
      sql += " WHERE 1=1 " + request.getWhereClause(predicats, columns);
    }
    // ORDER BY parameter is the first primary key by default
    sql += request.getOrderBy(ds);
    return sql;
  }

  /**
   * Retrieve the mongoDB request model
   * 
   * @param ds
   *          the dataset
   * @param datasource
   *          the SitoolsDatasource
   * @return MongoDBRequestModel
   */
  private MongoDBRequestModel getMongoDBRequest(DataSet ds, SitoolsDataSource datasource) {
    List<Column> columns = ds.getColumnModel();
    List<Predicat> predicats = ds.getPredicat();
    List<Structure> structures = ds.getStructures();

    RequestNoSQL request = new RequestMongoDB();
    if (structures.size() == 0) {
      return null;
    }
    String collectionName = structures.get(0).getName();

    MongoDBRequestModel mongoRequest = new MongoDBRequestModel();
    mongoRequest.setCollectionName(collectionName);

    mongoRequest.setStart(0);

    String jsonFilter = request.getFilterClause(predicats, columns);
    mongoRequest.setFilterString(jsonFilter);
    mongoRequest.setLimit(0);

    String jsonKeys = request.getAttributes(columns);
    mongoRequest.setKeysString(jsonKeys);

    return mongoRequest;
  }

  /**
   * Return true if nbRecords equals 1, false otherwise
   * 
   * @param ds
   *          the dataset
   * @param datasource
   *          the datasource
   * @return boolean
   * @throws SitoolsException
   *           throws SitoolsException
   */
  private boolean testSqlRequest(DataSet ds, SitoolsDataSource datasource) throws SitoolsException {
    String sql = getSqlRequest(ds, datasource);
    int nbRecords;
    try {
      nbRecords = executeSQLRequest((SitoolsSQLDataSource) datasource, sql);
    }
    catch (SQLException e) {
      throw new SitoolsException(e.getLocalizedMessage(), e);
    }
    return (nbRecords == 1);
  }

  /**
   * Return true if nbRecords equals 1, false otherwise
   * 
   * @param ds
   *          the dataset
   * @param datasource
   *          the datasource
   * @return boolean
   */
  private boolean testMongoDBRequest(DataSet ds, SitoolsDataSource datasource) {
    MongoDBRequestModel request = getMongoDBRequest(ds, datasource);
    int nbRecords;
    nbRecords = executeMongoDBRequest((SitoolsMongoDBDataSource) datasource, request);
    return (nbRecords == 1);
  }

  /**
   * test the SQL request generated by the DataSet
   * 
   * @param datasource
   *          the datasource
   * @param sql
   *          the sql request
   * @return 1 if request is correct else -1
   * @throws SQLException
   *           if error when executing the query
   */
  protected int executeSQLRequest(SitoolsSQLDataSource datasource, String sql) throws SQLException {
    ResultSet resultSet = null;
    try {
      getLogger().log(Level.INFO, "TEST SQL : " + sql);
      resultSet = datasource.limitedQuery(sql, 0, 0);
      if (resultSet != null) {
        return 1;
      }
      return -1;
    }
    finally {
      if (resultSet != null) {
        try {
          resultSet.close();
        }
        catch (Exception e) {

          getLogger().log(Level.INFO, null, e);
        }
      }
    }
  }

  /**
   * Return 1 if the cursor != null, -1 otherwise
   * 
   * @param datasource
   *          the datasource
   * @param request
   *          the mongoDB request model
   * @return int
   */
  private int executeMongoDBRequest(SitoolsMongoDBDataSource datasource, MongoDBRequestModel request) {
    getLogger().log(Level.INFO, "TEST MONGODB : " + request.toStringRequest());
    DBCursor cursor = datasource.limitedQuery(request);
    if (cursor != null) {
      return 1;
    }
    else {
      return -1;
    }

  }

  /**
   * Get the list of primary key in the dataset
   * 
   * @param ds
   *          the dataset to search in
   * @return primary keys
   */
  public List<String> getPrimaryKeys(DataSet ds) {
    List<String> pks = new ArrayList<String>();
    List<Column> columns = ds.getColumnModel();
    for (Column column : columns) {
      if (column.isPrimaryKey()) {
        pks.add(column.getColumnAlias());
      }
    }
    return pks;
  }

}
