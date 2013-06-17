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
package fr.cnes.sitools;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.security.model.Group;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.server.Consts;

/**
 * Test CRUD Users & Groups with JDBC DataSource persistence
 * 
 * @since UserStory : ADM Security - Sprint : 4
 * @author jp.boignard (AKKA Technologies)
 */
public abstract class AbstractUsersAndGroupsAdministrationTestCase extends AbstractSitoolsServerTestCase {

  /** Test title */
  protected static String title = "Users And Groups Administration API";

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_SECURITY_URL);
  }

  /**
   * Test Get Users.
   */
  @Test
  public void testCRUDUsers() {
    // docAPI.setActive(false);
    User item = createObjectUser();
    createUser(item);
    retrieveUser(item);
    updateUser(item);
    deleteUser(item);

    createWadl(getBaseUrl(), "users");
  }

  /**
   * Create Group
   * 
   * @return User user object
   */
  public User createObjectUser() {
    User myUser = new User("test-identifier", "mOtDePaSsE", "Prénom", "Nom", "prenom.nom@societe.fr");
    return myUser;
  }

  /**
   * Create User
   * 
   * @param item
   *          User
   */
  public void createUser(User item) {

    // TODO call POST item
    User resultUser = item;

    assertEquals(item.getIdentifier(), resultUser.getIdentifier());
    assertEquals(item.getFirstName(), resultUser.getFirstName());
    assertEquals(item.getLastName(), resultUser.getLastName());
    assertEquals(item.getEmail(), resultUser.getEmail());
    /** assertEquals(item.getSecret(), resultUser.getSecret()); */
  }

  /**
   * Retrieve User
   * 
   * @param item
   *          User
   */
  public void retrieveUser(User item) {
    // TODO call GET item
  }

  /**
   * Update User
   * 
   * @param item
   *          User
   */
  public void updateUser(User item) {

    // TODO call PUT item
    User resultUser = item;

    assertEquals(item.getIdentifier(), resultUser.getIdentifier());
    assertEquals(item.getFirstName(), resultUser.getFirstName());
    assertEquals(item.getLastName(), resultUser.getLastName());
    assertEquals(item.getEmail(), resultUser.getEmail());
    /** return secret = MD5 */
    /** assertEquals(item.getSecret(), resultUser.getSecret()); */
  }

  /**
   * Delete User
   * 
   * @param item
   *          User to delete
   */
  public void deleteUser(User item) {
    // TODO call DELETE item
  }

  /**
   * Test case for Groups Creation, Retrieve, Update, Delete
   */
  @Test
  public void testCRUDGroups() {
    // docAPI.setActive(false);
    Group item = createObjectGroup();
    createGroup(item);
    retrieveGroup(item);
    updateGroup(item);
    deleteGroup(item);

    createWadl(getBaseUrl(), "groups");
  }

  /**
   * Create Group
   * 
   * @return Group object
   */
  public Group createObjectGroup() {
    Group myGroup = new Group("test-groupName", "test-groupDescription");
    return myGroup;
  }

  /**
   * Create Group
   * 
   * @param item
   *          Group
   */
  public void createGroup(Group item) {
    // TODO call POST item
  }

  /**
   * Retrieve Group
   * 
   * @param item
   *          Group
   */
  public void retrieveGroup(Group item) {
    // TODO call GET item
  }

  /**
   * Update Group
   * 
   * @param item
   *          Group
   */
  public void updateGroup(Group item) {
    // TODO call PUT item
  }

  /**
   * Delete Group
   * 
   * @param item
   *          Group to be deleted
   */
  public void deleteGroup(Group item) {
    // TODO call DELETE item
  }

  /**
   * Test Get Users by group.
   */
  public void testGetUsersByGroup() {
    // String groupName = "administrateurs";
    // try {
    // ArrayList<User> users = store.getUsersByGroup(groupName);
    // if ((users == null) || (users.size() == 0)) {
    // fail("Should return none empty list of users");
    // }
    // }
    // catch (SitoolsException e) {
    // fail("Exception - Should return users for group " + groupName);
    // }
  }

  /**
   * Test Get Groups by user.
   */
  @Test
  public void testGetGroupsByUser() {
    // String userIdentifier = "admin";
    // try {
    // ArrayList<Group> groups = store.getGroupsByUser(userIdentifier);
    // if ((groups == null) || (groups.size() == 0)) {
    // fail("Should return none empty list of groups for user " + userIdentifier);
    // }
    // }
    // catch (SitoolsException e) {
    // fail("Exception - Should return groups for user " + userIdentifier);
    // }
  }

}
