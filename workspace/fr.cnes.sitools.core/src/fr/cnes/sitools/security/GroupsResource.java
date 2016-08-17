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
package fr.cnes.sitools.security;

import java.util.List;
import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.security.model.Group;

/**
 * Groups resource
 * 
 * @author AKKA
 * 
 */
public final class GroupsResource extends UsersAndGroupsResource {

  @Override
  public void sitoolsDescribe() {
    setName("GroupsResource");
    setDescription("Resource for managing a group collection");
  }

  /**
   * Get XML representation
   * 
   * @return a representation in XML format
   */
  @Get("xml")
  public Representation getXML() {
    Response response = getGroupsResponse();
    return getRepresentation(response, MediaType.APPLICATION_XML);
  }

  @Override
  public void describeGet(MethodInfo info, String path) {
    this.addStandardGetRequestInfo(info);
    if (path.endsWith("users/{user}/groups")) {
      info.setDocumentation("GET " + path + " : Gets the list of groups by user.");
      ParameterInfo paramUserId = new ParameterInfo("user", false, "xs:string", ParameterStyle.TEMPLATE,
          "User identifier.");
      info.getRequest().getParameters().add(paramUserId);
    }
    else {
      info.setDocumentation("GET " + path + " : Gets the list of groups.");
      this.addStandardResourceCollectionFilterInfo(info);
    }
    this.addStandardObjectResponseInfo(info);
  }

  /**
   * Get JSON representation
   * 
   * @return a representation in JSON format
   */
  @Get("json")
  public Representation getJSON() {
    Response response = getGroupsResponse();
    return getRepresentation(response, MediaType.APPLICATION_JSON);
  }

  /**
   * Get Java Class representation
   * 
   * @return a representation in Java Class format
   */
  @Get("class")
  public Representation getObject() {
    Response response = getGroupsResponse();
    return getRepresentation(response, MediaType.APPLICATION_JAVA_OBJECT);
  }

  /**
   * Get the response bounded to a group
   * 
   * @return a response representing the group
   */
  private Response getGroupsResponse() {
    Response response = null;
    try {
      List<Group> groups = null;
      ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
      if (getUserId() != null) {
        groups = getStore().getGroupsByUser(getUserId(), filter);
      }
      else {
        groups = getStore().getGroups(filter);
      }
      trace(Level.FINE, "View groups");
      response = new Response(true, groups, Group.class);
      response.setTotal((filter.getTotalCount() == null) ? groups.size() : filter.getTotalCount());

    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot view groups");
      response = new Response(false, e.getMessage());
    }
    // debug ?
    response.setUrl(this.getReference().toString());
    return response;
  }

  /**
   * Create a new Group
   * 
   * @param representation
   *          Group representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newGroup(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "USER_REPRESENTATION_REQUIRED");
    }
    try {
      Group input = null;
      if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
        // Parse the XML representation to get the bean
        input = new XstreamRepresentation<Group>(representation).getObject();

      }
      else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
        // Parse the JSON representation to get the bean
        input = new JacksonRepresentation<Group>(representation, Group.class).getObject();
      }

      // check if the group already exists or not
      Group existingGroup = getStore().getGroupById(input.getName());
      if (existingGroup != null) {
        trace(Level.INFO, "Cannot create group");
        Response response = new Response(false, "label.group.already.exists");
        return getRepresentation(response, variant.getMediaType());
      }

      // Business service
      input.checkUserUnicity();
      Group output = getStore().createGroup(input);

      // Notify observers
      Notification notification = new Notification();
      notification.setEvent("GROUP_CREATED");
      notification.setObservable(output.getName());
      getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);
      trace(Level.INFO, "Create group " + output.getName());

      // Response
      Response response = new Response(true, output, Group.class, "group");
      return getRepresentation(response, variant.getMediaType());
    }
    catch (SitoolsException e) {
      trace(Level.INFO, "Cannot create group");
      getLogger().log(Level.WARNING, null, e);
      Response response = new Response(false, e.getMessage());
      return getRepresentation(response, variant.getMediaType());
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot create group");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot create group");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a new group.");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo("user", false, "xs:string", ParameterStyle.TEMPLATE,
        "User identifier.");
    info.getRequest().getParameters().add(paramUserId);
    this.addStandardObjectResponseInfo(info);
  }

}
