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
package fr.cnes.sitools.resources.order;

import java.util.ArrayList;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.resource.ResourceException;

import fr.cnes.sitools.common.exception.SitoolsException;
import fr.cnes.sitools.common.model.Response;
import fr.cnes.sitools.dataset.DataSetApplication;
import fr.cnes.sitools.dataset.database.DatabaseRequest;
import fr.cnes.sitools.dataset.database.DatabaseRequestFactory;
import fr.cnes.sitools.dataset.database.DatabaseRequestParameters;
import fr.cnes.sitools.dataset.database.common.DataSetExplorerUtil;
import fr.cnes.sitools.dataset.model.DataSet;
import fr.cnes.sitools.plugins.resources.model.ResourceParameter;
import fr.cnes.sitools.resources.order.utils.ListReferencesAPI;
import fr.cnes.sitools.tasks.TaskUtils;
import fr.cnes.sitools.util.RIAPUtils;

public abstract class AbstractDatasetOrderResource extends AbstractOrderResource {

  /** The {@link DatabaseRequestParameters} of the current resource */
  protected DatabaseRequestParameters dbParams;
  /** The {@link DataSet} model object */
  protected DataSet ds;

  /**
   * First step of the order, Initialize it
   * 
   * @throws Exception
   *           if there is any error
   */
  public void initialiseOrder() throws Exception {
    ds = ((DataSetApplication) getApplication()).getDataSet();
    super.initialiseOrder();
    dbParams = prepareRequest();

  }

  /**
   * Prepare the database request. This method can be overridden in order to
   * change the database request
   * 
   * @return a {@link DatabaseRequestParameters} representing the database
   *         request parameters
   * @throws Exception
   *           if there is an error while preparing the request
   */
  public DatabaseRequestParameters prepareRequest() throws Exception {
    task.setCustomStatus("CREATING REQUEST");
    // create the DataSet request
    DataSetApplication datasetApp = (DataSetApplication) getContext().getAttributes().get(TaskUtils.PARENT_APPLICATION);
    DataSetExplorerUtil dsExplorerUtil = new DataSetExplorerUtil(datasetApp, getRequest(), getContext());
    // Get request parameters
    if (datasetApp.getConverterChained() != null) {
      datasetApp.getConverterChained().getContext().getAttributes().put("REQUEST", getRequest());
    }
    DatabaseRequestParameters params = dsExplorerUtil.getDatabaseParams();

    // parameter too_many_selected_threshold is used as a limit max for the
    // number of records
    ResourceParameter maxRowsParam = this.getModel().getParameterByName("too_many_selected_threshold");
    String maxRowsStr = maxRowsParam.getValue();
    int maxRows;
    try {
      maxRows = Integer.valueOf(maxRowsStr);
    }
    catch (NumberFormatException e) {
      throw new SitoolsException("too_many_selected_threshold parameter must be a number", e);
    }
    catch (Exception e) {
      throw new SitoolsException("too_many_selected_threshold parameter canno't be empty", e);
    }

    String requestQuery = getRequest().getResourceRef().getQuery();
    Form bodyForm = (Form) getContext().getAttributes().get(TaskUtils.BODY_CONTENT);
    int count = getCountOnDataset(ds, requestQuery, bodyForm);
    if (maxRows == -1 || count <= maxRows) {
      params.setPaginationExtend(count);
      params.setCountDone(false);
      params.setMaxrows(count);
    }
    else {
      ResourceParameter errorTextParam = getModel().getParameterByName("too_many_selected_threshold_text");
      String errorText = (errorTextParam != null && !"".equals(errorTextParam.getValue())) ? errorTextParam.getValue()
          : "Too many file selected";
      throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, errorText);
    }

    return params;
  }

  /**
   * Get the number of records in result of a search on the specified dataset
   * with the specified requestQuery or a Form containing the url of a file on
   * the server
   * 
   * @param ds
   *          the DataSet
   * @param requestQuery
   *          the query request
   * @param form
   *          the form containing the url of a file on the server
   * @return the number of records in result of a search, 0 if there is an error
   * @throws Exception
   *           if there are some errors
   */
  @SuppressWarnings("unchecked")
  public int getCountOnDataset(DataSet ds, String requestQuery, Form form) throws Exception {

    Request reqGET = getCountRequest(ds, requestQuery, form);
    ArrayList<Preference<MediaType>> objectMediaType = new ArrayList<Preference<MediaType>>();
    objectMediaType.add(new Preference<MediaType>(MediaType.APPLICATION_JAVA_OBJECT));
    reqGET.getClientInfo().setAcceptedMediaTypes(objectMediaType);
    org.restlet.Response response = null;

    response = getContext().getClientDispatcher().handle(reqGET);

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
      // if the cause of the error is a SitoolsException, let's get it to send a
      // more understandable error message
      if (e.getCause() != null && e.getCause().getClass().equals(SitoolsException.class)) {
        SitoolsException se = (SitoolsException) e.getCause();
        throw new SitoolsException(e.getMessage() + " // " + se.getMessage());
      }
      else {
        throw e;
      }
    }
  }

  /**
   * Get the {@link Request} Object to query to have the count on a dataset with
   * either a request query or a {@link Form} containing the url of a file on
   * the server
   * 
   * @param ds
   *          the {@link DataSet}
   * @param requestQuery
   *          a full http query
   * @param form
   *          a {@link Form} containing the url of a file on the server
   * 
   * @return a {@link Request} to query to have the count on a dataset
   */
  private Request getCountRequest(DataSet ds, String requestQuery, Form form) {
    String url = ds.getSitoolsAttachementForUsers() + "/count";
    Reference ref = new Reference(url);
    Request request;
    if (form != null) {
      request = new Request(Method.POST, RIAPUtils.getRiapBase() + ref, form.getWebRepresentation());
    }
    else {
      ref.setQuery(requestQuery);
      request = new Request(Method.GET, RIAPUtils.getRiapBase() + ref);
    }
    return request;

  }

  /**
   * Execute the request
   * 
   * @param params
   *          the a {@link DatabaseRequestParameters} representing the database
   *          request parameters
   * @return a {@link DatabaseRequest}
   * @throws SitoolsException
   *           if there is an error while creating the request
   */
  public DatabaseRequest executeRequest(DatabaseRequestParameters params) throws SitoolsException {
    DatabaseRequest databaseRequest = DatabaseRequestFactory.getDatabaseRequest(params);
    if (params.getDistinct()) {
      databaseRequest.createDistinctRequest();
    }
    else {
      databaseRequest.createRequest();
    }
    return databaseRequest;
  }

  /**
   * Abstract method to list all the files to order.
   * 
   * @param dbRequest
   *          the {@link DatabaseRequest} containing the request to the database
   * @return a {@link ListReferencesAPI} containing the list of Reference to
   *         order
   * @throws SitoolsException
   *           if there is any error
   */
  public abstract ListReferencesAPI listFilesToOrder(DatabaseRequest dbRequest) throws SitoolsException;

  /**
   * Abstract method to list all the files to order.
   * 
   * 
   * @return a {@link ListReferencesAPI} containing the list of Reference to
   *         order
   * @throws SitoolsException
   *           if there is any error
   */
  public ListReferencesAPI listFilesToOrder() throws SitoolsException {

    DatabaseRequest dbRequest = null;
    try {
      dbRequest = executeRequest(dbParams);
      return listFilesToOrder(dbRequest);
    }
    finally {
      if (dbRequest != null) {
        dbRequest.close();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.cnes.sitools.resources.order.AbstractOrderResource#getOrderName()
   */
  @Override
  public String getOrderName() {
    return ds.getName();
  }

}
