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

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;

import fr.cnes.sitools.api.DocAPI;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.utils.CreateDatasetUtil;

/**
 * 
 * Test the Unit functionalities on the form filters
 * 
 * @author m.gond ( AKKA Technologies )
 */
public class DataSetFormWithUnitTestCase extends AbstractDataSetManagerTestCase {
  /**
   * The Url attachment for Dataset fuse
   */
  private String urlAttachDatasetFuse = "/dataset/fuse";
  /**
   * The id of the datasetFuse
   */
  private String datasetFuseId = "120000001";

  /**
   * The dataset
   */
  private DataSet datasetFuse = null;

  static {
    setMediaTest(MediaType.APPLICATION_JSON);

    docAPI = new DocAPI(DataSetFormWithUnitTestCase.class, "Dataset Form Filters with Unit API in JSON format");
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
    docAPI.setActive(false);

    datasetFuse = createDatasetFusePG(datasetFuseId);

  }

  @After
  @Override
  /**
   * Stop server
   * @throws java.lang.Exception
   */
  public void tearDown() throws Exception {
    super.tearDown();
    deleteDataset(datasetFuseId);
  }

  /**
   * Produces documentation of the DataSet forms management API.
   */
  @Test
  public void getDocumentationAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Dataset form filters with Units");

    docAPI.appendSubChapter("type filter : Number field", "number");
    testFilterNumberFieldAPI();
    docAPI.appendSubChapter("type filter : Numeric between", "between");
    testFilterNumericBetweenAPI();
    docAPI.appendSubChapter("type filter : One or Between", "oneOrBetween");
    testFilterOneOrBetweenAPI();

    docAPI.close();
  }

  /**
   * Tests the filters contained in params on the dataset at the given datasetRecordUrl Assert that the number of record
   * returned is equal to the given expectedCount
   * 
   * @param params
   *          the filters param
   * @param expectedCount
   *          the number of records expected
   * @param datasetRecordUrl
   *          the url of the dataset records
   */
  public void testFilter(String params, String expectedCount, String datasetRecordUrl) {
    String result = retrieve(params, datasetRecordUrl);
    String expected = "{\"success\": true,\r\n" + "\"total\":" + expectedCount + ",\r\n";
    result = result.replaceAll("[\r\n]+", "");
    expected = expected.replaceAll("[\r\n]+", "");
    if (!result.startsWith(expected)) {
      Assert.fail(result + "<>" + expected);
    }
  }

  /**
   * Test only with API on NUMBER_FIELD
   */
  public void testFilterNumberFieldAPI() {
    String params = "p[0]=NUMBER_FIELD|cycle|100|length|cm&start=0&limit=1&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("p[i]", "filter of form");
      parameters.put("singleSelection", "selection type");
      parameters.put("NUMBER_FIELD", "type: NUMBER_FIELD");
      parameters.put("cycle", "column alias");
      parameters.put("1000", "value is 1000");
      parameters.put("length", "Dimension is length");
      parameters.put("cm", "Unit is cm");
      retrieveDocAPI(params, parameters, datasetFuse.getSitoolsAttachementForUsers());
    }
  }

  /**
   * Test NumberField with only unit defined
   * 
   */
  @Test
  public void testFilterNumberFieldWithUnit() {
    // un test avec un filtre string
    String params = "p[0]=NUMBER_FIELD|cycle|100||cm&start=0&limit=300&media=json";
    String expectedRecords = "119";
    String url = datasetFuse.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url);
  }

  /**
   * Test NumberField with only dimension defined
   * 
   */
  @Test
  public void testFilterNumberFieldWithDimension() {
    // un test avec un filtre string
    String params = "p[0]=NUMBER_FIELD|cycle|100|length|&start=0&limit=300&media=json";
    String expectedRecords = "0";
    String url = datasetFuse.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url);
  }

  /**
   * Test NumberField with dimension and unit defined
   * 
   */
  @Test
  public void testFilterNumberFieldWithUnitAndDimension() {
    // un test avec un filtre string
    String params = "p[0]=NUMBER_FIELD|cycle|100|length|cm&start=0&limit=300&media=json";
    String expectedRecords = "119";
    String url = datasetFuse.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url);
  }

  /**
   * Test NumberField without dimension or unit defined
   * 
   */
  @Test
  public void testFilterNumberFieldWithoutUnitOrDimension() {
    // un test avec un filtre string
    String params = "p[0]=NUMBER_FIELD|cycle|100||&start=0&limit=300&media=json";
    String expectedRecords = "0";
    String url = datasetFuse.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url);
  }

  /**
   * Test NumberField without dimension or unit defined and no pipes
   * 
   */
  @Test
  public void testFilterNumberFieldWithoutAnyDimensionOrUnitOrPipes() {
    // un test avec un filtre string
    String params = "p[0]=NUMBER_FIELD|cycle|100&start=0&limit=300&media=json";
    String expectedRecords = "0";
    String url = datasetFuse.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url);
  }

  /**
   * NUMERIC_BETWEEN ####################################################
   */
  /**
   * Test only with API on NUMERIC_BETWEEN
   */
  public void testFilterNumericBetweenAPI() {
    String params = "p[0]=NUMERIC_BETWEEN|cycle|100|300|length|cm&start=0&limit=1&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("p[i]", "filter of form");
      parameters.put("singleSelection", "selection type");
      parameters.put("NUMBER_FIELD", "type: NUMBER_FIELD");
      parameters.put("cycle", "column alias");
      parameters.put("100|300", "between 100 and 300");
      parameters.put("length", "Dimension is length");
      parameters.put("cm", "Unit is cm");
      retrieveDocAPI(params, parameters, datasetFuse.getSitoolsAttachementForUsers());
    }
  }

  /**
   * Test NumericBetween with only unit defined
   * 
   */
  @Test
  public void testFilterNumericBetweenWithUnit() {
    // un test avec un filtre string
    String params = "p[0]=NUMERIC_BETWEEN|cycle|100|300||cm&start=0&limit=300&media=json";
    String expectedRecords = "366";
    String url = datasetFuse.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url);
  }

  /**
   * Test NumericBetween with only dimension defined
   * 
   */
  @Test
  public void testFilterNumericBetweenWithDimension() {
    // un test avec un filtre string
    String params = "p[0]=NUMERIC_BETWEEN|cycle|100|300|length|&start=0&limit=300&media=json";
    String expectedRecords = "0";
    String url = datasetFuse.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url);
  }

  /**
   * Test NumericBetween with dimension and unit defined
   * 
   */
  @Test
  public void testFilterNumericBetweenWithUnitAndDimension() {
    // un test avec un filtre string
    String params = "p[0]=NUMERIC_BETWEEN|cycle|100|300|length|cm&start=0&limit=300&media=json";
    String expectedRecords = "366";
    String url = datasetFuse.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url);
  }

  /**
   * Test NumericBetween without dimension or unit defined
   * 
   */
  @Test
  public void testFilterNumericBetweenWithoutUnitOrDimension() {
    // un test avec un filtre string
    String params = "p[0]=NUMERIC_BETWEEN|cycle|100|300||&start=0&limit=300&media=json";
    String expectedRecords = "0";
    String url = datasetFuse.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url);
  }

  /**
   * Test NumericBetween without dimension or unit defined and no pipes
   * 
   */
  @Test
  public void testFilterNumericBetweenWithoutAnyDimensionOrUnitOrPipes() {
    // un test avec un filtre string
    String params = "p[0]=NUMERIC_BETWEEN|cycle|100|300&start=0&limit=300&media=json";
    String expectedRecords = "0";
    String url = datasetFuse.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url);
  }

  /**
   * Test NumericBetween with dimension and unit defined
   * 
   */
  @Test
  public void testFilterNumericBetweenWithUnitAndDimensionDifferentDimension() {
    // un test avec un filtre string
    String params = "p[0]=NUMERIC_BETWEEN|cycle|1|0.1|Photometric_range|GHz&start=0&limit=300&media=json";
    String expectedRecords = "347";
    String url = datasetFuse.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url);
  }

  /**
   * ONE OR BETWEEN ####################################################
   */

  /**
   * Test only with API on ONE_OR_BETWEEN
   */
  public void testFilterOneOrBetweenAPI() {
    String params = "p[0]=ONE_OR_BETWEEN|cycle||100|300|length|cm&start=0&limit=1&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("p[i]", "filter of form");
      parameters.put("singleSelection", "selection type");
      parameters.put("NUMBER_FIELD", "type: NUMBER_FIELD");
      parameters.put("cycle", "column alias");
      parameters.put("|100|300", "value is between 100 and 300");
      parameters.put("length", "Dimension is length");
      parameters.put("cm", "Unit is cm");
      retrieveDocAPI(params, parameters, datasetFuse.getSitoolsAttachementForUsers());
    }
  }

  /**
   * Test OneOrBetween with only unit defined
   * 
   */
  @Test
  public void testFilterOneOrBetweenWithUnit() {
    // un test avec un filtre string
    String params = "p[0]=ONE_OR_BETWEEN|cycle||100|300||cm&start=0&limit=300&media=json";
    String expectedRecords = "366";
    String url = datasetFuse.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url);
  }

  /**
   * Test OneOrBetween with only dimension defined
   * 
   */
  @Test
  public void testFilterOneOrBetweenWithDimension() {
    // un test avec un filtre string
    String params = "p[0]=ONE_OR_BETWEEN|cycle||100|300|length|&start=0&limit=300&media=json";
    String expectedRecords = "0";
    String url = datasetFuse.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url);
  }

  /**
   * Test OneOrBetween with dimension and unit defined
   * 
   */
  @Test
  public void testFilterOneOrBetweenWithUnitAndDimension() {
    // un test avec un filtre string
    String params = "p[0]=ONE_OR_BETWEEN|cycle||100|300|length|cm&start=0&limit=300&media=json";
    String expectedRecords = "366";
    String url = datasetFuse.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url);
  }

  /**
   * Test OneOrBetween without dimension or unit defined
   * 
   */
  @Test
  public void testFilterOneOrBetweenWithoutUnitOrDimension() {
    // un test avec un filtre string
    String params = "p[0]=ONE_OR_BETWEEN|cycle||100|300||&start=0&limit=300&media=json";
    String expectedRecords = "0";
    String url = datasetFuse.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url);
  }

  /**
   * Test OneOrBetween without dimension or unit defined and no pipes
   * 
   */
  @Test
  public void testFilterOneOrBetweenWithoutAnyDimensionOrUnitOrPipes() {
    // un test avec un filtre string
    String params = "p[0]=ONE_OR_BETWEEN|cycle||100|300&start=0&limit=300&media=json";
    String expectedRecords = "0";
    String url = datasetFuse.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url);
  }

  /**
   * Test OneOrBetween with dimension and unit defined
   * 
   */
  @Test
  public void testFilterOneOrBetweenWithUnitAndDimensionDifferentDimension() {
    // un test avec un filtre string
    String params = "p[0]=ONE_OR_BETWEEN|cycle||1|0.1|Photometric_range|GHz&start=0&limit=300&media=json";
    String expectedRecords = "347";
    String url = datasetFuse.getSitoolsAttachementForUsers();
    testFilter(params, expectedRecords, url);
  }

  /**
   * Create and activate a Dataset for Postgresql datasource. This dataset is created on the test.table_tests table
   * 
   * @param id
   *          the dataset ID
   * @return the DataSet created
   * @throws InterruptedException
   *           if something is wrong
   */
  private DataSet createDatasetFusePG(String id) throws InterruptedException {
    DataSet item = CreateDatasetUtil.createDatasetFusePG(id, urlAttachDatasetFuse);

    persistDataset(item);

    changeStatus(item.getId(), "/start");

    return item;

  }
}
