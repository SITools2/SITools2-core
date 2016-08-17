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
package fr.cnes.sitools.plugins.applications;

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
import fr.cnes.sitools.engine.SitoolsEngine;
import fr.cnes.sitools.plugins.applications.business.AbstractApplicationPlugin;
import fr.cnes.sitools.plugins.applications.dto.ApplicationPluginDescriptionDTO;

/**
 * Class for SVA plug-in resources
 * 
 * @author AKKA
 * 
 */
public final class ApplicationPluginListingCollectionResource extends SitoolsResource {

  @Override
  public void sitoolsDescribe() {
    setName("ApplicationPluginListingCollectionResource");
    setDescription("Gives the list of available application plug-ins on the server.");
  }

  /**
   * GET request
   * 
   * @param variant
   *          a restlet representation {@code Variant}
   * @return a restlet representation
   */
  @Get
  public Representation getAppPlugins(Variant variant) {

    Response response;
    SitoolsEngine sitoolsEng = SitoolsEngine.getInstance();
    List<AbstractApplicationPlugin> listSvas = sitoolsEng.getRegisteredApplicationPlugins();

    if (listSvas != null && listSvas.size() > 0) {
      ArrayList<ApplicationPluginDescriptionDTO> listDesc = new ArrayList<ApplicationPluginDescriptionDTO>();
      ApplicationPluginDescriptionDTO currentDescription;
      AbstractApplicationPlugin currentApp;
      for (Iterator<AbstractApplicationPlugin> it = listSvas.iterator(); it.hasNext();) {
        currentApp = it.next();
        currentDescription = new ApplicationPluginDescriptionDTO();
        currentDescription.setName(currentApp.getName());
        currentDescription.setDescription(currentApp.getDescription());
        currentDescription.setClassName(currentApp.getClass().getCanonicalName());
        currentDescription.setClassAuthor(currentApp.getAuthor());
        currentDescription.setClassVersion(currentApp.getModel().getClassVersion());
        currentDescription.setClassOwner(currentApp.getModel().getClassOwner());

        listDesc.add(currentDescription);
      }
      response = new Response(true, listDesc, ApplicationPluginDescriptionDTO.class, "applications");
    }

    else {
      response = new Response(false, "NO_APPS_FOUND");
    }
    return getRepresentation(response, variant);
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Gives the list of available Application plug-ins on the server.");
    info.setIdentifier("AppPluginList");

    // Request
    this.addStandardGetRequestInfo(info);

    // Response 200
    this.addStandardResponseInfo(info);
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
    configure(xstream, response);
    xstream.omitField(ApplicationPluginDescriptionDTO.class, "model");

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

}
