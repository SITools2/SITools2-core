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

import java.io.IOException;
import java.util.logging.Level;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.services.model.ServiceCollectionModel;
import fr.cnes.sitools.dataset.services.model.ServiceModel;
import fr.cnes.sitools.plugins.guiservices.implement.model.GuiServicePluginModel;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Resource to manage a guiservice on a specific parent id
 * 
 * 
 * @author m.gond
 */
public class GuiServiceResource extends AbstractGuiServiceResource {
  /** The resource pluginId */
  private String guiServiceId;

  @Override
  public void doInit() {
    super.doInit();

    guiServiceId = (String) this.getRequest().getAttributes().get("guiServiceId");

  }

  @Override
  public void sitoolsDescribe() {
    setName("GuiServiceResource");
    setDescription("Resource to deal with collection of GuiService plugin");
  }

  /**
   * Get a GuiServicePluginModel from its id
   * 
   * @param variant
   *          the variant needed
   * @return the representation of the GuiServicePluginModel with the given variant
   */
  @Get
  public Representation getGuiService(Variant variant) {
    String url = getGuiServicesUrl() + "/" + guiServiceId;
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
   * Update / Validate existing GuiServicePluginModel
   * 
   * @param representation
   *          the representation parameter
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateGuiService(Representation representation, Variant variant) {
    Response response = null;
    try {
      GuiServicePluginModel guiServiceInput = getObjectGuiServicePluginModel(representation);

      ServiceCollectionModel serviceCollection = getStore().retrieve(getParentId());
      if (!serviceExists(serviceCollection, guiServiceId)) {
        response = new Response(false, "guiService.not.defined");
      }
      else {

        String url = getGuiServicesUrl() + "/" + guiServiceId;
        GuiServicePluginModel guiServiceOutput = RIAPUtils.updateObject(guiServiceInput, url, getContext());

        ServiceModel service = getServiceModel(serviceCollection, guiServiceId);
        populateGuiServiceModel(guiServiceOutput, service);

        getStore().update(serviceCollection);
        trace(Level.INFO, "Update the dataset service " + guiServiceOutput.getName() + " for the dataset - id : " + guiServiceOutput.getParent());
        response = new Response(true, guiServiceOutput, GuiServicePluginModel.class, "guiServicePlugin");
      }
      return getRepresentation(response, variant);
    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot update dataset GUI service ");
      getLogger().log(Level.INFO, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot update dataset GUI service ");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public final void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify a single gui service sending its new representation");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo param = new ParameterInfo("guiServiceId", true, "class", ParameterStyle.TEMPLATE,
        "gui service identifier");
    info.getRequest().getParameters().add(param);
    param = new ParameterInfo("parentId", true, "class", ParameterStyle.TEMPLATE, "Parent object identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

  /**
   * Delete a GuiServicePluginModel
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteGuiService(Variant variant) {
    try {
      ServiceCollectionModel serviceCollection = getStore().retrieve(getParentId());
      Response response = null;
      if (serviceExists(serviceCollection, guiServiceId)) {

        String url = getGuiServicesUrl() + "/" + guiServiceId;
        boolean ok = RIAPUtils.deleteObject(url, getContext());
        if (ok) {
          ServiceModel service = getServiceModel(serviceCollection, guiServiceId);
          serviceCollection.getServices().remove(service);
          getStore().update(serviceCollection);
          response = new Response(true, "guiService.deleted.success");
          trace(Level.INFO, "Delete the dataset service " + service.getName() + " for the dataset - id : " + getParentId());

        }
        else {
          response = new Response(false, "guiService.deleted.failure");
          trace(Level.INFO, "Cannot delete the dataset GUI service ");
        }

      }
      else {
        response = new Response(false, "guiService.not.defined");
        trace(Level.INFO, "Cannot delete the dataset GUI service ");
      }
      return getRepresentation(response, variant);

    }
    catch (ResourceException e) {
      trace(Level.INFO, "Cannot delete the dataset GUI service ");
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      trace(Level.INFO, "Cannot delete the dataset GUI service ");
      getLogger().log(Level.WARNING, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public final void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete a single gui service by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("guiServiceId", true, "class", ParameterStyle.TEMPLATE,
        "gui service identifier");
    info.getRequest().getParameters().add(param);
    param = new ParameterInfo("parentId", true, "class", ParameterStyle.TEMPLATE, "Parent object identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
