package fr.cnes.sitools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.security.userblacklist.UserBlackListModel;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.GetRepresentationUtils;
import fr.cnes.sitools.utils.GetResponseUtils;

public abstract class AbstractUserBlackListTestCase extends AbstractSitoolsServerTestCase {
  /** The settings */
  private static SitoolsSettings settings = SitoolsSettings.getInstance();

  /** NB_ALLOWED_REQ_BEFORE_BLACKLIST */
  private static final int NB_ALLOWED_REQ_BEFORE_BLACKLIST = settings.getInt("Starter.NB_ALLOWED_REQ_BEFORE_BLACKLIST");

  /** userId */
  private String userId = "admin_test";

  /** userId */
  private String userPwd = "Vz0x2CbXj3";

  /** userId */
  private String userEmail = "m.gond@akka.eu";

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_USER_BLACKLIST_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUserUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_SECURITY_URL);
  }

  /**
   * Absolute path location for project store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return settings.getStoreDIR(Consts.APP_USER_BLACKLIST_STORE_DIR);
  }

  @Before
  @Override
  /**
   * Init and Start a server with GraphApplication
   * 
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    super.setUp();
    File storeDirectory = new File(getTestRepository());
    cleanDirectory(storeDirectory);
  }

  /**
   * Test UserBlackListModel
   */
  @Test
  public void test() {
    User user = createUser(userId, userEmail, userPwd);
    try {

      persistUser(user);
      docAPI.setActive(false);
      assertNone();

      blacklistUser(userId, userPwd);

      checkUserBlacklisted(userId, userPwd);

      retrieveAll(userId);

      delete(userId);
      // check that the user is not blacklisted but with bad password because a new password has been generated for the
      // user when removing it from blacklist
      checkUserNotBlacklistedBadPassword(userId, userPwd);

      assertNone();
    }
    finally {
      deleteUser(user);
    }
  }

  /**
   * Test UserBlackListModel
   */
  @Test
  public void testUnBlacklist() {

    docAPI.setActive(false);
    User user = createUser(userId, userEmail, userPwd);
    try {

      persistUser(user);

      assertNone();

      blacklistUser(userId, userPwd);

      checkUserBlacklisted(userId, userPwd);

      retrieveAll(userId);

      unblacklistUserFail(userId, userEmail + "___");

      unblacklistUserFail(userId + "___", userEmail);

      unblacklistUser(userId, userEmail);

      assertNone();

    }
    finally {
      deleteUser(user);
    }

  }

  private User createUser(String userId2, String userEmail2, String userPwd2) {
    return new User(userId2, userPwd2, "", "", userEmail2);
  }

  private void persistUser(User user) {
    String url = getBaseUserUrl() + "/users";
    ClientResource cr = new ClientResource(url);

    Representation result = cr.post(GetRepresentationUtils.getRepresentationUser(user, getMediaTest()));
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = GetResponseUtils.getResponseUserOrGroup(getMediaTest(), result, User.class);
    assertNotNull(response);
    assertTrue(response.getMessage(), response.getSuccess());
    RIAPUtils.exhaust(result);
  }

  private void deleteUser(User user) {
    String url = getBaseUserUrl() + "/users/" + user.getIdentifier();
    ClientResource cr = new ClientResource(url);
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      // try {
      // System.out.println(result.getText());
      // }
      // catch (IOException e) {
      // // TODO Auto-generated catch block
      // e.printStackTrace();
      // }

      Response response = GetResponseUtils.getResponse(getMediaTest(), result, getMediaTest());
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Invokes GET and asserts result response is an empty array.
   */
  public void assertNone() {
    String url = getBaseUrl();
    ClientResource cr = new ClientResource(url);
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = GetResponseUtils.getResponseUserBlacklist(getMediaTest(), result, UserBlackListModel.class,
          true);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(0), response.getTotal());
      RIAPUtils.exhaust(result);
    }

  }

  private void retrieveAll(String user) {
    String url = getBaseUrl();
    ClientResource cr = new ClientResource(url);
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = GetResponseUtils.getResponseUserBlacklist(getMediaTest(), result, UserBlackListModel.class,
          true);

      assertTrue(response.getSuccess());
      assertEquals(new Integer(1), response.getTotal());
      assertNotNull(response.getData());
      assertEquals(1, response.getData().size());

      ArrayList<Object> userBlacklistList = response.getData();
      UserBlackListModel model = (UserBlackListModel) userBlacklistList.get(0);
      assertEquals(user, model.getUsername());
      assertTrue(model.getUserExists());
      RIAPUtils.exhaust(result);
    }

  }

  private void delete(String user) {
    String url = getBaseUrl() + "/" + user;
    ClientResource cr = new ClientResource(url);
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = GetResponseUtils.getResponseUserBlacklist(getMediaTest(), result, UserBlackListModel.class);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Check that a user is not blacklisted with a request with a bad password
   * 
   * @param user
   *          the user
   * @param password
   *          the password
   */
  private void checkUserNotBlacklistedBadPassword(String user, String password) {
    request(getBaseUrl(), Status.CLIENT_ERROR_UNAUTHORIZED, user, password);
  }

  private void checkUserBlacklisted(String user, String password) {
    request(getBaseUrl(), Status.CLIENT_ERROR_FORBIDDEN, user, password);
  }

  private void blacklistUser(String user, String password) {
    for (int i = 0; i < NB_ALLOWED_REQ_BEFORE_BLACKLIST; i++) {
      System.out.println("REQ : " + i);
      request(getBaseUrl(), Status.CLIENT_ERROR_UNAUTHORIZED, user, password + "+++");
    }
  }

  private void unblacklistUser(String user, String email) {
    unblacklistUser(user, email, true);
  }

  private void unblacklistUserFail(String user, String email) {
    unblacklistUser(user, email, false);
  }

  private void unblacklistUser(String user, String email, boolean status) {
    String url = super.getBaseUrl() + "/unblacklist";
    User myUser = new User(user, "", "", "", email);
    ClientResource cr = new ClientResource(url);
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      Representation result = cr.put(GetRepresentationUtils.getRepresentationUser(myUser, getMediaTest()),
          getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = GetResponseUtils.getResponseUserOrGroup(getMediaTest(), result, User.class);

      assertNotNull(response);
      assertEquals(response.getMessage(), status, response.getSuccess());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Request the provided url with the user and password. Expect the following status
   * 
   * @param url
   *          the url
   * @param status
   *          the status
   * @param user
   *          the user
   * @param password
   *          the password
   */
  private void request(String url, Status status, String user, String password) {
    final Client client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, url);
    org.restlet.Response response = null;
    try {
      ChallengeResponse cr = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, password);
      request.setChallengeResponse(cr);
      response = client.handle(request);

      assertNotNull(response);
      assertEquals(status, response.getStatus());
      assertEquals(status.isError(), response.getStatus().isError());
      assertEquals(status.isSuccess(), response.getStatus().isSuccess());

    }
    finally {
      if (response != null) {
        RIAPUtils.exhaust(response);
      }
    }
  }

}
