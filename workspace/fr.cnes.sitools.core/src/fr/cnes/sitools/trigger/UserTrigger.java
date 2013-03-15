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

import java.util.ArrayList;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Status;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.notification.TriggerResource;
import fr.cnes.sitools.notification.business.NotificationManager;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.security.authentication.SitoolsRealm;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.userstorage.model.DiskStorage;
import fr.cnes.sitools.userstorage.model.UserStorage;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Invoked when USER event notification
 *
 * @author jp.boignard (AKKA Technologies)
 */
public class UserTrigger extends TriggerResource {

  /**
   * Method applied after POST request
   * @param representation the representation sent by the POST
   */
  @Post
  public void event(Representation representation) {
    SitoolsSettings settings = ((SitoolsApplication) getApplication()).getSettings();
    Notification notification = NotificationManager.getObject(representation);
    String userId = notification.getObservable();
    SitoolsRealm realm = settings.getAuthenticationRealm();
    
    if ((notification == null) || notification.getEvent() == null) {   
      getLogger().warning("Notification Event null");
      return;
    }
    
    // ====================================================

    if (notification.getEvent().equals("USER_CREATED")) {   
      // update realm 
      realm.refreshUsersAndGroups();
    
      // Creation de l'espace de stockage utilisateur
      UserStorage us = new UserStorage();
      us.setUserId(userId);
      DiskStorage ds = new DiskStorage();
      ds.setQuota(((SitoolsApplication) getApplication()).getSettings().getLong("Starter.userStorageSize"));
      us.setStorage(ds);
      
      ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
      objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
      
      Request reqPOST = new Request(Method.POST, RIAPUtils.getRiapBase() + settings.getString(Consts.APP_USERSTORAGE_URL) + "/users", new ObjectRepresentation<UserStorage>(
          us));
      reqPOST.getClientInfo().setAcceptedMediaTypes(objectMediaType);
      org.restlet.Response r = ((SitoolsApplication) getApplication()).getContext().getClientDispatcher().handle(reqPOST);

      if (r == null || Status.isError(r.getStatus().getCode())) {
        // echec access User application
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
      }
     
    }
    
    // ====================================================
    
    if (notification.getEvent().equals("USER_UPDATED")) {   
      realm.refreshUsersAndGroups();
    }
    
    // ====================================================

    if (notification.getEvent().equals("USER_DELETED")) { 
      
      // update realm 
      realm.removeUser(userId);
      
      // delete user storage
      Request reqDELETE = new Request(Method.DELETE, RIAPUtils.getRiapBase() + settings.getString(Consts.APP_USERSTORAGE_URL) + "/users/" + userId);
      org.restlet.Response r = ((SitoolsApplication) getApplication()).getContext().getClientDispatcher().handle(reqDELETE);

      if (r == null || Status.isError(r.getStatus().getCode())) {
        // echec access User application
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
      }
      
    }
    
    
    
  }
  
}
