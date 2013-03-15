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
package fr.cnes.sitools.datasource.mongodb;

import java.util.ArrayList;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import com.mongodb.DB;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.datasource.common.SitoolsDataSourceModel;
import fr.cnes.sitools.datasource.mongodb.business.SitoolsMongoDBDataSource;
import fr.cnes.sitools.datasource.mongodb.business.SitoolsMongoDBDataSourceFactory;

/**
 * Current data source status (if enabled)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class DataSourceMonitoringResource extends AbstractDataSourceResource {

  @Override
  public void sitoolsDescribe() {
    setName("DataSourceMonitoringResource");
    setDescription("Resource for monitoring actived datasource");
  }

  @Override
  @Get
  public Representation get(Variant variant) {
    Response response = null;
    ArrayList<String> messages = new ArrayList<String>();
    messages.add("JDBC DataSource Monitoring");
    try {

      if (getDatasourceId() != null) {
        SitoolsMongoDBDataSource sds = SitoolsMongoDBDataSourceFactory.getDataSource(getDatasourceId());
        traceStatus(sds, messages);
      }
      else {
        ArrayList<SitoolsMongoDBDataSource> dataSources = SitoolsMongoDBDataSourceFactory.getAll();
        for (SitoolsMongoDBDataSource sitoolsDataSource : dataSources) {
          traceStatus(sitoolsDataSource, messages);

        }
      }
      response = new Response(true, messages, String.class, "statusInfo");
      return getRepresentation(response, variant);
    }

    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * Describe the GET method
   * 
   * @param info
   *          WADL method information
   * @param path
   *          url attachment of the resource
   */
  public void describeGet(MethodInfo info, String path) {
    if (path.endsWith("{datasourceId}")) {
      info.setDocumentation("GET " + path + " : Monitors one JDBC datasource.");
    }
    else {
      info.setDocumentation("GET " + path + " : Monitors all JDBC datasource(s).");
    }

    // info.setDocumentation("Method to monitor one/all JDBC datasource(s).");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("datasourceId", false, "xs:string", ParameterStyle.TEMPLATE,
        "datasourceId to retrieve a single datasource definition.");
    info.getRequest().getParameters().add(param);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Trace informations for a sitoolsDataSource
   * 
   * @param sitoolsDataSource
   *          SitoolsDataSource
   * @param messages
   *          ArrayList<String> messages
   */
  private void traceStatus(SitoolsMongoDBDataSource sitoolsDataSource, ArrayList<String> messages) {

    // Mongo mongo = sitoolsDataSource.getMongo();

    DB db = sitoolsDataSource.getDatabase();

    SitoolsDataSourceModel bds = sitoolsDataSource.getDsModel();

    messages.add("--------------------------------------------------");
    messages.add("Url: " + bds.getUrl());
    messages.add("User: " + bds.getUserLogin());
    messages.add("Database stats : " + db.getStats());
    // messages.add("NumActive: " + bds.getNumActive());
    // messages.add("MaxActive: " + bds.getMaxActive());
    // messages.add("MaxIdl: " + bds.getMaxIdle());
    // messages.add("MinIdl: " + bds.getMinIdle());
    // messages.add("NumIdle: " + bds.getNumIdle());

  }
}
