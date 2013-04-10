package fr.cnes.sitools.dataset.services;

import java.util.logging.Level;

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
import fr.cnes.sitools.dataset.services.model.ServiceCollectionModel;

/**
 * Resource to manage collection of guiservices on a specific parent id
 * 
 * 
 * @author m.gond
 */
public class ServiceCollectionResource extends AbstractServiceResource {

  @Override
  public void sitoolsDescribe() {
    setName("ServiceCollectionResource");
    setDescription("Resource to deal with collection of services on a dataset");
  }

  @Get
  @Override
  public Representation get(Variant variant) {
    Response response = null;
    ServiceCollectionModel serviceCollection = getStore().retrieve(getParentId());
    if (serviceCollection == null) {
      serviceCollection = new ServiceCollectionModel();
      serviceCollection.setId(getParentId());
    }
    response = new Response(true, serviceCollection, ServiceCollectionModel.class, "ServiceCollectionModel");

    return getRepresentation(response, variant);
  }

  @Override
  public final void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the list of services on a dataset");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("parentId", true, "class", ParameterStyle.TEMPLATE,
        "Parent object identifier");
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
  public Representation newGuiServicePluginPlugin(Representation representation, Variant variant) {
    try {

      ServiceCollectionModel servicesInput = getObject(representation);

      // Business service
      servicesInput.setId(getParentId());

      ServiceCollectionModel servicesOuput = getStore().create(servicesInput);

      Response response = new Response(true, servicesOuput, ServiceCollectionModel.class, "services");
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
    ParameterInfo param = new ParameterInfo("parentId", true, "class", ParameterStyle.TEMPLATE,
        "Parent object identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
