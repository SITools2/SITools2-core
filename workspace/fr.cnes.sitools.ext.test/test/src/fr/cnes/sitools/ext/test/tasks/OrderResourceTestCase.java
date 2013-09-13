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
package fr.cnes.sitools.ext.test.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.ReferenceList;
import org.restlet.engine.util.DateUtils;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.AbstractSitoolsTestCase;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.Event;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.order.model.Order;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.tasks.model.TaskModel;
import fr.cnes.sitools.userstorage.model.DiskStorage;
import fr.cnes.sitools.userstorage.model.UserStorage;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * 
 * OrderResourceTestCase
 * 
 * @author m.gond
 */
public class OrderResourceTestCase extends AbstractTaskResourceTestCase {

  /**
   * The if of the dataset
   */
  private static final String DATASET_ID = "e6892ea4-680a-47d5-af82-c72908dea319";
  /** The url of the dataset */
  private static final String DATASET_URL = "/testmysql";

  /** The class name of the resourceModel */
  private String orderResourceModelClassName = "fr.cnes.sitools.resources.order.OrderResourceModel";

  /** The columnAlias for the colUrl parameter */
  private String colUrlDataset = "data_url";

  /** The url attachment for the resource model */
  private String urlAttach = "/order";
  /** user login */
  private String userLogin = "admin";
  /** user password */
  private String password = "admin";

  /** The urlFilePath */
  private String urlFilePath = "/tmp/SvaRecordDefinitionFile.json";
  /** The content of the file used for Resource */
  private String urlFileContent = "{\"orderRecord\":{\"records\":[{\"prop_id\":\"A001\"},{\"prop_id\":\"A002\"},{\"prop_id\":\"A003\"}]}}";
  /** The fileName template to use */
  private String fileNameTemplate = "dataset_order_${date:" + DateUtils.FORMAT_RFC_3339.get(0).replace(':', '_') + "}";

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  public String getBaseDatasetUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_DATASETS_URL) + "/" + DATASET_ID;
  }

  /**
   * absolute URL for admin view of orders
   * 
   * @return url
   */
  protected String getOrderUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_ORDERS_ADMIN_URL);
  }

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  protected String getUserStorageUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_USERSTORAGE_URL);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.SitoolsServerTestCase#setUp()
   */
  @Override
  public void setUp() throws Exception {
    super.setUp();

    File dirStoreTasks = new File(settings.getStoreDIR(Consts.APP_TASK_STORE_DIR));
    cleanDirectory(dirStoreTasks);

    File dirStoreOrder = new File(settings.getStoreDIR(Consts.APP_ORDERS_STORE_DIR));
    cleanDirectory(dirStoreOrder);

    File dirStoreResPlugin = new File(settings.getStoreDIR(Consts.APP_PLUGINS_RESOURCES_STORE_DIR));
    cleanDirectory(dirStoreResPlugin);
  }

  /**
   * Test the Order Resource
   * 
   * @throws ClassNotFoundException
   *           if the class cannot be found
   * @throws InstantiationException
   *           if there is an error while instantiating the resource
   * @throws IllegalAccessException
   *           if there is an error while instantiating the resource
   * @throws IOException
   *           if the response cannot be read
   * @throws InterruptedException
   *           if there is an error while waiting for the order to finsh
   */
  @Test
  public void testOrderResource() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
    IOException, InterruptedException {

    // FIXME faire en XML aussi
    setMediaTest(MediaType.APPLICATION_JSON);
    createUserStorage(userLogin);
    assertNoneTasks(userLogin, password);
    assertNoneOrder();
    ResourceModel taskResource = createResourceModel(orderResourceModelClassName, "1000", urlAttach);
    taskResource = fillOrderResourceParameters(taskResource, false);
    create(taskResource, getBaseDatasetUrl());

    getServicesDescription(getHostUrl() + DATASET_URL);

    TaskModel taskModel = invokeOrder(urlAttach, "?ranges=[[0,0]]&runType=async");

    taskModel = testGetAsyncTaskModel(taskModel, userLogin, password, false);

    assertStatusSuccess(taskModel);

    // get the order url
    String url = getHostUrl() + taskModel.getUrlResult();
    Order order = getOrder(url);
    // assert if the order is done
    assertOrderDone(order);
    //
    assertFileOrderedExists(order, 1);
    deleteTask(userLogin, taskModel.getId(), password, false);
    assertNoneTasks(userLogin, password);
    deleteOrder(order);
    assertNoneOrder();

    createWadl(getHostUrl() + DATASET_URL, "dataset_application_with_orderresource");
    // queryResourceHTML();
    delete(taskResource, getBaseDatasetUrl());

  }

  /**
   * Test the Order resource with the resource parameters given as a file in the userstorage
   * 
   * @throws ClassNotFoundException
   *           if the class cannot be found
   * @throws InstantiationException
   *           if there is an error while instantiating the resource
   * @throws IllegalAccessException
   *           if there is an error while instantiating the resource
   * @throws IOException
   *           if the response cannot be read
   * @throws InterruptedException
   *           if there is an error while waiting for the order to finsh
   */
  @Test
  public void testOrderResourceWithFile() throws ClassNotFoundException, InstantiationException,
    IllegalAccessException, IOException, InterruptedException {

    // FIXME faire en XML aussi
    setMediaTest(MediaType.APPLICATION_JSON);
    createUserStorage(userLogin);
    assertNoneTasks(userLogin, password);
    assertNoneOrder();
    ResourceModel taskResource = createResourceModel(orderResourceModelClassName, "1000", urlAttach);
    taskResource = fillOrderResourceParameters(taskResource, false);
    taskResource.getParameterByName("too_many_selected_threshold").setValue("5");

    create(taskResource, getBaseDatasetUrl());

    postJSON();

    TaskModel taskModel = invokeOrderWithFile(urlAttach);

    taskModel = testGetAsyncTaskModel(taskModel, userLogin, password, false);

    assertStatusSuccess(taskModel);

    // get the order url
    String url = getHostUrl() + taskModel.getUrlResult();
    Order order = getOrder(url);
    // assert if the order is done
    assertOrderDone(order);
    //
    assertFileOrderedExists(order, 3);
    deleteTask(userLogin, taskModel.getId(), password, false);
    assertNoneTasks(userLogin, password);
    deleteOrder(order);
    assertNoneOrder();

    // queryResourceHTML();
    delete(taskResource, getBaseDatasetUrl());
  }

  /**
   * Test the resource order with a ZIP parameter
   * 
   * @throws ClassNotFoundException
   *           if the class cannot be found
   * @throws InstantiationException
   *           if there is an error while instantiating the resource
   * @throws IllegalAccessException
   *           if there is an error while instantiating the resource
   * @throws IOException
   *           if the response cannot be read
   * @throws InterruptedException
   *           if there is an error while waiting for the order to finsh
   */
  @Test
  public void testOrderResourceZip() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
    IOException, InterruptedException {
    setMediaTest(MediaType.APPLICATION_JSON);

    createUserStorage(userLogin);
    assertNoneTasks(userLogin, password);
    // assertNone order
    assertNoneOrder();
    // FIXME faire en XML aussi
    setMediaTest(MediaType.APPLICATION_JSON);
    createUserStorage(userLogin);
    assertNoneTasks(userLogin, password);
    assertNoneOrder();
    ResourceModel taskResource = createResourceModel(orderResourceModelClassName, "1000", urlAttach);
    taskResource = fillOrderResourceParameters(taskResource, true);

    create(taskResource, getBaseDatasetUrl());

    TaskModel taskModel = invokeOrder(urlAttach, "?ranges=[[0,0]]&runType=async");

    taskModel = testGetAsyncTaskModel(taskModel, userLogin, password, false);

    assertStatusSuccess(taskModel);
    // get the order url
    String url = getHostUrl() + taskModel.getUrlResult();
    Order order = getOrder(url);
    // assert if the order is done
    assertOrderDone(order);
    //
    assertFileOrderedExistsForZip(order);
    deleteTask(userLogin, taskModel.getId(), password, false);
    assertNoneTasks(userLogin, password);
    deleteOrder(order);
    assertNoneOrder();
  }

  /**
   * Test the HTML Resource
   * 
   * @throws ClassNotFoundException
   *           if the class cannot be found
   * @throws InstantiationException
   *           if there is an error while instantiating the resource
   * @throws IllegalAccessException
   *           if there is an error while instantiating the resource
   * @throws IOException
   *           if the response cannot be read
   * @throws InterruptedException
   *           if there is an error while waiting for the order to finsh
   */
  @Test
  public void testOrderResourceWithRange() throws ClassNotFoundException, InstantiationException,
    IllegalAccessException, IOException, InterruptedException {

    // FIXME faire en XML aussi
    setMediaTest(MediaType.APPLICATION_JSON);
    createUserStorage(userLogin);
    assertNoneTasks(userLogin, password);
    assertNoneOrder();
    ResourceModel taskResource = createResourceModel(orderResourceModelClassName, "1000", urlAttach);
    taskResource = fillOrderResourceParameters(taskResource, false);
    taskResource.getParameterByName("too_many_selected_threshold").setValue("10");

    create(taskResource, getBaseDatasetUrl());

    TaskModel taskModel = invokeOrder(urlAttach, "?ranges=[[2,3],[1,1],[5,5]]&runType=async");

    taskModel = testGetAsyncTaskModel(taskModel, userLogin, password, false);

    assertStatusSuccess(taskModel);

    // get the order url
    String url = getHostUrl() + taskModel.getUrlResult();
    Order order = getOrder(url);
    // assert if the order is done
    assertOrderDone(order);
    //
    assertFileOrderedExists(order, 4);
    deleteTask(userLogin, taskModel.getId(), password, false);
    assertNoneTasks(userLogin, password);
    deleteOrder(order);
    assertNoneOrder();

    // queryResourceHTML();
    delete(taskResource, getBaseDatasetUrl());
  }

  /**
   * Test the HTML Resource
   * 
   * @throws ClassNotFoundException
   *           if the class cannot be found
   * @throws InstantiationException
   *           if there is an error while instantiating the resource
   * @throws IllegalAccessException
   *           if there is an error while instantiating the resource
   * @throws IOException
   *           if the response cannot be read
   * @throws InterruptedException
   *           if there is an error while waiting for the order to finsh
   */
  @Test
  public void testOrderResourceWithDownloadLimit() throws ClassNotFoundException, InstantiationException,
    IllegalAccessException, IOException, InterruptedException {

    setMediaTest(MediaType.APPLICATION_JSON);
    assertNoneTasks(userLogin, password);
    ResourceModel taskResource = createResourceModel(orderResourceModelClassName, "1000", urlAttach);
    taskResource = fillOrderResourceParameters(taskResource, false);
    taskResource.getParameterByName("too_many_selected_threshold").setValue("5");

    create(taskResource, getBaseDatasetUrl());

    TaskModel taskModel = invokeOrder(urlAttach, "?ranges=[[1,10]]&runType=async");

    taskModel = testGetAsyncTaskModel(taskModel, userLogin, password, false);

    assertStatusError(taskModel);

    deleteTask(userLogin, taskModel.getId(), password, false);
    assertNoneTasks(userLogin, password);

    // queryResourceHTML();
    delete(taskResource, getBaseDatasetUrl());
  }

  /**
   * Test the HTML Resource
   * 
   * @throws ClassNotFoundException
   *           if the class cannot be found
   * @throws InstantiationException
   *           if there is an error while instantiating the resource
   * @throws IllegalAccessException
   *           if there is an error while instantiating the resource
   * @throws IOException
   *           if the response cannot be read
   * @throws InterruptedException
   *           if there is an error while waiting for the order to finsh
   */
  @Test
  public void testOrderResourceWithDownloadLimitNoLimitRequest() throws ClassNotFoundException, InstantiationException,
    IllegalAccessException, IOException, InterruptedException {

    setMediaTest(MediaType.APPLICATION_JSON);
    assertNoneTasks(userLogin, password);
    ResourceModel taskResource = createResourceModel(orderResourceModelClassName, "1000", urlAttach);
    taskResource = fillOrderResourceParameters(taskResource, false);
    taskResource.getParameterByName("too_many_selected_threshold").setValue("5");

    create(taskResource, getBaseDatasetUrl());

    TaskModel taskModel = invokeOrder(urlAttach, "?runType=async");

    taskModel = testGetAsyncTaskModel(taskModel, userLogin, password, false);

    assertStatusError(taskModel);

    deleteTask(userLogin, taskModel.getId(), password, false);
    assertNoneTasks(userLogin, password);

    // queryResourceHTML();
    delete(taskResource, getBaseDatasetUrl());
  }

  /**
   * Fill the specific parameters for an OrderResource
   * 
   * @param taskResource
   *          the ResourceModel to fill
   * @param zip
   *          if the order needs to be zipped or not
   * @return the taskResource completed
   */
  protected ResourceModel fillOrderResourceParameters(ResourceModel taskResource, boolean zip) {

    ResourceParameter paramColUrl = taskResource.getParameterByName("colUrl");
    paramColUrl.setValue(colUrlDataset);

    ResourceParameter paramZip = taskResource.getParameterByName("zip");
    paramZip.setValue(String.valueOf(zip));

    ResourceParameter paramFileName = taskResource.getParameterByName("fileName");
    paramFileName.setValue(fileNameTemplate);

    taskResource.getParameterByName("too_many_selected_threshold").setValue("-1");

    return taskResource;
  }

  /**
   * Invoke an order and return the status returned
   * 
   * @param urlAttach2
   *          the url attachment of the resource
   * @return the TaskModel returned representing the status of the Task resource
   */
  protected TaskModel invokeOrder(String urlAttach2, String parameters) {
    String url = getHostUrl() + DATASET_URL + urlAttach2 + parameters;
    Representation result = null;
    try {
      ClientResource cr = new ClientResource(url);
      ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userLogin, password);
      cr.setChallengeResponse(chal);
      result = cr.post(null, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, TaskModel.class);
      assertTrue(response.getSuccess());

      return (TaskModel) response.getItem();
    }
    finally {
      RIAPUtils.exhaust(result);
    }
  }

  private void getServicesDescription(String parentApplicationUrl) {
    String url = parentApplicationUrl + "/services/server";
    Representation result = null;
    try {
      ClientResource cr = new ClientResource(url);
      result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, ResourceModelDTO.class, true);
      assertTrue(response.getSuccess());
      assertNotNull(response.getData());
      assertEquals(1, response.getData().size());

      ResourceModelDTO resDTO = (ResourceModelDTO) response.getData().get(0);
      List<ResourceParameter> parameters = resDTO.getParameters();
      boolean found = false;
      for (ResourceParameter resourceParameter : parameters) {
        if (resourceParameter.getName().equals("fileName")) {
          found = true;
          assertEquals("xs:template", resourceParameter.getValueType());
          String value = resourceParameter.getValue();
          assertFalse(value.contains("${date:"));

          break;
        }
      }
      if (!found) {
        fail("Parameter fileName not found");
      }

    }
    finally {
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Invoke an order with a file as parameter
   * 
   * @param urlAttach2
   *          the url attachment of the resource
   * @return the TaskModel returned representing the status of the Task resource
   * @throws UnsupportedEncodingException
   *           if the encoding is not supported
   */
  protected TaskModel invokeOrderWithFile(String urlAttach2) throws UnsupportedEncodingException {
    String url = getHostUrl() + DATASET_URL + urlAttach2 + "?runType=async&limit=4";

    String urlFile = URLEncoder
        .encode(settings.getString(Consts.APP_USERSTORAGE_USER_URL).replace("{identifier}", userLogin) + "/files"
            + urlFilePath, "UTF-8");
    StringRepresentation strRepr = new StringRepresentation("urlFile=" + urlFile);

    Representation result = null;
    try {
      ClientResource cr = new ClientResource(url);
      ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userLogin, password);
      cr.setChallengeResponse(chal);
      result = cr.post(strRepr, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, TaskModel.class);
      assertTrue(response.getSuccess());

      return (TaskModel) response.getItem();
    }
    finally {
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Assert if there are no Orders
   */
  protected void assertNoneOrder() {

    ClientResource cr = new ClientResource(getOrderUrl());
    ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userLogin, password);

    cr.setChallengeResponse(chal);

    Representation result = cr.get(getMediaTest());

    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    Response response = getResponseOrderandUserstorage(getMediaTest(), result, Order.class, true);
    assertTrue(response.getSuccess());
    assertEquals(0, response.getTotal().intValue());

  }

  /**
   * POST a new userStorage definition.
   * 
   * @param userId
   *          user login used for user storage
   */
  private void createUserStorage(String userId) {
    UserStorage us = new UserStorage();
    us.setUserId(userId);
    DiskStorage uds = new DiskStorage();
    uds.setQuota((long) 1000000);
    us.setStorage(uds);

    JsonRepresentation rep = new JsonRepresentation(us);

    ClientResource cr = new ClientResource(getUserStorageUrl() + "/users");
    Representation result = cr.post(rep);
    assertNotNull(result);
    // Parse response
    Response response = getResponseOrderandUserstorage(getMediaTest(), result, UserStorage.class);
    assertNotNull(response);
    assertTrue(response.getSuccess());

    RIAPUtils.exhaust(result);

  }

  /**
   * Assert if the order exists and is done
   * 
   * @param order
   *          The order
   */
  protected void assertOrderDone(Order order) {
    assertNotNull(order);
    assertEquals("done", order.getStatus());

  }

  /**
   * Assert if the FileOrdered have been copied It gets the list of files from the order, and gets the file described in
   * the list of files
   * 
   * @param order
   *          the order to assert
   * @param nbFileCopied
   *          the expected number of files to be copied
   */
  private void assertFileOrderedExists(Order order, int nbFileCopied) {
    assertNotNull(order);
    assertNotNull(order.getResourceCollection());

    assertEquals(1, order.getResourceCollection().size());
    Representation result = null;
    try {

      String res = order.getResourceCollection().get(0);

      ClientResource cr = new ClientResource(res);

      ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userLogin, password);

      cr.setChallengeResponse(chal);

      result = cr.get(getMediaTest());

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      try {
        // get the list of files
        String text = result.getText();
        String[] contents = text.split("\r\n");
        assertNotNull(contents);
        assertEquals(nbFileCopied, contents.length);
        Reference content = new Reference(contents[0]);
        // asserts
        assertNotNull(content);
        assertNotSame("", content.toString());
        // get the file corresponding to the first url, check that this file
        // exists
        cr = new ClientResource(content);

        chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userLogin, password);

        cr.setChallengeResponse(chal);

        result = cr.get(getMediaTest());
        assertNotNull(result);
        assertTrue(cr.getStatus().isSuccess());

      }
      catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      finally {
        RIAPUtils.exhaust(result);
      }

    }
    finally {
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Get the Order at the given url
   * 
   * @param url
   *          the url where to find the Order
   * @return the Order found
   */
  protected Order getOrder(String url) {

    ClientResource cr = new ClientResource(url);

    ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userLogin, password);

    cr.setChallengeResponse(chal);

    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponseOrderandUserstorage(getMediaTest(), result, Order.class);
    assertTrue(response.getSuccess());
    result.release();
    return (Order) response.getItem();
  }

  /**
   * Delete the given Order
   * 
   * @param order
   *          the order to delete
   */
  protected void deleteOrder(Order order) {
    // TODO Auto-generated method stub
    String url = getOrderUrl() + "/" + order.getId();

    ClientResource cr = new ClientResource(url);
    ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userLogin, password);

    cr.setChallengeResponse(chal);

    Representation result = cr.delete(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponseOrderandUserstorage(getMediaTest(), result, Order.class);
    assertTrue(response.getSuccess());
    result.release();

  }

  /**
   * Assert if the FileOrdered have been copied It gets the list of files from the order, and gets the file described in
   * the list of files
   * 
   * @param order
   *          the order to assert
   * @throws IOException
   */
  private void assertFileOrderedExistsForZip(Order order) throws IOException {
    assertNotNull(order);
    assertNotNull(order.getResourceCollection());

    assertEquals(1, order.getResourceCollection().size());
    Representation result = null;
    try {
      // let's get the order file list url
      Reference fileListUrl;
      Reference zipUrl;

      ReferenceList refList = new ReferenceList();
      refList.add(order.getResourceCollection().get(0));
      // refList.add(order.getResourceCollection().get(1));

      // if (refList.get(0).getExtensions().equals("txt")) {
      // fileListUrl = refList.get(0);
      // zipUrl = refList.get(1);
      // }
      // else {
      // fileListUrl = refList.get(1);
      // zipUrl = refList.get(0);
      // }

      fileListUrl = refList.get(0);

      ClientResource cr = new ClientResource(fileListUrl);

      ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userLogin, password);

      cr.setChallengeResponse(chal);

      result = cr.get(getMediaTest());

      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      try {
        String fileTxt = result.getText();
        String[] contents = fileTxt.split("\r\n");
        assertNotNull(contents);
        zipUrl = new Reference(contents[0]);

        cr = new ClientResource(zipUrl);

        chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userLogin, password);

        cr.setChallengeResponse(chal);

        result = cr.get(getMediaTest());

        assertNotNull(result);
        assertTrue(cr.getStatus().isSuccess());
        assertEquals(MediaType.APPLICATION_ZIP, result.getMediaType());

      }
      finally {
        RIAPUtils.exhaust(result);
      }

    }
    finally {
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Post some JSON to the user userstorage
   */
  private void postJSON() {
    // http://localhost:8182/sitools/userstorage/admin?filepath=%2Ftmp&filename=SvaRecordDefinitionFile.json
    JsonRepresentation repr = new JsonRepresentation(urlFileContent);
    String url = getBaseUrl() + settings.getString(Consts.APP_USERSTORAGE_USER_URL).replace("{identifier}", userLogin)
        + "/files?filename=SvaRecordDefinitionFile.json&filepath=%2Ftmp";
    ClientResource cr = new ClientResource(url);
    ChallengeResponse challenge = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userLogin, password);
    cr.setChallengeResponse(challenge);
    Representation result = cr.post(repr, MediaType.APPLICATION_JSON);
    assertNotNull(result);
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
  public static Response getResponseOrderandUserstorage(MediaType media, Representation representation,
      Class<?> dataClass) {
    return getResponseOrderandUserstorage(media, representation, dataClass, false);
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
  public static Response getResponseOrderandUserstorage(MediaType media, Representation representation,
      Class<?> dataClass, boolean isArray) {
    try {
      if (!media.isCompatible(MediaType.APPLICATION_JSON) && !media.isCompatible(MediaType.APPLICATION_XML)) {
        Logger.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);

      // for order
      xstream.alias("order", Order.class);

      // for userstorage
      xstream.alias("userstorage", UserStorage.class);
      xstream.alias("diskStorage", DiskStorage.class);

      if (isArray) {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);
        // xstream.omitField(Response.class, "data");
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
          xstream.addImplicitCollection(Order.class, "events", Event.class);
          xstream.addImplicitCollection(Order.class, "resourceCollection", String.class);
        }

      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == Order.class) {
          xstream.aliasField("order", Response.class, "item");
          if (media.isCompatible(MediaType.APPLICATION_JSON)) {
            xstream.addImplicitCollection(Order.class, "events", Event.class);
            xstream.addImplicitCollection(Order.class, "resourceCollection", String.class);
          }
        }
        if (dataClass == UserStorage.class) {
          xstream.aliasField("userstorage", Response.class, "item");
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
  public static Representation getRepresentation(TaskModel item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JsonRepresentation(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<TaskModel> rep = new XstreamRepresentation<TaskModel>(media, item);
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
   * Configures XStream mapping of Response object with SvaModel content.
   * 
   * @param xstream
   *          XStream
   */
  private static void configure(XStream xstream) {
    xstream.autodetectAnnotations(false);
    xstream.alias("response", Response.class);
    xstream.alias("TaskModel", TaskModel.class);
  }

}
