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
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsMediaType;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.userstorage.UserStorageApplication;
import fr.cnes.sitools.userstorage.UserStorageManagement;
import fr.cnes.sitools.userstorage.UserStorageStoreXML;
import fr.cnes.sitools.userstorage.model.DiskStorage;
import fr.cnes.sitools.userstorage.model.UserStorage;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * UserStorage management and UserStorage access test case.
 * 
 * @author AKKA
 */
public abstract class AbstractUserStorageManagerTestCase extends AbstractSitoolsTestCase {

  /** The title */
  protected static String title = "";

  /** static xml store instance for the test */
  private static UserStorageStoreXML store = null;

  /** base url for UserStorage access. */
  private String userReference;

  /** Restlet Component for server */
  private Component component = null;

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_USERSTORAGE_URL);

  }

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_USERSTORAGE_URL);
  }

  /**
   * Absolute path location for data set store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_USERSTORAGE_STORE_DIR);
  }

  /**
   * Starts server with the 2 applications : UserStorageManagement and UserStorageApplication
   * 
   * @throws Exception
   *           if failed.
   */
  @Before
  public void setUp() throws Exception {

    SitoolsSettings settings = SitoolsSettings.getInstance();

    if (this.component == null) {
      this.component = createTestComponent(settings);

      // Context
      Context ctx = this.component.getContext().createChildContext();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, settings);

      if (store == null) {
        File storeDirectory = new File(getTestRepository());
        cleanDirectory(storeDirectory);
        store = new UserStorageStoreXML(storeDirectory, ctx);
      }

      ctx.getAttributes().put(ContextAttributes.APP_STORE, store);
      ctx.getAttributes().put("USER_STORAGE_ROOT",
          TEST_FILES_REPOSITORY + settings.getString("Starter.USERSTORAGE_ROOT"));

      this.component.getDefaultHost().attach(getAttachUrl(), new UserStorageManagement(ctx));

      // ===========================================================================
      // Exposition des espaces de stockage utilisateurs >>
      // UserStorageApplication

      // Reference
      userReference = super.getBaseUrl() + settings.getString(Consts.APP_USERSTORAGE_USER_URL);

      // Context
      Context appContext = this.component.getContext().createChildContext();
      appContext.getAttributes().put(ContextAttributes.SETTINGS, settings);

      appContext.getAttributes().put(ContextAttributes.APP_STORE, store);
      appContext.getAttributes().put("USER_STORAGE_ROOT",
          TEST_FILES_REPOSITORY + settings.getString("Starter.USERSTORAGE_ROOT"));

      // Application
      UserStorageApplication userStorageApplication = new UserStorageApplication(appContext);

      // Attachment
      // host.attach(appReference, userStorageApplication);
      this.component.getDefaultHost().attach(SITOOLS_URL + settings.getString(Consts.APP_USERSTORAGE_USER_URL),
          userStorageApplication);

    }

    if (!this.component.isStarted()) {
      this.component.start();
    }

  }

  /**
   * Stop server.
   * 
   * @throws Exception
   *           if failed.
   */
  @After
  public void tearDown() throws Exception {
    super.tearDown();
    this.component.stop();
    this.component = null;
  }

  /**
   * <code>
   * Admin gets an existing UserStorage definition.
   * Admin creates a new UserStorage definition.
   * User gets files from its userstorage.
   * </code>
   */
  @Test
  public void adminUserStorageTest() {
    docAPI.setActive(false);

    assertNoneUserStorage();

    createUserStorage("show");

    stopUserStorage("show");

    startUserStorage("show");

    ChallengeResponse challenge = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "show", "time");

    // User Storage getFiles();
    getFiles("show", challenge);

    postJSONFile("show", challenge);

    postXMLFile("show", challenge);

    getUserStorageStatusForUser("show", challenge);

    deleteFolder("show", challenge, "/dataSelection");

    cleanUserStorage("show");

    deleteUserStorage("show");

    createWadl(getBaseUrl(), "admin_storage");

  }

  /**
   * Scenario for API documentation <code>
   * Admin gets an existing UserStorage definition.
   * Admin creates a new UserStorage definition.
   * User gets files from its userstorage.
   * </code>
   */
  @Test
  public void adminUserStorage2docAPITest() {
    docAPI.setActive(true);
    docAPI.appendChapter(title);
    docAPI.appendSubChapter("Retrieving the user storage", "retriving");
    assertNoneUserStorage();
    docAPI.appendSubChapter("Creating a user storage", "creating");
    createUserStorage("show");
    docAPI.appendSubChapter("Stop a user storage", "stop");
    stopUserStorage("show");
    docAPI.appendSubChapter("Start a user storage", "start");
    startUserStorage("show");
    docAPI.appendSubChapter("Clean a user storage", "clean");
    cleanUserStorage("show");
    docAPI.appendSubChapter("Delete a user storage", "delete");
    deleteUserStorage("show");
    docAPI.close();

  }

  /**
   * POST a new userStorage definition.
   */
  private void createUserStorage(String userId) {
    UserStorage us = new UserStorage();
    us.setUserId(userId);
    us.setStatus(null);
    DiskStorage uds = new DiskStorage();
    uds.setUserStoragePath(TEST_FILES_REPOSITORY + "/storage/" + userId);
    uds.setQuota((long) 1);
    us.setStorage(uds);

    Representation rep = getRepresentation(us, getMediaTest());

    if (docAPI.isActive()) {
      postDocAPI(getBaseUrl() + "/users", "", rep, new HashMap<String, String>(), getBaseUrl() + "/users");
    }
    else {
      ClientResource cr = new ClientResource(getBaseUrl() + "/users");
      Representation result = cr.post(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      // Parse response
      Response response = getResponse(getMediaTest(), result, UserStorage.class);
      assertNotNull(response);
      assertTrue(response.getSuccess());

      UserStorage usOut = (UserStorage) response.getItem();
      assertEquals(userId, usOut.getUserId());
      RIAPUtils.exhaust(result);

    }
  }

  /**
   * GET files from UserStorage directory The user gets files from its storage.
   * 
   * @param userId
   *          User identifier
   * @param challenge
   *          ChallengeResponse to pass with the request
   */
  private void getFiles(String userId, ChallengeResponse challenge) {
    ClientResource cr = new ClientResource(userReference.replace("{identifier}", userId) + "/files");
    cr.setChallengeResponse(challenge);

    // Representation HTML par défaut
    Representation result = null;
    try {
      result = cr.get(MediaType.TEXT_HTML);
      assertEquals(Status.SUCCESS_OK.getCode(), cr.getStatus().getCode());
      assertNotNull(result);
      assertEquals(MediaType.TEXT_HTML, result.getMediaType());
    }
    catch (ResourceException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }

    // Representation TEXT_URI_LIST
    try {
      result = cr.get(MediaType.TEXT_URI_LIST);
      assertEquals(Status.SUCCESS_OK.getCode(), cr.getStatus().getCode());
      assertNotNull(result);
      assertEquals(MediaType.TEXT_URI_LIST, result.getMediaType());
    }
    catch (ResourceException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }

    try {
      result = cr.get(MediaType.APPLICATION_JSON);
      assertEquals(Status.SUCCESS_OK.getCode(), cr.getStatus().getCode());
      assertNotNull(result);
      assertEquals(SitoolsMediaType.APPLICATION_SITOOLS_JSON_DIRECTORY, result.getMediaType());
    }
    catch (ResourceException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }

    RIAPUtils.exhaust(result);

  }

  /**
   * Invokes GET and asserts result response is an empty array.
   */
  public void assertNoneUserStorage() {
    ClientResource cr = new ClientResource(getBaseUrl() + "/users");
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    docAPI.appendResponse(result);
    if (!docAPI.isActive()) {

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, UserStorage.class, true);
      assertTrue(response.getSuccess());
      assertEquals(response.getTotal().intValue(), 0);
    }

    RIAPUtils.exhaust(result);

  }

  /**
   * Post some JSON to the userstorage to create a folder and a file
   * 
   * @param userId
   *          the user identifier
   * @param challenge
   *          ChallengeResponse to pass with the request
   */
  private void postJSONFile(String userId, ChallengeResponse challenge) {
    // TODO Auto-generated method stub
    String url = userReference.replace("{identifier}", userId)
        + "/files?filepath=%2FdataSelection%2Frecords&filename=file.json";
    String json = "{'orderRecord':{'records':[]}}";
    JsonRepresentation repr = new JsonRepresentation(json);

    ClientResource cr = new ClientResource(url);
    cr.setChallengeResponse(challenge);

    Representation result = null;

    result = cr.post(repr, getMediaTest());

    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    try {
      // Always returns some JSON so we can deserialise it with a Simple
      // JSONObject
      String txt = result.getText();
      assertNotNull(txt);
      JSONObject jsonResponse = new JSONObject(txt);

      boolean success = jsonResponse.getBoolean("success");
      assertNotNull(success);
      assertTrue(success);

    }
    catch (JSONException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    catch (IOException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }

  }

  /**
   * Post some XML to the userstorage to create a folder and a file
   * 
   * @param userId
   *          the user identifier
   * @param challenge
   *          ChallengeResponse to pass with the request
   */
  private void postXMLFile(String userId, ChallengeResponse challenge) {
    // TODO Auto-generated method stub
    String url = userReference.replace("{identifier}", userId)
        + "/files?filepath=%2FdataSelection%2Frecords&filename=file.xml";
    String xml = "<xml><orderRecord><records></records></orderRecord></xml>";

    StringRepresentation repr = new StringRepresentation(xml, MediaType.TEXT_XML);

    ClientResource cr = new ClientResource(url);
    cr.setChallengeResponse(challenge);

    Representation result = null;

    result = cr.post(repr, getMediaTest());

    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    try {
      // Always returns some JSON so we can deserialise it with a Simple
      // JSONObject
      String txt = result.getText();
      assertNotNull(txt);
      JSONObject jsonResponse = new JSONObject(txt);

      boolean success = jsonResponse.getBoolean("success");
      assertNotNull(success);
      assertTrue(success);

    }
    catch (JSONException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    catch (IOException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }

  }

  /**
   * Gets the status of the userstorage
   * 
   * @param userId
   *          the identifier
   * @param challenge
   *          ChallengeResponse to pass with the request
   */
  private void getUserStorageStatusForUser(String userId, ChallengeResponse challenge) {
    ClientResource cr = new ClientResource(userReference.replace("{identifier}", userId) + "/status");
    cr.setChallengeResponse(challenge);

    Representation result = cr.get(getMediaTest());
    docAPI.appendResponse(result);

    if (!docAPI.isActive()) {

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, UserStorage.class);
      assertTrue(response.getSuccess());

      assertNotNull(response.getItem());
      UserStorage userstorage = (UserStorage) response.getItem();
      assertEquals(userId, userstorage.getUserId());
      assertNotNull(userstorage.getStorage());
      assertNull(userstorage.getStorage().getUserStoragePath());
    }

    RIAPUtils.exhaust(result);

  }

  /**
   * Delete a folder in the user storage
   * 
   * @param userId
   *          the userId
   * @param challenge
   *          the challenge to pass with the request
   * @param folderUrl
   *          the url of the folder to delete
   */
  private void deleteFolder(String userId, ChallengeResponse challenge, String folderUrl) {
    // first test without recursion with a folder containing a file
    // result expected is an error with CLIENT_ERROR_FORBIDEN status ( 403 )
    String url = userReference.replace("{identifier}", userId) + "/files" + folderUrl;

    final Client client = new Client(Protocol.HTTP);
    Request request = new Request(Method.DELETE, url);
    request.setChallengeResponse(challenge);
    org.restlet.Response response = client.handle(request);

    assertEquals(Status.CLIENT_ERROR_FORBIDDEN, response.getStatus());

    // Then test with recursive=true
    // result expected is a success with status SUCCESS_NO_CONTENT ( 204 )
    url += "?recursive=true";
    final Client client2 = new Client(Protocol.HTTP);
    Request request2 = new Request(Method.DELETE, url);
    request.setChallengeResponse(challenge);
    org.restlet.Response response2 = client2.handle(request2);

    assertEquals(Status.SUCCESS_NO_CONTENT, response2.getStatus());
  }

  /**
   * Call the clean action on a userStorage
   * 
   * @param userId
   *          the User identifier
   */
  private void cleanUserStorage(String userId) {
    actOnUserStorage("/clean", userId);
  }

  /**
   * Call the stop action on a UserStorage
   * 
   * @param userId
   *          the User identifier
   */
  private void stopUserStorage(String userId) {
    actOnUserStorage("/stop", userId);
  }

  /**
   * Call the start action on a UserStorage
   * 
   * @param userId
   *          the User identifier
   */
  private void startUserStorage(String userId) {
    actOnUserStorage("/start", userId);
  }

  /**
   * Call the given action on a UserStorage and assert that the response is successful
   * 
   * @param action
   *          the action to call
   * @param userId
   *          the User identifier
   */
  private void actOnUserStorage(String action, String userId) {
    Representation rep = new StringRepresentation("");
    String url = getBaseUrl() + "/users/" + userId + action;
    if (docAPI.isActive()) {
      putDocAPI(url, "", rep, new HashMap<String, String>(), url.replace(userId, "{identifier}"));
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(rep, getMediaTest());
      assertNotNull(result);
      // Parse response
      Response response = getResponse(getMediaTest(), result, UserStorage.class);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }

  }

  private void deleteUserStorage(String userId) {
    String url = getBaseUrl() + "/users/" + userId;
    if (docAPI.isActive()) {
      deleteDocAPI(url, "", new HashMap<String, String>(), url.replace("show", "{identifier}"));
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      // Parse response
      Response response = getResponse(getMediaTest(), result, UserStorage.class);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }

  }

  // ------------------------------------------------------------
  // RESPONSE REPRESENTATION WRAPPING

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
      if (!media.isCompatible(getMediaTest()) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.alias("response", Response.class);
      xstream.alias("userstorage", UserStorage.class);
      xstream.alias("storage", DiskStorage.class);

      if (isArray) {
        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
        if (dataClass == UserStorage.class) {
          xstream.aliasField("userstorage", Response.class, "item");
        }
      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(getMediaTest())) {
        Response response = rep.getObject("response");

        return response;
      }
      else {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null; // TODO complete test with ObjectRepresentation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

  /**
   * Builds XML or JSON Representation of Project for Create and Update methods.
   * 
   * @param item
   *          Project
   * @param media
   *          APPLICATION_XML or APPLICATION_JSON
   * @return XML or JSON Representation
   */
  public static Representation getRepresentation(UserStorage item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<UserStorage>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<UserStorage> rep = new XstreamRepresentation<UserStorage>(media, item);
      configure(xstream);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null; // TODO complete test with ObjectRepresentation
    }
  }

  /**
   * Configures XStream mapping of Response object with ConverterModelDTO content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);

    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");
  }

}
