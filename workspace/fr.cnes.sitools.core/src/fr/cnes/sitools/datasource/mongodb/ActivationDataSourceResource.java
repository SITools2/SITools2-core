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
package fr.cnes.sitools.datasource.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;

import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.Mongo;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.datasource.common.SitoolsDataSourceModel;
import fr.cnes.sitools.datasource.mongodb.model.MongoDBDataSource;

/**
 * Actions on DataSource : testConnection activation / disable of the associated Application(s)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class ActivationDataSourceResource extends AbstractDataSourceResource {

  @Override
  public void sitoolsDescribe() {
    setName("ActivationDataSourceResource");
    setDescription("Resource to perform several actions on a datasource.");
  }

  /**
   * PUT to activate DataSource
   * 
   * @param representation
   *          the representation sent
   * @param variant
   *          the variant sent
   * @return Representation
   */
  @Put
  public Representation action(Representation representation, Variant variant) {
    Response response = null;

    do {
      // on accepte de faire un test avec la representation fournie
      // même si la datasource id n'existe pas.
      if (this.getReference().toString().endsWith("test")) {
        if (representation != null) {
          MongoDBDataSource dsInput = getObject(representation);
          List<String> trace = new ArrayList<String>();
          boolean result = testDataSourceConnection(dsInput, trace);
          trace(Level.INFO, "Test the connection for the MongoDB data source " + dsInput.getName());
          response = new Response(result, trace, String.class, "logs");
          return getRepresentation(response, variant);
        }
      }

      // on charge la datasource
      MongoDBDataSource ds = getStore().retrieve(getDatasourceId());
      if (ds == null) {
        response = new Response(false, "DATASOURCE_NOT_FOUND");
        trace(Level.INFO, "Cannot perform action on the MongoDB data source - id: " + getDatasourceId());
        break;
      }
      if (this.getReference().toString().endsWith("test")) {
        List<String> trace = new ArrayList<String>();
        boolean result = testDataSourceConnection(ds, trace);
        response = new Response(result, trace, String.class, "logs");
        trace(Level.INFO, "Test the connection for the MongoDB data source " + ds.getName());
        return getRepresentation(response, variant);
      }

      if (this.getReference().toString().endsWith("start")) {
        if ("ACTIVE".equals(ds.getStatus())) {
          SitoolsDataSourceModel dsResult = getStore().update(ds);
          response = new Response(true, dsResult, MongoDBDataSource.class, "mongodbdatasource");
          response.setMessage("datasource.update.blocked");
          trace(Level.INFO, "Cannot start the MongoDB data source " + ds.getName());
          break;
        }

        try {
          if (!testDataSourceConnection(ds, null)) {
            response = new Response(false, ds, MongoDBDataSource.class, "mongodbdatasource");
            response.setMessage("datasource.connection.error");
            trace(Level.INFO, "Cannot start the MongoDB data source " + ds.getName());
            break;
          }

          getMongoDBDataSourceAdministration().attachDataSource(ds);
          ds.setStatus("ACTIVE");
          SitoolsDataSourceModel dsResult = getStore().update(ds);
          response = new Response(true, dsResult, MongoDBDataSource.class, "mongodbdatasource"); // TODO API ajouter
                                                                                                 // ,"datasource"
          response.setMessage("datasource.update.success");
          trace(Level.INFO, "Start the MongoDB data source " + ds.getName());
        }
        catch (Exception e) {
          response = new Response(false, "datasource.update.error");
          trace(Level.INFO, "Cannot start the MongoDB data source " + ds.getName());
        }
        break;
      }

      if (this.getReference().toString().endsWith("stop")) {
        if (!"ACTIVE".equals(ds.getStatus())) {
          SitoolsDataSourceModel dsResult = getStore().update(ds);
          response = new Response(true, dsResult, MongoDBDataSource.class, "mongodbdatasource");
          response.setMessage("datasource.stop.blocked");
          trace(Level.INFO, "Cannot stop the MongoDB data source " + ds.getName());
          break;
        }

        try {
          getMongoDBDataSourceAdministration().detachDataSource(ds);
          ds.setStatus("INACTIVE");
          SitoolsDataSourceModel dsResult = getStore().update(ds);
          response = new Response(true, dsResult, MongoDBDataSource.class, "mongodbdatasource");
          response.setMessage("datasource.stop.success");
          trace(Level.INFO, "Stop the MongoDB data source " + ds.getName());
        }
        catch (Exception e) {
          response = new Response(false, "datasource.stop.error");
          trace(Level.INFO, "Cannot stop the MongoDB data source " + ds.getName());
        }
        break;
      }

    } while (false);

    // Response
    return getRepresentation(response, variant);
  }

  /**
   * Describe the GET method
   * 
   * @param info
   *          WADL method information
   * @param path
   *          url attachment of the resource
   */
  public void describePut(MethodInfo info, String path) {
    if (path.endsWith("start")) {
      info.setDocumentation(" PUT /"
          + path
          + " : starts a MongoDB datasource, making available a connection pool and making a linked DBExplorerApplication ACTIVE.");
    }
    else if (path.endsWith("stop")) {
      info.setDocumentation(" PUT /"
          + path
          + " : stops a MongoDB datasource, making unavailable a connection pool and making a linked DBExplorerApplication INACTIVE.");
    }
    else if (path.endsWith("test")) {
      info.setDocumentation(" PUT /" + path
          + " : checks a datasource by executing an SQL request on a MongoDB connection.");
    }

    // info.setDocumentation("Method to test/start/stop one/all MongoDB datasource(s).");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo param = new ParameterInfo("datasourceId", false, "xs:string", ParameterStyle.TEMPLATE,
        "datasourceId to retrieve a single datasource definition.");
    info.getRequest().getParameters().add(param);
    this.addStandardResponseInfo(info);
  }

  /**
   * Testing connections
   * 
   * @param ds
   *          the DataSource to test
   * @param trace
   *          a {@link List} of String to store the trace of the test. Can be null
   * @return Representation results of the connection test.
   */
  public boolean testDataSourceConnection(MongoDBDataSource ds, List<String> trace) {
    Boolean result = Boolean.TRUE;

    Mongo mongo = null;

    try {
      do {
        trace(trace, "Test mongo data source connection ...");
        try {
          mongo = new Mongo(ds.getUrl(), ds.getPortNumber());
        }
        catch (Exception e) {
          result = false;
          getMongoDBDataSourceAdministration().getLogger().info(e.getMessage());
          trace(trace, "Load driver class failed. Cause: " + e.getMessage());
          break;
        }

        try {
          // check if the database exists
          // List<String> databases = mongo.getDatabaseNames();
          // if (!databases.contains(ds.getDatabaseName())) {
          // result = false;
          // getMongoDBDataSourceAdministration().getLogger().info("Database does not exist");
          // trace.add("Database " + ds.getDatabaseName() + " does not exist");
          // break;
          // }

          DB db = mongo.getDB(ds.getDatabaseName());
          // if no user and password given lets authenticate on the database
          if (ds.isAuthentication() && !db.isAuthenticated()
              && !db.authenticate(ds.getUserLogin(), ds.getUserPassword().toCharArray())) {
            result = false;
            getMongoDBDataSourceAdministration().getLogger().info("Authentication failed");
            trace(trace, "Authentication failed");
            break;
          }

          // try to get the stats of the database to check whether or not the database is accessible
          CommandResult cmd = db.getStats();
          if (!cmd.ok()) {
            result = false;
            getMongoDBDataSourceAdministration().getLogger().info("Error connecting to the database " + cmd);
            trace(trace, "Error connecting to the database " + cmd);
            break;
          }
          trace(trace, "Get connection to the database : OK");
        }
        catch (Exception e) {
          result = false;
          getMongoDBDataSourceAdministration().getLogger().info(e.getMessage());
          trace(trace, "Get connection to the database failed. Cause: " + e.getMessage());
          break;
        }
      } while (false);

    }
    finally {
      if (mongo != null) {
        mongo.close();
      }

    }

    return result;
  }

  /**
   * Utility method to trace msg to a given List of String
   * 
   * @param trace
   *          the List of String
   * @param msgToTrace
   *          the message to trace
   */
  private void trace(List<String> trace, String msgToTrace) {
    if (trace != null) {
      trace.add(msgToTrace);
    }

  }

}
