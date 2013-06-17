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
package fr.cnes.sitools.client;

import java.util.Iterator;
import java.util.List;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.ResponseInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;

import fr.cnes.sitools.applications.ClientUserApplication;
import fr.cnes.sitools.client.model.FeedModelDTO;
import fr.cnes.sitools.client.model.PortalIndexDTO;
import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.feeds.model.FeedModel;
import fr.cnes.sitools.feeds.model.FeedSource;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource providing an index.html file for a given project. Add links to OpenSearch research engines in the file.
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
public final class PortalIndex extends SitoolsResource {
  /**
   * The clientUserApplication
   */
  private ClientUserApplication application;

  @Override
  public void sitoolsDescribe() {
    setName("PortalIndex");
    setDescription("Resource to return the index.html page of the portal");
  }

  @Override
  protected void doInit() {
    super.doInit();
    application = (ClientUserApplication) this.getApplication();
  }

  @Get
  @Override
  public Representation get(Variant variant) {
    return get();
  }

  @Get
  @Override
  public Representation get() {

    getApplication().getLogger().info("get portalIndex");
    String portalId = application.getPortalId();
    String projectList = "";

    SitoolsStore<Project> store = ((ClientUserApplication) getApplication()).getStore();
    PortalIndexDTO pid = new PortalIndexDTO();

    List<FeedModel> listFeeds = this.getFeedsForPortal(portalId);
    this.addFeedsDTOForPortal(listFeeds, pid, portalId);

    if (store != null) {
      // get all the projects
      List<Project> projects = store.getList();
      Iterator<Project> it = projects.iterator();
      String glue = "";
      Project current;

      // get all the applications of the project
      while (it.hasNext()) {
        current = it.next();
        projectList += glue + current.getName();
        glue = ", ";

      }
    }
    pid.setProjectList(projectList);

    // Dynamic sitools url
    pid.setAppUrl(application.getSettings().getString(Consts.APP_URL));

    Reference ref = LocalReference.createFileReference(application.getPortalIndexUrl());

    Representation portalFtl = new ClientResource(ref).get();

    // Wraps the bean with a FreeMarker representation
    return new TemplateRepresentation(portalFtl, pid, MediaType.TEXT_HTML);
  }

  /**
   * Describe the GET method
   * 
   * @param info
   *          the WADL documentation info
   */
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get the portal view.");
    this.addStandardGetRequestInfo(info);
    ResponseInfo responseInfo = new ResponseInfo();
    RepresentationInfo representationInfo = new RepresentationInfo();
    representationInfo.setReference("html_freemarker");
    responseInfo.getRepresentations().add(representationInfo);
    info.getResponses().add(responseInfo);
  }

  /**
   * Get the List of FeedModel model object using RIAP.
   * 
   * @param id
   *          : the project or DataSet of archive id
   * @return a list of FeedModel model object corresponding to the given id null if the is no FeedModel object
   *         corresponding to the given id
   * 
   */
  private List<FeedModel> getFeedsForPortal(String id) {
    List<FeedModel> feedsModel = RIAPUtils.getListOfObjects(application.getSettings().getString(Consts.APP_PORTAL_URL)
        + "/" + id + application.getSettings().getString(Consts.APP_FEEDS_URL), getContext());

    // // TODO voir si on affiche les flux dans l'HTML ou non, pour l'instant on ne le fait pas
    // for (Iterator<FeedModel> iterator = feedsModel.iterator(); iterator.hasNext();) {
    // FeedModel feedModel = (FeedModel) iterator.next();
    // if (FeedSource.EXTERNAL.equals(feedModel.getFeedSource())) {
    // iterator.remove();
    // }
    //
    // }

    return feedsModel;
  }

  /**
   * Add FeedModelDTO to the given ProjectIndexDTO
   * 
   * @param listFeeds
   *          the list of FeedModel
   * @param pid
   *          the ProjectIndexDTO
   * @param portalId
   *          the portal identifier needed for feed objects attachment
   * 
   */
  private void addFeedsDTOForPortal(List<FeedModel> listFeeds, PortalIndexDTO pid, String portalId) {
    if (listFeeds != null) {
      SitoolsSettings settings = application.getSettings();
      String baseUrl = settings.getString(Consts.APP_URL) + settings.getString(Consts.APP_PORTAL_STORE_DIR);
      for (Iterator<FeedModel> iterator = listFeeds.iterator(); iterator.hasNext();) {
        FeedModel feedModel = iterator.next();
        if (feedModel.isVisible()) {
          FeedModelDTO feedDto = new FeedModelDTO();
          feedDto.setFeedType(feedModel.getFeedType());
          feedDto.setId(feedModel.getName());
          feedDto.setUrl(baseUrl + "/" + portalId);
          if (FeedSource.EXTERNAL.equals(feedModel.getFeedSource())) {
            feedDto.setTitle(feedModel.getName());
          }
          else {
            feedDto.setTitle(feedModel.getTitle());
          }

          pid.getFeeds().add(feedDto);
        }
      }
    }
  }
}
