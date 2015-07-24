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
package fr.cnes.sitools.project.modules;

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
import fr.cnes.sitools.project.modules.model.ProjectModuleModel;

import java.io.IOException;

/**
 * Base class for resource of management of ProjectModule
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AbstractProjectModuleResource extends SitoolsResource {

  /** parent application */
  private ProjectModuleApplication application = null;

  /** store */
  private ProjectModuleStoreInterface store = null;

  /** ProjectModule identifier parameter */
  private String projectModuleId = null;

  @Override
  public final void doInit() {
    super.doInit();

    // Declares the two variants supported
    addVariant(new Variant(MediaType.APPLICATION_XML));
    addVariant(new Variant(MediaType.APPLICATION_JSON));
    addVariant(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    application = (ProjectModuleApplication) getApplication();
    setStore(application.getStore());

    setProjectModuleId((String) this.getRequest().getAttributes().get("projectModuleId"));
  }

  /**
   * Response to Representation
   * 
   * @param response
   *          the server response
   * @param media
   *          the media used
   * @return Representation
   */
  public final Representation getRepresentation(Response response, MediaType media) {
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("projectModule", ProjectModuleModel.class);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Get the object from representation
   * 
   * @param representation
   *          the representation used
   * @param variant
   *          the variant used
   * @return ProjectModule
   */
  public final ProjectModuleModel getObject(Representation representation, Variant variant) {
    ProjectModuleModel projectModuleInput = null;
    if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      try {
        projectModuleInput = new JacksonRepresentation<ProjectModuleModel>(representation, ProjectModuleModel.class).getObject();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return projectModuleInput;
  }

  /**
   * Sets the value of projectModuleId
   * 
   * @param projectModuleId
   *          the projectModuleId to set
   */
  public final void setProjectModuleId(String projectModuleId) {
    this.projectModuleId = projectModuleId;
  }

  /**
   * Gets the projectModuleId value
   * 
   * @return the projectModuleId
   */
  public final String getProjectModuleId() {
    return projectModuleId;
  }

  /**
   * Sets the value of store
   * 
   * @param store
   *          the store to set
   */
  public final void setStore(ProjectModuleStoreInterface store) {
    this.store = store;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public final ProjectModuleStoreInterface getStore() {
    return store;
  }

}
