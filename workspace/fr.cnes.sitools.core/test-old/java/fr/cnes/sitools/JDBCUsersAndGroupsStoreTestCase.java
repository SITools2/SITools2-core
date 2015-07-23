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
package fr.cnes.sitools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Context;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSource;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSourceFactory;
import fr.cnes.sitools.security.JDBCUsersAndGroupsStore;
import fr.cnes.sitools.security.model.Group;
import fr.cnes.sitools.security.model.User;

/**
 * Test CRUD Users & Groups with JDBC DataSource persistence
 * 
 * @since UserStory : ADM Security - Sprint : 4
 * @author jp.boignard (AKKA Technologies)
 */
public class JDBCUsersAndGroupsStoreTestCase extends AbstractSitoolsTestCase {
  /**
   * static jdbc datasource instance for the test
   */
  private static SitoolsSQLDataSource ds = null;

  /**
   * static jdbc store instance for the test
   */
  private static JDBCUsersAndGroupsStore store = null;

  @Before
  @Override
  /**
   * Create store
   * @throws Exception
   */
  public void setUp() throws Exception {
    Context ctxUAG = new Context();
    ctxUAG.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());

    if (ds == null) {
      ds = SitoolsSQLDataSourceFactory
          .getInstance()
          .setupDataSource(
              SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_DRIVER"), SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_URL"), SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_USER"), SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_PASSWORD"), SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_SCHEMA")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    if (store == null) {
      store = new JDBCUsersAndGroupsStore("SitoolsJDBCStore", ds, ctxUAG);
    }
  }

  @After
  @Override
  /**
   * TODO Stop & Close   Store & DataSource
   * Nothing
   * @throws Exception
   */
  public void tearDown() throws Exception {
    // stop / close Store
    // store.stop();
    //
    // stop / close DataSource
    // ds.stop();
  }

  /**
   * Test Get Users.
   */
  @Test
  public void testGetUsers() {
    try {
      List<User> users = store.getUsers();
      if ((users == null) || (users.size() == 0)) {
        fail("Should return none empty list of users");
      }
    }
    catch (SitoolsException e) {
      fail("Should return all users");
    }
  }

  /**
   * Test Get Groups.
   */
  @Test
  public void testGetGroups() {
    try {
      List<Group> groups = store.getGroups();
      if ((groups == null) || (groups.size() == 0)) {
        fail("Should return none empty list of groups");
      }
    }
    catch (SitoolsException e) {
      fail("Exception - Should return groups");
    }
  }

  /**
   * Test Get Users by group.
   */
  @Test
  public void testGetUsersByGroup() {
    String groupName = "administrator";
    try {
      List<User> users = store.getUsersByGroup(groupName);
      if ((users == null) || (users.size() == 0)) {
        fail("Should return none empty list of users");
      }
    }
    catch (SitoolsException e) {
      fail("Exception - Should return users for group " + groupName);
    }
  }

  /**
   * Test Get Groups by user.
   */
  @Test
  public void testGetGroupsByUser() {
    String userIdentifier = "admin";
    try {
      List<Group> groups = store.getGroupsByUser(userIdentifier);
      if ((groups == null) || (groups.size() == 0)) {
        fail("Should return none empty list of groups for user " + userIdentifier);
      }
    }
    catch (SitoolsException e) {
      fail("Exception - Should return groups for user " + userIdentifier);
    }
  }

  /**
   * Test Get User by id.
   */
  @Test
  public void testGetUserById() {
    String userIdentifier = "admin";
    try {

      User user = store.getUserById(userIdentifier);
      if (user == null) {
        fail("Should return user");
      }

    }
    catch (SitoolsException e) {
      fail("Exception - Should return groups for user " + userIdentifier);
    }

    try {
      userIdentifier = "AXKJLJSIOJ";
      User user = store.getUserById(userIdentifier);
      assertNull(user);
      // plus d'exception fail("Should throw exception for unknown user");
    }
    catch (SitoolsException e) {
      // Exception expected
      // plus d'exception assertTrue(true);
      fail("Should not throw an exception");
    }
  }

  /**
   * Test Get Group by id.
   */
  @Test
  public void testGetGroupById() {
    String groupName = "administrator";
    try {

      Group group = store.getGroupById(groupName);
      if (group == null) {
        fail("Should return group");
      }

    }
    catch (SitoolsException e) {
      fail("Exception - Should return group");
    }

    try {
      groupName = "AXKJLJSIOJ";
      Group group = store.getGroupById(groupName);
      assertNull(group);
      // plus d'exception fail("Should throw exception for unknown group");
    }
    catch (SitoolsException e) {
      // Exception expected
      // assertTrue(true);
      fail("Should not throw an exception");
    }
  }

  /**
   * Test CRUD Users scenario.
   */
  @Test
  public void testCRUDUsers() {
    createUser();
    updateUser();
    deleteUser();
  }

  /**
   * Test CRUD Groups scenario.
   */
  @Test
  public void testCRUDGroups() {
    createGroup();
    updateGroup();
    deleteGroup();
  }


  /**
   * Test Exceptions dans les CRUDs
   */
  @Test
  public void testCRUDUsersExceptions() {
    createBadUserWrongLogin();
  }

  /**
   * Invokes store.create and asserts result is conform.
   */
  public void createUser() {
    User myUser = new User("test-identifier", "mOtDePaSsE", "Prénom", "Nom", "prenom.nom@societe.fr");
    try {
      User resultUser = store.createUser(myUser);
      assertEquals(myUser.getIdentifier(), resultUser.getIdentifier());
      assertEquals(myUser.getFirstName(), resultUser.getFirstName());
      assertEquals(myUser.getLastName(), resultUser.getLastName());
      assertEquals(myUser.getEmail(), resultUser.getEmail());
      assertEquals(myUser.getSecret(), resultUser.getSecret());
    }
    catch (SitoolsException e) {
      fail("Cannot create a user cause: " + e.getMessage());
    }
  }

  /**
   * Invokes store.update and asserts result is conform.
   */
  public void updateUser() {
    User myUser = new User("test-identifier", "modified-mOtDePaSsE", "modified-Prénom", "modified-Nom",
        "modified-prenom.nom@societe.fr");
    try {
      User resultUser = store.updateUser(myUser);

      assertEquals(myUser.getIdentifier(), resultUser.getIdentifier());
      assertEquals(myUser.getFirstName(), resultUser.getFirstName());
      assertEquals(myUser.getLastName(), resultUser.getLastName());
      assertEquals(myUser.getEmail(), resultUser.getEmail());
      assertEquals(myUser.getSecret(), resultUser.getSecret());
    }
    catch (SitoolsException e) {
      fail("Cannot update a user cause: " + e.getMessage());
    }
  }

  /**
   * Invokes store.delete and asserts result is conform.
   */
  public void deleteUser() {
    User myUser = new User("test-identifier", "mOtDePaSsE", "Prénom", "Nom", "prenom.nom@societe.fr");
    try {
      store.deleteUser(myUser.getIdentifier());

    }
    catch (SitoolsException e) {
      fail("Cannot delete a user cause: " + e.getMessage());
    }
  }

  /**
   * Invokes store.create and asserts result is conform.
   */
  public void createGroup() {
    Group myGroup = new Group("test-groupName", "test-groupDescription");
    try {
      store.createGroup(myGroup);
    }
    catch (SitoolsException e) {
      fail("Cannot create a user cause: " + e.getMessage());
    }
  }

  /**
   * Invokes store.update and asserts result is conform.
   */
  public void updateGroup() {
    Group myGroup = new Group("test-groupName", "modified-test-groupDescription");
    try {
      Group groupResult = store.updateGroup(myGroup);

      assertEquals(groupResult.getDescription(), myGroup.getDescription());

    }
    catch (SitoolsException e) {
      fail("Cannot update group cause: " + e.getMessage());
    }
  }

  /**
   * Invokes store.delete and asserts result is conform.
   */
  public void deleteGroup() {
    try {
      store.deleteGroup("test-groupName");
    }
    catch (SitoolsException e) {
      fail("Cannot delete group cause: " + e.getMessage());
    }
  }


  /**
   * Create badly formed user to check exception throwing identifier must have at least 4 characters.
   */
  public void createBadUserWrongLogin() {
    User myUser = new User("te", "", "Prénom", "Nom", "prenom.nom@societe.fr");
    try {
      store.createUser(myUser);
      fail("La création d'un tel user devrait lever une exception.");
    }
    catch (SitoolsException se) {
      assertEquals(se.getMessage().startsWith("WRONG_USER_LOGIN"), true);
    }
  }
}
