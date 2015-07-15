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
package fr.cnes.sitools.ext.test.common;

import java.util.Locale;

import org.junit.BeforeClass;
import org.restlet.engine.Engine;

import fr.cnes.sitools.AbstractSitoolsTestCase;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.server.Starter;

public abstract class AbstractExtSitoolsTestCase extends AbstractSitoolsTestCase {

  /**
   * system property name for test port
   */
  private static final String PROPERTY_TEST_PORT = "sitools.test.port";

  /**
   * Default test port for all tests
   */
  public static final int DEFAULT_TEST_PORT_EXT = 1341;

  public static SitoolsSettings settings;

  /**
   * Port for test defined in this order : 1. System property sitools.test.port 2. default test port (1337)
   * 
   * @return test port
   */
  public static int getTestPort() {
    if (System.getProperties().containsKey(PROPERTY_TEST_PORT)) {
      return Integer.parseInt(System.getProperty(PROPERTY_TEST_PORT));
    }
    return DEFAULT_TEST_PORT_EXT;
  }

  /**
   * Executed once before all test methods
   */
  @BeforeClass
  public static void before() {
    // change the server port to avoid collisions with the core tests
    System.getProperties().setProperty(PROPERTY_TEST_PORT, String.valueOf(DEFAULT_TEST_PORT_EXT));
    Engine.clearThreadLocalVariables();

    settings = SitoolsSettings.getInstance("sitools", Starter.class.getClassLoader(), Locale.FRANCE, true);

    settings.setStoreDIR(settings.getString("Tests.STORE_DIR") + "_ext");
    settings.setTmpFolderUrl(settings.getStoreDIR(Consts.APP_TMP_FOLDER_DIR));

  }

  /**
   * Absolute path location for data files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return TEST_FILES_REPOSITORY + "_ext";
  }

}
