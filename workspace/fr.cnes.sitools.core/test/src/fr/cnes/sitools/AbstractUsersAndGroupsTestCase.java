package fr.cnes.sitools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.restlet.Component;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSource;
import fr.cnes.sitools.security.JDBCUsersAndGroupsStore;
import fr.cnes.sitools.security.model.Group;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.utils.GetResponseUtils;

/**
 * Abstract test class for User and Group administration This class must be implemented for each database with custom
 * configuration
 * 
 * 
 * @author m.gond
 */
public abstract class AbstractUsersAndGroupsTestCase extends AbstractSitoolsTestCase {

  /**
   * static jdbc datasource instance for the test
   */
  protected static SitoolsSQLDataSource ds = null;
  /**
   * static jdbc store instance for the test
   */
  protected static JDBCUsersAndGroupsStore store = null;
  /**
   * Restlet Component for server
   */
  protected Component component = null;
  /**
   * The sitoolsSettings
   */
  protected SitoolsSettings settings = SitoolsSettings.getInstance();

  /**
   * REST API Representation wrapper of User
   * 
   * @param bean
   *          User
   * @return XStreamRepresentation
   */
  public static Representation getRepresentationXML(User bean) {
    return new XstreamRepresentation<User>(MediaType.APPLICATION_XML, bean);
  }

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + settings.getString(Consts.APP_SECURITY_URL);
  }

  @Override
  protected String getBaseUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_SECURITY_URL);
  }

  /**
   * Test CRUD Users with JSon format exchanges.
   */
  @Test
  public void testCRUDUsers() {
    try {
      createUser();
      retrieveUser();
      sortUsers();
      filterUsers();
      updateUser();
    }
    finally {
      deleteUser();
    }
  }

  /**
   * Test CRUD Groups with JSon format exchanges.
   * 
   * @throws InterruptedException
   *           if happens
   */
  @Test
  public synchronized void testCRUDGroups() throws InterruptedException {
    wait(10000);
    try {
      createGroup();
      retrieveGroup();
      sortGroups();
      filterGroups();
      updateGroup();
    }
    finally {
      deleteGroup();
    }
  }

  /**
   * Invokes GET and asserts result response is a non empty User array.
   */
  @Test
  public void testGetUsers() {
    ClientResource cr = new ClientResource(getBaseUrl() + "/users");
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseUserOrGroup(MediaType.APPLICATION_JSON, result, User.class, true);
    assertTrue(response.getSuccess());
    assertNotNull(response.getTotal());
    assertTrue(response.getTotal() > 0);
  }

  /**
   * Invokes GET and asserts result response is a non empty Group array.
   */
  @Test
  public void testGetGroups() {
    ClientResource cr = new ClientResource(getBaseUrl() + "/groups");
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseUserOrGroup(MediaType.APPLICATION_JSON, result, Group.class, true);
    assertTrue(response.getSuccess());
    assertNotNull(response.getTotal());
    assertTrue(response.getTotal() > 0);
  }

  /**
   * Invokes GET and asserts result response is a non empty User array.
   */
  @Test
  public void testGetUsersByGroup() {
    ClientResource cr = new ClientResource(getBaseUrl() + "/groups/administrator/users");
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseUserOrGroup(MediaType.APPLICATION_JSON, result, User.class, true);
    assertTrue(response.getSuccess());
    assertNotNull(response.getTotal());
    assertTrue(response.getTotal() > 0);
  }

  /**
   * Invokes GET and asserts result response is a non empty Group array.
   */
  @Test
  public void testGetGroupsByUser() {
    ClientResource cr = new ClientResource(getBaseUrl() + "/users/admin/groups");
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = GetResponseUtils.getResponseUserOrGroup(MediaType.APPLICATION_JSON, result, Group.class, true);
    assertTrue(response.getSuccess());
    assertNotNull(response.getTotal());
    assertTrue(response.getTotal() > 0);
  }

  /**
   * Invoke POST
   */
  public void createUser() {
    User myUser = new User("test-identifier", "mOtDePaSsE1", "Prénom", "Nom test identifier", "m.gond@akka.eu");
    ClientResource crUsers = new ClientResource(getBaseUrl() + "/users");

    Representation result = crUsers.post(getRepresentationJSON(myUser));
    assertTrue(crUsers.getStatus().isSuccess());
    assertNotNull(result);
    Response response = GetResponseUtils.getResponseUserOrGroup(MediaType.APPLICATION_JSON, result, User.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    User resultUser = (User) response.getItem();
    assertEquals(resultUser.getIdentifier(), myUser.getIdentifier());
    assertEquals(resultUser.getFirstName(), myUser.getFirstName());
    assertEquals(resultUser.getLastName(), myUser.getLastName());
    assertNull(resultUser.getSecret()); // secret is private
    assertEquals(resultUser.getEmail(), myUser.getEmail());
  }

  /**
   * Invoke GET
   */
  public void retrieveUser() {
    User myUser = new User("test-identifier", "mOtDePaSsE", "Prénom", "Nom test identifier", "m.gond@akka.eu");
    ClientResource cr = new ClientResource(getBaseUrl() + "/users/" + myUser.getIdentifier());
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseUserOrGroup(MediaType.APPLICATION_JSON, result, User.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    User resultUser = (User) response.getItem();
    assertEquals(resultUser.getIdentifier(), myUser.getIdentifier());
    assertEquals(resultUser.getFirstName(), myUser.getFirstName());
    assertEquals(resultUser.getLastName(), myUser.getLastName());
    assertNull(resultUser.getSecret()); // secret is private
    assertEquals(resultUser.getEmail(), myUser.getEmail());
  }

  /**
   * Invoke GET
   */
  public void filterUsers() {
    User myUser = new User("test-identifier", "mOtDePaSsE", "Prénom", "Nom test identifier", "m.gond@akka.eu");
    ClientResource cr = new ClientResource(getBaseUrl() + "/users?action=find&query=nom test");
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseUserOrGroup(MediaType.APPLICATION_JSON, result, User.class, true);
    assertTrue(response.getSuccess());
    assertNotNull(response.getData());
    assertEquals(1, response.getData().size());

    User resultUser = (User) response.getData().get(0);
    assertEquals(resultUser.getIdentifier(), myUser.getIdentifier());
    assertEquals(resultUser.getFirstName(), myUser.getFirstName());
    assertEquals(resultUser.getLastName(), myUser.getLastName());
    assertNull(resultUser.getSecret()); // secret is private
    assertEquals(resultUser.getEmail(), myUser.getEmail());
  }

  /**
   * Invoke GET
   */
  public void sortUsers() {
    List<User> usersSortedFromServer = getUserListFromServer(true);
    List<User> usersNotSorted = getUserListFromServer(false);

    Collections.sort(usersNotSorted, new Comparator<User>() {
      @Override
      public int compare(User u1, User u2) {
        return u1.getIdentifier().compareTo(u2.getIdentifier());
      }
    });

    assertEquals(usersNotSorted.size(), usersSortedFromServer.size());

    System.out.println("USERS FROM SERVER SORTED LOCALLY");
    for (User user : usersNotSorted) {
      System.out.println(user.getIdentifier() + ",");
    }

    System.out.println("USERS FROM SERVER SORTED REMOTELLY");
    for (User user : usersSortedFromServer) {
      System.out.println(user.getIdentifier() + ",");
    }

    int i = 0;
    while (i < usersNotSorted.size()) {
      assertEquals(usersNotSorted.get(i).getIdentifier(), usersSortedFromServer.get(i).getIdentifier());
      i++;
    }

  }

  private List<User> getUserListFromServer(boolean sorted) {
    String url = getBaseUrl() + "/users?start=0&limit=100&action=enum";
    if (sorted) {
      url += "&sort=identifier&dir=ASC";
    }
    ClientResource cr = new ClientResource(url);
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseUserOrGroup(MediaType.APPLICATION_JSON, result, User.class, true);
    assertTrue(response.getSuccess());
    assertNotNull(response.getData());
    return getList(response.getData());
  }

  // / GROUPS

  /**
   * Invoke GET
   */
  public void filterGroups() {

    Group myGroup = new Group("test-groupName", "test-groupDescription");
    ClientResource crGroups = new ClientResource(getBaseUrl() + "/groups?action=find&query=test-group");

    Representation result = crGroups.get(MediaType.APPLICATION_JSON);
    assertTrue(crGroups.getStatus().isSuccess());
    assertNotNull(result);

    Response response = GetResponseUtils.getResponseUserOrGroup(MediaType.APPLICATION_JSON, result, Group.class, true);
    assertTrue(response.getSuccess());
    assertNotNull(response.getData());

    assertEquals(1, response.getData().size());

    Group group = (Group) response.getData().get(0);
    assertEquals(group.getName(), myGroup.getName());
    assertEquals(group.getDescription(), myGroup.getDescription());
  }

  /**
   * Invoke GET
   */
  public void sortGroups() {
    List<Group> groupsSortedFromServer = getGroupListFromServer(true);
    List<Group> groupNotSorted = getGroupListFromServer(false);

    Collections.sort(groupNotSorted, new Comparator<Group>() {
      @Override
      public int compare(Group g1, Group g2) {
        return g1.getName().compareTo(g2.getName());
      }
    });

    assertEquals(groupNotSorted.size(), groupsSortedFromServer.size());

    System.out.println("GROUPS FROM SERVER SORTED LOCALLY");
    for (Group group : groupNotSorted) {
      System.out.println(group.getName() + ",");
    }

    System.out.println("GROUPS FROM SERVER SORTED REMOTELLY");
    for (Group group : groupsSortedFromServer) {
      System.out.println(group.getName() + ",");
    }

    int i = 0;
    while (i < groupNotSorted.size()) {
      assertEquals(groupNotSorted.get(i).getName(), groupsSortedFromServer.get(i).getName());
      i++;
    }

  }

  private List<Group> getGroupListFromServer(boolean sorted) {
    String url = getBaseUrl() + "/groups?start=0&limit=100&action=enum";
    if (sorted) {
      url += "&sort=name&dir=ASC";
    }
    ClientResource cr = new ClientResource(url);
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseUserOrGroup(MediaType.APPLICATION_JSON, result, Group.class, true);
    assertTrue(response.getSuccess());
    assertNotNull(response.getData());
    return getList(response.getData());
  }

  private <T> List<T> getList(ArrayList<Object> data) {
    ArrayList<T> users = new ArrayList<T>();
    for (Object object : data) {
      users.add((T) object);
    }
    return users;
  }

  /**
   * Invoke PUT
   */
  public void updateUser() {
    User myUser = new User("test-identifier", "modified-mOtDePaSsE", "modified-Prénom", "modified-Nom",
        "modified-prenom.nom@societe.fr");

    ClientResource crUsers = new ClientResource(getBaseUrl() + "/users/" + myUser.getIdentifier());

    Representation result = crUsers.put(getRepresentationJSON(myUser));
    assertNotNull(result);
    assertTrue(crUsers.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseUserOrGroup(MediaType.APPLICATION_JSON, result, User.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    User resultUser = (User) response.getItem();
    assertEquals(resultUser.getIdentifier(), myUser.getIdentifier());
    assertEquals(resultUser.getFirstName(), myUser.getFirstName());
    assertEquals(resultUser.getLastName(), myUser.getLastName());
    assertNull(resultUser.getSecret()); // secret is private
    assertEquals(resultUser.getEmail(), myUser.getEmail());
  }

  /**
   * Invoke DELETE
   */
  public void deleteUser() {
    User myUser = new User("test-identifier", "mOtDePaSsE", "Prénom", "Nom test identifier", "prenom.nom@societe.fr");
    ClientResource cr = new ClientResource(getBaseUrl() + "/users/" + myUser.getIdentifier());
    Representation resultRepresentation = cr.delete(MediaType.APPLICATION_JSON);
    assertNotNull(resultRepresentation);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseUserOrGroup(MediaType.APPLICATION_JSON, resultRepresentation,
        User.class);
    assertTrue(response.getSuccess());
  }

  /**
   * REST API Representation wrapper of User
   * 
   * @param bean
   *          User
   * @return JacksonRepresentation
   */
  public static Representation getRepresentationJSON(User bean) {
    return new JacksonRepresentation<User>(bean);
  }

  /**
   * REST API Representation wrapper of User
   * 
   * @param bean
   *          User
   * @return FormRepresentation
   */
  public static Representation getRepresentationFORM(User bean) {
    // Form Representation
    Form form = new Form();
    form.add("identifier", bean.getIdentifier());
    form.add("firstname", bean.getFirstName());
    form.add("lastname", bean.getLastName());
    form.add("email", bean.getEmail());
    return form.getWebRepresentation();
  }

  /**
   * REST API Representation wrapper of Group
   * 
   * @param bean
   *          Group
   * @return XStreammRepresentation
   */
  public static Representation getRepresentationXML(Group bean) {
    return new XstreamRepresentation<Group>(MediaType.APPLICATION_XML, bean);
  }

  /**
   * Invoke POST
   */
  public void createGroup() {
    Group myGroup = new Group("test-groupName", "test-groupDescription");
    ClientResource crGroups = new ClientResource(getBaseUrl() + "/groups");

    Representation result = crGroups.post(getRepresentationJSON(myGroup));
    assertTrue(crGroups.getStatus().isSuccess());
    assertNotNull(result);

    Response response = GetResponseUtils.getResponseUserOrGroup(MediaType.APPLICATION_JSON, result, Group.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());

    Group group = (Group) response.getItem();
    assertEquals(group.getName(), myGroup.getName());
    assertEquals(group.getDescription(), myGroup.getDescription());

  }

  /**
   * Invoke GET
   */
  public void retrieveGroup() {
    Group myGroup = new Group("test-groupName", "test-groupDescription");
    ClientResource cr = new ClientResource(getBaseUrl() + "/groups/" + myGroup.getName());
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseUserOrGroup(MediaType.APPLICATION_JSON, result, Group.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());

    Group group = (Group) response.getItem();
    assertEquals(group.getName(), myGroup.getName());
    assertEquals(group.getDescription(), myGroup.getDescription());
  }

  /**
   * Invoke PUT
   */
  public void updateGroup() {
    Group myGroup = new Group("test-groupName", "modified-test-groupDescription");
    ClientResource crGroup = new ClientResource(getBaseUrl() + "/groups/" + myGroup.getName());

    Representation result = crGroup.put(getRepresentationJSON(myGroup));
    assertTrue(crGroup.getStatus().isSuccess());
    assertNotNull(result);

    Response response = GetResponseUtils.getResponseUserOrGroup(MediaType.APPLICATION_JSON, result, Group.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());

    Group group = (Group) response.getItem();
    assertEquals(group.getName(), myGroup.getName());
    assertEquals(group.getDescription(), myGroup.getDescription());
  }

  /**
   * Invoke DELETE
   */
  public void deleteGroup() {
    Group myGroup = new Group("test-groupName", "test-groupDescription");
    ClientResource cr = new ClientResource(getBaseUrl() + "/groups/" + myGroup.getName());
    Representation resultRepresentation = cr.delete(MediaType.APPLICATION_JSON);
    assertNotNull(resultRepresentation);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseUserOrGroup(MediaType.APPLICATION_JSON, resultRepresentation,
        Group.class);
    assertTrue(response.getSuccess());
  }

  /**
   * REST API Representation wrapper of Group
   * 
   * @param bean
   *          Group
   * @return Representation
   */
  public static Representation getRepresentationJSON(Group bean) {
    return new JacksonRepresentation<Group>(bean);
  }

  /**
   * REST API Representation wrapper of Group
   * 
   * @param bean
   *          Group
   * @return FormRepresentation
   */
  public static Representation getRepresentationFORM(Group bean) {
    // Form Representation
    Form form = new Form();
    form.add("name", bean.getName());
    form.add("description", bean.getDescription());
    return form.getWebRepresentation();
  }

  public AbstractUsersAndGroupsTestCase() {
    super();
  }

}