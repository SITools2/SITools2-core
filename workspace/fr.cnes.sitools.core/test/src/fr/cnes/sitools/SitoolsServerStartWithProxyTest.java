/*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;

import fr.cnes.sitools.api.DocAPI;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.proxy.ProxySettings;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.server.Starter;
import fr.cnes.sitools.util.FileCopyUtils;
import fr.cnes.sitools.util.FileUtils;

/**
 * Test of server start with proxy
 * 
 * @author m.marseille (AKKA Technologies)
 * 
 */
public class SitoolsServerStartWithProxyTest {

  /**
   * common logger for all tests
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

  /** settings for the tests */
  private static SitoolsSettings settings = null;

  /**
   * system property name for test port
   */
  private static final String PROPERTY_TEST_PORT = "sitools.test.port";

  /**
   * MEDIA type to be set in concrete subclasses of test case
   */
  private static MediaType mediaTest = MediaType.APPLICATION_XML;

  // /**
  // * TITLE for DocAPI of the test case
  // */
  // private static String titleTest = "TEST " + new Date().toString();
  //
  // /**
  // * Class for DocAPI of the test case
  // */
  // private static Class<? extends AbstractSitoolsServerTestCase> classTest = AbstractSitoolsServerTestCase.class;

  /**
   * Helper for generating API documentation
   */
  private static DocAPI docAPI;

  /**
   * server port for the each test instance.
   */
  // private final int TEST_PORT = getTestPort();

  /**
   * 
   * Port for test defined in this order : <code>
   * 1. System property sitools.test.port 
   * 2. default test port (1340)
   * </code>
   * 
   * @return test port
   */
  protected int getTestPort() {
    if (System.getProperties().containsKey(PROPERTY_TEST_PORT)) {
      return Integer.parseInt(System.getProperty(PROPERTY_TEST_PORT));
    }
    return DEFAULT_TEST_PORT;
  }

  /**
   * Try to remove files from directory
   * 
   * @param dir
   *          directory to be cleaned
   */
  public static void cleanDirectory(File dir) {
    if (dir == null) {
      LOGGER.warning("Null directory");
      return;
    }

    LOGGER.info("Clean XML files in directory " + dir.getAbsolutePath());
    try {
      FileUtils.cleanDirectory(dir, new String[] {"xml"}, false);
    }
    catch (IOException e) {
      Engine.getLogger(AbstractSitoolsServerTestCase.class.getName()).warning(
          "Unable to clean " + dir.getPath() + "\n cause:" + e.getMessage());
    }
  }

  /**
   * Clean Directory for start
   * 
   * @param dir
   *          the directory to clean
   */
  public static void cleanDirectoryAll(File dir) {
    if (dir == null) {
      LOGGER.warning("Null directory");
      return;
    }

    LOGGER.info("Clean directory " + dir.getAbsolutePath());
    try {
      FileUtils.cleanDirectory(dir, new String[] {}, false);
    }
    catch (IOException e) {
      Engine.getLogger(AbstractSitoolsServerTestCase.class.getName()).warning(
          "Unable to clean " + dir.getPath() + "\n cause:" + e.getMessage());
    }
  }

  /**
   * Set up the data directory
   * 
   * @param source
   *          source dir
   * @param cible
   *          target dir
   */
  public static void setUpDataDirectory(String source, String cible) {
    cleanDirectoryAll(new File(cible));
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
  protected String getHostUrl() {
    return "http://localhost:" + getTestPort();
  }

  /**
   * before test
   * 
   * @throws Exception
   *           if happens
   */
  @Before
  public void setUp() throws Exception {
    //
  }

  /**
   * After test
   * 
   * @throws Exception
   *           if happens
   */
  @After
  public void tearDown() throws Exception {
    //
  }

  /**
   * Before class load
   */
  @BeforeClass
  public static void before() {
    Engine.clearThreadLocalVariables();

    settings = SitoolsSettings.getInstance("sitools", Starter.class.getClassLoader(), Locale.FRANCE, true);

    String source = settings.getRootDirectory() + TEST_FILES_REFERENCE_REPOSITORY;
    String cible = settings.getRootDirectory() + TEST_FILES_REPOSITORY;

    LOGGER.info("COPY SOURCE:" + source + " CIBLE:" + cible);

    setUpDataDirectory(source, cible);
  }

  /**
   * After class load
   */
  @AfterClass
  public static void afterClass() {
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
   * Starts server with proxy
   */
  @Test
  public void startWithProxy() {
    try {
      settings.setStoreDIR(TEST_FILES_REPOSITORY);
      String[] args = new String[1];
      args[0] = "proxy";
      settings.setPublicHostDomain("localhost");
      Starter.startWithProxy(args, SitoolsSettings.getInstance());
    }
    catch (Exception e) {
      e.printStackTrace();
      fail();
    }
    finally {
      Starter.stop();
      ProxySettings.reset();
    }
  }

  /**
   * Starts server without proxy settings
   */
  @Test
  public void startWithoutProxy() {
    try {
      settings.setStoreDIR(TEST_FILES_REPOSITORY);
      String[] args = new String[0];

      settings.setPublicHostDomain("localhost");
      Starter.startWithProxy(args, SitoolsSettings.getInstance());
    }
    catch (Exception e) {
      e.printStackTrace();
      fail();
    }
    finally {
      Starter.stop();
    }
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
    SitoolsServerStartWithProxyTest.mediaTest = mediaTest;
  }

  // /**
  // * Sets the title of the test for doc API
  // *
  // * @param title
  // * title of the test
  // */
  // public static void setTitleTest(String title) {
  // titleTest = title;
  // }

  // /**
  // * Sets the class of the test for doc API
  // *
  // * @param aClass
  // * class name
  // */
  // public static void setClassTest(Class<? extends AbstractSitoolsServerTestCase> aClass) {
  // classTest = aClass;
  // }

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

}
