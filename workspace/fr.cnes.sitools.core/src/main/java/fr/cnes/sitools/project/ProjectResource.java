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
package fr.cnes.sitools.project;

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
import fr.cnes.sitools.project.model.Project;

/**
 * Project resource
 * 
 * @author AKKA Technologies
 */
public final class ProjectResource extends AbstractProjectResource {

  @Override
  public void sitoolsDescribe() {
    setName("ProjectResource");
    setDescription("Resource for managing an identified project");
    setNegotiated(false);
  }

  /**
   * get a single project by name
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveProject(Variant variant) {
    Project project = getStore().retrieve(getProjectId());
    Response response;
    if (project != null) {
      response = new Response(true, project, Project.class, "project");
      trace(Level.FINE, "Edit information for the projet " + project.getName());
    }
    else {
      trace(Level.INFO, "Cannot Edit information for the projet - id: " + getProjectId());
      response = new Response(false, "project.not.found");
    }

    return getRepresentation(response, variant);
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve a single project by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramProjectId = new ParameterInfo("projectId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Name of the project");
    info.getRequest().getParameters().add(paramProjectId);
    this.addStandardObjectResponseInfo(info);
  }

  /**
   * Update / Validate existing project
   * 
   * @param representation
   *          Project representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateProject(Representation representation, Variant variant) {
    Project projectOutput = null;
    try {
      Response response;
      Project projectInput = null;
      if (representation != null) {

        // Parse object representation
        projectInput = getObject(representation, variant);

        if ("ACTIVE".equals(projectInput.getStatus())) {
          trace(Level.INFO, "Cannot update the projet " + projectInput.getName());
          response = new Response(false, "PROJECT_ACTIVE");
          return getRepresentation(response, variant);
        }
        // check for project unicity
        response = checkUnicity(projectInput, false);
        if (response != null && !response.getSuccess()) {
          return getRepresentation(response, variant);
        }

        // Business service
        projectInput.setStatus("INACTIVE");
        projectOutput = getStore().update(projectInput);

        // Register Project as observer of datasets resources
        unregisterObserver(projectOutput);

        registerObserver(projectOutput);

      }

      if (projectOutput != null) {
        trace(Level.INFO, "Update the projet " + projectInput.getName());
        response = new Response(true, projectOutput, Project.class, "project");

      }
      else {
        trace(Level.INFO, "Cannot update the projet - id: " + getProjectId());
        response = new Response(false, "project.update.failure");
      }

      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot update the projet - id: " + getProjectId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot update the projet - id: " + getProjectId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify a project sending its new representation.");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramProjectId = new ParameterInfo("projectId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the project");
    info.getRequest().getParameters().add(paramProjectId);
    this.addStandardObjectResponseInfo(info);
  }

  /**
   * Delete project
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteProject(Variant variant) {
    try {
      Project input = null;
      try {
        input = getStore().retrieve(getProjectId());
        getProjectApplication().detachProjectDefinitif(input);
      }
      catch (Exception e) {
        trace(Level.INFO, "Cannot delete the projet - id: " + getProjectId());
        getLogger().log(Level.WARNING, null, e);
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
      }

      Response response = null;

      if (input != null) {

        // Register Project as observer of datasets resources
        unregisterObserver(input);

        // Notify observers
        Notification notification = new Notification();
        notification.setObservable(getProjectId());
        notification.setStatus("DELETED");
        notification.setEvent("PROJECT_DELETED");
        notification.setMessage("project definitively deleted.");
        getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

        // Business service
        getStore().delete(getProjectId());

        response = new Response(true, "project.delete.success");
        trace(Level.INFO, "Delete the projet " + input.getName());
      }
      else {
        trace(Level.INFO, "Cannot delete the projet - id: " + getProjectId());
        response = new Response(false, "project.delete.notfound");
      }
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot delete the projet - id: " + getProjectId());
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot delete the projet - id: " + getProjectId());
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete a single project by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo paramProjectId = new ParameterInfo("projectId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the project");
    info.getRequest().getParameters().add(paramProjectId);
    this.addStandardSimpleResponseInfo(info);
  }

}
