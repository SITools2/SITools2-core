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
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.datasource.common.SitoolsDataSourceModel;
import fr.cnes.sitools.datasource.mongodb.model.MongoDBDataSource;

/**
 * Resource for managing single MongoDBDataSource (GET UPDATE DELETE)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class MongoDBDataSourceResource extends AbstractDataSourceResource {

  @Override
  public void sitoolsDescribe() {
    setName("DataSourceResource");
    setDescription("Resource for managing an identified mongodb datasource");
    setNegotiated(false);
  }

  /**
   * get all DataSources
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveDataSource(Variant variant) {
    try {
      // XStream xstream = XStreamFactory.getInstance().getXStreamWriter(variant.getMediaType(), false);

      if (getDatasourceId() != null) {
        SitoolsDataSourceModel datasource = getStore().retrieve(getDatasourceId());
        Response response;
        if (datasource != null) {
          trace(Level.FINE, "Edit MongoDB data source information for the data source " + datasource.getName());
          response = new Response(true, datasource, MongoDBDataSource.class, "mongodbdatasource");
        }
        else {
          trace(Level.INFO, "Cannot edit MongoDB data source information for the data source " + getDatasourceId());
          response = new Response(false, "cannot find mongodb datasource");
        }
        return getRepresentation(response, variant);
      }
      else {
        ResourceCollectionFilter filter = new ResourceCollectionFilter(getRequest());
        List<MongoDBDataSource> datasources = getStore().getList(filter);
        int total = datasources.size();
        datasources = getStore().getPage(filter, datasources);
        trace(Level.FINE, "View available MongoDB data sources");
        Response response = new Response(true, datasources, MongoDBDataSource.class, "mongodbdatasources");
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
   * GET description
   * 
   * @param info
   *          WADL method information
   */
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Get the definition of a datasource.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("datasourceId", true, "xs:string", ParameterStyle.TEMPLATE,
        "datasourceId to retrieve a single datasource definition.");
    info.getRequest().getParameters().add(param);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
    this.addStandardResourceCollectionFilterInfo(info);
  }

  /**
   * Update / Validate existing DataSource
   * 
   * @param representation
   *          the representation sent
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateDataSource(Representation representation, Variant variant) {
    SitoolsDataSourceModel dsOutput = null;
    try {
      MongoDBDataSource dsInput = null;
      if (representation != null) {
        dsInput = getObject(representation);

        // Business service
        dsInput.setStatus("INACTIVE");
        dsOutput = getStore().update(dsInput);
      }

      if (dsOutput != null) {
        trace(Level.INFO, "Update information for the MongoDB data source " + dsOutput.getName());
        Response response = new Response(true, dsOutput, MongoDBDataSource.class, "mongodbdatasource");
        return getRepresentation(response, variant);
      }
      else {
        trace(Level.INFO, "Update information for the MongoDB data source - id: " + getDatasourceId());
        Response response = new Response(false, "Can not validate datasource");
        return getRepresentation(response, variant);
      }

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Update information for the MongoDB data source - id: " + getDatasourceId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Update information for the MongoDB data source - id: " + getDatasourceId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * PUT description
   * 
   * @param info
   *          WADL method information
   */
  public void describePut(MethodInfo info) {
    info.setDocumentation("Modifies the definition of a datasource sending its new definition.");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo param = new ParameterInfo("datasourceId", true, "xs:string", ParameterStyle.TEMPLATE,
        "datasourceId to retrieve a single datasource definition.");
    info.getRequest().getParameters().add(param);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Delete data source
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteDataSource(Variant variant) {
    try {
      Response response = null;
      try {
        SitoolsDataSourceModel datasourceOutput = getStore().retrieve(getDatasourceId());
        if (datasourceOutput != null) {
          getMongoDBDataSourceAdministration().detachDataSourceDefinitif(datasourceOutput);
          // Business service
          getStore().delete(getDatasourceId());
          trace(Level.INFO, "Delete the MongoDB data source " + datasourceOutput.getName());
          response = new Response(true, "datasource.delete.success");
        }
        else {
          trace(Level.INFO, "Cannot delete the MongoDB data source - id: " + getDatasourceId());
          response = new Response(true, "datasource.delete.failure");
        }
      }
      catch (Exception e) {
        getLogger().log(Level.INFO, null, e);
      }

      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot delete the MongoDB data source - id: " + getDatasourceId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot delete the MongoDB data source - id: " + getDatasourceId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  /**
   * DELETE description
   * 
   * @param info
   *          WADL method information
   */
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Deletes a datasource by ID.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("datasourceId", true, "xs:string", ParameterStyle.TEMPLATE,
        "datasourceId to retrieve a single datasource definition.");
    info.getRequest().getParameters().add(param);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
