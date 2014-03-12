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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.engine.Engine;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.datasource.jdbc.model.AttributeValue;
import fr.cnes.sitools.datasource.jdbc.model.Record;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.AttributeValueConverter;
import fr.cnes.sitools.utils.GetRepresentationUtils;
import fr.cnes.sitools.utils.GetResponseUtils;

/**
 * 
 * Test DataSetApplication Rest API
 * 
 * @since UserStory : ADM DataSets, Sprint : 4
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public abstract class AbstractDataSetManagerTestCase extends AbstractSitoolsServerTestCase {

  /** Dataset identifier for postgres tests */
  protected static final String DATASET_ID = "myds";

  /** Dataset identifier for mysql tests */
  protected static final String DATASET_ID_FUSE = "testmysql";

  /** Class logger */
  private static Logger logger = Engine.getLogger(AbstractDataSetManagerTestCase.class.getName());

  /**
   * Invoke POST
   * 
   * @param item
   *          DataSet
   */
  public void create(DataSet item) {
    JsonRepresentation rep = new JsonRepresentation(item);
    ClientResource cr = new ClientResource(getUrl());
    Representation result = cr.post(rep, getMediaTest());
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);

    Response response = getResponse(getMediaTest(), result, DataSet.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());

    DataSet rs = (DataSet) response.getItem();
    assertEquals(rs, item);
    RIAPUtils.exhaust(result);
    cr.release();
  }

  /**
   * Create object
   * 
   * @param id
   *          new instance identifier
   * @return a DataSet instance for tests
   */
  public DataSet createObject(String id) {
    DataSet item = new DataSet();
    item.setId(id);
    item.setName("myds");
    item.setDescription("dataset");

    return item;
  }

  /**
   * Invoke GET
   * 
   * @param params
   *          other selection parameters
   * @param datasetUrl
   *          DataSet identifier
   * @return String
   */
  public String retrieve(String params, String datasetUrl) {
    String uri = getHostUrl() + datasetUrl + "/records?" + params;
    ClientResource cr = new ClientResource(uri);
    logger.finest("URI: " + uri);
    Representation result = cr.get(getMediaTest());
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    String response = "";
    try {
      response = result.getText();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      Assert.fail("No result");
    }
    RIAPUtils.exhaust(result);
    cr.release();
    return response;
  }

  /**
   * Invoke GET
   * 
   * @param params
   *          String
   * @param parameters
   *          Map<String, String>
   * @param urlDataset
   *          the url of the dataset
   */
  public void retrieveDocAPI(String params, Map<String, String> parameters, String urlDataset) {
    String uri = getHostUrl() + urlDataset + "/records?" + params;
    ClientResource cr = new ClientResource(uri);
    logger.finest("URI: " + uri);
    Representation result = cr.get(getMediaTest());
    docAPI.appendSection("Format");
    // url type
    String urlLocal = getHostUrl() + "/" + "%URI dataset%" + "/records?params...";
    // request
    ClientResource crLocal = new ClientResource(urlLocal);
    docAPI.appendRequest(Method.GET, crLocal);
    // parameters
    docAPI.appendParameters(parameters);
    docAPI.appendSection("Example");
    docAPI.appendRequest(Method.GET, cr);
    // response
    docAPI.appendResponse(result);
    cr.release();
    crLocal.release();
  }

  /**
   * Assert 2 datasets are identicals
   * 
   * @param item
   *          expected
   * @param rs
   *          new dataset
   */
  public void assertEqualsDs(DataSet item, DataSet rs) {
    assertEquals(rs.getName(), item.getName());
    assertEquals(rs.getDescription(), item.getDescription());
    // TODO assert all properties

  }

  /**
   * Create a record from the specified details
   * 
   * @param recDetails
   *          the map of the details of the record
   * @return a record with the specified details
   */
  protected Record getRecord(HashMap<String, String> recDetails) {

    Record record = new Record();

    AttributeValue attr;

    for (Map.Entry<String, String> entry : recDetails.entrySet()) {
      attr = new AttributeValue();
      attr.setName(entry.getKey());
      attr.setValue(entry.getValue());
      record.getAttributeValues().add(attr);
    }

    return record;

  }

  /**
   * Query a dataset given by the urlAttach
   * 
   * @param urlAttach
   *          the url of the dataset
   */
  protected void queryDataset(String urlAttach) {
    String url = getHostUrl() + urlAttach + "/records";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = null;
      try {
        result = cr.get(getMediaTest());

        assertNotNull(result);
        assertTrue(cr.getStatus().isSuccess());

        try {
          assertNotNull(result.getText());
        }
        catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      finally {
        RIAPUtils.exhaust(result);
      }
    }

  }

  /**
   * Query the dataset with the given attachment and id, an expected Record must be provided as well to check if the
   * queried one is ok
   * 
   * @param urlAttach
   *          the url of the dataset
   * @param id
   *          the id of the record
   * @param expectedRecord
   *          the record expected
   */
  protected void queryDataset(String urlAttach, String id, Record expectedRecord) {
    try {
      id = URLEncoder.encode(id, "UTF-8");

      String url = getHostUrl() + urlAttach + "/records/" + id;
      if (docAPI.isActive()) {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        retrieveDocAPI(url, "", parameters, url);
      }
      else {
        ClientResource cr = new ClientResource(url);
        Representation result = null;
        try {
          result = cr.get(getMediaTest());

          assertNotNull(result);
          assertTrue(cr.getStatus().isSuccess());

          Response repr = getResponseRecord(getMediaTest(), result, Record.class);

          assertNotNull(repr);
          assertTrue(repr.getSuccess());

          assertNotNull(repr.getItem());

          assertTrue(repr.getItem() instanceof Record);

          Record currentRec = (Record) repr.getItem();
          if (expectedRecord != null) {
            assertRecord(expectedRecord, currentRec);
          }
        }
        finally {
          RIAPUtils.exhaust(result);
        }
      }
    }
    catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * Query the dataset using the DatasetRequestUrl.
   * 
   * @param urlAttach
   *          the dataset url attachment
   * @param params
   *          the params of the request
   * @param expectedRecord
   *          the expected Record to compare
   */
  protected void queryDatasetRequestUrl(String urlAttach, String params, Record expectedRecord, Boolean compareRecords) {
    String url = getHostUrl() + urlAttach + params;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = null;
      try {
        result = cr.get(getMediaTest());

        assertNotNull(result);
        assertTrue(cr.getStatus().isSuccess());

        ArrayList<Record> records = getRecords(getMediaTest(), result);

        assertNotNull(records);
        assertEquals(1, records.size());

        if (compareRecords) {
          assertRecord(expectedRecord, records.get(0));
        }

      }
      finally {
        RIAPUtils.exhaust(result);
        cr.release();
      }
    }

  }

  /**
   * Query the dataset using the DatasetRequestUrl and assert that the number of record is nbrecordsExpected
   * 
   * @param urlAttach
   *          the dataset url attachment
   * @param params
   *          the params of the request
   * @param nbrecordsExpected
   *          the number of expected Records
   */
  protected void queryDatasetRequestUrl(String urlAttach, String params, int nbrecordsExpected) {
    String url = getHostUrl() + urlAttach + params;
    ClientResource cr = new ClientResource(url);
    Representation result = null;
    try {
      result = cr.get(getMediaTest());

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      ArrayList<Record> records = getRecords(getMediaTest(), result);

      assertNotNull(records);
      assertEquals(nbrecordsExpected, records.size());

    }
    finally {
      RIAPUtils.exhaust(result);
      cr.release();
    }
  }

  /**
   * Query the dataset using the DatasetRequestUrl and return the list of Records
   * 
   * @param urlAttach
   *          the dataset url attachment
   * @param params
   *          the params of the request
   * @return a List of Record
   */
  protected List<Record> queryDatasetRequestUrl(String urlAttach, String params) {
    String url = getHostUrl() + urlAttach + params;
    ClientResource cr = new ClientResource(url);
    Representation result = null;
    try {
      result = cr.get(getMediaTest());

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      ArrayList<Record> records = getRecords(getMediaTest(), result);
      return records;
    }
    finally {
      RIAPUtils.exhaust(result);
      cr.release();
    }
  }

  /**
   * Change the status of a dataset corresponding to the given id. The status is given by the statusUrl Correct
   * statusUrl are /start or /stop
   * 
   * @param id
   *          the dataset identifier
   * @param statusUrl
   *          the url of the status needed
   */
  protected void changeStatus(String id, String statusUrl) {
    // TODO Auto-generated method stub
    String url = getUrl() + "/" + id + statusUrl;
    Representation repr = new StringRepresentation("");
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("DatasetId", "The dataset Identifier");
      String template = String.format(getUrl(), "%DatasetId%");
      putDocAPI(url, "", repr, parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = null;
      try {
        result = cr.put(repr, getMediaTest());

        assertNotNull(result);
        assertTrue(cr.getStatus().isSuccess());

        Response response = getResponse(getMediaTest(), result, DataSet.class);
        assertNotNull(response);
        assertTrue(response.getSuccess());
        if (statusUrl.equals("/start")) {
          assertEquals("dataset.update.success", response.getMessage());
        }
        else if (statusUrl.equals("/stop")) {
          assertEquals("dataset.stop.success", response.getMessage());
        }
      }
      finally {
        RIAPUtils.exhaust(result);
        cr.release();
      }
    }

  }

  /**
   * Delete a Dataset corresponding to the given id
   * 
   * @param id
   *          the dataset identifier
   * @throws InterruptedException
   */
  public void deleteDataset(String id) throws InterruptedException {

    // delete the dataset
    String url = getUrl() + "/" + id;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("DatasetId", "The dataset Identifier");
      parameters.put("<POST>", "DataSet Object");
      String template = String.format(getUrl(), "%DatasetId%");
      deleteDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = null;
      try {
        result = cr.delete(getMediaTest());

        assertNotNull(result);
        assertTrue(cr.getStatus().isSuccess());

        Response response = getResponse(getMediaTest(), result, DataSet.class);
        assertNotNull(response);
        assertTrue(response.getSuccess());
        Thread.sleep(2000);

      }
      catch (Exception e) {
        System.out.println("can not find dataset with id " + id + " to delete");
      }
      finally {
        RIAPUtils.exhaust(result);
        cr.release();
      }
    }
  }

  /**
   * Assert if expected Record and actual Record are the same
   * 
   * @param expected
   *          the expected Record
   * @param actual
   *          the actual Record
   */
  protected void assertRecord(Record expected, Record actual) {

    assertNotNull(expected);
    assertNotNull(actual);

    assertNotNull(expected.getAttributeValues());
    assertNotNull(actual.getAttributeValues());

    assertEquals(expected.getAttributeValues().size(), actual.getAttributeValues().size());

    AttributeValue attrActual;
    AttributeValue attrExpected;

    for (int i = 0; i < expected.getAttributeValues().size(); i++) {
      attrExpected = expected.getAttributeValues().get(i);

      attrActual = getAttributeValue(attrExpected.getName(), actual);
      assertAttributeValue(attrExpected, attrActual);

    }

  }

  /**
   * Return the attributeValue corresponding to the given key in the given record
   * 
   * @param key
   *          the key
   * @param rec
   *          the record
   * @return the attributeValue corresponding to the given key in the given record
   */
  protected AttributeValue getAttributeValue(String key, Record rec) {
    List<AttributeValue> list = rec.getAttributeValues();
    AttributeValue ret = null;

    for (Iterator<AttributeValue> iterator = list.iterator(); iterator.hasNext() && ret == null;) {
      AttributeValue attributeValue = iterator.next();
      if (attributeValue.getName().equals(key)) {
        ret = attributeValue;
      }
    }
    return ret;
  }

  /**
   * Assert if expected assertAttributeValue and actual assertAttributeValue are the same
   * 
   * @param attrExpected
   *          the expected AttributeValue
   * @param attrActual
   *          the actual AttributeValue
   */
  protected void assertAttributeValue(AttributeValue attrExpected, AttributeValue attrActual) {
    assertNotNull(attrActual);
    assertNotNull(attrExpected);

    assertEquals(attrExpected.getName(), attrActual.getName());
    assertEquals("Attribute with name : " + attrExpected.getName(), attrExpected.getValue(), attrActual.getValue());

  }

  /**
   * Create the given dataset on the server
   * 
   * @param item
   *          the Dataset to create
   * @throws InterruptedException
   */
  public void persistDataset(DataSet item) throws InterruptedException {
    // TODO Auto-generated method stub
    String url = getUrl();
    Representation repr = getRepresentation(item, getMediaTest());

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("DatasetId", "The dataset Identifier");
      parameters.put("<POST>", "DataSet Object");
      String template = String.format(getUrl(), "%DatasetId%");
      postDocAPI(url, "", repr, parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = null;
      try {
        result = cr.post(repr, getMediaTest());

        assertNotNull(result);
        assertTrue(cr.getStatus().isSuccess());

        Response response = getResponse(getMediaTest(), result, DataSet.class);
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(response.getItem());

        DataSet ds = (DataSet) response.getItem();
        Thread.sleep(2000);
        assertEquals(item.getId(), ds.getId());
        assertEquals("NEW", ds.getStatus());
      }
      finally {
        RIAPUtils.exhaust(result);
        cr.release();
      }
    }
  }

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_URL);
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
      xstream.alias("attributeValues", ArrayList.class);

      xstream.alias("attribute", AttributeValue.class);

      xstream.registerConverter(new AttributeValueConverter());

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == Record.class) {
          xstream.aliasField("record", Response.class, "item");
          xstream.aliasField("uri", Record.class, "id");
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

    xstream.getMapper();

    xstream.autodetectAnnotations(false);

    if (media.isCompatible(MediaType.APPLICATION_XML)) {

      xstream.alias("root", ArrayList.class);
      xstream.alias("record", Record.class);
      xstream.alias("attributeValue", AttributeValue.class);

      xstream.alias("response", Response.class);

      xstream.registerConverter(new AttributeValueConverter());

      SitoolsXStreamRepresentation<ArrayList<Record>> rep = new SitoolsXStreamRepresentation<ArrayList<Record>>(
          representation);
      rep.setXstream(xstream);

      ArrayList<Record> response = rep.getObject("root");
      // remove the Response
      response.remove(response.size() - 1);

      return response;

    }

    else if (media.isCompatible(MediaType.APPLICATION_JSON)) {

      // hand made JSON deserialization
      try {
        String result = representation.getText();
        JSONObject json = new JSONObject(result);

        ArrayList<Record> recs = new ArrayList<Record>();

        JSONArray records = json.getJSONArray("data");

        AttributeValue attr;
        JSONObject recordJSON;
        Record record = null;
        String[] names = null;
        for (int i = 0; i < records.length(); i++) {
          recordJSON = records.getJSONObject(i);

          recordJSON.remove("uri");

          names = JSONObject.getNames(recordJSON);
          record = new Record();
          if (names != null) {
            for (int j = 0; j < names.length; j++) {
              attr = new AttributeValue();
              attr.setName(names[j]);
              attr.setValue(recordJSON.get(names[j]));
              record.getAttributeValues().add(attr);

            }
          }
          recs.add(record);
        }

        return recs;
      }
      catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return null;
      }
      catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return null;
      }

    }
    else {
      Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null; // TODO complete test with ObjectRepresentation
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
  public static Representation getRepresentation(DataSet item, MediaType media) {
    return GetRepresentationUtils.getRepresentationDataset(item, media);
  }

}
