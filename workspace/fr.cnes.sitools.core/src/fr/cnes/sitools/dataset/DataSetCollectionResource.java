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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import fr.cnes.sitools.util.Util;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.notification.model.Notification;

/**
 * DataSet collection resource with CR operations
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class DataSetCollectionResource extends AbstractDataSetResource {

  @Override
  public void sitoolsDescribe() {
    setName("DataSetCollectionResource");
    setDescription("Resource for managing dataset collection");

  }

  /**
   * Initiate the resource
   */
  @Override
  public void doInit() {
    super.doInit();
    this.setNegotiated(false);
  }

  /**
   * Create a new DataSet
   * 
   * @param representation
   *          DataSet Representation
   * @param variant
   *          Variant user preferred MediaType
   * @return Representation
   */
  @Post
  public Representation newDataSet(Representation representation, Variant variant) {
    if (representation == null) {
      trace(Level.INFO, "Cannot create the dataset");
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "DATASET_REPRESENTATION_REQUIRED");
    }
    try {
      Response response = null;
      DataSet datasetInput = getObject(representation);
      // to prevent illegal status like "ACTIVE"
      datasetInput.setStatus("NEW");
      datasetInput.setExpirationDate(new Date(new GregorianCalendar().getTime().getTime()));

      // attribution d'un id par défaut à chaque colonne.
      if (datasetInput.getColumnModel() != null) {
        for (Column column : datasetInput.getColumnModel()) {
          if ((column.getId() == null) || column.getId().equals("")) {
            column.setId(UUID.randomUUID().toString());
          }
        }
      }

      if (Util.isEmpty(datasetInput.getSitoolsAttachementForUsers())) {
        String sitoolsAttachment = "/" + datasetInput.getName().toLowerCase().replaceAll(" ", "_");
        datasetInput.setSitoolsAttachementForUsers(sitoolsAttachment);
      }

      // check for dataset unicity
      response = checkUnicity(datasetInput, true);
      if (response != null && !response.getSuccess()) {
        trace(Level.INFO, "Cannot create the dataset " + datasetInput.getName());
        return getRepresentation(response, variant);
      }


      // Business service
      DataSet datasetOutput = store.create(datasetInput);

      // Notify observers
      Notification notification = new Notification();
      notification.setObservable(datasetOutput.getId());
      notification.setStatus(datasetInput.getStatus());
      notification.setEvent("DATASET_CREATED");
      notification.setMessage("New dataset created.");
      getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

      
      // Register DataSet as observer of Dictionary resources
      unregisterObserver(datasetOutput);
      registerObserver(datasetOutput);

      trace(Level.INFO, "Create the dataset " + datasetOutput.getName());
      // Response
      response = new Response(true, datasetOutput, DataSet.class, "dataset");
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot create the dataset");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot create the dataset");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a dataset");
    info.setIdentifier("create_dataset");
    addStandardPostOrPutRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

  /**
   * Gets multiple DataSets or one DataSet if id is given in parameter into a standard Response representation
   * 
   * @param variant
   *          Variant client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveDataSet(Variant variant) {
    try {
      if (datasetId != null) {
        DataSet dataset = store.retrieve(getDatasetId());
        Response response;
        if (dataset != null) {
          trace(Level.FINE, "Edit information for the dataset " + dataset.getName());
          response = new Response(true, dataset, DataSet.class, "dataset");
        }
        else {
          trace(Level.INFO, "Cannot Edit information for the dataset - id: " + getDatasetId());
          response = new Response(false, "dataset.not.found");
        }
        return getRepresentation(response, variant);
      }
      else {
        ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
        List<DataSet> datasets = store.getList(filter);
        int total = datasets.size();
        datasets = store.getPage(filter, datasets);

        trace(Level.FINE, "View available datasets");
        Response response = new Response(true, datasets, DataSet.class, "datasets");
        response.setTotal(total);
        return getRepresentation(response, variant);
      }
    }
    catch (ResourceException e) {
      trace(Level.INFO, "View available datasets");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "View available datasets");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve a single or all datasets");
    info.setIdentifier("retrieve_dataset");
    addStandardGetRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
    addStandardResourceCollectionFilterInfo(info);
  }

}
