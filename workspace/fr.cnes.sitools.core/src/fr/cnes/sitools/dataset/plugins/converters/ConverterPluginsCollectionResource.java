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
package fr.cnes.sitools.dataset.plugins.converters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.converter.business.AbstractConverter;
import fr.cnes.sitools.dataset.plugins.converters.model.ConverterPluginsDescriptionDTO;
import fr.cnes.sitools.engine.SitoolsEngine;

/**
 * Converters plug-in resource class.
 * 
 * @author AKKA
 * 
 */
public final class ConverterPluginsCollectionResource extends SitoolsResource {

  @Override
  public void sitoolsDescribe() {
    setName("ConverterCollectionResource");
    setDescription("Expose the list of available converters on the server");
  }

  /**
   * GET request
   * 
   * @param variant
   *          a RESTlet representation {@code Variant}
   * @return a RESTlet representation
   */
  @Get
  public Representation getConverters(Variant variant) {

    Response response;
    SitoolsEngine sitoolsEng = SitoolsEngine.getInstance();
    List<AbstractConverter> listConverters = sitoolsEng.getDatasetConverters();

    if (listConverters != null && listConverters.size() > 0) {
      ArrayList<ConverterPluginsDescriptionDTO> listDesc = new ArrayList<ConverterPluginsDescriptionDTO>();
      ConverterPluginsDescriptionDTO currentDescription;
      AbstractConverter currentConverter;
      for (Iterator<AbstractConverter> it = listConverters.iterator(); it.hasNext();) {
        currentConverter = it.next();
        currentDescription = new ConverterPluginsDescriptionDTO();
        currentDescription.setName(currentConverter.getName());
        currentDescription.setDescription(currentConverter.getDescription());
        currentDescription.setClassName(currentConverter.getClass().getCanonicalName());
        currentDescription.setClassAuthor(currentConverter.getClassAuthor());
        currentDescription.setClassOwner(currentConverter.getClassOwner());
        currentDescription.setClassVersion(currentConverter.getClassVersion());

        listDesc.add(currentDescription);
      }
      response = new Response(true, listDesc, ConverterPluginsDescriptionDTO.class, "converters");
      trace(Level.FINE, "View available dataset converters");
    }

    else {
      response = new Response(false, "NO_CONVERTERS_FOUND");
    }
    return getRepresentation(response, variant);

  }

  /**
   * GET method description
   * 
   * @param info
   *          WADL method information
   */
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of available converters in Sitools2.");
    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
  }

  /**
   * Response to Representation
   * 
   * @param response
   *          a JSON/XML answer
   * @param media
   *          the media type (JSON/XML)
   * @return Representation the RESTlet version of the response
   */
  public Representation getRepresentation(Response response, MediaType media) {

    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    xstream.alias("response", Response.class);
    xstream.alias("converterDescription", ConverterPluginsDescriptionDTO.class);

    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");

    // xstream.addImplicitCollection(Response.class, "data",Group.class);
    // pour supprimer @class sur data
    if (response.getItemClass() != null) {
      xstream.alias("item", Object.class, response.getItemClass());
    }
    if (response.getItemName() != null) {
      xstream.aliasField(response.getItemName(), Response.class, "item");
    }

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

}
