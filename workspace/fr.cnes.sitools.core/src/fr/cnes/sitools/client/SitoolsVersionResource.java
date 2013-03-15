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
package fr.cnes.sitools.client;

import org.restlet.data.MediaType;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;

/**
 * Get the current version of Sitools
 * 
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class SitoolsVersionResource extends SitoolsResource {

  @Override
  public void sitoolsDescribe() {
    setName("SitoolsVersionResource");
    setDescription("Gets the Current version of Sitools");
    
  }

  /**
   * Returns the version of Sitools
   * 
   * @param variant
   *          the variant needed
   * @return a representation of the Sitools version
   */
  @Get
  protected Representation get(Variant variant) {
    String version = getSitoolsSetting("Starter.VERSION");

    Response resp = new Response(true, version, String.class, "version");
    

    return getRepresentation(resp, variant);
  }

  /**
   * Gets representation according to the specified MediaType.
   * 
   * @param response
   *          : The response to get the representation from
   * @param media
   *          : The MediaType asked
   * @return The Representation of the response with the selected mediaType
   */
  public Representation getRepresentation(Response response, MediaType media) {
    getLogger().info(media.toString());

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    
    
    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }
  
  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the current version of SITools2");
    info.setIdentifier("retrieve_version");
    addStandardGetRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

}
