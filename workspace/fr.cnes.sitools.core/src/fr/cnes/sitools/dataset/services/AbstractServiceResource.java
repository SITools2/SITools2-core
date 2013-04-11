package fr.cnes.sitools.dataset.services;

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
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.dataset.services.model.ServiceCollectionModel;
import fr.cnes.sitools.dataset.services.model.ServiceModel;

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
  private SitoolsStore<ServiceCollectionModel> store = null;

  /** GuiService identifier parameter */
  private String guiServicePluginId = null;

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
    getLogger().info(media.toString());
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
  public final void setStore(SitoolsStore<ServiceCollectionModel> store) {
    this.store = store;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public final SitoolsStore<ServiceCollectionModel> getStore() {
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

  public boolean serviceExists(ServiceCollectionModel serviceCollection, String idService) {
    return getServiceModel(serviceCollection, idService) != null;
  }

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
}
