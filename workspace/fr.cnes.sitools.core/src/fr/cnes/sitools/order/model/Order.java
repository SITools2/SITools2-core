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
package fr.cnes.sitools.order.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.Event;
import fr.cnes.sitools.common.model.IResource;

/**
 * POJO for Order
 * 
 * @author jp.boignard (AKKA Technologies)
 */
@XStreamAlias("order")
public class Order implements IResource, Serializable {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -8244779684875693528L;

  /** order identifier sets by the server */
  private String id;

  /** User identifier */
  private String userId;

  /** Description */
  private String description;

  /** Resource collection - can be url collection or id collection */
  private List<String> resourceCollection = null;
  /**
   * Resource collection for admin only - can be url collection or id collection
   */
  private List<String> adminResourceCollection = null;

  /** Url (pointing to the user storage) describing the content of the order. */
  private String resourceDescriptor = null;

  /**
   * en cours de validation | en cours de traitement | envoyée à l'utilisateur |
   * validée par l'utilisateur
   */
  /** Status of the order sets by the server */
  private String status;

  /** Date order sets by the server */
  private Date dateOrder = null;

  /** Evenements du processus de gestion des commandes */
  private List<Event> events = null;

  /**
   * TODO Rajouter Project id, DataSet id, name ?
   * 
   */

  /**
   * Gets the id value
   * 
   * @return the id
   */
  public final String getId() {
    return id;
  }

  /**
   * Sets the value of id
   * 
   * @param id
   *          the id to set
   */
  public final void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the userId value
   * 
   * @return the userId
   */
  public final String getUserId() {
    return userId;
  }

  /**
   * Get the name of the user sending the order
   * 
   * @return the User ID
   */
  public final String getName() {
    return userId;
  }

  /**
   * Sets the value of userId
   * 
   * @param userId
   *          the userId to set
   */
  public final void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * Gets the description value
   * 
   * @return the description
   */
  public final String getDescription() {
    return description;
  }

  /**
   * Sets the value of description
   * 
   * @param description
   *          the description to set
   */
  public final void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the resourceCollection value
   * 
   * @return the resourceCollection
   */
  public final List<String> getResourceCollection() {
    return resourceCollection;
  }

  /**
   * Sets the value of resourceCollection
   * 
   * @param resourceCollection
   *          the resourceCollection to set
   */
  public final void setResourceCollection(List<String> resourceCollection) {
    this.resourceCollection = resourceCollection;
  }

  /**
   * Gets the resourceDescriptor value
   * 
   * @return the resourceDescriptor
   */
  public final String getResourceDescriptor() {
    return resourceDescriptor;
  }

  /**
   * Sets the value of resourceDescriptor
   * 
   * @param resourceDescriptor
   *          the resourceDescriptor to set
   */
  public final void setResourceDescriptor(String resourceDescriptor) {
    this.resourceDescriptor = resourceDescriptor;
  }

  /**
   * Gets the status value
   * 
   * @return the status
   */
  public final String getStatus() {
    return status;
  }

  /**
   * Sets the value of status
   * 
   * @param status
   *          the status to set
   */
  public final void setStatus(String status) {
    this.status = status;
  }

  /**
   * Gets the dateOrder value
   * 
   * @return the dateOrder
   */
  public final Date getDateOrder() {
    return dateOrder;
  }

  /**
   * Sets the value of dateOrder
   * 
   * @param date
   *          the dateOrder to set
   */
  public final void setDateOrder(Date date) {
    this.dateOrder = date;
  }

  /**
   * Gets the events value
   * 
   * @return the events
   */
  public final List<Event> getEvents() {
    return events;
  }

  /**
   * Sets the value of events
   * 
   * @param events
   *          the events to set
   */
  public final void setEvents(List<Event> events) {
    this.events = events;
  }

  /**
   * Sets the value of adminResourceCollection
   * @param adminResourceCollection the adminResourceCollection to set
   */
  public final void setAdminResourceCollection(List<String> adminResourceCollection) {
    this.adminResourceCollection = adminResourceCollection;
  }

  /**
   * Gets the adminResourceCollection value
   * @return the adminResourceCollection
   */
  public final List<String> getAdminResourceCollection() {
    return adminResourceCollection;
  }

}
