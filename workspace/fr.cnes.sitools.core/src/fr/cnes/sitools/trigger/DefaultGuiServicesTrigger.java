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
package fr.cnes.sitools.trigger;

import java.util.List;

import org.restlet.Context;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.notification.business.NotificationManager;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.plugins.guiservices.declare.model.GuiServiceModel;
import fr.cnes.sitools.plugins.guiservices.implement.model.GuiServicePluginModel;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Invoked when a dataset is created
 * 
 * @author m.arpin (AKKA Technologies)
 */
public class DefaultGuiServicesTrigger extends DefaultFiltersTrigger {
  /**
   * Method applied after POST request
   * 
   * @param representation
   *          the representation sent by the POST
   */
  @Post
  public void event(Representation representation) {
    super.event(representation);
    
    Notification notification = NotificationManager.getObject(representation);
    if ((notification == null) || notification.getEvent() == null) {
      getLogger().warning("Notification Event null");
      return;
    }

    if (notification.getEvent().equals("DATASET_CREATED")) {
      String datasetId = notification.getObservable();

      // Get all the defaults guiServices
      List<GuiServiceModel> allGuiServices = RIAPUtils.getListOfObjects(getSitoolsSetting(Consts.APP_GUI_SERVICES_URL),
          getContext());

      for (GuiServiceModel guiService : allGuiServices) {
        if (guiService.isDefaultGuiService()) {
          GuiServicePluginModel guiSericePluginModel = new GuiServicePluginModel();

          guiSericePluginModel.setName(guiService.getName());
          guiSericePluginModel.setDescription(guiService.getDescription());

          guiSericePluginModel.setAuthor(guiService.getAuthor());
          guiSericePluginModel.setVersion(guiService.getVersion());

          guiSericePluginModel.setXtype(guiService.getXtype());

          guiSericePluginModel.setPriority(guiService.getPriority());

          guiSericePluginModel.setDependencies(guiService.getDependencies());

          guiSericePluginModel.setLabel(guiService.getLabel());
          guiSericePluginModel.setIcon(guiService.getIcon());

          // TODO Le positionner quand Bastien aura fini la contextualisation
          // guiSericePluginModel.setDataSetSelection(guiService.getDataSetSelection());

          createGuiService(getContext(), datasetId, guiSericePluginModel);
        }

      }
    }

  }

  /**
   * Create a guiserviceplugin attached to the dataset
   * 
   * @param datasetId
   *          dataset identifier
   * @param guiService
   *          the guiserviceplugin model * @param context the context
   */
  protected void createGuiService(Context context, String datasetId, GuiServicePluginModel guiService) {

    SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
    RIAPUtils.persistObject(guiService,
        settings.getString(Consts.APP_DATASETS_URL) + "/" + datasetId + settings.getString(Consts.APP_SERVICES_URL)
            + "/gui", context);

  }
}
