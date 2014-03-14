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
package fr.cnes.sitools.ext.test.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.restlet.Context;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.engine.Engine;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.AbstractSitoolsServerTestCase;
import fr.cnes.sitools.AbstractSitoolsTestCase;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.model.ExtensionModel;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.validator.ConstraintViolation;
import fr.cnes.sitools.ext.test.common.AbstractExtSitoolsServerTestCase;
import fr.cnes.sitools.plugins.resources.dto.ResourceModelDTO;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.tasks.model.TaskModel;
import fr.cnes.sitools.tasks.model.TaskResourceModel;
import fr.cnes.sitools.tasks.model.TaskStatus;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Utility class to provide Method to test TaskResources
 * 
 * 
 * @author m.gond
 */
public abstract class AbstractTaskResourceTestCase extends AbstractExtSitoolsServerTestCase {

  /** Max number of tries before stopping */
  private int maxTries = 5;

  /**
   * absolute url for project management REST API
   * 
   * @return url
   */
  protected String getBaseUrlAdmin() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_TASK_URL);
  }

  /**
   * absolute url for project management REST API
   * 
   * @return url
   */
  protected String getBaseUrlUser() {
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_USERRESOURCE_ROOT_URL) + "/{identifier}"
        + SitoolsSettings.getInstance().getString(Consts.APP_TASK_URL);
  }

  // create ResourceModel object
  /**
   * Create a new TaskResourceModel object of with the followinf taskResourceModelClassName class
   * 
   * @param taskResourceModelClassName
   *          the name of the class of the TaskResourceModel
   * @param id
   *          the if of the model
   * @param urlAttach
   *          the attachement of the ressource
   * @return a new TaskResourceModel
   * @throws ClassNotFoundException
   *           if the class corresponding to taskResourceModelClassName doesn't exists
   * @throws InstantiationException
   *           if there is an error while Instantiating a new TaskResourceModel
   * @throws IllegalAccessException
   *           if there is an error while Instantiating a new TaskResourceModel
   */
  public ResourceModel createResourceModel(String taskResourceModelClassName, String id, String urlAttach) throws ClassNotFoundException,
    InstantiationException, IllegalAccessException {

    @SuppressWarnings("unchecked")
    Class<ResourceModel> resourceModelClass = (Class<ResourceModel>) Class.forName(taskResourceModelClassName);

    ResourceModel resourceModel = resourceModelClass.newInstance();
    Context context = settings.getComponent().getContext();
    resourceModel.initParametersForAdmin(context);

    resourceModel.setId(id);
    resourceModel.getParameterByName("url").setValue(urlAttach);

    return resourceModel;
  }

  // create

  /**
   * Add a ResourceModel to the server
   * 
   * @param resourceModel
   *          ResourceModel
   * @param baseUrl
   *          the baseUrl of the application to create the Resource
   * 
   */
  public void create(ResourceModel resourceModel, String baseUrl) {

    ResourceModelDTO dto = getResourceModelDTO(resourceModel);

    Representation appRep = getRepresentation(dto, getMediaTest());
    ClientResource cr = new ClientResource(baseUrl + settings.getString(Consts.APP_RESOURCES_URL));
    Representation result = cr.post(appRep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    Response response = getResponse(getMediaTest(), result, ResourceModelDTO.class);
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
  public void update(TaskResourceModel resourceModel, String baseUrl) {
    ResourceModelDTO dto = getResourceModelDTO(resourceModel);

    Representation appRep = getRepresentation(dto, getMediaTest());
    ClientResource cr = new ClientResource(baseUrl + settings.getString(Consts.APP_RESOURCES_URL) + "/" + resourceModel.getId());
    Representation result = cr.put(appRep, getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    Response response = getResponse(getMediaTest(), result, ResourceModelDTO.class);
    assertTrue(response.getSuccess());
    assertNotNull(response.getItem());
    ResourceModelDTO resourceModelOut = (ResourceModelDTO) response.getItem();
    assertEquals(resourceModel.getId(), resourceModelOut.getId());
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
  public void delete(ResourceModel resourceModel, String baseUrl) {
    ClientResource cr = new ClientResource(baseUrl + settings.getString(Consts.APP_RESOURCES_URL) + "/" + resourceModel.getId());
    Representation result = cr.delete(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    assertNotNull(result);
    Response response = getResponse(getMediaTest(), result, ResourceModel.class);
    assertTrue(response.getSuccess());
    RIAPUtils.exhaust(result);
  }

  /**
   * Assert that there is no Tasks for the given user
   * 
   * @param userId
   *          the User identifier
   * @param password
   *          the User password
   */
  public void assertNoneTasks(String userId, String password) {
    String url = getUrlForTasks(userId);
    ClientResource cr = new ClientResource(url);
    ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userId, password);
    cr.setChallengeResponse(chal);
    Representation result = cr.get(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, TaskModel.class, true);
    assertTrue(response.getSuccess());
    assertEquals(new Integer(0), response.getTotal());
    RIAPUtils.exhaust(result);

  }

  /**
   * Delete the given task
   * 
   * @param userId
   *          the userId
   * @param taskId
   *          the id of the task to delete
   * @param password
   *          the User password
   * @param asPublic
   * @param asPublic
   *          if the requests are made as public or with the given userId and password
   */
  public void deleteTask(String userId, String taskId, String password, boolean asPublic) {
    String url = getUrlForTasks(userId) + "/" + taskId;

    ClientResource cr = new ClientResource(url);
    if (!asPublic) {
      ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userId, password);
      cr.setChallengeResponse(chal);
    }
    Representation result = cr.delete(getMediaTest());
    assertNotNull(result);
    assertTrue(cr.getStatus().isSuccess());
    Response response = getResponse(getMediaTest(), result, TaskModel.class);
    assertTrue(response.getSuccess());
    RIAPUtils.exhaust(result);
  }

  /**
   * Get a TaskModel for the given userId and taskId
   * 
   * @param userId
   *          the userId
   * @param taskId
   *          the id of the task to delete
   * @param password
   *          the User password
   * @return the TaskModel
   */
  public TaskModel getTaskModel(String userId, String taskId, String password) {
    String url = getUrlForTasks(userId) + "/" + taskId;
    Representation result = null;
    try {
      ClientResource cr = new ClientResource(url);
      ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userId, password);
      cr.setChallengeResponse(chal);
      result = cr.get(getMediaTest());
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
   * Get a TaskModel at the given url
   * 
   * @param url
   *          the url
   * @param userId
   *          the User identifier
   * @param password
   *          the User password
   * @param asPublic
   *          if the requests are made as public or with the given userId and password
   * @return the TaskModel
   */
  public TaskModel getTaskModelWithUrlParam(String url, String userId, String password, boolean asPublic) {
    Representation result = null;
    try {
      ClientResource cr = new ClientResource(url);
      if (!asPublic) {
        ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userId, password);
        cr.setChallengeResponse(chal);
      }
      result = cr.get(getMediaTest());
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
   * Gets the Status of a given TaskModel Loop while the status is FINISHED
   * 
   * @param taskModel
   *          the SvaTaskModel
   * @param userId
   *          the User identifier
   * @param password
   *          the password
   * @param asPublic
   *          if the requests are made as public or with the given userId and password
   * @return SvaTaskModel the last Status
   * @throws InterruptedException
   *           if any thread has interrupted the current thread. The interrupted status of the current thread is cleared
   *           when this exception is thrown.
   */
  public TaskModel testGetAsyncTaskModel(TaskModel taskModel, String userId, String password, boolean asPublic) throws InterruptedException {
    String urlStatus = super.getHostUrl() + taskModel.getStatusUrl();

    int i = 0;

    while (taskModel != null && i < maxTries
        && (taskModel.getStatus() == TaskStatus.TASK_STATUS_PENDING || taskModel.getStatus() == TaskStatus.TASK_STATUS_RUNNING)) {
      Thread.sleep(1000);
      urlStatus = super.getHostUrl() + taskModel.getStatusUrl();
      taskModel = getTaskModelWithUrlParam(urlStatus, userId, password, asPublic);
      System.out.println("TRY TO GET TASK MODEL " + taskModel.getStatus() + " CUSTOM STATUS = " + taskModel.getCustomStatus());
      i++;

    }
    return taskModel;
  }

  /**
   * Get the url
   * 
   * @param userId
   *          the user id
   * @return the url
   */
  public String getUrlForTasks(String userId) {
    String url = "";
    if (userId != null) {
      url = getBaseUrlUser().replace("{identifier}", userId);
    }
    else {
      url = getBaseUrlAdmin();
    }
    return url;
  }

  /**
   * Get a ResourceModelDTO from a ResourceModel
   * 
   * @param resource
   *          the ResourceModel
   * @return a ResourceModelDTO
   */
  public ResourceModelDTO getResourceModelDTO(ResourceModel resource) {
    return ResourceModelDTO.resourceModelToDTO(resource);
  }

  /**
   * Assert that the status given is successful
   * 
   * @param status
   *          the status to assert
   */
  public void assertStatusSuccess(TaskModel status) {
    assertNotNull(status.getStatus());
    assertEquals(status.getCustomStatus(), TaskStatus.TASK_STATUS_FINISHED, status.getStatus());
  }

  /**
   * Assert that the staus given is an error
   * 
   * @param status
   *          the status to assert
   */
  public void assertStatusError(TaskModel status) {
    assertNotNull(status.getStatus());
    assertEquals(status.getCustomStatus(), TaskStatus.TASK_STATUS_FAILURE, status.getStatus());
  }

  // query

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
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
        return null;
      }

      XStream xstream = XStreamFactory.getInstance().getXStreamReader(media);
      xstream.autodetectAnnotations(false);
      xstream.alias("response", Response.class);
      xstream.alias("resourcePlugin", ResourceModelDTO.class);
      xstream.alias("resourceParameter", ResourceParameter.class);
      xstream.omitField(ExtensionModel.class, "parametersMap");

      xstream.alias("TaskModel", TaskModel.class);
      xstream.alias("image", Resource.class);
      xstream.alias("status", TaskStatus.class);

      if (dataClass == ConstraintViolation.class) {
        xstream.alias("constraintViolation", ConstraintViolation.class);
      }

      if (dataClass == ResourceModelDTO.class && media.isCompatible(MediaType.APPLICATION_JSON)) {
        xstream.addImplicitCollection(ResourceModelDTO.class, "parameters", ResourceParameter.class);
      }

      if (dataClass == TaskModel.class && media.isCompatible(MediaType.APPLICATION_JSON)) {
        xstream.addImplicitCollection(TaskModel.class, "properties", Object.class);

      }

      if (isArray) {
        if (media.isCompatible(MediaType.APPLICATION_JSON)) {
          if (dataClass == ConstraintViolation.class) {
            xstream.addImplicitCollection(Response.class, "data", dataClass);
          }
          else {
            xstream.addImplicitCollection(Response.class, "data", ResourceModelDTO.class);
            xstream.addImplicitCollection(ResourceModelDTO.class, "parameters", ResourceParameter.class);
          }
        }
        else {
          xstream.alias("resourcePlugin", Object.class, ResourceModelDTO.class);
        }
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (dataClass == ResourceModelDTO.class) {
          xstream.aliasField("resourcePlugin", Response.class, "item");
          xstream.alias("resourcePlugin", Object.class, ResourceModelDTO.class);
        }
        if (dataClass == TaskModel.class) {
          xstream.aliasField("TaskModel", Response.class, "item");
        }
      }

      SitoolsXStreamRepresentation<Response> rep = new SitoolsXStreamRepresentation<Response>(representation);
      rep.setXstream(xstream);

      if (media.isCompatible(getMediaTest())) {
        Response response = rep.getObject("response");

        return response;
      }
      else {
        Engine.getLogger(AbstractSitoolsTestCase.class.getName()).warning("Only JSON or XML supported in tests");
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
  public static Representation getRepresentation(ResourceModelDTO item, MediaType media) {
    if (media.equals(MediaType.APPLICATION_JSON)) {
      return new JacksonRepresentation<ResourceModelDTO>(item);
    }
    else if (media.equals(MediaType.APPLICATION_XML)) {
      XStream xstream = XStreamFactory.getInstance().getXStream(media, false);
      XstreamRepresentation<ResourceModelDTO> rep = new XstreamRepresentation<ResourceModelDTO>(media, item);
      configure(xstream);
      rep.setXstream(xstream);
      return rep;
    }
    else {
      Engine.getLogger(AbstractSitoolsServerTestCase.class.getName()).warning("Only JSON or XML supported in tests");
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
    xstream.alias("resourcePlugin", ResourceModel.class);
    xstream.alias("resourceParameter", ResourceParameter.class);
  }

}
