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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.AttributeValueConverter;
import fr.cnes.sitools.utils.CreateDatasetUtil;
import fr.cnes.sitools.utils.GetResponseUtils;

/**
 * TESTS the datasets primary key access with all the possible types It also tests the links between 2 datasets using
 * the datasetRequestURL parameter
 * 
 * Database type tested are : for PostgreSQL : - int - smallint - bigint - float - double - numeric - varchar - char -
 * timestamp - date - time - timestamp_with_time_zone - time_without_time_zone - serial - bigserial
 * 
 * for MySQL : - varchar - tinyint - mediumint - int - bigint - decimal - timestamp - datetime - date - year - time -
 * char
 * 
 * Float and Double are not used for MySQL as they are not precise enough, decimal should be used
 * 
 * @author m.gond (AKKA Technologies)
 */
@Ignore
public abstract class AbstractDatasetPrimaryKeyTestCase extends AbstractDataSetManagerTestCase {

  /** url attachment of the dataset with postgreSQL datasource */
  private static String urlAttachPostgreSQL = "/dataset/tests/pg";

  /** url attachment of the dataset with MySQL datasource */
  private static String urlAttachMySQL = "/dataset/tests/mysql";

  /** url attachment of the dataset with MySQL datasource */
  private static String urlAttachMongoDB = "/dataset/tests/mongodb";

  /** list of columns with primary key value for postgres datasource's dataset */
  private static HashMap<String, String> listColumnsPG;

  /** list of columns with primary key value for mysql datasource's dataset */
  private static HashMap<String, String> listColumnsMYSQL;

  /** list of columns with primary key value for mongoDB datasource's dataset */
  private static HashMap<String, String> listColumnsMongoDB;

  /** Expected record for Postgresql dataset */
  private static Record expectedPgRecord;

  /** Expected record for mysql dataset */
  private static Record expectedMySQLRecord;

  /** Expected record for MongoDB dataset */
  private static Record expectedMongoDBRecord;

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_URL);
  }

  @Before
  @Override
  /**
   * Create component, store and application and start server
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    super.setUp();

    listColumnsPG = new HashMap<String, String>();
    listColumnsPG.put("int", "0");
    listColumnsPG.put("float", "1.1553268");
    listColumnsPG.put("double", "8.96095939005054");
    listColumnsPG.put("varchar", "0");
    listColumnsPG.put("varchar_id", "0");
    listColumnsPG.put("timestamp", "2011-04-21T14:01:13.047");
    listColumnsPG.put("date", "2011-04-21T00:00:00.000");
    listColumnsPG.put("smallint", "1");
    listColumnsPG.put("timestamp_with_time_zone", "2011-04-21T14:01:13.048");
    listColumnsPG.put("bigint", "97");
    listColumnsPG.put("numeric", "0.675227390775783");
    listColumnsPG.put("char10", "ceci est 8");
    listColumnsPG.put("serial", "1");
    listColumnsPG.put("bigserial", "1");
    listColumnsPG.put("time_without_time_zone", "14:01:13.047"); // lieu

    expectedPgRecord = getRecord(listColumnsPG);

    listColumnsMYSQL = new HashMap<String, String>();
    listColumnsMYSQL.put("field_varchar_id", "0");
    listColumnsMYSQL.put("field_tiny_int", "10");
    listColumnsMYSQL.put("field_small_int", "1");
    listColumnsMYSQL.put("field_medium_int", "10");
    listColumnsMYSQL.put("field_int", "10");
    listColumnsMYSQL.put("field_big_int", "97");
    // listColumnsMYSQL.put("field_float", "1.15533");
    // listColumnsMYSQL.put("field_double", "8.96095939005054");
    listColumnsMYSQL.put("field_decimal", "125.25360");
    listColumnsMYSQL.put("field_varchar", "varchar");
    listColumnsMYSQL.put("field_timestamp", "2011-04-27T11:23:36.000");
    listColumnsMYSQL.put("field_datetime", "2011-03-11T11:01:50.000");
    listColumnsMYSQL.put("field_date", "2010-04-01T00:00:00.000");
    listColumnsMYSQL.put("field_year", "2011-01-01T00:00:00.000");
    listColumnsMYSQL.put("field_time", "14:01:13.000");
    listColumnsMYSQL.put("field_char", "y");

    expectedMySQLRecord = getRecord(listColumnsMYSQL);

    listColumnsMongoDB = new HashMap<String, String>();
    listColumnsMongoDB.put("_id", "504d9d37c6edb2c032f3d5e5");
    listColumnsMongoDB.put("int", "0");
    listColumnsMongoDB.put("float", "1.1553268");
    listColumnsMongoDB.put("double", "8.9609593900505");
    listColumnsMongoDB.put("varchar", "0");
    listColumnsMongoDB.put("text", "ceci est un enregistrement 0");
    listColumnsMongoDB.put("date", "2011-04-21T00:00:00.000");
    listColumnsMongoDB.put("date_with_time", "2011-04-21T14:02:00.791");
    listColumnsMongoDB.put("char", "y");
    listColumnsMongoDB.put("bool", "true");

    expectedMongoDBRecord = getRecord(listColumnsMongoDB);

  }

  /**
   * Tests the primary keys access for postgresql dataset
   * 
   * @throws InterruptedException
   */
  @Test
  public void testPG() throws InterruptedException {
    docAPI.setActive(false);

    int i = 123654;
    for (Map.Entry<String, String> entry : listColumnsPG.entrySet()) {
      System.out.println("create with key = " + entry.getKey() + " id = " + i);
      // create the dataset
      createDatasetPG(new Integer(i).toString(), entry.getKey(), false);
      // query with GET
      queryDataset(urlAttachPostgreSQL);
      queryDataset(urlAttachPostgreSQL, entry.getValue(), expectedPgRecord);
      deleteDataset(new Integer(i).toString());
      i++;
    }
  }

  /**
   * Tests the primary keys access for mysql dataset
   * 
   * @throws InterruptedException
   */
  @Test
  public void testMysql() throws InterruptedException {
    docAPI.setActive(false);

    int i = 12365854;
    for (Map.Entry<String, String> entry : listColumnsMYSQL.entrySet()) {
      System.out.println("create with key = " + entry.getKey() + " id = " + i);
      // create the dataset
      createDatasetMysql(new Integer(i).toString(), entry.getKey(), false);
      // query with GET
      queryDataset(urlAttachMySQL);

      queryDataset(urlAttachMySQL, entry.getValue(), expectedMySQLRecord);
      deleteDataset(new Integer(i).toString());
      i++;
    }
  }

  /**
   * Tests the primary keys access for mysql dataset
   * 
   * @throws InterruptedException
   */
  // @Test
  public void testMongoDB() throws InterruptedException {
    docAPI.setActive(false);

    int i = 123658545;
    try {
      for (Map.Entry<String, String> entry : listColumnsMongoDB.entrySet()) {
        System.out.println("create with key = " + entry.getKey() + " id = " + i);
        // create the dataset
        createDatasetMongoDB(new Integer(i).toString(), entry.getKey(), false);
        // query with GET
        queryDataset(urlAttachMongoDB);

        queryDataset(urlAttachMongoDB, entry.getValue(), expectedMongoDBRecord);
        deleteDataset(new Integer(i).toString());
        i++;
      }
    }
    finally {
      deleteDataset(new Integer(i).toString());
    }
  }

  /**
   * Tests the datasetRequestUrl access for postgresql dataset
   * 
   * @throws InterruptedException
   * @throws UnsupportedEncodingException
   */
  @Test
  public void testDatasetRequestUrlPG() throws InterruptedException, UnsupportedEncodingException {
    docAPI.setActive(false);

    int i = 122;

    // create the dataset
    createDatasetPG(new Integer(i).toString(), "int", true);

    for (Map.Entry<String, String> entry : listColumnsPG.entrySet()) {
      // query with GET
      String params = "/records?limit=300&start=0&p[0]=RADIO|" + entry.getKey() + "|"
          + URLEncoder.encode(entry.getValue(), "UTF-8");
      queryDatasetRequestUrl(urlAttachPostgreSQL, params, expectedPgRecord, true);
    }
    deleteDataset(new Integer(i).toString());

  }

  /**
   * Tests the datasetRequestUrl access for mysql dataset
   * 
   * @throws InterruptedException
   * @throws UnsupportedEncodingException
   */
  @Test
  public void testDatasetRequestUrlMysql() throws InterruptedException, UnsupportedEncodingException {
    docAPI.setActive(false);

    int i = 1225;

    // create the dataset
    createDatasetMysql(new Integer(i).toString(), "field_int", true);

    for (Map.Entry<String, String> entry : listColumnsMYSQL.entrySet()) {
      // query with GET
      String params = "/records?limit=300&start=0&p[0]=RADIO|" + entry.getKey() + "|"
          + URLEncoder.encode(entry.getValue(), "UTF-8");
      queryDatasetRequestUrl(urlAttachMySQL, params, expectedMySQLRecord, true);
    }
    deleteDataset(new Integer(i).toString());

  }

  /**
   * Tests the datasetRequestUrl access for mysql dataset
   * 
   * @throws InterruptedException
   * @throws UnsupportedEncodingException
   */
  @Test
  public void testDatasetRequestUrlMongoDB() throws InterruptedException, UnsupportedEncodingException {
    docAPI.setActive(false);

    int i = 1226;

    try {
      // create the dataset
      createDatasetMongoDB(new Integer(i).toString(), "_id", true);

      for (Map.Entry<String, String> entry : listColumnsMongoDB.entrySet()) {
        System.out.println(" Key : " + entry.getKey());
        // query with GET
        String params = "/records?limit=300&start=0&p[0]=RADIO|" + entry.getKey() + "|"
            + URLEncoder.encode(entry.getValue(), "UTF-8");
        queryDatasetRequestUrl(urlAttachMongoDB, params, expectedMongoDBRecord, true);
      }
      deleteDataset(new Integer(i).toString());
    }
    finally {
      deleteDataset(new Integer(i).toString());
    }

  }

  /**
   * Create and activate a Dataset for Postgresql datasource. This dataset is created on the test.table_tests table
   * 
   * @param id
   *          the id of the dataset
   * @param primaryKey
   *          the primary key needed
   * @param withDatasetUrl
   *          true if datasetRequestUrl should be included, false otherwise
   * @return the DataSet created
   * @throws InterruptedException
   *           if something is wrong
   */
  private DataSet createDatasetPG(String id, String primaryKey, boolean withDatasetUrl) throws InterruptedException {
    DataSet item = CreateDatasetUtil.createDatasetTestPG(id, primaryKey, withDatasetUrl, urlAttachPostgreSQL);

    persistDataset(item);
    item.setDirty(false);

    changeStatus(item.getId(), "/start");

    return item;

  }

  /**
   * Create and activate a Dataset for MySQL datasource. This dataset is created on the TABLE_TESTS table
   * 
   * @param id
   *          the id of the dataset
   * @param primaryKey
   *          the primary key needed
   * @param withDatasetUrl
   *          true if datasetRequestUrl should be included, false otherwise
   * @return the DataSet created
   * @throws InterruptedException
   *           if something is wrong
   */
  private DataSet createDatasetMysql(String id, String primaryKey, boolean withDatasetUrl) throws InterruptedException {
    DataSet item = CreateDatasetUtil.createDatasetTestMySQL(id, primaryKey, withDatasetUrl, urlAttachMySQL);

    item.setDirty(false);

    persistDataset(item);

    changeStatus(item.getId(), "/start");

    return item;

  }

  /**
   * Create and activate a Dataset for MySQL datasource. This dataset is created on the TABLE_TESTS table
   * 
   * @param id
   *          the id of the dataset
   * @param primaryKey
   *          the primary key needed
   * @param withDatasetUrl
   *          true if datasetRequestUrl should be included, false otherwise
   * @return the DataSet created
   * @throws InterruptedException
   *           if something is wrong
   */
  private DataSet createDatasetMongoDB(String id, String primaryKey, boolean withDatasetUrl)
      throws InterruptedException {
    DataSet item = CreateDatasetUtil.createDatasetTestMongoDB(id, primaryKey, withDatasetUrl, urlAttachMongoDB);

    item.setDirty(false);

    persistDataset(item);

    changeStatus(item.getId(), "/start");

    return item;

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
  public Response getResponse(MediaType media, Representation representation, Class<?> dataClass) {
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
  public Response getResponse(MediaType media, Representation representation, Class<?> dataClass, boolean isArray) {
    return GetResponseUtils.getResponseDataset(media, representation, dataClass, isArray);
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
  public static Representation getRepresentation(DataSet item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<DataSet>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<DataSet> rep = new XstreamRepresentation<DataSet>(media, item);
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

  }

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
  public static Response getResponseRecord(MediaType media, Representation representation, Class<?> dataClass) {
    return getResponseRecord(media, representation, dataClass, false);
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
  public static Response getResponseRecord(MediaType media, Representation representation, Class<?> dataClass,
      boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("record", Record.class);
      xstream.alias("attributeValues", AttributeValue.class);

      xstream.registerConverter(new AttributeValueConverter());

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == Record.class) {
          xstream.aliasField("record", Response.class, "item");
        }

        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Record.class, "attributeValues", AttributeValue.class);
        }
      }
      xstream.aliasField("data", Response.class, "data");

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(getMediaTest())) {
        Response response = rep.getObject("response");
        // Response response = rep.getObject();

        return response;
      }
      else {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON is supported in tests");
        return null; // TODO complete test for XML, Object representation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

  // ------------------------------------------------------------
  // ARRAYLIST<RECORD> WRAPPING

  /**
   * REST API Response Representation wrapper for single or multiple items expexted
   * 
   * @param media
   *          MediaType expected
   * @param representation
   *          service response representation
   * @return ArrayList<Record>
   */
  public static ArrayList<Record> getRecords(MediaType media, Representation representation) {
    if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
      Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null;
    }

    XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
    xstream.autodetectAnnotations(false);

    if (media.isCompatible(MediaType.APPLICATION_XML)) {

      xstream.alias("records", ArrayList.class);
      xstream.alias("record", Record.class);
      xstream.alias("attributeValues", AttributeValue.class);

      xstream.registerConverter(new AttributeValueConverter());

      SitoolsXStreamRepresentation<ArrayList<Record>> rep = new SitoolsXStreamRepresentation<ArrayList<Record>>(
          representation);
      rep.setXstream(xstream);

      ArrayList<Record> response = rep.getObject("records");

      return response;

    }

    else if (media.isCompatible(MediaType.APPLICATION_JSON)) {

      try {
        // hand made JSON deserialization
        JsonFactory f = new JsonFactory();
        JsonParser jp = f.createJsonParser(representation.getStream());

        ArrayList<Record> recs = new ArrayList<Record>();

        jp.nextToken(); // will return JsonToken.START_OBJECT (verify?)
        while (jp.nextToken() != JsonToken.END_OBJECT) {
          String fieldname = jp.getCurrentName();
          if ("data".equals(fieldname)) { // contains an object
            jp.nextToken(); // move to value, or START_OBJECT/START_ARRAY
            while (jp.nextToken() != JsonToken.END_ARRAY) {
              Record record = new Record();
              // lets get a record, loop through the properties of the record
              while (jp.nextToken() != JsonToken.END_OBJECT) {
                jp.nextToken();
                // lets deal with attribute values
                AttributeValue attr;
                String namefield = jp.getCurrentName();
                if (!"uri".equals(namefield)) {
                  attr = new AttributeValue();
                  attr.setName(namefield);
                  attr.setValue(jp.getText());
                  record.getAttributeValues().add(attr);
                }
              }
              recs.add(record);
            }
          }
        }
        jp.close(); // ensure resources get cleaned up timely and properly
        return recs;
      }
      catch (JsonParseException e) {
        e.printStackTrace();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      return null;

    }
    else {
      Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null; // TODO complete test with ObjectRepresentation
    }
  }

}
