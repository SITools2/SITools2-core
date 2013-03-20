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
package fr.cnes.sitools.role.model;

import java.io.Serializable;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import fr.cnes.sitools.common.model.IResource;
import fr.cnes.sitools.common.model.Resource;


/**
 * Role class
 * @author jp.boignard (AKKA Technologies)
 */
@XStreamAlias("role")
public final class Role implements IResource, Serializable {

  /** serialVersionUID */
  @XStreamOmitField
  private static final long serialVersionUID = 679680084708270224L;

  /**
   * Role identifier
   */
  private String id;

  /**
   * Role name
   */
  private String name;

  /**
   * Role description
   */
  private String description;

  /**
   * Users and groups are resources exposed by another application.
   * Multiple users and groups can be attached to a role. 
   * A role lists users and groups as resources.
   * 
   */
  @XStreamAlias("users")
  private List<Resource> users = null;

  /**
   * List of groups as resources
   */
  @XStreamAlias("groups")
  private List<Resource> groups = null;

  /**
   * Gets the id value
   * 
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of id
   * 
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the name value
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of name
   * 
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the description value
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the value of description
   * 
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the users value
   * 
   * @return the users
   */
  public List<Resource> getUsers() {
    return users;
  }

  /**
   * Sets the value of users
   * 
   * @param users
   *          the users to set
   */
  public void setUsers(List<Resource> users) {
    this.users = users;
  }

  /**
   * Gets the groups value
   * 
   * @return the groups
   */
  public List<Resource> getGroups() {
    return groups;
  }

  /**
   * Sets the value of groups
   * 
   * @param groups
   *          the groups to set
   */
  public void setGroups(List<Resource> groups) {
    this.groups = groups;
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
    Role other = (Role) obj;
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
   * SITools to RESTlet role converter
   * 
   * @return org.restlet.security.Role
   */
  public org.restlet.security.Role wrap() {
    org.restlet.security.Role role = new org.restlet.security.Role();
    role.setName(this.getName());
    role.setDescription(this.getDescription());
    // childroles ...
    return role;
  }

}
