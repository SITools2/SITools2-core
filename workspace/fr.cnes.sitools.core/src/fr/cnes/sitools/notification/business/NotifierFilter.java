    /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Filter;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.notification.model.Notification;

/**
 * The aim is to make the notification as transparent as possible from
 * an observable object point of view. 
 * 
 * 1. For an application that shows observable we attach a NotifyFilter that watches
 * after each request handle if an observable attribute is present in the answer, and if yes
 * get this notification object.  
 * 
 * 2. For a server resource object, we add in the response attributes a boolean Observable set to true
 * and a notification object indicating the observable UUID, a status, and a message for observers.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class NotifierFilter extends Filter {

  /**
   * Engine of notification for filter
   */
  private NotificationManager engine = null;

  /**
   * Default constructor
   * 
   * @param context
   *          restlet context
   */
  public NotifierFilter(Context context) {
    super(context);
    this.engine = ((SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS)).getNotificationManager();
  }

  /**
   * Constructor
   * 
   * @param context
   *          Restlet context
   * @param engine
   *          NotificationManager to notifyObservers
   */
  public NotifierFilter(Context context, NotificationManager engine) {
    super(context);
    this.engine = engine;
  }

  @Override
  public final void afterHandle(Request request, Response response) {
    Notification notification = (Notification) response.getAttributes().get(Notification.ATTRIBUTE);
    if (null == notification) {
      return;
    }

    if (null == engine) {
      getLogger().warning("Can not notify observers because NotificationManager instance is null.");
      return;
    }

    try {
      NotificationTask task = new NotificationTask(getContext(), engine, notification);
      getApplication().getTaskService().execute(task);
    }
    catch (Exception e) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, null, e);
    }

  }


}
