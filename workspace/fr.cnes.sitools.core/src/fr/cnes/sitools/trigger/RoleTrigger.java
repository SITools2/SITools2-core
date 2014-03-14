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
import fr.cnes.sitools.role.model.Role;
import fr.cnes.sitools.security.authentication.SitoolsRealm;

/**
 * Role trigger class
 * 
 *
 * @author AKKA
 */
public class RoleTrigger extends TriggerResource {
  
  /**
   * Method applied after POST request
   * @param representation the representation sent by the POST
   */
  @Post
  public void event(Representation representation) {
    Notification notification = NotificationManager.getObject(representation);
    Role roleOutput = (Role) notification.getEventSource();
    if (roleOutput == null) {
      getLogger().warning("Notification.eventSource is null. A Role instance is expected.");
      return;
    }
    
    if ((notification == null) || notification.getEvent() == null) {   
      getLogger().warning("Notification Event null");
      return;
    }
    
    SitoolsRealm realm = ((SitoolsApplication) getApplication()).getSettings().getAuthenticationRealm();
    
    if (notification.getEvent().equals("ROLE_DELETED")) {
      realm.removeRole(roleOutput.getName());
    }

    if (notification.getEvent().equals("ROLE_USERS_UPDATED")) {
      realm.refreshRoleMappings(roleOutput);
    }
    
    if (notification.getEvent().equals("ROLE_GROUPS_UPDATED")) {
      realm.refreshRoleMappings(roleOutput);
    }
  }
}

