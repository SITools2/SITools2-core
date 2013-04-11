package fr.cnes.sitools.dataset.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.dataset.services.model.ServiceCollectionModel;
import fr.cnes.sitools.dataset.services.model.ServiceEnum;
import fr.cnes.sitools.dataset.services.model.ServiceModel;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource to manage a guiservice on a specific parent id
 * 
 * 
 * @author m.gond
 */
public class ServerServiceCollectionResource extends AbstractServerServiceResource {

  @Override
  public void sitoolsDescribe() {
    setName("ServerServiceResource");
    setDescription("Resource to deal with collection of GuiService plugin");
  }

  @Get
  @Override
  public Representation get(Variant variant) {
    String url = getResourcesUrl();
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
    info.setDocumentation("Method to retrieve a single GuiService plugin by ID and parent Id");
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
   * Create / attach a new resource to an application
   * 
   * @param representation
   *          The representation parameter
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Post
  public Representation newServerService(Representation representation, Variant variant) {
    try {
      Response response = null;
      ResourceModelDTO serverService = getObjectResourceModel(representation);

      String url = getResourcesUrl();
      Response responsePersist = handleResourceModelCall(serverService, url, getContext(), Method.POST);

      if (responsePersist.isSuccess()) {
        // if the response is a success we have a ResourceModelDTO in return and it has been successfully added
        if (responsePersist.getItem() == null) {
          throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Empty ResourceModelDTO in return");
        }
        ResourceModelDTO serverServiceOutput = (ResourceModelDTO) responsePersist.getItem();
        ServiceCollectionModel services = getStore().retrieve(getParentId());

        if (services == null) {
          services = new ServiceCollectionModel();
          services.setId(getParentId());
          getStore().create(services);
        }

        ServiceModel service = new ServiceModel();
        service.setId(serverServiceOutput.getId());
        service.setName(serverServiceOutput.getName());
        service.setDescription(serverServiceOutput.getDescription());
        service.setType(ServiceEnum.SERVER);

        if (services.getServices() == null) {
          services.setServices(new ArrayList<ServiceModel>());
        }
        services.getServices().add(service);
        getStore().update(services);
      }
      response = responsePersist;
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      e.printStackTrace();
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public final void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a new list of services sending its representation.");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo param = new ParameterInfo("parentId", true, "class", ParameterStyle.TEMPLATE,
        "Parent object identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
