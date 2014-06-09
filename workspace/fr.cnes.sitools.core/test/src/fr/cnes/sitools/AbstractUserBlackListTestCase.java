package fr.cnes.sitools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.applications.PublicApplication;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.security.captcha.Captcha;
import fr.cnes.sitools.security.captcha.CaptchaContainer;
import fr.cnes.sitools.security.filter.RequestCounter;
import fr.cnes.sitools.security.model.User;
import fr.cnes.sitools.security.userblacklist.UserBlackListModel;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.GetRepresentationUtils;
import fr.cnes.sitools.utils.GetResponseUtils;

/**
 * The Class AbstractUserBlackListTestCase.
 * 
 * @author m.gond
 */
public abstract class AbstractUserBlackListTestCase extends AbstractSitoolsServerTestCase {

  /** Name of the token param. */
  private static final String TOKEN_PARAM_NAME = "cdChallengeMail";

  /** The settings. */
  private static SitoolsSettings settings = SitoolsSettings.getInstance();

  /** NB_ALLOWED_REQ_BEFORE_BLACKLIST. */
  private static final int NB_ALLOWED_REQ_BEFORE_BLACKLIST = settings.getInt("Starter.NB_ALLOWED_REQ_BEFORE_BLACKLIST");

  /** userId. */
  private String userId = "admin_test";

  /** userId. */
  private String userPwd = "Vz0x2CbXj3";

  /** userId. */
  private String newUserPwd = "Vz0x2CbXj3456";

  /** userId. */
  private String userEmail = "m.gond@akka.eu";

  /** The public application. */
  private PublicApplication publicApplication = null;

  /**
   * absolute url for dataset management REST API.
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_USER_BLACKLIST_URL);
  }

  /**
   * absolute url for dataset management REST API.
   * 
   * @return url
   */
  protected String getBaseUserUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_SECURITY_URL);
  }

  /**
   * Absolute path location for project store files.
   * 
   * @return path
   */
  protected String getTestRepository() {
    return settings.getStoreDIR(Consts.APP_USER_BLACKLIST_STORE_DIR);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.AbstractSitoolsServerTestCase#setUp()
   */
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
    cleanDirectory(storeDirectory);cleanMapDirectories(storeDirectory);

    RequestCounter counter = (RequestCounter) settings.getStores().get(Consts.SECURITY_FILTER_USER_BLACKLIST_CONTAINER);
    counter.remove(userId);
  }

  /**
   * Test UserBlackListModel.
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

      checkRequestCounter(userId, 0);

      assertNone();
    }
    finally {
      deleteUser(user);
    }
  }

  /**
   * Check request counter.
   * 
   * @param userId2
   *          the user id2
   * @param i
   *          the i
   */
  private void checkRequestCounter(String userId2, int i) {
    RequestCounter counter = (RequestCounter) settings.getStores().get(Consts.SECURITY_FILTER_USER_BLACKLIST_CONTAINER);
    assertEquals(i, counter.getNumberOfRequests(userId2));
  }

  /**
   * Test UserBlackListModel.
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

      // call the unblacklist url => send an email to the user
      unblacklistUserFail(userId, userEmail + "___");

      unblacklistUserFail(userId + "___", userEmail);

      unblacklistUser(userId, userEmail);

      // simulate the unlock account with a token generated from the test
      String token = getToken(userId);

      Captcha captcha = getCaptcha();

      unlockAccountBadToken("badToken", captcha);

      unlockAccountBadCaptcha(token, captcha);

      captcha = getCaptcha();

      unlockAccount(token, newUserPwd, captcha);
      
      captcha = getCaptcha();
      //token is supposed to be invalid now
      unlockAccountBadToken(token, captcha);

      checkUserNotBlacklisted(userId, newUserPwd);

      checkRequestCounter(userId, 0);

      assertNone();

    }
    finally {
      deleteUser(user);
    }

  }

  /**
   * Gets the token.
   * 
   * @param userId
   *          the user id
   * @return the token
   */
  private String getToken(String userId) {
    if (this.publicApplication == null) {
      this.publicApplication = getPublicApplication();
    }
    return this.publicApplication.getChallengeToken().getToken(userId);
  }

  /**
   * Gets the public application.
   * 
   * @return the public application
   */
  private PublicApplication getPublicApplication() {
    PublicApplication appReturn = null;
    Map<String, SitoolsApplication> applications = settings.getAppRegistry().getApplications();
    for (Entry<String, SitoolsApplication> app : applications.entrySet()) {
      if (app.getValue() instanceof PublicApplication) {
        appReturn = (PublicApplication) app.getValue();
        break;
      }
    }
    return appReturn;
  }

  /**
   * Gets the captcha.
   * 
   * @return the captcha
   */
  private Captcha getCaptcha() {
    if (this.publicApplication == null) {
      this.publicApplication = getPublicApplication();
    }

    CaptchaContainer captchaContainer = (CaptchaContainer) this.publicApplication.getContext().getAttributes()
        .get("Security.Captcha.CaptchaContainer");
    return captchaContainer.post(100, 100, 10);
  }

  /**
   * Creates the user.
   * 
   * @param userId2
   *          the user id2
   * @param userEmail2
   *          the user email2
   * @param userPwd2
   *          the user pwd2
   * @return the user
   */
  private User createUser(String userId2, String userEmail2, String userPwd2) {
    return new User(userId2, userPwd2, "", "", userEmail2);
  }

  /**
   * Persist user.
   * 
   * @param user
   *          the user
   */
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

  /**
   * Delete user.
   * 
   * @param user
   *          the user
   */
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

  /**
   * Retrieve all.
   * 
   * @param user
   *          the user
   */
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

  /**
   * Delete.
   * 
   * @param user
   *          the user
   */
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
   * Check user blacklisted.
   * 
   * @param user
   *          the user
   * @param password
   *          the password
   */
  private void checkUserBlacklisted(String user, String password) {
    request(getBaseUrl(), Status.CLIENT_ERROR_FORBIDDEN, user, password);
  }

  /**
   * Check user not blacklisted.
   * 
   * @param user
   *          the user
   * @param password
   *          the password
   */
  private void checkUserNotBlacklisted(String user, String password) {
    request(getBaseUrl(), Status.SUCCESS_OK, user, password);
  }

  /**
   * Blacklist user.
   * 
   * @param user
   *          the user
   * @param password
   *          the password
   */
  private void blacklistUser(String user, String password) {
    for (int i = 0; i < NB_ALLOWED_REQ_BEFORE_BLACKLIST; i++) {
      request(getBaseUrl(), Status.CLIENT_ERROR_UNAUTHORIZED, user, password + "+++");
    }
  }

  /**
   * Unblacklist user.
   * 
   * @param user
   *          the user
   * @param email
   *          the email
   */
  private void unblacklistUser(String user, String email) {
    unblacklistUser(user, email, true);
  }

  /**
   * Unblacklist user fail.
   * 
   * @param user
   *          the user
   * @param email
   *          the email
   */
  private void unblacklistUserFail(String user, String email) {
    unblacklistUser(user, email, false);
  }

  /**
   * Unblacklist user.
   * 
   * @param user
   *          the user
   * @param email
   *          the email
   * @param status
   *          the status
   */
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
   * Unlock account.
   * 
   * @param token
   *          the token
   * @param pwd
   *          the pwd
   * @param captcha
   *          the captcha
   */
  private void unlockAccount(String token, String pwd, Captcha captcha) {
    unlockAccount(token, pwd, Status.SUCCESS_OK, captcha);
  }

  /**
   * Unlock account bad token.
   * 
   * @param token
   *          the token
   * @param captcha
   *          the captcha
   */
  private void unlockAccountBadToken(String token, Captcha captcha) {
    unlockAccount(token, null, Status.CLIENT_ERROR_GONE, captcha);
  }

  /**
   * Unlock account bad captcha.
   * 
   * @param token
   *          the token
   * @param captcha
   *          the captcha
   */
  private void unlockAccountBadCaptcha(String token, Captcha captcha) {
    unlockAccount(token, null, Status.CLIENT_ERROR_FORBIDDEN, captcha);
  }

  /**
   * Unlock account.
   * 
   * @param token
   *          the token
   * @param pwd
   *          the pwd
   * @param status
   *          the status
   * @param captcha
   *          the captcha
   */
  private void unlockAccount(String token, String pwd, Status status, Captcha captcha) {
    Reference ref = new Reference(super.getBaseUrl() + "/unlockAccount");
    ref.addQueryParameter(TOKEN_PARAM_NAME, token);
    if (captcha != null) {
      ref.addQueryParameter("captcha.id", captcha.getId());
      ref.addQueryParameter("captcha.key", captcha.getAnswer());
    }

    final Client client = new Client(Protocol.HTTP);
    Request request = new Request(Method.PUT, ref);
    org.restlet.Response response = null;
    try {
      ArrayList<Preference<MediaType>> mediaTest = new ArrayList<Preference<MediaType>>();
      mediaTest.add(new Preference<MediaType>(getMediaTest()));
      request.getClientInfo().setAcceptedMediaTypes(mediaTest);

      User myUser = new User("", newUserPwd, "", "", "");
      request.setEntity(GetRepresentationUtils.getRepresentationUser(myUser, getMediaTest()));

      response = client.handle(request);

      assertNotNull(response);
      assertEquals(status, response.getStatus());
      assertEquals(status.isError(), response.getStatus().isError());
      assertEquals(status.isSuccess(), response.getStatus().isSuccess());

      if (status.isSuccess()) {
        Response resp = GetResponseUtils.getResponseUserOrGroup(getMediaTest(), response.getEntity(), User.class);

        assertNotNull(resp);
        assertEquals(resp.getMessage(), true, resp.getSuccess());
      }

    }
    finally {
      if (response != null) {
        RIAPUtils.exhaust(response);
      }
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
