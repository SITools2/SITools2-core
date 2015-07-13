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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.converter.dto.ConverterModelDTO;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameterType;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test case for dataset exploration API
 * 
 * @author m.marseille (AKKA Technologies)
 * 
 */
public abstract class AbstractDatasetExplorerTestCase extends AbstractDataSetManagerTestCase {

  /** Dataset ID for test */
  protected static final String DATASET_URL = "/fuse";

  /** Dataset ID for test */
  protected static final String DATASET_URL_MONGODB = "/mongo_fuse";

  /**
   * The sql injection string
   */
  private String sqlInjectionStr = "' OR '1' = '1";
  /**
   * The sql delete injection string
   */
  private String sqlDeleteInjectionStr = "'; DELETE from sitools.\"USERS\";--";

  /**
   * Converter classname
   */
  private String converterClassname = "fr.cnes.sitools.converter.tests.ConverterValidatorTest";

  /**
   * Get the base URL for the sql dataset
   * 
   * @return the base url for the sql dataset
   */
  public String getBaseUrlDataset() {
    return getHostUrl() + DATASET_URL;
  }

  /**
   * Get the base url for the mongodb dataset
   * 
   * @return the base url for the mongodb dataset
   */
  public String getBaseUrlDatasetMongoDB() {
    return getHostUrl() + DATASET_URL_MONGODB;
  }

  /**
   * Start the server
   */
  @Before
  public void setUp() {
    try {
      super.setUp();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Stop the server after tests
   */
  @After
  public void tearDown() {
    try {
      super.tearDown();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Test Dataset exploration API
   */
  // @Test
  public void testSQL() {
    docAPI.setActive(false);
    DataSet ds = getDataset(getBaseUrlDataset());
    String url = DATASET_URL;
    getCount(url, 4689);
    getCountWithCountResource(4689, url);

    getRecordsWithCount(url);
    getRecordsWithNoCount(url);
    getRecord(url, "A0010101A001010117");
    getRecordTrySQLInjection(url, sqlInjectionStr);
    getRecordTrySQLInjection(url, sqlDeleteInjectionStr);
    getRecordsDistinct(url, "aperture", 4);
    getRecordsDistinctUnknownColumn(url, "testtest");
    getRecordsUnknownColumn(url, "aperture", "testtest");

    getRecordsWithConverterExecution(ds, "ra_targ");

    getMonitoring();
  }

  /**
   * Test Dataset exploration API with ranges parameters
   */
  @Test
  public void testRangeSQL() {
    docAPI.setActive(false);
    String url = DATASET_URL;
    getCountWithCountResourceWithRange(url);
    getCountWithCountResourceWithRangeOutofMaxRecords(url);
    getRecordsWithRangeOfIndex(url);
    getRecordsWithRangeOfIndexStartLimit(url);
    getRecordsWithRangeOfIndexOutOfMaxRecords(url);
    getRecordsWithRangeOfIndexNegative(url);
    getRecordsWithRangeOfIndexStartSuperiorToEnd(url);

  }

  /**
   * Test Dataset exploration API with ranges parameters
   */
  @Test
  public void testRangeMongoDB() {
    docAPI.setActive(false);
    String url = DATASET_URL_MONGODB;
    getCountWithCountResourceWithRange(url);
    getCountWithCountResourceWithRangeOutofMaxRecordsMongoDB(url);
    getRecordsWithRangeOfIndex(url);
    getRecordsWithRangeOfIndexStartLimit(url);
    getRecordsWithRangeOfIndexOutOfMaxRecordsMongoDB(url);
    getRecordsWithRangeOfIndexNegative(url);
    getRecordsWithRangeOfIndexStartSuperiorToEnd(url);
  }

  /**
   * Test Dataset exploration API
   */
  @Test
  public void testMongoDB() {
    docAPI.setActive(false);
    DataSet ds = getDataset(getBaseUrlDatasetMongoDB());
    String url = DATASET_URL_MONGODB;
    getCount(url, 813);
    getCountWithCountResource(813, url);
    getCountWithCountResourceWithRange(url);
    getRecordsWithCount(url);
    getRecordsWithNoCount(url);
    getRecord(url, "A001");
    getRecordTrySQLInjection(url, sqlInjectionStr);
    getRecordTrySQLInjection(url, sqlDeleteInjectionStr);
    getRecordsWithRangeOfIndex(url);
    getRecordsWithRangeOfIndexStartLimit(url);
    getRecordsDistinct(url, "cycle", 10);
    getRecordsDistinctUnknownColumn(url, "testtest");
    getRecordsUnknownColumn(url, "fname", "testtest");

    getRecordsWithConverterExecution(ds, "cycle");

    getMonitoring();
  }

  /**
   * Test CRUD Graph with JSon format exchanges.
   */
  @Test
  public void testCRUD2docAPI() {
    docAPI.setActive(false);
    DataSet ds = getDataset(getBaseUrlDataset());
    String url = DATASET_URL;
    docAPI.setActive(true);
    docAPI.appendChapter("Working with dataset API");

    docAPI.appendSubChapter("Obtaining the total number of records in a dataset, not recommended method ", "counting");

    getCount(url, 4689);
    docAPI.appendSubChapter("Obtaining the total nunmber of records in a dataset, recommended method",
        "counting_recommended");
    getCountWithCountResource(4689, url);

    docAPI.appendSubChapter("Obtaining a defined set of records, from 2 to 6.", "retrieving");
    docAPI.appendSection("Retrieving records from #2 to #6, hence 5 records.");
    docAPI.appendComment("NB the first records is #0");

    getRecordsWithCount(url);

    docAPI.appendSection("Counting the total number of records is disabled ");
    docAPI.appendComment("This option is useful with large datasets by sparing time.");

    getRecordsWithNoCount(url);

    docAPI.appendSubChapter("Get a particular record", "record");
    getRecord(url, "A0010101A001010117");

    docAPI.appendSubChapter("Query a dataset with a SQL injection try", "SqlInject");
    docAPI.appendSection("Query a dataset with a SQL injection try ");
    getRecordTrySQLInjection(url, sqlInjectionStr);

    docAPI.appendSubChapter("Query record with a range of query", "Range");
    getRecordsWithRangeOfIndex(url);

    docAPI.appendSubChapter("Query record with a range of query with start and limit parameter", "RangeLimitStart");
    getRecordsWithRangeOfIndexStartLimit(url);

    docAPI.appendSubChapter(
        "Obtaining the nunmber of records in a request on a dataset with ranges parameters, recommended method",
        "counting_recommended_ranges");

    getCountWithCountResourceWithRange(url);

    docAPI.close();
  }

  /**
   * Retrieve the dataset object
   * 
   * @param url
   *          the URL of the dataset
   * 
   * @return the dataset tested
   */
  private DataSet getDataset(String url) {
    DataSet ds = null;
    ClientResource cr = new ClientResource(url);
    Representation rep = cr.get(getMediaTest());
    Response response = getResponse(getMediaTest(), rep, DataSet.class, false);
    assertNotNull(response);
    assertTrue(response.getSuccess());
    ds = (DataSet) response.getItem();
    return ds;
  }

  /**
   * Test the limit=0 parameter to get the number of records
   * 
   * @param urlAttachDataset
   *          the url of the dataset
   * @param expectedCount
   *          the expected count number of records
   */
  private void getCount(String urlAttachDataset, int expectedCount) {
    String uri = getHostUrl() + urlAttachDataset;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("limit", "0");
      this.retrieveDocAPI(uri + "/records?limit=0", "", parameters, String.format(uri, "/records?limit=0"));
    }
    else {
      ClientResource cr = new ClientResource(uri + "/records");
      cr.getRequest().getResourceRef().addQueryParameter("limit", "0");
      docAPI.appendRequest(Method.GET, cr);
      Representation rep = cr.get(getMediaTest());
      assertTrue(cr.getStatus().isSuccess());
      assertNotNull(rep);
      Response response = getResponseRecord(getMediaTest(), rep, Record.class);
      assertNotNull(response);
      assertEquals(Integer.valueOf(expectedCount), response.getTotal());

      RIAPUtils.exhaust(rep);
    }
  }

  /**
   * Test the limit=0 parameter to get the number of records
   * 
   * @param expectedCount
   *          the expected number of records
   * @param urlAttachDataset
   *          the url attachment of the dataset
   */
  private void getCountWithCountResource(int expectedCount, String urlAttachDataset) {
    String uri = getHostUrl() + urlAttachDataset;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      this.retrieveDocAPI(uri + "/count", "", parameters, String.format(urlAttachDataset, "/count"));
    }
    else {
      ClientResource cr = new ClientResource(uri + "/count");
      docAPI.appendRequest(Method.GET, cr);
      Representation rep = cr.get(getMediaTest());
      assertTrue(cr.getStatus().isSuccess());
      assertNotNull(rep);
      Response response = getResponseRecord(getMediaTest(), rep, Record.class);
      assertNotNull(response);
      assertEquals(Integer.valueOf(expectedCount), response.getTotal());

      RIAPUtils.exhaust(rep);
    }
  }

  /**
   * Test the limit=0 parameter to get the number of records
   * 
   * @param urlAttachDataset
   *          the url attachment of the dataset
   */
  private void getCountWithCountResourceWithRange(String urlAttachDataset) {
    getCountResourceWithRange(urlAttachDataset, "[[0,150]]", 151);

  }

  /**
   * Test the limit=0 parameter to get the number of records
   * 
   * @param urlAttachDataset
   *          the url attachment of the dataset
   */
  private void getCountWithCountResourceWithRangeOutofMaxRecords(String urlAttachDataset) {
    getCountResourceWithRange(urlAttachDataset, "[[0,150], [4680, 6000]]", 151 + 9);
  }

  /**
   * Test the limit=0 parameter to get the number of records
   * 
   * @param urlAttachDataset
   *          the url attachment of the dataset
   */
  private void getCountWithCountResourceWithRangeOutofMaxRecordsMongoDB(String urlAttachDataset) {
    getCountResourceWithRange(urlAttachDataset, "[[0,150], [800, 1000]]", 151 + 13);
  }

  private void getCountResourceWithRange(String urlAttachDataset, String ranges, int expectedCount) {
    String url = getHostUrl() + urlAttachDataset + "/count?ranges=" + ranges;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("ranges", "The list of range to select");
      this.retrieveDocAPI(url, "", parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(url);
      docAPI.appendRequest(Method.GET, cr);
      Representation rep = cr.get(getMediaTest());
      assertTrue(cr.getStatus().isSuccess());
      assertNotNull(rep);
      Response response = getResponseRecord(getMediaTest(), rep, Record.class);
      assertNotNull(response);
      assertEquals(Integer.valueOf(expectedCount), response.getTotal());

      RIAPUtils.exhaust(rep);
    }
  }

  /**
   * Get a set of records with a start and a limit
   * 
   * @param urlAttachDataset
   *          the url attachment of the dataset
   */
  private void getRecordsWithCount(String urlAttachDataset) {
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("start", "start record number");
      parameters.put("limit", "number of records to send, including the starting one");
      String uri = getHostUrl() + urlAttachDataset;
      this.retrieveDocAPI(uri + "/records?start=2&limit=5", "", parameters,
          String.format(uri, "/records?start=2&limit=5"));
    }
    else {
      queryDatasetRequestUrl(urlAttachDataset + "/records", "?start=2&limit=5", 5);

    }
  }

  /**
   * Get a set of records without counting
   * 
   * @param urlAttachDataset
   *          the url attachment of the dataset
   */
  private void getRecordsWithNoCount(String urlAttachDataset) {
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("start", "start record number");
      parameters.put("limit", "number of records to send, including the starting one");
      parameters.put("nocount", "true indicates that counting before request is disabled");
      String uri = getHostUrl() + urlAttachDataset;
      this.retrieveDocAPI(uri + "/records?start=2&limit=5&nocount=true", "", parameters,
          String.format(uri, "/records?start=2&limit=5&nocount=true"));
    }
    else {
      queryDatasetRequestUrl(urlAttachDataset + "/records", "?start=2&limit=5&nocount=true", 5);

    }
  }

  /**
   * Get a single record by ID
   * 
   * @param urlAttachDataset
   *          the url attachment of the dataset
   * @param recordId
   *          the ID of the record
   */
  private void getRecord(String urlAttachDataset, String recordId) {
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("recordId", "The id of the record");
      String uri = getHostUrl() + urlAttachDataset;
      this.retrieveDocAPI(uri + "/records/" + recordId, "", parameters, String.format(uri, "/records/%recordId%"));
    }
    else {
      queryDataset(urlAttachDataset, recordId, null);
    }

  }

  /**
   * Get a single record by ID, but try to inject SQL string
   * 
   * @param urlAttachDataset
   *          the url attachment of the dataset
   * @param sqlInjectionStr
   *          the request to execute
   */
  private void getRecordTrySQLInjection(String urlAttachDataset, String sqlInjectionStr) {
    String uri = getHostUrl() + urlAttachDataset;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      this.retrieveDocAPI(uri + "/records/" + sqlInjectionStr, "", parameters,
          String.format(uri, "/records/" + sqlInjectionStr));
    }
    else {
      ClientResource cr = new ClientResource(uri + "/records/" + sqlInjectionStr);
      Representation rep = cr.get(getMediaTest());
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponseRecord(getMediaTest(), rep, Response.class, false);
      assertNotNull(response);
      assertFalse(response.getSuccess());
      RIAPUtils.exhaust(rep);
    }

  }

  /**
   * Get records with ranges of indexes
   * 
   * @param urlAttachDataset
   *          the url attachment of the dataset
   */
  private void getRecordsWithRangeOfIndex(String urlAttachDataset) {
    getRecordsWithRange(urlAttachDataset, "[[2,5],[1,1],[6,8]]", 8);
  }

  /**
   * Get records with ranges of indexes
   * 
   * @param urlAttachDataset
   *          the url attachment of the dataset
   */
  private void getRecordsWithRangeOfIndexStartLimit(String urlAttachDataset) {
    getRecordsWithRange(urlAttachDataset, "[[2,5],[1,1],[6,8]]&start=0&limit=4", 5);
  }

  /**
   * Get records with ranges of indexes
   * 
   * @param urlAttachDataset
   *          the url attachment of the dataset
   */
  private void getRecordsWithRangeOfIndexOutOfMaxRecords(String urlAttachDataset) {
    getRecordsWithRange(urlAttachDataset, "[[0,9], [4680, 6000]]", 19);
  }

  /**
   * Get records with ranges of indexes
   * 
   * @param urlAttachDataset
   *          the url attachment of the dataset
   */
  private void getRecordsWithRangeOfIndexNegative(String urlAttachDataset) {
    getRecordsWithRange(urlAttachDataset, "[[0,9], [-1, 50], [-10,-5]]", 10);
  }

  /**
   * Get records with ranges of indexes
   * 
   * @param urlAttachDataset
   *          the url attachment of the dataset
   */
  private void getRecordsWithRangeOfIndexStartSuperiorToEnd(String urlAttachDataset) {
    getRecordsWithRange(urlAttachDataset, "[[0,9], [10, 0]]", 10);
  }

  /**
   * Get records with ranges of indexes
   * 
   * @param urlAttachDataset
   *          the url attachment of the dataset
   */
  private void getRecordsWithRangeOfIndexInvalidRange(String urlAttachDataset) {
    getRecordsWithRange(urlAttachDataset, "[[-10,-1]]", 0);
  }

  /**
   * Get records with ranges of indexes
   * 
   * @param urlAttachDataset
   *          the url attachment of the dataset
   */
  private void getRecordsWithRangeOfIndexOutOfMaxRecordsMongoDB(String urlAttachDataset) {
    getRecordsWithRange(urlAttachDataset, "[[0,9], [800, 900]]", 10 + 13);
  }

  private void getRecordsWithRange(String urlAttachDataset, String ranges, int nbrecordsExpected) {
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("ranges", "Array of ranges to query");
      String uri = getHostUrl() + urlAttachDataset;
      this.retrieveDocAPI(uri + "/records?ranges=" + ranges, "", parameters,
          String.format(uri, "/records?ranges=" + ranges));
    }
    else {
      queryDatasetRequestUrl(urlAttachDataset + "/records", "?ranges=" + ranges, nbrecordsExpected);

    }
  }

  /**
   * Test a distinct request
   * 
   * @param urlAttachDataset
   *          the url attachment of the dataset
   * @param colName
   *          the name of the column to perform the distinct query
   * @param expectedNumberOfRecords
   *          the number of records expected
   */
  private void getRecordsDistinct(String urlAttachDataset, String colName, int expectedNumberOfRecords) {
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("ranges", "Array of ranges to query");
      String uri = getHostUrl() + urlAttachDataset;
      this.retrieveDocAPI(uri + "/records?distinct=true&colModel=" + colName, "", parameters,
          String.format(uri, "/records?distinct=true&colModel=" + colName));
    }
    else {
      queryDatasetRequestUrl(urlAttachDataset + "/records", "?distinct=true&colModel=" + colName,
          expectedNumberOfRecords);

    }
  }

  /**
   * Test a distinct request with a column which is not in the dataset The expected result is a normal result with no
   * records in it
   * 
   * @param urlAttachDataset
   *          the url attachment of the dataset @was testtest
   */
  private void getRecordsDistinctUnknownColumn(String urlAttachDataset, String colName) {
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("ranges", "Array of ranges to query");
      String uri = getHostUrl() + getBaseUrlDataset();
      this.retrieveDocAPI(uri + "/records?distinct=true&colModel=" + colName, "", parameters,
          String.format(uri, "/records?distinct=true&colModel=" + colName));
    }
    else {
      queryDatasetRequestUrl(urlAttachDataset + "/records", "?distinct=true&colModel=" + colName, 0);

    }
  }

  /**
   * Test a distinct request with a column which is not in the dataset The expected result is a normal result with no
   * records in it
   * 
   * @param urlAttachDataset
   *          the url attachment of the dataset
   * @was testtest et aperture
   */
  private void getRecordsUnknownColumn(String urlAttachDataset, String colName1, String colName2) {
    String colModel = colName1 + ", " + colName2;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("ranges", "Array of ranges to query");
      String uri = getHostUrl() + getBaseUrlDataset();
      this.retrieveDocAPI(uri + "/records?colModel=\"" + colModel + "\"&start=2&limit=5", "", parameters,
          String.format(uri, "?colModel=\"" + colModel + "\"&start=2&limit=5"));
    }
    else {
      List<Record> records = queryDatasetRequestUrl(urlAttachDataset + "/records", "?colModel=\"" + colModel
          + "\"&start=2&limit=5");

      assertNotNull(records);
      assertEquals(5, records.size());

      Record rec = records.get(0);
      assertNotNull(rec);
      List<AttributeValue> attr = rec.getAttributeValues();
      List<String> columnNames = new ArrayList<String>();
      for (AttributeValue attributeValue : attr) {
        columnNames.add(attributeValue.getName());
      }

      assertTrue(columnNames.contains(colName1));
      assertFalse(columnNames.contains(colName2));

    }
  }

  /**
   * Execute a query on the dataset with a converter attached to it
   * 
   * @param dataset
   *          the DataSet
   * @param column
   *          the column to use for the converter
   */
  private void getRecordsWithConverterExecution(DataSet dataset, String column) {
    createConverterObject(dataset.getDescription(), dataset.getId(), column);
    queryDatasetRequestUrl(dataset.getSitoolsAttachementForUsers() + "/records", "?start=2&limit=5", 5);
    deleteConverters(dataset.getId());
  }

  /**
   * Delete the converters for a given datasetId
   * 
   * @param id
   *          the of the dataset
   */
  private void deleteConverters(String id) {
    ClientResource cr = new ClientResource(getBaseUrl() + settings.getString(Consts.APP_DATASETS_URL) + "/" + id
        + settings.getString(Consts.APP_DATASETS_CONVERTERS_URL));
    Representation result = cr.delete(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    RIAPUtils.exhaust(result);

  }

  /**
   * Create a ConverterModelDTO object with the specified description and identifier
   * 
   * @param description
   *          the description
   * @param id
   *          the ConverterModelDTO identifier
   * @param column
   *          the column to use for the converter
   */
  public void createConverterObject(String description, String id, String column) {
    ConverterModelDTO conv = new ConverterModelDTO();

    conv.setClassName(converterClassname);
    conv.setDescriptionAction(description);
    conv.setName("TestConverter");
    conv.setClassAuthor("AKKA/CNES");
    conv.setClassVersion("1.0");
    conv.setDescription("FOR TEST PURPOSE ONLY, DON'T USE IT, IT DOESN'T DO ANYTHING");
    conv.setId(id);

    ConverterParameter param1 = new ConverterParameter("1", "1", ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    param1.setValue("param1_value");
    ConverterParameter param2 = new ConverterParameter("2", "2", ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    param2.setValue("param2_value");

    ConverterParameter paramColIn = new ConverterParameter("colIn", "colIn",
        ConverterParameterType.CONVERTER_PARAMETER_IN);
    paramColIn.setAttachedColumn(column);
    ConverterParameter paramColOut = new ConverterParameter("colOut", "colOut",
        ConverterParameterType.CONVERTER_PARAMETER_OUT);
    paramColOut.setAttachedColumn(column);
    ConverterParameter paramColInOut = new ConverterParameter("colInOut", "colInOut",
        ConverterParameterType.CONVERTER_PARAMETER_INOUT);
    paramColInOut.setAttachedColumn(column);

    conv.getParameters().add(param1);
    conv.getParameters().add(param2);
    conv.getParameters().add(paramColIn);
    conv.getParameters().add(paramColOut);
    conv.getParameters().add(paramColInOut);

    Representation rep = getRepresentationConverter(conv, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl() + settings.getString(Consts.APP_DATASETS_URL) + "/" + id
        + settings.getString(Consts.APP_DATASETS_CONVERTERS_URL));
    Representation result = cr.post(rep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    RIAPUtils.exhaust(result);

    this.changeStatus(id, "/stop");
    this.changeStatus(id, "/start");

  }

  /**
   * Call the monitoring resource
   */
  private void getMonitoring() {
    ClientResource cr = new ClientResource(getBaseUrlDataset() + "/monitoring");
    Representation rep = cr.get(getMediaTest());
    assertTrue(cr.getStatus().isSuccess());
    RIAPUtils.exhaust(rep);
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
  public static Representation getRepresentationConverter(ConverterModelDTO item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<ConverterModelDTO>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<ConverterModelDTO> rep = new XstreamRepresentation<ConverterModelDTO>(media, item);
      configureConverter(xstream);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null; // TODO complete test with ObjectRepresentation
    }
  }

  /**
   * Configures XStream mapping of Response object with ConverterModelDTO content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configureConverter(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);

    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");
  }

}
