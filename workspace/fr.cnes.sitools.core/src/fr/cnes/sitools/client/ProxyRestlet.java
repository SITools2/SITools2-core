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
package fr.cnes.sitools.client;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.ResourceInfo;
import org.restlet.ext.wadl.WadlDescribable;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Redirector;

import fr.cnes.sitools.proxy.RedirectorProxy;

/**
 * Proxy resource that calls an external Url. Be careful that this resource does not act as a true proxy, all headers
 * are not copied from the external request to the response.
 * 
 * 
 * @author m.gond
 */
public class ProxyRestlet extends Restlet implements WadlDescribable {
  /**
   * Name of the rewrite_redirection parameter
   * */
  private static final String REWRITE_REDIRECTION_PARAM_NAME = "rewrite_redirection";

  /**
   * Name of the parameter containing the url
   */
  private static final String EXTERNAL_URL_PARAM_NAME = "external_url";

  /**
   * Default constructor
   */
  public ProxyRestlet() {
    super();
  }

  /**
   * Constructor with {@link Context}
   * 
   * @param context
   *          the {@link Context}
   */
  public ProxyRestlet(Context context) {
    super(context);
  }

  @Override
  public void handle(Request request, Response response) {

    Form query = request.getResourceRef().getQueryAsForm();
    String externalUrl = query.getFirstValue(EXTERNAL_URL_PARAM_NAME);
    if (externalUrl == null || "".equals(externalUrl)) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "external_url parameter is mandatory");
    }

    Redirector redirector = new RedirectorProxy(getContext(), externalUrl, Redirector.MODE_SERVER_OUTBOUND);

    redirector.handle(request, response);

    boolean rewriteRedirection = Boolean.parseBoolean(query.getFirstValue(REWRITE_REDIRECTION_PARAM_NAME, "false"));

    if (rewriteRedirection && response.getStatus().isRedirection()) {
      Reference refReturned = new Reference(request.getResourceRef().getBaseRef());
      refReturned.addQueryParameter(EXTERNAL_URL_PARAM_NAME, response.getLocationRef().toString());
      response.setLocationRef(refReturned);
    }

  }

  @Override
  public ResourceInfo getResourceInfo(ApplicationInfo applicationInfo) {
    ResourceInfo resourceInfo = new ResourceInfo();
    return resourceInfo;
  }

  /**
   * WADL describe method
   * 
   * @param resource
   *          the ResourceInfo
   */
  public void describe(ResourceInfo resource) {
    setName("ProxyRestlet");
    setDescription("Simple proxy Restlet");
  }

}
