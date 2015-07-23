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
        fr.cnes.sitools.json.ApplicationManagerTestCase.class,
        fr.cnes.sitools.json.ApplicationPluginTestCase.class,
        fr.cnes.sitools.json.AsynchronousResourceTaskTestCase.class,
        fr.cnes.sitools.json.AuthorizationTestCase.class,
        fr.cnes.sitools.json.CollectionsTestCase.class,
        fr.cnes.sitools.json.ConceptTemplateTestCase.class,
        fr.cnes.sitools.json.ConverterNotificationTestCase.class,
        fr.cnes.sitools.json.ConverterPluginTestCase.class,
        fr.cnes.sitools.json.ConverterTestCase.class,
        fr.cnes.sitools.json.DatabaseTypesTestCase.class,
        fr.cnes.sitools.json.DataSetConverterTestCase.class,
        fr.cnes.sitools.json.DataSetDictionaryMappingTestCase.class,
        fr.cnes.sitools.json.DatasetExplorerTestCase.class,
        fr.cnes.sitools.json.DatasetListObjectTestCase.class,
        fr.cnes.sitools.json.DatasetPrimaryKeyTestCase.class,
        fr.cnes.sitools.json.DataSetPropertyTestCase.class,
        fr.cnes.sitools.json.DatasetServicesTestCase.class,
        fr.cnes.sitools.json.DatasetViewTestCase.class,
        fr.cnes.sitools.json.DatastorageFilterPluginTestCase.class,
        fr.cnes.sitools.json.DatastorageTestCase.class,
        fr.cnes.sitools.json.DictionaryTestCase.class,
        fr.cnes.sitools.json.DimensionTestCase.class,
        fr.cnes.sitools.json.EditProfileTestCase.class,
        fr.cnes.sitools.json.EditUserProfileTestCase.class,
        fr.cnes.sitools.json.FeedsDatasetsTestCase.class,
        fr.cnes.sitools.json.FeedsPortalTestCase.class,
        fr.cnes.sitools.json.FeedsProjectsTestCase.class,
        fr.cnes.sitools.json.FilterNotificationTestCase.class,
        fr.cnes.sitools.json.FilterPluginTestCase.class,
        fr.cnes.sitools.json.FilterTestCase.class,
        fr.cnes.sitools.json.FormComponentsTestCase.class,
        fr.cnes.sitools.json.FormProjectTestCase.class,
        fr.cnes.sitools.json.GraphTestCase.class,
        fr.cnes.sitools.json.GuiServiceDeclareTestCase.class,
        fr.cnes.sitools.json.GuiServiceImplementTestCase.class,
        fr.cnes.sitools.json.JDBCDataSourceTestCase.class,
        fr.cnes.sitools.json.JettyPropsTestCase.class,
        fr.cnes.sitools.json.MailTestCase.class,
        fr.cnes.sitools.json.MongoDBDataSourceTestCase.class,
        fr.cnes.sitools.json.MultiApplicationStartingTestCase.class,
        fr.cnes.sitools.json.MultidatasetSearchTestCase.class,
        fr.cnes.sitools.json.NotificationAPITestCase.class,
        fr.cnes.sitools.json.OpensearchActionTestCase.class,
        fr.cnes.sitools.json.OrderTestCase.class,
        fr.cnes.sitools.json.PortalTestCase.class,
        fr.cnes.sitools.json.ProjectApplicationTestCase.class,
        fr.cnes.sitools.json.ProjectListObjectTestCase.class,
        fr.cnes.sitools.json.ProjectProjectModuleTestCase.class,
        fr.cnes.sitools.json.ProjectTestCase.class,
        fr.cnes.sitools.json.ResetPasswordTestCase.class,
        fr.cnes.sitools.json.ResourcePluginTestCase.class,
        fr.cnes.sitools.json.RoleTestCase.class,
        fr.cnes.sitools.json.StorageDirectoryTestCase.class,
        fr.cnes.sitools.json.SynchronousResourceTaskTestCase.class,
        fr.cnes.sitools.json.TaskTestCase.class,
        fr.cnes.sitools.json.UserBlackListTestCase.class,
        fr.cnes.sitools.json.UserRoleTestCase.class,
        fr.cnes.sitools.json.UsersAndGroupsRoleNotificationTestCase.class,
        fr.cnes.sitools.json.UserStorageManagerTestCase.class})
public class AllTestsJsonSuite {

}