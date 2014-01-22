package fr.cnes.sitools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.role.model.Role;
import fr.cnes.sitools.security.model.Group;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.utils.GetRepresentationUtils;
import fr.cnes.sitools.utils.GetResponseUtils;

/**
 * Abstract test class for User and Group administration This class must be implemented for each database with custom
 * configuration
 * 
 * 
 * @author m.gond
 */
public abstract class AbstractUsersAndGroupsRoleNotificationTestCase extends AbstractSitoolsServerTestCase {

  protected String getBaseSecurityUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_SECURITY_URL);
  }

  protected String getBaseRoleUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_ROLES_URL);
  }

  /**
   * Simply test the Notification Resource, without the trigger mechanism to remove a user from roles
   * 
   * @throws InterruptedException
   */
  @Test
  public void testUserNotificationResource() throws InterruptedException {
    User user = createUserModel();
    Role role = createRoleModel("myRole1");
    Role role1 = createRoleModel("myRole2");

    addUserToRole(user, role);
    addUserToRole(user, role1);

    createRole(role);
    createRole(role1);

    role = retrieveRole(role.getId());
    assertNotNull(role.getUsers());
    assertEquals(1, role.getUsers().size());

    role1 = retrieveRole(role1.getId());
    assertNotNull(role1.getUsers());
    assertEquals(1, role1.getUsers().size());

    removeUserFromRoles(user);

    // wait for the notification to complete
    role = retrieveRole(role.getId());
    assertNull(role.getUsers());
    deleteRole(role);

    role1 = retrieveRole(role1.getId());
    assertNull(role1.getUsers());
    deleteRole(role1);
  }

  /**
   * Simply test the Notification Resource, without the trigger mechanism to remove a group from roles
   * 
   * @throws InterruptedException
   */
  @Test
  public void testGroupNotificationResource() throws InterruptedException {
    Group group = createGroupModel();
    Role role = createRoleModel("myRole1");
    Role role1 = createRoleModel("myRole2");

    addGroupToRole(group, role);
    addGroupToRole(group, role1);

    createRole(role);
    createRole(role1);

    role = retrieveRole(role.getId());
    assertNotNull(role.getGroups());
    assertEquals(1, role.getGroups().size());

    role1 = retrieveRole(role1.getId());
    assertNotNull(role1.getGroups());
    assertEquals(1, role1.getGroups().size());

    removeGroupFromRoles(group);

    // wait for the notification to complete
    role = retrieveRole(role.getId());
    assertNull(role.getGroups());
    deleteRole(role);

    role1 = retrieveRole(role1.getId());
    assertNull(role1.getGroups());
    deleteRole(role1);
  }

  /**
   * Test that creates a user and add to a role and then delete the user. Then it is expected that the user is not in
   * the role anymore
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  @Test
  public void testCreateUserAddToRoleAndDeleteUser() throws InterruptedException {
    User user = createUserModel();
    Role role = createRoleModel("myRole");
    try {

      addUserToRole(user, role);

      createUser(user);

      createRole(role);

      role = retrieveRole(role.getId());
      assertNotNull(role.getUsers());
      assertEquals(1, role.getUsers().size());
    }
    finally {
      deleteUser(user);
      // wait for the notification to complete
      Thread.sleep(5000);
      role = retrieveRole(role.getId());
      assertNull(role.getUsers());
      deleteRole(role);

    }

  }

  /**
   * Test that creates a user and add to 2 roles and then delete the user. Then it is expected that the user is not in
   * the roles anymore
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  public void testCreateUserAddMultipleRolesAndDeleteUser() throws InterruptedException {
    User user = createUserModel();
    Role role = createRoleModel("myRole1");
    Role role1 = createRoleModel("myRole2");
    try {

      addUserToRole(user, role);
      addUserToRole(user, role1);

      createUser(user);

      createRole(role);
      createRole(role1);

      role = retrieveRole(role.getId());
      assertNotNull(role.getUsers());
      assertEquals(1, role.getUsers().size());

      role1 = retrieveRole(role1.getId());
      assertNotNull(role1.getUsers());
      assertEquals(1, role1.getUsers().size());
    }
    finally {
      deleteUser(user);
      // wait for the notification to complete
      Thread.sleep(5000);
      role = retrieveRole(role.getId());
      assertNull(role.getUsers());
      deleteRole(role);

      role1 = retrieveRole(role1.getId());
      assertNull(role1.getUsers());
      deleteRole(role1);
    }
  }

  /**
   * Test that creates a group and add to a role and then delete the group. Then it is expected that the group is not in
   * the role anymore
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  @Test
  public void testCreateGroupAddToRoleAndDeleteGroup() throws InterruptedException {
    Group group = createGroupModel();
    Role role = createRoleModel("myRole");
    try {
      addGroupToRole(group, role);

      createGroup(group);

      createRole(role);

      role = retrieveRole(role.getId());
      assertNotNull(role.getGroups());
      assertEquals(1, role.getGroups().size());
    }
    finally {
      deleteGroup(group);
      Thread.sleep(5000);
      role = retrieveRole(role.getId());
      assertNull(role.getGroups());

      deleteRole(role);
    }

  }

  /**
   * Test that creates a group and add to 2 roles and then delete the group. Then it is expected that the group is not
   * in the roles anymore
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  @Test
  public void testCreateGroupAddMultipleRolesAndDeleteGroup() throws InterruptedException {
    Group group = createGroupModel();
    Role role = createRoleModel("myRole1");
    Role role1 = createRoleModel("myRole2");
    try {

      addGroupToRole(group, role);
      addGroupToRole(group, role1);

      createGroup(group);

      createRole(role);
      createRole(role1);

      role = retrieveRole(role.getId());
      assertNotNull(role.getGroups());
      assertEquals(1, role.getGroups().size());

      role1 = retrieveRole(role1.getId());
      assertNotNull(role1.getGroups());
      assertEquals(1, role1.getGroups().size());
    }
    finally {
      deleteGroup(group);
      // wait for the notification to complete
      Thread.sleep(5000);
      role = retrieveRole(role.getId());
      assertNull(role.getGroups());
      deleteRole(role);

      role1 = retrieveRole(role1.getId());
      assertNull(role1.getGroups());
      deleteRole(role1);
    }
  }

  private User createUserModel() {
    User myUser = new User("test-identifier", "mOtDePaSsE", "Prénom", "Nom", "m.gond@akka.eu");
    return myUser;
  }

  private Role createRoleModel(String roleName) {
    Role myRole = new Role();
    myRole.setName(roleName);
    myRole.setId(roleName);
    return myRole;

  }

  private void addUserToRole(User user, Role role) {
    List<Resource> users = new ArrayList<Resource>();
    Resource resUser = new Resource();
    resUser.setId(user.getIdentifier());
    users.add(resUser);
    role.setUsers(users);
  }

  private void addGroupToRole(Group group, Role role) {
    List<Resource> groups = new ArrayList<Resource>();
    Resource resGroup = new Resource();
    resGroup.setId(group.getName());
    groups.add(resGroup);
    role.setGroups(groups);
  }

  private Group createGroupModel() {
    Group myGroup = new Group("test-groupName", "test-groupDescription");
    return myGroup;
  }

  /**
   * Invoke POST
   */
  private void createUser(User myUser) {
    ClientResource crUsers = new ClientResource(getBaseSecurityUrl() + "/users");

    Representation result = crUsers.post(GetRepresentationUtils.getRepresentationUser(myUser, getMediaTest()));
    assertTrue(crUsers.getStatus().isSuccess());
    assertNotNull(result);
    Response response = GetResponseUtils.getResponseUserOrGroup(getMediaTest(), result, User.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    User resultUser = (User) response.getItem();
    assertEquals(resultUser.getIdentifier(), myUser.getIdentifier());
  }

  /**
   * Invoke POST to create Role
   */
  private void createRole(Role myRole) {
    ClientResource crUsers = new ClientResource(getBaseRoleUrl());

    Representation result = crUsers.post(GetRepresentationUtils.getRepresentationRole(myRole, getMediaTest()));
    assertTrue(crUsers.getStatus().isSuccess());
    assertNotNull(result);
    Response response = GetResponseUtils.getResponseRole(getMediaTest(), result, Role.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    Role roleResult = (Role) response.getItem();
    assertEquals(roleResult.getName(), myRole.getName());
  }

  /**
   * Invoke DELETE
   */
  private void deleteUser(User myUser) {
    ClientResource cr = new ClientResource(getBaseSecurityUrl() + "/users/" + myUser.getIdentifier());
    Representation resultRepresentation = cr.delete(getMediaTest());
    assertNotNull(resultRepresentation);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseUserOrGroup(getMediaTest(), resultRepresentation, User.class);
    assertTrue(response.getSuccess());
  }

  /**
   * Invoke DELETE on role
   */
  private void deleteRole(Role role) {
    ClientResource cr = new ClientResource(getBaseRoleUrl() + "/" + role.getId());
    Representation resultRepresentation = cr.delete(getMediaTest());
    assertNotNull(resultRepresentation);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseRole(getMediaTest(), resultRepresentation, Role.class);
    assertTrue(response.getSuccess());
  }

  /**
   * Invoke POST
   */
  private void createGroup(Group myGroup) {
    ClientResource crGroups = new ClientResource(getBaseSecurityUrl() + "/groups");

    Representation result = crGroups.post(GetRepresentationUtils.getRepresentationGroup(myGroup, getMediaTest()));
    assertTrue(crGroups.getStatus().isSuccess());
    assertNotNull(result);

    Response response = GetResponseUtils.getResponseUserOrGroup(getMediaTest(), result, Group.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());

    Group group = (Group) response.getItem();
    assertEquals(group.getName(), myGroup.getName());
    assertEquals(group.getDescription(), myGroup.getDescription());

  }

  /**
   * Invoke GET
   */
  private Role retrieveRole(String roleId) {
    ClientResource cr = new ClientResource(getBaseRoleUrl() + "/" + roleId);
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseRole(getMediaTest(), result, Role.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());

    Role role = (Role) response.getItem();
    return role;

  }

  /**
   * Invoke DELETE
   */
  private void deleteGroup(Group myGroup) {
    ClientResource cr = new ClientResource(getBaseSecurityUrl() + "/groups/" + myGroup.getName());
    Representation resultRepresentation = cr.delete(getMediaTest());
    assertNotNull(resultRepresentation);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseUserOrGroup(getMediaTest(), resultRepresentation, Group.class);
    assertTrue(response.getSuccess());
  }

  private void removeUserFromRoles(User user) {
    ClientResource crUsers = new ClientResource(getBaseRoleUrl() + "/users/notify/" + user.getIdentifier());

    Representation result = crUsers.put(new EmptyRepresentation());
    assertTrue(crUsers.getStatus().isSuccess());
    assertNotNull(result);
    Response response = GetResponseUtils.getResponseResource(getMediaTest(), result, Resource.class);
    assertTrue(response.getSuccess());
  }

  private void removeGroupFromRoles(Group group) {
    ClientResource crUsers = new ClientResource(getBaseRoleUrl() + "/groups/notify/" + group.getName());

    Representation result = crUsers.put(new EmptyRepresentation());
    assertTrue(crUsers.getStatus().isSuccess());
    assertNotNull(result);
    Response response = GetResponseUtils.getResponseResource(getMediaTest(), result, Resource.class);
    assertTrue(response.getSuccess());
  }

}