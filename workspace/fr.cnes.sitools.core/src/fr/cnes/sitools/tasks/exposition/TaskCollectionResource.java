/*******************************************************************************
 * Copyright 2011, 2012 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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

import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;

import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.tasks.business.Task;
import fr.cnes.sitools.tasks.business.TaskManager;
import fr.cnes.sitools.tasks.model.TaskModel;

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
    List<TaskModel> list = this.getListTasks(getUserId());
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
}
