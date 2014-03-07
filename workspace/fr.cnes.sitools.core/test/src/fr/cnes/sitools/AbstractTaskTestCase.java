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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Restlet;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.ext.xstream.XstreamRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.security.User;

import com.thoughtworks.xstream.XStream;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.SitoolsXStreamRepresentation;
import fr.cnes.sitools.common.XStreamFactory;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Category;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.common.store.SitoolsStore;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSource;
import fr.cnes.sitools.datasource.jdbc.business.SitoolsSQLDataSourceFactory;
import fr.cnes.sitools.plugins.resources.ResourcePluginStoreXML;
import fr.cnes.sitools.role.RoleStoreXML;
import fr.cnes.sitools.role.model.Role;
import fr.cnes.sitools.security.JDBCUsersAndGroupsStore;
import fr.cnes.sitools.security.authentication.SitoolsMemoryRealm;
import fr.cnes.sitools.security.authorization.AuthorizationApplication;
import fr.cnes.sitools.security.authorization.AuthorizationStore;
import fr.cnes.sitools.security.authorization.AuthorizationStoreXML;
import fr.cnes.sitools.security.authorization.client.ResourceAuthorization;
import fr.cnes.sitools.security.userblacklist.UserBlackListModel;
import fr.cnes.sitools.security.userblacklist.UserBlackListStoreXML;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.tasks.TaskStoreXML;
import fr.cnes.sitools.tasks.TaskUtils;
import fr.cnes.sitools.tasks.business.Task;
import fr.cnes.sitools.tasks.business.TaskManager;
import fr.cnes.sitools.tasks.exposition.TaskApplication;
import fr.cnes.sitools.tasks.model.TaskModel;
import fr.cnes.sitools.tasks.model.TaskResourceModel;
import fr.cnes.sitools.tasks.model.TaskStatus;
import fr.cnes.sitools.util.RIAPUtils;

public class AbstractTaskTestCase extends AbstractSitoolsTestCase {

  /**
   * static xml store instance for the test
   */
  private static TaskStoreXML store = null;

  /**
   * static xml store instance for the test
   */
  private static ResourcePluginStoreXML storeResource = null;
  /**
   * static xml store instance for the test
   */
  private static SitoolsStore<UserBlackListModel> storeBlacklist = null;

  /** The SitoolsMemoryRealm */
  private static SitoolsMemoryRealm smr = null;
  /** AuthorizationStore */
  private AuthorizationStore storeAuthorization;

  /**
   * Restlet Component for server
   */
  private Component component = null;

  private String userAdminId = "admin";
  private String userAdminPwd = "admin";

  /**
   * relative url for project management REST API
   * 
   * @return url
   */
  protected String getAttachUrlAdmin() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_TASK_URL);
  }

  /**
   * relative url for project management REST API
   * 
   * @return url
   */
  protected String getAttachUrlUser() {
    return SITOOLS_URL + SitoolsSettings.getInstance().getString(Consts.APP_USERRESOURCE_ROOT_URL) + "/{identifier}"
        + SitoolsSettings.getInstance().getString(Consts.APP_TASK_URL);
  }

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
    return super.getBaseUrl() + SitoolsSettings.getInstance().getString(Consts.APP_USERRESOURCE_ROOT_URL)
        + "/{identifier}" + SitoolsSettings.getInstance().getString(Consts.APP_TASK_URL);
  }

  @Before
  @Override
  /**
   * Init and Start a server with TaskApplication
   *
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {
    SitoolsSettings settings = SitoolsSettings.getInstance();

    if (this.component == null) {
      this.component = new Component();
      this.component.getServers().add(Protocol.HTTP, getTestPort());
      this.component.getClients().add(Protocol.HTTP);
      this.component.getClients().add(Protocol.FILE);
      this.component.getClients().add(Protocol.CLAP);

      // Context
      Context appContextAuthorization = this.component.getContext().createChildContext();
      appContextAuthorization.getAttributes().put(ContextAttributes.SETTINGS, settings);

      if (smr == null) {
        SitoolsSQLDataSource dsSecurity = SitoolsSQLDataSourceFactory
            .getInstance()
            .setupDataSource(
                settings.getString("Starter.DATABASE_DRIVER"), settings.getString("Starter.DATABASE_URL"), settings.getString("Starter.DATABASE_USER"), settings.getString("Starter.DATABASE_PASSWORD"), settings.getString("Starter.DATABASE_SCHEMA")); //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        JDBCUsersAndGroupsStore storeUandG = new JDBCUsersAndGroupsStore("SitoolsJDBCStore", dsSecurity,
            appContextAuthorization);

        SitoolsStore<Role> storeRole = new RoleStoreXML(new File(settings.getStoreDIR(Consts.APP_ROLES_STORE_DIR)),
            appContextAuthorization);

        // Realm
        smr = new SitoolsMemoryRealm(storeUandG, storeRole, settings);

      }

      // attachment du sitoolsMemoryRealm
      this.component.getContext().setDefaultEnroler(smr.getEnroler());
      this.component.getContext().setDefaultVerifier(smr.getVerifier());

      // Set global AuthenticationRealm in SitoolsProperties.
      settings.setAuthenticationRealm(smr);
      this.component.getContext().getAttributes().put("APP_REALM", smr);

      if (storeAuthorization == null) {
        storeAuthorization = new AuthorizationStoreXML(new File(
            settings.getStoreDIR(Consts.APP_AUTHORIZATIONS_STORE_DIR)), appContextAuthorization);
      }

      appContextAuthorization.getAttributes().put(ContextAttributes.APP_STORE, storeAuthorization);

      // Application
      AuthorizationApplication appAuthorization = new AuthorizationApplication(appContextAuthorization);

      // Attachement
      component.getInternalRouter().attach(settings.getString(Consts.APP_AUTHORIZATIONS_URL), appAuthorization);

      settings.setStores(new HashMap<String, Object>());

      // Context
      Context appContext = this.component.getContext().createChildContext();
      appContext.getAttributes().put(ContextAttributes.SETTINGS, settings);

      if (store == null) {
        File storeDirectory = new File(getTestRepository() + settings.getString(Consts.APP_TASK_STORE_DIR));
        cleanDirectory(storeDirectory);
        store = new TaskStoreXML(storeDirectory, appContext);
      }

      if (storeResource == null) {
        File storeDirectory = new File(getTestRepository() + settings.getString(Consts.APP_PLUGINS_RESOURCES_STORE_DIR));
        cleanDirectory(storeDirectory);
        storeResource = new ResourcePluginStoreXML(storeDirectory, appContext);
      }

      if (storeBlacklist == null) {
        File storeBlacklistDirectory = new File(getTestRepository()
            + settings.getString(Consts.APP_USER_BLACKLIST_STORE_DIR));
        cleanDirectory(storeBlacklistDirectory);
        storeBlacklist = new UserBlackListStoreXML(storeBlacklistDirectory, appContext);
      }

      settings.getStores().put(Consts.APP_STORE_USER_BLACKLIST, storeBlacklist);

      appContext.getAttributes().put(ContextAttributes.APP_STORE, store);
      appContext.getAttributes().put(Consts.APP_STORE_PLUGINS_RESOURCES, storeResource);
      appContext.getAttributes().put("APP_REALM", smr);

      final String identifier = "identifier";

      TaskApplication taskUser = new TaskApplication(appContext) {
        @Override
        public void sitoolsDescribe() {
          setCategory(Category.USER);
          this.setName("TaskApplication for user");
          this.setDescription("TaskApplication");
        }

        // On ajoute un authorizer spécifique pour qu'un user puisse avoir que ses taches.
        @Override
        public Restlet getSecure() {
          // GET, PUT and DELETE authorized for public tasks on this application
          // it means that public user can retrieve, edit or delete every public tasks, but only public tasks
          List<Method> methods = new ArrayList<Method>();
          methods.add(Method.GET);
          methods.add(Method.PUT);
          methods.add(Method.DELETE);
          methods.add(Method.OPTIONS);
          return addSecurity(this, identifier, methods);
        }
      };
      // create an authorization on the taskUser
      ResourceAuthorization author = new ResourceAuthorization();
      author.setId(taskUser.getId());
      author.setName(taskUser.getName());
      author.setUrl(taskUser.getAttachementRef());

      storeAuthorization.create(author);

      Restlet taskUserRestlet = taskUser.getSecure();
      this.component.getDefaultHost().attach(getAttachUrlUser(), taskUserRestlet);

      TaskApplication taskAdmin = new TaskApplication(appContext) {
        @Override
        public void sitoolsDescribe() {
          setCategory(Category.ADMIN);
          this.setName("TaskApplication for admin");
          this.setDescription("TaskApplication");
        }
      };

      this.component.getDefaultHost().attach(getAttachUrlAdmin(), taskAdmin);
      // Attachement
      component.getInternalRouter().attach(settings.getString(Consts.APP_TASK_URL), taskAdmin);

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

  @Test
  public void testForUser() {
    docAPI.setActive(false);
    String userId = "admin";
    String password = "admin";
    assertNone(userId, password, false);
    // create a task
    Task task = createTask(userId);
    // retrieve all tasks
    retrieveTasks(userId, 1, password, false);
    // retrieve a task
    retrieveTask(userId, task, password, false);
    // change its status
    changeTaskStatus(userId, task, TaskStatus.TASK_STATUS_CANCELED_RUNNING, password, false);
    // set finish
    setTaskFinish(userId, task.getTaskId(), password, false);

    Task task2 = createTask(userId);
    retrieveTasks(userId, 2, password, false);
    Task task3 = createTask(userId + "_test");
    retrieveTasks(userId, 2, password, false);

    deleteTaskForbiden(userId, task3, password, false);
    retrieveTasks(userId, 2, password, false);
    deleteTask(userId, task2.getTaskId(), password, false);
    retrieveTasks(userId, 1, password, false);

    cleanTasks(userId, password, false);
    retrieveTasks(userId, 0, password, false);

    // On supprime à la main
    TaskManager.getInstance().deleteTask(task3.getTaskId(), true);

    assertNone(userId, password, false);

  }

  @Test
  public void testForUserPublic() {
    docAPI.setActive(false);
    String userId = "public";
    String password = null;
    assertNone(userId, password, true);
    // create a task
    Task task = createTask(userId);
    // retrieve all tasks
    retrieveTasks(userId, 1, password, true);
    // retrieve a task
    retrieveTask(userId, task, password, true);
    // change its status
    changeTaskStatus(userId, task, TaskStatus.TASK_STATUS_CANCELED_RUNNING, password, true);
    // set finish
    setTaskFinish(userId, task.getTaskId(), password, true);

    Task task2 = createTask(userId);
    retrieveTasks(userId, 2, password, true);
    Task task3 = createTask(userId + "_test");
    retrieveTasks(userId, 2, password, true);

    deleteTaskForbiden(userId, task3, password, true);
    retrieveTasks(userId, 2, password, true);
    deleteTask(userId, task2.getTaskId(), password, true);
    retrieveTasks(userId, 1, password, true);

    cleanTasks(userId, password, true);
    retrieveTasks(userId, 0, password, true);

    // On supprime à la main
    TaskManager.getInstance().deleteTask(task3.getTaskId(), true);

    assertNone(userId, password, true);

  }

  @Test
  public void testForAdminWithAPI() {
    docAPI.setActive(true);
    docAPI.appendChapter("Tasks management");
    String userId = "admin";
    // create a task
    Task task = createTask(userId);
    docAPI.appendSubChapter("Get list of tasks", "list");
    retrieveTasks(null, 1, null, false);
    docAPI.appendSubChapter("Get a task", "retrieve");
    retrieveTask(null, task, null, false);

    // change its status
    docAPI.appendSubChapter("Update a task", "update");
    changeTaskStatus(null, task, TaskStatus.TASK_STATUS_CANCELED_RUNNING, null, false);
    // set finish
    docAPI.appendSubChapter("Set task as finish", "finish");
    setTaskFinish(null, task.getTaskId(), null, false);

    docAPI.appendSubChapter("Delete a task", "delete");
    deleteTask(null, task.getTaskId(), null, false);
    docAPI.appendSubChapter("Clean all tasks", "clean");
    cleanTasks(null, null, false);

    docAPI.close();

  }

  @Test
  public void testForAdmin() {
    docAPI.setActive(false);
    String userId = "admin";
    assertNone(null, null, false);
    // create a task
    Task task = createTask(userId);
    retrieveTasks(null, 1, null, false);
    retrieveTask(null, task, null, false);

    // change its status
    changeTaskStatus(null, task, TaskStatus.TASK_STATUS_CANCELED_RUNNING, null, false);
    // set finish
    setTaskFinish(null, task.getTaskId(), null, false);

    Task task2 = createTask(userId + "_test");
    retrieveTasks(null, 2, null, false);

    deleteTask(null, task2.getTaskId(), null, false);
    retrieveTasks(null, 1, null, false);

    cleanTasks(null, null, false);

    assertNone(null, null, false);

    // create task the rest way

  }

  @Test
  public void testForAdminRestWay() {
    docAPI.setActive(false);
    assertNone(null, null, false);

    TaskModel taskModel = createTaskModelRestWay(null, null, false, Status.SUCCESS_OK);
    retrieveTasks(null, 1, null, false);
    // On supprime à la main
    deleteTask(null, taskModel.getId(), null, false);

    assertNone(null, null, false);
  }

  /**
   * Allow a specific user to create a task
   */
  @Test
  public void testForUserRestWay() {
    docAPI.setActive(false);
    String userId = "admin";
    assertNone(null, null, false);

    TaskModel taskModel = createTaskModelRestWay(userId, userAdminPwd, false, Status.SUCCESS_OK);
    retrieveTasks(null, 1, null, false);
    // On supprime à la main
    deleteTask(null, taskModel.getId(), null, false);

    assertNone(null, null, false);
  }

  /**
   * Does not allow public user to create a task
   */
  @Test
  public void testForPublicRestWay() {
    docAPI.setActive(false);
    String userId = "public";
    assertNone(userId, null, true);

    createTaskModelRestWay(userId, null, true, Status.CLIENT_ERROR_FORBIDDEN);
  }

  /**
   * Assert that the server returns no tasks
   * 
   * @param userId
   *          the userId
   * @param asPublic
   *          TODO
   */
  private void assertNone(String userId, String password, boolean asPublic) {
    String url = getUrl(userId);
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template;
      if (userId != null) {
        parameters.put("identifier", "dataset identifier");
        template = getBaseUrlUser();
      }
      else {
        template = getBaseUrlAdmin();
      }
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      if (userId == null && password == null) {
        userId = userAdminId;
        password = userAdminPwd;
      }
      if (!asPublic) {
        ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userId, password);
        cr.setChallengeResponse(chal);
      }
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, TaskModel.class, true);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(0), response.getTotal());
      RIAPUtils.exhaust(result);
    }
  }

  private void retrieveTasks(String userId, int expectedNumber, String password, boolean asPublic) {
    String url = getUrl(userId);
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template;
      if (userId != null) {
        parameters.put("identifier", "dataset identifier");
        template = getBaseUrlUser();
      }
      else {
        template = getBaseUrlAdmin();
      }
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      if (userId == null && password == null) {
        userId = userAdminId;
        password = userAdminPwd;
      }
      if (!asPublic) {
        ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userId, password);
        cr.setChallengeResponse(chal);
      }
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, TaskModel.class, true);
      assertTrue(response.getSuccess());
      assertEquals(new Integer(expectedNumber), response.getTotal());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Retrieve a Task
   * 
   * @param userId
   *          the userId
   * @param task
   *          the task
   * @param asPublic
   *          TODO
   */
  private void retrieveTask(String userId, Task task, String password, boolean asPublic) {
    String url = getUrl(userId) + "/" + task.getTaskId();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template;
      if (userId != null) {
        parameters.put("identifier", "dataset identifier");
        template = getBaseUrlUser();
      }
      else {
        template = getBaseUrlAdmin();
      }
      parameters.put("taskId", "The task identifier");
      retrieveDocAPI(url, "", parameters, template + "/%taskId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      if (userId == null && password == null) {
        userId = userAdminId;
        password = userAdminPwd;
      }
      if (!asPublic) {
        ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userId, password);
        cr.setChallengeResponse(chal);
      }
      Representation result = cr.get(getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, TaskModel.class);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
      TaskModel taskModelOut = (TaskModel) response.getItem();
      TaskModel taskModelIn = task.getTaskModel();
      assertEquals(taskModelIn.getId(), taskModelOut.getId());
      RIAPUtils.exhaust(result);
    }
  }

  /**
   * Change the status of the task
   * 
   * @param userId
   *          the userId
   * @param taskStatus
   *          the status to set
   * @param asPublic
   *          TODO
   * @param taskId
   *          the if of the task
   */
  private void changeTaskStatus(String userId, Task task, TaskStatus taskStatus, String password, boolean asPublic) {
    String url = getUrl(userId) + "/" + task.getTaskId();
    task.getTaskModel().setStatus(taskStatus);
    Representation rep = getRepresentation(task.getTaskModel(), getMediaTest());
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template;
      if (userId != null) {
        parameters.put("identifier", "dataset identifier");
        template = getBaseUrlUser();
      }
      else {
        template = getBaseUrlAdmin();
      }
      parameters.put("taskId", "The task identifier");
      putDocAPI(url, "", rep, parameters, template + "/%taskId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      if (userId == null && password == null) {
        userId = userAdminId;
        password = userAdminPwd;
      }
      if (!asPublic) {
        ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userId, password);
        cr.setChallengeResponse(chal);
      }
      Representation result = cr.put(rep, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, TaskModel.class);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
      TaskModel taskModelOut = (TaskModel) response.getItem();
      assertNotNull(taskModelOut.getStatus());
      assertEquals(taskStatus, taskModelOut.getStatus());
      RIAPUtils.exhaust(result);
    }

  }

  private void setTaskFinish(String userId, String taskId, String password, boolean asPublic) {
    String url = getUrl(userId) + "/" + taskId + "?action=finish";
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template;
      if (userId != null) {
        parameters.put("identifier", "dataset identifier");
        template = getBaseUrlUser();
      }
      else {
        template = getBaseUrlAdmin();
      }
      parameters.put("taskId", "The task identifier");
      parameters.put("action", "The action to perform (finish is the only option)");
      putDocAPI(url, "", new StringRepresentation(""), parameters, template + "/%taskId%?action=finish");
    }
    else {
      ClientResource cr = new ClientResource(url);
      if (userId == null && password == null) {
        userId = userAdminId;
        password = userAdminPwd;
      }
      if (!asPublic) {
        ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userId, password);
        cr.setChallengeResponse(chal);
      }
      Representation result = cr.put(null, getMediaTest());
      assertNotNull(result);
      assertTrue(cr.getStatus().isSuccess());
      Response response = getResponse(getMediaTest(), result, TaskModel.class);
      assertTrue(response.getSuccess());
      assertNotNull(response.getItem());
      TaskModel taskModelOut = (TaskModel) response.getItem();
      assertNotNull(taskModelOut.getStatus());
      assertEquals(TaskStatus.TASK_STATUS_FINISHED, taskModelOut.getStatus());
      RIAPUtils.exhaust(result);
    }

  }

  /**
   * Delete the given task
   * 
   * @param userId
   *          the userId
   * @param task
   *          the task to delete
   * @param asPublic
   *          TODO
   */
  private void deleteTask(String userId, String taskId, String password, boolean asPublic) {
    String url = getUrl(userId) + "/" + taskId;
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template;
      if (userId != null) {
        parameters.put("identifier", "dataset identifier");
        template = getBaseUrlUser();
      }
      else {
        template = getBaseUrlAdmin();
      }
      parameters.put("taskId", "The task identifier");
      deleteDocAPI(url, "", parameters, template + "/%taskId%");
    }
    else {
      ClientResource cr = new ClientResource(url);
      if (userId == null && password == null) {
        userId = userAdminId;
        password = userAdminPwd;
      }
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
  }

  /**
   * Clean all the tasks
   * 
   * @param userId
   *          the userId
   * @param asPublic
   *          TODO
   * 
   */
  private void cleanTasks(String userId, String password, boolean asPublic) {
    String url = getUrl(userId);
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template;
      if (userId != null) {
        parameters.put("identifier", "dataset identifier");
        template = getBaseUrlUser();
      }
      else {
        template = getBaseUrlAdmin();
      }
      deleteDocAPI(url, "", parameters, template);
    }
    else {
      ClientResource cr = new ClientResource(url);
      if (userId == null && password == null) {
        userId = userAdminId;
        password = userAdminPwd;
      }
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
  }

  /**
   * Delete the given task
   * 
   * @param userId
   *          the userId
   * @param task
   *          the task to delete
   * @param asPublic
   *          TODO
   */
  private void deleteTaskForbiden(String userId, Task task, String password, boolean asPublic) {
    String url = getUrl(userId) + "/" + task.getTaskId();
    if (docAPI.isActive()) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      String template;
      if (userId != null) {
        parameters.put("identifier", "dataset identifier");
        template = getBaseUrlUser();
      }
      else {
        template = getBaseUrlAdmin();
      }
      retrieveDocAPI(url, "", parameters, template);
    }
    else {
      final Client client = new Client(Protocol.HTTP);
      Request request = new Request(Method.GET, url);
      if (userId == null && password == null) {
        userId = userAdminId;
        password = userAdminPwd;
      }
      if (!asPublic) {
        ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userId, password);
        request.setChallengeResponse(chal);
      }
      org.restlet.Response response = client.handle(request);
      try {
        assertNotNull(response);
        assertTrue(response.getStatus().isError());
        assertEquals(Status.CLIENT_ERROR_FORBIDDEN, response.getStatus());
      }
      finally {
        RIAPUtils.exhaust(response);
      }
    }
  }

  /**
   * Get the url
   * 
   * @param userId
   *          the user id
   * @return the url
   */
  private String getUrl(String userId) {
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
   * Create a new Task created by the admin user
   * 
   * @return the new Task
   */
  private Task createTask(String username) {
    // Context context = getContext().createChildContext();
    Context context = new Context();
    context.getAttributes().put(TaskUtils.LOG_FOLDER,
        SitoolsSettings.getInstance().getStoreDIR(Consts.APP_RESOURCE_LOGS_DIR));
    // Create a task
    Request request = new Request();
    org.restlet.Response response = new org.restlet.Response(request);

    TaskResourceModel model = new TaskResourceModel();
    model.setName("taskResourceForTest");
    model.setResourceClassName("fr.cnes.sitools.resources.tasks.test.MyTaskResourceFacade");
    model.setResourceImplClassName("fr.cnes.sitools.resources.tasks.test.MyTaskResourceImpl");

    User user = new User(username, "admin");

    // CREATE
    Task task = TaskManager.getInstance().createTask(context, request, response, model, user, Level.INFO, true);
    assertNotNull(task);
    return task;

  }

  /**
   * Create a new Task created by the admin user
   * 
   * @return the new Task
   */
  private TaskModel createTaskModelRestWay(String userId, String password, boolean asPublic, Status expectedStatus) {

    TaskModel model = new TaskModel();
    model.setName("taskResourceForTest");
    model.setDescription("A task created the rest way");
    model.setUserId(userId);

    Representation rep = getRepresentation(model, getMediaTest());
    String url = getUrl(userId);
    final Client client = new Client(Protocol.HTTP);
    Request request = new Request(Method.POST, url);
    request.setEntity(rep);
    if (userId == null && password == null) {
      userId = userAdminId;
      password = userAdminPwd;
    }
    if (!asPublic) {
      ChallengeResponse chal = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, userId, password);
      request.setChallengeResponse(chal);
    }
    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(getMediaTest()));
    request.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response responseRestlet = client.handle(request);
    try {
      assertNotNull(responseRestlet);
      if (expectedStatus.isError()) {
        assertTrue(responseRestlet.getStatus().isError());
        assertEquals(expectedStatus, responseRestlet.getStatus());
      }
      else {

        Representation result = responseRestlet.getEntity();
        assertNotNull(result);
        assertTrue(responseRestlet.getStatus().isSuccess());
        Response response = getResponse(getMediaTest(), result, TaskModel.class);
        assertTrue(response.getSuccess());
        TaskModel task = (TaskModel) response.getItem();
        assertEquals(model.getDescription(), task.getDescription());
        RIAPUtils.exhaust(result);
        return task;
      }
    }
    finally {
      RIAPUtils.exhaust(responseRestlet);
    }
    return null;

  }

  @Test
  public void createTaskRIAP() {

    String userId = "admin";
    assertNone(null, null, false);

    TaskModel taskModel = new TaskModel();
    taskModel.setName("A task");
    taskModel.setDescription("A task created the rest way with RIAP");
    taskModel.setUserId(userId);

    SitoolsSettings settings = SitoolsSettings.getInstance();

    String url = settings.getString(Consts.APP_TASK_URL);

    TaskModel taskModelPersist = RIAPUtils.persistObject(taskModel, url, this.component.getContext());

    retrieveTasks(null, 1, null, false);
    // On supprime à la main
    deleteTask(null, taskModelPersist.getId(), null, false);

    assertNone(null, null, false);
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
      xstream.alias("TaskModel", TaskModel.class);

      if (isArray) {
        if (media.equals(MediaType.APPLICATION_JSON)) {
          xstream.addImplicitCollection(Response.class, "data", dataClass);
        }
      }
      else {
        xstream.alias("item", dataClass);
        xstream.alias("item", Object.class, dataClass);

        if (media.equals(MediaType.APPLICATION_JSON)) {
        }

        if (dataClass == TaskModel.class) {
          xstream.aliasField("TaskModel", Response.class, "item");
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
      return new JacksonRepresentation<TaskModel>(item);
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
    // Parce que les annotations ne sont apparemment prises en compte
    xstream.omitField(Response.class, "itemName");
    xstream.omitField(Response.class, "itemClass");

  }
}
