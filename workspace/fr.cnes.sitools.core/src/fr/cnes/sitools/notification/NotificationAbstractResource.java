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
package fr.cnes.sitools.notification;

import org.restlet.data.MediaType;
import org.restlet.representation.Variant;

import fr.cnes.sitools.common.SitoolsResource;

/**
 * Base class for resources
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public abstract class NotificationAbstractResource extends SitoolsResource {

  /** Application */
  private volatile NotificationApplication application = null;
  
  /** observable id in the request */
  private volatile String observableUUID = null;

  /** observer id in the request */
  private volatile String observerUUID = null;

  @Override
  protected void doInit() {
    super.doInit();

    // Declares the variants supported
    getVariants().add(new Variant(MediaType.APPLICATION_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    getVariants().add(new Variant(MediaType.APPLICATION_JAVA_OBJECT));

    application = (NotificationApplication) getApplication();

    observableUUID = (String) this.getRequest().getAttributes().get("observableUUID");
    observerUUID = (String) this.getRequest().getAttributes().get("observerUUID");
  }
  
  
  /**
   * Get the notification application
   * @return the notification application
   */
  public final NotificationApplication getNotificationApplication() {
    return this.application;
  }
  
  /**
   * Get the observable identifier
   * @return UUID of the observable
   */
  public final String getObservableUUID() {
    return this.observableUUID;
  }
  
  /**
   * Get the observable identifier
   * @return UUID of the observable
   */
  public final String getObserverUUID() {
    return this.observerUUID;
  }
  
  
}
