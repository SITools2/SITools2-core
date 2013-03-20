/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.guiservice;

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
import fr.cnes.sitools.guiservice.model.GuiServiceModel;

/**
 * Class Resource for managing single GuiService (GET UPDATE DELETE)
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class GuiServiceResource extends AbstractGuiServiceResource {

  @Override
  public void sitoolsDescribe() {
    setName("GuiServiceResource");
    setDescription("Resource for managing an identified gui services");
    setNegotiated(false);
  }

  @Override
  public final void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve a single GuiService by ID");
    this.addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("guiServiceId", true, "class", ParameterStyle.TEMPLATE, "Module identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardObjectResponseInfo(info);
    addStandardResourceCollectionFilterInfo(info);
  }

  /**
   * Update / Validate existing guiService
   * 
   * @param representation
   *          GuiService representation
   * @param variant
   *          client preferred media type
   * @return Representation
   */
  @Put
  public Representation updateGuiService(Representation representation, Variant variant) {
    GuiServiceModel guiServiceOutput = null;
    try {

      GuiServiceModel guiServiceInput = null;
      if (representation != null) {
        // Parse object representation
        guiServiceInput = getObject(representation, variant);

        // Business service
        guiServiceOutput = getStore().update(guiServiceInput);
      }

      Response response = new Response(true, guiServiceOutput, GuiServiceModel.class, "guiService");
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
    ParameterInfo param = new ParameterInfo("guiServiceId", true, "class", ParameterStyle.TEMPLATE, "gui service identifier");
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
  public Representation deleteGuiService(Variant variant) {
    try {
      GuiServiceModel model = getStore().retrieve(getGuiServiceId());
      Response response = null;
      if (model == null) {
        response = new Response(false, "guiService.delete.failure");
      }
      else {
        // Business service
        getStore().delete(getGuiServiceId());

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
    ParameterInfo param = new ParameterInfo("guiServiceId", true, "class", ParameterStyle.TEMPLATE, "gui service identifier");
    info.getRequest().getParameters().add(param);
    this.addStandardSimpleResponseInfo(info);
    this.addStandardInternalServerErrorInfo(info);
  }

}
