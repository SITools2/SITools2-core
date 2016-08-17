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
package fr.cnes.sitools.dataset.view;

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
import fr.cnes.sitools.dataset.view.model.DatasetView;

/**
 * Class Resource for managing single DatasetView (GET UPDATE DELETE)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class DatasetViewResource extends AbstractDatasetViewResource {
  
  @Override
  public void sitoolsDescribe() {
    setName("DatasetViewResource");
    setDescription("Resource for managing an identified datasetView");
    setNegotiated(false);
  }

  /**
   * get all datasetViews
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveDatasetView(Variant variant) {
    // XStream xstream = XStreamFactory.getInstance().getXStreamWriter(variant.getMediaType(), false);
    if (getDatasetViewId() != null) {
      DatasetView datasetView = getStore().retrieve(getDatasetViewId());
      Response response = new Response(true, datasetView, DatasetView.class, "datasetView");
      return getRepresentation(response, variant);
    }
    else {
      ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
      List<DatasetView> datasetViews = getStore().getList(filter);
      int total = datasetViews.size();
      datasetViews = getStore().getPage(filter, datasetViews);
      Response response = new Response(true, datasetViews, DatasetView.class, "datasetViews");
      response.setTotal(total);
      return getRepresentation(response, variant);
    }
  }
  
  @Override
  public final void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve a single DatasetView by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("datasetViewId", true, "class", ParameterStyle.TEMPLATE, "Form component identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
    addStandardResourceCollectionFilterInfo(info);
  }

  /**
   * Update / Validate existing datasetView
   * 
   * @param representation
   *          DatasetView representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateDatasetView(Representation representation, Variant variant) {
    DatasetView datasetViewOutput = null;
    try {

      DatasetView datasetViewInput = null;
      if (representation != null) {
        // Parse object representation
        datasetViewInput = getObject(representation, variant);

        // Business service
        datasetViewOutput = getStore().update(datasetViewInput);
      }

      Response response = new Response(true, datasetViewOutput, DatasetView.class, "datasetView");
      trace(Level.INFO, "Update the Dataset view - id : " + getDatasetViewId());
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot update the Dataset view - id : " + getDatasetViewId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot update the Dataset view - id : " + getDatasetViewId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }
  
  @Override
  public final void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify a single dataset view sending its new representation");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo param = new ParameterInfo("datasetViewId", true, "class", ParameterStyle.TEMPLATE, "Form component identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Delete datasetView
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteDatasetView(Variant variant) {
    try {
      // Business service
      getStore().delete(getDatasetViewId());

      // Response
      Response response = new Response(true, "datasetView.delete.success");
      trace(Level.INFO, "Delete the Dataset view - id : " + getDatasetViewId());
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot delete the Dataset view - id : " + getDatasetViewId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot delete the Dataset view - id : " + getDatasetViewId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }
  
  @Override
  public final void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete a single dataset view by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("datasetViewId", true, "class", ParameterStyle.TEMPLATE, "Form component identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
