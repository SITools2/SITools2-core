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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
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
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dictionary.ConceptTemplateAdministration;
import fr.cnes.sitools.dictionary.ConceptTemplateStoreInterface;
import fr.cnes.sitools.dictionary.ConceptTemplateStoreXMLMap;
import fr.cnes.sitools.dictionary.model.ConceptTemplate;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.Property;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test CRUD ConceptTemplate Rest API
 * 
 * @since UserStory : ADM-TH, Sprint : 4
 * 
 * @author AKKA Technologies
 * 
 */
public abstract class AbstractConceptTemplateTestCase extends AbstractSitoolsTestCase {

  /**
   * static xml store instance for the test
   */
  private static ConceptTemplateStoreInterface store = null;

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
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DICTIONARIES_TEMPLATES_URL);
  }

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_DICTIONARIES_TEMPLATES_URL);
  }

  /**
   * Absolute path location for data set store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository()
        + SitoolsSettings.getInstance().getString(Consts.APP_DICTIONARIES_TEMPLATES_STORE_DIR) + "/map";
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
        store = new ConceptTemplateStoreXMLMap(storeDirectory, ctx);
      }
      ctx.getAttributes().put(ContextAttributes.APP_STORE, store);
      ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());
      this.component.getDefaultHost().attach(getAttachUrl(), new ConceptTemplateAdministration(ctx));
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
   * Test CRUD ConceptTemplate
   */
  @Test
  public void testCRUD() {
    docAPI.setActive(false);
    assertNone();
    ConceptTemplate item = createObjectConceptTemplate();
    create(item);
    retrieve(item);
    // retrieveProperty(item, "1");
    update(item);
    testEqualsAndHashCode(item);
    delete(item);
    assertNone();
    createWadl(getBaseUrl(), "templates");
  }

  /**
   * Generates documentation API.
   */
  @Test
  public void testCRUD2docAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Manipulating template Collection");

    assertNone();
    ConceptTemplate item = createObjectConceptTemplate();

    docAPI.appendSubChapter("Creating a new template", "creating");
    create(item);

    docAPI.appendChapter("Manipulating an existing template resource");

    docAPI.appendSubChapter("Retrieving a template", "retrieving");
    retrieve(item);

    docAPI.appendSubChapter("Retrieving a property in template", "retrieve");
    // retrieveProperty(item, "1");

    docAPI.appendSubChapter("Updating a template", "updating");
    update(item);

    docAPI.appendSubChapter("Deleting a template", "delete");
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
   *          a ConceptTemplate instance
   */
  public void create(ConceptTemplate item) {
    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl());
    docAPI.appendRequest(Method.POST, cr, rep);

    Representation result = cr.post(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, ConceptTemplate.class);
      assertTrue(response.getSuccess());
      ConceptTemplate thes = (ConceptTemplate) response.getItem();
      assertEquals(thes.getName(), item.getName());
      assertEquals(thes.getDescription(), item.getDescription());
      assertEquals(thes.getProperties().size(), 2);
      RIAPUtils.exhaust(result);

    }
  }

  /**
   * Create object
   * 
   * @return a ConceptTemplate instance for tests
   */
  public ConceptTemplate createObjectConceptTemplate() {
    ConceptTemplate item = new ConceptTemplate();
    item.setId("1000000");
    item.setName("testCreateConceptTemplate_name");
    item.setDescription("testCreateConceptTemplate_description");

    Property n1 = new Property();
    n1.setName("1-name");
    n1.setValue("1");
    n1.setScope("PUBLIC");

    Property n2 = new Property();
    n2.setName("2-name");
    n1.setValue("2");
    n1.setScope("PUBLIC");

    ArrayList<Property> properties = new ArrayList<Property>();
    properties.add(n1);
    properties.add(n2);
    item.setProperties(properties);
    return item;
  }

  /**
   * Invoke GET
   * 
   * @param item
   *          a ConceptTemplate instance
   */
  public void retrieve(ConceptTemplate item) {
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.GET, cr);

    Representation result = cr.get(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, ConceptTemplate.class);
      assertTrue(response.getSuccess());
      ConceptTemplate thes = (ConceptTemplate) response.getItem();
      assertEquals(thes.getName(), item.getName());
      assertEquals(thes.getDescription(), item.getDescription());
      assertEquals(thes.getProperties().size(), 2);
      RIAPUtils.exhaust(result);

    }
  }

  // /**
  // * Invoke GET on property url
  // *
  // * @param item
  // * a ConceptTemplate instance
  // * @param propId
  // * a notion identifier
  // */
  // public void retrieveProperty(ConceptTemplate item, String propId) {
  // ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId() + "/properties/" + propId);
  // Representation result = cr.get(getMediaTest());
  // assertNotNull(result);
  // assertTrue(cr.getStatus().isSuccess());
  //
  // Response response = getResponse(getMediaTest(), result, Property.class);
  // assertTrue(response.getSuccess());
  // Property property = (Property) response.getItem();
  // // TODO
  // // assertEquals(property.getName(), property.getName());
  // // assertEquals(property.getValue(), property.getValue());
  // RIAPUtils.exhaust(result);
  //
  // }

  /**
   * Invoke PUT
   * 
   * @param item
   *          a ConceptTemplate instance
   */
  public void update(ConceptTemplate item) {
    item.setName("testCreateConceptTemplate_name_modified");
    item.setDescription("testCreateConceptTemplate_description");
    Property n1 = new Property();
    n1.setName("1-name_modified");
    n1.setValue("1-value_modified");

    Property n3 = new Property();
    n3.setName("3-name");
    n3.setValue("value");

    Representation rep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.PUT, cr, rep);

    Representation result = cr.put(rep, getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, ConceptTemplate.class);
      assertTrue(response.getSuccess());
      ConceptTemplate thes = (ConceptTemplate) response.getItem();
      assertEquals(thes.getName(), item.getName());
      assertEquals(thes.getDescription(), item.getDescription());
      assertEquals(thes.getProperties().size(), 2);

    }
    RIAPUtils.exhaust(result);

  }

  /**
   * Invoke DELETE
   * 
   * @param item
   *          a ConceptTemplate instance
   */
  public void delete(ConceptTemplate item) {
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    docAPI.appendRequest(Method.DELETE, cr);
    Representation result = cr.delete(getMediaTest());
    if (!docAPI.appendResponse(result)) {
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      Response response = getResponse(getMediaTest(), result, ConceptTemplate.class);
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
  private void testEqualsAndHashCode(ConceptTemplate item) {
    assertTrue(item.equals(item));
    assertNotNull(item.hashCode());
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
          xstream.addImplicitCollection(ConceptTemplate.class, "properties", Property.class);
          xstream.aliasField("properties", ConceptTemplate.class, "properties");
        }

        if (dataClass == ConceptTemplate.class) {
          xstream.aliasField("template", Response.class, "item");
        }
        if (dataClass == Property.class) {
          xstream.aliasField("property", Response.class, "item");
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
   * Builds XML or JSON Representation of ConceptTemplate for Create and Update methods.
   * 
   * @param item
   *          ConceptTemplate
   * @param media
   *          APPLICATION_XML or APPLICATION_JSON
   * @return XML or JSON Representation
   */
  public static Representation getRepresentation(ConceptTemplate item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<ConceptTemplate>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<ConceptTemplate> rep = new XstreamRepresentation<ConceptTemplate>(media, item);
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
   * Configures XStream mapping of Response object with ConceptTemplate content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);
    xstream.alias("template", fr.cnes.sitools.dictionary.model.ConceptTemplate.class);
    xstream.alias("property", Property.class);
  }
}
