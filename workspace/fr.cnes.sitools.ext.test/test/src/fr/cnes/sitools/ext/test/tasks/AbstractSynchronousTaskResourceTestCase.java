 /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Test the Task Resources Synchronously
 * 
 * 
 * @author m.gond
 */
public abstract class AbstractSynchronousTaskResourceTestCase extends AbstractTaskResourceTestCase {
  /**
   * The if of the dataset
   */
  private static final String PROJECT_ID = "premier";
  /**
   * The url of the dataset
   */
  private static final String PROJECT_URL = "/proj/premier";

  /**
   * The class name of the resourceModel
   */
  private String resourceModelClassName = "fr.cnes.sitools.resources.tasks.test.MyTaskResourceFacadeModel";

  /**
   * The url attachment for the resource model
   */
  private String urlAttach = "/test";

  /**
   * absolute url for dataset management REST API
   * 
   * @return url
   */
  public final String getBaseProjectUrl() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_PROJECTS_URL) + "/" + PROJECT_ID;
  }

  /**
   * Test the Resource with a GET call
   * 
   * @throws ClassNotFoundException
   *           if the class cannot be found
   * @throws InstantiationException
   *           if there is an error while instantiating the resource
   * @throws IllegalAccessException
   *           if there is an error while instantiating the resource
   * @throws IOException
   *           if the response cannot be read
   */
  @Test
  public void testGet() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
    ResourceModel taskResource = createResourceModel(resourceModelClassName, "1000", urlAttach);
    create(taskResource, getBaseProjectUrl());
    String url = getHostUrl() + PROJECT_URL + urlAttach;
    ClientResource cr = new ClientResource(url);

    Representation result = cr.get();
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    assertEquals("TestGet", result.getText());

    RIAPUtils.exhaust(result);
    delete(taskResource, getBaseProjectUrl());
  }

  /**
   * Test the Resource with a POST call
   * 
   * @throws ClassNotFoundException
   *           if the class cannot be found
   * @throws InstantiationException
   *           if there is an error while instantiating the resource
   * @throws IllegalAccessException
   *           if there is an error while instantiating the resource
   * @throws IOException
   *           if the response cannot be read
   */
  @Test
  public void testPost() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
    ResourceModel taskResource = createResourceModel(resourceModelClassName, "1000", urlAttach);
    create(taskResource, getBaseProjectUrl());
    String url = getHostUrl() + PROJECT_URL + urlAttach;
    ClientResource cr = new ClientResource(url);
    // Test GET
    Representation result = cr.post(new EmptyRepresentation(), getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    assertEquals("TestPost", result.getText());

    RIAPUtils.exhaust(result);
    delete(taskResource, getBaseProjectUrl());
  }

  /**
   * Test the Resource with a PUT call
   * 
   * @throws ClassNotFoundException
   *           if the class cannot be found
   * @throws InstantiationException
   *           if there is an error while instantiating the resource
   * @throws IllegalAccessException
   *           if there is an error while instantiating the resource
   * @throws IOException
   *           if the response cannot be read
   */
  @Test
  public void testPut() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
    ResourceModel taskResource = createResourceModel(resourceModelClassName, "1000", urlAttach);
    create(taskResource, getBaseProjectUrl());
    String url = getHostUrl() + PROJECT_URL + urlAttach;
    ClientResource cr = new ClientResource(url);
    Representation result = cr.put(new EmptyRepresentation(), getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    assertEquals("TestPut", result.getText());

    RIAPUtils.exhaust(result);
    delete(taskResource, getBaseProjectUrl());
  }

  /**
   * Test the Resource with a DELETE call
   * 
   * @throws ClassNotFoundException
   *           if the class cannot be found
   * @throws InstantiationException
   *           if there is an error while instantiating the resource
   * @throws IllegalAccessException
   *           if there is an error while instantiating the resource
   * @throws IOException
   *           if the response cannot be read
   */
  @Test
  public void testDelete() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
    ResourceModel taskResource = createResourceModel(resourceModelClassName, "1000", urlAttach);
    create(taskResource, getBaseProjectUrl());
    String url = getHostUrl() + PROJECT_URL + urlAttach;
    ClientResource cr = new ClientResource(url);
    Representation result = cr.delete(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());

    assertEquals("TestDelete", result.getText());

    RIAPUtils.exhaust(result);
    delete(taskResource, getBaseProjectUrl());
  }

  /**
   * Test the Resource with a DELETE call
   * 
   * @throws ClassNotFoundException
   *           if the class cannot be found
   * @throws InstantiationException
   *           if there is an error while instantiating the resource
   * @throws IllegalAccessException
   *           if there is an error while instantiating the resource
   * @throws IOException
   *           if the response cannot be read
   */
  @Test
  public void testGetError() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
    String errorMessage = "errorMessage";
    ResourceModel taskResource = createResourceModel(resourceModelClassName, "1000", urlAttach);
    taskResource.getParameterByName("error").setValue("true");
    taskResource.getParameterByName("error_message").setValue(errorMessage);

    create(taskResource, getBaseProjectUrl());
    String url = getHostUrl() + PROJECT_URL + urlAttach;

    final Client client = new Client(Protocol.HTTP);
    Request request = new Request(Method.GET, url);

    org.restlet.Response response = client.handle(request);

    assertNotNull(response);
    assertEquals(Status.SERVER_ERROR_INTERNAL, response.getStatus());

    RIAPUtils.exhaust(response);
    delete(taskResource, getBaseProjectUrl());
  }
}
