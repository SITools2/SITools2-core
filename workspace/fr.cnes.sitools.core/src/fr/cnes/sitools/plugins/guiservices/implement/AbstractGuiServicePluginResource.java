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
package fr.cnes.sitools.plugins.guiservices.implement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsMediaType;
import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.notification.business.NotificationManager;
import fr.cnes.sitools.notification.model.RestletObserver;
import fr.cnes.sitools.plugins.guiservices.declare.model.GuiServiceModel;
import fr.cnes.sitools.plugins.guiservices.implement.model.GuiServicePluginModel;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Abstract resource for GuiPluginService
 * 
 * 
 * @author m.gond
 */
public abstract class AbstractGuiServicePluginResource extends SitoolsResource {
  /** parent application */
  private GuiServicePluginApplication application = null;

  /** store */
  private GuiServicePluginStoreInterface store = null;

  /** GuiService identifier parameter */
  private String guiServicePluginId = null;

  /** The parent Id */
  private String parentId;

  @Override
  public final void doInit() {
    super.doInit();

    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    getVariants().add(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    application = (GuiServicePluginApplication) getApplication();
    setStore(application.getStore());

    setGuiServicePluginId((String) this.getRequest().getAttributes().get("guiServiceId"));

    setParentId((String) this.getRequest().getAttributes().get("parentId"));
  }

  @Get
  @Override
  public Representation get(Variant variant) {

    if (getGuiServicePluginId() != null) {
      GuiServicePluginModel guiService = getStore().retrieve(getGuiServicePluginId());
      addCurrentGuiServiceModelDescription(guiService);
      Response response = new Response(true, guiService, GuiServicePluginModel.class, "guiServicePlugin");
      return getRepresentation(response, variant);
    }
    else {
      ResourceCollectionFilter filter = new ResourceCollectionFilter(getRequest());
      List<GuiServicePluginModel> resourceList = getStore().getList(filter);
      List<GuiServicePluginModel> guiServicePluginArray = new ArrayList<GuiServicePluginModel>();
      for (GuiServicePluginModel resource : resourceList) {
        if (resource.getParent().equals(getParentId())) {
          // Response
          // fillParameters(resources);
          guiServicePluginArray.add(resource);
        }
      }
      int total = guiServicePluginArray.size();
      guiServicePluginArray = getStore().getPage(filter, guiServicePluginArray);
      addCurrentGuiServiceModelDescription(guiServicePluginArray);
      Response response = new Response(true, guiServicePluginArray, GuiServicePluginModel.class, "guiServicePlugins");
      response.setTotal(total);
      return getRepresentation(response, variant);
    }

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
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)
      || media.isCompatible(SitoolsMediaType.APPLICATION_JAVA_OBJECT_SITOOLS_MODEL)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("guiServicePlugin", GuiServicePluginModel.class);

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
   * @throws IOException
   *           if there is an error while parsing the java representation of the object
   */
  public final GuiServicePluginModel getObject(Representation representation) throws IOException {
    GuiServicePluginModel projectModuleInput = null;
    if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      @SuppressWarnings("unchecked")
      ObjectRepresentation<GuiServicePluginModel> obj = (ObjectRepresentation<GuiServicePluginModel>) representation;
      projectModuleInput = obj.getObject();
    }
    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the GuiService bean
      XstreamRepresentation<GuiServicePluginModel> repXML = new XstreamRepresentation<GuiServicePluginModel>(
        representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      xstream.autodetectAnnotations(false);
      xstream.alias("guiServicePlugin", GuiServicePluginModel.class);
      repXML.setXstream(xstream);
      projectModuleInput = repXML.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      projectModuleInput = new JacksonRepresentation<GuiServicePluginModel>(representation, GuiServicePluginModel.class)
        .getObject();
    }
    return projectModuleInput;
  }

  /**
   * Register as observer
   * 
   * @param input
   *          The ConverterChainedModel
   */
  public final void registerObserver(GuiServicePluginModel input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }

    RestletObserver observer = new RestletObserver();
    // passage RIAP
    String uriToNotify = RIAPUtils.getRiapBase() + getSitoolsSetting(Consts.APP_DATASETS_URL) + "/" + input.getParent()
      + getSitoolsSetting(Consts.APP_GUI_SERVICES_URL) + "/" + input.getId() + "/notify";
    observer.setUriToNotify(uriToNotify);
    observer.setMethodToNotify("PUT");
    observer.setUuid("GuiServicePlugin." + input.getId());

    notificationManager.addObserver(input.getParent(), observer);

  }

  /**
   * Unregister as Observer
   * 
   * @param input
   *          ConverterChainedModel Objet
   */
  public final void unregisterObserver(GuiServicePluginModel input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }
    notificationManager.removeObserver(input.getParent(), "GuiServicePlugin." + input.getId());
  }

  /**
   * Add the current {@link GuiServiceModel} version to the given List of {@link GuiServicePluginModel}
   * 
   * @param guiServicePluginArray
   *          a {@link List} of {@link GuiServicePluginModel}
   */
  private void addCurrentGuiServiceModelDescription(List<GuiServicePluginModel> guiServicePluginArray) {
    List<GuiServiceModel> serviceModels = RIAPUtils.getListOfObjects(
      getSettings().getString(Consts.APP_GUI_SERVICES_URL), getContext());
    for (GuiServicePluginModel guiServicePluginModel : guiServicePluginArray) {
      addCurrentGuiServiceModelDescription(guiServicePluginModel, serviceModels);
    }

  }

  /**
   * Add the current {@link GuiServiceModel} version to the {@link GuiServicePluginModel}
   * 
   * @param guiService
   *          a {@link GuiServicePluginModel}
   */
  private void addCurrentGuiServiceModelDescription(GuiServicePluginModel guiService) {
    if (guiService != null) {
      List<GuiServiceModel> serviceModels = RIAPUtils.getListOfObjects(
        getSettings().getString(Consts.APP_GUI_SERVICES_URL), getContext());
      addCurrentGuiServiceModelDescription(guiService, serviceModels);
    }
  }

  /**
   * Add the current {@link GuiServiceModel} version to the {@link GuiServicePluginModel}
   * 
   * @param guiService
   *          a {@link GuiServicePluginModel}
   * @param serviceModels
   *          the list of {@link GuiServiceModel} to get the version from
   */
  private void addCurrentGuiServiceModelDescription(GuiServicePluginModel guiService,
    List<GuiServiceModel> serviceModels) {
    GuiServiceModel guiServiceModel = null;
    if (serviceModels != null) {
      for (GuiServiceModel model : serviceModels) {
        if (model.getXtype().equals(guiService.getXtype())) {
          guiServiceModel = model;
          break;
        }
      }

      if (guiServiceModel != null) {
        guiService.setCurrentGuiServiceVersion(guiServiceModel.getVersion());
      }
      else {
        guiService.setCurrentGuiServiceVersion("GUI_SERVICE_MODEL_NOT_FOUND");
      }
    }
  }

  /**
   * 
   * 
   * Gets the guiServicePluginId value
   * 
   * @return the guiServicePluginId
   */
  public String getGuiServicePluginId() {
    return guiServicePluginId;
  }

  /**
   * Sets the value of guiServicePluginId
   * 
   * @param guiServicePluginId
   *          the guiServicePluginId to set
   */
  public void setGuiServicePluginId(String guiServicePluginId) {
    this.guiServicePluginId = guiServicePluginId;
  }

  /**
   * Sets the value of store
   * 
   * @param store
   *          the store to set
   */
  public final void setStore(GuiServicePluginStoreInterface store) {
    this.store = store;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public final GuiServicePluginStoreInterface getStore() {
    return store;
  }

  /**
   * Gets the parentId value
   * 
   * @return the parentId
   */
  public String getParentId() {
    return parentId;
  }

  /**
   * Sets the value of parentId
   * 
   * @param parentId
   *          the parentId to set
   */
  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

}
