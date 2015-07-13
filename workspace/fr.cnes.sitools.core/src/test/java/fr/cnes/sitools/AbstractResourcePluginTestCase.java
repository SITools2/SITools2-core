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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.plugins.resources.dto.ResourcePluginDescriptionDTO;
import fr.cnes.sitools.plugins.resources.model.ResourceBehaviorType;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.plugins.resources.model.ResourceParameterType;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.GetRepresentationUtils;
import fr.cnes.sitools.utils.GetResponseUtils;

/**
 * Base test class for resource plugins
 * 
 * @author m.marseille (AKKA Technologies)
 */
public abstract class AbstractResourcePluginTestCase extends AbstractSitoolsServerTestCase {

  /**
   * Project name for tests
   */
  private static final String PROJECT_NAME = "premier";

  /**
   * Project attachment for users
   */
  private static final String PROJECT_ATTACH = "/proj/premier";

  /**
   * Plugin attachment
   */
  private static final String PLUGIN_ATTACH = "/plugintest";

  /**
   * Plugin attachment modified
   */
  private static final String PLUGIN_ATTACH_MODIFIED = "/modified";

  /** dataset id for test purpose. */
  private static final String DATASET_ID = "bf77955a-2cec-4fc3-b95d-7397025fb299";
  /** Application class Name for test purpose on Dataset */
  private static final String DATASET_CLASS_NAME = "fr.cnes.sitools.dataset.DataSetApplication";

  /**
   * Resource plugin class name
   */
  private String resourcePluginClassName = "fr.cnes.sitools.resources.basic.BasicParameterizedResourceModel";

  /**
   * Resource DTO for asserts
   */
  private ResourceModelDTO resourceDto;
  /**
   * Classname for validation test
   */
  private String classname = "fr.cnes.sitools.resources.test.TestParameterizedResourceModel";

  /** number of columns of the dataset */
  private int nbColumnDataset = 7;

  /** number of parameters of the Resource by default */
  private int nbParamResource = 10;

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  public final String getProjectBaseUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_PROJECTS_URL) + "/" + PROJECT_NAME;
  }

  /**
   * Get the url for attachment of resource to project
   * 
   * @return url
   */
  public final String getProjectResourcesUrl() {
    return getProjectBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_RESOURCES_URL);
  }

  /**
   * Project attachment url
   * 
   * @return url
   */
  public final String getProjectAttachmentUrl() {
    return super.getHostUrl() + PROJECT_ATTACH;
  }

  /**
   * Get the url for plugin listings
   * 
   * @return the URL
   */
  public final String getResourcePluginUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_PLUGINS_RESOURCES_URL);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
  }

  /**
   * Test the plugin exposition
   */
  @Test
  public void testPluginsExposition() {
    // get the list of plugins, expect an error because appClassName is a mandatory parameter
    getListOfPlugins(null, false);
    // get the list of plugins
    getListOfPlugins(DATASET_CLASS_NAME, true);

    // get a particular plugin definition, with its parameters, expect an error because appClassName and parent are
    // mandatory parameters
    getPlugin(null, null, -1, false);
    // get a particular plugin definition, with its parameters, expect nbParamResource parameters because the given
    // datasetId is not a valid datasetId
    getPlugin(DATASET_CLASS_NAME, DATASET_ID + "test", nbParamResource, true);
    // get a particular plugin definition, with its parameters, expect nbParamResource + nbColumnDataset parameters
    // because the given
    // datasetId is a valid datasetId
    getPlugin(DATASET_CLASS_NAME, DATASET_ID, nbParamResource + nbColumnDataset, true);
  }

  /**
   * Test of exposition, then attaching and using
   * 
   * @throws IOException
   *           if problem
   */
  @Test
  public void testPluginCRUD() throws IOException {
    resourceDto = createResourceModelDTO();
    create(resourceDto);
    getPluginResponse();
    modifyPlugin();
    deletePlugin();
  }

  /**
   * Test of attaching to a particular application not compatible
   * 
   * @throws IOException
   *           if problem
   */
  @Test
  public void testPluginCRUDWithApplication() throws IOException {
    getListOfPluginsWithApplicationParameter();

    // Attachement sur une ProjectApplication d'une resource compatible DataSetApplication => ERREUR
    ResourceModelDTO resModel = createResourceModelDTO();
    // bistouille pour tester le cas ou l'applicationClassName =
    // fr.cnes.sitools.dataset.DataSetApplication
    resModel.setApplicationClassName("fr.cnes.sitools.dataset.DataSetApplication");
    create(resModel);
    getPluginResponseError503();
    resourceDto = resModel;
    deletePlugin();

    // Attachement sur une ProjectApplication d'une resource compatible ProjectApplication => OK
    ResourceModelDTO resModel2 = createResourceModelDTO();
    resModel2.setApplicationClassName("fr.cnes.sitools.project.ProjectApplication");
    create(resModel2);
    getPluginResponse();
    resourceDto = resModel2;
    deletePlugin();

    // Attachement sur une ProjectApplication d'une resource compatible SitoolsParameterizedApplication => OK
    ResourceModelDTO resModel3 = createResourceModelDTO();
    // bistouille pour tester le cas ou l'applicationClassName =
    // fr.cnes.sitools.common.application.SitoolsParameterizedApplication
    resModel3.setApplicationClassName("fr.cnes.sitools.common.application.SitoolsParameterizedApplication");
    create(resModel3);
    getPluginResponse();
    resourceDto = resModel3;
    deletePlugin();

    // Attachement sur une ProjectApplication d'une resource compatible avec toutes les applications => OK
    ResourceModelDTO resModel4 = createResourceModelDTO();
    resModel.setApplicationClassName("");
    create(resModel4);
    getPluginResponse();
    resourceDto = resModel4;
    deletePlugin();
  }

  /**
   * Test CRUD Graph with JSon format exchanges.
   */
  @Test
  public void testCRUDwithValidation() {

    docAPI.setActive(false);
    // create a new resourcePlugin
    ResourceModelDTO resModel = createObjectForValidation("100000");
    // change the params value for the validation to fail
    getParameterByName("1", resModel).setValue("param1_value_changed");
    getParameterByName("2", resModel).setValue("param2_value_changed");
    // add it to the server, it will fail with 2 violations
    createWithValidation(resModel, 2);

    // change the params value for only 1 violation
    getParameterByName("1", resModel).setValue("param1_value");
    // add it to the server, it will fail with 2 violations
    createWithValidation(resModel, 1);

    // change the classname to test with a not existing class name
    resModel.setClassName("resourcePlugin");

    // add it to the server, it will fail with 1 violation
    createWithValidation(resModel, 1);

    // set the classname to an existing class name
    resModel.setClassName(classname);

    // change the params value to create it
    getParameterByName("2", resModel).setValue("param2_value");
    // create the filter
    create(resModel);
    // change the params value for the update to fail
    getParameterByName("1", resModel).setValue("param2_value_changed");
    updateWithValidation(resModel, 1);
    // delete the filterChained
    deletePlugin();

  }

  /**
   * Test CRUD Graph with JSon format exchanges.
   */
  @Test
  public void testCRUDwithDateTemplate() {

    docAPI.setActive(false);
    // create a new resourcePlugin
    ResourceModelDTO resModel = createObjectForValidation("100000");

    // add it to the server, it will fail with 2 violations
    create(resModel);

    deletePlugin();

  }

  /**
   * Get the list of plugins
   */
  private void getListOfPluginsWithApplicationParameter() {
    Representation rep = null;
    try {
      ClientResource cr = new ClientResource(getResourcePluginUrl()
          + "/classes?appClassName=fr.cnes.sitools.common.application.SitoolsParameterizedApplication&parent="
          + PROJECT_NAME);
      rep = cr.get(getMediaTest());
      assertNotNull(rep);
      assertTrue(cr.getStatus().isSuccess());

      Response responsePlugin = getResponse(getMediaTest(), rep, ResourcePluginDescriptionDTO.class, true);
      assertNotNull(responsePlugin);
      assertNotNull(responsePlugin.getData());
      ArrayList<Object> data = responsePlugin.getData();
      for (Object object : data) {
        ResourcePluginDescriptionDTO dto = (ResourcePluginDescriptionDTO) object;
        assertNotNull(dto);
      }
    }
    finally {
      RIAPUtils.exhaust(rep);
    }
  }

  /**
   * Get plugin description from previous test
   * 
   * @param expectSuccess
   */
  private void getPlugin(String appClassName, String parent, int expectedParams, boolean expectSuccess) {
    Representation rep = null;
    try {
      ClientResource cr = new ClientResource(getResourcePluginUrl() + "/classes/" + this.resourcePluginClassName);

      if (appClassName != null && !appClassName.equals("")) {
        cr.getReference().addQueryParameter("appClassName", appClassName);
      }
      if (parent != null && !parent.equals("")) {
        cr.getReference().addQueryParameter("parent", parent);
      }

      rep = cr.get(getMediaTest());
      assertNotNull(rep);
      assertTrue(cr.getStatus().isSuccess());

      Response responsePlugin = getResponse(getMediaTest(), rep, ResourceModelDTO.class);
      assertNotNull(responsePlugin);
      assertEquals(expectSuccess, responsePlugin.getSuccess());
      if (expectSuccess) {
        assertNotNull(responsePlugin.getItem());
        ResourceModelDTO dto = (ResourceModelDTO) responsePlugin.getItem();
        assertNotNull(dto.getParameters());
        assertEquals(expectedParams, dto.getParameters().size());
      }
    }
    finally {
      RIAPUtils.exhaust(rep);
    }
  }

  /**
   * Get the list of plugins
   */
  private void getListOfPlugins(String appClassName, boolean expectSuccess) {
    Representation rep = null;
    try {
      ClientResource cr = new ClientResource(getResourcePluginUrl() + "/classes");
      if (appClassName != null && !appClassName.equals("")) {
        cr.getReference().addQueryParameter("appClassName", appClassName);
      }
      rep = cr.get(getMediaTest());
      assertNotNull(rep);
      assertTrue(cr.getStatus().isSuccess());

      Response responsePlugin = getResponse(getMediaTest(), rep, ResourcePluginDescriptionDTO.class, true);
      assertNotNull(responsePlugin);
      assertEquals(expectSuccess, responsePlugin.getSuccess());
      if (expectSuccess) {
        assertNotNull(responsePlugin.getData());
      }
    }
    finally {
      RIAPUtils.exhaust(rep);
    }
  }

  /**
   * Create a resourceModel
   * 
   * @return
   */
  private ResourceModelDTO createResourceModelDTO() {
    ResourceModelDTO model = new ResourceModelDTO();
    model.setId("1000000");
    model.setName("testResource");

    model.getParameters()
        .add(new ResourceParameter("fileName", "fileName", ResourceParameterType.PARAMETER_USER_INPUT));
    model.getParameters().add(new ResourceParameter("url", "urlAttach", ResourceParameterType.PARAMETER_ATTACHMENT));
    model.getParameters().add(new ResourceParameter("text", "textToSend", ResourceParameterType.PARAMETER_INTERN));
    getParameterByName("url", model).setValue(PLUGIN_ATTACH);
    getParameterByName("text", model).setValue("TEST");
    model.setResourceClassName("fr.cnes.sitools.resources.basic.BasicParameterizedResource");
    model.setClassName("fr.cnes.sitools.resources.basic.BasicParameterizedResourceModel");
    model.setDescriptionAction("testAction");
    model.setBehavior(ResourceBehaviorType.DISPLAY_IN_DESKTOP);
    return model;
  }

  /**
   * Go to plugin attached
   */
  private void getPluginResponse() {
    ClientResource cr = new ClientResource(getProjectAttachmentUrl() + PLUGIN_ATTACH);
    Representation result = cr.get(getMediaTest());
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    assertTrue(result.getMediaType().equals(MediaType.TEXT_PLAIN));
    RIAPUtils.exhaust(result);
  }

  /**
   * Go to plugin attached
   */
  private void getPluginResponseError503() {
    String url = getProjectAttachmentUrl() + PLUGIN_ATTACH;
    final Client client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, url);
    org.restlet.Response response = client.handle(request);
    try {
      assertNotNull(response);
      assertTrue(response.getStatus().isError());
      assertEquals(Status.SERVER_ERROR_SERVICE_UNAVAILABLE, response.getStatus());
    }
    finally {
      RIAPUtils.exhaust(response);
    }
  }

  /**
   * Modify plugin instance
   */
  private void modifyPlugin() {
    // Get the list of resources attached to the application
    ClientResource cr = new ClientResource(getProjectResourcesUrl());
    Representation pluginRep = cr.get(getMediaTest());
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(pluginRep);
    Response responsePlugin = getResponse(getMediaTest(), pluginRep, ResourceModelDTO.class, true);
    assertTrue(responsePlugin.getSuccess());
    assertTrue(responsePlugin.getTotal() == 1);
    // Get a single plugin
    String pluginId = resourceDto.getId();
    cr = new ClientResource(getProjectResourcesUrl() + "/" + pluginId);
    pluginRep = cr.get(getMediaTest());
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(pluginRep);
    Response response = getResponse(getMediaTest(), pluginRep, ResourceModelDTO.class);
    assertTrue(response.getSuccess());
    // Modify and update
    getParameterByName("url", resourceDto).setValue(PLUGIN_ATTACH_MODIFIED);
    resourceDto.setParent(PROJECT_NAME);
    Representation repr = getRepresentation(resourceDto, getMediaTest());
    ClientResource clr = new ClientResource(getProjectResourcesUrl());
    Representation result = clr.put(repr, getMediaTest());
    assertTrue(clr.getStatus().isSuccess());
    assertNotNull(result);
    // Test that modified plugin is responding
    cr = new ClientResource(getProjectAttachmentUrl() + PLUGIN_ATTACH_MODIFIED);
    result = cr.get(getMediaTest());
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    assertTrue(result.getMediaType().equals(MediaType.TEXT_PLAIN));
    RIAPUtils.exhaust(pluginRep);
    RIAPUtils.exhaust(result);
  }

  /**
   * Delete plugin instance
   */
  private void deletePlugin() {
    // Delete the plugin
    String pluginId = resourceDto.getId();
    ClientResource cr = new ClientResource(getProjectBaseUrl() + settings.getString(Consts.APP_RESOURCES_URL) + "/"
        + pluginId);
    Representation pluginRep = cr.delete(getMediaTest());
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(pluginRep);
    // Check that there's nobody now ...
    cr = new ClientResource(getProjectResourcesUrl());
    Representation rep = cr.get(getMediaTest());
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(pluginRep);
    Response responsePlugin = getResponse(getMediaTest(), pluginRep, ResourceModelDTO.class);
    assertTrue(responsePlugin.getSuccess());
    assertTrue(responsePlugin.getTotal() == null);
    RIAPUtils.exhaust(pluginRep);
    RIAPUtils.exhaust(rep);
  }

  /**
   * Create a ResourceModelDTO with the given Id
   * 
   * @param id
   *          the id of the ResourceModelDTO
   * @return the ResourceModelDTO
   */
  private ResourceModelDTO createObjectForValidation(String id) {
    ResourceModelDTO resModel = new ResourceModelDTO();

    resModel.setClassName(classname);
    resModel.setName("TestResourcePlugin");
    resModel.setResourceClassName("fr.cnes.sitools.resources.test.TestParameterizedResource");

    resModel.setClassAuthor("AKKA/CNES");
    resModel.setClassVersion("1.0");
    resModel.setDescription("FOR TEST PURPOSE ONLY, DON'T USE IT, IT DOESN'T DO ANYTHING");
    resModel.setId(id);
    resModel.setParent(PROJECT_NAME);

    // Attention ordre des params important pour les tests.

    ResourceParameter param1 = new ResourceParameter("1", "1", ResourceParameterType.PARAMETER_INTERN);
    param1.setValue("param1_value");

    ResourceParameter param2 = new ResourceParameter("2", "2", ResourceParameterType.PARAMETER_INTERN);
    param2.setValue("param2_value");

    ResourceParameter attach = new ResourceParameter("url", "url", ResourceParameterType.PARAMETER_ATTACHMENT);
    attach.setValue(PLUGIN_ATTACH);

    resModel.getParameters().add(param1);
    resModel.getParameters().add(param2);

    resModel.getParameters().add(attach);

    resourceDto = resModel;
    return resModel;
  }

  /**
   * Add an ResourceModelDTO
   * 
   * @param item
   *          ResourceModelDTO
   * 
   * 
   */
  public void create(ResourceModelDTO item) {
    Representation appRep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getProjectBaseUrl() + settings.getString(Consts.APP_RESOURCES_URL));
    Representation result = cr.post(appRep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    RIAPUtils.exhaust(result);
  }

  /**
   * Add an ResourceModelDTO
   * 
   * @param item
   *          ResourceModelDTO
   * 
   */
  public void update(ResourceModelDTO item) {
    Representation appRep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getProjectBaseUrl() + settings.getString(Consts.APP_RESOURCES_URL) + "/"
        + item.getId());
    Representation result = cr.put(appRep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    RIAPUtils.exhaust(result);
  }

  /**
   * Add an ResourceModelDTO and assert that the validation process has failed
   * 
   * @param item
   *          ApplicationPluginModel
   * @param nbViolations
   *          the number of violations to assert
   * 
   */
  public void createWithValidation(ResourceModelDTO item, int nbViolations) {
    Representation appRep = getRepresentation(item, getMediaTest());
    ClientResource cr = new ClientResource(getProjectBaseUrl() + settings.getString(Consts.APP_RESOURCES_URL));
    Representation result = cr.post(appRep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, ConstraintViolation.class, true);
    assertFalse(response.getSuccess());
    ArrayList<Object> list = response.getData();
    assertEquals(nbViolations, list.size());
  }

  /**
   * Update an ResourceModelDTO and assert that the validation process has failed
   * 
   * @param model
   *          ResourceModelDTO
   * @param nbViolations
   *          the number of violations to assert
   * 
   */
  public void updateWithValidation(ResourceModelDTO model, int nbViolations) {
    Representation appRep = getRepresentation(model, getMediaTest());

    ClientResource cr = new ClientResource(getProjectResourcesUrl() + "/" + model.getId());
    Representation result = cr.put(appRep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, ConstraintViolation.class, true);
    assertFalse(response.getSuccess());
    ArrayList<Object> list = response.getData();
    assertEquals(nbViolations, list.size());

  }

  /**
   * Return the parameter by name
   * 
   * @param name
   *          the parameter name
   * @param dto
   *          the {@link ResourceModelDTO}
   * @return the corresponding parameter
   */
  public final ResourceParameter getParameterByName(String name, ResourceModelDTO dto) {
    ResourceParameter param = null;
    for (ResourceParameter par : dto.getParameters()) {
      if (par.getName() != null && par.getName().equals(name)) {
        param = par;
      }
    }
    return param;
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
    return GetResponseUtils.getResponseResource(media, representation, dataClass, isArray);
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
  public static Representation getRepresentation(ResourceModelDTO item, MediaType media) {
    return GetRepresentationUtils.getRepresentationResource(item, media);
  }

}
