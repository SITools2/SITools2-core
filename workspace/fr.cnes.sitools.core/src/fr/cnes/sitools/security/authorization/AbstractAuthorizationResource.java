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

import java.io.IOException;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.security.authorization.client.ResourceAuthorization;
import fr.cnes.sitools.security.authorization.client.RoleAndMethodsAuthorization;

/**
 * Abstract Resource class for Authorizations management
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AbstractAuthorizationResource extends SitoolsResource {

  /** parent application */
  private AuthorizationApplication application = null;

  /** store */
  private AuthorizationStore store = null;

  /** project identifier parameter */
  private String resId = null;

  /**
   * Default constructor
   */
  public AbstractAuthorizationResource() {
    super();
  }

  @Override
  public void doInit() {
    super.doInit();

    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));

    application = (AuthorizationApplication) getApplication();
    store = application.getStore();

    resId = (String) this.getRequest().getAttributes().get("resId");
  }

  /**
   * XStream configuration for Response object mapping
   * 
   * @param xstream
   *          XStream
   * @param response
   *          Response
   */
  public final void configure(XStream xstream, Response response) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);
    xstream.alias("resourceAuthorization", ResourceAuthorization.class);
    xstream.alias("authorize", RoleAndMethodsAuthorization.class);

    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");

    // pour supprimer @class sur data
    if (response.getItemClass() != null) {
      xstream.alias("item", Object.class, response.getItemClass());
    }
    if (response.getItemName() != null) {
      xstream.aliasField(response.getItemName(), Response.class, "item");
    }
  }

  /**
   * Get the resource ID
   * 
   * @return the resource ID
   */
  public final String getResId() {
    return this.resId;
  }

  /**
   * Get the store associated to the application
   * 
   * @return the store associated
   */
  public final AuthorizationStore getStore() {
    return this.store;
  }

  /**
   * Get the application associated to the resource
   * 
   * @return the application associated
   */
  public final AuthorizationApplication getAuthorizationApplication() {
    return this.application;
  }

  /**
   * Get the ResourceAuthorization for the Representation
   * 
   * @param representation
   *          contains a ResourceAuthorization
   * @return the ResourceAuthorization for the Representation
   * @throws IOException
   *           if there is an error while parsing the representation
   */
  protected ResourceAuthorization getObject(Representation representation) throws IOException {
    ResourceAuthorization authorizationInput = null;
    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the bean
      authorizationInput = new XstreamRepresentation<ResourceAuthorization>(representation).getObject();

    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      authorizationInput = new JacksonRepresentation<ResourceAuthorization>(representation, ResourceAuthorization.class)
          .getObject();
    }

    else if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      @SuppressWarnings("unchecked")
      ObjectRepresentation<ResourceAuthorization> obj = (ObjectRepresentation<ResourceAuthorization>) representation;
      authorizationInput = obj.getObject();
    }
    return authorizationInput;
  }

}
