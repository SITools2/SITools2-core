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
package fr.cnes.sitools.project;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.restlet.data.Method;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.security.User;

import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.registry.AppRegistryApplication;
import fr.cnes.sitools.security.SecurityUtil;

/**
 * Get the list of datasets for the project with authorizations
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class ProjectListDatasetsResource extends AbstractProjectResource {

  @Override
  public void sitoolsDescribe() {
    setName("ProjectListDatasetsResource");
    setDescription("List of datasets for a project with authorization");
  }

  /**
   * Get the list of datasets for the project with authorizations
   * 
   * @param variant
   *          the variant asked
   * @return a representation containing the list of datasets authorized
   */
  @Get
  public Representation getDatasetList(Variant variant) {

    User user = this.getRequest().getClientInfo().getUser();

    String userIdentifier = (user == null) ? null : user.getIdentifier();

    Project proj = ((ProjectApplication) getApplication()).getProject();
    List<Resource> dsList = proj.getDataSets();

    ArrayList<Resource> dsListOutput = new ArrayList<Resource>();
    AppRegistryApplication appManager = ((SitoolsApplication) getApplication()).getSettings().getAppRegistry();
    if (dsList != null) {
      for (Iterator<Resource> iterator = dsList.iterator(); iterator.hasNext();) {
        Resource ds = iterator.next();
        // pour l'instant on ne renvoi que les datasets authorisés, à voir s'il
        // faut aussi filtrer sur le statut
        // retrouver l'objet application
        SitoolsApplication myApp = appManager.getApplication(ds.getId());
        boolean authorized = SecurityUtil.authorize(myApp, userIdentifier, Method.GET);

        if (authorized || (ds.getVisible() != null && ds.getVisible())) {
          ds.setAuthorized(Boolean.valueOf(authorized).toString());
          dsListOutput.add(ds);          
        }
      }

    }
    Response response = new Response(true, dsListOutput, Resource.class, "datasets");
    return getRepresentation(response, variant);
    
  }
  
  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of datasets associated to the project.");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
