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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.opensearch.model.Opensearch;
import fr.cnes.sitools.dataset.opensearch.model.OpensearchColumn;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * AbstractOpensearchTestCase
 * 
 * @author m.gond (AKKA Technologies)
 * 
 *         En cas d'echec du test : Supprimer le fichier int@2.xml du repertoire TESTS\opensearch correspondant à l'osId
 *         ci-dessous
 */
@Ignore
public abstract class AbstractOpensearchSearchTestCase extends SitoolsServerTestCase {

  /** Opensearch id */
  private String osId = "1d9e040c-5fb4-4e7e-af39-978dc4500183";
  /** The number of iteration to wait for the indexation to finish */
  private int nbMaxIteration = 10;
  /** The time to wait between to status call */
  private int timeToWait = 1000;
  /** The query text */
  private String queryText = "*:*";
  /** The urlAttachment */
  private String urlAttachment = "/fuse";
  /** The name of the opensearch */
  private String osName = "fuseOs";
  /** The description of the opensearch */
  private String osDescription = "fuse os description";
  /** The suggest text */
  private String suggestText = "fu";

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_URL) + "/%s"
        + SitoolsSettings.getInstance().getString(Consts.APP_OPENSEARCH_URL);
  }

  /**
   * Test getting converter list.
   */
  @Test
  public void test() {
    docAPI.setActive(false);
    try {

      this.assertNone(osId);

      Opensearch os = createObject(osId);

      createOs(os);

      updateOs(os);

      activateOs(osId);
      // cancelOs(osId);
      // cancelOs(osId);
      //
      // activateOs(osId);

      waitForActivating(osId);
      if (getMediaTest().isCompatible(MediaType.APPLICATION_XML)) {
        getDescription(osId);
      }

      query(urlAttachment, queryText);

      suggest(urlAttachment, suggestText);

      stopOs(osId);

      delete(osId);

      this.assertNone(osId);

    }
    catch (IOException e) {
      e.printStackTrace();
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Test getting converter list.
   */
  @Test
  public void testAPI() {
    docAPI.setActive(true);
    try {
      docAPI.appendSubChapter("Get Opensearch for a given DatasetId", "getOs");
      this.assertNone(osId);

      Opensearch os = createObject(osId);
      docAPI.appendSubChapter("Create an Opensearch for a DataSet", "createOs");
      createOs(os);
      docAPI.appendSubChapter("Update an Opensearch", "updateOs");
      updateOs(os);

      docAPI.appendSubChapter("Activate Opensearch", "activate1");
      activateOs(osId);

      // docAPI.appendSubChapter("Cancel the activation", "cancel1");
      // cancelOs(osId);
      // docAPI.appendSubChapter("Cancel again the activation", "cancel2");
      // cancelOs(osId);
      // docAPI.appendSubChapter("Activate Opensearch", "activate2");
      // activateOs(osId);

      waitForActivating(osId);

      if (getMediaTest().isCompatible(MediaType.APPLICATION_XML)) {
        docAPI.appendSubChapter("Get opensearch description file (Only XML)", "description");
        getDescription(osId);
      }
      docAPI.appendSubChapter("Query Opensearch", "query");
      query(urlAttachment, queryText);
      docAPI.appendSubChapter("Query suggest Opensearch", "suggest");
      suggest(urlAttachment, suggestText);

      docAPI.appendSubChapter("Stop Opensearch", "stop");
      stopOs(osId);
      docAPI.appendSubChapter("Delete Opensearch", "delete");
      delete(osId);
      docAPI.appendSubChapter("Get Opensearch for a given DatasetId", "getOs2");
      this.assertNone(osId);

    }
    catch (IOException e) {
      e.printStackTrace();
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
    docAPI.close();
  }

  /**
   * Create the Opensearch
   * 
   * @param os
   *          the opensearchId
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if there is an IOException
   */
  private void createOs(Opensearch os) throws IOException {
    String url = String.format(getUrl(), os.getId());
    Representation repr = getRepresentation(os, getMediaTest());
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("DatasetId", "The dataset Identifier");
      parameters.put("<POST>", "Opensearch Object");
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

        Response response = getResponse(getMediaTest(), result, Opensearch.class);
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(response.getItem());

        Opensearch osReturn = (Opensearch) response.getItem();
        assertEquals(osName, osReturn.getName());
        assertEquals(osDescription, osReturn.getDescription());
      }
      finally {
        RIAPUtils.exhaust(result);
        cr.release();
      }

      // System.out.println("RESEASE : " + nbCalls ); RIAPUtils.exhaust(result);
    }

  }

  /**
   * Update an Opensearch object
   * 
   * @param os
   *          the opensearch to update
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if there is an exception at the end
   */
  private void updateOs(Opensearch os) throws IOException {
    os.setName(osName + "_updated");
    os.setDescription(osDescription + "_updated");
    String url = String.format(getUrl(), os.getId());
    Representation repr = getRepresentation(os, getMediaTest());
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("DatasetId", "The dataset Identifier");
      parameters.put("<PUT>", "Opensearch Object");
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

        Response response = getResponse(getMediaTest(), result, Opensearch.class);
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(response.getItem());

        Opensearch osReturn = (Opensearch) response.getItem();
        assertEquals(osName + "_updated", osReturn.getName());
        assertEquals(osDescription + "_updated", osReturn.getDescription());
      }
      finally {
        RIAPUtils.exhaust(result);
        cr.release();
      }
      // System.out.println("RESEASE : " + nbCalls ); RIAPUtils.exhaust(result);
    }

  }

  /**
   * Activate the Opensearch
   * 
   * @param osId2
   *          the opensearchId
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if there is an IOException
   */
  private void activateOs(String osId2) throws IOException {
    String url = String.format(getUrl(), osId) + "/start";
    System.out.println("ACTIVATE");
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("OpensearchId", "The opensearch Identifier");
      String template = String.format(getUrl(), "%OpensearchId%") + "/start";
      putDocAPI(url, "", new StringRepresentation(""), parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = null;
      try {

        result = cr.put(null, getMediaTest());

        assertNotNull(result);
        assertTrue(cr.getStatus().isSuccess());

        Response response = getResponse(getMediaTest(), result, Opensearch.class);
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertNotNull(response.getItem());

        Opensearch os = (Opensearch) response.getItem();
        assertEquals("PENDING", os.getStatus());
      }
      finally {
        RIAPUtils.exhaust(result);

      }
      // System.out.println("RESEASE : " + nbCalls ); RIAPUtils.exhaust(result);
    }

  }

  /**
   * Query the opensearch
   * 
   * @param urlAttachment
   *          the urlAttachment of the opensearch
   * @param queryText
   *          the queryText
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if there is an exception releasing
   *           resources
   */
  private void query(String urlAttachment, String queryText) throws IOException {
    String url = getHostUrl() + urlAttachment + "/opensearch/search?q=" + queryText;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template = String.format(getUrl(), "%OpensearchId%");
      docAPI.setMediaTest(MediaType.APPLICATION_ALL_XML);
      retrieveDocAPI(url, "", parameters, template);
      docAPI.setMediaTest(MediaType.APPLICATION_JSON);

    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = null;
      try {

        result = cr.get(getMediaTest());

        assertNotNull(result);
        assertTrue(cr.getStatus().isSuccess());
      }
      finally {

        RIAPUtils.exhaust(result);
        cr.release();
      }
      // status = os.getStatus();
      // System.out.println("RESEASE : " + nbCalls ); RIAPUtils.exhaust(result);
    }

  }

  /**
   * Get the description file
   * 
   * @param osId
   *          the opensearch id
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if there is an exception releasing the
   *           ressources
   */
  private void getDescription(String osId) throws IOException {
    String url = getHostUrl() + urlAttachment + "/opensearch.xml";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template = String.format(getUrl(), "%OpensearchId%");
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = null;
      try {

        result = cr.get(getMediaTest());

        assertNotNull(result);
        assertTrue(cr.getStatus().isSuccess());

      }
      finally {

        RIAPUtils.exhaust(result);
        cr.release();
      }
      // status = os.getStatus();
      // System.out.println("RESEASE : " + nbCalls ); RIAPUtils.exhaust(result);
    }
  }

  /**
   * Test the suggest function of the opensearch
   * 
   * @param urlAttachment2
   *          the urlAttachment of the opensearch
   * @param suggestText2
   *          the queryText
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if there is an exception releasing
   *           resources
   */
  private void suggest(String urlAttachment2, String suggestText2) throws IOException {
    String url = getHostUrl() + urlAttachment2 + "/opensearch/suggest?q=" + suggestText2;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template = String.format(getUrl(), "%OpensearchId%");
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = null;
      try {

        result = cr.get(getMediaTest());

        assertNotNull(result);
        assertTrue(cr.getStatus().isSuccess());

        if (getMediaTest().equals(MediaType.APPLICATION_JSON)) {
          // general method, same as with data binding
          ObjectMapper mapper = new ObjectMapper();
          // (note: can also use more specific type, like ArrayNode or ObjectNode!)
          JsonNode rootNode = mapper.readValue(result.getStream(), JsonNode.class); // src can be a File, URL,
                                                                                    // InputStream etc
          JsonNode success = rootNode.get("success");
          assertEquals("true", success.getTextValue());

        }
      }
      finally {

        RIAPUtils.exhaust(result);
        cr.release();
      }
      // status = os.getStatus();
      // System.out.println("RESEASE : " + nbCalls ); RIAPUtils.exhaust(result);
    }

  }

  /**
   * Activate the Opensearch
   * 
   * @param osId2
   *          the opensearchId
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if there is an IOException
   */
  private void stopOs(String osId2) throws IOException {
    String url = String.format(getUrl(), osId) + "/stop";
    System.out.println("STOP");
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("OpensearchId", "The opensearch Identifier");
      String template = String.format(getUrl(), "%OpensearchId%") + "/stop";
      putDocAPI(url, "", new StringRepresentation(""), parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = null;
      try {

        result = cr.put(null, getMediaTest());

        assertNotNull(result);
        assertTrue(cr.getStatus().isSuccess());

        Response response = getResponse(getMediaTest(), result, Opensearch.class);

        assertTrue(response.getSuccess());

        assertNotNull(response.getItem());

        Opensearch os = (Opensearch) response.getItem();
        assertEquals("INACTIVE", os.getStatus());
      }
      finally {

        RIAPUtils.exhaust(result);
        cr.release();
      }
      // System.out.println("RESEASE : " + nbCalls ); RIAPUtils.exhaust(result);
    }

  }

  /**
   * Activate the Opensearch
   * 
   * @param osId2
   *          the opensearchId
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if there is an IOException
   */
  private void cancelOs(String osId2) throws IOException {
    String url = String.format(getUrl(), osId) + "/cancel";
    System.out.println("CANCEL");
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("OpensearchId", "The opensearch Identifier");
      String template = String.format(getUrl(), "%OpensearchId%") + "/cancel";
      putDocAPI(url, "", new StringRepresentation(""), parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = null;
      try {

        result = cr.put(null, getMediaTest());

        assertNotNull(result);
        assertTrue(cr.getStatus().isSuccess());

        Response response = getResponse(getMediaTest(), result, Opensearch.class);
        assertTrue(response.getSuccess());
        assertNotNull(response.getMessage());

        assertEquals("opensearch.cancel.successfull", response.getMessage());
      }
      finally {

        RIAPUtils.exhaust(result);
        cr.release();
      }
      // System.out.println("RESEASE : " + nbCalls ); RIAPUtils.exhaust(result);
    }
  }

  /**
   * Wait until the Opensearch is Activated
   * 
   * @param osId
   *          the Opensearch id
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if there is an IOException
   * @throws InterruptedException
   *           if there is an error while doing Thread.sleep
   */
  private void waitForActivating(String osId) throws IOException, InterruptedException {
    String status = null;
    for (int i = 0; i < nbMaxIteration && !"ACTIVE".equals(status); i++) {
      Thread.sleep(timeToWait);
      status = checkStatus(osId);
    }
  }

  /**
   * Check the status and return it
   * 
   * @param osId
   *          the opensearch id
   * @return the Status of the given osId
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if there is an IOException
   */
  private String checkStatus(String osId) throws IOException {
    String url = String.format(getUrl(), osId);
    String status = null;

    ClientResource cr = new ClientResource(url);
    Representation result = null;
    try {
      result = cr.get(getMediaTest());

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Opensearch.class);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
      Opensearch os = (Opensearch) response.getItem();

      status = os.getStatus();
      // System.out.println("RESEASE : " + nbCalls ); RIAPUtils.exhaust(result);
    }
    finally {
      RIAPUtils.exhaust(result);
      cr.release();
    }
    return status;
  }

  /**
   * Delete an Opensearch object
   * 
   * @param osId
   *          the opensearchId to Delete
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if there is an exception at the end
   */
  private void delete(String osId) throws IOException {
    String url = String.format(getUrl(), osId);

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("DatasetId", "The dataset Identifier");
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

        Response response = getResponse(getMediaTest(), result, Opensearch.class);
        assertNotNull(response);
        assertTrue(response.getSuccess());
        assertEquals("opensearch.delete.success", response.getMessage());

        // System.out.println("RESEASE : " + nbCalls );
        // RIAPUtils.exhaust(result);
      }
      finally {
        RIAPUtils.exhaust(result);
        cr.release();
      }
    }

  }

  /**
   * Invokes GET and asserts result response is an empty array.
   * 
   * @param datasetId
   *          Opensearch needed to set datasetId (= item.id) in the url
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS
   */
  public void assertNone(String datasetId) throws IOException {
    String url = String.format(getUrl(), datasetId);

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

        Response response = getResponse(getMediaTest(), result, Opensearch.class);
        assertTrue(response.getSuccess());

        assertNull(response.getItem());
      }
      finally {
        RIAPUtils.exhaust(result);
        cr.release();
      }
      // System.out.println("RESEASE : " + nbCalls ); RIAPUtils.exhaust(result);
    }
  }

  /**
   * Create object
   * 
   * @param id
   *          new instance identifier
   * @return a DataSet instance for tests
   */
  public Opensearch createObject(String id) {
    Opensearch item = new Opensearch();
    item.setName(osName);
    item.setDescription(osDescription);
    item.setId(id);
    item.setStatus("INACTIVE");

    ArrayList<OpensearchColumn> indexedColumn = new ArrayList<OpensearchColumn>();
    OpensearchColumn col1 = new OpensearchColumn();
    col1.setIdColumn("b445a74c-b798-4db0-98b0-d9ae9a2f2515");
    OpensearchColumn col2 = new OpensearchColumn();
    col2.setIdColumn("9e68af23-c32d-4b09-9d54-d5943c93dff1");
    OpensearchColumn col3 = new OpensearchColumn();
    col3.setIdColumn("d6261b17-4ad8-40ea-8f89-224c46083f8b");
    col3.setType("string");

    indexedColumn.add(col1);
    indexedColumn.add(col2);
    indexedColumn.add(col3);

    item.setIndexedColumns(indexedColumn);

    item.setGuidField("d6261b17-4ad8-40ea-8f89-224c46083f8b");
    item.setTitleField("b445a74c-b798-4db0-98b0-d9ae9a2f2515");
    item.setPubDateField("9e68af23-c32d-4b09-9d54-d5943c93dff1");

    item.setUniqueKey("d6261b17-4ad8-40ea-8f89-224c46083f8b");
    item.setDefaultSearchField("b445a74c-b798-4db0-98b0-d9ae9a2f2515");

    ArrayList<String> keywordColumn = new ArrayList<String>();
    keywordColumn.add("targname");
    keywordColumn.add("dateobs");

    item.setKeywordColumns(keywordColumn);

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
      xstream.alias("opensearch", Opensearch.class);
      xstream.alias("opensearchColumn", OpensearchColumn.class);

      // xstream.aliasField("structures", DataSet.class, "structures");
      // xstream.aliasField("columnModel", DataSet.class, "columnModel");

      // xstream.addImplicitCollection(DataSet.class, "columnModel",
      // "columnModel", Column.class);

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
        if (dataClass == Opensearch.class) {
          xstream.aliasField("opensearch", Response.class, "item");
          if (media.isCompatible(MediaType.APPLICATION_JSON)) {
            xstream.addImplicitCollection(Opensearch.class, "indexedColumns", OpensearchColumn.class);
            xstream.addImplicitCollection(Opensearch.class, "keywordColumns", String.class);
          }
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
        Engine.getLogger(AbstractOpensearchSearchTestCase.class.getName()).warning("Only JSON is supported in tests");
        return null; // TODO complete test for XML, Object representation
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
  public static Representation getRepresentation(Opensearch item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<Opensearch>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<Opensearch> rep = new XstreamRepresentation<Opensearch>(media, item);
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
    // xstream.alias("opensearch", Opensearch.class);
    // xstream.alias("opensearchColumn", OpensearchColumn.class);
    // xstream.addImplicitCollection(Opensearch.class, "indexedColumns",
    // OpensearchColumn.class);
    // xstream.addImplicitCollection(Opensearch.class, "keywordColumns",
    // String.class);

  }

}
