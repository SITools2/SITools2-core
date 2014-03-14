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
package fr.cnes.sitools.security.authorization;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.security.authorization.client.ResourceAuthorization;

/**
 * Class Resource for managing ResourceAuthorization Collection (GET, POST)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class AuthorizationCollectionResource extends AbstractAuthorizationResource {

  @Override
  public void sitoolsDescribe() {
    setName("ResourceAuthorizationCollectionResource");
    setDescription("Resource for managing authorization collection");
  }

  /**
   * Update / Validate existing authorization
   * 
   * @param representation
   *          ResourceAuthorization Representation
   * @param variant
   *          Variant client preferred media type
   * @return Response Representation of ResourceAuthorization
   */
  @Post
  public Representation newResourceAuthorization(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "AUTHORIZATION_REPRESENTATION_REQUIRED");
    }
    try {
      ResourceAuthorization authorizationInput = null;
      if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
        // Parse the XML representation to get the bean
        authorizationInput = new XstreamRepresentation<ResourceAuthorization>(representation).getObject();

      }
      else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
        // Parse the JSON representation to get the bean
        authorizationInput = new JacksonRepresentation<ResourceAuthorization>(representation,
            ResourceAuthorization.class).getObject();
      } 

      else if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
        @SuppressWarnings("unchecked")
        ObjectRepresentation<ResourceAuthorization> obj = (ObjectRepresentation<ResourceAuthorization>) representation;
        authorizationInput = obj.getObject();
      }     
      
      // Business service
      ResourceAuthorization authorizationOutput = getStore().create(authorizationInput);

      // Notify observers
      Notification notification = new Notification();
      notification.setEvent("AUTHORIZATION_CREATED");
      notification.setObservable(authorizationOutput.getId());
      getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);
       
      // Response

      Response response = new Response(true, authorizationOutput, ResourceAuthorization.class, "authorization");
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
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a new authorization.");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * get all objects
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveResourceAuthorization(Variant variant) {
    try {
      ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
      String type = this.getQuery().getFirstValue("type");
      List<ResourceAuthorization> auhorizations = new ArrayList<ResourceAuthorization>();
      if (type != null && !type.equals("")) {
        auhorizations = getStore().getListByType(filter, type);
      }
      else {
        auhorizations = getStore().getList(filter);
      }
      int total = auhorizations.size();
      auhorizations = getStore().getPage(filter, auhorizations);
      Response response = new Response(true, auhorizations, ResourceAuthorization.class, "authorizations");
      response.setTotal(total);
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
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get the list of all authorizations.");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
    this.addStandardResourceCollectionFilterInfo(info);
  }

}
