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
package fr.cnes.sitools.inscription.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import fr.cnes.sitools.common.model.Event;
import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.util.Property;

/**
 * Class for user registration
 * 
 * cpassword is not mapped / not persistent. It is sent by the client.
 * 
 * TODO à voir : faut-il ignorer toutes les properties inconnues pour eviter les exceptions de mapping json > java...
 * 
 * @author jp.boignard (AKKA Technologies)
 */
@XStreamAlias("inscription")
@JsonIgnoreProperties("cpassword")
public class Inscription implements IResource {
  
  /** minimal length for identifier and password */
  private static final int MINIMAL_STRING_LENGTH = 4; 

  /** internal identifier */
  private String id;

  /** surname */
  private String firstName;

  /** name */
  private String lastName;

  /** business identifier / user login */
  private String identifier;

  /** password */
  private String password;

  /** user registration comment */
  private String comment;

  /** email */
  private String email;
  
  
  /** Hidden field for IResource */
  @XStreamOmitField
  @JsonIgnore
  private String name;
  
  /** Hidden field for IResource */
  @XStreamOmitField
  @JsonIgnore
  private String description;
  
  /**
   * Exemple : Organisme (CDC)
   */

  private List<Property> properties = null;

  /**
   * TODO EVO - Tracer les actions du processus d'inscription.
   */
  private List<Event> events = null;

  /**
   * Inscription status
   */
  private String status;

  /**
   * Default constructor
   */
  public Inscription() {
    super();
  }

  /**
   * Gets the identifier value
   * 
   * @return the identifier
   */
  public final String getIdentifier() {
    return identifier;
  }

  /**
   * Sets the value of identifier
   * 
   * @param identifier
   *          the identifier to set
   */
  public final void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

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
   * Gets the firstName value
   * 
   * @return the firstName
   */
  public final String getFirstName() {
    return firstName;
  }

  /**
   * Sets the value of firstName
   * 
   * @param firstName
   *          the firstName to set
   */
  public final void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Gets the lastName value
   * 
   * @return the lastName
   */
  public final String getLastName() {
    return lastName;
  }

  /**
   * Sets the value of lastName
   * 
   * @param lastName
   *          the lastName to set
   */
  public final void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * Gets the password value
   * 
   * @return the password
   */
  public final String getPassword() {
    return password;
  }

  /**
   * Sets the value of password
   * 
   * @param password
   *          the password to set
   */
  public final void setPassword(String password) {
    this.password = password;
  }

  /**
   * Gets the comment value
   * 
   * @return the comment
   */
  public final String getComment() {
    return comment;
  }

  /**
   * Sets the value of comment
   * 
   * @param comment
   *          the comment to set
   */
  public final void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * Gets the email value
   * 
   * @return the email
   */
  public final String getEmail() {
    return email;
  }

  /**
   * Sets the value of email
   * 
   * @param email
   *          the email to set
   */
  public final void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets the properties value
   * 
   * @return the properties
   */
  public final List<Property> getProperties() {
    return properties;
  }

  /**
   * Sets the value of properties
   * 
   * @param properties
   *          the properties to set
   */
  public final void setProperties(List<Property> properties) {
    this.properties = properties;
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

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Inscription other = (Inscription) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    }
    else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

  /**
   * wrapper to Sitools.model.User
   * 
   * @return User
   */
  public User wrapToUser() {
    User result = new User();
    result.setIdentifier(this.identifier);
    result.setEmail(this.getEmail());
    result.setFirstName(this.getFirstName());
    result.setLastName(this.getLastName());
    result.setSecret(this.getPassword());
    result.setProperties(this.getProperties());
    return result;
  }

  /**
   * Check l'inscription
   * 
   * TODO EVO : validator qui retourne une liste de codes d'anomalies.
   * 
   * @param inscription
   *          object to check
   * @return boolean
   */
  public static final boolean isValid(Inscription inscription) {
    return (inscription != null) 
      && (inscription.getIdentifier() != null) 
      && (inscription.getIdentifier().length() >= MINIMAL_STRING_LENGTH) 
      && (inscription.getPassword().length() >= MINIMAL_STRING_LENGTH);
  }

  @Override
  public final String getName() {
    return this.name;
  }

  @Override
  public final String getDescription() {
    return this.description;
  }

}
