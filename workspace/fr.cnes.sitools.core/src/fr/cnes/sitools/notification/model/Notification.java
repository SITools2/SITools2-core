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
package fr.cnes.sitools.notification.model;

import java.io.Serializable;

/**
 * Notification class for sending information to observers from observable resource when changes.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class Notification implements Serializable {

  /** object notification key in response attributes */
  public static final String ATTRIBUTE = "sitools.notification";

  /** serialVersionUID */
  private static final long serialVersionUID = 1L;

  /** universal identifier of observable resource */
  private String observable;

  /** new status of observable resource, DELETED */
  private String status;

  /** message to observers */
  private String message;
  
  /** event for triggers */
  private String event;
  
  /** event source object */
  private Object eventSource;

  /**
   * Default constructor
   */
  public Notification() {
    super();
  }

  /**
   * Gets the observable value
   * 
   * @return the observable
   */
  public String getObservable() {
    return observable;
  }

  /**
   * Sets the value of observable
   * 
   * @param observable
   *          the observable to set
   */
  public void setObservable(String observable) {
    this.observable = observable;
  }

  /**
   * Gets the status value
   * 
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the value of status
   * 
   * @param status
   *          the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Gets the message value
   * 
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the value of message
   * 
   * @param message
   *          the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Gets the event value
   * @return the event
   */
  public String getEvent() {
    return event;
  }

  /**
   * Sets the value of event
   * @param event the event to set
   */
  public void setEvent(String event) {
    this.event = event;
  }

  /**
   * Gets the eventSource value
   * @return the eventSource
   */
  public Object getEventSource() {
    return eventSource;
  }

  /**
   * Sets the value of eventSource
   * @param eventSource the eventSource to set
   */
  public void setEventSource(Object eventSource) {
    this.eventSource = eventSource;
  }

}
