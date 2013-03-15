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
package fr.cnes.sitools.security.authorization.business;

import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.security.MethodAuthorizer;
import org.restlet.service.TunnelService;

/**
 * Method authorizer taking into account of tunneling method
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class SitoolsMethodAuthorizer extends MethodAuthorizer {

  /** needed for getting TunnelService because security restlet handle is executed before application handler */
  private Application application;

  /**
   * Constructor with Application parent
   * @param application Application parent of the authorizer (can be null)
   */
  public SitoolsMethodAuthorizer(Application application) {
    super();
    this.application = application;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.security.MethodAuthorizer#authorize(org.restlet.Request, org.restlet.Response)
   */
  @Override
  public boolean authorize(Request request, Response response) {
    boolean authorized = false;

    Method method = request.getMethod();
     
    if (application != null) {
      TunnelService tunnelService = getApplication().getTunnelService();

      if ((null != tunnelService) && tunnelService.isMethodTunnel() && (null != request.getResourceRef())) {
        final String methodName = request.getResourceRef().getQueryAsForm()
            .getFirstValue(tunnelService.getMethodParameter());
        Method tunnelledMethod = Method.valueOf(methodName);
        if (null != tunnelledMethod) {
          if (Method.OPTIONS.equals(tunnelledMethod) && method.equals(Method.GET)) {
            method = Method.OPTIONS;
          }
          else if (Method.PUT.equals(tunnelledMethod) && method.equals(Method.POST)) {
            method = Method.PUT;
          }
          else if (Method.DELETE.equals(tunnelledMethod) && method.equals(Method.POST)) {
            method = Method.DELETE;
          }
        }

      }
    }

    if (request.getClientInfo().isAuthenticated()) {
      // Verify if the request method is one of the forbidden methods
      for (Method authenticatedMethod : getAuthenticatedMethods()) {
        authorized = authorized || method.equals(authenticatedMethod);
      }
    }
    else {
      // Verify if the request method is one of the authorized methods
      for (Method authorizedMethod : getAnonymousMethods()) {
        authorized = authorized || method.equals(authorizedMethod);
      }
    }

    return authorized;
  }

  @Override
  public Application getApplication() {
    if (application == null) {
      return super.getApplication();
    }
    return application;
  };

}
