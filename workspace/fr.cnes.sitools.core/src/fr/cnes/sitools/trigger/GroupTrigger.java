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

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.notification.TriggerResource;
import fr.cnes.sitools.notification.business.NotificationManager;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.security.authentication.SitoolsRealm;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Invoked when GROUP event notification
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class GroupTrigger extends TriggerResource {

  /**
   * Method applied after POST request
   * 
   * @param representation
   *          the representation sent by the POST
   */
  @Post
  public void event(Representation representation) {
    SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();
    Notification notification = NotificationManager.getObject(representation);
    String groupName = notification.getObservable();
    SitoolsRealm realm = ((SitoolsApplication) getApplication()).getSettings().getAuthenticationRealm();

    if ((notification == null) || notification.getEvent() == null) {
      getLogger().warning("Notification Event null");
      return;
    }

    if (notification.getEvent().equals("GROUP_CREATED")) {
      realm.refreshUsersAndGroups();
    }

    if (notification.getEvent().equals("GROUP_UPDATED")) {
      realm.refreshUsersAndGroups();
    }

    if (notification.getEvent().equals("GROUP_DELETED")) {

      // remove group from the roles
      String url = settings.getString(Consts.APP_ROLES_URL) + "/groups/notify/" + groupName;
      RIAPUtils.handle(url, new EmptyRepresentation(), Method.PUT, MediaType.APPLICATION_JAVA_OBJECT, getContext());

      realm.removeGroup(groupName);
    }

  }

}
