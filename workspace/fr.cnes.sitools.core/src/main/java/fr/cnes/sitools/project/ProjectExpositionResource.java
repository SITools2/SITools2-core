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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.opensearch.model.OpensearchColumn;
import fr.cnes.sitools.project.graph.model.GraphNodeComplete;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.project.model.ProjectModule;
import fr.cnes.sitools.role.model.Role;

/**
 * Resource to expose Projects, Only get available
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class ProjectExpositionResource extends AbstractProjectResource {

  @Override
  public void sitoolsDescribe() {
    setName("ProjectResource");
    setDescription("Resource exposing the project description");
  }

  /**
   * get on project
   * 
   * @param variant
   *          variant required
   * @return representation corresponding to the required variant
   */
  @Get
  public Representation retrieveProject(Variant variant) {
    return getProject(variant);
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the project description.");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * TODO >> ProjectExpositionResource.class
   * 
   * @param variant
   *          client preferred output representation
   * @return Project representation
   */
  private Representation getProject(Variant variant) {
    try {
      ProjectApplication projApp = (ProjectApplication) getProjectApplication();
      Project project = projApp.getProject();
      project.setStatus("ACTIVE");

      Project newProject = cloneProject(project);
      if (newProject.getModules() != null) {
        for (Iterator<ProjectModule> iterator = newProject.getModules().iterator(); iterator.hasNext();) {
          ProjectModule projMod = iterator.next();
          boolean isSame = false;

          if (projMod.getListRoles() != null && !projMod.getListRoles().isEmpty()) {
            for (org.restlet.security.Role clientRole : getClientInfo().getRoles()) {
              for (Role projectRole : projMod.getListRoles()) {
                if (clientRole.getName().equals(projectRole.getName())) {
                  isSame = true;
                  break;
                }
              }
            }
            if (!isSame) {
              iterator.remove();
            }
          }
        }
      }
      Response response = new Response(true, newProject, Project.class, "project");
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

  /**
   * Clone a project
   * 
   * @param project
   *          the project to clone
   * @return project
   * */
  private Project cloneProject(Project project) {

    Project current = new Project();
    current.setName(project.getName());
    current.setDescription(project.getDescription());
    current.setImage(project.getImage());
    current.setCss(project.getCss());
    current.setStatus(project.getStatus());
    current.setSitoolsAttachementForUsers(project.getSitoolsAttachementForUsers());
    current.setVisible(project.isVisible());
    current.setHtmlDescription(project.getHtmlDescription());
    current.setHtmlHeader(project.getHtmlHeader());
    current.setMaintenanceText(project.getMaintenanceText());
    current.setMaintenance(project.isMaintenance());

    if (project.getModules() != null) {
      List<ProjectModule> projectMod = new ArrayList<ProjectModule>();
      for (ProjectModule mod : project.getModules()) {
        projectMod.add(mod);
      }
      current.setModules(projectMod);
    }
    if (project.getDataSets() != null) {
      List<Resource> projectDataset = new ArrayList<Resource>();
      for (Resource res : project.getDataSets()) {
        projectDataset.add(res);
      }
      current.setDataSets(projectDataset);
    }

    return current;
  }

  /**
   * Response to Representation
   * 
   * @param response
   *          the response to treat
   * @param media
   *          the media to use
   * @return Representation
   */
  @Override
  public Representation getRepresentation(Response response, MediaType media) {
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("project", Project.class);
    xstream.alias("dataset", Resource.class);
    // for graphs representation
    xstream.alias("graphNodeComplete", GraphNodeComplete.class);
    // for opensearch representation
    xstream.alias("opensearchColumn", OpensearchColumn.class);
    // xstream.omitField(Project.class, "id");
    xstream.omitField(Resource.class, "listRoles");

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

}
