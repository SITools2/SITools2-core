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
package fr.cnes.sitools.form.project.services.business;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.ObjectRepresentation;

import fr.cnes.sitools.common.SitoolsSettings;
import fr.cnes.sitools.common.application.ContextAttributes;
import fr.cnes.sitools.common.application.SitoolsApplication;
import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.form.project.services.dto.DataSetQueryStatusDTO;
import fr.cnes.sitools.form.project.services.dto.DatasetQueryStatus;
import fr.cnes.sitools.registry.AppRegistryApplication;
import fr.cnes.sitools.security.SecurityUtil;
import fr.cnes.sitools.common.Consts;
import fr.cnes.sitools.util.RIAPUtils;

/**
 * A Callable used to perform a Count on a DataSet with a specified query String
 * 
 * @author jp.boignard (AKKA Technologies)
 */
public class DataSetCounterCallable implements Callable<DataSetCounterJobStatus> {
  /** The request query String */
  private String requestQuery;
  /** The id of the dataset */
  private String datasetId;
  /** The context */
  private Context context;
  /** The user identifier */
  private String userIdentifier;

  /**
   * a Constructor with parameters
   * 
   * @param requestQuery
   *          the request query String
   * @param datasetId
   *          the dataset identifier
   * @param context
   *          the Context
   * @param userIdentifier
   *          the user identifier
   */
  public DataSetCounterCallable(String requestQuery, String datasetId, Context context, String userIdentifier) {
    super();
    this.requestQuery = requestQuery;
    this.datasetId = datasetId;
    this.context = context;
    this.userIdentifier = userIdentifier;
  }

  public DataSetCounterJobStatus call() throws Exception {
    DataSetCounterJobStatus status = null;
    context.getLogger().log(Level.INFO, "Job started on dataset " + this.datasetId);

    SitoolsSettings settings = (SitoolsSettings) context.getAttributes().get(ContextAttributes.SETTINGS);
    // first get the dataset object
    DataSet ds = RIAPUtils.getObject(datasetId, settings.getString(Consts.APP_DATASETS_URL), context);
    // if the dataset exists and is ACTIVE let's query it
    if (ds != null && "ACTIVE".equals(ds.getStatus())) {
      AppRegistryApplication appManager = settings.getAppRegistry();
      // check the access rights on the dataset
      SitoolsApplication dsApp = appManager.getApplication(ds.getId());
      boolean authorized = SecurityUtil.authorize(dsApp, userIdentifier, Method.GET);
      // if the user has access rights on the dataset let's query it for real
      if (authorized) {
        // send the request to the dataset
        int recordsResult;
        DataSetQueryStatusDTO dataset = null;
        try {
          recordsResult = getNbRecordsResult(ds, requestQuery);
          dataset = createDataSetQueryStatusDTO(ds, recordsResult);
          status = new DataSetCounterJobStatus(true, dataset);
        }
        catch (SitoolsException e) {
          dataset = createDataSetQueryStatusError(ds, e.getLocalizedMessage());
          status = new DataSetCounterJobStatus(false, dataset);
        }
        // create a quick description for the client
        catch (Exception e) {
          dataset = createDataSetQueryStatusError(ds, e.getLocalizedMessage());
          status = new DataSetCounterJobStatus(false, dataset);
        }
      }
    }

    context.getLogger().log(Level.INFO, "Job finished on dataset " + this.datasetId);
    return status;
  }

  /**
   * Create a {@link DataSetQueryStatusDTO} with success = true from a DataSet and a number of records
   * 
   * @param ds
   *          the DataSet
   * @param recordsResult
   *          the number of records
   * @return a {@link DataSetQueryStatusDTO}
   */
  private DataSetQueryStatusDTO createDataSetQueryStatusDTO(DataSet ds, int recordsResult) {
    DataSetQueryStatusDTO dataset = new DataSetQueryStatusDTO();
    dataset.setId(ds.getId());
    dataset.setName(ds.getName());
    dataset.setDescription(ds.getDescription());
    dataset.setNbRecord(recordsResult);
    dataset.setImage(ds.getImage());
    dataset.setUrl(ds.getSitoolsAttachementForUsers());
    dataset.setStatus(DatasetQueryStatus.REQUEST_DONE);

    return dataset;
  }

  /**
   * Create a {@link DataSetQueryStatusDTO} with success = false from a DataSet and an errorMessage
   * 
   * @param ds
   *          the DataSet
   * @param errorMessage
   *          the error message
   * @return a {@link DataSetQueryStatusDTO}
   */
  private DataSetQueryStatusDTO createDataSetQueryStatusError(DataSet ds, String errorMessage) {
    DataSetQueryStatusDTO dataset = new DataSetQueryStatusDTO();
    dataset.setId(ds.getId());
    dataset.setName(ds.getName());
    dataset.setDescription(ds.getDescription());
    dataset.setImage(ds.getImage());
    dataset.setUrl(ds.getSitoolsAttachementForUsers());
    dataset.setStatus(DatasetQueryStatus.REQUEST_ERROR);
    dataset.setErrorMessage(errorMessage);

    return dataset;
  }

  /**
   * Get the number of records in result of a search on the specified dataset with the specified requestQuery
   * 
   * @param ds
   *          the DataSet
   * @param requestQuery
   *          the query request
   * @return the number of records in result of a search, 0 if there is an error
   * @throws Exception
   *           if there are some errors
   */
  @SuppressWarnings("unchecked")
  private int getNbRecordsResult(DataSet ds, String requestQuery) throws Exception {
    // gets the medias
    String url = ds.getSitoolsAttachementForUsers() + "/count?" + requestQuery;

    Reference ref = new Reference(url);
    // ref.addQueryParameter("start", "0");
    // ref.addQueryParameter("limit", "0");

    Request reqGET = new Request(Method.GET, RIAPUtils.getRiapBase() + ref);
    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response response = null;

    response = context.getClientDispatcher().handle(reqGET);

    if (response == null || Status.isError(response.getStatus().getCode())) {
      RIAPUtils.exhaust(response);
      throw new SitoolsException(response.getStatus().getName() + " // " + response.getStatus().getDescription());
    }

    ObjectRepresentation<Response> or = (ObjectRepresentation<Response>) response.getEntity();
    try {
      Response resp = or.getObject();
      // check if there is an object in the response, if not return null
      if (resp == null) {
        throw new SitoolsException("Expected Response from the count call was null");
      }
      return resp.getTotal();
    }
    catch (Exception e) {
      // if the cause of the error is a SitoolsException, let's get it to send a more understandable error message
      if (e.getCause() != null && e.getCause().getClass().equals(SitoolsException.class)) {
        SitoolsException se = (SitoolsException) e.getCause();
        throw new SitoolsException(e.getMessage() + " // " + se.getMessage());
      }
      else {
        throw e;
      }
    }
  }

}
