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
package fr.cnes.sitools.security;

import java.util.ArrayList;
import java.util.List;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.security.model.Group;
import fr.cnes.sitools.security.model.User;

/**
 * Users and Groups XML storage
 * 
 * @author AKKA Technologies
 */
public final class UsersAndGroupsStoreXML implements UsersAndGroupsStore {

  @Override
  public String getName() {
    return null;
  }

  @Override
  public List<User> getUsers() throws SitoolsException {
    return null;
  }

  @Override
  public List<User> getUsers(ResourceCollectionFilter filter) throws SitoolsException {
    return null;
  }

  @Override
  public List<User> getUsersByGroup(String name) throws SitoolsException {
    return null;
  }

  @Override
  public List<User> getUsersByGroup(String name, ResourceCollectionFilter filter) throws SitoolsException {
    return null;
  }

  @Override
  public List<User> getUsers(int start, int limit, String query) throws SitoolsException {
    return null;
  }

  @Override
  public User getUserById(String identifier) throws SitoolsException {
    return null;
  }

  @Override
  public User createUser(User bean) throws SitoolsException {
    return null;
  }

  @Override
  public User updateUser(User bean) throws SitoolsException {
    return null;
  }

  @Override
  public boolean deleteUser(String identifier) throws SitoolsException {
    return false;
  }

  @Override
  public List<Group> getGroups() throws SitoolsException {
    return null;
  }

  @Override
  public ArrayList<Group> getGroups(ResourceCollectionFilter filter) throws SitoolsException {
    return null;
  }

  @Override
  public List<Group> getGroupsByUser(String identifier) throws SitoolsException {
    return null;
  }

  @Override
  public List<Group> getGroupsByUser(String identifier, ResourceCollectionFilter filter) throws SitoolsException {
    return null;
  }

  @Override
  public List<Group> getGroups(int start, int limit, String query) throws SitoolsException {
    return null;
  }

  @Override
  public Group getGroupById(String name) throws SitoolsException {
    return null;
  }

  @Override
  public Group createGroup(Group bean) throws SitoolsException {
    return null;
  }

  @Override
  public Group updateGroup(Group bean) throws SitoolsException {
    return null;
  }

  @Override
  public boolean deleteGroup(String name) throws SitoolsException {
    return false;
  }

  @Override
  public Group updateGroupUsers(Group bean) throws SitoolsException {
    return null;
  }

  @Override
  public boolean isUserModifiable() {
    return true;
  }

  @Override
  public boolean isGroupModifiable() {
    return true;
  }

}
