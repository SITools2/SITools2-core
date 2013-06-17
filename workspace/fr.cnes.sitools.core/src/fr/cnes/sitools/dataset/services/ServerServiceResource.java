     /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.services.model.ServiceCollectionModel;
import fr.cnes.sitools.dataset.services.model.ServiceModel;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
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
    setDescription("Resource to deal with a single server service");
  }

  /**
   * Retrieve an existing Server service
   * 
   * @param variant
   *          client preferred media type
   * @return Representation the server service found
   */
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
    info.setDocumentation("Method to retrieve a single server service by its ID and parent Id");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("resourcePluginId", true, "class", ParameterStyle.TEMPLATE,
        "Server service identifier");
    info.getRequest().getParameters().add(param);
    param = new ParameterInfo("parentId", true, "class", ParameterStyle.TEMPLATE, "Parent object identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
    addStandardResourceCollectionFilterInfo(info);
  }

  /**
   * Update / Validate existing Server service
   * 
   * @param representation
   *          the representation parameter
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateServerService(Representation representation, Variant variant) {
    Response response = null;
    try {
      ResourceModelDTO serverService = getObjectResourceModel(representation);

      ServiceCollectionModel serviceCollection = getStore().retrieve(getParentId());
      if (!serviceExists(serviceCollection, resourcePluginId)) {
        response = new Response(false, "resource.not.defined");
      }
      else {

        String url = getResourcesUrl() + "/" + resourcePluginId;
        Response responsePersist = handleResourceModelCall(serverService, url, getContext(), Method.PUT);
        if (responsePersist.isSuccess()) {
          // if the response is a success we have a ResourceModelDTO in return and it has been successfully added
          if (responsePersist.getItem() == null) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Empty ResourceModelDTO in return");
          }
          ResourceModelDTO serverServiceOutput = (ResourceModelDTO) responsePersist.getItem();

          ServiceModel service = getServiceModel(serviceCollection, resourcePluginId);
          populateServiceModel(serverServiceOutput, service);

          getStore().update(serviceCollection);

        }
        response = responsePersist;
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

  /**
   * Describe the Put command
   * 
   * @param info
   *          the info sent
   */
  @Override
  public void describePut(MethodInfo info) {

    // Method
    info.setDocumentation("Method to modify a server service attached to a dataset");
    info.setIdentifier("update_resource_plugin");

    // Request
    this.addStandardPostOrPutRequestInfo(info);

    ParameterInfo pic = new ParameterInfo("resourcePluginId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the resource");
    info.getRequest().getParameters().add(pic);

    pic = new ParameterInfo("parentId", true, "class", ParameterStyle.TEMPLATE, "Parent object identifier");
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
    info.setDocumentation("This method deletes a server service attached to a dataset");
    info.setIdentifier("delete_resource_plugin");

    this.addStandardGetRequestInfo(info);

    ParameterInfo pic = new ParameterInfo("pluginId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the resource");
    info.getRequest().getParameters().add(pic);

    pic = new ParameterInfo("parentId", true, "class", ParameterStyle.TEMPLATE, "Parent object identifier");
    info.getRequest().getParameters().add(pic);

    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
