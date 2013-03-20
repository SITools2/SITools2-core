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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.api.DocAPI;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.filter.dto.FilterChainedModelDTO;
import fr.cnes.sitools.dataset.filter.dto.FilterModelDTO;
import fr.cnes.sitools.dataset.filter.model.FilterParameter;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.datasource.jdbc.model.Structure;
import fr.cnes.sitools.json.GraphTestCase;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.CreateDatasetUtil;

/**
 * 
 * Test DataSetApplication Rest API
 * 
 * @since UserStory : ADM DataSets, Sprint : 4
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class DataSetFilterTestCase extends AbstractDataSetManagerTestCase {

  /** Test title */
  protected static final String TITLE = "Dataset Filter API with JSON format";
  /** url attachment of the dataset with postgreSQL datasource */
  private static String urlAttachPostgreSQL = "/dataset/tests/pgDatasetFilterTest";

  /** url attachment of the dataset with MySQL datasource */
  private static String urlAttachMySQL = "/dataset/tests/mysqlDatasetFilterTest";

  /** url attachment of the dataset with MongoDB datasource */
  private static String urlAttachMongoDB = "/dataset/tests/mongoDBDatasetFilterTest";

  /** Expected record for Postgresql dataset */
  private static Record expectedPgRecord;

  /** Expected record for mysql dataset */
  private static Record expectedMySQLRecord;

  /** Expected record for MongoDB dataset */
  private static Record expectedMongoDBRecord;

  /** list of columns with primary key value for postgres datasource's dataset */
  private static HashMap<String, String> listColumnsPG;

  /** list of columns with primary key value for mysql datasource's dataset */
  private static HashMap<String, String> listColumnsMYSQL;

  /** list of columns with primary key value for mongoDB datasource's dataset */
  private static HashMap<String, String> listColumnsMongoDB;

  /** Dataset on MysqlDataSource */
  private DataSet datasetMysql;

  /** Dataset on postgresql datasource */
  private DataSet datasetPg;

  /** Dataset on MongoDB */
  private DataSet datasetMongoDB;

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrlFilter() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_URL) + "/%s"
        + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_FILTERS_URL);
  }

  static {
    setMediaTest(MediaType.APPLICATION_JSON);

    docAPI = new DocAPI(GraphTestCase.class, TITLE);
    docAPI.setActive(true);
    docAPI.setMediaTest(MediaType.APPLICATION_JSON);

  }

  @Before
  @Override
  /**
   * Create component, store and application and start server
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    super.setUp();
    datasetMysql = createDatasetMysql("dsMysql");
    datasetPg = createDatasetPG("dsPg");
    datasetMongoDB = createDatasetMongoDB("dsMongoDB");

    listColumnsPG = new HashMap<String, String>();
    listColumnsPG.put("int", "0");
    listColumnsPG.put("float", "1.1553268");
    listColumnsPG.put("double", "8.96095939005054");
    listColumnsPG.put("varchar", "0");
    listColumnsPG.put("varchar_id", "0");
    listColumnsPG.put("timestamp", "2011-04-21 14:01:13.047791");
    listColumnsPG.put("date", "2011-04-21");
    listColumnsPG.put("time", "14:01:13.04697+02");
    listColumnsPG.put("smallint", "1");
    listColumnsPG.put("timestamp_with_time_zone", "2011-04-21 14:01:13.047794+02");
    listColumnsPG.put("bigint", "97");
    listColumnsPG.put("numeric", "0.675227390775783");
    listColumnsPG.put("char10", "ceci est 8");
    listColumnsPG.put("serial", "1");
    listColumnsPG.put("bigserial", "1");
    listColumnsPG.put("time_without_time_zone", "14:01:13.04697"); // lieu

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
    listColumnsMYSQL.put("field_timestamp", "2011-04-27 11:23:36.0");
    listColumnsMYSQL.put("field_datetime", "2011-03-11 11:01:50.0");
    listColumnsMYSQL.put("field_date", "2010-04-01");
    listColumnsMYSQL.put("field_year", "2011-01-01");
    listColumnsMYSQL.put("field_time", "14:01:13");
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

    docAPI.setActive(false);

  }

  @After
  @Override
  /**
   * Stop server
   * @throws java.lang.Exception
   */
  public void tearDown() throws Exception {
    super.tearDown();
    deleteDataset("dsMysql");
    deleteDataset("dsPg");
    deleteDataset("dsMongoDB");

  }

  /**
   * Test DataSet Metadatas. TODO SPRINT 6
   */
  @Test
  public void testDataSetAdminQuerying() {
    // ajout d'un predicat string

    // ajout d'un predicat numeric

    // ajout d'un predicat String et d'un predicat numeric
  }

  /**
   * Test that the default filter have been attached to the dataset
   */
  @Test
  public void testDefaultFilter() {
    // PG
    testDefaultFilter(datasetPg.getId());
    // MYSQL
    testDefaultFilter(datasetMysql.getId());
    // MYSQL
    testDefaultFilter(datasetMongoDB.getId());
  }

  public void testDefaultFilter(String datasetId) {
    String url = String.format(getBaseUrlFilter(), datasetId);
    ClientResource cr = new ClientResource(url);
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, FilterChainedModelDTO.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    FilterChainedModelDTO filterOut = (FilterChainedModelDTO) response.getItem();
    assertEquals(datasetId, filterOut.getId());
    assertNotSame(0, filterOut.getFilters().size());
    RIAPUtils.exhaust(result);
  }

  /**
   * ######################################################### MYSQL
   * #########################################################
   */

  /**
   * Test DataSet Metadatas. TODO SPRINT 6
   * http://localhost:8182/DatasetSelenium/records?_dc=1295443762784&filter%5B0%5D%
   * 5BcolumnAlias%5D=DATASET2&filter%5B0%5D%5Bdata%5D%5Btype%5D=string&filter%5B0%5D%5Bdata%5D%5Bvalue%5D=%22%
   * 25er%25%22&filter%5B0%5D%5Bdata%5D%5Bcomparison%5D=LIKE&start=0&limit=300
   * 
   * @throws UnsupportedEncodingException
   */
  @Test
  public void testDataSetGridQueryingFilterString() throws UnsupportedEncodingException {
    // un test avec un filtre string
    // dataset = createObject(DATASET_ID);
    // persistDataset(dataset);

    String params = "/records?filter[0][columnAlias]=field_varchar_id&filter[0][data][type]=string&filter[0][data][comparison]=LIKE"
        + "&filter[0][data][value]=0&start=0&limit=300&media=json";

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("filter[i].columnAlias", "column filtered");
      parameters.put("filter[i].data.type", "filter type");
      parameters.put("filter[i].data.comparison", "operator : LIKE or EQ");
      parameters.put("filter[i].data.value", "value used for the comparison");
      parameters.put("start", "position of the first element to send");
      parameters.put("limit", "Number of the element to send");
      parameters.put("media", "format asked (JSON OR XML");
      retrieveDocAPI(getHostUrl() + urlAttachMySQL + params, parameters, datasetMysql.getSitoolsAttachementForUsers());
    }
    else {
      queryDatasetRequestUrl(urlAttachMySQL, params, expectedMySQLRecord, false);
    }
  }

  /**
   * Test dataset for querying by filter numeric with an operator >
   * 
   */
  @Test
  public void testDataSetGridQueryingFilterNumericGt() {
    String params = "/records?limit=300&start=0&filter[0][columnAlias]=field_int&filter[0][data][type]=numeric&filter[0][data][comparison]=gte"
        + "&filter[0][data][value]=1&media=json";

    queryDatasetRequestUrl(urlAttachMySQL, params, expectedMySQLRecord, false);
  }

/**
   * Test dataset for querying by filter numeric with an operator "<"
   */
  @Test
  public void testDataSetGridQueryingFilterNumericLt() {
    String params = "/records?limit=300&start=0&filter[0][columnAlias]=field_int&filter[0][data][type]=numeric&filter[0][data][comparison]=lte"
        + "&filter[0][data][value]=0&media=json";

    queryDatasetRequestUrl(urlAttachMySQL, params, expectedMySQLRecord, false);
  }

  /**
   * Test dataset for querying by multiple filters
   * 
   */
  @Test
  public void testDataSetGridQueryingFilterNumericMultiple() {
    // un test avec 1 filtres String + 1 numeric au choix
    String params = "/records?limit=300&start=0&filter[0][columnAlias]=field_int&filter[0][data][type]=numeric&filter[0][data][comparison]=gte&filter[0][data][value]=0"
        + "&filter[1][columnAlias]=field_varchar&filter[1][data][comparison]=LIKE&filter[1][data][type]=string&filter[1][data][value]=varchar&media=json";

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("filter[i].columnAlias", "column filtered");
      parameters.put("filter[i].data.type", "filter type");
      parameters.put("filter[i].data.comparison", "operator : LT, GT or EQ");
      parameters.put("filter[i].data.value", "value used for the comparison");
      parameters.put("start", "position of the first element to send");
      parameters.put("limit", "Number of the element to send");
      parameters.put("media", "format asked (JSON OR XML");
      retrieveDocAPI(params, parameters, datasetMysql.getSitoolsAttachementForUsers());
    }
    else {

      queryDatasetRequestUrl(urlAttachMySQL, params, expectedMySQLRecord, false);
    }
  }

  /**
   * Test dataset for querying by multiple filters
   * 
   * @throws UnsupportedEncodingException
   */
  @Test
  public void getDocumentationAPI() throws UnsupportedEncodingException {
    docAPI.setActive(true);
    docAPI.appendChapter(TITLE);

    docAPI.appendSubChapter("Dataset string filter", "stringFilter");
    testDataSetGridQueryingFilterString();
    docAPI.appendSubChapter("Dataset numeric filter", "numericFilter");
    testDataSetGridQueryingFilterNumericMultiple();

    docAPI.close();
  }

  /**
   * ######################################################### MONGODB
   * #########################################################
   */

  /**
   * Test DataSet Metadatas. TODO SPRINT 6
   * http://localhost:8182/DatasetSelenium/records?_dc=1295443762784&filter%5B0%5D%
   * 5BcolumnAlias%5D=DATASET2&filter%5B0%5D%5Bdata%5D%5Btype%5D=string&filter%5B0%5D%5Bdata%5D%5Bvalue%5D=%22%
   * 25er%25%22&filter%5B0%5D%5Bdata%5D%5Bcomparison%5D=LIKE&start=0&limit=300
   * 
   * @throws UnsupportedEncodingException
   */
  @Test
  public void testDataSetGridQueryingFilterStringMongoDB() throws UnsupportedEncodingException {
    // un test avec un filtre string
    // dataset = createObject(DATASET_ID);
    // persistDataset(dataset);

    String params = "/records?filter[0][columnAlias]=varchar&filter[0][data][type]=string&filter[0][data][comparison]=LIKE"
        + "&filter[0][data][value]=0&start=0&limit=300&media=json";

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("filter[i].columnAlias", "column filtered");
      parameters.put("filter[i].data.type", "filter type");
      parameters.put("filter[i].data.comparison", "operator : LIKE or EQ");
      parameters.put("filter[i].data.value", "value used for the comparison");
      parameters.put("start", "position of the first element to send");
      parameters.put("limit", "Number of the element to send");
      parameters.put("media", "format asked (JSON OR XML");
      retrieveDocAPI(params, parameters, datasetMongoDB.getSitoolsAttachementForUsers());
    }
    else {
      queryDatasetRequestUrl(urlAttachMongoDB, params, expectedMongoDBRecord, true);
    }
  }

  /**
   * Test dataset for querying by filter numeric with an operator >
   * 
   */
  @Test
  public void testDataSetGridQueryingFilterNumericGtMongoDB() {
    String params = "/records?limit=300&start=0&filter[0][columnAlias]=double&filter[0][data][type]=numeric&filter[0][data][comparison]=gt"
        + "&filter[0][data][value]=6&media=json";

    queryDatasetRequestUrl(urlAttachMongoDB, params, expectedMongoDBRecord, true);
  }

/**
   * Test dataset for querying by filter numeric with an operator "<"
   */
  @Test
  public void testDataSetGridQueryingFilterNumericLtMongoDB() {
    String params = "/records?limit=300&start=0&filter[0][columnAlias]=float&filter[0][data][type]=numeric&filter[0][data][comparison]=lt"
        + "&filter[0][data][value]=1.2&media=json";

    queryDatasetRequestUrl(urlAttachMongoDB, params, expectedMongoDBRecord, true);
  }

  /**
   * Test dataset for querying by multiple filters
   * 
   */
  @Test
  public void testDataSetGridQueryingFilterNumericMultipleMongoDB() {
    // un test avec 1 filtres String + 1 numeric au choix
    String params = "/records?limit=300&start=0&filter[0][columnAlias]=int&filter[0][data][type]=numeric&filter[0][data][comparison]=lt&filter[0][data][value]=1"
        + "&filter[1][columnAlias]=text&filter[1][data][comparison]=LIKE&filter[1][data][type]=string&filter[1][data][value]=ceci est un enregistrement 0&media=json";

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("filter[i].columnAlias", "column filtered");
      parameters.put("filter[i].data.type", "filter type");
      parameters.put("filter[i].data.comparison", "operator : LT, GT or EQ");
      parameters.put("filter[i].data.value", "value used for the comparison");
      parameters.put("start", "position of the first element to send");
      parameters.put("limit", "Number of the element to send");
      parameters.put("media", "format asked (JSON OR XML");
      retrieveDocAPI(params, parameters, datasetMongoDB.getSitoolsAttachementForUsers());
    }
    else {
      queryDatasetRequestUrl(urlAttachMongoDB, params, expectedMongoDBRecord, true);
    }
  }

  /**
   * Test dataset for querying by multiple filters
   * 
   * @throws UnsupportedEncodingException
   */
  @Test
  public void getDocumentationAPIMongoDB() throws UnsupportedEncodingException {
    docAPI.setActive(true);
    docAPI.appendChapter(TITLE);

    docAPI.appendSubChapter("Dataset string filter", "stringFilter");
    testDataSetGridQueryingFilterStringMongoDB();
    docAPI.appendSubChapter("Dataset numeric filter", "numericFilter");
    testDataSetGridQueryingFilterNumericMultipleMongoDB();

    docAPI.close();
  }

  /**
   * ######################################################### POSTGRESQL
   * #########################################################
   */

  /**
   * Test DataSet Metadatas. TODO SPRINT 6
   * http://localhost:8182/DatasetSelenium/records?_dc=1295443762784&filter%5B0%5D%
   * 5BcolumnAlias%5D=DATASET2&filter%5B0%5D%5Bdata%5D%5Btype%5D=string&filter%5B0%5D%5Bdata%5D%5Bvalue%5D=%22%
   * 25er%25%22&filter%5B0%5D%5Bdata%5D%5Bcomparison%5D=LIKE&start=0&limit=300
   * 
   * @throws UnsupportedEncodingException
   */
  @Test
  public void testDataSetGridQueryingFilterStringPG() throws UnsupportedEncodingException {
    // un test avec un filtre string
    // dataset = createObject(DATASET_ID);
    // persistDataset(dataset);

    String params = "/records?filter[0][columnAlias]=varchar_id&filter[0][data][type]=string&filter[0][data][comparison]=LIKE"
        + "&filter[0][data][value]=0&start=0&limit=300&media=json";

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("filter[i].columnAlias", "column filtered");
      parameters.put("filter[i].data.type", "filter type");
      parameters.put("filter[i].data.comparison", "operator : LIKE or EQ");
      parameters.put("filter[i].data.value", "value used for the comparison");
      parameters.put("start", "position of the first element to send");
      parameters.put("limit", "Number of the element to send");
      parameters.put("media", "format asked (JSON OR XML");
      retrieveDocAPI(params, parameters, datasetPg.getSitoolsAttachementForUsers());
    }
    else {
      queryDatasetRequestUrl(urlAttachPostgreSQL, params, expectedPgRecord, false);
    }
  }

  /**
   * Test dataset for querying by filter numeric with an operator >
   * 
   */
  @Test
  public void testDataSetGridQueryingFilterNumericGtPG() {
    String params = "/records?limit=300&start=0&filter[0][columnAlias]=bigint&filter[0][data][type]=numeric&filter[0][data][comparison]=gt"
        + "&filter[0][data][value]=22&media=json";

    queryDatasetRequestUrl(urlAttachPostgreSQL, params, expectedPgRecord, false);
  }

/**
   * Test dataset for querying by filter numeric with an operator "<"
   */
  @Test
  public void testDataSetGridQueryingFilterNumericLtPG() {
    String params = "/records?limit=300&start=0&filter[0][columnAlias]=int&filter[0][data][type]=numeric&filter[0][data][comparison]=lt"
        + "&filter[0][data][value]=1&media=json";

    queryDatasetRequestUrl(urlAttachPostgreSQL, params, expectedPgRecord, false);
  }

  /**
   * Test dataset for querying by multiple filters
   * 
   */
  @Test
  public void testDataSetGridQueryingFilterNumericMultiplePG() {
    // un test avec 1 filtres String + 1 numeric au choix
    String params = "/records?limit=300&start=0&filter[0][columnAlias]=int&filter[0][data][type]=numeric&filter[0][data][comparison]=lt&filter[0][data][value]=1"
        + "&filter[1][columnAlias]=char10&filter[1][data][comparison]=LIKE&filter[1][data][type]=string&filter[1][data][value]=ceci est 8&media=json";

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("filter[i].columnAlias", "column filtered");
      parameters.put("filter[i].data.type", "filter type");
      parameters.put("filter[i].data.comparison", "operator : LT, GT or EQ");
      parameters.put("filter[i].data.value", "value used for the comparison");
      parameters.put("start", "position of the first element to send");
      parameters.put("limit", "Number of the element to send");
      parameters.put("media", "format asked (JSON OR XML");
      retrieveDocAPI(params, parameters, datasetPg.getSitoolsAttachementForUsers());
    }
    else {
      queryDatasetRequestUrl(urlAttachPostgreSQL, params, expectedPgRecord, false);
    }
  }

  /**
   * Test dataset for querying by multiple filters
   * 
   * @throws UnsupportedEncodingException
   */
  @Test
  public void getDocumentationAPIPG() throws UnsupportedEncodingException {
    docAPI.setActive(true);
    docAPI.appendChapter(TITLE);

    docAPI.appendSubChapter("Dataset string filter", "stringFilter");
    testDataSetGridQueryingFilterStringPG();
    docAPI.appendSubChapter("Dataset numeric filter", "numericFilter");
    testDataSetGridQueryingFilterNumericMultiplePG();

    docAPI.close();
  }

  /**
   * Create and activate a Dataset for Postgresql datasource. This dataset is created on the test.table_tests table
   * 
   * @param id
   *          the dataset ID
   * @return the DataSet created
   * @throws InterruptedException
   */
  private DataSet createDatasetPG(String id) throws InterruptedException {

    DataSet item = CreateDatasetUtil.createDatasetTestPG(id, "int", false, urlAttachPostgreSQL);

    persistDataset(item);
    item.setDirty(false);

    changeStatus(item.getId(), "/start");

    return item;

  }

  /**
   * Create and activate a Dataset for MySQL datasource. This dataset is created on the TABLE_TESTS table
   * 
   * @return the DataSet created
   * @throws InterruptedException
   */
  private DataSet createDatasetMysql(String id) throws InterruptedException {

    DataSet item = CreateDatasetUtil.createDatasetTestMySQL(id, "field_varchar_id", false, urlAttachMySQL);

    persistDataset(item);

    changeStatus(item.getId(), "/start");

    return item;

  }

  /**
   * Create and activate a Dataset for Postgresql datasource. This dataset is created on the test.table_tests table
   * 
   * @param id
   *          the dataset ID
   * @return the DataSet created
   * @throws InterruptedException
   */
  private DataSet createDatasetMongoDB(String id) throws InterruptedException {

    DataSet item = CreateDatasetUtil.createDatasetTestMongoDB(id, "int", false, urlAttachMongoDB);

    persistDataset(item);
    item.setDirty(false);

    changeStatus(item.getId(), "/start");

    return item;

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
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("dataset", DataSet.class);
      xstream.alias("column", Column.class);
      xstream.alias("structure", Structure.class);

      if (dataClass == FilterChainedModelDTO.class) {
        xstream.alias("filterChainedModel", FilterChainedModelDTO.class);
        xstream.alias("filterModel", FilterModelDTO.class);
      }
      xstream.alias("filterParameter", FilterParameter.class);

      xstream.addImplicitCollection(Response.class, "data", dataClass);

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == DataSet.class) {
          xstream.aliasField("dataset", Response.class, "item");
        }

        if (media.isCompatible(MediaType.APPLICATION_JSON)) {

          if (dataClass == FilterChainedModelDTO.class) {
            xstream.addImplicitCollection(FilterChainedModelDTO.class, "filters", FilterModelDTO.class);
            xstream.addImplicitCollection(FilterModelDTO.class, "parameters", FilterParameter.class);
          }
          if (dataClass == DataSet.class) {
            xstream.addImplicitCollection(DataSet.class, "columnModel", "columnModel", Column.class);
            xstream.addImplicitCollection(DataSet.class, "structures", "structures", Structure.class);
            xstream.addImplicitCollection(DataSet.class, "predicat", "predicat", Predicat.class);
          }
        }
        if (dataClass == FilterChainedModelDTO.class) {
          xstream.aliasField("filterChainedModel", Response.class, "item");
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
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON is supported in tests");
        return null; // TODO complete test for XML, Object representation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
      try {
        representation.getText();
      }
      catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

}
