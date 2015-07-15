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
package fr.cnes.sitools.ext.test.resources;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.tasks.AbstractTaskResourceTestCase;
import fr.cnes.sitools.util.RIAPUtils;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.engine.util.DateUtils;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test the DirectOrderResource Create a Resource on a dataset and try to get archives from multiple format known and
 * unknown
 * 
 * 
 * @author m.gond
 */
public class DirectOrderResourceTestCase extends AbstractTaskResourceTestCase {

  /**
   * The if of the dataset
   */
  private static final String DATASET_ID = "e6892ea4-680a-47d5-af82-c72908dea319";
  /**
   * The url of the dataset
   */
  private static final String DATASET_URL = "/testmysql";

  /**
   * The class name of the resourceModel
   */
  private String orderResourceModelClassName = "fr.cnes.sitools.resources.order.DirectOrderResourceModel";

  /** The columnAlias for the colUrl parameter */
  private String colUrlDataset = "data_url";

  /** The url attachment for the resource model */
  private String urlAttach = "/order/direct";
  /** The ResourceModel */
  private ResourceModel resourceModel;

  /** The fileName template to use */
  private String fileNameTemplate = "dataset_order_${date:" + DateUtils.FORMAT_RFC_3339.get(0).replace(':', '_') + "}";

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  public final String getBaseDatasetUrl() {
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
   * Set up
   * 
   * @throws Exception
   *           if there is something wrong
   */
  @Before
  public void setUp() throws Exception {
    super.setUp();
    setMediaTest(MediaType.APPLICATION_JSON);

    File dirStoreOrder = new File(settings.getStoreDIR(Consts.APP_ORDERS_STORE_DIR) + "/map");
    cleanDirectory(dirStoreOrder);

    File dirStoreResPlugin = new File(settings.getStoreDIR(Consts.APP_PLUGINS_RESOURCES_STORE_DIR) + "/map");
    cleanDirectory(dirStoreResPlugin);

  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.AbstractSitoolsServerTestCase#tearDown()
   */
  @Override
  public void tearDown() throws Exception {
    super.tearDown();
    if (resourceModel != null) {
      delete(resourceModel, getBaseDatasetUrl());
    }

  }

  /**
   * Test the DirectOrderResource and expect a Zip archive result
   * 
   * @throws ClassNotFoundException
   *           if the class corresponding to taskResourceModelClassName doesn't exists
   * @throws InstantiationException
   *           if there is an error while Instantiating a new TaskResourceModel
   * @throws IllegalAccessException
   *           if there is an error while Instantiating a new TaskResourceModel
   */
  @Test
  public void testOrderResourceZip() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    testOrder(MediaType.APPLICATION_ZIP, "zip");

  }

  /**
   * Test the DirectOrderResource and expect a Tar archive result
   * 
   * @throws ClassNotFoundException
   *           if the class corresponding to taskResourceModelClassName doesn't exists
   * @throws InstantiationException
   *           if there is an error while Instantiating a new TaskResourceModel
   * @throws IllegalAccessException
   *           if there is an error while Instantiating a new TaskResourceModel
   * 
   */
  @Test
  public void testOrderResourceTar() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    testOrder(MediaType.APPLICATION_TAR, "tar");

  }

  /**
   * Test the DirectOrderResource and expect a Tar.gz archive result
   * 
   * @throws ClassNotFoundException
   *           if the class corresponding to taskResourceModelClassName doesn't exists
   * @throws InstantiationException
   *           if there is an error while Instantiating a new TaskResourceModel
   * @throws IllegalAccessException
   *           if there is an error while Instantiating a new TaskResourceModel
   * 
   */
  @Test
  public void testOrderResourceTarGz() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    testOrder(MediaType.APPLICATION_TAR, "tar.gz");
  }

  /**
   * Test the DirectOrderResource and expect a Tar.gz archive result
   * 
   * @throws ClassNotFoundException
   *           if the class corresponding to taskResourceModelClassName doesn't exists
   * @throws InstantiationException
   *           if there is an error while Instantiating a new TaskResourceModel
   * @throws IllegalAccessException
   *           if there is an error while Instantiating a new TaskResourceModel
   * 
   */
  @Test
  public void testOrderResourceUnknowArchiveType() throws ClassNotFoundException, InstantiationException,
      IllegalAccessException {
    resourceModel = createResourceModel(orderResourceModelClassName, "1000", urlAttach);
    resourceModel = fillOrderResourceParameters(resourceModel);
    resourceModel.getParameterByName("too_many_selected_threshold").setValue("10");
    create(resourceModel, getBaseDatasetUrl());
    testOrderError("gzip", Status.CLIENT_ERROR_NOT_ACCEPTABLE);
  }

  /**
   * Test the DirectOrderResource and expect a Tar.gz archive result
   * 
   * @throws ClassNotFoundException
   *           if the class corresponding to taskResourceModelClassName doesn't exists
   * @throws InstantiationException
   *           if there is an error while Instantiating a new TaskResourceModel
   * @throws IllegalAccessException
   *           if there is an error while Instantiating a new TaskResourceModel
   * 
   */
  @Test
  public void testOrderResourceTooManyFileLimitNotReachedOrdered() throws ClassNotFoundException,
      InstantiationException, IllegalAccessException {
    resourceModel = createResourceModel(orderResourceModelClassName, "1000", urlAttach);
    resourceModel = fillOrderResourceParameters(resourceModel);
    resourceModel.getParameterByName("too_many_selected_threshold").setValue("5");
    create(resourceModel, getBaseDatasetUrl());
    testOrder(MediaType.APPLICATION_TAR, "tar");
  }

  /**
   * Test the DirectOrderResource and expect a Tar.gz archive result
   * 
   * @throws ClassNotFoundException
   *           if the class corresponding to taskResourceModelClassName doesn't exists
   * @throws InstantiationException
   *           if there is an error while Instantiating a new TaskResourceModel
   * @throws IllegalAccessException
   *           if there is an error while Instantiating a new TaskResourceModel
   */
  @Test
  public void testOrderResourceTooManyFileOrdered() throws ClassNotFoundException, InstantiationException,
      IllegalAccessException {
    resourceModel = createResourceModel(orderResourceModelClassName, "1000", urlAttach);
    resourceModel = fillOrderResourceParameters(resourceModel);
    resourceModel.getParameterByName("too_many_selected_threshold").setValue("2");
    create(resourceModel, getBaseDatasetUrl());
    testOrderError("tar", Status.CLIENT_ERROR_BAD_REQUEST);
  }

  /**
   * Common test method with {@link org.restlet.data.MediaType} parameter, fileName and archiveType
   *
   * @param mediaType
   *          the {@link org.restlet.data.MediaType}
   *
   * @param archiveType
   *          the archiveType
   * @throws ClassNotFoundException
   *           if the class corresponding to taskResourceModelClassName doesn't exists
   * @throws InstantiationException
   *           if there is an error while Instantiating a new TaskResourceModel
   * @throws IllegalAccessException
   *           if there is an error while Instantiating a new TaskResourceModel
   */
  private void testOrder(MediaType mediaType, String archiveType) throws ClassNotFoundException,
      InstantiationException, IllegalAccessException {
    resourceModel = createResourceModel(orderResourceModelClassName, "1000", urlAttach);
    resourceModel = fillOrderResourceParameters(resourceModel);
    create(resourceModel, getBaseDatasetUrl());
    String fileName = getServicesDescription(getHostUrl() + DATASET_URL);

    invokeOrderGet(urlAttach, "?ranges=[[0,2]]&archiveType=" + archiveType + "&fileName=" + fileName, mediaType,
        fileName + "." + archiveType);
  }

  /**
   * test order with error expected because the archiveType needed is not available
   *
   * @param archiveType
   *          the archiveType needed
   * @param expectedErrorStatus
   *          the Expected error {@link org.restlet.data.Status}
   */
  private void testOrderError(String archiveType, Status expectedErrorStatus) {
    String url = getHostUrl() + DATASET_URL + urlAttach + "?ranges=[[1,5]]&archiveType=" + archiveType;
    Representation result = null;

    final Client client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, url);
    org.restlet.Response response = client.handle(request);
    try {
      assertNull(result);
      assertTrue(response.getStatus().isError());
      assertEquals(expectedErrorStatus, response.getStatus());

    }
    finally {
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Fill the specific parameters for an OrderResource
   *
   * @param resourceModel
   *          the ResourceModel to fill
   *
   * @return the taskResource completed
   */
  protected ResourceModel fillOrderResourceParameters(ResourceModel resourceModel) {

    ResourceParameter paramColUrl = resourceModel.getParameterByName("colUrl");
    paramColUrl.setValue(colUrlDataset);

    ResourceParameter paramArchiveType = resourceModel.getParameterByName("archiveType");
    paramArchiveType.setUserUpdatable(true);
    paramArchiveType.setValue("tar");

    ResourceParameter paramFileName = resourceModel.getParameterByName("fileName");
    paramFileName.setValue(fileNameTemplate);

    resourceModel.getParameterByName("too_many_selected_threshold").setValue("3");

    return resourceModel;
  }

  /**
   * Invoke an order and assert that the it is successful, that the {@link org.restlet.data.MediaType} of the result is expectedMediaType
   * and that its name if expectedFileName
   *
   * @param urlAttach2
   *          the url attachment of the resource
   * @param parameters
   *          the parameters to add to the request
   * @param expectedMediaType
   *          the expected {@link org.restlet.data.MediaType} in return
   * @param expectedFileName
   *          the expected file name in return
   * 
   */
  protected void invokeOrderGet(String urlAttach2, String parameters, MediaType expectedMediaType,
      String expectedFileName) {
    String url = getHostUrl() + DATASET_URL + urlAttach2 + parameters;
    Representation result = null;
    try {
      ClientResource cr = new ClientResource(url);
      result = cr.get();
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());

      assertEquals(expectedMediaType, result.getMediaType());

      assertNotNull(result.getDisposition());
      String fileName = result.getDisposition().getFilename();
      assertNotNull(fileName);
      assertEquals(expectedFileName, fileName);

    }
    finally {
      RIAPUtils.exhaust(result);
    }
  }

  private String getServicesDescription(String parentApplicationUrl) {
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
      String fileName = null;
      for (ResourceParameter resourceParameter : parameters) {
        if (resourceParameter.getName().equals("fileName")) {
          found = true;
          assertEquals("xs:template", resourceParameter.getValueType());
          String value = resourceParameter.getValue();
          assertFalse(value.contains("${date:"));
          fileName = resourceParameter.getValue();
          break;
        }
      }
      if (!found) {
        fail("Parameter fileName not found");
      }
      return fileName;
    }
    finally {
      RIAPUtils.exhaust(result);
    }

  }
}
