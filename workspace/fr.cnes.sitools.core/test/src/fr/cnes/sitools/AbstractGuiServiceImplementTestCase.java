/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Dependencies;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.model.Url;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.plugins.guiservices.implement.GuiServicePluginApplication;
import fr.cnes.sitools.plugins.guiservices.implement.GuiServicePluginStoreXML;
import fr.cnes.sitools.plugins.guiservices.implement.model.GuiServicePluginModel;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.GetRepresentationUtils;
import fr.cnes.sitools.utils.GetResponseUtils;

public abstract class AbstractGuiServiceImplementTestCase extends AbstractSitoolsTestCase {

  /**
   * static xml store instance for the test
   */
  private static SitoolsStore<GuiServicePluginModel> store = null;

  /** The parent ID */
  private static final String PARENT_ID = "5555";

  /** The settings */
  private SitoolsSettings settings = SitoolsSettings.getInstance();

  /**
   * Restlet Component for server
   */
  private Component component = null;

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + settings.getString(Consts.APP_DATASETS_URL) + "/{parentId}"
        + settings.getString(Consts.APP_GUI_SERVICES_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_DATASETS_URL) + "/{parentId}"
        + settings.getString(Consts.APP_GUI_SERVICES_URL);
  }

  /**
   * Absolute path location for project store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + settings.getString(Consts.APP_GUI_SERVICES_PLUGIN_STORE_DIR);
  }

  @Before
  @Override
  /**
   * Init and Start a server with GraphApplication
   * 
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {

    if (this.component == null) {
      this.component = new Component();
      this.component.getServers().add(Protocol.HTTP, getTestPort());
      this.component.getClients().add(Protocol.HTTP);
      this.component.getClients().add(Protocol.FILE);
      this.component.getClients().add(Protocol.CLAP);

      // Context
      Context ctx = this.component.getContext().createChildContext();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, settings);

      if (store == null) {
        File storeDirectory = new File(getTestRepository());
        cleanDirectory(storeDirectory);
        store = new GuiServicePluginStoreXML(storeDirectory, ctx);

      }

      ctx.getAttributes().put(ContextAttributes.APP_STORE, store);
      ctx.getAttributes().put(ContextAttributes.APP_ATTACH_REF, getAttachUrl());
      this.component.getDefaultHost().attach(getAttachUrl(), new GuiServicePluginApplication(ctx));
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

  @Test
  public void testCRUD() throws Exception {
    docAPI.setActive(false);
    String moduleId = "10000";
    assertNone();

    GuiServicePluginModel module = createObject(moduleId);

    create(module);

    retrieveCollection(1);

    retrieve(moduleId, module);

    module = updateObject(module);

    update(moduleId, module);

    delete(module);

    assertNone();

  }

  @Test
  public void testCRUDAPI() throws Exception {
    docAPI.setActive(true);

    String moduleId = "10000";
    docAPI.appendChapter("Gui service management API");
    docAPI.appendSubChapter("Get empty gui service list", "assert_none_1");
    assertNone();

    GuiServicePluginModel module = createObject(moduleId);
    docAPI.appendSubChapter("Create a new gui service", "create");
    create(module);
    docAPI.appendSubChapter("Retrieve gui services list", "retrieveCol");
    retrieveCollection(1);
    docAPI.appendSubChapter("Retrieve a gui service", "retrieve");
    retrieve(moduleId, module);

    module = updateObject(module);
    docAPI.appendSubChapter("Update a gui service", "update");
    update(moduleId, module);
    docAPI.appendSubChapter("Delete a gui service", "delete");
    delete(module);
    docAPI.close();

  }

  private void create(GuiServicePluginModel guiServiceIn) {
    Representation rep = getRepresentation(guiServiceIn, getMediaTest());
    String url = getServiceUrl(PARENT_ID);
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("POST", "A <i>GuiService</i> object");
      postDocAPI(url, "", rep, parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(getServiceUrl(PARENT_ID));
      Representation result = cr.post(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, GuiServicePluginModel.class);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
      GuiServicePluginModel guiServiceOut = (GuiServicePluginModel) response.getItem();
      assertEquals(guiServiceIn.getId(), guiServiceOut.getId());
      assertEquals(guiServiceIn.getName(), guiServiceOut.getName());
      assertEquals(guiServiceIn.getDescription(), guiServiceOut.getDescription());

      RIAPUtils.exhaust(result);
    }
  }

  private void retrieveCollection(int nbExpectedGuiService) {
    String url = getServiceUrl(PARENT_ID);
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, getServiceUrl(PARENT_ID));
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, GuiServicePluginModel.class, true);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(nbExpectedGuiService), response.getTotal());
      RIAPUtils.exhaust(result);
    }
  }

  private void retrieve(String guiServiceId, GuiServicePluginModel guiServiceIn) {
    String url = getServiceUrl(PARENT_ID) + "/" + guiServiceId;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("guiServiceId", "Gui service identifier");
      retrieveDocAPI(url, "", parameters, getServiceUrl(PARENT_ID) + "/%guiServiceId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, GuiServicePluginModel.class);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
      GuiServicePluginModel guiServiceOut = (GuiServicePluginModel) response.getItem();
      assertEquals(guiServiceIn.getId(), guiServiceOut.getId());
      assertEquals(guiServiceIn.getName(), guiServiceOut.getName());
      assertEquals(guiServiceIn.getDescription(), guiServiceOut.getDescription());
      RIAPUtils.exhaust(result);
    }
  }

  private GuiServicePluginModel updateObject(GuiServicePluginModel guiService) {
    guiService.setName(guiService.getName() + "_updated");
    guiService.setDescription(guiService.getDescription() + "_updated");
    guiService.setAuthor("AKKA technologies");
    guiService.setVersion("1.1");
    return guiService;
  }

  private void update(String moduleId, GuiServicePluginModel guiServiceIn) {
    Representation rep = getRepresentation(guiServiceIn, getMediaTest());
    String url = getServiceUrl(PARENT_ID) + "/" + moduleId;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("guiServiceId", "GuiService identifier");
      parameters.put("PUT", "A <i>GuiService</i> object");
      putDocAPI(url, "", rep, parameters, getServiceUrl(PARENT_ID) + "/%guiServiceId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, GuiServicePluginModel.class);
      assertTrue(response.getSuccess());
      GuiServicePluginModel guiServiceOut = (GuiServicePluginModel) response.getItem();
      assertEquals(guiServiceIn.getId(), guiServiceOut.getId());
      assertEquals(guiServiceIn.getName(), guiServiceOut.getName());
      assertEquals(guiServiceIn.getDescription(), guiServiceOut.getDescription());
      assertEquals(guiServiceIn.getAuthor(), guiServiceOut.getAuthor());
      assertEquals(guiServiceIn.getVersion(), guiServiceOut.getVersion());
      RIAPUtils.exhaust(result);

    }
  }

  private void delete(GuiServicePluginModel guiService) {
    String url = getServiceUrl(PARENT_ID) + "/" + guiService.getId();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("guiServiceId", "GuiService identifier");
      deleteDocAPI(url, "", parameters, getServiceUrl(PARENT_ID) + "/%guiServiceId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, GuiServicePluginModel.class);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Create a new ProjectModule Object
   * 
   * @param id
   *          the id
   * @return a new ProjectModule Object
   */
  public static GuiServicePluginModel createObject(String id) {
    GuiServicePluginModel guiServiceModel = new GuiServicePluginModel();

    guiServiceModel.setId(id);
    guiServiceModel.setName("projectModule_name");
    guiServiceModel.setDescription("projectModule_description");

    guiServiceModel.setAuthor("AKKA");
    guiServiceModel.setVersion("1.0");

    List<Url> js = new ArrayList<Url>();
    js.add(new Url("/sitools/js"));

    List<Url> css = new ArrayList<Url>();
    css.add(new Url("/sitools/css"));

    Dependencies dep = new Dependencies();

    dep.setCss(css);
    dep.setJs(js);

    guiServiceModel.setDependencies(dep);

    return guiServiceModel;
  }

  /**
   * Assert that no modules exists on the server
   */
  private void assertNone() {
    retrieveCollection(0);
  }

  private String getServiceUrl(String parentId) {
    return getBaseUrl().replaceAll("\\{parentId\\}", parentId);
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
    return GetResponseUtils.getResponseGuiServicePlugin(media, representation, dataClass, isArray);
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
  public static Representation getRepresentation(GuiServicePluginModel item, MediaType media) {
    return GetRepresentationUtils.getRepresentationGuiServicePlugin(item, media);
  }

}
