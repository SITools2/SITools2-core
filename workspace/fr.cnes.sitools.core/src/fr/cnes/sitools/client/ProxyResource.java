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
package fr.cnes.sitools.client;

import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.util.ClientResourceProxy;

/**
 * Proxy resource that calls an external Url. Be careful that this resource does not act as a true proxy, all headers
 * are not copied from the external request to the response.
 * 
 * 
 * @author m.gond
 */
public class ProxyResource extends SitoolsResource {
  /** The external URL to access */
  private String externalUrl;

  @Override
  public void sitoolsDescribe() {
    setName(this.getClass().getName());
    setDescription("Proxy resource");
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.common.SitoolsResource#doInit()
   */
  @Override
  protected void doInit() {
    super.doInit();
    externalUrl = getRequest().getResourceRef().getQueryAsForm().getFirstValue("external_url");
    if (externalUrl == null || "".equals(externalUrl)) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "external_url parameter is mandatory");
    }

  }

  @Get
  @Override
  public Representation get() {
    ClientResourceProxy clientResourceProxy = new ClientResourceProxy(externalUrl, Method.GET);
    ClientResource clientResource = clientResourceProxy.getClientResource();
    clientResource.setRetryOnError(false);
    Representation repr = clientResource.handle();
    setStatus(clientResource.getResponse().getStatus());
    getResponse().getAttributes().put(ContextAttributes.NO_STATUS_SERVICE, true);
    return repr;
  }

  @Override
  protected void describeGet(MethodInfo info) {
    info.setDocumentation("Retrieve the content located at the given url and send it back");
    info.setIdentifier("retrieve_get");
    addStandardGetRequestInfo(info);
    ParameterInfo paramInfo = new ParameterInfo();
    paramInfo.setDocumentation("The external URL to access");
    paramInfo.setName("external_url");
    paramInfo.setType("xs:string");
    paramInfo.setStyle(ParameterStyle.TEMPLATE);
    paramInfo.setRequired(true);
    info.getRequest().getParameters().add(paramInfo);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
    addStandardResourceCollectionFilterInfo(info);
  }

}
