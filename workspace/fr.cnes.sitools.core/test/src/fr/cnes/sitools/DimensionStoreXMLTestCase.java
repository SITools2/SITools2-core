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
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Context;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.units.dimension.DimensionStoreInterface;
import fr.cnes.sitools.units.dimension.DimensionStoreXMLMap;
import fr.cnes.sitools.units.dimension.model.SitoolsDimension;

/**
 * Test SitoolsDimensionStoreXML
 * 
 * @since UserStory : ADM Dictionaries - Sprint : 4
 * @author jp.boignard (AKKA Technologies)
 */
public class DimensionStoreXMLTestCase extends AbstractSitoolsTestCase {
  /**
   * static xml store instance for the test
   */
  private static DimensionStoreInterface store = null;

  @Override
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DICTIONARIES_URL);
  }

  @Override
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_DICTIONARIES_STORE_DIR);
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
      store = new DimensionStoreXMLMap(storeDirectory, ctx);
    }
  }

  @After
  @Override
  /**
   * Nothing
   * @throws Exception
   */
  public void tearDown() throws Exception {
    super.tearDown();
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
   * Invokes store.create and asserts result is conform.
   */
  public void create() {
    SitoolsDimension element = new SitoolsDimension();
    element.setId("testCreateSitoolsDimension");
    element.setName("test_Name");
    element.setDescription("test_Description");
    element.setDimensionHelperName("My_dimension_helper");
    SitoolsDimension result = store.create(element);
    assertNotNull(result);
    assertEquals("test_Name", result.getName());
    assertEquals("test_Description", result.getDescription());
    assertEquals("My_dimension_helper", result.getDimensionHelperName());
  }

  /**
   * Invokes store.getArray and check result is an empty array
   */
  public void assertNone() {
    SitoolsDimension[] list = store.getArray();
    assertNotNull(list);
    assertEquals(0, list.length);
  }

  /**
   * Invokes store.retrieve and asserts result is conform.
   */
  public void retrieve() {
    SitoolsDimension result = store.retrieve("testCreateSitoolsDimension");
    assertNotNull(result);
    assertEquals("testCreateSitoolsDimension", result.getId());
  }

  /**
   * Invokes store.update and asserts result is conform.
   */
  public void update() {
    SitoolsDimension element = new SitoolsDimension();
    element.setId("testCreateSitoolsDimension");
    element.setName("test_Name_modified");
    element.setDescription("test_Description_modified");
    element.setDimensionHelperName("My_dimension_helper_modified");

    SitoolsDimension result = store.update(element);
    assertNotNull(result);
    assertEquals("test_Name_modified", result.getName());
    assertEquals("test_Description_modified", result.getDescription());
    assertEquals("My_dimension_helper_modified", result.getDimensionHelperName());
  }

  /**
   * Invokes store.delete and asserts result is conform.
   */
  public void delete() {
    boolean result = store.delete("testCreateSitoolsDimension");
    assertTrue(result);

    SitoolsDimension testDeletedSitoolsDimension = store.retrieve("testCreateSitoolsDimension");
    assertNull(testDeletedSitoolsDimension);
  }

}
