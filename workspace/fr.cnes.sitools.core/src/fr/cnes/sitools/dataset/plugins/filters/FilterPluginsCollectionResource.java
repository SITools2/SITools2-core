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
package fr.cnes.sitools.dataset.plugins.filters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import fr.cnes.sitools.dataset.filter.business.AbstractFilter;
import fr.cnes.sitools.dataset.plugins.filters.model.FilterPluginsDescriptionDTO;
import fr.cnes.sitools.engine.SitoolsEngine;

/**
 * Class for filters plug-in resources
 * 
 * @author AKKA
 * 
 */
public final class FilterPluginsCollectionResource extends SitoolsResource {

  @Override
  public void sitoolsDescribe() {
    setName("FilterCollectionResource");
    setDescription("Show the list of filters available on the server.");
    setNegotiated(false);
  }

  /**
   * GET request
   * 
   * @param variant
   *          a restlet representation variant
   * @return a restlet representation
   */
  @Get
  public Representation getFilters(Variant variant) {

    Response response;
    SitoolsEngine sitoolsEng = SitoolsEngine.getInstance();
    List<AbstractFilter> listFilters = sitoolsEng.getDatasetFilters();

    if (listFilters != null && listFilters.size() > 0) {
      ArrayList<FilterPluginsDescriptionDTO> listDesc = new ArrayList<FilterPluginsDescriptionDTO>();
      FilterPluginsDescriptionDTO currentDescription;
      AbstractFilter currentFilter;
      for (Iterator<AbstractFilter> it = listFilters.iterator(); it.hasNext();) {
        currentFilter = it.next();
        currentDescription = new FilterPluginsDescriptionDTO();
        currentDescription.setName(currentFilter.getName());
        currentDescription.setDescription(currentFilter.getDescription());
        currentDescription.setClassName(currentFilter.getClass().getCanonicalName());
        currentDescription.setClassAuthor(currentFilter.getClassAuthor());
        currentDescription.setClassVersion(currentFilter.getClassVersion());
        currentDescription.setClassOwner(currentFilter.getClassOwner());
        currentDescription.setDefaultFilter(currentFilter.getDefaultFilter());

        listDesc.add(currentDescription);
      }
      response = new Response(true, listDesc, FilterPluginsDescriptionDTO.class, "filters");
    }

    else {
      response = new Response(false, "NO_FILTERS_FOUND");
    }
    return getRepresentation(response, variant);

  }
  
  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Get the list of available filters");
    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
  }

  /**
   * Gets representation according to the specified Variant if present. If
   * variant is null (when content negociation = false) sets the variant to the
   * first client accepted mediaType.
   * 
   * @param response
   *          json or xml answer
   * @param variant
   *          restlet variant
   * @return Representation
   */
  public Representation getRepresentation(Response response, Variant variant) {
    MediaType defaultMediaType = null;
    if (variant == null) {
      if (this.getRequest().getClientInfo().getAcceptedMediaTypes().size() > 0) {
        MediaType first = this.getRequest().getClientInfo().getAcceptedMediaTypes().get(0).getMetadata();
        if (first.isConcrete() && (first.isCompatible(MediaType.APPLICATION_JAVA_OBJECT))) {
          defaultMediaType = first;
        }
      }
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
   * Response to Representation
   * 
   * @param response
   *          a json/xml answer
   * @param media
   *          the media type (json/xml)
   * @return Representation the restlet version of the response
   */
  public Representation getRepresentation(Response response, MediaType media) {

    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    xstream.alias("response", Response.class);
    xstream.alias("filterDescription", FilterPluginsDescriptionDTO.class);

    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");

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
