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
package fr.cnes.sitools.suite;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Generated with Eclipse context menu option : "Recreate test suite ..."
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class AllTests {

  /**
   * Default constructor
   */
  private AllTests() {
    super();
  }

  /**
   * standard method for test suite
   * 
   * @return Test
   */
  public static Test suite() {
    TestSuite suite = new TestSuite(AllTests.class.getName());
    // //$JUnit-BEGIN$
    // // suite.addTestSuite(SitoolsTestCase.class);
    //
    // suite.addTestSuite(JDBCUsersAndGroupsStoreTestCase.class);
    // suite.addTestSuite(UsersAndGroupsTestCase.class);
    //
    // suite.addTestSuite(RoleTestCase.class);
    // // suite.addTestSuite(RoleStoreXMLTestCase.class);
    //
    // // suite.addTestSuite(SecurityTestCase.class);
    // suite.addTestSuite(SecurityManagerTestCase.class);
    //
    // suite.addTestSuite(InscriptionStoreXMLTestCase.class);
    // suite.addTestSuite(InscriptionTestCase.class);
    //
    // suite.addTestSuite(DictionaryStoreXMLTestCase.class);
    // suite.addTestSuite(DictionaryTestCase.class);
    //
    // suite.addTestSuite(DataSourceStoreXMLTestCase.class);
    // suite.addTestSuite(DataSourceTestCase.class);
    //
    // suite.addTestSuite(JDBCDataSourceStoreXMLTestCase.class);
    // suite.addTestSuite(JDBCDataSourceTestCase.class);
    //
    // suite.addTestSuite(DataSetStoreXMLTestCase.class);
    // suite.addTestSuite(DataSetTestCase.class);
    // suite.addTestSuite(DataSetApplicationTestCase.class);
    //
    // suite.addTestSuite(ProjectStoreXMLTestCase.class);
    //
    // suite.addTestSuite(fr.cnes.sitools.xml.ProjectTestCase.class);
    // suite.addTestSuite(fr.cnes.sitools.json.ProjectTestCase.class);
    //
    // // suite.addTestSuite(FormTestCase.class);
    // suite.addTestSuite(FormDTOTestCase.class);
    // // suite.addTestSuite(FormStoreXMLTestCase.class);
    //
    // suite.addTestSuite(OpenSearchStoreXMLTestCase.class);
    // suite.addTestSuite(OpenSearchTestCase.class);
    //
    // //$JUnit-END$
    return suite;
  }

}
