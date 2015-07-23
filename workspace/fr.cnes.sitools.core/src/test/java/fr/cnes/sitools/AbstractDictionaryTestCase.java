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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dictionary.DictionaryAdministration;
import fr.cnes.sitools.dictionary.Store.DictionaryStoreInterface;
import fr.cnes.sitools.dictionary.Store.DictionaryStoreXMLMap;
import fr.cnes.sitools.dictionary.model.Concept;
import fr.cnes.sitools.dictionary.model.ConceptTemplate;
import fr.cnes.sitools.dictionary.model.Dictionary;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.Property;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test CRUD Dictionary Rest API
 * 
 * @since UserStory : ADM-TH, Sprint : 4
 * 
 * @author AKKA Technologies
 * 
 */
@Ignore
public abstract class AbstractDictionaryTestCase extends AbstractSitoolsTestCase {

  /**
   * static xml store instance for the test
   */
  private static DictionaryStoreInterface store = null;

  /**
   * Restlet Component for server
   */
  private Component component = null;

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DICTIONARIES_URL);
  }

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_DICTIONARIES_URL);
  }

  /**
   * Absolute path location for data set store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_DICTIONARIES_STORE_DIR)
        + "/map";
  }

  @Before
  @Override
  /**
   * Create component, store and application and start server  
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {

    if (this.component == null) {
      this.component = createTestComponent(SitoolsSettings.getInstance());

      // Context
      Context ctx = this.component.getContext().createChildContext();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());

      if (store == null) {
        File storeDirectory = new File(getTestRepository());
        storeDirectory.mkdirs();
        cleanDirectory(storeDirectory);
        cleanMapDirectories(storeDirectory);
        store = new DictionaryStoreXMLMap(storeDirectory, ctx);
      }

      ctx.getAttributes().put(ContextAttributes.APP_STORE, store);
      ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());
      this.component.getDefaultHost().attach(getAttachUrl(), new DictionaryAdministration(ctx));
    }

    if (!this.component.isStarted()) {
      this.component.start();
    }

  }

  @After
  @Override
  /**
   * Stop server
   * @throws java.lang.Exception
   */
  public void tearDown() throws Exception {
    super.tearDown();
    this.component.stop();
    this.component = null;
  }

  /**
   * Test CRUD Dictionary
   */
  @Test
  public void testCRUD() {
    docAPI.setActive(false);
    assertNone();
    ConceptTemplate template = createObjectConceptTemplate();
    Dictionary item = createObjectDictionary(template);
    create(item);
    testEqualsAndHashCode(item);
    retrieve(item);
    Concept concept1 = item.getConcepts().get(0);
    retrieveConcept(item.getId(), concept1.getId(), concept1);
    item = updateDictionaryObject(item);
    update(item);
    changeConceptsOrder(item);
    delete(item);
    assertNone();
    createWadl(getBaseUrl(), "dictionaries");
  }

  /**
   * Generates documentation API.
   */
  @Test
  public void testCRUD2docAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Manipulating Dictionnary Collection");

    assertNone();
    ConceptTemplate template = createObjectConceptTemplate();
    Dictionary item = createObjectDictionary(template);

    docAPI.appendSubChapter("Creating a new Dictionnary", "creating");
    create(item);

    docAPI.appendChapter("Manipulating an existing Dictionnary resource");

    docAPI.appendSubChapter("Retrieving a Dictionnary", "retrieving");
    retrieve(item);

    docAPI.appendSubChapter("Retrieving a notion in Dictionnary", "retrieve");
    // retrieveNotion(item, "1");

    docAPI.appendSubChapter("Updating a Dictionnary", "updating");
    item = updateDictionaryObject(item);
    update(item);

    docAPI.appendSubChapter("Deleting a Dictionnary", "delete");
    delete(item);
    docAPI.close();
  }

  /**
   * Invokes GET and asserts result response is an empty array.
   */
  public void assertNone() {
    ClientResource cr = new ClientResource(getBaseUrl());
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    RIAPUtils.exhaust(result);

  }

  /**
   * Invoke POST
   * 
   * @param item
   *          a dictionary instance
   */
  public void create(Dictionary item) {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl());
    docAPI.appendRequest(Method.POST, cr, rep);

    Representation result = cr.post(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Dictionary.class);
      assertTrue(response.getSuccess());
      Dictionary thes = (Dictionary) response.getItem();
      assertEquals(item.getName(), thes.getName());
      assertEquals(item.getDescription(), thes.getDescription());
      assertNotNull(item.getConceptTemplate());
      assertEquals(item.getConceptTemplate().getId(), thes.getConceptTemplate().getId());
      assertNotNull(thes.getConcepts());
      assertConcepts(item.getConcepts(), thes.getConcepts());

      RIAPUtils.exhaust(result);

    }
  }

  /**
   * Create a conceptTemplate
   * 
   * @return a new ContextTemplate object
   */
  private ConceptTemplate createObjectConceptTemplate() {
    ConceptTemplate template = new ConceptTemplate();

    template.setId("template_id");
    template.setDescription("template_description");
    template.setName("template_name");

    ArrayList<Property> listProp = new ArrayList<Property>();

    Property prop = new Property();
    prop.setName("name");
    prop.setValue("name_value");
    listProp.add(prop);

    prop = new Property();
    prop.setName("description");
    prop.setValue("description_value");
    listProp.add(prop);

    prop = new Property();
    prop.setName("unit");
    prop.setValue("unit_value");
    listProp.add(prop);

    template.setProperties(listProp);

    return template;

  }

  /**
   * Create object
   * 
   * @param template
   *          the template of the concepts of the dictionary
   * @return a Dictionary instance for tests
   */
  public Dictionary createObjectDictionary(ConceptTemplate template) {
    Dictionary item = new Dictionary();
    item.setId("1000000");
    item.setName("testCreateDictionary_name");
    item.setDescription("testCreateDictionary_description");

    item.setConceptTemplate(template);

    List<Concept> conceptList = new ArrayList<Concept>();

    Concept concept = new Concept();
    concept.setName("concept1");
    concept.setDescription("the concept1");
    concept.setProperties(createProperties(concept, template));
    concept.setDictionaryId(item.getId());
    concept.setId("0");
    conceptList.add(concept);

    concept = new Concept();
    concept.setName("concept2");
    concept.setDescription("the concept2");
    concept.setProperties(createProperties(concept, template));
    concept.setDictionaryId(item.getId());
    concept.setId("1");
    conceptList.add(concept);

    item.setConcepts(conceptList);
    return item;
  }

  /**
   * Create the properties for a Given concept according to the given template
   * 
   * @param concept
   *          the Concept
   * @param template
   *          the template
   * @return return the list of properties for a given concept according to the given template
   */
  private List<Property> createProperties(Concept concept, ConceptTemplate template) {
    List<Property> conceptPropList = new ArrayList<Property>();
    List<Property> templatePropList = template.getProperties();

    for (Iterator<Property> iterator = templatePropList.iterator(); iterator.hasNext();) {
      Property property = iterator.next();
      Property conceptProp = new Property();

      conceptProp.setName(property.getName());
      conceptProp.setValue(property.getValue() + "_" + concept.getName());
      conceptPropList.add(conceptProp);
    }
    return conceptPropList;
  }

  /**
   * Invoke GET
   * 
   * @param item
   *          a dictionary instance
   */
  public void retrieve(Dictionary item) {
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Dictionary.class);
      assertTrue(response.getSuccess());
      Dictionary thes = (Dictionary) response.getItem();
      assertEquals(thes.getName(), item.getName());
      assertEquals(thes.getDescription(), item.getDescription());
      assertNotNull(item.getConceptTemplate());
      assertEquals(item.getConceptTemplate().getId(), thes.getConceptTemplate().getId());
      assertConcepts(item.getConcepts(), thes.getConcepts());
      RIAPUtils.exhaust(result);

    }
  }

  /**
   * Invoke GET on notion url
   * 
   * @param dictionaryId
   *          a dictionary id
   * @param conceptId
   *          a concept identifier
   * @param originalConcept
   *          the Concept to assert
   * 
   */
  public void retrieveConcept(String dictionaryId, String conceptId, Concept originalConcept) {
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + dictionaryId + "/concepts/" + conceptId);
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(getMediaTest(), result, Concept.class);
    assertTrue(response.getSuccess());
    Concept concept = (Concept) response.getItem();
    assertEqualsConcept(originalConcept, concept);

    RIAPUtils.exhaust(result);

  }

  /**
   * Assert that 2 concept are equals
   * 
   * @param expected
   *          the expected concept
   * @param actual
   *          the actual concept
   */
  public void assertEqualsConcept(Concept expected, Concept actual) {
    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getDictionaryId(), actual.getDictionaryId());

    // assert the properties list
    assertEquals(expected.getProperties().size(), actual.getProperties().size());
    for (int i = 0; i < expected.getProperties().size(); i++) {
      Property propExp = expected.getProperties().get(i);
      Property propAct = actual.getProperties().get(i);
      assertEquals(propExp.getName(), propAct.getName());
      assertEquals(propExp.getValue(), propAct.getValue());
      assertEquals(propExp.getScope(), propAct.getScope());
    }
  }

  /**
   * Assert that 2 lists of Concepts are equals
   * 
   * @param expected
   *          the expected list
   * @param actual
   *          the actual list
   */
  public void assertConcepts(List<Concept> expected, List<Concept> actual) {
    assertNotNull(expected);
    assertNotNull(actual);
    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < expected.size(); i++) {
      assertEqualsConcept(expected.get(i), actual.get(i));
    }

  }

  /**
   * Update the given Dictionary
   * 
   * @param item
   *          the dictionary
   * @return the updated dictionary
   */
  public Dictionary update(Dictionary item) {

    // item.setNotionsArray(new Notion[] { n1, n3 });
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.PUT, cr, rep);

    Representation result = cr.put(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Dictionary.class);
      assertTrue(response.getSuccess());
      Dictionary thes = (Dictionary) response.getItem();
      assertEquals(thes.getName(), item.getName());
      assertEquals(thes.getDescription(), item.getDescription());
      assertNotNull(thes.getConcepts());
      assertConcepts(item.getConcepts(), thes.getConcepts());
    }
    RIAPUtils.exhaust(result);
    return item;

  }

  /**
   * Change the content of the given Dictionary and return its new value
   * 
   * @param item
   *          the Dictionary to change
   * @return the new Dictionary
   */
  private Dictionary updateDictionaryObject(Dictionary item) {
    item.setName("testCreateDictionary_name_modified");
    item.setDescription("testCreateDictionary_description");

    // Concepts
    Concept concept1 = item.getConcepts().get(0);
    for (Iterator<Property> iterator = concept1.getProperties().iterator(); iterator.hasNext();) {
      Property prop = iterator.next();
      prop.setValue(prop.getValue() + "_modified");
    }

    // add a concept
    Concept concept = new Concept();
    concept.setName("concept3");
    concept.setDescription("the concept3");
    concept.setProperties(createProperties(concept, item.getConceptTemplate()));
    concept.setDictionaryId(item.getId());
    concept.setId("2");
    item.getConcepts().add(concept);

    return item;
  }

  /**
   * Change the order of the concept from the given Dictionary
   * 
   * @param item
   *          the dictionary
   */
  private void changeConceptsOrder(Dictionary item) {
    ArrayList<Concept> newList = new ArrayList<Concept>();

    newList.add(item.getConcepts().get(2));
    newList.add(item.getConcepts().get(1));
    newList.add(item.getConcepts().get(0));

    item.setConcepts(newList);

    item.getConcepts().get(0).setId("0");
    item.getConcepts().get(1).setId("1");
    item.getConcepts().get(2).setId("2");

    update(item);

  }

  /**
   * Invoke DELETE
   * 
   * @param item
   *          a dictionary instance
   */
  public void delete(Dictionary item) {
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.DELETE, cr);
    Representation result = cr.delete(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, Dictionary.class);
      assertTrue(response.getSuccess());
    }
    RIAPUtils.exhaust(result);

  }

  /**
   * Test The equals and HashCode method
   * 
   * @param item
   *          a ConceptTemplate
   */
  private void testEqualsAndHashCode(Dictionary item) {
    Dictionary itemCopy = copyDictionary(item);
    assertTrue(item.equals(itemCopy));
    assertNotNull(itemCopy.hashCode());
    // also test it on concept
    Concept concept = item.getConcepts().get(0);
    assertNotNull(concept);
    Concept conceptCopy = copyConcept(concept);
    assertNotNull(conceptCopy);
    assertTrue(concept.equals(conceptCopy));
    assertNotNull(conceptCopy.hashCode());
  }

  /**
   * Create a new Dictionary Object from the given Dictionary
   * 
   * @param item
   *          a Dictionary
   * @return a new DIctionary Object from the given Dictionary
   */
  private Dictionary copyDictionary(Dictionary item) {
    Dictionary dico = new Dictionary();
    dico.setName(item.getName());
    dico.setDescription(item.getDescription());
    dico.setId(item.getId());
    dico.setConceptTemplate(item.getConceptTemplate());
    dico.setConcepts(item.getConcepts());
    return dico;
  }

  /**
   * Create a new Concept Object from the given Concept
   * 
   * @param item
   *          a Concept
   * @return a new Concept Object from the given Concept
   */
  private Concept copyConcept(Concept item) {
    Concept current = new Concept();

    current.setId(item.getId());
    current.setDescription(item.getDescription());
    current.setDictionaryId(item.getDictionaryId());
    current.setName(item.getName());
    current.setProperties(item.getProperties());
    current.setType(item.getType());
    current.setUrl(item.getUrl());
    return current;
  }

  // ------------------------------------------------------------
  // RESPONSE REPRESENTATION

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
  public static Response getResponse(MediaType media, Representation representation, Class<?> dataClass, boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      configure(xstream);

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
  public static Representation getRepresentation(Dictionary item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON) || media.equals(MediaType.APPLICATION_XML)) {
      return new JacksonRepresentation<Dictionary>(item);
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
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);
    xstream.alias("dictionary", Dictionary.class);
    xstream.alias("concept", Concept.class);
  }
}
