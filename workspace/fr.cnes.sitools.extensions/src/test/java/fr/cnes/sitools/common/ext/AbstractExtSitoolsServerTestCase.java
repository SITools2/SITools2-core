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
package fr.cnes.sitools.common.ext;

import java.io.File;
import java.util.Locale;

import org.junit.BeforeClass;
import org.restlet.engine.Engine;

import fr.cnes.sitools.common.AbstractSitoolsServerTestCase;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.server.Starter;
import fr.cnes.sitools.util.FileCopyUtils;

public abstract class AbstractExtSitoolsServerTestCase extends AbstractSitoolsServerTestCase {

  /**
   * Executed once before all test methods
   */
  @BeforeClass
  public static void before() {
    // change the server port to avoid collisions with the core tests
    System.getProperties().setProperty(PROPERTY_TEST_PORT, "1341");
    Engine.clearThreadLocalVariables();
    settings = SitoolsSettings.getInstance("sitools", Starter.class.getClassLoader(), Locale.FRANCE, true);

    String source = settings.getRootDirectory() + TEST_FILES_REPOSITORY;
    String cible = settings.getRootDirectory() + TEST_FILES_REPOSITORY + "_ext";

    File fileCible = new File(cible);
    fileCible.mkdirs();
    setUpDataDirectory(source, cible);

    // Copy the test directory from the core project
    source = settings.getRootDirectory() + "/workspace/fr.cnes.sitools.core/src/test/resources/data";

    FileCopyUtils.copyAFolderExclude(source, cible, ".svn");
    // Copy the test directory from the ext project
    String sourceExtTest = settings.getRootDirectory() + "/workspace/fr.cnes.sitools.extensions/src/test/resources/data";
    FileCopyUtils.copyAFolderExclude(sourceExtTest, cible, ".svn");

    settings.setStoreDIR(TEST_FILES_REPOSITORY + "_ext");
    settings.setTmpFolderUrl(settings.getStoreDIR(Consts.APP_TMP_FOLDER_DIR));
    
//    cleanMapDirectories(fileCible);

    AbstractSitoolsServerTestCase.start();
  }

}
