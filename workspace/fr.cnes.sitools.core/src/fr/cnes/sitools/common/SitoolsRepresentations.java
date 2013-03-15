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
package fr.cnes.sitools.common;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.model.Response;

/**
 * Class to produce common representation of a standard and generic SITools Response.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class SitoolsRepresentations {

  /**
   * Private constructor for utility class
   */
  private SitoolsRepresentations() {
    super();
  }

  /**
   * Gets the Response Representation (XML / JSON) according to the given variant
   * 
   * @param response
   *          Response
   * @param variant
   *          RESTlet MediaType
   * @return Representation (XStreamRepresentation)
   */
  public static Representation getRepresentation(Response response, Variant variant, Context context) {
    if (variant.getMediaType().isCompatible(MediaType.APPLICATION_XML)
        || variant.getMediaType().isCompatible(MediaType.APPLICATION_JSON)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(variant.getMediaType(), context);
      configure(xstream, response);
      XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(variant.getMediaType(), response);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
    }
  }

  /**
   * XStream configuration depending on response content.
   * 
   * @param xstream
   *          the stream to configure
   * @param response
   *          the response to use for configure
   */
  public static void configure(XStream xstream, Response response) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);

    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");

    // Array(List) of String
    if ((response.getData() != null) && (response.getItemClass().equals(String.class))) {
      if (response.getItemName() != null) {
        xstream.aliasField(response.getItemName(), Response.class, "data");
      }
      return;
    }

    // Other cases ...
    if ((response.getItemClass() != null) && (response.getData() != null)) {
      xstream.addImplicitCollection(Response.class, "data", "data", response.getItemClass());
    }

    // pour supprimer @class sur data
    if ((response.getData() != null) && (response.getItemClass() != null)) {
      xstream.alias("item", Object.class, response.getItemClass());
    }

    if (response.getData() != null) {
      // normalement il est null
      xstream.omitField(Response.class, "item");

      if (response.getItemName() != null) {
        xstream.aliasField(response.getItemName(), Response.class, "item");
      }
    }
  }

}
