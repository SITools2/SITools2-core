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
package fr.cnes.sitools.dataset.opensearch;

import java.io.IOException;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.notification.model.Notification;

/**
 * Resource for updating/deleting OpenSearch definition when notified by DataSets change events.
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class OpensearchNotificationResource extends AbstractSearchResource {

  @Override
  public void sitoolsDescribe() {
    setName("ProjectNotificationResource");
    setDescription("Manage notification of project resources updating");
  }

  /**
   * Initiate the resource
   */
  @Override
  public void doInit() {
    super.doInit();
  }

  /**
   * Update / Validate existing project
   * 
   * @param representation
   *          Project representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation notification(Representation representation, Variant variant) {
    try {
      Notification notification = null;
      if (representation != null) {
        notification = getObject(representation);
      }

      if (notification != null && "DATASET_STATUS_CHANGED".equals(notification.getEvent())) {
        if ("INACTIVE".equals(notification.getStatus())) {
          // Business service
          Opensearch opensearch = getStore().retrieve(getDatasetId());
          // We stop the opensearch in that case
          if ("ACTIVE".equals(opensearch.getStatus())) {
            Response response = stopOsIndex(opensearch);
            if (response.getSuccess()) {
              deleteFeedOpensearch();
              return new StringRepresentation("OK");
            }
            else {
              return new StringRepresentation("DEPRECATED");
            }
          }
          return new StringRepresentation("OK");
        }
      }

      if ((notification != null) && "DATASET_DELETED".equals(notification.getEvent())) {
        // Business service
        Opensearch opensearch = getStore().retrieve(getDatasetId());
        // We stop and delete the opensearch in that case
        if ("ACTIVE".equals(opensearch.getStatus())) {
          Response response = stopOsIndex(opensearch);
          if (response.getSuccess()) {
            if (getStore().delete(opensearch.getId())) {
              deleteFeedOpensearch();
              return new StringRepresentation("OK");
            }
            else {
              return new StringRepresentation("DEPRECATED");
            }
          }
          else {
            return new StringRepresentation("DEPRECATED");
          }
        }
        // if the opensearch is INACTIVE we only delete it
        else {
          if (getStore().delete(opensearch.getId())) {
            return new StringRepresentation("OK");
          }
          else {
            return new StringRepresentation("DEPRECATED");
          }
        }
      }
      // In case of dataset modification
      if (notification != null && "DATASET_UPDATED".equals(notification.getEvent())) {
        Opensearch opensearch = getStore().retrieve(getDatasetId());
        DataSet dataset = this.getDataset(opensearch.getParent());
        opensearch.setParentUrl(dataset.getSitoolsAttachementForUsers());
        getStore().update(opensearch);
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

  /**
   * Describe the PUT HTTP command
   * 
   * @param info
   *          the method WADL info
   */
  public void describePut(MethodInfo info) {
    this.addStandardNotificationInfo(info);
  }

  /**
   * Gets Notification object
   * 
   * @param representation
   *          Notification representation
   * @return Notification object
   */
  public Notification getObject(Representation representation) {
    try {
      ObjectRepresentation<Notification> or;
      try {
        or = new ObjectRepresentation<Notification>(representation);
        return or.getObject();
      }
      catch (IllegalArgumentException e) {
        getLogger().log(Level.INFO, null, e);
      }
      catch (ClassNotFoundException e) {
        getLogger().log(Level.INFO, null, e);
      }

    }
    catch (IOException e) {
      getLogger().log(Level.WARNING, "Bad representation of project resource updating notification", e);
    }
    return null;
  }
}
