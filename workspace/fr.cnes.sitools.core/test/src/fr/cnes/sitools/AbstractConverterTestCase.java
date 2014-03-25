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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.engine.Engine;
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
import fr.cnes.sitools.dataset.converter.ConverterApplication;
import fr.cnes.sitools.dataset.converter.ConverterStoreXML;
import fr.cnes.sitools.dataset.converter.dto.ConverterChainedModelDTO;
import fr.cnes.sitools.dataset.converter.dto.ConverterChainedOrderDTO;
import fr.cnes.sitools.dataset.converter.dto.ConverterModelDTO;
import fr.cnes.sitools.dataset.converter.model.ConverterChainedModel;
import fr.cnes.sitools.dataset.converter.model.ConverterParameter;
import fr.cnes.sitools.dataset.converter.model.ConverterParameterType;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test case of converters.
 * 
 * @author AKKA Technologies
 */
public abstract class AbstractConverterTestCase extends AbstractSitoolsTestCase {
  /**
   * static xml store instance for the test
   */
  private static SitoolsStore<ConverterChainedModel> store = null;

  /**
   * datasetId to attach the application
   */
  private String datasetId = "bf77955a-2cec-4fc3-b95d-7397025fb299";
  /**
   * Converter classname
   */
  private String classname = "fr.cnes.sitools.converter.tests.ConverterValidatorTest";

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
    return SITOOLS_URL + "/{datasetId}" + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_CONVERTERS_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + "/%s" + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_CONVERTERS_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseRefUrl() {
    return super.getBaseUrl() + "/{datasetId}"
        + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_CONVERTERS_URL);
  }

  /**
   * Absolute path location for project store files
   * 
   * @return path
   */
  protected String getTestRepository() {
    return super.getTestRepository()
        + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_CONVERTERS_STORE_DIR);
  }

  @Before
  @Override
  /**
   * Init and Start a server with GraphApplication
   * 
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {

    SitoolsSettings settings = SitoolsSettings.getInstance();
    if (this.component == null) {
      this.component = createTestComponent(settings);

      // Context
      Context ctx = this.component.getContext().createChildContext();
      ctx.getAttributes().put(ContextAttributes.SETTINGS, SitoolsSettings.getInstance());

      if (store == null) {
        File storeDirectory = new File(getTestRepository());
        cleanDirectory(storeDirectory);
        store = new ConverterStoreXML(storeDirectory, ctx);

      }
      
      ctx.getAttributes().put(ContextAttributes.APP_STORE, store);
      ctx.getAttributes().put(ContextAttributes.APP_ATTACH_REF, getAttachUrl());
      this.component.getDefaultHost().attach(getAttachUrl(), new ConverterApplication(ctx));
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
    try {
      docAPI.setActive(false);
      assertNone();
      // create a new converter
      ConverterModelDTO conv = createConverterObject("conv_description_1", "100000");
      // add it to the server
      create(conv);
      // retrieve the converterChained
      retrieveConvChained();
      // retrieve the converter
      retrieveConverter(conv);
      // stop a converter
      stop(conv);
      // start a converter
      start(conv);
      // create a new converter
      ConverterModelDTO conv2 = createConverterObject("conv_description_2", "100002");
      // add this converter
      addConverter(conv2);
      // assert the order is conv, conv2
      assertConvOrder(conv, conv2);
      // update the first converter
      conv.setDescriptionAction("conv_description_1_modif");
      update(conv);
      // change the converter order
      changeOrder(conv2, conv);
      // delete the first converter
      deleteConv(conv);
      // delete the converterChained
      deleteConvChained();
      assertNone();
      createWadl(String.format(getBaseUrl(), datasetId), "dataset_converters");
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
      docAPI.appendChapter("Manipulating ConverterChainedModelDTO Collection");
      assertNone();
      // create a new converter
      ConverterModelDTO conv = createConverterObject("conv_description_1", "100000");
      // add it to the server
      docAPI.appendSubChapter("Create a new Converter", "create");
      create(conv);
      // retrieve the converterChained
      docAPI.appendSubChapter("Retrieve a ConverterChainedModelDTO", "retrieving");
      retrieveConvChained();
      // retrieve the converter
      docAPI.appendSubChapter("Retrieve a ConverterModel", "retrievingConverter");
      retrieveConverter(conv);
      // stop a converter
      docAPI.appendSubChapter("Stop a ConverterModel", "stopConverter");
      stop(conv);
      // start a converter
      docAPI.appendSubChapter("Start a ConverterModel", "startConverter");
      start(conv);
      // create a new converter
      docAPI.appendSubChapter("Add a Converter", "create2");
      ConverterModelDTO conv2 = createConverterObject("conv_description_2", "100002");
      // add this converter
      addConverter(conv2);
      // update the first converter
      docAPI.appendSubChapter("Update a Converter", "update");
      conv.setDescriptionAction("conv_description_1_modif");
      update(conv);
      // change the converter order
      docAPI.appendSubChapter("Change the order of the converters", "changeOrders");
      changeOrder(conv2, conv);
      // delete the first converter
      docAPI.appendSubChapter("Delete a converter", "deleteConv");
      deleteConv(conv);
      // delete the converterChained
      docAPI.appendSubChapter("Delete a ConverterChainedModelDTO", "deleting");
      deleteConvChained();
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
    ConverterModelDTO converter = createConverterObject("conv_description_1", "100000");
    // change the params value for the validation to fail
    converter.getParameters().get(0).setValue("param1_value_changed");
    converter.getParameters().get(1).setValue("param2_value_changed");
    // add it to the server, it will fail with 2 violations
    createWithValidation(converter, 2);
    // change the params value for only 1 violation
    converter.getParameters().get(0).setValue("param1_value");
    // add it to the server, it will fail with 2 violations
    createWithValidation(converter, 1);
    // change the classname to test with a not existing class name
    converter.setClassName("converter");
    // add it to the server, it will fail with 1 violation
    createWithValidation(converter, 1);
    // set the classname to an existing class name
    converter.setClassName(classname);
    // change the params value to create it
    converter.getParameters().get(1).setValue("param2_value");
    // create the filter
    create(converter);
    // change the params value for the update to fail
    converter.getParameters().get(1).setValue("param2_value_changed");
    updateWithValidation(converter, 1);
    // delete the filterChained
    deleteConvChained();
    assertNone();
  }

  /**
   * Add a new converter to the converterChained
   * 
   * @param conv2
   *          the ConveterModel to add
   * 
   */
  private void addConverter(ConverterModelDTO conv2) {
    Representation rep = getRepresentation(conv2, getMediaTest());
    String url = String.format(getBaseUrl(), datasetId);
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("converterChainedId", "ConverterChained identifier");
      parameters.put("POST", "A <i>ConverterChainedModelDTO</i> object");
      postDocAPI(url, "", rep, parameters, String.format(getBaseUrl(), "%identifier%") + "/%converterChainedId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.post(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ConverterModelDTO.class);
      assertTrue(response.getSuccess());

      ConverterModelDTO convOut = (ConverterModelDTO) response.getItem();
      assertNotNull(convOut);

      assertEquals(conv2.getDescriptionAction(), convOut.getDescriptionAction());
      RIAPUtils.exhaust(result);

    }
  }

  /**
   * Create a ConverterModelDTO object with the specified description and identifier
   * 
   * @param description
   *          the description
   * @param id
   *          the ConverterModelDTO identifier
   * @return the created ConverterModelDTO
   */
  public ConverterModelDTO createConverterObject(String description, String id) {
    ConverterModelDTO conv = new ConverterModelDTO();

    conv.setClassName(classname);
    conv.setDescriptionAction(description);
    conv.setName("TestConverter");
    conv.setClassAuthor("AKKA/CNES");
    conv.setClassVersion("1.0");
    conv.setDescription("FOR TEST PURPOSE ONLY, DON'T USE IT, IT DOESN'T DO ANYTHING");
    conv.setId(id);

    ConverterParameter param1 = new ConverterParameter("1", "1", ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    param1.setValue("param1_value");
    ConverterParameter param2 = new ConverterParameter("2", "2", ConverterParameterType.CONVERTER_PARAMETER_INTERN);
    param2.setValue("param2_value");

    conv.getParameters().add(param1);
    conv.getParameters().add(param2);

    return conv;

  }

  /**
   * Add a converter to a Dataset
   * 
   * @param item
   *          ConverterModelDTO
   */
  public void create(ConverterModelDTO item) {
    Representation rep = getRepresentation(item, getMediaTest());
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("POST", "A <i>ConverterChainedModelDTO</i> object");
      postDocAPI(String.format(getBaseUrl(), datasetId), "", rep, parameters,
          String.format(getBaseUrl(), "%identifier%"));
    }
    else {
      ClientResource cr = new ClientResource(String.format(getBaseUrl(), datasetId));
      Representation result = cr.post(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ConverterModelDTO.class);
      assertTrue(response.getSuccess());
      ConverterModelDTO conv = (ConverterModelDTO) response.getItem();
      assertEquals(item.getId(), conv.getId());
      assertEquals(item.getDescriptionAction(), conv.getDescriptionAction());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Add a converter to a Dataset and assert that the validation process has failed
   * 
   * @param item
   *          ConverterModelDTO
   * @param nbViolations
   *          the number of violations to assert
   * 
   */
  public void createWithValidation(ConverterModelDTO item, int nbViolations) {
    Representation rep = getRepresentation(item, getMediaTest());
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("POST", "A <i>ConverterModelDTO</i> object");
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
   * Invoke GET, Gets the converterChainedModel details
   * 
   * @return the converterChainedModel details
   */
  public ConverterChainedModelDTO retrieveConvChained() {

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("converterChainedId", "converterChained identifier");
      retrieveDocAPI(String.format(getBaseUrl(), datasetId), "", parameters,
          String.format(getBaseUrl(), "%identifier%") + "/%converterChainedId%");
    }
    else {
      String url = String.format(getBaseUrl(), datasetId);
      ClientResource cr = new ClientResource(url);
      docAPI.appendRequest(Method.GET, cr);

      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ConverterChainedModelDTO.class);
      assertTrue(response.getSuccess());
      ConverterChainedModelDTO convOut = (ConverterChainedModelDTO) response.getItem();
      assertEquals(datasetId, convOut.getId());
      RIAPUtils.exhaust(result);
      return convOut;
    }
    return null;
  }

  /**
   * Invoke GET
   * 
   * 
   * 
   * @param item
   *          ConverterModelDTO
   */
  public void retrieveConverter(ConverterModelDTO item) {
    String url = String.format(getBaseUrl(), datasetId) + "/" + item.getId();

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("converterChainedId", "ConverterChained identifier");
      parameters.put("converterId", "converter identifier");
      retrieveDocAPI(url, "", parameters, String.format(getBaseUrl(), "%identifier%")
          + "/%converterChainedId%/%converterId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      docAPI.appendRequest(Method.GET, cr);

      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ConverterModelDTO.class);
      assertTrue(response.getSuccess());
      ConverterModelDTO convOut = (ConverterModelDTO) response.getItem();
      assertEquals(item.getDescriptionAction(), convOut.getDescriptionAction());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Update the given ConverterModelDTO in the given converterChained
   * 
   * @param item
   *          the ConverterModelDTO
   * 
   */
  public void update(ConverterModelDTO item) {
    Representation rep = getRepresentation(item, getMediaTest());
    String url = String.format(getBaseUrl(), datasetId) + "/" + item.getId();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("converterChainedId", "ConverterChained identifier");
      parameters.put("converterId", "converter identifier");
      parameters.put("PUT", "A <i>ConverterChainedModelDTO</i> object");
      putDocAPI(url, "", rep, parameters, String.format(getBaseUrl(), "%identifier%")
          + "/%converterChainedId%/%converterId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ConverterModelDTO.class);
      assertTrue(response.getSuccess());
      ConverterModelDTO convOut = (ConverterModelDTO) response.getItem();
      assertEquals(item.getDescriptionAction(), convOut.getDescriptionAction());
      RIAPUtils.exhaust(result);

    }
  }

  /**
   * Update a converter and assert if the validation process has failed
   * 
   * @param item
   *          ConverterModelDTO
   * @param nbViolations
   *          the number of violations to assert
   * 
   */
  public void updateWithValidation(ConverterModelDTO item, int nbViolations) {
    Representation rep = getRepresentation(item, getMediaTest());
    String url = String.format(getBaseUrl(), datasetId) + "/" + item.getId();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("filterId", "converter identifier");
      parameters.put("PUT", "A <i>ConverterModelDTO</i> object");
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
   * Delete a converterModel from a converterChained
   * 
   * @param item
   *          ConverterModelDTO to delete
   */
  public void deleteConv(ConverterModelDTO item) {
    String url = String.format(getBaseUrl(), datasetId) + "/" + item.getId();

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("converterChainedId", "ConverterChained identifier");
      parameters.put("converterId", "converter identifier");
      deleteDocAPI(url, "", parameters, String.format(getBaseUrl(), "%identifier%")
          + "/%converterChainedId%/%converterId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ConverterModelDTO.class);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Invoke DELETE
   * 
   */
  public void deleteConvChained() {
    String url = String.format(getBaseUrl(), datasetId);

    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("identifierConvert", "convert identifier");
      deleteDocAPI(url, "", parameters, String.format(getBaseUrl(), "%identifier%") + "/%identifierConvert%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ConverterChainedModelDTO.class);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);

    }
  }

  /**
   * Change the order of the converters in the converterChained
   * 
   * @param first
   *          the first Converter
   * @param second
   *          the second Converter
   * 
   */
  private void changeOrder(ConverterModelDTO first, ConverterModelDTO second) {
    List<String> listId = new ArrayList<String>();
    listId.add(first.getId());
    listId.add(second.getId());

    ConverterChainedOrderDTO dto = new ConverterChainedOrderDTO();
    dto.setId(datasetId);
    dto.setIdOrder(listId);

    Representation rep = getRepresentationDTO(dto, getMediaTest());
    String url = String.format(getBaseUrl(), datasetId);
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("converterChainedId", "ConverterChained identifier");
      parameters.put("PUT", "A <i>ConverterChainedModelDTO</i> object");
      putDocAPI(url, "", rep, parameters, String.format(getBaseUrl(), "%identifier%") + "/%converterChainedId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ConverterChainedModelDTO.class);
      assertTrue(response.getSuccess());
      ConverterChainedModelDTO convOut = (ConverterChainedModelDTO) response.getItem();
      assertNotNull(convOut);
      assertConvOrder(first, second);
      RIAPUtils.exhaust(result);

    }

  }

  /**
   * Assert that the first converter is first and the second converter is second in the converterChained
   * 
   * @param first
   *          the first Converter
   * @param second
   *          the second Converter
   */
  private void assertConvOrder(ConverterModelDTO first, ConverterModelDTO second) {
    ConverterChainedModelDTO convChainedOut = retrieveConvChained();

    assertNotNull(convChainedOut);
    List<ConverterModelDTO> converters = convChainedOut.getConverters();
    assertNotNull(converters);
    assertEquals(2, converters.size());

    ConverterModelDTO firstOut = converters.get(0);
    assertNotNull(firstOut);
    assertEquals(first.getId(), firstOut.getId());

    ConverterModelDTO secondOut = converters.get(1);
    assertNotNull(secondOut);
    assertEquals(second.getId(), secondOut.getId());

  }

  /**
   * Start the converter
   * 
   * @param model
   *          the ConverterModelDTO to start
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if release fails
   */
  private void start(ConverterModelDTO model) throws IOException {

    StringRepresentation rep = new StringRepresentation("");
    String url = String.format(getBaseUrl(), datasetId) + "/" + model.getId() + "/start";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      parameters.put("converterId", "converter identifier");
      putDocAPI(url, "", rep, parameters, String.format(getBaseUrl(), "%identifier%") + "/%filterId%/start");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ConverterModelDTO.class);
      assertTrue(response.getSuccess());
      ConverterModelDTO converterModel = (ConverterModelDTO) response.getItem();
      assertStatus("ACTIVE", converterModel);
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Stop the converter
   * 
   * @param model
   *          the ConverterModelDTO to start
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if release fails
   */
  private void stop(ConverterModelDTO model) throws IOException {
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
      Response response = getResponse(getMediaTest(), result, ConverterModelDTO.class);
      assertTrue(response.getSuccess());
      ConverterModelDTO converterModel = (ConverterModelDTO) response.getItem();
      assertStatus("INACTIVE", converterModel);
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
  private void assertStatus(String status, ConverterModelDTO modelOut) {
    assertEquals(status, modelOut.getStatus());
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

    Response response = getResponse(getMediaTest(), result, ConverterChainedModelDTO.class);
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
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.alias("response", Response.class);
      if (dataClass == ConverterChainedModelDTO.class) {
        xstream.alias("converterChainedModel", ConverterChainedModelDTO.class);
        xstream.alias("converterModel", ConverterModelDTO.class);
      }
      xstream.alias("converterParameter", ConverterParameter.class);

      if (dataClass == ConstraintViolation.class) {
        xstream.alias("constraintViolation", ConstraintViolation.class);
      }

      if (isArray) {
        if (dataClass == ConverterChainedModelDTO.class) {
          xstream.addImplicitCollection(ConverterChainedModelDTO.class, "data", dataClass);
        }
        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (media.equals(MediaType.APPLICATION_JSON)) {
          if (dataClass == ConverterChainedModelDTO.class) {
            xstream.addImplicitCollection(ConverterChainedModelDTO.class, "converters", ConverterModelDTO.class);
          }
          xstream.addImplicitCollection(ConverterModelDTO.class, "parameters", ConverterParameter.class);
        }

        if (dataClass == ConverterChainedModelDTO.class) {
          xstream.aliasField("converterChainedModel", Response.class, "item");
        }
        if (dataClass == ConverterModelDTO.class) {
          xstream.aliasField("converter", Response.class, "item");
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
  public static Representation getRepresentation(ConverterModelDTO item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<ConverterModelDTO>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<ConverterModelDTO> rep = new XstreamRepresentation<ConverterModelDTO>(media, item);
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
   * Builds XML or JSON Representation of Project for Create and Update methods.
   * 
   * @param item
   *          Project
   * @param media
   *          APPLICATION_XML or APPLICATION_JSON
   * @return XML or JSON Representation
   */
  public static Representation getRepresentationDTO(ConverterChainedOrderDTO item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JsonRepresentation(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<ConverterChainedOrderDTO> rep = new XstreamRepresentation<ConverterChainedOrderDTO>(media,
          item);
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
   * Configures XStream mapping of Response object with ConverterModelDTO content.
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
