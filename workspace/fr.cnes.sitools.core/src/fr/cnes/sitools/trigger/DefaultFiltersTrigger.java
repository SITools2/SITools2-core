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
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.dataset.filter.dto.FilterModelDTO;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.plugins.filters.model.FilterPluginsDescriptionDTO;
import fr.cnes.sitools.notification.NotificationApplication;
import fr.cnes.sitools.notification.TriggerResource;
import fr.cnes.sitools.notification.business.NotificationManager;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Invoked when Dictionnay event notification
 * 
 * @author d.arpin (AKKA Technologies)
 */
public class DefaultFiltersTrigger extends TriggerResource {
  /**
   * Method applied after POST request
   * 
   * @param representation
   *          the representation sent by the POST
   */
  @Post
  public void event(Representation representation) {
    Notification notification = NotificationManager.getObject(representation);
    // TODO : voir comment récupérer proprement le store des datasets ici
    NotificationApplication application = (NotificationApplication) getApplication();
    @SuppressWarnings("unchecked")
    SitoolsStore<DataSet> dsStore = (SitoolsStore<DataSet>) application.getSettings().getStores()
        .get(Consts.APP_STORE_DATASET);

    if ((notification == null) || notification.getEvent() == null) {
      getLogger().warning("Notification Event null");
      return;
    }

    if (notification.getEvent().equals("DATASET_CREATED")) {
      String datasetId = notification.getObservable();
      int loop = 0;
      // wait for instanciation of the dataset in the store
      if (dsStore.retrieve(datasetId) == null && loop < 10) {
        try {
          Thread.sleep(1000);
          loop++;
        }
        catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

      // Get all the defaults filter
      List<FilterPluginsDescriptionDTO> allFilters = RIAPUtils.getListOfObjects(
          getSitoolsSetting(Consts.APP_DATASETS_FILTERS_URL), getContext());
      for (FilterPluginsDescriptionDTO abstractFilter : allFilters) {
        if (abstractFilter.getDefaultFilter() != null && abstractFilter.getDefaultFilter()) {
          FilterModelDTO filter = new FilterModelDTO();
          filter.setClassAuthor(abstractFilter.getClassAuthor());
          filter.setClassName(abstractFilter.getClassName());
          filter.setClassVersion(abstractFilter.getClassVersion());
          filter.setCurrentClassAuthor(abstractFilter.getClassAuthor());
          filter.setCurrentClassVersion(abstractFilter.getClassVersion());
          filter.setClassOwner(abstractFilter.getClassOwner());
          filter.setDescription(abstractFilter.getDescription());
          filter.setName(abstractFilter.getName());
          createFilter(getContext(), datasetId, filter);
        }

      }
    }

  }

  /**
   * Create a filterModel attached to the dataset
   * 
   * @param datasetId
   *          dataset identifier
   * @param filter
   *          the filter model
   * @param context
   *          the context
   */
  protected void createFilter(Context context, String datasetId, FilterModelDTO filter) {

    SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
    RIAPUtils.persistObject(
        filter,
        settings.getString(Consts.APP_DATASETS_URL) + "/" + datasetId
            + settings.getString(Consts.APP_DATASETS_FILTERS_URL), context);

  }
}
