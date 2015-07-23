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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import com.google.common.base.Joiner;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.role.model.Role;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource for managing Role and Users association
 * 
 * @author AKKA
 * 
 */
public class RoleUsersResource extends AbstractRoleResource {

  @Override
  public void sitoolsDescribe() {
    setName("RoleUsersResource");
    setDescription("Resource for managing a user collection");
  }

  /**
   * Gets XML representation of role users list
   * 
   * @return XStreamRepresentation
   */
  @Get("xml")
  public Representation getXML() {
    Response response = getUsersResponse();
    return getRepresentation(response, MediaType.APPLICATION_XML);
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get users that belongs to this role.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramRoleId = new ParameterInfo("roleId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the role to get.");
    info.getRequest().getParameters().add(paramRoleId);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Gets JSON representation of role users list
   * 
   * @return XStreamRepresentation
   */
  @Get("json")
  public Representation getJSON() {
    Response response = getUsersResponse();
    return getRepresentation(response, MediaType.APPLICATION_JSON);
  }

  /**
   * Gets Object representation of role users list
   * 
   * @return ObjectRepresentation
   */
  @Get("class")
  public Representation getObject() {
    Response response = getUsersResponse();
    return getRepresentation(response, MediaType.APPLICATION_JAVA_OBJECT);
  }

  /**
   * Calls store and gets response with role users
   * 
   * @return Response
   */
  private Response getUsersResponse() {
    SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();
    Response response = null;
    try {
      List<Resource> userRes = null;
      List<User> users = new ArrayList<User>();
      userRes = getStore().retrieve(getRoleId()).getUsers();
      if (userRes != null) {
        for (Resource res : userRes) {
          // Prepare request
          Request reqGET = new Request(Method.GET, RIAPUtils.getRiapBase()
              + settings.getString(Consts.APP_SECURITY_URL) + "/users/" + res.getId());
          ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
          objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
          reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
          // Do request
          org.restlet.Response resp = getRoleApplication().getContext().getClientDispatcher().handle(reqGET);
          // Get response
          if (resp == null) {
            // echec access user application
            getLogger().warning("Error calling " + reqGET.getResourceRef().toString());
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
          }
          if (Status.isError(resp.getStatus().getCode())) {
            getLogger().info(resp.getStatus().toString());
            throw new ResourceException(resp.getStatus(), "Error calling " + reqGET.getResourceRef().toString());
          }
          @SuppressWarnings("unchecked")
          ObjectRepresentation<Response> or = (ObjectRepresentation<Response>) resp.getEntity();
          Response myObj;
          try {
            myObj = or.getObject();
          }
          catch (IOException e) { // marshalling error
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
          }
          if (myObj.isSuccess()) {
            users.add((User) myObj.getItem());
          }
        }
      }
      response = new Response(true, users, User.class);

    }
    catch (Exception e) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
    // debug ?
    response.setUrl(this.getReference().toString());
    return response;
  }

  /**
   * Update role
   * 
   * @param representation
   *          input
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateRole(Representation representation, Variant variant) {
    Role roleOutput = null;
    Role roleInput = null;
    Response response;
    try {
      if (representation != null) {
        roleInput = getObject(representation, variant);
        Role roleFromStore = getStore().retrieve(getRoleId());

        String usersStr = getUsersListAsString(roleInput);

        if (roleFromStore == null) {
          trace(Level.INFO, "Cannot add user(s) " + usersStr + " in the profile - id : " + getRoleId());
          response = new Response(false, "Can find existing role with id " + getRoleId());
          return getRepresentation(response, variant);
        }

        roleInput.setGroups(roleFromStore.getGroups());

        // Business service
        roleOutput = getStore().update(roleInput);

        if (roleOutput != null) {
          // Response
          response = new Response(true, roleOutput, Role.class, "role");

          // Notify observers
          Notification notification = new Notification();
          notification.setEvent("ROLE_USERS_UPDATED");

          // TODO OPTIMISATION POSSIBLE : passer le name dans l'observable evite de serializer l'objet Role ensuite si
          // pas
          // utile dans le trigger / observers
          notification.setObservable(roleOutput.getId());
          notification.setEventSource(roleOutput);
          getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

          trace(Level.INFO, "Add user(s) " + usersStr + " in the profile : " + roleFromStore.getName());
        }
        else {
          trace(Level.INFO, "Cannot add user(s) " + usersStr + " in the profile - id : " + getRoleId());
          // Response
          response = new Response(false, "Can not validate role");
        }
      }
      else {
        trace(Level.INFO, "Cannot add user(s) <undefined users> in the profile - id : " + getRoleId());
        // Response
        response = new Response(false, "Can not validate role");
      }

      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot add user(s) <undefined users> in the profile - id : " + getRoleId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot add user(s) <undefined users> in the profile - id : " + getRoleId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify the role with modfied users.");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo paramRoleId = new ParameterInfo("roleId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the role to modify.");
    info.getRequest().getParameters().add(paramRoleId);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Get the list of users for a Group as a String
   * 
   * @param input
   *          the Group
   * @return the List of users as a String (joined on ,)
   */
  private String getUsersListAsString(Role input) {
    if (input == null || input.getUsers() == null) {
      return "";
    }
    List<String> users = new ArrayList<String>();
    for (Resource user : input.getUsers()) {
      users.add(user.getId());
    }
    String usersStr = Joiner.on(",").join(users);
    return usersStr;
  }
}
