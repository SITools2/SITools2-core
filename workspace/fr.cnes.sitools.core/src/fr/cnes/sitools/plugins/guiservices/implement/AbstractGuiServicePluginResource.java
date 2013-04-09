package fr.cnes.sitools.plugins.guiservices.implement;

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

import fr.cnes.sitools.common.SitoolsResource;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.plugins.guiservices.implement.model.GuiServicePluginModel;

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
  private SitoolsStore<GuiServicePluginModel> store = null;

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
    getLogger().info(media.toString());
    if (media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
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
   */
  public final GuiServicePluginModel getObject(Representation representation) {
    GuiServicePluginModel projectModuleInput = null;
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
  public final void setStore(SitoolsStore<GuiServicePluginModel> store) {
    this.store = store;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public final SitoolsStore<GuiServicePluginModel> getStore() {
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
