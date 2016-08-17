    /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.plugins.guiservices.implement;

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.plugins.guiservices.implement.model.GuiServicePluginModel;

/**
 * Resource to manage a guiservice on a specific parent id
 * 
 * 
 * @author m.gond
 */
public class GuiServicePluginResource extends AbstractGuiServicePluginResource {

  @Override
  public void sitoolsDescribe() {
    setName("GuiServicePluginCollectionResource");
    setDescription("Resource to deal with collection of GuiService plugin");
    setNegotiated(false);
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
   * Update a GuiService plugin to a dataset
   * 
   * @param representation
   *          The representation parameter
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateGuiServicePluginPlugin(Representation representation, Variant variant) {
    GuiServicePluginModel guiServicePluginOutput = null;
    try {

      GuiServicePluginModel guiServicePluginInput = null;
      if (representation != null) {

        guiServicePluginInput = getObject(representation);

        guiServicePluginOutput = getStore().update(guiServicePluginInput);

        // Register GuiServicePluginCollectionResource as observer of datasets resources
        unregisterObserver(guiServicePluginOutput);
        
        registerObserver(guiServicePluginOutput);

      }
      Response response = new Response(true, guiServicePluginOutput, GuiServicePluginModel.class, "guiServicePlugin");
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
   * Delete guiService
   * 
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Delete
  public Representation deleteGuiServicePlugin(Variant variant) {
    try {
      GuiServicePluginModel model = getStore().retrieve(getGuiServicePluginId());
      Response response = null;
      if (model == null) {
        response = new Response(false, "guiService.delete.failure");
      }
      else {
        // Business service
        getStore().delete(getGuiServicePluginId());

        // Register ConverterChainedModel as observer of datasets resources
        unregisterObserver(model);

        // Response
        response = new Response(true, "guiService.delete.success");
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
