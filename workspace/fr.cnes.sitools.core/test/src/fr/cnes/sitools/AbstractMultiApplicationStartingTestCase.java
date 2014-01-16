package fr.cnes.sitools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.GetResponseUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class TestMultiProjectCreation.
 */
public abstract class AbstractMultiApplicationStartingTestCase extends AbstractSitoolsServerTestCase {

  /** The Constant DATASET_URL_ATTACH. */
  private static final String DATASET_URL_ATTACH = "/mondataset";

  /** The Constant PROJECT_URL_ATTACH. */
  private static final String PROJECT_URL_ATTACH = "/proj/premier";

  /** The Constant APP_PLUGIN_URL_ATTACH. */
  private static final String APP_PLUGIN_URL_ATTACH = "/application_test_security";

  /** The Constant USER. */
  private static final String USER = "admin";

  /** The Constant PASSWORD. */
  private static final String PASSWORD = "admin";

  /** The Constant DATASET_ID. */
  private static final String DATASET_ID = "bf77955a-2cec-4fc3-b95d-7397025fb299";

  /** The Constant PROJECT_ID. */
  private static final String PROJECT_ID = "premier";

  /** The Constant APP_PLUGIN_ID. */
  private static final String APP_PLUGIN_ID = "6c170e5e-3e22-401f-9972-0c099285f245";

  /** The get applications. */
  private String getApplications = getBaseUrl() + "/admin/miscellaneous/applications";

  /**
   * Gets the data set url.
   * 
   * @return the data set url
   */
  private String getDataSetUrl() {
    return getBaseUrl() + settings.getString(Consts.APP_DATASETS_URL) + "/" + DATASET_ID;
  }

  /**
   * Gets the project url.
   * 
   * @return the project url
   */
  private String getProjectUrl() {
    return getBaseUrl() + settings.getString(Consts.APP_PROJECTS_URL) + "/" + PROJECT_ID;
  }

  /**
   * Gets the application plugin url.
   * 
   * @return the application plugin url
   */
  private String getApplicationPluginUrl() {
    return getBaseUrl() + settings.getString(Consts.APP_PLUGINS_APPLICATIONS_URL) + "/instances/" + APP_PLUGIN_ID;
  }

  /**
   * absolute url for role management REST API
   * 
   * @return url
   */
  protected String getApplicationsUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_APPLICATIONS_URL);
  }

  /**
   * Test dataset activation.
   * 
   * @throws Exception
   *           the exception
   */
  @Test
  public void testDatasetActivation() throws Exception {
    stopApplication(DATASET_ID, USER, PASSWORD);

    assertApplicationStarted(DATASET_URL_ATTACH, 0, USER, PASSWORD);

    ApplicationActivatorThread thread1 = new ApplicationActivatorThread(getDataSetUrl() + "/start", USER, PASSWORD,
        "dataset");
    ApplicationActivatorThread thread2 = new ApplicationActivatorThread(getDataSetUrl() + "/start", USER, PASSWORD,
        "dataset");

    thread1.start();
    thread2.start();

    Thread.sleep(10000);

    assertApplicationStarted(DATASET_URL_ATTACH, 1, USER, PASSWORD);
  }

  /**
   * Test project activation.
   * 
   * @throws Exception
   *           the exception
   */
  @Test
  public void testProjectActivation() throws Exception {

    stopApplication(PROJECT_ID, USER, PASSWORD);

    assertApplicationStarted(PROJECT_URL_ATTACH, 0, USER, PASSWORD);

    ApplicationActivatorThread thread1 = new ApplicationActivatorThread(getProjectUrl() + "/start", USER, PASSWORD,
        "project");
    ApplicationActivatorThread thread2 = new ApplicationActivatorThread(getProjectUrl() + "/start", USER, PASSWORD,
        "project");

    thread1.start();
    thread2.start();

    Thread.sleep(10000);

    assertApplicationStarted(PROJECT_URL_ATTACH, 1, USER, PASSWORD);
  }

  /**
   * Test application plugin activation.
   * 
   * @throws Exception
   *           the exception
   */
  @Test
  public void testApplicationPluginActivation() throws Exception {

    stopApplication(APP_PLUGIN_ID, USER, PASSWORD);

    assertApplicationStarted(APP_PLUGIN_URL_ATTACH, 0, USER, PASSWORD);

    ApplicationActivatorThread thread1 = new ApplicationActivatorThread(getApplicationPluginUrl() + "/start", USER,
        PASSWORD, "ApplicationPluginModel");
    ApplicationActivatorThread thread2 = new ApplicationActivatorThread(getApplicationPluginUrl() + "/start", USER,
        PASSWORD, "ApplicationPluginModel");

    thread1.start();
    thread2.start();

    Thread.sleep(10000);

    assertApplicationStarted(APP_PLUGIN_URL_ATTACH, 1, USER, PASSWORD);
  }

  /**
   * Assert application started.
   * 
   * @param appUrl
   *          the app url
   * @param nbAppStarted
   *          the nb app started
   * @param user
   *          the user
   * @param password
   *          the password
   */
  private void assertApplicationStarted(String appUrl, int nbAppStarted, String user, String password) {
    ClientResource client = new ClientResource(getApplications);
    ChallengeResponse resp = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, password);
    client.setChallengeResponse(resp);
    Representation out = client.get(getMediaTest());

    assertTrue(client.getStatus().isSuccess());
    assertNotNull(out);

    Response response = GetResponseUtils.getResponseResponseModel(getMediaTest(), out, Resource.class, true);
    assertNotNull(response);
    assertTrue(response.getSuccess());
    int nbApp = 0;
    ArrayList<Object> data = response.getData();
    for (Object object : data) {
      Resource app = (Resource) object;
      if (appUrl.equals(app.getUrl())) {
        nbApp++;
      }
    }
    assertEquals(nbAppStarted, nbApp);
  }

  /**
   * Stop application.
   * 
   * @param id
   *          the id
   * @param user
   *          the user
   * @param password
   *          the password
   * @throws IOException
   */
  private void stopApplication(String id, String user, String password) {
    ClientResource client = new ClientResource(getApplicationsUrl() + "/" + id + "/stop");
    ChallengeResponse resp = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, password);
    client.setChallengeResponse(resp);
    Representation out = client.put(new EmptyRepresentation(), getMediaTest());

    assertTrue(client.getStatus().isSuccess());
    assertNotNull(out);

    Response response = GetResponseUtils.getResponse(getMediaTest(), out, getMediaTest());
    assertTrue(response.getSuccess());
    assertEquals("APPLICATION_STOPPED", response.getMessage());
  }

  /**
   * The Class ApplicationActivatorThread.
   */
  private class ApplicationActivatorThread extends Thread {

    /** The url. */
    private String url;

    /** The user. */
    private String user;

    /** The password. */
    private String password;

    /** The field to omit during deserialization */
    private String fieldToOmit;

    /**
     * Instantiates a new application activator thread.
     * 
     * @param url
     *          the url
     * @param user
     *          the user
     * @param password
     *          the password
     */
    public ApplicationActivatorThread(String url, String user, String password, String fieldToOmit) {
      this.url = url;
      this.user = user;
      this.password = password;
      this.fieldToOmit = fieldToOmit;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#run()
     */
    public void run() {
      ClientResource client = new ClientResource(url);
      ChallengeResponse resp = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user, password);
      client.setChallengeResponse(resp);
      Representation out = client.put(new EmptyRepresentation(), getMediaTest());
      // Assert don't work in THREADS :(
      RIAPUtils.exhaust(out);

      // Response response = GetResponseUtils.getResponse(getMediaTest(), out, getMediaTest(), fieldToOmit);
      // assertTrue(response.getSuccess());
    }
  }

}
