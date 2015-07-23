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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.CookieSetting;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.util.Series;

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
import fr.cnes.sitools.security.JDBCUsersAndGroupsStore;
import fr.cnes.sitools.security.UsersAndGroupsAdministration;
import fr.cnes.sitools.security.captcha.CaptchaContainer;
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
public class InscriptionWithCaptchaTestCase extends AbstractSitoolsTestCase {
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
      this.component = new Component();
      this.component.getServers().add(Protocol.HTTP, getTestPort());
      this.component.getClients().add(Protocol.HTTP);
      this.component.getClients().add(Protocol.FILE);
      this.component.getClients().add(Protocol.CLAP);

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
        cleanDirectory(storeDirectory);
        cleanMapDirectories(storeDirectory);
        store = new InscriptionStoreXMLMap(storeDirectory, ctxAdmin);
      }

      ctxAdmin.getAttributes().put(ContextAttributes.APP_STORE, store);

      this.component.getDefaultHost().attach(getAttachUrlAdmin(), new InscriptionApplication(ctxAdmin));

      Context ctxUser = this.component.getContext().createChildContext();
      ctxUser.getAttributes().put(ContextAttributes.SETTINGS, settings);
      ctxUser.getAttributes().put(ContextAttributes.APP_STORE, store);

      // captcha filter is enabled by defaut (this or nothing is the same)
      ctxUser.getAttributes().put("Security.Captcha.enabled", true);

      CaptchaContainer captchaContainer = new CaptchaContainer();
      ctxUser.getAttributes().put("Security.Captcha.CaptchaContainer", captchaContainer);

      this.component.getDefaultHost().attach(getAttachUrlUser(), new UserInscriptionApplication(ctxUser));

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
    item.setPassword("password");
    item.setFirstName("test-userInscription.firstName");
    item.setLastName("test-userInscription.lastName");
    item.setEmail("test-userInscription.email");
    item.setComment("test-userInscription.comment");

    assertNone();

    userInscription(item);

    // WITH CAPTCHA
    // adminGetInscriptions();
    //
    // adminValidateInscriptionWithNullRepresentation(item);
    //
    // adminDelete(item, false); // inscription should not exists

    assertNone();

    getImage(MediaType.IMAGE_PNG);
    getImage(MediaType.IMAGE_JPEG);

  }

  /**
   * test getCaptcha image resource with different image formats
   * 
   * @param expectedMediaType
   *          MediaType
   */
  public void getImage(MediaType expectedMediaType) {
    // Get image captcha
    ClientResource cr = null;
    try {
      cr = new ClientResource(this.getBaseUrlUser() + "/captcha");
      Representation representation = cr.get(expectedMediaType);
      MediaType media = representation.getMediaType();
      assertEquals(expectedMediaType, media);

      Series<CookieSetting> cookies = cr.getResponse().getCookieSettings();
      CookieSetting id = cookies.getFirst("captcha");
      assertNotNull(id);
      assertEquals("/", id.getPath());
      assertNotNull(id.getValue());
    }
    catch (ResourceException e) {
      // TODO Auto-generated catch block
      assertEquals(Status.CLIENT_ERROR_FORBIDDEN.getCode(), cr.getStatus().getCode());
      assertFalse(cr.getStatus().isSuccess());
    }
  }

  /**
   * Invoke POST
   * 
   * @param item
   *          inscription
   */
  public void userInscription(Inscription item) {
    Representation rep = new JacksonRepresentation<Inscription>(item);
    ClientResource cr = null;
    try {
      cr = new ClientResource(this.getBaseUrlUser());
      Representation result = cr.post(rep, MediaType.APPLICATION_JSON);

      fail("POST without captcha should return 403 status");
      assertEquals(Status.CLIENT_ERROR_FORBIDDEN.getCode(), cr.getStatus().getCode());
      assertFalse(cr.getStatus().isSuccess());
    }
    catch (ResourceException e) {
      // TODO Auto-generated catch block
      assertEquals(Status.CLIENT_ERROR_FORBIDDEN.getCode(), cr.getStatus().getCode());
      assertFalse(cr.getStatus().isSuccess());
    }

    // pas la peine de regarder la reponse ?
    // assertNotNull(result);
    // Response response = getResponse(MediaType.APPLICATION_JSON, result, Inscription.class);
    // assertNotNull(response);
    // LOGGER.info(response.toString());
    // assertFalse(response.getSuccess());

    // TODO
    // completer par un cas de recuperation de l'image et du cookie
    // post de l'inscription avec le captcha.id ok et le captcha.key incorrect (on ne peut pas le connaitre)

    // assertTrue(response.getSuccess());
    // assertNotNull(response.getItem());
    // assertEquals(item.getFirstName(), item.getFirstName());
    // assertEquals(item.getLastName(), item.getLastName());
    // assertEquals(item.getComment(), item.getComment());
    // assertEquals(item.getEmail(), item.getEmail());
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
