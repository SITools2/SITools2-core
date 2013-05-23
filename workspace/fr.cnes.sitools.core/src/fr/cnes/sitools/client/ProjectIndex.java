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
package fr.cnes.sitools.client;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.ResponseInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.applications.ClientUserApplication;
import fr.cnes.sitools.client.model.FeedModelDTO;
import fr.cnes.sitools.client.model.ProjectIndexDTO;
import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.feeds.model.FeedModel;
import fr.cnes.sitools.feeds.model.FeedSource;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import freemarker.template.Configuration;

/**
 * Resource providing an index.html file for a given project. Add links to OpenSearch research engines in the file.
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
public final class ProjectIndex extends SitoolsResource {
  /**
   * The clientUserApplication
   */
  private ClientUserApplication application;

  /**
   * Identifier of the project.
   */
  private String projectName;

  @Override
  public void sitoolsDescribe() {
    setName("ProjectIndex");
    setDescription("Resource to return the index.html page of the desktop");
  }

  @Override
  public void doInit() {
    super.doInit();
    this.projectName = (String) this.getRequest().getAttributes().get("projectName");
    setNegotiated(false);
    getVariants().add(new Variant(MediaType.TEXT_HTML));
    getVariants().add(new Variant(MediaType.ALL));
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

    // DTO to store the informations for the index.html page
    ProjectIndexDTO pid = new ProjectIndexDTO();
    // get the project
    Project proj = this.getProject(projectName);

    if (proj != null) {

      pid.setProjectDescription(proj.getDescription());
      pid.setProjectName(proj.getName());
      pid.setProjectCss(proj.getCss());
      pid.setProject(proj);

      // Dynamic sitools url
      pid.setAppUrl(application.getSettings().getString(Consts.APP_URL));

      // gets the list of feeds for project
      List<FeedModel> listFeeds = this.getFeeds(proj.getId());
      this.addFeedsDTOForProjects(listFeeds, pid, proj.getSitoolsAttachementForUsers());

      // get the datasets of the project
      List<Resource> datasets = proj.getDataSets();
      if (datasets != null) {
        Iterator<Resource> it = datasets.iterator();
        Resource dataset;
        Opensearch op;

        // get all the applications of the project
        while (it.hasNext()) {
          dataset = it.next();
          // get the dataset application Resource description
          Resource res = getApplication(dataset.getId());
          // gets the list of feeds for the dataset
          listFeeds = this.getFeeds(dataset.getId());
          this.addFeedsDTOForDataSets(listFeeds, pid, res);
          // get the opensearch model
          op = getOpensearch(dataset.getId());
          if ((res != null) && (op != null) && (op.getStatus() != null) && op.getStatus().equals("ACTIVE")) {
            // add the informations to the DTO
            pid.addAppDatasetOpensearchDTO(res.getUrl(), op.getName(), dataset.getName());
          }
        }
      }

      // generate the index.html page with the opensearch informations
      //
      //
      Reference ref = LocalReference.createFileReference(application.getProjectIndexUrl());
      Configuration config = new Configuration();
      try {
        config
            .setDirectoryForTemplateLoading(new File(application.getSettings().getStoreDIR(Consts.TEMPLATE_STORE_DIR)));
      }
      catch (IOException e) {
        getLogger().log(Level.INFO, null, e);
      }

      Representation projectFtl = new ClientResource(ref).get();

      // Wraps the bean with a FreeMarker representation
      TemplateRepresentation result = new TemplateRepresentation(projectFtl, config, pid, MediaType.TEXT_HTML);

      return result;
    }
    else {
      throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
    }
  }

  /**
   * Describe the GET method
   * 
   * @param info
   *          the WADL documentation info
   */
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to get the desktop view of project.");
    this.addStandardGetRequestInfo(info);
    ResponseInfo responseInfo = new ResponseInfo();
    RepresentationInfo representationInfo = new RepresentationInfo();
    representationInfo.setReference("html_freemarker");
    responseInfo.getRepresentations().add(representationInfo);
    info.getResponses().add(responseInfo);
    ParameterInfo paramInfo = new ParameterInfo("projectId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Unique identifier (UUID) of the project.");
    info.getRequest().getParameters().add(paramInfo);
  }

  /**
   * Add FeedModelDTO to the given ProjectIndexDTO
   * 
   * @param listFeeds
   *          the list of FeedModel
   * @param pid
   *          the ProjectIndexDTO
   * @param res
   *          the Resource representing the DataSet
   */
  private void addFeedsDTOForDataSets(List<FeedModel> listFeeds, ProjectIndexDTO pid, Resource res) {
    if (listFeeds != null && res != null) {
      for (Iterator<FeedModel> iterator = listFeeds.iterator(); iterator.hasNext();) {
        FeedModel feedModel = iterator.next();
        FeedModelDTO feedDto = new FeedModelDTO();
        feedDto.setFeedType(feedModel.getFeedType());
        feedDto.setId(feedModel.getName());
        feedDto.setUrl(res.getUrl());
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

  /**
   * Add FeedModelDTO to the given ProjectIndexDTO
   * 
   * @param listFeeds
   *          the list of FeedModel
   * @param pid
   *          the ProjectIndexDTO
   * @param attachProject
   *          the project attachment
   */
  private void addFeedsDTOForProjects(List<FeedModel> listFeeds, ProjectIndexDTO pid, String attachProject) {
    if (listFeeds != null) {
      for (Iterator<FeedModel> iterator = listFeeds.iterator(); iterator.hasNext();) {
        FeedModel feedModel = iterator.next();
        FeedModelDTO feedDto = new FeedModelDTO();
        feedDto.setFeedType(feedModel.getFeedType());
        feedDto.setId(feedModel.getName());
        feedDto.setUrl(attachProject);
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
    if ((projects != null) && (projects.size() > 0)) {
      // return first
      return projects.get(0);
    }
    else {
      return null;
    }
  }

  /**
   * Get the project model object using RIAP.
   * 
   * @param id
   *          : the project model object id
   * @return an project model objet corresponding to the given id null if the is no project object corresponding to the
   *         given id
   * 
   */
  private Resource getApplication(String id) {
    return (Resource) RIAPUtils.getObject(id, application.getSettings().getString(Consts.APP_APPLICATIONS_URL),
        getContext());
  }

  /**
   * Get the OpenSearch model object using RIAP.
   * 
   * @param id
   *          : the OpenSearch model object id
   * @return an OpenSearch model object corresponding to the given id null if the is no OpenSearch object corresponding
   *         to the given id
   * 
   */
  private Opensearch getOpensearch(String id) {
    return RIAPUtils.getObject(getSitoolsSetting(Consts.APP_DATASETS_URL) + "/" + id
        + getSitoolsSetting(Consts.APP_OPENSEARCH_URL), getContext());
  }

  /**
   * Get the List of FeedModel model object using RIAP.
   * 
   * @param id
   *          : the project or DataSet of archive id
   * @return a List of FeedModel model Object corresponding to the given id null if the is no FeedModel object
   *         corresponding to the given id
   * 
   */
  private List<FeedModel> getFeeds(String id) {
    List<FeedModel> feedsModel = RIAPUtils.getListOfObjects(
        application.getSettings().getString(Consts.APP_FEEDS_OBJECT_URL) + "/" + id
            + application.getSettings().getString(Consts.APP_FEEDS_URL), getContext());

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

}
