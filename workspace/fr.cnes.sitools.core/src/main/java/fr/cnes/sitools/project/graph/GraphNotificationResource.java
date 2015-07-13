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
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.project.graph.model.Graph;
import fr.cnes.sitools.project.graph.model.GraphNodeComplete;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource to handler Notifications on Graph Resources
 * 
 * @author AKKA Technologies
 */
public class GraphNotificationResource extends AbstractGraphResource {

  @Override
  public void sitoolsDescribe() {
    setName("GraphNotificationResource");
    setDescription("Manage notifications of graph resources updates");
    setNegotiated(true);
  }

  /**
   * Handle notifications of observable Resource In its case projectResource
   * 
   * @param representation
   *          Graph representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation notification(Representation representation, Variant variant) {
    try {
      Notification notification = null;
      if (representation != null) {
        notification = getNotificationObject(representation);
      }

      if ((notification != null) && "PROJECT_DELETED".equals(notification.getEvent())) {
        // Business service
        boolean ok = getStore().delete(getProjectId());
        if (ok) {
          return new StringRepresentation("OK");
        }
        else {
          return new StringRepresentation("DEPRECATED");
        }
      }
      else if ((notification != null) && "DATASET_STATUS_CHANGED".equals(notification.getEvent())) {
        // parcourir l'arbre pour mettre a jour les noeuds
        DataSet ds = RIAPUtils.getObject(notification.getObservable(), getSitoolsSetting(Consts.APP_DATASETS_URL),
            getContext());
        Graph graph = getStore().retrieve(getGraphId());
        if (graph == null) {
          return new StringRepresentation("DEPRECATED");
        }
        List<GraphNodeComplete> children = graph.updateDatasetChildren(graph.getNodeList(), ds);
        graph.setNodeList(children);
        getStore().update(graph);
        return new StringRepresentation("OK");
      }
      else if ((notification != null) && "DATASET_DELETED".equals(notification.getEvent())) {
        String dsId = notification.getObservable();
        Graph graph = getStore().retrieve(getGraphId());
        if (graph == null) {
          return new StringRepresentation("DEPRECATED");
        }
        List<GraphNodeComplete> children = graph.deleteDatasetChildren(graph.getNodeList(), dsId);
        graph.setNodeList(children);
        getStore().update(graph);
        return new StringRepresentation("OK");
      }  
      else if ((notification != null) && "DATASET_UPDATED".equals(notification.getEvent())) {
        // parcourir l'arbre pour mettre a jour les noeuds
        DataSet ds = RIAPUtils.getObject(notification.getObservable(), getSitoolsSetting(Consts.APP_DATASETS_URL),
            getContext());
        Graph graph = getStore().retrieve(getGraphId());
        if (graph == null) {
          return new StringRepresentation("DEPRECATED");
        }
        List<GraphNodeComplete> children = graph.updateDatasetChildren(graph.getNodeList(), ds);
        graph.setNodeList(children);
        getStore().update(graph);
        return new StringRepresentation("OK");
      }
      else {
        // Others status
        return new StringRepresentation("OK");
      }

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

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to handle notification from observed objects.");
    this.addStandardNotificationInfo(info);
  }

}
