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

import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.tasks.business.Task;
import fr.cnes.sitools.tasks.business.TaskManager;

/**
 * Resource to return the result of a Task
 * 
 * 
 * @author m.gond
 */
public class TaskRepresentResource extends AbstractTaskResource {
  /** The if of the task */
  private String taskId;

  @Override
  public void sitoolsDescribe() {
    setName("TaskRepresentResource");
    setDescription("Handle a the result of task");
  }

  @Override
  public void doInit() {
    super.doInit();
    taskId = (String) this.getRequest().getAttributes().get("taskId");
  }

  /**
   * GET the list of tasks assigned to a SVA
   * 
   * @param variant
   *          the variant sent by the request
   * @return a representation of all tasks treated by the SVA
   */
  @Get
  public Representation getResult(Variant variant) {
    Representation repr;
    Task task = TaskManager.getInstance().getById(this.taskId);
    if (task == null) {
      Response response = new Response(false, "NOT_FOUND");
      repr = getRepresentation(response, variant);
    }
    else {
      if (task.getStatus() != null) {
        throw new ResourceException(task.getStatus());
      }
      else if ((task.getResult() != null) && task.getResult().isAvailable()) {
        repr = task.getResult();
      }
      else {
        throw new ResourceException(Status.SUCCESS_NO_CONTENT);
      }

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
    info.setDocumentation("Method to retrieve the representation of the task result.");
    info.setIdentifier("retrieve_task_representation_result");
    addStandardGetRequestInfo(info);
    ParameterInfo param = new ParameterInfo("taskId", true, "xs:string", ParameterStyle.TEMPLATE,
        "identifier of the task");
    info.getRequest().getParameters().add(param);
    addStandardResponseInfo(info);
  }

}
