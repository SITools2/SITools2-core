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
package fr.cnes.sitools.security.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import fr.cnes.sitools.util.Property;

/**
 * Class Sitools.User from org.restlet.security.User
 * 
 * @author AKKA Technologies
 */
@XStreamAlias("user")
public final class User implements Serializable {

  /** serialVersionUID */
  @XStreamOmitField
  private static final long serialVersionUID = 6078211849526657335L;

  /** Identifier / loggin of the user */
  private String identifier;

  /** First name */
  private String firstName;

  /** Last name */
  private String lastName;

  /** e-mail */
  private String email;

  /** properties */
  private List<Property> properties = null;

  /** Password */
  @XStreamOmitField
  // TRES IMPORTANT
  private String secret;

  /**
   * Les groupes auxquels l'User appartient
   */
  // private ArrayList<Group> groups = null;

  /**
   * Copy constructor - wrapper from Restlet User
   * 
   * @param user
   *          Restlet User
   */
  public User(org.restlet.security.User user) {
    super();
    this.setIdentifier(user.getIdentifier());
    this.setFirstName(user.getFirstName());
    this.setLastName(user.getLastName());
    this.setSecret(new String(user.getSecret()));
    this.setEmail(user.getEmail());
  }

  /**
   * Default constructor
   */
  public User() {
    super();
  }

  /**
   * Constructor (full)
   * 
   * @param identifier
   *          User login
   * @param secret
   *          User password
   * @param firstName
   *          User first name
   * @param lastName
   *          User last name
   * @param email
   *          User email
   */
  public User(String identifier, String secret, String firstName, String lastName, String email) {
    this.setIdentifier(identifier);
    this.setFirstName(firstName);
    this.setLastName(lastName);
    this.setSecret(secret);
    this.setEmail(email);
  }

  // public ArrayList<Group> getGroups() {
  // return groups;
  // }

  /**
   * To convert a business User in Restlet User for security management
   * 
   * @param realm
   *          REALM - used to encode user password
   * @return org.restlet.security.User
   * @deprecated
   * @see #wrap()
   */
  public org.restlet.security.User wrap(String realm) {
    String decodeSecret = getSecret();
    return new org.restlet.security.User(identifier, decodeSecret, firstName, lastName, email);
  }

  /**
   * To convert a business User in Restlet User for security management
   * 
   * @return org.restlet.security.User
   */
  public org.restlet.security.User wrap() {
    return new org.restlet.security.User(identifier, secret, firstName, lastName, email);
  }

  /**
   * Gets the identifier value
   * 
   * @return the identifier
   */
  public String getIdentifier() {
    return identifier;
  }

  /**
   * Sets the value of identifier
   * 
   * @param identifier
   *          the identifier to set
   */
  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  /**
   * Gets the firstName value
   * 
   * @return the firstName
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Sets the value of firstName
   * 
   * @param firstName
   *          the firstName to set
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * Gets the lastName value
   * 
   * @return the lastName
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Sets the value of lastName
   * 
   * @param lastName
   *          the lastName to set
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * Gets the email value
   * 
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the value of email
   * 
   * @param email
   *          the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets the secret value
   * 
   * @return the secret
   */
  public String getSecret() {
    return secret;
  }

  /**
   * Sets the value of secret
   * 
   * @param secret
   *          the secret to set
   */
  public void setSecret(String secret) {
    this.secret = secret;
  }

  /**
   * Check l'inscription
   * 
   * TODO EVO : validator qui retourne une liste de codes d'anomalies.
   * 
   * @param user
   *          object to check
   * @return boolean
   */
  public static boolean isValid(User user) {
    return (user != null) && (user.getIdentifier() != null) && !user.getIdentifier().equals("")
        && (user.getIdentifier().length() >= 4) && (user.getSecret().length() >= 4);
  }

  /**
   * Gets the properties value
   * 
   * @return the properties
   */
  public List<Property> getProperties() {
    return properties;
  }

  /**
   * Sets the value of properties
   * 
   * @param properties
   *          the properties to set
   */
  public void setProperties(List<Property> properties) {
    if (properties != null) {
      this.properties = new ArrayList<Property>(properties);
    }
  }

}
