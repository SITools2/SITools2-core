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
package fr.cnes.sitools.plugins.guiservices.declare;

import java.util.List;
import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.plugins.guiservices.declare.model.GuiServiceModel;

/**
 * Base class for resource of management of GuiService
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class AbstractGuiServiceResource extends SitoolsResource {

  /** parent application */
  private GuiServiceApplication application = null;

  /** store */
  private GuiServiceStoreInterface store = null;

  /** GuiService identifier parameter */
  private String guiServiceId = null;

  @Override
  public final void doInit() {
    super.doInit();

    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    getVariants().add(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    application = (GuiServiceApplication) getApplication();
    setStore(application.getStore());

    setGuiServiceId((String) this.getRequest().getAttributes().get("guiServiceId"));
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
    xstream.alias("guiService", GuiServiceModel.class);

    XstreamRepresentation<Response> rep = new XstreamRepresentation<Response>(media, response);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Get the object from representation
   * 
   * @param representation
   *          the representation used
   * @return GuiService
   */
  public final GuiServiceModel getObject(Representation representation) {
    GuiServiceModel projectModuleInput = null;
    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the GuiService bean
      XstreamRepresentation<GuiServiceModel> repXML = new XstreamRepresentation<GuiServiceModel>(representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      xstream.autodetectAnnotations(false);
      xstream.alias("guiService", GuiServiceModel.class);
      repXML.setXstream(xstream);
      projectModuleInput = repXML.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      projectModuleInput = new JacksonRepresentation<GuiServiceModel>(representation, GuiServiceModel.class)
          .getObject();
    }
    return projectModuleInput;
  }

  /**
   * Sets the value of projectModuleId
   * 
   * @param projectModuleId
   *          the projectModuleId to set
   */
  public final void setGuiServiceId(String projectModuleId) {
    this.guiServiceId = projectModuleId;
  }

  /**
   * Gets the projectModuleId value
   * 
   * @return the projectModuleId
   */
  public final String getGuiServiceId() {
    return guiServiceId;
  }

  /**
   * Sets the value of store
   * 
   * @param store
   *          the store to set
   */
  public final void setStore(GuiServiceStoreInterface store) {
    this.store = store;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public final GuiServiceStoreInterface getStore() {
    return store;
  }

  /**
   * get all guiServices or the selected guiService
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Get
  public Representation retrieveGuiService(Variant variant) {
    if (getGuiServiceId() != null) {
      GuiServiceModel guiService = getStore().retrieve(getGuiServiceId());
      if (guiService != null) {
        Response response = new Response(true, guiService, GuiServiceModel.class, "guiService");
        return getRepresentation(response, variant);
      }
      else {
        Response response = new Response(false, "NO_GUI_SERVICE_FOUND");
        return getRepresentation(response, variant);
      }
    }
    else {
      ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
      List<GuiServiceModel> guiServices = getStore().getList(filter);
      int total = guiServices.size();
      guiServices = getStore().getPage(filter, guiServices);
      Response response = new Response(true, guiServices, GuiServiceModel.class, "guiServices");
      response.setTotal(total);
      trace(Level.FINE, "View available GUI services");
      return getRepresentation(response, variant);
    }
  }

}
