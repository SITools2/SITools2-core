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
package fr.cnes.sitools.role;

import java.util.List;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.role.model.Role;

/**
 * Class for role list management
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class RoleCollectionResource extends AbstractRoleResource {

  @Override
  public void sitoolsDescribe() {
    setName("RoleCollectionResource");
    setDescription("Resource for managing a role collection");
    setNegotiated(false);
  }

  /**
   * Update / Validate existing role
   * 
   * @param representation
   *          of the input role
   * @param variant
   *          required output representation
   * @return Representation of the new Role
   */
  @Post
  public Representation newRole(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "ROLE_REPRESENTATION_REQUIRED");
    }
    try {
      // Parse object representation
      Role roleInput = getObject(representation, variant);

      // Business service
      Role roleOutput = getStore().create(roleInput);

      // Response
      Response response = new Response(true, roleOutput, Role.class, "role");
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a new role sending its representation");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Get all roles
   * 
   * @param variant
   *          required output representation
   * @return Representation of the retrieved Role
   */
  @Get
  public Representation retrieveRole(Variant variant) {
    try {
      ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
      List<Role> roles = getStore().getList(filter);
      int total = roles.size();
      roles = getStore().getPage(filter, roles);
      Response response = new Response(true, roles, Role.class, "roles");
      response.setTotal(total);
      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get all roles.");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
    this.addStandardResourceCollectionFilterInfo(info);    
  }

}
