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
package fr.cnes.sitools.dataset.services;

import java.util.ArrayList;
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
import fr.cnes.sitools.dataset.services.model.ServiceCollectionModel;
import fr.cnes.sitools.dataset.services.model.ServiceModel;
import fr.cnes.sitools.notification.business.NotificationManager;
import fr.cnes.sitools.notification.model.RestletObserver;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Abstract resource for GuiPluginService
 * 
 * 
 * @author m.gond
 */
public abstract class AbstractServiceResource extends SitoolsResource {
  /** parent application */
  private ServiceApplication application = null;

  /** store */
  private ServiceStoreInterface store = null;

  /** The parent Id */
  private String parentId;

  @Override
  public void doInit() {
    super.doInit();

    // Declares the two variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    getVariants().add(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    application = (ServiceApplication) getApplication();
    setStore(application.getStore());

    setParentId((String) this.getRequest().getAttributes().get("parentId"));
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
  public Representation getRepresentation(Response response, MediaType media) {
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Response>(response);
    }

    XStream xstream = XStreamFactory.getInstance().getXStream(media, getContext());
    configure(xstream, response);
    xstream.alias("guiServicePlugin", ServiceCollectionModel.class);

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
  public final ServiceCollectionModel getObject(Representation representation) {
    ServiceCollectionModel projectModuleInput = null;
    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the GuiService bean
      XstreamRepresentation<ServiceCollectionModel> repXML = new XstreamRepresentation<ServiceCollectionModel>(
          representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      xstream.autodetectAnnotations(false);
      xstream.alias("guiServicePlugin", ServiceCollectionModel.class);
      repXML.setXstream(xstream);
      projectModuleInput = repXML.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      projectModuleInput = new JacksonRepresentation<ServiceCollectionModel>(representation,
          ServiceCollectionModel.class).getObject();
    }
    return projectModuleInput;
  }

  /**
   * Sets the value of store
   * 
   * @param store
   *          the store to set
   */
  public final void setStore(ServiceStoreInterface store) {
    this.store = store;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public final ServiceStoreInterface getStore() {
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

  /**
   * Check if the service exists in the given {@link ServiceCollectionModel}
   * 
   * @param serviceCollection
   *          the list of service
   * @param idService
   *          the id of the service to look for
   * @return true if the service exists, false otherwise
   */
  public boolean serviceExists(ServiceCollectionModel serviceCollection, String idService) {
    return getServiceModel(serviceCollection, idService) != null;
  }

  /**
   * Get a service by its name in the given {@link ServiceCollectionModel}
   * 
   * @param serviceCollection
   *          the list of service
   * @param idService
   *          the id of the service to look for
   * @return the {@link ServiceModel} if it exists, null otherwise
   */
  public ServiceModel getServiceModel(ServiceCollectionModel serviceCollection, String idService) {
    if (serviceCollection == null) {
      return null;
    }
    List<ServiceModel> services = serviceCollection.getServices();
    ServiceModel out = null;
    for (ServiceModel serviceModel : services) {
      if (serviceModel.getId().equals(idService)) {
        out = serviceModel;
        break;
      }
    }
    return out;
  }

  /**
   * Register as observer
   * 
   * @param input
   *          The ConverterChainedModel
   */
  public final void registerObserver(ServiceCollectionModel input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }

    RestletObserver observer = new RestletObserver();
    // passage RIAP
    String uriToNotify = RIAPUtils.getRiapBase() + getSitoolsSetting(Consts.APP_DATASETS_URL) + "/" + input.getId()
        + getSitoolsSetting(Consts.APP_SERVICES_URL) + "/notify";
    observer.setUriToNotify(uriToNotify);
    observer.setMethodToNotify("PUT");
    observer.setUuid("ServiceCollectionModel." + input.getId());

    notificationManager.addObserver(input.getId(), observer);

  }

  /**
   * Unregister as Observer
   * 
   * @param input
   *          ConverterChainedModel Objet
   */
  public final void unregisterObserver(ServiceCollectionModel input) {
    NotificationManager notificationManager = application.getSettings().getNotificationManager();
    if (notificationManager == null) {
      getLogger().warning("NotificationManager is null");
      return;
    }
    notificationManager.removeObserver(input.getId(), "ServiceCollectionModel." + input.getId());
  }

  /**
   * Get the ServiceCollectionModel associated to the current dataset or create one if it doesn't already exist
   * 
   * @return the ServiceCollectionModel associated to the current dataset
   */
  protected ServiceCollectionModel getServiceCollectionModel() {
    ServiceCollectionModel services = getStore().retrieve(getParentId());
    // create a new ServiceCollectionModel if it doesn't already exists
    if (services == null) {
      services = new ServiceCollectionModel();
      services.setId(getParentId());
      services.setServices(new ArrayList<ServiceModel>());
      getStore().create(services);
      
      //register observer
      registerObserver(services);

    }
    return services;
  }

}
