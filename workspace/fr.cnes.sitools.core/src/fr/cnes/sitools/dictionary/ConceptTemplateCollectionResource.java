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
package fr.cnes.sitools.dictionary;

import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ResponseInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dictionary.model.ConceptTemplate;

/**
 * Resource for concept template collection
 * 
 * @author jp.boignard (AKKA technologies)
 */
public class ConceptTemplateCollectionResource extends AbstractConceptTemplateResource {

  @Override
  public void sitoolsDescribe() {
    setName("ConceptTemplateCollectionResource");
    setDescription("Resource for managing the collection of templates of concepts");
  }

  /**
   * Create a new template
   * 
   * @param representation
   *          input
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newTemplate(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "template_REPRESENTATION_REQUIRED");
    }
    try {
      // Parse object representation
      ConceptTemplate input = getObject(representation, variant);

      // Business service
      ConceptTemplate output = getStore().create(input);

      // Response
      trace(Level.INFO, "Create the dictionary structure " + output.getName());
      Response response = new Response(true, output, ConceptTemplate.class, "template");
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot create the dictionary structure");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot create the dictionary structure");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  protected void describePost(MethodInfo info) {

    // -> Global method info
    info.setIdentifier("create_template");
    info.setDocumentation("To create a new template.");

    // -> Response info

    // Requests
    this.addStandardPostOrPutRequestInfo(info);

    // Successful responses
    this.addStandardResponseInfo(info);

    // Failures
    ResponseInfo responseInfo = new ResponseInfo();
    responseInfo.getStatuses().add(Status.CLIENT_ERROR_BAD_REQUEST);
    responseInfo.setDocumentation("template representation required");
    info.getResponses().add(responseInfo);

    responseInfo = new ResponseInfo();
    responseInfo.getStatuses().add(Status.SERVER_ERROR_INTERNAL);
    responseInfo.setDocumentation("Server internal error occurred");
    info.getResponses().add(responseInfo);

  }

  /**
   * get all template
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveTemplate(Variant variant) {
    try {
      if (getConceptTemplateId() != null) {
        ConceptTemplate template = getStore().retrieve(getConceptTemplateId());
        trace(Level.FINE, "Edit information for the dictionary structure " + template.getName());
        Response response = new Response(true, template, ConceptTemplate.class, "template");
        return getRepresentation(response, variant);
      }
      else {
        ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
        List<ConceptTemplate> templates = getStore().getList(filter);
        int total = templates.size();
        templates = getStore().getPage(filter, templates);
        trace(Level.FINE, "View available dictionary structures");
        Response response = new Response(true, templates, ConceptTemplate.class, "templates");
        response.setTotal(total);
        return getRepresentation(response, variant);
      }
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot view available dictionary structures");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot view available dictionary structures");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  protected void describeGet(MethodInfo info) {

    // -> Global method info
    info.setIdentifier("get_templates");
    info.setDocumentation("Get the list of templates.");

    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
    this.addStandardResourceCollectionFilterInfo(info);

  }

}