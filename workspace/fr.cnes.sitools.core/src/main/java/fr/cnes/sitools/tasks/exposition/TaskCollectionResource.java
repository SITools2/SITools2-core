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
package fr.cnes.sitools.tasks.exposition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.tasks.TaskUtils;
import fr.cnes.sitools.tasks.business.Task;
import fr.cnes.sitools.tasks.business.TaskManager;
import fr.cnes.sitools.tasks.model.TaskModel;
import fr.cnes.sitools.tasks.model.TaskResourceModel;

/**
 * Handle the list of tasks or only the list for a particular User
 * 
 * 
 * @author m.gond
 */
public class TaskCollectionResource extends AbstractTaskResource {

  @Override
  public void sitoolsDescribe() {
    setName("TaskCollectionResource");
    setDescription("Handle the list of tasks");
  }

  @Override
  public void doInit() {
    super.doInit();
    this.setNegotiated(false);
  }

  /**
   * Delete all tasks
   * 
   * @param variant
   *          the variant needed
   * @return a Response representation
   */
  @Delete
  public Representation cleanTasks(Variant variant) {
    Representation represent = null;
    Response response = null;

    List<TaskModel> list = this.getListTasks(getUserId());

    if (list != null) {
      for (Iterator<TaskModel> iterator = list.iterator(); iterator.hasNext();) {
        TaskModel taskModel = iterator.next();
        // delete it via the TaskManager to also delete the Task from the Map
        TaskManager.getInstance().deleteTask(taskModel.getId(), true);
      }
      response = new Response(true, "tasks deleted");
    }
    else {
      response = new Response(false, "to tasks to delete");
    }
    represent = getRepresentation(response, variant);
    return represent;
  }

  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete all tasks");
    info.setIdentifier("delete_tasks");
    addStandardGetRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

  /**
   * Get the list of tasks or the list of tasks for a particular User
   * 
   * @param variant
   *          the variant needed
   * @return a response containing the list of Tasks
   */
  @Get
  public Representation getTasks(Variant variant) {
    Representation represent = null;
    Response response = null;

    ResourceCollectionFilter filter = new ResourceCollectionFilter(this.getRequest());
    List<TaskModel> list = this.getListTasks(getUserId(), filter);
    if (list != null) {
      int total = list.size();
      Collection<TaskModel> taskModelsCollection = TaskManager.getInstance().getTaskModelsPage(filter, list);
      TaskModel[] taskModels = new TaskModel[taskModelsCollection.size()];
      taskModels = taskModelsCollection.toArray(taskModels);
      response = new Response(true, taskModels);
      response.setTotal(total);
    }
    represent = getRepresentation(response, variant);
    return represent;
  }

  @Override
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve a all tasks");
    info.setIdentifier("retrieve_tasks");
    addStandardGetRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
    addStandardResourceCollectionFilterInfo(info);
  }

  /**
   * Gets the list of all tasks if the userId is null, otherwise returns the list for the given user
   * 
   * @param userId
   *          the User identifier
   * @return the list of all tasks or the ones of the userId
   */
  private List<TaskModel> getListTasks(String userId) {
    List<Task> tasks = TaskManager.getInstance().getTasks();
    List<TaskModel> taskModels = new ArrayList<TaskModel>();
    for (Task task : tasks) {
      if (task.isPersist() && (userId == null || (userId != null && userId.equals(task.getUserId())))) {
        taskModels.add(task.getTaskModel());
      }
    }
    return taskModels;
  }

  /**
   * Get the list of task for a particular user and ResourceCollectionFilter
   * 
   * @param userId
   *          the user identifier
   * @param filter
   *          the filter
   * @return the list of task for a particular user and ResourceCollectionFilter
   */
  private List<TaskModel> getListTasks(String userId, ResourceCollectionFilter filter) {
    List<Task> tasks = TaskManager.getInstance().getTasks();
    List<TaskModel> taskModels = new ArrayList<TaskModel>();
    for (Task task : tasks) {
      if (task.isPersist() && (userId == null || (userId != null && userId.equals(task.getUserId())))) {
        taskModels.add(task.getTaskModel());
      }
    }

    TaskManager.getInstance().getStore().sort(taskModels, filter);

    return taskModels;
  }

  /**
   * Create a new DataSet
   * 
   * @param representation
   *          DataSet Representation
   * @param variant
   *          Variant user preferred MediaType
   * @return Representation
   */
  @Post
  public Representation newTaskModel(Representation representation, Variant variant) {
    if (representation == null) {
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "TASK_MODEL_REPRESENTATION_REQUIRED");
    }
    try {
      TaskModel taskModelInput = getTaskModelFromRepresentation(representation);

      Context context = getContext().createChildContext();
      context.getAttributes().put(TaskUtils.LOG_FOLDER,
          SitoolsSettings.getInstance().getStoreDIR(Consts.APP_RESOURCE_LOGS_DIR));
      // Create a task
      Request request = new Request();
      org.restlet.Response response = new org.restlet.Response(request);

      TaskResourceModel model = new TaskResourceModel();
      User user = getClientInfo().getUser();

      Task task = TaskManager.getInstance().createTask(context, request, response, model, user, Level.INFO, true);
      taskModelInput.setId(task.getTaskModel().getId());
      task.setTaskModel(taskModelInput);

      Response responseResult = new Response(true, task.getTaskModel(), TaskModel.class, "TaskModel");
      return getRepresentation(responseResult, variant);

    }
    catch (ResourceException e) {
      getLogger().log(Level.INFO, null, e);
      throw e;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
  }

  @Override
  public void describePost(MethodInfo info) {
    info.setDocumentation("Method to create a dataset");
    info.setIdentifier("create_dataset");
    addStandardPostOrPutRequestInfo(info);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }
}
