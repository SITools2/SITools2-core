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
 * Test Dataset filters for datasource on MongoDB datasource
 * 
 * @since UserStory : ADM DataSets, Sprint : 4
 * 
 * @author m.gond
 */
public class DataSetMongoDBFormTestCase extends AbstractDataSetManagerTestCase {

  private String urlAttachDatasetUsers = "/dataset/users";

  private String urlAttachDatasetFuse = "/dataset/fuse";

  // private String urlAttachDatasetJeoEntry = "/dataset/jeo/entries";

  private String urlAttachDatasetArticles = "/dataset/articles";

  // private static DataSet datasetJeoEntry;

  /**
   * The dataset
   */
  private static DataSet datasetUsers = null;

  /**
   * The dataset
   */
  private static DataSet datasetFuse = null;
  /**
   * The dataset
   */
  private static DataSet datasetArticles = null;

  static {
    setMediaTest(MediaType.APPLICATION_JSON);

    docAPI = new DocAPI(DataSetMongoDBFormTestCase.class, "Dataset Filter API by Form with JSON format");
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

    datasetUsers = createDatasetUsersMongoDB("10000001");
    datasetFuse = createDatasetFuseMongoDB("120000001");
    datasetArticles = createDatasetArticlesMongoDB("130000001");
    // datasetJeoEntry = createDatasetJeoEntryPG("f53cfe5a-c853-401c-9c6e-4b5dc11745d3");
    // datasetHeaders = createDatasetHeaders("4563216542");

  }

  @After
  @Override
  /**
   * Stop server
   * @throws java.lang.Exception
   */
  public void tearDown() throws Exception {
    super.tearDown();
    deleteDataset("10000001");
    deleteDataset("120000001");
    deleteDataset("130000001");
    // deleteDataset("f53cfe5a-c853-401c-9c6e-4b5dc11745d3");
    // deleteDataset("4563216542");

  }

  /**
   * Produces documentation of the DataSet forms management API.
   */
  @Test
  public void getDocumentationAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Dataset Filter by Form");

    docAPI.appendSubChapter("type filter : multiple selection", "multiple");
    testDataSetGridQueryingFilterMultipleValueRadio();
    docAPI.appendSubChapter("type filter : single selection", "single");
    testDataSetGridQueryingFilterSingleValueTextField();
    docAPI.appendSubChapter("type filter : single selection between", "singleBetween");
    testDataSetGridQueryingFilterSingleNumericBetween();
    docAPI.appendSubChapter("type filter : single selection one or between ", "singleOneOrBetween");
    testDataSetGridQueryingFilterSingleNumericOneOrBetween();

    docAPI.close();
  }

  /**
   * Test DataSet Metadatas. TODO SPRINT 6
   * 
   */
  @Test
  public void testDataSetGridQueryingFilterMultipleValueRadio() {
    String params = "p[0]=LISTBOXMULTIPLE|firstname|jc|jp&start=0&limit=300&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("p[i]", "filter of form");
      parameters.put("LISTBOX", "type: CHECKBOX or LISTBOX");
      parameters.put("firstname", "column alias");
      parameters.put("jc|...", "List of values");
      retrieveDocAPI(params, parameters, datasetUsers.getSitoolsAttachementForUsers());
    }
    else {
      String result = retrieve(params, datasetUsers.getSitoolsAttachementForUsers());
      String expected = "{\"success\": true,\r\n" + "\"total\":3,\r\n";
      result = result.replaceAll("[\r\n]+", "");
      expected = expected.replaceAll("[\r\n]+", "");
      if (!result.startsWith(expected)) {
        Assert.fail(result + "<>" + expected);
      }
    }
  }

  /**
   * Test DataSet Metadatas. TODO SPRINT 6
   * 
   * //
   */
  @Test
  public void testDataSetGridQueryingFilterMultipleValueCheckbox() {
    // un test avec un filtre string
    String params = "p[0]=CHECKBOX|firstname|jc|jp&start=0&limit=300&media=json";
    String result = retrieve(params, datasetUsers.getSitoolsAttachementForUsers());
    String expected = "{\"success\": true,\r\n" + "\"total\":3,\r\n";
    result = result.replaceAll("[\r\n]+", "");
    expected = expected.replaceAll("[\r\n]+", "");
    if (!result.startsWith(expected)) {
      Assert.fail(result + "<>" + expected);
    }
  }

  /**
   * Test DataSet Metadatas. TODO SPRINT 6
   * 
   */
  @Test
  public void testDataSetGridQueryingFilterSingleValueTextField() {
    // un test avec un filtre string
    String params = "p[0]=TEXTFIELD|firstname|jc&start=0&limit=300&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("p[i]", "filter of form");
      parameters.put("SingleSelection", "selection type");
      parameters.put("TEXTFIELD", "type: TEXTFIELD or LISTBOX or RADIO or DROPDOWNLIST or BOOLEAN_CHECKBOX");
      parameters.put("firstname", "column alias");
      parameters.put("jc|...", "List of values");
      retrieveDocAPI(params, parameters, datasetUsers.getSitoolsAttachementForUsers());
    }
    else {
      String result = retrieve(params, datasetUsers.getSitoolsAttachementForUsers());
      String expected = "{\"success\": true,\r\n" + "\"total\":2,\r\n";
      result = result.replaceAll("[\r\n]+", "");
      expected = expected.replaceAll("[\r\n]+", "");
      if (!result.startsWith(expected)) {
        Assert.fail(result + "<>" + expected);
      }
    }
  }

  /**
   * Test DataSet Metadatas. TODO SPRINT 6
   * 
   */
  @Test
  public void testDataSetGridQueryingFilterSingleValueDropDownList() {
    // un test avec un filtre string
    String params = "p[0]=DROPDOWNLIST|firstname|jc&start=0&limit=300&media=json";
    String result = retrieve(params, datasetUsers.getSitoolsAttachementForUsers());
    String expected = "{\"success\": true,\r\n" + "\"total\":2,\r\n";
    result = result.replaceAll("[\r\n]+", "");
    expected = expected.replaceAll("[\r\n]+", "");
    if (!result.startsWith(expected)) {
      Assert.fail(result + "<>" + expected);
    }
  }

  /**
   * Test DataSet Metadatas. TODO SPRINT 6
   * 
   */
  @Test
  public void testDataSetGridQueryingFilterSingleValueListBox() {
    // un test avec un filtre string
    String params = "p[0]=LISTBOX|firstname|jc&start=0&limit=300&media=json";
    String result = retrieve(params, datasetUsers.getSitoolsAttachementForUsers());
    String expected = "{\"success\": true,\r\n" + "\"total\":2,\r\n";
    result = result.replaceAll("[\r\n]+", "");
    expected = expected.replaceAll("[\r\n]+", "");
    if (!result.startsWith(expected)) {
      Assert.fail(result + "<>" + expected);
    }
  }

  /**
   * Test DataSet Metadatas. TODO SPRINT 6
   * 
   */
  @Test
  public void testDataSetGridQueryingFilterSingleValueRadio() {
    // un test avec un filtre string
    String params = "p[0]=RADIO|firstname|jc&start=0&limit=300&media=json";
    String result = retrieve(params, datasetUsers.getSitoolsAttachementForUsers());
    String expected = "{\"success\": true,\r\n" + "\"total\":2,\r\n";
    result = result.replaceAll("[\r\n]+", "");
    expected = expected.replaceAll("[\r\n]+", "");
    if (!result.startsWith(expected)) {
      Assert.fail(result + "<>" + expected);
    }
  }

  /**
   * Test DataSet Metadatas. TODO SPRINT 6
   * 
   */
  @Test
  public void testDataSetGridQueryingFilterSingleNumericBetween() {
    // un test avec un filtre string
    String params = "p[0]=NUMERIC_BETWEEN|cycle|1|4&start=0&limit=300&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("p[i]", "filter of form");
      parameters.put("NUMERIC_BETWEEN", "type: NUMERIC_BETWEEN");
      parameters.put("cycle", "column alias");
      parameters.put("1|4", "between 1 and 4");
      retrieveDocAPI(params, parameters, datasetFuse.getSitoolsAttachementForUsers());
    }
    else {
      String result = retrieve(params, datasetFuse.getSitoolsAttachementForUsers());
      String expected = "{\"success\": true,\r\n" + "\"total\":478,\r\n";
      result = result.replaceAll("[\r\n]+", "");
      expected = expected.replaceAll("[\r\n]+", "");
      if (!result.startsWith(expected)) {
        Assert.fail(result + "<>" + expected);
      }
    }
  }

  /**
   * Test DataSet Metadatas. TODO SPRINT 6
   * 
   */
  @Test
  public void testDataSetGridQueryingFilterSingleNumericOneOrBetween() {
    // un test avec un filtre string
    String params = "p[0]=ONE_OR_BETWEEN|cycle|9||&start=0&limit=300&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("p[i]", "filter of form");
      parameters.put("ONE_OR_BETWEEN", "type: ONE_OR_BETWEEN");
      parameters.put("cycle", "column alias");
      parameters.put("9||", "value is 9");
      retrieveDocAPI(params, parameters, datasetFuse.getSitoolsAttachementForUsers());
    }
    else {
      String result = retrieve(params, datasetFuse.getSitoolsAttachementForUsers());
      String expected = "{\"success\": true,\r\n" + "\"total\":5,\r\n";
      result = result.replaceAll("[\r\n]+", "");
      expected = expected.replaceAll("[\r\n]+", "");
      if (!result.startsWith(expected)) {
        Assert.fail(result + "<>" + expected);
      }
    }
  }

  /**
   * Test of specific filter NumericOrBetween
   */
  @Test
  public void testDataSetGridQueryingFilterSingleNumericOneOrBetween2() {
    // un test avec un filtre string
    String params = "p[0]=ONE_OR_BETWEEN|cycle||1|4&start=0&limit=300&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("p[i]", "filter of form");
      parameters.put("ONE_OR_BETWEEN", "type: ONE_OR_BETWEEN");
      parameters.put("cycle", "column alias");
      parameters.put("|1|4", "value is between 1 and 4");
      retrieveDocAPI(params, parameters, datasetFuse.getSitoolsAttachementForUsers());
    }
    else {
      String result = retrieve(params, datasetFuse.getSitoolsAttachementForUsers());
      String expected = "{\"success\": true,\r\n" + "\"total\":478,\r\n";
      result = result.replaceAll("[\r\n]+", "");
      expected = expected.replaceAll("[\r\n]+", "");
      if (!result.startsWith(expected)) {
        Assert.fail(result + "<>" + expected);
      }
    }
  }

  /**
   * Test DataSet Metadatas. TODO SPRINT 6
   * 
   */
  @Test
  public void testDataSetGridQueryingFilterNumberField() {
    // un test avec un filtre string
    String params = "p[0]=NUMBER_FIELD|cycle|1&start=0&limit=300&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("c[i]", "filter of form");
      parameters.put("NUMBER_FIELD", "type: NUMBER_FIELD");
      parameters.put("cycle", "column alias");
      parameters.put("1", "equals to 1");
      retrieveDocAPI(params, parameters, datasetFuse.getSitoolsAttachementForUsers());
    }
    else {
      String result = retrieve(params, datasetFuse.getSitoolsAttachementForUsers());
      String expected = "{\"success\": true,\r\n" + "\"total\":119,\r\n";
      result = result.replaceAll("[\r\n]+", "");
      expected = expected.replaceAll("[\r\n]+", "");
      if (!result.startsWith(expected)) {
        Assert.fail(result + "<>" + expected);
      }
    }
  }

  /**
   * Test DataSet Metadatas. TODO SPRINT 6
   * 
   */
  @Test
  public void testDataSetGridQueryingFilterDateBetweenField() {
    // un test avec un filtre string
    String params = "p[0]=DATE_BETWEEN|startDate|1995-01-01T00:00:00.000|2000-01-01T00:00:00.000&start=0&limit=300&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("c[i]", "filter of form");
      parameters.put("DATE_BETWEEN", "type: DATE_BETWEEN");
      parameters.put("dateobs", "column alias");
      parameters.put("1995-01-01T00:00:00.000|2000-01-01T00:00:00.000", "between 1995-01-01|2000-01-01");
      retrieveDocAPI(params, parameters, datasetArticles.getSitoolsAttachementForUsers());
    }
    else {
      String result = retrieve(params, datasetArticles.getSitoolsAttachementForUsers());
      String expected = "{\"success\": true,\r\n" + "\"total\":56,\r\n";
      result = result.replaceAll("[\r\n]+", "");
      expected = expected.replaceAll("[\r\n]+", "");
      if (!result.startsWith(expected)) {
        Assert.fail(result + "<>" + expected);
      }
    }
  }

  // /**
  // * Test DataSet Metadatas. TODO SPRINT 6
  // *
  // */
  // @Test
  // public void testDataSetGridQueryingFilterConeSearchCartesianField() {
  // // un test avec un filtre string
  // String params = "p[0]=CONE_SEARCH_CARTESIEN|x_pos,y_pos,z_pos|0|45|5&start=0&limit=300&media=json";
  // if (docAPI.isActive()) {
  // Map<String, String> parameters = new LinkedHashMap<String, String>();
  // parameters.put("URI dataset", "URI ");
  // parameters.put("c[i]", "filter of form");
  // parameters.put("CONE_SEARCH_CARTESIEN", "type: CONE_SEARCH_CARTESIEN");
  // parameters.put("x_pos,y_pos,z_pos", "column alias");
  // parameters.put("0|45|5", "x = 5, y = 45, z = 5");
  // retrieveDocAPI(params, parameters, datasetHeaders.getSitoolsAttachementForUsers());
  // }
  // else {
  // String result = retrieve(params, datasetHeaders.getSitoolsAttachementForUsers());
  // String expected = "{\"success\": true,\r\n" + "\"total\":9,\r\n";
  // result = result.replaceAll("[\r\n]+", "");
  // expected = expected.replaceAll("[\r\n]+", "");
  // if (!result.startsWith(expected)) {
  // Assert.fail(result + "<>" + expected);
  // }
  // }
  // }

  // /**
  // * Test of specific filter bbox filter
  // *
  // * @throws InterruptedException
  // * if there is an error while deleting the dataset
  // */
  // @Test
  // public void testDataSetGridQueryingFilterBbox() throws InterruptedException {
  //
  // // un test avec un filtre string
  // String params = "p[0]=MAPPANEL|coord|0.0,40.0,4.0,44.0&start=0&limit=300&media=json";
  // if (docAPI.isActive()) {
  // Map<String, String> parameters = new LinkedHashMap<String, String>();
  // parameters.put("URI dataset", "URI ");
  // parameters.put("p[i]", "filter of form");
  // parameters.put("MAPPANEL", "type: MAPPANEL");
  // parameters.put("coord", "column alias");
  // parameters.put("0.0,40.0,4.0,44.0", "Coordinates of the top left and bottom right points of the boundary box");
  // retrieveDocAPI(params, parameters, datasetJeoEntry.getSitoolsAttachementForUsers());
  // }
  // else {
  // String result = retrieve(params, datasetJeoEntry.getSitoolsAttachementForUsers());
  // String expected = "{\"success\": true,\r\n" + "\"total\":2,\r\n";
  // result = result.replaceAll("[\r\n]+", "");
  // expected = expected.replaceAll("[\r\n]+", "");
  // if (!result.startsWith(expected)) {
  // Assert.fail(result + "<>" + expected);
  // }
  // }
  //
  // }

  /**
   * Create and activate a Dataset for Postgresql datasource. This dataset is created on the test.table_tests table
   * 
   * @param id
   *          the dataset ID
   * @return the DataSet created
   * @throws InterruptedException
   *           if something is wrong
   */
  private DataSet createDatasetUsersMongoDB(String id) throws InterruptedException {
    DataSet item = CreateDatasetUtil.createDatasetUsersMongoDB(id, urlAttachDatasetUsers);

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
   *           if something is wrong
   */
  private DataSet createDatasetFuseMongoDB(String id) throws InterruptedException {
    DataSet item = CreateDatasetUtil.createDatasetFuseMongoDB(id, urlAttachDatasetFuse);

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
   *           if something is wrong
   */
  private DataSet createDatasetArticlesMongoDB(String id) throws InterruptedException {
    DataSet item = CreateDatasetUtil.createDatasetArticlesMongoDB(id, urlAttachDatasetArticles);

    persistDataset(item);

    changeStatus(item.getId(), "/start");

    return item;

  }
  // /**
  // * Create and activate a Dataset for Postgresql datasource.This dataset is created on the sitools.jeo_entries table
  // of
  // * the CNES_SIG database *
  // *
  // * @param id
  // * the dataset ID
  // * @return the DataSet created
  // * @throws InterruptedException
  // * if something is wrong
  // */
  // private DataSet createDatasetJeoEntryPG(String id) throws InterruptedException {
  // DataSet item = CreateDatasetUtil.createDatasetJeoEntryPG(id, urlAttachDatasetJeoEntry);
  //
  // persistDataset(item);
  //
  // changeStatus(item.getId(), "/start");
  //
  // return item;
  //
  // }

  // private DataSet createDatasetHeaders(String id) throws InterruptedException {
  // DataSet item = CreateDatasetUtil.createDatasetHeadersSimplePG(id, urlAttachDatasetHeaders);
  //
  // persistDataset(item);
  //
  // changeStatus(item.getId(), "/start");
  //
  // return item;
  // }

}
