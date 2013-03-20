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
package fr.cnes.sitools.tasks.business;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.engine.util.DateUtils;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.security.User;

import fr.cnes.sitools.common.resource.SitoolsParameterizedResource;
import fr.cnes.sitools.plugins.resources.model.ResourceModel;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.tasks.TaskUtils;
import fr.cnes.sitools.tasks.model.TaskModel;
import fr.cnes.sitools.tasks.model.TaskResourceModel;
import fr.cnes.sitools.tasks.model.TaskRunTypeAdministration;
import fr.cnes.sitools.tasks.model.TaskStatus;

/**
 * 
 * Task class aims at execute the API servers requests; retrieve the results; treat them to create a file or other;
 * notify the end of the job. It also aims at put all operations details in a log file for future investigations by the
 * user.
 * 
 * @author jp.boignard (AKKA Technologies)
 * 
 */
public final class Task implements Runnable {

  /** The Request / application context */
  private Context context;

  /** The request */
  private Request request;

  /** The response */
  private Response response;

  /** TaskModel */
  private TaskModel taskModel;

  /** The object containing the result of the task */
  private Representation result;

  /** The object containing the error of a task execution */
  private Status status;

  /** The resourceModel */
  private ResourceModel resourceModel;

  /** The user in charge of the task, can be null */
  private User user;

  /** The root URL for the task */
  private String rootUrl;

  // /** Future Restlet 2.1 */
  // private Future<?> future;

  /** logging folder */
  private String logFolder;

  /** if the task if persisted or not */
  private boolean persist;

  /**
   * There is one specific logger per task in the user storage or in a temporary file. *
   */
  private Logger logger = null;

  /**
   * The Task constuctor
   * 
   * @param context
   *          the Restlet Context of the Task
   * @param r
   *          the request object
   * @param response
   *          the response object
   * @param id
   *          the id of the Task
   * @param resourceModel
   *          the Resource model
   * @param user
   *          the User
   * @param loggerLvl
   *          the level of the logger
   */
  public Task(Context context, Request r, Response response, String id, ResourceModel resourceModel, User user,
      Level loggerLvl) {
    super();
    this.setContext(context);
    this.setRequest(r);
    this.setResponse(response);
    this.setUser(user);
    this.logFolder = (String) context.getAttributes().get(TaskUtils.LOG_FOLDER);

    TaskModel model = new TaskModel();
    model.setId(id);
    model.setModelId(resourceModel.getId());

    model.setModelName(resourceModel.getDescriptionAction());

    model.setRunTypeAdministration(null);

    this.setResourceModel(resourceModel);
    this.setTaskModel(model);

    if (user != null) {
      this.setUserId(user.getIdentifier());
    }

    // create a logger for the Task
    logger = Logger.getLogger(id);
    loggerLvl = (loggerLvl == null) ? Level.INFO : loggerLvl;

    logger.setLevel(loggerLvl);

    // create a fileHandler to log into a file
    try {
      File f = new File(logFolder);
      if (!f.exists()) {
        f.mkdirs();
        f.setReadable(true);
        f.setWritable(true);
      }
    }
    catch (SecurityException e) {
      // TODO Auto-generated catch block
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }

  }

  /**
   * Constructor Used for Task recovery at starting
   * 
   * @param taskModel
   *          The TaskModel of the Task
   */
  public Task(TaskModel taskModel) {
    super();
    this.setTaskModel(taskModel);
  }

  /**
   * Instantiate a new Server Resource with the following Context, Request and Response
   * 
   * @param context
   *          the Context
   * @param request
   *          the Request
   * @param response
   *          the Response
   * @return a new ServerResource
   */
  private ServerResource instantiate(Context context, Request request, Response response) {

    SitoolsParameterizedResource resImpl = null;
    try {
      ResourceParameter resParam = getResourceModel().getParameterByName(TaskResourceModel.RESOURCE_IMPL_PARAM_NAME);
      @SuppressWarnings("unchecked")
      Class<SitoolsParameterizedResource> classImpl = (Class<SitoolsParameterizedResource>) Class.forName(resParam
          .getValue());
      resImpl = classImpl.newInstance();

      // chaque SitoolsParameteriedResource possede son propre context (cf SitoolsParameterizedapplication attach)
      context.getAttributes().put(TaskUtils.TASK, this);
      context.setLogger(logger);

      resImpl.init(context, request, response);

    }
    catch (ClassNotFoundException e) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
    catch (InstantiationException e) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
    catch (IllegalAccessException e) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
    }
    return resImpl;
  }

  /**
   * Execute the task
   * 
   * @return Representation the result of the execution
   */
  private Representation execute() {

    ServerResource resImpl = null;
    Representation repr = null;

    Date startDate = new Date();
    this.setStartDate(startDate);

    // init the log handler
    FileHandler fl;
    try {
      // fl = new FileHandler(logFolder + "/" + dsApp.getDataSet().getName() + "_"
      // + DateUtils.format(startDate, TaskUtils.getTimestampPattern()).toString() + "-%u.log", true);
      String resName = this.getResourceModel().getName();
      int nameLength = (resName.length() > 8) ? 8 : resName.length();
      fl = new FileHandler(logFolder + "/" + resName.substring(0, nameLength) + "_"
          + DateUtils.format(startDate, TaskUtils.getTimestampPattern()).toString() + "-%u.log", true);
      fl.setFormatter(new TaskLogFormatter());
      logger.addHandler(fl);

      this.getLogger().info("TASK STARTED at : " + this.getStartDate());
    }
    catch (SecurityException e1) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e1);

    }
    catch (IOException e1) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e1);
    }
    resImpl = instantiate(getContext(), this.getRequest(), this.getResponse());
    if ((response == null) || response.getStatus().isSuccess()) {
      try {
        repr = resImpl.handle();
        this.setEndDate(new Date());
        if (getResponse().getStatus().isError()) {
          this.setStatus(getResponse().getStatus());
          this.setTaskStatus(TaskStatus.TASK_STATUS_FAILURE);
          this.setCustomStatus(this.getStatus().getDescription());
          this.setEndDate(new Date());
          this.setUrlResult(rootUrl + "/represent");
          // logger.log(Level.WARNING, "ResourceException", this.getThrowable());
        }
      }
      finally {
        TaskManager.getInstance().updateTask(this);
      }
    }
    else {
      // Probably during the instantiation of the target
      // server resource, or earlier the status was
      // changed from the default one. Don't go further.
      this.setStatus(getResponse().getStatus());
      this.setTaskStatus(TaskStatus.TASK_STATUS_FAILURE);
      this.setCustomStatus(this.getStatus().getDescription());
      this.setEndDate(new Date());
      this.setUrlResult(rootUrl + "/represent");
      TaskManager.getInstance().updateTask(this);
    }

    this.getLogger().info("TASK ENDED at : " + this.getEndDate());

    long timeElapsed = this.getEndDate().getTime() - this.getStartDate().getTime();

    this.getLogger().log(
        Level.INFO,
        "Time Elapsed : " + (timeElapsed / (60 * 60 * 1000)) + "h " + (timeElapsed / (60 * 1000)) + "min  "
            + (timeElapsed / 1000) + "sec " + timeElapsed + "ms");

    // FIXME repr can be null ...
    return repr;
  }

  /*
   * (non-Javadoc) Run Asynchronously
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    this.setTaskStatus(TaskStatus.TASK_STATUS_RUNNING);
    Representation repr = execute();
    this.setResult(repr);
    if (repr != null) {
      this.setUrlResult(rootUrl + "/represent");
    }
    if (!this.getTaskStatus().equals(TaskStatus.TASK_STATUS_FAILURE)) {
      this.setTaskStatus(TaskStatus.TASK_STATUS_FINISHED);
    }
  }

  /**
   * Synchronous run
   */
  public void runSynchrone() {
    this.setTaskStatus(TaskStatus.TASK_STATUS_RUNNING);
    Representation repr = execute();
    this.setResult(repr);
    if (!this.getTaskStatus().equals(TaskStatus.TASK_STATUS_FAILURE)) {
      this.setTaskStatus(TaskStatus.TASK_STATUS_FINISHED);
    }
    if (repr != null) {
      this.setUrlResult(rootUrl + "/represent");
    }
  }

  /**
   * Sets the value of customStatus
   * 
   * @param customStatus
   *          the customStatus to set
   */
  public void setCustomStatus(String customStatus) {
    this.getTaskModel().setCustomStatus(customStatus);
    TaskManager.getInstance().updateTask(this);
  }

  /**
   * Gets the customStatus value
   * 
   * @return the customStatus
   */
  public String getCustomStatus() {
    return this.getTaskModel().getCustomStatus();
  }

  /**
   * Sets the value of urlResult
   * 
   * @param urlResult
   *          the urlResult to set
   */
  public void setUrlResult(String urlResult) {
    this.getTaskModel().setUrlResult(urlResult);
    TaskManager.getInstance().updateTask(this);
  }

  /**
   * Gets the urlResult value
   * 
   * @return the urlResult
   */
  public String getUrlResult() {
    return this.getTaskModel().getUrlResult();
  }

  /**
   * Sets the value of mediaResult
   * 
   * @param mediaResult
   *          the mediaResult to set
   */
  public void setMediaResult(String mediaResult) {
    this.getTaskModel().setMediaResult(mediaResult);
    TaskManager.getInstance().updateTask(this);
  }

  /**
   * Gets the mediaResult value
   * 
   * @return the mediaResult
   */
  public String getMediaResult() {
    return this.getTaskModel().getMediaResult();
  }

  /**
   * Sets the value of statusUrl
   * 
   * @param statusUrl
   *          the statusUrl to set
   */
  public void setStatusUrl(String statusUrl) {
    this.getTaskModel().setStatusUrl(statusUrl);
    TaskManager.getInstance().updateTask(this);
  }

  /**
   * Gets the statusUrl value
   * 
   * @return the statusUrl
   */
  public String getStatusUrl() {
    return this.getTaskModel().getStatusUrl();
  }

  /**
   * Get the task Identifier
   * 
   * @return the task ID
   */
  public String getTaskId() {
    return this.getTaskModel().getId();
  }

  /**
   * Set the task Identifier
   * 
   * @param id
   *          the identifier
   */
  public void setTaskId(String id) {
    this.getTaskModel().setId(id);
  }

  /**
   * Gets the context value
   * 
   * @return the context
   */
  public Context getContext() {
    return context;
  }

  /**
   * Sets the value of context
   * 
   * @param context
   *          the context to set
   */
  public void setContext(Context context) {
    this.context = context;
  }

  /**
   * Set the request
   * 
   * @param request
   *          the request to set
   */
  public void setRequest(Request request) {
    this.request = request;
  }

  /**
   * Get the request
   * 
   * @return the request to get
   */
  public Request getRequest() {
    return request;
  }

  /**
   * Gets the response value
   * 
   * @return the response
   */
  public Response getResponse() {
    return response;
  }

  /**
   * Sets the value of response
   * 
   * @param response
   *          the response to set
   */
  public void setResponse(Response response) {
    this.response = response;
  }

  /**
   * Set the result of the treatment
   * 
   * @param result
   *          the result to set
   */
  public void setResult(Representation result) {
    this.result = result;
  }

  /**
   * Get the result of the treatment
   * 
   * @return the result
   */
  public Representation getResult() {
    return result;
  }

  /**
   * Gets the status value
   * 
   * @return the status
   */
  public synchronized TaskStatus getTaskStatus() {
    return this.getTaskModel().getStatus();
  }

  /**
   * Sets the value of status
   * 
   * @param status
   *          the status to set
   */
  public synchronized void setTaskStatus(TaskStatus status) {
    this.getTaskModel().setStatus(status);
    TaskManager.getInstance().updateTask(this);
  }

  /**
   * Sets the value of user
   * 
   * @param userId
   *          the userId to set
   */
  public void setUserId(String userId) {
    this.getTaskModel().setUserId(userId);
  }

  /**
   * Gets the user value
   * 
   * @return the user
   */
  public String getUserId() {
    return this.getTaskModel().getUserId();
  }

  /**
   * Sets the value of time stamp
   * 
   * @param timestamp
   *          the time stamp to set
   */
  public void setTimestamp(long timestamp) {
    this.getTaskModel().setTimestamp(timestamp);
    TaskManager.getInstance().updateTask(this);
  }

  /**
   * Gets the time stamp value
   * 
   * @return the time stamp
   */
  public long getTimestamp() {
    return this.getTaskModel().getTimestamp();
  }

  /**
   * Sets the value of rootUrl
   * 
   * @param rootUrl
   *          the rootUrl to set
   */
  public void setRootUrl(String rootUrl) {
    this.rootUrl = rootUrl;
  }

  /**
   * Gets the rootUrl value
   * 
   * @return the rootUrl
   */
  public String getRootUrl() {
    return rootUrl;
  }

  /**
   * Sets the value of user
   * 
   * @param user
   *          the user to set
   */
  public void setUser(User user) {
    this.user = user;
  }

  /**
   * Gets the user value
   * 
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * Gets the startDate value
   * 
   * @return the startDate
   */
  public Date getStartDate() {
    return this.getTaskModel().getStartDate();
  }

  /**
   * Sets the value of startDate
   * 
   * @param startDate
   *          the startDate to set
   */
  public void setStartDate(Date startDate) {
    this.getTaskModel().setStartDate(startDate);
    TaskManager.getInstance().updateTask(this);
  }

  /**
   * Gets the endDate value
   * 
   * @return the endDate
   */
  public Date getEndDate() {
    return this.getTaskModel().getEndDate();
  }

  /**
   * Sets the value of endDate
   * 
   * @param endDate
   *          the endDate to set
   */
  public void setEndDate(Date endDate) {
    this.getTaskModel().setEndDate(endDate);
    TaskManager.getInstance().updateTask(this);
  }

  /**
   * Sets the value of logger
   * 
   * @param logger
   *          the logger to set
   */
  public void setLogger(Logger logger) {
    this.logger = logger;
  }

  /**
   * Gets the logger value
   * 
   * @return the logger
   */
  public Logger getLogger() {
    return logger;
  }

  /**
   * Sets the value of runType
   * 
   * @param runType
   *          the runType to set
   */
  public void setRunType(TaskRunTypeAdministration runType) {
    this.getTaskModel().setRunTypeAdministration(runType);
    TaskManager.getInstance().updateTask(this);
  }

  /**
   * Gets the runType value
   * 
   * @return the runType
   */
  public TaskRunTypeAdministration getRunTypeAdministration() {
    return this.getTaskModel().getRunTypeAdministration();
  }

  /**
   * Sets the value of persist
   * 
   * @param persist
   *          the persist to set
   */
  public void setPersist(boolean persist) {
    this.persist = persist;
  }

  /**
   * Gets the persist value
   * 
   * @return the persist
   */
  public boolean isPersist() {
    return persist;
  }

  /**
   * Gets the taskModel value
   * 
   * @return the taskModel
   */
  public TaskModel getTaskModel() {
    return taskModel;
  }

  /**
   * Sets the value of taskModel
   * 
   * @param taskModel
   *          the taskModel to set
   */
  public void setTaskModel(TaskModel taskModel) {
    this.taskModel = taskModel;
  }

  /**
   * Gets the resourceModel value
   * 
   * @return the resourceModel
   */
  public ResourceModel getResourceModel() {
    return resourceModel;
  }

  /**
   * Sets the value of resourceModel
   * 
   * @param resourceModel
   *          the resourceModel to set
   */
  public void setResourceModel(ResourceModel resourceModel) {
    this.resourceModel = resourceModel;
  }

  /**
   * Sets the value of status
   * 
   * @param status
   *          the status to set
   */
  public void setStatus(Status status) {
    this.status = status;
  }

  /**
   * Gets the status value
   * 
   * @return the status
   */
  public Status getStatus() {
    return status;
  }

}
