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
package fr.cnes.sitools.tasks.exposition;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.tasks.model.TaskModel;

/**
 * Abstract Resource to handle Tasks
 * 
 * 
 * @author m.gond
 */
public abstract class AbstractTaskResource extends SitoolsResource {

  /** The application */
  private TaskApplication application;

  /** The if of the user */
  private String userId = null;

  @Override
  public void sitoolsDescribe() {
    setName("AbstractTaskResource");
    setDescription("The abstract resource for Tasks");
  }

  @Override
  public void doInit() {
    super.doInit();
    application = (TaskApplication) getApplication();
    userId = (String) this.getRequest().getAttributes().get("identifier");

//    if (userId != null && !userId.equals("anonymous")) {
//      User user = getRequest().getClientInfo().getUser();
//      if (user == null) {
//        throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN, "Those tasks are forbiden");
//      }
//      String userIdentifier = user.getIdentifier();
//      if (!userId.equals(userIdentifier)) {
//        throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN, "Those tasks are forbiden");
//      }
//    }
  }

  /**
   * Get the TaskModel object from the representation sent
   * 
   * @param representation
   *          the representation sent (POST or PUT)
   * @return the corresponding TaskModel
   */
  public final TaskModel getTaskModelFromRepresentation(Representation representation) {
    TaskModel task = null;
    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      XstreamRepresentation<TaskModel> repXML = new XstreamRepresentation<TaskModel>(representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      xstream.autodetectAnnotations(false);
      xstream.alias("TaskModel", TaskModel.class);

      repXML.setXstream(xstream);
      task = repXML.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      task = new JacksonRepresentation<TaskModel>(representation, TaskModel.class).getObject();
    }
    return task;
  }

  /**
   * Gets the application value
   * 
   * @return the application
   */
  public TaskApplication getTaskApplication() {
    return application;
  }

  /**
   * Gets the userId value
   * 
   * @return the userId
   */
  public String getUserId() {
    return userId;
  }

}
