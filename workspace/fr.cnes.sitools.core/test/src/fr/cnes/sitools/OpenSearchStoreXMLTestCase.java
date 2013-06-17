 /*******************************************************************************
 * Copyright 2010-2013 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
import fr.cnes.sitools.dataset.opensearch.OpenSearchStoreXML;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.server.Consts;

/**
 * Test CRUD OpenSearch with XML OpenSearchStore persistence
 * 
 * @since UserStory : ADM OpenSearchs - Sprint : 4
 * @author jp.boignard (AKKA Technologies)
 */
public class OpenSearchStoreXMLTestCase extends AbstractSitoolsTestCase {
  /**
   * static xml store instance for the test
   */
  private static OpenSearchStoreXML store = null;

  @Override
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_OPENSEARCH_URL);
  }

  @Override
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_OPENSEARCH_STORE_DIR);
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
      Context ctx = new Context();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());   
      store = new OpenSearchStoreXML(storeDirectory, ctx);
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
    Opensearch item = createObject("testCreateOpenSearch");

    create(item);
    assertFirst(item);
    retrieve(item);

    item.setName("test_Name_modified");
    item.setDescription("test_Description_modified");

    update(item);
    delete(item);
    assertNone();
  }

  /**
   * Invokes store.getArray and check result is an empty array
   */
  public void assertNone() {
    Opensearch[] list = store.getArray();
    assertNotNull(list);
    assertTrue(list.length == 0);
  }

  /**
   * Invokes store.getArray and check first item identifier.
   * 
   * @param item
   *          Opensearch
   */
  public void assertFirst(Opensearch item) {
    Opensearch[] list = store.getArray();
    assertNotNull(list);
    assertTrue(list[0].getId().equals(item.getId()));
  }

  /**
   * Invokes store.create and asserts result is conform.
   * 
   * @param item
   *          Opensearch
   */
  public void create(Opensearch item) {
    Opensearch result = store.create(item);
    assertNotNull(result);
    assertEquals(item.getName(), result.getName());
    assertEquals(item.getDescription(), result.getDescription());
  }

  /**
   * new Opensearch with the specified id and default values for others parameters
   * 
   * @param id
   *          identifier
   * @return instance of Opensearch with the specified id
   */
  public Opensearch createObject(String id) {
    Opensearch element = new Opensearch();
    element.setId(id);
    element.setName("test_Name");
    element.setDescription("test_Description");
    return element;
  }

  /**
   * Invokes store.retrieve and asserts result is conform.
   * 
   * @param item
   *          Opensearch
   */
  public void retrieve(Opensearch item) {
    Opensearch result = store.retrieve(item.getId());
    assertNotNull(result);
    assertEquals(item.getId(), result.getId());
  }

  /**
   * Invokes store.update and asserts result is conform.
   * 
   * @param item
   *          Opensearch
   */
  public void update(Opensearch item) {
    Opensearch result = store.update(item);
    assertNotNull(result);
    assertEquals(item.getName(), result.getName());
    assertEquals(item.getDescription(), result.getDescription());
  }

  /**
   * Invokes store.delete and asserts result is conform.
   * 
   * @param item
   *          Opensearch
   */
  public void delete(Opensearch item) {
    boolean result = store.delete(item.getId());
    assertTrue(result);

    Opensearch testOpensearchDeleted = store.retrieve(item.getId());
    assertNull(testOpensearchDeleted);
  }

  /**
   * Test des requetes avec XQuery / XPath >> quand nous aurons une BD XML Test de creation/consultation/suppression
   * multiples
   */
  @Test
  public void testGetOpenSearchsByXQuery() {
    // il reste la "testCreateOpenSearch" dans le store

    // // all
    // Opensearch[] list = store.getArrayByXQuery("//Opensearch");
    // assertNotNull(list);
    // assertEquals(list.length, 1);
    //
    // String id = "testCreateOpensearch";
    // list = store.getArrayByXQuery("//Opensearch[id ='"+ id +"']");
    // assertNotNull(list);
    // assertTrue(list[0].getId().equals(id));
    //
    //
    // // startsWith
    // String start = "test";
    // list = store.getArrayByXQuery("//Opensearch[starts-with(firstName,'"+start+"')]");
    // assertNotNull(list);
    // assertTrue(list[0].getFirstName().startsWith(start));

    fillDatabase(10);

    Opensearch[] completeList = store.getArray();
    assertNotNull(completeList);

    // pagination list
    ResourceCollectionFilter filter = new ResourceCollectionFilter(2, 5, "test");
    Opensearch[] newList = store.getArray(filter);
    assertNotNull(newList);

    clearDatabase(10);
  }

  /**
   * Invokes store.create for numerous elements ( "test"+no for identifier)
   * 
   * @param nbElement
   *          the number of elements to be created
   */
  private void fillDatabase(int nbElement) {
    Opensearch element = null;
    for (int i = 0; i < nbElement; i++) {
      element = new Opensearch();
      element.setId("test" + i);
      element.setName("test_Name" + i);
      element.setDescription("test_Description" + i);
      Opensearch result = store.create(element);
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
