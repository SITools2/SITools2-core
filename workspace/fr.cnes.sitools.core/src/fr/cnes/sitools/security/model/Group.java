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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Resource;

/**
 * Class Sitools.Group from org.restlet.security.Group class
 * 
 * @author AKKA
 * 
 */
@XStreamAlias("group")
public final class Group implements Serializable {

  /** serialVersionUID */
  @XStreamOmitField
  private static final long serialVersionUID = 6221857392548234871L;

  /**
   * Group name
   */
  private String name;

  /**
   * Group description
   */
  private String description;

  /**
   * Users belonging to the group
   */
  private ArrayList<Resource> users = null;

  /**
   * Default constructor
   */
  public Group() {
    super();
  }

  /**
   * Full constructor
   * 
   * @param name
   *          Group name
   * @param description
   *          Group description
   */
  public Group(String name, String description) {
    this.name = name;
    this.description = description;
  }

  /**
   * Copy constructor
   * 
   * @param group
   *          Restlet Group
   */
  public Group(org.restlet.security.Group group) {
    this.name = group.getName();
    this.description = group.getDescription();
  }

  /**
   * Wrap a model.Group to a Restlet Group
   * 
   * @return Restlet Group
   */
  public org.restlet.security.Group wrap() {
    return new org.restlet.security.Group(this.name, this.description);
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
  public ArrayList<Resource> getUsers() {
    return users;
  }

  /**
   * Sets the value of users
   * 
   * @param users
   *          the users to set
   */
  public void setUsers(ArrayList<Resource> users) {
    this.users = users;
  }

  /**
   * checkUser Unicity
   * 
   * @throws SitoolsException
   *           the exception returned on duplicate member
   */
  public void checkUserUnicity() throws SitoolsException {
    if (users != null) {
      for (int i = 0; i < users.size(); i++) {
        Resource user = users.get(i);
        for (int j = i + 1; j < users.size(); j++) {
          if (user.getId().equals(users.get(j).getId())) {
            throw new SitoolsException("DUPLICATE_MEMBER");
          }
        }
      }
    }
  }

}
