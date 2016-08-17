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
package fr.cnes.sitools.portal;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.persistence.PersistenceDao;
import fr.cnes.sitools.portal.model.Portal;

/**
 * Base class for portal management resources.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AbstractPortalResource extends SitoolsResource {

  /** Application */
  private PortalApplication application = null;
  
  /** Store */
  private PortalStoreInterface store = null;
  
  /** id in the request */
  private String portalId = "portail-sitools"; 

  @Override
  public void doInit() {
    super.doInit();

    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));

    application = (PortalApplication) getApplication();
    store = application.getStore();

  }

  /**
   * Gets representation according to the specified Variant if present. If
   * variant is null (when content negociation = false) sets the variant to the
   * first client accepted mediaType.
   * 
   * @param response
   *          : The response to get the representation from
   * @param variant
   *          : The variant needed
   * @return Representation
   */
  public final Representation getRepresentation(Response response, Variant variant) {
    MediaType defaultMediaType = null;
    if (variant == null) {
      if (this.getRequest().getClientInfo().getAcceptedMediaTypes().size() > 0) {
        MediaType first = this.getRequest().getClientInfo().getAcceptedMediaTypes().get(0).getMetadata();
        if (first.isConcrete() && (first.isCompatible(MediaType.APPLICATION_JAVA_OBJECT))) {
          defaultMediaType = first;
        }
      }
      // negociation de contenu (@see classe ServerResource.doNegotiatedHandle)
      if ((defaultMediaType == null) && (getVariants() != null) && (!getVariants().isEmpty())) {
        Variant preferredVariant = getClientInfo().getPreferredVariant(getVariants(), getMetadataService());
        defaultMediaType = preferredVariant.getMediaType();
      }
    }
    else {
      defaultMediaType = variant.getMediaType();
    }

    return getRepresentation(response, defaultMediaType);
  }

  /**
   * Configure XStream mapping for xml and json serialisation
   * 
   * @param xstream
   *          XStream
   * @param response
   *          Response
   */
  public final void configure(XStream xstream, Response response) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);
    xstream.alias("portal", Portal.class);

    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");

    if (response.getItemClass() != null) {
      xstream.alias("item", Object.class, response.getItemClass());
    }
    if (response.getItemName() != null) {
      xstream.aliasField(response.getItemName(), Response.class, "item");
    }
  }

  /**
   * Gets a representation according to the requested media
   * 
   * @param response
   *          Response
   * @param media
   *          requested MediaType
   * @return ObjectRepresentation or XStreamRepresentation
   */
  @Override
  public final Representation getRepresentation(Response response, MediaType media) {
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("portal", Portal.class);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Decode the given representation to a Portal object.
   * 
   * @param representation
   *          Representation
   * @param variant
   *          Variant
   * @return Portal
   */
  public final Portal getObject(Representation representation, Variant variant) {
    Portal portalInput = null;

    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      XstreamRepresentation<Portal> repXML = new XstreamRepresentation<Portal>(representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      xstream.autodetectAnnotations(false);
      xstream.alias("portal", Portal.class);
      repXML.setXstream(xstream);
      portalInput = repXML.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      portalInput = new JacksonRepresentation<Portal>(representation, Portal.class).getObject();
    }
    return portalInput;
  }
  
  /**
   * Get the portal ID
   * @return the portal ID as String
   */
  public final String getPortalId() {
    return this.portalId;
  }

  /**
   * Return the store associated to the Portal resource
   * @return the store
   */
  public final PortalStoreInterface getStore() {
    return this.store;
  }
  
  /**
   * Get the portal application associated to the resource
   * @return the portal application handling the resource
   */
  public final PortalApplication getPortalApplication() {
    return this.application;
  }
  
}
