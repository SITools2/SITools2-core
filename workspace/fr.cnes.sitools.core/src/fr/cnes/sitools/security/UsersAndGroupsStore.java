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
package fr.cnes.sitools.security;

import java.util.List;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.security.model.Group;
import fr.cnes.sitools.security.model.User;

/**
 * Interface for user and groups management. Many implementation can be done : JDBC DB, XML, LDAP
 * 
 * @author AKKA Technologies
 */
public interface UsersAndGroupsStore {

  /**
   * Store name
   * 
   * @return Store name
   */
  String getName();

  /**
   * Gets complete list of users
   * 
   * @return ArrayList<User>
   * @throws SitoolsException
   *           if problem
   */
  List<User> getUsers() throws SitoolsException;

  /**
   * Gets filtered list of users
   * 
   * @param filter
   *          criteria (pagination, ...)
   * @return ArrayList<User>
   * @throws SitoolsException
   *           if problem
   */
  List<User> getUsers(ResourceCollectionFilter filter) throws SitoolsException;

  /**
   * Gets user for a group
   * 
   * @param name
   *          Group name
   * @return ArrayList<User>
   * @throws SitoolsException
   *           if problem
   */
  List<User> getUsersByGroup(String name) throws SitoolsException;

  /**
   * Gets users of a group according to the filter
   * 
   * @param name
   *          Group name
   * @param filter
   *          Generic filter (pagination, query)
   * @return ArrayList<User>
   * @throws SitoolsException
   *           if problem
   */
  List<User> getUsersByGroup(String name, ResourceCollectionFilter filter) throws SitoolsException;

  /**
   * Gets users according to the pagination and query
   * 
   * @param start
   *          pagination start index
   * @param limit
   *          pagination number of items
   * @param query
   *          filtering user
   * @return ArrayList<User>
   * @throws SitoolsException
   *           if problem
   */
  List<User> getUsers(int start, int limit, String query) throws SitoolsException;

  /**
   * Gets one user by identifier
   * 
   * @param identifier
   *          User identifier
   * @return User
   * @throws SitoolsException
   *           if problem
   */
  User getUserById(String identifier) throws SitoolsException;

  /**
   * Method for creating users
   * 
   * @param bean
   *          User
   * @return User
   * @throws SitoolsException
   *           if problem
   */
  User createUser(User bean) throws SitoolsException;

  /**
   * Method for updating user
   * 
   * @param bean
   *          User
   * @return User
   * @throws SitoolsException
   *           if problem
   */
  User updateUser(User bean) throws SitoolsException;

  /**
   * Method for deleting a user
   * 
   * @param identifier
   *          user identifier
   * @return boolean true if deleted
   * @throws SitoolsException
   *           if problem
   */
  boolean deleteUser(String identifier) throws SitoolsException;

  /**
   * Gets all groups
   * 
   * @return ArrayList<Group>
   * @throws SitoolsException
   *           if problem
   */
  List<Group> getGroups() throws SitoolsException;

  /**
   * Gets groups according to the generic filter
   * 
   * @param filter
   *          Generic filter (pagination, query)
   * @return ArrayList<Group>
   * @throws SitoolsException
   *           if problem
   */
  List<Group> getGroups(ResourceCollectionFilter filter) throws SitoolsException;

  /**
   * getGroupsByUser Gets groups which contain specified user
   * 
   * @param identifier
   *          User identifier
   * @return ArrayList<Group>
   * @throws SitoolsException
   *           if problem
   */
  List<Group> getGroupsByUser(String identifier) throws SitoolsException;

  /**
   * getGroupsByUser Gets groups which contains a user according to the specified filter
   * 
   * @param identifier
   *          User identifier
   * @param filter
   *          Generic filter (pagination, query)
   * @return ArrayList<Group>
   * @throws SitoolsException
   *           if problem
   */
  List<Group> getGroupsByUser(String identifier, ResourceCollectionFilter filter) throws SitoolsException;

  /**
   * getGroups
   * 
   * @param start
   *          index of first item
   * @param limit
   *          number max of items
   * @param query
   *          LIKE String refer to Group name
   * @return ArrayList<Group>
   * @throws SitoolsException
   *           if problem
   */
  List<Group> getGroups(int start, int limit, String query) throws SitoolsException;

  /**
   * getGroupById : Gets group by its id (name)
   * 
   * @param name
   *          Group name
   * @return Group
   * @throws SitoolsException
   *           if problem
   */
  Group getGroupById(String name) throws SitoolsException;

  /**
   * Creates a new Group
   * 
   * @param bean
   *          input
   * @return Group
   * @throws SitoolsException
   *           if problem
   */
  Group createGroup(Group bean) throws SitoolsException;

  /**
   * Updates a Group
   * 
   * @param bean
   *          Group
   * @return updated Group
   * @throws SitoolsException
   *           if problem
   */
  Group updateGroup(Group bean) throws SitoolsException;

  /**
   * Deletes a group
   * 
   * @param name
   *          Groups name
   * @return true if deleted
   * @throws SitoolsException
   *           if problem
   */
  boolean deleteGroup(String name) throws SitoolsException;

  /**
   * Update users associated with a group
   * 
   * @param bean
   *          Group
   * @return updated Group
   * @throws SitoolsException
   *           if problem
   */
  Group updateGroupUsers(Group bean) throws SitoolsException;

  /**
   * GESTION DES AUTORISATIONS SUR LE STORE Users & Groups
   */

  /**
   * Can modify Users (C U D)
   * 
   * @return boolean
   */
  boolean isUserModifiable();

  /**
   * Can modify Groups (C U D)
   * 
   * @return boolean
   */
  boolean isGroupModifiable();

}
