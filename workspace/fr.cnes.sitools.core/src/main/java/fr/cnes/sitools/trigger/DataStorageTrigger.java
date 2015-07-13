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
package fr.cnes.sitools.trigger;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;
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
public class DataStorageTrigger extends TriggerResource {

  /**
   * Method applied after POST request
   * 
   * @param representation
   *          the representation sent by the POST
   */
  @Post
  public void event(Representation representation) {
    Notification notification = NotificationManager.getObject(representation);
    if ((notification == null) || notification.getEvent() == null) {
      getLogger().warning("Notification Event null");
      return;
    }

    if (notification.getEvent().equals("STORAGE_DELETED")) {
      String storageId = notification.getObservable();
      deleteAuthorization(getContext(), storageId);
      deleteCustomFilter(getContext(), storageId);
    }

  }

  /**
   * delete the authorizations attached to a datastorage
   * 
   * @param context
   *          context
   * @param storageId
   *          the storage id
   */
  protected void deleteAuthorization(Context context, String storageId) {
    org.restlet.Response r = null;
    try {
      SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
      
      // delete user storage
      Request reqDELETE = new Request(Method.DELETE, RIAPUtils.getRiapBase() + settings.getString(Consts.APP_AUTHORIZATIONS_URL) + "/" + storageId);
      org.restlet.Response r1 = ((SitoolsApplication) getApplication()).getContext().getClientDispatcher().handle(reqDELETE);

      if (r1 == null || Status.isError(r1.getStatus().getCode())) {
        // echec access User application
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
      }
    }
    finally {
      RIAPUtils.exhaust(r);
    }

  }
  
  
  /**
   * delete the custom filter attached to a datastorage
   * 
   * @param context
   *          context
   * @param storageId
   *          the storage id
   */
  protected void deleteCustomFilter(Context context, String storageId) {
    org.restlet.Response r = null;
    try {
      SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
      
      // delete user storage
      Request reqDELETE = new Request(Method.DELETE, RIAPUtils.getRiapBase() + settings.getString(Consts.APP_PLUGINS_FILTERS_INSTANCES_URL) + "/" + storageId);
      org.restlet.Response r1 = ((SitoolsApplication) getApplication()).getContext().getClientDispatcher().handle(reqDELETE);

      if (r1 == null || Status.isError(r1.getStatus().getCode())) {
        // echec access User application
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
      }
    }
    finally {
      RIAPUtils.exhaust(r);
    }

  }

}
