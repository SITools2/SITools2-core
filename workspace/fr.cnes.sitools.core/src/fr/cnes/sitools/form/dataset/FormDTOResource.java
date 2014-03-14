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
package fr.cnes.sitools.form.dataset;

import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.ResponseInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.form.dataset.dto.FormDTO;
import fr.cnes.sitools.form.dataset.model.Form;

/**
 * Form management via FormDTO
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class FormDTOResource extends AbstractFormResource {

  @Override
  public void sitoolsDescribe() {
    setName("FormResource");
    setDescription("Resource for managing an identified form");
  }

  /**
   * Get a single form by ID
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieve(Variant variant) {
    try {
      Response response = null;
      if (getFormId() != null) {
        Form form = getStore().retrieve(getFormId());
        if (form != null) {
          FormDTO formDTO = FormDTO.formToDTO(form);
          response = new Response(true, formDTO, FormDTO.class, "form");
        }
        else {
          response = new Response(false, "FORM_NOT_FOUND");
        }
        return getRepresentation(response, variant);
      }
      else {
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
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
   * Describe the Get command
   * 
   * @param info
   *          the info sent
   */
  @Override
  public void describeGet(MethodInfo info) {

    // Method
    info.setDocumentation("This method permits to retrieve a form by ID");
    info.setIdentifier("retrieve_form_by_id");

    // Request
    this.addStandardGetRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("formId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the form");
    info.getRequest().getParameters().add(pic);

    // Response 200
    this.addStandardResponseInfo(info);

    // Failures
    ResponseInfo response = new ResponseInfo("Response the form ID is not recognized");
    response.getStatuses().add(Status.CLIENT_ERROR_BAD_REQUEST);
    info.getResponses().add(response);

    this.addStandardInternalServerErrorInfo(info);

  }

  /**
   * Update / Validate existing forms
   * 
   * @param representation
   *          FormDTO representation
   * @param variant
   *          client preferred media type
   * @return Response representation FormDTO Representation if update succeed
   */
  @Put
  public Representation update(Representation representation, Variant variant) {
    try {
      FormDTO formDTOInput = null;
      if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
        // Parse the XML representation to get the bean
        formDTOInput = new XstreamRepresentation<FormDTO>(representation).getObject();

      }
      else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
        // Parse the JSON representation to get the bean
        formDTOInput = new JacksonRepresentation<FormDTO>(representation, FormDTO.class).getObject();
      }

      // Wrap
      Form formInput = FormDTO.dtoToForm(formDTOInput);
      if (formInput.getParent() == null) {
        formInput.setParent(getDatasetId());
      }

      // retrieve the parentUrl
      Form formData = getStore().retrieve(formInput.getId());
      formInput.setParentUrl(formData.getParentUrl());

      // Business service
      Form formOutput = getStore().update(formInput);

      if (formOutput != null) {

        // Wrap
        FormDTO formDTOOutput = FormDTO.formToDTO(formOutput);

        unregisterObserver(formOutput);
        registerObserver(formOutput);

        // Response
        Response response = new Response(true, formDTOOutput, FormDTO.class, "form");
        return getRepresentation(response, variant);
      }
      else {
        Response response = new Response(false, "Can not validate form");
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
   * Describe the Put command
   * 
   * @param info
   *          the info sent
   */
  @Override
  public void describePut(MethodInfo info) {

    // Method
    info.setDocumentation("This method permits to modify a form, sending its new representation");
    info.setIdentifier("update_form");

    // Request
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("formId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the form");
    info.getRequest().getParameters().add(pic);

    // Response 200
    this.addStandardResponseInfo(info);

    // Response 500
    this.addStandardInternalServerErrorInfo(info);

  }

  /**
   * Delete form
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation delete(Variant variant) {
    try {
      Response response;
      Form form = getStore().retrieve(getFormId());
      if (form != null) {

        // Business service
        getStore().delete(getFormId());
        unregisterObserver(form);
        // Response

        response = new Response(true, "form.delete.success");
      }
      else {
        response = new Response(true, "form.delete.failure");
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

    // Method
    info.setDocumentation("This method delete a form as indicated by the ID");
    info.setIdentifier("delete_form");

    // Request
    this.addStandardGetRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("formId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the form");
    info.getRequest().getParameters().add(pic);

    // Response 200
    this.addStandardSimpleResponseInfo(info);

    // Response 500
    this.addStandardInternalServerErrorInfo(info);

  }

}
