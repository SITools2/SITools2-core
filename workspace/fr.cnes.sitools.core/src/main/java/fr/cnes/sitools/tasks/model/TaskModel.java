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
package fr.cnes.sitools.tasks.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import fr.cnes.sitools.common.model.IResource;

/**
 * Model for Task
 * 
 * @author m.gond (AKKA Technologies)
 * 
 * 
 */
@XStreamAlias("TaskModel")
public final class TaskModel implements IResource, Cloneable, Serializable {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -7321795037001312418L;

  /** The ID of the task */
  private String id;

  /** The Name of the task NEW R4S4 */
  private String name;

  /** The description NEW R4S4 */
  private String description;

  /** The status of the task */
  private TaskStatus status = TaskStatus.TASK_STATUS_PENDING;

  /** The id of the model */
  private String modelId;

  /** A custom status set by the programmer during the execution */
  private String customStatus;

  /** The execution remaining time */
  private long timestamp;

  /** The URL of the status resource */
  private String statusUrl;

  /** The URL of the result */
  private String urlResult;

  /** The mediaType of the result */
  private String mediaResult;

  /** the id of the user */
  private String userId;

  /** date when the Task starts */
  private Date startDate;
  /** date when the Task stops */
  private Date endDate;
  /** Run type */
  private TaskRunTypeAdministration runTypeAdministration;
  /** Run type User Input */
  private TaskRunTypeUserInput runTypeUserInput;

  /** name of the ResourceModel ? */
  private String modelName;

  /** A list of properties */
  private List<Object> properties;

  /**
   * TaskModel default constructor
   */
  public TaskModel() {
    super();
    this.setProperties(new ArrayList<Object>());
  }

  /**
   * Gets the id value
   * 
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the value of id
   * 
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the name value
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of name
   * 
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the description value
   * 
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the value of description
   * 
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the status value
   * 
   * @return the status
   */
  public TaskStatus getStatus() {
    return status;
  }

  /**
   * Sets the value of status
   * 
   * @param status
   *          the status to set
   */
  public void setStatus(TaskStatus status) {
    this.status = status;
  }

  /**
   * Gets the modelId value
   * 
   * @return the modelId
   */
  public String getModelId() {
    return modelId;
  }

  /**
   * Sets the value of modelId
   * 
   * @param modelId
   *          the modelId to set
   */
  public void setModelId(String modelId) {
    this.modelId = modelId;
  }

  /**
   * Sets the value of customStatus
   * 
   * @param customStatus
   *          the customStatus to set
   */
  public void setCustomStatus(String customStatus) {
    this.customStatus = customStatus;
  }

  /**
   * Gets the customStatus value
   * 
   * @return the customStatus
   */
  public String getCustomStatus() {
    return customStatus;
  }

  /**
   * Sets the value of time stamp
   * 
   * @param timestamp
   *          the time stamp to set
   */
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * Gets the time stamp value
   * 
   * @return the time stamp
   */
  public long getTimestamp() {
    return timestamp;
  }

  /**
   * Sets the value of statusUrl
   * 
   * @param statusUrl
   *          the statusUrl to set
   */
  public void setStatusUrl(String statusUrl) {
    this.statusUrl = statusUrl;
  }

  /**
   * Gets the statusUrl value
   * 
   * @return the statusUrl
   */
  public String getStatusUrl() {
    return statusUrl;
  }

  /**
   * Sets the value of urlResult
   * 
   * @param urlResult
   *          the urlResult to set
   */
  public void setUrlResult(String urlResult) {
    this.urlResult = urlResult;
  }

  /**
   * Gets the urlResult value
   * 
   * @return the urlResult
   */
  public String getUrlResult() {
    return urlResult;
  }

  /**
   * Sets the value of userId
   * 
   * @param userId
   *          the userId to set
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * Gets the userId value
   * 
   * @return the userId
   */
  public String getUserId() {
    return userId;
  }

  /**
   * Gets the startDate value
   * 
   * @return the startDate
   */
  public Date getStartDate() {
    return startDate;
  }

  /**
   * Sets the value of startDate
   * 
   * @param startDate
   *          the startDate to set
   */
  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  /**
   * Gets the endDate value
   * 
   * @return the endDate
   */
  public Date getEndDate() {
    return endDate;
  }

  /**
   * Sets the value of endDate
   * 
   * @param endDate
   *          the endDate to set
   */
  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  /**
   * Sets the value of runType
   * 
   * @param runType
   *          the runType to set
   */
  public void setRunTypeAdministration(TaskRunTypeAdministration runType) {
    this.runTypeAdministration = runType;
  }

  /**
   * Gets the runType value
   * 
   * @return the runType
   */
  public TaskRunTypeAdministration getRunTypeAdministration() {
    return runTypeAdministration;
  }

  /**
   * Sets the value of modelName
   * 
   * @param modelName
   *          the modelName to set
   */
  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  /**
   * Gets the modelName value
   * 
   * @return the modelName
   */
  public String getModelName() {
    return modelName;
  }

  /**
   * Gets the runTypeUserInput value
   * 
   * @return the runTypeUserInput
   */
  public TaskRunTypeUserInput getRunTypeUserInput() {
    return runTypeUserInput;
  }

  /**
   * Sets the value of runTypeUserInput
   * 
   * @param runTypeUserInput
   *          the runTypeUserInput to set
   */
  public void setRunTypeUserInput(TaskRunTypeUserInput runTypeUserInput) {
    this.runTypeUserInput = runTypeUserInput;
  }

  /**
   * Gets the mediaResult value
   * 
   * @return the mediaResult
   */
  public String getMediaResult() {
    return mediaResult;
  }

  /**
   * Sets the value of mediaResult
   * 
   * @param mediaResult
   *          the mediaResult to set
   */
  public void setMediaResult(String mediaResult) {
    this.mediaResult = mediaResult;
  }

  /**
   * Sets the value of properties
   * 
   * @param properties
   *          the properties to set
   */
  public void setProperties(List<Object> properties) {
    this.properties = properties;
  }

  /**
   * Gets the properties value
   * 
   * @return the properties
   */
  public List<Object> getProperties() {
    return properties;
  }

  /**
   * Clone method
   * 
   * @return TaskModel a {@link TaskModel}, clone of the current Object
   */
  public TaskModel clone() {
    TaskModel model = new TaskModel();
    model.setId(id);
    model.setDescription(description);
    model.setCustomStatus(customStatus);
    model.setEndDate(endDate);
    model.setMediaResult(mediaResult);
    model.setModelId(modelId);
    model.setModelName(modelName);
    model.setName(modelName);
    List<Object> newProperties;
    synchronized (properties) {
      newProperties = new ArrayList<Object>(properties);
    }
    model.setProperties(newProperties);
    model.setRunTypeAdministration(runTypeAdministration);
    model.setRunTypeUserInput(runTypeUserInput);
    model.setStartDate(startDate);
    model.setStatus(status);
    model.setStatusUrl(statusUrl);
    model.setTimestamp(timestamp);
    model.setUrlResult(urlResult);
    model.setUserId(userId);
    return model;
  }

}
