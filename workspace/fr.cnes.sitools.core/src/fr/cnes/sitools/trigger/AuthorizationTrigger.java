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

import org.restlet.representation.Representation;
import org.restlet.resource.Post;

import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.notification.TriggerResource;
import fr.cnes.sitools.notification.business.NotificationManager;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.registry.AppRegistryApplication;

/**
 * Authorization trigger class
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class AuthorizationTrigger extends TriggerResource {

  /**
   * Method applied after POST request
   * 
   * @param representation
   *          the representation sent by the POST
   */
  @Post
  public void event(Representation representation) {
    Notification notification = NotificationManager.getObject(representation);
    String application = notification.getObservable();

    if ((notification == null) || notification.getEvent() == null) {
      getLogger().warning("Notification Event null");
      return;
    }

    // redémarrer automatiquement l'application apres mise a jour des droits
    if ((notification.getEvent().equals("AUTHORIZATION_CREATED")
        || notification.getEvent().equals("AUTHORIZATION_UPDATED") || notification.getEvent().equals(
          "AUTHORIZATION_DELETED"))
        && (application != null)) {

      AppRegistryApplication registry = ((SitoolsApplication) getApplication()).getSettings().getAppRegistry();

      if (application.equals("default")) {
        registry.reattachAllApplications();
      }
      else if (application.equals(registry.getId())) {
        registry.reattachApplication(registry, false);
      }
      else {
        SitoolsApplication app = registry.getApplication(application);
        registry.reattachApplication(app);
      }
    }
  }
}
