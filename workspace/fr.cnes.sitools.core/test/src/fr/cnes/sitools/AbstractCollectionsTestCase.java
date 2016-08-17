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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.collections.model.Collection;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.ColumnConceptMapping;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.model.DictionaryMapping;
import fr.cnes.sitools.dictionary.model.Concept;
import fr.cnes.sitools.dictionary.model.ConceptTemplate;
import fr.cnes.sitools.properties.model.SitoolsProperty;
import fr.cnes.sitools.properties.model.SitoolsPropertyType;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.Property;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.CreateDatasetUtil;
import fr.cnes.sitools.utils.GetRepresentationUtils;
import fr.cnes.sitools.utils.GetResponseUtils;

public class AbstractCollectionsTestCase extends AbstractDataSetManagerTestCase {

  private String dictionaryId = "429c7ff8-960a-4a49-9565-59d5c189ad22";

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseCollectionsUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_COLLECTIONS_URL);
  }

  /**
   * Absolute path location for project store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return settings.getStoreDIR(Consts.APP_COLLECTIONS_STORE_DIR);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getDatasetBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_URL);
  }

  @Before
  @Override
  /**
   * Init and Start a server with GraphApplication
   * 
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    // File storeDirectory = new File(getTestRepository());
    // cleanDirectory(storeDirectory);cleanMapDirectories(storeDirectory);

  }

  /**
   * Test CRUD Graph with JSon format exchanges.
   */
  @Test
  public void testCRUD() {

    docAPI.setActive(false);
    assertNone();
    // create a new converter
    Collection collection = createCollectionObject("100000");
    // add it to the server
    create(collection);

    retrieve(collection);

    update(collection);

    delete(collection);

    assertNone();

  }

  /**
   * Test CRUD Graph with JSon format exchanges.
   */
  @Test
  public void testCRUDAPI() {

    docAPI.setActive(true);
    docAPI.appendSubChapter("Get collection list", "list");
    assertNone();
    // create a new converter
    Collection collection = createCollectionObject("100000");
    // add it to the server
    docAPI.appendSubChapter("Create a new Collection", "create");
    create(collection);
    docAPI.appendSubChapter("retrieve a Collection", "retrieve");
    retrieve(collection);
    docAPI.appendSubChapter("update a Collection", "update");
    update(collection);
    docAPI.appendSubChapter("Delete new Collection", "delete");
    delete(collection);

    docAPI.close();

  }

  /**
   * Test the common concept resource Create 2 datasets, map some concepts to the datasets and
   * 
   * @throws InterruptedException
   */
  @Test
  public void testListProperties() throws InterruptedException {
    docAPI.setActive(false);
    String collectionId = "3333333";
    List<DataSet> datasets = null;
    Collection collection = null;
    try {
      datasets = createDatasetsAndMappings();
      collection = createCollection(collectionId, datasets);
      create(collection);

      String url = getBaseCollectionsUrl() + "/" + collectionId + "/properties";

      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponseListOfConceptsOrProperties(getMediaTest(), result, SitoolsProperty.class, true);
      assertTrue(response.getSuccess());
      assertNotNull(response.getData());
      assertEquals(3, response.getData().size());

      RIAPUtils.exhaust(result);

    }
    finally {
      if (datasets != null) {
        for (DataSet dataset : datasets) {
          deleteDataset(dataset.getId());
        }
      }
      if (collection != null) {
        delete(collection);
      }
    }

  }

  /**
   * Test the common concept resource Create 2 datasets, map some concepts to the datasets and
   * 
   * @throws InterruptedException
   */
  @Test
  public void testCommonConcepts() throws InterruptedException {
    docAPI.setActive(false);
    String collectionId = "3333333";
    List<DataSet> datasets = null;
    Collection collection = null;
    try {
      datasets = createDatasetsAndMappings();
      collection = createCollection(collectionId, datasets);
      create(collection);

      String url = getBaseCollectionsUrl() + "/" + collectionId + "/concepts/" + dictionaryId;

      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponseListOfConceptsOrProperties(getMediaTest(), result, Concept.class, true);
      assertTrue(response.getSuccess());
      assertNotNull(response.getData());
      assertEquals(1, response.getData().size());

      ArrayList<Object> concepts = response.getData();
      Concept concept = (Concept) concepts.get(0);
      assertEquals("0", concept.getId());

      RIAPUtils.exhaust(result);

    }
    finally {
      if (datasets != null) {
        for (DataSet dataset : datasets) {
          deleteDataset(dataset.getId());
        }
      }
      if (collection != null) {
        delete(collection);
      }
    }

  }

  /**
   * Invokes GET and asserts result response is an empty array.
   */
  public void assertNone() {
    String url = getBaseCollectionsUrl();
    ClientResource cr = new ClientResource(url);
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponseCollections(getMediaTest(), result, Collection.class, true);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(1), response.getTotal());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Create a Collection Object
   * 
   * @param id
   *          the id of the Collection
   * 
   * @return a new Collection Object
   */
  private Collection createCollectionObject(String id) {
    Collection collection = new Collection();
    collection.setName("A collection");
    collection.setId(id);

    List<Resource> datasets = new ArrayList<Resource>();
    Resource dataset1 = new Resource();
    dataset1.setName("A dataset1");
    dataset1.setId("dataset1_id");
    dataset1.setDescription("A dataset1 description");
    datasets.add(dataset1);
    Resource dataset2 = new Resource();
    dataset2.setName("A dataset2");
    dataset2.setId("dataset2_id");
    dataset2.setDescription("A dataset2 description");
    datasets.add(dataset2);

    collection.setDataSets(datasets);

    return collection;
  }

  /**
   * Create a new Collection Object from a list of DataSet
   * 
   * @param id
   *          the if of the Collection
   * @param datasets
   *          the list of DataSet
   * @return a new Collection Object
   */
  private Collection createCollection(String id, List<DataSet> datasets) {
    Collection collection = new Collection();
    collection.setName("A collection");
    collection.setId(id);

    List<Resource> datasetsRes = new ArrayList<Resource>();
    for (DataSet dataSet : datasets) {
      Resource resource = new Resource();
      resource.setName(dataSet.getName());
      resource.setId(dataSet.getId());
      resource.setDescription(dataSet.getDescription());
      datasetsRes.add(resource);
    }
    collection.setDataSets(datasetsRes);
    return collection;
  }

  /**
   * Persist a Collection on the Server
   * 
   * @param collection
   *          the Collection Object to persist
   */
  private void create(Collection collection) {
    String url = getBaseCollectionsUrl();
    Representation rep = getRepresentationCollections(collection, getMediaTest());
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("POST", "A <i>Collection</i> object");
      postDocAPI(url, "", rep, parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.post(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponseCollections(getMediaTest(), result, Collection.class);
      assertTrue(response.getSuccess());
      Collection collectionOut = (Collection) response.getItem();
      assertEquals(collection.getName(), collectionOut.getName());

      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Retrieve a given Collection and assert that it is the same as the one given in parameter
   * 
   * @param collection
   *          the expected Collection
   */
  private void retrieve(Collection collection) {
    String url = getBaseCollectionsUrl() + "/" + collection.getId();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "collection identifier");
      retrieveDocAPI(url, "", parameters, getBaseCollectionsUrl() + "/%identifier%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponseCollections(getMediaTest(), result, Collection.class);
      assertTrue(response.getSuccess());
      Collection collectionOut = (Collection) response.getItem();
      assertEquals(collection.getName(), collectionOut.getName());

      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Update the given collection on the server
   * 
   * @param collection
   *          a Collection Object
   */
  private void update(Collection collection) {
    String url = getBaseCollectionsUrl() + "/" + collection.getId();
    String newName = collection.getName() + "_update";
    collection.setName(newName);
    Representation rep = getRepresentationCollections(collection, getMediaTest());
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("PUT", "A <i>Collection</i> object");
      putDocAPI(url, "", rep, parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponseCollections(getMediaTest(), result, Collection.class);
      assertTrue(response.getSuccess());
      Collection collectionOut = (Collection) response.getItem();
      assertEquals(collection.getName(), collectionOut.getName());

      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Delete a Collection Object on the server
   * 
   * @param collection
   *          the Collection Object to delete
   */
  private void delete(Collection collection) {
    String url = getBaseCollectionsUrl() + "/" + collection.getId();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "collection identifier");
      deleteDocAPI(url, "", parameters, getBaseCollectionsUrl() + "/%identifier%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponseCollections(getMediaTest(), result, Collection.class);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }

  }

  // ---------------------------------------------------------
  // Methods for testCommonConcepts

  /**
   * Create a List of DataSet with a dictionary mapping for each DataSet The DataSets are persisted on the server
   * 
   * @return a List of persisted dataset with dictionary Mapping
   * @throws InterruptedException
   *           if there is an error while persisting the dataset
   */
  private List<DataSet> createDatasetsAndMappings() throws InterruptedException {
    List<DataSet> datasets = new ArrayList<DataSet>();

    DataSet ds1 = CreateDatasetUtil.createDatasetFusePG("111111", "/ds1");
    List<SitoolsProperty> properties = new ArrayList<SitoolsProperty>();
    properties.add(new SitoolsProperty("satellite", "fuse", null, SitoolsPropertyType.String));
    properties.add(new SitoolsProperty("date", "2012-04-04 11:00:00", null, SitoolsPropertyType.Date));
    ds1.setProperties(properties);
    persistDataset(ds1);
    datasets.add(ds1);

    DictionaryMapping mappingDs1 = createDicoMapping(dictionaryId, true);
    addDictionaryMapping(ds1.getId(), mappingDs1);

    DataSet ds2 = CreateDatasetUtil.createDatasetFusePG("222222", "/ds2");
    properties = new ArrayList<SitoolsProperty>();
    properties.add(new SitoolsProperty("theSatellite", "fuse", null, SitoolsPropertyType.String));
    properties.add(new SitoolsProperty("date", "2012-04-04 11:00:00", null, SitoolsPropertyType.Date));
    ds2.setProperties(properties);
    persistDataset(ds2);

    DictionaryMapping mappingDs2 = createDicoMapping(dictionaryId, false);
    addDictionaryMapping(ds2.getId(), mappingDs2);
    datasets.add(ds2);

    return datasets;

  }

  /**
   * Create a DictionaryMapping object
   * 
   * @param dictionaryId
   *          the id of the Dictionary
   * @param withTitle
   *          if the column title has to be mapped or not
   * @return the dictionaryMapping
   */
  public DictionaryMapping createDicoMapping(String dictionaryId, boolean withTitle) {
    /** DICTIONARY MAPPING */

    DictionaryMapping dicoMapping = new DictionaryMapping();
    dicoMapping.setDictionaryId(dictionaryId);

    ArrayList<ColumnConceptMapping> mappings = new ArrayList<ColumnConceptMapping>();

    ColumnConceptMapping colConMapping = new ColumnConceptMapping();
    colConMapping.setColumnId("prop_id");
    colConMapping.setConceptId("0");
    mappings.add(colConMapping);

    if (withTitle) {

      colConMapping = new ColumnConceptMapping();
      colConMapping.setColumnId("title");
      colConMapping.setConceptId("1");
      mappings.add(colConMapping);
    }

    mappings.add(colConMapping);
    dicoMapping.setMapping(mappings);

    dicoMapping.setDefaultDico(true);

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
  public static Response getResponseCollections(MediaType media, Representation representation, Class<?> dataClass) {
    return getResponseCollections(media, representation, dataClass, false);
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
  public static Response getResponseCollections(MediaType media, Representation representation, Class<?> dataClass,
      boolean isArray) {
    try {
      if (!media.isCompatible(getMediaTest()) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.alias("response", Response.class);
      xstream.alias("collection", Collection.class);

      if (media.equals(MediaType.APPLICATION_JSON)) {
        xstream.addImplicitCollection(Collection.class, "dataSets", Resource.class);
        xstream.addImplicitCollection(Resource.class, "properties", Property.class);

      }

      if (isArray) {
        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
        xstream.aliasField("collection", Response.class, "item");

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
        return null; // TODO complete test with ObjectRepresentation
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
  public static Representation getRepresentationCollections(Collection item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<Collection>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<Collection> rep = new XstreamRepresentation<Collection>(media, item);
      configureCollections(xstream);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null; // TODO complete test with ObjectRepresentation
    }
  }

  /**
   * Configures XStream mapping of Response object with Collection content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configureCollections(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);

    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");
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
  public static Response getResponseListOfConceptsOrProperties(MediaType media, Representation representation,
      Class<?> dataClass, boolean isArray) {
    try {
      if (!media.isCompatible(getMediaTest()) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.alias("response", Response.class);
      xstream.alias("concept", Concept.class);
      xstream.alias("conceptTemplate", ConceptTemplate.class);

      if (media.isCompatible(MediaType.APPLICATION_JSON)) {
        xstream.addImplicitCollection(ConceptTemplate.class, "properties", Property.class);
        xstream.aliasField("properties", ConceptTemplate.class, "properties");
      }

      if (isArray) {
        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

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
        return null; // TODO complete test with ObjectRepresentation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

}
