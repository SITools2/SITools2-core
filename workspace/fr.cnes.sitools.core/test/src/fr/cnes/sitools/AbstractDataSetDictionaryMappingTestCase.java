 /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.dto.DataSetExpositionDTO;
import fr.cnes.sitools.dataset.model.ColumnConceptMapping;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.DictionaryMapping;
import fr.cnes.sitools.dictionary.DictionaryStoreInterface;
import fr.cnes.sitools.dictionary.model.Concept;
import fr.cnes.sitools.dictionary.model.ConceptTemplate;
import fr.cnes.sitools.dictionary.model.Dictionary;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.Property;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.CreateDatasetUtil;
import fr.cnes.sitools.utils.GetRepresentationUtils;
import fr.cnes.sitools.utils.GetResponseUtils;

/**
 * JUnit test to test the Dataset dictionary mapping It tests the CRUD, the notifications and the client side querying
 * 
 * @author m.gond
 */
public abstract class AbstractDataSetDictionaryMappingTestCase extends AbstractDataSetManagerTestCase {

  /** DictionaryId 1 */
  private String dictionaryId_1 = "429c7ff8-960a-4a49-9565-59d5c189ad22";
  /** DictionaryId 2 */
  private String dictionaryId_2 = "1111111-960a-4a49-9565-59d5c189ad22";

  /** datasetId */
  private String datasetId = "4a94c591-19fb-4422-a451-2ab2a0997715";

  /** The Dataset */
  private DataSet item;

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getDatasetBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_URL);
  }

  /**
   * Test the CRUD for both XML or JSON
   * 
   * @throws InterruptedException
   *           if there is an error
   */
  @Test
  public void testCRUD() throws InterruptedException {
    item = createDataset(datasetId, "/test/ds");
    docAPI.setActive(false);
    // TODO Auto-generated method stub
    try {
      assertNoneDicoMapping(item.getId());
      changeStatus(item.getId(), "/start");
      DictionaryMapping dicoMapping = createDicoMapping(dictionaryId_1);
      addDictionaryMappingError(item.getId(), dicoMapping);
      changeStatus(item.getId(), "/stop");
      addDictionaryMappingOk(item.getId(), dicoMapping);
      retriveMappings(item.getId(), 1);
      // lets add it again and delete it with the proper Method
      deleteDicoMapping(item.getId(), dicoMapping);
      assertNoneDicoMapping(item.getId());
      super.deleteDataset(item.getId());
      Thread.sleep(2000);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

  /**
   * Test with API
   */
  @Test
  public void testCRUDAPI() {
    try {
      // TODO Auto-generated method stub
      item = createDataset(datasetId, "/ds/test");
      docAPI.setActive(true);
      docAPI.appendChapter("Manipulating Dictionary Mappings on a Dataset");
      assertNoneDicoMapping(item.getId());
      changeStatus(item.getId(), "/start");
      DictionaryMapping dicoMapping = createDicoMapping(dictionaryId_1);
      docAPI.appendSubChapter("Add a mapping but fails because dataset active", "create_but_fail");
      addDictionaryMappingError(item.getId(), dicoMapping);
      changeStatus(item.getId(), "/stop");
      docAPI.appendSubChapter("Add a mapping", "create");
      addDictionaryMappingOk(item.getId(), dicoMapping);
      docAPI.appendSubChapter("Retrieve all the mappings", "retrieve");
      retriveMappings(item.getId(), 1);
      // clear dicoMapping
      dicoMapping = new DictionaryMapping();
      docAPI.appendSubChapter("Delete a mapping", "add empty");
      deleteDicoMapping(item.getId(), dicoMapping);
      assertNoneDicoMapping(item.getId());
      super.deleteDataset(item.getId());
      docAPI.close();
      Thread.sleep(2000);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Test the notifications on the dictionary mappings It tests that if a concept is deleted, the mappings corresponding
   * to that concept are deleted and that if a dictionary is deleted the mappings corresponding are also deleted
   */
  @Test
  public void testNotification() {
    docAPI.setActive(false);
    // TODO Auto-generated method stub
    try {
      item = createDataset(datasetId, "/test/ds");
      DictionaryMapping dicoMapping = createDicoMapping(dictionaryId_1);
      addDictionaryMappingOk(item.getId(), dicoMapping);

      getMappingAssertConcepts(item.getId(), dicoMapping.getDictionaryId(), 3, "1", 2);

      // dedlete a concept in the dictionary and check that the entry is deleted in the mapping
      DictionaryStoreInterface store = (DictionaryStoreInterface) SitoolsSettings.getInstance().getStores()
          .get(Consts.APP_STORE_DICTIONARY);

      Dictionary dico = store.retrieve(dictionaryId_1);
      dico.getConcepts().remove(1);
      updateDictionary(dico);

      Thread.sleep(5000);

      getMappingAssertConcepts(item.getId(), dicoMapping.getDictionaryId(), 1, "1", 1);

      // delete the dictionary and check that the mapping is deleted in the dataset
      deleteDictionary(dicoMapping.getDictionaryId());
      Thread.sleep(5000);
      assertNoneDicoMapping(item.getId());

      this.deleteDataset(item.getId());

      // restore the dictionary deleted
      store.create(dico);

    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testDictionaryMappingQuerying() {
    docAPI.setActive(false);
    // TODO Auto-generated method stub
    try {
      item = createDataset(datasetId, "/test/ds");
      DictionaryMapping dicoMapping = createDicoMapping(dictionaryId_1);
      addDictionaryMappingOk(item.getId(), dicoMapping);
      DictionaryMapping dicoMapping2 = createDicoMapping(dictionaryId_2);
      dicoMapping2.setDefaultDico(false);
      addDictionaryMappingOk(item.getId(), dicoMapping2);

      changeStatus(item.getId(), "/start");
      // query default one
      System.out.println("default");
      queryMappingOnExposedDataSet(item.getSitoolsAttachementForUsers(), 1, dictionaryId_1);
      // query all mappings
      System.out.println("all");
      queryMappingOnExposedDataSet(item.getSitoolsAttachementForUsers() + "/mappings", 2, null);
      // query the second dictionary
      System.out.println("second dico");
      queryMappingOnExposedDataSet(item.getSitoolsAttachementForUsers() + "/mappings/" + dictionaryId_2, 1,
          dictionaryId_2);

      this.deleteDataset(item.getId());

    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Create a DictionaryMapping object
   * 
   * @return the dictionaryMapping
   */
  public DictionaryMapping createDicoMapping(String dictionaryId) {
    /** DICTIONARY MAPPING */

    DictionaryMapping dicoMapping = new DictionaryMapping();
    dicoMapping.setDictionaryId(dictionaryId);

    ArrayList<ColumnConceptMapping> mappings = new ArrayList<ColumnConceptMapping>();

    ColumnConceptMapping colConMapping = new ColumnConceptMapping();
    colConMapping.setColumnId("1");
    colConMapping.setConceptId("0");
    mappings.add(colConMapping);

    colConMapping = new ColumnConceptMapping();
    colConMapping.setColumnId("1");
    colConMapping.setConceptId("1");
    mappings.add(colConMapping);

    colConMapping = new ColumnConceptMapping();
    colConMapping.setColumnId("2");
    colConMapping.setConceptId("1");

    mappings.add(colConMapping);
    dicoMapping.setMapping(mappings);

    dicoMapping.setDefaultDico(true);

    return dicoMapping;
  }

  /**
   * Add a DictionaryMapping to a dataset but fail because the dataset is started
   * 
   * @param id
   *          the id of the dataset
   * @param dicoMapping
   *          the dico to add
   */
  private void addDictionaryMappingError(String id, DictionaryMapping dicoMapping) {
    String url = getDatasetBaseUrl() + "/" + id + "/mappings/" + dicoMapping.getDictionaryId();
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
        assertFalse(response.getSuccess());
      }
      finally {
        RIAPUtils.exhaust(repr);
      }
    }

  }

  /**
   * Add a DictionaryMapping to a dataset
   * 
   * @param id
   *          the id of the dataset
   * @param dicoMapping
   *          the dico to add
   */
  private void addDictionaryMappingOk(String id, DictionaryMapping dicoMapping) {
    String url = getDatasetBaseUrl() + "/" + id + "/mappings/" + dicoMapping.getDictionaryId();
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

        assertEqualsMapping(dicoMapping, (DictionaryMapping) response.getItem());
      }
      finally {
        RIAPUtils.exhaust(repr);
      }
    }

  }

  /**
   * Delete the given dicoMapping in the dataset specified by the given id
   * 
   * @param id
   *          the dataset identifier
   * @param dicoMapping
   *          the DictionaryMapping to delete
   */
  private void deleteDicoMapping(String id, DictionaryMapping dicoMapping) {
    String url = getDatasetBaseUrl() + "/" + id + "/mappings/" + dicoMapping.getDictionaryId();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("id", "Dataset identifier");
      parameters.put("dicoId", "Dictionary identifier");
      String template = getDatasetBaseUrl() + "/%id/mappings/%dicoId";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = GetResponseUtils.getResponseDicoMapping(getMediaTest(), result, DictionaryMapping.class);
      assertTrue(response.getSuccess());
      assertEquals("MAPPING_DELETED", response.getMessage());
      RIAPUtils.exhaust(result);
    }

  }

  private void deleteDictionary(String id) {
    String url = getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DICTIONARIES_URL) + "/" + id;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("id", "Dictionary identifier");
      String template = getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DICTIONARIES_URL) + "/%id";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, Dictionary.class);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Retrieve the list of mappings for a given dataset and assert that the number of mappings is nbMappings
   * 
   * @param id
   *          the id of the dataset
   * @param nbMappings
   *          the number of mappings expected
   */
  private void retriveMappings(String id, int nbMappings) {
    String url = getDatasetBaseUrl() + "/" + id + "/mappings";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("id", "Dataset identifier");
      String template = getDatasetBaseUrl() + "/%id/mappings";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = GetResponseUtils
          .getResponseDicoMapping(getMediaTest(), result, DictionaryMapping.class, true);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(nbMappings), response.getTotal());

      RIAPUtils.exhaust(result);
    }
  }

  private void getMappingAssertConcepts(String id, String dicoId, int nbMappings, String columnId, int conceptNb) {
    String url = getDatasetBaseUrl() + "/" + id + "/mappings/" + dicoId;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("id", "Dataset identifier");
      String template = getBaseUrl() + "/%id/mappings/" + dicoId;
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = GetResponseUtils.getResponseDicoMapping(getMediaTest(), result, DictionaryMapping.class,
          false);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
      DictionaryMapping dicoMappingOut = (DictionaryMapping) response.getItem();
      assertNotNull(dicoMappingOut.getMapping());
      assertEquals(nbMappings, dicoMappingOut.getMapping().size());

      int localNbMappings = 0;
      for (Iterator<ColumnConceptMapping> iterator = dicoMappingOut.getMapping().iterator(); iterator.hasNext();) {
        ColumnConceptMapping mapping = iterator.next();
        if (mapping.getColumnId().equals(columnId)) {
          localNbMappings++;
        }
      }
      assertEquals(conceptNb, localNbMappings);

      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Query the mapping on the exposed dataset
   * 
   * @param urlDs
   *          the url to query
   * @param nbMappings
   *          the number of mappings ( dictionary ) to find
   * @param dicoId
   *          the dictionary id if there is only 1 mapping
   * 
   * 
   */
  private void queryMappingOnExposedDataSet(String urlDs, int nbMappings, String dicoId) {
    String url = getHostUrl() + urlDs;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("id", "Dataset identifier");
      String template = getDatasetBaseUrl() + "/%id/mappings";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, DataSetExpositionDTO.class, false);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
      DataSetExpositionDTO ds = (DataSetExpositionDTO) response.getItem();
      assertEquals(nbMappings, ds.getDictionaryMappings().size());
      if (nbMappings == 1) {
        assertEquals(dicoId, ds.getDictionaryMappings().get(0).getDictionaryId());
      }
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Assert 2 DictionaryMapping
   * 
   * @param expected
   *          expected DictionaryMapping
   * @param actual
   *          actual DictionaryMapping
   */
  private void assertEqualsMapping(DictionaryMapping expected, DictionaryMapping actual) {
    // assertEquals(expected, actual);
    assertEquals(expected.isDefaultDico(), actual.isDefaultDico());
    if (expected != null && actual != null) {
      assertEquals(expected.getDictionaryId(), actual.getDictionaryId());
      if (expected != null && actual != null) {
        assertEquals(expected.getMapping().size(), actual.getMapping().size());
      }
    }
  }

  /**
   * Assert that there is no DicoMapping on the dataset
   * 
   * @param id
   *          the DataSet id
   */
  private void assertNoneDicoMapping(String id) {
    String url = getDatasetBaseUrl() + "/" + id + "/mappings";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("id", "Dataset identifier");
      String template = getBaseUrl() + "/%id/mappings";
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = GetResponseUtils
          .getResponseDicoMapping(getMediaTest(), result, DictionaryMapping.class, true);
      assertNotNull(response);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(0), response.getTotal());

      // RIAPUtils.exhaust(result);
    }
  }

  private DataSet createDataset(String id, String urlAttachment) throws InterruptedException {
    DataSet ds = CreateDatasetUtil.createDatasetTestPG(id, "int", false, urlAttachment);

    persistDataset(ds);
    ds.setDirty(false);

    return ds;
  }

  /**
   * Update the given Dictionary
   * 
   * @param item
   *          the dictionary
   * @return the updated dictionary
   */
  public Dictionary updateDictionary(Dictionary item) {

    String url = getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DICTIONARIES_URL) + "/"
        + item.getId();
    Representation rep = getRepresentationDictionary(item, getMediaTest());
    ClientResource cr = new ClientResource(url);
    docAPI.appendRequest(Method.PUT, cr, rep);

    Representation result = cr.put(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponseDictionary(getMediaTest(), result, Dictionary.class, false);
      assertTrue(response.getSuccess());
    }
    RIAPUtils.exhaust(result);
    return item;

  }

  /**
   * REST API Response Representation wrapper for single or multiple items expected
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
  public static Response getResponseDictionary(MediaType media, Representation representation, Class<?> dataClass,
      boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      configureDictionary(xstream);

      if (isArray) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Dictionary.class, "concepts", Concept.class);
          xstream.aliasField("concepts", Dictionary.class, "concepts");
        }

        if (dataClass == Dictionary.class) {
          xstream.aliasField("dictionary", Response.class, "item");
          xstream.alias("conceptTemplate", ConceptTemplate.class);
          if (media.isCompatible(MediaType.APPLICATION_JSON)) {
            xstream.addImplicitCollection(ConceptTemplate.class, "properties", Property.class);
            xstream.aliasField("properties", ConceptTemplate.class, "properties");
          }

        }
        if (dataClass == Concept.class) {
          xstream.aliasField("concept", Response.class, "item");
          if (media.isCompatible(MediaType.APPLICATION_JSON)) {
            xstream.addImplicitCollection(ConceptTemplate.class, "properties", Property.class);
            xstream.aliasField("properties", ConceptTemplate.class, "properties");
          }
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
   * Builds XML or JSON Representation of Dictionary for Create and Update methods.
   * 
   * @param item
   *          Dictionary
   * @param media
   *          APPLICATION_XML or APPLICATION_JSON
   * @return XML or JSON Representation
   */
  public static Representation getRepresentationDictionary(Dictionary item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<Dictionary>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<Dictionary> rep = new XstreamRepresentation<Dictionary>(media, item);
      configureDictionary(xstream);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null; // TODO complete test with ObjectRepresentation
    }
  }

  /**
   * Configures XStream mapping of Response object with Dictionary content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configureDictionary(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);
    xstream.alias("dictionary", Dictionary.class);
    xstream.alias("concept", Concept.class);
  }
}
