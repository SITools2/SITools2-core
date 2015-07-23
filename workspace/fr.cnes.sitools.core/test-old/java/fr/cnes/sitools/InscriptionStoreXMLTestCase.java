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
import fr.cnes.sitools.inscription.InscriptionStoreInterface;
import fr.cnes.sitools.inscription.InscriptionStoreXMLMap;
import fr.cnes.sitools.inscription.model.Inscription;
import fr.cnes.sitools.common.Consts;

/**
 * Test InscriptionStoreXML
 * 
 * @since UserStory : ADM DataSets - Sprint : 4
 * @author jp.boignard (AKKA Technologies)
 */
public class InscriptionStoreXMLTestCase extends AbstractSitoolsTestCase {
  /**
   * static xml store instance for the test
   */
  private static InscriptionStoreInterface store = null;

  @Override
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_INSCRIPTIONS_ADMIN_URL);
  }

  @Override
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_INSCRIPTIONS_STORE_DIR)
        + "/map";
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
      store = new InscriptionStoreXMLMap(storeDirectory, ctx);
    }
  }

  @After
  @Override
  public void tearDown() throws Exception {
  }

  /**
   * Test CRUD scenario.
   */
  @Test
  public void testAdminCRUDInscription() {
    assertNone();
    Inscription item = createObject("testCreateInscription");
    create(item);
    retrieve(item);
    update(item);
    delete(item);
    assertNone();
  }

  /**
   * Invokes store.create and asserts result is conform.
   * 
   * @param element
   *          an Inscription object
   */
  public void create(Inscription element) {
    Inscription result = store.create(element);
    assertNotNull(result);
    assertEquals("test_identifier", result.getIdentifier());
    assertEquals("test_firstName", result.getFirstName());
    assertEquals("test_lastName", result.getLastName());
    assertEquals("test_comment", result.getComment());

    // L'encodage du password doit etre fait hors StoreXML
    assertEquals("test_password", result.getPassword());

  }

  /**
   * new Inscription with the specified id and default values for others parameters
   * 
   * @param id
   *          identifier
   * @return instance of Inscription with the specified id
   */
  public Inscription createObject(String id) {
    Inscription element = new Inscription();
    element.setId("testCreateInscription");
    element.setIdentifier("test_identifier");
    element.setFirstName("test_firstName");
    element.setLastName("test_lastName");
    element.setPassword("test_password");
    element.setComment("test_comment");

    return element;
  }

  /**
   * Invokes store.getArray and check result is an empty array
   */
  public void assertNone() {
    Inscription[] list = store.getArray();
    assertNotNull(list);
    assertEquals(0, list.length);
  }

  /**
   * Invokes store.retrieve and asserts result is conform.
   * 
   * @param element
   *          an Inscription object
   */
  public void retrieve(Inscription element) {
    Inscription result = store.retrieve(element.getId());
    assertNotNull(result);
    result.getId().equals("testCreateInscription");
  }

  /**
   * Invokes store.update and asserts result is conform.
   * 
   * @param element
   *          an Inscription object
   */
  public void update(Inscription element) {
    element.setId("testCreateInscription");
    element.setFirstName("test_firstName_modified");
    element.setLastName("test_lastName_modified");
    element.setComment("test_comment_modified");
    element.setPassword("test_password_modified");

    Inscription result = store.update(element);
    assertNotNull(result);
    assertEquals(element.getFirstName(), result.getFirstName());
    assertEquals(element.getLastName(), result.getLastName());
    assertEquals(element.getComment(), result.getComment());
    assertEquals(element.getPassword(), result.getPassword());
  }

  /**
   * Test des requetes avec XQuery / XPath >> quand nous aurons une BD XML Test de creation/consultation/suppression
   * multiples
   */
  @Test
  public void testGetInscriptionsByXQuery() {
    assertNone();
    Inscription element = createObject("1234567890");
    create(element);

    // // all
    // Inscription[] list = store.getArrayByXQuery("//inscription");
    // assertNotNull(list);
    // assertEquals(list.length, 1);
    //
    // String id = "testCreateInscription";
    // list = store.getArrayByXQuery("//inscription[id ='"+ id +"']");
    // assertNotNull(list);
    // assertTrue(list[0].getId().equals(id));
    //
    //
    // // startsWith
    // String start = "test";
    // list = store.getArrayByXQuery("//inscription[starts-with(firstName,'"+start+"')]");
    // assertNotNull(list);
    // assertTrue(list[0].getFirstName().startsWith(start));

    fillDatabase(10);

    Inscription[] completeList = store.getArray();
    assertNotNull(completeList);

    // pagination list
    ResourceCollectionFilter filter = new ResourceCollectionFilter(2, 5, "test");
    Inscription[] newList = store.getArray(filter);
    assertNotNull(newList);

    clearDatabase(10);
    delete(element);
    assertNone();
  }

  /**
   * Invokes store.delete and asserts result is conform.
   * 
   * @param element
   *          an Inscription object
   */
  public void delete(Inscription element) {
    boolean result = store.delete(element.getId());
    assertTrue(result);

    Inscription testDeletedInscription = store.retrieve(element.getId());
    assertNull(testDeletedInscription);
  }

  /**
   * Invokes store.create for numerous elements ( "test"+no for identifier)
   * 
   * @param nbElement
   *          the number of elements to be created
   */
  private void fillDatabase(int nbElement) {
    Inscription element = null;
    for (int i = 0; i < nbElement; i++) {
      element = new Inscription();
      element.setId("test" + i);
      element.setFirstName("test_firstName" + i);
      element.setLastName("test_lastName" + i);
      element.setPassword("test_lastName" + i);
      element.setComment("test_comment" + i);
      Inscription result = store.create(element);
      assertNotNull(result);
    }
  }

  /**
   * Invokes store.delete on the "test"+no elements with 0 <= no < nbElement
   * 
   * @param nbElement
   *          the no of the tested element
   */
  private void clearDatabase(int nbElement) {
    for (int i = 0; i < nbElement; i++) {
      store.delete("test" + i);
    }
  }

}
