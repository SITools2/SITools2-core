     /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.dataset.converter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ResponseInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsMediaType;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.dataset.converter.dto.ConverterChainedModelDTO;
import fr.cnes.sitools.dataset.converter.dto.ConverterChainedOrderDTO;
import fr.cnes.sitools.dataset.converter.dto.ConverterModelDTO;
import fr.cnes.sitools.dataset.converter.model.ConverterChainedModel;
import fr.cnes.sitools.dataset.converter.model.ConverterModel;

/**
 * Resource for a converter
 * 
 * @author AKKA
 * 
 */
public final class ConverterChainedResource extends AbstractConverterResource {

  /** store */
  private SitoolsStore<ConverterChainedModel> store = null;

  @Override
  public void doInit() {
    super.doInit();
    getVariants().add(new Variant(SitoolsMediaType.APPLICATION_JAVA_OBJECT_SITOOLS_MODEL));

    store = ((ConverterApplication) getApplication()).getStore();
  }

  @Override
  public void sitoolsDescribe() {
    setName("ConverterChainedResource");
    setDescription("Create, read, update and delete converter chains for a dataset.");
  }

  /**
   * Add a new converter to the converter list If the list of converter does not exists, it is created as well
   * 
   * @param representation
   *          The representation parameter
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newConverter(Representation representation, Variant variant) {
    Response response = null;
    try {
      ConverterModelDTO converterInputDTO = getObject(representation);

      ConverterModel converterInput = getConverterModelFromDTO(converterInputDTO);

      // // VALIDATION PART
      Set<ConstraintViolation> constraints = checkValidity(converterInput);
      if (constraints != null) {
        ConstraintViolation[] array = new ConstraintViolation[constraints.size()];
        array = constraints.toArray(array);

        response = new Response(false, array, ConstraintViolation.class, "constraints");
        return getRepresentation(response, variant);
      }

      // END OF THE VALIDATION PART

      // get the converterChained
      ConverterChainedModel convList = getStore().retrieve(getDatasetId());
      if (convList == null) {
        convList = new ConverterChainedModel();
        convList.setId(getDatasetId());
        // Business service
        convList.setParent(getDatasetId());
        convList = getStore().create(convList);

      }

      if (converterInput.getId() == null || converterInput.getId().equals("")) {
        converterInput.setId(UUID.randomUUID().toString());
      }

      // activate the converter by default
      converterInput.setStatus("ACTIVE");

      convList.getConverters().add(converterInput);

      ConverterChainedModel converterChained = getStore().update(convList);
      ConverterModel convOut = getConverterModel(converterChained, converterInput.getId());
      // Response

      // register observer
      registerObserver(converterChained);
      ConverterModelDTO converterOutDTO = getConverterModelDTO(convOut);
      response = new Response(true, converterOutDTO, ConverterModelDTO.class, "converter");
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
   * Describe the POST command
   * 
   * @param info
   *          the info sent
   */
  @Override
  public void describePost(MethodInfo info) {

    // Method
    info.setDocumentation("This method permits to create a converter and add it to the chain of converters");
    info.setIdentifier("create_converter");

    // Request
    this.addStandardPostOrPutRequestInfo(info);

    // Response 200 & 403
    this.addStandardResponseInfo(info);

    // Response 500
    this.addStandardInternalServerErrorInfo(info);

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.ServerResource#get(org.restlet.representation.Variant)
   */
  @Get
  @Override
  public Representation get(Variant variant) {

    ConverterChainedModel convChModel = getStore().retrieve(getDatasetId());
    Response response;
    // Return the Model if it has been specifically asked, return the DTO otherwise
    MediaType mediaType = getMediaType(variant);
    if (mediaType.isCompatible(SitoolsMediaType.APPLICATION_JAVA_OBJECT_SITOOLS_MODEL)) {
      response = new Response(true, convChModel, ConverterChainedModel.class, "converterChainedModel");
    }
    else {
      ConverterChainedModelDTO converterChainedOutDTO = getConverterChainedModelDTO(convChModel);
      addCurrentClassDescriptions(converterChainedOutDTO);
      response = new Response(true, converterChainedOutDTO, ConverterChainedModelDTO.class, "converterChainedModel");
    }
    return getRepresentation(response, variant);
  }

  /**
   * Describe the Get command
   * 
   * @param info
   *          the info sent
   */
  @Override
  public void describeGet(MethodInfo info) {

    // Method
    info.setDocumentation("This method permits to retrieve a chain of converters");
    info.setIdentifier("retrieve_converterschained");

    // Request
    this.addStandardGetRequestInfo(info);

    // Response 200
    this.addStandardResponseInfo(info);
  }

  /**
   * Update / Validate existing Converters Basicaly, it changes the order of the converters
   * 
   * @param representation
   *          the representation parameter
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateConverterChainedModel(Representation representation, Variant variant) {
    ConverterChainedModel converterOutput = null;
    Response response = null;
    try {
      ConverterChainedOrderDTO orderDTO = null;
      if (representation != null) {
        if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
          // Parse the XML representation to get the osearch bean
          orderDTO = new XstreamRepresentation<ConverterChainedOrderDTO>(representation).getObject();
        }
        else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
          // Parse the JSON representation to get the bean
          orderDTO = new JacksonRepresentation<ConverterChainedOrderDTO>(representation, ConverterChainedOrderDTO.class)
              .getObject();
        }

        converterOutput = getStore().retrieve(getDatasetId());
        if (converterOutput.getId().equals(orderDTO.getId())) {
          converterOutput = this.changeConverterOrder(converterOutput, orderDTO);

          // Business service
          converterOutput = getStore().update(converterOutput);
          // Register ConverterResource as observer of datasets resources
          unregisterObserver(converterOutput);

          registerObserver(converterOutput);

          if (converterOutput != null) {
            // Response
            ConverterChainedModelDTO converterChainedOutDTO = getConverterChainedModelDTO(converterOutput);
            response = new Response(true, converterChainedOutDTO, ConverterChainedModelDTO.class,
                "converterChainedModel");

          }

        }
        else {
          response = new Response(false, "Can not validate converterChained");
        }

      }

      else {
        // Response

        response = new Response(false, "Can not validate converterChained");

      }
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
   * Describe the Put command
   * 
   * @param info
   *          the info sent
   */
  @Override
  public void describePut(MethodInfo info) {

    // Method
    info.setDocumentation("This method permits to modify the order of the converters in the chain of converter");
    info.setIdentifier("update_converterschained");

    // Request
    this.addStandardPostOrPutRequestInfo(info);

    // Response 200
    this.addStandardResponseInfo(info);
    // Response 500

    ResponseInfo response = new ResponseInfo("Response when internal server error occurs");
    response.getStatuses().add(Status.SERVER_ERROR_INTERNAL);
    info.getResponses().add(response);
  }

  /**
   * Sort the converter list using the given order
   * 
   * @param convChained
   *          the converterChainedModel to sort
   * @param orderDTO
   *          the new order
   * @return the sorted converterChainedModel
   */
  private ConverterChainedModel changeConverterOrder(ConverterChainedModel convChained,
      ConverterChainedOrderDTO orderDTO) {

    List<ConverterModel> outputList = new ArrayList<ConverterModel>();
    List<ConverterModel> convList = convChained.getConverters();

    boolean found = false;
    if (orderDTO.getIdOrder() != null) {
      for (Iterator<String> iterator = orderDTO.getIdOrder().iterator(); iterator.hasNext();) {
        String convId = iterator.next();
        found = false;
        for (Iterator<ConverterModel> it2 = convList.iterator(); it2.hasNext() && !found;) {
          ConverterModel converterModel = it2.next();
          if (convId.equals(converterModel.getId())) {
            outputList.add(converterModel);
            it2.remove();
            found = true;
          }
        }
      }
    }
    convChained.setConverters(outputList);
    return convChained;

  }

  /**
   * Delete Converter
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteConverterChainedModel(Variant variant) {
    try {
      ConverterChainedModel conv = getStore().retrieve(getDatasetId());
      Response response = null;
      if (conv != null) {

        // Business service
        getStore().delete(getDatasetId());

        // Response
        response = new Response(true, "converterChained.deleted.success");
        // Register ConverterChainedModel as observer of datasets resources
        unregisterObserver(conv);

      }
      else {
        response = new Response(false, "converterChained.deleted.failure");
      }
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
   * Describe the Delete command
   * 
   * @param info
   *          the info sent
   */
  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("This method delete a chain of converters");
    info.setIdentifier("delete_converterschained");
    this.addStandardGetRequestInfo(info);

    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public SitoolsStore<ConverterChainedModel> getStore() {
    return store;
  }

}
