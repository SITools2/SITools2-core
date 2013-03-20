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
package fr.cnes.sitools.security.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import fr.cnes.sitools.role.model.Role;
import fr.cnes.sitools.util.Property;

/**
 * Class Sitools.User from org.restlet.security.User
 * 
 * @author AKKA Technologies
 */
@XStreamAlias("user")
public final class UserRole implements Serializable {

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

  /**
   * Les roles auxquels l'User appartient
   */
  private ArrayList<Role> roles = null;

  /**
   * Copy constructor - wrapper from Restlet User
   * 
   * @param user
   *          Restlet User
   */
  public UserRole(org.restlet.security.User user) {
    super();
    this.setIdentifier(user.getIdentifier());
    this.setFirstName(user.getFirstName());
    this.setLastName(user.getLastName());
    this.setEmail(user.getEmail());
  }

  /**
   * Default constructor
   */
  public UserRole() {
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
  public UserRole(String identifier, String secret, String firstName, String lastName, String email) {
    this.setIdentifier(identifier);
    this.setFirstName(firstName);
    this.setLastName(lastName);
    this.setEmail(email);
  }

  // public ArrayList<Group> getGroups() {
  // return groups;
  // }



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

  /**
   * Gets the roles value
   * @return the roles
   */
  public ArrayList<Role> getRoles() {
    return roles;
  }

  /**
   * Sets the value of roles
   * @param roles the roles to set
   */
  public void setRoles(ArrayList<Role> roles) {
    this.roles = roles;
  }

}
