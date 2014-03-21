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

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.dataset.services.model.ServiceEnum;
import fr.cnes.sitools.dataset.services.model.ServiceModel;
import fr.cnes.sitools.plugins.guiservices.implement.model.GuiServicePluginModel;
import fr.cnes.sitools.server.Consts;

/**
 * Abstract class to manage GuiServices
 * 
 * 
 * @author m.gond
 */
public abstract class AbstractGuiServiceResource extends AbstractServiceResource {

  /**
   * Get the object from representation
   * 
   * @param representation
   *          the representation used
   * @return GuiService
   * @throws IOException
   *           if there is an error while parsing the java representation of the object
   */
  public final GuiServicePluginModel getObjectGuiServicePluginModel(Representation representation) throws IOException {
    GuiServicePluginModel projectModuleInput = null;
    if (representation.getMediaType().isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      @SuppressWarnings("unchecked")
      ObjectRepresentation<GuiServicePluginModel> obj = (ObjectRepresentation<GuiServicePluginModel>) representation;
      projectModuleInput = obj.getObject();
    }
    if (MediaType.APPLICATION_XML.isCompatible(representation.getMediaType())) {
      // Parse the XML representation to get the GuiService bean
      XstreamRepresentation<GuiServicePluginModel> repXML = new XstreamRepresentation<GuiServicePluginModel>(
          representation);
      XStream xstream = XStreamFactory.getInstance().getXStreamReader(MediaType.APPLICATION_XML);
      xstream.autodetectAnnotations(false);
      xstream.alias("guiServicePlugin", GuiServicePluginModel.class);
      repXML.setXstream(xstream);
      projectModuleInput = repXML.getObject();
    }
    else if (MediaType.APPLICATION_JSON.isCompatible(representation.getMediaType())) {
      // Parse the JSON representation to get the bean
      projectModuleInput = new JacksonRepresentation<GuiServicePluginModel>(representation, GuiServicePluginModel.class)
          .getObject();
    }
    return projectModuleInput;
  }

  /**
   * Return the url of the resource plugin application
   * 
   * @return the url of the resource plugin application
   */
  public String getGuiServicesUrl() {
    SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();
    return settings.getString(Consts.APP_DATASETS_URL) + "/" + getParentId()
        + settings.getString(Consts.APP_GUI_SERVICES_URL);
  }
  
  
  /**
   * Populate a given {@link ServiceModel} with the value from a {@link GuiServicePluginModel}
   * 
   * @param guiService
   *          the {@link GuiServicePluginModel} to get the values from
   * @param serviceModel
   *          the {@link ServiceModel} to populate
   */
  protected void populateGuiServiceModel(GuiServicePluginModel guiService, ServiceModel serviceModel) {
    serviceModel.setId(guiService.getId());
    serviceModel.setName(guiService.getName());
    serviceModel.setDescription(guiService.getDescription());
    serviceModel.setType(ServiceEnum.GUI);
    serviceModel.setIcon(guiService.getIcon());
    serviceModel.setDataSetSelection(guiService.getDataSetSelection());
  }
}
