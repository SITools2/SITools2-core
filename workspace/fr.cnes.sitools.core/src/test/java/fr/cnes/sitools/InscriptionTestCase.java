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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
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
import fr.cnes.sitools.inscription.InscriptionApplication;
import fr.cnes.sitools.inscription.InscriptionStoreInterface;
import fr.cnes.sitools.inscription.InscriptionStoreXMLMap;
import fr.cnes.sitools.inscription.UserInscriptionApplication;
import fr.cnes.sitools.inscription.model.Inscription;
import fr.cnes.sitools.mail.MailAdministration;
import fr.cnes.sitools.security.JDBCUsersAndGroupsStore;
import fr.cnes.sitools.security.UsersAndGroupsAdministration;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test Inscription management Rest API
 * 
 * @since UserStory : ADM Inscriptions, Sprint : 4
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public class InscriptionTestCase extends AbstractSitoolsTestCase {
  /**
   * static xml store instance for the test
   */
  private static InscriptionStoreInterface store = null;

  /**
   * Restlet Component for server
   */
  private Component component = null;

  /**
   * relative url for inscription management REST API
   * 
   * @return url
   */
  protected String getAttachUrlUser() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_INSCRIPTIONS_USER_URL);
  }

  /**
   * relative url for inscription management REST API
   * 
   * @return url
   */
  protected String getAttachUrlAdmin() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_INSCRIPTIONS_ADMIN_URL);
  }

  /**
   * absolute url for inscription management REST API
   * 
   * @return url
   */
  protected String getBaseUrlAdmin() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_INSCRIPTIONS_ADMIN_URL);
  }

  /**
   * absolute url for inscription management REST API
   * 
   * @return url
   */
  protected String getBaseUrlUser() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_INSCRIPTIONS_USER_URL);
  }

  /**
   * Absolute path location for inscription store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_INSCRIPTIONS_STORE_DIR)
        + "/map";
  }

  @Before
  @Override
  /**
   * Create component, store and application and start server
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    SitoolsSettings settings = SitoolsSettings.getInstance();

    if (this.component == null) {
      this.component = createTestComponent(settings);

      // USERS AND GROUPS
      // Context
      Context ctxUAG = this.component.getContext().createChildContext();
      ctxUAG.getAttributes().put(ContextAttributes.SETTINGS, settings);

      SitoolsSQLDataSource ds = SitoolsSQLDataSourceFactory
          .getInstance()
          .setupDataSource(
              SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_DRIVER"), SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_URL"), SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_USER"), SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_PASSWORD"), SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_SCHEMA")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      JDBCUsersAndGroupsStore ugstore = new JDBCUsersAndGroupsStore("SitoolsJDBCStore", ds, ctxUAG);

      ctxUAG.getAttributes().put(ContextAttributes.APP_STORE, ugstore);

      UsersAndGroupsAdministration anApplication = new UsersAndGroupsAdministration(ctxUAG);

      // attach to the internatl router
      component.getInternalRouter().attach(settings.getString(Consts.APP_SECURITY_URL), anApplication);

      // INSCRIPTIONS

      Context ctxAdmin = this.component.getContext().createChildContext();
      ctxAdmin.getAttributes().put(ContextAttributes.SETTINGS, settings);

      if (store == null) {
        File storeDirectory = new File(getTestRepository());
        storeDirectory.mkdirs();
        cleanDirectory(storeDirectory);cleanMapDirectories(storeDirectory);
        store = new InscriptionStoreXMLMap(storeDirectory, ctxAdmin);
      }

      ctxAdmin.getAttributes().put(ContextAttributes.APP_STORE, store);

      this.component.getDefaultHost().attach(getAttachUrlAdmin(), new InscriptionApplication(ctxAdmin));

      Context ctxUser = this.component.getContext().createChildContext();
      ctxUser.getAttributes().put(ContextAttributes.SETTINGS, settings);
      ctxUser.getAttributes().put(ContextAttributes.APP_STORE, store);

      // disable captcha filter for this test only !
      ctxUser.getAttributes().put("Security.filter.captcha.enabled", false);

      this.component.getDefaultHost().attach(getAttachUrlUser(), new UserInscriptionApplication(ctxUser));

      // Mail application
      Context ctxMail = this.component.getContext().createChildContext();
      ctxMail.getAttributes().put(ContextAttributes.SETTINGS, settings);

      // Application
      MailAdministration mailAdministration = new MailAdministration(ctxMail, component);
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

    try {
      // TODO supprimer les utilisateurs pour revenir à l'état initial
      try {
        deleteReq("1000000");
        deleteReq("1000001");
        deleteReq("1000002");
      }
      catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      this.component.stop();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      this.component = null;
    }
  }

  /**
   * Call Security REST API via RIAP in order to delete a user with the specified identifier
   * 
   * @param id
   *          user identifier
   */
  private void deleteReq(String id) {
    try {
      Request reqDELETE = new Request(Method.DELETE, "riap://component"
          + SitoolsSettings.getInstance().getString(Consts.APP_SECURITY_URL) + "/users/" + id);
      component.getContext().getClientDispatcher().handle(reqDELETE);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Test CRUD DataSet with JSon format exchanges.
   * 
   * Pré-conditions : - Un serveur d'administration - Une application d'inscription - Une application de gestion des
   * utilisateurs
   */
  @Test
  public void testCRUDJson() {
    Inscription item = new Inscription();
    item.setId("1000000");
    item.setIdentifier("1000000");
    item.setFirstName("firstName");
    item.setLastName("lastName");
    item.setComment("comment");
    item.setEmail("m.gond@akka.eu");
    item.setPassword("mOtDePaSsE1");

    assertNone();
    userInscription(item);
    adminGetInscriptions();
    adminValidateInscriptionWithNullRepresentation(item);
    assertNone();
  }

  /**
   * 
   * Test d'une modification d'une inscription par un put d'une nouvelle representation
   * 
   * Scenario : - l'admin crée une nouvelle inscription - l'admin modifie l'inscription => validation ? - création d'un
   * utilisateur - suppression de l'inscription
   * 
   */
  @Test
  public void testAdminCRUDInscriptionWithRepresentation() {
    assertNone();

    Inscription item1 = new Inscription();
    item1.setId("1000001");
    item1.setIdentifier("1000001");
    item1.setPassword("password");
    item1.setFirstName("firstName");
    item1.setLastName("lastName");
    item1.setComment("comment");
    item1.setEmail("email");

    adminCreate(item1);

    // la suppression doit réussir puisque l'inscription existe
    adminDelete(item1, true);

    // on la recrée
    adminCreate(item1);

    adminRetrieve(item1);

    item1.setFirstName("firstName-modified");
    item1.setLastName("lastName-modified");
    item1.setComment("comment-modified");
    item1.setEmail("email-modified");

    // modification d'une inscription
    adminUpdate(item1); // => Ne doit pas faire Validate

    // la suppression doit réussir puisque l'inscription existe
    adminDelete(item1, true);
    createWadl(getBaseUrlUser(), "inscription_user");
    createWadl(getBaseUrlAdmin(), "inscription_admin");
  }

  /**
   * Test d'une validation d'une inscription par un put avec une representation null
   * 
   * Scenario : - l'admin crée une nouvelle inscription - l'admin modifie l'inscription sans fournir de representation
   * => validation -> création d'un utilisateur -> suppression de l'inscription
   * 
   */
  @Test
  public void testAdminCRUDInscriptionWithNullRepresentation() {

    Inscription item = new Inscription();
    item.setId("1000002");
    item.setIdentifier("1000002");
    item.setPassword("mOtDePaSsE1");
    item.setFirstName("test-userInscription.firstName");
    item.setLastName("test-userInscription.lastName");
    item.setEmail("test-userInscription.email");
    item.setComment("test-userInscription.comment");

    assertNone();

    userInscription(item);

    adminGetInscriptions();

    adminValidateInscriptionWithNullRepresentation(item);

    adminDelete(item, false); // inscription should not exists

    assertNone();
  }

  /**
   * Invoke POST
   * 
   * @param item
   *          inscription
   */
  public void userInscription(Inscription item) {
    Representation rep = getRepresentation(item);
    ClientResource cr = new ClientResource(this.getBaseUrlUser());
    Representation result = cr.post(rep, MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = getResponse(MediaType.APPLICATION_JSON, result, Inscription.class);
    assertNotNull(response);
    LOGGER.info(response.toString());
    assertTrue(response.getMessage(), response.getSuccess());
    assertNotNull(response.getItem());
    assertEquals(item.getFirstName(), item.getFirstName());
    assertEquals(item.getLastName(), item.getLastName());
    assertEquals(item.getComment(), item.getComment());
    assertEquals(item.getEmail(), item.getEmail());
  }

  /**
   * Invoke GET
   */
  public void adminGetInscriptions() {
    ClientResource cr = new ClientResource(this.getBaseUrlAdmin());
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = getResponse(MediaType.APPLICATION_JSON, result, Inscription.class, true);
    assertNotNull(response);
    LOGGER.info(response.toString());
    assertTrue(response.getSuccess());

    // un tableau est attendu dans la reponse
    assertNull(response.getItem());
    assertNotNull(response.getData());
  }

  /**
   * Invokes GET and asserts result response is an empty array.
   */
  public void assertNone() {
    ClientResource cr = new ClientResource(this.getBaseUrlAdmin());
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, Inscription.class, true);
    assertNotNull(response);
    LOGGER.info(response.toString());
    assertTrue(response.getSuccess());
    assertNull(response.getItem());
    assertEquals(0, response.getTotal().intValue());
  }

  /**
   * Validate inscription (Representation must be null)
   * 
   * @param item
   *          inscription
   */
  public void adminValidateInscriptionWithNullRepresentation(Inscription item) {
    ClientResource cr = new ClientResource(this.getBaseUrlAdmin() + "/" + item.getId());
    Representation result = cr.put(null, MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = getResponse(MediaType.APPLICATION_JSON, result, Inscription.class);
    assertNotNull(response);
    LOGGER.info(response.toString());
    assertTrue(response.getSuccess());
    // assertNotNull(response.getItem());

    // Test with an empty representation
    // Representation rep = new EmptyRepresentation();

  }

  /**
   * Invoke POST by user admin
   * 
   * @param item
   *          inscription
   */
  public void adminCreate(Inscription item) {
    Representation rep = getRepresentation(item);
    ClientResource cr = new ClientResource(this.getBaseUrlAdmin());
    Representation result = cr.post(rep, MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = getResponse(MediaType.APPLICATION_JSON, result, Inscription.class);
    assertNotNull(response);
    LOGGER.info(response.toString());
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    assertEquals(item.getFirstName(), item.getFirstName());
    assertEquals(item.getLastName(), item.getLastName());
    assertEquals(item.getComment(), item.getComment());
    assertEquals(item.getEmail(), item.getEmail());
  }

  /**
   * Invoke GET
   * 
   * @param item
   *          inscription
   */
  public void adminRetrieve(Inscription item) {
    ClientResource cr = new ClientResource(this.getBaseUrlAdmin() + "/" + item.getId());
    Representation result = cr.get(MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = getResponse(MediaType.APPLICATION_JSON, result, Inscription.class);
    assertNotNull(response);
    LOGGER.info(response.toString());
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          inscription
   */
  public void adminUpdate(Inscription item) {
    Representation rep = getRepresentation(item);
    ClientResource cr = new ClientResource(this.getBaseUrlAdmin() + "/" + item.getId());
    Representation result = cr.put(rep, MediaType.APPLICATION_JSON);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = getResponse(MediaType.APPLICATION_JSON, result, Inscription.class);
    assertNotNull(response);
    LOGGER.info(response.toString());
    assertTrue(response.getSuccess());
    // assertEquals(item.getFirstName(), item.getFirstName());
    // assertEquals(item.getLastName(), item.getLastName());
    // assertEquals(item.getComment(), item.getComment());
    // assertEquals(item.getEmail(), item.getEmail());
  }

  /**
   * Get the json representation of the following inscription
   * 
   * @param item
   *          the Inscription
   * @return the json representation of the following inscription
   */
  private Representation getRepresentation(Inscription item) {
    Representation rep = new JacksonRepresentation<Inscription>(item);
    return rep;
  }

  /**
   * Invoke DELETE assert true if inscription exist and param "exists" is true. assert true if inscription doesnt exist
   * and param "exists" is false.
   * 
   * @param item
   *          inscription
   * @param exists
   *          if inscription exists or not
   */
  public void adminDelete(Inscription item, boolean exists) {
    ClientResource cr = new ClientResource(this.getBaseUrlAdmin() + "/" + item.getId());
    Representation result = cr.delete(MediaType.APPLICATION_JSON);
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(MediaType.APPLICATION_JSON, result, Inscription.class);
    assertNotNull(response);
    LOGGER.info(response.toString());
    assertEquals(response.getSuccess().booleanValue(), exists);
  }

  // ------------------------------------------------------------
  // RESPONSE REPRESENTATION

  /**
   * REST API Response wrapper for single item expected.
   * 
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @param dataClass
   *          class expected in the item property of the Response object
   * @return Response the response.
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
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("inscription", Inscription.class);
      xstream.omitField(Inscription.class, "name");
      xstream.omitField(Inscription.class, "description");

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == Inscription.class) {
          xstream.aliasField("inscription", Response.class, "item");
        }
      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(MediaType.APPLICATION_JSON)) {
        Response response = rep.getObject("response");
        // Response response = rep.getObject();

        return response;
      }
      else {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null; // TODO complete test for XML, Object
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }
}
