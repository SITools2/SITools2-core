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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.project.ProjectAdministration;
import fr.cnes.sitools.project.ProjectStoreXML;
import fr.cnes.sitools.project.graph.GraphStoreXML;
import fr.cnes.sitools.project.graph.model.Graph;
import fr.cnes.sitools.project.model.Project;
import fr.cnes.sitools.project.model.ProjectModule;
import fr.cnes.sitools.registry.AppRegistryApplication;
import fr.cnes.sitools.registry.AppRegistryStoreXML;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test CRUD Project Rest API
 * 
 * @since UserStory : ADM Projects, Sprint : 4
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public abstract class AbstractProjectTestCase extends AbstractSitoolsTestCase {

  /**
   * static xml store instance for the test
   */
  private static ProjectStoreXML store = null;

  /**
   * static xml store instance for the test
   */
  private static SitoolsStore<Graph> storeGraph = null;

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
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_PROJECTS_URL);
  }

  /**
   * relative url for project management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_PROJECTS_URL);
  }

  /**
   * Absolute path location for project store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_PROJECTS_STORE_DIR);
  }

  /**
   * Absolute path location for project store files
   * 
   * @return path
   */
  protected String getTestGraphRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_GRAPHS_STORE_DIR);
  }

  @Before
  @Override
  /**
   * Init and Start a server with ProjectApplication
   * 
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    File storeDirectory = new File(getTestRepository());
    File storeGraphDirectory = new File(getTestGraphRepository());
    File appRegistry = new File(super.getTestRepository()
        + SitoolsSettings.getInstance().getString(Consts.APP_APPLICATIONS_STORE_DIR));

    if (this.component == null) {
      this.component = new Component();
      this.component.getServers().add(Protocol.HTTP, getTestPort());
      this.component.getClients().add(Protocol.HTTP);
      this.component.getClients().add(Protocol.FILE);
      this.component.getClients().add(Protocol.CLAP);

      // Context
      Context ctx = this.component.getContext().createChildContext();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());

      cleanDirectory(storeDirectory);
      storeGraph = new GraphStoreXML(storeGraphDirectory, ctx);
      store = new ProjectStoreXML(storeDirectory, ctx);

      Map<String, Object> stores = new ConcurrentHashMap<String, Object>();
      stores.put(Consts.APP_STORE_PROJECT, store);
      stores.put(Consts.APP_STORE_GRAPH, storeGraph);

      SitoolsSettings settings = SitoolsSettings.getInstance();
      settings.setStores(stores);

      ctx.getAttributes().put(ContextAttributes.APP_STORE, store);

      // ===========================================================================
      // ApplicationManager for application registering

      // Store
      AppRegistryStoreXML storeApp = new AppRegistryStoreXML(appRegistry, ctx);

      // Context
      Context appContext = component.getContext().createChildContext();
      String appReference = getBaseUrl() + settings.getString(Consts.APP_APPLICATIONS_URL);
      appContext.getAttributes().put(ContextAttributes.SETTINGS, settings);
      appContext.getAttributes().put(ContextAttributes.APP_ATTACH_REF, appReference);
      appContext.getAttributes().put(ContextAttributes.APP_STORE, storeApp);

      // Application
      AppRegistryApplication appManager = new AppRegistryApplication(appContext);

      // for applications whose attach / detach themselves other applications to
      // the virtualhost.
      settings.setAppRegistry(appManager);

      this.component.getDefaultHost().attach(getAttachUrl(),
          new ProjectAdministration(this.component.getDefaultHost(), ctx));
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
      Project item = createObject("new_project");
      create(item);
      retrieve(item);
      update(item);
      delete(item);
      assertNone();
      createWadl(getBaseUrl(), "projects_admin");
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Test CRUD Project with JSon format exchanges.
   */
  @Test
  public void testCRUD2docAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Manipulating Project Collection");

    try {
      assertNone();
      Project item = createObject("1000000");

      docAPI.appendSubChapter("Creating a new Project", "create");
      create(item);

      docAPI.appendChapter("Manipulating an existing Project resource");

      docAPI.appendSubChapter("Retrieving a Project", "retrieving");
      retrieve(item);

      docAPI.appendSubChapter("Updating a Project", "updating");
      update(item);

      docAPI.appendSubChapter("Deleting a Project", "deleting");
      delete(item);
      docAPI.close();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * Create an object for tests
   * 
   * @param id
   *          project id
   * @return Project
   */
  public Project createObject(String id) {
    Project item = new Project();
    item.setId(id);
    item.setName(id);
    item.setDescription("project description");
    Resource image = new Resource();
    image.setUrl("http://uneimage.png");
    item.setImage(image);
    Resource dataset1 = new Resource();
    dataset1.setId("9991");
    Resource dataset2 = new Resource();
    dataset2.setId("9992");
    ArrayList<Resource> datasets = new ArrayList<Resource>();
    datasets.add(dataset1);
    datasets.add(dataset2);
    item.setDataSets(datasets);

    ArrayList<ProjectModule> modules = new ArrayList<ProjectModule>();
    ProjectModule projectModule1 = new ProjectModule();
    projectModule1.setId("12345");
    projectModule1.setName("a new project");
    projectModule1.setDescription("a new project");
    modules.add(projectModule1);
    item.setModules(modules);

    return item;
  }

  /**
   * Invoke POST
   * 
   * @param item
   *          Project
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void create(Project item) throws IOException {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl());
    docAPI.appendRequest(Method.POST, cr, rep);

    Representation result = cr.post(rep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    if (!docAPI.appendResponse(result)) {
      Response response = getResponse(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      Project prj = (Project) response.getItem();
      assertEquals(prj.getName(), item.getName());
      assertEquals(prj.getDescription(), item.getDescription());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Invoke GET
   * 
   * @param item
   *          Project
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void retrieve(Project item) throws IOException {
    String url = getBaseUrl() + "/" + item.getId();
    ClientResource cr = new ClientResource(url);
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      Project prj = (Project) response.getItem();
      assertEquals(prj.getName(), item.getName());
      assertEquals(prj.getDescription(), item.getDescription());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          Project
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void update(Project item) throws IOException {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.PUT, cr, rep);

    Representation result = cr.put(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      Project prj = (Project) response.getItem();
      assertEquals(prj.getName(), item.getName());
      assertEquals(prj.getDescription(), item.getDescription());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Invoke DELETE
   * 
   * @param item
   *          Project
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void delete(Project item) throws IOException {
    String url = getBaseUrl() + "/" + item.getId();
    ClientResource cr = new ClientResource(url);
    docAPI.appendRequest(Method.DELETE, cr);

    Representation result = cr.delete(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Invokes GET and asserts result response is an empty array.
   * 
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void assertNone() throws IOException {
    ClientResource cr = new ClientResource(getBaseUrl());
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Project.class);
      assertTrue(response.getSuccess());
      assertEquals(0, response.getTotal().intValue());
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
    try {
      if (!media.isCompatible(getMediaTest()) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("project", Project.class);
      xstream.alias("dataset", Resource.class);
      // xstream.alias("dataset", Resource.class);

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Project.class, "dataSets", Resource.class);
          xstream.aliasField("dataSets", Project.class, "dataSets");
        }

        if (dataClass == Project.class) {
          xstream.aliasField("project", Response.class, "item");
          // if (dataClass == DataSet.class)
          // xstream.aliasField("dataset", Response.class, "item");
        }
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
  public static Representation getRepresentation(Project item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JsonRepresentation(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<Project> rep = new XstreamRepresentation<Project>(media, item);
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
   * Configures XStream mapping for Response object with Project content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);
    xstream.alias("project", Project.class);
    xstream.alias("dataset", Resource.class);
    // xstream.addImplicitCollection(Project.class, "dataSets", "dataSets",
    // Resource.class);
    // xstream.aliasField("dataSets", Project.class, "dataSets");
    // xstream.alias("image", Resource.class);
    // xstream.addImplicitCollection(Project.class, "dataSets", Resource.class);
    // xstream.aliasField("dataSets", Project.class, "dataSets");
    // xstream.aliasField("image", Project.class, "image");
  }

}
