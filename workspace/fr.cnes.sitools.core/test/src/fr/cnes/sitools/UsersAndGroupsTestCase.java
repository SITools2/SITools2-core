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
package fr.cnes.sitools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSource;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSourceFactory;
import fr.cnes.sitools.mail.MailAdministration;
import fr.cnes.sitools.security.JDBCUsersAndGroupsStore;
import fr.cnes.sitools.security.UsersAndGroupsAdministration;
import fr.cnes.sitools.security.model.Group;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test CRUD Users and Groups Rest API
 * 
 * @since UserStory : ADM-security, Release 1 - Sprint : 4
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class UsersAndGroupsTestCase extends AbstractSitoolsTestCase {
  /**
   * static jdbc datasource instance for the test
   */
  private static SitoolsSQLDataSource ds = null;

  /**
   * static jdbc store instance for the test
   */
  private static JDBCUsersAndGroupsStore store = null;

  /**
   * Restlet Component for server
   */
  private Component component = null;
  /**
   * The sitoolsSettings
   */
  private SitoolsSettings settings = SitoolsSettings.getInstance();

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + settings.getString(Consts.APP_SECURITY_URL);
  }

  @Override
  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_SECURITY_URL);
  }

  @Before
  @Override
  /**
   * Init and Start a server with InscriptionApplication
   * La datasource et le store sont créés une seule fois pour le test. 
   * Le composant est arrêté et recréé à chaque opération
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    if (ds == null) {
      ds = SitoolsSQLDataSourceFactory
          .getInstance()
          .setupDataSource(
              settings.getString("Tests.PGSQL_DATABASE_DRIVER"), settings.getString("Tests.PGSQL_DATABASE_URL"), settings.getString("Tests.PGSQL_DATABASE_USER"), settings.getString("Tests.PGSQL_DATABASE_PASSWORD"), settings.getString("Tests.PGSQL_DATABASE_SCHEMA")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    if (store == null) {
      store = new JDBCUsersAndGroupsStore("SitoolsJDBCStore", ds);
    }

    if (this.component == null) {
      this.component = new Component();
      this.component.getServers().add(Protocol.HTTP, getTestPort());
      this.component.getClients().add(Protocol.HTTP);
      this.component.getClients().add(Protocol.FILE);
      this.component.getClients().add(Protocol.CLAP);

      // Context
      Context ctx = this.component.getContext().createChildContext();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, settings);
      ctx.getAttributes().put(ContextAttributes.APP_STORE, store);

      this.component.getDefaultHost().attach(getAttachUrl(), new UsersAndGroupsAdministration(ctx));

      // Attachement de l'application de MAIL
      Context mailCtx = this.component.getContext().createChildContext();
      mailCtx.getAttributes().put(ContextAttributes.SETTINGS, settings);

      // Application
      MailAdministration mailAdministration = new MailAdministration(mailCtx, component);

      component.getInternalRouter().attach(settings.getString(Consts.APP_MAIL_ADMIN_URL), mailAdministration);

    }

    if (!this.component.isStarted()) {
      this.component.start();
    }
  }

  @After
  @Override
  /**
   * Stop server
   * @throws java.lang.Exception
   */
  public void tearDown() throws Exception {
    super.tearDown();
    this.component.stop();
    this.component = null;
  }

  /**
   * Test CRUD Users with JSon format exchanges.
   */
  @Test
  public void testCRUDUsers() {
    createUser();
    retrieveUser();
    updateUser();
    deleteUser();
    createWadl(getBaseUrl(), "users_groups");
  }

  /**
   * Test CRUD Groups with JSon format exchanges.
   * 
   * @throws InterruptedException
   */
  @Test
  public synchronized void testCRUDGroups() throws InterruptedException {
    wait(10000);
    createGroup();
    retrieveGroup();
    updateGroup();
    deleteGroup();
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

    Response response = getResponse(MediaType.APPLICATION_JSON, result, User.class, true);
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

    Response response = getResponse(MediaType.APPLICATION_JSON, result, Group.class, true);
    assertTrue(response.getSuccess());
    assertNotNull(response.getTotal());
    assertTrue(response.getTotal() > 0);
  }

  /**
   * Invokes GET and asserts result response is a non empty User array.
   */
  @Test
  public void testGetUsersByGroup() {
    ClientResource cr = new ClientResource(getBaseUrl() + "/groups/administrateurs/users");
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, User.class, true);
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
    Response response = getResponse(MediaType.APPLICATION_JSON, result, Group.class, true);
    assertTrue(response.getSuccess());
    assertNotNull(response.getTotal());
    assertTrue(response.getTotal() > 0);
  }

  // ----------------------------------------------------------------
  // CRUD USER

  /**
   * Invoke POST
   */
  public void createUser() {
    User myUser = new User("test-identifier", "mOtDePaSsE", "Prénom", "Nom", "m.gond@akka.eu");
    ClientResource crUsers = new ClientResource(getBaseUrl() + "/users");

    Representation result = crUsers.post(getRepresentationJSON(myUser));
    assertTrue(crUsers.getStatus().isSuccess());
    assertNotNull(result);
    Response response = getResponse(MediaType.APPLICATION_JSON, result, User.class);
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
    User myUser = new User("test-identifier", "mOtDePaSsE", "Prénom", "Nom", "m.gond@akka.eu");
    ClientResource cr = new ClientResource(getBaseUrl() + "/users/" + myUser.getIdentifier());
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, User.class);
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
   * Invoke PUT
   */
  public void updateUser() {
    User myUser = new User("test-identifier", "modified-mOtDePaSsE", "modified-Prénom", "modified-Nom",
        "modified-prenom.nom@societe.fr");

    ClientResource crUsers = new ClientResource(getBaseUrl() + "/users/" + myUser.getIdentifier());

    Representation result = crUsers.put(getRepresentationJSON(myUser));
    assertNotNull(result);
    assertTrue(crUsers.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, User.class);
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
    User myUser = new User("test-identifier", "mOtDePaSsE", "Prénom", "Nom", "prenom.nom@societe.fr");
    ClientResource cr = new ClientResource(getBaseUrl() + "/users/" + myUser.getIdentifier());
    Representation resultRepresentation = cr.delete(MediaType.APPLICATION_JSON);
    assertNotNull(resultRepresentation);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, resultRepresentation, User.class);
    assertTrue(response.getSuccess());
  }

  // ----------------------------------------------------------------
  // USER REPRESENTATIONS

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
   * REST API Representation wrapper of User
   * 
   * @param bean
   *          User
   * @return JsonRepresentation
   */
  public static Representation getRepresentationJSON(User bean) {
    return new JsonRepresentation(bean);
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

  // ----------------------------------------------------------------
  // CRUD GROUP

  /**
   * Invoke POST
   */
  public void createGroup() {
    Group myGroup = new Group("test-groupName", "test-groupDescription");
    ClientResource crGroups = new ClientResource(getBaseUrl() + "/groups");

    Representation result = crGroups.post(getRepresentationJSON(myGroup));
    assertTrue(crGroups.getStatus().isSuccess());
    assertNotNull(result);

    Response response = getResponse(MediaType.APPLICATION_JSON, result, Group.class);
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

    Response response = getResponse(MediaType.APPLICATION_JSON, result, Group.class);
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

    Response response = getResponse(MediaType.APPLICATION_JSON, result, Group.class);
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

    Response response = getResponse(MediaType.APPLICATION_JSON, resultRepresentation, Group.class);
    assertTrue(response.getSuccess());
  }

  // ----------------------------------------------------------------
  // GROUP REPRESENTATIONS

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
   * REST API Representation wrapper of Group
   * 
   * @param bean
   *          Group
   * @return JsonRepresentation
   */
  public static Representation getRepresentationJSON(Group bean) {
    return new JsonRepresentation(bean);
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

  // ------------------------------------------------------------
  // RESPONSE REPRESENTATION

  /**
   * REST API Response Representation wrapper for single or multiple items expexted
   * 
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @param dataClass
   *          class expected for items of the Response object
   * @return Response
   */
  public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass) {
    return getResponse(media, representation, dataClass, false);
  }

  /**
   * REST API Response Representation wrapper for single or multiple items expexted
   * 
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @param dataClass
   *          class expected for items of the Response object
   * @param isArray
   *          if true wrap the data property else wrap the item property
   * @return Response
   */
  public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass, boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("user", User.class);
      xstream.alias("group", Group.class);

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == User.class) {
          xstream.aliasField("user", Response.class, "item");
        }
        if (dataClass == Group.class) {
          xstream.aliasField("group", Response.class, "item");
        }
      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(MediaType.APPLICATION_JSON)) {
        Response response = rep.getObject("response");
        // TODO MEMO usage of SitoolsXStreamRepresentation.getObject("response") instead of standard signature Response
        // response = rep.getObject();

        return response;
      }
      else {
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON supported in tests");
        return null; // TODO complete test for XML, Object
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

}
