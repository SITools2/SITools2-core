package fr.cnes.sitools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.dataset.services.model.ServiceCollectionModel;
import fr.cnes.sitools.dataset.services.model.ServiceModel;
import fr.cnes.sitools.plugins.guiservices.implement.model.GuiServicePluginModel;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.tasks.AbstractTaskResourceTestCase;
import fr.cnes.sitools.tasks.model.TaskResourceModel;
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

    File dirStoreServices = new File(settings.getStoreDIR(Consts.APP_SERVICES_STORE_DIR));
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
    ResourceModel resourceModel = null;
    GuiServicePluginModel guiService = null;
    try {
      createDataset(datasetId, urlAttachDataset);
      assertNoneServices();
      resourceModel = createServiceResource();
      assertServiceResource(1);
      // guiService = createServiceIHM();
      assertServicesCount(1);
      deleteResourceModel(resourceModel, getServiceUrl(datasetId));
      assertServicesCount(0);
      if (guiService != null) {
        deleteGuiService(guiService);
      }
    }
    finally {
      deleteDataset(datasetId);
    }
  }

  @Test
  public void testServerServiceCRUD() throws InterruptedException, ClassNotFoundException, InstantiationException,
      IllegalAccessException {
    ResourceModel resourceModel = null;
    try {
      createDataset(datasetId, urlAttachDataset);
      assertServiceResource(0);
      resourceModel = createServiceResource();
      assertServiceResource(1);
      resourceModel.setDescription("description changed");
      resourceModel.setParent(datasetId);
      updateServiceResource(resourceModel, getServiceUrl(datasetId));
      // guiService = createServiceIHM();
      deleteResourceModel(resourceModel, getServiceUrl(datasetId));
      assertServiceResource(0);
    }
    finally {
      deleteDataset(datasetId);
    }
  }

  private void assertServiceResource(int expected) {
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
      Response response = GetResponseUtils.getResponseResource(getMediaTest(), result, ResourceModel.class, true);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(expected), response.getTotal());
      if (expected == 0) {
        assertNull(response.getData());
      }
      else {
        assertEquals(expected, response.getData().size());
      }
      RIAPUtils.exhaust(result);
    }

  }

  private void assertNoneServices() {
    assertServicesCount(0);
  }

  private void assertServicesCount(int expectedServicesCount) {
    String url = getBaseDatasetUrl().replaceAll("\\{parentId\\}", datasetId)
        + settings.getString(Consts.APP_SERVICES_URL);
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
        assertNull(services.getServices());
      }
      else {
        assertNotNull(services.getServices());
        assertEquals(expectedServicesCount, services.getServices().size());
      }
      RIAPUtils.exhaust(result);
    }
  }

  private ResourceModel createServiceResource() throws ClassNotFoundException, InstantiationException,
      IllegalAccessException {
    ResourceModel htmlResource = AbstractTaskResourceTestCase.createResourceModel(resourceModelClass,
        "test_html_resource", urlAttachResource);
    htmlResource.setName("HTML Resource TEST");
    htmlResource.setDescription("HTML Resource description");
    htmlResource.getParameterByName("title").setValue("HTML title");
    persistResourceModel(htmlResource, getServiceUrl(datasetId));
    return htmlResource;
  }

  private GuiServicePluginModel createServiceIHM() {
    GuiServicePluginModel service = AbstractGuiServiceImplementTestCase.createObject("bbbbb");
    persistGuiServicePlugin(service);
    return service;
  }

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

  private void persistGuiServicePlugin(GuiServicePluginModel guiServiceIn) {
    Representation rep = GetRepresentationUtils.getRepresentationGuiServicePlugin(guiServiceIn, getMediaTest());
    String url = getServiceUrl(datasetId);
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
      GuiServicePluginModel guiServiceOut = (GuiServicePluginModel) response.getItem();
      assertEquals(guiServiceIn.getId(), guiServiceOut.getId());
      assertEquals(guiServiceIn.getName(), guiServiceOut.getName());
      assertEquals(guiServiceIn.getDescription(), guiServiceOut.getDescription());

      RIAPUtils.exhaust(result);
    }
  }

  private String getServiceUrl(String parentId) {
    return getBaseDatasetUrl().replaceAll("\\{parentId\\}", parentId) + settings.getString(Consts.APP_SERVICES_URL);
  }

  private void deleteGuiService(GuiServicePluginModel guiService) {
    String url = getServiceUrl(datasetId) + "/" + guiService.getId();
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
  public void updateServiceResource(ResourceModel resourceModel, String baseUrl) {
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
  public void deleteResourceModel(ResourceModel resourceModel, String baseUrl) {
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
   * Get a ResourceModelDTO from a ResourceModel
   * 
   * @param resource
   *          the ResourceModel
   * @return a ResourceModelDTO
   */
  private ResourceModelDTO getResourceModelDTO(ResourceModel resource) {
    return ResourceModelDTO.resourceModelToDTO(resource);
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
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
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
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null; // TODO complete test with ObjectRepresentation
      }
    }
    finally {
      RIAPUtils.exhaust(representation);
    }
  }

}
