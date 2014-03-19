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
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.notification.model.Notification;

/**
 * DataSet resource with RUD operations
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class DataSetResource extends AbstractDataSetResource {

  @Override
  public void sitoolsDescribe() {
    setName("DataSetResource");
    setDescription("Resource for managing an identified dataset");
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
   * Get on DataSet
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveDataSet(Variant variant) {
    try {

      if (getDatasetId() != null) {
        DataSet dataset = store.retrieve(getDatasetId());
        Response response = null;
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
      trace(Level.INFO, "Cannot Edit information for the dataset - id: " + getDatasetId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot Edit information for the dataset - id: " + getDatasetId());
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve a single or all datasets");
    info.setIdentifier("retrieve_dataset");
    addStandardGetRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("datasetId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the dataset");
    info.getRequest().getParameters().add(pic);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
    addStandardResourceCollectionFilterInfo(info);
  }

  /**
   * Update / Validate existing DataSet
   * 
   * @param representation
   *          DataSet Representation
   * @param variant
   *          Variant client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateDataSet(Representation representation, Variant variant) {
    DataSet datasetOutput = null;
    try {
      DataSet datasetInput = null;
      if (representation != null) {
        datasetInput = getObject(representation);

        datasetOutput = store.retrieve(getDatasetId());
        if ("ACTIVE".equals(datasetOutput.getStatus())) {
          trace(Level.INFO, "Cannot update the dataset - id: " + getDatasetId());
          Response response = new Response(false, "DATASET_ACTIVE");
          return getRepresentation(response, variant);
        }

        // Business service
        datasetInput.setStatus("INACTIVE");
        datasetInput.setExpirationDate(new Date(new GregorianCalendar().getTime().getTime()));

        if (datasetInput.getDictionaryMappings() == null) {
          datasetInput.setDictionaryMappings(datasetOutput.getDictionaryMappings());
        }

        datasetOutput = store.update(datasetInput);

        // Notify observers
        Notification notification = new Notification();
        notification.setObservable(datasetOutput.getId());
        notification.setStatus(datasetOutput.getStatus());
        notification.setEvent("DATASET_UPDATED");
        notification.setMessage("dataset.update.success");
        getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

        // Register DataSet as observer of Dictionary resources
        unregisterObserver(datasetOutput);
        registerObserver(datasetOutput);
      }

      if (datasetOutput != null) {
        trace(Level.INFO, "Update the dataset " + datasetOutput.getName());
        Response response = new Response(true, datasetOutput, DataSet.class, "dataset");
        return getRepresentation(response, variant);
      }
      else {
        trace(Level.INFO, "Cannot update the dataset - id: " + getDatasetId());
        Response response = new Response(false, "Can not validate dataset");
        return getRepresentation(response, variant);
      }

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot update the dataset - id: " + getDatasetId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot update the dataset - id: " + getDatasetId());
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify a single dataset sending its new representation");
    info.setIdentifier("update_dataset");
    addStandardPostOrPutRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("datasetId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the dataset");
    info.getRequest().getParameters().add(pic);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

  /**
   * Delete DataSet
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteDataSet(Variant variant) {
    try {
      Response response;
      DataSet datasetOutput = store.retrieve(getDatasetId());
      if (datasetOutput != null) {
        try {
          application.detachDataSetDefinitif(datasetOutput);
        }
        catch (Exception e) {
          getLogger().log(Level.INFO, null, e);
        }

        // Business service
        store.delete(getDatasetId());

        // unregister DataSet as observer of Dictionary resources
        unregisterObserver(datasetOutput);

        // Response
        response = new Response(true, "datasetCrud.popup");

        // Notify observers
        Notification notification = new Notification();
        notification.setObservable(getDatasetId());
        notification.setStatus("DELETED");
        notification.setEvent("DATASET_DELETED");
        notification.setMessage("DataSet definitively deleted.");
        getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

        trace(Level.INFO, "Delete the dataset " + datasetOutput.getName());
      }
      else {
        trace(Level.INFO, "Cannot delete the dataset - id:" + getDatasetId());
        response = new Response(false, "dataset.delete.failure");
      }
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot delete the dataset - id:" + getDatasetId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot delete the dataset - id:" + getDatasetId());
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete a dataset by ID");
    info.setIdentifier("delete_dataset");
    addStandardGetRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("datasetId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the dataset");
    info.getRequest().getParameters().add(pic);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

}
