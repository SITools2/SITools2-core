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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.*;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.engine.Engine;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.api.DocAPI;
import fr.cnes.sitools.api.DocWadl;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.server.Starter;
import fr.cnes.sitools.util.FileCopyUtils;
import fr.cnes.sitools.util.FileUtils;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Classe de base pour les tests unitaires des Serveurs/Applications Restlet et de la persistance des données
 * 
 * On suppose qu'un serveur est lancé au démarrage de la suite de tests avec une configuration donnée.
 * 
 * @author AKKA Technologies
 * @see org.restlet.test.RestletTestCase
 */
@Ignore
public abstract class AbstractSitoolsServerTestCase {

  /**
   * Class logger
   */
  public static final Logger LOGGER = Engine.getLogger(AbstractSitoolsServerTestCase.class.getName());

  /**
   * BASE URL of global Sitools application
   */
  public static final String SITOOLS_URL = SitoolsSettings.getInstance().getString(Consts.APP_URL);

  /**
   * Root path for storing files
   */
  public static final String TEST_FILES_REPOSITORY = SitoolsSettings.getInstance().getString("Tests.STORE_DIR");

  /**
   * Root path for storing files
   */
  public static final String TEST_FILES_REFERENCE_REPOSITORY = SitoolsSettings.getInstance().getString(
      "Tests.REFERENCE_STORE_DIR");

  /**
   * Default test port for all tests
   */
  public static final int DEFAULT_TEST_PORT = 1340;

  /**
   * Helper for generating API documentation
   */
  protected static DocAPI docAPI;

  /**
   * Settings for the test
   */
  protected static SitoolsSettings settings = null;

  /**
   * MEDIA type to be set in concrete subclasses of test case
   */
  protected static MediaType mediaTest = MediaType.APPLICATION_XML;

  /**
   * system property name for test port
   */
  protected static final String PROPERTY_TEST_PORT = "sitools.test.port";

  /**
   * server port for the each test instance.
   */
  protected final int portTest = getTestPort();

  /**
   * To launch sitools server with secure applications
   */
  protected static boolean secure = true;

  /**
   * Port for test defined in this order : 1. System property sitools.test.port 2. default test port (1340)
   * 
   * @return test port
   */
  protected static int getTestPort() {
    if (System.getProperties().containsKey(PROPERTY_TEST_PORT)) {
      return Integer.parseInt(System.getProperty(PROPERTY_TEST_PORT));
    }
    return DEFAULT_TEST_PORT;
  }

  public static boolean isSecure() {
    return secure;
  }

  public static void setSecure(boolean secureParam) {
    secure = secureParam;
  }

  /**
   * Try to remove files from directory
   * 
   * @param dir
   *          directory to be cleaned
   * @param recursive
   *          true to go through all children of dir, false otherwise
   */
  public static void cleanDirectory(File dir, boolean recursive) {
    if (dir == null) {
      LOGGER.warning("Null directory");
      return;
    }

    LOGGER.info("Clean XML files in directory " + dir.getAbsolutePath());
    try {
      FileUtils.cleanDirectory(dir, new String[] {"xml"}, recursive);
    }
    catch (IOException e) {
      Engine.getLogger(AbstractSitoolsServerTestCase.class.getName()).warning(
          "Unable to clean " + dir.getPath() + "\n cause:" + e.getMessage());
    }
  }

  /**
   * Try to remove files from directory
   * 
   * @param dir
   *          directory to be cleaned
   */
  public static void cleanDirectory(File dir) {
    cleanDirectory(dir, false);
  }

  /**
   * Supprime tous les fichiers du repertoire.
   * 
   * @param dir
   *          File directory to clean up
   * @param recursive
   *          true to go through all children of dir, false otherwise
   * 
   * 
   */
  public static void cleanDirectoryAll(File dir, boolean recursive) {
    if (dir == null) {
      LOGGER.warning("Null directory");
      return;
    }

    LOGGER.info("Clean directory " + dir.getAbsolutePath());
    try {
      FileUtils.cleanDirectory(dir, new String[] {}, recursive);
    }
    catch (IOException e) {
      Engine.getLogger(AbstractSitoolsServerTestCase.class.getName()).warning(
          "Unable to clean " + dir.getPath() + "\n cause:" + e.getMessage());
    }
  }

  /**
   * Supprime tous les fichiers du repertoire.
   * 
   * @param dir
   *          File directory to clean up
   */
  public static void cleanDirectoryAll(File dir) {
    cleanDirectoryAll(dir, false);
  }

  /**
   * Try to remove files from all the map directories under rootDir
   * 
   * @param rootDir
   *          directory to be cleaned
   */
  public static void cleanMapDirectories(File rootDir) {
    if (rootDir == null) {
      LOGGER.warning("Null directory");
      return;
    }

    try {
      if (rootDir.getName().equals("map")) {
        FileUtils.cleanDirectory(rootDir, new String[] {"xml"}, false);
      }

      LOGGER.info("Clean XML files in maps directory " + rootDir.getAbsolutePath());
      File[] children = rootDir.listFiles();
      for (File file : children) {
        if (file.isDirectory()) {
          cleanMapDirectories(file);
        }
      }
    }
    catch (IOException e) {
      Engine.getLogger(AbstractSitoolsServerTestCase.class.getName()).warning(
          "Unable to clean " + rootDir.getPath() + "\n cause:" + e.getMessage());
    }
  }

  /**
   * Try to remove files from all the map directories under rootDirPath
   * 
   * @param rootDirPath
   *          the path of the directory to be cleaned
   */
  public static void cleanMapDirectories(String rootDirPath) {
    cleanDirectory(new File(rootDirPath));
  }

  /**
   * Copie les fichiers du repertoire source vers le repertoire cible
   * 
   * @param source
   *          String directory path
   * @param cible
   *          String directory path
   */
  public static void setUpDataDirectory(String source, String cible) {
    cleanDirectory(new File(cible), true);
    LOGGER.info("Copy files from:" + source + " cible:" + cible);
    File cibleFile = new File(cible);
    if (!cibleFile.exists()) {
      cibleFile.mkdirs();
    }
    FileCopyUtils.copyAFolderExclude(source, cible, ".svn");
  }

  /**
   * Absolute path location for data files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return TEST_FILES_REPOSITORY;
  }

  /**
   * absolute url for sitools REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return "http://localhost:" + getTestPort() + SITOOLS_URL;
  }

  /**
   * absolute url for sitools REST API
   * 
   * @return url
   */
  protected static String getHostUrl() {
    return "http://localhost:" + getTestPort();
  }

  /**
   * Executed before each test method.
   * 
   * @throws Exception
   *           if failed
   */
  @Before
  public void setUp() throws Exception {
    //
  }

  /**
   * Executed after each test method.
   * 
   * @throws Exception
   *           if failed
   */
  @After
  public void tearDown() throws Exception {
    //
  }

  /**
   * Executed once before all test methods
   */
  @BeforeClass
  public static void before() {
    setup();
    start();
  }

  /** Setup tests variables before starting server */
  protected static void setup() {
    Engine.clearThreadLocalVariables();

    settings = SitoolsSettings.getInstance("sitools", Starter.class.getClassLoader(), Locale.FRANCE, true);

    String source = settings.getRootDirectory() + TEST_FILES_REFERENCE_REPOSITORY;
    String cible = settings.getRootDirectory() + TEST_FILES_REPOSITORY;

    LOGGER.info("COPY SOURCE:" + source + " CIBLE:" + cible);

    setUpDataDirectory(source, cible);
    settings.setStoreDIR(TEST_FILES_REPOSITORY);
    settings.setTmpFolderUrl(settings.getStoreDIR(Consts.APP_TMP_FOLDER_DIR));

    if (!isSecure()) {
      settings.setSecured(false);
    }
  }

  /** Starts server */
  protected static void start() {
    try {
      // String[] args = new String[0];
      // ProxySettings.init(args, settings);
      Starter.start("localhost", getTestPort(), "http://localhost:" + getTestPort());
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Executed once after all test methods
   */
  @AfterClass
  public static void afterClass() {
    Starter.stop();
    Engine.clearThreadLocalVariables();

    // Close test-case documentation file
    if (docAPI != null) {
      docAPI.close();
      docAPI = null;
    }
  }

  /**
   * Trace global parameters for the test.
   */
  @Test
  public void testConfig() {
    LOGGER.info(this.getClass().getName() + " TEST BASE URL = " + getBaseUrl());
    LOGGER.info(this.getClass().getName() + " TEST REPOSITORY = " + getTestRepository());
    LOGGER.info(this.getClass().getName() + " TEST PORT = " + getTestPort());

    // asert assertTrue("Check data directory presence", (new
    // File(getTestRepository()).exists()));
  }

  /**
   * Gets the mediaTest value
   * 
   * @return the mediaTest
   */
  public static MediaType getMediaTest() {
    return mediaTest;
  }

  /**
   * Sets the value of mediaTest
   * 
   * @param mediaTest
   *          the mediaTest to set
   */
  public static void setMediaTest(MediaType mediaTest) {
    AbstractSitoolsServerTestCase.mediaTest = mediaTest;
  }

  /**
   * Gets the docAPI value
   * 
   * @return the docAPI
   */
  public static DocAPI getDocAPI() {
    return docAPI;
  }

  /**
   * Sets the value of docAPI
   * 
   * @param docAPI
   *          the docAPI to set
   */
  public static void setDocAPI(DocAPI docAPI) {
    AbstractSitoolsServerTestCase.docAPI = docAPI;
  }

  /**
   * Invoke GET
   * 
   * @param uri
   *          String
   * @param params
   *          String
   * @param parameters
   *          Map<String, String>
   * @param uriTemplate
   *          String
   */
  public void retrieveDocAPI(String uri, String params, Map<String, String> parameters, String uriTemplate) {
    ClientResource cr = new ClientResource(uri);
    System.out.println("URI: " + uriTemplate);
    Representation result = cr.get(docAPI.getMediaTest());
    docAPI.appendSection("Format");
    // url type
    // request
    ClientResource crLocal = new ClientResource(uri);
    docAPI.appendRequest(Method.GET, crLocal);
    // parameters
    docAPI.appendParameters(parameters);
    docAPI.appendSection("Example");
    docAPI.appendRequest(Method.GET, cr);
    // response
    docAPI.appendResponse(result);

    RIAPUtils.exhaust(result);

  }

  /**
   * Invoke GET
   * 
   * @param uri
   *          String
   * @param params
   *          String
   * @param parameters
   *          Map<String, String>
   * @param uriTemplate
   *          String
   */
  public void retrieveDocAPI(String uri, String params, Map<String, String> parameters, String uriTemplate,
      ChallengeResponse challengeResponse) {
    ClientResource cr = new ClientResource(uri);
    cr.setChallengeResponse(challengeResponse);

    System.out.println("URI: " + uriTemplate);
    Representation result = cr.get(docAPI.getMediaTest());
    docAPI.appendSection("Format");
    // url type
    // request
    ClientResource crLocal = new ClientResource(uri);
    docAPI.appendRequest(Method.GET, crLocal);
    // parameters
    docAPI.appendParameters(parameters);
    docAPI.appendSection("Example");
    docAPI.appendRequest(Method.GET, cr);
    // response
    docAPI.appendResponse(result);

    RIAPUtils.exhaust(result);

  }

  /**
   * Invoke POST
   * 
   * @param uri
   *          String
   * @param params
   *          String
   * @param object
   *          Representation
   * @param parameters
   *          Map<String, String>
   * @param uriTemplate
   *          String
   */
  public void postDocAPI(String uri, String params, Representation object, Map<String, String> parameters,
      String uriTemplate) {

    ClientResource cr = new ClientResource(uri);
    System.out.println("URI: " + uriTemplate);
    docAPI.appendSection("Format");
    // url type
    // request
    ClientResource crLocal = new ClientResource(uriTemplate);
    docAPI.appendRequest(Method.POST, crLocal);
    // parameters
    docAPI.appendParameters(parameters);
    docAPI.appendSection("Example");
    docAPI.appendRequest(Method.POST, cr, object);

    Representation result = cr.post(object, docAPI.getMediaTest());

    // response
    docAPI.appendResponse(result);

    RIAPUtils.exhaust(result);

  }

  /**
   * Invoke PUT
   * 
   * @param uri
   *          String
   * @param params
   *          String
   * @param object
   *          Representation
   * @param parameters
   *          Map<String, String>
   * @param uriTemplate
   *          String
   */
  public void putDocAPI(String uri, String params, Representation object, Map<String, String> parameters,
      String uriTemplate) {
    ClientResource cr = new ClientResource(uri);
    System.out.println("URI: " + uriTemplate);

    docAPI.appendSection("Format");
    // url type
    // request
    ClientResource crLocal = new ClientResource(uriTemplate);
    docAPI.appendRequest(Method.PUT, crLocal);
    // parameters
    docAPI.appendParameters(parameters);
    docAPI.appendSection("Example");
    docAPI.appendRequest(Method.PUT, cr, object);

    // response
    Representation result = cr.put(object, docAPI.getMediaTest());
    docAPI.appendResponse(result);

    RIAPUtils.exhaust(result);

  }

  /**
   * Invoke PUT
   * 
   * @param uri
   *          String
   * @param params
   *          String
   * @param parameters
   *          Map<String, String>
   * @param uriTemplate
   *          String
   */
  public void deleteDocAPI(String uri, String params, Map<String, String> parameters, String uriTemplate) {
    ClientResource cr = new ClientResource(uri);
    Representation result = cr.delete(docAPI.getMediaTest());
    docAPI.appendSection("Format");
    // url type
    // request
    ClientResource crLocal = new ClientResource(uriTemplate);
    docAPI.appendRequest(Method.DELETE, crLocal);
    // parameters
    docAPI.appendParameters(parameters);
    docAPI.appendSection("Example");
    docAPI.appendRequest(Method.DELETE, cr);
    // response
    docAPI.appendResponse(result);

    RIAPUtils.exhaust(result);
  }

  /**
   * Create WADL documentation
   * 
   * @param url
   *          application url
   * @param docPath
   *          repository name
   */
  protected void createWadl(String url, String docPath) {
    ClientResource cr = new ClientResource(url);
    DocWadl dw = new DocWadl(docPath);
    try {
      cr.options().write(dw.getWadlPrintStream());
      cr.options(MediaType.TEXT_HTML).write(dw.getHtmlPrintStream());
    }
    catch (ResourceException e) {
      e.printStackTrace();
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }

  }

}
