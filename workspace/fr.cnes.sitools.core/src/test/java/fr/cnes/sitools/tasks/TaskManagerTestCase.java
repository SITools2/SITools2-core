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
package fr.cnes.sitools.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Protocol;
import org.restlet.security.User;

import fr.cnes.sitools.AbstractSitoolsTestCase;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsParameterizedApplication;
import fr.cnes.sitools.plugins.resources.ResourcePluginStoreInterface;
import fr.cnes.sitools.plugins.resources.ResourcePluginStoreXMLMap;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.tasks.business.Task;
import fr.cnes.sitools.tasks.business.TaskManager;
import fr.cnes.sitools.tasks.model.TaskModel;
import fr.cnes.sitools.tasks.model.TaskResourceModel;
import fr.cnes.sitools.tasks.model.TaskStatus;

/**
 * Créer une nouvelle application Configurer par le code une nouvelle resource pour l'application
 * 
 * SitoolsParameterizedApplication a laquelle rattacher une SitoolsParameterizedResource
 * 
 * Un Composant Server
 * 
 * Store Resource et Task
 * 
 * Client invoque methode sur l'url de la ressource
 * 
 * Une resource configurée sur l'application
 * 
 * @author AKKA Technologies
 */
public class TaskManagerTestCase extends AbstractSitoolsTestCase {

  public static final String RESOURCE_FACADE_NAME = "fr.cnes.sitools.mock.resources.tasks.MyTaskResourceFacade";
  public static final String TASK_RESOURCE_IMPL_NAME = "fr.cnes.sitools.mock.resources.tasks.MyTaskResourceImpl";
  /**
   * static xml store instance for the test
   */
  private static TaskStoreInterface storeTask = null;

  /**
   * static xml store instance for the test
   */
  private static ResourcePluginStoreInterface storeResource = null;

  /**
   * Restlet Component for server
   */
  private Component component = null;

  /**
   * Absolute path location for data set store files
   * 
   * @return path
   */
  protected String getStoreTaskResourceRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_TASK_STORE_DIR) + "/map";
  }

  /**
   * Absolute path location for data set store files
   * 
   * @return path
   */
  protected String getStoreTaskRepository() {
    return super.getTestRepository() + SitoolsSettings.getInstance().getString(Consts.APP_PLUGINS_RESOURCES_STORE_DIR)
        + "/map";
  }

  private SitoolsSettings settings = SitoolsSettings.getInstance();

  @Before
  @Override
  /**
   * Create component, store and application and start server  
   * @throws java.lang.Exception
   */
  public void setUp() throws Exception {

    if (this.component == null) {
      this.component = new Component();
      this.component.getServers().add(Protocol.HTTP, getTestPort());
      this.component.getClients().add(Protocol.HTTP);
      this.component.getClients().add(Protocol.FILE);
      this.component.getClients().add(Protocol.CLAP);
    }

    this.component.getContext().getAttributes().put(ContextAttributes.SETTINGS, settings);

    if (storeTask == null) {
      File storeDirectory = new File(getStoreTaskResourceRepository());
      storeDirectory.mkdirs();
      cleanDirectory(storeDirectory);
      cleanMapDirectories(storeDirectory);
      storeTask = new TaskStoreXMLMap(storeDirectory, component.getContext());
    }
    if (storeResource == null) {
      File storeDirectory = new File(getStoreTaskRepository());
      storeDirectory.mkdirs();
      cleanDirectory(storeDirectory);
      cleanMapDirectories(storeDirectory);
      storeResource = new ResourcePluginStoreXMLMap(storeDirectory, component.getContext());
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
  public void testCRUDTask() {
    assertEquals(0, storeTask.getList().size());
    // get the contextt
    Context context = component.getContext().createChildContext();
    context.getAttributes().put(Consts.APP_STORE_TASK, storeTask);
    context.getAttributes().put(Consts.APP_STORE_PLUGINS_RESOURCES, storeResource);
    context.getAttributes().put(TaskUtils.LOG_FOLDER, settings.getStoreDIR(Consts.APP_RESOURCE_LOGS_DIR));

    // create and intialise the TaskManager
    TaskManager manager = TaskManager.getInstance();
    manager.init(context);
    assertEquals(0, manager.getTasks().size());

    // Create a task
    Request request = new Request();
    Response response = new Response(request);

    TaskResourceModel model = new TaskResourceModel();
    model.setName("taskResourceForTest");
    model.setResourceClassName(RESOURCE_FACADE_NAME);
    model.setResourceImplClassName(TASK_RESOURCE_IMPL_NAME);
    model.setId("10000");

    User user = new User("admin", "admin");

    // CREATE
    Task task = manager.createTask(context, request, response, model, user, Level.INFO, true);
    assertNotNull(task);
    assertNotNull(task.getTaskModel());

    // UPDATE AND RETRIEVE MODEL
    task.setTaskStatus(TaskStatus.TASK_STATUS_CANCELED);
    manager.updateTask(task);

    TaskModel taskModel = storeTask.retrieve(task.getTaskModel().getId());
    assertNotNull(taskModel);

    assertEquals(TaskStatus.TASK_STATUS_CANCELED, taskModel.getStatus());

    // RETRIEVE TASK
    Task taskFromManager = manager.getById(task.getTaskId());

    assertNotNull(taskFromManager);
    assertEquals(task.getTaskStatus(), taskFromManager.getTaskStatus());

    // Get the task by its ResourceModel
    List<Task> tasks = manager.getByResourceModel(taskFromManager.getResourceModel().getId());
    assertNotNull(tasks);
    assertEquals(1, tasks.size());

    // Get all the tasks
    tasks = manager.getTasks();
    assertNotNull(tasks);
    assertEquals(1, tasks.size());

    // DELETE TASK
    boolean ok = manager.deleteTask(task.getTaskId(), true);
    assertTrue(ok);
    assertEquals(0, storeTask.getArray().length);
    assertEquals(0, manager.getTasks().size());

  }

  @Test
  public void test() throws InterruptedException {
    // get the contextt
    Context context = component.getContext().createChildContext();
    context.getAttributes().put(Consts.APP_STORE_TASK, storeTask);
    context.getAttributes().put(Consts.APP_STORE_PLUGINS_RESOURCES, storeResource);
    context.getAttributes().put(TaskUtils.LOG_FOLDER, settings.getStoreDIR(Consts.APP_RESOURCE_LOGS_DIR));

    // create and intialise the TaskManager
    TaskManager manager = TaskManager.getInstance();
    manager.init(context);

    // create a resource model
    TaskResourceModel model = new TaskResourceModel();
    model.setName("taskResourceForTest");
    model.setResourceClassName(RESOURCE_FACADE_NAME);
    model.setResourceImplClassName(TASK_RESOURCE_IMPL_NAME);
    model = (TaskResourceModel) storeResource.create(model);

    // Create a task
    Request request = new Request();
    Response response = new Response(request);

    User user = new User("admin", "admin");

    Task task = manager.createTask(context, request, response, model, user, Level.INFO, false);
    assertNotNull(task);

    // execute the task
    manager.runSynchrone(task);

    assertNotNull(task.getTaskStatus());
    assertEquals(TaskStatus.TASK_STATUS_FAILURE, task.getTaskStatus());

    Context appContext = component.getContext().createChildContext();
    appContext.getAttributes().put(ContextAttributes.SETTINGS, settings);

    // add an application to the context
    SitoolsParameterizedApplication application = new SitoolsParameterizedApplication(appContext) {
      @Override
      public void sitoolsDescribe() {
        // TODO Auto-generated method stub
        setName("application for test");
        setDescription("application for test");
      }
    };

    context.getAttributes().put("ParentApplication", application);

    // execute the task
    manager.runAsynchrone(task, context);

    assertNotNull(task.getTaskStatus());
    int nbTentatives = 4;
    int tentatives = 0;
    do {
      Thread.sleep(500);
      tentatives++;
    } while (task.getTaskStatus().equals(TaskStatus.TASK_STATUS_RUNNING) || tentatives < nbTentatives);

    assertEquals(TaskStatus.TASK_STATUS_FAILURE, task.getTaskStatus());

    boolean ok = manager.deleteTask(task.getTaskId(), false);
    assertTrue(ok);
    assertEquals(0, storeTask.getArray().length);
    assertEquals(0, manager.getTasks().size());

    // DELETE THE RESOURCE
    assertTrue(storeResource.delete(model.getId()));
  }
}
