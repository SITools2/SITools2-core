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
package fr.cnes.sitools.dataset.view;

import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.view.model.DatasetView;

/**
 * Class Resource for managing DatasetView Collection (GET, POST)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class DatasetViewCollectionResource extends AbstractDatasetViewResource {
  
  @Override
  public void sitoolsDescribe() {
    setName("DatasetViewCollectionResource");
    setDescription("Resource for managing DatasetView collection");
    setNegotiated(false);
  }

  /**
   * Update / Validate existing DatasetView
   * 
   * @param representation
   *          DatasetView representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newDatasetView(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "DATASET_VIEW_REPRESENTATION_REQUIRED");
    }
    try {
      // Parse object representation
      DatasetView datasetViewInput = getObject(representation, variant);

      // Business service
      DatasetView datasetViewOutput = getStore().create(datasetViewInput);

      // Response
      Response response = new Response(true, datasetViewOutput, DatasetView.class, "datasetView");
      trace(Level.INFO, "Add Dataset view - id : " + datasetViewOutput.getId());
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot add Dataset view");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot add Dataset view");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }
  
  @Override
  public final void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a new DatasetView sending its representation.");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * get all DatasetView
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveDatasetView(Variant variant) {
    try {

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
        trace(Level.FINE, "View available Dataset views");
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
  
  @Override
  public final void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of DatasetView available on the server.");
    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
    this.addStandardResourceCollectionFilterInfo(info);
  }

}
