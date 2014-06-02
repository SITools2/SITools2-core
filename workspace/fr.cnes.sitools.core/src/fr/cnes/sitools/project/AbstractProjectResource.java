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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.opensearch.model.OpensearchColumn;
import fr.cnes.sitools.notification.business.NotificationManager;
import fr.cnes.sitools.notification.model.RestletObserver;
import fr.cnes.sitools.project.graph.model.GraphNodeComplete;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Abstract Resource class for Projects management
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AbstractProjectResource extends SitoolsResource {

  /** parent application */
  private AbstractProjectApplication application = null;

  /** store */
  private ProjectStoreInterface store = null;

  /** project identifier parameter */
  private String projectId = null;

  /**
   * Default constructor
   */
  public AbstractProjectResource() {
    super();
  }

  @Override
  public final void doInit() {
    super.doInit();

    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    getVariants().add(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    application = (AbstractProjectApplication) getApplication();
    store = application.getStore();

    projectId = (String) this.getRequest().getAttributes().get("projectId");
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
    configure(xstream, response);
    xstream.alias("project", Project.class);
    xstream.alias("dataset", Resource.class);
    // for graphs representation
    xstream.alias("graphNodeComplete", GraphNodeComplete.class);
    // for opensearch representation
    xstream.alias("opensearchColumn", OpensearchColumn.class);
    xstream.setMode(XStream.NO_REFERENCES);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Get the object from the representation
   * 
   * @param representation
   *          the representation to use
   * @param variant
   *          the variant to use
   * @return a project
   */
  public final Project getObject(Representation representation, Variant variant) {
    Project projectInput = null;
    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the bean
      XstreamRepresentation<Project> repXML = new XstreamRepresentation<Project>(representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      xstream.autodetectAnnotations(false);
      xstream.alias("project", Project.class);
      xstream.alias("dataset", Resource.class);

      repXML.setXstream(xstream);
      projectInput = repXML.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      projectInput = new JacksonRepresentation<Project>(representation, Project.class).getObject();
    }
    return projectInput;
  }

  /**
   * Get the object from the representation
   * 
   * @param representation
   *          the representation to use
   * @param variant
   *          the variant to use
   * @return a project
   * @throws IOException
   *           if there is an error while deserializing a Java Object
   */
  public final Resource getObjectResource(Representation representation, Variant variant) throws IOException {
    Resource resourceInput = null;
    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the bean
      XstreamRepresentation<Resource> repXML = new XstreamRepresentation<Resource>(representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      xstream.autodetectAnnotations(false);
      xstream.alias("resource", Resource.class);

      repXML.setXstream(xstream);
      resourceInput = repXML.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      resourceInput = new JacksonRepresentation<Resource>(representation, Resource.class).getObject();
    }
    else if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      @SuppressWarnings("unchecked")
      ObjectRepresentation<Resource> obj = (ObjectRepresentation<Resource>) representation;
      resourceInput = obj.getObject();
    }

    return resourceInput;
  }

  /**
   * Register an observer
   * 
   * @param input
   *          the project as input
   */
  public final void registerObserver(Project input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().severe("NotificationManager is null");
      return;
    }

    List<Resource> list = input.getDataSets();
    if (list != null) {
      RestletObserver observer = new RestletObserver();
      String uriToNotify = RIAPUtils.getRiapBase() + application.getSettings().getString(Consts.APP_PROJECTS_URL) + "/"
          + input.getId() + "/notify";

      observer.setUriToNotify(uriToNotify);
      observer.setMethodToNotify("PUT");
      observer.setUuid(input.getId());

      for (Resource resource : list) {
        notificationManager.addObserver(resource.getId(), observer);
      }
    }
  }

  /**
   * Unregister an observer
   * 
   * @param input
   *          the project to unregister
   */
  public final void unregisterObserver(Project input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().severe("NotificationManager is null");
      return;
    }

    List<Resource> list = input.getDataSets();
    if (list != null) {
      for (Resource resource : list) {
        notificationManager.removeObserver(resource.getId(), input.getId());
      }
    }
  }

  /**
   * Get the dataset object with the given id
   * 
   * @param id
   *          the id of the dataset
   * @return a dataset object corresponding to the given id
   */
  public final DataSet getDataset(String id) {
    return RIAPUtils.getObject(id, getSitoolsSetting(Consts.APP_DATASETS_URL), getContext());
  }

  /**
   * Get the project identifier
   * 
   * @return the project ID
   */
  public final String getProjectId() {
    return this.projectId;
  }

  /**
   * Get the store associated to the application
   * 
   * @return the store available for this resource
   */
  public final ProjectStoreInterface getStore() {
    return this.store;
  }

  /**
   * Get the project application object
   * 
   * @return the project application
   */
  public final AbstractProjectApplication getProjectApplication() {
    return this.application;
  }

  /**
   * Check that the name and the sitoolsUserAttachment are unique over the project collection includeProjectInput is
   * used to specify whether or not to include projectInput in the verification process ( for example when updating a
   * project )
   * 
   * @param projectInput
   *          the project to check with the collection
   * @param includeProjectInput
   *          true to include the project in the verification, false otherwise
   * @return a Response with the error message if the checking fail, null otherwise
   */
  public final Response checkUnicity(Project projectInput, boolean includeProjectInput) {
    Response response = null;
    List<Project> storedProjects = getStore().getList();
    List<String> storedProjectNames = new ArrayList<String>();
    List<String> storedProjectUrlAttach = new ArrayList<String>();

    if (projectInput.getName() == null || "".equals(projectInput.getName())) {
      response = new Response(false, projectInput, Project.class, "project");
      response.setMessage("project.name.mandatory");
      return response;
    }
    if (storedProjects != null) {
      for (Project project : storedProjects) {
        if (includeProjectInput || (!includeProjectInput && !projectInput.getId().equals(project.getId()))) {
          storedProjectNames.add(project.getName());
          // if the attachment is empty it will be filled latter by a unique attachment
          // empty attachment must not be checked for unicity
          if (!project.getSitoolsAttachementForUsers().equals("")) {
            storedProjectUrlAttach.add(project.getSitoolsAttachementForUsers());
          }
        }
      }
      if (storedProjectNames.contains(projectInput.getName())) {
        response = new Response(false, projectInput, Project.class, "project");
        response.setMessage("project.name.already.assigned");
        return response;
      }
      if (storedProjectUrlAttach.contains(projectInput.getSitoolsAttachementForUsers())) {
        response = new Response(false, projectInput, Project.class, "project");
        response.setMessage("project.attachment.already.assigned");
        return response;
      }
    }
    return response;
  }
}
