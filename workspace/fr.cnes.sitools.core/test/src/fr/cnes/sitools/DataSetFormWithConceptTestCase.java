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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.api.DocAPI;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.ColumnConceptMapping;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.DictionaryMapping;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.CreateDatasetUtil;
import fr.cnes.sitools.utils.GetRepresentationUtils;
import fr.cnes.sitools.utils.GetResponseUtils;

/**
 * 
 * Test DataSetApplication Rest API
 * 
 * @since UserStory : ADM DataSets, Sprint : 4
 * 
 * @author m.gond
 */
public class DataSetFormWithConceptTestCase extends AbstractDataSetManagerTestCase {

  private String urlAttachDatasetUsers = "/dataset/users";

  private String urlAttachDatasetJeoEntry = "/dataset/jeo/entries";

  private String urlAttachDatasetHeaders = "/dataset/headers";

  private String urlAttachDatasetFuse = "/dataset/fuse";

  private static DataSet datasetJeoEntry;

  private String dictionaryId = "6caf5368-6bbd-49c7-be43-9ae95cbb5ff6";

  /**
   * The dataset
   */
  private static DataSet datasetUsers = null;

  /**
   * The dataset
   */
  private static DataSet datasetHeaders = null;
  /**
   * The dataset
   */
  private static DataSet datasetFuse = null;

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getDatasetBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_URL);
  }

  static {
    setMediaTest(MediaType.APPLICATION_JSON);

    docAPI = new DocAPI(DataSetFormWithConceptTestCase.class, "Dataset Filter API by Form with JSON format");
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

    datasetUsers = createDatasetUsersPG("10000001");
    datasetFuse = createDatasetFusePG("120000001");
    datasetJeoEntry = createDatasetJeoEntryPG("f53cfe5a-c853-401c-9c6e-4b5dc11745d3");
    datasetHeaders = createDatasetHeaders("4563216542");

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
    deleteDataset("f53cfe5a-c853-401c-9c6e-4b5dc11745d3");
    deleteDataset("4563216542");

  }

  /**
   * Produces documentation of the DataSet forms management API.
   */
  @Test
  public void getDocumentationAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Dataset Filter by Form with Dictionary Concepts parameters");

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
    String params = "c[0]=LISTBOXMULTIPLE|TestDictionary,name|jc|jp&start=0&limit=300&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("c[i]", "filter of form");
      parameters.put("LISTBOX", "type: CHECKBOX or LISTBOX");
      parameters.put("TestDictionary,name", "Dictionary name, concept name");
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
   */
  @Test
  public void testDataSetGridQueryingFilterMultipleValueCheckbox() {
    // un test avec un filtre string
    String params = "c[0]=CHECKBOX|TestDictionary,name|jc|jp&start=0&limit=300&media=json";
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
    String params = "c[0]=TEXTFIELD|TestDictionary,name|jc&start=0&limit=300&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("c[i]", "filter of form");
      parameters.put("SingleSelection", "selection type");
      parameters.put("TEXTFIELD", "type: TEXTFIELD or LISTBOX or RADIO or DROPDOWNLIST or BOOLEAN_CHECKBOX");
      parameters.put("TestDictionary,name", "Dictionary name, concept name");
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
    String params = "c[0]=DROPDOWNLIST|TestDictionary,name|jc&start=0&limit=300&media=json";
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
    String params = "c[0]=LISTBOX|TestDictionary,name|jc&start=0&limit=300&media=json";
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
    String params = "c[0]=RADIO|TestDictionary,name|jc&start=0&limit=300&media=json";
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
    String params = "c[0]=NUMERIC_BETWEEN|TestDictionary,cycle|1|4&start=0&limit=300&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("c[i]", "filter of form");
      parameters.put("NUMERIC_BETWEEN", "type: NUMERIC_BETWEEN");
      parameters.put("TestDictionary,cycle", "Dictionary name, concept name");
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
    String params = "c[0]=ONE_OR_BETWEEN|TestDictionary,cycle|9||&start=0&limit=300&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("c[i]", "filter of form");
      parameters.put("ONE_OR_BETWEEN", "type: ONE_OR_BETWEEN");
      parameters.put("TestDictionary,cycle", "Dictionary name, concept name");
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
    String params = "c[0]=ONE_OR_BETWEEN|TestDictionary,cycle||1|4&start=0&limit=300&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("c[i]", "filter of form");
      parameters.put("ONE_OR_BETWEEN", "type: ONE_OR_BETWEEN");
      parameters.put("TestDictionary,cycle", "Dictionary name, concept name");
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
    String params = "c[0]=NUMBER_FIELD|TestDictionary,cycle|1&start=0&limit=300&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("c[i]", "filter of form");
      parameters.put("NUMBER_FIELD", "type: NUMBER_FIELD");
      parameters.put("TestDictionary,cycle", "Dictionary name, concept name");
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
    String params = "c[0]=DATE_BETWEEN|TestDictionary,date|2000-01-01T00:00:00.000|2000-01-06T00:00:00.000&start=0&limit=300&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("c[i]", "filter of form");
      parameters.put("DATE_BETWEEN", "type: DATE_BETWEEN");
      parameters.put("TestDictionary,dateobs", "Dictionary name, concept name");
      parameters.put("2000-01-01T00:00:00.000|2000-01-06T00:00:00.000", "between 2000-01-01|2000-01-06");
      retrieveDocAPI(params, parameters, datasetHeaders.getSitoolsAttachementForUsers());
    }
    else {
      String result = retrieve(params, datasetHeaders.getSitoolsAttachementForUsers());
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
   */
  @Test
  public void testDataSetGridQueryingFilterConeSearchCartesianField() {
    // un test avec un filtre string
    String params = "c[0]=CONE_SEARCH_CARTESIEN|TestDictionary,x,y,z|0|45|5&start=0&limit=300&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("c[i]", "filter of form");
      parameters.put("CONE_SEARCH_CARTESIEN", "type: CONE_SEARCH_CARTESIEN");
      parameters.put("TestDictionary,x,y,z", "Dictionary name, concepts name");
      parameters.put("0|45|5", "x = 5, y = 45, z = 5");
      retrieveDocAPI(params, parameters, datasetHeaders.getSitoolsAttachementForUsers());
    }
    else {
      String result = retrieve(params, datasetHeaders.getSitoolsAttachementForUsers());
      String expected = "{\"success\": true,\r\n" + "\"total\":9,\r\n";
      result = result.replaceAll("[\r\n]+", "");
      expected = expected.replaceAll("[\r\n]+", "");
      if (!result.startsWith(expected)) {
        Assert.fail(result + "<>" + expected);
      }
    }
  }

  /**
   * Test of specific filter bbox filter
   * 
   * @throws InterruptedException
   *           if there is an error while deleting the dataset
   */
  @Test
  public void testDataSetGridQueryingFilterBbox() throws InterruptedException {

    // un test savec un filtre string
    String params = "c[0]=MAPPANEL|TestDictionary,coordinates|0.0,40.0,4.0,44.0&start=0&limit=300&media=json";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("URI dataset", "URI ");
      parameters.put("c[i]", "filter of form");
      parameters.put("MAPPANEL", "type: MAPPANEL");
      parameters.put("TestDictionary,coordinates", "Dictionary name, concept name");
      parameters.put("0.0,40.0,4.0,44.0", "Coordinates of the top left and bottom right points of the boundary box");
      retrieveDocAPI(params, parameters, datasetJeoEntry.getSitoolsAttachementForUsers());
    }
    else {
      String result = retrieve(params, datasetJeoEntry.getSitoolsAttachementForUsers());
      String expected = "{\"success\": true,\r\n" + "\"total\":2,\r\n";
      result = result.replaceAll("[\r\n]+", "");
      expected = expected.replaceAll("[\r\n]+", "");
      if (!result.startsWith(expected)) {
        Assert.fail(result + "<>" + expected);
      }
    }
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
  private DataSet createDatasetUsersPG(String id) throws InterruptedException {
    DataSet item = CreateDatasetUtil.createDatasetUsersPG(id, urlAttachDatasetUsers);

    persistDataset(item);

    DictionaryMapping dicoMapping = createDicoMappingForUsers(dictionaryId);
    addDictionaryMapping(item.getId(), dicoMapping);

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
  private DataSet createDatasetFusePG(String id) throws InterruptedException {
    DataSet item = CreateDatasetUtil.createDatasetFusePG(id, urlAttachDatasetFuse);

    persistDataset(item);

    DictionaryMapping dicoMapping = createDicoMappingForFuse(dictionaryId);
    addDictionaryMapping(item.getId(), dicoMapping);

    changeStatus(item.getId(), "/start");

    return item;

  }

  /**
   * Create and activate a Dataset for Postgresql datasource.This dataset is created on the sitools.jeo_entries table of
   * the CNES_SIG database *
   * 
   * @param id
   *          the dataset ID
   * @return the DataSet created
   * @throws InterruptedException
   *           if something is wrong
   */
  private DataSet createDatasetJeoEntryPG(String id) throws InterruptedException {
    DataSet item = CreateDatasetUtil.createDatasetJeoEntryPG(id, urlAttachDatasetJeoEntry);

    persistDataset(item);
    DictionaryMapping dicoMapping = createDicoMappingForJeoEntry(dictionaryId);
    addDictionaryMapping(item.getId(), dicoMapping);
    changeStatus(item.getId(), "/start");

    return item;

  }

  private DataSet createDatasetHeaders(String id) throws InterruptedException {
    DataSet item = CreateDatasetUtil.createDatasetHeadersSimplePG(id, urlAttachDatasetHeaders);

    persistDataset(item);

    DictionaryMapping dicoMapping = createDicoMappingForHeaders(dictionaryId);
    addDictionaryMapping(item.getId(), dicoMapping);

    changeStatus(item.getId(), "/start");

    return item;
  }

  // ---------------------------------------------------------
  // Methods for testCommonConcepts

  /**
   * Create a DictionaryMapping object
   * 
   * @param dictionaryId
   *          the id of the Dictionary
   * 
   * @return the dictionaryMapping
   */
  public DictionaryMapping createDicoMappingForUsers(String dictionaryId) {
    /** DICTIONARY MAPPING */

    DictionaryMapping dicoMapping = new DictionaryMapping();
    dicoMapping.setDictionaryId(dictionaryId);

    ArrayList<ColumnConceptMapping> mappings = new ArrayList<ColumnConceptMapping>();

    ColumnConceptMapping colConMapping = new ColumnConceptMapping();
    colConMapping.setColumnId("firstname");
    colConMapping.setConceptId("0");
    mappings.add(colConMapping);

    dicoMapping.setMapping(mappings);

    return dicoMapping;
  }

  /**
   * Create a DictionaryMapping object
   * 
   * @param dictionaryId
   *          the id of the Dictionary
   * 
   * @return the dictionaryMapping
   */
  public DictionaryMapping createDicoMappingForFuse(String dictionaryId) {
    /** DICTIONARY MAPPING */

    DictionaryMapping dicoMapping = new DictionaryMapping();
    dicoMapping.setDictionaryId(dictionaryId);

    ArrayList<ColumnConceptMapping> mappings = new ArrayList<ColumnConceptMapping>();

    ColumnConceptMapping colConMapping = new ColumnConceptMapping();
    colConMapping.setColumnId("cycle");
    colConMapping.setConceptId("1");
    mappings.add(colConMapping);

    dicoMapping.setMapping(mappings);

    return dicoMapping;
  }

  /**
   * Create a DictionaryMapping object
   * 
   * @param dictionaryId
   *          the id of the Dictionary
   * 
   * @return the dictionaryMapping
   */
  public DictionaryMapping createDicoMappingForHeaders(String dictionaryId) {
    /** DICTIONARY MAPPING */

    DictionaryMapping dicoMapping = new DictionaryMapping();
    dicoMapping.setDictionaryId(dictionaryId);

    ArrayList<ColumnConceptMapping> mappings = new ArrayList<ColumnConceptMapping>();

    ColumnConceptMapping colConMapping = new ColumnConceptMapping();
    colConMapping.setColumnId("x_pos");
    colConMapping.setConceptId("3");
    mappings.add(colConMapping);

    colConMapping = new ColumnConceptMapping();
    colConMapping.setColumnId("y_pos");
    colConMapping.setConceptId("4");
    mappings.add(colConMapping);

    colConMapping = new ColumnConceptMapping();
    colConMapping.setColumnId("z_pos");
    colConMapping.setConceptId("5");
    mappings.add(colConMapping);

    colConMapping = new ColumnConceptMapping();
    colConMapping.setColumnId("dateobs");
    colConMapping.setConceptId("6");
    mappings.add(colConMapping);

    dicoMapping.setMapping(mappings);

    dicoMapping.setDefaultDico(true);

    return dicoMapping;
  }

  /**
   * Create a DictionaryMapping object
   * 
   * @param dictionaryId
   *          the id of the Dictionary
   * 
   * @return the dictionaryMapping
   */
  public DictionaryMapping createDicoMappingForJeoEntry(String dictionaryId) {
    /** DICTIONARY MAPPING */

    DictionaryMapping dicoMapping = new DictionaryMapping();
    dicoMapping.setDictionaryId(dictionaryId);

    ArrayList<ColumnConceptMapping> mappings = new ArrayList<ColumnConceptMapping>();

    ColumnConceptMapping colConMapping = new ColumnConceptMapping();
    colConMapping.setColumnId("coord");
    colConMapping.setConceptId("2");
    mappings.add(colConMapping);

    dicoMapping.setMapping(mappings);

    return dicoMapping;
  }

  /**
   * Add a DictionaryMapping to a dataset
   * 
   * @param datasetId
   *          the id of the dataset
   * @param dicoMapping
   *          the dico to add
   */
  private void addDictionaryMapping(String datasetId, DictionaryMapping dicoMapping) {
    String url = getDatasetBaseUrl() + "/" + datasetId + "/mappings/" + dicoMapping.getDictionaryId();
    Representation repr = GetRepresentationUtils.getRepresentationDicoMapping(dicoMapping, getMediaTest());
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("DatasetId", "The dataset Identifier");
      parameters.put("DictionaryId", "The dictionary Identifier");
      String template = getDatasetBaseUrl() + "/%DatasetId%/mappings/%DictionaryId%";

      putDocAPI(url, "", repr, parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = null;
      try {
        result = cr.put(repr, getMediaTest());
        Response response = GetResponseUtils.getResponseDicoMapping(getMediaTest(), result, DictionaryMapping.class);
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(response.getItem());
      }
      finally {
        RIAPUtils.exhaust(repr);
      }
    }
  }

}
