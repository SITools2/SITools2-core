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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.DictionaryMapping;
import fr.cnes.sitools.notification.model.Notification;

/**
 * Resource for managing Dictionary mappings on a DataSet
 * 
 * 
 * @author m.gond (Akka Technologies)
 */
public class DataSetDictionaryMappingResource extends AbstractDataSetResource {
  /** The dictionaryId to get the mapping */
  private String dictionaryId = null;

  @Override
  public void sitoolsDescribe() {
    setName("DataSetDictionaryMappingsResource");
    setDescription("Resource for managing Dictionary mappings on a DataSet");
  }

  /**
   * Initiate the resource
   */
  @Override
  public void doInit() {
    super.doInit();
    this.setNegotiated(false);
    setDictionaryId((String) this.getRequest().getAttributes().get("dictionaryId"));

  }

  /**
   * Get a Mapping from a given dictionaryId
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveMappings(Variant variant) {
    try {
      Response response = null;
      if (getDatasetId() != null) {
        DataSet dataset = store.retrieve(getDatasetId());
        if (dataset == null) {
          trace(Level.FINE, "Cannot edit semantic dictionary on the dataset - id: " + getDatasetId()
              + " for the dictionary - id: " + getDictionaryId());
          response = new Response(false, "DATASET_NOT_FOUND");
        }
        else {
          if (dataset.getDictionaryIds() == null || !dataset.getDictionaryIds().contains(getDictionaryId())) {
            response = new Response(false, "DICTIONARY_NOT_FOUND");
            trace(Level.FINE, "Cannot edit semantic dictionary on the dataset " + dataset.getName()
                + " for the dictionary - id: " + getDictionaryId());
          }
          else {
            trace(Level.FINE, "Edit semantic dictionary on the dataset " + dataset.getName() + " for the dictionary "
                + getDictionaryId());
            response = new Response(true, dataset.getDictionaryMapping(getDictionaryId()), DictionaryMapping.class,
                "dictionaryMapping");

          }
        }
      }
      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      trace(Level.FINE, "Cannot edit semantic dictionary on the dataset - id: " + getDatasetId()
          + " for the dictionary - id: " + getDictionaryId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.FINE, "Cannot edit semantic dictionary on the dataset - id: " + getDatasetId()
          + " for the dictionary - id: " + getDictionaryId());
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the mapping for a particular dictionary");
    info.setIdentifier("retrieve_mapping");
    addStandardGetRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("datasetId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the dataset");
    ParameterInfo pic2 = new ParameterInfo("dictionaryId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the dictionary");
    info.getRequest().getParameters().add(pic);
    info.getRequest().getParameters().add(pic2);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

  /**
   * Put a mapping to add or modify it on the dataset object
   * 
   * @param representation
   *          DictionaryMapping Representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation putMapping(Representation representation, Variant variant) {

    Response response = null;
    try {
      // on charge le dataset
      DataSet ds = store.retrieve(getDatasetId());
      if (ds == null) {
        trace(Level.INFO, "Cannot apply a semantic dictionary on the dataset - id: " + getDatasetId()
            + " for the dictionary - id: " + getDictionaryId());
        response = new Response(false, "DATASET_NOT_FOUND");
      }
      else if ("ACTIVE".equals(ds.getStatus())) {
        trace(Level.INFO, "Cannot apply a semantic dictionary on the dataset - id: " + getDatasetId()
            + " for the dictionary - id: " + getDictionaryId());
        response = new Response(false, "DATASET_ACTIVE");
      }
      else {
        DictionaryMapping mappingOutput = null;
        DictionaryMapping mappingInput = null;
        if (representation != null) {
          mappingInput = getObjectDictionaryMapping(representation);
          List<DictionaryMapping> listMapping = ds.getDictionaryMappings();
          if (listMapping == null) {
            listMapping = new ArrayList<DictionaryMapping>();
            ds.setDictionaryMappings(listMapping);
          }
          DictionaryMapping dicoMapping = ds.getDictionaryMapping(getDictionaryId());
          // if the dicoMapping already is in the List, let's delete it
          if (dicoMapping != null) {
            listMapping.remove(dicoMapping);
          }

          // if this is the default dictionary, let's look if there was no default dictionary before
          // if there was one, the new one is the dictionary
          if (mappingInput.isDefaultDico()) {
            for (Iterator<DictionaryMapping> itMapping = listMapping.iterator(); itMapping.hasNext();) {
              DictionaryMapping map = (DictionaryMapping) itMapping.next();
              if (map.isDefaultDico()) {
                map.setDefaultDico(false);
              }
            }
          }

          // we add the new mapping only if it's not empty
          // if (mappingInput.getMapping() != null && !mappingInput.getMapping().isEmpty()) {
          listMapping.add(mappingInput);
          // }

          ds.setDictionaryMappings(listMapping);
          // update the dataset
          DataSet dsOut = store.update(ds);
          mappingOutput = dsOut.getDictionaryMapping(getDictionaryId());
          response = new Response(true, mappingOutput, DictionaryMapping.class, "dictionaryMapping");

          // Notify observers
          Notification notification = new Notification();
          notification.setObservable(dsOut.getId());
          notification.setStatus(dsOut.getStatus());
          notification.setEvent("DATASET_DICOMAPPING_UPDATED");
          notification.setMessage("dataset.update.success");
          getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

          // Register DataSet as observer of Dictionary resources
          unregisterObserver(dsOut);
          registerObserver(dsOut);

          trace(Level.INFO, "Apply a semantic dictionary on the dataset " + dsOut.getName()
              + " for the dictionary - id: " + getDictionaryId());
        }
      }

      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot apply a semantic dictionary on the dataset - id: " + getDatasetId()
          + " for the dictionary - id: " + getDictionaryId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot apply a semantic dictionary on the dataset - id: " + getDatasetId()
          + " for the dictionary - id: " + getDictionaryId());
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify the dataset by adding or modifying a mapping for a particular dictionary sending a DictionaryMapping representation");
    info.setIdentifier("update_mapping");
    addStandardPostOrPutRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("datasetId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the dataset");
    ParameterInfo pic2 = new ParameterInfo("datasetId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the dictionary");
    info.getRequest().getParameters().add(pic);
    info.getRequest().getParameters().add(pic2);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

  /**
   * Delete a mapping
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteMapping(Variant variant) {
    try {
      DataSet datasetOutput = store.retrieve(getDatasetId());
      Response response;
      if (datasetOutput == null) {
        trace(Level.INFO, "Cannot delete a semantic dictionary on the dataset - id: " + getDatasetId()
            + " for the dictionary - id: " + getDictionaryId());
        response = new Response(false, "DATASET_NOT_FOUND");
      }
      else {
        boolean updated = false;
        unregisterObserver(datasetOutput);
        if (datasetOutput.getDictionaryIds() != null && datasetOutput.getDictionaryIds().contains(getDictionaryId())) {
          for (Iterator<DictionaryMapping> iterator = datasetOutput.getDictionaryMappings().iterator(); iterator
              .hasNext();) {
            DictionaryMapping mapping = iterator.next();
            if (mapping.getDictionaryId().equals(getDictionaryId())) {
              iterator.remove();
              updated = true;
              break;
            }
          }
          if (updated) {
            store.update(datasetOutput);
          }
          // Register DataSet as observer of Dictionary resources

          registerObserver(datasetOutput);
          trace(Level.INFO, "Delete a semantic dictionary on the dataset " + datasetOutput.getName()
              + " for the dictionary - id: " + getDictionaryId());
          response = new Response(true, "MAPPING_DELETED");
        }
        else {
          trace(Level.INFO, "Cannot delete a semantic dictionary on the dataset " + datasetOutput.getName()
              + " for the dictionary - id: " + getDictionaryId());
          response = new Response(false, "MAPPING_NOT_FOUND");
        }
      }
      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot delete a semantic dictionary on the dataset - id: " + getDatasetId()
          + " for the dictionary - id: " + getDictionaryId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot delete a semantic dictionary on the dataset - id: " + getDatasetId()
          + " for the dictionary - id: " + getDictionaryId());
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete a mapping for a particular Dictionary");
    info.setIdentifier("delete_mappingt");
    addStandardGetRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("datasetId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the dataset");
    ParameterInfo pic2 = new ParameterInfo("datasetId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the dictionary");
    info.getRequest().getParameters().add(pic);
    info.getRequest().getParameters().add(pic2);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

  /**
   * Gets DictionaryMapping object from Representation
   * 
   * @param representation
   *          of a DictionaryMapping
   * @return DictionaryMapping
   */
  private DictionaryMapping getObjectDictionaryMapping(Representation representation) {
    DictionaryMapping object = null;

    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the dataset bean
      object = new XstreamRepresentation<DictionaryMapping>(representation).getObject();

    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      object = new JacksonRepresentation<DictionaryMapping>(representation, DictionaryMapping.class).getObject();
    }

    return object;
  }

  /**
   * Gets the dictionaryId value
   * 
   * @return the dictionaryId
   */
  public String getDictionaryId() {
    return dictionaryId;
  }

  /**
   * Sets the value of dictionaryId
   * 
   * @param dictionaryId
   *          the dictionaryId to set
   */
  public void setDictionaryId(String dictionaryId) {
    this.dictionaryId = dictionaryId;
  }

}
