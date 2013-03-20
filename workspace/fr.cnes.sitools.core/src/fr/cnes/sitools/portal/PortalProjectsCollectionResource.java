/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import java.util.Iterator;
import java.util.List;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
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
public final class PortalProjectsCollectionResource extends SitoolsResource {

  @Override
  public void sitoolsDescribe() {
    setName("PortalProjectsCollectionResource");
    setDescription("Resource listing projects collection without the list of datasets");
    setNegotiated(false);
  }

  /**
   * get all projects without the list of datasets
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveProjects(Variant variant) {

    User user = this.getRequest().getClientInfo().getUser();

    String userIdentifier = (user == null) ? null : user.getIdentifier();

    List<Project> projects = getProjects();

    Form query = this.getRequest().getResourceRef().getQueryAsForm();
    String filterStr = query.getFirstValue("filter");
    // if the mode if authorization only, return only the project that are
    // authorized
    boolean modeAuth = ("auth".equals(filterStr)) ? true : false;

    AppRegistryApplication appManager = ((SitoolsApplication) getApplication()).getSettings().getAppRegistry();

    for (Iterator<Project> iterator = projects.iterator(); iterator.hasNext();) {
      Project project = (Project) iterator.next();

      // retrouver l'objet application
      SitoolsApplication myApp = appManager.getApplication(project.getId());

      boolean authorized = SecurityUtil.authorize(myApp, userIdentifier, Method.GET);
      boolean visible = project.isVisible();
      if ((!modeAuth && !authorized && !visible) || (modeAuth && !authorized) || !"ACTIVE".equals(project.getStatus())) {
        iterator.remove();
      }
      else {
        project.setDataSets(null);
        if (!authorized) {
          project.setAuthorized(false);
        }
        else {
          project.setAuthorized(true);
        }
      }
    }

    int total = projects.size();
    Response response = new Response(true, projects, Project.class, "projects");
    response.setTotal(total);
    return getRepresentation(response, variant);

  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve  the complete list of visible, authorized and active projects.");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
  }

  /**
   * Get the List of Project model object using RIAP.
   * 
   * @return a List of Project model object corresponding to the given id null if the is no Project object corresponding
   *         to the given id
   * 
   */
  private List<Project> getProjects() {
    SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();
    return RIAPUtils.getListOfObjects(settings.getString(Consts.APP_PROJECTS_URL), this.getContext());
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
    getLogger().info(media.toString());
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
    xstream.omitField(Project.class, "id");

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
