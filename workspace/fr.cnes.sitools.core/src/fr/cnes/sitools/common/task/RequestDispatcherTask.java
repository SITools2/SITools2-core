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
package fr.cnes.sitools.common.task;

import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;

/**
 * Task to dispatch a pre-build request in asynchronous mode. <b/> Requests must have all ChallengeResponse set. <b/>
 * 
 * Initially created to meet the needs of mailing
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class RequestDispatcherTask implements Runnable {

  /**
   * Pre-built request
   */
  private Request request = null;
  /**
   * Context of parent request to reach ClientDispatcher
   */
  private Context context = null;

  /**
   * Sitools response of request server mail Could be logged or processed by a task manager ...
   */
  private fr.cnes.sitools.common.model.Response taskResponse = null;

  /**
   * Constructor
   * 
   * @param context
   *          Context parent request to reach ClientDispatcher
   * @param taskRequest
   *          pre-built Request
   */
  public RequestDispatcherTask(Context context, Request taskRequest) {
    this.context = context;
    this.request = taskRequest;
  }

  @Override
  public void run() {

    try {
      Response response = getContext().getClientDispatcher().handle(request);

      if (response.getStatus().isSuccess()) {
        taskResponse = new fr.cnes.sitools.common.model.Response(true, "task.success");
      }
      else {
        taskResponse = new fr.cnes.sitools.common.model.Response(false, "task.failed");
      }
    }
    catch (Exception e) {
      taskResponse = new fr.cnes.sitools.common.model.Response(false, "task.failed.exception");
      context.getLogger().log(Level.INFO, null, e);
    }
  }

  /**
   * Get the request to dispatch
   * 
   * @return Request
   */
  public Request getRequest() {
    return request;
  }

  /**
   * Get the Restlet context to reach ClientDispatcher
   * 
   * @return Context
   */
  public Context getContext() {
    return context;
  }

  /**
   * Final Response.
   * 
   * @return Sitools Response when task finished
   */
  public fr.cnes.sitools.common.model.Response getTaskResponse() {
    return taskResponse;
  }

}
