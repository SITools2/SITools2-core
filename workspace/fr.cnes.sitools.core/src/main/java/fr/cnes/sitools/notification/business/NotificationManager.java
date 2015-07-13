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
package fr.cnes.sitools.notification.business;

import java.io.IOException;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.notification.model.Notification;
import fr.cnes.sitools.notification.model.RestletObservable;
import fr.cnes.sitools.notification.model.RestletObserver;
import fr.cnes.sitools.notification.store.NotificationStore;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Release 1 : manage notification on DELETE only
 * 
 * Release 2 : manage POST / PUT
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class NotificationManager {

  /** Store of observable / observers relations */
  private NotificationStore store = null;

  /**
   * Notification Manager
   * 
   * @param store
   *          NotificationStore
   */
  public NotificationManager(NotificationStore store) {
    super();
    this.store = store;
  }

  /**
   * Gets the store value
   * 
   * @return the store
   */
  public final NotificationStore getStore() {
    return store;
  }

  /**
   * Sets the value of store
   * 
   * @param store
   *          the store to set
   */
  public final void setStore(NotificationStore store) {
    this.store = store;
  }

  /**
   * Representation of a notification
   * 
   * @param notification
   *          Notification object
   * @param media
   *          Restlet MediaType
   * @param context
   *          a Restlet {@link Context}
   * @return Representation
   */
  public static Representation getRepresentation(Notification notification, MediaType media, Context context) {

    if ((media == null) || media.isCompatible(MediaType.APPLICATION_JAVA_OBJECT)) {
      return new ObjectRepresentation<Notification>(notification);
    }

    // Media XML || JSON
    XStream xstream = XStreamFactory.getInstance().getXStream(media, context);
    xstream.alias("notification", Notification.class);

    XstreamRepresentation<Notification> rep = new XstreamRepresentation<Notification>(media, notification);
    rep.setXstream(xstream);
    return rep;
  }

  /**
   * Used by NotificationResource
   * 
   * @param context
   *          Restlet Context
   * @param observableUUID
   *          Observable unique identifier
   * @param notification
   *          Notification object to send to observers
   */
  public final void notifyObservers(Context context, String observableUUID, Notification notification) {
    RestletObservable obs = store.getObservable(observableUUID);

    // add new observable
    if (obs == null) {
      obs = new RestletObservable();
      obs.setUri(observableUUID);
      store.addObservable(observableUUID, obs);
    }
    else {
      // notify observers
      obs.notifyObservers(context, notification);
    }
    // delete observable if status DELETED
    if ((notification != null) && "DELETED".equals(notification.getStatus())) {
      store.removeObservable(observableUUID);
    }
  }

  /**
   * Add an observer
   * 
   * @param observableUUID
   *          UUID of the observable
   * @param observer
   *          the restlet observer
   */
  public final void addObserver(String observableUUID, RestletObserver observer) {
    RestletObservable obs = store.getObservable(observableUUID);
    if (obs != null) {
      obs.addObserver(observer);
    }
  }

  /**
   * Remove an observer
   * 
   * @param observableUUID
   *          UUID of the observable
   * @param observerUUID
   *          UUID of the observer
   */
  public final void removeObserver(String observableUUID, String observerUUID) {
    RestletObservable obs = store.getObservable(observableUUID);
    if (obs != null) {
      obs.removeObserver(observerUUID);
    }

  }

  /**
   * Calls trigger resource registered for this event
   * 
   * @param context
   *          RESTlet context
   * @param observable
   *          the observable to trigger
   * @param notification
   *          notification sent
   */
  public final void triggerEvent(Context context, String observable, Notification notification) {
    SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);

    if (notification.getEvent() != null) {
      Representation notificationRepresentation = new ObjectRepresentation<Notification>(notification);
      Request reqPOST = new Request(Method.POST, RIAPUtils.getRiapBase()
          + settings.getString(Consts.APP_NOTIFICATIONS_URL) + "/triggers/" + notification.getEvent(),
          notificationRepresentation);
      Response response = null;

      try {
        response = context.getClientDispatcher().handle(reqPOST);
        if (response.getEntity() != null) {
          response.getEntity().exhaust();
        }

      }
      catch (IOException e) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, null, e);
      }
    }
  }

  /**
   * Gets Notification object
   * 
   * @param representation
   *          Notification representation
   * @return Notification object
   */
  public static Notification getObject(Representation representation) {
    try {
      ObjectRepresentation<Notification> or;
      try {
        or = new ObjectRepresentation<Notification>(representation);
        return or.getObject();
      }
      catch (IllegalArgumentException e) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, null, e);
      }
      catch (ClassNotFoundException e) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, null, e);
      }

    }
    catch (IOException e) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, null, e);
    }
  }

}
