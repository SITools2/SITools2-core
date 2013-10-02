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
package fr.cnes.sitools.tasks.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.User;

import fr.cnes.sitools.common.application.SitoolsParameterizedApplication;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.plugins.resources.ResourcePluginStoreXML;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.tasks.TaskStoreXML;
import fr.cnes.sitools.tasks.TaskUtils;
import fr.cnes.sitools.tasks.model.TaskModel;
import fr.cnes.sitools.tasks.model.TaskRunTypeAdministration;
import fr.cnes.sitools.tasks.model.TaskStatus;

/**
 * Singleton class for managing Tasks
 * 
 * @author m.gond (AKKA Technologies)
 * 
 */
public final class TaskManager {

  /** singleton instance */
  private static TaskManager instance = null;

  /** if the TaskManager as been initialized */
  private static boolean isInit = false;

  /** Map of Task instances */
  private Map<String, Task> tasksMap;

  /** Store for Task models */
  private TaskStoreXML storeTaskModel;

  /** Store for ResourceModel */
  private ResourcePluginStoreXML storeResourceModel;

  // /** The current context */
  // private Context context;

  /**
   * private constructor for singleton
   */
  private TaskManager() {
    tasksMap = new ConcurrentHashMap<String, Task>();
  }

  /**
   * Gets the instance value
   * 
   * @return the instance
   */
  public static synchronized TaskManager getInstance() {
    if (instance == null) {
      instance = new TaskManager();
    }
    return instance;
  }

  /**
   * Initialize the Manager
   * 
   * @param context
   *          the Context
   */
  public void init(Context context) {
    if (!isInit) {
      // this.context = context;
      storeTaskModel = (TaskStoreXML) context.getAttributes().get(Consts.APP_STORE_TASK);

      storeResourceModel = (ResourcePluginStoreXML) context.getAttributes().get(Consts.APP_STORE_PLUGINS_RESOURCES);
      // load existing Tasks, put the status to CANCELED if it was runnning
      // or
      // pending
      Collection<TaskModel> coll = storeTaskModel.getList();
      for (Iterator<TaskModel> iterator = coll.iterator(); iterator.hasNext();) {

        TaskModel taskModel = iterator.next();
        // context.getLogger().log(Level.INFO, "LET's add a Task in the task manager : " + taskModel.getName());

        if (taskModel.getStatus().equals(TaskStatus.TASK_STATUS_PENDING)) {
          taskModel.setStatus(TaskStatus.TASK_STATUS_CANCELED_PENDING);
          // persist the task
          this.storeTaskModel.update(taskModel);
        }
        else if (taskModel.getStatus().equals(TaskStatus.TASK_STATUS_RUNNING)) {
          taskModel.setStatus(TaskStatus.TASK_STATUS_CANCELED_RUNNING);
          // persist the task
          this.storeTaskModel.update(taskModel);
        }

        // Fill in the tasksMap
        Task task = new Task(taskModel);
        task.setPersist(true);
        // sets the ResourceModel associated to that task
        ResourceModel resModel = (ResourceModel) this.storeResourceModel.retrieve(taskModel.getModelId());
        // if (resModel instanceof TaskResourceModel) {
        task.setResourceModel(resModel);
        this.tasksMap.put(task.getTaskId(), task);
        // }
        // else {
        // context.getLogger().warning("TaskManager.init : TaskResourceModel expected");
        // }

      }
      isInit = true;
    }

  }

  /**
   * Create a new Task
   * 
   * @param context
   *          the Restlet Context of the Task
   * @param request
   *          the request object
   * @param response
   *          the response object
   * @param model
   *          the Resource model
   * @param user
   *          the User
   * @param loggerLvl
   *          the level of the logger
   * @param persist
   *          if the task needs to be persisted or not
   * @return a new Task
   */
  public Task createTask(Context context, Request request, Response response, ResourceModel model, User user,
      Level loggerLvl, boolean persist) {
    String taskid = UUID.randomUUID().toString();
    Task task = new Task(context, request, response, taskid, model, user, loggerLvl);
    task.setPersist(persist);
    this.tasksMap.put(taskid, task);
    // persist the task
    if (persist) {
      this.storeTaskModel.create(task.getTaskModel());
    }

    return task;
  }

  /**
   * Run the given Task synchronously
   * 
   * @param task
   *          the Task
   */
  public void runSynchrone(Task task) {
    task.setRunType(TaskRunTypeAdministration.TASK_FORCE_RUN_SYNC);
    task.runSynchrone();

  }

  /**
   * Run the given Task synchronously
   * 
   * @param task
   *          the Task
   * @param context
   *          the context
   * 
   */
  public void runAsynchrone(Task task, Context context) {
    task.setRunType(TaskRunTypeAdministration.TASK_FORCE_RUN_ASYNC);

    SitoolsParameterizedApplication application = (SitoolsParameterizedApplication) context.getAttributes().get(
        TaskUtils.PARENT_APPLICATION);

    application.getTaskService().execute(task);

    // Future<?> future = application.getTaskService().submit(task);

    // Task.setFuture(future);
  }

  /**
   * Gets all the tasks with the same resourceModel specified by the given resourceModelId
   * 
   * @param resourceModelId
   *          The id of the resourceModel
   * @return ArrayList<Task>
   */
  public List<Task> getByResourceModel(String resourceModelId) {
    ArrayList<Task> tasks = new ArrayList<Task>();
    if (resourceModelId != null) {
      Collection<Task> tasksList = this.tasksMap.values();
      for (Iterator<Task> iterator = tasksList.iterator(); iterator.hasNext();) {
        Task task = iterator.next();
        if (task.getResourceModel().getId().equals(resourceModelId)) {
          tasks.add(task);
        }
      }
    }
    return tasks;
  }

  /**
   * Get a Task for the specified taskId
   * 
   * @param taskId
   *          The id
   * @return A Task corresponding to the following <code>taskId</code>
   */
  public Task getById(String taskId) {
    return this.tasksMap.get(taskId);
  }

  /**
   * Persist the Task
   * 
   * @param task
   *          The Task to persist
   */
  public void updateTask(Task task) {
    if (task.isPersist()) {
      this.storeTaskModel.update(task.getTaskModel());
    }
  }

  /**
   * Delete A Task and its model if deleteModel = true
   * 
   * @param taskId
   *          the id of the task
   * @param deleteModel
   *          if it has to delete the model as well
   * @return true if the task is deleted, false otherwise
   */
  public boolean deleteTask(String taskId, boolean deleteModel) {
    boolean ok = true;
    if (deleteModel) {
      ok = this.storeTaskModel.delete(taskId);
    }
    Task task = this.tasksMap.remove(taskId);
    return ok && task != null;
  }

  /**
   * Return the list of persisted tasks
   * 
   * @return the list of tasks persisted
   */
  public List<Task> getTasks() {
    return new ArrayList<Task>(this.tasksMap.values());
  }

  /**
   * Get the list of TaskModel according to filter and full list Used by the TaskCollectionResource, only an helpfull
   * method
   * 
   * @param filter
   *          query filters
   * @param result
   *          global results
   * @return the list of TaskModel to be returned in the page
   */
  public List<TaskModel> getTaskModelsPage(ResourceCollectionFilter filter, List<TaskModel> result) {
    return this.storeTaskModel.getPage(filter, result);
  }

  /**
   * getStore
   * 
   * @return
   */
  public TaskStoreXML getStore() {
    return storeTaskModel;
  }

}
