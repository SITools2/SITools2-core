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

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.ResourceCollectionFilter;
import fr.cnes.sitools.common.model.ResourceComparator;
import fr.cnes.sitools.persistence.XmlSynchronizedMapStore;
import fr.cnes.sitools.tasks.model.TaskModel;

/**
 * Implementation of TaskModelStore with XStream FilePersistenceStrategy
 * 
 * @author AKKA
 * 
 */
public class TaskStoreXMLMap extends XmlSynchronizedMapStore<TaskModel> implements TaskStoreInterface {

  /** default location for file persistence */
  private static final String COLLECTION_NAME = "tasks";

  /**
   * Constructor with the XML file location
   * 
   * @param location
   *          directory of FilePersistenceStrategy
   * @param context
   *          the Restlet Context
   */
  public TaskStoreXMLMap(File location, Context context) {
    super(TaskModel.class, location, context);
  }

  /**
   * Default constructor
   * 
   * @param context
   *          the Restlet Context
   */
  public TaskStoreXMLMap(Context context) {
    super(TaskModel.class, context);
    File defaultLocation = new File(COLLECTION_NAME);
    init(defaultLocation);
  }

  @Override
  public TaskModel update(TaskModel taskModel) {
    TaskModel result = null;

    Map<String, TaskModel> map = getMap();
    synchronized (map) {
      TaskModel current = map.get(taskModel.getId());
      result = current;
      current.setName(taskModel.getName());
      current.setDescription(taskModel.getDescription());
      current.setStatus(taskModel.getStatus());

      current.setCustomStatus(taskModel.getCustomStatus());
      current.setStartDate(taskModel.getStartDate());
      current.setEndDate(taskModel.getEndDate());
      current.setModelId(taskModel.getModelId());
      current.setRunTypeAdministration(taskModel.getRunTypeAdministration());
      current.setRunTypeUserInput(taskModel.getRunTypeUserInput());

      current.setStatusUrl(taskModel.getStatusUrl());
      current.setModelName(taskModel.getModelName());
      current.setTimestamp(taskModel.getTimestamp());
      current.setUrlResult(taskModel.getUrlResult());
      current.setUserId(taskModel.getUserId());
      map.put(taskModel.getId(), current);
    }
    return result;
  }

  /**
   * XStream FilePersistenceStrategy initialization
   * 
   * @param location
   *          Directory
   */
  public void init(File location) {
    Map<String, Class<?>> aliases = new ConcurrentHashMap<String, Class<?>>();
    aliases.put("TaskModel", TaskModel.class);
    this.init(location, aliases);
  }

  @Override
  public void sort(List<TaskModel> result, ResourceCollectionFilter filter) {

    if ((filter != null) && (filter.getSort() != null) && !filter.getSort().equals("")) {

      if (filter.getSort().equals("startDate")) {

        Collections.sort(result, new ResourceComparator<TaskModel>(filter) {

          @Override
          public int compare(TaskModel arg0, TaskModel arg1) {

            if (arg0.getStartDate() != null && arg1.getStartDate() != null) {
              Date d1 = arg0.getStartDate();
              Date d2 = arg1.getStartDate();
              if ((getFilter() != null) && (getFilter().getOrder() != null) && (getFilter().getOrder().equals(DESC))) {
                return d1.compareTo(d2);
              }
              else {
                return d2.compareTo(d1);
              }

            }
            else {
              return -1;
            }
          }
        });
      }

      if (filter.getSort().equals("endDate")) {

        Collections.sort(result, new ResourceComparator<TaskModel>(filter) {

          @Override
          public int compare(TaskModel arg0, TaskModel arg1) {

            if (arg0.getEndDate() != null && arg1.getEndDate() != null) {
              Date d1 = arg0.getEndDate();
              Date d2 = arg1.getEndDate();
              if ((getFilter() != null) && (getFilter().getOrder() != null) && (getFilter().getOrder().equals(DESC))) {
                return d1.compareTo(d2);
              }
              else {
                return d2.compareTo(d1);
              }

            }
            else {
              return -1;
            }
          }
        });
      }

    }

  }

  @Override
  public List<TaskModel> retrieveByParent(String id) {
    throw new RuntimeException(SitoolsException.NOT_IMPLEMENTED);
  }

  @Override
  public String getCollectionName() {
    return COLLECTION_NAME;
  }

}
