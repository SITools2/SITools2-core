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
        fr.cnes.sitools.xml.ApplicationManagerTestCase.class,
        fr.cnes.sitools.xml.ApplicationPluginTestCase.class,
        fr.cnes.sitools.xml.AsynchronousResourceTaskTestCase.class,
        fr.cnes.sitools.xml.AuthorizationTestCase.class,
        fr.cnes.sitools.xml.CollectionsTestCase.class,
        fr.cnes.sitools.xml.ConceptTemplateTestCase.class,
        fr.cnes.sitools.xml.ConverterNotificationTestCase.class,
        fr.cnes.sitools.xml.ConverterPluginTestCase.class,
        fr.cnes.sitools.xml.ConverterTestCase.class,
        fr.cnes.sitools.xml.DatabaseTypesTestCase.class,
        fr.cnes.sitools.xml.DataSetConverterTestCase.class,
        fr.cnes.sitools.xml.DataSetDictionaryMappingTestCase.class,
        fr.cnes.sitools.xml.DatasetExplorerTestCase.class,
        fr.cnes.sitools.xml.DatasetListObjectTestCase.class,
        fr.cnes.sitools.xml.DataSetPropertyTestCase.class,
        fr.cnes.sitools.xml.DatasetServicesTestCase.class,
        fr.cnes.sitools.xml.DatasetViewTestCase.class,
        fr.cnes.sitools.xml.DatastorageFilterPluginTestCase.class,
        fr.cnes.sitools.xml.DatastorageTestCase.class,
        fr.cnes.sitools.xml.DictionaryTestCase.class,
        fr.cnes.sitools.xml.DimensionTestCase.class,
        fr.cnes.sitools.xml.EditProfileTestCase.class,
        fr.cnes.sitools.xml.EditUserProfileTestCase.class,
        fr.cnes.sitools.xml.FeedsDatasetsTestCase.class,
        fr.cnes.sitools.xml.FeedsPortalTestCase.class,
        fr.cnes.sitools.xml.FeedsProjectsTestCase.class,
        fr.cnes.sitools.xml.FilterNotificationTestCase.class,
        fr.cnes.sitools.xml.FilterPluginTestCase.class,
        fr.cnes.sitools.xml.FilterTestCase.class,
        fr.cnes.sitools.xml.FormComponentsTestCase.class,
        fr.cnes.sitools.xml.FormProjectTestCase.class,
        fr.cnes.sitools.xml.GraphTestCase.class,
        fr.cnes.sitools.xml.GuiServiceDeclareTestCase.class,
        fr.cnes.sitools.xml.GuiServiceImplementTestCase.class,
        fr.cnes.sitools.xml.JDBCDataSourceTestCase.class,
        fr.cnes.sitools.xml.JettyPropsTestCase.class,
        fr.cnes.sitools.xml.MailTestCase.class,
        fr.cnes.sitools.xml.MongoDBDataSourceTestCase.class,
        fr.cnes.sitools.xml.MultidatasetSearchTestCase.class,
        fr.cnes.sitools.xml.NotificationAPITestCase.class,
        fr.cnes.sitools.xml.OrderTestCase.class,
        fr.cnes.sitools.xml.PortalTestCase.class,
        fr.cnes.sitools.xml.ProjectApplicationTestCase.class,
        fr.cnes.sitools.xml.ProjectListObjectTestCase.class,
        fr.cnes.sitools.xml.ProjectProjectModuleTestCase.class,
        fr.cnes.sitools.xml.ProjectTestCase.class,
        fr.cnes.sitools.xml.ResetPasswordTestCase.class,
        fr.cnes.sitools.xml.ResourcePluginTestCase.class,
        fr.cnes.sitools.xml.RoleTestCase.class,
        fr.cnes.sitools.xml.StorageDirectoryTestCase.class,
        fr.cnes.sitools.xml.SynchronousResourceTaskTestCase.class,
        fr.cnes.sitools.xml.TaskTestCase.class,
        fr.cnes.sitools.xml.UserBlackListTestCase.class,
        fr.cnes.sitools.xml.UserRoleTestCase.class,
        fr.cnes.sitools.xml.UserStorageManagerTestCase.class})
public class AllTestsXmlSuite {

}