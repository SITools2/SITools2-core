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
import java.util.ArrayList;

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

import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.datasource.common.SitoolsDataSourceModel;
import fr.cnes.sitools.datasource.mongodb.model.MongoDBDataSource;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * 
 * Test CRUD MongoDBDataSource Rest API
 * 
 * @since UserStory : ADM-DataSources, Sprint : 4
 * 
 * @author jp.boignard (AKKA Technologies)
 */
@Ignore
public abstract class AbstractMongoDBataSourceTestCase extends AbstractSitoolsServerInsecureTestCase {

  /**
   * Nombre de datasources definies initialement dans /data/datasources
   */
  private static int expectedDatasources = 1;

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + settings.getString(Consts.APP_DATASOURCES_MONGODB_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + settings.getString(Consts.APP_DATASOURCES_MONGODB_URL);
  }

  /**
   * Absolute path location for data set store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + settings.getString(Consts.APP_DATASOURCES_MONGODB_STORE_DIR);
  }

  /**
   * Test CRUD DataSource with JSon format exchanges.
   */
  @Test
  public void testCRUD() {
    docAPI.setActive(false);
    try {
      // 0 datasources existantes dès le lancement du serveur
      assertCount(expectedDatasources);
      MongoDBDataSource item = createObject("1000000");
      create(item);
      retrieve(item);
      update(item);
      delete(item);
      assertCount(expectedDatasources);
      createWadl(getBaseUrl(), "datasources");
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
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

      MongoDBDataSource item = createObject("1000000");

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
      // TODO Auto-generated catch block
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
      MongoDBDataSource item = createObject("1000000");
      testConnection(item, true);

      create(item);

      activate(item);

      testConnection(item.getId(), true);

      monitoring(item);

      explore(item);

      desactivate(item);

      delete(item);

      assertCount(expectedDatasources);
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * Test CRUD DataSource with JSon format exchanges.
   */
  @Test
  public void tstDataSourceActivationError() {
    docAPI.setActive(false);
    try {
      assertCount(expectedDatasources);
      MongoDBDataSource item = createObject("1000000");
      create(item);
      // test with url error
      String datasourceUrl = item.getUrl();
      item.setUrl(item.getUrl() + "_error");
      testConnection(item, false);
      item.setUrl(datasourceUrl);

      // test with port error
      Integer portNumber = item.getPortNumber();
      item.setPortNumber(portNumber * 10);
      testConnection(item, false);
      item.setPortNumber(portNumber);

      // test with wrong database name
      String databaseName = item.getDatabaseName();
      item.setDatabaseName(item.getDatabaseName() + "_error");
      testConnection(item, false);
      item.setDatabaseName(databaseName);

      // // test with wrong authentification
      item.setUserPassword(item.getUserPassword() + "_error");
      testConnection(item, false);

      delete(item);

      assertCount(expectedDatasources);
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
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
      MongoDBDataSource item1 = createObject("1000000");
      create(item1);
      MongoDBDataSource item2 = createObject("2000000");
      create(item2);
      MongoDBDataSource item3 = createObject("3000000");
      create(item3);
      assertCount(expectedDatasources + 3);

      delete(item1);
      delete(item2);
      delete(item3);
      assertCount(expectedDatasources);
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
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
      Response response = getResponse(getMediaTest(), result, MongoDBDataSource.class, true);
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
   * @return a MongoDBDataSource instance for tests
   */
  public MongoDBDataSource createObject(String id) {
    // FIXME using an existing database for tests ...
    MongoDBDataSource item = new MongoDBDataSource();
    item.setId(id);
    item.setName("name");
    item.setDescription("description");
    item.setSitoolsAttachementForUsers("/sitools/datasources/new");
    item.setUrl(settings.getString("Tests.MONGODB_DATABASE_URL"));
    item.setPortNumber(new Integer(settings.getString("Tests.MONGODB_DATABASE_PORT")));
    item.setDatabaseName(settings.getString("Tests.MONGODB_DATABASE_DATABASENAME"));

    item.setUserLogin(settings.getString("Tests.MONGODB_DATABASE_USER"));
    item.setUserPassword(settings.getString("Tests.MONGODB_DATABASE_PASSWORD"));

    item.setDriverClass("com.mongodb.Driver");
    item.setAuthentication(true);
    return item;
  }

  /**
   * Invoke POST
   * 
   * @param item
   *          a MongoDBDataSource instance
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void create(MongoDBDataSource item) throws IOException {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl());
    docAPI.appendRequest(Method.POST, cr, rep);

    Representation result = cr.post(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, MongoDBDataSource.class);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke GET
   * 
   * @param item
   *          a MongoDBDataSource instance
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void retrieve(SitoolsDataSourceModel item) throws IOException {
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, MongoDBDataSource.class);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          a MongoDBDataSource instance
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void update(MongoDBDataSource item) throws IOException {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.PUT, cr, rep);

    Representation result = cr.put(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, MongoDBDataSource.class);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
    }
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          a MongoDBDataSource instance
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void activate(MongoDBDataSource item) throws IOException {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId() + "/start");
    Representation result = cr.put(rep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, MongoDBDataSource.class);
    System.out.println(response.getMessage());
    assertTrue(response.getSuccess());
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          a MongoDBDataSource instance
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void desactivate(MongoDBDataSource item) throws IOException {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId() + "/stop");
    Representation result = cr.put(rep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, MongoDBDataSource.class);
    System.out.println(response.getMessage());
    assertTrue(response.getSuccess());
    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          a MongoDBDataSource instance
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void testConnection(MongoDBDataSource item, boolean expectSuccess) throws IOException {
    Representation rep = null;
    if (item != null) {
      rep = getRepresentation(item, getMediaTest());
    }
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId() + "/test");
    Representation result = cr.put(rep, getMediaTest());
    assertNotNull(result);

    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, String.class, true);
    assertNotNull(response);

    System.out.println("ERROR : " + printLog(response.getData()));

    assertEquals(expectSuccess, response.getSuccess());

    RIAPUtils.exhaust(result);
  }

  /**
   * Invoke PUT
   * 
   * @param item
   *          a MongoDBDataSource instance
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void testConnection(String itemId, boolean expectSuccess) throws IOException {
    Representation rep = null;

    ClientResource cr = new ClientResource(getBaseUrl() + "/" + itemId + "/test");
    Representation result = cr.put(rep, getMediaTest());
    assertNotNull(result);

    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, String.class, true);
    assertNotNull(response);

    System.out.println("ERROR : " + printLog(response.getData()));

    assertEquals(expectSuccess, response.getSuccess());

    RIAPUtils.exhaust(result);
  }

  private String printLog(ArrayList<Object> data) {
    String ret = "";
    for (Object object : data) {
      ret += object.toString();
    }

    return ret;
  }

  /**
   * Utilisation du DBExplorer pour consulter la liste des schemas, tables
   * 
   * @param item
   *          MongoDBDataSource
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void explore(SitoolsDataSourceModel item) throws IOException {
    String dbAttachement = getHostUrl() + item.getSitoolsAttachementForUsers();
    String resource;

    ClientResource cr = new ClientResource(dbAttachement);
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    consumeRepresentation(result);

    resource = dbAttachement + "/collections";
    cr = new ClientResource(resource);
    result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    consumeRepresentation(result);

    resource = dbAttachement + "/collections/Metadata";
    cr = new ClientResource(resource);
    result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    consumeRepresentation(result);

    resource = dbAttachement + "/collections/Metadata/metadata";
    cr = new ClientResource(resource);
    result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    consumeRepresentation(result);

    resource = dbAttachement + "/collections/Metadata/records?start=0&limit=1";
    cr = new ClientResource(resource);
    result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    consumeRepresentation(result);

    resource = dbAttachement
        + "/collections/Metadata/records/F:\\L3B_Cyclope\\2007\\H9V9\\CYCL_BIO_1km_V3.1_VGT_H9V9_2007359.hdf.gz";
    /** default dataset */
    cr = new ClientResource(resource);
    result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    consumeRepresentation(result);

    RIAPUtils.exhaust(result);

  }

  /**
   * 
   */
  /**
   * Invoke PUT
   * 
   * @param item
   *          a MongoDBDataSource instance
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void monitoring(SitoolsDataSourceModel item) throws IOException {
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
   *          a MongoDBDataSource instance
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void delete(SitoolsDataSourceModel item) throws IOException {
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.DELETE, cr);

    Representation result = cr.delete(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, MongoDBDataSource.class);
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
      xstream.alias("mongodbdatasource", MongoDBDataSource.class);
      xstream.alias("logs", String.class);

      xstream.alias("item", dataClass);
      xstream.alias("item", Object.class, dataClass);
      if (isArray) {
        if (media == MediaType.APPLICATION_JSON) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }

        if (dataClass == String.class) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }

        // xstream.addImplicitCollection(Response.class, "data", "item", dataClass);

        // xstream.alias("item", Object.class, dataClass);
        // xstream.alias("item", dataClass);

        /*
         * } else { xstream.alias("item", dataClass); }
         */
      }
      else {

        if (dataClass == MongoDBDataSource.class) {
          xstream.aliasField("mongodbdatasource", Response.class, "item");
        }
        if (dataClass == String.class) {
          xstream.aliasField("logs", Response.class, "item");
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
  public static Representation getRepresentation(MongoDBDataSource item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<MongoDBDataSource>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<MongoDBDataSource> rep = new XstreamRepresentation<MongoDBDataSource>(media, item);
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
   * configure xstream with aliases
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
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
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
