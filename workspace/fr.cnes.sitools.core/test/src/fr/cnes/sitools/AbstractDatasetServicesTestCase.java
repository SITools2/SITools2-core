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

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.services.model.ServiceCollectionModel;
import fr.cnes.sitools.dataset.services.model.ServiceEnum;
import fr.cnes.sitools.dataset.services.model.ServiceModel;
import fr.cnes.sitools.plugins.guiservices.declare.GuiServiceStoreInterface;
import fr.cnes.sitools.plugins.guiservices.declare.model.GuiServiceModel;
import fr.cnes.sitools.plugins.guiservices.implement.model.GuiServicePluginModel;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.tasks.AbstractTaskResourceTestCase;
import fr.cnes.sitools.util.RIAPUtils;
import fr.cnes.sitools.utils.CreateDatasetUtil;
import fr.cnes.sitools.utils.GetRepresentationUtils;
import fr.cnes.sitools.utils.GetResponseUtils;

/**
 * Class to test the services on a dataset. Which means testing the resource plugins and the gui services, their orders,
 * categories ...
 * 
 * @author m.gond
 */
public class AbstractDatasetServicesTestCase extends AbstractDataSetManagerTestCase {

  /** url attachment of the dataset */
  private String urlAttachDataset = "/dsTestServices";

  /** The dataset id */
  private String datasetId = "aaaaa";

  /**
   * The class name of the resourceModel
   */
  private String resourceModelClass = "fr.cnes.sitools.resources.html.HtmlResourceModel";

  /**
   * The url attachment for the resource model
   */
  private String urlAttachResource = "/html";

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  public final String getBaseDatasetUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_URL) + "/" + "{parentId}";
  }

  @Before
  @Override
  /**
   * Create component, store and application and start server
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    super.setUp();

    File dirStoreServices = new File(settings.getStoreDIR(Consts.APP_SERVICES_STORE_DIR) + "/map");
    cleanDirectory(dirStoreServices);

  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.AbstractSitoolsServerTestCase#tearDown()
   */
  @Override
  @After
  public void tearDown() throws Exception {
    super.tearDown();

  }

  /**
   * <p>
   * Test:
   * <ul>
   * <li>Assert no service on the dataset</li>
   * <li>Add a resourceModel</li>
   * <li>Add a guiService</li>
   * <li>retrieve list of services</li>
   * <li>Change services order</li>
   * <li>remove a service</li>
   * <li>retrieve list of services</li>
   * <li>remove other service</li>
   * <li>assert no service on the dataset</li>
   * <ul>
   * <p>
   * 
   * @throws InterruptedException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws ClassNotFoundException
   */
  @Test
  public void testDatasetServices() throws InterruptedException, ClassNotFoundException, InstantiationException,
      IllegalAccessException {
    ResourceModel serverService = null;
    GuiServiceModel guiService = null;
    try {
      createDataset(datasetId, urlAttachDataset);
      assertNoneServices();
      serverService = createServerService(true);
      String serviceUrl = getServiceUrl(datasetId);
      persistResourceModel(serverService, serviceUrl);
      assertServerService(1);
      guiService = createGuiService();
      assertServicesCount(2);
      // assert services order on the datasetApplication (expose services for user)
      assertServicesOrderForUser(serverService.getId(), guiService.getId());
      assertServicesOrderForAdmin(serverService.getId(), guiService.getId());

      assertServicesOrder(serverService.getId(), guiService.getId(), serviceUrl);
      ServiceCollectionModel services = createCollectionToChangeOrder(serverService, guiService);
      persistServicesOrder(services);
      assertServicesOrderForAdmin(guiService.getId(), serverService.getId());
      // assert services order on the datasetApplication (expose services for user)
      assertServicesOrderForUser(guiService.getId(), serverService.getId());

      deleteGuiService(guiService, serviceUrl);
      assertServicesCount(1);
      deleteServerService(serverService, serviceUrl);
      assertServicesCount(0);
    }
    finally {
      deleteDataset(datasetId);
    }
  }

  @Test
  public void testServerServiceCRUD() throws InterruptedException, ClassNotFoundException, InstantiationException,
      IllegalAccessException {
    ResourceModel serverService = null;
    try {
      createDataset(datasetId, urlAttachDataset);
      assertServerService(0);
      serverService = createServerService(true);
      persistResourceModel(serverService, getServiceUrl(datasetId));
      assertServerService(1);
      retrieveServerServiceForUser(serverService.getId());
      serverService.setDescription("description changed");
      serverService.setParent(datasetId);
      updateServerService(serverService, getServiceUrl(datasetId));
      deleteServerService(serverService, getServiceUrl(datasetId));
      assertServerService(0);
    }
    finally {
      deleteDataset(datasetId);
    }
  }

  @Test
  public void testServerServiceWithViolationsCRUD() throws InterruptedException, ClassNotFoundException,
      InstantiationException, IllegalAccessException {
    ResourceModel serverService = null;
    try {
      createDataset(datasetId, urlAttachDataset);
      assertServerService(0);
      serverService = createServerService(false);
      String serviceUrl = getServiceUrl(datasetId);
      persistServerServiceWithValidation(serverService, 1, serviceUrl);
      serverService.getParameterByName("title").setValue("HTML title");
      persistResourceModel(serverService, serviceUrl);
      assertServerService(1);

      serverService.getParameterByName("title").setValue("");
      updateServerServiceWithValidation(serverService, 1, serviceUrl);
      deleteServerService(serverService, getServiceUrl(datasetId));
      assertServerService(0);
    }
    finally {
      deleteDataset(datasetId);
    }
  }

  @Test
  public void testGuiServiceCRUD() throws InterruptedException, ClassNotFoundException, InstantiationException,
      IllegalAccessException {
    GuiServicePluginModel guiService = null;
    try {
      createDataset(datasetId, urlAttachDataset);
      assertGuiServiceForAdmin(0);
      assertGuiServiceForUser(0);
      guiService = createGuiService();
      assertGuiServiceForAdmin(1);
      assertGuiServiceForUser(1);
      retrieveGuiServiceForUser(guiService.getId());
      guiService.setDescription("New description");

      updateGuiService(guiService, getServiceUrl(datasetId));
      deleteGuiService(guiService, getServiceUrl(datasetId));

      assertGuiServiceForAdmin(0);
      assertGuiServiceForUser(0);
    }
    finally {
      deleteDataset(datasetId);
    }
  }

  @Test
  public void testDatasetServicesNotifications() throws InterruptedException, ClassNotFoundException,
      InstantiationException, IllegalAccessException {

    ResourceModel serverService = null;

    createDataset(datasetId, urlAttachDataset);
    assertServicesCount(0);
    serverService = createServerService(true);
    String serviceUrl = getServiceUrl(datasetId);
    persistResourceModel(serverService, serviceUrl);
    assertServerService(1);
    createGuiService();
    assertGuiServiceForAdmin(1);
    assertServicesCount(2);

    deleteDataset(datasetId);
    assertServerService(0);
    assertGuiServiceForAdmin(0);
    assertServicesCount(0);

  }

  @Test
  public void testDatasetDefaultGuiService() throws InterruptedException, ClassNotFoundException,
      InstantiationException, IllegalAccessException {

    // change the first GUI service on the store to create it by default when creating a dataset
    GuiServiceStoreInterface storeGuiService = (GuiServiceStoreInterface) settings.getStores().get(
        Consts.APP_STORE_GUI_SERVICE);
    GuiServiceModel guiServiceModel = storeGuiService.retrieve("8c48e76b-7ce4-4af1-8c4e-a120e47d5ed8");
    guiServiceModel.setDefaultGuiService(true);
    storeGuiService.update(guiServiceModel);
    try {
      createDataset(datasetId, urlAttachDataset);
      assertServicesCount(1);
      assertGuiServiceForAdmin(1);
    }
    finally {
      deleteDataset(datasetId);
    }

  }

  // -----------------------------------------------------
  // SERVICES METHODS

  private void assertNoneServices() {
    assertServicesCount(0);
  }

  private void assertServicesCount(int expectedServicesCount) {
    String url = getServiceUrl(datasetId);
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      retrieveDocAPI(url, "", parameters, String.format(getBaseUrl(), "%identifier%") + "/services");
    }
    else {
      ClientResource cr = new ClientResource(url);
      docAPI.appendRequest(Method.GET, cr);

      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponseCollectionServices(getMediaTest(), result, ServiceCollectionModel.class, false);

      assertTrue(response.getSuccess());
      ServiceCollectionModel services = (ServiceCollectionModel) response.getItem();

      if (expectedServicesCount == 0) {
        assertTrue(services.getServices() == null || services.getServices().size() == 0);
      }
      else {
        assertNotNull(services.getServices());
        assertEquals(expectedServicesCount, services.getServices().size());
      }
      RIAPUtils.exhaust(result);
    }
  }

  private String getServiceUrl(String parentId) {
    return getBaseDatasetUrl().replaceAll("\\{parentId\\}", parentId) + settings.getString(Consts.APP_SERVICES_URL);
  }

  private String getUserServiceUrl(String datasetUrl) {
    return getHostUrl() + datasetUrl + settings.getString(Consts.APP_SERVICES_URL);
  }

  private ServiceCollectionModel createCollectionToChangeOrder(ResourceModel resourceModel, GuiServiceModel guiService) {

    ServiceCollectionModel collection = new ServiceCollectionModel();
    List<ServiceModel> services = new ArrayList<ServiceModel>();

    ServiceModel service = new ServiceModel();
    service.setId(guiService.getId());
    service.setName(guiService.getName());
    service.setDescription(guiService.getDescription());
    service.setIcon(guiService.getIcon());
    service.setLabel(guiService.getLabel());
    service.setType(ServiceEnum.GUI);
    services.add(service);

    service = new ServiceModel();
    service.setId(resourceModel.getId());
    service.setName(resourceModel.getName());
    service.setDescription(resourceModel.getDescription());
    service.setType(ServiceEnum.SERVER);
    services.add(service);

    collection.setServices(services);
    collection.setId(datasetId);

    return collection;
  }

  private void assertServicesOrderForUser(String id, String id2) {
    String baseUrl = getUserServiceUrl(urlAttachDataset);
    assertServicesOrder(id, id2, baseUrl);
  }

  private void assertServicesOrderForAdmin(String id, String id2) {
    String baseUrl = getServiceUrl(datasetId);
    assertServicesOrder(id, id2, baseUrl);
  }

  private void assertServicesOrder(String id1, String id2, String baseUrl) {
    String url = baseUrl;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      retrieveDocAPI(url, "", parameters, String.format(getBaseUrl(), "%identifier%") + "/services");
    }
    else {
      ClientResource cr = new ClientResource(url);
      docAPI.appendRequest(Method.GET, cr);

      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponseCollectionServices(getMediaTest(), result, ServiceCollectionModel.class, false);

      assertTrue(response.getSuccess());
      ServiceCollectionModel services = (ServiceCollectionModel) response.getItem();

      assertNotNull(services.getServices());
      assertEquals(2, services.getServices().size());

      assertEquals(id1, services.getServices().get(0).getId());
      assertEquals(id2, services.getServices().get(1).getId());

      RIAPUtils.exhaust(result);
    }
  }

  private void persistServicesOrder(ServiceCollectionModel services) {
    Representation rep = getRepresentationCollectionServices(services, getMediaTest());
    String url = getServiceUrl(datasetId);
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("POST", "A <i>ServiceCollectionModel</i> object");
      postDocAPI(url, "", rep, parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponseCollectionServices(getMediaTest(), result, ServiceCollectionModel.class);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
      ServiceCollectionModel servicesOutput = (ServiceCollectionModel) response.getItem();
      assertEquals(datasetId, servicesOutput.getId());
      RIAPUtils.exhaust(result);
    }

  }

  // ----------------------------------------------------------------------
  // SERVER SERVICE METHODS

  private void assertServerService(int expected) {
    String url = getServiceUrl(datasetId) + "/server";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      retrieveDocAPI(url, "", parameters, String.format(getBaseUrl(), "%identifier%") + "/services");
    }
    else {
      ClientResource cr = new ClientResource(url);
      docAPI.appendRequest(Method.GET, cr);

      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = GetResponseUtils.getResponseResource(getMediaTest(), result, ResourceModelDTO.class, true);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(expected), response.getTotal());
      if (expected != 0) {
      }
      RIAPUtils.exhaust(result);
    }

  }

  private ResourceModel createServerService(boolean withTitle) throws ClassNotFoundException, InstantiationException,
      IllegalAccessException {
    ResourceModel htmlResource = AbstractTaskResourceTestCase.createResourceModel(resourceModelClass,
        "test_html_resource", urlAttachResource);
    htmlResource.setName("HTML Resource TEST");
    htmlResource.setDescription("HTML Resource description");
    if (withTitle) {
      htmlResource.getParameterByName("title").setValue("HTML title");
    }
    return htmlResource;
  }

  /**
   * Add a ResourceModel to the server
   * 
   * @param resourceModel
   *          ResourceModel
   * @param baseUrl
   *          the baseUrl of the application to create the Resource
   * 
   */
  public void persistResourceModel(ResourceModel resourceModel, String baseUrl) {

    ResourceModelDTO dto = getResourceModelDTO(resourceModel);

    Representation appRep = GetRepresentationUtils.getRepresentationResource(dto, getMediaTest());
    ClientResource cr = new ClientResource(baseUrl + "/server");
    Representation result = cr.post(appRep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    Response response = GetResponseUtils.getResponseResource(getMediaTest(), result, ResourceModelDTO.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    ResourceModelDTO resourceModelOut = (ResourceModelDTO) response.getItem();
    assertEquals(resourceModel.getId(), resourceModelOut.getId());

    RIAPUtils.exhaust(result);
  }

  /**
   * Edit a TaskResourceModel
   * 
   * @param resourceModel
   *          The TaskResourceModel
   * @param baseUrl
   *          the baseUrl of the application to update the Resource
   * 
   * 
   */
  public void updateServerService(ResourceModel resourceModel, String baseUrl) {
    ResourceModelDTO dto = getResourceModelDTO(resourceModel);

    Representation appRep = GetRepresentationUtils.getRepresentationResource(dto, getMediaTest());
    ClientResource cr = new ClientResource(baseUrl + "/server/" + resourceModel.getId());
    Representation result = cr.put(appRep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    Response response = GetResponseUtils.getResponseResource(getMediaTest(), result, ResourceModelDTO.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    ResourceModelDTO resourceModelOut = (ResourceModelDTO) response.getItem();
    assertEquals(resourceModel.getId(), resourceModelOut.getId());
    assertEquals(resourceModel.getDescription(), resourceModelOut.getDescription());
    RIAPUtils.exhaust(result);
  }

  // delete
  /**
   * Delete a TaskResourceModel
   * 
   * @param resourceModel
   *          The TaskResourceModel
   * @param baseUrl
   *          the baseUrl of the application to update the Resource
   * 
   * 
   */
  public void deleteServerService(ResourceModel resourceModel, String baseUrl) {
    ClientResource cr = new ClientResource(baseUrl + "/server/" + resourceModel.getId());
    Representation result = cr.delete(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    Response response = getResponse(getMediaTest(), result, ResourceModel.class);
    assertTrue(response.getSuccess());
    RIAPUtils.exhaust(result);
  }

  /**
   * Add an ResourceModel and assert that the validation process has failed
   * 
   * @param item
   *          ResourceModel
   * @param nbViolations
   *          the number of violations to assert
   * @param baseUrl
   *          the base url of the services service
   * 
   */
  public void persistServerServiceWithValidation(ResourceModel item, int nbViolations, String baseUrl) {
    ResourceModelDTO dto = getResourceModelDTO(item);
    Representation appRep = GetRepresentationUtils.getRepresentationResource(dto, getMediaTest());
    ClientResource cr = new ClientResource(baseUrl + "/server");
    Representation result = cr.post(appRep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = GetResponseUtils.getResponseResource(getMediaTest(), result, ConstraintViolation.class, true);
    assertFalse(response.getSuccess());
    ArrayList<Object> list = response.getData();
    assertEquals(nbViolations, list.size());
  }

  /**
   * Update an ResourceModelDTO and assert that the validation process has failed
   * 
   * @param item
   *          ResourceModel
   * @param nbViolations
   *          the number of violations to assert
   * @param baseUrl
   *          the base url of the services service
   * 
   */
  public void updateServerServiceWithValidation(ResourceModel item, int nbViolations, String baseUrl) {
    ResourceModelDTO dto = getResourceModelDTO(item);
    Representation appRep = GetRepresentationUtils.getRepresentationResource(dto, getMediaTest());

    ClientResource cr = new ClientResource(baseUrl + "/server/" + item.getId());
    Representation result = cr.put(appRep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = GetResponseUtils.getResponseResource(getMediaTest(), result, ConstraintViolation.class, true);
    assertFalse(response.getSuccess());
    ArrayList<Object> list = response.getData();
    assertEquals(nbViolations, list.size());

  }

  private void retrieveServerServiceForUser(String id) {
    String baseUrl = getHostUrl() + urlAttachDataset;
    ClientResource cr = new ClientResource(baseUrl + "/services/server/" + id);
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = GetResponseUtils.getResponseResource(getMediaTest(), result, ResourceModelDTO.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
  }

  /**
   * Get a ResourceModelDTO from a ResourceModel
   * 
   * @param resource
   *          the ResourceModel
   * @return a ResourceModelDTO
   */
  private ResourceModelDTO getResourceModelDTO(ResourceModel resource) {
    return ResourceModelDTO.resourceModelToDTO(resource);
  }

  // ----------------------------------------------------------
  // GUI SERVICE METHODS

  private void assertGuiServiceForUser(int expected) {
    String baseUrl = getUserServiceUrl(urlAttachDataset);
    assertGuiService(expected, baseUrl);
  }

  private void assertGuiServiceForAdmin(int expected) {
    String baseUrl = getServiceUrl(datasetId);
    assertGuiService(expected, baseUrl);
  }

  /**
   * Assert that the number of gui services for a particular dataset is expected
   * 
   * @param expected
   *          the number of gui services expected
   */
  private void assertGuiService(int expected, String baseUrl) {
    String url = baseUrl + "/gui";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("identifier", "dataset identifier");
      retrieveDocAPI(url, "", parameters, String.format(getBaseUrl(), "%identifier%") + "/services/gui");
    }
    else {
      ClientResource cr = new ClientResource(url);
      docAPI.appendRequest(Method.GET, cr);

      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = GetResponseUtils.getResponseGuiServicePlugin(getMediaTest(), result,
          GuiServicePluginModel.class, true);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(expected), response.getTotal());
      if (expected == 0) {
        assertTrue(response.getData() == null || response.getData().size() == 0);
      }
      else {
        assertEquals(expected, response.getData().size());
      }
      RIAPUtils.exhaust(result);
    }

  }

  private GuiServicePluginModel createGuiService() {
    GuiServicePluginModel service = AbstractGuiServiceImplementTestCase.createObject("bbbbb");
    persistGuiServicePlugin(service, getServiceUrl(datasetId));
    return service;
  }

  private void persistGuiServicePlugin(GuiServicePluginModel guiServiceIn, String baseUrl) {
    Representation rep = GetRepresentationUtils.getRepresentationGuiServicePlugin(guiServiceIn, getMediaTest());
    String url = baseUrl + "/gui";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("POST", "A <i>GuiService</i> object");
      postDocAPI(url, "", rep, parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.post(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = GetResponseUtils.getResponseGuiServicePlugin(getMediaTest(), result,
          GuiServicePluginModel.class);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
      GuiServiceModel guiServiceOut = (GuiServiceModel) response.getItem();
      assertEquals(guiServiceIn.getId(), guiServiceOut.getId());
      assertEquals(guiServiceIn.getName(), guiServiceOut.getName());
      assertEquals(guiServiceIn.getDescription(), guiServiceOut.getDescription());

      RIAPUtils.exhaust(result);
    }
  }

  private void updateGuiService(GuiServicePluginModel guiServiceIn, String baseUrl) {
    Representation rep = GetRepresentationUtils.getRepresentationGuiServicePlugin(guiServiceIn, getMediaTest());
    String url = baseUrl + "/gui/" + guiServiceIn.getId();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("POST", "A <i>GuiService</i> object");
      postDocAPI(url, "", rep, parameters, url);
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.put(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = GetResponseUtils.getResponseGuiServicePlugin(getMediaTest(), result,
          GuiServicePluginModel.class);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
      GuiServiceModel guiServiceOut = (GuiServiceModel) response.getItem();
      assertEquals(guiServiceIn.getId(), guiServiceOut.getId());
      assertEquals(guiServiceIn.getName(), guiServiceOut.getName());
      assertEquals(guiServiceIn.getDescription(), guiServiceOut.getDescription());

      RIAPUtils.exhaust(result);
    }
  }

  private void deleteGuiService(GuiServiceModel guiService, String baseUrl) {
    String url = baseUrl + "/gui/" + guiService.getId();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("guiServiceId", "GuiService identifier");
      deleteDocAPI(url, "", parameters, getServiceUrl("{datasetId}") + "/%guiServiceId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      Representation result = cr.delete(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, GuiServicePluginModel.class);
      assertTrue(response.getSuccess());
      RIAPUtils.exhaust(result);
    }
  }

  private void retrieveGuiServiceForUser(String id) {
    String baseUrl = getHostUrl() + urlAttachDataset;
    ClientResource cr = new ClientResource(baseUrl + "/services/gui/" + id);
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = GetResponseUtils.getResponseGuiServicePlugin(getMediaTest(), result,
        GuiServicePluginModel.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
  }

  // ------------------------------------------------------------
  // OTHER METHODS
  /**
   * Create and activate a Dataset for Postgresql datasource. This dataset is created on the test.table_tests table
   * 
   * @param id
   *          the id of the dataset
   * @param urlAttachment
   *          the url attachment
   * @return the DataSet created
   * @throws InterruptedException
   *           if something is wrong
   */
  private DataSet createDataset(String id, String urlAttachment) throws InterruptedException {
    DataSet item = CreateDatasetUtil.createDatasetFusePG(id, urlAttachment);
    persistDataset(item);
    item.setDirty(false);
    changeStatus(item.getId(), "/start");
    return item;
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
  public static Response getResponseCollectionServices(MediaType media, Representation representation,
      Class<?> dataClass) {
    return getResponseCollectionServices(media, representation, dataClass, false);
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
  public static Response getResponseCollectionServices(MediaType media, Representation representation,
      Class<?> dataClass, boolean isArray) {
    try {
      if (!media.isCompatible(getMediaTest()) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.alias("response", Response.class);
      xstream.alias("ServiceCollectionModel", ServiceCollectionModel.class);
      xstream.alias("ServiceModel", ServiceModel.class);

      if (media.equals(MediaType.APPLICATION_JSON)) {
        xstream.addImplicitCollection(ServiceCollectionModel.class, "services", ServiceModel.class);
      }

      if (isArray) {
        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
        xstream.aliasField("ServiceCollectionModel", Response.class, "item");
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
  public static Representation getRepresentationCollectionServices(ServiceCollectionModel item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<ServiceCollectionModel>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<ServiceCollectionModel> rep = new XstreamRepresentation<ServiceCollectionModel>(media, item);
      configureCollectionServices(xstream);
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
  private static void configureCollectionServices(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);

    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");
  }

}
