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
package fr.cnes.sitools.security;

import java.util.ArrayList;
import java.util.List;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.applications.PublicApplication;
import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.role.model.Role;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.security.model.UserRole;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource for managing single User
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class FindRoleResource extends SitoolsResource {

  public void sitoolsDescribe() {
    setName("FindRoleResource");
    setDescription("Resource to get the Roles of a given user");
    setNegotiated(false);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.ServerResource#get(org.restlet.representation.Variant)
   */
  @Get
  @Override
  public Representation get(Variant variant) {

    Response response = getUserResponse();
    return getRepresentation(response, variant);
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get the list of users, by group if group ID is given.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo("group", false, "xs:string", ParameterStyle.TEMPLATE,
        "Group identifier.");
    info.getRequest().getParameters().add(paramUserId);
    this.addStandardObjectResponseInfo(info);
  }

  /**
   * Calls business layer
   * 
   * @return Sitools.model.Response
   */
  private Response getUserResponse() {
    Response response = null;
    try {
//      User user =  getClientInfo().getUser();
      PublicApplication application = (PublicApplication) this.getApplication();
      User user = RIAPUtils.getObject(getClientInfo().getUser().getIdentifier(), application.getSettings().getString(Consts.APP_SECURITY_URL) + "/users", getContext());
      
      if (user == null) {
        getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        response = new Response(false, "No user defined");
      }
      else {
        List<org.restlet.security.Role> roles = getClientInfo().getRoles();
        UserRole ur = new UserRole();
        ur.setEmail(user.getEmail());
        ur.setFirstName(user.getFirstName());
        ur.setIdentifier(user.getIdentifier());
        ur.setLastName(user.getLastName());
        // ur.setProperties(user.getProperties());
        ArrayList<Role> rolesOut = new ArrayList<Role>();
        for (org.restlet.security.Role role : roles) {
          Role sitoolsRole = new Role();
          sitoolsRole.setDescription(role.getDescription());
          sitoolsRole.setName(role.getName());
          rolesOut.add(sitoolsRole);
        }
        ur.setRoles(rolesOut);

        response = new Response(true, ur, UserRole.class, "user");
      }
    }
    catch (Exception e) {
      response = new Response(false, e.getMessage());
    }
    return response;
  }
}
