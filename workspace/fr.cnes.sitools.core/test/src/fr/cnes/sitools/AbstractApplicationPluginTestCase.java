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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.plugins.applications.dto.ApplicationPluginModelDTO;
import fr.cnes.sitools.plugins.applications.model.ApplicationPluginParameter;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

// TODO test GET sur /admin/{applicationPluginId}

/**
 * Tests converters
 * 
 * @author AKKA Technologies
 * 
 */
public abstract class AbstractApplicationPluginTestCase extends AbstractSitoolsServerTestCase {
  /**
   * Classname for validation test
   */
  private String classname = "fr.cnes.sitools.applications.tests.ApplicationTest";

  private static final String idApplication = "123456789";

  private static final int NB_APPLICATION_BASE = 1;

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_PLUGINS_APPLICATIONS_URL);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
  }

  /**
   * Test getting converter list.
   */
  @Test
  public void testCRUD() {
    docAPI.setActive(false);
    try {
      // Get application list
      getApplicationList();
      // CRUD APPLICATION
      assertNone();
      // create object
      ApplicationPluginModelDTO model = createObject(idApplication);
      // create ApplicationPluginModelDTO
      create(model);
      // Get the application created
      getApplicationByName(model);
      getApplicationById(model);
      // get all app
      retrieveAllApp(1);
      // start the application
      start(model);
      // check application running
      checkAppRunning(model);
      // update the ApplicationPluginModelDTO, fail because Application active
      update(model, false);
      // stop the application
      stop(model);
      // check that the application is stopped
      checkAppStopped(model);
      // modify the label
      model.setLabel("MODIFY APPLICATION");
      // update the application
      update(model, true);
      // start the application
      start(model);
      // check application running
      checkAppRunning(model);
      // delete ApplicationPluginModelDTO
      delete(model.getId());
      // CRUD APPLICATION
      assertNone();
      // create WADL
      createWadl(getBaseUrl(), "applications_plugins");
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Produces DOC API for converters
   */
  @Test
  public void testCRUDdocAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("ApplicationPlugins API");
    docAPI.appendSubChapter("Getting ApplicationPlugins list", "list");
    try {
      // Get the list of applications
      getApplicationList();
      // CRUD APPLICATION
      // create object
      ApplicationPluginModelDTO model = createObject(idApplication);
      docAPI.appendSubChapter("Create an ApplicationPlugins ", "create");
      // create ApplicationPluginModelDTO
      create(model);
      // Get the application created by class name or id
      docAPI.appendSubChapter("get ApplicationPlugins ", "get");
      getApplicationByName(model);
      getApplicationById(model);
      docAPI.appendSubChapter("get the list of ApplicationPlugins ", "getList");
      // get all app
      retrieveAllApp(1);
      // start the application
      docAPI.appendSubChapter("Start the application ", "start");
      start(model);
      docAPI.appendSubChapter("Check the application is running ", "check");
      // check application running
      checkAppRunning(model);
      docAPI.appendSubChapter("Update the model ", "updateFail");
      docAPI.appendComment("Supposed to fail because Application is running");
      // update the ApplicationPluginModelDTO, fail because Application active
      update(model, false);
      // stop the application
      docAPI.appendSubChapter("Stop the application ", "stop");
      stop(model);
      // modify the label
      model.setLabel("MODIFY APPLICATION");
      docAPI.appendSubChapter("Update the application ", "updateOk");
      // update the application
      update(model, true);
      docAPI.appendSubChapter("Start the application ", "start");
      // start the application
      start(model);
      // check application running
      checkAppRunning(model);

      docAPI.appendSubChapter("Delete an ApplicationPlugins ", "delete");
      // delete ApplicationPluginModelDTO
      delete(model.getId());

      docAPI.appendSubChapter("Create an ApplicationPlugin but fail because of validation ", "validation");
      // create a new applicationPlugin
      ApplicationPluginModelDTO appModel = createObjectForValidation("app_description_1", idApplication);
      // change the params value for the validation to fail
      appModel.getParameters().get(0).setValue("param1_value_changed");
      appModel.getParameters().get(1).setValue("param2_value_changed");
      // add it to the server, it will fail with 2 violations
      createWithValidation(appModel, 2);

    }
    catch (IOException e) {
      e.printStackTrace();
    }
    docAPI.close();

  }

  /**
   * Test CRUD Graph with JSon format exchanges.
   */
  @Test
  public void testCRUDwithValidation() {
    try {
      docAPI.setActive(false);
      assertNone();
      // create a new applicationPlugin
      ApplicationPluginModelDTO appModel = createObjectForValidation("app_description_1", idApplication);
      // change the params value for the validation to fail
      appModel.getParameters().get(0).setValue("param1_value_changed");
      appModel.getParameters().get(1).setValue("param2_value_changed");
      // add it to the server, it will fail with 2 violations
      createWithValidation(appModel, 2);
      // change the params value for only 1 violation
      appModel.getParameters().get(0).setValue("param1_value");
      // add it to the server, it will fail with 2 violations
      createWithValidation(appModel, 1);
      // change the classname to test with a not existing class name
      appModel.setClassName("applicationPlugin");
      // add it to the server, it will fail with 1 violation
      createWithValidation(appModel, 1);
      // set the classname to an existing class name
      appModel.setClassName(classname);
      // change the params value to create it
      appModel.getParameters().get(1).setValue("param2_value");
      // create the filter
      create(appModel);
      // change the params value for the update to fail
      appModel.getParameters().get(1).setValue("param2_value_changed");
      updateWithValidation(appModel, 1);
      // delete the filterChained
      delete(appModel.getId());
      assertNone();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Check if the application is running
   * 
   * @param model
   *          ApplicationPluginModelDTO to check
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if release fails
   */
  private void checkAppRunning(ApplicationPluginModelDTO model) throws IOException {
    String url = getHostUrl() + model.getUrlAttach();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Check if the application is stopped
   * 
   * @param model
   *          ApplicationPluginModelDTO to check
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if release fails
   */
  private void checkAppStopped(ApplicationPluginModelDTO model) throws IOException {
    String url = getHostUrl() + model.getUrlAttach();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {
      final Client client = new Client(Protocol.HTTP);
      Request request = new Request(Method.GET, url);
      org.restlet.Response response = null;
      try {
        response = client.handle(request);

        assertNotNull(response);
        assertTrue(response.getStatus().isError());
        assertEquals(Status.CLIENT_ERROR_NOT_FOUND, response.getStatus());

      }
      finally {
        if (response != null) {
          RIAPUtils.exhaust(response);
        }
      }
    }

  }

  /**
   * Assert if there are no SvaTasks
   * 
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if release fails
   */
  private void assertNone() throws IOException {

    retrieveAllApp(0);
  }

  /**
   * Retrieve all the current application and assert that number of application is numberOfApplications
   * 
   * @param numberOfApplications
   *          the number of applications expected
   */
  private void retrieveAllApp(int numberOfApplications) {
    String url = getBaseUrl() + "/instances";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(url, "", parameters, url);
    }
    else {

      ClientResource cr = new ClientResource(url);

      Representation result = cr.get(getMediaTest());

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      // String txt;
      // try {
      // txt = result.getText();
      // System.out.println(txt);
      // }
      // catch (IOException e) {
      // // TODO Auto-generated catch block
      // e.printStackTrace();
      // }

      Response response = getResponse(getMediaTest(), result, ApplicationPluginModelDTO.class, true);
      assertTrue(response.getSuccess());
      assertEquals(NB_APPLICATION_BASE + numberOfApplications, response.getTotal().intValue());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * getApplicationPluginList Calls GET on converter list URL.
   * 
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if release fails
   */
  private void getApplicationList() throws IOException {
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(getBaseUrl() + "/classes", "", parameters, getBaseUrl() + "/classes");
    }
    else {
      ClientResource cr = new ClientResource(getBaseUrl() + "/classes");
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * getApplicationPluginList Calls GET on a single application.
   * 
   * @param model
   *          the model to get with its name
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if release fails
   */
  private void getApplicationByName(ApplicationPluginModelDTO model) throws IOException {
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(getBaseUrl() + "/classes/" + model.getClassName(), "", parameters, getBaseUrl() + "/classes/" + model.getClassName());
    }
    else {
      ClientResource cr = new ClientResource(getBaseUrl() + "/classes/" + model.getClassName());
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * getApplicationPluginList Calls GET on a single application.
   * 
   * @param model
   *          the model to get with its ID
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if release fails
   */
  private void getApplicationById(ApplicationPluginModelDTO model) throws IOException {
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      retrieveDocAPI(getBaseUrl() + "/instances/" + model.getId(), "", parameters, getBaseUrl() + "/instances/" + model.getId());
    }
    else {
      ClientResource cr = new ClientResource(getBaseUrl() + "/instances/" + model.getId());
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * POST of ApplicationPluginModelDTO to create the object
   * 
   * @param model
   *          the ApplicationPluginModelDTO
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if release fails
   */
  private void create(ApplicationPluginModelDTO model) throws IOException {
    Representation appRep = getRepresentation(model, getMediaTest());
    String url = getBaseUrl() + "/instances";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("POST", "A <i>ApplicationPluginModelDTO</i> object");
      postDocAPI(url, "", appRep, parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.post(appRep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ApplicationPluginModelDTO.class);
      assertTrue(response.getSuccess());
      ApplicationPluginModelDTO modelOut = (ApplicationPluginModelDTO) response.getItem();
      assertApplicationPluginModelDTO(model, modelOut);
      assertStatus("NEW", modelOut);
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Add an ApplicationPlugin and assert that the validation process has failed
   * 
   * @param item
   *          ApplicationPluginModelDTO
   * @param nbViolations
   *          the number of violations to assert
   * 
   */
  public void createWithValidation(ApplicationPluginModelDTO item, int nbViolations) {
    Representation appRep = getRepresentation(item, getMediaTest());
    String url = getBaseUrl() + "/instances";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("POST", "A <i>ApplicationPluginModelDTO</i> object");
      postDocAPI(url, "", appRep, parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.post(appRep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ConstraintViolation.class, true);
      assertFalse(response.getSuccess());
      ArrayList<Object> list = response.getData();
      assertEquals(nbViolations, list.size());
    }
  }

  /**
   * Update the ApplicationPluginModelDTO, check if the success is expected
   * 
   * @param model
   *          the ApplicationPluginModelDTO
   * @param successExpected
   *          true if the update is supposed to succeed, false otherwiseF
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if release fails
   */
  private void update(ApplicationPluginModelDTO model, boolean successExpected) throws IOException {
    Representation appRep = getRepresentation(model, getMediaTest());
    String url = getBaseUrl() + "/instances/" + model.getId();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("PUT", "A <i>ApplicationPluginModelDTO</i> object");
      parameters.put("appPluginId", "ApplicationPluginModelDTO identifier");
      String template = getBaseUrl() + "/instances/%appPluginId%";
      putDocAPI(url, "", appRep, parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(appRep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ApplicationPluginModelDTO.class);
      if (successExpected) {
        assertTrue(response.getSuccess());
        ApplicationPluginModelDTO modelOut = (ApplicationPluginModelDTO) response.getItem();
        assertApplicationPluginModelDTO(model, modelOut);
        assertStatus("INACTIVE", modelOut);
      }
      else {
        assertFalse(response.getSuccess());
      }
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Add an ApplicationPlugin and assert that the validation process has failed
   * 
   * @param model
   *          ApplicationPluginModelDTO
   * @param nbViolations
   *          the number of violations to assert
   * 
   */
  public void updateWithValidation(ApplicationPluginModelDTO model, int nbViolations) {
    Representation appRep = getRepresentation(model, getMediaTest());
    String url = getBaseUrl() + "/instances/" + model.getId();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("POST", "A <i>ApplicationPluginModelDTO</i> object");
      postDocAPI(url, "", appRep, parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(appRep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ConstraintViolation.class, true);
      assertFalse(response.getSuccess());
      ArrayList<Object> list = response.getData();
      assertEquals(nbViolations, list.size());
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
  private void assertStatus(String status, ApplicationPluginModelDTO modelOut) {
    assertEquals(status, modelOut.getStatus());
  }

  /**
   * Assert ApplicationPluginModelDTO
   * 
   * @param model
   *          expected
   * @param modelOut
   *          actual
   */
  private void assertApplicationPluginModelDTO(ApplicationPluginModelDTO model, ApplicationPluginModelDTO modelOut) {
    assertEquals(model.getLabel(), modelOut.getLabel());
    assertEquals(model.getUrlAttach(), modelOut.getUrlAttach());
    assertEquals(model.getClassName(), modelOut.getClassName());
  }

  /**
   * Create an Object ApplicationPLuginModel
   * 
   * @param id
   *          the id od the ApplicationPluginModelDTO to create
   * @return the created ApplicationPluginModelDTO
   */
  private ApplicationPluginModelDTO createObject(String id) {
    ApplicationPluginModelDTO model = new ApplicationPluginModelDTO();

    model.setClassName("fr.cnes.sitools.applications.basic.BasicApp");
    model.setId(id);
    model.setLabel("new Application plugin");
    model.setUrlAttach("/testApp");
    model.setCategory(Category.USER);

    return model;
  }

  /**
   * Create a ApplicationPluginModelDTO object with the specified description and identifier
   * 
   * @param description
   *          the description
   * @param id
   *          the ApplicationPluginModelDTO identifier
   * @return the created ApplicationPluginModelDTO
   */
  public ApplicationPluginModelDTO createObjectForValidation(String description, String id) {
    ApplicationPluginModelDTO appModel = new ApplicationPluginModelDTO();

    appModel.setClassName(classname);
    appModel.setDescriptionAction(description);
    appModel.setName("TestApplicationPlugin");
    appModel.setClassAuthor("AKKA/CNES");
    appModel.setClassVersion("1.0");
    appModel.setClassOwner("AKKA/CNES");
    appModel.setDescription("FOR TEST PURPOSE ONLY, DON'T USE IT, IT DOESN'T DO ANYTHING");
    appModel.setId(id);
    appModel.setUrlAttach("/applicationTest");

    ApplicationPluginParameter param1 = new ApplicationPluginParameter("1", "1");
    param1.setValue("param1_value");
    ApplicationPluginParameter param2 = new ApplicationPluginParameter("2", "2");
    param2.setValue("param2_value");

    appModel.getParameters().add(param1);
    appModel.getParameters().add(param2);

    return appModel;

  }

  /**
   * Delete the ApplicationPluginModelDTO according to the given id
   * 
   * @param id
   *          the id of the ApplicationPluginModelDTO to delete
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if release fails
   */
  private void delete(String id) throws IOException {
    String url = getBaseUrl() + "/instances/" + id;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("appPluginId", "ApplicationPluginModelDTO identifier");
      String template = getBaseUrl() + "/instances/%appPluginId%";
      deleteDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ApplicationPluginModelDTO.class);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Stop the application
   * 
   * @param model
   *          the ApplicationPluginModelDTO to stop
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if release fails
   */
  private void stop(ApplicationPluginModelDTO model) throws IOException {

    StringRepresentation projRep = new StringRepresentation("");
    String url = getBaseUrl() + "/instances/" + model.getId() + "/stop";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("appPluginId", "ApplicationPluginModelDTO identifier");
      String template = getBaseUrl() + "/instances/%appPluginId%/stop";
      putDocAPI(url, "", projRep, parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(projRep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ApplicationPluginModelDTO.class);
      assertTrue(response.getSuccess());
      ApplicationPluginModelDTO modelOut = (ApplicationPluginModelDTO) response.getItem();
      assertStatus("INACTIVE", modelOut);
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Start the application
   * 
   * @param model
   *          the ApplicationPluginModelDTO to start
   * @throws IOException
   *           Exception when copying configuration files from TEST to data/TESTS if release fails
   */
  private void start(ApplicationPluginModelDTO model) throws IOException {

    StringRepresentation projRep = new StringRepresentation("");
    String url = getBaseUrl() + "/instances/" + model.getId() + "/start";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("appPluginId", "ApplicationPluginModelDTO identifier");
      String template = getBaseUrl() + "/instances/%appPluginId%/start";
      putDocAPI(url, "", projRep, parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(projRep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ApplicationPluginModelDTO.class);
      assertTrue(response.getSuccess());
      ApplicationPluginModelDTO modelOut = (ApplicationPluginModelDTO) response.getItem();
      assertStatus("ACTIVE", modelOut);
      RIAPUtils.exhaust(result);
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
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("ApplicationPluginModel", ApplicationPluginModelDTO.class);
      xstream.alias("parameters", ApplicationPluginParameter.class);

      if (dataClass == ConstraintViolation.class) {
        xstream.alias("constraintViolation", ConstraintViolation.class);
      }

      if (isArray) {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
        // xstream.omitField(Response.class, "data");
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
          xstream.addImplicitCollection(ApplicationPluginModelDTO.class, "parameters", ApplicationPluginParameter.class);

        }

      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == ApplicationPluginModelDTO.class) {
          xstream.aliasField("ApplicationPluginModel", Response.class, "item");
          if (media.isCompatible(MediaType.APPLICATION_JSON)) {
            xstream.addImplicitCollection(ApplicationPluginModelDTO.class, "parameters", ApplicationPluginParameter.class);
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
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
        // TODO complete test with ObjectRepresentation
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
  public static Representation getRepresentation(ApplicationPluginModelDTO item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<ApplicationPluginModelDTO>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<ApplicationPluginModelDTO> rep = new XstreamRepresentation<ApplicationPluginModelDTO>(media, item);
      configure(xstream);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
      return null;
      // TODO complete test with ObjectRepresentation
    }
  }

  /**
   * Configures XStream mapping for Response object with Project content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);
    xstream.alias("ApplicationPluginModel", ApplicationPluginModelDTO.class);
  }

}
