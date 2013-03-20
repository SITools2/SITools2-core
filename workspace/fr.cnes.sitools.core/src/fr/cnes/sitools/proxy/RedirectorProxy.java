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
package fr.cnes.sitools.proxy;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.ResourceInfo;
import org.restlet.ext.wadl.WadlDescribable;
import org.restlet.representation.Representation;
import org.restlet.routing.Redirector;

/**
 * Proxy redirection if set in ProxySettings.
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class RedirectorProxy extends Redirector implements WadlDescribable {

  /**
   * Constructor
   * 
   * @param context
   *          restlet context
   * @param targetPattern
   *          target pattern
   * @param mode
   *          mode
   */
  public RedirectorProxy(Context context, String targetPattern, int mode) {
    super(context, targetPattern, mode);
  }

  /**
   * Constructor
   * 
   * @param context
   *          restlet context
   * @param targetTemplate
   *          targetTemplate
   */
  public RedirectorProxy(Context context, String targetTemplate) {
    super(context, targetTemplate);
  }

  @Override
  public void handle(Request request, Response response) {

    if ((ProxySettings.getProxyAuthentication() != null) && request.getProxyChallengeResponse() == null) {
      request.setProxyChallengeResponse(ProxySettings.getProxyAuthentication());
    }

    try {
      super.handle(request, response);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Optionally rewrites the response entity returned in the {@link #MODE_SERVER_INBOUND} and
   * {@link #MODE_SERVER_OUTBOUND} modes. By default, it just returns the initial entity without any modification.
   * 
   * @param initialEntity
   *          The initial entity returned.
   * @return The rewritten entity.
   */
  protected Representation rewrite(Representation initialEntity) {
    // initialEntity can be gzip etc ...
    return initialEntity;
  }

  @Override
  public ResourceInfo getResourceInfo(ApplicationInfo applicationInfo) {
    return new ResourceInfo();
  }

}
