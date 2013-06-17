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
package fr.cnes.sitools.form.dataset;

import java.util.ArrayList;
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
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.form.dataset.dto.FormDTO;
import fr.cnes.sitools.form.dataset.model.Form;

/**
 * Class Resource for managing Project Collection (GET, POST)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class FormDTOCollectionResource extends AbstractFormResource {

  @Override
  public void sitoolsDescribe() {
    setName("FormCollectionResource");
    setDescription("Resource for managing form collection");
    setNegotiated(false);
  }

  /**
   * Create new form
   * 
   * @param variant
   *          client preferred media type
   * @param representation
   *          representation of the formed as function of the variant
   * @return Representation
   */
  @Post
  public Representation newForm(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "FORM_REPRESENTATION_REQUIRED");
    }
    try {
      FormDTO formDTOInput = getObject(representation, variant);

      // Wrap
      Form formInput = FormDTO.dtoToForm(formDTOInput);

      // Business service
      if (formInput.getParent() == null) {
        formInput.setParent(getDatasetId());
      }

      // get the dataset to get its url
      DataSet ds = this.getDataset(getDatasetId());

      if (ds != null) {
        formInput.setParentUrl(ds.getSitoolsAttachementForUsers());
      }

      Form formOutput = getStore().create(formInput);

      registerObserver(formOutput);

      // Wrap
      FormDTO formDTOOutput = FormDTO.formToDTO(formOutput);

      // Response
      Response response = new Response(true, formDTOOutput, FormDTO.class, "form");
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
    info.setDocumentation("This method permits to create a form attached to a dataset");
    info.setIdentifier("create_form");

    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);

  }

  /**
   * get all forms
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
        if ((getDatasetId() != null) && (!getDatasetId().equals(form.getParent()))) {
          response = new Response(false, "FORM_NOT_BELONGS_TO_DATASET");
        }
        else {
          FormDTO formDTO = FormDTO.formToDTO(form);
          response = new Response(true, formDTO, FormDTO.class, "form");
        }

        return getRepresentation(response, variant);
      }
      else {
        ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
        if (getDatasetId() != null) {
          filter.setParent(getDatasetId());
        }
        List<Form> forms = getStore().getList(filter);
        int total = forms.size();
        forms = getStore().getPage(filter, forms);
        List<FormDTO> formsDTO = new ArrayList<FormDTO>();
        if (forms != null) {
          for (Form form : forms) {
            formsDTO.add(FormDTO.formToDTO(form));
          }
        }
        response = new Response(true, formsDTO, FormDTO.class, "forms");
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
   * Describe the Get command
   * 
   * @param info
   *          the info sent
   */
  @Override
  public void describeGet(MethodInfo info) {

    // Method
    info.setDocumentation("This method permits to retrieve a form, or all of them");
    info.setIdentifier("retrieve_forms");

    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);    

  }

}
