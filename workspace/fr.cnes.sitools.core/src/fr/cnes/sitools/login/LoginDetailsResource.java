/*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.login;

import java.util.ArrayList;
import java.util.List;

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.Property;

/**
 * Resource just for return a standard response when authentication succeed This resource can be attached in each
 * application to return user roles...
 * 
 * @author AKKA Technologies
 * 
 */
public final class LoginDetailsResource extends SitoolsResource {

  @Override
  public void sitoolsDescribe() {
    setName("LoginDetailsResource");
    setDescription("Resource to get the login details");
  }

  /**
   * Login tentative
   * 
   * @param variant
   *          client preference for response media type
   * @return Representation if success
   */
  @Get
  public Representation login(Variant variant) {

    SitoolsSettings settings = getSettings();

    List<Property> list = new ArrayList<Property>();
    String delegateLogin = settings.getString(Consts.SECURITY_DELEGATE_LOGIN, "false");
    String delegateLogout = settings.getString(Consts.SECURITY_DELEGATE_LOGOUT, "false");
    String delegateUserManagment = settings.getString(Consts.SECURITY_DELEGATE_USER_MANAGMENT, "false");
    list.add(new Property(Consts.SECURITY_DELEGATE_LOGIN, delegateLogin, null));
    list.add(new Property(Consts.SECURITY_DELEGATE_LOGIN_URL, settings.getString(Consts.SECURITY_DELEGATE_LOGIN_URL),
        null));
    list.add(new Property(Consts.SECURITY_DELEGATE_LOGOUT, delegateLogout, null));
    list.add(new Property(Consts.SECURITY_DELEGATE_LOGOUT_URL, settings.getString(Consts.SECURITY_DELEGATE_LOGOUT_URL),
        null));
    list.add(new Property(Consts.SECURITY_DELEGATE_USER_MANAGMENT, delegateUserManagment, null));
    list.add(new Property(Consts.SECURITY_DELEGATE_USER_MANAGMENT_URL, settings
        .getString(Consts.SECURITY_DELEGATE_USER_MANAGMENT_URL), null));

    Response response = new Response(true, list, Property.class, "property");

    return getRepresentation(response, variant);
  }

  /**
   * Describe the GET method
   * 
   * @param info
   *          WADL method info
   */
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get information about the Login process.");
    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
  }

}
