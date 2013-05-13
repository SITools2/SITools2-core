package fr.cnes.sitools.dataset.services;

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
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
  @Put
  public Representation updateServices(Representation representation, Variant variant) {
    try {
      Response response = null;
      ServiceCollectionModel serviceCollection = getStore().retrieve(getParentId());
      if (serviceCollection == null) {
        response = new Response(false, "services.not.found");
      }
      else {

        ServiceCollectionModel servicesInput = getObject(representation);

        ServiceCollectionModel servicesOuput = getStore().update(servicesInput);

        unregisterObserver(servicesOuput);
        registerObserver(servicesOuput);

        response = new Response(true, servicesOuput, ServiceCollectionModel.class, "ServiceCollectionModel");
      }
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public final void describePut(MethodInfo info) {
    this.addStandardPostOrPutRequestInfo(info);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
    info.setDocumentation("Method to update a list of services on a dataset sending its representation.");
    ParameterInfo param = new ParameterInfo("parentId", true, "class", ParameterStyle.TEMPLATE,
        "Parent object identifier");
    info.getRequest().getParameters().add(param);

  }

}
