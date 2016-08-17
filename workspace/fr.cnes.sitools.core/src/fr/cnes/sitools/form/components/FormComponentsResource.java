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
import fr.cnes.sitools.form.components.model.FormComponent;

/**
 * Class Resource for managing single FormComponent (GET UPDATE DELETE)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class FormComponentsResource extends AbstractFormComponentsResource {
  
  @Override
  public void sitoolsDescribe() {
    setName("FormComponentResource");
    setDescription("Resource for managing an identified formComponent");
    setNegotiated(false);
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
    // XStream xstream = XStreamFactory.getInstance().getXStreamWriter(variant.getMediaType(), false);
    if (getFormComponentId() != null) {
      FormComponent formComponent = getStore().retrieve(getFormComponentId());
      Response response = new Response(true, formComponent, FormComponent.class, "formComponent");
      trace(Level.INFO, "Edit configuration parameters of the query form component type - id : " + getFormComponentId());
      return getRepresentation(response, variant);
    }
    else {
      ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
      List<FormComponent> formComponents = getStore().getList(filter);
      int total = formComponents.size();
      formComponents = getStore().getPage(filter, formComponents);
      Response response = new Response(true, formComponents, FormComponent.class, "formComponents");
      response.setTotal(total);
      return getRepresentation(response, variant);
    }
  }
  
  @Override
  public final void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve a single form component by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("formComponentId", true, "class", ParameterStyle.TEMPLATE, "Form component identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
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
  @Put
  public Representation updateFormComponent(Representation representation, Variant variant) {
    FormComponent formComponentOutput = null;
    try {

      FormComponent formComponentInput = null;
      if (representation != null) {
        // Parse object representation
        formComponentInput = getObject(representation, variant);

        // Business service
        formComponentOutput = getStore().update(formComponentInput);
      }

      Response response = new Response(true, formComponentOutput, FormComponent.class, "formComponent");
      trace(Level.INFO, "Update configuration parameters of the query form component type - id : " + getFormComponentId());
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot update the query form component type - id : " + getFormComponentId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot update the query form component type - id : " + getFormComponentId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }
  
  @Override
  public final void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify a single form component sending its new representation");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo param = new ParameterInfo("formComponentId", true, "class", ParameterStyle.TEMPLATE, "Form component identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Delete formComponent
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteFormComponent(Variant variant) {
    try {
      // Business service
      getStore().delete(getFormComponentId());

      // Response
      Response response = new Response(true, "formComponent.delete.success");
      trace(Level.INFO, "Delete the query form component type - id : " + getFormComponentId());
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot delete the query form component type - id : " + getFormComponentId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot delete the query form component type - id : " + getFormComponentId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }
  
  @Override
  public final void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete a single form component by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("formComponentId", true, "class", ParameterStyle.TEMPLATE, "Form component identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
