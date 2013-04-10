package fr.cnes.sitools.dataset.services;

import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.ResponseInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.services.model.ServiceCollectionModel;
import fr.cnes.sitools.dataset.services.model.ServiceEnum;
import fr.cnes.sitools.dataset.services.model.ServiceModel;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource to manage a guiservice on a specific parent id
 * 
 * 
 * @author m.gond
 */
public class ServerServiceResource extends AbstractServerServiceResource {
  /** The resource pluginId */
  private String resourcePluginId;

  @Override
  public void doInit() {
    super.doInit();

    resourcePluginId = (String) this.getRequest().getAttributes().get("resourcePluginId");

  }

  @Override
  public void sitoolsDescribe() {
    setName("ServerServiceResource");
    setDescription("Resource to deal with collection of GuiService plugin");
  }

  @Get
  @Override
  public Representation get(Variant variant) {
    String url = getResourcesUrl() + "/" + resourcePluginId;
    Reference ref = new Reference(url);
    String parameters = getRequest().getResourceRef().getQuery();
    if (parameters != null && !parameters.isEmpty()) {
      ref.setQuery(parameters);
    }
    MediaType mediaType = getMediaType(variant);
    return RIAPUtils.handle(url, Method.GET, mediaType, getContext());
  }

  @Override
  public final void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve a single ResourcePlugin plugin by ID and parent Id");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("guiServiceId", true, "class", ParameterStyle.TEMPLATE,
        "Gui service identifier");
    info.getRequest().getParameters().add(param);
    param = new ParameterInfo("parentId", true, "class", ParameterStyle.TEMPLATE, "Parent object identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
    addStandardResourceCollectionFilterInfo(info);
  }

  /**
   * Update / Validate existing Converters
   * 
   * @param representation
   *          the representation parameter
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateResourcePlugin(Representation representation, Variant variant) {
    Response response = null;
    try {
      ResourceModelDTO serverService = getObjectResourceModel(representation);

      ServiceCollectionModel serviceCollection = getStore().retrieve(getParentId());
      if (!serviceExists(serviceCollection, resourcePluginId)) {
        response = new Response(false, "resource.not.defined");
      }
      else {

        String url = getResourcesUrl() + "/" + resourcePluginId;
        ResourceModelDTO serverServiceOutput = RIAPUtils.updateObject(serverService, url, getContext());

        ServiceModel service = getServiceModel(serviceCollection, resourcePluginId);
        service.setId(serverServiceOutput.getId());
        service.setName(serverServiceOutput.getName());
        service.setDescription(serverServiceOutput.getDescription());
        service.setType(ServiceEnum.SERVER);

        getStore().update(serviceCollection);

        response = new Response(true, serverServiceOutput, ResourceModelDTO.class, "resourcePlugin");
      }
      return getRepresentation(response, variant);
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
   * Describe the Put command
   * 
   * @param info
   *          the info sent
   */
  @Override
  public void describePut(MethodInfo info) {

    // Method
    info.setDocumentation("This method permits to modify a resource attached to an object");
    info.setIdentifier("update_resource_plugin");

    // Request
    this.addStandardPostOrPutRequestInfo(info);

    ParameterInfo pic = new ParameterInfo("pluginId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the resource");
    info.getRequest().getParameters().add(pic);

    // Response 200
    this.addStandardResponseInfo(info);
    // Response 500

    ResponseInfo response = new ResponseInfo("Response when internal server error occurs");
    response.getStatuses().add(Status.SERVER_ERROR_INTERNAL);
    info.getResponses().add(response);
  }

  /**
   * Delete Converter
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteServerService(Variant variant) {
    try {
      ServiceCollectionModel serviceCollection = getStore().retrieve(getParentId());
      Response response = null;
      if (serviceExists(serviceCollection, resourcePluginId)) {

        String url = getResourcesUrl() + "/" + resourcePluginId;
        boolean ok = RIAPUtils.deleteObject(url, getContext());
        if (ok) {
          ServiceModel service = getServiceModel(serviceCollection, resourcePluginId);
          serviceCollection.getServices().remove(service);
          getStore().update(serviceCollection);
          response = new Response(true, "resourceplugin.deleted.success");

        }
        else {
          response = new Response(false, "resourceplugin.deleted.failure");
        }

      }
      else {
        response = new Response(false, "resource.not.defined");
      }
      return getRepresentation(response, variant);

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
   * Describe the Delete command
   * 
   * @param info
   *          the info sent
   */
  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("This method delete a resource attached to an object");
    info.setIdentifier("delete_resource_plugin");

    this.addStandardGetRequestInfo(info);

    ParameterInfo pic = new ParameterInfo("pluginId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the resource");
    info.getRequest().getParameters().add(pic);

    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  public String getResourcesUrl() {
    SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();
    return settings.getString(Consts.APP_APPLICATIONS_URL) + "/" + getParentId()
        + settings.getString(Consts.APP_RESOURCES_URL);
  }

  

}
