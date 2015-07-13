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

import java.util.logging.Level;

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.tasks.business.Task;
import fr.cnes.sitools.tasks.business.TaskManager;
import fr.cnes.sitools.tasks.model.TaskModel;
import fr.cnes.sitools.tasks.model.TaskStatus;

/**
 * Resource to manage single Task
 * 
 * 
 * @author m.gond
 */
public class TaskResource extends AbstractTaskResource {

  /** The if of the task */
  private String taskId;

  @Override
  public void sitoolsDescribe() {
    setName("TaskResource");
    setDescription("Handle a particular task");
  }

  @Override
  public void doInit() {
    super.doInit();
    this.setNegotiated(false);
    taskId = (String) this.getRequest().getAttributes().get("taskId");   

  }

  /**
   * Get a Task, return it with the specified variant
   * 
   * @param variant
   *          the Variant needed
   * @return a Representation representing the Task with the specified Variant
   */
  @Get
  public Representation get(Variant variant) {
    Representation repr = null;
    // Retrieve the Task
    Task task = TaskManager.getInstance().getById(taskId);
    if (task == null) {
      Response response = new Response(false, "NOT_FOUND");
      repr = getRepresentation(response, variant);
    }
    else if (getUserId() != null && !getUserId().equals(task.getUserId())) {
      getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
    }
    else {

      Response response = new Response(true, task.getTaskModel().clone(), TaskModel.class, "TaskModel");
      repr = getRepresentation(response, variant);
    }
    return repr;
  }

  /**
   * Describe the GET method.
   * 
   * @param info
   *          the WADL method information
   */
  public void describeGet(MethodInfo info) {
    info.setDocumentation("Method to retrieve the representation of a task.");
    info.setIdentifier("retrieve_task_representation_result");
    addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("taskId", true, "xs:string", ParameterStyle.TEMPLATE,
        "identifier of the task");
    info.getRequest().getParameters().add(param);
    addStandardResponseInfo(info);
  }

  /**
   * Modify a task, it can only be set to finish or modify the whole task
   * 
   * @param representation
   *          The input Task
   * @param variant
   *          the Variant needed
   * @return a Representation representing the modified Task with the specified Variant
   */
  @Put
  public Representation put(Representation representation, Variant variant) {
    Representation repr = null;
    try {
      // Retrieve the SVA
      Task task = TaskManager.getInstance().getById(taskId);
      if (task == null) {
        Response response = new Response(false, "NOT_FOUND");
        repr = getRepresentation(response, variant);
      }
      else if (getUserId() != null && !getUserId().equals(task.getUserId())) {
        getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
      }
      else {
        if (this.getReference().toString().endsWith("finish")) {
          task.setTaskStatus(TaskStatus.TASK_STATUS_FINISHED);
          Response response = new Response(true, task.getTaskModel(), TaskModel.class, "TaskModel");
          repr = getRepresentation(response, variant);
        }
        else {
          TaskModel input = getTaskModelFromRepresentation(representation);
          task.setTaskModel(input);
          TaskManager.getInstance().updateTask(task);
          Response response = new Response(true, task.getTaskModel(), TaskModel.class, "TaskModel");
          repr = getRepresentation(response, variant);
        }
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, null, e);
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
    return repr;

  }

  @Override
  public void describePut(MethodInfo info) {
    info.setDocumentation("Method to modify a single Task sending its new representation or modify its status to finish");
    info.setIdentifier("update_task");
    addStandardPostOrPutRequestInfo(info);
    ParameterInfo pic = new ParameterInfo("taskId", true, "xs:string", ParameterStyle.TEMPLATE,
        "Identifier of the task");
    info.getRequest().getParameters().add(pic);
    pic = new ParameterInfo("action", true, "xs:string", ParameterStyle.TEMPLATE,
        "The action to perform if no entity sent (only possible value is finish)");
    pic.setDefaultValue("finish");
    info.getRequest().getParameters().add(pic);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }

  /**
   * Delete a Task
   * 
   * @param variant
   *          the Variant needed for the response
   * @return A response telling whether to Task has beed deleted or not
   */
  @Delete
  public Representation deleteTask(Variant variant) {
    // TODO 1 supprimer une task terminée (pas de gestion du thread)
    boolean ok = false;
    Representation repr = null;
    Response response;
    Task task = TaskManager.getInstance().getById(taskId);
    if (task == null) {
      response = new Response(false, "NOT_FOUND");
      repr = getRepresentation(response, variant);
    }
    else if (getUserId() != null && !getUserId().equals(task.getUserId())) {
      getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
    }
    else {
      ok = TaskManager.getInstance().deleteTask(this.taskId, true);
      if (ok) {
        response = new Response(true, "label.task.deleted");
        repr = getRepresentation(response, variant);
      }
      else {
        response = new Response(false, "label.task.notDeleted");
        repr = getRepresentation(response, variant);
      }
    }
    return repr;
    // TODO 2 arreter le thread ? ...
  }

  @Override
  public void describeDelete(MethodInfo info) {
    info.setDocumentation("Method to delete a task by ID");
    info.setIdentifier("delete_dataset");
    addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("taskId", true, "xs:string", ParameterStyle.TEMPLATE,
        "identifier of the task");
    info.getRequest().getParameters().add(param);
    addStandardResponseInfo(info);
    addStandardInternalServerErrorInfo(info);
  }
}
