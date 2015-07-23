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
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Context;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.security.authorization.AuthorizationStoreInterface;
import fr.cnes.sitools.security.authorization.AuthorizationStoreXMLMap;
import fr.cnes.sitools.security.authorization.client.ResourceAuthorization;
import fr.cnes.sitools.security.authorization.client.RoleAndMethodsAuthorization;
import fr.cnes.sitools.common.Consts;

/**
 * Test CRUD Authorization with XML AUthorizationStore persistence
 * 
 * @since UserStory : ADM AUthorizations - Sprint : 4
 * @author jp.boignard (AKKA Technologies)
 */
public class AuthorizationStoreXMLTestCase extends AbstractSitoolsTestCase {
  /**
   * static xml store instance for the test
   */
  private static AuthorizationStoreInterface store = null;

  @Override
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_AUTHORIZATIONS_URL);
  }

  @Override
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_AUTHORIZATIONS_STORE_DIR)
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
      store = new AuthorizationStoreXMLMap(storeDirectory, ctx);
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
    ResourceAuthorization item = createObject("testCreateAuthorization");

    create(item);
    assertFirst(item);
    retrieve(item);

    item.setName("test_Name_modified");
    item.setDescription("test_Description_modified");

    RoleAndMethodsAuthorization rama = new RoleAndMethodsAuthorization();
    rama.setRole("ProductOwner_bis");
    rama.setGetMethod(true);
    rama.setHeadMethod(true);
    rama.setOptionsMethod(true);
    rama.setPostMethod(true); // can post new user stories.
    rama.setPutMethod(false);
    rama.setDeleteMethod(false);
    item.getAuthorizations().add(rama);

    update(item);
    retrieve(item);
    delete(item);
    assertNone();
  }

  /**
   * Invokes store.getArray and check result is an empty array
   */
  public void assertNone() {
    ResourceAuthorization[] list = store.getArray();
    assertNotNull(list);
    assertTrue(list.length == 0);
  }

  /**
   * Invokes store.getArray and check first item identifier.
   * 
   * @param item
   *          ResourceAuthorization
   */
  public void assertFirst(ResourceAuthorization item) {
    ResourceAuthorization[] list = store.getArray();
    assertNotNull(list);
    assertTrue(list[0].getId().equals(item.getId()));
  }

  /**
   * Invokes store.create and asserts result is conform.
   * 
   * @param item
   *          ResourceAuthorization
   */
  public void create(ResourceAuthorization item) {
    ResourceAuthorization result = store.create(item);
    assertNotNull(result);
    assertEquals(item.getName(), result.getName());
    assertEquals(item.getDescription(), result.getDescription());
  }

  /**
   * new ResourceAuthorization with the specified id and default values for others parameters
   * 
   * @param id
   *          identifier
   * @return instance of ResourceAuthorization with the specified id
   */
  public ResourceAuthorization createObject(String id) {
    ResourceAuthorization element = new ResourceAuthorization();
    element.setId(id);
    element.setName("test_Name");
    element.setDescription("test_Description");
    element.setUrl("test_url");
    ArrayList<RoleAndMethodsAuthorization> authorizations = new ArrayList<RoleAndMethodsAuthorization>();
    RoleAndMethodsAuthorization rama = new RoleAndMethodsAuthorization();
    rama.setRole("ScrumMaster");
    rama.setAllMethod(true);
    authorizations.add(rama);
    rama = new RoleAndMethodsAuthorization();
    rama.setRole("ProductOwner");
    rama.setGetMethod(true);
    rama.setHeadMethod(true);
    rama.setOptionsMethod(true);
    rama.setPostMethod(true); // can post new user stories.
    rama.setPutMethod(false);
    rama.setDeleteMethod(false);
    authorizations.add(rama);
    rama = new RoleAndMethodsAuthorization();
    rama.setRole("Team");
    rama.setGetMethod(true);
    rama.setHeadMethod(true);
    rama.setOptionsMethod(true);
    rama.setPostMethod(true);
    rama.setPutMethod(true); // can update user stories.
    rama.setDeleteMethod(false);
    authorizations.add(rama);
    element.setAuthorizations(authorizations);
    return element;
  }

  /**
   * Invokes store.retrieve and asserts result is conform.
   * 
   * @param item
   *          ResourceAuthorization
   */
  public void retrieve(ResourceAuthorization item) {
    ResourceAuthorization result = store.retrieve(item.getId());
    assertNotNull(result);
    assertEquals(item.getId(), result.getId());
    
    assertAuthorization(item.getAuthorizations(), result.getAuthorizations());
  }


  private void assertAuthorization(ArrayList<RoleAndMethodsAuthorization> expected,
      ArrayList<RoleAndMethodsAuthorization> actual) {

    assertNotNull(actual);
    for (RoleAndMethodsAuthorization ramaExpected : expected) {
      RoleAndMethodsAuthorization ramaActual = findRama(actual, ramaExpected);
      assertNotNull(ramaActual);
      assertRama(ramaExpected, ramaActual);
    }

  }

  private void assertRama(RoleAndMethodsAuthorization ramaExpected, RoleAndMethodsAuthorization ramaActual) {
    assertEquals(ramaExpected.getRole(), ramaActual.getRole());
    assertEquals(ramaExpected.getDescription(), ramaActual.getDescription());
    assertEquals(ramaExpected.getAllMethod(), ramaActual.getAllMethod());
    assertEquals(ramaExpected.getDeleteMethod(), ramaActual.getDeleteMethod());
    assertEquals(ramaExpected.getGetMethod(), ramaActual.getGetMethod());
    assertEquals(ramaExpected.getHeadMethod(), ramaActual.getHeadMethod());
    assertEquals(ramaExpected.getOptionsMethod(), ramaActual.getOptionsMethod());
    assertEquals(ramaExpected.getPostMethod(), ramaActual.getPostMethod());
    assertEquals(ramaExpected.getPutMethod(), ramaActual.getPutMethod());
  }

  private RoleAndMethodsAuthorization findRama(ArrayList<RoleAndMethodsAuthorization> actual,
      RoleAndMethodsAuthorization ramaExpected) {
    RoleAndMethodsAuthorization out = null;
    for (RoleAndMethodsAuthorization roleAndMethodsAuthorization : actual) {
      if (roleAndMethodsAuthorization.getRole().equals(ramaExpected.getRole())) {
        out = roleAndMethodsAuthorization;
        break;
      }
    }
    return out;
  }

  /**
   * Invokes store.update and asserts result is conform.
   * 
   * @param item
   *          ResourceAuthorization
   */
  public void update(ResourceAuthorization item) {
    ResourceAuthorization result = store.update(item);
    assertNotNull(result);
    assertEquals(item.getName(), result.getName());
    assertEquals(item.getDescription(), result.getDescription());
    assertAuthorization(item.getAuthorizations(), result.getAuthorizations());
  }

  /**
   * Invokes store.delete and asserts result is conform.
   * 
   * @param item
   *          ResourceAuthorization
   */
  public void delete(ResourceAuthorization item) {
    boolean result = store.delete(item.getId());
    assertTrue(result);

    ResourceAuthorization testResourceAuthorizationDeleted = store.retrieve(item.getId());
    assertNull(testResourceAuthorizationDeleted);
  }

  /**
   * Test des requetes avec XQuery / XPath >> quand nous aurons une BD XML Test de creation/consultation/suppression
   * multiples
   */
  @Test
  public void testGetAuthorizationsByXQuery() {

    fillDatabase(10);

    ResourceAuthorization[] completeList = store.getArray();
    assertNotNull(completeList);

    // pagination list
    ResourceCollectionFilter filter = new ResourceCollectionFilter(2, 5, "test");
    ResourceAuthorization[] newList = store.getArray(filter);
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
    ResourceAuthorization element = null;
    for (int i = 0; i < nbElement; i++) {
      element = new ResourceAuthorization();
      element.setId("test" + i);
      element.setName("test_Name" + i);
      element.setDescription("test_Description" + i);
      ResourceAuthorization result = store.create(element);
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
