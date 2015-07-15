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
import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
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
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.project.ProjectAdministration;
import fr.cnes.sitools.project.ProjectStoreInterface;
import fr.cnes.sitools.project.ProjectStoreXMLMap;
import fr.cnes.sitools.project.graph.GraphStoreInterface;
import fr.cnes.sitools.project.graph.GraphStoreXMLMap;
import fr.cnes.sitools.project.graph.model.Graph;
import fr.cnes.sitools.project.graph.model.GraphNodeComplete;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test CRUD Graph Rest API
 * 
 * @since UserStory : ADM Graphs, Sprint : 4
 * 
 * @author jp.boignard (AKKA Technologies)
 */
@Ignore
public abstract class AbstractGraphTestCase extends AbstractSitoolsTestCase {

  /**
   * static xml store instance for the test
   */
  private static GraphStoreInterface store = null;
  /**
   * static xml store instance for the test
   */
  private static ProjectStoreInterface storeProject = null;

  /** specific project identifier for test purpose. */
  private static String projectId = "350f9f7e-834f-4825-a218-03916c790e71";

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
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_GRAPHS_STORE_DIR) + "/map";
  }

  /**
   * Absolute path location for project store files
   * 
   * @return path
   */
  protected String getTestProjectRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_PROJECTS_STORE_DIR) + "/map";
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
        File storeProjectDirectory = new File(getTestProjectRepository());
        storeDirectory.mkdirs();
        storeProjectDirectory.mkdirs();
        cleanDirectory(storeDirectory);
        cleanMapDirectories(storeDirectory);
        store = new GraphStoreXMLMap(storeDirectory, ctx);
        storeProject = new ProjectStoreXMLMap(storeProjectDirectory, ctx);

        Map<String, Object> stores = new ConcurrentHashMap<String, Object>();
        stores.put(Consts.APP_STORE_PROJECT, storeProject);
        stores.put(Consts.APP_STORE_GRAPH, store);

        SitoolsSettings.getInstance().setStores(stores);

      }

      ctx.getAttributes().put(ContextAttributes.APP_STORE, storeProject);
      ctx.getAttributes().put(Consts.APP_STORE_GRAPH, store);

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
   * Test CRUD Graph with JSon format exchanges.
   */
  @Test
  public void testCRUD() {
    docAPI.setActive(false);
    crud();
  }

  /**
   * method for graph testing and documenting
   */
  private void crud() {
    docAPI.setActive(true);
    docAPI.appendChapter("Manipulating Graph");

    docAPI.appendSubChapter("Retrieve a Graph collection", "retrievingCollection");

    try {
      assertNone();
      Graph item = createObject(projectId);

      docAPI.appendSubChapter("Creating a new Graph", "creating");
      create(item);

      docAPI.appendSubChapter("Retrieving a Graph", "retrieving");
      retrieve(item);

      docAPI.appendSubChapter("Updating a Graph", "updating");
      update(item);

      docAPI.appendSubChapter("Deleting a Graph", "deleting");
      delete(item);
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    docAPI.close();
  }

  /**
   * Test CRUD Graph with JSon format exchanges.
   */
  @Test
  public void testCRUD2docAPI() {
    docAPI.setActive(true);
    crud();
  }

  /**
   * Create an object for tests
   * 
   * @param id
   *          project id
   * @return Graph
   */
  public Graph createObject(String id) {
    Graph item = new Graph();
    item.setId(id);
    item.setName("graph name");

    ArrayList<GraphNodeComplete> nodeList = new ArrayList<GraphNodeComplete>();

    GraphNodeComplete graph1 = new GraphNodeComplete();
    graph1.setText("graphNode1");
    graph1.setDescription("graphNode1 description");
    graph1.setLeaf(false);
    Resource image1 = new Resource();
    image1.setUrl("graphNode1 URL");
    image1.setType("image");
    image1.setMediaType("image");
    graph1.setImage(image1);

    ArrayList<GraphNodeComplete> children1 = new ArrayList<GraphNodeComplete>();

    GraphNodeComplete graphDS11 = new GraphNodeComplete();
    graphDS11.setText("graphNodeDS1");
    graphDS11.setDescription("graphNodeDS1 description");
    graphDS11.setLeaf(true);
    graphDS11.setImageDs("graphNodeDS1 urlImageDataset");
    graphDS11.setDatasetId("414321321654300324654");

    GraphNodeComplete graphDS12 = new GraphNodeComplete();
    graphDS12.setText("graphNodeDS2");
    graphDS12.setDescription("graphNodeDS2 description");
    graphDS12.setLeaf(true);
    graphDS12.setImageDs("graphNodeDS2 urlImageDataset");
    graphDS12.setDatasetId("424322322654300324654");

    children1.add(graphDS11);
    children1.add(graphDS12);

    graph1.setChildren(children1);

    GraphNodeComplete graph2 = new GraphNodeComplete();
    graph2.setText("graphNode2");
    graph2.setDescription("graphNode2 description");
    graph2.setLeaf(false);
    Resource image2 = new Resource();
    image2.setUrl("graphNode2 URL");
    image2.setType("image");
    image2.setMediaType("image");
    graph2.setImage(image2);

    ArrayList<GraphNodeComplete> children2 = new ArrayList<GraphNodeComplete>();

    GraphNodeComplete graphDS21 = new GraphNodeComplete();
    graphDS21.setText("graphNodeDS1");
    graphDS21.setDescription("graphNodeDS1 description");
    graphDS21.setLeaf(true);
    graphDS21.setImageDs("graphNodeDS1 urlImageDataset");
    graphDS21.setDatasetId("414321321654300324654");

    children2.add(graphDS21);

    graph2.setChildren(children2);

    nodeList.add(graph1);
    nodeList.add(graph2);

    item.setNodeList(nodeList);

    return item;
  }

  /**
   * Invoke POST
   * 
   * @param item
   *          Graph
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void create(Graph item) throws IOException {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + projectId + "/graph");

    if (docAPI.isActive()) {
      docAPI.appendSection("Format");
      String url = getBaseUrl() + "/%identifier%/graph";
      ClientResource crLocal = new ClientResource(url);
      docAPI.appendRequest(Method.POST, crLocal, rep);
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "project identifier for create the graph");
      docAPI.appendParameters(parameters);
      docAPI.appendSection("Example");
      docAPI.appendRequest(Method.POST, cr, rep);

      // POST
      Representation result = cr.post(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      docAPI.appendResponse(result);

      RIAPUtils.exhaust(result);
    }
    else {
      // POST
      Representation result = cr.post(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Graph.class);
      assertTrue(response.getSuccess());
      Graph prj = (Graph) response.getItem();
      assertEquals(prj.getName(), item.getName());
      assertEquals(prj.getId(), item.getId());

      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Invoke GET
   * 
   * @param item
   *          Graph
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void retrieve(Graph item) throws IOException {
    String url = getBaseUrl() + "/" + projectId + "/graph";
    ClientResource cr = new ClientResource(url);
    Representation result = cr.get(getMediaTest());
    if (docAPI.isActive()) {
      docAPI.appendSection("Format");
      String urlLocal = getBaseUrl() + "/%identifier%/graph";
      ClientResource crLocal = new ClientResource(urlLocal);
      docAPI.appendRequest(Method.GET, crLocal);
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "project identifier for retrieving the graph");
      docAPI.appendParameters(parameters);
      docAPI.appendSection("Example");
      docAPI.appendRequest(Method.GET, cr);
      docAPI.appendResponse(result);
    }
    else {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Graph.class);
      assertTrue(response.getSuccess());
      Graph prj = (Graph) response.getItem();
      assertEquals(prj.getName(), item.getName());
      assertEquals(prj.getId(), item.getId());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          Graph
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void update(Graph item) throws IOException {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + projectId + "/graph");

    if (docAPI.isActive()) {
      docAPI
          .appendComment("L'API supporte le <strong>JSON</strong> ou le <strong>XML</strong> pour requêter le server");
      docAPI.appendSection("Format");
      String urlLocal = getBaseUrl() + "/%identifier%/graph";
      ClientResource crLocal = new ClientResource(urlLocal);
      docAPI.appendRequest(Method.PUT, crLocal);
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "project identifier for updating the graph");
      docAPI.appendParameters(parameters);
      docAPI.appendSection("Example");
      docAPI.appendRequest(Method.PUT, cr, rep);

      // PUT
      Representation result = cr.put(rep, getMediaTest());
      docAPI.appendResponse(result);

      RIAPUtils.exhaust(result);
    }
    else {
      // PUT
      Representation result = cr.put(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Graph.class);
      assertTrue(response.getSuccess());
      Graph prj = (Graph) response.getItem();
      assertEquals(prj.getName(), item.getName());
      assertEquals(prj.getId(), item.getId());

      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Invoke DELETE
   * 
   * @param item
   *          Graph
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void delete(Graph item) throws IOException {
    String url = getBaseUrl() + "/" + projectId + "/graph";
    ClientResource cr = new ClientResource(url);
    Representation result = cr.delete(getMediaTest());
    if (docAPI.isActive()) {
      docAPI.appendSection("Format");
      String urlLocal = getBaseUrl() + "/%identifier%/graph";
      ClientResource crLocal = new ClientResource(urlLocal);
      docAPI.appendRequest(Method.DELETE, crLocal);
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "project identifier for updating the graph");
      docAPI.appendParameters(parameters);
      docAPI.appendSection("Example");
      docAPI.appendRequest(Method.DELETE, cr);
      docAPI.appendResponse(result);
    }
    else {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Graph.class);
      assertTrue(response.getSuccess());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invokes GET and asserts result response is an empty array.
   * 
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void assertNone() throws IOException {
    String url = getBaseUrl();
    ClientResource cr = new ClientResource(url + "/" + projectId + "/graph");
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Graph.class);
      assertTrue(response.getSuccess());
    }
    RIAPUtils.exhaust(result);
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
      xstream.alias("graph", Graph.class);
      xstream.alias("graphNodeComplete", GraphNodeComplete.class);
      // xstream.alias("dataset", Resource.class);

      if (isArray) {
        xstream.addImplicitCollection(GraphNodeComplete.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Graph.class, "nodeList", GraphNodeComplete.class);
          xstream.addImplicitCollection(GraphNodeComplete.class, "children", GraphNodeComplete.class);
          xstream.aliasField("nodeList", Graph.class, "nodeList");
          xstream.aliasField("children", GraphNodeComplete.class, "children");
        }

        if (dataClass == Graph.class) {
          xstream.aliasField("graph", Response.class, "item");
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
  public static Representation getRepresentation(Graph item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<Graph>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<Graph> rep = new XstreamRepresentation<Graph>(media, item);
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
   * Configures XStream mapping of a Response object with graph content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);
    xstream.alias("graph", Graph.class);
    xstream.alias("graphNodeComplete", GraphNodeComplete.class);
    // xstream.addImplicitCollection(Project.class, "dataSets", "dataSets",
    // Resource.class);
    // xstream.aliasField("dataSets", Project.class, "dataSets");
    // xstream.alias("image", Resource.class);
    // xstream.addImplicitCollection(Project.class, "dataSets", Resource.class);
    // xstream.aliasField("dataSets", Project.class, "dataSets");
    // xstream.aliasField("image", Project.class, "image");
  }

}
