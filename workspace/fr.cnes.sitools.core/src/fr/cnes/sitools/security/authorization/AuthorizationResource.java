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
package fr.cnes.sitools.security.authorization;

import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.security.authorization.client.ResourceAuthorization;

/**
 * Resource for managing (GET/PUT/DELETE) single ResourceAuthorization.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class AuthorizationResource extends AbstractAuthorizationResource {

  @Override
  public void sitoolsDescribe() {
    setName("AuthorizationResource");
    setDescription("Resource for managing authorizations on identified resources ");
    setNegotiated(false);
  }

  /**
   * Gets an authorization by ID
   * 
   * @param variant
   *          client preferred media type
   * @return Response Representation of ResourceAuthorization(s)
   */
  @Get
  public Representation retrieveResourceAuthorization(Variant variant) {
    try {
      if (getResId() != null) {
        ResourceAuthorization authorization = getStore().retrieve(getResId());
        Response response = null;
        if (authorization != null) {
          response = new Response(true, authorization, ResourceAuthorization.class, "authorization");
        }
        else {
          response = new Response(false, "Authorization " + getResId() + " not found");
        }
        return getRepresentation(response, variant);
      }
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
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
    info.setDocumentation("Method to get the authorization by ID.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramResId = new ParameterInfo("resId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the resource.");
    info.getRequest().getParameters().add(paramResId);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Update / Validate existing authorization
   * 
   * @param representation
   *          ResourceAuthorization Representation
   * @param variant
   *          Variant client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateResourceAuthorization(Representation representation, Variant variant) {
    ResourceAuthorization authorizationOutput = null;
    try {
      ResourceAuthorization authorizationInput = null;
      if (representation != null) {
        if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
          // Parse the XML representation to get the bean
          authorizationInput = new XstreamRepresentation<ResourceAuthorization>(representation).getObject();

        }
        else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {

          authorizationInput = new JacksonRepresentation<ResourceAuthorization>(representation,
              ResourceAuthorization.class).getObject();
        }
        else if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
          @SuppressWarnings("unchecked")
          ObjectRepresentation<ResourceAuthorization> obj = (ObjectRepresentation<ResourceAuthorization>) representation;
          authorizationInput = obj.getObject();
        }

        // Business service
        if (authorizationInput.getAuthorizations() == null) {

          // on doit supprimer toute la definition de l'authorization pour cette application
          boolean deleted = getStore().delete(authorizationInput.getId());

          if (deleted) {
            // Notify observers
            Notification notification = new Notification();
            notification.setEvent("AUTHORIZATION_DELETED");
            notification.setObservable(authorizationInput.getId());
            getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);
          }
          authorizationOutput = authorizationInput;

        }
        else {
          authorizationOutput = getStore().update(authorizationInput);

          // Notify observers
          Notification notification = new Notification();

          // le client fait toujours un PUT (donc possible que l'update est echoue si inexistant)
          if (authorizationOutput == null) {
            authorizationOutput = getStore().create(authorizationInput);
            notification.setEvent("AUTHORIZATION_CREATED");
          }
          else {
            notification.setEvent("AUTHORIZATION_UPDATED");
          }

          notification.setObservable(authorizationOutput.getId());
          getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);
        }
      }

      if (authorizationOutput != null) {
        // Response

        Response response = new Response(true, authorizationOutput, ResourceAuthorization.class, "authorization");
        return getRepresentation(response, variant);
      }
      else {
        // Response
        Response response = new Response(false, "Can not validate authorization");
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
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify the authorization sending its new representation.");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Delete authorization
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteResourceAuthorization(Variant variant) {
    try {
      // Business service
      boolean success = getStore().delete(getResId());

      // Response
      Response response = null;
      if (success) {
        response = new Response(true, "ResourceAuthorization deleted");
      }
      else {
        response = new Response(false, "Failed to delete authorization " + getResId());
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

  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete the authorization by ID.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramResId = new ParameterInfo("resId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the resource.");
    info.getRequest().getParameters().add(paramResId);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
