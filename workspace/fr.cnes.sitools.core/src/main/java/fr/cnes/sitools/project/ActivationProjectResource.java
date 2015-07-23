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

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.project.model.Project;

/**
 * Resource to handle action on a Project actions are start or stop
 * 
 * @author m.gond (AKKA Technologies)
 */
public final class ActivationProjectResource extends AbstractProjectResource {

  @Override
  public void sitoolsDescribe() {
    setName("ActivationProjectResource");
    setDescription("Resource to modify the current status of a project.");
    setNegotiated(false);
  }

  /**
   * Actions on PUT
   * 
   * @param representation
   *          could be null.
   * @param variant
   *          MediaType of response
   * @return Representation response
   */
  @Put
  public Representation action(Representation representation, Variant variant) {
    Response response = null;
    Representation rep = null;
    ProjectStoreInterface store = getStore();
    synchronized (store) {
      try {
        do {
          // on charge le project
          Project proj = store.retrieve(getProjectId());
          if (proj == null) {
            trace(Level.INFO, "Cannot perform action on the project - id: " + getProjectId());
            response = new Response(false, "PROJECT_NOT_FOUND");
            break;
          }

          if (this.getReference().toString().endsWith("start")) {
            if ("ACTIVE".equals(proj.getStatus())) {
              trace(Level.INFO, "Cannot start the project " + proj.getName());
              response = new Response(true, "project.update.blocked");
              break;
            }
            try {
              getProjectApplication().attachProject(proj);

              Project projResult = store.retrieve(getProjectId());

              response = new Response(true, projResult, Project.class, "project");
              response.setMessage("project.update.success");

              // Notify observers
              Notification notification = new Notification();
              notification.setObservable(projResult.getId());
              notification.setStatus(projResult.getStatus());
              notification.setMessage("project.update.success");
              getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);
              trace(Level.INFO, "Start the project " + proj.getName());

            }
            catch (Exception e) {
              trace(Level.INFO, "Cannot start the project - id: " + proj.getName());
              response = new Response(false, "project.update.error");
              throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
            }
            break;
          }

          if (this.getReference().toString().endsWith("stop")) {
            if (!"ACTIVE".equals(proj.getStatus())) {

              // Par mesure de securite
              try {
                getProjectApplication().detachProject(proj);
              }
              catch (Exception e) {
                trace(Level.INFO, "Cannot stop the project " + proj.getName());
                throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
              }

              response = new Response(true, "project.stop.blocked");
              trace(Level.INFO, "Cannot stop the project " + proj.getName());
              break;
            }

            // FIXME Transaction de desactivation d'un project
            try {
              getProjectApplication().detachProject(proj);
              Project projResult = store.retrieve(getProjectId());

              response = new Response(true, projResult, Project.class, "project");
              response.setMessage("project.stop.success");

              // Notify observers
              Notification notification = new Notification();
              notification.setObservable(projResult.getId());
              notification.setStatus(projResult.getStatus());
              notification.setMessage("project.stop.success");
              getResponse().getAttributes().put(Notification.ATTRIBUTE, notification);
              trace(Level.INFO, "Stop the project " + proj.getName());
            }
            catch (Exception e) {
              trace(Level.INFO, "Cannot stop the project " + proj.getName());
              response = new Response(false, "project.stop.error");
              throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
            }
            break;
          }
          if (this.getReference().toString().endsWith("startmaintenance")) {
            try {
              if (proj.isMaintenance()) {
                trace(Level.INFO, "Cannot start the maintenance mode of the project " + proj.getName());
                response = new Response(true, "project.maintenance.on.blocked");
                break;
              }

              getProjectApplication().detachProject(proj);
              proj.setMaintenance(true);
              Project projResult = store.update(proj);

              if ("ACTIVE".equals(projResult.getStatus())) {
                getProjectApplication().attachProject(proj);
              }

              response = new Response(true, projResult, Project.class, "project");
              response.setMessage("project.maintenance.on.success");
              trace(Level.INFO, "Start the maintenance mode of the project " + proj.getName());
            }
            catch (Exception e) {
              trace(Level.INFO, "Cannot start the maintenance mode of the project " + proj.getName());
              response = new Response(false, "project.update.error");
              throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
            }
            break;
          }
          if (this.getReference().toString().endsWith("stopmaintenance")) {
            try {
              if (!proj.isMaintenance()) {
                trace(Level.INFO, "Cannot stop the maintenance mode of the project " + proj.getName());
                response = new Response(true, "project.maintenance.off.blocked");
                break;
              }

              getProjectApplication().detachProject(proj);
              proj.setMaintenance(false);
              Project projResult = store.update(proj);

              if ("ACTIVE".equals(projResult.getStatus())) {
                getProjectApplication().attachProject(proj);
              }

              response = new Response(true, projResult, Project.class, "project");
              response.setMessage("project.maintenance.off.success");
              trace(Level.INFO, "Stop the maintenance mode of the project " + proj.getName());
            }
            catch (Exception e) {
              trace(Level.INFO, "Cannot stop the maintenance mode of the project " + proj.getName());
              response = new Response(false, "project.update.error");
              throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
            }
            break;
          }

        } while (false);

        // Response
        if (response == null) {
          response = new Response(false, "project.action.error");
        }
      }
      finally {
        rep = getRepresentation(response, variant);
      }
    }
    return rep;
  }

  public void describePut(MethodInfo info, String path) {
    if (path.endsWith("start")) {
      info.setDocumentation(" PUT /" + path + " : starts the project application linked to the project object.");
    }
    else if (path.endsWith("stop")) {
      info.setDocumentation(" PUT /" + path + " : stops the project application linked to the project object.");
    }
    else if (path.endsWith("maintain")) {
      info.setDocumentation(" PUT /" + path + " : changes the project maintenance attribute.");
    }
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo paramProjectId = new ParameterInfo("projectId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the project");
    info.getRequest().getParameters().add(paramProjectId);
    this.addStandardSimpleResponseInfo(info);
  }

}
