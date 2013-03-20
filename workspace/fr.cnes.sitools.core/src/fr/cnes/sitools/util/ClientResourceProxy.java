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
package fr.cnes.sitools.util;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.resource.ClientProxy;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.proxy.ProxySettings;

/**
 * ClientResourceProxy based on ProxySettings
 * 
 * @author Jean-Christophe Malapert
 */
public class ClientResourceProxy implements ClientProxy {

  /** The method of the clientResource */
  private Method method;
  /** The reference */
  private Reference reference;

  /**
   * Constructor with url and Method
   * 
   * @param url
   *          the url
   * @param method
   *          the Method
   */
  public ClientResourceProxy(String url, Method method) {
    this.method = method;
    this.reference = new Reference(url);
  }

  /**
   * Constructor with Reference and Method
   * 
   * @param reference
   *          the Reference
   * @param method
   *          the Method
   */
  public ClientResourceProxy(Reference reference, Method method) {
    this.method = method;
    this.reference = reference;
  }

  @Override
  public ClientResource getClientResource() {
    Request request = new Request(method, reference);
    if ((ProxySettings.getProxyAuthentication() != null) && request.getProxyChallengeResponse() == null) {
      request.setProxyChallengeResponse(ProxySettings.getProxyAuthentication());
    }
    Response response = new Response(request);
    ClientResource client = new ClientResource(request, response);
    return client;
  }
}
