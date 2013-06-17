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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dictionary.model.Dictionary;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test to create a lot of Dictionaries then see pagination on admin client side
 * 
 * @author m.marseille (AKKA Technologies)
 */
public class MultilpleDictionaryTestCase extends AbstractSitoolsServerTestCase {

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
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_DICTIONARIES_STORE_DIR);
  }

  @Before
  @Override
  /**
   * Create component, store and application and start server  
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    super.setUp();
  }

  @After
  @Override
  /**
   * Stop server
   * @throws java.lang.Exception
   */
  public void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Test CRUD Dictionary
   */
  @Test
  public void testCRUD() {
    assertNone();
    Dictionary item;
    List<Dictionary> dics = new ArrayList<Dictionary>();
    int i;
    for (i = 0; i < 30; i++) {
      item = createObjectDictionary(String.valueOf(i));
      create(item);
      dics.add(item);
    }
    for (Dictionary dic : dics) {
      delete(dic);
    }
    assertNone();
  }

  /**
   * Invokes GET and asserts result response is an empty array.
   */
  public void assertNone() {
    ClientResource cr = new ClientResource(getBaseUrl());
    Representation result = cr.get(MediaType.APPLICATION_JSON);
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
    Representation rep = getRepresentation(item, MediaType.APPLICATION_JSON);
    ClientResource cr = new ClientResource(getBaseUrl());
    Representation result = cr.post(rep, getMediaTest());
    RIAPUtils.exhaust(rep);
    RIAPUtils.exhaust(result);
  }

  /**
   * Create object
   * 
   * @param id
   *          TODO
   * 
   * @return a Dictionary instance for tests
   */
  public Dictionary createObjectDictionary(String id) {
    Dictionary item = new Dictionary();
    item.setId(id);
    item.setName("testDictionary_" + id);
    item.setDescription("testCreateDictionary_description");
    return item;
  }

  /**
   * Invoke DELETE
   * 
   * @param item
   *          a dictionary instance
   */
  public void delete(Dictionary item) {
    ClientResource cr = new ClientResource(getBaseUrl() + "/" + item.getId());
    Representation result = cr.delete(MediaType.APPLICATION_JSON);
    RIAPUtils.exhaust(result);
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
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
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
          xstream.aliasField("notions", Dictionary.class, "notions");
        }

        if (dataClass == Dictionary.class) {
          xstream.aliasField("dictionary", Response.class, "item");
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
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
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
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JsonRepresentation(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<Dictionary> rep = new XstreamRepresentation<Dictionary>(media, item);
      configure(xstream);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
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
  }

}
