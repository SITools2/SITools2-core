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
package fr.cnes.sitools.project.graph;

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
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.notification.business.NotificationManager;
import fr.cnes.sitools.notification.model.RestletObserver;
import fr.cnes.sitools.project.ProjectAdministration;
import fr.cnes.sitools.project.graph.model.Graph;
import fr.cnes.sitools.project.graph.model.GraphNodeComplete;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Abstract Resource class for Graphs management
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AbstractGraphResource extends SitoolsResource {
  /**
   * The name of the project ID parameter name
   */
  public static final String PROJECT_ID_PARAM_NAME = "projectId";

  /** parent application */
  private ProjectAdministration application = null;

  /** store */
  private SitoolsStore<Graph> store = null;

  /** project identifier parameter */
  private String projectId = null;

  /** graph identifier parameter **/
  private String graphId = null;

  /**
   * Default constructor
   */
  public AbstractGraphResource() {
    super();
  }

  @Override
  public final void doInit() {
    super.doInit();

    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));

    application = (ProjectAdministration) getApplication();
    store = application.getGraphStore();

    projectId = (String) this.getRequest().getAttributes().get(PROJECT_ID_PARAM_NAME);
    // We have only one graph for a project. Graph and project have the same id
    // for simplification
    graphId = projectId;
  }

  /**
   * Response to Representation
   * 
   * @param response
   *          The response
   * @param media
   *          The media type needed
   * @return Representation
   * 
   */
  public final Representation getRepresentation(Response response, MediaType media) {
    getLogger().info(media.toString());
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    xstream.alias("response", Response.class);
    xstream.alias("graphNodeComplete", GraphNodeComplete.class);

    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");

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

  /**
   * Gets the Graph object from the given representation and the given variant
   * 
   * @param representation
   *          A graph representation
   * @param variant
   *          The variant of the representation
   * @return A Graph Object
   */
  public final Graph getObject(Representation representation, Variant variant) {
    Graph projectInput = null;
    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the bean
      XstreamRepresentation<Graph> repXML = new XstreamRepresentation<Graph>(representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      xstream.autodetectAnnotations(false);
      xstream.alias("graph", Graph.class);
      xstream.alias("graphNodeComplete", GraphNodeComplete.class);
      repXML.setXstream(xstream);
      projectInput = repXML.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      projectInput = new JacksonRepresentation<Graph>(representation, Graph.class).getObject();
    }
    return projectInput;
  }

  /**
   * Register as observer
   * 
   * @param input
   *          The ConverterChainedModel
   */
  public final void registerObserver(Graph input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }
    // passage en RIAP
    RestletObserver observer = new RestletObserver();
    String uriToNotify = RIAPUtils.getRiapBase() + application.getSettings().getString(Consts.APP_PROJECTS_URL) + "/"
      + input.getId() + "/graph/notify";
    observer.setUriToNotify(uriToNotify);
    observer.setMethodToNotify("PUT");
    observer.setUuid("Graph." + input.getId());

    // le graphe se fait l'observer de chacun de ses datasets
    List<GraphNodeComplete> dsList = input.getAllDatasets(input.getNodeList());
    for (GraphNodeComplete graphNodeComplete : dsList) {
      notificationManager.addObserver(graphNodeComplete.getDatasetId(), observer);
    }
    // le graph est aussi l'observer du projet parent
    notificationManager.addObserver(input.getParent(), observer);

  }

  /**
   * Unregister as Observer
   * 
   * @param input
   *          ConverterChainedModel Object
   */
  public final void unregisterObserver(Graph input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }

    // delete l'observer de chacun de ses datasets
    List<GraphNodeComplete> dsList = input.getAllDatasets(input.getNodeList());
    for (GraphNodeComplete graphNodeComplete : dsList) {
      notificationManager.removeObserver(graphNodeComplete.getDatasetId(), "Graph." + input.getId());
    }
    // delete project observer
    notificationManager.removeObserver(input.getParent(), "Graph." + input.getId());
  }

  /**
   * Gets the application value
   * 
   * @return the application
   */
  public final ProjectAdministration getProjectAdministration() {
    return application;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public final SitoolsStore<Graph> getStore() {
    return store;
  }

  /**
   * Gets the projectId value
   * 
   * @return the projectId
   */
  public final String getProjectId() {
    return projectId;
  }

  /**
   * Gets the graphId value
   * 
   * @return the graphId
   */
  public final String getGraphId() {
    return graphId;
  }

}
