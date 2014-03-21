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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Main test suite for all tests
 *
 * @author m.marseille (AKKA technologies)
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
  AuthorizationStoreXMLTestCase.class,
  ClientAdminTestCase.class,
  ClientUserTestCase.class,
  DataSetApplicationTestCase.class,
  DataSetCsvExportTestCase.class,
  DataSetFilterTestCase.class,
  DataSetFormTestCase.class,
  AbstractDataSetManagerTestCase.class,
  DataSetStoreXMLTestCase.class,
  DataSetTestCase.class,
  DictionaryStoreXMLTestCase.class,
  FeedsNotificationDatasetTestCase.class,
  FeedsNotificationProjectTestCase.class,
  FormDTOTestCase.class,
  FormTestCase.class,
  GraphStoreXMLTestCase.class,
  InscriptionStoreXMLTestCase.class,
  InscriptionTestCase.class,
  JDBCDataSourceStoreXMLTestCase.class,
  JDBCUsersAndGroupsStoreTestCase.class,
  MultiDatasetOpensearchTestCase.class,
  MultilpleDictionaryTestCase.class,
  NotificationTestCase.class,
  OpenSearchStoreXMLTestCase.class,
  OpenSearchTestCase.class,
  ProjectStoreXMLTestCase.class,
  PublicApplicationTestCase.class,
  SecurityBasicDIGESTMD5TestCase.class,
  SecurityBasicLDAPMD5TestCase.class,
  SecurityDigestTestCase.class,
  SecurityManagerTestCase.class,
  SecurityTestCase.class,
  SitoolsServerStartWithProxyTest.class,
  SitoolsServerTestCase.class,
  SolrTestCase.class,
  UploadTestCase.class,
  UsersAndGroupsMySQLTestCase.class,
  UsersAndGroupsPgSQLTestCase.class,
  UsersAndGroupsTestCase.class,
  UserStorageTestCase.class,
  fr.cnes.sitools.json.ApplicationManagerTestCase.class,
  fr.cnes.sitools.json.ApplicationPluginTestCase.class,
  fr.cnes.sitools.json.AuthorizationTestCase.class,
  fr.cnes.sitools.json.ConverterNotificationTestCase.class,
  fr.cnes.sitools.json.ConverterPluginTestCase.class,
  fr.cnes.sitools.json.ConverterTestCase.class,
  fr.cnes.sitools.json.DatabaseTypesTestCase.class,
  fr.cnes.sitools.json.DatasetExplorerTestCase.class,
  fr.cnes.sitools.json.DatasetPrimaryKeyTestCase.class,
  fr.cnes.sitools.json.DictionaryTestCase.class,
  fr.cnes.sitools.json.DimensionTestCase.class,
  fr.cnes.sitools.json.FeedsDatasetsTestCase.class,
  fr.cnes.sitools.json.FeedsPortalTestCase.class,
  fr.cnes.sitools.json.FeedsProjectsTestCase.class,
  fr.cnes.sitools.json.FilterNotificationTestCase.class,
  fr.cnes.sitools.json.FilterPluginTestCase.class,
  fr.cnes.sitools.json.FilterTestCase.class,
  fr.cnes.sitools.json.FormComponentsTestCase.class,
  fr.cnes.sitools.json.GraphTestCase.class,
  fr.cnes.sitools.json.JDBCDataSourceTestCase.class,
  fr.cnes.sitools.json.MailTestCase.class,
  fr.cnes.sitools.json.NotificationAPITestCase.class,
  fr.cnes.sitools.json.OpensearchActionTestCase.class,
  fr.cnes.sitools.json.OrderTestCase.class,
  fr.cnes.sitools.json.ProjectApplicationTestCase.class,
  fr.cnes.sitools.json.ProjectListObjectTestCase.class,
  fr.cnes.sitools.json.ProjectTestCase.class,
  fr.cnes.sitools.json.ResourcePluginTestCase.class,
  fr.cnes.sitools.json.RoleTestCase.class,
  fr.cnes.sitools.json.SecurityFilterTestCase.class,
  fr.cnes.sitools.json.StorageDirectoryTestCase.class,  
  fr.cnes.sitools.json.UserStorageManagerTestCase.class,
  fr.cnes.sitools.xml.ApplicationManagerTestCase.class,
  fr.cnes.sitools.xml.ApplicationPluginTestCase.class,
  fr.cnes.sitools.xml.AuthorizationTestCase.class,
  fr.cnes.sitools.xml.ConverterNotificationTestCase.class,
  fr.cnes.sitools.xml.ConverterPluginTestCase.class,
  fr.cnes.sitools.xml.ConverterTestCase.class,
  fr.cnes.sitools.xml.DatabaseTypesTestCase.class,
  fr.cnes.sitools.xml.DatasetExplorerTestCase.class,
  fr.cnes.sitools.xml.DictionaryTestCase.class,
  fr.cnes.sitools.xml.DimensionTestCase.class,
  fr.cnes.sitools.xml.FeedsDatasetsTestCase.class,
  fr.cnes.sitools.xml.FeedsPortalTestCase.class,
  fr.cnes.sitools.xml.FeedsProjectsTestCase.class,
  fr.cnes.sitools.xml.FilterNotificationTestCase.class,
  fr.cnes.sitools.xml.FilterPluginTestCase.class,
  fr.cnes.sitools.xml.FilterTestCase.class,
  fr.cnes.sitools.xml.FormComponentsTestCase.class,
  fr.cnes.sitools.xml.GraphTestCase.class,
  fr.cnes.sitools.xml.JDBCDataSourceTestCase.class,
  fr.cnes.sitools.xml.MailTestCase.class,
  fr.cnes.sitools.xml.NotificationAPITestCase.class,
  fr.cnes.sitools.xml.OrderTestCase.class,
  fr.cnes.sitools.xml.ProjectApplicationTestCase.class,
  fr.cnes.sitools.xml.ProjectListObjectTestCase.class,
  fr.cnes.sitools.xml.ProjectTestCase.class,
  fr.cnes.sitools.xml.ResourcePluginTestCase.class,
  fr.cnes.sitools.xml.RoleTestCase.class,
  fr.cnes.sitools.xml.SecurityFilterTestCase.class,
  fr.cnes.sitools.xml.StorageDirectoryTestCase.class,
  fr.cnes.sitools.xml.UserStorageManagerTestCase.class
})
public class AbstractAllTests {

}
