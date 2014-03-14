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
package fr.cnes.sitools.common.model;

import java.io.Serializable;
import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Evenements enregistrés dans le processus de gestion des commandes, des
 * inscriptions etc.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
@XStreamAlias("event")
public final class Event implements Serializable {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -482012696358966404L;

  /** Event date. */
  private Date eventDate;

  /** Event author. */
  private String author;

  /** Event description. */
  private String description;

  /** Event detail message. */
  private String message;

  /** Indicates if user notification is needed. */
  private boolean notify;

  /**
   * Default constructor.
   */
  public Event() {
    super();
  }

  /**
   * Gets the eventDate value.
   * 
   * @return the eventDate
   */
  public Date getEventDate() {
    return eventDate;
  }

  /**
   * Sets the value of eventDate.
   * 
   * @param eventDATE
   *          the eventDate to set
   */
  public void setEventDate(Date eventDATE) {
    this.eventDate = eventDATE;
  }

  /**
   * Gets the author value.
   * 
   * @return the author
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Sets the value of author.
   * 
   * @param authorName
   *          the author to set
   */
  public void setAuthor(String authorName) {
    this.author = authorName;
  }

  /**
   * Gets the description value.
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the value of description.
   * 
   * @param desc
   *          the description to set
   */
  public void setDescription(String desc) {
    this.description = desc;
  }

  /**
   * Gets the message value.
   * 
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the value of message.
   * 
   * @param mess
   *          the message to set
   */
  public void setMessage(String mess) {
    this.message = mess;
  }

  /**
   * Gets the notify value.
   * 
   * @return the notify
   */
  public boolean isNotify() {
    return notify;
  }

  /**
   * Sets the value of notify.
   * 
   * @param not
   *          the notify to set
   */
  public void setNotify(boolean not) {
    this.notify = not;
  }

}
