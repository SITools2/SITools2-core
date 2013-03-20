package fr.cnes.sitools.common.task;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;

/**
 * Task to dispatch a pre-build request in asynchronous mode. <b/>
 * Requests must have all ChallengeResponse set. <b/>
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
      e.printStackTrace();
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
