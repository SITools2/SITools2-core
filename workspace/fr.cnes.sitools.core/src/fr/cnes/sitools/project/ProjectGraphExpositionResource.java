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
package fr.cnes.sitools.project;

import java.util.Iterator;
import java.util.List;

import org.restlet.data.Method;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.security.User;

import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.project.graph.GraphStoreInterface;
import fr.cnes.sitools.project.graph.model.Graph;
import fr.cnes.sitools.project.graph.model.GraphNodeComplete;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.registry.AppRegistryApplication;
import fr.cnes.sitools.security.SecurityUtil;

/**
 * Resource to handle Graph
 * 
 * @author m.gond (AKKA Technologies)
 */

public final class ProjectGraphExpositionResource extends AbstractProjectResource {

  @Override
  public void sitoolsDescribe() {
    setName("ProjectGraphExpositionResource");
    setDescription("Resource to expose the list of graphs associated to the project.");
  }

  /**
   * Get the graph of a project with authorizations
   * 
   * @param variant
   *          the variant asked
   * @return a representation of the project with the list of datasets authorized
   */
  @Get
  public Representation getGraph(Variant variant) {

    Representation rep = null;

    User user = this.getRequest().getClientInfo().getUser();

    String userIdentifier = (user == null) ? null : user.getIdentifier();

    Project proj = ((ProjectApplication) getApplication()).getProject();

    GraphStoreInterface graphStore = ((ProjectApplication) getApplication()).getGraphStore();
    Graph graph = graphStore.retrieve(proj.getId());
    if (graph != null) {

      List<GraphNodeComplete> nodes = graph.getNodeList();

      nodes = getNodesAuthorized(nodes, userIdentifier);
      graph.setNodeList(nodes);

    }
    Response response = new Response(true, graph, Graph.class, "graph");
    rep = getRepresentation(response, variant);
    return rep;
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of graphs associated to the project.");
    this.addStandardGetRequestInfo(info);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Get the list of authorized node
   * 
   * @param nodes
   *          the initial list of nodes
   * @param userIdentifier
   *          the userIdentifier
   * @return the list of authorized node
   */
  private List<GraphNodeComplete> getNodesAuthorized(List<GraphNodeComplete> nodes, String userIdentifier) {

    if (nodes == null) {
      return nodes;
    }
    else {

      AppRegistryApplication appManager = ((SitoolsApplication) getApplication()).getSettings().getAppRegistry();

      for (Iterator<GraphNodeComplete> iterator = nodes.iterator(); iterator.hasNext();) {
        GraphNodeComplete node = iterator.next();
        if (GraphNodeComplete.NODE_TYPE_DATASET.equals(node.getType())) {

          //remove all datasets that are not active
          if (!"ACTIVE".equals(node.getStatus())) {
            iterator.remove();
          }
          else {
          // retrouver l'objet application
            SitoolsApplication myApp = appManager.getApplication(node.getDatasetId());
  
            boolean authorized = SecurityUtil.authorize(myApp, userIdentifier, Method.GET);
  
            // DataSet ds = this.getDataset(dsId);
            boolean visible = node.getVisible();
  
            if (authorized || visible) {
              node.setAuthorized(Boolean.valueOf(authorized).toString());
            }
            else {
              // remove the node from the arrayList if it is not authorized
              iterator.remove();
            }
          }

        }
        node.setChildren(getNodesAuthorized(node.getChildren(), userIdentifier));
      }
    }
    return nodes;
  }

}
