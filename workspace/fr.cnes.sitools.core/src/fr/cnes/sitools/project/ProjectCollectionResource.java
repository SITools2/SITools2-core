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
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.project.model.Project;

/**
 * Class Resource for managing Project Collection (GET, POST)
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public final class ProjectCollectionResource extends AbstractProjectResource {

  @Override
  public void sitoolsDescribe() {
    setName("ProjectCollectionResource");
    setDescription("Resource for managing project collection");
    setNegotiated(false);
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
  @Post
  public Representation newProject(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "PROJECT_REPRESENTATION_REQUIRED");
    }
    try {
      Response response = null;
      // Parse object representation
      Project projectInput = getObject(representation, variant);
      projectInput.setStatus("NEW");

      // Check that the project does not already exists, or that attachment is not already assigned
      // First need an alphanumeric name for convenience
      if (!projectInput.getName().matches("^[a-zA-Z0-9\\-\\.\\_]+$")) {
        response = new Response(false, projectInput, Project.class, "project");
        response.setMessage("project.name.invalid.for.regexp");
        return getRepresentation(response, variant);
      }
      // check for project unicity
      response = checkUnicity(projectInput, true);
      if (response != null && !response.getSuccess()) {
        return getRepresentation(response, variant);
      }
      // Business service
      Project projectOutput = getStore().create(projectInput);

      // Register Project as observer of datasets resources
      registerObserver(projectOutput);

      // Response
      response = new Response(true, projectOutput, Project.class, "project");

      // Notify observers
      Notification notification = new Notification();
      notification.setObservable(projectOutput.getId());
      notification.setStatus("CREATED");
      notification.setEvent("PROJECT_CREATED");
      notification.setMessage("project created.");
      getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);

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
    info.setDocumentation("Method to create a new project.");
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * get all projects
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveProject(Variant variant) {
    try {
      ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
      List<Project> projects = getStore().getList(filter);
      int total = projects.size();
      projects = getStore().getPage(filter, projects);
      Response response = new Response(true, projects, Project.class, "projects");
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
    info.setDocumentation("Method to the list of all projects available in Sitools2.");
    this.addStandardGetRequestInfo(info);

    this.addStandardResourceCollectionFilterInfo(info);

    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
    this.addStandardResourceCollectionFilterInfo(info);
  }

}
