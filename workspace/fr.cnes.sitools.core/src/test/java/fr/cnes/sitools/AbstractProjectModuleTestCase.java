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
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Dependencies;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.model.Url;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.project.modules.ProjectModuleApplication;
import fr.cnes.sitools.project.modules.ProjectModuleStoreXMLMap;
import fr.cnes.sitools.project.modules.model.ProjectModuleModel;
import fr.cnes.sitools.role.model.Role;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

@Ignore
public abstract class AbstractProjectModuleTestCase extends AbstractSitoolsTestCase {

  /**
   * static xml store instance for the test
   */
  private static SitoolsStore<ProjectModuleModel> store = null;

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
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_PROJECTS_MODULES_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_PROJECTS_MODULES_URL);
  }

  /**
   * Absolute path location for project store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_PROJECTS_MODULES_STORE_DIR)
        + "/map";
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
      this.component = createTestComponent(SitoolsSettings.getInstance());

      // Context
      Context ctx = this.component.getContext().createChildContext();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());

      if (store == null) {
        File storeDirectory = new File(getTestRepository());
        storeDirectory.mkdirs();
        cleanDirectory(storeDirectory);
        cleanMapDirectories(storeDirectory);
        store = new ProjectModuleStoreXMLMap(storeDirectory, ctx);

      }

      ctx.getAttributes().put(ContextAttributes.APP_STORE, store);
      ctx.getAttributes().put(ContextAttributes.APP_ATTACH_REF, getAttachUrl());
      this.component.getDefaultHost().attach(getAttachUrl(), new ProjectModuleApplication(ctx));
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

    ProjectModuleModel module = createObject(moduleId);

    create(module);

    retrieveCollection(1);

    retrieve(moduleId, module);

    module = updateObject(module);

    update(moduleId, module);

    getListComponentsJSON();

    delete(module);

    assertNone();

  }

  @Test
  public void testCRUDAPI() throws Exception {
    docAPI.setActive(true);

    String moduleId = "10000";
    docAPI.appendChapter("Project module managment API");
    docAPI.appendSubChapter("Get empty project module list", "assert_none_1");
    assertNone();

    ProjectModuleModel module = createObject(moduleId);
    docAPI.appendSubChapter("Create a new project module", "create");
    create(module);
    docAPI.appendSubChapter("Retrieve project module list", "retrieveCol");
    retrieveCollection(1);
    docAPI.appendSubChapter("Retrieve a project module", "retrieve");
    retrieve(moduleId, module);

    module = updateObject(module);
    docAPI.appendSubChapter("Update a project module", "update");
    update(moduleId, module);
    docAPI.appendSubChapter("Get the list of project modules for the client", "getList");
    getListComponentsJSON();
    docAPI.appendSubChapter("Delete a project module", "delete");
    delete(module);
    docAPI.appendSubChapter("Get empty project module list", "assert_none_2");
    assertNone();
    docAPI.close();

  }

  private void create(ProjectModuleModel moduleIn) {
    Representation rep = getRepresentation(moduleIn, getMediaTest());
    String url = getBaseUrl();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("POST", "A <i>ProjectModule</i> object");
      postDocAPI(url, "", rep, parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(getBaseUrl());
      Representation result = cr.post(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ProjectModuleModel.class);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
      ProjectModuleModel moduleOut = (ProjectModuleModel) response.getItem();
      assertEquals(moduleIn.getId(), moduleOut.getId());
      assertEquals(moduleIn.getName(), moduleOut.getName());
      assertEquals(moduleIn.getDescription(), moduleOut.getDescription());
      // assertDependencies(moduleIn.getDependencies(), moduleOut.getDependencies());

      RIAPUtils.exhaust(result);
    }
  }

  private void retrieveCollection(int nbExpectedProjectModule) {
    String url = getBaseUrl();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      retrieveDocAPI(url, "", parameters, getBaseUrl());
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ProjectModuleModel.class, true);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(nbExpectedProjectModule), response.getTotal());
      RIAPUtils.exhaust(result);
    }
  }

  private void retrieve(String moduleId, ProjectModuleModel moduleIn) {
    String url = getBaseUrl() + "/" + moduleId;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("moduleId", "ProjectModule identifier");
      retrieveDocAPI(url, "", parameters, getBaseUrl() + "/%moduleId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ProjectModuleModel.class);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
      ProjectModuleModel moduleOut = (ProjectModuleModel) response.getItem();
      assertEquals(moduleIn.getId(), moduleOut.getId());
      assertEquals(moduleIn.getName(), moduleOut.getName());
      assertEquals(moduleIn.getDescription(), moduleOut.getDescription());
      RIAPUtils.exhaust(result);
    }
  }

  private ProjectModuleModel updateObject(ProjectModuleModel module) {
    module.setName(module.getName() + "_updated");
    module.setDescription(module.getDescription() + "_updated");
    return module;
  }

  private void update(String moduleId, ProjectModuleModel moduleIn) {
    Representation rep = getRepresentation(moduleIn, getMediaTest());
    String url = getBaseUrl() + "/" + moduleId;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("moduleId", "ProjectModule identifier");
      parameters.put("PUT", "A <i>ProjectModule</i> object");
      putDocAPI(url, "", rep, parameters, getBaseUrl() + "/%moduleId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ProjectModuleModel.class);
      assertTrue(response.getSuccess());
      ProjectModuleModel moduleOut = (ProjectModuleModel) response.getItem();
      assertEquals(moduleIn.getId(), moduleOut.getId());
      assertEquals(moduleIn.getName(), moduleOut.getName());
      assertEquals(moduleIn.getDescription(), moduleOut.getDescription());
      RIAPUtils.exhaust(result);

    }
  }

  private void getListComponentsJSON() throws IOException {
    String url = getBaseUrl() + "/listComponents.json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      assertNotNull(result.getText());
      RIAPUtils.exhaust(result);
    }
  }

  private void delete(ProjectModuleModel module) {
    String url = getBaseUrl() + "/" + module.getId();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("moduleId", "ProjectModule identifier");
      parameters.put("PUT", "A <i>ProjectModule</i> object");
      deleteDocAPI(url, "", parameters, getBaseUrl() + "/%moduleId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ProjectModuleModel.class);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }
  }

  // /**
  // * Assert that the 2 given {@link Dependencies} are the same
  // *
  // * @param depExpected
  // * the expected {@link Dependencies}
  // * @param depActual
  // * the actual {@link Dependencies}
  // */
  // private void assertDependencies(Dependencies depExpected, Dependencies depActual) {
  // if (!(depExpected == null && depActual == null)) {
  // assertNotNull(depExpected);
  // assertNotNull(depActual);
  //
  //
  //
  // }
  //
  // }

  /**
   * Create a new ProjectModule Object
   * 
   * @param id
   *          the id
   * @return a new ProjectModule Object
   */
  private ProjectModuleModel createObject(String id) {
    ProjectModuleModel projectModule = new ProjectModuleModel();

    projectModule.setId(id);
    projectModule.setName("projectModule_name");
    projectModule.setDescription("projectModule_description");

    List<Role> listRoles = new ArrayList<Role>();
    Role roleTest = new Role();
    roleTest.setId("idTest");
    roleTest.setName("test_role");
    roleTest.setDescription("a description of a test role");

    listRoles.add(roleTest);
    projectModule.setListRoles(listRoles);

    List<Url> js = new ArrayList<Url>();
    js.add(new Url("/sitools/js"));

    List<Url> css = new ArrayList<Url>();
    css.add(new Url("/sitools/css"));

    Dependencies dep = new Dependencies();

    dep.setCss(css);
    dep.setJs(js);

    projectModule.setDependencies(dep);

    return projectModule;
  }

  /**
   * Assert that no modules exists on the server
   */
  private void assertNone() {
    retrieveCollection(0);
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
    try {
      if (!media.isCompatible(getMediaTest()) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.alias("response", Response.class);
      xstream.alias("projectModule", ProjectModuleModel.class);

      if (media.equals(MediaType.APPLICATION_JSON)) {
        xstream.addImplicitCollection(Dependencies.class, "js", Url.class);
        xstream.addImplicitCollection(Dependencies.class, "css", Url.class);
        xstream.addImplicitCollection(ProjectModuleModel.class, "listRoles", Role.class);

      }

      if (isArray) {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        xstream.aliasField("projectModule", Response.class, "item");
      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(getMediaTest())) {
        Response response = rep.getObject("response");

        return response;
      }
      else {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null; // TODO complete test with ObjectRepresentation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
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
  public static Representation getRepresentation(ProjectModuleModel item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<ProjectModuleModel>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<ProjectModuleModel> rep = new XstreamRepresentation<ProjectModuleModel>(media, item);
      configure(xstream);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null; // TODO complete test with ObjectRepresentation
    }
  }

  /**
   * Configures XStream mapping of Response object with ConverterModel content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);

    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");
  }

}
