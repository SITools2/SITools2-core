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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.util.Property;

/**
 * Resource for notification on project
 * 
 * @author AKKA Technologies
 * 
 */
public final class ProjectNotificationResource extends AbstractProjectResource {

  @Override
  public void sitoolsDescribe() {
    setName("ProjectNotificationResource");
    setDescription("Manage notification of project resources updating");
    setNegotiated(true);
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

      if ((notification != null) && "DELETED".equals(notification.getStatus())) {
        // Business service
        Project projectInput = getStore().retrieve(getProjectId());
        boolean updated = false;
        for (Iterator<Resource> iterator = projectInput.getDataSets().iterator(); iterator.hasNext();) {
          Resource dataset = (Resource) iterator.next();
          if (dataset.getId().equals(notification.getObservable())) {
            getLogger().info("Remove resource from project");
            iterator.remove();
            updated = true;
          }
        }
        if (updated) {
          getStore().update(projectInput);
          return new StringRepresentation("OK");
        }
        return new StringRepresentation("DEPRECATED");
      }
      else if (notification != null && "DATASET_STATUS_CHANGED".equals(notification.getEvent())) {
        getLogger().log(Level.INFO, "Try to get project with id : " + getProjectId());
        Project projectInput = getStore().retrieve(getProjectId());
        boolean updated = false;
        for (Iterator<Resource> iterator = projectInput.getDataSets().iterator(); iterator.hasNext();) {
          Resource dataset = (Resource) iterator.next();
          if (dataset.getId().equals(notification.getObservable())) {
            DataSet dsObject = this.getDataset(dataset.getId());
            dataset.setName(dsObject.getName());
            dataset.setDescription(dsObject.getDescription());
            dataset.setVisible(dsObject.isVisible());
            dataset.setStatus(dsObject.getStatus());

            ArrayList<Property> dsProp = dataset.getProperties();
            int nbRecord = dsObject.getNbRecords();
            // String imageUrl = null;
            // if (dsObject.getImage() != null) {
            // imageUrl = dsObject.getImage().getUrl();
            // }
            // String description = dsObject.getDescriptionHTML();
            if (dsProp != null) {
              for (Property property : dsProp) {
                if (property.getName().equals("nbRecord")) {
                  property.setValue(String.valueOf(nbRecord));
                }
              }
            }
            dataset.setProperties(dsProp);

            getLogger().info("Dataset resource updated");
            updated = true;
          }
        }
        if (updated) {
          getStore().update(projectInput);
          getProjectApplication().detachProject(projectInput);
          getProjectApplication().attachProject(projectInput);

          return new StringRepresentation("OK");
        }
        return new StringRepresentation("DEPRECATED");
      }
      else if (notification != null && "DATASET_UPDATED".equals(notification.getEvent())) {
        Project projectInput = getStore().retrieve(getProjectId());
        boolean updated = false;
        for (Iterator<Resource> iterator = projectInput.getDataSets().iterator(); iterator.hasNext();) {
          Resource dataset = (Resource) iterator.next();
          if (dataset.getId().equals(notification.getObservable())) {
            DataSet dsObject = this.getDataset(dataset.getId());
            dataset.setName(dsObject.getName());
            dataset.setDescription(dsObject.getDescription());
            dataset.setVisible(dsObject.isVisible());
            dataset.setUrl(dsObject.getSitoolsAttachementForUsers());

            ArrayList<Property> dsProp = dataset.getProperties();
            String imageUrl = null;
            if (dsObject.getImage() != null) {
              imageUrl = dsObject.getImage().getUrl();
            }
            String description = dsObject.getDescriptionHTML();
            for (Property property : dsProp) {
              if (property.getName().equals("imageUrl")) {
                property.setValue(imageUrl);
              }
              else if (property.getName().equals("descriptionHTML")) {
                property.setValue(description);
              }

            }
            dataset.setProperties(dsProp);

            getLogger().info("Dataset resource updated");
            updated = true;
          }

        }
        if (updated) {
          getStore().update(projectInput);
          getProjectApplication().detachProject(projectInput);
          getProjectApplication().attachProject(projectInput);

          return new StringRepresentation("OK");
        }
        return new StringRepresentation("DEPRECATED");
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

  /**
   * Get Notification object
   * 
   * @param representation
   *          the representation to use
   * @return Notification
   */
  public Notification getObject(Representation representation) {
    try {
      ObjectRepresentation<Notification> or;
      try {
        or = new ObjectRepresentation<Notification>(representation);
        return or.getObject();
      }
      catch (IllegalArgumentException e) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
      }
      catch (ClassNotFoundException e) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
      }

    }
    catch (IOException e) {
      getLogger().log(Level.WARNING, "Bad representation of project resource updating notification", e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }
}
