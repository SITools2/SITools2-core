/*******************************************************************************
 * Copyright 2010-2014 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
 * <p/>
 * This file is part of SITools2.
 * <p/>
 * SITools2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * SITools2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with SITools2.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package fr.cnes.sitools.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        fr.cnes.sitools.AuthorizationStoreXMLTestCase.class,
        fr.cnes.sitools.ClientAdminSiteMapTestCase.class,
        fr.cnes.sitools.ClientAdminTestCase.class,
        fr.cnes.sitools.ClientUserTestCase.class,
        fr.cnes.sitools.ConceptTemplateStoreXMLTestCase.class,
        fr.cnes.sitools.DataSetApplicationTestCase.class,
        fr.cnes.sitools.DataSetCsvExportTestCase.class,
        fr.cnes.sitools.DataSetFilterTestCase.class,
        fr.cnes.sitools.DataSetFilterWithErrorsTestCase.class,
        fr.cnes.sitools.DataSetFormTestCase.class,
        fr.cnes.sitools.DataSetFormWithConceptTestCase.class,
        fr.cnes.sitools.DataSetFormWithUnitTestCase.class,
        fr.cnes.sitools.DataSetMongoDBFormTestCase.class,
        fr.cnes.sitools.DatasetQueryingJointureTestCase.class,
        fr.cnes.sitools.DatasetQueryingJointureViewTestCase.class,
        fr.cnes.sitools.DataSetRecordSelectionTestCase.class,
        fr.cnes.sitools.DataSetStoreXMLTestCase.class,
        fr.cnes.sitools.DataSetTestCase.class,
        fr.cnes.sitools.DictionaryStoreXMLTestCase.class,
        fr.cnes.sitools.DimensionStoreXMLTestCase.class,
        fr.cnes.sitools.FeedsNotificationDatasetTestCase.class,
        fr.cnes.sitools.FeedsNotificationProjectTestCase.class,
        fr.cnes.sitools.FormDTOTestCase.class,
        fr.cnes.sitools.FormTestCase.class,
        fr.cnes.sitools.GraphStoreXMLTestCase.class,
        fr.cnes.sitools.GuiServiceStoreXMLTestCase.class,
        fr.cnes.sitools.InscriptionStoreXMLTestCase.class,
        fr.cnes.sitools.InscriptionTestCase.class,
        fr.cnes.sitools.InscriptionWithCaptchaTestCase.class,
        fr.cnes.sitools.JDBCDataSourceStoreXMLTestCase.class,
        fr.cnes.sitools.JDBCUsersAndGroupsStoreTestCase.class,
        fr.cnes.sitools.MongoDBDatasetTestCase.class,
        fr.cnes.sitools.MultiDatasetOpensearchTestCase.class,
        fr.cnes.sitools.MultilpleDictionaryTestCase.class,
        fr.cnes.sitools.NotAuthenticatedFilterTestCase.class,
        fr.cnes.sitools.NotificationTestCase.class,
        fr.cnes.sitools.OpenSearchStoreXMLTestCase.class,
        fr.cnes.sitools.OpenSearchTestCase.class,
        fr.cnes.sitools.ProjectStoreXMLTestCase.class,
        fr.cnes.sitools.PublicApplicationTestCase.class,
        fr.cnes.sitools.SecurityAuthorizationTestCase.class,
        fr.cnes.sitools.SecurityBasicDIGESTMD5TestCase.class,
        fr.cnes.sitools.SecurityBasicLDAPMD5TestCase.class,
        fr.cnes.sitools.SecurityDigestTestCase.class,
        fr.cnes.sitools.SecurityEncryptionTestCase.class,
        fr.cnes.sitools.SecurityManagerTestCase.class,
        fr.cnes.sitools.SecurityTestCase.class,
        fr.cnes.sitools.SitoolsServerStartWithProxyTest.class,
        fr.cnes.sitools.SitoolsServerTestCase.class,
        fr.cnes.sitools.SitoolsServerWithMigrationErrorTestCase.class,
        fr.cnes.sitools.SitoolsServerWithMigrationTestCase.class,
        fr.cnes.sitools.SitoolsUtilsTestCase.class,
        fr.cnes.sitools.SolrDirectoryTestCase.class,
        fr.cnes.sitools.SolrTestCase.class,
        fr.cnes.sitools.TaskStoreXMLTestCase.class,
        fr.cnes.sitools.TemplateTestCase.class,
        fr.cnes.sitools.UploadTestCase.class,
        fr.cnes.sitools.UserBlackListFilterTestCase.class,
        fr.cnes.sitools.UserBlackListStoreXMLTestCase.class,
        fr.cnes.sitools.UsersAndGroupsHSQLDBSQLTestCase.class,
        fr.cnes.sitools.UsersAndGroupsMySQLTestCase.class,
        fr.cnes.sitools.UsersAndGroupsPgSQLTestCase.class,
        fr.cnes.sitools.UsersAndGroupsTestCase.class,
        fr.cnes.sitools.UserStorageTestCase.class})
public class AllTestsRootSuite {

}