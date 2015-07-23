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

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.project.model.ProjectModule;
import fr.cnes.sitools.project.modules.model.ProjectModuleModel;
import fr.cnes.sitools.role.model.Role;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Get the list of projectModules for the project with authorizations
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class ProjectListProjectModulesResource extends AbstractProjectResource {

  @Override
  public void sitoolsDescribe() {
    setName("ProjectListProjectModulesResource");
    setDescription("List of projectModules for a project with authorization");
  }

  /**
   * Get the list of projectModules for the project with authorizations
   * 
   * @param variant
   *          the variant asked
   * @return a representation containing the list of projectModules authorized
   */
  @Get
  public Representation getProjectModuleList(Variant variant) {
    Project proj = ((ProjectApplication) getApplication()).getProject();
    List<ProjectModule> projectModuleList = proj.getModules();

    List<ProjectModuleModel> projectModuleListOutput = new ArrayList<ProjectModuleModel>();
    if (projectModuleList != null) {
      for (Iterator<ProjectModule> iterator = projectModuleList.iterator(); iterator.hasNext();) {
        ProjectModule projModule = iterator.next();

        boolean authorized = false;

        if (projModule.getListRoles() != null && !projModule.getListRoles().isEmpty()) {
          for (org.restlet.security.Role clientRole : getClientInfo().getRoles()) {
            for (Role projectRole : projModule.getListRoles()) {
              if (clientRole.getName().equals(projectRole.getName())) {
                authorized = true;
                break;
              }
            }
          }
        }
        else if (!authorized && (projModule.getListRoles() == null || projModule.getListRoles().isEmpty())) {
          authorized = true;
        }

        if (authorized) {
          ProjectModuleModel module = getProjectModuleModel(projModule.getId());
          if (module == null) {
            // create a ProjectModuleModel to have a object on the client side
            module = new ProjectModuleModel();
            module.setName(projModule.getName());
          }
          projectModuleListOutput.add(module);
        }

      }

    }
    Response response = new Response(true, projectModuleListOutput, ProjectModuleModel.class, "ProjectModuleModel");
    return getRepresentation(response, variant);

  }

  /**
   * Get the {@link ProjectModuleModel} from its id
   * 
   * @param id
   *          the id of the {@link ProjectModuleModel}
   * @return the {@link ProjectModuleModel} from its id
   */
  private ProjectModuleModel getProjectModuleModel(String id) {
    SitoolsSettings settings = getSettings();
    ProjectModuleModel projectModuleModel = RIAPUtils.getObject(id,
        settings.getString(Consts.APP_PROJECTS_MODULES_URL), getContext());
    return projectModuleModel;
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of projectModules associated to the project.");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
