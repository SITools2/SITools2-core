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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.dataset.filter.FilterApplication;
import fr.cnes.sitools.dataset.filter.FilterStoreXML;
import fr.cnes.sitools.dataset.filter.dto.FilterChainedModelDTO;
import fr.cnes.sitools.dataset.filter.dto.FilterChainedOrderDTO;
import fr.cnes.sitools.dataset.filter.dto.FilterModelDTO;
import fr.cnes.sitools.dataset.filter.model.FilterChainedModel;
import fr.cnes.sitools.dataset.filter.model.FilterParameter;
import fr.cnes.sitools.dataset.filter.model.FilterParameterType;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test case of filters.
 * 
 * @author AKKA Technologies
 */
public abstract class AbstractFilterTestCase extends AbstractSitoolsTestCase {
  /**
   * static xml store instance for the test
   */
  private static SitoolsStore<FilterChainedModel> store = null;

  /**
   * datasetId to attach the application
   */
  private String datasetId = "bf77955a-2cec-4fc3-b95d-7397025fb299";

  /**
   * Filter classname
   */
  private String classname = "fr.cnes.sitools.filter.tests.FilterTest";

  /**
   * Restlet Component for server
   */
  private Component component = null;

  /**
   * relative url for dataset management REST API
   * 
   * @return url
   */
  protected String getAttachUrl() {
    return SITOOLS_URL + "/{datasetId}" + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_FILTERS_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + "/%s" + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_FILTERS_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseRefUrl() {
    return super.getBaseUrl() + "/{datasetId}"
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

  @Before
  @Override
  /**
   * Init and Start a server with GraphApplication
   * 
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {

    if (this.component == null) {
      this.component = new Component();
      this.component.getServers().add(Protocol.HTTP, getTestPort());
      this.component.getClients().add(Protocol.HTTP);
      this.component.getClients().add(Protocol.FILE);
      this.component.getClients().add(Protocol.CLAP);

      // Context
      Context ctx = this.component.getContext().createChildContext();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());

      if (store == null) {
        File storeDirectory = new File(getTestRepository());
        cleanDirectory(storeDirectory);
        store = new FilterStoreXML(storeDirectory, ctx);

      }
      
      ctx.getAttributes().put(ContextAttributes.APP_STORE, store);
      ctx.getAttributes().put(ContextAttributes.APP_ATTACH_REF, getAttachUrl());
      this.component.getDefaultHost().attach(getAttachUrl(), new FilterApplication(ctx));
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
   * Test CRUD Graph with JSon format exchanges.
   */
  @Test
  public void testCRUD() {
    docAPI.setActive(false);
    try {
      assertNone();
      // create a new filter
      FilterModelDTO filter = createFilterObject("filter_description_1", "100000");
      // add it to the server
      create(filter);
      // retrieve the filterChained
      retrieveFilterChained();
      // retrieve the filter
      retrieveFilter(filter);
      // stop a filter
      stop(filter);
      // start a filter
      start(filter);
      // create a new filter
      FilterModelDTO filter2 = createFilterObject("filter_description_2", "100002");
      // add this filter
      addFilter(filter2);
      // assert the order is filter, filter2
      assertFilterOrder(filter, filter2);
      // update the first filter
      filter.setDescriptionAction("filter_description_1_modif");
      update(filter);
      // change the filter order
      changeOrder(filter2, filter);
      // delete the first filter
      deleteFilter(filter);
      // delete the filterChained
      deleteFilterChained();
      assertNone();
      createWadl(String.format(getBaseUrl(), datasetId), "dataset_filters");
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Test CRUD Graph with JSon format exchanges.
   */
  @Test
  public void testCRUD2docAPI() {
    try {
      docAPI.setActive(true);
      docAPI.appendChapter("Manipulating FilterChainedModelDTO Collection");
      assertNone();
      // create a new filter
      FilterModelDTO filter = createFilterObject("filter_description_1", "100000");
      // add it to the server
      docAPI.appendSubChapter("Create a new Filter", "create");
      create(filter);
      // retrieve the filterChained
      docAPI.appendSubChapter("Retrieve a FilterChainedModelDTO", "retrieving");
      retrieveFilterChained();
      // retrieve the filter
      docAPI.appendSubChapter("Retrieve a FilterModelDTO", "retrievingFilter");
      retrieveFilter(filter);
      // stop a filter
      docAPI.appendSubChapter("Stop a FilterModelDTO", "stopFilter");
      stop(filter);
      // start a filter
      docAPI.appendSubChapter("Start a FilterModelDTO", "startFilter");
      start(filter);
      // create a new filter
      docAPI.appendSubChapter("Add a Filter", "create2");
      FilterModelDTO filter2 = createFilterObject("filter_description_2", "100002");
      // add this filter
      addFilter(filter2);
      // update the first filter
      docAPI.appendSubChapter("Update a Filter", "update");
      filter.setDescriptionAction("filter_description_1_modif");
      update(filter);
      // change the filter order
      docAPI.appendSubChapter("Change the order of the filters", "changeOrders");
      changeOrder(filter2, filter);
      // delete the first filter
      docAPI.appendSubChapter("Delete a filter", "deleteFilter");
      deleteFilter(filter);
      // delete the filterChained
      docAPI.appendSubChapter("Delete a FilterChainedModelDTO", "deleting");
      deleteFilterChained();
      assertNone();
      docAPI.close();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Test CRUD Graph with JSon format exchanges.
   */
  @Test
  public void testCRUDwithValidation() {
    docAPI.setActive(false);
    assertNone();
    // create a new filter
    FilterModelDTO filter = createFilterObject("filter_description_1", "100000");
    // change the params value for the validation to fail
    filter.getParameters().get(0).setValue("param1_value_changed");
    filter.getParameters().get(1).setValue("param2_value_changed");
    // add it to the server, it will fail with 2 violations
    createWithValidation(filter, 2);
    // change the params value for only 1 violation
    filter.getParameters().get(0).setValue("param1_value");
    // add it to the server, it will fail with 2 violations
    createWithValidation(filter, 1);
    // change the classname to test with a not existing class name
    filter.setClassName("filter");
    // add it to the server, it will fail with 1 violation
    createWithValidation(filter, 1);
    // set the classname to an existing class name
    filter.setClassName(classname);
    // change the params value to create it
    filter.getParameters().get(1).setValue("param2_value");
    // create the filter
    create(filter);
    // change the params value for the update to fail
    filter.getParameters().get(1).setValue("param2_value_changed");
    updateWithValidation(filter, 1);
    // delete the filterChained
    deleteFilterChained();
    assertNone();
    createWadl(String.format(getBaseUrl(), datasetId), "dataset_filters");
  }

  /**
   * Add a new filter to the filterChained
   * 
   * @param filter2
   *          the FiltereterModel to add
   * 
   */
  private void addFilter(FilterModelDTO filter2) {
    Representation rep = getRepresentation(filter2, getMediaTest());
    String url = String.format(getBaseUrl(), datasetId);
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("filterChainedId", "FilterChained identifier");
      parameters.put("POST", "A <i>FilterChainedModelDTO</i> object");
      postDocAPI(url, "", rep, parameters, String.format(getBaseUrl(), "%identifier%") + "/%filterChainedId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.post(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FilterModelDTO.class);
      assertTrue(response.getSuccess());

      FilterModelDTO filterOut = (FilterModelDTO) response.getItem();
      assertNotNull(filterOut);

      assertEquals(filter2.getDescriptionAction(), filterOut.getDescriptionAction());
      RIAPUtils.exhaust(result);

    }
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

    filter.setClassName(classname);
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
   * Add a filter to a Dataset
   * 
   * @param item
   *          FilterModelDTO
   */
  public void create(FilterModelDTO item) {
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
      Response response = getResponse(getMediaTest(), result, FilterModelDTO.class);
      assertTrue(response.getSuccess());
      FilterModelDTO filter = (FilterModelDTO) response.getItem();
      assertEquals(item.getId(), filter.getId());
      assertEquals(item.getDescriptionAction(), filter.getDescriptionAction());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Add a filter to a Dataset and assert if the validation process has failed
   * 
   * @param item
   *          FilterModelDTO
   * @param nbViolations
   *          the number of violations to assert
   * 
   */
  public void createWithValidation(FilterModelDTO item, int nbViolations) {
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
      Response response = getResponse(getMediaTest(), result, ConstraintViolation.class, true);
      assertFalse(response.getSuccess());
      ArrayList<Object> list = response.getData();
      assertEquals(nbViolations, list.size());
    }
  }

  /**
   * Invoke GET, Gets the filterChainedModel details
   * 
   * @return the filterChainedModel details
   */
  public FilterChainedModelDTO retrieveFilterChained() {

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      retrieveDocAPI(String.format(getBaseUrl(), datasetId), "", parameters,
          String.format(getBaseUrl(), "%identifier%"));
    }
    else {
      String url = String.format(getBaseUrl(), datasetId);
      ClientResource cr = new ClientResource(url);
      docAPI.appendRequest(Method.GET, cr);

      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FilterChainedModelDTO.class);
      assertTrue(response.getSuccess());
      FilterChainedModelDTO filterOut = (FilterChainedModelDTO) response.getItem();
      assertEquals(datasetId, filterOut.getId());
      RIAPUtils.exhaust(result);
      return filterOut;
    }
    return null;
  }

  /**
   * Invoke GET
   * 
   * 
   * 
   * @param item
   *          FilterModelDTO
   */
  public void retrieveFilter(FilterModelDTO item) {
    String url = String.format(getBaseUrl(), datasetId) + "/" + item.getId();

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("filterId", "filter identifier");
      retrieveDocAPI(url, "", parameters, String.format(getBaseUrl(), "%identifier%") + "/%filterId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      docAPI.appendRequest(Method.GET, cr);

      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FilterModelDTO.class);
      assertTrue(response.getSuccess());
      FilterModelDTO filterOut = (FilterModelDTO) response.getItem();
      assertEquals(item.getDescriptionAction(), filterOut.getDescriptionAction());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Update the given FilterModelDTO in the given filterChained
   * 
   * @param item
   *          the FilterModelDTO
   * 
   */
  public void update(FilterModelDTO item) {
    Representation rep = getRepresentation(item, getMediaTest());
    String url = String.format(getBaseUrl(), datasetId) + "/" + item.getId();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("filterId", "filter identifier");
      parameters.put("PUT", "A <i>FilterChainedModelDTO</i> object");
      putDocAPI(url, "", rep, parameters, String.format(getBaseUrl(), "%identifier%") + "/%filterId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FilterModelDTO.class);
      assertTrue(response.getSuccess());
      FilterModelDTO filterOut = (FilterModelDTO) response.getItem();
      assertEquals(item.getDescriptionAction(), filterOut.getDescriptionAction());
      RIAPUtils.exhaust(result);

    }
  }

  /**
   * Update a filter and assert if the validation process has failed
   * 
   * @param item
   *          FilterModelDTO
   * @param nbViolations
   *          the number of violations to assert
   * 
   */
  public void updateWithValidation(FilterModelDTO item, int nbViolations) {
    Representation rep = getRepresentation(item, getMediaTest());
    String url = String.format(getBaseUrl(), datasetId) + "/" + item.getId();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("filterId", "filter identifier");
      parameters.put("PUT", "A <i>FilterChainedModelDTO</i> object");
      putDocAPI(url, "", rep, parameters, String.format(getBaseUrl(), "%identifier%") + "/%filterId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ConstraintViolation.class, true);
      assertFalse(response.getSuccess());
      ArrayList<Object> list = response.getData();
      assertEquals(nbViolations, list.size());

    }
  }

  /**
   * Delete a filterModel from a filterChained
   * 
   * @param item
   *          FilterModelDTO to delete
   */
  public void deleteFilter(FilterModelDTO item) {
    String url = String.format(getBaseUrl(), datasetId) + "/" + item.getId();

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("filterId", "filter identifier");
      deleteDocAPI(url, "", parameters, String.format(getBaseUrl(), "%identifier%") + "/%filterId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FilterModelDTO.class);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Invoke DELETE
   * 
   */
  public void deleteFilterChained() {
    String url = String.format(getBaseUrl(), datasetId);

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("filterId", "filter identifier");
      deleteDocAPI(url, "", parameters, String.format(getBaseUrl(), "%identifier%") + "/%filterId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FilterChainedModelDTO.class);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);

    }
  }

  /**
   * Change the order of the filters in the filterChained
   * 
   * @param first
   *          the first Filter
   * @param second
   *          the second Filter
   * 
   */
  private void changeOrder(FilterModelDTO first, FilterModelDTO second) {
    List<String> listId = new ArrayList<String>();
    listId.add(first.getId());
    listId.add(second.getId());

    FilterChainedOrderDTO dto = new FilterChainedOrderDTO();
    dto.setId(datasetId);
    dto.setIdOrder(listId);

    Representation rep = getRepresentationDTO(dto, getMediaTest());
    String url = String.format(getBaseUrl(), datasetId);
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("PUT", "A <i>FilterChainedModelDTO</i> object");
      putDocAPI(url, "", rep, parameters, String.format(getBaseUrl(), "%identifier%"));
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FilterChainedModelDTO.class);
      assertTrue(response.getSuccess());
      FilterChainedModelDTO filterOut = (FilterChainedModelDTO) response.getItem();
      assertNotNull(filterOut);
      assertFilterOrder(first, second);
      RIAPUtils.exhaust(result);

    }

  }

  /**
   * Start the filter
   * 
   * @param model
   *          the filterModel to start
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if release fails
   */
  private void start(FilterModelDTO model) throws IOException {

    StringRepresentation rep = new StringRepresentation("");
    String url = String.format(getBaseUrl(), datasetId) + "/" + model.getId() + "/start";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("filterId", "filter identifier");
      putDocAPI(url, "", rep, parameters, String.format(getBaseUrl(), "%identifier%") + "/%filterId%/start");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FilterModelDTO.class);
      assertTrue(response.getSuccess());
      FilterModelDTO filterModel = (FilterModelDTO) response.getItem();
      assertStatus("ACTIVE", filterModel);
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Stop the Filter
   * 
   * @param model
   *          the filterModel to start
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if release fails
   */
  private void stop(FilterModelDTO model) throws IOException {
    StringRepresentation rep = new StringRepresentation("");
    String url = String.format(getBaseUrl(), datasetId) + "/" + model.getId() + "/stop";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("filterId", "filter identifier");
      putDocAPI(url, "", rep, parameters, String.format(getBaseUrl(), "%identifier%") + "/%filterId%/start");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, FilterModelDTO.class);
      assertTrue(response.getSuccess());
      FilterModelDTO filterModel = (FilterModelDTO) response.getItem();
      assertStatus("INACTIVE", filterModel);
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Assert the status
   * 
   * @param status
   *          the expected status
   * @param modelOut
   *          the model to assert
   */
  private void assertStatus(String status, FilterModelDTO modelOut) {
    assertEquals(status, modelOut.getStatus());
  }

  /**
   * Assert that the first filter is first and the second filter is second in the filterChained
   * 
   * @param first
   *          the first Filter
   * @param second
   *          the second Filter
   */
  private void assertFilterOrder(FilterModelDTO first, FilterModelDTO second) {
    FilterChainedModelDTO filterChainedOut = retrieveFilterChained();

    assertNotNull(filterChainedOut);
    List<FilterModelDTO> filters = filterChainedOut.getFilters();
    assertNotNull(filters);
    assertEquals(2, filters.size());

    FilterModelDTO firstOut = filters.get(0);
    assertNotNull(firstOut);
    assertEquals(first.getId(), firstOut.getId());

    FilterModelDTO secondOut = filters.get(1);
    assertNotNull(secondOut);
    assertEquals(second.getId(), secondOut.getId());

  }

  /**
   * Invokes GET and asserts result response is an empty array.
   */
  public void assertNone() {
    String url = String.format(getBaseUrl(), datasetId);
    ClientResource cr = new ClientResource(url);

    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponse(getMediaTest(), result, FilterChainedModelDTO.class);
    assertTrue(response.getSuccess());
    assertNull(response.getItem());
    RIAPUtils.exhaust(result);

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
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.alias("response", Response.class);
      if (dataClass == FilterChainedModelDTO.class) {
        xstream.alias("filterChainedModel", FilterChainedModelDTO.class);
        xstream.alias("filterModel", FilterModelDTO.class);
      }
      xstream.alias("filterParameter", FilterParameter.class);

      if (dataClass == ConstraintViolation.class) {
        xstream.alias("constraintViolation", ConstraintViolation.class);
      }

      if (isArray) {
        if (dataClass == FilterChainedModelDTO.class) {
          xstream.addImplicitCollection(FilterChainedModelDTO.class, "data", dataClass);
        }
        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

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
      Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
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
  public static Representation getRepresentationDTO(FilterChainedOrderDTO item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JsonRepresentation(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<FilterChainedOrderDTO> rep = new XstreamRepresentation<FilterChainedOrderDTO>(media, item);
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
   * Configures XStream mapping of Response object with FilterModelDTO content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);

    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");
  }

}
