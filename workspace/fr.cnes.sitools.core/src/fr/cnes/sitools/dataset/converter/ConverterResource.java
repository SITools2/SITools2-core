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
package fr.cnes.sitools.dataset.converter;

import java.util.Set;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.ResponseInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.dataset.converter.dto.ConverterModelDTO;
import fr.cnes.sitools.dataset.converter.model.ConverterChainedModel;
import fr.cnes.sitools.dataset.converter.model.ConverterModel;

/**
 * Resource for a converter
 * 
 * @author AKKA
 * 
 */
public final class ConverterResource extends AbstractConverterResource {

  /** converterChained identifier parameter */
  private String converterId = null;

  /** store */
  private SitoolsStore<ConverterChainedModel> store = null;

  @Override
  public void sitoolsDescribe() {
    setName("ConverterResource");
    setDescription("Create, read, update and delete converter chains for a dataset.");
  }

  @Override
  public void doInit() {
    super.doInit();

    store = ((ConverterApplication) getApplication()).getStore();

    converterId = (String) this.getRequest().getAttributes().get("converterId");
  }

  /**
   * Get the description of a converter in the converterChained list
   * 
   * @param variant
   *          the variant needed
   * @return the converter object
   */
  @Get
  @Override
  public Representation get(Variant variant) {
    Response response = null;
    if (getDatasetId() != null) {
      ConverterChainedModel convChainedModel = getStore().retrieve(getDatasetId());
      if (convChainedModel != null) {
        ConverterModel conv = getConverterModel(convChainedModel, converterId);
        if (conv != null) {
          ConverterModelDTO converterOutDTO = getConverterModelDTO(conv);
          response = new Response(true, converterOutDTO, ConverterModelDTO.class, "converter");
        }
        else {
          // converterChainedModel does not exists
          response = new Response(false, "converter.dontExists");
        }
      }
      else {
        // converterChainedModel null
        response = new Response(false, "NOT FOUND");
      }
    }
    else {
      // converterChainedModel null
      response = new Response(false, "NOT FOUND");
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
    info.setDocumentation("This method permits to retrieve a converter in a chain of converters");
    info.setIdentifier("retrieve_converter");

    // Request
    this.addStandardGetRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("converterId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the converter");
    info.getRequest().getParameters().add(pic);

    // Response 200
    this.addStandardResponseInfo(info);
  }

  /**
   * Modify an existing converter in the converterChained list
   * 
   * @param representation
   *          the converter representation
   * @param variant
   *          the variant needed
   * @return the modified converter
   */
  @Put
  @Override
  public Representation put(Representation representation, Variant variant) {
    Response response = null;

    if (getDatasetId() != null) {

      ConverterModelDTO converterInputDTO = getObject(representation);

      ConverterModel converterInput = getConverterModelFromDTO(converterInputDTO);

      ConverterChainedModel convChainedModel = getStore().retrieve(getDatasetId());
      if (convChainedModel != null) {
        ConverterModel conv = getConverterModel(convChainedModel, converterId);
        if (conv != null) {
          updateConverter(conv, converterInput);

          if (converterInput.getClassName() == null || converterInput.getClassName().equals("")) {
            converterInput.setClassName(conv.getClassName());
          }

          // // VALIDATION PART
          Set<ConstraintViolation> constraints = checkValidity(converterInput);
          if (constraints != null) {
            ConstraintViolation[] array = new ConstraintViolation[constraints.size()];
            array = constraints.toArray(array);

            response = new Response(false, array, ConstraintViolation.class, "constraints");
            return getRepresentation(response, variant);
          }

          getStore().update(convChainedModel);
          ConverterModel convOut = getConverterModel(convChainedModel, converterId);

          ConverterModelDTO converterOutDTO = getConverterModelDTO(convOut);
          response = new Response(true, converterOutDTO, ConverterModelDTO.class, "converter");

        }
        else {
          // converterChainedModel does not exists
          response = new Response(false, "converter.dontExists");
        }
      }
      else {
        // converterChainedModel null
        response = new Response(false, "NOT FOUND");
      }
    }
    else {
      // converterChainedModel null
      response = new Response(false, "NOT FOUND");
    }
    return getRepresentation(response, variant);
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
    info.setDocumentation("This method permits to modify a converter in the chain of converter");
    info.setIdentifier("update_converter");

    // Request
    this.addStandardPostOrPutRequestInfo(info);

    ParameterInfo pic = new ParameterInfo("converterId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the converter");
    info.getRequest().getParameters().add(pic);

    // Response 200
    this.addStandardResponseInfo(info);
    // Response 500

    ResponseInfo response = new ResponseInfo("Response when internal server error occurs");
    response.getStatuses().add(Status.SERVER_ERROR_INTERNAL);
    info.getResponses().add(response);
  }

  /**
   * Update dest ConverterModel objet with input ConverterModel's attributes
   * 
   * @param dest
   *          the ConverterModel to update
   * @param input
   *          the source of the update
   */
  private void updateConverter(ConverterModel dest, ConverterModel input) {
    dest.setDescriptionAction(input.getDescriptionAction());
    dest.setParametersMap(input.getParametersMap());
  }

  /**
   * Delete an existing converter in the converterChained list
   * 
   * @param variant
   *          the variant needed
   * @return a representation telling whether it is successful or not
   */
  @Delete
  @Override
  protected Representation delete(Variant variant) {

    ConverterChainedModel convChained = getStore().retrieve(getDatasetId());
    Response response = null;
    if (convChained != null) {
      ConverterModel conv = getConverterModel(convChained, converterId);
      if (conv != null) {
        boolean success = convChained.getConverters().remove(conv);
        getStore().update(convChained);
        if (success) {
          response = new Response(true, "converter.deleted.success");
        }
        else {
          response = new Response(false, "converter.deleted.failure");
        }
      }
      else {
        response = new Response(false, "converter.deleted.failure.notfound");
      }

    }
    else {
      response = new Response(false, "converter.deleted.failure.convChained.notfound");
    }
    return getRepresentation(response, variant);

  }

  /**
   * Describe the Delete command
   * 
   * @param info
   *          the info sent
   */
  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("This method delete a converter  in the chain of converter");
    info.setIdentifier("delete_converter");

    this.addStandardGetRequestInfo(info);

    ParameterInfo pic = new ParameterInfo("converterId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the converter");
    info.getRequest().getParameters().add(pic);

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
