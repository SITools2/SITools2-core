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
package fr.cnes.sitools.dictionary;

import java.util.HashMap;
import java.util.Map;
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

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dictionary.model.ConceptTemplate;
import fr.cnes.sitools.notification.model.Notification;

/**
 * Resource for concept template services
 * @author jp.boignard (AKKA technologies)
 */
public class ConceptTemplateResource extends AbstractConceptTemplateResource {

  @Override
  public void sitoolsDescribe() {
    setName("ConceptTemplateResource");
    setDescription("Resource for managing a template of concept - CRUD");
    setNegotiated(false);
  }

  /**
   * get template
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveConceptTemplate(Variant variant) {
    try {

      if (getConceptTemplateId() != null) {
        ConceptTemplate template = getStore().retrieve(getConceptTemplateId());
        Response response = new Response(true, template, ConceptTemplate.class, "template");
        return getRepresentation(response, variant);
      }
      else {
        ConceptTemplate[] template = getStore().getArray();
        Response response = new Response(true, template);
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
  protected void describeGet(MethodInfo info) {

    // -> Global method info
    info.setIdentifier("get_template");
    info.setDocumentation("Get a single template from its ID");

    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("templateId", true, "xs:string", ParameterStyle.TEMPLATE, "Identifier of the template to work with.");
    info.getRequest().getParameters().add(param);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);

  }

  /**
   * Update existing template
   * 
   * @param representation
   *          input
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateConceptTemplate(Representation representation, Variant variant) {
    ConceptTemplate templateOutput = null;
    try {
      if (representation != null) {
        // Parse object representation
        ConceptTemplate templateInput = getObject(representation, variant);
        ConceptTemplate oldConceptTemplate = getStore().retrieve(templateInput.getId());
        
        // Business service
        templateOutput = getStore().update(templateInput);
        
        Map<String, ConceptTemplate> map = new HashMap<String, ConceptTemplate>();
        map.put("oldConceptTemplate", oldConceptTemplate);
        map.put("newConceptTemplate", templateInput);
        
        Notification notification = new Notification();
        notification.setObservable(getConceptTemplateId());
        notification.setEvent("TEMPLATE_UPDATED");
        notification.setMessage("template.delete.success");
        notification.setEventSource(map);
        
        getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);
      }

      if (templateOutput != null) {
        // Response
        Response response = new Response(true, templateOutput, ConceptTemplate.class, "template");
        return getRepresentation(response, variant);

      }
      else {
        // Response
        Response response = new Response(false, "Can not validate template");
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
  protected void describePut(MethodInfo info) {

    // -> Global method info
    info.setIdentifier("modify_template");
    info.setDocumentation("Method to modify an existing template.");

    // -> Response info

    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo param = new ParameterInfo("templateId", true, "xs:string", ParameterStyle.TEMPLATE, "Identifier of the template to work with.");
    info.getRequest().getParameters().add(param);
    this.addStandardResponseInfo(info);

    // Failures
    this.addStandardInternalServerErrorInfo(info);

  }

  /**
   * Delete template
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteConceptTemplate(Variant variant) {
    try {
      ConceptTemplate templateToDelete = getStore().retrieve(getConceptTemplateId());
      // Business service
      getStore().delete(getConceptTemplateId());

      // Response
      Response response = new Response(true, "template.delete.success");
      
      // Notify observers
      Notification notification = new Notification();
      notification.setObservable(getConceptTemplateId());
      notification.setEvent("TEMPLATE_DELETED");
      notification.setMessage("template.delete.success");
      notification.setEventSource(templateToDelete);
      getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);
      
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
  
  @Override
  protected void describeDelete(MethodInfo info) {

    // -> Global method info
    info.setIdentifier("delete_template");
    info.setDocumentation("Method to delete an existing template.");

    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("templateId", true, "xs:string", ParameterStyle.TEMPLATE, "Identifier of the template to work with.");
    info.getRequest().getParameters().add(param);
    this.addStandardSimpleResponseInfo(info);
    
    // Failures
    this.addStandardInternalServerErrorInfo(info);

  }
  
}
