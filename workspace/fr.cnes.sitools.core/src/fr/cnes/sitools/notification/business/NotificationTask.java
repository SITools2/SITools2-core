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

import java.util.logging.Level;

import org.restlet.Context;

import fr.cnes.sitools.notification.model.Notification;

/**
 * Notification task
 * @author AKKA
 *
 */
public class NotificationTask implements Runnable {

  /**
   * Notification associated
   */
  private Notification notification;
  
  /**
   * Manager associated
   */
  private NotificationManager engine;
  
  /**
   * Restlet context associated
   */
  private Context context;

  /**
   * Constructor
   * @param context the restlet context
   * @param engine the engine
   * @param notification the notification
   */
  public NotificationTask(Context context, NotificationManager engine, Notification notification) {
    this.notification = notification;
    this.engine = engine;
    this.context = context;
  }

  /**
   * Run the task
   */
  public final void run() {

    Context.getCurrentLogger().log(Level.FINE, "NotificationTask started.");

    engine.notifyObservers(context, notification.getObservable(), notification);
    
    engine.triggerEvent(context, notification.getObservable(), notification);
   
    Context.getCurrentLogger().log(Level.FINE, "NotificationTask finished.");

  }

}
