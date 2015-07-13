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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.api.DocWadl;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.filter.dto.FilterChainedModelDTO;
import fr.cnes.sitools.dataset.filter.dto.FilterModelDTO;
import fr.cnes.sitools.dataset.filter.model.FilterParameter;
import fr.cnes.sitools.dataset.filter.model.FilterParameterType;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Filter notification test case -> if a dataset is deleted, filters attached are deleted
 * 
 * @author m.marseille (AKKA Technologies)
 */
public abstract class AbstractFilterNotificationTestCase extends AbstractSitoolsServerTestCase {

  /**
   * datasetId to attach the application
   */
  private String datasetId = "bf77955a-2cec-4fc3-b95d-7397025fb299";

  /**
   * FilterId
   */
  private String filterId = "100000";

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + "/datasets/{datasetId}"
        + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_FILTERS_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + "/datasets/%s"
        + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_FILTERS_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseRefUrl() {
    return super.getBaseUrl() + "/datasets/{datasetId}"
        + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_FILTERS_URL);
  }

  /**
   * Absolute path location for project store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_FILTERS_STORE_DIR);
  }

  /**
   * Get the dataset URL
   * 
   * @return the dataset URL
   */
  protected String getBaseDatasetUrl() {
    return super.getBaseUrl() + "/datasets/" + datasetId;
  }

  /**
   * Get the attachment for dataset
   * 
   * @return the attachment URL for dataset
   */
  protected String getAttachDatasetUrl() {
    return SITOOLS_URL + "/datasets/" + datasetId;
  }

  /**
   * Absolute path location for project store files
   * 
   * @return path
   */
  protected String getDatasetTestRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_STORE_DIR);
  }

  @Before
  @Override
  /**
   * Init and Start a server
   * 
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
   * Test CRUD Graph with JSon format exchanges.
   */
  @Test
  public void testCRUD() {
    docAPI.setActive(false);
    try {
      assertNone();
      // FilterChainedModelDTO item = createObject(datasetId, filterId);
      FilterModelDTO filter = createFilterObject("filter_description_1", "100000");
      create(filter);
      checknotify();
      assertNone();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Check the notification API by deleting the dataset and check that the filter is deleted too.
   * 
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS when occurs
   */
  public void checknotify() throws IOException {
    // Delete the dataset
    ClientResource cr = new ClientResource(getBaseDatasetUrl());
    ChallengeResponse chr = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "admin", "admin");
    cr.setChallengeResponse(chr);
    Representation repr = cr.delete(getMediaTest());
    cr.release();
    assertNotNull(repr);
    Response response = getResponse(getMediaTest(), repr, Response.class);
    RIAPUtils.exhaust(repr);
    assertNotNull(response);
    assertTrue(response.getSuccess());
    try {
      Thread.sleep(2000);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Create WADL
   */
  public void createWadl() {
    ClientResource cr = new ClientResource(String.format(getBaseUrl(), datasetId));
    DocWadl dw = new DocWadl("dataset_filters");
    try {
      cr.options().write(dw.getWadlPrintStream());
      cr.options(MediaType.TEXT_HTML).write(dw.getHtmlPrintStream());
      cr.release();
    }
    catch (ResourceException e) {
      e.printStackTrace();
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Create an object for tests
   * 
   * @param datasetId
   *          dataset identifier
   * @param filterId
   *          filter identifier
   * @return FilterChainedModelDTO
   */
  public FilterChainedModelDTO createObject(String datasetId, String filterId) {
    FilterChainedModelDTO item = new FilterChainedModelDTO();
    item.setId(filterId);
    item.setName("Filter");
    item.setParent(datasetId);
    item.setDescription("FilterDescription");
    return item;
  }

  /**
   * Create a FilterModelDTO object with the specified description and identifier
   * 
   * @param description
   *          the description
   * @param id
   *          the FilterModelDTO identifier
   * @return the created FilterModelDTO
   */
  public FilterModelDTO createFilterObject(String description, String id) {
    FilterModelDTO filter = new FilterModelDTO();

    filter.setClassName("fr.cnes.sitools.filter.tests.FilterTest");
    filter.setDescriptionAction(description);
    filter.setName("TestFilter");
    filter.setClassAuthor("AKKA/CNES");
    filter.setClassVersion("1.0");
    filter.setDescription("FOR TEST PURPOSE ONLY, DON'T USE IT, IT DOESN'T DO ANYTHING");
    filter.setId(id);

    FilterParameter param1 = new FilterParameter("1", "1", FilterParameterType.PARAMETER_INTERN);
    param1.setValue("param1_value");
    FilterParameter param2 = new FilterParameter("2", "2", FilterParameterType.PARAMETER_INTERN);
    param2.setValue("param2_value");

    filter.getParameters().add(param1);
    filter.getParameters().add(param2);

    return filter;

  }

  /**
   * Invoke POST
   * 
   * @param item
   *          Graph
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS when exhaust fails
   */
  public void create(FilterModelDTO item) throws IOException {
    Representation rep = getRepresentation(item, getMediaTest());
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("POST", "A <i>FilterChainedModelDTO</i> object");
      postDocAPI(String.format(getBaseUrl(), datasetId), "", rep, parameters,
          String.format(getBaseUrl(), "%identifier%"));
    }
    else {
      ClientResource cr = new ClientResource(String.format(getBaseUrl(), datasetId));
      Representation result = cr.post(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      cr.release();
      Response response = getResponse(getMediaTest(), result, FilterModelDTO.class);
      result.getText();
      result.exhaust();
      result.release();
      assertTrue(response.getSuccess());
      FilterModelDTO filter = (FilterModelDTO) response.getItem();
      assertEquals(filter.getName(), item.getName());
      assertEquals(filter.getId(), item.getId());
    }

  }

  /**
   * Invokes GET and asserts result response is an empty array.
   * 
   * @return true if no filters are found
   * @param skipAssert
   *          to skip assertions
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS when occurs
   */
  public boolean assertNone(boolean skipAssert) throws IOException {
    ClientResource crn = new ClientResource(String.format(getBaseUrl(), datasetId));
    Representation result = crn.get(getMediaTest());
    assertNotNull(result);
    assertTrue(crn.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, FilterChainedModelDTO.class);
    assertTrue(response.getSuccess());
    if (!skipAssert) {
      assertNull(response.getItem());
    }
    RIAPUtils.exhaust(result);
    return (response.getItem() == null);
  }

  /**
   * Default assertNone
   * 
   * @return true if no object found
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS when occurs
   */
  public boolean assertNone() throws IOException {
    return assertNone(false);
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
      xstream.alias("response", Response.class);
      if (dataClass == FilterChainedModelDTO.class) {
        xstream.alias("filterChainedModel", FilterChainedModelDTO.class);
        xstream.alias("filterModel", FilterModelDTO.class);
      }
      xstream.alias("filterParameter", FilterParameter.class);

      xstream.alias("item", dataClass);
      xstream.alias("item", Object.class, dataClass);

      if (isArray && media.equals(MediaType.APPLICATION_JSON)) {
        xstream.addImplicitCollection(Response.class, "data", dataClass);
      }
      else {

        if (media.equals(MediaType.APPLICATION_JSON)) {
          if (dataClass == FilterChainedModelDTO.class) {
            xstream.addImplicitCollection(FilterChainedModelDTO.class, "filters", FilterModelDTO.class);
          }
          xstream.addImplicitCollection(FilterModelDTO.class, "parameters", FilterParameter.class);

        }

        if (dataClass == FilterChainedModelDTO.class) {
          xstream.aliasField("filterChainedModel", Response.class, "item");
        }
        if (dataClass == FilterModelDTO.class) {
          xstream.aliasField("filter", Response.class, "item");
        }
        xstream.aliasField("data", Response.class, "item");

      }
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
  public static Representation getRepresentation(FilterModelDTO item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<FilterModelDTO>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<FilterModelDTO> rep = new XstreamRepresentation<FilterModelDTO>(media, item);
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
   * Configures XStream mapping of Response object with FilterModelDTO content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);

    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");

  }

}
