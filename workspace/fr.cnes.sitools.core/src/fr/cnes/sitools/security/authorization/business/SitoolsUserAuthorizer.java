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
package fr.cnes.sitools.security.authorization.business;

import java.util.List;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.security.DelegatedAuthorizer;

import fr.cnes.sitools.security.SecurityUtil;

/**
 * Specific authorizer for user where identifier is a request attribute passed to the constructor.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class SitoolsUserAuthorizer extends DelegatedAuthorizer {

  /** serialVersionUID */
  private static final long serialVersionUID = -9072105456458512655L;

  /** user identifier attribute in request */
  private String attribute;
  /** list of methods allowed for public user */
  private List<Method> methods;

  /**
   * Constructor Authorizer encapsulation
   * 
   * @param attribute
   *          user identifier attribute in request
   */
  public SitoolsUserAuthorizer(String attribute) {
    this.attribute = attribute;
    methods = null;
  }

  /**
   * Constructor Authorizer encapsulation
   * 
   * @param attribute
   *          user identifier attribute in request
   * @param methods
   *          the list of Method to allow for public user
   */
  public SitoolsUserAuthorizer(String attribute, List<Method> methods) {
    this.attribute = attribute;
    this.methods = methods;
  }

  /**
   * Constructor
   */
  public SitoolsUserAuthorizer() {
    super();
  }

  @Override
  public boolean authorize(Request request, Response response) {
    if (request.getProtocol().getSchemeName().startsWith("riap")) {
      return true;
    }
    if (SecurityUtil.PUBLIC_USER.equals(request.getAttributes().get(attribute))
        && (methods != null && methods.contains(request.getMethod()))) {
      return true;
    }
    return ((request.getClientInfo() != null) && request.getClientInfo().isAuthenticated() && request.getClientInfo()
        .getUser().getIdentifier().equals(request.getAttributes().get(attribute)));
  }
}
