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
package fr.cnes.sitools.portal;

import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.security.User;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.registry.AppRegistryApplication;
import fr.cnes.sitools.security.SecurityUtil;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource to list project collection without the list of datasets
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class PortalProjectResource extends SitoolsResource {
  /**
   * The projectId
   */
  private String projectId;

  @Override
  public void sitoolsDescribe() {
    setName("PortalProjectResource");
    setDescription("Resource getting a project without the list of datasets");
    setNegotiated(false);
  }

  @Override
  protected void doInit() {
    super.doInit();
    projectId = (String) this.getRequest().getAttributes().get("projectId");
  }

  /**
   * get a project without the list of datasets
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveProject(Variant variant) {

    User user = this.getRequest().getClientInfo().getUser();

    String userIdentifier = (user == null) ? null : user.getIdentifier();

    Project project = getProject(projectId);

    AppRegistryApplication appManager = ((SitoolsApplication) getApplication()).getSettings().getAppRegistry();

    // retrouver l'objet application
    SitoolsApplication myApp = appManager.getApplication(project.getId());

    boolean authorized = SecurityUtil.authorize(myApp, userIdentifier, Method.GET);

    project.setDataSets(null);
    if (!authorized) {
      getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
    }
    else if (!"ACTIVE".equals(project.getStatus())) {
      getResponse().setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
    }
    else {
      project.setAuthorized(true);
    }

    Response response = new Response(true, project, Project.class, "project");
    return getRepresentation(response, variant);
  }

  /**
   * Get a project from its name
   * 
   * @param projectName
   *          the name of the project
   * @return the project with the corresponding projectId2
   */
  private Project getProject(String projectName) {
    SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();
    List<Project> projects = RIAPUtils.getListOfObjects(settings.getString(Consts.APP_PROJECTS_URL) + "?query="
        + projectName + "&mode=strict", this.getContext());
    return projects.get(0);
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve a project if it is authorized and active");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
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
  public Representation getRepresentation(Response response, MediaType media) {
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    xstream.alias("response", Response.class);
    xstream.alias("project", Project.class);
    xstream.alias("dataset", Resource.class);
    
    xstream.omitField(Project.class, "modules");
    
    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");
//    xstream.omitField(Project.class, "id");

    if (response.getItemClass() != null) {
      xstream.alias("item", Object.class, response.getItemClass());
    }
    if (response.getItemName() != null) {
      xstream.aliasField(response.getItemName(), Response.class, "item");
    }

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

}
