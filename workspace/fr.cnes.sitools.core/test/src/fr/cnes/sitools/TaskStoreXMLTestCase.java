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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Context;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.tasks.TaskStoreInterface;
import fr.cnes.sitools.tasks.TaskStoreXMLMap;
import fr.cnes.sitools.tasks.model.TaskModel;

/**
 * Test UserBlacklistStoreXML
 * 
 * @since UserStory : ADM UserBlacklists - Sprint : 4
 * @author jp.boignard (AKKA Technologies)
 */
public class TaskStoreXMLTestCase extends AbstractSitoolsTestCase {
  /**
   * static xml store instance for the test
   */
  private static TaskStoreInterface store = null;

  @Override
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_USER_BLACKLIST_STORE_DIR);
  }

  @Before
  @Override
  /**
   * Create store
   * @throws Exception
   */
  public void setUp() throws Exception {
    if (store == null) {
      File storeDirectory = new File(getTestRepository());
      cleanDirectory(storeDirectory);
      cleanMapDirectories(storeDirectory);
      Context ctx = new Context();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());
      store = new TaskStoreXMLMap(storeDirectory, ctx);
    }
  }

  @After
  @Override
  /**
   * Nothing
   * @throws Exception
   */
  public void tearDown() throws Exception {
  }

  /**
   * Test CRUD scenario.
   */
  @Test
  public void testCRUD() {
    assertNone();
    create();
    retrieve();
    update();
    delete();
    assertNone();
  }

  /**
   * Invokes store.getArray and check result is an empty array
   */
  public void assertNone() {
    TaskModel[] list = store.getArray();
    assertNotNull(list);
    assertTrue(list.length == 0);
  }

  /**
   * Invokes store.create and asserts result is conform.
   */
  public void create() {
    TaskModel element = new TaskModel();
    element.setId("admin");
    element.setName("admin");
    element.setUserId("admin");
    element.setCustomStatus("status");

    TaskModel result = store.create(element);
    assertNotNull(result);
    assertEquals("admin", result.getId());
    assertEquals("admin", result.getName());
  }

  /**
   * Invokes store.retrieve and asserts result is conform.
   */
  public void retrieve() {
    TaskModel result = store.retrieve("admin");
    assertNotNull(result);
    result.getId().equals("testCreateUserBlacklist");
  }

  /**
   * Invokes store.update and asserts result is conform.
   */
  public void update() {
    TaskModel element = new TaskModel();
    element.setId("admin");
    element.setName("admin_modified");
    element.setUserId("admin_modified");
    element.setCustomStatus("status_modified");
    TaskModel result = store.update(element);
    assertNotNull(result);
    assertEquals("admin", result.getId());
    assertEquals("admin_modified", result.getName());
  }

  /**
   * Invokes store.delete and asserts result is conform.
   */
  public void delete() {
    boolean result = store.delete("admin");
    assertTrue(result);

    TaskModel testDeletedUserBlacklist = store.retrieve("admin");
    assertNull(testDeletedUserBlacklist);
  }

}
