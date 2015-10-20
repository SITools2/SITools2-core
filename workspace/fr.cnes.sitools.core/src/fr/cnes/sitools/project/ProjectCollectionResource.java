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

import fr.cnes.sitools.util.Util;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.project.model.MinimalProjectPriorityDTO;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.project.model.ProjectPriorityDTO;

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
      trace(Level.INFO, "Cannot create the project");
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "PROJECT_REPRESENTATION_REQUIRED");
    }
    try {
      Response response = null;
      // Parse object representation
      Project projectInput = getObject(representation, variant);
      projectInput.setStatus("NEW");

      // check for project unicity
      response = checkUnicity(projectInput, true);
      if (response != null && !response.getSuccess()) {
        trace(Level.INFO, "Cannot create the project " + projectInput.getName());
        return getRepresentation(response, variant);
      }

      if (Util.isEmpty(projectInput.getSitoolsAttachementForUsers())) {
        String sitoolsAttachment = "/" + projectInput.getName().toLowerCase().replaceAll(" ", "_");
        projectInput.setSitoolsAttachementForUsers(sitoolsAttachment);
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

      trace(Level.INFO, "Create the project " + projectInput.getName());
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot create the project");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot create the project");
      getLogger().log(Level.WARNING, null, e);
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
      trace(Level.FINE, "View available projects");
      Response response = new Response(true, projects, Project.class, "projects");
      response.setTotal(total);
      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot view available projects");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot view available projects");
      getLogger().log(Level.WARNING, null, e);
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
  public Representation saveProjectsList(Representation representation, Variant variant) {
    try {
      Response response = null;
      // Parse object representation
      ProjectPriorityDTO projectListInput = getListObject(representation, variant);


      for (MinimalProjectPriorityDTO m : projectListInput.getMinimalProjectPriorityList()) {
        Project projectFromStore = getStore().retrieve(m.getId());
        
        // update properties
        projectFromStore.setCategoryProject(m.getCategoryProject());
        projectFromStore.setPriority(m.getPriority());
        getStore().update(projectFromStore);
      }
      // Response
      response = new Response(true, "Priority and Category successfully updated");
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot update projects priority and category");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot update projects priority and category");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

}
