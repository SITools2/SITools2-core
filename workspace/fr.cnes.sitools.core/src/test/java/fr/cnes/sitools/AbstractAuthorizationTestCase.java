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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.security.authorization.AuthorizationApplication;
import fr.cnes.sitools.security.authorization.AuthorizationStoreInterface;
import fr.cnes.sitools.security.authorization.AuthorizationStoreXMLMap;
import fr.cnes.sitools.security.authorization.client.ResourceAuthorization;
import fr.cnes.sitools.security.authorization.client.RoleAndMethodsAuthorization;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.GetRepresentationUtils;
import fr.cnes.sitools.utils.GetResponseUtils;

/**
 * Test for authorizations
 * 
 * @author m.marseille (AKKA Technologies)
 * 
 */
public abstract class AbstractAuthorizationTestCase extends AbstractSitoolsTestCase {

  /** Title of the test */
  protected static final String TITLE = "Authorization API with JSON format";

  /**
   * static xml store instance for the test
   */
  private static AuthorizationStoreInterface store = null;

  /**
   * Restlet Component for server
   */
  private Component component = null;

  /**
   * absolute url for project management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_AUTHORIZATIONS_URL);
  }

  /**
   * relative url for project management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_AUTHORIZATIONS_URL);
  }

  /**
   * Absolute path location for authorizations store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_AUTHORIZATIONS_STORE_DIR)
        + "/map";
  }

  @Before
  @Override
  /**
   * Init and Start a server with ProjectApplication
   * 
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {

    if (this.component == null) {
      this.component = createTestComponent(SitoolsSettings.getInstance());

      // Context
      Context appContext = this.component.getContext().createChildContext();
      appContext.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());

      File storeDirectory = new File(getTestRepository());
      storeDirectory.mkdirs();
      cleanDirectory(storeDirectory);
      cleanMapDirectories(storeDirectory);
      if (store == null) {
        store = new AuthorizationStoreXMLMap(storeDirectory, appContext);
      }

      appContext.getAttributes().put(ContextAttributes.APP_STORE, store);

      this.component.getDefaultHost().attach(getAttachUrl(), new AuthorizationApplication(appContext));
    }

    if (!this.component.isStarted()) {
      this.component.start();
    }
  }

  @After
  @Override
  /**
   * Stop server
   * @throws java.lang.Exception
   */
  public void tearDown() throws Exception {
    super.tearDown();
    this.component.stop();
    this.component = null;
  }

  /**
   * Test CRUD Project with JSon format exchanges.
   */
  @Test
  public void testCRUD() {
    docAPI.setActive(false);
    try {
      assertNone();
      ResourceAuthorization item = createObject();
      create(item);
      retrieve(item);
      delete(item);
      assertNone();
    }
    catch (IOException e) {
      Engine.getLogger(this.getClass().getName()).warning(e.getMessage());
    }
  }

  /**
   * Test CRUD Project with JSon format exchanges.
   */
  @Test
  public void testCRUD2docAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter(TITLE);
    ResourceAuthorization item = createObject();
    try {
      docAPI.appendSubChapter("Creating a new Authorization", "creating");
      create(item);

      docAPI.appendSubChapter("Retrieving a Authorization", "retrieving");
      retrieve(item);

      docAPI.appendSubChapter("Deleting a Authorization", "deleting");
      delete(item);
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    docAPI.close();
  }

  /**
   * Create an object for tests
   * 
   * @return ResourceAuthorization
   */
  public ResourceAuthorization createObject() {
    ResourceAuthorization item = new ResourceAuthorization();
    item.setId("urn:uuid:SolrApplication:type:fr.cnes.sitools.solr.SolrApplication");
    item.setName("SolrApplication");
    item.setDescription("Sample Solr integration");
    item.setUrl("http://localhost:8182/sitools/solr");
    ArrayList<RoleAndMethodsAuthorization> authorizations = new ArrayList<RoleAndMethodsAuthorization>();
    RoleAndMethodsAuthorization aut = new RoleAndMethodsAuthorization();
    aut.setRole("Editor");
    aut.setAllMethod(true);
    aut.setGetMethod(true);
    authorizations.add(aut);
    item.setAuthorizations(authorizations);
    return item;
  }

  /**
   * Invoke Get to check that nobody's in there
   * 
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   * 
   */
  public void assertNone() throws IOException {
    ClientResource cr = new ClientResource(getBaseUrl());
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    if (!docAPI.appendResponse(result)) {
      Response response = getResponse(getMediaTest(), result, ResourceAuthorization.class, false);
      assertTrue(response.getSuccess());
      int valeur = response.getTotal().intValue();
      assertEquals(0, valeur);
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke POST
   * 
   * @param item
   *          RsourceAuthorization
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void create(ResourceAuthorization item) throws IOException {
    Representation rep = getRepresentation(item, getMediaTest());
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "Identifier");
      putDocAPI(getBaseUrl() + "/" + item.getId(), "", rep, parameters, getBaseUrl() + "/%identifier%");
    }
    else {
      ClientResource cr = new ClientResource(getBaseUrl());
      Representation result = cr.post(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ResourceAuthorization.class, false);
      assertTrue(response.getSuccess());
      ResourceAuthorization prj = (ResourceAuthorization) response.getItem();
      assertEquals(prj.getName(), item.getName());
      assertEquals(prj.getDescription(), item.getDescription());
      assertEquals(prj.getId(), item.getId());
      assertEquals(prj.getType(), item.getType());
      assertEquals(prj.getUrl(), item.getUrl());
      ArrayList<RoleAndMethodsAuthorization> listprj = prj.getAuthorizations();
      ArrayList<RoleAndMethodsAuthorization> listitem = prj.getAuthorizations();
      for (int index = 0; index < listprj.size(); index++) {
        assertEquals(listprj.get(index).getRole(), listitem.get(index).getRole());
        assertEquals(listprj.get(index).getDeleteMethod(), listitem.get(index).getDeleteMethod());
        assertEquals(listprj.get(index).getGetMethod(), listitem.get(index).getGetMethod());
        assertEquals(listprj.get(index).getHeadMethod(), listitem.get(index).getHeadMethod());
        assertEquals(listprj.get(index).getPostMethod(), listitem.get(index).getPostMethod());
        assertEquals(listprj.get(index).getPutMethod(), listitem.get(index).getPutMethod());
        assertEquals(listprj.get(index).getOptionsMethod(), listitem.get(index).getOptionsMethod());
        assertEquals(listprj.get(index).getAllMethod(), listitem.get(index).getAllMethod());
      }
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Invoke Get
   * 
   * @param item
   *          ResourceAuthorization
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void retrieve(ResourceAuthorization item) throws IOException {
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "Identifier application");
      retrieveDocAPI(getBaseUrl() + "/" + item.getId(), "", parameters, getBaseUrl() + "/%identifier%");
    }
    else {
      ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ResourceAuthorization.class, false);
      assertTrue(response.getSuccess());
      ResourceAuthorization prj = (ResourceAuthorization) response.getItem();
      assertEquals(prj.getName(), item.getName());
      assertEquals(prj.getDescription(), item.getDescription());
      assertEquals(prj.getId(), item.getId());
      assertEquals(prj.getType(), item.getType());
      assertEquals(prj.getUrl(), item.getUrl());
      ArrayList<RoleAndMethodsAuthorization> listprj = prj.getAuthorizations();
      ArrayList<RoleAndMethodsAuthorization> listitem = prj.getAuthorizations();
      for (int index = 0; index < listprj.size(); index++) {
        assertEquals(listprj.get(index).getRole(), listitem.get(index).getRole());
        assertEquals(listprj.get(index).getDeleteMethod(), listitem.get(index).getDeleteMethod());
        assertEquals(listprj.get(index).getGetMethod(), listitem.get(index).getGetMethod());
        assertEquals(listprj.get(index).getHeadMethod(), listitem.get(index).getHeadMethod());
        assertEquals(listprj.get(index).getPostMethod(), listitem.get(index).getPostMethod());
        assertEquals(listprj.get(index).getPutMethod(), listitem.get(index).getPutMethod());
        assertEquals(listprj.get(index).getOptionsMethod(), listitem.get(index).getOptionsMethod());
        assertEquals(listprj.get(index).getAllMethod(), listitem.get(index).getAllMethod());
      }
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Invoke Delete
   * 
   * @param item
   *          ResourceAuthorization
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void delete(ResourceAuthorization item) throws IOException {
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "Identifier application");
      deleteDocAPI(getBaseUrl() + "/" + item.getId(), "", parameters, getBaseUrl() + "/%identifier%");
    }
    else {
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ResourceAuthorization.class, false);
      assertTrue(response.getSuccess());
      assertEquals(null, response.getTotal());
      RIAPUtils.exhaust(result);
    }
  }

  // ------------------------------------------------------------
  // RESPONSE REPRESENTATION WRAPPING

  /**
   * REST API Response wrapper for single item expected.
   * 
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @param dataClass
   *          class expected in the item property of the Response object
   * @return Response the response.
   */
  public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass) {
    return getResponse(media, representation, dataClass, false);
  }

  /**
   * REST API Response Representation wrapper for single or multiple items expexted
   * 
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @param dataClass
   *          class expected for items of the Response object
   * @param isArray
   *          if true wrap the data property else wrap the item property
   * @return Response
   */
  public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass, boolean isArray) {
    return GetResponseUtils.getResponseResourceAuthorization(media, representation, dataClass, isArray);
  }

  /**
   * Builds XML or JSON Representation of Project for Create and Update methods.
   * 
   * @param item
   *          Project
   * @param media
   *          APPLICATION_XML or APPLICATION_JSON
   * @return XML or JSON Representation
   */
  public static Representation getRepresentation(ResourceAuthorization item, MediaType media) {
    return GetRepresentationUtils.getRepresentationResourceAuthorization(item, media);
  }

}
