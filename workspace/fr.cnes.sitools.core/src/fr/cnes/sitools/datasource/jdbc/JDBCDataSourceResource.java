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
package fr.cnes.sitools.datasource.jdbc;

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
import fr.cnes.sitools.datasource.jdbc.model.JDBCDataSource;

/**
 * Resource for managing single JDBCDataSource (GET UPDATE DELETE)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class JDBCDataSourceResource extends AbstractDataSourceResource {

  @Override
  public void sitoolsDescribe() {
    setName("DataSourceResource");
    setDescription("Resource for managing an identified jdbc datasource");
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
        JDBCDataSource dataset = getStore().retrieve(getDatasourceId());
        Response response = new Response(true, dataset, JDBCDataSource.class, "jdbcdatasource");
        return getRepresentation(response, variant);
      }
      else {
        ResourceCollectionFilter filter = new ResourceCollectionFilter(getRequest());
        List<JDBCDataSource> datasources = getStore().getList(filter);
        int total = datasources.size();
        datasources = getStore().getPage(filter, datasources);
        Response response = new Response(true, datasources, JDBCDataSource.class, "jdbcdatasources");
        response.setTotal(total);
        return getRepresentation(response, variant);
      }
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
    JDBCDataSource dsOutput = null;
    try {
      JDBCDataSource dsInput = null;
      if (representation != null) {
        dsInput = getObject(representation);

        // Business service
        dsInput.setStatus("INACTIVE");
        dsOutput = getStore().update(dsInput);
      }

      if (dsOutput != null) {
        Response response = new Response(true, dsOutput, JDBCDataSource.class, "jdbcdatasource");
        return getRepresentation(response, variant);
      }
      else {
        Response response = new Response(false, "Can not validate datasource");
        return getRepresentation(response, variant);
      }

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
      try {
        JDBCDataSource datasourceOutput = getStore().retrieve(getDatasourceId());
        if (datasourceOutput != null) {
          getJDBCDataSourceAdministration().detachDataSourceDefinitif(datasourceOutput);
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }

      // Business service
      getStore().delete(getDatasourceId());

      Response response = new Response(true, "datasource.delete.success");
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
