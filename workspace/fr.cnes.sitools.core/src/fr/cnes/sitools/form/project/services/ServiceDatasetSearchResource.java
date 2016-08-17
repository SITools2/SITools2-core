    /*******************************************************************************
 * Copyright 2010-2016 CNES - CENTRE NATIONAL d'ETUDES SPATIALES
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
package fr.cnes.sitools.form.project.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

import fr.cnes.sitools.collections.model.Collection;
import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.model.Resource;
import fr.cnes.sitools.form.project.services.business.DataSetCounterCallable;
import fr.cnes.sitools.form.project.services.business.DataSetCounterJobStatus;
import fr.cnes.sitools.form.project.services.dto.DataSetQueryStatusDTO;
import fr.cnes.sitools.form.project.services.dto.DatasetQueryStatus;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.server.Consts;
import fr.cnes.sitools.tasks.TaskUtils;
import fr.cnes.sitools.tasks.business.Task;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * Multidataset search service
 * <p>
 * Gets a list of dataset identifier and return the datasets description with the number of records
 * </p>
 * 
 * @author m.gond
 */
public class ServiceDatasetSearchResource extends ServiceDatasetSearchResourceFacade {
  /**
   * The list of DataSet identifiers
   */
  private String datasetList = null;
  /**
   * The SitoolsSettings
   */
  private SitoolsSettings settings = null;

  /** The Thread pool Size */
  private int threadPoolSize;

  /** The Maximum number of datasets allowed */
  private Integer nbDatasetsMax;

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.common.resource.SitoolsParameterizedResource#doInit()
   */
  @Override
  public void doInit() {
    // TODO Auto-generated method stub
    super.doInit();

    datasetList = getRequest().getResourceRef().getQueryAsForm().getFirstValue("datasetsList");
    settings = (SitoolsSettings) getContext().getAttributes().get(ContextAttributes.SETTINGS);

    ResourceParameter nbThreadSize = getModel().getParameterByName("nbThreads");
    if (nbThreadSize == null) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "No parameter nbThreads defined");
    }
    else {
      try {
        threadPoolSize = Integer.parseInt(nbThreadSize.getValue());
      }
      catch (NumberFormatException e) {
        throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Parameter nbThreads is not a number", e);
      }
    }

    ResourceParameter nbDatasetsMaxParam = getModel().getParameterByName("nbDatasetsMax");
    if (nbDatasetsMaxParam == null) {
      throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "No parameter nbDatasetsMax defined");
    }

    else {
      try {
        nbDatasetsMax = Integer.parseInt(nbDatasetsMaxParam.getValue());
      }
      catch (NumberFormatException e) {
        nbDatasetsMax = null;
        // throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Parameter nbDatasetsMax is not a number", e);
      }

    }

  }

  /**
   * Create the order
   * 
   * @param represent
   *          the {@link Representation} entity
   * @param variant
   *          The {@link Variant} needed
   * @return a representation
   */
  @Post
  public Representation processSearch(Representation represent, Variant variant) {
    String requestQuery = getRequest().getResourceRef().getQuery();
    Task task = TaskUtils.getTask(getContext());
    String[] datasetIds = null;
    // if the client sent a datasetList let's get those datasets
    if (datasetList != null) {
      datasetIds = datasetList.split("\\|");
    }
    // if no datasetList was given, let's take the datasets from the collection
    else {
      // Get the Collection Object
      String collectionId = getOverrideParameterValue("collection");
      Collection collection = RIAPUtils.getObject(collectionId, settings.getString(Consts.APP_COLLECTIONS_URL),
          getContext());
      // If the collection exists and contains some datasets, let's create a list of datasets
      if (collection != null && collection.getDataSets() != null) {
        List<String> datasetIdList = new ArrayList<String>();
        datasetIds = new String[collection.getDataSets().size()];
        for (Resource dataset : collection.getDataSets()) {
          datasetIdList.add(dataset.getId());
        }
        datasetIds = datasetIdList.toArray(new String[0]);
      }
    }

    if (datasetIds != null) {
      if (nbDatasetsMax != null && datasetIds.length > nbDatasetsMax) {
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "To many datasets given only " + nbDatasetsMax
            + " allowed ");
      }
      List<Future<DataSetCounterJobStatus>> listFutureJobStatus = new ArrayList<Future<DataSetCounterJobStatus>>();
      // HashMap<String, JobStatus> listJobStatus = new HashMap<String, JobStatus>();
      ExecutorService pooledExecutor = Executors.newFixedThreadPool(threadPoolSize);

      // DataSetQueryManager.getInstance().init(task);
      User user = this.getRequest().getClientInfo().getUser();
      String userIdentifier = (user == null) ? null : user.getIdentifier();
      for (int i = 0; i < datasetIds.length; i++) {
        DataSetCounterCallable job = new DataSetCounterCallable(requestQuery, datasetIds[i], getContext(),
            userIdentifier);
        Future<DataSetCounterJobStatus> future = pooledExecutor.submit(job);
        listFutureJobStatus.add(future);
      }

      pooledExecutor.shutdown();

      // loop while all the thread are finished
      while (!pooledExecutor.isTerminated()) {
        // update the list of dataset in the taskModel
        addCountToTaskModel(listFutureJobStatus, task);
      }
      // update the list of dataset in the taskModel in case some thread finished but was not added in the taskModel
      addCountToTaskModel(listFutureJobStatus, task);

    }

    return null;
  }

  /**
   * Loop through the list of Threads and update the list of {@link DataSetQueryStatusDTO} in the TaskModel
   * 
   * @param listFutureJobStatus
   *          the list of {@link DataSetCounterJobStatus} representing the different threads
   * @param task
   *          the Task
   */
  private void addCountToTaskModel(List<Future<DataSetCounterJobStatus>> listFutureJobStatus, Task task) {
    for (Iterator<Future<DataSetCounterJobStatus>> iterator = listFutureJobStatus.iterator(); iterator.hasNext();) {
      Future<DataSetCounterJobStatus> status = iterator.next();
      if (status.isDone()) {
        DataSetCounterJobStatus jobstatus;
        DataSetQueryStatusDTO dsQueryStatus = null;
        try {
          jobstatus = status.get();
          if (jobstatus != null) {
            dsQueryStatus = jobstatus.getResult();
          }
        }
        catch (InterruptedException e) {
          dsQueryStatus = new DataSetQueryStatusDTO();
          dsQueryStatus.setName("InterruptedException");
          dsQueryStatus.setErrorMessage(e.getLocalizedMessage());
          dsQueryStatus.setStatus(DatasetQueryStatus.REQUEST_ERROR);
        }
        catch (ExecutionException e) {
          dsQueryStatus = new DataSetQueryStatusDTO();
          dsQueryStatus.setName("ExecutionException");
          dsQueryStatus.setErrorMessage(e.getLocalizedMessage());
          dsQueryStatus.setStatus(DatasetQueryStatus.REQUEST_ERROR);
        }
        List<Object> properties = task.getTaskModel().getProperties();
        if (dsQueryStatus != null
            && (dsQueryStatus.getStatus().equals(DatasetQueryStatus.REQUEST_ERROR) || dsQueryStatus.getNbRecord() > 0)) {
          synchronized (properties) {
            properties.add(dsQueryStatus);
          }
        }

        iterator.remove();
      }
    }
  }
}
