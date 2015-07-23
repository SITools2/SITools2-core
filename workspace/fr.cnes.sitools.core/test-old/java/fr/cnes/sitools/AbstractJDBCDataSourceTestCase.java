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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
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
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.datasource.jdbc.model.JDBCDataSource;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * 
 * Test CRUD JDBCDataSource Rest API
 * 
 * @since UserStory : ADM-DataSources, Sprint : 4
 * 
 * @author jp.boignard (AKKA Technologies)
 */
@Ignore
public abstract class AbstractJDBCDataSourceTestCase extends AbstractSitoolsServerInsecureTestCase {

  /**
   * Nombre de datasources definies initialement dans /data/datasources
   */
  private static int expectedDatasources = 3;

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_DATASOURCES_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASOURCES_URL);
  }

  /**
   * Absolute path location for data set store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_DATASOURCES_STORE_DIR)
        + "/map";
  }

  // @Before
  // @Override
  // /**
  // * Create component, store and application and start server
  // * @throws java.lang.Exception
  // */
  // public void setUp() throws Exception {
  // File storeDirectory = new File(getTestRepository());
  // cleanDirectory(storeDirectory);cleanMapDirectories(storeDirectory);
  // if (store == null) {
  // store = new JDBCDataSourceStoreXML(storeDirectory);
  // }
  //
  // if (this.component == null) {
  // this.component = new Component();
  // this.component.getServers().add(Protocol.HTTP, getTestPort());
  // this.component.getClients().add(Protocol.HTTP);
  // this.component.getClients().add(Protocol.FILE);
  // this.component.getClients().add(Protocol.CLAP);
  //
  //
  //
  // SitoolsSettings settings = SitoolsSettings.getInstance();
  //
  // // Context
  // Context ctx = this.component.getContext().createChildContext();
  // ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());
  //
  // ctx.getAttributes().put(ContextAttributes.APP_STORE, store);
  //
  //
  // // =============================================================
  // // Create applications
  //
  // // ===========================================================================
  // // ApplicationManager for application registering
  //
  // // Store
  // AppRegistryStore storeApp = (AppRegistryStore) settings.getStores().get(Consts.APP_STORE_REGISTRY);
  //
  // // Context
  // Context appContext = component.getContext().createChildContext();
  // String appReference = getBaseUrl() + settings.getString(Consts.APP_APPLICATIONS_URL);
  // appContext.getAttributes().put(ContextAttributes.SETTINGS, settings);
  // appContext.getAttributes().put(ContextAttributes.APP_ATTACH_REF, appReference);
  // appContext.getAttributes().put(ContextAttributes.APP_STORE, storeApp);
  //
  // // Application
  // AppRegistryApplication appManager = new AppRegistryApplication(appContext);
  // appManager.setHost(host);
  //
  // // for applications whose attach / detach themselves other applications to
  // // the virtualhost.
  // settings.setAppRegistry(appManager);
  //
  // // Attachment => GOTO the end
  //
  // component.getInternalRouter().attach(settings.getString(Consts.APP_APPLICATIONS_URL), appManager);
  //
  //
  //
  // this.component.getDefaultHost().attach(getAttachUrl(),
  // new JDBCDataSourceAdministration(this.component.getDefaultHost(), ctx));
  // }
  //
  // if (!this.component.isStarted()) {
  // this.component.start();
  // }
  // }
  //
  // @After
  // @Override
  // /**
  // * Stop server
  // * @throws java.lang.Exception
  // */
  // public void tearDown() throws Exception {
  // super.tearDown();
  // this.component.stop();
  // this.component = null;
  // }

  /**
   * Test CRUD DataSource with JSon format exchanges.
   */
  @Test
  public void testCRUD() {
    docAPI.setActive(false);
    try {
      // 3 datasources existantes dès le lancement du serveur
      assertCount(expectedDatasources);
      JDBCDataSource item = createObject("1000000");
      create(item);
      retrieve(item);
      update(item);
      delete(item);
      assertCount(expectedDatasources);
      createWadl(getBaseUrl(), "datasources");
    }
    catch (IOException e) {

      e.printStackTrace();
    }

  }

  /**
   * Test CRUD DataSource with JSon format exchanges.
   */
  @Test
  public void testCRUD2docAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Manipulating JDBC DataSource Collection");

    docAPI.appendSubChapter("Getting empty list of JDBC DataSource", "getting");
    try {
      assertCount(0);

      JDBCDataSource item = createObject("1000000");

      docAPI.appendSubChapter("Creating a new JDBC DataSource", "create");
      create(item);

      docAPI.appendSubChapter("Getting not empty list of JDBC DataSource", "gettingJDBC");
      assertCount(1);

      docAPI.appendChapter("Manipulating an existing JDBC DataSource resource");
      docAPI.appendSubChapter("Retrieving a JDBC DataSource", "retrieving");

      retrieve(item);

      docAPI.appendSubChapter("Updating a JDBC DataSource", "updating");
      update(item);

      docAPI.appendSubChapter("Deleting a JDBC DataSource", "deleting");
      delete(item);
      docAPI.close();
      assertCount(0);
    }
    catch (IOException e) {

      e.printStackTrace();
    }

  }

  /**
   * Test CRUD DataSource with JSon format exchanges.
   */
  @Test
  public void tstDataSourceActivation() {
    docAPI.setActive(false);
    try {
      assertCount(expectedDatasources);
      assertCount(expectedDatasources);
      JDBCDataSource item = createObject("1000000");
      create(item);

      testConnection(item);

      activate(item);

      monitoring(item);

      explore(item);

      desactivate(item);

      delete(item);

      assertCount(expectedDatasources);
    }
    catch (IOException e) {

      e.printStackTrace();
    }

  }

  /**
   * Scenario multiple CRUD datasource
   */
  @Test
  public void testMultiple() {
    docAPI.setActive(false);
    try {
      assertCount(expectedDatasources);
      JDBCDataSource item1 = createObject("1000000");
      create(item1);
      JDBCDataSource item2 = createObject("2000000");
      create(item2);
      JDBCDataSource item3 = createObject("3000000");
      create(item3);
      assertCount(expectedDatasources + 3);

      delete(item1);
      delete(item2);
      delete(item3);
      assertCount(expectedDatasources);
    }
    catch (IOException e) {

      e.printStackTrace();
    }
  }

  /**
   * Invokes GET and asserts result response is an array of "count" elements.
   * 
   * @param count
   *          number of items expected in array
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void assertCount(int count) throws IOException {
    ClientResource cr = new ClientResource(getBaseUrl());
    docAPI.appendRequest(Method.GET, cr);
    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, JDBCDataSource.class, true);
      assertTrue(response.getSuccess());
      assertEquals(count, response.getTotal().intValue());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Create object
   * 
   * @param id
   *          new instance identifier
   * @return a JDBCDataSource instance for tests
   */
  public JDBCDataSource createObject(String id) {
    // FIXME using an existing database for tests ...
    JDBCDataSource item = new JDBCDataSource();
    item.setId(id);
    item.setName("name");
    item.setDescription("description");
    item.setSchemaOnConnection(SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_SCHEMA"));
    item.setSitoolsAttachementForUsers("/sitools/datasources/new");
    item.setDriverClass(SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_DRIVER"));
    item.setUrl(SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_URL"));
    item.setUserLogin(SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_USER"));
    item.setUserPassword(SitoolsSettings.getInstance().getString("Tests.PGSQL_DATABASE_PASSWORD"));
    return item;
  }

  /**
   * Invoke POST
   * 
   * @param item
   *          a JDBCDataSource instance
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void create(JDBCDataSource item) throws IOException {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl());
    docAPI.appendRequest(Method.POST, cr, rep);

    Representation result = cr.post(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, JDBCDataSource.class);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke GET
   * 
   * @param item
   *          a JDBCDataSource instance
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void retrieve(JDBCDataSource item) throws IOException {
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, JDBCDataSource.class);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          a JDBCDataSource instance
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void update(JDBCDataSource item) throws IOException {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.PUT, cr, rep);

    Representation result = cr.put(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, JDBCDataSource.class);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          a JDBCDataSource instance
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void activate(JDBCDataSource item) throws IOException {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId() + "/start");
    Representation result = cr.put(rep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          a JDBCDataSource instance
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void desactivate(JDBCDataSource item) throws IOException {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId() + "/stop");
    Representation result = cr.put(rep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          a JDBCDataSource instance
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void testConnection(JDBCDataSource item) throws IOException {

    // FIXME Implementer retour XML
    if (getMediaTest().equals(MediaType.APPLICATION_XML)) {
      return;
    }

    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId() + "/test");
    Representation result = cr.put(rep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    RIAPUtils.exhaust(result);
  }

  /**
   * Utilisation du DBExplorer pour consulter la liste des schemas, tables
   * 
   * @param item
   *          JDBCDataSource
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void explore(JDBCDataSource item) throws IOException {
    String dbAttachement = "http://localhost:" + getTestPort() + item.getSitoolsAttachementForUsers();
    String resource;

    ClientResource cr = new ClientResource(dbAttachement);
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    consumeRepresentation(result);

    resource = dbAttachement + "/schemas/sitools";
    cr = new ClientResource(resource);
    result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    consumeRepresentation(result);

    resource = dbAttachement + "/schemas/sitools/tables";
    cr = new ClientResource(resource);
    result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    consumeRepresentation(result);

    resource = dbAttachement + "/schemas/sitools/tables/USERS";
    cr = new ClientResource(resource);
    result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    consumeRepresentation(result);

    resource = dbAttachement + "/schemas/sitools/tables/USERS/records";
    cr = new ClientResource(resource);
    result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    consumeRepresentation(result);

    resource = dbAttachement + "/schemas/sitools/tables/USERS/dataset";
    /** default dataset */
    cr = new ClientResource(resource);
    result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    consumeRepresentation(result);

    RIAPUtils.exhaust(result);

    /*
     * router.attachDefault(targetClass); router.attach("/{tableName}", targetClass); //
     * router.attach("/{tableName}/records", targetClass); // router.attach("/{tableName}/records/{record}",
     * targetClass);
     * 
     * // with schema (depends on database.schemaOnConnection if given) router.attach("/schemas/{schemaName}",
     * targetClass); router.attach("/schemas/{schemaName}/tables", targetClass);
     * router.attach("/schemas/{schemaName}/tables/{tableName}", targetClass); //
     * router.attach("/schemas/{schemaName}/tables/{tableName}/records", targetClass); //
     * router.attach("/schemas/{schemaName}/tables/{tableName}/records/{record}", targetClass);
     * 
     * router.attach("/schemas/{schemaName}/tables/{tableName}/dataset", DataSetWrapperResource.class);
     */
  }

  /**
   * 
   */
  /**
   * Invoke PUT
   * 
   * @param item
   *          a JDBCDataSource instance
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void monitoring(JDBCDataSource item) throws IOException {
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId() + "/monitoring");
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(getMediaTest(), result, String.class, true);
    assertTrue(response.getSuccess());
    // assertNotNull(response.getItem());
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke DELETE
   * 
   * @param item
   *          a JDBCDataSource instance
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void delete(JDBCDataSource item) throws IOException {
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.DELETE, cr);

    Representation result = cr.delete(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, JDBCDataSource.class);
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
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("jdbcdatasource", JDBCDataSource.class);

      if (isArray) {
        if (media == MediaType.APPLICATION_JSON) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }
        else {
          xstream.alias("item", dataClass);
        }
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == JDBCDataSource.class) {
          xstream.aliasField("jdbcdatasource", Response.class, "item");
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
        return null; // TODO complete test for XML, Object
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
  public static Representation getRepresentation(JDBCDataSource item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<JDBCDataSource>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<JDBCDataSource> rep = new XstreamRepresentation<JDBCDataSource>(media, item);
      configure(xstream);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null;
    }
  }

  /**
   * configure xstream with aliases
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("jdbcdatasource", JDBCDataSource.class);
  }

  /**
   * 
   * @param representation
   */
  public void consumeRepresentation(Representation representation) {
    try {
      File temp = File.createTempFile("sitools_test", ".out");
      FileOutputStream fos = new FileOutputStream(temp);
      representation.write(fos);
    }
    catch (FileNotFoundException e) {

      e.printStackTrace();
    }
    catch (IOException e) {

      e.printStackTrace();
    }
  }

}
