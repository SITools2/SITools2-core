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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Context;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.project.graph.GraphStoreInterface;
import fr.cnes.sitools.project.graph.GraphStoreXMLMap;
import fr.cnes.sitools.project.graph.model.Graph;
import fr.cnes.sitools.common.Consts;

;

/**
 * Test GraphStoreXML
 * 
 * @since UserStory : ADM Graphs - Sprint : 7
 * @author jp.boignard (AKKA Technologies)
 */
public class GraphStoreXMLTestCase extends AbstractSitoolsTestCase {
  /**
   * static xml store instance for the test
   */
  private static GraphStoreInterface store = null;

  @Override
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_PROJECTS_URL);
  }

  @Override
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_PROJECTS_STORE_DIR) + "/map";
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
      storeDirectory.mkdirs();
      cleanDirectory(storeDirectory);
      cleanMapDirectories(storeDirectory);
      Context ctx = new Context();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());
      store = new GraphStoreXMLMap(storeDirectory, ctx);
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
    Graph[] list = store.getArray();
    assertNotNull(list);
    assertTrue(list.length == 0);
  }

  /**
   * Invokes store.create and asserts result is conform.
   */
  public void create() {
    Graph element = new Graph();
    element.setId("testCreateGraph");
    element.setName("test_Name_modified");
    Graph result = store.create(element);
    assertNotNull(result);
    assertEquals("test_Name_modified", result.getName());

  }

  /**
   * Invokes store.retrieve and asserts result is conform.
   */
  public void retrieve() {
    Graph result = store.retrieve("testCreateGraph");
    assertNotNull(result);
    result.getId().equals("testCreateGraph");
  }

  /**
   * Invokes store.update and asserts result is conform.
   */
  public void update() {
    Graph element = new Graph();
    element.setId("testCreateGraph");
    element.setName("test_Name_modified");

    Graph result = store.update(element);
    assertNotNull(result);
    assertEquals("test_Name_modified", result.getName());

  }

  /**
   * Invokes store.delete and asserts result is conform.
   */
  public void delete() {
    boolean result = store.delete("testCreateGraph");
    assertTrue(result);

    Graph testDeletedGraph = store.retrieve("testCreateGraph");
    assertNull(testDeletedGraph);
  }

  /**
   * Test des requetes avec XQuery / XPath >> quand nous aurons une BD XML Test de creation/consultation/suppression
   * multiples
   */
  @Test
  public void testGetGraphsByXQuery() {
    assertNone();

    create();
    fillDatabase(10);

    // // all
    // Graph[] list = store.getArrayByXQuery("//Graph");
    // assertNotNull(list);
    // assertEquals(list.length, 1);
    //
    // String id = "testCreateGraph";
    // list = store.getArrayByXQuery("//Graph[id ='"+ id +"']");
    // assertNotNull(list);
    // assertTrue(list[0].getId().equals(id));
    //
    //
    // // startsWith
    // String start = "test";
    // list = store.getArrayByXQuery("//Graph[starts-with(firstName,'"+start+"')]");
    // assertNotNull(list);
    // assertTrue(list[0].getFirstName().startsWith(start));

    Graph[] completeList = store.getArray();
    assertNotNull(completeList);

    // pagination list
    ResourceCollectionFilter filter = new ResourceCollectionFilter(2, 5, "test");
    Graph[] newList = store.getArray(filter);
    assertNotNull(newList);

    delete();
    clearDatabase(10);

    assertNone();
  }

  /**
   * Invokes store.create for numerous elements ( "test"+no for identifier)
   * 
   * @param nbElement
   *          the number of elements to be created
   */
  private void fillDatabase(int nbElement) {
    Graph element = null;
    for (int i = 0; i < nbElement; i++) {
      element = new Graph();
      element.setId("test" + i);
      element.setName("test_Name" + i);

      Graph result = store.create(element);
      assertNotNull(result);
    }
  }

  /**
   * Invokes store.delete on the "test"+no elements with 0 <= no < nbElement
   * 
   * @param nbElement
   *          number of element to clear suppose elements are identifier by "test"+no
   */
  private void clearDatabase(int nbElement) {
    for (int i = 0; i < nbElement; i++) {
      store.delete("test" + i);
    }
  }

}
