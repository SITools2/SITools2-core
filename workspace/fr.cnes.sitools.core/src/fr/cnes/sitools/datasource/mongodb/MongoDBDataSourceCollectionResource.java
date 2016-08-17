/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.datasource.common.SitoolsDataSourceModel;
import fr.cnes.sitools.datasource.mongodb.model.MongoDBDataSource;

/**
 * Class for MongoDB data source collection management
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class MongoDBDataSourceCollectionResource extends AbstractDataSourceResource {

  @Override
  public void sitoolsDescribe() {
    setName("MongoDBDataSourceCollectionResource");
    setDescription("Resource for managing jdbc datasource collection");
    this.setNegotiated(false);

  }

  /**
   * Create a new DataSource
   * 
   * @param representation
   *          input
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newDataSource(Representation representation, Variant variant) {
    if (representation == null) {
      trace(Level.INFO, "Cannot create a MongoDB data source");
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "DATASOURCE_REPRESENTATION_REQUIRED");
    }
    try {
      MongoDBDataSource datasourceInput = getObject(representation);

      datasourceInput.setStatus("NEW");

      if ((datasourceInput.getSitoolsAttachementForUsers() == null)
          || datasourceInput.getSitoolsAttachementForUsers().equals("")) {
        datasourceInput.setSitoolsAttachementForUsers("/datasourcesmongodb/" + datasourceInput.getName());
      }

      // Business service
      SitoolsDataSourceModel datasourceOutput = getStore().create(datasourceInput);

      trace(Level.INFO, "Create a MongoDB data source " + datasourceOutput.getName());
      // Response
      Response response = new Response(true, datasourceOutput, MongoDBDataSource.class, "mongodbdatasource");
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot create a MongoDB data source");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot create a MongoDB data source");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * Describe the POST method
   * 
   * @param info
   *          WADL method information
   */
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a new MongoDB datasource.");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * get all DataSets
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveDataSource(Variant variant) {
    try {
      if (getDatasourceId() != null) {
        SitoolsDataSourceModel datasource = getStore().retrieve(getDatasourceId());
        Response response;
        if (datasource != null) {
          trace(Level.FINE, "Edit MongoDB data source information for the data source " + datasource.getName());
          response = new Response(true, datasource, MongoDBDataSource.class, "jdbcdatasource");
        }
        else {
          trace(Level.INFO, "Cannot edit MongoDB data source information for the data source " + getDatasourceId());
          response = new Response(false, "cannot find mongodb datasource");
        }
        return getRepresentation(response, variant);
      }
      else {
        ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
        List<MongoDBDataSource> datasources = getStore().getList(filter);
        int total = datasources.size();
        datasources = getStore().getPage(filter, datasources);
        trace(Level.FINE, "View available MongoDB data sources");
        Response response = new Response(true, datasources, MongoDBDataSource.class, "jdbcdatasource");
        response.setTotal(total);
        return getRepresentation(response, variant);
      }
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot view available MongoDB data sources");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot view available MongoDB data sources");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * Describe the GET method
   * 
   * @param info
   *          WADL method information
   */
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to list all MongoDB datasources.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("datasourceId", false, "xs:string", ParameterStyle.TEMPLATE,
        "datasourceId to retrieve a single datasource definition.");
    info.getRequest().getParameters().add(param);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
    this.addStandardResourceCollectionFilterInfo(info);
  }

}
