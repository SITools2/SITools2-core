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
package fr.cnes.sitools.form.components;

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
import fr.cnes.sitools.form.components.model.FormComponent;

/**
 * Class Resource for managing FormComponent Collection (GET, POST)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class FormComponentsCollectionResource extends AbstractFormComponentsResource {
  
  @Override
  public void sitoolsDescribe() {
    setName("FormComponentCollectionResource");
    setDescription("Resource for managing formComponent collection");
    setNegotiated(false);
  }

  /**
   * Update / Validate existing formComponent
   * 
   * @param representation
   *          FormComponent representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newFormComponent(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "PROJECT_REPRESENTATION_REQUIRED");
    }
    try {
      // Parse object representation
      FormComponent formComponentInput = getObject(representation, variant);

      // Business service
      FormComponent formComponentOutput = getStore().create(formComponentInput);

      // Response
      Response response = new Response(true, formComponentOutput, FormComponent.class, "formComponent");
      trace(Level.INFO, "Add the query form component type - id : " + formComponentOutput.getId());
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot add query form component type");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot add query form component type");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }
  
  @Override
  public final void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a new form components sending its representation.");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * get all formComponents
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveFormComponent(Variant variant) {
    try {

      if (getFormComponentId() != null) {
        FormComponent formComponent = getStore().retrieve(getFormComponentId());
        Response response = new Response(true, formComponent, FormComponent.class, "formComponent");
        return getRepresentation(response, variant);
      }
      else {
        ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
        List<FormComponent> formComponents = getStore().getList(filter);
        int total = formComponents.size();
        formComponents = getStore().getPage(filter, formComponents);
        Response response = new Response(true, formComponents, FormComponent.class, "formComponents");
        response.setTotal(total);
        trace(Level.FINE, "View available query form component types");
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
    info.setDocumentation("Method to retrieve the list of form components available on the server.");
    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
