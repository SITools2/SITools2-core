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

import java.io.IOException;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.api.DocAPI;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.Column;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.Predicat;
import fr.cnes.sitools.datasource.jdbc.model.Structure;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.CreateDatasetUtil;

/**
 * 
 * Test the pluggable filters but with incorrect parameters -> For String parameters it tests the SQL injection -> For
 * numeric parameters it test non numeric parameters The numeric and some date filters do not execute the SQL request (
 * the type of the parameter is tested before), those tests are executed only with the postgresql dataset Other filter
 * do execute the SQL request, those filters are tested with both postgresql and mysql datasets
 * 
 * 
 * 
 * @author m.gond (AKKA Technologies)
 */
public class DataSetFilterWithErrorsTestCase extends AbstractDataSetManagerTestCase {
  /**
   * the url attachment for the dataset for postgresql datasource
   */
  private String urlAttachDatasetHeadersPG = "/dataset/pg/fuse";

  /**
   * The dataset for the postgresql datasource
   */
  private DataSet datasetHeadersPG = null;

  /**
   * The id of the dataset for the postgresql datasource
   */

  private String datasetHeadersIdPG = "111111111";

  /**
   * the url attachment for the dataset for the mysql datasource
   */
  private String urlAttachDatasetHeadersMysql = "/dataset/mysql/fuse";

  /**
   * The dataset for the mysql datasource
   */
  private DataSet datasetHeadersMysql = null;

  /**
   * The id of the dataset for the postgresql datasource
   */

  private String datasetHeadersIdMysql = "222222222";

  /**
   * The sql injection string
   */
  private String sqlInjectionStr = "' OR '1' = '1";

  /**
   * The sql injection string
   */
  private String wrongDateString = "2003-02-10T21:02";

  /**
   * The sql injection string
   */
  private String dateStringFrom = "2003-02-10T21:02:50.000";

  /**
   * The sql injection string
   */
  private String dateStringTo = "2001-02-10T21:02:50.000";

  /**
   * The sql delete injection string
   */
  private String sqlDeleteInjectionStr = "0; DELETE from sitools.\"USERS\";";

  static {
    setMediaTest(MediaType.APPLICATION_JSON);

    docAPI = new DocAPI(DataSetFilterWithErrorsTestCase.class, "Tests of Dataset Form Filters with errors");
    docAPI.setActive(false);
  }

  @Before
  @Override
  /**
   * Create component, store and application and start server
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    super.setUp();
    docAPI.setActive(false);
    datasetHeadersPG = createDatasetHeadersPG(datasetHeadersIdPG);
    datasetHeadersMysql = createDatasetHeadersMySQL(datasetHeadersIdMysql);

  }

  @After
  @Override
  /**
   * Stop server
   * @throws java.lang.Exception
   */
  public void tearDown() throws Exception {
    super.tearDown();
    deleteDataset(datasetHeadersIdPG);
    deleteDataset(datasetHeadersIdMysql);
  }

  /**
   * Test of the filter ConeSearchCartesienFilter
   * 
   */
  @Test
  public void testConeSearchCartesienFilter() {
    // CONE_SEARCH_CARTESIEN
    String params = "p[0]=CONE_SEARCH_CARTESIEN|y_pos,x_pos,z_pos|" + sqlInjectionStr + "|" + sqlInjectionStr + "|" + sqlInjectionStr
        + "&start=0&limit=300&media=json";
    String url = datasetHeadersPG.getSitoolsAttachementForUsers();
    testFilterExpectError(params, url, Status.CLIENT_ERROR_BAD_REQUEST);
  }

  /**
   * Test of the filter ConeSearchPgSphereFilter
   * 
   */
  @Test
  public void testConeSearchPgSphereFilter() {
    // CONE_SEARCH_PG_SPHERE
    String params = "p[0]=CONE_SEARCH_PG_SPHERE|y_pos,x_pos,z_pos|" + sqlInjectionStr + "|" + sqlInjectionStr + "|" + sqlInjectionStr
        + "&start=0&limit=300&media=json";
    String url = datasetHeadersPG.getSitoolsAttachementForUsers();
    testFilterExpectError(params, url, Status.CLIENT_ERROR_BAD_REQUEST);
  }

  // *******************************************
  // DATE BETWEEN, the SQL request is executed, it needs to be tested with both postgresql and mysql datasource
  // *******************************************

  /**
   * Test of the filter DateBetweenFilter with postgresql datasource
   */
  @Test
  public void testDateBetweenFilterPG() {
    testDateBetweenFilterPG(datasetHeadersPG, "dateobs");
  }

  /**
   * Test of the filter DateBetweenFilter with mysql datasource
   */
  @Test
  public void testDateBetweenFilterMySQL() {
    testDateBetweenFilterMySql(datasetHeadersMysql, "DATEOBS");
  }

  /**
   * Test of the filter DateBetweenFilter
   * 
   * @param item
   *          the Dataset
   * @param colName
   *          the name of the column
   * 
   */
  public void testDateBetweenFilterPG(DataSet item, String colName) {
    // DATE_BETWEEN
    String params = "p[0]=DATE_BETWEEN|" + colName + "|" + sqlInjectionStr + "|" + sqlInjectionStr + "&start=0&limit=300&media=json";
    String url = item.getSitoolsAttachementForUsers();
    testFilterExpectError(params, url, Status.CLIENT_ERROR_BAD_REQUEST);
  }

  /**
   * Test of the filter DateBetweenFilter
   * 
   * @param item
   *          the Dataset
   * @param colName
   *          the name of the column
   * 
   */
  public void testDateBetweenFilterMySql(DataSet item, String colName) {
    // DATE_BETWEEN
    String params = "p[0]=DATE_BETWEEN|" + colName + "|" + sqlInjectionStr + "|" + sqlInjectionStr + "&start=0&limit=300&media=json";
    String url = item.getSitoolsAttachementForUsers();
    testFilterExpectError(params, url, Status.CLIENT_ERROR_BAD_REQUEST);
  }

  // *******************************************
  // GRID FILTER, some of the requests are executed, it needs to be tested with both postgresql and mysql datasource
  // *******************************************

  /**
   * Test of the filter GridFilter with postgresql datasource
   */
  @Test
  public void testGridFilterStringPG() {
    testGridFilterString(datasetHeadersPG, "targname");
  }

  /**
   * Test of the filter GridFilter with mysql datasource
   */
  @Test
  public void testGridFilterStringMySQL() {
    testGridFilterString(datasetHeadersMysql, "TARGNAME");
  }

  /**
   * Test of the filter GridFilter with type string
   * 
   * @param item
   *          the DataSet.
   * @param colName
   *          the name of the column
   * 
   */
  public void testGridFilterString(DataSet item, String colName) {
    // String filter on targname
    String params = "filter[0][columnAlias]=" + colName + "&filter[0][data][type]=string&filter[0][data][comparison]=LIKE" + "&filter[0][data][value]="
        + sqlInjectionStr + "&start=0&limit=300&media=json";
    String expectedRecords = "0";
    String url = item.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url, expectedRecords, "0");
  }

  /**
   * Test of the filter GridFilter with other types (date, numeric) SQL request are not executed, only the java part is
   * tested
   */
  @Test
  public void testGridFilterOthers() {
    // numeric filter on ra_targ
    String url = datasetHeadersPG.getSitoolsAttachementForUsers();
    String params = "filter[0][columnAlias]=ra_targ&filter[0][data][type]=numeric&filter[0][data][comparison]=eq" + "&filter[0][data][value]="
        + sqlInjectionStr + "&start=0&limit=300&media=json";
    testFilterExpectError(params, url, Status.CLIENT_ERROR_BAD_REQUEST);

    // date filter on dateobs
    params = "filter[0][columnAlias]=dateobs&filter[0][data][type]=date&filter[0][data][comparison]=eq" + "&filter[0][data][value]=" + sqlInjectionStr
        + "&start=0&limit=300&media=json";
    testFilterExpectError(params, url, Status.CLIENT_ERROR_BAD_REQUEST);

  }

  // ***************************************
  // MultipleValueFilter, the SQL request is executed, it needs to be tested with both postgresql and mysql datasource
  // ***************************************
  /**
   * Test of the filter MultipleValueFilter with postgresql datasource
   */
  @Test
  public void testMultipleValueFilterPG() {
    testMultipleValueFilter(datasetHeadersPG, "targname");
  }

  /**
   * Test of the filter MultipleValueFilter with mysql datasource
   */
  @Test
  public void testMultipleValueFilterMySQL() {
    testMultipleValueFilter(datasetHeadersMysql, "TARGNAME");
  }

  /**
   * Test of the filter MultipleValueFilter
   * 
   * @param item
   *          the DataSet
   * @param colName
   *          the name of the column
   */
  public void testMultipleValueFilter(DataSet item, String colName) {
    // LISTBOXMULTIPLE
    String params = "p[0]=LISTBOXMULTIPLE|" + colName + "|" + sqlInjectionStr + "|" + sqlInjectionStr + "&start=0&limit=300&media=json";
    String url = item.getSitoolsAttachementForUsers();
    String expectedRecords = "0";
    testFilter(params, expectedRecords, url, expectedRecords, "0");

    // CHECKBOX
    params = "p[0]=CHECKBOX|" + colName + "|" + sqlInjectionStr + "|" + sqlInjectionStr + "&start=0&limit=300&media=json";
    testFilter(params, expectedRecords, url, expectedRecords, "0");
  }

  /**
   * Test of the filter NumberFieldFilter
   * 
   */
  @Test
  public void testNumberFieldFilter() {
    // NUMBER_FIELD
    String params = "p[0]=NUMBER_FIELD|y_pos|" + sqlInjectionStr + "&start=0&limit=300&media=json";
    String url = datasetHeadersPG.getSitoolsAttachementForUsers();
    testFilterExpectError(params, url, Status.CLIENT_ERROR_BAD_REQUEST);
  }

  /**
   * Test of the filter NumericBetweenFilter
   * 
   */
  @Test
  public void testNumericBetweenFilter() {
    // NUMERIC_BETWEEN
    String params = "p[0]=NUMERIC_BETWEEN|y_pos|" + sqlInjectionStr + "|" + sqlInjectionStr + "&start=0&limit=300&media=json";
    String url = datasetHeadersPG.getSitoolsAttachementForUsers();
    testFilterExpectError(params, url, Status.CLIENT_ERROR_BAD_REQUEST);
  }

  /**
   * Test of the filter OneOrBetweenFilter
   * 
   */
  @Test
  public void testOneOrBetweenFilter() {
    // ONE_OR_BETWEEN, ONE
    String params = "p[0]=ONE_OR_BETWEEN|y_pos|" + sqlInjectionStr + "||&start=0&limit=300&media=json";
    String url = datasetHeadersPG.getSitoolsAttachementForUsers();
    testFilterExpectError(params, url, Status.CLIENT_ERROR_BAD_REQUEST);

    // ONE_OR_BETWEEN, BETWEEN
    params = "p[0]=ONE_OR_BETWEEN|y_pos||" + sqlInjectionStr + "|" + sqlInjectionStr + "&start=0&limit=300&media=json";
    testFilterExpectError(params, url, Status.CLIENT_ERROR_BAD_REQUEST);
  }

  // ***************************************
  // SingleValueFilter, the SQL requests are executed, it needs to be tested with both postgresql and mysql datasource
  // ***************************************
  /**
   * Test of the filter SingleValueFilter with postgresql datasource
   */
  @Test
  public void testSingleValueFilterPG() {
    testSingleValueFilter(datasetHeadersPG, "targname");
  }

  /**
   * Test of the filter MultipleValueFilter with mysql datasource
   */
  @Test
  public void testSingleValueFilterMySQL() {
    testSingleValueFilter(datasetHeadersMysql, "TARGNAME");
  }

  /**
   * Test of the filter SingleValueFilter
   * 
   * @param item
   *          the DataSet
   * @param colName
   *          the name of the column
   */
  public void testSingleValueFilter(DataSet item, String colName) {
    // TEXTFIELD
    String params = "p[0]=TEXTFIELD|" + colName + "|" + sqlInjectionStr + "&start=0&limit=300&media=json";
    String expectedRecords = "0";
    String url = item.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url, expectedRecords, "0");
    // LISTBOX
    params = "p[0]=LISTBOX|" + colName + "|" + sqlInjectionStr + "&start=0&limit=300&media=json";
    testFilter(params, expectedRecords, url, expectedRecords, "0");
    // DROPDOWNLIST
    params = "p[0]=DROPDOWNLIST|" + colName + "|" + sqlInjectionStr + "&start=0&limit=300&media=json";
    testFilter(params, expectedRecords, url, expectedRecords, "0");
    // RADIO
    params = "p[0]=RADIO|" + colName + "|" + sqlInjectionStr + "&start=0&limit=300&media=json";
    testFilter(params, expectedRecords, url, expectedRecords, "0");
    // BOOLEAN_CHECKBOX
    params = "p[0]=BOOLEAN_CHECKBOX|" + colName + "|" + sqlInjectionStr + "&start=0&limit=300&media=json";
    testFilter(params, expectedRecords, url, expectedRecords, "0");
  }

  // **************************************************
  // Test of start and limit
  // **************************************************
  /**
   * Test of the start, start argument will be 0 and limit will be 300
   * 
   * @throws InterruptedException
   *           if there is something wrong while creating or deleting user dataset
   */
  @Test
  public void testStart() throws InterruptedException {
    String params = "start=" + sqlDeleteInjectionStr + "&limit=300";
    String expectedTotalCount = "5062";
    String expectedRecords = "300";
    String url = datasetHeadersPG.getSitoolsAttachementForUsers();
    testFilter(params, expectedTotalCount, url, expectedRecords, "0");
    checkUserDataSet("123456789");
  }

  /**
   * Test of the start, start argument will be 0 and limit will be 500, default value
   * 
   * @throws InterruptedException
   *           if there is something wrong while creating or deleting user dataset
   */
  @Test
  public void testLimit() throws InterruptedException {
    String params = "start=0&limit=" + sqlDeleteInjectionStr;
    String expectedTotalCount = "5062";
    String expectedRecords = "500";
    String url = datasetHeadersPG.getSitoolsAttachementForUsers();
    testFilter(params, expectedTotalCount, url, expectedRecords, "0");
    checkUserDataSet("123456789");
  }

  /**
   * Test of the filter DateBetweenFilter with a wrong date syntax Expect an error from the server and no records
   * returned
   */
  @Test
  public void testDateBetweenFilterWrongDateSyntax() {
    // DATE_BETWEEN
    String params = "p[0]=DATE_BETWEEN|" + "dateobs" + "|" + wrongDateString + "|" + wrongDateString + "&start=0&limit=300&media=json";
    String url = datasetHeadersPG.getSitoolsAttachementForUsers();
    testFilterExpectError(params, url, Status.CLIENT_ERROR_BAD_REQUEST);
  }

  /**
   * Test of the filter DateBetweenFilter with a from date superior to the to date Expect an error from the server and
   * no records returned
   */
  @Test
  public void testDateBetweenFilterBadRequest() {
    // DATE_BETWEEN
    String params = "p[0]=DATE_BETWEEN|" + "dateobs" + "|" + dateStringFrom + "|" + dateStringTo + "&start=0&limit=300&media=json";
    String url = datasetHeadersPG.getSitoolsAttachementForUsers();
    testFilterExpectError(params, url, Status.CLIENT_ERROR_BAD_REQUEST);
  }

  /**
   * Test of the filter GridFilter with other types (date, numeric) SQL request are not executed, only the java part is
   * tested
   */
  @Test
  public void testDateBetweenFilterGridWrongDateSyntax() {
    // numeric filter on ra_targ
    String url = datasetHeadersPG.getSitoolsAttachementForUsers();

    // date filter on dateobs
    String params = "filter[0][columnAlias]=dateobs&filter[0][data][type]=date&filter[0][data][comparison]=eq" + "&filter[0][data][value]=" + wrongDateString
        + "&start=0&limit=300&media=json";
    testFilterExpectError(params, url, Status.CLIENT_ERROR_BAD_REQUEST);
  }

  /**
   * Test of the filter DateBetweenFilter with a from date superior to the to date Expect an error from the server and
   * no records returned
   */
  @Test
  public void testDateBetweenFilterEmptyDate() {
    // DATE_BETWEEN
    String params = "p[0]=DATE_BETWEEN|" + "dateobs" + "|" + "|" + dateStringTo + "&start=0&limit=300&media=json";
    String url = datasetHeadersPG.getSitoolsAttachementForUsers();
    testFilterExpectError(params, url, Status.CLIENT_ERROR_BAD_REQUEST);
  }

  /**
   * ############################ UTILS ############################
   */

  /**
   * Tests the filters contained in params on the dataset at the given datasetRecordUrl Assert that the number of record
   * returned is equal to the given expectedCount
   * 
   * @param params
   *          the filters param
   * @param expectedCountTotalCount
   *          the total number of records expected
   * @param datasetUrl
   *          the url of the dataset
   * @param expectedCount
   *          the number of record returned expected
   * @param offset
   *          the offset expected
   */
  public void testFilter(String params, String expectedCountTotalCount, String datasetUrl, String expectedCount, String offset) {
    String result = retrieve(params, datasetUrl);
    String expected = "{\"success\": true,\r\n" + "\"total\":" + expectedCountTotalCount + ",\r\n";
    String expectedEnd = "\"count\":" + expectedCount + ",\r\n" + "\"offset\":" + offset + "}";
    result = result.replaceAll("[\r\n]+", "");
    expected = expected.replaceAll("[\r\n]+", "");
    expectedEnd = expectedEnd.replaceAll("[\r\n]+", "");
    if (!result.startsWith(expected)) {
      Assert.fail(result + "<>" + expected);
    }
    if (!result.endsWith(expectedEnd)) {
      Assert.fail(result + "<>" + expectedEnd);
    }
  }

  /**
   * Tests the filters contained in params on the dataset at the given datasetRecordUrl Assert that the response is an
   * error with the given status
   * 
   * @param params
   *          the filters param
   * @param datasetUrl
   *          the url of the dataset records
   * @param status
   *          the status excepted
   */
  public void testFilterExpectError(String params, String datasetUrl, Status status) {
    String uri = getHostUrl() + datasetUrl + "/records?" + params;
    final Client client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, uri);
    org.restlet.Response response = client.handle(request);
    try {
      assertNotNull(response);
      assertTrue(response.getStatus().isError());
      assertEquals(status, response.getStatus());
    }
    finally {
      RIAPUtils.exhaust(response);
    }
  }

  /**
   * Create and activate a Dataset for Postgresql datasource. This dataset is created on the fuse.headers table
   * 
   * @param id
   *          the dataset ID
   * @return the DataSet created
   * @throws InterruptedException
   *           if something is wrong
   */
  private DataSet createDatasetHeadersPG(String id) throws InterruptedException {
    DataSet item = CreateDatasetUtil.createDatasetHeadersSimplePG(id, urlAttachDatasetHeadersPG);

    persistDataset(item);
    changeStatus(item.getId(), "/start");

    return item;

  }

  /**
   * Create and activate a Dataset for Mysql datasource. This dataset is created on the HEADERS table
   * 
   * @param id
   *          the dataset ID
   * @return the DataSet created
   * @throws InterruptedException
   *           if something is wrong
   */
  private DataSet createDatasetHeadersMySQL(String id) throws InterruptedException {
    DataSet item = CreateDatasetUtil.createDatasetHeadersSimpleMySQL(id, urlAttachDatasetHeadersMysql);

    persistDataset(item);
    changeStatus(item.getId(), "/start");

    return item;

  }

  /**
   * Check that the user dataset stil contains all its elements
   * 
   * @param id
   *          the datasetId
   * @throws InterruptedException
   *           if there is an error while creating or deleting the dataset
   */
  private void checkUserDataSet(String id) throws InterruptedException {
    // check if no data where deleted

    DataSet userDataSet = CreateDatasetUtil.createDatasetUsersPG(id, "/dataset/users");

    persistDataset(userDataSet);

    changeStatus(userDataSet.getId(), "/start");
    String params = "start=0&limit=300";
    String expectedRecords = "12";
    testFilter(params, expectedRecords, "/dataset/users", expectedRecords, "0");

    deleteDataset(id);
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
          xstream.addImplicitCollection(DataSet.class, "columnModel", "columnModel", Column.class);
          xstream.addImplicitCollection(DataSet.class, "structures", "structures", Structure.class);
          xstream.addImplicitCollection(DataSet.class, "predicat", "predicat", Predicat.class);
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
