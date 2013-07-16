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
package fr.cnes.sitools.security;

import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.security.model.Group;

/**
 * Group resource
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class GroupResource extends UsersAndGroupsResource implements fr.cnes.sitools.security.api.GroupResource {

  /**
   * Negotiated content false To avoid a potential bug on content negotiation
   */
  public GroupResource() {
    super();
    this.setNegotiated(false);
  }

  @Override
  public void sitoolsDescribe() {
    setName("GroupResource");
    setDescription("Resource for managing a group - Retrieve Update Delete, add/remove Users");
  }

  @Override
  public Representation getJSON() {
    Response response = getGroupResponse();
    return getRepresentation(response, MediaType.APPLICATION_JSON);
  }

  @Override
  public Representation getXML() {
    Response response = getGroupResponse();
    return getRepresentation(response, MediaType.APPLICATION_XML);
  }

  @Override
  public Representation getObject() {
    Response response = getGroupResponse();
    return getRepresentation(response, MediaType.APPLICATION_JAVA_OBJECT);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.ServerResource#get(org.restlet.representation.Variant)
   */
  @Get
  @Override
  public Representation get(Variant variant) {
    Response response = getGroupResponse();
    return getRepresentation(response, variant);
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get a group by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo("group", false, "xs:string", ParameterStyle.TEMPLATE,
        "Group identifier.");
    info.getRequest().getParameters().add(paramUserId);
    this.addStandardObjectResponseInfo(info);
  }

  /**
   * Get the response bounded to a group
   * 
   * @return a response representing the group
   */
  protected Response getGroupResponse() {
    Response response = null;
    try {
      Group group = getStore().getGroupById(getGroupName());
      response = new Response(true, group, Group.class, "group");
    }
    catch (Exception e) {
      response = new Response(false, e.getMessage());
      getLogger().log(Level.INFO, null, e);
    }
    return response;
  }

  /**
   * Update Group
   * 
   * @param representation
   *          Group representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Override
  @Put
  public Representation update(Representation representation, Variant variant) {
    try {
      Group input = getGroupObject(representation);
      input.setName(getGroupName());
      // Business service
      Group output = getStore().updateGroup(input);

      // Notify observers
      Notification notification = new Notification();
      notification.setEvent("GROUP_UPDATED");
      notification.setObservable(output.getName());
      getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

      // Response
      Response response = new Response(true, output, Group.class, "group");
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
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify a group sending its new representation.");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo("group", false, "xs:string", ParameterStyle.TEMPLATE,
        "Group identifier.");
    info.getRequest().getParameters().add(paramUserId);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  @Override
  public Representation delete(Variant variant) {
    Response response = null;
    try {
      getStore().deleteGroup(getGroupName());

      // Notify observers
      Notification notification = new Notification();
      notification.setEvent("GROUP_DELETED");
      notification.setObservable(getGroupName());
      getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

      response = new Response(true, "Group " + getGroupName() + " deleted.");
    }
    catch (Exception e) {
      response = new Response(false, e.getMessage());
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, null, e);
    }
    return getRepresentation(response, variant);
  }

  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete a group by ID.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramUserId = new ParameterInfo("group", false, "xs:string", ParameterStyle.TEMPLATE,
        "Group identifier.");
    info.getRequest().getParameters().add(paramUserId);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
