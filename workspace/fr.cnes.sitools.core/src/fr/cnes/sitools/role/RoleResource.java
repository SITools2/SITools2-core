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

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.role.model.Role;

/**
 * Class Resource for managing single Role (GET UPDATE DELETE)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class RoleResource extends AbstractRoleResource {

  @Override
  public void sitoolsDescribe() {
    setName("RoleResource");
    setDescription("Resource for managing a role - CRUD");
    setNegotiated(false);
  }

  /**
   * get Role
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveRole(Variant variant) {
    try {
      Role role = getStore().retrieve(getRoleId());
      Response response;
      if (role != null) {
        trace(Level.FINE, "View profile information for the profile " + role.getName());
        response = new Response(true, role, Role.class, "role");
      }
      else {
        trace(Level.INFO, "Cannot view profile information for the profile - id: " + getRoleId());
        response = new Response(false, role, Role.class, "role");
      }
      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot view profile information for the profile - id: " + getRoleId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot view profile information for the profile - id: " + getRoleId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get a single role by ID.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramRoleId = new ParameterInfo("roleId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the role to get.");
    info.getRequest().getParameters().add(paramRoleId);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Update existing role
   * 
   * @param representation
   *          input Role representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateRole(Representation representation, Variant variant) {
    Role roleOutput = null;
    try {
      Role roleInput = null;
      if (representation != null) {
        Role roleFromStore = getStore().retrieve(getRoleId());
        if (roleFromStore == null) {
          trace(Level.INFO, "Cannot update profile information for the profile - id: " + getRoleId());
          Response response = new Response(false, "Can find existing role with id " + getRoleId());
          return getRepresentation(response, variant);
        }

        // Parse object representation
        roleInput = getObject(representation, variant);
        if (!roleInput.getName().equals(roleFromStore.getName()) && checkRoleExists(roleInput)) {
          trace(Level.INFO, "Cannot update profile information for the profile " + roleInput.getName());
          Response response = new Response(false, "Cannot edit role name");
          return getRepresentation(response, variant);
        }

        roleInput.setUsers(roleFromStore.getUsers());
        roleInput.setGroups(roleFromStore.getGroups());

        // Business service
        roleOutput = getStore().update(roleInput);
      }

      if (roleOutput != null) {
        trace(Level.INFO, "Update profile information for the profile " + roleOutput.getName());
        // Response
        Response response = new Response(true, roleOutput, Role.class, "role");
        return getRepresentation(response, variant);
      }
      else {
        trace(Level.INFO, "Cannot update profile information for the profile - id: " + getRoleId());
        // Response
        Response response = new Response(false, "Can not validate role");
        return getRepresentation(response, variant);
      }

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot update profile information for the profile - id: " + getRoleId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot update profile information for the profile - id: " + getRoleId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify a role sending its new representation. Users and Groups can't be modified that way");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo paramRoleId = new ParameterInfo("roleId", false, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the role to modify.");
    info.getRequest().getParameters().add(paramRoleId);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Delete role
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteRole(Variant variant) {
    try {
      // Business service
      Role roleOutput = getStore().retrieve(getRoleId());
      Response response;

      if (roleOutput != null) {

        response = checkRoleUsed(roleOutput);

        // role not used in authorizations
        if (response == null) {
          getStore().delete(getRoleId());

          // Notify observers
          Notification notification = new Notification();
          notification.setEvent("ROLE_DELETED");
          notification.setObservable(roleOutput.getId());
          notification.setEventSource(roleOutput);
          getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);
          
          // Response
          response = new Response(true, "role.delete.success");
          trace(Level.INFO, "Delete profile " + roleOutput.getName());
        }
      }
      else {
        trace(Level.INFO, "Cannot delete profile - id: " + getRoleId());
        response = new Response(false, "role.delete.failure");
      }

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
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete a role by ID.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramRoleId = new ParameterInfo("roleId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the role to get.");
    info.getRequest().getParameters().add(paramRoleId);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }
}
