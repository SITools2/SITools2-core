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
package fr.cnes.sitools.plugins.resources;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.ResponseInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.application.SitoolsParameterizedApplication;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.registry.AppRegistryApplication;

/**
 * Resource handling resource plugins on applications instances
 * 
 * @author m.marseille (AKKA Technologies)
 */
public final class ResourcePluginResource extends AbstractResourcePluginResource {

  /** store */
  private SitoolsStore<ResourceModel> store = null;

  @Override
  public void doInit() {
    super.doInit();

    store = ((ResourcePluginApplication) getApplication()).getStore();

  }

  @Override
  public void sitoolsDescribe() {
    setName("ResourcePluginResource");
    setDescription("Resource handling resource plugins on applications instances");
    setNegotiated(false);
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
  public Representation newResourcePlugin(Representation representation, Variant variant) {
    try {
      ResourceModelDTO resourceInputDTO = getObject(representation);

      ResourceModel resourceInput = getResourceModelFromDTO(resourceInputDTO);

      // this.fromListToMap(resourceInput);

      // // VALIDATION PART
      Set<ConstraintViolation> constraints = checkValidity(resourceInput);
      if (constraints != null) {
        ConstraintViolation[] array = new ConstraintViolation[constraints.size()];
        array = constraints.toArray(array);

        Response response = new Response(false, array, ConstraintViolation.class, "constraints");
        return getRepresentation(response, variant);
      }

      // set converterChainedModel id
      if (resourceInput.getId() == null || resourceInput.getId().equals("")) {
        resourceInput.setId(UUID.randomUUID().toString());
      }
      // Business service
      resourceInput.setParent(getParentId());

      // Response
      // fillParametersMap(resourceInput);

      ResourceModel resourceOutput = getStore().create(resourceInput);

      // register observer
      registerObserver(resourceOutput);

      // Add to dynamic list of resources
      SitoolsParameterizedApplication app = (SitoolsParameterizedApplication) ((ResourcePluginApplication) getApplication())
          .getSettings().getAppRegistry().getApplication(getParentId());
      if (null != app && app.isStarted()) {
        refreshApplication(app);
      }
      // fillParameters(resourceOutput);

      ResourceModelDTO resourceOutputDTO = getResourceModelDTO(resourceOutput);

      Response response = new Response(true, resourceOutputDTO, ResourceModelDTO.class, "resourcePlugin");
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
   * Describe the POST command
   * 
   * @param info
   *          the info sent
   * @param path
   *          url attachment of the resource
   */
  @Override
  public void describePost(MethodInfo info, String path) {

    info.setDocumentation("Creates a new resource attached to an object");

    // Method
    // info.setDocumentation("This method permits to create a resource attached to a dataset");
    info.setIdentifier("create_resource_plugin");

    // Request
    this.addStandardPostOrPutRequestInfo(info);

    // Response 200
    this.addStandardResponseInfo(info);

    // Response 500
    ResponseInfo response = new ResponseInfo("Response when internal server error occurs");
    response.getStatuses().add(Status.SERVER_ERROR_INTERNAL);
    info.getResponses().add(response);

  }

  // /**
  // * Fill in the map from what is sent / clear the list
  // * @param resourceInput the input sent
  // */
  // private void fillParametersMap(ResourceModel resourceInput) {
  // resourceInput.getParametersMap().clear();
  // for (ParameterizedResourcesParameter param : resourceInput.getParameters()) {
  // resourceInput.getParametersMap().put(param.getName(), param);
  // }
  // resourceInput.getParameters().clear();
  // }

  // /**
  // * Fill in the map from what is sent / clear the list
  // *
  // * @param resourceInput
  // * the input sent
  // */
  // private void fillParameters(ResourceModel resourceInput) {
  // if (resourceInput != null) {
  // resourceInput.setParameters(new ArrayList<ResourceParameter>(resourceInput.getParametersMap().values()));
  // }
  // resourceInput.getParametersMap().clear();
  // }

  @Get
  @Override
  public Representation get(Variant variant) {

    if (getResourcePluginId() != null) {
      ResourceModel resource = getStore().retrieve(getResourcePluginId());
      ResourceModelDTO resourceOut = getResourceModelDTO(resource);
      // Response
      // fillParameters(resource);
      addCurrentClassDescription(resourceOut);
      Response response = new Response(true, resourceOut, ResourceModelDTO.class, "resourcePlugin");
      return getRepresentation(response, variant);
    }
    else {
      ResourceCollectionFilter filter = new ResourceCollectionFilter(getRequest());
      List<ResourceModel> resourceList = getStore().getList(filter);
      List<ResourceModel> resourceArray = new ArrayList<ResourceModel>();
      for (ResourceModel resource : resourceList) {
        if (resource.getParent().equals(getParentId())) {
          // Response
          // fillParameters(resources);
          resourceArray.add(resource);
        }
      }
      int total = resourceArray.size();
      resourceArray = getStore().getPage(filter, resourceArray);

      List<ResourceModelDTO> resourceDTOArray = new ArrayList<ResourceModelDTO>();
      for (ResourceModel resource : resourceArray) {
        resourceDTOArray.add(getResourceModelDTO(resource));
      }

      addCurrentClassDescription(resourceDTOArray);
      Response response = new Response(true, resourceDTOArray, ResourceModelDTO.class, "resourcePlugins");
      response.setTotal(total);
      return getRepresentation(response, variant);
    }

  }

  /**
   * GET method description
   * 
   * @param info
   *          WADL method information
   * @param path
   *          url attachment of the resource
   */
  public void describeGet(MethodInfo info, String path) {
    this.addStandardGetRequestInfo(info);
    this.addStandardResponseInfo(info);
    if (path.endsWith("{pluginId}")) {
      info.setDocumentation("GET " + path + ": Method to retrieve a specified resource attached to an object");
      ParameterInfo pic = new ParameterInfo("pluginId", true, "xs:string", ParameterStyle.TEMPLATE,
          "Identifier of the resource");
      info.getRequest().getParameters().add(pic);
    }
    else {
      info.setDocumentation("GET " + path + ": Method to retrieve all resources for an object");
    }

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
    ResourceModel resourceOutput = null;
    try {
      if (representation != null) {
        ResourceModelDTO resourceInputDTO = getObject(representation);

        ResourceModel resourceInput = getResourceModelFromDTO(resourceInputDTO);

        // this.fromListToMap(resourceInput);

        // // VALIDATION PART
        Set<ConstraintViolation> constraints = checkValidity(resourceInput);
        if (constraints != null) {
          ConstraintViolation[] array = new ConstraintViolation[constraints.size()];
          array = constraints.toArray(array);

          Response response = new Response(false, array, ConstraintViolation.class, "constraints");
          return getRepresentation(response, variant);
        }

        // Business service
        resourceOutput = getStore().update(resourceInput);

        // Register ConverterResource as observer of datasets resources
        unregisterObserver(resourceOutput);

        registerObserver(resourceOutput);
      }

      if (resourceOutput != null) {
        // Response

        // Add to dynamic list of resources
        SitoolsParameterizedApplication app = (SitoolsParameterizedApplication) ((ResourcePluginApplication) getApplication())
            .getSettings().getAppRegistry().getApplication(getParentId());

        // if (app == null) {
        // // throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Resource parent application not found");
        // // nothing
        // }
        // else

        if (app != null && app.isStarted()) {
          refreshApplication(app);
        }

        // fillParameters(resourceOutput);
        ResourceModelDTO resourceOutputDTO = getResourceModelDTO(resourceOutput);

        Response response = new Response(true, resourceOutputDTO, ResourceModelDTO.class, "resourcePlugin");
        return getRepresentation(response, variant);

      }
      else {
        // Response
        Response response = new Response(false, "Can not validate resource plugin");
        return getRepresentation(response, variant);

      }

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
  public Representation deleteConverterChainedModel(Variant variant) {
    try {
      ResourceModel resource = getStore().retrieve(getResourcePluginId());
      Response response = null;
      if (resource != null) {

        // Business service
        getStore().delete(getResourcePluginId());

        // Response
        response = new Response(true, "resourceplugin.deleted.success");

        // Register ConverterChainedModel as observer of datasets resources
        unregisterObserver(resource);

        // Add to dynamic list of resources
        SitoolsParameterizedApplication app = (SitoolsParameterizedApplication) ((ResourcePluginApplication) getApplication())
            .getSettings().getAppRegistry().getApplication(getParentId());
        refreshApplication(app);

      }
      else {
        response = new Response(false, "resourceplugin.deleted.failure");
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

  /**
   * register the new SitoolsParameterizedResource to the application
   * 
   * @param app
   *          the application
   */
  private void refreshApplication(SitoolsParameterizedApplication app) {
    if (app != null) {
      AppRegistryApplication registry = ((SitoolsApplication) getApplication()).getSettings().getAppRegistry();
      app.setInboundRoot(app.createInboundRoot());
      registry.reattachApplication(app);
    }
  }

  /**
   * Add current Class description for comparison with stored application descriptions
   * 
   * @param resourcePluginModel
   *          the app plugins
   */
  private void addCurrentClassDescription(List<ResourceModelDTO> resourcePluginModel) {
    if (resourcePluginModel.size() > 0) {
      for (ResourceModelDTO app : resourcePluginModel) {
        addCurrentClassDescription(app);
      }
    }

  }

  /**
   * Add the current class descriptions of the converters in the chained model
   * 
   * @param resource
   *          the chained model
   */
  private void addCurrentClassDescription(ResourceModelDTO resource) {
    if (resource != null && resource.getClassName() != null) {
      try {
        @SuppressWarnings("unchecked")
        Class<ResourceModel> resourceClass = (Class<ResourceModel>) Class.forName(resource.getClassName());
        Constructor<ResourceModel> resourceConstructor = resourceClass.getDeclaredConstructor();
        ResourceModel object = resourceConstructor.newInstance();
        resource.setCurrentClassAuthor(object.getClassAuthor());
        resource.setCurrentClassVersion(object.getClassVersion());
      }
      catch (ClassNotFoundException e) {
        resource.setCurrentClassAuthor("CLASS_NOT_FOUND");
        resource.setCurrentClassVersion("CLASS_NOT_FOUND");
        getLogger().severe(e.getMessage());
      }
      catch (SecurityException e) {
        getLogger().severe(e.getMessage());
      }
      catch (NoSuchMethodException e) {
        getLogger().severe(e.getMessage());
      }
      catch (IllegalArgumentException e) {
        getLogger().severe(e.getMessage());
      }
      catch (InstantiationException e) {
        getLogger().severe(e.getMessage());
      }
      catch (IllegalAccessException e) {
        getLogger().severe(e.getMessage());
      }
      catch (InvocationTargetException e) {
        getLogger().severe(e.getMessage());
      }
    }
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public SitoolsStore<ResourceModel> getStore() {
    return store;
  }

}
