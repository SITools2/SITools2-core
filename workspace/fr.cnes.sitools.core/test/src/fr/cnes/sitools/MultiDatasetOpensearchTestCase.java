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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.api.DocAPI;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.portal.multidatasets.opensearch.dto.OpensearchDescriptionDTO;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * 
 * Test MultiDataset Opensearch Rest API
 * 
 * @since Sprint : 1, release 1
 * 
 * @author AKKA Technologies
 */
public class MultiDatasetOpensearchTestCase extends AbstractSitoolsServerTestCase {
  /** The Title of the test **/
  protected static final String TITLE = "Multidataset opensearch";

  /** portal opensearch url */
  protected static final String PORTAL_OS_URL = "/portal/opensearch";

  /** portal opensearch complete url */
  protected final String fullPortalOsUrl = super.getHostUrl() + AbstractSitoolsServerTestCase.SITOOLS_URL
      + PORTAL_OS_URL;

  /** query */
  protected final String queryStr = "fuse";

  /** nbresults */
  protected final String nbResults = "10";

  static {
    setMediaTest(MediaType.APPLICATION_XML);

    docAPI = new DocAPI(MultiDatasetOpensearchTestCase.class, TITLE);
    docAPI.setMediaTest(MediaType.APPLICATION_XML);

  }

  /**
   * TEST with XML mediaType Test the opensearch list and a request
   */
  @Test
  public void test() {
    docAPI.setActive(false);
    testOpensearchList();
    testMultiDsOs();
    createWadl(fullPortalOsUrl, "multidataset_opensearch");
  }

  /**
   * TEST with XML mediaType with DocAPI Test the opensearch list and a request
   */
  @Test
  public void testDocAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Manipulating Multi-dataset opensearch with XML and JSON format");
    docAPI.appendSubChapter("Retrieving opensearch list", "retrievingXML");
    testOpensearchList();
    docAPI.appendSubChapter("Searching multiple opensearch", "searching");
    testMultiDsOs();

  }

  /**
   * TEST with JSON mediaType Test the opensearch list and the suggest
   */
  @Test
  public void testJSON() {
    setMediaTest(MediaType.APPLICATION_JSON);
    docAPI.setActive(false);
    testOpensearchList();

  }

  /**
   * TEST with JSON mediaType with DocAPI Test the opensearch list and the suggest
   */
  @Test
  public void testDocAPIJSON() {
    docAPI.setMediaTest(MediaType.APPLICATION_JSON);
    setMediaTest(MediaType.APPLICATION_JSON);
    docAPI.setActive(true);
    docAPI.appendSubChapter("Retrieving opensearch list", "retrievingJSON");
    testOpensearchList();
    docAPI.appendSubChapter("Suggest on multiple opensearch", "suggesting");
    testMultiDsOsSuggest();
    docAPI.close();

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
   * Test the list of opensearch
   */

  public void testOpensearchList() {
    ClientResource cr = new ClientResource(fullPortalOsUrl);
    Representation result = cr.get(getMediaTest());
    try {
      if (docAPI.isActive()) {
        docAPI.appendSection("Format");
        String urlLocal = fullPortalOsUrl;
        ClientResource crLocal = new ClientResource(urlLocal);
        docAPI.appendRequest(Method.GET, crLocal);
        docAPI.appendRequest(Method.GET, cr);
        docAPI.appendResponse(result);
      }
      else {
        assertNotNull(result);
        assertSuccess(cr);
        Response response = getResponse(getMediaTest(), result, OpensearchDescriptionDTO.class, true);

        assertEquals(new Integer(2), response.getTotal());
      }
    }
    finally {
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Test the MultiDataset opensearch Request
   */
  private void testMultiDsOs() {

    String uri = fullPortalOsUrl + "/search?q=" + queryStr + "&nbResults=" + nbResults;
    ClientResource cr = new ClientResource(uri);
    Representation result = cr.get(getMediaTest());
    try {
      if (docAPI.isActive()) {
        docAPI.appendSection("Format");
        ClientResource crLocal = new ClientResource(uri);
        docAPI.appendRequest(Method.GET, crLocal);
        docAPI.appendRequest(Method.GET, cr);
        docAPI.appendResponse(result);
      }
      else {
        assertNotNull(result);
        assertSuccess(cr);
        // Response response = getResponse(getMediaTest(), result,
        // OpensearchDescriptionDTO.class, true);
        // assertEquals(new Integer(2), response.getTotal());
      }
    }
    finally {
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Test the suggest fonctionnality with multiple opensearch
   */
  private void testMultiDsOsSuggest() {
    String uri = fullPortalOsUrl + "/suggest?q=" + queryStr;
    ClientResource cr = new ClientResource(uri);
    Representation result = cr.get(getMediaTest());
    try {
      if (docAPI.isActive()) {
        docAPI.appendSection("Format");
        ClientResource crLocal = new ClientResource(uri);
        docAPI.appendRequest(Method.GET, crLocal);
        docAPI.appendRequest(Method.GET, cr);
        docAPI.appendResponse(result);
      }
      else {
        assertNotNull(result);
        assertSuccess(cr);

        // Response response = getResponse(getMediaTest(), result,
        // OpensearchDescriptionDTO.class, true);
        // assertEquals(new Integer(2), response.getTotal());
      }
    }
    finally {
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Check if the status is success
   * 
   * @param client
   *          The ClientResource
   */
  private void assertSuccess(ClientResource client) {
    boolean ok = Status.isSuccess(client.getStatus().getCode());
    assertTrue(ok);
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
      if (!media.isCompatible(getMediaTest()) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);

      if (isArray) {
        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }
      }

      if (media.equals(MediaType.APPLICATION_XML)) {
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
