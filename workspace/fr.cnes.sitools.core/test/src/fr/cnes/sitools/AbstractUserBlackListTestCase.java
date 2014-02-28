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
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.security.userblacklist.UserBlackListModel;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.GetResponseUtils;

public abstract class AbstractUserBlackListTestCase extends AbstractSitoolsServerTestCase {
  /** The settings */
  private static SitoolsSettings settings = SitoolsSettings.getInstance();

  /** NB_ALLOWED_REQ_BEFORE_BLACKLIST */
  private static final int NB_ALLOWED_REQ_BEFORE_BLACKLIST = settings.getInt("Starter.NB_ALLOWED_REQ_BEFORE_BLACKLIST");

  /**
   * static xml store instance for the test
   */
  private static SitoolsStore<UserBlackListModel> store = null;

  /** userId */
  private String userId = "admin";

  /** userId */
  private String userPwd = "admin";

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_USER_BLACKLIST_URL);
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
    if (store == null) {
      File storeDirectory = new File(getTestRepository());
      cleanDirectory(storeDirectory);
    }
  }

  /**
   * Test UserBlackListModel
   */
  @Test
  public void test() {

    docAPI.setActive(false);
    assertNone();

    blacklistUser(userId, userPwd);

    checkUserBlacklisted(userId, userPwd);

    retrieveAll(userId);

    delete(userId);

    checkUserNotBlacklisted(userId, userPwd);

    assertNone();

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

  private void checkUserNotBlacklisted(String user, String password) {
    request(getBaseUrl(), Status.SUCCESS_OK, user, password);
  }

  private void checkUserBlacklisted(String user, String password) {
    request(getBaseUrl(), Status.CLIENT_ERROR_FORBIDDEN, user, password);
  }

  private void blacklistUser(String user, String password) {
    for (int i = 0; i < NB_ALLOWED_REQ_BEFORE_BLACKLIST; i++) {
      request(getBaseUrl(), Status.CLIENT_ERROR_UNAUTHORIZED, user, password + "+++");
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
