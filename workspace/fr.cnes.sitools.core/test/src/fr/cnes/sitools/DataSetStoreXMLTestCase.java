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
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Context;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.dataset.DataSetStoreXML;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.datasource.jdbc.model.Structure;
import fr.cnes.sitools.server.Consts;

/**
 * Test CRUD DataSet with XML DataSetStore persistence
 * 
 * @since UserStory : ADM DataSets - Sprint : 4
 * @author jp.boignard (AKKA Technologies)
 */
public class DataSetStoreXMLTestCase extends AbstractSitoolsTestCase {
  /**
   * static xml store instance for the test
   */
  private static DataSetStoreXML store = null;

  @Override
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_URL);
  }

  @Override
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_STORE_DIR);
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
      store = new DataSetStoreXML(storeDirectory, ctx);
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
    DataSet item = createObject("testCreateDataSet");

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
    DataSet[] list = store.getArray();
    assertNotNull(list);
    assertTrue(list.length == 0);
  }

  /**
   * Invokes store.getArray and check first item identifier.
   * 
   * @param item
   *          DataSet
   */
  public void assertFirst(DataSet item) {
    DataSet[] list = store.getArray();
    assertNotNull(list);
    assertTrue(list[0].getId().equals(item.getId()));
  }

  /**
   * Invokes store.create and asserts result is conform.
   * 
   * @param item
   *          DataSet
   */
  public void create(DataSet item) {
    DataSet result = store.create(item);
    assertNotNull(result);
    assertEquals(item.getName(), result.getName());
    assertEquals(item.getDescription(), result.getDescription());
  }

  /**
   * new DataSet with the specified id and default values for others parameters
   * 
   * @param id
   *          identifier
   * @return instance of DataSet with the specified id
   */
  public DataSet createObject(String id) {
    DataSet element = new DataSet();
    element.setId(id);
    element.setName("test_Name");
    element.setDescription("test_Description");
    Structure a = new Structure("A", "TABLE1");
    Structure b = new Structure("B", "TABLE2");
    ArrayList<Structure> aliases = new ArrayList<Structure>();
    aliases.add(a);
    aliases.add(b);
    element.setStructures(aliases);

    ArrayList<Column> columns = new ArrayList<Column>();
    Column c1 = new Column("1", "1", "1", 1, true, true, "filter");
    Column c2 = new Column("2", "2", "2", 2, true, true, "filter");
    columns.add(c1);
    columns.add(c2);
    element.setColumnModel(columns);
    return element;
  }

  /**
   * Invokes store.retrieve and asserts result is conform.
   * 
   * @param item
   *          DataSet
   */
  public void retrieve(DataSet item) {
    DataSet result = store.retrieve(item.getId());
    assertNotNull(result);
    assertEquals(item.getId(), result.getId());
  }

  /**
   * Invokes store.update and asserts result is conform.
   * 
   * @param item
   *          DataSet
   */
  public void update(DataSet item) {
    DataSet result = store.update(item);
    assertNotNull(result);
    assertEquals(item.getName(), result.getName());
    assertEquals(item.getDescription(), result.getDescription());
  }

  /**
   * Invokes store.delete and asserts result is conform.
   * 
   * @param item
   *          DataSet
   */
  public void delete(DataSet item) {
    boolean result = store.delete(item.getId());
    assertTrue(result);

    DataSet testDataSetDeleted = store.retrieve(item.getId());
    assertNull(testDataSetDeleted);
  }

  /**
   * Test des requetes avec XQuery / XPath >> quand nous aurons une BD XML Test de creation/consultation/suppression
   * multiples
   */
  @Test
  public void testGetDataSetsByXQuery() {
    // il reste la "testCreateDataSet" dans le store

    // // all
    // DataSet[] list = store.getArrayByXQuery("//DataSet");
    // assertNotNull(list);
    // assertEquals(list.length, 1);
    //
    // String id = "testCreateDataSet";
    // list = store.getArrayByXQuery("//DataSet[id ='"+ id +"']");
    // assertNotNull(list);
    // assertTrue(list[0].getId().equals(id));
    //
    //
    // // startsWith
    // String start = "test";
    // list = store.getArrayByXQuery("//DataSet[starts-with(firstName,'"+start+"')]");
    // assertNotNull(list);
    // assertTrue(list[0].getFirstName().startsWith(start));

    fillDatabase(10);

    DataSet[] completeList = store.getArray();
    assertNotNull(completeList);

    // pagination list
    ResourceCollectionFilter filter = new ResourceCollectionFilter(2, 5, "test");
    DataSet[] newList = store.getArray(filter);
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
    DataSet element = null;
    for (int i = 0; i < nbElement; i++) {
      element = new DataSet();
      element.setId("test" + i);
      element.setName("test_Name" + i);
      element.setDescription("test_Description" + i);
      DataSet result = store.create(element);
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
