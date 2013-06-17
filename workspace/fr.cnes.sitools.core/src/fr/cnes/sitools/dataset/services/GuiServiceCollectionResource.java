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
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
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
public class GuiServiceCollectionResource extends AbstractGuiServiceResource {

  @Override
  public void sitoolsDescribe() {
    setName("GuiServiceCollectionResource");
    setDescription("Resource to deal with collection of GuiServices plugin");
    setNegotiated(false);
  }

  /**
   * Get all the GuiServicePluginModel for a given dataset
   * 
   * @param variant
   *          the variant needed
   * @return the representation of the list of GuiServicePluginModel in the given variant
   */
  @Get
  public Representation getGuiServices(Variant variant) {
    String url = getGuiServicesUrl();
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
    info.setDocumentation("Method to retrieve the list of GuiServices plugin for a specific parent Id");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("parentId", true, "class", ParameterStyle.TEMPLATE,
        "Parent object identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
    addStandardResourceCollectionFilterInfo(info);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.restlet.resource.ServerResource#post(org.restlet.representation.Representation,
   * org.restlet.representation.Variant)
   */
  @Post
  @Override
  public Representation post(Representation entity, Variant variant) throws ResourceException {
    try {
      GuiServicePluginModel guiServiceInput = getObjectGuiServicePluginModel(entity);

      String url = getGuiServicesUrl();
      GuiServicePluginModel guiServiceOutput = RIAPUtils.persistObject(guiServiceInput, url, getContext());
      ServiceCollectionModel services = getServiceCollectionModel();

      ServiceModel service = new ServiceModel();
      populateGuiServiceModel(guiServiceOutput, service);
      service.setLabel(guiServiceOutput.getLabel());
      service.setVisible(true);

      services.getServices().add(service);
      getStore().update(services);

      Response response = new Response(true, guiServiceOutput, GuiServicePluginModel.class, "guiServicePlugin");
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
  public final void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a new GuiService sending its representation.");
    this.addStandardPostOrPutRequestInfo(info);
    ParameterInfo param = new ParameterInfo("parentId", true, "class", ParameterStyle.TEMPLATE,
        "Parent object identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
